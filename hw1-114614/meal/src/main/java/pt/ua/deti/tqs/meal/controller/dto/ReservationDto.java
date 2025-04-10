package pt.ua.deti.tqs.meal.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import pt.ua.deti.tqs.meal.domain.Reservation;

import java.time.LocalDateTime;

@Schema(description = "Reservation Data Transfer Object")
public class ReservationDto {
    @Schema(description = "Unique reservation token", example = "ABC123XYZ")
    private String token;
    
    @Schema(description = "Timestamp when the reservation was created", example = "2023-05-15T14:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Whether the reservation has been used", example = "false")
    private boolean used;
    
    @Schema(description = "ID of the meal being reserved", example = "42")
    private Long mealId;
    
    @Schema(description = "Name of the meal being reserved", example = "Vegetarian Pasta")
    private String mealName;
    
    @Schema(description = "Name of the restaurant offering the meal", example = "Italian Bistro")
    private String restaurantName;
    
    // Default constructor for JSON deserialization
    public ReservationDto() {
    }

    public ReservationDto(Reservation reservation) {
        this.token = reservation.getToken();
        this.createdAt = reservation.getCreatedAt();
        this.used = reservation.isUsed();
        
        if (reservation.getMeal() != null) {
            this.mealId = reservation.getMeal().getId();
            this.mealName = reservation.getMeal().getName();
            
            if (reservation.getMeal().getRestaurant() != null) {
                this.restaurantName = reservation.getMeal().getRestaurant().getName();
            }
        }
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isUsed() {
        return used;
    }

    public Long getMealId() {
        return mealId;
    }

    public String getMealName() {
        return mealName;
    }

    public String getRestaurantName() {
        return restaurantName;
    }
} 