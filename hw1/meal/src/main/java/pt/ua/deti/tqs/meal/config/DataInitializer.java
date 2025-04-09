package pt.ua.deti.tqs.meal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.repository.MealRepository;
import pt.ua.deti.tqs.meal.repository.RestaurantRepository;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private MealRepository mealRepository;
    
    @Override
    public void run(String... args) {
        logger.info("Initializing sample data");
        
        // Create restaurants
        Restaurant restaurant1 = new Restaurant("Tasca do Manel", "Aveiro, Portugal");
        Restaurant restaurant2 = new Restaurant("Marisqueira Atlântico", "Costa Nova, Portugal");
        Restaurant restaurant3 = new Restaurant("Pizzaria Bella Italia", "Aveiro, Portugal");
        
        restaurant1 = restaurantRepository.save(restaurant1);
        restaurant2 = restaurantRepository.save(restaurant2);
        restaurant3 = restaurantRepository.save(restaurant3);
        
        logger.info("Created {} restaurants", restaurantRepository.count());
        
        // Create meals
        // For restaurant 1
        createMealsForRestaurant(restaurant1);
        
        // For restaurant 2
        createMealsForRestaurant(restaurant2);
        
        // For restaurant 3
        createMealsForRestaurant(restaurant3);
        
        logger.info("Created {} meals", mealRepository.count());
    }
    
    private void createMealsForRestaurant(Restaurant restaurant) {
        LocalDate today = LocalDate.now();
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            
            String suffix = switch (i) {
                case 0 -> " (Today)";
                case 1 -> " (Tomorrow)";
                default -> " (+" + i + " days)";
            };
            
            if (restaurant.getName().contains("Tasca")) {
                createMeal("Francesinha" + suffix, "Traditional Porto sandwich with meat, cheese and spicy sauce", date, restaurant);
                createMeal("Bacalhau à Brás" + suffix, "Codfish with fried potatoes, onions, eggs and olives", date, restaurant);
            } else if (restaurant.getName().contains("Marisqueira")) {
                createMeal("Cataplana de Marisco" + suffix, "Seafood stew with clams, prawns and fish", date, restaurant);
                createMeal("Arroz de Tamboril" + suffix, "Monkfish rice with prawns and peppers", date, restaurant);
            } else if (restaurant.getName().contains("Pizzaria")) {
                createMeal("Pizza Margherita" + suffix, "Classic pizza with tomato sauce, mozzarella and basil", date, restaurant);
                createMeal("Pizza Pepperoni" + suffix, "Pizza with tomato sauce, mozzarella and pepperoni", date, restaurant);
            }
        }
    }
    
    private Meal createMeal(String name, String description, LocalDate date, Restaurant restaurant) {
        Meal meal = new Meal();
        meal.setName(name);
        meal.setDescription(description);
        meal.setDate(date);
        meal.setRestaurant(restaurant);
        return mealRepository.save(meal);
    }
} 