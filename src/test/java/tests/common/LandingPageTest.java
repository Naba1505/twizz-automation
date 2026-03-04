package tests.common;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;

public class LandingPageTest extends BaseTestClass {

    @Test(priority = 1, description = "Verify that the landing page is accessible and elements are visible,clickable and accessible")
    public void testLandingPage() {
        landingPage.navigate();
        landingPage.waitForPageToLoad();
        Assert.assertTrue(landingPage.isTwizzLogoVisible(), "Twizz logo is not visible on the landing page.");
        
        // Test Creator Registration button
        landingPage.clickCreatorRegistrationButton();
        Assert.assertTrue(landingPage.isOnCreatorRegistrationPage(), "Failed to navigate to Creator Registration page");
        
        // Test Fans Registration button
        landingPage.navigate();
        landingPage.clickFansRegistrationButton();
        Assert.assertTrue(landingPage.isOnFanRegistrationPage(), "Failed to navigate to Fan Registration page");
        
        // Test Login button
        landingPage.navigate();
        landingPage.clickLoginButton();
        Assert.assertTrue(landingPage.isOnLoginPage(), "Failed to navigate to Login page");
    }

}
