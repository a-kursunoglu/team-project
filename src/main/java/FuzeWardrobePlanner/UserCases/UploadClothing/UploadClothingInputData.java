package FuzeWardrobePlanner.UserCases.UploadClothing;

public class UploadClothingInputData {
    private final String name;
    private final String category;
    private final int weatherRating;
    private final boolean waterproof;
    private final String imagePath;

    public UploadClothingInputData(String name, String category, int weatherRating,
                                   boolean waterproof, String imagePath) {
        this.name = name;
        this.category = category;
        this.weatherRating = weatherRating;
        this.waterproof = waterproof;
        this.imagePath = imagePath;
    }
    public String getName() {
        return name;
    }
    public String getCategory() {
        return category;
    }
    public int getWeatherRating() {
        return weatherRating;
    }
    public boolean isWaterproof() {
        return waterproof;
    }
    public String getImagePath() {
        return imagePath;
    }
}
