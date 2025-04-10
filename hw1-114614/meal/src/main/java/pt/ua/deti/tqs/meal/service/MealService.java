package pt.ua.deti.tqs.meal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.repository.MealRepository;
import pt.ua.deti.tqs.meal.repository.RestaurantRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MealService {
    private static final Logger logger = LoggerFactory.getLogger(MealService.class);

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

   
    public List<Meal> getMealsForRestaurant(Long restaurantId, int days) {
        logger.info("Finding meals for restaurant ID {} for the next {} days", restaurantId, days);
        
        Optional<Restaurant> restaurant = restaurantRepository.findById(restaurantId);
        
        if (restaurant.isEmpty()) {
            logger.warn("Restaurant with ID {} not found", restaurantId);
            return new ArrayList<>();
        }
        
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days - 1);
        
        List<Meal> allMeals = mealRepository.findAll();
        
        return allMeals.stream()
                .filter(meal -> meal.getRestaurant() != null && 
                               meal.getRestaurant().getId().equals(restaurantId))
                .filter(meal -> {
                    LocalDate mealDate = meal.getAvailableDate();
                    return mealDate != null && 
                           (mealDate.isEqual(today) || mealDate.isAfter(today)) && 
                           (mealDate.isEqual(endDate) || mealDate.isBefore(endDate));
                })
                .collect(Collectors.toList());
    }
    

    public Optional<Meal> getMealById(Long mealId) {
        logger.info("Finding meal with ID {}", mealId);
        return mealRepository.findById(mealId);
    }
} 