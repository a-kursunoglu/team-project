package fuze.usecases.managewardrobe;

import fuze.entity.clothing.ClothingArticle;

import java.util.List;

public interface WardrobeRepository {
    void save(ClothingArticle article);
    boolean existsByName(String name);
    List<ClothingArticle> getAll();
    boolean deleteByName(String name);

    boolean delete(String name);
}
