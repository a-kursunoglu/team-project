package fuze.interfaceadapter;

import fuze.usecases.uploadclothing.UploadClothingOutputBoundary;
import fuze.usecases.uploadclothing.UploadClothingOutputData;

public class UploadClothingPresenter implements UploadClothingOutputBoundary {
    private UploadClothingView view;

    public UploadClothingPresenter(UploadClothingView view) {
        this.view = view;
    }

    public void setView(UploadClothingView view) {
        this.view = view;
    }


    @Override
    public void prepareSuccessView(UploadClothingOutputData outputData) {
        view.showSuccess(outputData.getMessage());
    }

    @Override
    public void prepareFailView(String errorMessage) {
        view.showError(errorMessage);
    }

}
