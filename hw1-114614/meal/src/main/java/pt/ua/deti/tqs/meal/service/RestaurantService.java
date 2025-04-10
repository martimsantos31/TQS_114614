package pt.ua.deti.tqs.meal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.exception.ResourceNotFoundException;
import pt.ua.deti.tqs.meal.repository.RestaurantRepository;

import java.util.List;

@Service
public class RestaurantService {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantService.class);
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    /**
     * Get all restaurants
     * @return List of all restaurants
     */
    @Cacheable("restaurantServiceFindAll")
    public List<Restaurant> getAllRestaurants() {
        logger.info("Fetching all restaurants");
        return restaurantRepository.findAll();
    }
    
    /**
     * Get a restaurant by ID
     * @param id The restaurant ID
     * @return Restaurant if found
     * @throws ResourceNotFoundException if not found
     */
    @Cacheable(value = "restaurantServiceFindById", key = "#id")
    public Restaurant getRestaurantById(Long id) {
        logger.info("Finding restaurant with ID: {}", id);
        return restaurantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }
    
    /**
     * Create a new restaurant
     * @param restaurant The restaurant to create
     * @return The created restaurant
     */
    @CacheEvict(value = {"restaurantServiceFindAll", "restaurantServiceFindById"}, allEntries = true)
    public Restaurant createRestaurant(Restaurant restaurant) {
        logger.info("Creating new restaurant: {}", restaurant.getName());
        return restaurantRepository.save(restaurant);
    }
    
    /**
     * Update an existing restaurant
     * @param id The restaurant ID to update
     * @param restaurantDetails The updated restaurant details
     * @return The updated restaurant
     * @throws ResourceNotFoundException if not found
     */
    @CacheEvict(value = {"restaurantServiceFindAll", "restaurantServiceFindById"}, allEntries = true)
    public Restaurant updateRestaurant(Long id, Restaurant restaurantDetails) {
        logger.info("Updating restaurant with ID: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        
        restaurant.setName(restaurantDetails.getName());
        restaurant.setDescription(restaurantDetails.getDescription());
        
        return restaurantRepository.save(restaurant);
    }
    
    /**
     * Delete a restaurant
     * @param id The restaurant ID to delete
     * @throws ResourceNotFoundException if not found
     */
    @CacheEvict(value = {"restaurantServiceFindAll", "restaurantServiceFindById"}, allEntries = true)
    public void deleteRestaurant(Long id) {
        logger.info("Deleting restaurant with ID: {}", id);
        
        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
        
        restaurantRepository.delete(restaurant);
    }
} 