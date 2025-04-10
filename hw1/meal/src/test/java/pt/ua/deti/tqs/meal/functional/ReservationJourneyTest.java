package pt.ua.deti.tqs.meal.functional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * API functional tests for the Reservation journey.
 * 
 * Note: These tests focus on the API functionality, not the frontend UI.
 * Since the frontend is a separate application, we test the API endpoints
 * that the frontend would call.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationJourneyTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetAllRestaurants() {
        // Get all restaurants - using parameterizedTypeReference for proper type handling
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/restaurants", 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        // Verify we got a successful response
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isGreaterThan(0);
    }

    @Test
    void testGetRestaurantDetails() {
        // First get all restaurants
        ResponseEntity<List<Map<String, Object>>> allRestaurantsResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/restaurants", 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        // Get the ID of the first restaurant
        List<Map<String, Object>> restaurants = allRestaurantsResponse.getBody();
        assertThat(restaurants).isNotEmpty();
        
        Integer restaurantId = ((Number) restaurants.get(0).get("id")).intValue();
        
        // Get the restaurant details
        ResponseEntity<Map<String, Object>> restaurantResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/restaurants/" + restaurantId, 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        // Verify we got a successful response
        assertThat(restaurantResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(restaurantResponse.getBody()).isNotNull();
        assertThat(restaurantResponse.getBody().get("name")).isNotNull();
        
        // Get meals for the restaurant - use the correct meals endpoint
        ResponseEntity<List<Map<String, Object>>> mealsResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/meals?restaurantId=" + restaurantId, 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        // Verify we got meals
        assertThat(mealsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(mealsResponse.getBody()).isNotEmpty();
    }

    @Test
    void testCreateAndCheckReservation() {
        // First get all restaurants
        ResponseEntity<List<Map<String, Object>>> allRestaurantsResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/restaurants", 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        // Get the ID of the first restaurant
        List<Map<String, Object>> restaurants = allRestaurantsResponse.getBody();
        Integer restaurantId = ((Number) restaurants.get(0).get("id")).intValue();
        
        // Get meals for the restaurant
        ResponseEntity<List<Map<String, Object>>> mealsResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/meals?restaurantId=" + restaurantId, 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        // Get the ID of the first meal
        List<Map<String, Object>> meals = mealsResponse.getBody();
        assertThat(meals).isNotEmpty();
        
        Integer mealId = ((Number) meals.get(0).get("id")).intValue();
        
        // Create a reservation - use exchange instead of postForEntity for consistency
        ResponseEntity<Map<String, Object>> reservationResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/reservations?mealId=" + mealId,
            HttpMethod.POST,
            null, 
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        // Verify the reservation was created
        assertThat(reservationResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(reservationResponse.getBody()).isNotNull();
        assertThat(reservationResponse.getBody().get("token")).isNotNull();
        
        String token = (String) reservationResponse.getBody().get("token");
        
        // Check the reservation
        ResponseEntity<Map<String, Object>> checkResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/reservations/" + token,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        // Verify we can retrieve the reservation
        assertThat(checkResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(checkResponse.getBody().get("token")).isEqualTo(token);
        assertThat(checkResponse.getBody().get("used")).isEqualTo(false);
    }

    @Test
    void testUseReservation() {
        // First get all restaurants
        ResponseEntity<List<Map<String, Object>>> allRestaurantsResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/restaurants", 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        // Get the ID of the first restaurant
        List<Map<String, Object>> restaurants = allRestaurantsResponse.getBody();
        Integer restaurantId = ((Number) restaurants.get(0).get("id")).intValue();
        
        // Get meals for the restaurant
        ResponseEntity<List<Map<String, Object>>> mealsResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/meals?restaurantId=" + restaurantId, 
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );
        
        // Get the ID of the first meal
        List<Map<String, Object>> meals = mealsResponse.getBody();
        assertThat(meals).isNotEmpty();
        
        Integer mealId = ((Number) meals.get(0).get("id")).intValue();
        
        // Create a reservation - use exchange instead of postForEntity
        ResponseEntity<Map<String, Object>> reservationResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/reservations?mealId=" + mealId,
            HttpMethod.POST,
            null, 
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        String token = (String) reservationResponse.getBody().get("token");
        
        // Mark reservation as used
        ResponseEntity<Map<String, Object>> useResponse = restTemplate.exchange(
            "http://localhost:" + port + "/api/v1/reservations/" + token + "/use",
            HttpMethod.PUT,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        
        // Verify the reservation was marked as used
        assertThat(useResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(useResponse.getBody().get("used")).isEqualTo(true);
    }
} 