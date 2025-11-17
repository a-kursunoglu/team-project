package FuzeWardrobePlanner.Entity.Clothing;

import java.util.Map;

public class Outfit {
    private Map<String, ClothingArticle> items;
    private String title;
    private int weatherRating;
    private boolean waterproof;

    public Outfit(Map<String, ClothingArticle> items, String title, int weatherRating, boolean waterproof) {
        this.items = items;
        this.title = title;
        this.weatherRating = weatherRating;
        this.waterproof = waterproof;
    }

    public Map<String, ClothingArticle> getItems() {
        return items;
    }

    public void setItems(Map<String, ClothingArticle> items) {
        this.items = items;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getWeatherRating() {
        return weatherRating;
    }

    public void setWeatherRating(int weatherRating) {
        this.weatherRating = weatherRating;
    }

    public boolean isWaterproof() {
        return waterproof;
    }

    public void setWaterproof(boolean waterproof) {
        this.waterproof = waterproof;
    }

    @Override
    public String toString() {
        return "Outfit{" +
                "items=" + items +
                ", title='" + title + '\'' +
                ", weatherRating=" + weatherRating +
                ", waterproof=" + waterproof +
                '}';
    }
}
