package pt.ua.deti.tqs.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

public class FlightsPage {
    WebDriver driver;

    @FindBy(xpath = "//input[@value='Choose This Flight']")
    List<WebElement> chooseFlightButtons;

    public FlightsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void chooseFirstFlight() {
        if (!chooseFlightButtons.isEmpty()) {
            chooseFlightButtons.get(0).click();
        }
    }
}