package tests.creator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorClearSearchPage;

/**
 * Test class for Creator Clear Recent Searches functionality.
 * Tests clearing recent search history from the discover/search screen.
 */
@Epic("Creator")
@Feature("Clear Recent Searches")
public class CreatorClearSearchTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorClearSearchTest.class);

    @Story("Clear all recent searches from search history")
    @Test(priority = 1, description = "Creator can clear all recent searches from discover/search screen")
    public void creatorCanClearAllRecentSearches() {
        // Navigate to Discover screen
        CreatorClearSearchPage clearSearch = new CreatorClearSearchPage(page);
        clearSearch.navigateToDiscover();

        // Click on search field to open search interface
        logger.info("[Creator Clear Search] Opening search interface");
        clearSearch.clickSearchField();

        // Verify "Recent" text is displayed
        logger.info("[Creator Clear Search] Verifying 'Recent' text is displayed");
        boolean recentVisible = clearSearch.isRecentTextVisible();
        Assert.assertTrue(recentVisible, "Expected 'Recent' text to be visible in search interface");

        // Get initial count of recent searches
        int initialCount = clearSearch.getRecentSearchCount();
        logger.info("[Creator Clear Search] Found {} recent searches to clear", initialCount);

        // If no recent searches, skip the test
        if (initialCount == 0) {
            logger.info("[Creator Clear Search] No recent searches found, test passed (nothing to clear)");
            return;
        }

        // Clear all recent searches
        logger.info("[Creator Clear Search] Clearing all recent searches");
        int clearedCount = clearSearch.clearAllRecentSearches();
        
        // Verify at least one search was cleared
        Assert.assertTrue(clearedCount > 0, 
                "Expected to clear at least 1 recent search but cleared " + clearedCount);
        
        logger.info("[Creator Clear Search] Cleared {} recent searches", clearedCount);

        // Verify all searches are cleared
        logger.info("[Creator Clear Search] Verifying all searches are cleared");
        boolean allCleared = clearSearch.verifyAllSearchesCleared();
        Assert.assertTrue(allCleared, 
                "Expected all recent searches to be cleared but some remain");
        
        logger.info("[Creator Clear Search] Successfully cleared all recent searches");
    }
}
