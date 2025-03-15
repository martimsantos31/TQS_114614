package pt.ua.deti.tqs.lab3_2cars.services;

import org.springframework.stereotype.Service;
import pt.ua.deti.tqs.lab3_2cars.entities.Car;
import pt.ua.deti.tqs.lab3_2cars.repositorys.CarRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CarManagerService {
    private final CarRepository carRepository;

    public CarManagerService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car save(Car car){
        return carRepository.save(car);
    }

    public List<Car> findAll(){
        return carRepository.findAll();
    }

    public Optional<Car> getCarDetails(Long carId){
        return carRepository.findByCarId(carId);
    }

    public Optional<Car> findReplacementCar(Long carId) {
        Optional<Car> originalCar = carRepository.findByCarId(carId);
        if (originalCar.isEmpty()) {
            return Optional.empty();
        }

        List<Car> replacements = carRepository.findBySegmentAndEngineType(
                originalCar.get().getSegment(),
                originalCar.get().getEngineType()
        );

        return replacements.stream()
                .filter(car -> !car.getCarId().equals(carId))
                .findFirst();
    }
}
