package pt.ua.deti.tqs.lab3_2cars.repositorys;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ua.deti.tqs.lab3_2cars.entities.Car;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    Optional<Car> findByCarId(Long carId);
    List<Car> findAll();
    List<Car> findBySegmentAndEngineType(String segment, String engineType);
}
