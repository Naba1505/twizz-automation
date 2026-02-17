package pages.creator;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import pages.common.BasePage;

/**
 * Page Object for Creator Manager Settings
 * Flow: Creator Settings → Manager → Reject Invitation
 */
public class CreatorManagerPage extends BasePage {

    public CreatorManagerPage(Page page) {
        super(page);
    }

    @Step("Click on settings icon")
    public void clickSettingsIcon() {
        Locator settingsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
        settingsIcon.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked on settings icon");
    }

    @Step("Click on Manager menu item")
    public void clickManagerMenuItem() {
        Locator managerMenuItem = page.getByText("Manager");
        managerMenuItem.scrollIntoViewIfNeeded();
        managerMenuItem.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked on Manager menu item");
    }

    @Step("Verify 'Manager' heading is visible")
    public boolean isManagerHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Manager"));
        boolean isVisible = heading.isVisible();
        logger.info("[Creator Manager] 'Manager' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify 'Invitation' text is visible")
    public boolean isInvitationTextVisible() {
        Locator invitationText = page.getByText("Invitation", new Page.GetByTextOptions().setExact(true));
        boolean isVisible = invitationText.isVisible();
        logger.info("[Creator Manager] 'Invitation' text visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Refuse button")
    public void clickRefuseButton() {
        Locator refuseButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Refuse"));
        refuseButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked Refuse button");
    }

    @Step("Verify confirmation dialog is visible")
    public boolean isConfirmationDialogVisible() {
        Locator confirmationText = page.getByText("Are you sure you want to");
        boolean isVisible = confirmationText.isVisible();
        logger.info("[Creator Manager] Confirmation dialog visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click 'I refuse' button to confirm")
    public void clickIRefuseButton() {
        Locator iRefuseButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I refuse"));
        iRefuseButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked 'I refuse' button");
    }

    @Step("Verify 'Invitation rejected' message is visible")
    public boolean isInvitationRejectedMessageVisible() {
        Locator rejectedMessage = page.getByText("Invitation rejected");
        try {
            rejectedMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Creator Manager] Invitation rejected message did not appear within timeout");
        }
        boolean isVisible = rejectedMessage.isVisible();
        logger.info("[Creator Manager] 'Invitation rejected' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Accept button")
    public void clickAcceptButton() {
        Locator acceptButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept"));
        acceptButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked Accept button");
    }

    @Step("Click 'I accept' button to confirm")
    public void clickIAcceptButton() {
        Locator iAcceptButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I accept"));
        iAcceptButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked 'I accept' button");
    }

    @Step("Verify 'Invitation accepted' message is visible")
    public boolean isInvitationAcceptedMessageVisible() {
        Locator acceptedMessage = page.getByText("Invitation accepted");
        try {
            acceptedMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Creator Manager] Invitation accepted message did not appear within timeout");
        }
        boolean isVisible = acceptedMessage.isVisible();
        logger.info("[Creator Manager] 'Invitation accepted' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Complete reject invitation flow")
    public void rejectInvitation() {
        clickSettingsIcon();
        clickManagerMenuItem();
        clickRefuseButton();
        clickIRefuseButton();
        logger.info("[Creator Manager] Completed reject invitation flow");
    }

    @Step("Complete accept invitation flow")
    public void acceptInvitation() {
        clickSettingsIcon();
        clickManagerMenuItem();
        clickAcceptButton();
        clickIAcceptButton();
        logger.info("[Creator Manager] Completed accept invitation flow");
    }
}
