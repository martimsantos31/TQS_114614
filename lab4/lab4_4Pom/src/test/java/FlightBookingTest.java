import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import pt.ua.deti.tqs.pages.HomePage;
import pt.ua.deti.tqs.pages.FlightsPage;
import pt.ua.deti.tqs.pages.BookingPage;

import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FlightBookingTest {
    WebDriver driver;

    @BeforeAll
    public void setupAll() {

    }

    @BeforeEach
    public void setup() {
        driver = new FirefoxDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("Testar a reserva de um voo no BlazeDemo")
    public void bookAFlight() {
        HomePage home = new HomePage(driver);
        driver.get("https://blazedemo.com/");

        home.selectFromPort("Boston");
        home.selectToPort("London");
        home.clickFindFlights();

        FlightsPage flightsPage = new FlightsPage(driver);
        flightsPage.chooseFirstFlight();

        BookingPage bookingPage = new BookingPage(driver);

        bookingPage.fillBookingForm("John Doe", "123 Main St", "New York", "NY", "10001");
        bookingPage.clickPurchaseFlight();

        String confirmationText = driver.getPageSource();
        Assertions.assertTrue(confirmationText.contains("Thank you for your purchase"),
                "A confirmação de reserva não apareceu!");
    }

    @AfterEach
    public void close() {
        driver.quit();
    }

    @AfterAll
    public void teardownAll() {

    }
}