import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.usecases.managewardrobe.WardrobeRepository;
import fuze.framework.data.InMemoryWardrobeRepository;
import fuze.usecases.managewardrobe.ManageWardrobeInteractor;

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
}
