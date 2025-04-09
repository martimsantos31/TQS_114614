package pt.ua.deti.tqs.meal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Reservation;
import pt.ua.deti.tqs.meal.repository.MealRepository;
import pt.ua.deti.tqs.meal.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    public Optional<Reservation> createReservation(Long mealId) {
        logger.info("Creating reservation for meal ID: {}", mealId);
        
        Optional<Meal> meal = mealRepository.findById(mealId);
        if (meal.isEmpty()) {
            logger.warn("Meal with ID {} not found", mealId);
            return Optional.empty();
        }

        Reservation reservation = new Reservation();
        reservation.setMeal(meal.get());
        reservation.setToken(generateToken());
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUsed(false);

        Reservation savedReservation = reservationRepository.save(reservation);
        logger.info("Created reservation with ID {} and token {}", savedReservation.getId(), savedReservation.getToken());
        
        return Optional.of(savedReservation);
    }

    /**
     * Get reservation details by token
     * @param token The reservation token
     * @return Optional containing the reservation if found
     */
    public Optional<Reservation> getReservationByToken(String token) {
        logger.info("Finding reservation with token: {}", token);
        return reservationRepository.findByToken(token);
    }

    /**
     * Get reservation details by code
     * @param code The reservation code
     * @return Optional containing the reservation if found
     */
    public Optional<Reservation> getReservationByCode(String code) {
        logger.info("Finding reservation with code: {}", code);
        return reservationRepository.findByToken(code);
    }

    /**
     * Get all active (non-used) reservations for a restaurant
     * @param restaurantId The restaurant ID
     * @return List of active reservations
     */
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
     * @return Optional containing the updated reservation if successful
     */
    public Optional<Reservation> markReservationAsUsed(String token) {
        logger.info("Marking reservation with token {} as used", token);
        
        Optional<Reservation> reservation = reservationRepository.findByToken(token);
        if (reservation.isEmpty()) {
            logger.warn("Reservation with token {} not found", token);
            return Optional.empty();
        }
        
        if (reservation.get().isUsed()) {
            logger.warn("Reservation with token {} has already been used", token);
            return Optional.empty();
        }
        
        Reservation r = reservation.get();
        r.setUsed(true);
        Reservation updatedReservation = reservationRepository.save(r);
        
        logger.info("Reservation with token {} marked as used", token);
        return Optional.of(updatedReservation);
    }
    
    /**
     * Delete a reservation by token
     * @param token The reservation token
     * @return true if deletion was successful, false if the reservation was not found or already used
     */
    public boolean deleteReservation(String token) {
        logger.info("Deleting reservation with token: {}", token);
        
        Optional<Reservation> reservation = reservationRepository.findByToken(token);
        if (reservation.isEmpty()) {
            logger.warn("Reservation with token {} not found", token);
            return false;
        }
        
        // Do not allow deletion of used reservations
        if (reservation.get().isUsed()) {
            logger.warn("Cannot delete reservation with token {} as it has already been used", token);
            return false;
        }
        
        // Delete the reservation
        reservationRepository.delete(reservation.get());
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