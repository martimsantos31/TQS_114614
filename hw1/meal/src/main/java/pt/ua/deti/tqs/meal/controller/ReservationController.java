package pt.ua.deti.tqs.meal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Reservation", description = "Reservation management APIs")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);
    
    @Autowired
    private ReservationService reservationService;
    
    /**
     * Create a new reservation
     * @param mealId The meal ID to reserve
     * @return The created reservation details
     */
    @Operation(summary = "Create a new reservation", description = "Creates a new reservation for the specified meal")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation created successfully",
                content = @Content(schema = @Schema(implementation = ReservationDto.class))),
        @ApiResponse(responseCode = "404", description = "Meal not found"),
        @ApiResponse(responseCode = "400", description = "Error creating reservation")
    })
    @PostMapping
    public ResponseEntity<?> createReservation(
            @Parameter(description = "ID of the meal to reserve") @RequestParam Long mealId) {
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
    @Operation(summary = "Get reservation by token", description = "Returns reservation details for the specified token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation found",
                content = @Content(schema = @Schema(implementation = ReservationDto.class))),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @GetMapping("/{token}")
    public ResponseEntity<?> getReservation(
            @Parameter(description = "Token of the reservation") @PathVariable String token) {
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
    @Operation(summary = "Find reservation by code", description = "Returns reservation details for the specified code (for staff use)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation found",
                content = @Content(schema = @Schema(implementation = ReservationDto.class))),
        @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    @GetMapping("/code/{code}")
    public ResponseEntity<?> findReservationByCode(
            @Parameter(description = "Code of the reservation") @PathVariable String code) {
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
    @Operation(summary = "Mark reservation as used", description = "Marks a reservation as used (check-in) by token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation marked as used",
                content = @Content(schema = @Schema(implementation = ReservationDto.class))),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "400", description = "Reservation already used")
    })
    @PutMapping("/{token}/use")
    public ResponseEntity<?> useReservation(
            @Parameter(description = "Token of the reservation") @PathVariable String token) {
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
    @Operation(summary = "Mark reservation as used by code", description = "Marks a reservation as used (check-in) by code (for staff use)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation marked as used",
                content = @Content(schema = @Schema(implementation = ReservationDto.class))),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "400", description = "Reservation already used")
    })
    @PutMapping("/code/{code}/use")
    public ResponseEntity<?> useReservationByCode(
            @Parameter(description = "Code of the reservation") @PathVariable String code) {
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
    @Operation(summary = "Cancel reservation", description = "Cancels (deletes) a reservation by token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reservation successfully cancelled"),
        @ApiResponse(responseCode = "404", description = "Reservation not found"),
        @ApiResponse(responseCode = "400", description = "Reservation already used")
    })
    @DeleteMapping("/{token}")
    public ResponseEntity<?> cancelReservation(
            @Parameter(description = "Token of the reservation") @PathVariable String token) {
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