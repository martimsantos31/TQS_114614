package pt.ua.deti.tqs.meal.controller.dto;

import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.service.WeatherService.WeatherForecast;

import java.time.LocalDate;

public class MealResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate date;
    private RestaurantDto restaurant;
    private WeatherDto weather;

    public MealResponse(Meal meal, WeatherForecast forecast) {
        this.id = meal.getId();
        this.name = meal.getName();
        this.description = meal.getDescription();
        this.date = meal.getDate();
        
        if (meal.getRestaurant() != null) {
            this.restaurant = new RestaurantDto(
                meal.getRestaurant().getId(),
                meal.getRestaurant().getName(),
                meal.getRestaurant().getLocation()
            );
        }
        
        if (forecast != null) {
            this.weather = new WeatherDto(
                forecast.getDescription(), 
                forecast.getMinTemperature(),
                forecast.getMaxTemperature(),
                forecast.getPrecipitationProbability()
            );
        }
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

    public RestaurantDto getRestaurant() {
        return restaurant;
    }

    public WeatherDto getWeather() {
        return weather;
    }

    public static class RestaurantDto {
        private Long id;
        private String name;
        private String location;

        public RestaurantDto(Long id, String name, String location) {
            this.id = id;
            this.name = name;
            this.location = location;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getLocation() {
            return location;
        }
    }

    public static class WeatherDto {
        private String description;
        private double minTemperature;
        private double maxTemperature;
        private String precipitationProbability;

        public WeatherDto(String description, double minTemperature, double maxTemperature, String precipitationProbability) {
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
            return (minTemperature + maxTemperature) / 2;
        }
    }
} 