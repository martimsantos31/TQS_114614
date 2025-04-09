package pt.ua.deti.tqs.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.meal.controller.dto.ReservationDto;
import pt.ua.deti.tqs.meal.domain.Reservation;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.exception.ResourceNotFoundException;
import pt.ua.deti.tqs.meal.service.ReservationService;
import pt.ua.deti.tqs.meal.service.RestaurantService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/restaurants")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5173"})
public class RestaurantController {
    private static final Logger logger = LoggerFactory.getLogger(RestaurantController.class);
    
    @Autowired
    private RestaurantService restaurantService;
    
    @Autowired
    private ReservationService reservationService;
    
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        logger.info("Request to get all restaurants");
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable Long id) {
        logger.info("Request to get restaurant with ID: {}", id);
        
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            return ResponseEntity.ok(restaurant);
        } catch (ResourceNotFoundException e) {
            logger.warn("Restaurant not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all active reservations for a restaurant
     * @param id The restaurant ID
     * @return List of active reservations
     */
    @GetMapping("/{id}/reservations/active")
    public ResponseEntity<?> getActiveReservations(@PathVariable Long id) {
        logger.info("Request to get active reservations for restaurant with ID: {}", id);
        
        try {
            // Just check if the restaurant exists
            restaurantService.getRestaurantById(id);
            
            List<Reservation> activeReservations = reservationService.getActiveReservationsForRestaurant(id);
            List<ReservationDto> reservationDtos = activeReservations.stream()
                    .map(ReservationDto::new)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(reservationDtos);
        } catch (ResourceNotFoundException e) {
            logger.warn("Restaurant not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
} 