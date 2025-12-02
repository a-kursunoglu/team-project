package fuze.usecases.uploadclothing;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.usecases.managewardrobe.WardrobeRepository;

public class UploadClothingInteractor implements UploadClothingInputBoundary {
    /** The wardrobe repository to refer and upload to.*/
    private final WardrobeRepository wardrobeDataAccess;
    /** Larger logic boundary.*/
    private final UploadClothingOutputBoundary presenter;

    /**
     * Creates an instance of the interactor.
     * @param wardrobeDataAccess the wardrobe to modify and check
     * @param presenter the GUI to display to
     */
    public UploadClothingInteractor(final WardrobeRepository wardrobeDataAccess,
                                    final UploadClothingOutputBoundary
                                            presenter) {
        this.wardrobeDataAccess = wardrobeDataAccess;
        this.presenter = presenter;

    }
    @Override
    public void execute(final UploadClothingInputData inputData) {
        if (inputData.getName() == null || inputData.getName().isEmpty()) {
            presenter.prepareFailView("Name cannot be empty.");
            return;
        }
        if (wardrobeDataAccess.existsByName(inputData.getName())) {
            presenter.prepareFailView("A clothing item with "
                    + "this name already exists.");
            return;
        }
        if (inputData.getCategory() == null
                || inputData.getCategory().isEmpty()) {
            presenter.prepareFailView("Category cannot be null.");
            return;
        }
        int warmth = inputData.getWeatherRating();

        if (warmth < 1 || warmth > 5) {
            presenter.prepareFailView("Warmth level "
                    + "must be non-negative.");
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
