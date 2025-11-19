package FuzeWardrobePlanner.App.Gui;

import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
import FuzeWardrobePlanner.UserCases.ManageWardrobeInteractor;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ClothingTile extends JPanel {

    public ClothingTile(ClothingArticle item, ManageWardrobeInteractor interactor, Runnable refreshCallback) {
        setLayout(null);
        setPreferredSize(new Dimension(100, 100));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        ImageIcon icon = null;
        Object rawImage = item.getImage();

        if (rawImage != null) {
            Image img = null;

            if (rawImage instanceof BufferedImage) {
                img = (BufferedImage) rawImage;
            } else if (rawImage instanceof Image) {
                img = (Image) rawImage;
            }

            if (img != null) {
                Image scaled = img.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                icon = new ImageIcon(scaled);
            }
        }

        JLabel imageLabel = new JLabel();
        if (icon != null) {
            imageLabel.setIcon(icon);
        }
        imageLabel.setBounds(5, 5, 90, 90);
        add(imageLabel);

        JButton deleteButton = new JButton("X");
        deleteButton.setMargin(new Insets(0, 0, 0, 0));
        deleteButton.setBounds(75, 5, 20, 20);

        deleteButton.addActionListener(e -> {
            interactor.deleteItem(item.getName()); // item.getName() must return String
            if (refreshCallback != null) {
                refreshCallback.run();
            }
        });

        add(deleteButton);
    }
}