package tests.business.manager;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Test class for Twizz Business Manager Delete Creator (Cleanup)
 * This test should run after all other creator tests to clean up test data
 */
public class BusinessManagerDeleteCreatorTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerDeleteCreatorTest.class);

    @Test(priority = 1, description = "Manager can delete creator from agency (Cleanup)")
    public void managerCanDeleteCreator() {
        logger.info("[Manager Delete Creator] Starting test: Delete creator cleanup flow");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        
        logger.info("[Manager Delete Creator] Using manager username: {}", username);
        
        // Login as Manager
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Delete Creator] Successfully logged in as Manager");
        
        // Click on Agency icon
        businessManagerDeleteCreatorPage.clickAgencyIcon();
        
        // Verify agency content element is visible
        Assert.assertTrue(businessManagerDeleteCreatorPage.isAgencyContentElementVisible(), 
            "Agency content element is not visible");
        logger.info("[Manager Delete Creator] Agency content element is visible");
        
        // Click on creator info element
        businessManagerDeleteCreatorPage.clickCreatorInfo();
        
        // Verify 'Twizz identity Card' heading
        Assert.assertTrue(businessManagerDeleteCreatorPage.isTwizzIdentityCardHeadingVisible(), 
            "'Twizz identity Card' heading is not visible");
        logger.info("[Manager Delete Creator] On creator details ID screen");
        
        // Click on 'Delete the creator' text
        businessManagerDeleteCreatorPage.clickDeleteCreatorText();
        
        // Verify delete confirmation dialog
        Assert.assertTrue(businessManagerDeleteCreatorPage.isDeleteConfirmationDialogVisible(), 
            "Delete confirmation dialog is not visible");
        logger.info("[Manager Delete Creator] Delete confirmation dialog displayed");
        
        // Click Validate button to confirm deletion
        businessManagerDeleteCreatorPage.clickValidateButton();
        
        // Verify creator deleted success message
        Assert.assertTrue(businessManagerDeleteCreatorPage.isCreatorDeletedSuccessMessageVisible(), 
            "'Creator deleted successfully' message is not visible");
        logger.info("[Manager Delete Creator] Creator deleted successfully");
        
        logger.info("[Manager Delete Creator] Test completed successfully - cleanup done");
    }
}
