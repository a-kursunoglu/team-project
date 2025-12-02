package fuze.usecases.viewweather;

import fuze.entity.location.LocationStringToCoordinate;
import fuze.entity.weather.WeatherDay;
import fuze.framework.weatherapi.WeatherFetcher;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class FutureWeatherInteractor {
    /**
     * Creates a list for the weather throughout the next two weeks.
     * @param city is a string representation of the location (default Toronto)
     * @return A list of WeatherDay's
     */
    public List<WeatherDay> getTwoweeksWeather(final String city) {
        LocationStringToCoordinate converter =
                new LocationStringToCoordinate(city);
        double lon = converter.getLongitude();
        double lat = converter.getLatitude();
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
