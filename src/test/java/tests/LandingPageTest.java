package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;

public class LandingPageTest extends BaseTestClass {

    @Test(priority = 1, description = "Verify that the landing page is accessible and elements are visible,clickable and accessible")
    public void testLandingPage() {
        landingPage.navigate();
        landingPage.waitForPageToLoad();
        Assert.assertTrue(landingPage.isTwizzLogoVisible(), "Twizz logo is not visible on the landing page.");
        landingPage.clickCreatorRegistrationButton();
        landingPage.navigate();
        landingPage.clickFansRegistrationButton();
        landingPage.navigate();
        landingPage.clickLoginButton();
    }

}