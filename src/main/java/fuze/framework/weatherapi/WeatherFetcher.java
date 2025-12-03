package fuze.framework.weatherapi;

import fuze.entity.weather.WeatherDay;

import java.time.LocalDate;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * A class to give another layer between the weather entities and the API
 * handling, This also allows us to make fewer API calls by consolidating
 * them into one call over a range of days.
 */
public class WeatherFetcher {
    /**
     * @param startDate is of form "yyyy-mm-dd" (same as throughout
     *                  this project)
     * @param forecastDays is an int representing the number of
     *                     days to predict out to
     * @param longitude is a double and so is
     * @param latitude which will sometimes be stored in double[] form
     */
    private boolean isDataLoaded = false;
    private int forecastDays;
    private double longitude;
    private double latitude;
    private String startDate;
    private JSONObject weatherData;

    /**
     * Creates an instance of WeatherFetcher with default settings.
     */
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

    /**
     * Creates instance but for specified location.
     * @param longitude lon double
     * @param latitude lat double
     */
    public WeatherFetcher(final double longitude, final double latitude) {
        // This is the default that will run for WeatherWeek
        // 7 forecast days by default (6 cuz starts at zero)
        this.forecastDays = 6;
        // Default location of Toronto (Drake's home)
        this.longitude = longitude;
        this.latitude = latitude;
        this.startDate = LocalDate.now().toString();
        this.weatherData = new JSONObject();
        this.isDataLoaded = false;
        loadWeeklyForecast();
    }

    /**
     * Combines all of the above.
     * @param startDate see above
     * @param forecastDays see above
     * @param longitude see above
     * @param latitude see above
     */
    public WeatherFetcher(final String startDate, final int forecastDays,
                          final double longitude, final double latitude) {
        // This one requires all the parameters, it's really only used
        // when instancing WeatherTrip
        this.longitude = longitude;
        this.latitude = latitude;
        this.forecastDays = forecastDays;
        this.startDate = startDate;
        this.weatherData = new JSONObject();
        this.isDataLoaded = false;
        loadWeeklyForecast();
    }

    /**
     * Returns actual JSON array
     * @return JSONArray
     */
    public JSONArray getForecastDates() {
        if (weatherData == null) {
            return new JSONArray();
        }
        JSONObject daily = weatherData.optJSONObject("daily");
        if (daily == null) {
            return new JSONArray();
        }
        JSONArray time = daily.optJSONArray("time");
        return time != null ? time : new JSONArray();
    }

    /**
     * This loads a whole week instead of making a bunch of calls, it is
     * important to have the isLoaded bool because otherwise the data hasn't
     * been properly called.
     */
    private void loadWeeklyForecast() {
        try {
            APIReader apiReader = new APIReader();
            JSONObject data = apiReader.readAPI(this.longitude,
                    this.latitude, this.startDate, this.forecastDays);
            if (data != null) {
                this.weatherData = data;
                this.isDataLoaded = true;
            } else {
                this.isDataLoaded = false;
                this.weatherData = new JSONObject();
            }
        } catch (Exception e) {
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
    public WeatherDay getWeatherByDate(final String date) {
        if (!isDataLoaded || weatherData == null || !weatherData.has("daily")) {
            return null;
        }
        //Please refer to WeatherDay
        int i = 0;
        JSONObject daily = weatherData.getJSONObject("daily");
        JSONArray time = daily.optJSONArray("time");
        if (time == null) {
            return null;
        }
        while (i < time.length() && i <= this.forecastDays) {
            String day = time.optString(i, "");
            if (day.equals(date)) {
                JSONArray codes = daily.optJSONArray("weather_code");
                JSONArray highs = daily.optJSONArray("temperature_2m_max");
                JSONArray lows = daily.optJSONArray("temperature_2m_min");
                if (codes == null || highs == null || lows == null) {
                    return null;
                }
                int weatherCondition = codes.optInt(i);
                double longitude = weatherData.optDouble("longitude");
                double latitude = weatherData.optDouble("latitude");
                double tempHigh = highs.optDouble(i);
                double tempLow = lows.optDouble(i);
                double[] location = new double[]{longitude, latitude};
                WeatherDay weatherDay = new WeatherDay(weatherCondition, tempHigh,
                        tempLow, location, date);
                return weatherDay;
            }
            i++;
        }
        return null;
    }
}