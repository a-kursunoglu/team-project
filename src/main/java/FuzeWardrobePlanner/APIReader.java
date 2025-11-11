package FuzeWardrobePlanner;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONObject;
/**
 * A class to handle all interactions with the API, contains only one method
 * which returns a JSONObject.
 */
public class APIReader {
    /**
     * Fetch the list of sub breeds for the given breed.
     * @param longitude is a double representing the geographical longitude
     * @param latitude is a double representing the geographical latitude
     * @param startDate is a string for the starting forecast day in "YYYY-MM-DD"
     * @return JSONObject with all the returns from open-meteo, this includes a
     * "time" string format for each day
     * "temperature_2m_max" showing the maximum temperature of each day
     * "temperature_2m_min" showing the minimum temperature of each day
     * "weather_code" uses WMO code which I had to look up and I suggest you do too
     * @throws IOException if incorrect inputs for open-meteo
     * @throws InterruptedException if interrupted request
     */

    public JSONObject readAPI(double longitude, double latitude, String startDate) throws IOException, InterruptedException {
        String endDate;
        try {
            // No simple way to get end date and didn't want to do one request at
            // a time, so wrote this work around.
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(startDate));
            c.add(Calendar.DATE, 7);
            endDate = sdf.format(c.getTime());
        }
        catch (ParseException e) {
            // If failed to find a valid endDate, it will default to the start date,
            // meaning a returned JSONObject with the weather data for just one day.
            endDate = startDate;
        }
        String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=" +
                        "%f&daily=temperature_2m_max&daily=weather_code,temperature_2m_min" +
                        "&timezone=auto&start_date=%s&end_date=%s",
                latitude, longitude, startDate, endDate
        );
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Same function but allows you to tweak number of days which is usually 7, but
    // this allows for more through the week
    public JSONObject readAPI(double longitude, double latitude, String startDate, int numDays) throws IOException, InterruptedException {
        String endDate;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(sdf.parse(startDate));
            c.add(Calendar.DATE, numDays);
            endDate = sdf.format(c.getTime());
        }
        catch (ParseException e) {
            endDate = startDate;
        }
        String url = String.format(
                "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=" +
                        "%f&daily=temperature_2m_max&daily=weather_code,temperature_2m_min" +
                        "&timezone=auto&start_date=%s&end_date=%s",
                latitude, longitude, startDate, endDate
        );
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return new JSONObject(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

