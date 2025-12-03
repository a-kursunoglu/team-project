package fuze.framework.gui.wardrobe;

import fuze.entity.clothing.ClothingArticle;
import fuze.usecases.managewardrobe.ManageWardrobeInteractor;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A UI panel that displays the user's wardrobe organized into four categories:
 * Tops, Bottoms, Outerwear, and Accessories.
 * It rebuilds and refreshes its layout automatically whenever items are added or removed.
 */
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

        JButton toggleButton = new JButton(categoryName + " ▼");
        toggleButton.setFont(new Font("Arial", Font.BOLD, 16));
        toggleButton.setFocusPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel grid = new JPanel();
        grid.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));

        List<ClothingArticle> items = interactor.getWardrobe().getByCategory(categoryName);

        for (ClothingArticle item : items) {
            ClothingTile tile = new ClothingTile(item, interactor, this::refresh);
            grid.add(tile);
        }

        JScrollPane scrollPane = new JScrollPane(grid);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(toggleButton, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        toggleButton.addActionListener(e -> {
            boolean visible = contentPanel.isVisible();
            contentPanel.setVisible(!visible);
            toggleButton.setText(categoryName + (visible ? " ▶" : " ▼"));
            panel.revalidate();
            panel.repaint();
        });

        return panel;
    }
}
