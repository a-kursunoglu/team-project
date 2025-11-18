package FuzeWardrobePlanner.Entity.Weather;

import java.util.ArrayList;
import java.util.List;

public class WeatherWeek {
    private List<WeatherDay> weekDays;
    private String defaultLocation;

    public WeatherWeek() {
        this.weekDays = new ArrayList<>();
        this.defaultLocation = "";
    }

    public WeatherWeek(String defaultLocation) {
        this.weekDays = new ArrayList<>();
        this.defaultLocation = defaultLocation;
    }

    public WeatherWeek(List<WeatherDay> weekDays, String defaultLocation) {
        this.weekDays = weekDays != null ? weekDays : new ArrayList<>();
        this.defaultLocation = defaultLocation;
    }

    public List<WeatherDay> getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(List<WeatherDay> weekDays) {
        this.weekDays = weekDays != null ? weekDays : new ArrayList<>();
    }

    public String getDefaultLocation() {
        return defaultLocation;
    }

    public void setDefaultLocation(String defaultLocation) {
        this.defaultLocation = defaultLocation;
    }

    public void addWeatherDay(WeatherDay weatherDay) {
        if (weatherDay != null) {
            weekDays.add(weatherDay);
        }
    }

    public WeatherDay getWeatherDay(int index) {
        if (index < 0 || index >= weekDays.size()) {
            return null;
        }
        return weekDays.get(index);
    }

    public WeatherDay removeWeatherDay(int index) {
        if (index < 0 || index >= weekDays.size()) {
            return null;
        }
        return weekDays.remove(index);
    }

    public int size() {
        return weekDays.size();
    }
}
