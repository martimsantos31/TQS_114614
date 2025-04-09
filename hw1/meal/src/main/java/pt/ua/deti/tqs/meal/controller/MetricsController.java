package pt.ua.deti.tqs.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ua.deti.tqs.meal.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

@RestController
@RequestMapping("/api/v1/metrics")
@CrossOrigin(origins = "http://localhost:5473")
public class MetricsController {
    private static final Logger logger = LoggerFactory.getLogger(MetricsController.class);
    
    @Autowired
    private WeatherService weatherService;
    
    @Autowired
    private CacheManager cacheManager;
    
    @GetMapping("/weather-cache")
    public ResponseEntity<Map<String, Integer>> getWeatherCacheStats() {
        return ResponseEntity.ok(weatherService.getCacheStats());
    }
    
    /**
     * Get cache statistics for monitoring
     * @return JSON object with cache statistics
     */
    @GetMapping("/cache")
    public ResponseEntity<Map<String, Object>> getCacheStatistics() {
        logger.info("Request for cache statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        // Get all cache names
        cacheManager.getCacheNames().forEach(cacheName -> {
            try {
                // Get the native Caffeine cache
                Cache<Object, Object> nativeCache = (Cache<Object, Object>) cacheManager
                        .getCache(cacheName)
                        .getNativeCache();
                
                // Get cache stats
                CacheStats cacheStats = nativeCache.stats();
                
                // Get entries and size
                ConcurrentMap<Object, Object> entries = nativeCache.asMap();
                
                Map<String, Object> cacheInfo = new HashMap<>();
                cacheInfo.put("size", entries.size());
                cacheInfo.put("hits", cacheStats.hitCount());
                cacheInfo.put("misses", cacheStats.missCount());
                cacheInfo.put("hitRate", cacheStats.hitRate());
                cacheInfo.put("evictions", cacheStats.evictionCount());
                
                stats.put(cacheName, cacheInfo);
            } catch (Exception e) {
                logger.error("Error getting stats for cache {}: {}", cacheName, e.getMessage());
                stats.put(cacheName, "Error: " + e.getMessage());
            }
        });
        
        return ResponseEntity.ok(stats);
    }
} 