package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorHelpAndContactPage;
import pages.creator.CreatorLoginPage;
import utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorHelpAndContactTest extends BaseTestClass {
    private static final Logger log = LoggerFactory.getLogger(CreatorHelpAndContactTest.class);

    @Test(priority = 1, description = "Verify Help and Contact form submission")
    public void verifyHelpAndContact() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorHelpAndContactPage helpPage = new CreatorHelpAndContactPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Open Settings and ensure URL contains settings path
        helpPage.openSettingsFromProfile();
        String url = page.url();
        log.info("Settings URL after click: {}", url);
        Assert.assertTrue(url.contains("/common/setting"), "Did not land on Settings screen");

        // Open Help and contact and verify title
        helpPage.openHelpAndContact();

        // Fill Subject and Message
        helpPage.fillSubject("QA Test");
        helpPage.fillMessage("QA Automation Message");

        // Send and assert success toast
        helpPage.clickSend();
        helpPage.assertSuccessToastVisible();
    }
}
