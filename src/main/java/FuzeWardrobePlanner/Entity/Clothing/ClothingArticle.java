package FuzeWardrobePlanner.Entity.Clothing;

public class ClothingArticle {
    private String name;
    private int weatherRating;
    private boolean waterproof;
    private Photo image;
    private String category;
    public ClothingArticle() {
    }

    public ClothingArticle(String name, String category, int weatherRating, boolean waterproof, Photo image) {
        this.name = name;
        this.category = category;
        this.weatherRating = weatherRating;
        this.waterproof = waterproof;
        this.image = image;
    }

    // getters
    public String getName() {
        return name;
    }

    public int getWeatherRating() {
        return weatherRating;
    }

    public boolean isWaterproof() {
        return waterproof;
    }

    public Photo getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }
}
