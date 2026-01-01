package tests.business.manager;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Test class for Twizz Business Manager Add Creator (Invite)
 * Tests the complete flow of inviting a creator to the agency
 */
public class BusinessManagerAddCreatorTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerAddCreatorTest.class);

    @Test(priority = 1, description = "Manager can invite a creator to agency")
    public void managerCanInviteCreator() {
        logger.info("[Manager Add Creator] Starting test: Manager invite creator flow");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        String creatorUsername = ConfigReader.getProperty("creator.handle", "@john_smith").replace("@", "");
        
        logger.info("[Manager Add Creator] Using manager username: {}", username);
        logger.info("[Manager Add Creator] Inviting creator: {}", creatorUsername);
        
        // Login as Manager
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Add Creator] Successfully logged in as Manager");
        
        // Click on Agency icon
        businessManagerAddCreatorPage.clickAgencyIcon();
        
        // Verify 'Your agency' title
        Assert.assertTrue(businessManagerAddCreatorPage.isYourAgencyTitleVisible(), 
            "'Your agency' title is not visible");
        logger.info("[Manager Add Creator] On Agency screen");
        
        // Verify 'Your creators' message
        Assert.assertTrue(businessManagerAddCreatorPage.isYourCreatorsMessageVisible(), 
            "'Your creators' message is not visible");
        
        // Click Add button
        businessManagerAddCreatorPage.clickAddButton();
        
        // Verify 'Invite a creator' heading
        Assert.assertTrue(businessManagerAddCreatorPage.isInviteCreatorHeadingVisible(), 
            "'Invite a creator' heading is not visible");
        logger.info("[Manager Add Creator] On Invite Creator screen");
        
        // Verify username instruction
        Assert.assertTrue(businessManagerAddCreatorPage.isUsernameInstructionVisible(), 
            "Username instruction text is not visible");
        
        // Search for creator
        businessManagerAddCreatorPage.searchCreatorByUsername(creatorUsername);
        
        // Select creator checkbox
        businessManagerAddCreatorPage.selectCreatorCheckbox();
        
        // Send invitation
        businessManagerAddCreatorPage.clickSendInvitation();
        
        // Verify invitation success message
        Assert.assertTrue(businessManagerAddCreatorPage.isInvitationSuccessMessageVisible(), 
            "Invitation success message is not visible");
        logger.info("[Manager Add Creator] Invitation sent successfully");
        
        // Click I understand if present
        businessManagerAddCreatorPage.clickIUnderstandButtonIfPresent();
        
        logger.info("[Manager Add Creator] Test completed successfully");
        logger.info("[Manager Add Creator] Invited creator: {}", creatorUsername);
    }

    @Test(priority = 2, description = "Manager sees duplicate invitation message when inviting same creator again")
    public void managerSeesDuplicateInvitationMessage() {
        logger.info("[Manager Add Creator] Starting test: Duplicate invitation flow");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        String creatorUsername = ConfigReader.getProperty("creator.handle", "@john_smith").replace("@", "");
        
        logger.info("[Manager Add Creator] Using manager username: {}", username);
        logger.info("[Manager Add Creator] Attempting to invite same creator: {}", creatorUsername);
        
        // Login as Manager
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Add Creator] Successfully logged in as Manager");
        
        // Click on Agency icon
        businessManagerAddCreatorPage.clickAgencyIcon();
        
        // Verify 'Your agency' title
        Assert.assertTrue(businessManagerAddCreatorPage.isYourAgencyTitleVisible(), 
            "'Your agency' title is not visible");
        logger.info("[Manager Add Creator] On Agency screen");
        
        // Click Add button
        businessManagerAddCreatorPage.clickAddButton();
        
        // Verify 'Invite a creator' heading
        Assert.assertTrue(businessManagerAddCreatorPage.isInviteCreatorHeadingVisible(), 
            "'Invite a creator' heading is not visible");
        logger.info("[Manager Add Creator] On Invite Creator screen");
        
        // Search for creator
        businessManagerAddCreatorPage.searchCreatorByUsername(creatorUsername);
        
        // Select creator checkbox
        businessManagerAddCreatorPage.selectCreatorCheckbox();
        
        // Send invitation
        businessManagerAddCreatorPage.clickSendInvitation();
        
        // Verify duplicate invitation message
        Assert.assertTrue(businessManagerAddCreatorPage.isDuplicateInvitationMessageVisible(), 
            "'there is an invitation' message is not visible");
        logger.info("[Manager Add Creator] Duplicate invitation message displayed successfully");
        
        logger.info("[Manager Add Creator] Test completed successfully");
        logger.info("[Manager Add Creator] Verified duplicate invitation for creator: {}", creatorUsername);
    }

    @Test(priority = 3, description = "Creator can reject manager's invitation")
    public void creatorCanRejectInvitation() {
        logger.info("[Creator Reject Invitation] Starting test: Creator reject invitation flow");
        
        // Get creator credentials from config
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        
        logger.info("[Creator Reject Invitation] Using creator username: {}", username);
        
        // Navigate to creator login page
        creatorLoginPage.navigate();
        
        // Login as Creator
        creatorLoginPage.login(username, password);
        
        // Verify on creator profile by checking URL
        String currentUrl = page.url();
        Assert.assertTrue(currentUrl.contains("/creator"), 
            "Not on creator profile. Current URL: " + currentUrl);
        logger.info("[Creator Reject Invitation] Successfully logged in as Creator");
        
        // Click on settings icon
        creatorManagerPage.clickSettingsIcon();
        
        // Click on Manager menu item
        creatorManagerPage.clickManagerMenuItem();
        
        // Verify Manager heading
        Assert.assertTrue(creatorManagerPage.isManagerHeadingVisible(), 
            "'Manager' heading is not visible");
        logger.info("[Creator Reject Invitation] On Manager screen");
        
        // Verify Invitation text
        Assert.assertTrue(creatorManagerPage.isInvitationTextVisible(), 
            "'Invitation' text is not visible");
        logger.info("[Creator Reject Invitation] Invitation is visible");
        
        // Click Refuse button
        creatorManagerPage.clickRefuseButton();
        
        // Verify confirmation dialog
        Assert.assertTrue(creatorManagerPage.isConfirmationDialogVisible(), 
            "Confirmation dialog is not visible");
        logger.info("[Creator Reject Invitation] Confirmation dialog displayed");
        
        // Click I refuse button
        creatorManagerPage.clickIRefuseButton();
        
        // Verify invitation rejected message
        Assert.assertTrue(creatorManagerPage.isInvitationRejectedMessageVisible(), 
            "'Invitation rejected' message is not visible");
        logger.info("[Creator Reject Invitation] Invitation rejected successfully");
        
        logger.info("[Creator Reject Invitation] Test completed successfully");
    }
}
