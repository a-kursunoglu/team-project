package FuzeWardrobePlanner.UserCases;

import FuzeWardrobePlanner.Entity.Clothing.Wardrobe;
import FuzeWardrobePlanner.Entity.Clothing.WardrobeRepository;
import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
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
