package FuzeWardrobePlanner.App.Gui;

import FuzeWardrobePlanner.Entity.Weather.WeatherDay;
import FuzeWardrobePlanner.Entity.Weather.WeatherWeek;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple weekly planner UI: shows location on the left and a 7x3 grid on the right.
 * Row 1: dates, Row 2: temperatures, Row 3: empty placeholders.
 */
public class WeeklyPlanner extends JFrame {

    private JLabel locationLabel;
    private JLabel[][] cells; // [row][col]

    public WeeklyPlanner() {
        super("Weekly Planner");
        initUi();
    }

    private void initUi() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        locationLabel = new JLabel("Location: -");
        locationLabel.setFont(locationLabel.getFont().deriveFont(Font.BOLD, 15f));
        leftPanel.add(locationLabel);
        add(leftPanel, BorderLayout.WEST);

        JPanel gridPanel = new JPanel(new GridLayout(3, 7, 6, 6));
        cells = new JLabel[3][7];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 7; c++) {
                JLabel cell = new JLabel("", SwingConstants.CENTER);
                cell.setBorder(new LineBorder(Color.LIGHT_GRAY));
                cells[r][c] = cell;
                gridPanel.add(cell);
            }
        }
        add(gridPanel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(1100, 250));
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Populate the grid with data from the given WeatherWeek.
     * Only the first 7 days are shown.
     */
    public void setWeatherWeek(WeatherWeek week) {
        String location = (week != null && week.getDefaultLocation() != null)
                ? week.getDefaultLocation()
                : "-";
        locationLabel.setText("Location: " + location);

        List<WeatherDay> days = (week != null && week.getWeekDays() != null)
                ? week.getWeekDays()
                : new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            WeatherDay day = i < days.size() ? days.get(i) : null;
            // Use placeholder variable-like labels when real data is missing so the UI is never blank.
            cells[0][i].setText(day != null ? safe(day.getDate()) : "date" + (i + 1));
            cells[1][i].setText(day != null ? (day.getAverageTemperature() + "°") : "temp" + (i + 1));
            cells[2][i].setText("outfit" + (i + 1)); // placeholder for future content
        }
    }

    private String safe(String value) {
        return value == null ? "-" : value;
    }
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        WeeklyPlanner planner = new WeeklyPlanner();
        planner.setVisible(true);
    });
}
}
