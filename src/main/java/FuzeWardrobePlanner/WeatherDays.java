package FuzeWardrobePlanner;

import java.util.LinkedList;
import java.util.Queue;

public class WeatherDays {

    private Queue<WeatherDay> day;
    private String location;

    public WeatherDays(WeatherDay weather, String location) {
        this.day = new LinkedList<>();
        this.day.add(weather);
        this.location = location;
    }

    public void addWeatherDay(WeatherDay weather) {
        this.day.add(weather);
    }

    public Queue<WeatherDay> getDayQueue() {
        return this.day;
    }

    public String getLocation() {
        return this.location;
    }
}