package pt.ua.deti.tqs.meal.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Spy
    @InjectMocks
    private WeatherService weatherService;

    @Test
    void whenGetForecastForDate_thenIncreaseMissCounter() {
        // Act
        weatherService.getForecastForDate(LocalDate.now());

        // Assert
        Map<String, Integer> stats = weatherService.getCacheStats();
        assertThat(stats.get("misses")).isEqualTo(1);
        assertThat(stats.get("hits")).isEqualTo(0);
    }

    @Test
    void whenGetForecastForMultipleDates_thenCacheWorksCorrectly() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        Set<LocalDate> dates = new HashSet<>();
        dates.add(today);
        dates.add(tomorrow);
        
        // Mock internal forecast generation to isolate cache behavior
        doReturn(WeatherService.WeatherForecast.builder()
                .description("Sunny")
                .minTemperature(20.0)
                .maxTemperature(30.0)
                .precipitationProbability("10%")
                .build())
                .when(weatherService).generateMockForecast(any());
        
        // Act - Get forecasts for both dates
        weatherService.getForecastForDates(dates);
        
        // Verify the correct number of calls to getForecastForDate
        verify(weatherService, times(2)).getForecastForDate(any(LocalDate.class));
        
        // Verify the expected number of calls to generateMockForecast
        // If each date causes a cache miss, we should see 2 calls
        verify(weatherService, times(2)).generateMockForecast(any(LocalDate.class));
    }

    @Test
    void testWeatherForecastGeneration() {
        // Act
        LocalDate date = LocalDate.of(2023, 5, 15);
        WeatherService.WeatherForecast forecast = weatherService.generateMockForecast(date);
        
        // Assert
        assertThat(forecast).isNotNull();
        assertThat(forecast.getDescription()).isNotNull();
        assertThat(forecast.getMinTemperature()).isNotNull();
        assertThat(forecast.getMaxTemperature()).isNotNull();
        assertThat(forecast.getPrecipitationProbability()).isNotNull();
    }
} 