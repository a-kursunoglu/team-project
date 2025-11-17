package FuzeWardrobePlanner.API;

import FuzeWardrobePlanner.Entity.Weather.WeatherDay;
import FuzeWardrobePlanner.API.APIReader;
import java.time.LocalDate;
import org.json.JSONObject;

import java.io.IOException;

public class WeatherFetcher {
    private boolean isDataLoaded = false;
    private int forecastDays;
    private double longitude;
    private double latitude;
    private String startDate;
    private JSONObject weatherData;


    public WeatherFetcher() {
        // to construct later
        // 7 forecast days by default
        this.forecastDays = 7;
        // Default location of Toronto (Drake's home)
        this.longitude = -79.38;
        this.latitude = 43.65;
        this.startDate = LocalDate.now().toString();
    }

    public WeatherFetcher(String startDate, int forecastDays) {
        // to be done later
        this.longitude = -79.38;
        this.latitude = 43.65;
        this.startDate = startDate;
    }

    public WeatherFetcher(String startDate, int forecastDays, double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.forecastDays = forecastDays;
        this.startDate = startDate;
    }


    public void loadWeeklyForecast() {


        // here to handle JSON Object from APIReader
        // Jaden to do but if someone else would like to that would be great, I
        // wrote all the return statements in the APIReader documentation.
        try{
            APIReader apiReader = new APIReader();
            this.weatherData = apiReader.readAPI(this.longitude, this.latitude, this.startDate, this.forecastDays);
            this.isDataLoaded = true;
        }
        catch (Exception e){
            this.isDataLoaded = false;
            this.weatherData = null;
        }

    }



    public WeatherDay getWeatherByDate(String date) {
        // TODO : this is to be implemented by whoever writes the WeatherDay class
        // do after loadWeeklyForecast method is implemented

        // OH ALSO, there the WMO data from APIReader needs to be stored in some way.
        // Like raining, snowing or sunny, whatever you see fit.
        // Please refer to this for WMO int values:
        // 00 - 03: clear skies
        // 04 - 19 & 40 - 49: cloudy
        // Rest: Snow or Rain, refer to:
        // https://www.nodc.noaa.gov/archive/arc0021/0002199/1.1/data/0-data/HTML/WMO-CODE/WMO4677.HTM
        return null;
        // ^delete later
    }
}
