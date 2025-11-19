package FuzeWardrobePlanner.Entity.Clothing;

import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
import java.util.List;

public interface WardrobeRepository {
    void save(ClothingArticle article);
    boolean existsByName(String name);
    List<ClothingArticle> getAll();
}
