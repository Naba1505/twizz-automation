package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorUnlockHistoryPage;
import utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorUnlockHistoryTest extends BaseTestClass {
    private static final Logger log = LoggerFactory.getLogger(CreatorUnlockHistoryTest.class);

    @Test(priority = 1, description = "Verify Unlock History navigation and details")
    public void verifyUnlockHistory() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorUnlockHistoryPage unlockPage = new CreatorUnlockHistoryPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Open Settings and ensure URL contains settings path
        unlockPage.openSettingsFromProfile();
        String url = page.url();
        log.info("Settings URL after click: {}", url);
        Assert.assertTrue(url.contains("/common/setting"), "Did not land on Settings screen");

        // Open Unlock history and verify title
        unlockPage.openUnlockHistory();

        // Open last unlock entry and assert Details screen
        unlockPage.openLastUnlockEntry();
        unlockPage.assertDetailsVisible();

        // Navigate back
        unlockPage.clickBackArrow();

        // Open first unlock entry and assert Details screen again
        unlockPage.openFirstUnlockEntry();
        unlockPage.assertDetailsVisible();

        // Navigate back until profile (plus icon) is visible
        unlockPage.navigateBackToProfile();
    }
}
