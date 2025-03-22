package pt.ua.deti.tqs.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class HomePage {
    WebDriver driver;

    @FindBy(name = "fromPort")
    WebElement fromPortDropdown;

    @FindBy(name = "toPort")
    WebElement toPortDropdown;

    @FindBy(xpath = "//input[@value='Find Flights']")
    WebElement findFlightsButton;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void selectFromPort(String city) {
        new Select(fromPortDropdown).selectByVisibleText(city);
    }

    public void selectToPort(String city) {
        new Select(toPortDropdown).selectByVisibleText(city);
    }

    public void clickFindFlights() {
        findFlightsButton.click();
    }
}