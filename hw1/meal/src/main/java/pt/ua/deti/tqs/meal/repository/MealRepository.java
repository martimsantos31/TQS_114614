package pt.ua.deti.tqs.meal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ua.deti.tqs.meal.domain.Meal;

public interface MealRepository extends JpaRepository<Meal, Long> {
}
