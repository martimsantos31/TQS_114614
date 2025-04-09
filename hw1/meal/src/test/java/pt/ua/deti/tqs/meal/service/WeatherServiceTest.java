package pt.ua.deti.tqs.meal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Spy
    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        // Set up necessary properties using ReflectionTestUtils
        ReflectionTestUtils.setField(weatherService, "apiUrl", "https://test-api.com");
        ReflectionTestUtils.setField(weatherService, "apiKey", "test-key");
    }

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
    void whenGetForecastForSameDateTwice_thenIncreaseHitCounter() {
        // Arrange
        // Disable caching to test our custom implementation
        doReturn(WeatherService.WeatherForecast.builder()
                .description("Sunny")
                .minTemperature(20.0)
                .maxTemperature(30.0)
                .precipitationProbability("10%")
                .build())
                .when(weatherService).generateMockForecast(any());

        // Act
        LocalDate today = LocalDate.now();
        weatherService.getForecastForDate(today);
        weatherService.getForecastForDate(today);

        // Assert
        Map<String, Integer> stats = weatherService.getCacheStats();
        // Since we've mocked the internal method, our real caching should work
        // The first call should be a miss, the second should be a hit
        assertThat(stats.get("misses")).isEqualTo(1);
        assertThat(stats.get("hits")).isEqualTo(1);
    }

    @Test
    void whenGetForecastForMultipleDates_thenCacheWorksCorrectly() {
        // Arrange
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        Set<LocalDate> dates = new HashSet<>();
        dates.add(today);
        dates.add(tomorrow);
        
        // Act - First get forecasts for both dates (should be misses)
        weatherService.getForecastForDates(dates);
        
        // Then get forecast for today again (should be a hit)
        weatherService.getForecastForDate(today);
        
        // Assert
        Map<String, Integer> stats = weatherService.getCacheStats();
        assertThat(stats.get("misses")).isEqualTo(2); // One for each date
        assertThat(stats.get("hits")).isEqualTo(1);   // One for reusing today's date
    }

    @Test
    void testGenerateMockForecast() {
        // Act
        LocalDate date = LocalDate.of(2023, 5, 15); // A fixed date for consistent testing
        WeatherService.WeatherForecast forecast = weatherService.generateMockForecast(date);
        
        // Assert
        assertThat(forecast).isNotNull();
        assertThat(forecast.getDescription()).isNotNull();
        assertThat(forecast.getMinTemperature()).isNotNull();
        assertThat(forecast.getMaxTemperature()).isNotNull();
        assertThat(forecast.getPrecipitationProbability()).isNotNull();
        
        // The mock forecast should use the day of month in its logic
        int dayOfMonth = date.getDayOfMonth();
        assertThat(forecast.getMinTemperature()).isGreaterThanOrEqualTo(15.0); // Base value in the mock
    }

    @Test
    void testWeatherForecastGeneration() {
        // Act
        LocalDate date = LocalDate.of(2023, 5, 15);
        WeatherService.WeatherForecast forecast = weatherService.getForecastForDate(date);
        
        // Assert
        assertThat(forecast).isNotNull();
        assertThat(forecast.getDescription()).isNotNull();
        assertThat(forecast.getMinTemperature()).isNotNull();
        assertThat(forecast.getMaxTemperature()).isNotNull();
        assertThat(forecast.getPrecipitationProbability()).isNotNull();
    }
} 