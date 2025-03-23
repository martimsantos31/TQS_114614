package pt.ua.deti.tqs.lab6_4carsassured;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.lab6_4carsassured.controller.CarController;
import pt.ua.deti.tqs.lab6_4carsassured.model.Car;
import pt.ua.deti.tqs.lab6_4carsassured.service.CarManagerService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@WebMvcTest(CarController.class)
public class CarControllerRestAssuredIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarManagerService carManagerService;

    @BeforeEach
    public void setUp() {
        List<Car> cars = Arrays.asList(
            new Car(1L, "Toyota", "Corolla", "Sedan", "Electric"),
            new Car(2L, "Honda", "Civic", "SUV", "Diesel")
        );
            when(carManagerService.findAll()).thenReturn(cars);
    }

    @Test
    void whenGetAllCars_thenReturnCarList() {
        RestAssuredMockMvc.given()
                .mockMvc(mockMvc)
                .when()
                .get("/cars")
                .then()
                .statusCode(200)
                .body("$.size()", is(2));
    }

}
