package FuzeWardrobePlanner.App.Gui;

import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
import FuzeWardrobePlanner.UserCases.ManageWardrobeInteractor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WardrobePanel extends JPanel {
    private final ManageWardrobeInteractor interactor;

    public WardrobePanel(ManageWardrobeInteractor interactor) {
        this.interactor = interactor;
        setLayout(new BorderLayout());
        refresh();
    }

    private void rebuildUI() {
        refresh();
    }

    public void refresh() {
        removeAll();
        setLayout(new GridLayout(2, 2, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        add(buildCategory("Tops"));
        add(buildCategory("Bottoms"));
        add(buildCategory("Outerwear"));
        add(buildCategory("Accessories"));

        revalidate();
        repaint();
    }

    private JPanel buildCategory(String categoryName) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel label = new JLabel(categoryName);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel grid = new JPanel();
        grid.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        List<ClothingArticle> items = interactor.getWardrobe().getByCategory(categoryName);

        for (ClothingArticle item : items) {
            ClothingTile tile = new ClothingTile(item, interactor, this::refresh);
            grid.add(tile);
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(grid, BorderLayout.CENTER);

        return panel;
    }
}
