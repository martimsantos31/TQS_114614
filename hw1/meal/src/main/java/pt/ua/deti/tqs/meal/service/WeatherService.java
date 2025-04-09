package pt.ua.deti.tqs.meal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeatherService {
    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    
    @Value("${weather.api.url:https://api.ipma.pt/open-data/forecast/meteorology/cities/daily}")
    private String weatherApiBaseUrl;
    
    @Value("${weather.city.code:1010500}")
    private String cityCode;
    
    // Cache structure: Date -> (WeatherForecast, Timestamp)
    private final Map<LocalDate, CacheEntry> forecastCache = new ConcurrentHashMap<>();
    
    // TTL for cache entries in milliseconds (default: 1 hour)
    @Value("${weather.cache.ttl:3600000}")
    private long cacheTtlMs;
    
    // Class to represent weather forecast
    public static class WeatherForecast {
        private String description;
        private double minTemperature;
        private double maxTemperature;
        private String precipitationProbability;
        
        public WeatherForecast(String description, double minTemperature, double maxTemperature, String precipitationProbability) {
            this.description = description;
            this.minTemperature = minTemperature;
            this.maxTemperature = maxTemperature;
            this.precipitationProbability = precipitationProbability;
        }
        
        public String getDescription() {
            return description;
        }
        
        public double getMinTemperature() {
            return minTemperature;
        }
        
        public double getMaxTemperature() {
            return maxTemperature;
        }
        
        public String getPrecipitationProbability() {
            return precipitationProbability;
        }
        
        // For backward compatibility
        public double getTemperature() {
            // Return the average of min and max for backward compatibility
            return (minTemperature + maxTemperature) / 2;
        }
    }
    
    // Cache entry class
    private static class CacheEntry {
        private final WeatherForecast forecast;
        private final long timestamp;
        
        public CacheEntry(WeatherForecast forecast) {
            this.forecast = forecast;
            this.timestamp = System.currentTimeMillis();
        }
        
        public WeatherForecast getForecast() {
            return forecast;
        }
        
        public boolean isExpired(long ttlMs) {
            return System.currentTimeMillis() - timestamp > ttlMs;
        }
    }
    
    // IPMA API response classes
    private static class IpmaResponse {
        private String owner;
        private String country;
        private List<IpmaForecast> data;
        
        public List<IpmaForecast> getData() {
            return data;
        }
        
        public void setData(List<IpmaForecast> data) {
            this.data = data;
        }
        
        public String getOwner() {
            return owner;
        }
        
        public void setOwner(String owner) {
            this.owner = owner;
        }
        
        public String getCountry() {
            return country;
        }
        
        public void setCountry(String country) {
            this.country = country;
        }
    }
    
    private static class IpmaForecast {
        private String precipitaProb;
        private String tMin;
        private String tMax;
        private String predWindDir;
        private String idWeatherType;
        private String classWindSpeed;
        private String longitude;
        private String latitude;
        private String forecastDate;
        
        public String getPrecipitaProb() {
            return precipitaProb;
        }
        
        public void setPrecipitaProb(String precipitaProb) {
            this.precipitaProb = precipitaProb;
        }
        
        public String gettMin() {
            return tMin;
        }
        
        public void settMin(String tMin) {
            this.tMin = tMin;
        }
        
        public String gettMax() {
            return tMax;
        }
        
        public void settMax(String tMax) {
            this.tMax = tMax;
        }
        
        public String getPredWindDir() {
            return predWindDir;
        }
        
        public void setPredWindDir(String predWindDir) {
            this.predWindDir = predWindDir;
        }
        
        public String getIdWeatherType() {
            return idWeatherType;
        }
        
        public void setIdWeatherType(String idWeatherType) {
            this.idWeatherType = idWeatherType;
        }
        
        public String getClassWindSpeed() {
            return classWindSpeed;
        }
        
        public void setClassWindSpeed(String classWindSpeed) {
            this.classWindSpeed = classWindSpeed;
        }
        
        public String getLongitude() {
            return longitude;
        }
        
        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
        
        public String getLatitude() {
            return latitude;
        }
        
        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }
        
        public String getForecastDate() {
            return forecastDate;
        }
        
        public void setForecastDate(String forecastDate) {
            this.forecastDate = forecastDate;
        }
    }
    
    public Map<LocalDate, WeatherForecast> getForecastForDates(Set<LocalDate> dates) {
        Map<LocalDate, WeatherForecast> result = new HashMap<>();
        
        for (LocalDate date : dates) {
            result.put(date, getForecastForDate(date));
        }
        
        return result;
    }
    
    public WeatherForecast getForecastForDate(LocalDate date) {
        // Check if we have a valid cache entry
        CacheEntry entry = forecastCache.get(date);
        if (entry != null) {
            long currentTime = System.currentTimeMillis();
            long cacheTime = entry.timestamp;
            long ageMs = currentTime - cacheTime;
            
            logger.info("Cache entry for date {} found. Age: {}ms, TTL: {}ms", date, ageMs, cacheTtlMs);
            
            if (!entry.isExpired(cacheTtlMs)) {
                logger.info("Cache hit for date: {}", date);
                return entry.getForecast();
            } else {
                logger.info("Cache expired for date: {} (age: {}ms > TTL: {}ms)", date, ageMs, cacheTtlMs);
            }
        } else {
            logger.info("No cache entry found for date: {}", date);
        }
        
        // If not in cache or expired, fetch from external API
        logger.info("Cache miss for date: {}, fetching from API", date);
        WeatherForecast forecast = fetchForecastFromApi(date);
        
        // Cache the result
        forecastCache.put(date, new CacheEntry(forecast));
        logger.info("Added new cache entry for date: {}", date);
        
        return forecast;
    }
    
    private WeatherForecast fetchForecastFromApi(LocalDate date) {
        // For demo purposes, if date is too far in the future, return dummy data
        long daysAhead = ChronoUnit.DAYS.between(LocalDate.now(), date);
        if (daysAhead > 5) {
            logger.info("Date {} is too far ahead, returning prediction", date);
            return new WeatherForecast("Previsão: Parcialmente nublado", 18.0, 26.0, "20%");
        }
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = weatherApiBaseUrl + "/" + cityCode + ".json";
            
            logger.info("Fetching weather data from IPMA API: {}", url);
            ResponseEntity<IpmaResponse> response = restTemplate.getForEntity(url, IpmaResponse.class);
            
            if (response.getBody() != null && response.getBody().getData() != null) {
                // Convert the date to the format used by IPMA (YYYY-MM-DD)
                String targetDateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
                
                // Find the forecast for the requested date
                for (IpmaForecast forecast : response.getBody().getData()) {
                    if (forecast.getForecastDate().equals(targetDateStr)) {
                        // Parse min and max temperatures
                        double tMin = parseTemperature(forecast.gettMin());
                        double tMax = parseTemperature(forecast.gettMax());
                        
                        // Get weather description based on weather type ID
                        String description = mapWeatherTypeToDescription(forecast.getIdWeatherType());
                        
                        logger.info("Successfully fetched weather data for: {}", date);
                        return new WeatherForecast(
                            description, 
                            tMin,
                            tMax,
                            forecast.getPrecipitaProb() + "%"
                        );
                    }
                }
                
                // If we didn't find a forecast for the specific date in the response
                logger.warn("No forecast found for date: {} in IPMA response", date);
            } else {
                logger.warn("Empty or invalid response from IPMA API");
            }
            
            // Fallback to mock data if we couldn't find a forecast in the response
            return generateMockForecast(date);
            
        } catch (Exception e) {
            logger.error("Error fetching weather data from IPMA: {}", e.getMessage());
            return new WeatherForecast("Dados meteorológicos indisponíveis", 0.0, 0.0, "N/A");
        }
    }
    
    private double parseTemperature(String tempStr) {
        try {
            return Double.parseDouble(tempStr);
        } catch (NumberFormatException e) {
            logger.warn("Error parsing temperature value: {}", e.getMessage());
            return 0.0;
        }
    }
    
    private String mapWeatherTypeToDescription(String weatherTypeId) {
        // IPMA weather type mapping
        // This is a simplified mapping - in a real application, you would use the full list from IPMA
        switch (weatherTypeId) {
            case "1":
                return "Céu limpo";
            case "2":
            case "3":
                return "Céu pouco nublado";
            case "4":
            case "5":
                return "Céu nublado";
            case "6":
            case "7":
                return "Céu muito nublado";
            case "8":
            case "9":
                return "Chuva fraca";
            case "10":
            case "11":
                return "Chuva moderada";
            case "12":
            case "13":
                return "Chuva forte";
            case "14":
            case "15":
                return "Aguaceiros";
            case "16":
            case "17":
                return "Trovoada";
            case "18":
                return "Nevoeiro";
            case "19":
                return "Neblina";
            default:
                return "Condições meteorológicas desconhecidas";
        }
    }
    
    private WeatherForecast generateMockForecast(LocalDate date) {
        // Mock different weather conditions based on the day of the month
        int day = date.getDayOfMonth();
        if (day % 3 == 0) {
            return new WeatherForecast("Céu limpo", 20.0, 30.0, "5%");
        } else if (day % 3 == 1) {
            return new WeatherForecast("Céu nublado", 15.0, 21.0, "20%");
        } else {
            return new WeatherForecast("Chuva fraca", 12.0, 18.0, "70%");
        }
    }
    
    // For monitoring purposes - cache stats
    public Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        int validEntries = 0;
        int expiredEntries = 0;
        
        for (Map.Entry<LocalDate, CacheEntry> entry : forecastCache.entrySet()) {
            if (entry.getValue().isExpired(cacheTtlMs)) {
                expiredEntries++;
            } else {
                validEntries++;
            }
        }
        
        stats.put("validEntries", validEntries);
        stats.put("expiredEntries", expiredEntries);
        stats.put("totalEntries", forecastCache.size());
        stats.put("cacheTtlMs", (int) cacheTtlMs);
        
        logger.info("Cache stats: {}", stats);
        return stats;
    }
} 