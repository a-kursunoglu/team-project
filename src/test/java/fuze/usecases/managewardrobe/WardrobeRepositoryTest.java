package fuze.usecases.managewardrobe;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.framework.data.InMemoryWardrobeRepository;
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

    @Test
    void testOrderPreserved() {
        repo.save(new ClothingArticle("A", "TOP", 1, false, new Photo("a.jpg")));
        repo.save(new ClothingArticle("B", "TOP", 1, false, new Photo("b.jpg")));

        List<ClothingArticle> items = repo.getAll();
        assertEquals("A", items.get(0).getName());
        assertEquals("B", items.get(1).getName());
    }

    @Test
    void testDeleteNonExisting() {
        boolean deleted = repo.deleteByName("Ghost");
        assertFalse(deleted);
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void testGetAllReturnsCopy() {
        repo.save(new ClothingArticle("Shirt", "TOP", 2, false, new Photo("s.jpg")));

        List<ClothingArticle> items = repo.getAll();
        items.clear();

        assertEquals(1, repo.getAll().size());
    }

    @Test
    void testDuplicateNamesAllowedExtended() {
        for (int i = 0; i < 10; i++) {
            repo.save(new ClothingArticle("Same", "TOP", 1, false, new Photo(i + ".jpg")));
        }
        assertEquals(10, repo.getAll().size());
    }

    @Test
    void testDeletePreservesOthers() {
        repo.save(new ClothingArticle("A", "TOP", 1, false, new Photo("a.jpg")));
        repo.save(new ClothingArticle("B", "TOP", 1, false, new Photo("b.jpg")));
        repo.save(new ClothingArticle("C", "TOP", 1, false, new Photo("c.jpg")));

        repo.deleteByName("B");

        List<ClothingArticle> items = repo.getAll();
        assertEquals(2, items.size());
        assertEquals("A", items.get(0).getName());
        assertEquals("C", items.get(1).getName());
    }

    @Test
    void testFindAfterMultipleSaves() {
        for (int i = 1; i <= 5; i++) {
            repo.save(new ClothingArticle("Item" + i, "TOP", i, false, new Photo(i + ".jpg")));
        }
        assertTrue(repo.existsByName("Item3"));
        assertFalse(repo.existsByName("Item10"));
    }

    @Test
    void testDeleteTwice() {
        repo.save(new ClothingArticle("A", "TOP", 1, false, new Photo("a.jpg")));

        assertTrue(repo.deleteByName("A"));
        assertFalse(repo.deleteByName("A"));
        assertTrue(repo.getAll().isEmpty());
    }

    @Test
    void testSaveWithNullPhoto() {
        ClothingArticle item = new ClothingArticle("NoPhoto", "TOP", 1, false, null);
        repo.save(item);

        assertEquals(1, repo.getAll().size());
        assertNull(repo.getAll().get(0).getImage());
    }

    @Test
    void testLargeInsert() {
        for (int i = 0; i < 100; i++) {
            repo.save(new ClothingArticle("Item" + i, "TOP", 1, false, new Photo(i + ".jpg")));
        }
        assertEquals(100, repo.getAll().size());
    }

    @Test
    void testDeleteHalf() {
        for (int i = 0; i < 20; i++) {
            repo.save(new ClothingArticle("Item" + i, "TOP", 1, false, new Photo(i + ".jpg")));
        }

        for (int i = 0; i < 10; i++) {
            repo.deleteByName("Item" + i);
        }

        assertEquals(10, repo.getAll().size());
    }

    @Test
    void testExistsAfterManyDeletes() {
        for (int i = 0; i < 15; i++) {
            repo.save(new ClothingArticle("Item" + i, "TOP", 1, false, new Photo(i + ".jpg")));
        }

        for (int i = 0; i < 15; i += 2) {
            repo.deleteByName("Item" + i);
        }

        assertTrue(repo.existsByName("Item1"));
        assertFalse(repo.existsByName("Item0"));
        assertFalse(repo.existsByName("Item4"));
    }

    @Test
    void testGetAllIndependenceBetweenCalls() {
        repo.save(new ClothingArticle("X", "TOP", 1, false, new Photo("x.jpg")));

        List<ClothingArticle> a = repo.getAll();
        List<ClothingArticle> b = repo.getAll();

        a.clear();

        assertEquals(1, b.size());
    }
}
