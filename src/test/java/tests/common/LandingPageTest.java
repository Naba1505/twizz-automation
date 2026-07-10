package tests.common;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;

public class LandingPageTest extends BaseTestClass {

    @Test(priority = 1, description = "Verify landing page loads and logo is visible")
    public void testLandingPageLoad() {
        Assert.assertTrue(landingPage.isTwizzLogoVisible(), "Twizz logo is not visible on the landing page.");
    }

    @Test(priority = 2, description = "Verify Fan Registration navigation via Become a fan button", dependsOnMethods = "testLandingPageLoad")
    public void testFanRegistrationNavigation() {
        landingPage.clickBecomeAFanButton();
        Assert.assertTrue(landingPage.isFanTabVisible(), "Fan tab is not visible on registration page");
        Assert.assertTrue(landingPage.isFanRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=fan");
    }

    @Test(priority = 3, description = "Verify Creator Registration navigation via Become a creator button", dependsOnMethods = "testLandingPageLoad")
    public void testCreatorRegistrationNavigation() {
        landingPage.clickBecomeACreatorButton();
        Assert.assertTrue(landingPage.isCreatorTabVisible(), "Creator tab is not visible on registration page");
        Assert.assertTrue(landingPage.isCreatorRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=creator");
    }

    @Test(priority = 4, description = "Verify Contact Us navigation and page content", dependsOnMethods = "testLandingPageLoad")
    public void testContactUsNavigation() {
        landingPage.clickContactUsButton();
        Assert.assertTrue(landingPage.isContactUsTextVisible(), "Contact us text 'We have offices and teams' is not visible");
        Assert.assertTrue(landingPage.isContactUsUrlCorrect(), "URL does not contain /contact");
    }

    @Test(priority = 5, description = "Verify Login navigation via Login button", dependsOnMethods = "testLandingPageLoad")
    public void testLoginNavigation() {
        landingPage.clickLoginButton();
        Assert.assertTrue(landingPage.isLoginTextVisible(), "Login text is not visible on login page");
        Assert.assertTrue(landingPage.isLoginUrlCorrect(), "URL does not contain /auth/signIn");
    }

    @Test(priority = 6, description = "Verify Register navigation via Register button", dependsOnMethods = "testLandingPageLoad")
    public void testRegisterNavigation() {
        landingPage.clickRegisterButton();
        Assert.assertTrue(landingPage.isRegistrationTextVisible(), "Registration text is not visible on sign up page");
        Assert.assertTrue(landingPage.isSignUpUrlCorrect(), "URL does not contain /auth/signUp");
    }

    @Test(priority = 7, description = "Verify Login navigation via second Login button (scroll to visible)", dependsOnMethods = "testLandingPageLoad")
    public void testSecondLoginButtonNavigation() {
        landingPage.clickSecondLoginButton();
        Assert.assertTrue(landingPage.isLoginTextVisible(), "Login text is not visible on login page");
        Assert.assertTrue(landingPage.isLoginUrlCorrect(), "URL does not contain /auth/signIn");
    }

    @Test(priority = 8, description = "Verify Fans button navigates to public discover and Home icon shows register prompt", dependsOnMethods = "testLandingPageLoad")
    public void testFansPublicDiscoverNavigation() {
        landingPage.clickFansButton();
        Assert.assertTrue(landingPage.isPublicDiscoverUrlCorrect(), "URL does not contain /public-discover");
        Assert.assertTrue(landingPage.isHomeIconVisible(), "Home icon is not visible on public discover page");
        landingPage.clickHomeIcon();
        Assert.assertTrue(landingPage.isRegisterToSeeContentVisible(), "'Register to see the content' text is not visible");
    }

    @Test(priority = 9, description = "Verify Discover TWIZZ button navigates to public discover and Search icon shows register prompt", dependsOnMethods = "testLandingPageLoad")
    public void testDiscoverTwizzNavigation() {
        landingPage.clickDiscoverTwizzButton();
        Assert.assertTrue(landingPage.isPublicDiscoverUrlCorrect(), "URL does not contain /public-discover");
        Assert.assertTrue(landingPage.isSearchIconVisible(), "Search icon is not visible on public discover page");
        landingPage.clickSearchIcon();
        Assert.assertTrue(landingPage.isRegisterToSeeContentVisible(), "'Register to see the content' text is not visible");
    }

}
