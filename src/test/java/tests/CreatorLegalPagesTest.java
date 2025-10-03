package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLegalPages;
import pages.CreatorLoginPage;
import utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorLegalPagesTest extends BaseTestClass {
    private static final Logger log = LoggerFactory.getLogger(CreatorLegalPagesTest.class);

    @Test(priority = 1, description = "Verify Terms & Conditions of Sale and Community Regulations screens")
    public void verifyLegalPages() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorLegalPages legal = new CreatorLegalPages(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Open Settings and ensure URL contains settings path
        legal.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Terms and conditions of sale
        legal.openTermsAndConditionsOfSale();
        legal.assertOnSaleTermsPage();
        legal.scrollDownToSaleBottomAndBackToTitle();
        legal.clickBackArrow();

        // Community regulations
        legal.openCommunityRegulations();
        legal.assertOnCommunityRegulationsPage();
        legal.scrollDownToCommunityBottomAndBackToTitle();
        legal.clickBackArrow();
    }
}
