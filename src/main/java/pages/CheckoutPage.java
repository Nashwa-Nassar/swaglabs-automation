package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for checkout step one (your information) and step two (overview).
 */
public class CheckoutPage {

    private final WebDriver driver;

    // Locators
    private final By firstName   = By.id("first-name");
    private final By lastName    = By.id("last-name");
    private final By postalCode  = By.id("postal-code");
    private final By continueBtn = By.id("continue");
    private final By itemTotal   = By.className("summary_subtotal_label");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }

    public void fillInformationAndContinue(String first, String last, String zip) {
        driver.findElement(firstName).sendKeys(first);
        driver.findElement(lastName).sendKeys(last);
        driver.findElement(postalCode).sendKeys(zip);
        driver.findElement(continueBtn).click();
    }

    /** Reads "Item total: $55.97" and returns 55.97. */
    public double getItemTotal() {
        String text = driver.findElement(itemTotal).getText();
        return Double.parseDouble(text.substring(text.indexOf('$') + 1).trim());
    }
}
