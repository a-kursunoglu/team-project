package fuze.use_cases.upload_clothing;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.WardrobeRepository;

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

    @Override
    public void deleteByName(String name) {
        storage.removeIf(item -> item.getName().equalsIgnoreCase(name));
    }
}
