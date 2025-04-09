package pt.ua.deti.tqs.meal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.ua.deti.tqs.meal.controller.dto.ReservationDto;
import pt.ua.deti.tqs.meal.domain.Reservation;
import pt.ua.deti.tqs.meal.exception.ResourceNotFoundException;
import pt.ua.deti.tqs.meal.service.ReservationService;

import java.util.List;
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
        
        try {
            Reservation reservation = reservationService.createReservation(mealId);
            return ResponseEntity.ok(new ReservationDto(reservation));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating reservation: " + e.getMessage());
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
        
        try {
            Reservation reservation = reservationService.getReservationByToken(token);
            return ResponseEntity.ok(new ReservationDto(reservation));
        } catch (ResourceNotFoundException e) {
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
        
        try {
            Reservation reservation = reservationService.getReservationByToken(code);
            return ResponseEntity.ok(new ReservationDto(reservation));
        } catch (ResourceNotFoundException e) {
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
        
        try {
            Reservation updatedReservation = reservationService.markReservationAsUsed(token);
            return ResponseEntity.ok(new ReservationDto(updatedReservation));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Reservation already used");
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
        
        try {
            Reservation updatedReservation = reservationService.markReservationAsUsed(code);
            return ResponseEntity.ok(new ReservationDto(updatedReservation));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Reservation already used");
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
        
        try {
            boolean deleted = reservationService.deleteReservation(token);
            return ResponseEntity.ok().body("Reservation successfully cancelled");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("Reservation already used");
        }
    }
}