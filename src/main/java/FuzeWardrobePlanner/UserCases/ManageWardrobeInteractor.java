package FuzeWardrobePlanner.UserCases;

import FuzeWardrobePlanner.Entity.Clothing.Wardrobe;
import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle; // import ClothingArticle
import java.util.List; // import List

public class ManageWardrobeInteractor {
    private final Wardrobe wardrobe;

    public ManageWardrobeInteractor(Wardrobe wardrobe) {
        this.wardrobe = wardrobe;
    }

    public boolean deleteItem(String name) {
        return wardrobe.deleteItemByName(name);
    }

    public List<ClothingArticle> viewWardrobe() {
        return wardrobe.getItems(); // make sure getItems() returns List<ClothingArticle>
    }

    public Wardrobe getWardrobe() {
        return this.wardrobe;
    }
}
