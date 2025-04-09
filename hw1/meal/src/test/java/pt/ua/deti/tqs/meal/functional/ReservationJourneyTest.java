package pt.ua.deti.tqs.meal.functional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationJourneyTest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setUp() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        
        driver = new FirefoxDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void testViewRestaurantsAndMeals() {
        // Open the home page
        driver.get("http://localhost:" + port);
        
        // Verify homepage title
        assertThat(driver.getTitle()).contains("Meal Booking");
        
        // Verify restaurants are loaded (wait for the first restaurant to appear)
        WebElement firstRestaurant = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".restaurant"))
        );
        assertThat(firstRestaurant).isNotNull();
        
        // Verify restaurant has a name and description
        WebElement restaurantName = firstRestaurant.findElement(By.cssSelector("h3"));
        WebElement restaurantDescription = firstRestaurant.findElement(By.cssSelector("p.description"));
        
        assertThat(restaurantName.getText()).isNotBlank();
        assertThat(restaurantDescription.getText()).isNotBlank();
    }

    @Test
    void testNavigateToRestaurantDetails() {
        // Open the home page
        driver.get("http://localhost:" + port);
        
        // Wait for restaurants to load
        WebElement firstRestaurant = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".restaurant"))
        );
        
        // Click on the first restaurant to view details
        WebElement viewDetailsButton = firstRestaurant.findElement(By.cssSelector("a.btn-primary"));
        viewDetailsButton.click();
        
        // Verify we're on the restaurant details page
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".restaurant-header")));
        
        // Verify meals are displayed
        WebElement mealsList = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".meals-container"))
        );
        assertThat(mealsList).isNotNull();
        
        // Check if there's at least one meal
        WebElement firstMeal = mealsList.findElement(By.cssSelector(".meal-card"));
        assertThat(firstMeal).isNotNull();
    }

    @Test
    void testCreateReservation() {
        // Open the home page
        driver.get("http://localhost:" + port);
        
        // Wait for restaurants to load and click on the first one
        WebElement firstRestaurant = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".restaurant"))
        );
        firstRestaurant.findElement(By.cssSelector("a.btn-primary")).click();
        
        // On the restaurant details page, wait for meals to load
        WebElement firstMeal = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".meal-card"))
        );
        
        // Click the "Book Now" button on the first meal
        WebElement bookButton = firstMeal.findElement(By.cssSelector(".btn-book"));
        bookButton.click();
        
        // Wait for the reservation confirmation
        WebElement reservationConfirmation = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector(".reservation-success"))
        );
        assertThat(reservationConfirmation).isNotNull();
        
        // Verify the reservation token is displayed
        WebElement reservationToken = reservationConfirmation.findElement(By.cssSelector(".token"));
        assertThat(reservationToken.getText()).isNotBlank();
    }
} 