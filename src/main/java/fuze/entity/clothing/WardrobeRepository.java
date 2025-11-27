package fuze.entity.clothing;

import java.util.List;

public interface WardrobeRepository {
    void save(ClothingArticle article);
    boolean existsByName(String name);
    List<ClothingArticle> getAll();
    void deleteByName(String name);
}
