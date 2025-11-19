package FuzeWardrobePlanner.UserCases.UploadClothing;

import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
import FuzeWardrobePlanner.Entity.Clothing.WardrobeRepository;

import java.util.ArrayList;
import java.util.List;

public class InMemoryWardrobeRepository implements WardrobeRepository {
    private final List<ClothingArticle> storage = new ArrayList<>();

    @Override
    public void save(ClothingArticle article) {
        storage.add(article);
    }

    @Override
    public boolean existsByName(String name) {
        return storage.stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(name));
    }

    @Override
    public List<ClothingArticle> getAll() {
        return new ArrayList<>(storage); // defensive copy
    }
}
