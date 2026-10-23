package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;
import utils.DataDriven;

public class LoginTest extends BaseTest {

    @Test(description = "Verify successful login")
    public void verifySuccessfulLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(
                DataDriven.jsonReader("validUser", "username"),
                DataDriven.jsonReader("validUser", "password"));

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "User was not redirected to the Inventory page");
    }

    @Test(description = "Verify invalid login")
    public void verifyInvalidLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(
                DataDriven.jsonReader("invalidUser", "username"),
                DataDriven.jsonReader("invalidUser", "password"));

        Assert.assertTrue(
                loginPage.getErrorMessage().contains("Username and password do not match"),
                "Unexpected error message: " + loginPage.getErrorMessage());
    }

    @Test(description = "Verify login without password")
    public void verifyLoginWithoutPassword() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(
                DataDriven.jsonReader("noPassword", "username"),
                DataDriven.jsonReader("noPassword", "password"));

        Assert.assertTrue(
                loginPage.getErrorMessage().contains("Password is required"),
                "Unexpected error message: " + loginPage.getErrorMessage());
    }
}
