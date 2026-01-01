package pages.business.employee;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for Employee Settings and Invitation Management
 * Flow: Employee Dashboard → Settings → View Invitations → Decline
 */
public class EmployeeSettingsPage {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeSettingsPage.class);
    private final Page page;

    public EmployeeSettingsPage(Page page) {
        this.page = page;
    }

    @Step("Click on Settings button")
    public void clickSettingsButton() {
        Locator settingsButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Settings"));
        settingsButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Employee Settings] Clicked on Settings button");
    }

    @Step("Verify 'Manage your relationships' heading is visible")
    public boolean isManageRelationshipsHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Manage your relationships"));
        boolean isVisible = heading.isVisible();
        logger.info("[Employee Settings] 'Manage your relationships' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click on 'View invitations' button")
    public void clickViewInvitationsButton() {
        Locator viewInvitationsButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("View invitations"));
        viewInvitationsButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Employee Settings] Clicked on 'View invitations' button");
    }

    @Step("Verify 'Invitation manageur' heading is visible")
    public boolean isInvitationManagerHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Invitation manageur"));
        boolean isVisible = heading.isVisible();
        logger.info("[Employee Settings] 'Invitation manageur' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Decline button")
    public void clickDeclineButton() {
        Locator declineButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Decline"));
        declineButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Employee Settings] Clicked Decline button");
    }

    @Step("Verify confirmation dialog is visible")
    public boolean isConfirmationDialogVisible() {
        Locator confirmationText = page.getByText("Are you sure you want to");
        boolean isVisible = confirmationText.isVisible();
        logger.info("[Employee Settings] Confirmation dialog visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click 'Finish' button to confirm")
    public void clickFinishButton() {
        Locator finishButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Finish"));
        finishButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Employee Settings] Clicked 'Finish' button");
    }

    @Step("Verify 'Rejected' message is visible")
    public boolean isRejectedMessageVisible() {
        Locator rejectedMessage = page.getByText("Rejected");
        try {
            rejectedMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Employee Settings] Rejected message did not appear within timeout");
        }
        boolean isVisible = rejectedMessage.isVisible();
        logger.info("[Employee Settings] 'Rejected' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Complete reject invitation flow")
    public void rejectInvitation() {
        clickSettingsButton();
        clickViewInvitationsButton();
        clickDeclineButton();
        clickFinishButton();
        logger.info("[Employee Settings] Completed reject invitation flow");
    }
}
