package fuze.use_cases;

import fuze.entity.clothing.Wardrobe;
import fuze.entity.clothing.WardrobeRepository;
import fuze.entity.clothing.ClothingArticle;
import java.util.List;

public class ManageWardrobeInteractor {
    private final WardrobeRepository repository;

    public ManageWardrobeInteractor(WardrobeRepository repository) {
        this.repository = repository;
    }

    public boolean deleteItem(String name) {
        repository.deleteByName(name);
        return true;
    }

    public List<ClothingArticle> viewWardrobe() {
        return repository.getAll();
    }

    public Wardrobe getWardrobe() {
        Wardrobe wardrobe = new Wardrobe();
        repository.getAll().forEach(wardrobe::addItem);
        return wardrobe;
    }
}
