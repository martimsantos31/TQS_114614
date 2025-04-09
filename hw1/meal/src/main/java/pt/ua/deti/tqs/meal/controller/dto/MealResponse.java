package pt.ua.deti.tqs.meal.controller.dto;

import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.service.WeatherService.WeatherForecast;

import java.time.LocalDate;

public class MealResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private Long restaurantId;
    private String restaurantName;
    private String restaurantDescription;
    private WeatherForecast weather;

    public MealResponse(Meal meal, WeatherForecast weather) {
        this.id = meal.getId();
        this.name = meal.getName();
        this.description = meal.getDescription();
        this.date = meal.getAvailableDate();
        
        if (meal.getRestaurant() != null) {
            this.restaurantId = meal.getRestaurant().getId();
            this.restaurantName = meal.getRestaurant().getName();
            this.restaurantDescription = meal.getRestaurant().getDescription();
        }
        
        this.weather = weather;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantDescription() {
        return restaurantDescription;
    }

    public WeatherForecast getWeather() {
        return weather;
    }
} 