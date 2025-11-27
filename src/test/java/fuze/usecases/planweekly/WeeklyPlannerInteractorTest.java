package fuze.usecases.planweekly;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.entity.weather.WeatherDay;
import fuze.entity.weather.WeatherWeek;
import fuze.usecases.generateoutfit.OutfitCreator;
import fuze.usecases.managewardrobe.WardrobeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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

    private void addBasicWardrobe() {
        Photo placeholder = new Photo("");
        wardrobe.save(new ClothingArticle("TopOne", "top", 2, false, placeholder));
        wardrobe.save(new ClothingArticle("BottomOne", "bottom", 2, false, placeholder));
        wardrobe.save(new ClothingArticle("JacketOne", "outer", 3, false, placeholder));
    }

    private WeatherDay[] buildDays(int count) {
        WeatherDay[] days = new WeatherDay[count];
        for (int i = 0; i < count; i++) {
            days[i] = new WeatherDay(0, 12.0, 6.0, new double[]{0.0, 0.0}, "2024-01-0" + (i + 1));
        }
        return days;
    }

    private static class FakeWardrobeRepository implements WardrobeRepository {
        private final List<ClothingArticle> items = new ArrayList<>();

        @Override
        public void save(ClothingArticle article) {
            items.add(article);
        }

        @Override
        public boolean existsByName(String name) {
            return items.stream().anyMatch(item -> item.getName().equalsIgnoreCase(name));
        }

        @Override
        public List<ClothingArticle> getAll() {
            return new ArrayList<>(items);
        }

        @Override
        public void deleteByName(String name) {
            items.removeIf(item -> item.getName().equalsIgnoreCase(name));
        }
    }

    private static class FakeWeatherWeek extends WeatherWeek {
        private final WeatherDay[] days;
        private final String defaultLocation;

        FakeWeatherWeek(WeatherDay[] days, String defaultLocation) {
            super();
            this.days = days;
            this.defaultLocation = defaultLocation;
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
