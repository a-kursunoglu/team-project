package FuzeWardrobePlanner.App.Gui;

import FuzeWardrobePlanner.Entity.Weather.WeatherDay;
import FuzeWardrobePlanner.Entity.Weather.WeatherTrip;
import FuzeWardrobePlanner.Entity.Weather.WeatherWeek;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Consumer;

public class TripPlanner extends JFrame {

    private JComboBox<String> locationDropdown;
    private JTextField startField;
    private JTextField endField;
    private JComboBox<String> outfitsNeededDropdown;
    private JTable plannerTable;
    private JButton generateButton;
    private JButton backButton;

    private final Consumer<WeatherWeek> onBackToMain;

    public TripPlanner() {
        this(null);
    }

    public TripPlanner(Consumer<WeatherWeek> onBackToMain) {
        super("Trip Planner");
        this.onBackToMain = onBackToMain;
        initUi();
    }

    private void initUi() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        content.add(buildFormPanel(), BorderLayout.WEST);
        content.add(buildTablePanel(), BorderLayout.CENTER);

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
        locationDropdown = new JComboBox<>(new String[]{"(select)", "Toronto", "New York", "Vancouver"});
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

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        String[] cols = {"D1", "D2", "D3", "D4", "D5", "D6", "D7"};
        Object[][] rows = new Object[3][cols.length];
        plannerTable = new JTable(rows, cols);
        plannerTable.setBorder(new LineBorder(Color.GRAY));
        plannerTable.setGridColor(Color.GRAY);
        plannerTable.setRowHeight(40);
        plannerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(plannerTable);
        scrollPane.setPreferredSize(new Dimension(800, 260));
        panel.add(scrollPane, BorderLayout.CENTER);

        adjustColumnWidths(cols.length);

        return panel;
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

        // Use WeatherTrip to fetch real weather for this trip
        WeatherTrip trip = new WeatherTrip(loc, startDate.toString(), days);

        String[] cols = new String[days];
        Object[][] rows = new Object[3][days];

        for (int i = 0; i < days; i++) {
            WeatherDay day = trip.getWeatherDay(i);
            if (day == null) {
                LocalDate d = startDate.plusDays(i);
                cols[i] = d.toString();
                rows[0][i] = "Date: " + d;
                rows[1][i] = "Temp: -";
                rows[2][i] = "Outfit: -";
            } else {
                cols[i] = day.getDate();
                rows[0][i] = "Date: " + day.getDate();

                double avg = (day.getTemperatureHigh() + day.getTemperatureLow()) / 2.0;
                rows[1][i] = "Temp: " + Math.round(avg) + "°";

                rows[2][i] = "Outfit: -"; // 这里可以以后接上真正的 outfit
            }
        }

        plannerTable.setModel(new DefaultTableModel(rows, cols));
        plannerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        adjustColumnWidths(days);
    }

    private void adjustColumnWidths(int days) {
        int width = 130;
        for (int i = 0; i < days; i++) {
            plannerTable.getColumnModel().getColumn(i).setPreferredWidth(width);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TripPlanner planner = new TripPlanner();
            planner.setVisible(true);
        });
    }
}
