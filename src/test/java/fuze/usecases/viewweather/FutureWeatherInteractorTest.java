package fuze.usecases.viewweather;

import fuze.entity.location.LocationStringToCoordinate;
import fuze.entity.weather.WeatherDay;
import fuze.framework.weatherapi.WeatherFetcher;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class FutureWeatherInteractorTest {

    @Test
    void testEmptyDates() {
        try (
                MockedConstruction<LocationStringToCoordinate> locMock =
                        Mockito.mockConstruction(LocationStringToCoordinate.class,
                                (m, c) -> {
                                    when(m.getLongitude()).thenReturn(1.0);
                                    when(m.getLatitude()).thenReturn(1.0);
                                });

                MockedConstruction<WeatherFetcher> fetchMock =
                        Mockito.mockConstruction(WeatherFetcher.class,
                                (m, c) -> when(m.getForecastDates()).thenReturn(new JSONArray()))
        ) {
            FutureWeatherInteractor interactor = new FutureWeatherInteractor();
            List<WeatherDay> result = interactor.getTwoweeksWeather("");
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Test
    void testNormalDates() {
        JSONArray arr = new JSONArray();
        arr.put("2025-01-01");
        arr.put("2025-01-02");
        arr.put("2025-01-03");

        WeatherDay d1 = Mockito.mock(WeatherDay.class);
        WeatherDay d2 = Mockito.mock(WeatherDay.class);

        try (
                MockedConstruction<LocationStringToCoordinate> locMock =
                        Mockito.mockConstruction(LocationStringToCoordinate.class,
                                (m, c) -> {
                                    when(m.getLongitude()).thenReturn(2.0);
                                    when(m.getLatitude()).thenReturn(3.0);
                                });

                MockedConstruction<WeatherFetcher> fetchMock =
                        Mockito.mockConstruction(WeatherFetcher.class,
                                (m, c) -> {
                                    when(m.getForecastDates()).thenReturn(arr);
                                    when(m.getWeatherByDate("2025-01-01")).thenReturn(d1);
                                    when(m.getWeatherByDate("2025-01-02")).thenReturn(d2);
                                    when(m.getWeatherByDate("2025-01-03")).thenReturn(null);
                                })
        ) {
            FutureWeatherInteractor interactor = new FutureWeatherInteractor();
            List<WeatherDay> result = interactor.getTwoweeksWeather("Paris");
            assertEquals(2, result.size());
            assertSame(d1, result.get(0));
            assertSame(d2, result.get(1));
        }
    }
}
