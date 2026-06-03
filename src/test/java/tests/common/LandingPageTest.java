package tests.common;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;

public class LandingPageTest extends BaseTestClass {

    @Test(priority = 1, description = "Verify landing page loads and logo is visible")
    public void testLandingPageLoad() {
        landingPage.navigate();
        landingPage.waitForPageToLoad();
        Assert.assertTrue(landingPage.isTwizzLogoVisible(), "Twizz logo is not visible on the landing page.");
    }

    @Test(priority = 2, description = "Verify Creator Registration navigation", dependsOnMethods = "testLandingPageLoad")
    public void testCreatorRegistrationNavigation() {
        landingPage.clickCreatorRegistrationButton();
        Assert.assertTrue(landingPage.isOnCreatorRegistrationPage(), "Failed to navigate to Creator Registration page");
    }

    @Test(priority = 3, description = "Verify Fans Registration navigation", dependsOnMethods = "testLandingPageLoad")
    public void testFansRegistrationNavigation() {
        landingPage.navigate();
        landingPage.clickFansRegistrationButton();
        Assert.assertTrue(landingPage.isOnFanRegistrationPage(), "Failed to navigate to Fan Registration page");
    }

    @Test(priority = 4, description = "Verify Login navigation", dependsOnMethods = "testLandingPageLoad")
    public void testLoginNavigation() {
        landingPage.navigate();
        landingPage.clickLoginButton();
        Assert.assertTrue(landingPage.isOnLoginPage(), "Failed to navigate to Login page");
    }
}
