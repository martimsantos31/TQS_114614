package pt.ua.deti.tqs.meal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Reservation;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.exception.ResourceNotFoundException;
import pt.ua.deti.tqs.meal.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    private Meal testMeal;
    private Reservation testReservation;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        // Create test data
        testRestaurant = new Restaurant();
        testRestaurant.setId(1L);
        testRestaurant.setName("Test Restaurant");
        testRestaurant.setDescription("Test Description");

        testMeal = new Meal();
        testMeal.setId(1L);
        testMeal.setName("Test Meal");
        testMeal.setDescription("Test Meal Description");
        testMeal.setRestaurant(testRestaurant);
        testMeal.setAvailableDate(LocalDate.now());

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setMeal(testMeal);
        testReservation.setToken("TEST123");
        testReservation.setCreatedAt(LocalDateTime.now());
        testReservation.setUsed(false);
    }

    @Test
    void whenGetReservation_thenReturnReservation() throws Exception {
        when(reservationService.getReservationByToken("TEST123")).thenReturn(testReservation);

        mockMvc.perform(get("/api/v1/reservations/TEST123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("TEST123")))
                .andExpect(jsonPath("$.used", is(false)));
    }

    @Test
    void whenGetNonExistentReservation_thenReturn404() throws Exception {
        when(reservationService.getReservationByToken("INVALID")).thenThrow(new ResourceNotFoundException("Reservation not found with token: INVALID"));

        mockMvc.perform(get("/api/v1/reservations/INVALID")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenFindReservationByCode_thenReturnReservation() throws Exception {
        when(reservationService.getReservationByToken("TEST123")).thenReturn(testReservation);

        mockMvc.perform(get("/api/v1/reservations/code/TEST123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("TEST123")))
                .andExpect(jsonPath("$.used", is(false)));
    }

    @Test
    void whenFindReservationByInvalidCode_thenReturn404() throws Exception {
        when(reservationService.getReservationByToken("INVALID")).thenThrow(new ResourceNotFoundException("Reservation not found with token: INVALID"));

        mockMvc.perform(get("/api/v1/reservations/code/INVALID")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenMarkReservationAsUsed_thenReturnUpdatedReservation() throws Exception {
        Reservation usedReservation = new Reservation();
        usedReservation.setId(1L);
        usedReservation.setMeal(testMeal);
        usedReservation.setToken("TEST123");
        usedReservation.setCreatedAt(LocalDateTime.now());
        usedReservation.setUsed(true);

        when(reservationService.markReservationAsUsed("TEST123")).thenReturn(usedReservation);

        mockMvc.perform(put("/api/v1/reservations/TEST123/use")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("TEST123")))
                .andExpect(jsonPath("$.used", is(true)));
    }

    @Test
    void whenMarkNonExistentReservationAsUsed_thenReturn404() throws Exception {
        when(reservationService.markReservationAsUsed("INVALID")).thenThrow(new ResourceNotFoundException("Reservation not found with token: INVALID"));

        mockMvc.perform(put("/api/v1/reservations/INVALID/use")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenMarkReservationAsUsedByCode_thenReturnUpdatedReservation() throws Exception {
        Reservation usedReservation = new Reservation();
        usedReservation.setId(1L);
        usedReservation.setMeal(testMeal);
        usedReservation.setToken("TEST123");
        usedReservation.setCreatedAt(LocalDateTime.now());
        usedReservation.setUsed(true);

        when(reservationService.markReservationAsUsed("TEST123")).thenReturn(usedReservation);

        mockMvc.perform(put("/api/v1/reservations/code/TEST123/use")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("TEST123")))
                .andExpect(jsonPath("$.used", is(true)));
    }

    @Test
    void whenCancelReservation_thenReturnSuccess() throws Exception {
        when(reservationService.deleteReservation("TEST123")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/reservations/TEST123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenCancelNonExistentReservation_thenReturn404() throws Exception {
        when(reservationService.deleteReservation("INVALID")).thenThrow(new ResourceNotFoundException("Reservation not found with token: INVALID"));

        mockMvc.perform(delete("/api/v1/reservations/INVALID")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenCreateReservation_thenReturnNewReservation() throws Exception {
        when(reservationService.createReservation(1L)).thenReturn(testReservation);

        mockMvc.perform(post("/api/v1/reservations?mealId=1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("TEST123")))
                .andExpect(jsonPath("$.used", is(false)));
    }

    @Test
    void whenCreateReservationWithInvalidMeal_thenReturn404() throws Exception {
        when(reservationService.createReservation(999L)).thenThrow(new ResourceNotFoundException("Meal not found with id: 999"));

        mockMvc.perform(post("/api/v1/reservations?mealId=999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
} 