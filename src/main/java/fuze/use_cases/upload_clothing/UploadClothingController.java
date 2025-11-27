package fuze.use_cases.upload_clothing;

public class UploadClothingController {
    private final UploadClothingInputBoundary interactor;

    public UploadClothingController(UploadClothingInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void addClothing(String name,
                            String category,
                            int weatherRating,
                            boolean waterproof,
                            String filePath) {

        UploadClothingInputData inputData = new UploadClothingInputData(
                name, category, weatherRating, waterproof, filePath
        );

        interactor.execute(inputData);
    }
}
