package FuzeWardrobePlanner.App.Gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Trip Planner UI matching the provided wireframe.
 * Contains inputs for location, start/end dates, outfit count, and a grid placeholder,
 * plus a Generate button and a Back to Main Page button.
 */
public class TripPlanner extends JFrame {

    private JComboBox<String> locationDropdown;
    private JTextField startField;
    private JTextField endField;
    private JComboBox<String> outfitsNeededDropdown;
    private JTable plannerTable;
    private JButton generateButton;
    private JButton backButton;

    public TripPlanner() {
        super("Trip Planner");
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

        setPreferredSize(new Dimension(1000, 500));
        pack();
        setLocationRelativeTo(null);

        wireActions();
    }

    private void wireActions() {
        generateButton.addActionListener(e -> {
            String start = startField.getText().trim();
            String end = endField.getText().trim();
            if (start.isEmpty() || end.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter both start and end dates (yyyy-mm-dd).",
                        "Missing dates",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            // TODO: replace with actual trip generation logic
            JOptionPane.showMessageDialog(this,
                    "Generate trip plan (to be implemented)",
                    "Trip Planner", JOptionPane.INFORMATION_MESSAGE);
        });

        backButton.addActionListener(e -> {
            // Simple navigation: open MainPage and close this window.
            SwingUtilities.invokeLater(() -> {
                MainPage main = new MainPage();
                main.loadFromWeatherWeek(new FuzeWardrobePlanner.Entity.Weather.WeatherWeek());
                main.setVisible(true);
            });
            dispose();
        });
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        form.add(label("Location:"));
        // TODO populate with actual locations
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
        // TODO: provide numeric choices 1-7
        outfitsNeededDropdown = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7"});
        outfitsNeededDropdown.setMaximumSize(new Dimension(250, 32));
        form.add(outfitsNeededDropdown);

        return form;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Placeholder table with 7 columns matching the wireframe
        String[] cols = {"D1", "D2", "D3", "D4", "D5", "D6", "D7"};
        Object[][] rows = new Object[3][cols.length]; // minimal rows to show grid lines
        plannerTable = new JTable(rows, cols);
        plannerTable.setBorder(new LineBorder(Color.GRAY));
        plannerTable.setGridColor(Color.GRAY);
        plannerTable.setRowHeight(40);

        JScrollPane scrollPane = new JScrollPane(plannerTable);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TripPlanner planner = new TripPlanner();
            planner.setVisible(true);
        });
    }
}
