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

        // Create wardrobe (entity)
        Wardrobe wardrobe = new Wardrobe();

        // TEMP: Add sample items until upload feature is done
        wardrobe.addItem(new ClothingArticle(
                "Hoodie", 2, false, new Photo(null, new byte[0])   // put your real photo later
        ));
        wardrobe.addItem(new ClothingArticle(
                "Jacket", 3, true, new Photo(null, new byte[0])
        ));

        // Create interactor
        ManageWardrobeInteractor interactor = new ManageWardrobeInteractor(wardrobe);

        // Create main window
        JFrame frame = new JFrame("My Wardrobe");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create and add wardrobe panel
        WardrobePanel wardrobePanel = new WardrobePanel(interactor);
        frame.add(new JScrollPane(wardrobePanel), BorderLayout.CENTER);

        // Show GUI
        frame.setVisible(true);
    }
}
