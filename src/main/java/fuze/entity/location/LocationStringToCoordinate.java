package fuze.entity.location;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
        List<String> cities = new ArrayList<>();
        try (BufferedReader br = openCityReader()) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length > 1) {
                    cities.add(values[1]);
                }
            }
        } catch (IOException e) {
            return new String[]{"Toronto Canada", "New York", "Vancouver"};
        }
        return cities.toArray(new String[0]);
    }


    private double[] stringToCoord(String city){
        double longitudeResult = -79.3733;
        double latitudeResult = 43.7417;
        try (BufferedReader br = openCityReader()) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                if (values.length > 3 && values[0].equals(city)) {
                    longitudeResult = Double.parseDouble(values[3]);
                    latitudeResult = Double.parseDouble(values[2]);
                    break;
                }
            }
        } catch (IOException e) {
            return new double[]{longitudeResult, latitudeResult};
        }
        return new double[]{longitudeResult, latitudeResult};

    }

    /**
     * Opens a reader for the city CSV regardless of where the app is launched
     * from. Tries the project path first, then falls back to the classpath.
     */
    private BufferedReader openCityReader() throws IOException {
        Path projectPath = Paths.get("src", "main", "java", "fuze", "entity",
                "location", "city_coordinates", "CitiesLongLat.csv");
        if (Files.exists(projectPath)) {
            return Files.newBufferedReader(projectPath, StandardCharsets.UTF_8);
        }
        InputStream stream = LocationStringToCoordinate.class
                .getClassLoader()
                .getResourceAsStream("fuze/entity/location/city_coordinates/CitiesLongLat.csv");
        if (stream != null) {
            return new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        }
        throw new FileNotFoundException("CitiesLongLat.csv not found");
    }
}
