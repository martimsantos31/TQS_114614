package pt.ua.deti.tqs.meal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Reservation;
import pt.ua.deti.tqs.meal.exception.ResourceNotFoundException;
import pt.ua.deti.tqs.meal.repository.MealRepository;
import pt.ua.deti.tqs.meal.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MealRepository mealRepository;

    /**
     * Create a new reservation
     * @param mealId The meal ID to reserve
     * @return The created reservation with token
     */
    public Reservation createReservation(Long mealId) {
        logger.info("Creating reservation for meal ID: {}", mealId);
        
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new ResourceNotFoundException("Meal not found with id: " + mealId));

        Reservation reservation = new Reservation();
        reservation.setMeal(meal);
        reservation.setToken(generateToken());
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUsed(false);

        Reservation savedReservation = reservationRepository.save(reservation);
        logger.info("Created reservation with ID {} and token {}", savedReservation.getId(), savedReservation.getToken());
        
        // Clear the active reservations cache since we added a new one
        return savedReservation;
    }

    /**
     * Get reservation details by token
     * @param token The reservation token
     * @return The reservation if found
     * @throws ResourceNotFoundException if not found
     */
    @Cacheable(value = "reservationsByToken", key = "#token")
    public Reservation getReservationByToken(String token) {
        logger.info("Finding reservation with token: {}", token);
        return reservationRepository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with token: " + token));
    }

    /**
     * Get all active (non-used) reservations
     * @return List of all active reservations
     */
    @Cacheable(value = "activeReservations")
    public List<Reservation> getActiveReservations() {
        logger.info("Finding all active reservations");
        return reservationRepository.findByUsed(false);
    }

    /**
     * Get all active (non-used) reservations for a restaurant
     * @param restaurantId The restaurant ID
     * @return List of active reservations
     */
    @Cacheable(value = "activeReservationsByRestaurant", key = "#restaurantId")
    public List<Reservation> getActiveReservationsForRestaurant(Long restaurantId) {
        logger.info("Finding active reservations for restaurant ID: {}", restaurantId);
        
        // Get all reservations where:
        // 1. The reservation is not used
        // 2. The meal belongs to the specified restaurant
        List<Reservation> allReservations = reservationRepository.findByUsed(false);
        
        return allReservations.stream()
                .filter(r -> r.getMeal().getRestaurant().getId().equals(restaurantId))
                .collect(Collectors.toList());
    }

    /**
     * Mark a reservation as used (when a user checks in)
     * @param token The reservation token
     * @return The updated reservation
     * @throws ResourceNotFoundException if not found
     */
    @CachePut(value = "reservationsByToken", key = "#token")
    @CacheEvict(value = {"activeReservations", "activeReservationsByRestaurant"}, allEntries = true)
    public Reservation markReservationAsUsed(String token) {
        logger.info("Marking reservation with token {} as used", token);
        
        Reservation reservation = reservationRepository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with token: " + token));
        
        if (reservation.isUsed()) {
            logger.warn("Reservation with token {} has already been used", token);
            throw new IllegalStateException("Reservation has already been used");
        }
        
        reservation.setUsed(true);
        Reservation updatedReservation = reservationRepository.save(reservation);
        
        logger.info("Reservation with token {} marked as used", token);
        return updatedReservation;
    }
    
    /**
     * Delete a reservation by token
     * @param token The reservation token
     * @return true if deletion was successful
     * @throws ResourceNotFoundException if not found
     * @throws IllegalStateException if already used
     */
    @CacheEvict(value = {"reservationsByToken", "activeReservations", "activeReservationsByRestaurant"}, allEntries = true)
    public boolean deleteReservation(String token) {
        logger.info("Deleting reservation with token: {}", token);
        
        Reservation reservation = reservationRepository.findByToken(token)
            .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with token: " + token));
        
        // Do not allow deletion of used reservations
        if (reservation.isUsed()) {
            logger.warn("Cannot delete reservation with token {} as it has already been used", token);
            throw new IllegalStateException("Cannot delete a used reservation");
        }
        
        // Delete the reservation
        reservationRepository.delete(reservation);
        logger.info("Reservation with token {} deleted successfully", token);
        return true;
    }

    /**
     * Generate a unique reservation token
     * @return A unique token string
     */
    private String generateToken() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 