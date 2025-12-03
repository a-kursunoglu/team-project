package fuze.entity.clothing;

public class ClothingArticle {
    private String name;
    private String category;
    private int weatherRating;
    private boolean waterproof;
    private Photo image;
    private int rating;

    public ClothingArticle(String name, String category, int weatherRating, boolean waterproof, Photo image) {
        this.name = name;
        this.category = category;
        this.weatherRating = weatherRating;
        this.waterproof = waterproof;
        this.image = image;
        this.rating = 0;
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

    public int getRating() { return  rating;}
}
