package tests.fan;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.fan.FanRegistrationPage;
import utils.ConfigReader;
import utils.DataGenerator;
import java.util.regex.Pattern;

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

        // Assert we land on fan home URL instead of relying on LIVE or header text
        page.waitForURL(Pattern.compile(".*/fan/home.*"), new Page.WaitForURLOptions().setTimeout(15000));
        Assert.assertTrue(page.url().contains("/fan/home"), "Fan did not land on /fan/home after registration. Actual: " + page.url());
    }
}
