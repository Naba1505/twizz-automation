package tests.fan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import pages.fan.FanSavedCardsPage;

@Epic("Fan")
@Feature("Cleanup")
public class FanCleanupCardsTest extends BaseFanTest {
    private static final Logger logger = LoggerFactory.getLogger(FanCleanupCardsTest.class);

    @Story("Cleanup existing saved cards in Settings > Saved Cards")
    @Test(priority = 1, description = "Navigate to Saved Cards and cleanup any existing saved cards")
    public void fanCleanupSavedCards() {
        logger.info("[Cleanup Cards] Starting test: Cleanup existing saved cards");

        // Navigate to Settings > Saved Cards
        FanSavedCardsPage cards = new FanSavedCardsPage(page);
        logger.info("[Cleanup Cards] Navigating to Settings > Saved Cards");
        cards.navigateToSavedCards();

        // Delete all existing cards
        logger.info("[Cleanup Cards] Cleaning up all existing saved cards");
        cards.deleteAllExistingCards();

        logger.info("[Cleanup Cards] Test passed: All existing cards cleaned up successfully");
    }
}
