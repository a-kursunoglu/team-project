package fuze.usecases.trippacking;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.entity.weather.WeatherDay;
import fuze.entity.weather.WeatherTrip;
import fuze.entity.weather.WeatherWeek;
import fuze.usecases.generateoutfit.OutfitCreator;
import fuze.usecases.managewardrobe.WardrobeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TripPlannerInteractorTest {

    @Test
    void validateDates_success() {
        TripPlannerInteractor interactor = new TripPlannerInteractor(null, null);
        LocalDate[] result = interactor.validateDates("2025-01-01", "2025-01-05");
        assertEquals(2, result.length);
        assertEquals(LocalDate.of(2025, 1, 1), result[0]);
        assertEquals(LocalDate.of(2025, 1, 5), result[1]);
    }

    @Test
    void validateDates_endBeforeStart_throws() {
        TripPlannerInteractor interactor = new TripPlannerInteractor(null, null);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> interactor.validateDates("2025-01-05", "2025-01-01")
        );
        assertEquals("End date cannot be before start date", ex.getMessage());
    }

    @Test
    void validateDates_badFormat_throws() {
        TripPlannerInteractor interactor = new TripPlannerInteractor(null, null);
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> interactor.validateDates("2025/01/01", "2025-01-05")
        );
        assertEquals("Dates must be in format yyyy-mm-dd.", ex.getMessage());
    }

    @Test
    void generateTripPlan_usesWeatherTripAndOutfitCreator() {
        WeatherDay day = new WeatherDay(0, 20.0, 10.0, new double[]{1.0, 2.0}, "2025-01-01");
        try (MockedConstruction<WeatherTrip> ignored =
                     mockConstruction(WeatherTrip.class, (mock, context) -> when(mock.getWeatherDay(0)).thenReturn(day))) {

            List<ClothingArticle> items = Arrays.asList(
                    new ClothingArticle("T1", "top", 2, false, null),
                    new ClothingArticle("B1", "bottom", 2, false, null)
            );
            WardrobeRepository repo = new FakeWardrobeRepository(items);
            OutfitCreator creator = new ConstantOutfitCreator(outfitWithTopBottom("TopX", "BottomX"));
            TripPlannerInteractor interactor = new TripPlannerInteractor(repo, creator);

            List<WeatherDay> result = interactor.generateTripPlan("Loc", LocalDate.of(2025, 1, 1), 1);

            assertEquals(1, result.size());
            assertSame(day, result.get(0));
            assertNotNull(result.get(0).getOutfit());
        }
    }

    @Test
    void generateTripPlan_fallsBackToWeatherWeekWhenTripReturnsNull() {
        WeatherDay weekDay = new WeatherDay(0, 25.0, 15.0, new double[]{3.0, 4.0}, "2025-01-01");
        try (MockedConstruction<WeatherTrip> ignored =
                     mockConstruction(WeatherTrip.class, (mock, context) -> when(mock.getWeatherDay(0)).thenReturn(null));
             MockedConstruction<WeatherWeek> ignored1 =
                     mockConstruction(WeatherWeek.class, (mock, context) -> when(mock.getWeatherDay(0)).thenReturn(weekDay))) {

            TripPlannerInteractor interactor =
                    new TripPlannerInteractor(null, new ConstantOutfitCreator(outfitWithTopBottom("A", "B")));

            List<WeatherDay> result = interactor.generateTripPlan("Loc", LocalDate.of(2025, 1, 1), 1);

            assertEquals(1, result.size());
            assertSame(weekDay, result.get(0));
            assertNull(result.get(0).getOutfit());
        }
    }

    @Test
    void generateTripPlan_zeroDaysReturnsEmptyList() {
        try (MockedConstruction<WeatherTrip> ignored = mockConstruction(WeatherTrip.class)) {
            TripPlannerInteractor interactor =
                    new TripPlannerInteractor(null, new ConstantOutfitCreator(outfitWithTopBottom("A", "B")));
            List<WeatherDay> result = interactor.generateTripPlan("Loc", LocalDate.of(2025, 1, 1), 0);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void buildWardrobeMap_categorizesCorrectly() throws Exception {
        TripPlannerInteractor interactor = new TripPlannerInteractor(null, null);

        ClothingArticle top = new ClothingArticle("Top", "TOP", 1, false, null);
        ClothingArticle bottom1 = new ClothingArticle("Bottom1", "bottom", 1, false, null);
        ClothingArticle bottom2 = new ClothingArticle("Bottom2", "pants", 1, false, null);
        ClothingArticle outer = new ClothingArticle("Outer", "outerwear", 1, false, null);
        ClothingArticle accessory = new ClothingArticle("Accessory", "accessories", 1, false, null);
        ClothingArticle other = new ClothingArticle("Other", "random", 1, false, null);

        List<ClothingArticle> items = Arrays.asList(
                null,
                new ClothingArticle("NoCat", null, 0, false, null),
                top,
                bottom1,
                bottom2,
                outer,
                accessory,
                other
        );

        Map<String, List<ClothingArticle>> map =
                (Map<String, List<ClothingArticle>>) invokePrivate(
                        interactor,
                        "buildWardrobeMap",
                        new Class[]{List.class},
                        items
                );

        assertEquals(1, map.get("top").size());
        assertEquals(2, map.get("bottom").size());
        assertEquals(1, map.get("outer").size());
        assertEquals(1, map.get("accessory").size());
        assertTrue(map.get("top").contains(top));
        assertTrue(map.get("bottom").contains(bottom1));
        assertTrue(map.get("bottom").contains(bottom2));
        assertTrue(map.get("outer").contains(outer));
        assertTrue(map.get("accessory").contains(accessory));
        assertFalse(map.get("top").contains(other));
        assertFalse(map.get("bottom").contains(other));
        assertFalse(map.get("outer").contains(other));
        assertFalse(map.get("accessory").contains(other));
    }

    @Test
    void generateOutfitWithNoRepeat_firstNullReturnsNull() throws Exception {
        TripPlannerInteractor interactor = new TripPlannerInteractor(null, new NullOutfitCreator());
        WeatherDay day = new WeatherDay(0, 20.0, 10.0, new double[]{0.0, 0.0}, "2025-01-01");

        Map<String, List<ClothingArticle>> wardrobe = new HashMap<>();
        Set<String> prev = new HashSet<>();

        Outfit result = (Outfit) invokePrivate(
                interactor,
                "generateOutfitWithNoRepeat",
                new Class[]{WeatherDay.class, Map.class, Set.class},
                day,
                wardrobe,
                prev
        );

        assertNull(result);
    }

    @Test
    void generateOutfitWithNoRepeat_firstDifferentReturnsFirst() throws Exception {
        Outfit baseOutfit = outfitWithTopBottom("X", "Y");
        TripPlannerInteractor interactor = new TripPlannerInteractor(null, new ConstantOutfitCreator(baseOutfit));
        WeatherDay day = new WeatherDay(0, 20.0, 10.0, new double[]{0.0, 0.0}, "2025-01-01");

        Map<String, List<ClothingArticle>> wardrobe = new HashMap<>();
        Set<String> prev = new HashSet<>();

        Outfit result = (Outfit) invokePrivate(
                interactor,
                "generateOutfitWithNoRepeat",
                new Class[]{WeatherDay.class, Map.class, Set.class},
                day,
                wardrobe,
                prev
        );

        assertSame(baseOutfit, result);
    }

    @Test
    void generateOutfitWithNoRepeat_retryReturnsNonRepeatingCandidate() throws Exception {
        Outfit repeating = outfitWithTopBottom("A", "B");
        Outfit different = outfitWithTopBottom("C", "D");

        TripPlannerInteractor interactor =
                new TripPlannerInteractor(null, new QueueOutfitCreator(Arrays.asList(repeating, different)));

        WeatherDay day = new WeatherDay(0, 20.0, 10.0, new double[]{0.0, 0.0}, "2025-01-01");

        Map<String, List<ClothingArticle>> wardrobe = new HashMap<>();
        Set<String> prev = new HashSet<>(Arrays.asList("A", "B"));

        Outfit result = (Outfit) invokePrivate(
                interactor,
                "generateOutfitWithNoRepeat",
                new Class[]{WeatherDay.class, Map.class, Set.class},
                day,
                wardrobe,
                prev
        );

        assertSame(different, result);
    }

    @Test
    void generateOutfitWithNoRepeat_lastTryNonNullReturnsLastTry() throws Exception {
        Outfit repeating = outfitWithTopBottom("A", "B");
        Outfit last = outfitWithTopBottom("X", "Y");

        List<Outfit> seq = Arrays.asList(
                repeating, repeating, repeating, repeating, repeating, last
        );

        TripPlannerInteractor interactor =
                new TripPlannerInteractor(null, new QueueOutfitCreator(seq));

        WeatherDay day = new WeatherDay(0, 20.0, 10.0, new double[]{0.0, 0.0}, "2025-01-01");

        ClothingArticle t1 = new ClothingArticle("A", "top", 1, false, null);
        ClothingArticle t2 = new ClothingArticle("X", "top", 1, false, null);
        ClothingArticle b1 = new ClothingArticle("B", "bottom", 1, false, null);
        ClothingArticle b2 = new ClothingArticle("Y", "bottom", 1, false, null);

        Map<String, List<ClothingArticle>> wardrobe = new HashMap<>();
        wardrobe.put("top", Arrays.asList(t1, t2));
        wardrobe.put("bottom", Arrays.asList(b1, b2));

        Set<String> prev = new HashSet<>(Arrays.asList("A", "B"));

        Outfit result = (Outfit) invokePrivate(
                interactor,
                "generateOutfitWithNoRepeat",
                new Class[]{WeatherDay.class, Map.class, Set.class},
                day,
                wardrobe,
                prev
        );

        assertNotNull(result);
        assertEquals("X", result.getItems().get("top").getName());
        assertEquals("Y", result.getItems().get("bottom").getName());
    }

    @Test
    void generateOutfitWithNoRepeat_lastTryNullReturnsFirst() throws Exception {
        Outfit repeating = outfitWithTopBottom("A", "B");
        List<Outfit> seq = Arrays.asList(
                repeating, repeating, repeating, repeating, repeating, null
        );

        TripPlannerInteractor interactor =
                new TripPlannerInteractor(null, new QueueOutfitCreator(seq));

        WeatherDay day = new WeatherDay(0, 20.0, 10.0, new double[]{0.0, 0.0}, "2025-01-01");

        Map<String, List<ClothingArticle>> wardrobe = new HashMap<>();
        wardrobe.put("top", Collections.singletonList(
                new ClothingArticle("A", "top", 1, false, null)));
        wardrobe.put("bottom", Collections.singletonList(
                new ClothingArticle("B", "bottom", 1, false, null)));

        Set<String> prev = new HashSet<>(Arrays.asList("A", "B"));

        Outfit result = (Outfit) invokePrivate(
                interactor,
                "generateOutfitWithNoRepeat",
                new Class[]{WeatherDay.class, Map.class, Set.class},
                day,
                wardrobe,
                prev
        );

        assertSame(repeating, result);
    }

    @Test
    void generateOutfitWithNoRepeat_previousNamesNullTreatsAsDifferent() throws Exception {
        Outfit baseOutfit = outfitWithTopBottom("X", "Y");
        TripPlannerInteractor interactor =
                new TripPlannerInteractor(null, new ConstantOutfitCreator(baseOutfit));

        WeatherDay day = new WeatherDay(0, 20.0, 10.0, new double[]{0.0, 0.0}, "2025-01-01");

        Map<String, List<ClothingArticle>> wardrobe = new HashMap<>();

        Outfit result = (Outfit) invokePrivate(
                interactor,
                "generateOutfitWithNoRepeat",
                new Class[]{WeatherDay.class, Map.class, Set.class},
                day,
                wardrobe,
                null
        );

        assertSame(baseOutfit, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void extractNames_handlesNullsAndCollectsValidNames() throws Exception {
        TripPlannerInteractor interactor = new TripPlannerInteractor(null, null);

        Map<String, ClothingArticle> map = new HashMap<>();
        map.put("a", null);
        map.put("b", new ClothingArticle(null, "top", 1, false, null));
        map.put("c", new ClothingArticle("X", "top", 1, false, null));
        Outfit outfit = new Outfit(map, "", 0, false);

        Set<String> names = (Set<String>) invokePrivate(
                interactor,
                "extractNames",
                new Class[]{Outfit.class},
                outfit
        );

        assertEquals(1, names.size());
        assertTrue(names.contains("X"));

        Set<String> empty1 = (Set<String>) invokePrivate(
                interactor,
                "extractNames",
                new Class[]{Outfit.class},
                new Object[]{null}
        );
        assertTrue(empty1.isEmpty());

        Outfit outfit2 = new Outfit(null, "", 0, false);
        Set<String> empty2 = (Set<String>) invokePrivate(
                interactor,
                "extractNames",
                new Class[]{Outfit.class},
                outfit2
        );
        assertTrue(empty2.isEmpty());
    }

    @Test
    void isSameTopOrBottom_variousCases() throws Exception {
        TripPlannerInteractor interactor = new TripPlannerInteractor(null, null);

        Boolean r1 = (Boolean) invokePrivate(
                interactor,
                "isSameTopOrBottom",
                new Class[]{Outfit.class, Set.class},
                null,
                new HashSet<>()
        );
        assertFalse(r1);

        Outfit o = outfitWithTopBottom("A", "B");
        Boolean r2 = (Boolean) invokePrivate(
                interactor,
                "isSameTopOrBottom",
                new Class[]{Outfit.class, Set.class},
                o,
                null
        );
        assertFalse(r2);

        Outfit o2 = new Outfit(null, "", 0, false);
        Boolean r3 = (Boolean) invokePrivate(
                interactor,
                "isSameTopOrBottom",
                new Class[]{Outfit.class, Set.class},
                o2,
                new HashSet<>(Arrays.asList("A", "B"))
        );
        assertFalse(r3);

        Outfit o3 = outfitWithTopBottom("A", "C");
        Boolean r4 = (Boolean) invokePrivate(
                interactor,
                "isSameTopOrBottom",
                new Class[]{Outfit.class, Set.class},
                o3,
                new HashSet<>(Arrays.asList("A", "B"))
        );
        assertFalse(r4);

        Outfit o4 = outfitWithTopBottom("A", "B");
        Boolean r5 = (Boolean) invokePrivate(
                interactor,
                "isSameTopOrBottom",
                new Class[]{Outfit.class, Set.class},
                o4,
                new HashSet<>(Arrays.asList("A", "B"))
        );
        assertTrue(r5);

        Map<String, ClothingArticle> map = new HashMap<>();
        map.put("bottom", new ClothingArticle("B", "bottom", 1, false, null));
        Outfit o5 = new Outfit(map, "", 0, false);
        Boolean r6 = (Boolean) invokePrivate(
                interactor,
                "isSameTopOrBottom",
                new Class[]{Outfit.class, Set.class},
                o5,
                new HashSet<>(Collections.singletonList("B"))
        );
        assertFalse(r6);
    }

    private static Object invokePrivate(TripPlannerInteractor interactor,
                                        String name,
                                        Class<?>[] types,
                                        Object... args) throws Exception {
        Method m = TripPlannerInteractor.class.getDeclaredMethod(name, types);
        m.setAccessible(true);
        return m.invoke(interactor, args);
    }

    private static Outfit outfitWithTopBottom(String topName, String bottomName) {
        ClothingArticle top = new ClothingArticle(topName, "top", 1, false, null);
        ClothingArticle bottom = new ClothingArticle(bottomName, "bottom", 1, false, null);
        Map<String, ClothingArticle> map = new HashMap<>();
        map.put("top", top);
        map.put("bottom", bottom);
        return new Outfit(map, "", top.getWeatherRating() + bottom.getWeatherRating(), false);
    }

    static class FakeWardrobeRepository implements WardrobeRepository {
        private final List<ClothingArticle> items;

        FakeWardrobeRepository(List<ClothingArticle> items) {
            this.items = items;
        }

        @Override
        public void save(ClothingArticle article) {
            items.add(article);
        }

        @Override
        public boolean existsByName(String name) {
            return false;
        }

        @Override
        public List<ClothingArticle> getAll() {
            return items;
        }

        @Override
        public boolean deleteByName(String name) {
            return false;
        }
    }

    static class ConstantOutfitCreator extends OutfitCreator {
        private final Outfit outfit;

        ConstantOutfitCreator(Outfit outfit) {
            this.outfit = outfit;
        }

        @Override
        public Outfit createOutfitForDay(WeatherDay day,
                                         Map<String, List<ClothingArticle>> wardrobe,
                                         boolean isRainingOverride) {
            return outfit;
        }
    }

    static class NullOutfitCreator extends OutfitCreator {
        @Override
        public Outfit createOutfitForDay(WeatherDay day,
                                         Map<String, List<ClothingArticle>> wardrobe,
                                         boolean isRainingOverride) {
            return null;
        }
    }

    static class QueueOutfitCreator extends OutfitCreator {
        private final List<Outfit> outfits;
        private int index;

        QueueOutfitCreator(List<Outfit> outfits) {
            this.outfits = outfits;
        }

        @Override
        public Outfit createOutfitForDay(WeatherDay day,
                                         Map<String, List<ClothingArticle>> wardrobe,
                                         boolean isRainingOverride) {
            if (index < outfits.size()) {
                Outfit o = outfits.get(index);
                index++;
                return o;
            }
            return null;
        }
    }
}
