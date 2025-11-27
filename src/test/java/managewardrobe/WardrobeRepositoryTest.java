package managewardrobe;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.framework.data.InMemoryWardrobeRepository;
import fuze.usecases.managewardrobe.WardrobeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WardrobeRepositoryTest {

    private WardrobeRepository repo;

    @BeforeEach
    void setup() {
        repo = new InMemoryWardrobeRepository();
    }

    @Test
    void testSaveAndFindAll() {
        ClothingArticle shirt = new ClothingArticle("Shirt", "TOP", 2, false, new Photo("shirt.jpg"));
        repo.save(shirt);

        List<ClothingArticle> items = repo.getAll();
        assertEquals(1, items.size());
        assertEquals("Shirt", items.get(0).getName());
    }

    @Test
    void testExistsByName() {
        repo.save(new ClothingArticle("Jacket", "OUTER", 3, true, new Photo("jacket.jpg")));

        assertTrue(repo.existsByName("Jacket"));
        assertFalse(repo.existsByName("Pants"));
    }

    @Test
    void testDelete() {
        repo.save(new ClothingArticle("Hat", "ACCESSORY", 1, false, new Photo("hat.jpg")));

        repo.deleteByName("Hat");

        assertFalse(repo.existsByName("Hat"));
    }
}
