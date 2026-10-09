package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

public class FanSavedCardsPage extends BasePage {

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
        waitForCardListLoaded();

        // Check for "No Card Found!" message
        Locator noCardMsg = page.getByText("No Card Found!").first();
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
        waitForCardListLoaded();

        Locator noCardMessage = page.getByText("No Card Found!").first();
        if (safeIsVisible(noCardMessage)) {
            logger.info("[Saved Cards] 'No Card Found!' message is already visible - no cards to delete");
            return;
        }
        
        int deletedCount = 0;
        int maxAttempts = 10;
        
        for (int attempts = 0; attempts < maxAttempts; attempts++) {
            if (safeIsVisible(noCardMessage)) {
                logger.info("[Saved Cards] 'No Card Found!' appeared after {} deletion(s)", deletedCount);
                break;
            }
            
            try {
                String firstName = ConfigReader.getProperty("fan.card.firstName", "Test");
                String lastName = ConfigReader.getProperty("fan.card.lastName", "Card");
                String fullName = firstName + " " + lastName;
                
                Locator cardByName = page.getByText(fullName);
                if (safeIsVisible(cardByName.first())) {
                    logger.info("[Saved Cards] Found card by config name: {}", fullName);
                    deleteCard(fullName);
                    deletedCount++;
                    continue;
                }
                
                // Fallback: find any text that looks like a cardholder name (2+ words, not UI labels)
                Locator allText = page.locator("text=/^[A-Z][a-z]+ [A-Z][a-z]+/");
                for (int i = 0; i < allText.count(); i++) {
                    String text = allText.nth(i).textContent().trim();
                    if (text.equals("No Card Found!") || text.equals("Saved Cards") || text.equals("Add a card") || 
                        text.equals("Card information") || text.contains("Efficient payment")) {
                        continue;
                    }
                    if (safeIsVisible(allText.nth(i))) {
                        logger.info("[Saved Cards] Found potential card name: {}", text);
                        deleteCard(text);
                        deletedCount++;
                        break;
                    }
                }
                
                if (deletedCount > attempts) {
                    continue;
                }
                
                logger.info("[Saved Cards] No more card entries found, stopping deletion");
                break;
            } catch (Exception e) {
                logger.warn("[Saved Cards] Deletion iteration {} failed: {}", attempts + 1, e.getMessage());
                break;
            }
        }
        
        logger.info("[Saved Cards] Completed deletion, removed {} cards", deletedCount);
        
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
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

        // Poll for the empty-state message - the card list loads asynchronously after reload
        Locator noCardMessage = page.getByText("No Card Found!").first();
        long end = System.currentTimeMillis() + ConfigReader.getShortTimeout();
        boolean found = false;
        while (System.currentTimeMillis() < end) {
            if (safeIsVisible(noCardMessage)) {
                found = true;
                break;
            }
            page.waitForTimeout(ConfigReader.getPollInterval());
        }
        if (!found) {
            throw new AssertionError("Expected 'No Card Found!' message to be visible, but it was not found");
        }

        logger.info("[Saved Cards] Verified: 'No Card Found!' message is displayed - no saved cards exist");
    }

    /**
     * Wait until the card list finishes loading: either the empty-state
     * message or at least one cardholder-name entry becomes visible.
     */
    private void waitForCardListLoaded() {
        Locator noCard = page.getByText("No Card Found!").first();
        Locator names = page.locator("text=/^[A-Z][a-z]+ [A-Z][a-z]+/");
        long end = System.currentTimeMillis() + ConfigReader.getVisibilityTimeout();
        while (System.currentTimeMillis() < end) {
            if (safeIsVisible(noCard)) return;
            for (int i = 0; i < names.count(); i++) {
                String text = "";
                try { text = names.nth(i).textContent().trim(); } catch (Throwable ignored) {}
                if (isUiLabel(text)) continue;
                if (safeIsVisible(names.nth(i))) return;
            }
            page.waitForTimeout(ConfigReader.getPollInterval());
        }
        logger.warn("[Saved Cards] Card list content did not appear within timeout");
    }

    private boolean isUiLabel(String text) {
        return text.equals("No Card Found!") || text.equals("Saved Cards") || text.equals("Add a card") ||
                text.equals("Card information") || text.contains("Efficient payment");
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

