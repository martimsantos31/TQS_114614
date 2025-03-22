package pt.ua.deti.tqs.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class BookingPage {
    WebDriver driver;

    @FindBy(id = "inputName")
    WebElement nameField;

    @FindBy(id = "address")
    WebElement addressField;

    @FindBy(id = "city")
    WebElement cityField;

    @FindBy(id = "state")
    WebElement stateField;

    @FindBy(id = "zipCode")
    WebElement zipCodeField;

    @FindBy(xpath = "//input[@value='Purchase Flight']")
    WebElement purchaseButton;

    public BookingPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void fillBookingForm(String name, String address, String city, String state, String zip) {
        nameField.sendKeys(name);
        addressField.sendKeys(address);
        cityField.sendKeys(city);
        stateField.sendKeys(state);
        zipCodeField.sendKeys(zip);
    }

    public void clickPurchaseFlight() {
        purchaseButton.click();
    }
}