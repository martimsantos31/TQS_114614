package pt.ua.deti.tqs.meal.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pt.ua.deti.tqs.meal.controller.dto.ReservationDto;
import pt.ua.deti.tqs.meal.domain.Meal;
import pt.ua.deti.tqs.meal.domain.Restaurant;
import pt.ua.deti.tqs.meal.repository.MealRepository;
import pt.ua.deti.tqs.meal.repository.RestaurantRepository;
import pt.ua.deti.tqs.meal.repository.ReservationRepository;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("integration-test")
public class PostgresIntegrationTest {
    
    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private MealRepository mealRepository;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false"); // Disable Flyway for tests
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop"); // Use Hibernate to create schema
    }
    
    @BeforeEach
    void setUp() {
        // Clear existing data in correct order to avoid foreign key constraint violations
        reservationRepository.deleteAll();
        mealRepository.deleteAll();
        restaurantRepository.deleteAll();
        
        // Create test restaurant
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Integration Test Restaurant");
        restaurant.setDescription("Test Description");
        restaurant = restaurantRepository.save(restaurant);
        
        // Create test meal
        Meal meal = new Meal();
        meal.setName("Integration Test Meal");
        meal.setDescription("Test Meal Description");
        meal.setRestaurant(restaurant);
        meal.setAvailableDate(LocalDate.now());
        mealRepository.save(meal);
    }
    
    @Test
    void testCreateAndGetReservation() {
        // Find the test meal
        Meal meal = mealRepository.findAll().get(0);
        
        // Create a reservation
        String url = "http://localhost:" + port + "/api/v1/reservations?mealId=" + meal.getId();
        ResponseEntity<ReservationDto> createResponse = restTemplate.postForEntity(url, null, ReservationDto.class);
        
        // Verify creation was successful
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getToken()).isNotNull();
        assertThat(createResponse.getBody().isUsed()).isFalse();
        
        // Get the reservation
        String token = createResponse.getBody().getToken();
        String getUrl = "http://localhost:" + port + "/api/v1/reservations/" + token;
        ResponseEntity<ReservationDto> getResponse = restTemplate.getForEntity(getUrl, ReservationDto.class);
        
        // Verify retrieval was successful
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().getToken()).isEqualTo(token);
        assertThat(getResponse.getBody().isUsed()).isFalse();
        
        // Test getting reservation by code too
        String getByCodeUrl = "http://localhost:" + port + "/api/v1/reservations/code/" + token;
        ResponseEntity<ReservationDto> getByCodeResponse = restTemplate.getForEntity(getByCodeUrl, ReservationDto.class);
        
        // Verify code-based retrieval was successful
        assertThat(getByCodeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getByCodeResponse.getBody()).isNotNull();
        assertThat(getByCodeResponse.getBody().getToken()).isEqualTo(token);
    }
    
    @Test
    void testMarkReservationAsUsed() {
        // Find the test meal
        Meal meal = mealRepository.findAll().get(0);
        
        // Create a reservation
        String url = "http://localhost:" + port + "/api/v1/reservations?mealId=" + meal.getId();
        ResponseEntity<ReservationDto> createResponse = restTemplate.postForEntity(url, null, ReservationDto.class);
        String token = createResponse.getBody().getToken();
        
        // Mark reservation as used
        String useUrl = "http://localhost:" + port + "/api/v1/reservations/" + token + "/use";
        ResponseEntity<ReservationDto> useResponse = restTemplate.exchange(
            useUrl, HttpMethod.PUT, HttpEntity.EMPTY, ReservationDto.class);
        
        // Verify the reservation was marked as used
        assertThat(useResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(useResponse.getBody()).isNotNull();
        assertThat(useResponse.getBody().getToken()).isEqualTo(token);
        assertThat(useResponse.getBody().isUsed()).isTrue();
        
        // Test using code endpoint too
        // Create a new reservation
        ResponseEntity<ReservationDto> createResponse2 = restTemplate.postForEntity(url, null, ReservationDto.class);
        String token2 = createResponse2.getBody().getToken();
        
        // Mark reservation as used via code endpoint
        String useByCodeUrl = "http://localhost:" + port + "/api/v1/reservations/code/" + token2 + "/use";
        ResponseEntity<ReservationDto> useByCodeResponse = restTemplate.exchange(
            useByCodeUrl, HttpMethod.PUT, HttpEntity.EMPTY, ReservationDto.class);
        
        // Verify the reservation was marked as used
        assertThat(useByCodeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(useByCodeResponse.getBody().isUsed()).isTrue();
    }
    
    @Test
    void testCancelReservation() {
        // Find the test meal
        Meal meal = mealRepository.findAll().get(0);
        
        // Create a reservation
        String url = "http://localhost:" + port + "/api/v1/reservations?mealId=" + meal.getId();
        ResponseEntity<ReservationDto> createResponse = restTemplate.postForEntity(url, null, ReservationDto.class);
        String token = createResponse.getBody().getToken();
        
        // Cancel the reservation
        String deleteUrl = "http://localhost:" + port + "/api/v1/reservations/" + token;
        restTemplate.delete(deleteUrl);
        
        // Try to get the cancelled reservation
        String getUrl = "http://localhost:" + port + "/api/v1/reservations/" + token;
        ResponseEntity<ReservationDto> getResponse = restTemplate.getForEntity(getUrl, ReservationDto.class);
        
        // Verify the reservation is gone
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
    
    @Test
    void testActiveReservations() {
        // Find the test meal
        Meal meal = mealRepository.findAll().get(0);
        
        // Create two reservations
        String url = "http://localhost:" + port + "/api/v1/reservations?mealId=" + meal.getId();
        restTemplate.postForEntity(url, null, ReservationDto.class);
        restTemplate.postForEntity(url, null, ReservationDto.class);
        
        // Get active reservations
        String activeUrl = "http://localhost:" + port + "/api/v1/restaurants/" + 
                meal.getRestaurant().getId() + "/reservations/active";
        ResponseEntity<Object[]> activeResponse = restTemplate.getForEntity(activeUrl, Object[].class);
        
        // Verify we have 2 active reservations
        assertThat(activeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(activeResponse.getBody()).isNotNull();
        assertThat(activeResponse.getBody().length).isEqualTo(2);
    }
    
    @Test
    void testCacheStatistics() {
        // First call to populate the cache
        String url = "http://localhost:" + port + "/api/v1/restaurants";
        restTemplate.getForEntity(url, Object[].class);
        
        // Second call should hit the cache
        restTemplate.getForEntity(url, Object[].class);
        
        // Third call should hit the cache again
        restTemplate.getForEntity(url, Object[].class);
        
        // Check cache statistics
        String statsUrl = "http://localhost:" + port + "/api/v1/metrics/cache";
        ResponseEntity<Map> statsResponse = restTemplate.getForEntity(statsUrl, Map.class);
        
        // Verify the cache statistics are available
        assertThat(statsResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(statsResponse.getBody()).isNotNull();
        
        // Verify specific cache statistics
        Map<String, Object> stats = statsResponse.getBody();
        // Check that we have some stats in the response
        assertThat(stats).containsKey("restaurantServiceFindAll");
        
        Map<String, Object> restaurantStats = (Map<String, Object>) stats.get("restaurantServiceFindAll");
        assertThat(restaurantStats).containsKey("hits");
        assertThat(restaurantStats).containsKey("misses");
        
        // Verify we have at least 2 hits (from our 3 calls, with first being a miss)
        Number hitCount = (Number) restaurantStats.get("hits");
        assertThat(hitCount.intValue()).isGreaterThanOrEqualTo(2);
    }
    
    @Test
    void testNonExistentResourceHandling() {
        // Try to get a non-existent reservation
        String nonExistentToken = "NON_EXISTENT_TOKEN";
        String getUrl = "http://localhost:" + port + "/api/v1/reservations/" + nonExistentToken;
        ResponseEntity<String> getResponse = restTemplate.getForEntity(getUrl, String.class);
        
        // Verify proper 404 handling
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        // Try to mark a non-existent reservation as used
        String useUrl = "http://localhost:" + port + "/api/v1/reservations/" + nonExistentToken + "/use";
        ResponseEntity<String> useResponse = restTemplate.exchange(
            useUrl, HttpMethod.PUT, HttpEntity.EMPTY, String.class);
        
        // Verify proper 404 handling
        assertThat(useResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        // Try to create a reservation with non-existent meal
        String createUrl = "http://localhost:" + port + "/api/v1/reservations?mealId=9999";
        ResponseEntity<String> createResponse = restTemplate.postForEntity(createUrl, null, String.class);
        
        // Verify proper 404 handling
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
} 