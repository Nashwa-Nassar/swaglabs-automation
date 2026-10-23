package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the Swag Labs inventory (products) page.
 */
public class InventoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators
    private final By cartIcon     = By.id("shopping_cart_container");
    private final By productItems = By.className("inventory_item");
    private final By menuButton   = By.id("react-burger-menu-btn");
    private final By logoutLink   = By.id("logout_sidebar_link");

    // Footer social icons
    public static final By LINKEDIN = By.cssSelector(".social_linkedin a");
    public static final By FACEBOOK = By.cssSelector(".social_facebook a");
    public static final By TWITTER  = By.cssSelector(".social_x a");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ---------- Part 1 ----------

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isCartIconDisplayed() {
        return driver.findElement(cartIcon).isDisplayed();
    }

    public int getProductsCount() {
        return driver.findElements(productItems).size();
    }

    // ---------- Part 2 ----------

    /** Finds a product card by its visible name. */
    private By productCard(String productName) {
        return By.xpath("//div[contains(@class,'inventory_item_name') and normalize-space()='"
                + productName + "']/ancestor::div[@class='inventory_item']");
    }

    private WebElement actionButton(String productName) {
        return driver.findElement(productCard(productName)).findElement(By.tagName("button"));
    }

    public void addToCart(String productName) {
        actionButton(productName).click();
    }

    /** Returns the button text for a product: "Add to cart" or "Remove". */
    public String getButtonText(String productName) {
        return actionButton(productName).getText().trim();
    }

    /** Reads a price such as "$29.99" and converts it to 29.99. */
    public double getProductPrice(String productName) {
        String text = driver.findElement(productCard(productName))
                .findElement(By.className("inventory_item_price")).getText();
        return Double.parseDouble(text.replace("$", "").trim());
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
    }

    /**
     * Clicks a footer social icon (opens in a new tab), waits for the new tab's URL to
     * contain the expected text, returns that URL, then closes the tab and goes back.
     */
    public String clickSocialAndGetUrl(By icon, String expectedUrlPart) {
        String originalWindow = driver.getWindowHandle();
        List<String> before = new ArrayList<>(driver.getWindowHandles());

        driver.findElement(icon).click();
        wait.until(ExpectedConditions.numberOfWindowsToBe(before.size() + 1));

        for (String handle : driver.getWindowHandles()) {
            if (!before.contains(handle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        try {
            wait.until(ExpectedConditions.urlContains(expectedUrlPart));
        } catch (TimeoutException ignored) {
            // fall through: the test's assertion will report the actual URL
        }
        String url = driver.getCurrentUrl();

        driver.close();
        driver.switchTo().window(originalWindow);
        return url;
    }
}
