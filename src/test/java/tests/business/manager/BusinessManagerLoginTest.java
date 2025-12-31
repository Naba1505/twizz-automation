package tests.business.manager;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Test class for Twizz Business Manager Login
 * Tests the complete login flow for a Manager account
 */
public class BusinessManagerLoginTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerLoginTest.class);

    @Test(priority = 1, description = "Manager can login successfully")
    public void managerCanLogin() {
        logger.info("[Manager Login] Starting test: Manager login flow");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        String displayName = ConfigReader.getProperty("business.manager.displayname", "David");
        
        logger.info("[Manager Login] Using username: {}", username);
        
        // Navigate to Sign In page
        businessManagerLoginPage.navigateToSignIn();
        
        // Verify login page
        Assert.assertTrue(businessManagerLoginPage.isLoginPageVisible(), 
            "Login page 'Connection' heading is not visible");
        logger.info("[Manager Login] On login page");
        
        // Fill credentials
        businessManagerLoginPage.fillUsername(username);
        businessManagerLoginPage.fillPassword(password);
        
        // Click login
        businessManagerLoginPage.clickLogin();
        
        // Verify manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard - URL does not contain '/manager'");
        logger.info("[Manager Login] Successfully navigated to manager dashboard");
        
        // Verify welcome message
        Assert.assertTrue(businessManagerLoginPage.isWelcomeMessageVisible(displayName), 
            "Welcome message 'Hello " + displayName + "' is not visible");
        logger.info("[Manager Login] Welcome message is visible");
        
        logger.info("[Manager Login] Test completed successfully");
        logger.info("[Manager Login] Logged in as Manager: {}", username);
    }
}
