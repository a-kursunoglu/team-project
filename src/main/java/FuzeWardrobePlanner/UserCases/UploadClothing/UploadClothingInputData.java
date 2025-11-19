package FuzeWardrobePlanner.UserCases.UploadClothing;

import FuzeWardrobePlanner.Entity.Clothing.Photo;

public class UploadClothingInputData {
    private final String name;
    private final String category;
    private final int weatherRating;
    private final boolean waterproof;
    private final String filePath;

    public UploadClothingInputData(String name, String category, int weatherRating,
                                   boolean waterproof, String filePath) {
        this.name = name;
        this.category = category;
        this.weatherRating = weatherRating;
        this.waterproof = waterproof;
        this.filePath = filePath;
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
    public String getFilePath() {
        return filePath;
    }
}
