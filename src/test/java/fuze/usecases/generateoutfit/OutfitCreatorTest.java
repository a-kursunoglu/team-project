package fuze.usecases.generateoutfit;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.entity.weather.WeatherDay;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class OutfitCreatorTest {

    private final OutfitCreator creator = new OutfitCreator();

    private ClothingArticle article(String name, String category, int warmth, boolean waterproof) {
        return new ClothingArticle(name, category, warmth, waterproof, null);
    }

    private WeatherDay makeDay(int weatherCode,
                               double high,
                               double low,
                               double longitude,
                               double latitude,
                               String date) {
        double[] loc = new double[]{longitude, latitude};
        return new WeatherDay(weatherCode, high, low, loc, date);
    }

    private Map<String, List<ClothingArticle>> wardrobe(
            List<ClothingArticle> tops,
            List<ClothingArticle> bottoms,
            List<ClothingArticle> outers,
            List<ClothingArticle> accessories
    ) {
        Map<String, List<ClothingArticle>> map = new HashMap<>();
        if (tops != null) map.put("top", tops);
        if (bottoms != null) map.put("bottom", bottoms);
        if (outers != null) map.put("outer", outers);
        if (accessories != null) map.put("accessory", accessories);
        return map;
    }

    @Test
    void nullWardrobeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> creator.createOutfitForDay(null, null, false));
    }

    @Test
    void missingTopOrBottomReturnsNull() {
        WeatherDay d = makeDay(0, 20, 18, -79.4, 43.7, "2025-11-27");

        Map<String, List<ClothingArticle>> w1 = wardrobe(
                List.of(article("t1", "top", 2, false)),
                null,
                null,
                null
        );
        assertNull(creator.createOutfitForDay(d, w1, false));

        Map<String, List<ClothingArticle>> w2 = wardrobe(
                null,
                List.of(article("b1", "bottom", 2, false)),
                null,
                null
        );
        assertNull(creator.createOutfitForDay(d, w2, false));
    }

    @Test
    void simpleOutfitNoRainNoOuterNoAccessory() {
        WeatherDay d = makeDay(0, 20, 18, -79.4, 43.7, "2025-11-27");

        ClothingArticle top = article("t1", "top", 1, false);
        ClothingArticle bottom = article("b1", "bottom", 1, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                null,
                null
        );

        Outfit outfit = creator.createOutfitForDay(d, w, false);
        assertNotNull(outfit);
        assertFalse(outfit.isWaterproof());

        assertTrue(outfit.getItems().containsKey("top"));
        assertTrue(outfit.getItems().containsKey("bottom"));
        assertFalse(outfit.getItems().containsKey("outer"));
        assertFalse(outfit.getItems().containsKey("accessory"));

        String title = outfit.getTitle();
        assertTrue(title.contains("2025-11-27"));
        assertTrue(title.contains("-79.4000"));
    }

    @Test
    void rainyDayUsesWaterproofTopWhenAvailable() {
        WeatherDay d = makeDay(61, 11, 9, -79.4, 43.7, "2025-11-27");

        ClothingArticle waterproofTop = article("rain jacket", "top", 3, true);
        ClothingArticle nonWaterproofTop = article("hoodie", "top", 3, false);
        ClothingArticle bottom = article("jeans", "bottom", 3, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(nonWaterproofTop, waterproofTop),
                List.of(bottom),
                null,
                null
        );

        Outfit outfit = creator.createOutfitForDay(d, w, false);
        assertNotNull(outfit);

        ClothingArticle chosenTop = outfit.getItems().get("top");
        assertTrue(chosenTop.isWaterproof());
        assertTrue(outfit.isWaterproof());
        assertTrue(outfit.getTitle().contains("(rain)"));
    }

    @Test
    void rainyDayWithoutWaterproofTriggersFallbackInChooseBest() {
        WeatherDay d = makeDay(61, 11, 9, -79.4, 43.7, "2025-11-28");

        ClothingArticle t1 = article("t1", "top", 3, false);
        ClothingArticle t2 = article("t2", "top", 2, false);
        ClothingArticle bottom = article("jeans", "bottom", 3, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(t1, t2),
                List.of(bottom),
                null,
                null
        );

        Outfit outfit = creator.createOutfitForDay(d, w, false);
        assertNotNull(outfit);

        ClothingArticle chosenTop = outfit.getItems().get("top");
        assertEquals(2, chosenTop.getWeatherRating());
        assertFalse(outfit.isWaterproof());
    }

    @Test
    void rainOverrideTrueEvenIfWeatherNotRainy() {
        WeatherDay d = makeDay(0, 11, 9, -79.4, 43.7, "2025-11-29");

        ClothingArticle waterproofTop = article("rain jacket", "top", 3, true);
        ClothingArticle bottom = article("jeans", "bottom", 3, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(waterproofTop),
                List.of(bottom),
                null,
                null
        );

        Outfit outfit = creator.createOutfitForDay(d, w, true);
        assertNotNull(outfit);
        assertTrue(outfit.isWaterproof());
        assertTrue(outfit.getTitle().contains("(rain)"));
    }

    @Test
    void coldDayAddsOuterAndAccessory() {
        WeatherDay d = makeDay(22, -9, -11, -79.4, 43.7, "2025-12-01");

        ClothingArticle top = article("thermal", "top", 2, false);
        ClothingArticle bottom = article("warm pants", "bottom", 2, false);
        ClothingArticle coat = article("winter coat", "outer", 4, false);
        ClothingArticle scarf = article("scarf", "accessory", 3, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                List.of(coat),
                List.of(scarf)
        );

        Outfit outfit = creator.createOutfitForDay(d, w, false);
        assertNotNull(outfit);

        assertTrue(outfit.getItems().containsKey("outer"));
        assertTrue(outfit.getItems().containsKey("accessory"));
    }

    @Test
    void warmDryDayNoOuterAndNoAccessoryEvenIfAvailable() {
        WeatherDay d = makeDay(0, 31, 29, -79.4, 43.7, "2025-07-01");

        ClothingArticle top = article("tshirt", "top", 0, false);
        ClothingArticle bottom = article("shorts", "bottom", 0, false);
        ClothingArticle lightOuter = article("light jacket", "outer", 0, false);
        ClothingArticle hat = article("hat", "accessory", 0, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                List.of(lightOuter),
                List.of(hat)
        );

        Outfit outfit = creator.createOutfitForDay(d, w, false);
        assertNotNull(outfit);

        assertFalse(outfit.getItems().containsKey("outer"));
        assertFalse(outfit.getItems().containsKey("accessory"));
    }

    @Test
    void outerListEmptyTriggersChooseBestWaterproofPath() {
        WeatherDay d = makeDay(61, 12, 10, -79.4, 43.7, "2025-11-30");

        ClothingArticle top = article("tshirt", "top", 1, false);
        ClothingArticle bottom = article("jeans", "bottom", 1, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                new ArrayList<>(),
                null
        );

        Outfit outfit = creator.createOutfitForDay(d, w, false);
        assertNotNull(outfit);
        assertFalse(outfit.getItems().containsKey("outer"));
    }

    @Test
    void nullDayUsesFallbackTargetWarmthAndUnknownLocationAndDate() {
        ClothingArticle top = article("t1", "top", 2, false);
        ClothingArticle bottom = article("b1", "bottom", 2, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                null,
                null
        );

        Outfit outfit = creator.createOutfitForDay(null, w, false);
        assertNotNull(outfit);

        String title = outfit.getTitle();
        assertTrue(title.contains("Unknown date"));
        assertTrue(title.contains("Unknown location"));
    }

    @Test
    void formatLocationHandlesNullAndShortLocationArray() {
        ClothingArticle top = article("t", "top", 2, false);
        ClothingArticle bottom = article("b", "bottom", 2, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                null,
                null
        );

        WeatherDay dNullLoc = new WeatherDay(0, 10, 0, null, "2025-11-27");
        Outfit o1 = creator.createOutfitForDay(dNullLoc, w, false);
        assertTrue(o1.getTitle().contains("Unknown location"));

        WeatherDay dShortLoc = new WeatherDay(0, 10, 0, new double[]{-79.4}, "2025-11-28");
        Outfit o2 = creator.createOutfitForDay(dShortLoc, w, false);
        assertTrue(o2.getTitle().contains("Unknown location"));

        WeatherDay dNormalLoc = new WeatherDay(0, 10, 0, new double[]{-79.4, 43.7}, "2025-11-29");
        Outfit o3 = creator.createOutfitForDay(dNormalLoc, w, false);
        assertFalse(o3.getTitle().contains("Unknown location"));
    }

    @Test
    void tempToWarmthRatingAllBucketsCoveredViaCreateOutfit() {
        ClothingArticle top = article("t", "top", 2, false);
        ClothingArticle bottom = article("b", "bottom", 2, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                null,
                null
        );

        WeatherDay d1 = makeDay(0, -6, -4, 0, 0, "2025-01-01");
        WeatherDay d2 = makeDay(0, 3, 1, 0, 0, "2025-02-01");
        WeatherDay d3 = makeDay(0, 10, 8, 0, 0, "2025-03-01");
        WeatherDay d4 = makeDay(0, 17, 15, 0, 0, "2025-04-01");
        WeatherDay d5 = makeDay(0, 24, 22, 0, 0, "2025-05-01");
        WeatherDay d6 = makeDay(0, 30, 28, 0, 0, "2025-06-01");

        assertNotNull(creator.createOutfitForDay(d1, w, false));
        assertNotNull(creator.createOutfitForDay(d2, w, false));
        assertNotNull(creator.createOutfitForDay(d3, w, false));
        assertNotNull(creator.createOutfitForDay(d4, w, false));
        assertNotNull(creator.createOutfitForDay(d5, w, false));
        assertNotNull(creator.createOutfitForDay(d6, w, false));
    }

    @Test
    void accessoryIncludedWhenColdByTemperatureCondition() {
        WeatherDay d = makeDay(0, 9, 7, 0, 0, "2025-11-27");

        ClothingArticle top = article("t", "top", 2, false);
        ClothingArticle bottom = article("b", "bottom", 2, false);
        ClothingArticle gloves = article("gloves", "accessory", 1, false);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                null,
                List.of(gloves)
        );

        Outfit o = creator.createOutfitForDay(d, w, false);
        assertNotNull(o);
        assertTrue(o.getItems().containsKey("accessory"));
    }

    @Test
    void accessoryIncludedWhenRainingEvenIfWarm() {
        WeatherDay d = makeDay(61, 18, 16, 0, 0, "2025-11-28");

        ClothingArticle top = article("t", "top", 2, true);
        ClothingArticle bottom = article("b", "bottom", 2, false);
        ClothingArticle umbrella = article("umbrella", "accessory", 0, true);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                null,
                List.of(umbrella)
        );

        Outfit o = creator.createOutfitForDay(d, w, false);
        assertNotNull(o);
        assertTrue(o.getItems().containsKey("accessory"));
        assertTrue(o.isWaterproof());
    }
    @Test
    void outerExistsButNotUsedWhenDryAndWarm() {
        // warm & dry => no rainLayer, no warmthLayer
        WeatherDay d = makeDay(0, 20, 19, -79.4, 43.7, "2025-11-30"); // avg=19 → targetWarmth=1

        ClothingArticle top = article("tshirt", "top", 1, false);
        ClothingArticle bottom = article("pants", "bottom", 1, false);

        ClothingArticle unusedOuter = article("jacket", "outer", 1, false);
        ClothingArticle unusedAccessory = article("ring", "accessory", 0, false); // accessory key exists but shouldn’t be used

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                List.of(unusedOuter),     // exists but should not be picked
                List.of(unusedAccessory)  // exists but should not be picked
        );

        Outfit o = creator.createOutfitForDay(d, w, false);
        assertNotNull(o);
        assertFalse(o.getItems().containsKey("outer"));
        assertFalse(o.getItems().containsKey("accessory"));
    }
    @Test
    void rainyDayUsesWaterproofOuterViaFallbackWhenWarmthTooFar() {
        WeatherDay d = makeDay(61, 24, 22, -79.4, 43.7, "2025-12-02");

        ClothingArticle top = article("t", "top", 1, false);
        ClothingArticle bottom = article("b", "bottom", 1, false);
        ClothingArticle farWarmOuter = article("coat", "outer", 5, true);

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                List.of(farWarmOuter),
                null
        );

        Outfit o = creator.createOutfitForDay(d, w, false);
        assertNotNull(o);
        assertTrue(o.getItems().containsKey("outer"));
        assertTrue(o.getItems().get("outer").isWaterproof());
    }
    @Test
    void rainyDayButNoWaterproofMeansOutfitNotWaterproof() {
        WeatherDay d = makeDay(61, 10, 9, -79.4, 43.7, "2025-12-03"); // raining

        ClothingArticle top = article("t", "top", 3, false);
        ClothingArticle bottom = article("b", "bottom", 3, false);
        ClothingArticle outer = article("coat", "outer", 4, false); // not waterproof

        Map<String, List<ClothingArticle>> w = wardrobe(
                List.of(top),
                List.of(bottom),
                List.of(outer),
                null
        );

        Outfit o = creator.createOutfitForDay(d, w, false);

        assertNotNull(o);
        assertFalse(o.isWaterproof());
    }
}
