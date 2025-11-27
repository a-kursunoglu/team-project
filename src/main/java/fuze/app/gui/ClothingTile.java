package fuze.app.gui;

import fuze.entity.clothing.ClothingArticle;
import fuze.use_cases.ManageWardrobeInteractor;

import javax.swing.*;
import java.awt.*;

public class ClothingTile extends JPanel {

    public ClothingTile(ClothingArticle item, ManageWardrobeInteractor interactor, Runnable refreshCallback) {
        setLayout(null);
        setPreferredSize(new Dimension(100, 120));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel imageLabel = new JLabel();
        imageLabel.setBounds(10, 25, 80, 80);

        if (item.getImage() != null && item.getImage().getFilePath() != null) {
            ImageIcon original = new ImageIcon(item.getImage().getFilePath());
            Image img = original.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(img));
        }

        add(imageLabel);

        JButton deleteButton = new JButton("X");
        deleteButton.setMargin(new Insets(0, 0, 0, 0));
        deleteButton.setBounds(75, 5, 20, 20);

        deleteButton.addActionListener(e -> {
            int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + item.getName() + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE
            );
            if (option == JOptionPane.YES_OPTION) {
                interactor.deleteItem(item.getName());
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            }
        });

        add(deleteButton);

        JLabel nameLabel = new JLabel(item.getName(), SwingConstants.CENTER);
        nameLabel.setBounds(0, 105, 100, 15);
        add(nameLabel);
    }
}