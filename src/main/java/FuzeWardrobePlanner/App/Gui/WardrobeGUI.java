package FuzeWardrobePlanner.App.Gui;

import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
import FuzeWardrobePlanner.Entity.Clothing.Photo;
import FuzeWardrobePlanner.Entity.Clothing.Wardrobe;
import FuzeWardrobePlanner.Entity.Clothing.WardrobeRepository;
import FuzeWardrobePlanner.UserCases.ManageWardrobeInteractor;


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
                "Hoodie", "TOPS", 2, false, new Photo("placeholder.jpg")   // put real photo here later
        ));
        wardrobe.addItem(new ClothingArticle(
                "Jacket", "OUTERWEAR", 3, true, new Photo("placeholder.jpg")
        ));
        wardrobe.addItem(new ClothingArticle(
                "Jacket2", "OUTERWEAR", 3, true, new Photo("placeholder.jpg")
        ));

        // Demo launcher: use a file-backed repo in user home
        String path = java.nio.file.Paths.get(System.getProperty("user.home"), ".fuzewardrobe", "wardrobe.json").toString();
        FuzeWardrobePlanner.Entity.Clothing.JsonWardrobeRepository repo =
                new FuzeWardrobePlanner.Entity.Clothing.JsonWardrobeRepository(path);
        WardrobeGUI gui = new WardrobeGUI(repo);
        gui.show();
    }

}
