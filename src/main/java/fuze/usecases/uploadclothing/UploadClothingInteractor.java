package fuze.usecases.uploadclothing;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.usecases.managewardrobe.WardrobeRepository;

public class UploadClothingInteractor implements UploadClothingInputBoundary {
    private final WardrobeRepository wardrobeDataAccess;
    private final UploadClothingOutputBoundary presenter;

    public UploadClothingInteractor(WardrobeRepository wardrobeDataAccess,
                                    UploadClothingOutputBoundary presenter) {
        this.wardrobeDataAccess = wardrobeDataAccess;
        this.presenter = presenter;

    }

    @Override
    public void execute(UploadClothingInputData inputData) {
        if (inputData.getName() == null || inputData.getName().isEmpty()) {
            presenter.prepareFailView("Name cannot be empty.");
            return;
        }
        if (wardrobeDataAccess.existsByName(inputData.getName())) {
            presenter.prepareFailView("A clothing item with this name already exists.");
            return;
        }
        Photo photo = new Photo(inputData.getFilePath());

        ClothingArticle article = new ClothingArticle(
                inputData.getName(),
                inputData.getCategory(),
                inputData.getWeatherRating(),
                inputData.isWaterproof(),
                photo
        );
        wardrobeDataAccess.save(article);
        UploadClothingOutputData outputData =
                new UploadClothingOutputData(article.getName(),
                        "Added clothing item: " + article.getName());

        presenter.prepareSuccessView(outputData);
    }

    }
