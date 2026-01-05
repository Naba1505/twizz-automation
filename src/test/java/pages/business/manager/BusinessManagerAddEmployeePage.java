package pages.business.manager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for Business Manager Add Employee (Invite)
 * Flow: Manager Dashboard → Agency → Add Employee → Send Invitation
 */
public class BusinessManagerAddEmployeePage {
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerAddEmployeePage.class);
    private final Page page;

    public BusinessManagerAddEmployeePage(Page page) {
        this.page = page;
    }

    @Step("Click on Agency icon")
    public void clickAgencyIcon() {
        Locator agencyIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Agency").setExact(true));
        agencyIcon.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Add Employee] Clicked on Agency icon");
    }

    @Step("Verify 'Your agency' title is visible")
    public boolean isYourAgencyTitleVisible() {
        Locator title = page.getByText("Your agency", new Page.GetByTextOptions().setExact(true));
        boolean isVisible = title.isVisible();
        logger.info("[Manager Add Employee] 'Your agency' title visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify 'Your employees' message is visible")
    public boolean isYourEmployeesMessageVisible() {
        Locator message = page.getByText("Your employees");
        boolean isVisible = message.isVisible();
        logger.info("[Manager Add Employee] 'Your employees' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Add button to invite employee")
    public void clickAddButton() {
        Locator addButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Add")).nth(1);
        addButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Add Employee] Clicked Add button");
    }

    @Step("Verify 'Invite an employee' heading is visible")
    public boolean isInviteEmployeeHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Invite an employee"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Add Employee] 'Invite an employee' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify username instruction is visible")
    public boolean isUsernameInstructionVisible() {
        Locator instruction = page.getByText("Enter the username of the");
        boolean isVisible = instruction.isVisible();
        logger.info("[Manager Add Employee] Username instruction visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Search employee by username: {username}")
    public void searchEmployeeByUsername(String username) {
        Locator usernameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username"));
        usernameField.click();
        usernameField.fill(username);
        page.waitForTimeout(1500);
        logger.info("[Manager Add Employee] Searched for employee: {}", username);
    }

    @Step("Select employee checkbox")
    public void selectEmployeeCheckbox() {
        Locator checkbox = page.locator(".checkbox");
        checkbox.click();
        page.waitForTimeout(500);
        logger.info("[Manager Add Employee] Selected employee checkbox");
    }

    @Step("Click Send invitation button")
    public void clickSendInvitation() {
        Locator sendButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send invitation"));
        sendButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(2000);
        logger.info("[Manager Add Employee] Clicked Send invitation button");
    }

    @Step("Verify invitation success message is visible")
    public boolean isInvitationSuccessMessageVisible() {
        Locator invitationSent = page.getByText("Invitation sent");
        Locator alreadyInvited = page.getByText("already");
        Locator successDialog = page.locator(".dialog, .modal, .popup").filter(new Locator.FilterOptions().setHasText("Invitation"));

        try {
            page.waitForTimeout(2000);

            if (invitationSent.isVisible()) {
                logger.info("[Manager Add Employee] 'Invitation sent' message is visible");
                return true;
            }
            if (alreadyInvited.isVisible()) {
                logger.info("[Manager Add Employee] Employee already invited message is visible");
                return true;
            }
            if (successDialog.count() > 0) {
                logger.info("[Manager Add Employee] Success dialog is visible");
                return true;
            }
        } catch (Exception e) {
            logger.warn("[Manager Add Employee] Could not verify success message: {}", e.getMessage());
        }

        logger.warn("[Manager Add Employee] No success message found");
        return false;
    }

    @Step("Verify 'there is an invitation' message is visible")
    public boolean isDuplicateInvitationMessageVisible() {
        Locator duplicateMessage = page.getByText("there is an invitation");
        try {
            duplicateMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Manager Add Employee] Duplicate invitation message did not appear within timeout");
        }
        boolean isVisible = duplicateMessage.isVisible();
        logger.info("[Manager Add Employee] 'there is an invitation already sent' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click 'I understand' button if present")
    public void clickIUnderstandButtonIfPresent() {
        try {
            Locator understandButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I understand"));
            page.waitForTimeout(1000);
            if (understandButton.isVisible()) {
                understandButton.click();
                page.waitForLoadState(LoadState.LOAD);
                page.waitForTimeout(1000);
                logger.info("[Manager Add Employee] Clicked 'I understand' button");
            } else {
                logger.info("[Manager Add Employee] 'I understand' button not present - continuing");
            }
        } catch (Exception e) {
            logger.info("[Manager Add Employee] Error checking/clicking 'I understand' button: {}", e.getMessage());
        }
    }

    @Step("Click on employee card to view details")
    public void clickEmployeeCard() {
        Locator employeeCard = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("employee"));
        employeeCard.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Add Employee] Clicked on employee card");
    }

    @Step("Verify 'Twizz identity Card' heading is visible")
    public boolean isTwizzIdentityCardHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Twizz identity Card"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Add Employee] 'Twizz identity Card' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Complete add employee flow for username: {employeeUsername}")
    public void addEmployee(String employeeUsername) {
        clickAgencyIcon();
        clickAddButton();
        searchEmployeeByUsername(employeeUsername);
        selectEmployeeCheckbox();
        clickSendInvitation();
        logger.info("[Manager Add Employee] Completed add employee flow for: {}", employeeUsername);
    }
}
