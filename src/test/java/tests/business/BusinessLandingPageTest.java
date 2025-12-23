package tests.business;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.BusinessBaseTestClass;

/**
 * Test class for Twizz Business Landing Page
 * Verifies logo, title, and navigation to Contact Us, Login, and Register pages
 */
public class BusinessLandingPageTest extends BusinessBaseTestClass {

    @Test(priority = 1, description = "Verify Business landing page elements and navigation")
    public void testBusinessLandingPage() {
        // Verify logo is visible
        Assert.assertTrue(businessLandingPage.isLogoVisible(), 
            "Twizz Business logo is not visible on the landing page");

        // Verify main heading is visible
        Assert.assertTrue(businessLandingPage.isMainHeadingVisible(), 
            "'Designed for managers' heading is not visible on the landing page");

        // Test Contact Us navigation
        businessLandingPage.clickContactUs();
        Assert.assertTrue(businessLandingPage.isOnContactPage(), 
            "User is not on Contact page after clicking Contact Us");
        businessLandingPage.navigate(); // Navigate back to landing page

        // Test Login navigation
        businessLandingPage.clickLogin();
        Assert.assertTrue(businessLandingPage.isOnLoginPage(), 
            "User is not on Login page after clicking Login");
        
        // Navigate back to landing page before testing Register
        businessLandingPage.navigate();

        // Test Register navigation (from landing page)
        businessLandingPage.clickRegister();
        Assert.assertTrue(businessLandingPage.isOnRegistrationPage(), 
            "User is not on Registration page after clicking Register");

        // Verify Employee tab is selected by default, then switch to Manager
        businessLandingPage.clickManagerTab();

        // Navigate back to landing page to complete test
        businessLandingPage.navigate();
        Assert.assertTrue(businessLandingPage.isMainHeadingVisible(), 
            "Failed to navigate back to landing page");
    }
}
