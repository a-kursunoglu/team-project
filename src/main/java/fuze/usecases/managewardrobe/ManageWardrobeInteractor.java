package fuze.usecases.managewardrobe;

import fuze.entity.clothing.Wardrobe;
import fuze.entity.clothing.ClothingArticle;
import java.util.List;

/**
 * Interactor for managing the user's wardrobe.
 * Provides application logic for deleting items and retrieving all stored
 * clothing.
 * Acts as the use case layer between the UI and the wardrobe repository.
 */
public class ManageWardrobeInteractor {
    /**
     * Wardrobe repository variable to handle the actual logic.
     */
    private final WardrobeRepository repository;

    /**
     * Creates instance of interactor.
     * @param repository see WardrobeRepository Class
     */
    public ManageWardrobeInteractor(final WardrobeRepository repository) {
        this.repository = repository;
    }

    /**
     * Interface to delete an item in the repository.
     * @param name string of the name of the item
     * @return whether the object has been deleted or
     * false if object doesn't exist
     */
    public boolean deleteItem(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }
        return repository.deleteByName(name);
    }

    /**
     * For listing out the wardrobe items in the repository.
     * @return Returns a list of all clothing articles
     */
    public List<ClothingArticle> viewWardrobe() {
        return repository.getAll();
    }

    /**
     * In the event you want to get the entirety of the wardrobe.
     * @return Returns a copy of the interactor's wardrobe.
     */
    public Wardrobe getWardrobe() {
        Wardrobe wardrobe = new Wardrobe();
        repository.getAll().forEach(wardrobe::addItem);
        return wardrobe;
    }
}
