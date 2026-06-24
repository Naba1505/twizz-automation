package tests.creator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
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

    @Story("Clear all recent searches from search history")
    @Test(priority = 1, description = "Creator can clear all recent searches from discover/search screen")
    public void creatorCanClearAllRecentSearches() {
        // Navigate to Discover screen
        CreatorClearSearchPage clearSearch = new CreatorClearSearchPage(page);
        clearSearch.navigateToDiscover();

        clearSearch.clickSearchField();

        int initialCount = clearSearch.getRecentSearchCount();

        if (initialCount > 0) {
            Assert.assertTrue(clearSearch.isRecentTextVisible(),
                    "Expected 'Recent' text to be visible when recent searches exist");
        }

        if (initialCount == 0) {
            return;
        }

        int clearedCount = clearSearch.clearAllRecentSearches();
        Assert.assertTrue(clearedCount > 0,
                "Expected to clear at least 1 recent search but cleared " + clearedCount);

        Assert.assertTrue(clearSearch.verifyAllSearchesCleared(),
                "Expected all recent searches to be cleared but some remain");
    }
}
