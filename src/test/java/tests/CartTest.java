package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutPage;
import pages.InventoryPage;
import pages.LoginPage;
import utils.DataDriven;

import java.util.List;

public class CartTest extends BaseTest {

    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;

    private String username;
    private String password;

    // Runs after BaseTest.setUp(): every cart test starts logged in
    @BeforeMethod
    public void loginFirst() {
        username = DataDriven.jsonReader("validUser", "username");
        password = DataDriven.jsonReader("validUser", "password");

        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        cartPage = new CartPage(driver);
        checkoutPage = new CheckoutPage(driver);

        loginPage.login(username, password);
    }

    /** Adds every product from the JSON "cartProducts" list, in order. */
    private void addAllJsonProducts() {
        for (String product : DataDriven.jsonArrayReader("cartProducts")) {
            inventoryPage.addToCart(product);
        }
    }

    // ---------- 1. Social links ----------
    @Test(description = "Verify social links")
    public void verifySocialLinks() {
        Assert.assertTrue(
                inventoryPage.clickSocialAndGetUrl(InventoryPage.LINKEDIN, "linkedin").contains("linkedin"),
                "LinkedIn URL is incorrect");
        Assert.assertTrue(
                inventoryPage.clickSocialAndGetUrl(InventoryPage.FACEBOOK, "facebook").contains("facebook"),
                "Facebook URL is incorrect");
        Assert.assertTrue(
                inventoryPage.clickSocialAndGetUrl(InventoryPage.TWITTER, "x.com").contains("x.com"),
                "X (Twitter) URL is incorrect");
    }

    // ---------- 2. Empty cart ----------
    @Test(description = "Verify cart is empty")
    public void verifyCartIsEmpty() {
        cartPage.openCart();
        Assert.assertEquals(cartPage.getItemsCount(), 0, "Cart should be empty");
    }

    // ---------- 3. Add 3 products (data-driven) ----------
    @Test(description = "Add 3 specific products and verify order")
    public void verifyAddThreeProducts() {
        List<String> expected = DataDriven.jsonArrayReader("cartProducts");
        addAllJsonProducts();

        cartPage.openCart();
        // List assertEquals checks order as well as content
        Assert.assertEquals(cartPage.getItemNames(), expected,
                "Cart items differ from the added products (or are in a different order)");
    }

    // ---------- 4. Remove one product ----------
    @Test(description = "Remove one product and verify inventory button states")
    public void verifyRemoveOneProduct() {
        List<String> products = DataDriven.jsonArrayReader("cartProducts");
        String toRemove = DataDriven.jsonReader("productToRemove");

        addAllJsonProducts();
        cartPage.openCart();
        cartPage.removeItem(toRemove);
        cartPage.continueShopping();

        Assert.assertEquals(inventoryPage.getButtonText(toRemove), "Add to cart",
                "Removed product should show 'Add to cart'");
        for (String product : products) {
            if (!product.equals(toRemove)) {
                Assert.assertEquals(inventoryPage.getButtonText(product), "Remove",
                        product + " should still show 'Remove'");
            }
        }
    }

    // ---------- 5. Cart total price ----------
    @Test(description = "Verify checkout Item Total equals the sum of inventory prices")
    public void verifyCartTotalPrice() {
        List<String> products = DataDriven.jsonArrayReader("cartProducts");

        // Read prices from the inventory page BEFORE adding to the cart
        double expectedTotal = 0;
        for (String product : products) {
            expectedTotal += inventoryPage.getProductPrice(product);
        }

        addAllJsonProducts();
        cartPage.openCart();
        cartPage.clickCheckout();
        checkoutPage.fillInformationAndContinue(
                DataDriven.jsonReader("checkoutInfo", "firstName"),
                DataDriven.jsonReader("checkoutInfo", "lastName"),
                DataDriven.jsonReader("checkoutInfo", "postalCode"));

        // Small delta because of double rounding
        Assert.assertEquals(checkoutPage.getItemTotal(), expectedTotal, 0.001,
                "Item Total at checkout does not match the sum of the inventory prices");
    }

    // ---------- 6. Checkout with an empty cart ----------
    @Test(description = "Checkout with an empty cart")
    public void verifyCheckoutWithEmptyCart() {
        cartPage.openCart();
        Assert.assertEquals(cartPage.getItemsCount(), 0, "Precondition failed: cart is not empty");

        cartPage.clickCheckout();

        // Observed behavior of saucedemo.com: it does NOT block checkout and moves to step one.
        // (This is a known site quirk/bug.) Re-verify manually and adjust if the site changes.
        Assert.assertTrue(cartPage.getCurrentUrl().contains("checkout-step-one.html"),
                "Expected to reach checkout step one, but URL was: " + cartPage.getCurrentUrl());
    }

    // ---------- 7. Cart state after logout/login ----------
    @Test(description = "Cart state after logout and login")
    public void verifyCartAfterLogoutLogin() {
        List<String> products = DataDriven.jsonArrayReader("cartProducts");
        List<String> added = products.subList(0, 2);
        for (String product : added) {
            inventoryPage.addToCart(product);
        }

        inventoryPage.logout();
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory"), "Logout did not work");

        loginPage.login(username, password);
        cartPage.openCart();

        // Observed behavior of saucedemo.com: the cart is kept in the browser's local storage,
        // so the items are still there after logging back in. Re-verify manually.
        Assert.assertEquals(cartPage.getItemNames(), added,
                "Cart items after logout/login differ from what was added");
    }
}
