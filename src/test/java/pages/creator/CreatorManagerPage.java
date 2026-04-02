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
        // Use CSS selector for the Refuse button from invitation list
        Locator refuseButton = page.locator(".invitation-manager-refuse-button");
        try {
            refuseButton.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Creator Manager] Refuse button not found with CSS, trying text-based locator");
            refuseButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Refuse"));
            if (refuseButton.count() == 0) {
                refuseButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Refuser")); // French
            }
        }
        refuseButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked Refuse button");
    }

    @Step("Verify confirmation dialog is visible")
    public boolean isConfirmationDialogVisible() {
        Locator confirmationDialog = page.locator(".manager-modal-title");
        try {
            confirmationDialog.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Creator Manager] Confirmation dialog did not appear within timeout");
        }
        boolean isVisible = confirmationDialog.isVisible();
        logger.info("[Creator Manager] Confirmation dialog visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click 'I refuse' button to confirm")
    public void clickIRefuseButton() {
        Locator iRefuseButton = page.locator(".manager-modal-refuse-button");
        iRefuseButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked 'I refuse' button");
    }

    @Step("Verify 'Invitation rejected' message is visible")
    public boolean isInvitationRejectedMessageVisible() {
        // Try multiple language variations for the success message
        Locator rejectedMessage = page.getByText("Invitation rejected");
        Locator rejectedMessageFr = page.getByText("Invitation refusée");
        
        try {
            // Wait for either English or French message
            page.waitForCondition(() -> 
                rejectedMessage.isVisible() || rejectedMessageFr.isVisible(),
                new Page.WaitForConditionOptions().setTimeout(5000)
            );
        } catch (Exception e) {
            logger.warn("[Creator Manager] Invitation rejected message did not appear within timeout");
        }
        
        boolean isVisible = rejectedMessage.isVisible() || rejectedMessageFr.isVisible();
        logger.info("[Creator Manager] 'Invitation rejected' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Accept button")
    public void clickAcceptButton() {
        // Use CSS selector for the Accept button container from invitation list
        Locator acceptButton = page.locator(".invitation-manager-button-container button");
        try {
            acceptButton.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Creator Manager] Accept button not found with CSS, trying text-based locator");
            acceptButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept"));
            if (acceptButton.count() == 0) {
                acceptButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accepter")); // French
            }
        }
        acceptButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked Accept button");
    }

    @Step("Click 'I accept' button to confirm")
    public void clickIAcceptButton() {
        Locator iAcceptButton = page.locator(".manager-modal-accept-button");
        iAcceptButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Creator Manager] Clicked 'I accept' button");
    }

    @Step("Verify 'Invitation accepted' message is visible")
    public boolean isInvitationAcceptedMessageVisible() {
        // Try multiple language variations for the success message
        Locator acceptedMessage = page.getByText("Invitation accepted");
        Locator acceptedMessageFr = page.getByText("Invitation acceptée");
        
        try {
            // Wait for either English or French message
            page.waitForCondition(() -> 
                acceptedMessage.isVisible() || acceptedMessageFr.isVisible(),
                new Page.WaitForConditionOptions().setTimeout(5000)
            );
        } catch (Exception e) {
            logger.warn("[Creator Manager] Invitation accepted message did not appear within timeout");
        }
        
        boolean isVisible = acceptedMessage.isVisible() || acceptedMessageFr.isVisible();
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
