package fuze.entity.weather;

import fuze.entity.location.LocationStringToCoordinate;
import fuze.weatherapi.WeatherFetcher;
import org.json.JSONArray;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Pretty much the same as WeatherWeek except designed for a trip in a different location
 * Also extends WeatherDays
 */
public class WeatherTrip extends WeatherDays{
    private Queue<WeatherDay> days;
    private WeatherFetcher weatherFetcher;
    private double[] location;
    private String startDate;
    private int tripLength;

    /**
     * Creates an instance of WeatherTrip
     * @param locationString String of the location ie "Toronto"
     * @param startDate String of form "yyyy-mm-dd" NOTE there will be errors if
     *                  startDate is too far in the future
     * @param tripLength Number of days of the trip, same applies from startDate,
     *                   if too long, weather won't have predictions
     */
    public WeatherTrip(String locationString, String startDate, int tripLength) {
        LocationStringToCoordinate locationObj = new LocationStringToCoordinate(locationString);
        double longitude = locationObj.getLongitude();
        double latitude = locationObj.getLatitude();
        this.location = new double[]{longitude, latitude};
        this.startDate = startDate;
        this.tripLength = Math.min(tripLength, 7);
        this.weatherFetcher = new WeatherFetcher(startDate, this.tripLength, longitude, latitude);
        this.days = constructWeatherQueue();
    }
    /**
     * Creates an instance of WeatherTrip
     * @param longitude double
     * @param latitude double
     * @param startDate String of form "yyyy-mm-dd" NOTE there will be errors if
     *                  startDate is too far in the future
     * @param tripLength Number of days of the trip, same applies from startDate,
     *                   if too long, weather won't have predictions
     */
    public WeatherTrip(double longitude, double latitude, String startDate, int tripLength) {
        this. location = new double[]{longitude, latitude};
        this.startDate = startDate;
        this.tripLength = Math.min(tripLength, 6);
        this.weatherFetcher = new WeatherFetcher(startDate, this.tripLength, longitude, latitude);
        this.days = constructWeatherQueue();
    }

    /**
     * Same as WeatherWeek
     * @return refer to WeatherWeek
     */
    private Queue<WeatherDay> constructWeatherQueue(){
        JSONArray dates = weatherFetcher.getForecastDates();
        Queue<WeatherDay> weatherQueue = new LinkedList<>();
        for (int i = 0; i < dates.length(); i++){
            weatherQueue.add(weatherFetcher.getWeatherByDate(dates.getString(i)));
        }
        return weatherQueue;
    }

    public double[] getLocation() {
        return location;
    }

    public int getTripLength() {
        return tripLength;
    }

    public String getStartDate() {
        return startDate;
    }

    @Override
    /**
     * BE CAREFUL AGAIN with calls to iterator so we don't have multiple references
     * to the same instance, iterating from halfway through the loop
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

    @Override
    /**
     * Same warning as above
     */
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
