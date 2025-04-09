package pt.ua.deti.tqs.meal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ua.deti.tqs.meal.domain.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

}

