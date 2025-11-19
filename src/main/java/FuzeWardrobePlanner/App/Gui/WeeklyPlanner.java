package FuzeWardrobePlanner.App.Gui;

import FuzeWardrobePlanner.Entity.Clothing.ClothingArticle;
import FuzeWardrobePlanner.Entity.Clothing.Outfit;
import FuzeWardrobePlanner.Entity.Clothing.Photo;
import FuzeWardrobePlanner.Entity.Weather.WeatherDay;
import FuzeWardrobePlanner.Entity.Weather.WeatherWeek;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.Map;

/**
 * Calendar-like weekly view showing date, average temperature, and recommended outfit with images.
 */
public class WeeklyPlanner extends JFrame {

    private JLabel locationLabel;
    private JPanel daysPanel;

    public WeeklyPlanner() {
        super("Weekly Planner");
        initUi();
    }

    private void initUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Left: location label
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locationLabel = new JLabel("Location: -");
        locationLabel.setFont(locationLabel.getFont().deriveFont(Font.BOLD, 15f));
        leftPanel.add(locationLabel);
        add(leftPanel, BorderLayout.WEST);

        // Center: 7 columns representing each day
        daysPanel = new JPanel(new GridLayout(1, 7, 6, 6));
        add(new JScrollPane(daysPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED),
            BorderLayout.CENTER);

        setPreferredSize(new Dimension(1200, 350));
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Populate the grid with data from the given WeatherWeek (up to 7 days).
     */
    public void setWeatherWeek(WeatherWeek week) {
        String location = (week != null && week.getDefaultLocation() != null)
                ? week.getDefaultLocation()
                : "-";
        locationLabel.setText("Location: " + location);

        daysPanel.removeAll();
        for (int i = 0; i < 7; i++) {
            WeatherDay day = (week != null) ? week.getWeatherDay(i) : null;
            daysPanel.add(buildDayCard(day, i + 1));
        }
        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private JPanel buildDayCard(WeatherDay day, int index) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(4, 4));
        card.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY), "Day " + index));

        // Header with date and temperature
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        String dateText = day != null ? safe(day.getDate()) : "date" + index;
        String tempText = day != null ? formatAvgTemp(day) : "temp" + index;
        header.add(new JLabel("Date: " + dateText));
        header.add(new JLabel("Avg temp: " + tempText));
        card.add(header, BorderLayout.NORTH);

        // Outfit section
        JPanel outfitPanel = new JPanel();
        outfitPanel.setLayout(new BoxLayout(outfitPanel, BoxLayout.Y_AXIS));
        Outfit outfit = day != null ? day.getOutfit() : null;
        if (outfit != null && outfit.getItems() != null && !outfit.getItems().isEmpty()) {
            outfitPanel.add(new JLabel("Outfit: " + safe(outfit.getTitle())));

            JPanel itemsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
            for (Map.Entry<String, ClothingArticle> entry : outfit.getItems().entrySet()) {
                itemsPanel.add(buildClothingBadge(entry.getKey(), entry.getValue()));
            }
            outfitPanel.add(itemsPanel);
        } else {
            outfitPanel.add(new JLabel("Outfit: -"));
        }
        card.add(outfitPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildClothingBadge(String key, ClothingArticle article) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(120, 120));
        panel.setBorder(new LineBorder(Color.LIGHT_GRAY));

        JLabel nameLabel = new JLabel(key + ": " + (article != null ? safe(article.getName()) : "-"),
                SwingConstants.CENTER);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN, 11f));
        panel.add(nameLabel, BorderLayout.NORTH);

        JLabel imageLabel = new JLabel("", SwingConstants.CENTER);
        if (article != null) {
            ImageIcon icon = toIcon(article.getImage(), 90, 70);
            if (icon != null) {
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setText("No image");
            }
        } else {
            imageLabel.setText("No image");
        }
        panel.add(imageLabel, BorderLayout.CENTER);

        return panel;
    }

    private ImageIcon toIcon(Photo photo, int maxW, int maxH) {
        if (photo == null) {
            return null;
        }
        ImageIcon rawIcon = null;
        if (photo.getJpegData() != null && photo.getJpegData().length > 0) {
            rawIcon = new ImageIcon(photo.getJpegData());
        } else if (photo.getFilePath() != null && !photo.getFilePath().isEmpty()) {
            rawIcon = new ImageIcon(photo.getFilePath());
        }
        if (rawIcon == null || rawIcon.getIconWidth() <= 0 || rawIcon.getIconHeight() <= 0) {
            return null;
        }
        Image scaled = rawIcon.getImage().getScaledInstance(maxW, maxH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private String formatAvgTemp(WeatherDay day) {
        double avg = (day.getTemperatureHigh() + day.getTemperatureLow()) / 2.0;
        return Math.round(avg) + "°";
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WeeklyPlanner planner = new WeeklyPlanner();
            // Sample load: populate from default WeatherWeek so location/slots are filled.
            WeatherWeek week = new WeatherWeek();
            planner.setWeatherWeek(week);
            planner.setVisible(true);
        });
    }
}
