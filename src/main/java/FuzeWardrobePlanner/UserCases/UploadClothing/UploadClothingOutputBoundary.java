package FuzeWardrobePlanner.UserCases.UploadClothing;

public interface UploadClothingOutputBoundary {
    void prepareSuccessView(UploadClothingOutputData outputData);
    void prepareFailView(String errorMessage);
}
