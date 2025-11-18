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
        refresh();
    }
    public void refresh() {
        removeAll();
        setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));

        List<ClothingArticle> items = interactor.viewWardrobe();
        for (ClothingArticle item : items) {
            ClothingTile tile = new ClothingTile(item, interactor, this::refresh);
            add(tile);
        }
        revalidate();
        repaint();
    }
}
