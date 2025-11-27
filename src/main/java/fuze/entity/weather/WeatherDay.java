package fuze.entity.weather;

import fuze.entity.clothing.Outfit;

/**
 * A class to represent everything for a given day, the outfit, all the
 * weather parameters, and whatever else you'd like!
 */
public class WeatherDay {

    private String weather;
    private double temperatureHigh;
    private double temperatureLow;
    private Outfit outfit;
    private double[] location;
    private String date;

    /**
     * Creates a new instance of WeatherDay, only really to be called by WeatherFetcher
     *
     * @param weather         is the int WMO code for the weather condition (look it up
     *                        cuz it's cool)
     * @param temperatureHigh Celsius double representing daily high
     * @param temperatureLow  Celsius double representing daily low
     * @param location        a double[] form of [longitude, latitude]
     * @param date            a string representation of "yyyy-mm-dd"
     */
    public WeatherDay(int weather, double temperatureHigh, double temperatureLow, double[] location, String date) {
        this.temperatureHigh = temperatureHigh;
        this.temperatureLow = temperatureLow;
        this.outfit = null; // CHANGE LATER
        this.location = location;
        this.date = date;
        this.weather = weatherCodeToString(weather);
    }

    /**
     * Handles all WMO Codes so we shouldn't have to worry about them outside of
     * this
     *
     * @param weatherCode same WMO code above
     * @return String representation of what I thought the most common codes
     * would be given the website we use and NOAA data.
     */
    public String weatherCodeToString(int weatherCode) {
        if (weatherCode == 0) {
            return "Sunny";
        } else if (weatherCode == 1 || weatherCode == 2) {
            return "Scattered Clouds";
        } else if (weatherCode < 20 || (40 <= weatherCode && weatherCode < 50)) {
            return "Cloudy Skies";
        } else if (weatherCode == 22 || weatherCode == 26) {
            return "Snowing";
        } else if (weatherCode <= 200) {
            return "Raining";
        }
        return "No Weather Data";
    }

    public String getWeather() {
        return weather;
    }

    public double getTemperatureHigh() {
        return temperatureHigh;
    }

    public double getTemperatureLow() {
        return temperatureLow;
    }

    public Outfit getOutfit() {
        return outfit;
    }

    public void setOutfit(Outfit outfit) {
        this.outfit = outfit;
    }

    public double[] getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "WeatherDay on " + date +
                " weather='" + weather + '\'' +
                ", temperature high=" + temperatureHigh +
                ", temperature low=" + temperatureLow +
                ", outfit=" + outfit +
                ", location='" + location[0]
                + ", " + location[1] + '\'';
    }

    public double getTemperature() {
        return (temperatureHigh + temperatureLow) / 2.0;
    }
}
