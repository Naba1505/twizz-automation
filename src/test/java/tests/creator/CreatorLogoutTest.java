package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorLogoutPage;
import utils.ConfigReader;

public class CreatorLogoutTest extends BaseTestClass {

    @Test(priority = 1, description = "Verify Logout functionality of Creator account")
    public void verifyCreatorLogout() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorLogoutPage logoutPage = new CreatorLogoutPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        logoutPage.openSettingsFromProfile();
        logoutPage.assertOnSettingsUrl();

        // Click Disconnect and assert logout to intro screen
        logoutPage.clickDisconnect();
        logoutPage.assertLoggedOutToIntro();
    }
}
