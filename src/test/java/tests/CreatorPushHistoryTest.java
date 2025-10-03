package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorPushHistoryPage;
import utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorPushHistoryTest extends BaseTestClass {
    private static final Logger log = LoggerFactory.getLogger(CreatorPushHistoryTest.class);

    @Test(priority = 1, description = "Verify History Of Media Pushes navigation and details")
    public void verifyHistoryOfMediaPushes() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPushHistoryPage historyPage = new CreatorPushHistoryPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Open Settings and ensure URL contains settings path
        historyPage.openSettingsFromProfile();
        String url = page.url();
        log.info("Settings URL after click: {}", url);
        Assert.assertTrue(url.contains("/common/setting"), "Did not land on Settings screen");

        // Open History of pushes and verify title
        historyPage.openHistoryOfPushes();

        // Open last media push entry and assert Performance screen
        historyPage.openLastMediaPushEntry();
        historyPage.assertPerformanceVisible();

        // Navigate back
        historyPage.clickBackArrow();

        // Open first media push entry and assert Performance screen again
        historyPage.openFirstMediaPushEntry();
        historyPage.assertPerformanceVisible();

        // Navigate back until profile (plus icon) is visible
        historyPage.navigateBackToProfile();
    }
}
