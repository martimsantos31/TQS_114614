package pt.ua.deti.tqs.meal.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Meal {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private LocalDate availableDate;

    @ManyToOne
    private Restaurant restaurant;
    
    public Meal() {
        
    }
    
    public Meal(String name, String description, LocalDate availableDate, Restaurant restaurant) {
        this.name = name;
        this.description = description;
        this.availableDate = availableDate;
        this.restaurant = restaurant;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getAvailableDate() {
        return availableDate;
    }
    
    public void setAvailableDate(LocalDate availableDate) {
        this.availableDate = availableDate;
    }
    
    public Restaurant getRestaurant() {
        return restaurant;
    }
    
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
}
