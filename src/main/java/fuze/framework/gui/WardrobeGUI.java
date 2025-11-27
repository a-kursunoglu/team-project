package fuze.framework.gui;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Photo;
import fuze.entity.clothing.Wardrobe;
import fuze.framework.data.JsonWardrobeRepository;
import fuze.usecases.managewardrobe.WardrobeRepository;
import fuze.usecases.managewardrobe.ManageWardrobeInteractor;


import javax.swing.*;
import java.awt.*;

public class WardrobeGUI {
    private final JFrame frame;

    public WardrobeGUI(WardrobeRepository repository) {
        ManageWardrobeInteractor interactor = new ManageWardrobeInteractor(repository);

        frame = new JFrame("My Wardrobe");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        WardrobePanel wardrobePanel = new WardrobePanel(interactor);
        frame.add(new JScrollPane(wardrobePanel), BorderLayout.CENTER);
    }

    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        Wardrobe wardrobe = new Wardrobe();

        wardrobe.addItem(new ClothingArticle(
                "Hoodie", "TOPS", 2, false, new Photo("placeholder.jpg")
        ));
        wardrobe.addItem(new ClothingArticle(
                "Jacket", "OUTERWEAR", 3, true, new Photo("placeholder.jpg")
        ));
        wardrobe.addItem(new ClothingArticle(
                "Jacket2", "OUTERWEAR", 3, true, new Photo("placeholder.jpg")
        ));

        String path = java.nio.file.Paths.get(System.getProperty("user.home"), ".fuzewardrobe", "wardrobe.json").toString();
        JsonWardrobeRepository repo =
                new JsonWardrobeRepository(path);
        WardrobeGUI gui = new WardrobeGUI(repo);
        gui.show();
    }

}
