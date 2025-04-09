package pt.ua.deti.tqs.meal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ua.deti.tqs.meal.service.WeatherService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/metrics")
@CrossOrigin(origins = "http://localhost:5473")
public class MetricsController {
    
    @Autowired
    private WeatherService weatherService;
    
    @GetMapping("/weather-cache")
    public ResponseEntity<Map<String, Integer>> getWeatherCacheStats() {
        return ResponseEntity.ok(weatherService.getCacheStats());
    }
} 