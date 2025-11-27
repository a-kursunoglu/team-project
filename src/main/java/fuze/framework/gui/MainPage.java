package fuze.framework.gui;

import fuze.entity.clothing.ClothingArticle;
import fuze.entity.clothing.Outfit;
import fuze.framework.data.JsonWardrobeRepository;
import fuze.interfaceadapter.UploadClothingController;
import fuze.interfaceadapter.UploadClothingPresenter;
import fuze.usecases.managewardrobe.WardrobeRepository;
import fuze.entity.location.LocationStringToCoordinate;
import fuze.entity.weather.WeatherDay;
import fuze.entity.weather.WeatherWeek;
import fuze.usecases.generateoutfit.OutfitCreator;
import fuze.usecases.planweekly.WeeklyPlanner;
import fuze.usecases.trippacking.TripPlanner;
import fuze.usecases.uploadclothing.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class MainPage extends JFrame {

    private JComboBox<String> locationDropdown;
    private JLabel currentLocationLabel;
    private JLabel temperatureLabel;
    private final WardrobeRepository wardrobeRepository;
    private WeatherWeek currentWeek;
    private String currentLocation;
    private final OutfitCreator outfitCreator = new OutfitCreator();
    private final LocationStringToCoordinate locationTranslator;
    private final String[] availableCities;

    private JPanel outfitPanel;

    public MainPage() {
        super("Fuze's Weather Wardrobe Planner");

        String userHome = System.getProperty("user.home");
        String wardrobePath = Paths.get(userHome, ".fuzewardrobe", "wardrobe.json").toString();
        this.wardrobeRepository = new JsonWardrobeRepository(wardrobePath);
        this.currentWeek = null;
        this.locationTranslator = new LocationStringToCoordinate();
        String[] cities = locationTranslator.getCities();
        this.availableCities = cities != null ? cities : new String[]{"Toronto Canada", "New York", "Vancouver"};
        this.currentLocation = availableCities[0];

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

        String defaultLoc = currentLocation;

        locationDropdown = new JComboBox<>(buildLocationOptions(defaultLoc));
        locationDropdown.setSelectedItem(defaultLoc);
        locationDropdown.setMaximumSize(new Dimension(250, 32));
        panel.add(locationDropdown);

        locationDropdown.addActionListener(e -> {
            String selected = Objects.toString(locationDropdown.getSelectedItem(), "Toronto Canada");
            currentLocation = selected;
            reloadWeatherForCurrentLocation();
        });

        panel.add(Box.createVerticalStrut(12));
        panel.add(label("Your location is set to:"));
        currentLocationLabel = bigLabel("Unknown");
        panel.add(currentLocationLabel);

        panel.add(Box.createVerticalStrut(12));
        panel.add(label("Temperature:"));
        temperatureLabel = veryBigLabel("--°");
        panel.add(temperatureLabel);

        panel.add(Box.createVerticalStrut(20));
        JButton addTripButton = makeButton("Add Trip");
        JButton addClothingButton = makeButton("Add Clothing Item");
        JButton viewWardrobeButton = makeButton("View Wardrobe");
        JButton planWeekButton = makeButton("Plan for the Week");
        JButton refreshButton = makeButton("Refresh Outfit");

        addClothingButton.addActionListener(e -> openUploadClothingWindow());
        addTripButton.addActionListener(e -> openTripPlanner());
        viewWardrobeButton.addActionListener(e -> openWardrobeWindow());
        planWeekButton.addActionListener(e -> openWeeklyPlanner());
        refreshButton.addActionListener(e -> refreshOutfits());

        panel.add(addTripButton);
        panel.add(addClothingButton);
        panel.add(viewWardrobeButton);
        panel.add(planWeekButton);
        panel.add(refreshButton);

        return panel;
    }

    private void openUploadClothingWindow() {
        UploadClothingPresenter presenter = new UploadClothingPresenter(null);

        UploadClothingInputBoundary interactor =
                new UploadClothingInteractor(wardrobeRepository, presenter);

        UploadClothingController controller =
                new UploadClothingController(interactor);

        UploadClothingPanel uploadPanel = new UploadClothingPanel(controller);

        presenter.setView(uploadPanel);

        JDialog dialog = new JDialog(this, "Add Clothing Item", true);
        dialog.setContentPane(uploadPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
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

    public void loadFromWeatherWeek(WeatherWeek week) {
        this.currentWeek = week;
        if (this.currentWeek != null) {
            generateOutfitsForWeek(this.currentWeek);
        }
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

    private void refreshOutfits() {
        currentWeek = new WeatherWeek(currentLocation);
        generateOutfitsForWeek(currentWeek);
        loadFromWeatherWeek(currentWeek);
    }

    private void reloadWeatherForCurrentLocation() {
        WeatherWeek week = new WeatherWeek(currentLocation);
        loadFromWeatherWeek(week);
    }

    private void generateOutfitsForWeek(WeatherWeek week) {
        Map<String, List<ClothingArticle>> wardrobeMap = buildWardrobeMap(wardrobeRepository.getAll());
        if (wardrobeMap.isEmpty()) {
            return;
        }
        Set<String> prevNames = new HashSet<>();
        for (int i = 0; i < 7; i++) {
            WeatherDay day = week.getWeatherDay(i);
            if (day == null) continue;
            Outfit outfit = generateOutfitWithNoRepeat(day, wardrobeMap, prevNames);
            day.setOutfit(outfit);
            prevNames = extractNames(outfit);
        }
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
        if (outfit == null || outfit.getItems() == null) return names;
        for (ClothingArticle item : outfit.getItems().values()) {
            if (item != null && item.getName() != null) {
                names.add(item.getName());
            }
        }
        return names;
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

    private void openTripPlanner() {
        SwingUtilities.invokeLater(() -> {
            TripPlanner planner = new TripPlanner(this::loadFromWeatherWeek, wardrobeRepository, outfitCreator, availableCities);
            planner.setVisible(true);
        });
    }

    private void openWardrobeWindow() {
        WardrobeGUI gui = new WardrobeGUI(wardrobeRepository);
        gui.show();
    }

    private void openWeeklyPlanner() {
        WeeklyPlanner planner = new WeeklyPlanner();
        if (currentWeek == null) {
            currentWeek = new WeatherWeek(currentLocation);
        }
        generateOutfitsForWeek(currentWeek);
        planner.setWeatherWeek(currentWeek);
        planner.setVisible(true);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainPage page = new MainPage();
            page.reloadWeatherForCurrentLocation();
            page.setVisible(true);
        });
    }

    private String[] buildLocationOptions(String defaultLocation) {
        LinkedHashSet<String> options = new LinkedHashSet<>();
        if (defaultLocation != null) {
            options.add(defaultLocation);
        }
        options.addAll(Arrays.asList(availableCities));
        return options.toArray(new String[0]);
    }
}
