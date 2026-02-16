package tests.fan;

import org.testng.annotations.Test;
import pages.fan.FanHomePage;
import pages.fan.FanMyCreatorsPage;

/**
 * Test class for Fan My Creators functionality.
 * Tests viewing subscribed creators and their subscription details.
 */
public class FanMyCreatorsTest extends BaseFanTest {

    @Test(priority = 1, 
          description = "Fan can view My Creators and scroll through existing creators")
    public void fanCanViewMyCreators() {
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
