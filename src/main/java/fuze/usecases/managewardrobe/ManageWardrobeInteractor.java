package fuze.usecases.managewardrobe;

import fuze.entity.clothing.Wardrobe;
import fuze.entity.clothing.ClothingArticle;
import java.util.List;

/**
 * Interactor for managing the user's wardrobe.
 * Provides application logic for deleting items and retrieving all stored clothing.
 * Acts as the use case layer between the UI and the wardrobe repository.
 */
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
