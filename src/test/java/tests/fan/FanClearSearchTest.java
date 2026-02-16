package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.fan.FanClearSearchPage;

/**
 * Test class for Fan Clear Recent Searches functionality.
 * Tests clearing recent search history from the discover/search screen.
 */
@Epic("Fan")
@Feature("Clear Recent Searches")
public class FanClearSearchTest extends BaseFanTest {
    private static final Logger logger = LoggerFactory.getLogger(FanClearSearchTest.class);

    @Story("Clear all recent searches from search history")
    @Test(priority = 1, description = "Fan can clear all recent searches from discover/search screen")
    public void fanCanClearAllRecentSearches() {
        // Navigate to Discover screen (or stay if already there)
        FanClearSearchPage clearSearch = new FanClearSearchPage(page);
        clearSearch.navigateToDiscover();

        // Click on search field to open search interface
        logger.info("[Fan Clear Search] Opening search interface");
        clearSearch.clickSearchField();

        // Verify "Recent" text is displayed
        logger.info("[Fan Clear Search] Verifying 'Recent' text is displayed");
        boolean recentVisible = clearSearch.isRecentTextVisible();
        Assert.assertTrue(recentVisible, "Expected 'Recent' text to be visible in search interface");

        // Get initial count of recent searches
        int initialCount = clearSearch.getRecentSearchCount();
        logger.info("[Fan Clear Search] Found {} recent searches to clear", initialCount);

        // If no recent searches, skip the test
        if (initialCount == 0) {
            logger.info("[Fan Clear Search] No recent searches found, test passed (nothing to clear)");
            return;
        }

        // Clear all recent searches
        logger.info("[Fan Clear Search] Clearing all recent searches");
        int clearedCount = clearSearch.clearAllRecentSearches();
        
        // Verify at least one search was cleared
        Assert.assertTrue(clearedCount > 0, 
                "Expected to clear at least 1 recent search but cleared " + clearedCount);
        
        logger.info("[Fan Clear Search] Cleared {} recent searches", clearedCount);

        // Verify all searches are cleared
        logger.info("[Fan Clear Search] Verifying all searches are cleared");
        boolean allCleared = clearSearch.verifyAllSearchesCleared();
        Assert.assertTrue(allCleared, 
                "Expected all recent searches to be cleared but some remain");
        
        logger.info("[Fan Clear Search] Successfully cleared all recent searches");
    }
}
