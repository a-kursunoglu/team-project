package fuze.usecases.viewweather;

import fuze.entity.location.LocationStringToCoordinate;
import fuze.entity.weather.WeatherDay;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class FutureWeatherGUI extends JFrame {

    private JComboBox<String> cityBox;
    private JTextArea outputArea;
    private final FutureWeatherInteractor interactor = new FutureWeatherInteractor();

    public FutureWeatherGUI() {
        super("Future Weather (2 Weeks)");

        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout(5, 5));

        JLabel label = new JLabel("City:");
        top.add(label, BorderLayout.WEST);

        LocationStringToCoordinate converter = new LocationStringToCoordinate();
        String[] cities = converter.getCities();

        cityBox = new JComboBox<>(cities);
        cityBox.setEditable(true);
        top.add(cityBox, BorderLayout.CENTER);

        JButton fetchBtn = new JButton("Check Weather");
        top.add(fetchBtn, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);

        fetchBtn.addActionListener(e -> showWeather());

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void showWeather() {
        Object selected = cityBox.getEditor().getItem();
        String city = (selected == null ? "" : selected.toString().trim());

        String displayCity = city.isEmpty() ? "Toronto Canada" : city;

        List<WeatherDay> list = interactor.getTwoweeksWeather(city);

        if (list == null || list.isEmpty()) {
            outputArea.setText("No forecast available for: " + displayCity);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Future Weather (Next 14 Days) for ")
                .append(displayCity)
                .append("\n\n");

        for (WeatherDay day : list) {
            sb.append("Date: ").append(day.getDate()).append("\n");
            sb.append("Weather: ").append(day.getWeather()).append("\n");
            sb.append("High: ").append(day.getTemperatureHigh()).append("°C\n");
            sb.append("Low: ").append(day.getTemperatureLow()).append("°C\n");
            sb.append("Location: lon=").append(day.getLocation()[0])
                    .append(", lat=").append(day.getLocation()[1]).append("\n\n");
        }

        outputArea.setText(sb.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FutureWeatherGUI::new);
    }
}
