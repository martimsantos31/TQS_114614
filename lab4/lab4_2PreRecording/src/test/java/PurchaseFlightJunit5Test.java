import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.firefox.FirefoxDriver;

@ExtendWith(SeleniumJupiter.class)
public class PurchaseFlightJunit5Test {

    @Test
    public void purchaseFlight(FirefoxDriver driver) {
        driver.get("https://blazedemo.com/");
        driver.manage().window().setSize(new Dimension(1512, 944));
        driver.findElement(By.cssSelector(".btn-primary")).click();
        driver.findElement(By.cssSelector("tr:nth-child(2) .btn")).click();
        driver.findElement(By.id("inputName")).sendKeys("dwjoiajd");
        driver.findElement(By.id("address")).sendKeys("lidawjodja");
        driver.findElement(By.id("city")).sendKeys("dlwaijdpa");
        driver.findElement(By.id("state")).sendKeys("waijdlawk");
        driver.findElement(By.id("zipCode")).sendKeys("123456");
        driver.findElement(By.cssSelector("option:nth-child(1)")).click();
        driver.findElement(By.id("creditCardNumber")).sendKeys("234567897654");
        driver.findElement(By.id("nameOnCard")).sendKeys("odjiasijdpa");
        driver.findElement(By.id("rememberMe")).click();
        driver.findElement(By.cssSelector(".btn-primary")).click();

        assertEquals("Thank you for your purchase today!", driver.findElement(By.cssSelector("h1")).getText());
        assertEquals("BlazeDemo Confirmation", driver.getTitle());
    }
}