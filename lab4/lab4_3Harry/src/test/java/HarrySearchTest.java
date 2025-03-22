
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
public class HarrySearchTest {

  @Test
  public void harrySearch(FirefoxDriver driver) {
    driver.get("https://cover-bookstore.onrender.com/");

    WebElement searchBar = driver.findElement(By.xpath("//input[@data-testid='book-search-input']"));
    searchBar.sendKeys("Harry Potter");

    WebElement searchButton = driver.findElement(By.xpath("//button[contains(@class, 'Navbar_searchBtn')]"));
    searchButton.click();


    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@data-testid='book-search-item']")));

    List<WebElement> books = driver.findElements(By.xpath("//span[contains(@class, 'SearchList_bookTitle')]"));

    boolean found = false;

      for (WebElement book : books) {
          if (book.getText().contains("Harry Potter and the Sorcerer's Stone")) {
              book.click();
              found = true;
              break;
          }
      }

    assertThat(found).isTrue();
  }
}