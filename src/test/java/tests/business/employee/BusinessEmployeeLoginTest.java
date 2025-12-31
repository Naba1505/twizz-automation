package tests.business.employee;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Test class for Twizz Business Employee Login
 * Tests the complete login flow for an Employee account
 */
public class BusinessEmployeeLoginTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessEmployeeLoginTest.class);

    @Test(priority = 1, description = "Employee can login successfully")
    public void employeeCanLogin() {
        logger.info("[Employee Login] Starting test: Employee login flow");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.employee.username", "TwizzEmployee@proton.me");
        String password = ConfigReader.getProperty("business.employee.password", "Twizz$123");
        String displayName = ConfigReader.getProperty("business.employee.displayname", "Scarlett");
        
        logger.info("[Employee Login] Using username: {}", username);
        
        // Navigate to Sign In page
        businessEmployeeLoginPage.navigateToSignIn();
        
        // Verify login page
        Assert.assertTrue(businessEmployeeLoginPage.isLoginPageVisible(), 
            "Login page 'Connection' heading is not visible");
        logger.info("[Employee Login] On login page");
        
        // Fill credentials
        businessEmployeeLoginPage.fillUsername(username);
        businessEmployeeLoginPage.fillPassword(password);
        
        // Click login
        businessEmployeeLoginPage.clickLogin();
        
        // Verify employee dashboard
        Assert.assertTrue(businessEmployeeLoginPage.isOnEmployeeDashboard(), 
            "Not on employee dashboard - URL does not contain '/employee'");
        logger.info("[Employee Login] Successfully navigated to employee dashboard");
        
        // Verify welcome message
        Assert.assertTrue(businessEmployeeLoginPage.isWelcomeMessageVisible(displayName), 
            "Welcome message 'Hello " + displayName + "' is not visible");
        logger.info("[Employee Login] Welcome message is visible");
        
        logger.info("[Employee Login] Test completed successfully");
        logger.info("[Employee Login] Logged in as Employee: {}", username);
    }
}
