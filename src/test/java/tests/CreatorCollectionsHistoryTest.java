package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorCollectionsHistoryPage;
import pages.CreatorLoginPage;
import utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorCollectionsHistoryTest extends BaseTestClass {
    private static final Logger log = LoggerFactory.getLogger(CreatorCollectionsHistoryTest.class);

    @Test(priority = 1, description = "Verify History of collections in Creator account")
    public void verifyHistoryOfCollections() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorCollectionsHistoryPage collectionsPage = new CreatorCollectionsHistoryPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Open Settings and ensure URL contains settings path
        collectionsPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Open History of collections and verify title
        collectionsPage.openHistoryOfCollections();

        // Open first collection and assert Details screen then short wait
        collectionsPage.openFirstCollection();
        collectionsPage.assertDetailsVisibleAndWait();

        // Navigate back to profile (three back clicks)
        collectionsPage.navigateBackToProfile();
    }
}
