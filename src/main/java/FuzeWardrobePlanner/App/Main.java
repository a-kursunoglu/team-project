package FuzeWardrobePlanner.App;

import FuzeWardrobePlanner.API.WeatherFetcher;
import FuzeWardrobePlanner.Entity.Weather.WeatherTrip;
import FuzeWardrobePlanner.Entity.Weather.WeatherWeek;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
       // Do whatever you want here until we implement the final stuff, yay
//        WeatherFetcher weatherFetcher = new WeatherFetcher();
//        System.out.println(weatherFetcher.getWeatherData().getJSONObject("daily").getJSONArray("time").getString(0));
//        Map<String, Object> weather = weatherFetcher.getWeatherData().toMap();
//        System.out.println(weather);
//        System.out.println(weather.get("daily").getClass());
//        WeatherDay weatherDay = weatherFetcher.getWeatherByDate("2025-11-24");
//        System.out.println(weatherDay.toString());
        WeatherWeek weatherWeek = new WeatherWeek();
        System.out.println(weatherWeek.getWeatherDay(1));
        WeatherTrip trip = new WeatherTrip(-122.42, 37.775,"2025-11-20", 10);
        System.out.println(trip.toString());

    }
}
