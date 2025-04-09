package pt.ua.deti.tqs.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.meal.service.WeatherService;
import pt.ua.deti.tqs.meal.service.WeatherService.WeatherForecast;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/weather")
@CrossOrigin(origins = "http://localhost:5473")
public class WeatherController {
    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);
    
    @Autowired
    private WeatherService weatherService;
    
    /**
     * Get weather forecast for a specific date
     * @param date The date to get forecast for
     * @return Weather forecast data
     */
    @GetMapping("/forecast")
    public ResponseEntity<?> getWeatherForecast(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        logger.info("Request to get weather forecast for date: {}", date);
        
        WeatherForecast forecast = weatherService.getForecastForDate(date);
        
        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("description", forecast.getDescription());
        response.put("minTemperature", forecast.getMinTemperature());
        response.put("maxTemperature", forecast.getMaxTemperature());
        response.put("precipitationProbability", forecast.getPrecipitationProbability());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get weather forecast for multiple days starting from today
     * @param days Number of days to get forecast for
     * @return Map of dates to weather forecasts
     */
    @GetMapping("/forecast/days")
    public ResponseEntity<?> getMultiDayForecast(
            @RequestParam(defaultValue = "5") int days
    ) {
        logger.info("Request to get weather forecast for the next {} days", days);
        
        if (days <= 0 || days > 10) {
            return ResponseEntity.badRequest().body("Days must be between 1 and 10");
        }
        
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> forecasts = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        for (int i = 0; i < days; i++) {
            LocalDate date = today.plusDays(i);
            WeatherForecast forecast = weatherService.getForecastForDate(date);
            
            Map<String, Object> forecastData = new HashMap<>();
            forecastData.put("description", forecast.getDescription());
            forecastData.put("minTemperature", forecast.getMinTemperature());
            forecastData.put("maxTemperature", forecast.getMaxTemperature());
            forecastData.put("precipitationProbability", forecast.getPrecipitationProbability());
            
            forecasts.put(date.toString(), forecastData);
        }
        
        response.put("forecasts", forecasts);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get weather cache statistics
     * @return Cache statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getWeatherStats() {
        logger.info("Request to get weather cache statistics");
        return ResponseEntity.ok(weatherService.getCacheStats());
    }
    
    /**
     * Test endpoint to verify cache TTL
     * @return Test results
     */
    @GetMapping("/test-cache")
    public ResponseEntity<?> testCache() {
        logger.info("Testing weather cache with TTL: {}ms", weatherService.getCacheStats().get("cacheTtlMs"));
        
        LocalDate today = LocalDate.now();
        Map<String, Object> response = new HashMap<>();
        
        // First request - should be a cache miss
        logger.info("First request - should be a cache miss");
        WeatherForecast forecast1 = weatherService.getForecastForDate(today);
        
        try {
            // Wait for 100ms (less than TTL)
            Thread.sleep(100);
            
            // Second request - should be a cache hit
            logger.info("Second request after 100ms - should be a cache hit");
            WeatherForecast forecast2 = weatherService.getForecastForDate(today);
            
            // Wait for 150ms (total 250ms, more than TTL)
            Thread.sleep(150);
            
            // Third request - should be a cache miss due to expiration
            logger.info("Third request after 250ms - should be a cache miss due to expiration");
            WeatherForecast forecast3 = weatherService.getForecastForDate(today);
            
            response.put("result", "Test completed");
            response.put("cacheStats", weatherService.getCacheStats());
            
            return ResponseEntity.ok(response);
        } catch (InterruptedException e) {
            logger.error("Test interrupted", e);
            return ResponseEntity.internalServerError().body("Test interrupted");
        }
    }
} 