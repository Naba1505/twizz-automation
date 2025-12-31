package tests.business.common;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for Twizz Business Landing Page
 * Verifies logo, title, and navigation to Contact Us, Login, and Register pages
 */
public class BusinessLandingPageTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessLandingPageTest.class);

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

    @Test(priority = 2, description = "Verify Business landing page footer links navigation")
    public void testBusinessLandingPageFooterLinks() {
        // Scroll to bottom of page using Payment Methods element
        businessLandingPage.scrollToPaymentMethods();
        
        // Test Contact Us link
        businessLandingPage.clickContactUsFooter();
        Assert.assertTrue(businessLandingPage.isEmailVisible(), 
            "creators@twizz.email is not visible on Contact page");
        businessLandingPage.navigate(); // Navigate back to landing page
        
        // Test Legal notices link
        businessLandingPage.clickLegalNotices();
        businessLandingPage.scrollToSiteHostedByText();
        businessLandingPage.clickTwizzLink();
        businessLandingPage.navigate(); // Navigate back to landing page
        
        // Test Content Policy link
        businessLandingPage.clickContentPolicy();
        businessLandingPage.scrollToTwizzUsersText();
        businessLandingPage.clickTwizzLink();
        businessLandingPage.navigate(); // Navigate back to landing page
        
        // Test Confidentiality link
        businessLandingPage.clickConfidentiality();
        businessLandingPage.scrollToPrivacyPolicyText();
        businessLandingPage.clickTwizzLink();
        businessLandingPage.navigate(); // Navigate back to landing page
        
        // Test General Conditions of Sale link
        businessLandingPage.clickGeneralConditionsOfSale();
        businessLandingPage.scrollToCreatorUndertakesText();
        businessLandingPage.clickTwizzLink();
        businessLandingPage.navigate(); // Navigate back to landing page
        
        // Test General Conditions of Use link
        businessLandingPage.clickGeneralConditionsOfUse();
        businessLandingPage.scrollToTwizzRecommendsText();
        businessLandingPage.clickTwizzLink();
        businessLandingPage.navigate(); // Navigate back to landing page
        
        // Test Blog link
        businessLandingPage.clickBlog();
        businessLandingPage.scrollToTwizzBlogLink();
    }

    @Test(priority = 3, description = "Verify Business landing page language switching")
    public void testBusinessLandingPageLanguageSwitch() {
        // Switch to French
        businessLandingPage.switchToFrench();
        Assert.assertTrue(businessLandingPage.isFrenchHeadingVisible(), 
            "French heading 'Conçu pour les manageurs' is not visible after language switch");
        
        // Switch to Spanish
        businessLandingPage.switchToSpanish();
        Assert.assertTrue(businessLandingPage.isSpanishHeadingVisible(), 
            "Spanish heading 'Diseñado para los managers' is not visible after language switch");
        
        // Switch back to English
        businessLandingPage.switchToEnglish();
        Assert.assertTrue(businessLandingPage.isMainHeadingVisible(), 
            "English heading 'Designed for managers' is not visible after language switch");
        
        logger.info("Language switch successful");
    }
}
