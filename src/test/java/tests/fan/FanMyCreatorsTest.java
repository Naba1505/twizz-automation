package tests.fan;

import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.fan.FanLoginPage;
import pages.fan.FanHomePage;
import pages.fan.FanMyCreatorsPage;
import utils.ConfigReader;

/**
 * Test class for Fan My Creators functionality.
 * Tests viewing subscribed creators and their subscription details.
 */
public class FanMyCreatorsTest extends BaseTestClass {

    @Test(priority = 1, 
          description = "Fan can view My Creators and scroll through existing creators")
    public void fanCanViewMyCreators() {
        // Arrange: credentials
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");

        // Login as Fan
        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

        // Verify on home screen
        FanHomePage home = new FanHomePage(page);
        home.assertOnHomeUrl();

        // Navigate to My Creators screen via Settings
        FanMyCreatorsPage myCreators = new FanMyCreatorsPage(page);
        myCreators.navigateToMyCreators();

        // Click See all results to load all creators
        myCreators.clickSeeAllResults();

        // Scroll to end of list (last creator avatar)
        myCreators.scrollToLastCreatorAvatar();

        // Scroll back to first creator avatar
        myCreators.scrollToFirstCreatorAvatar();

        // Verify My creators title is visible
        myCreators.verifyOnMyCreatorsScreen();

        // Navigate back to home screen
        myCreators.navigateBackToHome();

        // Verify back on home screen
        home.assertOnHomeUrl();
    }
}
