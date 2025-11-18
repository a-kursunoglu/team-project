package FuzeWardrobePlanner.Entity.Clothing;

import java.util.ArrayList;
import java.util.List;

public class Wardrobe {
    private final List<ClothingArticle> items = new ArrayList<>();

    public void addItem(ClothingArticle item){
        items.add(item);
    }

    public List<ClothingArticle> getItems() {
        return items;
    }

    public boolean deleteItem(ClothingArticle item){
        return items.remove(item);
    }

    public boolean deleteItemByName(String name){
        return items.removeIf(i -> i.getName().equalsIgnoreCase(name));
    }
}
