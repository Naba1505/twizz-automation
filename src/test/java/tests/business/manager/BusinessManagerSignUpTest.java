package tests.business.manager;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for Twizz Business Manager Sign Up
 * Tests the complete registration flow for a Manager account
 */
public class BusinessManagerSignUpTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerSignUpTest.class);

    @Test(priority = 1, description = "Manager can complete full registration flow")
    public void managerCanSignUp() {
        logger.info("[Manager Sign Up] Starting test: Manager registration flow");
        
        // Navigate to Sign In page
        businessManagerSignUpPage.navigateToSignIn();
        
        // Click Sign up link
        businessManagerSignUpPage.clickSignUpLink();
        
        // Verify registration page
        Assert.assertTrue(businessManagerSignUpPage.isRegistrationPageVisible(), 
            "Registration page 'Inscription' heading is not visible");
        logger.info("[Manager Sign Up] On registration page");
        
        // Page 1: Basic Information
        logger.info("[Manager Sign Up] Filling page 1 - Basic Information");
        businessManagerSignUpPage.clickManagerTab();
        
        businessManagerSignUpPage.fillLastName("Manager");
        businessManagerSignUpPage.fillFirstName("Twizz");
        
        String username = businessManagerSignUpPage.generateUniqueUsername();
        businessManagerSignUpPage.fillUsername(username);
        
        String email = businessManagerSignUpPage.generateUniqueEmail(username);
        businessManagerSignUpPage.fillEmail(email);
        
        businessManagerSignUpPage.selectBirthDate();
        businessManagerSignUpPage.confirmBirthDate();
        businessManagerSignUpPage.clickContinuePageOne();
        
        // Page 2: Password and Agency Details
        logger.info("[Manager Sign Up] Filling page 2 - Password and Agency Details");
        businessManagerSignUpPage.fillPassword("Twizz$123");
        businessManagerSignUpPage.fillAgencyName("Twizz");
        businessManagerSignUpPage.uploadProfileImage();
        businessManagerSignUpPage.fillPhoneNumber("9998887772");
        businessManagerSignUpPage.selectGender();
        businessManagerSignUpPage.clickContinuePageTwo();
        
        // Page 3: Status Selection
        logger.info("[Manager Sign Up] Filling page 3 - Status Selection");
        Assert.assertTrue(businessManagerSignUpPage.isYourStatusPageVisible(), 
            "'Your status' heading is not visible");
        businessManagerSignUpPage.selectPrivateIndividual();
        businessManagerSignUpPage.clickContinuePageThree();
        
        // Page 4: Identity Verification
        logger.info("[Manager Sign Up] Filling page 4 - Identity Verification");
        Assert.assertTrue(businessManagerSignUpPage.isIdentityVerificationPageVisible(), 
            "'Identity verification' heading is not visible");
        businessManagerSignUpPage.uploadIdentityDocument();
        businessManagerSignUpPage.uploadSelfieDocument();
        businessManagerSignUpPage.clickContinuePageFour();
        
        // Verify success message
        Assert.assertTrue(businessManagerSignUpPage.isSuccessMessageVisible(), 
            "Success message 'Thank you for your interest!' is not visible");
        logger.info("[Manager Sign Up] Registration successful - success message displayed");
        
        // Click I understand and verify back on sign in page
        businessManagerSignUpPage.clickIUnderstand();
        Assert.assertTrue(businessManagerSignUpPage.isBackOnSignInPage(), 
            "'Connection' heading is not visible - not back on sign in page");
        logger.info("[Manager Sign Up] Back on sign in page - registration flow completed");
        
        logger.info("[Manager Sign Up] Test completed successfully");
        logger.info("[Manager Sign Up] Registered Manager - Username: {}, Email: {}", username, email);
    }
}
