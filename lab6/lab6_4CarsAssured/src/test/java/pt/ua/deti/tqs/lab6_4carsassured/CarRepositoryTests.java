package pt.ua.deti.tqs.lab6_4carsassured;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import pt.ua.deti.tqs.lab6_4carsassured.model.Car;
import pt.ua.deti.tqs.lab6_4carsassured.repository.CarRepository;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CarRepositoryTests {

    @Autowired
    private CarRepository carRepository;


    @Test
    public void testFindById() {
        Car car =new Car(null, "Toyota", "Corolla", "Sedan", "Electric");;
        Car savedCar = carRepository.save(car);

        Optional<Car> retrievedCar = carRepository.findById(savedCar.getCarId());

        assertThat(retrievedCar).isPresent();
        assertThat(retrievedCar.get().getMaker()).isEqualTo("Toyota");
    }

    @Test
    public void testFindAll() {
        Car car1 = new Car(null, "BMW", "X5", "SUV", "Diesel");
    Car car2 = new Car(null, "Audi", "A4", "Sedan", "Gasoline");
        carRepository.save(car1);
        carRepository.save(car2);

        List<Car> cars = carRepository.findAll();

        assertThat(cars).hasSize(2);
    }

    @Test
    public void testDeleteCar() {
        Car car = new Car(null, "Mazda", "CX-5", "SUV", "Diesel");
        Car savedCar = carRepository.save(car);

        carRepository.deleteById(savedCar.getCarId());
        Optional<Car> deletedCar = carRepository.findById(savedCar.getCarId());

        assertThat(deletedCar).isEmpty();
    }
}
