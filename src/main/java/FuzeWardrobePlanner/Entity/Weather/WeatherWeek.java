package FuzeWardrobePlanner.Entity.Weather;

import FuzeWardrobePlanner.API.WeatherFetcher;
import FuzeWardrobePlanner.Entity.Weather.LocationStringToCoordinate;
import org.json.JSONArray;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Extends the abstract class of WeatherDays, this class organizes the WeatherDay-s
 * in a managable Queue, it also handles calls to WeatherFetcher so we don't make an
 * API call for each individual day.
 */
public class WeatherWeek extends WeatherDays{
    private Queue<WeatherDay> days;
    private WeatherFetcher weatherFetcher;
    private Iterator<WeatherDay> iterator;
    private double[] location;
    private String defaultLocationName;

    /**
     * Does not have any params because it uses default start date of today
     * and the default location of Toronto
     */
    public WeatherWeek() {
        // Default location of Toronto
        location = new double[]{-79.3733, 43.7417};
        this.weatherFetcher = new WeatherFetcher();
        this.defaultLocationName = "Toronto Canada";
        this.days = constructWeatherQueue();
    }

    public WeatherWeek(String locationString) {
        LocationStringToCoordinate locationObj = new LocationStringToCoordinate(locationString);
        double longitude = locationObj.getLongitude();
        double latitude = locationObj.getLatitude();
        this.location = new double[]{longitude, latitude};
        this.weatherFetcher = new WeatherFetcher(longitude, latitude);
        this.defaultLocationName = locationString;
        this.days = constructWeatherQueue();
    }


    /**
     * Constructs the actual Queue
     * @return a Queue of WeatherDay-s
     */
    private Queue<WeatherDay> constructWeatherQueue(){
        JSONArray dates = weatherFetcher.getForecastDates();
        Queue<WeatherDay> weatherQueue = new LinkedList<>();
        for (int i = 0; i < dates.length() - 1; i++){
            weatherQueue.add(weatherFetcher.getWeatherByDate(dates.getString(i)));
        }
        return weatherQueue;
    }

    public String getDefaultLocation() {
        return defaultLocationName;
    }


    @Override
    /**
     * Make sure WE are careful about using iterator calls so we're not starting loops halfway
     */
    public WeatherDay getWeatherDay(int index) {
        Iterator<WeatherDay> iterator = days.iterator();
        int i = 0;
        WeatherDay weather = null;
        while(iterator.hasNext() && i <= index){
            weather = iterator.next();
            i ++;
        }
        return weather;
    }

    @Override
    JSONArray getDays() {
        return this.weatherFetcher.getForecastDates();
    }

    /**
     * Highlight above comment^^^^
     * @return a string representation
     */
    @Override
    public String toString() {
        String result = "";
        Iterator<WeatherDay> iterator = days.iterator();
        while(iterator.hasNext()){
            WeatherDay weather = iterator.next();
            result = result + "\n" + weather.toString();
        }
        return result;
    }
}
