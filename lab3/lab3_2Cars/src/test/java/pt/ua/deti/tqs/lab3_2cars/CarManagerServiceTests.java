package pt.ua.deti.tqs.lab3_2cars;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pt.ua.deti.tqs.lab3_2cars.entities.Car;
import pt.ua.deti.tqs.lab3_2cars.repositorys.CarRepository;
import pt.ua.deti.tqs.lab3_2cars.services.CarManagerService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CarManagerServiceTests {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarManagerService carManagerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindReplacementCar() {
        Car originalCar = new Car(1L, "Toyota", "Corolla", "Sedan", "Petrol");
        Car replacementCar = new Car(2L, "Honda", "Civic", "Sedan", "Petrol");

        when(carRepository.findByCarId(1L)).thenReturn(Optional.of(originalCar));
        when(carRepository.findBySegmentAndEngineType("Sedan", "Petrol"))
                .thenReturn(List.of(replacementCar));

        Optional<Car> result = carManagerService.findReplacementCar(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getMaker()).isEqualTo("Honda");
    }

    @Test
    void testFindReplacementCar_NotFound() {
        when(carRepository.findByCarId(1L)).thenReturn(Optional.empty());

        Optional<Car> result = carManagerService.findReplacementCar(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void testSaveCar() {
        Car car = new Car(1L, "Toyota", "Corolla", "Sedan", "Electric");
        when(carRepository.save(Mockito.any(Car.class))).thenReturn(car);

        Car savedCar = carManagerService.save(car);

        assertNotNull(savedCar);
        assertEquals(car.getMaker(), savedCar.getMaker());
        assertEquals(car.getModel(), savedCar.getModel());
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void testFindAllCars() {
       List<Car> cars = Arrays.asList(
               new Car(1L, "Toyota", "Corolla", "Sedan", "Electric"),
               new Car(2L, "Honda", "Civic","SUV","Diesel")
       );

       when(carRepository.findAll()).thenReturn(cars);

       List<Car> foundCars = carManagerService.findAll();

       assertEquals(2, foundCars.size());
       verify(carRepository, times(1)).findAll();
    }

    @Test
    void testGetCarDetails_Found() {
        Car car = new Car(1L, "Toyota", "Corolla", "Sedan", "Electric");
        when(carRepository.findByCarId(1L)).thenReturn(Optional.of(car));

        Optional<Car> foundCar = carManagerService.getCarDetails(1L);

        assertTrue(foundCar.isPresent());
        assertEquals(car.getMaker(), foundCar.get().getMaker());
        assertEquals(car.getModel(), foundCar.get().getModel());
        verify(carRepository, times(1)).findByCarId(1L);
    }

    @Test
    void testGetCarDetails_NotFound() {
        when(carRepository.findByCarId(1L)).thenReturn(Optional.empty());

        Optional<Car> foundCar = carManagerService.getCarDetails(1L);

        assertFalse(foundCar.isPresent());
        verify(carRepository, times(1)).findByCarId(1L);
    }


}
