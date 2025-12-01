package fuze.usecases.uploadclothing;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.framework.data.InMemoryWardrobeRepository;
import fuze.usecases.managewardrobe.WardrobeRepository;
import fuze.usecases.uploadclothing.UploadClothingOutputData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UploadClothingInteractorTest {

    private WardrobeRepository repo;
    private TestPresenter presenter;
    private UploadClothingInteractor interactor;

    @BeforeEach
    void setup() {
        repo = new InMemoryWardrobeRepository();
        presenter = new TestPresenter();
        interactor = new UploadClothingInteractor(repo, presenter);
    }

    @Test
    void testSuccessfulUpload() {
        UploadClothingInputData input = new UploadClothingInputData(
                "Shirt",
                "TOP",
                2,
                false,
                "shirt.jpg"
        );

        interactor.execute(input);

        assertTrue(presenter.successCalled);
        assertEquals("Shirt", presenter.output.getName());
        assertTrue(repo.existsByName("Shirt"));
    }

    @Test
    void testEmptyNameFails() {
        UploadClothingInputData input = new UploadClothingInputData(
                "",
                "TOP",
                2,
                false,
                "shirt.jpg"
        );

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertEquals("Name cannot be empty.", presenter.errorMessage);
    }

    @Test
    void testDuplicateNameFails() {
        repo.save(new ClothingArticle("Shirt", "TOP", 2, false, new Photo("a.jpg")));

        UploadClothingInputData input = new UploadClothingInputData(
                "Shirt",
                "TOP",
                2,
                false,
                "shirt.jpg"
        );

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertEquals("A clothing item with this name already exists.", presenter.errorMessage);
    }

    @Test
    void testNullCategoryFails() {
        UploadClothingInputData input = new UploadClothingInputData(
                "Jacket",
                null,
                3,
                true,
                "jacket.jpg"
        );

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertEquals("Category cannot be null.", presenter.errorMessage);
    }

    @Test
    void testInvalidWarmthFails() {
        UploadClothingInputData input = new UploadClothingInputData(
                "Coat",
                "OUTER",
                -1,
                false,
                "coat.jpg"
        );

        interactor.execute(input);

        assertTrue(presenter.failCalled);
        assertEquals("Warmth level must be non-negative.", presenter.errorMessage);
    }

    @Test
    void testImagePathStoredCorrectly() {
        UploadClothingInputData input = new UploadClothingInputData(
                "Shoes",
                "FOOTWEAR",
                1,
                false,
                "shoes.png"
        );

        interactor.execute(input);

        ClothingArticle saved = repo.getAll().get(0);
        assertEquals("shoes.png", saved.getImage().getFilePath());
    }

    private static class TestPresenter implements UploadClothingOutputBoundary {

        boolean successCalled = false;
        boolean failCalled = false;
        UploadClothingOutputData output;
        String errorMessage;

        @Override
        public void prepareSuccessView(UploadClothingOutputData data) {
            successCalled = true;
            output = data;
        }

        @Override
        public void prepareFailView(String message) {
            failCalled = true;
            errorMessage = message;
        }
    }
}
