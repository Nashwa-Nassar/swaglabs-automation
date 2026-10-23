package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;
import utils.DataDriven;

public class InventoryTest extends BaseTest {

    private InventoryPage inventoryPage;

    // Runs after BaseTest.setUp() because subclass @BeforeMethod runs after the parent's
    @BeforeMethod
    public void loginFirst() {
        new LoginPage(driver).login(
                DataDriven.jsonReader("validUser", "username"),
                DataDriven.jsonReader("validUser", "password"));
        inventoryPage = new InventoryPage(driver);
    }

    @Test(description = "Verify inventory page elements after login")
    public void verifyInventoryPageElements() {
        Assert.assertEquals(inventoryPage.getPageTitle(), "Swag Labs",
                "Page title is incorrect");
        Assert.assertTrue(inventoryPage.isCartIconDisplayed(),
                "Cart icon is not displayed");
        Assert.assertEquals(inventoryPage.getProductsCount(), 6,
                "Expected 6 products on the Inventory page");
    }
}
