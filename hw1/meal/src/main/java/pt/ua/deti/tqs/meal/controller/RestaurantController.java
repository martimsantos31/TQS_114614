package pt.ua.deti.tqs.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.service.RestaurantService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/restaurants")
@CrossOrigin(origins = "http://localhost:5473")
public class RestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);
    
    @Autowired
    private RestaurantService restaurantService;
    
    
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        logger.info("Request to get all restaurants");
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }
    

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        logger.info("Request to get restaurant with ID: {}", id);
        
        Optional<Restaurant> restaurant = restaurantService.getRestaurantById(id);
        return restaurant
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
} 