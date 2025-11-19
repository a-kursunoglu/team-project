package FuzeWardrobePlanner.UserCases;

import FuzeWardrobePlanner.API.WeatherFetcher;
import FuzeWardrobePlanner.Entity.Weather.WeatherDay;

import java.time.LocalDate;

/**
 * ViewWeather: Displays today's weather (high, low, weather) based on location.
 * If user does NOT provide coordinates → use default Toronto weather.
 * If user DOES provide coordinates → fetch weather for that location.
 */
public class ViewWeather {

    /**
     * Case 1: User did NOT provide latitude/longitude
     * → use WeatherFetcher's default constructor (Toronto)
     */
    public void displayToday() {
        WeatherFetcher fetcher = new WeatherFetcher();   // default: Toronto
        String today = LocalDate.now().toString();

        WeatherDay day = fetcher.getWeatherByDate(today);

        if (day == null) {
            System.out.println("No weather data available for today.");
            return;
        }

        printWeather(day);
    }

    /**
     * Case 2: User provided latitude & longitude
     */
    public void displayToday(double latitude, double longitude) {

        String today = LocalDate.now().toString();
        int days = 3; // fetch at least today's range (you can choose 1 or more)

        // Your WeatherFetcher constructor is:
        // (String startDate, int forecastDays, double longitude, double latitude)
        WeatherFetcher fetcher =
                new WeatherFetcher(today, days, longitude, latitude);

        WeatherDay day = fetcher.getWeatherByDate(today);

        if (day == null) {
            System.out.println("No weather data available for this location.");
            return;
        }

        printWeather(day);
    }

    /**
     * Helper: prints WeatherDay information nicely
     */
    private void printWeather(WeatherDay day) {
        System.out.println("======== Today's Weather ========");
        System.out.println("Date: " + day.getDate());
        System.out.println("Weather: " + day.getWeather());
        System.out.println("High: " + day.getTemperatureHigh() + "°C");
        System.out.println("Low:  " + day.getTemperatureLow() + "°C");
        double[] loc = day.getLocation();
        System.out.println("Location: lon=" + loc[0] + ", lat=" + loc[1]);
        System.out.println("=================================");
    }
}
