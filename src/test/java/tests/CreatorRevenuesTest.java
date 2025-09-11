package tests;

import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorRevenuesPage;
import utils.ConfigReader;

public class CreatorRevenuesTest extends BaseTestClass {

    @Test(priority = 1, description = "Creator can view Revenues and info popovers")
    public void creatorCanViewRevenues() {
        // Arrange credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Revenues flow
        CreatorRevenuesPage revenues = new CreatorRevenuesPage(page);
        revenues.openRevenues();
        revenues.assertRevenuesScreen();
        revenues.checkValidatedInfo();
        revenues.checkWaitingInfo();
    }
}
