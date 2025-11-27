package fuze.usecases.trippacking;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.framework.gui.MainPage;
import fuze.usecases.managewardrobe.WardrobeRepository;
import fuze.entity.weather.WeatherDay;
import fuze.entity.weather.WeatherTrip;
import fuze.entity.weather.WeatherWeek;
import fuze.usecases.generateoutfit.OutfitCreator;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class TripPlanner extends JFrame {

    private JComboBox<String> locationDropdown;
    private JTextField startField;
    private JTextField endField;
    private JComboBox<String> outfitsNeededDropdown;
    private JPanel daysPanel;
    private JButton generateButton;
    private JButton backButton;

    private final Consumer<WeatherWeek> onBackToMain;
    private final WardrobeRepository wardrobeRepository;
    private final OutfitCreator outfitCreator;
    private final String[] availableCities;

    public TripPlanner() {
        this(null, null, null, null);
    }

    public TripPlanner(Consumer<WeatherWeek> onBackToMain,
                       WardrobeRepository wardrobeRepository,
                       OutfitCreator outfitCreator,
                       String[] availableCities) {
        super("Trip Planner");
        this.onBackToMain = onBackToMain;
        this.wardrobeRepository = wardrobeRepository;
        this.outfitCreator = outfitCreator != null ? outfitCreator : new OutfitCreator();
        this.availableCities = availableCities != null && availableCities.length > 0
                ? availableCities
                : new String[]{"Toronto Canada", "New York", "Vancouver"};
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
        backButton = new JButton("Back to Main Page");
        bottom.add(generateButton);
        bottom.add(backButton);

        add(content, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(1100, 500));
        pack();
        setLocationRelativeTo(null);

        wireActions();
    }

    private void wireActions() {
        generateButton.addActionListener(e -> generatePlan());

        backButton.addActionListener(e -> {
            if (onBackToMain != null) {
                onBackToMain.accept(new WeatherWeek());
            } else {
                SwingUtilities.invokeLater(() -> {
                    MainPage main = new MainPage();
                    main.loadFromWeatherWeek(new WeatherWeek());
                    main.setVisible(true);
                });
            }
            dispose();
        });
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

        form.add(Box.createVerticalStrut(10));
        form.add(label("Outfits Needed (7 max):"));
        outfitsNeededDropdown = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7"});
        outfitsNeededDropdown.setMaximumSize(new Dimension(250, 32));
        form.add(outfitsNeededDropdown);

        return form;
    }

    private JScrollPane buildCardsPanel() {
        daysPanel = new JPanel(new GridLayout(1, 7, 6, 6));
        JScrollPane scrollPane = new JScrollPane(daysPanel,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(900, 320));
        return scrollPane;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private void generatePlan() {
        String loc = (String) locationDropdown.getSelectedItem();
        if (loc == null || "(select)".equals(loc)) {
            JOptionPane.showMessageDialog(this,
                    "Please select a location.",
                    "Missing location",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String start = startField.getText().trim();
        String end = endField.getText().trim();
        if (start.isEmpty() || end.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both start and end dates (yyyy-mm-dd).",
                    "Missing dates",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate startDate;
        LocalDate endDate;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            startDate = LocalDate.parse(start, fmt);
            endDate = LocalDate.parse(end, fmt);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Dates must be in format yyyy-mm-dd.",
                    "Invalid date",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (endDate.isBefore(startDate)) {
            JOptionPane.showMessageDialog(this,
                    "End date cannot be before start date.",
                    "Invalid range",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int outfitsNeeded = Integer.parseInt(outfitsNeededDropdown.getSelectedItem().toString());
        int days = Math.min(7, outfitsNeeded);

        WeatherTrip trip = new WeatherTrip(loc, startDate.toString(), days);
        Map<String, List<ClothingArticle>> wardrobeMap = buildWardrobeMap(
                wardrobeRepository != null ? wardrobeRepository.getAll() : List.of()
        );
        Set<String> prevNames = new HashSet<>();

        if (daysPanel != null) {
            daysPanel.removeAll();
            daysPanel.setLayout(new GridLayout(1, Math.max(1, days), 6, 6));
        }
        for (int i = 0; i < days; i++) {
            WeatherDay day = trip.getWeatherDay(i);
            if (day == null) {
                LocalDate d = startDate.plusDays(i);
                day = new WeatherDay(0, 0, 0, trip.getLocation(), d.toString());
            } else {
                Outfit outfit = wardrobeMap.isEmpty()
                        ? null
                        : generateOutfitWithNoRepeat(day, wardrobeMap, prevNames);
                day.setOutfit(outfit);
                prevNames = extractNames(outfit);
            }
            if (daysPanel != null) {
                daysPanel.add(buildDayCard(day, day.getDate(), i + 1));
            }
        }

        if (daysPanel != null) {
            daysPanel.revalidate();
            daysPanel.repaint();
        }
    }

    private Map<String, List<ClothingArticle>> buildWardrobeMap(List<ClothingArticle> items) {
        Map<String, List<ClothingArticle>> map = new HashMap<>();
        map.put("top", new ArrayList<>());
        map.put("bottom", new ArrayList<>());
        map.put("outer", new ArrayList<>());
        map.put("accessory", new ArrayList<>());

        for (ClothingArticle item : items) {
            if (item == null || item.getCategory() == null) continue;
            String cat = item.getCategory().toLowerCase();
            if (cat.contains("top")) {
                map.get("top").add(item);
            } else if (cat.contains("bottom") || cat.contains("pant") || cat.contains("bottoms")) {
                map.get("bottom").add(item);
            } else if (cat.contains("outer")) {
                map.get("outer").add(item);
            } else if (cat.contains("access")) {
                map.get("accessory").add(item);
            }
        }
        return map;
    }

    private Outfit generateOutfitWithNoRepeat(WeatherDay day,
                                              Map<String, List<ClothingArticle>> wardrobeMap,
                                              Set<String> previousNames) {
        Outfit first = outfitCreator.createOutfitForDay(day, shuffledCopy(wardrobeMap), false);
        if (first == null) return null;
        if (!isSameTopOrBottom(first, previousNames)) {
            return first;
        }

        for (int attempt = 0; attempt < 4; attempt++) {
            Outfit candidate = outfitCreator.createOutfitForDay(day, shuffledCopy(wardrobeMap), false);
            if (candidate != null && !isSameTopOrBottom(candidate, previousNames)) {
                return candidate;
            }
        }

        Map<String, List<ClothingArticle>> filtered = new HashMap<>();
        for (Map.Entry<String, List<ClothingArticle>> entry : wardrobeMap.entrySet()) {
            List<ClothingArticle> list = new ArrayList<>();
            for (ClothingArticle item : entry.getValue()) {
                if (item != null && (previousNames == null || !previousNames.contains(item.getName()))) {
                    list.add(item);
                }
            }
            filtered.put(entry.getKey(), list);
        }
        Outfit lastTry = outfitCreator.createOutfitForDay(day, filtered, false);
        return lastTry != null ? lastTry : first;
    }

    private Map<String, List<ClothingArticle>> shuffledCopy(Map<String, List<ClothingArticle>> original) {
        Map<String, List<ClothingArticle>> copy = new HashMap<>();
        for (Map.Entry<String, List<ClothingArticle>> entry : original.entrySet()) {
            List<ClothingArticle> list = new ArrayList<>(entry.getValue());
            Collections.shuffle(list);
            copy.put(entry.getKey(), list);
        }
        return copy;
    }

    private boolean isSameTopOrBottom(Outfit outfit, Set<String> previousNames) {
        if (outfit == null || previousNames == null) return false;
        Map<String, ClothingArticle> items = outfit.getItems();
        if (items == null) return false;
        ClothingArticle top = items.get("top");
        ClothingArticle bottom = items.get("bottom");
        boolean sameTop = top != null && top.getName() != null && previousNames.contains(top.getName());
        boolean sameBottom = bottom != null && bottom.getName() != null && previousNames.contains(bottom.getName());
        return sameTop && sameBottom;
    }

    private Set<String> extractNames(Outfit outfit) {
        Set<String> names = new HashSet<>();
        if (outfit == null || outfit.getItems() == null) {
            return names;
        }
        for (ClothingArticle article : outfit.getItems().values()) {
            if (article != null && article.getName() != null) {
                names.add(article.getName());
            }
        }
        return names;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TripPlanner planner = new TripPlanner(null, null, null, null);
            planner.setVisible(true);
        });
    }

    private String[] buildLocationOptions() {
        LinkedHashSet<String> options = new LinkedHashSet<>();
        options.add("(select)");
        options.addAll(Arrays.asList(availableCities));
        return options.toArray(new String[0]);
    }
}
