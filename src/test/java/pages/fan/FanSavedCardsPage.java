package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FanSavedCardsPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanSavedCardsPage.class);
    

    public FanSavedCardsPage(Page page) { super(page); }

    @Step("Navigate to Settings > Saved Cards (Fan)")
    public void navigateToSavedCards() {
        logger.info("[Saved Cards] Navigating to Settings > Saved Cards");
        Locator settingsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon"));
        waitVisible(settingsIcon.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(settingsIcon.first(), 1, ConfigReader.getAnimationTimeout());
        // Click Settings entry if intermediate menu appears
        Locator settingsEntry = page.getByText("Settings");
        if (safeIsVisible(settingsEntry.first())) {
            clickWithRetry(settingsEntry.first(), 1, ConfigReader.getAnimationTimeout());
        }
        // Open Saved Cards
        Locator savedCards = page.getByText("Saved Cards");
        waitVisible(savedCards.first(), ConfigReader.getShortTimeout());
        clickWithRetry(savedCards.first(), 1, ConfigReader.getAnimationTimeout());
        assertOnSavedCards();
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Check for "No Card Found!" message
        Locator noCardMsg = page.getByText("No Card Found!");
        if (safeIsVisible(noCardMsg)) {
            logger.info("[Saved Cards] 'No Card Found!' message is visible - no cards to clean");
        } else {
            logger.info("[Saved Cards] Cards found on page, ready for cleanup");
        }
    }

    @Step("Assert on Saved Cards screen")
    public void assertOnSavedCards() {
        Locator title = page.getByText("Saved Cards");
        waitVisible(title.first(), ConfigReader.getVisibilityTimeout());
        logger.info("[Saved Cards] On Saved Cards screen");
    }

    @Step("Click 'Add a card' on Saved Cards")
    public void clickAddCard() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add a card"));
        waitVisible(btn.first(), ConfigReader.getShortTimeout());
        clickWithRetry(btn.first(), 1, ConfigReader.getAnimationTimeout());
        logger.info("[Saved Cards] Clicked 'Add a card' button");
    }

    @Step("Assert on Card information screen")
    public void assertOnCardInformationScreen() {
        Locator cardInfoHeading = page.getByText("Card information");
        waitVisible(cardInfoHeading.first(), ConfigReader.getVisibilityTimeout());
        logger.info("[Saved Cards] On Card information screen - heading visible");
    }

    @Step("Fill cardholder first name: {firstName}")
    public void fillFirstName(String firstName) {
        Locator input = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First name"));
        waitVisible(input.first(), ConfigReader.getShortTimeout());
        typeAndAssert(input.first(), firstName);
    }

    @Step("Fill cardholder last name: {lastName}")
    public void fillLastName(String lastName) {
        Locator input = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last name"));
        waitVisible(input.first(), ConfigReader.getShortTimeout());
        typeAndAssert(input.first(), lastName);
    }

    @Step("Fill card number")
    public void fillCardNumber(String number) {
        // Some implementations expose the masked label as the accessible name
        Locator input = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("**** **** **** ****"));
        waitVisible(input.first(), ConfigReader.getShortTimeout());
        input.first().fill(number);
    }

    @Step("Fill card expiry")
    public void fillExpiry(String expiry) {
        Locator input = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("MM/YY"));
        waitVisible(input.first(), ConfigReader.getShortTimeout());
        input.first().fill(expiry);
    }

    @Step("Fill card CVV")
    public void fillCvv(String cvv) {
        Locator input = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("CVV"));
        waitVisible(input.first(), ConfigReader.getShortTimeout());
        input.first().fill(cvv);
    }

    @Step("Accept terms checkbox if present")
    public void acceptTermsIfPresent() {
        Locator checkbox = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("I declare that I have read"));
        if (safeIsVisible(checkbox.first())) {
            try { checkbox.first().check(); } catch (Exception e) {
                logger.warn("[Saved Cards] Checkbox check failed, retrying with click: {}", e.getMessage());
                clickWithRetry(checkbox.first(), 1, ConfigReader.getAnimationTimeout());
            }
        }
    }

    @Step("Submit Add new card")
    public void submitAddCard() {
        Locator add = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add new card"));
        waitVisible(add.first(), ConfigReader.getShortTimeout());
        clickWithRetry(add.first(), 1, ConfigReader.getAnimationTimeout());
    }

    @Step("Assert saved card visible by holder name: {fullName}")
    public void assertSavedCardVisible(String fullName) {
        Locator name = page.getByText(fullName);
        waitVisible(name.first(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Open card actions for holder name: {fullName}")
    public void openCardActions(String fullName) {
        Locator card = page.getByText(fullName);
        waitVisible(card.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(card.first(), 1, ConfigReader.getAnimationTimeout());
        Locator actionTitle = page.getByText("What do you want to do?");
        logger.info("[Saved Cards] Waiting for actions popup: 'What do you want to do?'");
        waitVisible(actionTitle.first(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Delete card for holder name: {fullName}")
    public void deleteCard(String fullName) {
        openCardActions(fullName);
        // Some dialogs render multiple Delete buttons; prefer the last visible
        Locator deleteButtons = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete"));
        int count = deleteButtons.count();
        if (count == 0) throw new RuntimeException("No 'Delete' button found in actions popup");
        Locator targetDelete = deleteButtons.nth(count - 1);
        waitVisible(targetDelete, ConfigReader.getVisibilityTimeout());
        logger.info("[Saved Cards] Clicking 'Delete' button (index {} of {})", count - 1, count);
        clickWithRetry(targetDelete, 1, ConfigReader.getAnimationTimeout());

        // Ensure confirmation popup visible
        Locator confirmText = page.getByText("Do you really want to delete");
        logger.info("[Saved Cards] Waiting for confirmation popup: 'Do you really want to delete'");
        waitVisible(confirmText.first(), ConfigReader.getVisibilityTimeout());

        Locator yesDelete = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes delete"));
        waitVisible(yesDelete.first(), ConfigReader.getShortTimeout());
        clickWithRetry(yesDelete.first(), 1, ConfigReader.getAnimationTimeout());

        // Wait for the card entry to disappear
        logger.info("[Saved Cards] Waiting for card '{}' to be removed", fullName);
        waitForCardToDisappear(fullName, ConfigReader.getVisibilityTimeout());
    }

    @Step("Delete all existing saved cards")
    public void deleteAllExistingCards() {
        logger.info("[Saved Cards] Starting to delete all existing cards");
        
        // First check if "No Card Found!" message is already visible
        Locator noCardMessage = page.getByText("No Card Found!");
        if (safeIsVisible(noCardMessage)) {
            logger.info("[Saved Cards] 'No Card Found!' message is already visible - no cards to delete");
            return;
        }
        
        // Keep deleting until no more cards are found
        int deletedCount = 0;
        int maxAttempts = 10; // Prevent infinite loop
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            try {
                // Follow exact codegen approach - click on the card div
                page.locator("div").nth(4).click();
                
                // Click Delete button
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete")).click();
                
                // Confirm deletion - follow exact codegen sequence
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete")).click();
                
                // Ensure popup is displayed
                page.getByText("Do you really want to delete");
                
                // Click on "Yes delete" button
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes delete")).click();
                
                // Wait for "No Card Found!" to be visible
                try {
                    page.waitForSelector(":text-is('No Card Found!')", new Page.WaitForSelectorOptions().setTimeout(ConfigReader.getShortTimeout()));
                    logger.info("[Saved Cards] 'No Card Found!' message appeared after deletion");
                    deletedCount++;
                    break;
                } catch (Exception e) {
                    logger.info("[Saved Cards] 'No Card Found!' not yet visible, more cards remain");
                    deletedCount++;
                    attempts++;
                }
                
            } catch (Exception e) {
                logger.info("[Saved Cards] No more cards to delete or error occurred: {}", e.getMessage());
                break;
            }
        }
        
        logger.info("[Saved Cards] Completed deletion, removed {} cards", deletedCount);
        
        // Final verification
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        if (safeIsVisible(noCardMessage)) {
            logger.info("[Saved Cards] Verified 'No Card Found!' message is displayed");
        } else if (deletedCount == 0) {
            logger.info("[Saved Cards] No cards were found to delete initially");
        } else {
            logger.info("[Saved Cards] 'No Card Found!' not visible, but {} cards were deleted", deletedCount);
        }
    }

    @Step("Assert no saved cards exist")
    public void assertNoCardsExist() {
        logger.info("[Saved Cards] Checking if any saved cards exist");
        
        // Try to find any card entry
        Locator anyCard = page.locator("button[type='button'] span").first();
        
        // Wait for page to stabilize
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Check if any cards are visible
        if (anyCard.count() > 0 && safeIsVisible(anyCard)) {
            String cardText = anyCard.textContent();
            throw new AssertionError("Expected no saved cards, but found card: " + cardText);
        }
        
        logger.info("[Saved Cards] Verified: No saved cards exist");
    }

    @Step("Assert card not present for holder name: {fullName}")
    public void assertCardNotPresent(String fullName) {
        // Ensure disappearance within timeout
        waitForCardToDisappear(fullName, ConfigReader.getVisibilityTimeout());
    }

    private void waitForCardToDisappear(String fullName, int timeoutMs) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                Locator name = page.getByText(fullName);
                if (name.count() == 0) return;
                if (!safeIsVisible(name.first())) return;
            } catch (Throwable e) {
                logger.debug("[Saved Cards] Card element detached during disappearance check: {}", e.getMessage());
                return;
            }
            page.waitForTimeout(ConfigReader.getPollInterval());
        }
        throw new AssertionError("Card still visible after delete within timeout: " + fullName);
    }
}

