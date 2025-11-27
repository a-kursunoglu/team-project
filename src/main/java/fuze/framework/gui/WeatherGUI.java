package fuze.framework.gui;

import fuze.usecases.viewweather.ViewWeather;
import fuze.entity.weather.WeatherDay;
import fuze.framework.weatherapi.WeatherFetcher;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class WeatherGUI extends JFrame {

    private JTextField latField;
    private JTextField lonField;
    private JTextArea outputArea;

    public WeatherGUI() {
        super("Weather Viewer");

        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2));

        inputPanel.add(new JLabel("Latitude:"));
        latField = new JTextField();
        inputPanel.add(latField);

        inputPanel.add(new JLabel("Longitude:"));
        lonField = new JTextField();
        inputPanel.add(lonField);

        JButton fetchBtn = new JButton("Get Weather");
        inputPanel.add(fetchBtn);

        add(inputPanel, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        fetchBtn.addActionListener(e -> displayWeather());

        setVisible(true);
    }

    private void displayWeather() {
        String latText = latField.getText().trim();
        String lonText = lonField.getText().trim();

        ViewWeather view = new ViewWeather();
        WeatherDay day;

        if (latText.isEmpty() || lonText.isEmpty()) {

            WeatherFetcher f = new WeatherFetcher();
            day = f.getWeatherByDate(LocalDate.now().toString());
        } else {
            try {
                double latitude = Double.parseDouble(latText);
                double longitude = Double.parseDouble(lonText);

                WeatherFetcher f =
                        new WeatherFetcher(LocalDate.now().toString(), 3, longitude, latitude);

                day = f.getWeatherByDate(LocalDate.now().toString());
            } catch (Exception ex) {
                outputArea.setText("Invalid input. Please enter numbers.");
                return;
            }
        }

        if (day == null) {
            outputArea.setText("No weather data available.");
            return;
        }

        outputArea.setText(
                "Date: " + day.getDate() + "\n" +
                        "Weather: " + day.getWeather() + "\n" +
                        "High: " + day.getTemperatureHigh() + "°C\n" +
                        "Low: " + day.getTemperatureLow() + "°C\n" +
                        "Location: lon=" + day.getLocation()[0] +
                        ", lat=" + day.getLocation()[1] + "\n"
        );
    }

    public static void main(String[] args) {
        new WeatherGUI();
    }
}
