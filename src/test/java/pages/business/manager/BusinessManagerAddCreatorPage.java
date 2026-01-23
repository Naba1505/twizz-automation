package pages.business.manager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for Twizz Business Manager Add Creator (Invite)
 * Flow: Manager Dashboard → Agency → Add Creator → Send Invitation
 */
public class BusinessManagerAddCreatorPage {
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerAddCreatorPage.class);
    private final Page page;

    public BusinessManagerAddCreatorPage(Page page) {
        this.page = page;
    }

    @Step("Click on Agency icon")
    public void clickAgencyIcon() {
        Locator agencyIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Agency").setExact(true));
        agencyIcon.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Add Creator] Clicked on Agency icon");
    }

    @Step("Verify 'Your agency' title is visible")
    public boolean isYourAgencyTitleVisible() {
        Locator title = page.getByText("Your agency");
        boolean isVisible = title.isVisible();
        logger.info("[Manager Add Creator] 'Your agency' title visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify 'Your creators' message is visible")
    public boolean isYourCreatorsMessageVisible() {
        Locator message = page.getByText("Your creators");
        boolean isVisible = message.isVisible();
        logger.info("[Manager Add Creator] 'Your creators' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Add button to invite creator")
    public void clickAddButton() {
        Locator addButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Add")).first();
        addButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Add Creator] Clicked Add button");
    }

    @Step("Verify 'Invite a creator' heading is visible")
    public boolean isInviteCreatorHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Invite a creator"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Add Creator] 'Invite a creator' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify username instruction text is visible")
    public boolean isUsernameInstructionVisible() {
        Locator instruction = page.getByText("Enter the username of the");
        boolean isVisible = instruction.isVisible();
        logger.info("[Manager Add Creator] Username instruction text visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Search for creator by username: {username}")
    public void searchCreatorByUsername(String username) {
        Locator usernameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username"));
        usernameField.click();
        usernameField.fill(username);
        page.waitForTimeout(1500); // Wait for search results
        logger.info("[Manager Add Creator] Searched for creator: {}", username);
    }

    @Step("Select creator by enabling checkbox")
    public void selectCreatorCheckbox() {
        Locator checkbox = page.locator(".checkbox");
        checkbox.click();
        page.waitForTimeout(500);
        logger.info("[Manager Add Creator] Selected creator checkbox");
    }

    @Step("Click Send invitation button")
    public void clickSendInvitation() {
        Locator sendButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send invitation"));
        sendButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Add Creator] Clicked Send invitation button");
    }

    @Step("Verify invitation success message is visible")
    public boolean isInvitationSuccessMessageVisible() {
        // Try multiple possible success messages
        Locator invitationSent = page.getByText("Invitation sent");
        Locator alreadyInvited = page.getByText("already");
        Locator successDialog = page.locator(".dialog, .modal, .popup").filter(new Locator.FilterOptions().setHasText("Invitation"));
        
        try {
            // Wait for any success indicator
            page.waitForTimeout(2000);
            
            // Check for various success messages
            if (invitationSent.isVisible()) {
                logger.info("[Manager Add Creator] 'Invitation sent' message is visible");
                return true;
            }
            if (alreadyInvited.isVisible()) {
                logger.info("[Manager Add Creator] Creator already invited message is visible");
                return true;
            }
            if (successDialog.count() > 0) {
                logger.info("[Manager Add Creator] Success dialog is visible");
                return true;
            }
        } catch (Exception e) {
            logger.warn("[Manager Add Creator] Could not verify success message: {}", e.getMessage());
        }
        
        logger.warn("[Manager Add Creator] No success message found");
        return false;
    }

    @Step("Verify 'there is an invitation' message is visible")
    public boolean isDuplicateInvitationMessageVisible() {
        Locator duplicateMessage = page.getByText("there is an invitation");
        try {
            duplicateMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Manager Add Creator] Duplicate invitation message did not appear within timeout");
        }
        boolean isVisible = duplicateMessage.isVisible();
        logger.info("[Manager Add Creator] 'there is an invitation already sent' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click 'I understand' button if present")
    public void clickIUnderstandButtonIfPresent() {
        try {
            Locator understandButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I understand"));
            understandButton.waitFor(new Locator.WaitForOptions().setTimeout(3000));
            understandButton.click();
            page.waitForLoadState(LoadState.LOAD);
            page.waitForTimeout(1000);
            logger.info("[Manager Add Creator] Clicked 'I understand' button");
        } catch (Exception e) {
            logger.info("[Manager Add Creator] 'I understand' button not present or not clickable - continuing");
        }
    }

    @Step("Click 'I understand' button")
    public void clickIUnderstandButton() {
        Locator understandButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I understand"));
        understandButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Add Creator] Clicked 'I understand' button");
    }

    @Step("Click on creator card to view details")
    public void clickCreatorCard() {
        Locator creatorCard = page.getByText("Smith · @john_smith");
        creatorCard.click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(2000);
        logger.info("[Manager Add Creator] Clicked on creator card");
    }

    @Step("Verify 'Twizz identity Card' heading is visible")
    public boolean isTwizzIdentityCardHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Twizz identity Card"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Add Creator] 'Twizz identity Card' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Complete add creator flow")
    public void addCreator(String creatorUsername) {
        clickAgencyIcon();
        clickAddButton();
        searchCreatorByUsername(creatorUsername);
        selectCreatorCheckbox();
        clickSendInvitation();
        clickIUnderstandButton();
        logger.info("[Manager Add Creator] Completed add creator flow for: {}", creatorUsername);
    }
}
