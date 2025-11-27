package fuze.use_cases.upload_clothing;

public interface UploadClothingOutputBoundary {
    void prepareSuccessView(UploadClothingOutputData outputData);
    void prepareFailView(String errorMessage);
}
