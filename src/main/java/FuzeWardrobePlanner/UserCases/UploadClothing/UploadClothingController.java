package FuzeWardrobePlanner.UserCases.UploadClothing;

import FuzeWardrobePlanner.Entity.Clothing.Photo;

public class UploadClothingController {
    private final UploadClothingInputBoundary interactor;

    public UploadClothingController(UploadClothingInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void addClothing(String name,
                            String category,
                            int weatherRating,
                            boolean waterproof,
                            Photo imagePath) {

        UploadClothingInputData inputData = new UploadClothingInputData(
                name, category, weatherRating, waterproof, imagePath
        );

        interactor.execute(inputData);
    }
}
