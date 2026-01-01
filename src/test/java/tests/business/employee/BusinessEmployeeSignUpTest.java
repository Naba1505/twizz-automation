package tests.business.employee;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for Twizz Business Employee Sign Up
 * Tests the complete registration flow for an Employee account
 */
public class BusinessEmployeeSignUpTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessEmployeeSignUpTest.class);

    @Test(priority = 1, description = "Employee can complete full registration flow")
    public void employeeCanSignUp() {
        logger.info("[Employee Sign Up] Starting test: Employee registration flow");
        
        // Navigate to Sign In page
        businessEmployeeSignUpPage.navigateToSignIn();
        
        // Click Sign up link
        businessEmployeeSignUpPage.clickSignUpLink();
        
        // Verify registration page
        Assert.assertTrue(businessEmployeeSignUpPage.isRegistrationPageVisible(), 
            "Registration page 'Inscription' heading is not visible");
        logger.info("[Employee Sign Up] On registration page");
        
        // Verify Employee tab is selected by default
        Assert.assertTrue(businessEmployeeSignUpPage.isEmployeeTabSelected(), 
            "'Selected Employee' button is not visible - Employee tab not selected by default");
        logger.info("[Employee Sign Up] Employee tab is selected by default");
        
        // Page 1: Basic Information
        logger.info("[Employee Sign Up] Filling page 1 - Basic Information");
        businessEmployeeSignUpPage.fillLastName("Employee");
        businessEmployeeSignUpPage.fillFirstName("Twizz");
        
        String username = businessEmployeeSignUpPage.generateUniqueUsername();
        businessEmployeeSignUpPage.fillUsername(username);
        
        String email = businessEmployeeSignUpPage.generateUniqueEmail(username);
        businessEmployeeSignUpPage.fillEmail(email);
        
        businessEmployeeSignUpPage.selectBirthDate();
        businessEmployeeSignUpPage.confirmBirthDate();
        businessEmployeeSignUpPage.clickContinuePageOne();
        
        // Page 2: Password and Phone Details
        logger.info("[Employee Sign Up] Filling page 2 - Password and Phone Details");
        businessEmployeeSignUpPage.fillPassword("Twizz$123");
        businessEmployeeSignUpPage.fillPhoneNumber("9998881117");
        businessEmployeeSignUpPage.selectGender();
        businessEmployeeSignUpPage.clickContinuePageTwo();
        
        // Verify employee dashboard
        Assert.assertTrue(businessEmployeeSignUpPage.isOnEmployeeDashboard(), 
            "Not on employee dashboard - URL does not contain '/employee'");
        logger.info("[Employee Sign Up] Successfully navigated to employee dashboard");
        
        // Verify agency avatar is visible
        Assert.assertTrue(businessEmployeeSignUpPage.isAgencyAvatarVisible(), 
            "Agency avatar is not visible on employee dashboard");
        logger.info("[Employee Sign Up] Agency avatar is visible on dashboard");
        
        logger.info("[Employee Sign Up] Test completed successfully");
        logger.info("[Employee Sign Up] Registered Employee - Username: {}, Email: {}", username, email);
    }
}
