package pt.ua.deti.tqs.meal.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Reservation;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.exception.ResourceNotFoundException;
import pt.ua.deti.tqs.meal.repository.MealRepository;
import pt.ua.deti.tqs.meal.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Meal testMeal;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        // Create test data
        Restaurant restaurant = new Restaurant();
        restaurant.setId(1L);
        restaurant.setName("Test Restaurant");

        testMeal = new Meal();
        testMeal.setId(1L);
        testMeal.setName("Test Meal");
        testMeal.setRestaurant(restaurant);
        testMeal.setAvailableDate(LocalDate.now());

        testReservation = new Reservation();
        testReservation.setId(1L);
        testReservation.setMeal(testMeal);
        testReservation.setToken("TEST_TOKEN");
        testReservation.setUsed(false);
        testReservation.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void whenCreateReservation_thenReturnReservation() {
        // Arrange
        when(mealRepository.findById(1L)).thenReturn(Optional.of(testMeal));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation savedReservation = invocation.getArgument(0);
            savedReservation.setId(1L);
            return savedReservation;
        });

        // Act
        Reservation result = reservationService.createReservation(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMeal()).isEqualTo(testMeal);
        assertThat(result.isUsed()).isFalse();
        assertThat(result.getToken()).isNotNull();
    }

    @Test
    void whenCreateReservationWithInvalidMealId_thenThrowException() {
        // Arrange
        when(mealRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.createReservation(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Meal not found");
    }

    @Test
    void whenGetReservationByToken_thenReturnReservation() {
        // Arrange
        when(reservationRepository.findByToken("TEST_TOKEN")).thenReturn(Optional.of(testReservation));

        // Act
        Reservation result = reservationService.getReservationByToken("TEST_TOKEN");

        // Assert
        assertThat(result).isEqualTo(testReservation);
    }

    @Test
    void whenGetReservationByInvalidToken_thenThrowException() {
        // Arrange
        when(reservationRepository.findByToken("INVALID_TOKEN")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> reservationService.getReservationByToken("INVALID_TOKEN"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Reservation not found");
    }

    @Test
    void whenMarkReservationAsUsed_thenUpdateReservation() {
        // Arrange
        Reservation unusedReservation = new Reservation();
        unusedReservation.setId(1L);
        unusedReservation.setMeal(testMeal);
        unusedReservation.setToken("TEST_TOKEN");
        unusedReservation.setUsed(false);

        Reservation usedReservation = new Reservation();
        usedReservation.setId(1L);
        usedReservation.setMeal(testMeal);
        usedReservation.setToken("TEST_TOKEN");
        usedReservation.setUsed(true);

        when(reservationRepository.findByToken("TEST_TOKEN")).thenReturn(Optional.of(unusedReservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(usedReservation);

        // Act
        Reservation result = reservationService.markReservationAsUsed("TEST_TOKEN");

        // Assert
        assertThat(result.isUsed()).isTrue();
    }

    @Test
    void whenGetActiveReservations_thenReturnListOfReservations() {
        // Arrange
        List<Reservation> activeReservations = Arrays.asList(
                testReservation,
                new Reservation()
        );
        when(reservationRepository.findByUsed(false)).thenReturn(activeReservations);

        // Act
        List<Reservation> result = reservationService.getActiveReservations();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).contains(testReservation);
    }
} 