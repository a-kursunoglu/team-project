package fuze.usecases.viewweather;

import fuze.entity.weather.WeatherDay;
import fuze.framework.weatherapi.WeatherFetcher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ViewWeatherTest {

    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    private void startCaptureStdout() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    private String getStdout() {
        return outContent.toString();
    }

    @AfterEach
    void restoreStdout() {
        System.setOut(originalOut);
    }

    @Test
    void testDisplayToday_noData_printsNoDataMessage() {
        startCaptureStdout();

        try (MockedConstruction<WeatherFetcher> mocked =
                     mockConstruction(WeatherFetcher.class,
                             (mock, context) ->
                                     when(mock.getWeatherByDate(anyString())).thenReturn(null))) {

            new ViewWeather().displayToday();
        }

        assertTrue(getStdout().contains("No weather data available for today."));
    }

    @Test
    void testDisplayToday_withData_printsWeatherInfo() {
        startCaptureStdout();

        WeatherDay fakeDay = mock(WeatherDay.class);
        when(fakeDay.getDate()).thenReturn("2025-11-27");
        when(fakeDay.getWeather()).thenReturn("Sunny");
        when(fakeDay.getTemperatureHigh()).thenReturn(25.0);
        when(fakeDay.getTemperatureLow()).thenReturn(15.0);
        when(fakeDay.getLocation()).thenReturn(new double[]{-79.38, 43.65});

        try (MockedConstruction<WeatherFetcher> mocked =
                     mockConstruction(WeatherFetcher.class,
                             (mock, context) ->
                                     when(mock.getWeatherByDate(anyString())).thenReturn(fakeDay))) {

            new ViewWeather().displayToday();
        }

        String o = getStdout();
        assertTrue(o.contains("======== Today's Weather ========"));
        assertTrue(o.contains("Date: 2025-11-27"));
        assertTrue(o.contains("Weather: Sunny"));
        assertTrue(o.contains("High: 25.0°C"));
        assertTrue(o.contains("Low:  15.0°C"));
        assertTrue(o.contains("Location: lon=-79.38, lat=43.65"));
    }

    @Test
    void testDisplayTodayWithCoords_noData_printsNoDataMessage() {
        startCaptureStdout();

        try (MockedConstruction<WeatherFetcher> mocked =
                     mockConstruction(WeatherFetcher.class,
                             (mock, context) ->
                                     when(mock.getWeatherByDate(anyString())).thenReturn(null))) {

            new ViewWeather().displayToday(43.65, -79.38);
        }

        assertTrue(getStdout().contains("No weather data available for this location."));
    }

    @Test
    void testDisplayTodayWithCoords_withData_printsWeatherInfo() {
        startCaptureStdout();

        WeatherDay fakeDay = mock(WeatherDay.class);
        when(fakeDay.getDate()).thenReturn("2025-11-27");
        when(fakeDay.getWeather()).thenReturn("Cloudy");
        when(fakeDay.getTemperatureHigh()).thenReturn(10.0);
        when(fakeDay.getTemperatureLow()).thenReturn(2.0);
        when(fakeDay.getLocation()).thenReturn(new double[]{120.0, 30.0});

        try (MockedConstruction<WeatherFetcher> mocked =
                     mockConstruction(WeatherFetcher.class,
                             (mock, context) ->
                                     when(mock.getWeatherByDate(anyString())).thenReturn(fakeDay))) {

            new ViewWeather().displayToday(30.0, 120.0);
        }

        String o = getStdout();
        assertTrue(o.contains("======== Today's Weather ========"));
        assertTrue(o.contains("Date: 2025-11-27"));
        assertTrue(o.contains("Weather: Cloudy"));
        assertTrue(o.contains("High: 10.0°C"));
        assertTrue(o.contains("Low:  2.0°C"));
        assertTrue(o.contains("Location: lon=120.0, lat=30.0"));
    }
}
