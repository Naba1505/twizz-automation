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
        Assert.assertTrue(businessManagerAddCreatorPage.isAgencyUrlLoaded(), 
            "Agency URL is not loaded");
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
        Assert.assertTrue(businessManagerAddCreatorPage.isAgencyUrlLoaded(), 
            "Agency URL is not loaded");
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

    @Test(priority = 4, description = "Manager can invite creator from settings screen")
    public void managerCanInviteCreatorFromSettings() {
        logger.info("[Manager Invite from Settings] Starting test: Invite creator from settings screen");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        String creatorUsername = ConfigReader.getProperty("creator.handle", "@john_smith").replace("@", "");
        
        logger.info("[Manager Invite from Settings] Using manager username: {}", username);
        logger.info("[Manager Invite from Settings] Inviting creator: {}", creatorUsername);
        
        // Login as Manager and land on dashboard
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Invite from Settings] Successfully logged in as Manager");
        
        // Click on Settings icon
        businessManagerSettingsPage.clickSettingsIcon();
        logger.info("[Manager Invite from Settings] Clicked on Settings icon");
        
        // Click on Creator Go button
        businessManagerSettingsPage.clickCreatorGoButton();
        
        // Verify 'Your creators' text is visible
        Assert.assertTrue(businessManagerSettingsPage.isYourCreatorsTextVisible(), 
            "'Your creators' text is not visible");
        logger.info("[Manager Invite from Settings] On invite/add creator screen");
        
        // Click on 'Invite a creator' text
        businessManagerSettingsPage.clickInviteCreatorText();
        
        // Verify 'Invite a creator' heading
        Assert.assertTrue(businessManagerSettingsPage.isInviteCreatorHeadingVisible(), 
            "'Invite a creator' heading is not visible");
        logger.info("[Manager Invite from Settings] On Invite Creator page");
        
        // Verify username instruction text
        Assert.assertTrue(businessManagerSettingsPage.isUsernameInstructionVisible(), 
            "Username instruction text is not visible");
        
        // Search for creator
        businessManagerSettingsPage.searchCreatorByUsername(creatorUsername);
        
        // Select creator checkbox
        businessManagerSettingsPage.selectCreatorCheckbox();
        
        // Send invitation
        businessManagerSettingsPage.clickSendInvitationButton();
        
        // Verify invitation sent message
        Assert.assertTrue(businessManagerSettingsPage.isInvitationSentMessageVisible(), 
            "'Invitation sent' message is not visible");
        logger.info("[Manager Invite from Settings] Invitation sent successfully");
        
        // Click I understand button
        businessManagerSettingsPage.clickIUnderstandButton();
        
        logger.info("[Manager Invite from Settings] Test completed successfully");
        logger.info("[Manager Invite from Settings] Invited creator: {}", creatorUsername);
    }

    @Test(priority = 5, description = "Manager sees duplicate invitation message when inviting same creator from settings")
    public void managerSeesDuplicateInvitationFromSettings() {
        logger.info("[Manager Duplicate from Settings] Starting test: Duplicate invitation from settings screen");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        String creatorUsername = ConfigReader.getProperty("creator.handle", "@john_smith").replace("@", "");
        
        logger.info("[Manager Duplicate from Settings] Using manager username: {}", username);
        logger.info("[Manager Duplicate from Settings] Attempting to invite same creator: {}", creatorUsername);
        
        // Login as Manager and land on dashboard
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Duplicate from Settings] Successfully logged in as Manager");
        
        // Click on Settings icon
        businessManagerSettingsPage.clickSettingsIcon();
        
        // Click on Creator Go button
        businessManagerSettingsPage.clickCreatorGoButton();
        
        // Verify 'Your creators' text is visible
        Assert.assertTrue(businessManagerSettingsPage.isYourCreatorsTextVisible(), 
            "'Your creators' text is not visible");
        logger.info("[Manager Duplicate from Settings] On invite/add creator screen");
        
        // Click on 'Invite a creator' text
        businessManagerSettingsPage.clickInviteCreatorText();
        
        // Verify 'Invite a creator' heading
        Assert.assertTrue(businessManagerSettingsPage.isInviteCreatorHeadingVisible(), 
            "'Invite a creator' heading is not visible");
        logger.info("[Manager Duplicate from Settings] On Invite Creator page");
        
        // Search for creator
        businessManagerSettingsPage.searchCreatorByUsername(creatorUsername);
        
        // Select creator checkbox
        businessManagerSettingsPage.selectCreatorCheckbox();
        
        // Send invitation
        businessManagerSettingsPage.clickSendInvitationButton();
        
        // Verify duplicate invitation message
        Assert.assertTrue(businessManagerSettingsPage.isDuplicateInvitationMessageVisible(), 
            "'there is an invitation' message is not visible");
        logger.info("[Manager Duplicate from Settings] Duplicate invitation message displayed successfully");
        
        logger.info("[Manager Duplicate from Settings] Test completed successfully");
        logger.info("[Manager Duplicate from Settings] Verified duplicate invitation for creator: {}", creatorUsername);
    }

    @Test(priority = 6, description = "Creator can accept manager's invitation")
    public void creatorCanAcceptInvitation() {
        logger.info("[Creator Accept Invitation] Starting test: Creator accept invitation flow");
        
        // Get creator credentials from config
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        
        logger.info("[Creator Accept Invitation] Using creator username: {}", username);
        
        // Navigate to creator login page
        creatorLoginPage.navigate();
        
        // Login as Creator
        creatorLoginPage.login(username, password);
        
        // Verify on creator profile by checking URL
        String currentUrl = page.url();
        Assert.assertTrue(currentUrl.contains("/creator"), 
            "Not on creator profile. Current URL: " + currentUrl);
        logger.info("[Creator Accept Invitation] Successfully logged in as Creator");
        
        // Click on settings icon
        creatorManagerPage.clickSettingsIcon();
        
        // Click on Manager menu item
        creatorManagerPage.clickManagerMenuItem();
        
        // Verify Manager heading
        Assert.assertTrue(creatorManagerPage.isManagerHeadingVisible(), 
            "'Manager' heading is not visible");
        logger.info("[Creator Accept Invitation] On Manager screen");
        
        // Verify Invitation text
        Assert.assertTrue(creatorManagerPage.isInvitationTextVisible(), 
            "'Invitation' text is not visible");
        logger.info("[Creator Accept Invitation] Invitation is visible");
        
        // Click Accept button
        creatorManagerPage.clickAcceptButton();
        
        // Verify confirmation dialog
        Assert.assertTrue(creatorManagerPage.isConfirmationDialogVisible(), 
            "Confirmation dialog is not visible");
        logger.info("[Creator Accept Invitation] Confirmation dialog displayed");
        
        // Click I accept button
        creatorManagerPage.clickIAcceptButton();
        
        // Verify invitation accepted message
        Assert.assertTrue(creatorManagerPage.isInvitationAcceptedMessageVisible(), 
            "'Invitation accepted' message is not visible");
        logger.info("[Creator Accept Invitation] Invitation accepted successfully");
        
        logger.info("[Creator Accept Invitation] Test completed successfully");
    }

    @Test(priority = 7, description = "Manager can view added creator in agency screen")
    public void managerCanViewAddedCreator() {
        logger.info("[Manager View Creator] Starting test: View added creator in agency screen");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        
        logger.info("[Manager View Creator] Using manager username: {}", username);
        
        // Login as Manager and land on dashboard
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager View Creator] Successfully logged in as Manager");
        
        // Click on Agency icon
        businessManagerAddCreatorPage.clickAgencyIcon();
        
        // Verify 'Your agency' title
        Assert.assertTrue(businessManagerAddCreatorPage.isAgencyUrlLoaded(), 
            "Agency URL is not loaded");
        logger.info("[Manager View Creator] On Agency screen");
        
        // Verify agency content element is visible
        Assert.assertTrue(businessManagerAddCreatorPage.isAgencyContentElementVisible(), 
            "Agency content element is not visible");
        logger.info("[Manager View Creator] Agency content element is visible");
        
        // Click on creator info element
        businessManagerAddCreatorPage.clickCreatorInfo();
        
        // Verify 'Twizz identity Card' heading
        Assert.assertTrue(businessManagerAddCreatorPage.isTwizzIdentityCardHeadingVisible(), 
            "'Twizz identity Card' heading is not visible");
        logger.info("[Manager View Creator] Creator details displayed - creator is added and visible");
        
        logger.info("[Manager View Creator] Test completed successfully");
    }
}
