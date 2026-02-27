package tests.business.manager;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Test class for Twizz Business Manager Delete Employee (Cleanup)
 * This test should run after all other employee tests to clean up test data
 */
public class BusinessManagerDeleteEmployeeTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerDeleteEmployeeTest.class);

    @Test(priority = 1, description = "Manager can delete employee from agency (Cleanup)")
    public void managerCanDeleteEmployee() {
        logger.info("[Manager Delete Employee] Starting test: Delete employee cleanup flow");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        
        logger.info("[Manager Delete Employee] Using manager username: {}", username);
        
        // Login as Manager
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Delete Employee] Successfully logged in as Manager");
        
        // Click on Agency icon
        businessManagerDeleteEmployeePage.clickAgencyIcon();
        
        // Verify agency content element is visible
        Assert.assertTrue(businessManagerDeleteEmployeePage.isAgencyContentElementVisible(), 
            "Agency content element is not visible");
        logger.info("[Manager Delete Employee] Agency content element is visible");
        
        // Click on employee info element
        businessManagerDeleteEmployeePage.clickEmployeeInfo();
        
        // Verify 'Twizz identity Card' heading
        Assert.assertTrue(businessManagerDeleteEmployeePage.isTwizzIdentityCardHeadingVisible(), 
            "'Twizz identity Card' heading is not visible");
        logger.info("[Manager Delete Employee] On employee details ID screen");
        
        // Click on 'Delete this account' text
        businessManagerDeleteEmployeePage.clickDeleteAccountText();
        
        // Verify delete confirmation dialog
        Assert.assertTrue(businessManagerDeleteEmployeePage.isDeleteConfirmationDialogVisible(), 
            "Delete confirmation dialog is not visible");
        logger.info("[Manager Delete Employee] Delete confirmation dialog displayed");
        
        // Click Validate button to confirm deletion
        businessManagerDeleteEmployeePage.clickValidateButton();
        
        // Verify employee deleted success message
        Assert.assertTrue(businessManagerDeleteEmployeePage.isEmployeeDeletedSuccessMessageVisible(), 
            "'Employee deleted successfully' message is not visible");
        logger.info("[Manager Delete Employee] Employee deleted successfully");
        
        logger.info("[Manager Delete Employee] Test completed successfully - cleanup done");
    }
}
