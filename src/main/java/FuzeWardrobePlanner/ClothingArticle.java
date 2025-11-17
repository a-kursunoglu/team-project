package FuzeWardrobePlanner;

public class ClothingArticle {
    private String name;
    private int weatherRating;
    private boolean waterproof;
    private Photo image;

    public ClothingArticle(String name, int weatherRating, boolean waterproof, Photo image) {
        this.name = name;
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
}
