package pt.ua.deti.tqs.meal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    @Value("${api.weather.url:https://api.openweathermap.org/data/2.5/weather}")
    private String apiUrl;
    
    @Value("${api.weather.key:defaultKey}")
    private String apiKey;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // Simple counters for cache statistics
    private final AtomicInteger hits = new AtomicInteger(0);
    private final AtomicInteger misses = new AtomicInteger(0);
    
    /**
     * Get weather forecast for a specific date
     * @param date The date to get forecast for
     * @return Weather forecast
     */
    @Cacheable("weatherData")
    public WeatherForecast getForecastForDate(LocalDate date) {
        logger.info("Fetching weather forecast for date: {}", date);
        misses.incrementAndGet();
        
        // In a real application, we would call an external API
        // For this example, we'll just return mock data
        return generateMockForecast(date);
    }
    
    /**
     * Get weather forecasts for multiple dates
     * @param dates Set of dates to get forecasts for
     * @return Map of date to forecast
     */
    public Map<LocalDate, WeatherForecast> getForecastForDates(Set<LocalDate> dates) {
        logger.info("Fetching weather forecasts for {} dates", dates.size());
        
        Map<LocalDate, WeatherForecast> forecasts = new HashMap<>();
        
        for (LocalDate date : dates) {
            forecasts.put(date, getForecastForDate(date));
        }
        
        return forecasts;
    }
    
    /**
     * Generate mock weather forecast
     * @param date Date to generate forecast for
     * @return Mock forecast
     */
    public WeatherForecast generateMockForecast(LocalDate date) {
        // Mock data - in a real application this would come from an API
        int dayOfMonth = date.getDayOfMonth();
        
        String[] descriptions = {
            "Sunny", "Partly cloudy", "Cloudy", "Light rain", 
            "Thunderstorm", "Clear sky", "Foggy", "Windy"
        };
        
        String description = descriptions[dayOfMonth % descriptions.length];
        double minTemp = 15 + (dayOfMonth % 10);
        double maxTemp = minTemp + 5 + (dayOfMonth % 5);
        int precipitationChance = dayOfMonth % 100;
        
        return new WeatherForecast(
            description, 
            minTemp, 
            maxTemp, 
            precipitationChance + "%"
        );
    }
    
    /**
     * Get cache statistics
     * @return Map with hit and miss counts
     */
    public Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("hits", hits.get());
        stats.put("misses", misses.get());
        return stats;
    }
    
    /**
     * Get the number of cache hits
     * @return Number of cache hits
     */
    public int getHits() {
        return hits.get();
    }
    
    /**
     * Get the number of cache misses
     * @return Number of cache misses
     */
    public int getMisses() {
        return misses.get();
    }
    
    /**
     * Weather forecast data class
     */
    public static class WeatherForecast {
        private String description;
        private Double minTemperature;
        private Double maxTemperature;
        private String precipitationProbability;
        
        public WeatherForecast() {
            // Default constructor for builder
        }
        
        public WeatherForecast(String description, double minTemperature, 
                              double maxTemperature, String precipitationProbability) {
            this.description = description;
            this.minTemperature = minTemperature;
            this.maxTemperature = maxTemperature;
            this.precipitationProbability = precipitationProbability;
        }
        
        public String getDescription() {
            return description;
        }
        
        public Double getMinTemperature() {
            return minTemperature;
        }
        
        public Double getMaxTemperature() {
            return maxTemperature;
        }
        
        public String getPrecipitationProbability() {
            return precipitationProbability;
        }
        
        // For backward compatibility
        public double getTemperature() {
            return (minTemperature + maxTemperature) / 2;
        }
        
        // Builder method
        public static Builder builder() {
            return new Builder();
        }
        
        // Builder class
        public static class Builder {
            private final WeatherForecast forecast = new WeatherForecast();
            
            public Builder description(String description) {
                forecast.description = description;
                return this;
            }
            
            public Builder minTemperature(Double minTemperature) {
                forecast.minTemperature = minTemperature;
                return this;
            }
            
            public Builder maxTemperature(Double maxTemperature) {
                forecast.maxTemperature = maxTemperature;
                return this;
            }
            
            public Builder precipitationProbability(String precipitationProbability) {
                forecast.precipitationProbability = precipitationProbability;
                return this;
            }
            
            public WeatherForecast build() {
                return forecast;
            }
        }
    }
} 