package fuze.usecases.viewweather;

import fuze.entity.location.LocationStringToCoordinate;
import fuze.entity.weather.WeatherDay;
import fuze.framework.weatherapi.WeatherFetcher;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class FutureWeatherInteractor {

    public List<WeatherDay> getTwoweeksWeather(String city) {

        // 1. default -> Toronto
        if (city == null || city.isBlank()) {
            city = "Toronto Canada";
        }

        // 2. convert city name -> coordinates
        LocationStringToCoordinate converter = new LocationStringToCoordinate(city);
        double lon = converter.getLongitude();
        double lat = converter.getLatitude();

        // 3. Query 14-day data
        int days = 14;
        WeatherFetcher fetcher = new WeatherFetcher(
                java.time.LocalDate.now().toString(),
                days,
                lon,
                lat
        );

        JSONArray dateArray = fetcher.getForecastDates();
        List<WeatherDay> list = new ArrayList<>();

        if (dateArray == null || dateArray.isEmpty()) {
            return list;
        }

        for (int i = 0; i < dateArray.length(); i++) {
            String date = dateArray.getString(i);
            WeatherDay day = fetcher.getWeatherByDate(date);
            if (day != null) {
                list.add(day);
            }
        }

        return list;
    }
}
