package pt.ua.deti.tqs.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ua.deti.tqs.meal.controller.dto.MealResponse;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.service.MealService;
import pt.ua.deti.tqs.meal.service.WeatherService;
import pt.ua.deti.tqs.meal.service.WeatherService.WeatherForecast;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/meals")
@CrossOrigin(origins = "http://localhost:5173")
public class MealController {
    private static final Logger logger = LoggerFactory.getLogger(MealController.class);
    
    @Autowired
    private MealService mealService;

    @Autowired
    private WeatherService weatherService;

    @GetMapping
    public ResponseEntity<?> getMeals(
            @RequestParam Long restaurantId,
            @RequestParam(required = false, defaultValue = "7") int days
    ) {
        logger.info("Request to get meals for restaurant ID {} for {} days", restaurantId, days);
        
        List<Meal> meals = mealService.getMealsForRestaurant(restaurantId, days);
        
        Map<LocalDate, WeatherForecast> forecastMap = weatherService.getForecastForDates(
                meals.stream()
                    .map(Meal::getAvailableDate)
                    .collect(Collectors.toSet())
        );

    
        List<MealResponse> response = meals.stream()
                .map(meal -> new MealResponse(meal, forecastMap.get(meal.getAvailableDate())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}
