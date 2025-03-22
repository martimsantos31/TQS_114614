import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

@ExtendWith(SeleniumJupiter.class)
public class HarryNewSearchTest {

    @Test
    public void harrySearch(FirefoxDriver driver) {
        driver.get("https://cover-bookstore.onrender.com/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        WebElement searchBar = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[data-testid='book-search-input']")));
        searchBar.clear();
        searchBar.sendKeys("Harry Potter");

        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[class*='Navbar_searchBtn']")));
        searchButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='book-search-item']")));

        List<WebElement> books = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.cssSelector("span[class*='SearchList_bookTitle']")));

        assertThat(books.size()).isGreaterThan(0);

        boolean found = false;
        WebElement targetBook = null;

        for (WebElement book : books) {
            if (book.getText().contains("Harry Potter and the Sorcerer's Stone")) {
                targetBook = book;
                found = true;
                break;
            }
        }

        assertThat(found).isTrue();
    }
}