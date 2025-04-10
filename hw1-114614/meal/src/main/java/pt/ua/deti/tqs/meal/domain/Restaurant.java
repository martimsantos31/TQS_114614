package pt.ua.deti.tqs.meal.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Schema(description = "Restaurant entity")
public class Restaurant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier for the restaurant", example = "1")
    private Long id;
    
    @Schema(description = "Name of the restaurant", example = "Italian Bistro")
    private String name;
    
    @Schema(description = "Description of the restaurant", example = "Authentic Italian cuisine in a cozy atmosphere")
    private String description;
    
    public Restaurant() {
       
    }
    
    public Restaurant(String name, String description) {
        this.name = name;
        this.description = description;
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
}
