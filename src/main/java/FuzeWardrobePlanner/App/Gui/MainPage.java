package FuzeWardrobePlanner.App.Gui;

import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
import FuzeWardrobePlanner.Entity.Clothing.Outfit;
import FuzeWardrobePlanner.Entity.Weather.WeatherDay;
import FuzeWardrobePlanner.Entity.Weather.WeatherWeek;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

/**
 * Main landing page UI mock aligned to provided wireframe.
 */
public class MainPage extends JFrame {

    // Left side components
    private JComboBox<String> locationDropdown;
    private JLabel currentLocationLabel;
    private JLabel temperatureLabel;

    // Right side outfit display
    private JPanel outfitPanel;

    public MainPage() {
        super("Fuze's Weather Wardrobe Planner");
        initUi();
    }

    private void initUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        JPanel leftPanel = buildLeftPanel();
        JPanel rightPanel = buildRightPanel();

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(1100, 600));
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        panel.add(label("Location:"));
        // TODO: populate with real cities list; placeholder options for now
        locationDropdown = new JComboBox<>(new String[]{"(select city)", "Toronto", "New York", "Vancouver"});
        locationDropdown.setMaximumSize(new Dimension(250, 32));
        panel.add(locationDropdown);

        panel.add(Box.createVerticalStrut(12));
        panel.add(label("Your location is set to:"));
        currentLocationLabel = bigLabel("Unknown");
        panel.add(currentLocationLabel);

        panel.add(Box.createVerticalStrut(12));
        panel.add(label("Temperature:"));
        temperatureLabel = veryBigLabel("--°");
        panel.add(temperatureLabel);

        panel.add(Box.createVerticalStrut(20));
        panel.add(makeButton("Add Trip"));
        panel.add(makeButton("Add Clothing Item"));
        panel.add(makeButton("View Wardrobe"));
        panel.add(makeButton("Plan for the Week"));

        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(label("Outfit:"), BorderLayout.NORTH);

        outfitPanel = new JPanel();
        outfitPanel.setLayout(new BoxLayout(outfitPanel, BoxLayout.Y_AXIS));
        outfitPanel.setBorder(new LineBorder(Color.GRAY));
        outfitPanel.setPreferredSize(new Dimension(500, 400));
        panel.add(outfitPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Populate UI with WeatherWeek data (defaults to day 0 for "today").
     */
    public void loadFromWeatherWeek(WeatherWeek week) {
        WeatherDay today = week != null ? week.getWeatherDay(0) : null;
        String loc = week != null ? safe(week.getDefaultLocation()) : "Unknown";
        currentLocationLabel.setText(loc);

        if (today != null) {
            double avg = (today.getTemperatureHigh() + today.getTemperatureLow()) / 2.0;
            temperatureLabel.setText(Math.round(avg) + "°");
            renderOutfit(today.getOutfit());
        } else {
            temperatureLabel.setText("--°");
            renderOutfit(null);
        }
    }

    private void renderOutfit(Outfit outfit) {
        outfitPanel.removeAll();
        outfitPanel.add(Box.createVerticalStrut(8));
        if (outfit == null || outfit.getItems() == null || outfit.getItems().isEmpty()) {
            outfitPanel.add(centeredLabel("No outfit available"));
        } else {
            outfitPanel.add(centeredLabel("Title: " + safe(outfit.getTitle())));

            JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
            for (Map.Entry<String, ClothingArticle> entry : outfit.getItems().entrySet()) {
                grid.add(buildClothingCard(entry.getKey(), entry.getValue()));
            }
            outfitPanel.add(grid);
        }
        outfitPanel.revalidate();
        outfitPanel.repaint();
    }

    private JPanel buildClothingCard(String key, ClothingArticle article) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        panel.setPreferredSize(new Dimension(150, 120));

        JLabel name = new JLabel(key + ": " + (article != null ? safe(article.getName()) : "-"),
                SwingConstants.CENTER);
        name.setFont(name.getFont().deriveFont(Font.PLAIN, 12f));
        panel.add(name, BorderLayout.NORTH);

        JLabel image = new JLabel("", SwingConstants.CENTER);
        if (article != null) {
            ImageIcon icon = toIcon(article.getImage(), 120, 80);
            if (icon != null) {
                image.setIcon(icon);
            } else {
                image.setText("No image");
            }
        } else {
            image.setText("No image");
        }
        panel.add(image, BorderLayout.CENTER);
        return panel;
    }

    private JButton makeButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 32));
        return button;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel bigLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 16f));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel veryBigLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, 36f));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel centeredLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.CENTER);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }

    private ImageIcon toIcon(FuzeWardrobePlanner.Entity.Clothing.Photo photo, int maxW, int maxH) {
        if (photo == null || photo.getJpegData() == null || photo.getJpegData().length == 0) {
            return null;
        }
        ImageIcon rawIcon = new ImageIcon(photo.getJpegData());
        if (rawIcon.getIconWidth() <= 0 || rawIcon.getIconHeight() <= 0) {
            return null;
        }
        Image scaled = rawIcon.getImage().getScaledInstance(maxW, maxH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainPage page = new MainPage();
            page.loadFromWeatherWeek(new WeatherWeek());
            page.setVisible(true);
        });
    }
}
