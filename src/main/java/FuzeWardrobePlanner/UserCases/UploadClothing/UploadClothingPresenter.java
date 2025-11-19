package FuzeWardrobePlanner.UserCases.UploadClothing;

public class UploadClothingPresenter implements UploadClothingOutputBoundary {
    private final UploadClothingView view;

    public UploadClothingPresenter(UploadClothingView view) {
        this.view = view;
    }

    @Override
    public void prepareSuccessView(UploadClothingOutputData outputData) {
        // You can also trigger a refresh of the wardrobe list here if needed
        view.showSuccess(outputData.getMessage());
    }

    @Override
    public void prepareFailView(String errorMessage) {
        view.showError(errorMessage);
    }

}
