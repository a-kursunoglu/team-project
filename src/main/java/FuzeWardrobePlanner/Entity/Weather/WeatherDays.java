package FuzeWardrobePlanner.Entity.Weather;

import java.sql.Array;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import FuzeWardrobePlanner.Entity.Weather.WeatherDay;
import org.json.JSONArray;

/**
 * Abstract Class, extended by WeatherTrip and WeatherWeek
 * has a Queue that holds a linkedList of WeatherDay-s
 */
public abstract class WeatherDays {

    private Queue<WeatherDay> days;

    /**
     * I'm not sure if we'll use this, but I'll leave it in
     * @param weather a WeatherDay, it will be added to the end of the Queue
     */
    public void addWeatherDay(WeatherDay weather) {
        this.days.add(weather);
    }

    public Queue<WeatherDay> getDayQueue() {
        return this.days;
    }

    /**
     * All of our abstract methods
     * @param index the number of the days from startDate
     * @return returns the WeatherDay of a numbered day in the series
     */
    abstract WeatherDay getWeatherDay(int index);
    abstract JSONArray getDays();

    @Override
    public String toString() {
        String result = "";
        Iterator<WeatherDay> iterator = this.days.iterator();
        while(iterator.hasNext()){
            WeatherDay weather = iterator.next();
            result = result + "\n" + weather.toString();
        }
        return result;
    }
}