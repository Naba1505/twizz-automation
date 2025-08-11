package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import utils.ConfigReader;

public class CreatorLoginTest extends BaseTestClass {

    @Test(priority = 1, description = "Verify creator can login and lands on profile with handle visible")
    public void testCreatorLogin() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        String expectedHandle = ConfigReader.getProperty("creator.handle", "@john_smith");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);

        // Act
        loginPage.navigate();
        // Ensure we are on the login screen (Twizz logo and Login text visible)
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Assert
        Assert.assertTrue(loginPage.isHandleVisible(expectedHandle),
                "Expected handle not visible after login: " + expectedHandle);
    }
}
