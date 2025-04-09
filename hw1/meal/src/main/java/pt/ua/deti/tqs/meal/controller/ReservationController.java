package pt.ua.deti.tqs.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.meal.controller.dto.ReservationDto;
import pt.ua.deti.tqs.meal.domain.Reservation;
import pt.ua.deti.tqs.meal.service.ReservationService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/reservations")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5173"})
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    
    @Autowired
    private ReservationService reservationService;
    
    /**
     * Create a new reservation
     * @param mealId The meal ID to reserve
     * @return The created reservation details
     */
    @PostMapping
    public ResponseEntity<?> createReservation(@RequestParam Long mealId) {
        logger.info("Request to create reservation for meal ID: {}", mealId);
        
        Optional<Reservation> reservation = reservationService.createReservation(mealId);
        
        if (reservation.isPresent()) {
            return ResponseEntity.ok(new ReservationDto(reservation.get()));
        } else {
            return ResponseEntity.badRequest().body("Meal not found or not available");
        }
    }
    
    /**
     * Get reservation details by token
     * @param token The reservation token
     * @return Reservation details if found
     */
    @GetMapping("/{token}")
    public ResponseEntity<?> getReservation(@PathVariable String token) {
        logger.info("Request to get reservation with token: {}", token);
        
        Optional<Reservation> reservation = reservationService.getReservationByToken(token);
        
        if (reservation.isPresent()) {
            return ResponseEntity.ok(new ReservationDto(reservation.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Find a reservation by its code (for staff use)
     * @param code The reservation code
     * @return Reservation details if found
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<?> findReservationByCode(@PathVariable String code) {
        logger.info("Request to find reservation with code: {}", code);
        
        Optional<Reservation> reservation = reservationService.getReservationByCode(code);
        
        if (reservation.isPresent()) {
            return ResponseEntity.ok(new ReservationDto(reservation.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Mark a reservation as used (check-in) by token
     * @param token The reservation token
     * @return Updated reservation details
     */
    @PutMapping("/{token}/use")
    public ResponseEntity<?> useReservation(@PathVariable String token) {
        logger.info("Request to mark reservation with token {} as used", token);
        
        Optional<Reservation> updatedReservation = reservationService.markReservationAsUsed(token);
        
        if (updatedReservation.isPresent()) {
            return ResponseEntity.ok(new ReservationDto(updatedReservation.get()));
        } else {
            return ResponseEntity.badRequest().body("Reservation not found or already used");
        }
    }
    
    /**
     * Mark a reservation as used (check-in) by code
     * This is used by the staff interface where code is displayed
     * @param code The reservation code
     * @return Updated reservation details
     */
    @PutMapping("/code/{code}/use")
    public ResponseEntity<?> useReservationByCode(@PathVariable String code) {
        logger.info("Request to mark reservation with code {} as used", code);
        
        Optional<Reservation> updatedReservation = reservationService.markReservationAsUsed(code);
        
        if (updatedReservation.isPresent()) {
            return ResponseEntity.ok(new ReservationDto(updatedReservation.get()));
        } else {
            return ResponseEntity.badRequest().body("Reservation not found or already used");
        }
    }
    
    /**
     * Cancel/delete a reservation
     * @param token The reservation token
     * @return 200 OK if successful, 400 Bad Request if not found or already used
     */
    @DeleteMapping("/{token}")
    public ResponseEntity<?> cancelReservation(@PathVariable String token) {
        logger.info("Request to cancel reservation with token: {}", token);
        
        boolean deleted = reservationService.deleteReservation(token);
        
        if (deleted) {
            return ResponseEntity.ok().body("Reservation successfully cancelled");
        } else {
            return ResponseEntity.badRequest().body("Reservation not found or already used");
        }
    }
}