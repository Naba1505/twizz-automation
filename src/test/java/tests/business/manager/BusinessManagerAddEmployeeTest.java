package tests.business.manager;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Test class for Business Manager Add Employee (Invite)
 * Tests the complete flow of manager inviting an employee to the agency
 */
public class BusinessManagerAddEmployeeTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerAddEmployeeTest.class);

    @Test(priority = 1, description = "Manager can invite an employee to agency")
    public void managerCanInviteEmployee() {
        logger.info("[Manager Add Employee] Starting test: Manager invite employee flow");
        
        // Get manager and employee credentials from config
        String managerUsername = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String managerPassword = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        
        // Use the username from config or default to "scarlett"
        String employeeSearchName = ConfigReader.getProperty("business.employee.searchname", "scarlett");
        
        logger.info("[Manager Add Employee] Using manager username: {}", managerUsername);
        logger.info("[Manager Add Employee] Inviting employee: {}", employeeSearchName);
        
        // Login as Manager
        businessManagerLoginPage.navigateToSignIn();
        businessManagerLoginPage.fillUsername(managerUsername);
        businessManagerLoginPage.fillPassword(managerPassword);
        businessManagerLoginPage.clickLogin();
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Add Employee] Successfully logged in as Manager");
        
        // Click on Agency icon
        businessManagerAddEmployeePage.clickAgencyIcon();
        
        // Verify on Agency screen
        Assert.assertTrue(businessManagerAddEmployeePage.isAgencyUrlLoaded(), 
            "Agency URL is not loaded");
        logger.info("[Manager Add Employee] On Agency screen");
        
        // Verify 'Your employees' message
        Assert.assertTrue(businessManagerAddEmployeePage.isYourEmployeesMessageVisible(), 
            "'Your employees' message is not visible");
        logger.info("[Manager Add Employee] 'Your employees' section visible");
        
        // Click Add button to invite employee
        businessManagerAddEmployeePage.clickAddButton();
        
        // Verify on Invite Employee screen
        Assert.assertTrue(businessManagerAddEmployeePage.isInviteEmployeeHeadingVisible(), 
            "'Invite an employee' heading is not visible");
        logger.info("[Manager Add Employee] On Invite Employee screen");
        
        // Verify username instruction
        Assert.assertTrue(businessManagerAddEmployeePage.isUsernameInstructionVisible(), 
            "Username instruction is not visible");
        
        // Search for employee
        businessManagerAddEmployeePage.searchEmployeeByUsername(employeeSearchName);
        
        // Select employee checkbox
        businessManagerAddEmployeePage.selectEmployeeCheckbox();
        
        // Send invitation
        businessManagerAddEmployeePage.clickSendInvitation();
        
        // Verify invitation sent message
        Assert.assertTrue(businessManagerAddEmployeePage.isInvitationSuccessMessageVisible(), 
            "Invitation success message is not visible");
        logger.info("[Manager Add Employee] Invitation sent successfully");
        
        // Click I understand button if present
        businessManagerAddEmployeePage.clickIUnderstandButtonIfPresent();
        
        logger.info("[Manager Add Employee] Test completed successfully");
        logger.info("[Manager Add Employee] Invited employee: {}", employeeSearchName);
    }

    @Test(priority = 2, description = "Manager sees duplicate invitation message when inviting same employee again")
    public void managerSeesDuplicateInvitationMessage() {
        logger.info("[Manager Add Employee] Starting test: Duplicate invitation flow");
        
        // Get manager and employee credentials from config
        String managerUsername = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String managerPassword = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        String employeeSearchName = ConfigReader.getProperty("business.employee.searchname", "scarlett");
        
        logger.info("[Manager Add Employee] Using manager username: {}", managerUsername);
        logger.info("[Manager Add Employee] Attempting to invite same employee: {}", employeeSearchName);
        
        // Login as Manager
        businessManagerLoginPage.navigateToSignIn();
        businessManagerLoginPage.fillUsername(managerUsername);
        businessManagerLoginPage.fillPassword(managerPassword);
        businessManagerLoginPage.clickLogin();
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Add Employee] Successfully logged in as Manager");
        
        // Click on Agency icon
        businessManagerAddEmployeePage.clickAgencyIcon();
        
        // Verify on Agency screen
        Assert.assertTrue(businessManagerAddEmployeePage.isAgencyUrlLoaded(), 
            "Agency URL is not loaded");
        logger.info("[Manager Add Employee] On Agency screen");
        
        // Click Add button to invite employee
        businessManagerAddEmployeePage.clickAddButton();
        
        // Verify on Invite Employee screen
        Assert.assertTrue(businessManagerAddEmployeePage.isInviteEmployeeHeadingVisible(), 
            "'Invite an employee' heading is not visible");
        logger.info("[Manager Add Employee] On Invite Employee screen");
        
        // Search for employee
        businessManagerAddEmployeePage.searchEmployeeByUsername(employeeSearchName);
        
        // Select employee checkbox
        businessManagerAddEmployeePage.selectEmployeeCheckbox();
        
        // Send invitation
        businessManagerAddEmployeePage.clickSendInvitation();
        
        // Verify duplicate invitation message
        Assert.assertTrue(businessManagerAddEmployeePage.isDuplicateInvitationMessageVisible(), 
            "'there is an invitation' message is not visible");
        logger.info("[Manager Add Employee] Duplicate invitation message displayed successfully");
        
        logger.info("[Manager Add Employee] Test completed successfully");
        logger.info("[Manager Add Employee] Verified duplicate invitation for employee: {}", employeeSearchName);
    }

    @Test(priority = 3, description = "Employee can reject manager's invitation")
    public void employeeCanRejectInvitation() {
        logger.info("[Employee Reject Invitation] Starting test: Employee reject invitation flow");
        
        // Get employee credentials from config
        String username = ConfigReader.getProperty("business.employee.username", "TwizzEmployee@proton.me");
        String password = ConfigReader.getProperty("business.employee.password", "Twizz$123");
        
        logger.info("[Employee Reject Invitation] Using employee username: {}", username);
        
        // Set viewport to smaller size for mobile view (to show invitation option)
        page.setViewportSize(375, 667);
        logger.info("[Employee Reject Invitation] Set viewport to mobile size: 375x667");
        
        // Login as Employee
        businessEmployeeLoginPage.navigateToSignIn();
        businessEmployeeLoginPage.fillUsername(username);
        businessEmployeeLoginPage.fillPassword(password);
        businessEmployeeLoginPage.clickLogin();
        
        // Verify on employee dashboard
        Assert.assertTrue(businessEmployeeLoginPage.isOnEmployeeDashboard(), 
            "Not on employee dashboard");
        logger.info("[Employee Reject Invitation] Successfully logged in as Employee");
        
        // Click on Settings button
        employeeSettingsPage.clickSettingsButton();
        
        // Verify Manage relationships heading
        Assert.assertTrue(employeeSettingsPage.isManageRelationshipsHeadingVisible(), 
            "'Manage your relationships' heading is not visible");
        logger.info("[Employee Reject Invitation] On Settings screen");
        
        // Click View invitations button
        employeeSettingsPage.clickViewInvitationsButton();
        
        // Verify Invitation manager heading
        Assert.assertTrue(employeeSettingsPage.isInvitationManagerHeadingVisible(), 
            "'Invitation manageur' heading is not visible");
        logger.info("[Employee Reject Invitation] On Invitation screen");
        
        // Click Decline button
        employeeSettingsPage.clickDeclineButton();
        
        // Verify confirmation dialog
        Assert.assertTrue(employeeSettingsPage.isConfirmationDialogVisible(), 
            "Confirmation dialog is not visible");
        logger.info("[Employee Reject Invitation] Confirmation dialog displayed");
        
        // Click Finish button
        employeeSettingsPage.clickFinishButton();
        
        // Verify rejected message
        Assert.assertTrue(employeeSettingsPage.isRejectedMessageVisible(), 
            "'Rejected' message is not visible");
        logger.info("[Employee Reject Invitation] Invitation rejected successfully");
        
        logger.info("[Employee Reject Invitation] Test completed successfully");
    }

    @Test(priority = 4, description = "Manager can invite employee from settings screen")
    public void managerCanInviteEmployeeFromSettings() {
        logger.info("[Manager Invite from Settings] Starting test: Invite employee from settings screen");
        
        // Get manager credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        String employeeSearchName = ConfigReader.getProperty("business.employee.searchname", "scarlett");
        
        logger.info("[Manager Invite from Settings] Using manager username: {}", username);
        logger.info("[Manager Invite from Settings] Inviting employee: {}", employeeSearchName);
        
        // Login as Manager
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Invite from Settings] Successfully logged in as Manager");
        
        // Click on Settings icon
        businessManagerSettingsPage.clickSettingsIcon();
        logger.info("[Manager Invite from Settings] Clicked on Settings icon");
        
        // Click on 'Employee Go' button
        businessManagerSettingsPage.clickEmployeeGoButton();
        
        // Verify 'Your employees' text
        Assert.assertTrue(businessManagerSettingsPage.isYourEmployeesTextVisible(), 
            "'Your employees' text is not visible");
        logger.info("[Manager Invite from Settings] On invite/add employee screen");
        
        // Click 'Invite a employee' text
        businessManagerSettingsPage.clickInviteEmployeeText();
        
        // Verify 'Invite an employee' heading
        Assert.assertTrue(businessManagerSettingsPage.isInviteEmployeeHeadingVisible(), 
            "'Invite an employee' heading is not visible");
        logger.info("[Manager Invite from Settings] On Invite Employee page");
        
        // Search for employee by username
        businessManagerSettingsPage.searchEmployeeByUsername(employeeSearchName);
        
        // Select employee checkbox
        businessManagerSettingsPage.selectEmployeeCheckbox();
        
        // Click Send invitation button
        businessManagerSettingsPage.clickSendInvitationButton();
        
        // Verify invitation sent message
        Assert.assertTrue(businessManagerSettingsPage.isInvitationSentMessageVisible(), 
            "'Invitation sent' message is not visible");
        logger.info("[Manager Invite from Settings] Invitation sent successfully");
        
        // Click I understand button
        businessManagerSettingsPage.clickIUnderstandButton();
        
        logger.info("[Manager Invite from Settings] Test completed successfully");
        logger.info("[Manager Invite from Settings] Invited employee: {}", employeeSearchName);
    }

    @Test(priority = 5, description = "Manager sees duplicate invitation message from settings screen")
    public void managerSeesDuplicateInvitationFromSettings() {
        logger.info("[Manager Duplicate from Settings] Starting test: Duplicate invitation from settings screen");
        
        // Get manager credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        String employeeSearchName = ConfigReader.getProperty("business.employee.searchname", "scarlett");
        
        logger.info("[Manager Duplicate from Settings] Using manager username: {}", username);
        logger.info("[Manager Duplicate from Settings] Attempting to invite same employee: {}", employeeSearchName);
        
        // Login as Manager
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Duplicate from Settings] Successfully logged in as Manager");
        
        // Click on Settings icon
        businessManagerSettingsPage.clickSettingsIcon();
        
        // Click on 'Employee Go' button
        businessManagerSettingsPage.clickEmployeeGoButton();
        
        // Verify 'Your employees' text
        Assert.assertTrue(businessManagerSettingsPage.isYourEmployeesTextVisible(), 
            "'Your employees' text is not visible");
        logger.info("[Manager Duplicate from Settings] On invite/add employee screen");
        
        // Click 'Invite a employee' text
        businessManagerSettingsPage.clickInviteEmployeeText();
        
        // Verify 'Invite an employee' heading
        Assert.assertTrue(businessManagerSettingsPage.isInviteEmployeeHeadingVisible(), 
            "'Invite an employee' heading is not visible");
        logger.info("[Manager Duplicate from Settings] On Invite Employee page");
        
        // Search for employee by username
        businessManagerSettingsPage.searchEmployeeByUsername(employeeSearchName);
        
        // Select employee checkbox
        businessManagerSettingsPage.selectEmployeeCheckbox();
        
        // Click Send invitation button
        businessManagerSettingsPage.clickSendInvitationButton();
        
        // Verify duplicate invitation message
        Assert.assertTrue(businessManagerSettingsPage.isDuplicateInvitationMessageVisible(), 
            "'there is an invitation' message is not visible");
        logger.info("[Manager Duplicate from Settings] Duplicate invitation message displayed successfully");
        
        logger.info("[Manager Duplicate from Settings] Test completed successfully");
        logger.info("[Manager Duplicate from Settings] Verified duplicate invitation for employee: {}", employeeSearchName);
    }

    @Test(priority = 6, description = "Employee can accept manager's invitation")
    public void employeeCanAcceptInvitation() {
        logger.info("[Employee Accept Invitation] Starting test: Employee accept invitation flow");
        
        // Get employee credentials from config
        String username = ConfigReader.getProperty("business.employee.username", "TwizzEmployee@proton.me");
        String password = ConfigReader.getProperty("business.employee.password", "Twizz$123");
        
        logger.info("[Employee Accept Invitation] Using employee username: {}", username);
        
        // Set viewport to smaller size for mobile view (to show invitation option)
        page.setViewportSize(375, 667);
        logger.info("[Employee Accept Invitation] Set viewport to mobile size: 375x667");
        
        // Login as Employee
        businessEmployeeLoginPage.navigateToSignIn();
        businessEmployeeLoginPage.fillUsername(username);
        businessEmployeeLoginPage.fillPassword(password);
        businessEmployeeLoginPage.clickLogin();
        
        // Verify on employee dashboard
        Assert.assertTrue(businessEmployeeLoginPage.isOnEmployeeDashboard(), 
            "Not on employee dashboard");
        logger.info("[Employee Accept Invitation] Successfully logged in as Employee");
        
        // Click on Settings button
        employeeSettingsPage.clickSettingsButton();
        
        // Verify Manage relationships heading
        Assert.assertTrue(employeeSettingsPage.isManageRelationshipsHeadingVisible(), 
            "'Manage your relationships' heading is not visible");
        logger.info("[Employee Accept Invitation] On Settings screen");
        
        // Click View invitations button
        employeeSettingsPage.clickViewInvitationsButton();
        
        // Verify Invitation manager heading
        Assert.assertTrue(employeeSettingsPage.isInvitationManagerHeadingVisible(), 
            "'Invitation manageur' heading is not visible");
        logger.info("[Employee Accept Invitation] On Invitation screen");
        
        // Click Accept button
        employeeSettingsPage.clickAcceptButton();
        
        // Verify confirmation dialog
        Assert.assertTrue(employeeSettingsPage.isConfirmationDialogVisible(), 
            "Confirmation dialog is not visible");
        logger.info("[Employee Accept Invitation] Confirmation dialog displayed");
        
        // Click Finish button
        employeeSettingsPage.clickFinishButton();
        
        // Verify invitation accepted message
        Assert.assertTrue(employeeSettingsPage.isInvitationAcceptedMessageVisible(), 
            "'Invitation accepted' message is not visible");
        logger.info("[Employee Accept Invitation] Invitation accepted successfully");
        
        logger.info("[Employee Accept Invitation] Test completed successfully");
    }

    @Test(priority = 7, description = "Manager can view added employee in agency screen")
    public void managerCanViewAddedEmployee() {
        logger.info("[Manager View Employee] Starting test: View added employee in agency screen");
        
        // Get manager credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        
        logger.info("[Manager View Employee] Using manager username: {}", username);
        
        // Login as Manager
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager View Employee] Successfully logged in as Manager");
        
        // Click on Agency icon
        businessManagerAddEmployeePage.clickAgencyIcon();
        
        // Verify 'Your agency' title
        Assert.assertTrue(businessManagerAddEmployeePage.isAgencyUrlLoaded(), 
            "Agency URL is not loaded");
        logger.info("[Manager View Employee] On Agency screen");
        
        // Verify 'Your employees' section
        Assert.assertTrue(businessManagerAddEmployeePage.isYourEmployeesMessageVisible(), 
            "'Your employees' section is not visible");
        logger.info("[Manager View Employee] 'Your employees' section is visible");
        
        // Click on employee card
        businessManagerAddEmployeePage.clickEmployeeCard();
        
        // Verify 'Twizz identity Card' heading
        Assert.assertTrue(businessManagerAddEmployeePage.isTwizzIdentityCardHeadingVisible(), 
            "'Twizz identity Card' heading is not visible");
        logger.info("[Manager View Employee] Employee details displayed - employee is added successfully");
        
        logger.info("[Manager View Employee] Test completed successfully");
    }
}
