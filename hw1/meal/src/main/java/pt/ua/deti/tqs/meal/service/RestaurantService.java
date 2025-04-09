package pt.ua.deti.tqs.meal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.repository.RestaurantRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    /**
     * Get all restaurants
     * @return List of all restaurants
     */
    public List<Restaurant> getAllRestaurants() {
        logger.info("Fetching all restaurants");
        return restaurantRepository.findAll();
    }
    
    /**
     * Get a restaurant by ID
     * @param id The restaurant ID
     * @return Optional containing the restaurant if found
     */
    public Optional<Restaurant> getRestaurantById(Long id) {
        logger.info("Finding restaurant with ID: {}", id);
        return restaurantRepository.findById(id);
    }
} 