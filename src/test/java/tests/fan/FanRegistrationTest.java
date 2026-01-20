package tests.fan;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.AriaRole;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.fan.FanRegistrationPage;
import utils.ConfigReader;
import utils.DataGenerator;

public class FanRegistrationTest extends BaseTestClass {

    @Test(priority = 1, description = "Complete fan registration and verify auto-login to home")
    public void testFanRegistration() {
        FanRegistrationPage fanPage = new FanRegistrationPage(page);

        String firstName = DataGenerator.generateUniqueFirstName();
        String lastName = DataGenerator.generateUniqueLastName();
        String username = DataGenerator.generateUniqueUsername("TwizzFan");
        String email = DataGenerator.generateUniqueEmail("TwizzFan");
        String password = ConfigReader.getProperty("fan.default.password", "Yest$12j");

        fanPage.completeFanRegistrationFlow(firstName, lastName, username, email, password);

        // Assert Home icon is visible as success indicator (fan may land on /fan/home or /common/discover)
        Locator homeIcon = page.getByRole(AriaRole.IMG, new com.microsoft.playwright.Page.GetByRoleOptions().setName("Home icon"));
        homeIcon.first().waitFor(new Locator.WaitForOptions().setTimeout(20000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        Assert.assertTrue(homeIcon.first().isVisible(), "Fan did not land on home after registration - Home icon not visible. Actual URL: " + page.url());
    }
}
