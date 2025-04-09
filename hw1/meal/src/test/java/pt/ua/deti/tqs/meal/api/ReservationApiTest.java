package pt.ua.deti.tqs.meal.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.repository.MealRepository;
import pt.ua.deti.tqs.meal.repository.ReservationRepository;
import pt.ua.deti.tqs.meal.repository.RestaurantRepository;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private Long mealId;
    private String reservationToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1";

        // Clear data
        reservationRepository.deleteAll();
        mealRepository.deleteAll();
        restaurantRepository.deleteAll();

        // Create test data
        Restaurant restaurant = new Restaurant();
        restaurant.setName("REST-Assured Test Restaurant");
        restaurant.setDescription("Test Description");
        restaurant = restaurantRepository.save(restaurant);

        Meal meal = new Meal();
        meal.setName("REST-Assured Test Meal");
        meal.setDescription("A delicious test meal for REST-Assured tests");
        meal.setRestaurant(restaurant);
        meal.setAvailableDate(LocalDate.now());
        meal = mealRepository.save(meal);

        mealId = meal.getId();
    }

    @Test
    void whenCreateReservation_thenStatus200AndReservationReturned() {
        // Create a reservation
        reservationToken = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/reservations?mealId={id}", mealId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", notNullValue())
                .body("used", is(false))
                .extract().path("token");
    }

    @Test
    void whenCreateReservationWithInvalidMealId_thenStatus404() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/reservations?mealId={id}", 999)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", containsString("Meal not found"));
    }

    @Test
    void whenGetReservationByToken_thenStatus200AndReservationReturned() {
        // First create a reservation
        String token = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/reservations?mealId={id}", mealId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().path("token");

        // Then get it by token
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/reservations/{token}", token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", equalTo(token))
                .body("used", is(false));
    }

    @Test
    void whenGetReservationByInvalidToken_thenStatus404() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/reservations/{token}", "INVALID_TOKEN")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("message", containsString("Reservation not found"));
    }

    @Test
    void whenMarkReservationAsUsed_thenStatus200AndReservationIsUsed() {
        // First create a reservation
        String token = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/reservations?mealId={id}", mealId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().path("token");

        // Then mark it as used
        given()
                .contentType(ContentType.JSON)
                .when()
                .put("/reservations/{token}/use", token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("token", equalTo(token))
                .body("used", is(true));

        // Verify it's actually marked as used
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/reservations/{token}", token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("used", is(true));
    }

    @Test
    void whenCancelReservation_thenStatus200AndReservationDeleted() {
        // First create a reservation
        String token = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/reservations?mealId={id}", mealId)
                .then()
                .statusCode(HttpStatus.OK.value())
                .extract().path("token");

        // Then delete it
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/reservations/{token}", token)
                .then()
                .statusCode(HttpStatus.OK.value());

        // Verify it's actually deleted
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/reservations/{token}", token)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }
} 