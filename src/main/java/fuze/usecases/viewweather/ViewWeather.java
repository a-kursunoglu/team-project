package fuze.usecases.viewweather;

import fuze.framework.weatherapi.WeatherFetcher;
import fuze.entity.weather.WeatherDay;

import java.time.LocalDate;


public class ViewWeather {


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


    public void displayToday(double latitude, double longitude) {

        String today = LocalDate.now().toString();
        int days = 3;

        WeatherFetcher fetcher =
                new WeatherFetcher(today, days, longitude, latitude);

        WeatherDay day = fetcher.getWeatherByDate(today);

        if (day == null) {
            System.out.println("No weather data available for this location.");
            return;
        }

        printWeather(day);
    }


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
