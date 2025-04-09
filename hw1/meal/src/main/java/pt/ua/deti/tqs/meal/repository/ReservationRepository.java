package pt.ua.deti.tqs.meal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ua.deti.tqs.meal.domain.Reservation;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByToken(String token);
}