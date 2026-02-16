package pages.business.manager;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;

/**
 * Page Object for Business Manager Settings and Creator Invitation
 * Flow: Manager Dashboard → Settings → Creator Go → Invite Creator
 */
public class BusinessManagerSettingsPage extends BasePage {

    public BusinessManagerSettingsPage(Page page) {
        super(page);
    }

    @Step("Click on Settings icon")
    public void clickSettingsIcon() {
        Locator settingsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings"));
        settingsIcon.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Settings] Clicked on Settings icon");
    }

    @Step("Click on 'Creator Go' button")
    public void clickCreatorGoButton() {
        Locator creatorGoButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Creator Go"));
        creatorGoButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Settings] Clicked on 'Creator Go' button");
    }

    @Step("Verify 'Your creators' text is visible")
    public boolean isYourCreatorsTextVisible() {
        Locator yourCreatorsText = page.getByText("Your creators");
        boolean isVisible = yourCreatorsText.isVisible();
        logger.info("[Manager Settings] 'Your creators' text visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click on 'Invite a creator' text")
    public void clickInviteCreatorText() {
        Locator inviteCreatorText = page.getByText("Invite a creator");
        inviteCreatorText.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Settings] Clicked on 'Invite a creator' text");
    }

    @Step("Verify 'Invite a creator' heading is visible")
    public boolean isInviteCreatorHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Invite a creator"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Settings] 'Invite a creator' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify username instruction text is visible")
    public boolean isUsernameInstructionVisible() {
        Locator instruction = page.getByText("Enter the username of the");
        boolean isVisible = instruction.isVisible();
        logger.info("[Manager Settings] Username instruction text visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Search for creator by username: {username}")
    public void searchCreatorByUsername(String username) {
        Locator usernameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username"));
        usernameField.click();
        usernameField.fill(username);
        page.waitForTimeout(1500);
        logger.info("[Manager Settings] Searched for creator: {}", username);
    }

    @Step("Select creator by clicking checkbox")
    public void selectCreatorCheckbox() {
        Locator checkbox = page.locator(".checkbox");
        checkbox.click();
        page.waitForTimeout(500);
        logger.info("[Manager Settings] Selected creator checkbox");
    }

    @Step("Click 'Send invitation' button")
    public void clickSendInvitationButton() {
        Locator sendButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send invitation"));
        sendButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Settings] Clicked 'Send invitation' button");
    }

    @Step("Verify 'Invitation sent' message is visible")
    public boolean isInvitationSentMessageVisible() {
        Locator invitationSentMessage = page.getByText("Invitation sent");
        try {
            invitationSentMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Manager Settings] 'Invitation sent' message did not appear within timeout");
        }
        boolean isVisible = invitationSentMessage.isVisible();
        logger.info("[Manager Settings] 'Invitation sent' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify 'there is an invitation' message is visible")
    public boolean isDuplicateInvitationMessageVisible() {
        Locator duplicateMessage = page.getByText("there is an invitation");
        try {
            duplicateMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Manager Settings] Duplicate invitation message did not appear within timeout");
        }
        boolean isVisible = duplicateMessage.isVisible();
        logger.info("[Manager Settings] 'there is an invitation' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click 'I understand' button")
    public void clickIUnderstandButton() {
        Locator understandButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I understand"));
        understandButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Settings] Clicked 'I understand' button");
    }

    @Step("Click on 'Employee Go' button")
    public void clickEmployeeGoButton() {
        Locator employeeGoButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Employee Go"));
        employeeGoButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Settings] Clicked on 'Employee Go' button");
    }

    @Step("Verify 'Your employees' text is visible")
    public boolean isYourEmployeesTextVisible() {
        Locator yourEmployeesText = page.getByText("Your employees");
        boolean isVisible = yourEmployeesText.isVisible();
        logger.info("[Manager Settings] 'Your employees' text visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click on 'Invite a employee' text")
    public void clickInviteEmployeeText() {
        Locator inviteEmployeeText = page.getByText("Invite a employee");
        inviteEmployeeText.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Settings] Clicked on 'Invite a employee' text");
    }

    @Step("Verify 'Invite an employee' heading is visible")
    public boolean isInviteEmployeeHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Invite an employee"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Settings] 'Invite an employee' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Search for employee by username: {username}")
    public void searchEmployeeByUsername(String username) {
        Locator usernameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username"));
        usernameField.click();
        usernameField.fill(username);
        page.waitForTimeout(1500);
        logger.info("[Manager Settings] Searched for employee: {}", username);
    }

    @Step("Select employee by clicking checkbox")
    public void selectEmployeeCheckbox() {
        Locator checkbox = page.locator(".checkbox");
        checkbox.click();
        page.waitForTimeout(500);
        logger.info("[Manager Settings] Selected employee checkbox");
    }

    @Step("Complete invite creator from settings flow")
    public void inviteCreatorFromSettings(String creatorUsername) {
        clickSettingsIcon();
        clickCreatorGoButton();
        clickInviteCreatorText();
        searchCreatorByUsername(creatorUsername);
        selectCreatorCheckbox();
        clickSendInvitationButton();
        clickIUnderstandButton();
        logger.info("[Manager Settings] Completed invite creator from settings flow for: {}", creatorUsername);
    }

    @Step("Complete invite employee from settings flow")
    public void inviteEmployeeFromSettings(String employeeUsername) {
        clickSettingsIcon();
        clickEmployeeGoButton();
        clickInviteEmployeeText();
        searchEmployeeByUsername(employeeUsername);
        selectEmployeeCheckbox();
        clickSendInvitationButton();
        clickIUnderstandButton();
        logger.info("[Manager Settings] Completed invite employee from settings flow for: {}", employeeUsername);
    }
}
