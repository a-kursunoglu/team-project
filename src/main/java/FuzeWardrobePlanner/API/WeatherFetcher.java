package FuzeWardrobePlanner.API;

import FuzeWardrobePlanner.Entity.Weather.WeatherDay;
import FuzeWardrobePlanner.API.APIReader;
import java.time.LocalDate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A class to give another layer between the weather entities and the API handling,
 * This also allows us to make fewer API calls by consolidating them into one call
 * over a range of days.
 */
public class WeatherFetcher {
    /**
     * @param startDate is of form "yyyy-mm-dd" (same as throughout this project)
     * @param forecastDays is an int representing the number of days to predict out to
     * @param longitude is a double and so is
     * @param latitude which will sometimes be stored in double[] form
     */
    private boolean isDataLoaded = false;
    private int forecastDays;
    private double longitude;
    private double latitude;
    private String startDate;
    private JSONObject weatherData;


    public WeatherFetcher() {
        // This is the default that will run for WeatherWeek
        // 7 forecast days by default (6 cuz starts at zero)
        this.forecastDays = 6;
        // Default location of Toronto (Drake's home)
        this.longitude = -79.38;
        this.latitude = 43.65;
        this.startDate = LocalDate.now().toString();
        this.weatherData = new JSONObject();
        this.isDataLoaded = false;
        loadWeeklyForecast();
    }

    public WeatherFetcher(String startDate, int forecastDays) {
        // No use for this unless someone needs to implement it
        this.longitude = -79.38;
        this.latitude = 43.65;
        this.startDate = startDate;
        this.forecastDays = forecastDays;
        this.weatherData = new JSONObject();
        this.isDataLoaded = false;
        loadWeeklyForecast();
    }

    public WeatherFetcher(String startDate, int forecastDays, double longitude, double latitude) {
        // This one requires all the parameters, it's really only used when instancing
        // WeatherTrip
        this.longitude = longitude;
        this.latitude = latitude;
        this.forecastDays = forecastDays;
        this.startDate = startDate;
        this.weatherData = new JSONObject();
        this.isDataLoaded = false;
        loadWeeklyForecast();
    }

    public JSONObject getWeatherData() {
        return weatherData;
    }

    public boolean isDataLoaded() {
        return isDataLoaded;
    }

    public JSONArray getForecastDates() {
        return weatherData.getJSONObject("daily").getJSONArray("time");
    }

    /**
     * This loads a whole week instead of making a bunch of calls, it is important
     * to have the isLoaded bool because otherwise the data hasn't been properly
     * called.
     */
    private void loadWeeklyForecast() {
        // TODO: handle the errors like wrong location or no weather data
        try{
            APIReader apiReader = new APIReader();
            this.weatherData = apiReader.readAPI(this.longitude, this.latitude, this.startDate, this.forecastDays);
            this.isDataLoaded = true;
        }
        catch (Exception e){
            System.out.println("Error while loading weather data");
            this.isDataLoaded = false;
            this.weatherData = new JSONObject();
        }

    }


    /**
     *
     * @param date again in "yyyy-mm-dd"
     * @return a WeatherDay entity for the given day
     */
    public WeatherDay getWeatherByDate(String date) {
    //Please refer to WeatherDay
    int i = 0;
    while (i < this.forecastDays) {
        String day = weatherData.getJSONObject("daily").getJSONArray("time").getString(i);
        if (day.equals(date)) {
            int weatherCondition = weatherData.getJSONObject("daily")
                    .getJSONArray("weather_code").getInt(i);
            double longitude = weatherData.getDouble("longitude");
            double latitude = weatherData.getDouble("latitude");
            double tempHigh = weatherData.getJSONObject("daily")
                    .getJSONArray("temperature_2m_max").getDouble(i);
            double tempLow = weatherData.getJSONObject("daily")
                    .getJSONArray("temperature_2m_min").getDouble(i);
            double[] location = new double[]{longitude, latitude};
            WeatherDay weatherDay = new WeatherDay(weatherCondition, tempHigh, tempLow, location, date);
            return weatherDay;
        }
        i++;
    }
    return null;
}
}
