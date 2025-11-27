package fuze.framework.gui;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.entity.weather.WeatherDay;
import fuze.usecases.trippacking.TripPlannerService;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class TripPlannerUI extends JFrame {

    private final TripPlannerService service;

    private JComboBox<String> locationDropdown;
    private JTextField startField;
    private JTextField endField;
    private JComboBox<String> outfitsNeededDropdown;
    private JPanel daysPanel;
    private JButton generateButton;

    private final String[] availableCities;

    public TripPlannerUI(TripPlannerService service,
                         String[] cities) {

        super("Trip Planner");
        this.service = service;
        this.availableCities = cities != null ? cities : new String[]{"Toronto Canada", "New York", "Vancouver"};

        initUi();
    }

    private void initUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        content.add(buildFormPanel(), BorderLayout.WEST);
        content.add(buildCardsPanel(), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        generateButton = new JButton("Generate");
        bottom.add(generateButton);


        add(content, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(1100, 500));
        pack();
        setLocationRelativeTo(null);

        wireActions();
    }

    private JScrollPane buildCardsPanel() {
        daysPanel = new JPanel(new GridLayout(1,7,6,6));
        return new JScrollPane(daysPanel);
    }

    private void wireActions() {
        generateButton.addActionListener(e -> {
            try {
                String loc = (String) locationDropdown.getSelectedItem();
                LocalDate[] dates = service.validateDates(startField.getText(), endField.getText());
                int days = Integer.parseInt(outfitsNeededDropdown.getSelectedItem().toString());

                List<WeatherDay> list =
                        service.generateTripPlan(loc, dates[0], days);

                renderCards(list);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        form.add(label("Location:"));
        locationDropdown = new JComboBox<>(buildLocationOptions());
        locationDropdown.setMaximumSize(new Dimension(250, 32));
        form.add(locationDropdown);

        form.add(Box.createVerticalStrut(10));
        JPanel datesRow = new JPanel(new GridLayout(1, 4, 8, 0));
        datesRow.add(label("Start"));
        startField = new JTextField(10);
        startField.setMaximumSize(new Dimension(140, 28));
        datesRow.add(startField);
        datesRow.add(label("End"));
        endField = new JTextField(10);
        endField.setMaximumSize(new Dimension(140, 28));
        datesRow.add(endField);
        form.add(datesRow);
        JLabel formatHint = new JLabel("Date format: yyyy-mm-dd");
        formatHint.setFont(formatHint.getFont().deriveFont(Font.ITALIC, 11f));
        form.add(formatHint);
        JLabel forecastLimitHint = new JLabel("Note: Trips can only be planned up to ~2 weeks ahead (forecast limit).");
        forecastLimitHint.setFont(forecastLimitHint.getFont().deriveFont(Font.ITALIC, 11f));
        form.add(forecastLimitHint);

        form.add(Box.createVerticalStrut(10));
        form.add(label("Outfits Needed (7 max):"));
        outfitsNeededDropdown = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7"});
        outfitsNeededDropdown.setMaximumSize(new Dimension(250, 32));
        form.add(outfitsNeededDropdown);

        return form;
    }



    private void renderCards(List<WeatherDay> days) {
        daysPanel.removeAll();
        daysPanel.setLayout(new GridLayout(1, days.size()));

        int index = 1;
        for (WeatherDay d : days) {
            daysPanel.add(buildDayCard(d, d.getDate(), index++));
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private ImageIcon toIcon(fuze.entity.clothing.Photo photo, int maxW, int maxH) {
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

    private JPanel buildOutfitPanel(Outfit outfit) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 6, 6));
        if (outfit == null || outfit.getItems() == null || outfit.getItems().isEmpty()) {
            panel.add(new JLabel("Outfit: -"));
            return panel;
        }
        for (Map.Entry<String, ClothingArticle> entry : outfit.getItems().entrySet()) {
            ClothingArticle article = entry.getValue();
            if (article == null) continue;
            JPanel badge = new JPanel(new BorderLayout());
            badge.setBorder(new LineBorder(Color.LIGHT_GRAY));
            JLabel icon = new JLabel("", SwingConstants.CENTER);
            icon.setPreferredSize(new Dimension(70, 70));
            icon.setIcon(toIcon(article.getImage(), 70, 70));
            badge.add(icon, BorderLayout.CENTER);
            JLabel title = new JLabel(article.getName(), SwingConstants.CENTER);
            title.setFont(title.getFont().deriveFont(11f));
            badge.add(title, BorderLayout.SOUTH);
            panel.add(badge);
        }
        return panel;
    }
    private JPanel buildDayCard(WeatherDay day, String date, int index) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY), "Day " + index));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(new JLabel("Date: " + (date != null ? date : "-")));
        String tempText = "-";
        if (day != null) {
            double avg = (day.getTemperatureHigh() + day.getTemperatureLow()) / 2.0;
            tempText = Math.round(avg) + "°";
        }
        header.add(new JLabel("Avg temp: " + tempText));
        card.add(header, BorderLayout.NORTH);

        card.add(buildOutfitPanel(day != null ? day.getOutfit() : null), BorderLayout.CENTER);
        return card;
    }
    private String[] buildLocationOptions() {
        LinkedHashSet<String> options = new LinkedHashSet<>();
        options.add("(select)");
        options.addAll(Arrays.asList(availableCities));
        return options.toArray(new String[0]);
    }
}
