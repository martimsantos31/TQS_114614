package pt.ua.deti.tqs.lab6_4carsassured;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import pt.ua.deti.tqs.lab6_4carsassured.model.Car;
import pt.ua.deti.tqs.lab6_4carsassured.repository.CarRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integrationtest.properties")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarRestControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarRepository carRepository;

    @BeforeEach
    void setup() {
        carRepository.deleteAll();
        carRepository.save(new Car(1L,"Toyota", "Corolla", "Sedan", "Petrol"));
        carRepository.save(new Car(2L,"Honda", "Civic", "SUV", "Diesel"));
    }

    @Test
    void whenGetAllCars_thenReturnCarList() {
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                "/api/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Car>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody())
                .extracting(Car::getModel)
                .containsExactlyInAnyOrder("Corolla", "Civic");
    }
}
