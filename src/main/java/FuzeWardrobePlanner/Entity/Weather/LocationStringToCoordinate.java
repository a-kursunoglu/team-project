package FuzeWardrobePlanner.Entity.Weather;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * IMPORTANT TO WHOEVER IMPLEMENTS THIS:
 * If you want a dropdown of all possible cities, use following
 * LocationStringToCoordinate locationTranslator = new LocationStringToCoordinate();
 * String[] listForDropdown = locationTranslator.getCities()
 * - Then iterate through the Array or whatever to make the dropdown
 *
 * Helps handle locations by changing city names into coordinates
 */
public class LocationStringToCoordinate {
    private String locationString;
    private Double longitude;
    private Double latitude;

    /**
     * Creates an instance of LocationStringToCoordinate
     * @param locationString Name of the city
     */
    public LocationStringToCoordinate(String locationString) {
        this.locationString = locationString;
        double[] location = stringToCoord(locationString);
        this.longitude = location[0];
        this.latitude = location[1];
    }

    /**
     * Also creates a new instance but set to default.
     */
    public LocationStringToCoordinate() {
        this.locationString = "Toronto";
        double[] location = stringToCoord(locationString);
        this.longitude = location[0];
        this.latitude = location[1];
    }

    public void changeLocation(String locationString) {
        this.locationString = locationString;
        double[] location = stringToCoord(locationString);
        this.longitude = location[0];
        this.latitude = location[1];
    }

    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public String getLocationString() {
        return locationString;
    }

    /**
     * @return Returns an array of strings with every possible city
     */
    public String[] getCities() {
        String[] strings = new String[89];
        int i = 0;
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("src/main/java/FuzeWardrobePlanner" +
                            "/Entity/Weather/cityCoordinates/CitiesLongLat.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (i != 0) {
                    strings[i - 1] = values[1];
                }
                i++;
            }
            br.close();
        }catch (FileNotFoundException e){
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return strings;
    }


    private double[] stringToCoord(String city){
        Double longitudeResult = -79.3733;
        Double latitudeResult = 43.7417;
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("src/main/java/FuzeWardrobePlanner" +
                            "/Entity/Weather/cityCoordinates/CitiesLongLat.csv"));
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values[0].equals(city)){
                    longitudeResult = Double.parseDouble(values[3]);
                    latitudeResult = Double.parseDouble(values[2]);
                    break;
                }
            }
            br.close();
        }catch (FileNotFoundException e){
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        double[] location = new double[]{longitudeResult, latitudeResult};
        return location;

    }
}
