package FuzeWardrobePlanner.Entity.Clothing;

public class ClothingArticle {
    private String name;
    private String category;
    private int weatherRating;
    private boolean waterproof;
    private Photo image;
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

    public String getCategory() {return category;}

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
