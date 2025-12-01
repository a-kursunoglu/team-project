package fuze.usecases.planweekly;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.entity.clothing.Photo;
import fuze.entity.weather.WeatherDay;
import fuze.entity.weather.WeatherWeek;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyPlannerTest {

    private WeeklyPlanner planner;

    @BeforeAll
    static void enableHeadlessMode() {
        System.setProperty("java.awt.headless", "true");
    }

    @AfterEach
    void tearDown() {
        if (planner != null) {
            planner.dispose();
        }
    }

    @Test
    void nullWeek_populatesPlaceholders() {
        planner = new WeeklyPlanner();

        planner.setWeatherWeek(null);

        JLabel location = getField(planner, "locationLabel", JLabel.class);
        JPanel daysPanel = getField(planner, "daysPanel", JPanel.class);
        assertEquals("Location: -", location.getText());
        assertEquals(7, daysPanel.getComponentCount(), "Should always show 7 day slots");
    }

    @Test
    void rendersOutfitDetailsAndImages() throws Exception {
        planner = new WeeklyPlanner();

        WeatherDay[] days = new WeatherDay[]{
                makeDayWithOutfit("2024-02-01", "Cozy Day", true),
                makeDayWithOutfit("2024-02-02", "Missing Image", false)
        };
        WeatherWeek week = new StubWeatherWeek(days, "Testville");

        planner.setWeatherWeek(week);

        JPanel daysPanel = getField(planner, "daysPanel", JPanel.class);
        JPanel firstCard = (JPanel) daysPanel.getComponent(0);
        JPanel secondCard = (JPanel) daysPanel.getComponent(1);

        JPanel firstOutfitPanel = (JPanel) ((BorderLayout) firstCard.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        JLabel firstOutfitLabel = (JLabel) firstOutfitPanel.getComponent(0);
        assertEquals("Outfit: Cozy Day", firstOutfitLabel.getText());

        JPanel firstItemsPanel = (JPanel) firstOutfitPanel.getComponent(1);
        JPanel firstBadge = (JPanel) firstItemsPanel.getComponent(0);
        JLabel imageLabel = (JLabel) ((BorderLayout) firstBadge.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        assertNotNull(imageLabel.getIcon(), "Badge should render icon when photo data exists");

        JPanel secondOutfitPanel = (JPanel) ((BorderLayout) secondCard.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        JPanel secondItemsPanel = (JPanel) secondOutfitPanel.getComponent(1);
        JPanel secondBadge = (JPanel) secondItemsPanel.getComponent(0);
        JLabel secondImageLabel = (JLabel) ((BorderLayout) secondBadge.getLayout())
                .getLayoutComponent(BorderLayout.CENTER);
        assertEquals("No image", secondImageLabel.getText(), "Missing image should show fallback text");

        JPanel header = (JPanel) ((BorderLayout) firstCard.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        JLabel tempLabel = (JLabel) header.getComponent(1);
        assertTrue(tempLabel.getText().contains("10°"), "Average temperature should be rounded and shown");
    }

    private WeatherDay makeDayWithOutfit(String date, String outfitTitle, boolean includePhoto) throws Exception {
        WeatherDay day = new WeatherDay(0, 12.0, 8.0, new double[]{1.0, 1.0}, date);
        Map<String, ClothingArticle> items = new HashMap<>();
        Photo photo = includePhoto ? photoWithBytes() : new Photo("missing.jpg");
        items.put("top", new ClothingArticle("Topper", "top", 2, false, photo));
        items.put("bottom", new ClothingArticle("Pants", "pant", 2, false, photo));
        Outfit outfit = new Outfit(items, outfitTitle, 4, false);
        day.setOutfit(outfit);
        return day;
    }

    private Photo photoWithBytes() throws Exception {
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(img, "jpg", baos);
        Photo photo = new Photo("");
        Field jpegData = Photo.class.getDeclaredField("jpegData");
        jpegData.setAccessible(true);
        jpegData.set(photo, baos.toByteArray());
        return photo;
    }

    private <T> T getField(Object target, String name, Class<T> type) {
        try {
            Field field = target.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return type.cast(field.get(target));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Failed to access field " + name, e);
        }
    }

    private static class StubWeatherWeek extends WeatherWeek {
        private final WeatherDay[] days;
        private final String location;

        StubWeatherWeek(WeatherDay[] days, String location) {
            super(buildQueue(days), location);
            this.days = days != null ? days : new WeatherDay[0];
            this.location = location;
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
            return location;
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
    }
}
