package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import utils.ConfigReader;

public class LandingPageTest extends BaseTestClass {

    @Test
    public void testLandingPage() {
        String landingPageUrl = ConfigReader.getLandingPageUrl();
        landingPage.navigate();
        landingPage.waitForPageToLoad();

        Assert.assertTrue(landingPage.isTwizzLogoVisible(), "Twizz logo is not visible on the landing page.");

        landingPage.clickCreatorRegistrationButton();
        // Add assertions or verifications for creator registration page if needed

        landingPage.navigate(); // Navigate back to the landing page
        landingPage.clickFansRegistrationButton();
        // Add assertions or verifications for fans registration page if needed

        landingPage.navigate(); // Navigate back to the landing page
        landingPage.clickLoginButton();
        // Add assertions or verifications for login page if needed
    }


}