package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.FanRegistrationPage;
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

        Assert.assertTrue(fanPage.isHomeVisibleForUser(username),
                "Home should display 'For " + username + "' after fan registration");
    }
}
