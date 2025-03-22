package pt.ua.deti.tqs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class OnlineLibrarySteps {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setup() {
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Given("I am on the library homepage")
    public void i_am_on_the_library_homepage() {
        driver.get("https://cover-bookstore.onrender.com/");
    }

    @When("I search for {string}")
    public void i_search_for(String bookTitle) {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[data-testid='book-search-input']")));
        searchBox.clear();
        searchBox.sendKeys(bookTitle);

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[class*='Navbar_searchBtn']")));
        searchButton.click();
    }

    @Then("I should see the book {string} in the results")
    public void i_should_see_the_book_in_the_results(String expectedTitle) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='book-search-item']")));

        List<WebElement> results = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("span[class*='SearchList_bookTitle']")));
        Assertions.assertTrue(results.stream().anyMatch(e -> e.getText().equalsIgnoreCase(expectedTitle)), "Book not found in search results!");
    }

    @When("I search for books by author {string}")
    public void i_search_for_books_by_author(String author) {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[data-testid='book-search-input']")));
        searchBox.clear();
        searchBox.sendKeys(author);

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[class*='Navbar_searchBtn']")));
        searchButton.click();
    }

    @Then("I should see {int} books by {string} in the results")
    public void i_should_see_books_by_in_the_results(int expectedCount, String author) {
        List<WebElement> results = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("span[class*='SearchList_bookAuthor']")));
        for (WebElement book : results) {
            System.out.println("- " + book.getText());
        }
        long count = results.stream().filter(e -> e.getText().contains(author)).count();

        Assertions.assertEquals(expectedCount, count, "Incorrect number of books found for author: " + author);
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}