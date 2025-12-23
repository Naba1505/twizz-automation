package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorLogoutPage;
import utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorLogoutTest extends BaseTestClass {
    private static final Logger log = LoggerFactory.getLogger(CreatorLogoutTest.class);

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

        // Open Settings and ensure URL contains settings path
        logoutPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Click Disconnect and assert logout to intro screen
        logoutPage.clickDisconnect();
        logoutPage.assertLoggedOutToIntro();
    }
}
