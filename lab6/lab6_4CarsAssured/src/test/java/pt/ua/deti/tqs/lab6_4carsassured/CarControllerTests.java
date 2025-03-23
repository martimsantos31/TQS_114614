package pt.ua.deti.tqs.lab6_4carsassured;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.lab6_4carsassured.model.Car;
import pt.ua.deti.tqs.lab6_4carsassured.service.CarManagerService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarManagerService carManagerService;

    @Test
    public void testCreateCar() throws Exception {
        Car car = new Car(1L, "Toyota", "Corolla", "Sedan", "Electric");
        Mockito.when(carManagerService.save(Mockito.any(Car.class))).thenReturn(car);

        mockMvc.perform(post("/cars")
                .contentType("application/json")
                .content("{\"maker\": \"Toyota\", \"model\": \"Corolla\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maker").value("Toyota"));
    }

    @Test
    public void testGetAllCars() throws Exception {
        List<Car> cars = Arrays.asList(new Car(1L, "Toyota", "Corolla", "Sedan", "Electric"), new Car(2L, "Honda", "Civic","SUV","Diesel"));
        Mockito.when(carManagerService.findAll()).thenReturn(cars);

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].maker").value("Toyota"))
                .andExpect(jsonPath("$[1].maker").value("Honda"));
    }

    @Test
    public void testGetCarById() throws Exception {
    Car car = new Car(1L, "Toyota", "Corolla", "Sedan", "Electric");
        Mockito.when(carManagerService.getCarDetails(1L)).thenReturn(Optional.of(car));

        mockMvc.perform(get("/cars/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maker").value("Toyota"));
    }
}
