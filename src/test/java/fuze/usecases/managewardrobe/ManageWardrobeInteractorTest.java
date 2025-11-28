package fuze.usecases.managewardrobe;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.framework.data.InMemoryWardrobeRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ManageWardrobeInteractorTest {

    private WardrobeRepository repo;
    private ManageWardrobeInteractor interactor;

    @BeforeEach
    void setUp() {
        repo = new InMemoryWardrobeRepository();
        interactor = new ManageWardrobeInteractor(repo);

        repo.save(new ClothingArticle("Shirt", "TOP", 2, false, new Photo("shirt.jpg")));
        repo.save(new ClothingArticle("Jacket", "OUTER", 3, true, new Photo("jacket.jpg")));
    }

    @Test
    void testViewWardrobe() {
        List<ClothingArticle> items = interactor.viewWardrobe();

        assertEquals(2, items.size());
        assertEquals("Shirt", items.get(0).getName());
        assertEquals("Jacket", items.get(1).getName());
    }

    @Test
    void testDeleteItem() {
        boolean result = interactor.deleteItem("Shirt");

        assertTrue(result);

        List<ClothingArticle> items = interactor.viewWardrobe();
        assertEquals(1, items.size());
        assertEquals("Jacket", items.get(0).getName());
    }

    @Test
    void testGetWardrobe() {
        var wardrobe = interactor.getWardrobe();

        assertNotNull(wardrobe);
        assertEquals(2, wardrobe.getItems().size());
        assertEquals("Shirt", wardrobe.getItems().get(0).getName());
    }

    @Test
    void testDeleteItemNotFound() {
        boolean result = interactor.deleteItem("NonExistent");
        assertFalse(result);

        List<ClothingArticle> items = interactor.viewWardrobe();
        assertEquals(2, items.size());
    }

    @Test
    void testViewWardrobeReturnsCopy() {
        List<ClothingArticle> items = interactor.viewWardrobe();
        items.clear();

        List<ClothingArticle> after = interactor.viewWardrobe();
        assertEquals(2, after.size());
    }

    @Test
    void testDeleteFromEmptyWardrobe() {
        interactor.deleteItem("Shirt");
        interactor.deleteItem("Jacket");

        boolean result = interactor.deleteItem("Anything");
        assertFalse(result);

        assertEquals(0, interactor.viewWardrobe().size());
    }

    @Test
    void testWardrobeOrderPreserved() {
        List<ClothingArticle> items = interactor.viewWardrobe();
        assertEquals("Shirt", items.get(0).getName());
        assertEquals("Jacket", items.get(1).getName());
    }

    @Test
    void testDuplicateNamesAllowed() {
        repo.save(new ClothingArticle("Shirt", "TOP", 1, false, new Photo("s2.jpg")));
        List<ClothingArticle> items = interactor.viewWardrobe();
        assertEquals(3, items.size());
    }

    @Test
    void testDeleteNullName() {
        boolean result = interactor.deleteItem(null);
        assertFalse(result);

        // Wardrobe should remain unchanged
        assertEquals(2, interactor.viewWardrobe().size());
    }

    @Test
    void testDeleteBlankName() {
        boolean result = interactor.deleteItem(" ");
        assertFalse(result);

        assertEquals(2, interactor.viewWardrobe().size());
    }

    @Test
    void testDeleteSpecialCharacterName() {
        ClothingArticle weird = new ClothingArticle("@@@", "TOP", 1, false, new Photo("w.png"));
        repo.save(weird);

        assertTrue(interactor.deleteItem("@@@"));
    }

    @Test
    void testGetWardrobeDefensiveCopy() {
        List<ClothingArticle> original = interactor.getWardrobe().getItems();
        original.clear();

        assertEquals(2, interactor.getWardrobe().getItems().size());
    }
}
