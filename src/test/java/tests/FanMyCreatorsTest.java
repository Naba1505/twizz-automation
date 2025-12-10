package tests;

import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.FanLoginPage;
import pages.FanHomePage;
import pages.FanMyCreatorsPage;
import utils.ConfigReader;

/**
 * Test class for Fan My Creators functionality.
 * Tests viewing subscribed creators and their subscription details.
 */
public class FanMyCreatorsTest extends BaseTestClass {

    @Test(priority = 1, 
          description = "Fan can view My Creators and subscription details")
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

        // Click on first creator to view subscription details
        myCreators.clickFirstCreatorArrow();

        // Pause to view details
        myCreators.pauseToViewDetails(2000);

        // Click Cancel to navigate back to creators list
        myCreators.clickCancelButton();

        // Click See all results to load remaining creators
        myCreators.clickSeeAllResults();

        // Scroll to end of list
        myCreators.scrollToEndOfList();

        // Click on last creator to view details
        myCreators.clickLastCreatorArrow();

        // Pause to view details
        myCreators.pauseToViewDetails(2000);

        // Click Cancel to navigate back
        myCreators.clickCancelButton();

        // Scroll to top until title/first creator visible
        myCreators.scrollToTop();

        // Verify My creators title is visible
        myCreators.verifyOnMyCreatorsScreen();

        // Navigate back to home screen (click arrow left twice)
        myCreators.navigateBackToHome();

        // Verify back on home screen
        home.assertOnHomeUrl();
    }
}
