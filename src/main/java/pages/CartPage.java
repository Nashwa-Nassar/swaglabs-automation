package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for the Swag Labs cart page.
 */
public class CartPage {

    private final WebDriver driver;

    // Locators
    private final By cartLink        = By.className("shopping_cart_link");
    private final By cartItems       = By.className("cart_item");
    private final By itemNames       = By.className("inventory_item_name");
    private final By checkoutButton  = By.id("checkout");
    private final By continueShopBtn = By.id("continue-shopping");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public void openCart() {
        driver.findElement(cartLink).click();
    }

    /** Item names in the order they appear in the cart. */
    public List<String> getItemNames() {
        List<String> names = new ArrayList<>();
        for (WebElement element : driver.findElements(itemNames)) {
            names.add(element.getText().trim());
        }
        return names;
    }

    public int getItemsCount() {
        // Skip the implicit wait so an empty cart doesn't cost 10 seconds
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        try {
            return driver.findElements(cartItems).size();
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
    }

    public void removeItem(String productName) {
        driver.findElement(By.xpath("//div[contains(@class,'inventory_item_name') and normalize-space()='"
                + productName + "']/ancestor::div[@class='cart_item']//button")).click();
    }

    public void continueShopping() {
        driver.findElement(continueShopBtn).click();
    }

    public void clickCheckout() {
        driver.findElement(checkoutButton).click();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
