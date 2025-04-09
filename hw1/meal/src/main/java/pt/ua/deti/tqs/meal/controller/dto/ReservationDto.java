package pt.ua.deti.tqs.meal.controller.dto;

import pt.ua.deti.tqs.meal.domain.Reservation;

import java.time.LocalDateTime;

public class ReservationDto {
    private String token;
    private LocalDateTime createdAt;
    private boolean used;
    private Long mealId;
    private String mealName;
    private String restaurantName;

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