package FuzeWardrobePlanner.App;

import FuzeWardrobePlanner.App.Gui.WardrobePanel;
import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
import FuzeWardrobePlanner.Entity.Clothing.Photo;
import FuzeWardrobePlanner.Entity.Clothing.Wardrobe;
import FuzeWardrobePlanner.UserCases.ManageWardrobeInteractor;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {

        Wardrobe wardrobe = new Wardrobe();

        wardrobe.addItem(new ClothingArticle(
                "Hoodie", 2, false, new Photo("placeholder.jpg")   // put real photo later
        ));
        wardrobe.addItem(new ClothingArticle(
                "Jacket", 3, true, new Photo("placeholder.jpg")
        ));

        ManageWardrobeInteractor interactor = new ManageWardrobeInteractor(wardrobe);

        JFrame frame = new JFrame("My Wardrobe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        WardrobePanel wardrobePanel = new WardrobePanel(interactor);
        frame.add(new JScrollPane(wardrobePanel), BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
