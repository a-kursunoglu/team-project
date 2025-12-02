package fuze.usecases.planweekly;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.entity.clothing.Photo;
import fuze.entity.weather.WeatherDay;
import fuze.entity.weather.WeatherWeek;
import fuze.usecases.generateoutfit.OutfitCreator;
import fuze.usecases.managewardrobe.WardrobeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyPlannerInteractorTest {

    private WeeklyPlannerInteractor interactor;
    private FakeWardrobeRepository wardrobe;

    @BeforeEach
    void setUp() {
        wardrobe = new FakeWardrobeRepository();
        interactor = new WeeklyPlannerInteractor(wardrobe, new OutfitCreator());
    }

    @Test
    void validFlow_setsOutfitsForAllDays() {
        addBasicWardrobe();
        WeatherDay[] days = buildDays(7);
        WeatherWeek week = new FakeWeatherWeek(days, "Test City");

        interactor.generateOutfitsForWeek(week);

        for (WeatherDay day : days) {
            assertNotNull(day.getOutfit(), "Outfit should be set for each day");
        }
    }

    @Test
    void emptyWardrobe_leavesOutfitsUnset() {
        WeatherDay[] days = buildDays(7);
        WeatherWeek week = new FakeWeatherWeek(days, "Empty Wardrobe City");

        interactor.generateOutfitsForWeek(week);

        for (WeatherDay day : days) {
            assertNull(day.getOutfit(), "Outfit should remain null when wardrobe is empty");
        }
    }

    @Test
    void nullWeek_noException() {
        assertDoesNotThrow(() -> interactor.generateOutfitsForWeek(null));
    }

    @Test
    void nullDays_areSkipped() {
        addBasicWardrobe();
        WeatherDay[] days = buildDays(7);
        days[2] = null;
        days[5] = null;
        WeatherWeek week = new FakeWeatherWeek(days, "Partial City");

        interactor.generateOutfitsForWeek(week);

        assertNull(days[2], "Null days should remain null");
        assertNull(days[5], "Null days should remain null");
        for (int i = 0; i < days.length; i++) {
            if (days[i] != null) {
                assertNotNull(days[i].getOutfit(), "Outfit should be set for non-null days");
            }
        }
    }

    @Test
    void singleOutfitWardrobe_runsNoRepeatFallback() {
        Photo placeholder = new Photo("");
        wardrobe.save(new ClothingArticle("TopOne", "top", 2, false, placeholder));
        wardrobe.save(new ClothingArticle("BottomOne", "bottom", 2, false, placeholder));
        WeatherDay[] days = buildDays(2);
        WeatherWeek week = new FakeWeatherWeek(days, "Repeat City");

        interactor.generateOutfitsForWeek(week);

        assertNotNull(days[0].getOutfit());
        assertNotNull(days[1].getOutfit());
        assertEquals(days[0].getOutfit().getItems().get("top").getName(),
                days[1].getOutfit().getItems().get("top").getName(),
                "When only one outfit exists, it can repeat after fallback");
    }

    @Test
    void missingBottom_keepsOutfitNull() {
        Photo placeholder = new Photo("");
        wardrobe.save(new ClothingArticle("TopOnly", "top", 1, false, placeholder));
        wardrobe.save(null);
        WeatherDay[] days = new WeatherDay[]{
                makeDay(15.0, 10.0, "2024-03-01")
        };
        WeatherWeek week = new FakeWeatherWeek(days, "Top Only City");

        interactor.generateOutfitsForWeek(week);

        assertNull(days[0].getOutfit(), "Outfit should stay null when bottoms are missing");
    }

    @Test
    void filteredWardrobeUsed_whenAlternativesExist() {
        Photo placeholder = new Photo("");
        wardrobe.save(new ClothingArticle("TopPrimary", "top", 1, false, placeholder));
        wardrobe.save(new ClothingArticle("BottomPrimary", "bottom", 1, false, placeholder));
        wardrobe.save(new ClothingArticle("TopAlt", "top", 5, false, placeholder));
        wardrobe.save(new ClothingArticle("BottomAlt", "bottom", 5, false, placeholder));

        WeatherDay[] days = new WeatherDay[]{
                makeDay(20.0, 20.0, "2024-04-01"),
                makeDay(20.0, 20.0, "2024-04-02")
        };
        WeatherWeek week = new FakeWeatherWeek(days, "Fallback City");

        interactor.generateOutfitsForWeek(week);

        Outfit first = days[0].getOutfit();
        Outfit second = days[1].getOutfit();
        assertNotNull(first);
        assertNotNull(second);
        assertEquals("TopPrimary", first.getItems().get("top").getName());
        assertEquals("BottomPrimary", first.getItems().get("bottom").getName());
        assertEquals("TopAlt", second.getItems().get("top").getName(),
                "Second day should fall back to alternate top");
        assertEquals("BottomAlt", second.getItems().get("bottom").getName(),
                "Second day should fall back to alternate bottom");
    }

    @Test
    void colderWeather_addsLayersAndAccessories() {
        Photo placeholder = new Photo("");
        wardrobe.save(new ClothingArticle("LightTop", "top", 1, false, placeholder));
        wardrobe.save(new ClothingArticle("LightBottom", "bottom", 1, false, placeholder));
        wardrobe.save(new ClothingArticle("WarmTop", "top", 5, false, placeholder));
        wardrobe.save(new ClothingArticle("WarmBottom", "bottom", 5, false, placeholder));
        wardrobe.save(new ClothingArticle("HeavyCoat", "outer", 5, false, placeholder));
        wardrobe.save(new ClothingArticle("Scarf", "accessory", 3, false, placeholder));

        WeatherDay[] days = new WeatherDay[]{
                makeDay(18.0, 18.0, "2024-05-01"),
                makeDay(0.0, 0.0, "2024-05-02")
        };
        WeatherWeek week = new FakeWeatherWeek(days, "Accessory City");

        interactor.generateOutfitsForWeek(week);

        Outfit coldDay = days[1].getOutfit();
        assertNotNull(days[0].getOutfit());
        assertNotNull(coldDay);
        assertTrue(coldDay.getItems().containsKey("accessory"),
                "Cold weather should include an accessory");
    }

    @Test
    void nullOutfitFromCreator_leavesDayUnset() {
        Photo placeholder = new Photo("");
        wardrobe.save(new ClothingArticle("Topper", "top", 1, false, placeholder));
        wardrobe.save(new ClothingArticle("Pants", "pant", 1, false, placeholder));
        WeeklyPlannerInteractor customInteractor = new WeeklyPlannerInteractor(
                wardrobe, new SequenceOutfitCreator(Arrays.asList((Outfit) null)));

        WeatherDay[] days = buildDays(1);
        WeatherWeek week = new FakeWeatherWeek(days, "Null City");

        customInteractor.generateOutfitsForWeek(week);

        assertNull(days[0].getOutfit(), "Outfit should stay null when creator returns null");
    }

    @Test
    void repeatOutfitTriggersRetry() {
        Photo placeholder = new Photo("");
        ClothingArticle topPrimary = new ClothingArticle("TopPrimary", "top", 2, false, placeholder);
        ClothingArticle bottomPrimary = new ClothingArticle("BottomPrimary", "pant", 2, false, placeholder);
        ClothingArticle topAlt = new ClothingArticle("TopAlt", "top", 2, false, placeholder);
        ClothingArticle bottomAlt = new ClothingArticle("BottomAlt", "pant", 2, false, placeholder);
        wardrobe.save(topPrimary);
        wardrobe.save(bottomPrimary);
        wardrobe.save(topAlt);
        wardrobe.save(bottomAlt);

        Outfit first = buildOutfit("First", topPrimary, bottomPrimary);
        Outfit alternate = buildOutfit("Alt", topAlt, bottomAlt);
        WeeklyPlannerInteractor customInteractor = new WeeklyPlannerInteractor(
                wardrobe, new SequenceOutfitCreator(Arrays.asList(first, first, alternate)));

        WeatherDay[] days = buildDays(2);
        WeatherWeek week = new FakeWeatherWeek(days, "Retry City");

        customInteractor.generateOutfitsForWeek(week);

        assertSame(first, days[0].getOutfit(), "First day uses initial outfit");
        assertSame(alternate, days[1].getOutfit(), "Second day should switch to alternate outfit");
    }

    @Test
    void fallbackReturnsFirstWhenAllCandidatesRepeat() {
        Photo placeholder = new Photo("");
        ClothingArticle top = new ClothingArticle("RepeatTop", "top", 2, false, placeholder);
        ClothingArticle bottom = new ClothingArticle("RepeatBottom", "pant", 2, false, placeholder);
        wardrobe.save(top);
        wardrobe.save(bottom);

        Outfit repeat = buildOutfit("Repeat", top, bottom);
        List<Outfit> sequence = Arrays.asList(
                repeat, repeat, repeat, repeat, repeat, repeat, repeat
        );
        WeeklyPlannerInteractor customInteractor = new WeeklyPlannerInteractor(
                wardrobe, new SequenceOutfitCreator(sequence));

        WeatherDay[] days = buildDays(2);
        WeatherWeek week = new FakeWeatherWeek(days, "Repeat City");

        customInteractor.generateOutfitsForWeek(week);

        assertSame(repeat, days[1].getOutfit(), "When all retries fail, interactor reuses the first outfit");
    }

    @Test
    void privateHelpers_coverBranchyPaths() throws Exception {
        WeeklyPlannerInteractor instance = new WeeklyPlannerInteractor(wardrobe, new OutfitCreator());

        Map<String, List<ClothingArticle>> wardrobeMap = new HashMap<>();
        wardrobeMap.put("top", List.of(new ClothingArticle("T", "top", 1, false, new Photo(""))));
        wardrobeMap.put("bottom", new ArrayList<>());
        wardrobeMap.put("outer", null);
        wardrobeMap.put("accessory", new ArrayList<>());
        boolean isEmpty = invokePrivate(instance, "isWardrobeEmpty",
                new Class[]{Map.class}, wardrobeMap);
        assertFalse(isEmpty, "Non-empty category should make wardrobe non-empty");

        Set<String> prevNames = new HashSet<>(List.of("T", "B"));
        Outfit repeat = buildOutfit("Repeat", new ClothingArticle("T", "top", 1, false, new Photo("")),
                new ClothingArticle("B", "bottom", 1, false, new Photo("")));
        boolean sameTopBottom = invokePrivate(instance, "isSameTopOrBottom",
                new Class[]{Outfit.class, Set.class}, repeat, prevNames);
        assertTrue(sameTopBottom, "Matching top and bottom should return true");

        Set<String> extracted = invokePrivate(instance, "extractNames",
                new Class[]{Outfit.class}, new Outfit(new HashMap<>() {{
                    put("top", new ClothingArticle("T", "top", 1, false, new Photo("")));
                    put("bottom", null);
                    put("outer", new ClothingArticle(null, "outer", 1, false, new Photo("")));
                }}, "Any", 0, false));
        assertEquals(Set.of("T"), extracted, "Should ignore null articles or names");

        Outfit outfitA = new Outfit(null, "A", 0, false);
        Outfit outfitB = buildOutfit("B", new ClothingArticle("X", "top", 1, false, new Photo("")),
                new ClothingArticle("Y", "bottom", 1, false, new Photo("")));
        boolean sameOutfitNullItems = invokePrivate(instance, "isSameOutfit",
                new Class[]{Outfit.class, Outfit.class}, outfitA, outfitB);
        assertFalse(sameOutfitNullItems, "Null item maps should not be considered equal");

        outfitA.setItems(new HashMap<>() {{
            put("top", new ClothingArticle("X", "top", 1, false, new Photo("")));
        }});
        outfitB.setItems(new HashMap<>() {{
            put("top", new ClothingArticle("X", "top", 1, false, new Photo("")));
            put("bottom", new ClothingArticle("Y", "bottom", 1, false, new Photo("")));
        }});
        boolean sameOutfitMismatchedKeys = invokePrivate(instance, "isSameOutfit",
                new Class[]{Outfit.class, Outfit.class}, outfitA, outfitB);
        assertFalse(sameOutfitMismatchedKeys, "Different item keys should not match");

        List<ClothingArticle> rawItems = List.of(
                new ClothingArticle("TT", "Topper", 1, false, new Photo("")),
                new ClothingArticle("BB", "pant", 1, false, new Photo("")),
                new ClothingArticle("OO", "Outerwear", 1, false, new Photo("")),
                new ClothingArticle("AA", "Accessory", 1, false, new Photo(""))
        );
        Map<String, List<ClothingArticle>> mapped = invokePrivate(instance, "buildWardrobeMap",
                new Class[]{List.class}, rawItems);
        assertEquals(1, mapped.get("top").size());
        assertEquals(1, mapped.get("bottom").size());
        assertEquals(1, mapped.get("outer").size());
        assertEquals(1, mapped.get("accessory").size());

        boolean sameOutfitPositive = invokePrivate(instance, "isSameOutfit",
                new Class[]{Outfit.class, Outfit.class},
                buildOutfit("One", new ClothingArticle("X", "top", 1, false, new Photo("")),
                        new ClothingArticle("Y", "bottom", 1, false, new Photo(""))),
                buildOutfit("Two", new ClothingArticle("X", "top", 1, false, new Photo("")),
                        new ClothingArticle("Y", "bottom", 1, false, new Photo(""))));
        assertTrue(sameOutfitPositive, "Identical items should be considered the same outfit");

        boolean sameTopBottomNullPrev = invokePrivate(instance, "isSameTopOrBottom",
                new Class[]{Outfit.class, Set.class},
                buildOutfit("Repeat", new ClothingArticle("X", "top", 1, false, new Photo("")),
                        new ClothingArticle("Y", "bottom", 1, false, new Photo(""))),
                null);
        assertFalse(sameTopBottomNullPrev, "Null previous set should not flag as repeat");

        boolean wardrobeEmpty = invokePrivate(instance, "isWardrobeEmpty",
                new Class[]{Map.class}, new HashMap<>() {{
                    put("top", new ArrayList<>());
                    put("bottom", new ArrayList<>());
                    put("outer", new ArrayList<>());
                    put("accessory", new ArrayList<>());
                }});
        assertTrue(wardrobeEmpty, "All empty lists should count as empty wardrobe");

        boolean sameOutfitWithNull = invokePrivate(instance, "isSameOutfit",
                new Class[]{Outfit.class, Outfit.class}, buildOutfit("One", null, null), null);
        assertFalse(sameOutfitWithNull, "Null comparison should return false");

        boolean sameTopBottomNullOutfit = invokePrivate(instance, "isSameTopOrBottom",
                new Class[]{Outfit.class, Set.class}, null, new HashSet<>());
        assertFalse(sameTopBottomNullOutfit, "Null outfit should not cause repeat");

        Map<String, List<ClothingArticle>> mappedWithNulls = invokePrivate(instance, "buildWardrobeMap",
                new Class[]{List.class}, Arrays.asList(
                        new ClothingArticle("TT", "Top", 1, false, new Photo("")),
                        null,
                        new ClothingArticle("??", null, 1, false, new Photo(""))
                ));
        assertEquals(1, mappedWithNulls.get("top").size(), "Null items/categories should be skipped");
    }

    private void addBasicWardrobe() {
        Photo placeholder = new Photo("");
        wardrobe.save(new ClothingArticle("TopOne", "top", 2, false, placeholder));
        wardrobe.save(new ClothingArticle("BottomOne", "bottom", 2, false, placeholder));
        wardrobe.save(new ClothingArticle("JacketOne", "outer", 3, false, placeholder));
        wardrobe.save(new ClothingArticle("Scarf", "accessory", 1, false, placeholder));
    }

    private WeatherDay[] buildDays(int count) {
        WeatherDay[] days = new WeatherDay[count];
        for (int i = 0; i < count; i++) {
            days[i] = new WeatherDay(0, 12.0, 6.0, new double[]{0.0, 0.0}, "2024-01-0" + (i + 1));
        }
        return days;
    }

    private WeatherDay makeDay(double high, double low, String date) {
        return new WeatherDay(0, high, low, new double[]{0.0, 0.0}, date);
    }

    private Outfit buildOutfit(String title, ClothingArticle top, ClothingArticle bottom) {
        Map<String, ClothingArticle> items = new HashMap<>();
        items.put("top", top);
        items.put("bottom", bottom);
        return new Outfit(items, title, 0, false);
    }

    @SuppressWarnings("unchecked")
    private <T> T invokePrivate(WeeklyPlannerInteractor instance, String name,
                                Class<?>[] paramTypes, Object... args) throws Exception {
        Method m = WeeklyPlannerInteractor.class.getDeclaredMethod(name, paramTypes);
        m.setAccessible(true);
        return (T) m.invoke(instance, args);
    }

    private static class FakeWardrobeRepository implements WardrobeRepository {
        private final List<ClothingArticle> items = new ArrayList<>();

        @Override
        public void save(ClothingArticle article) {
            items.add(article);
        }

        @Override
        public boolean existsByName(String name) {
            return items.stream().anyMatch(item -> item != null && item.getName().equalsIgnoreCase(name));
        }

        @Override
        public List<ClothingArticle> getAll() {
            return new ArrayList<>(items);
        }

        @Override
        public boolean deleteByName(String name) {
            return items.removeIf(item -> item != null && item.getName().equalsIgnoreCase(name));
        }
    }

    private static class SequenceOutfitCreator extends OutfitCreator {
        private final List<Outfit> queue;
        private int index = 0;

        SequenceOutfitCreator(List<Outfit> outfits) {
            this.queue = outfits != null ? new ArrayList<>(outfits) : new ArrayList<>();
        }

        @Override
        public Outfit createOutfitForDay(WeatherDay day, Map<String, List<ClothingArticle>> wardrobe,
                                         boolean isRainingOverride) {
            if (index >= queue.size()) {
                return null;
            }
            return queue.get(index++);
        }
    }

    private static class FakeWeatherWeek extends WeatherWeek {
        private final WeatherDay[] days;
        private final String defaultLocation;

        FakeWeatherWeek(WeatherDay[] days, String defaultLocation) {
            super(buildQueue(days), defaultLocation);
            this.days = days != null ? days : new WeatherDay[0];
            this.defaultLocation = defaultLocation;
        }

        private static Queue<WeatherDay> buildQueue(WeatherDay[] days) {
            Queue<WeatherDay> queue = new LinkedList<>();
            if (days != null) {
                for (WeatherDay day : days) {
                    if (day != null) {
                        queue.add(day);
                    }
                }
            }
            return queue;
        }

        @Override
        public WeatherDay getWeatherDay(int index) {
            if (index < 0 || index >= days.length) {
                return null;
            }
            return days[index];
        }

        @Override
        public String getDefaultLocation() {
            return defaultLocation;
        }
    }
}
