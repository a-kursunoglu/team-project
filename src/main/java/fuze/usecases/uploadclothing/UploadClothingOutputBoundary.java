package fuze.usecases.uploadclothing;

public interface UploadClothingOutputBoundary {
    void prepareSuccessView(UploadClothingOutputData outputData);
    void prepareFailView(String errorMessage);
}
