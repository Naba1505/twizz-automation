package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

/**
 * Page Object for Creator Clear Recent Searches functionality.
 * Handles clearing recent search history from the discover/search screen.
 */
public class CreatorClearSearchPage extends BasePage {
    private static final String DISCOVER_PATH_FRAGMENT = "/common/discover";

    public CreatorClearSearchPage(Page page) {
        super(page);
    }

    @Step("Navigate to Discover screen via Search icon")
    public void navigateToDiscover() {
        logger.info("Navigating to Discover screen");
        
        // Click Search icon to navigate to discover
        Locator searchIcon = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("Search icon"));
        waitVisible(searchIcon.first(), ConfigReader.getShortTimeout());
        clickWithRetry(searchIcon.first(), 2, ConfigReader.getElementRetryDelay());

        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**",
                new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        
        logger.info("Navigated to Discover screen");
    }

    private Locator topSearchIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search")).first();
    }

    private Locator searchBox() {
        return page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search"));
    }

    private Locator backControl() {
        // Publication viewer back chevron has empty alt; profile back uses alt="arrow left"
        return page.locator(".discover-publication-header-hit img[src*='chevron-back'], img[alt='arrow left']").first();
    }

    @Step("Click on search icon to open search interface")
    public void clickSearchField() {
        logger.info("Clicking on search icon");

        waitVisible(topSearchIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(topSearchIcon(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(searchBox(), ConfigReader.getShortTimeout());

        logger.info("Search interface opened");
    }

    @Step("Fill search query: {query}")
    public void fillSearch(String query) {
        waitVisible(searchBox(), ConfigReader.getShortTimeout());
        searchBox().fill(query == null ? "" : query);
        waitForUiToSettle();
    }

    @Step("Click the first search result row")
    public void clickFirstSearchResult() {
        // Result rows are .row-recent-search on some builds; the avatar image works on all
        Locator firstResult = page.locator(".row-recent-search").first();
        try {
            waitVisible(firstResult, ConfigReader.getShortTimeout());
        } catch (Throwable e) {
            logger.debug("row-recent-search not found, falling back to avatar row: {}", e.getMessage());
            firstResult = page.locator("img.avatar").first();
            waitVisible(firstResult, ConfigReader.getShortTimeout());
        }
        clickWithRetry(firstResult, 1, ConfigReader.getElementRetryDelay());
        waitForUiToSettle();
        logger.info("Clicked first search result");
    }

    @Step("Navigate back to Discover from opened profile")
    public void navigateBackToDiscover() {
        // The profile onboarding overlay (.fan-profile-overlay) intercepts the back chevron - remove it
        try {
            page.evaluate("document.querySelectorAll('.fan-profile-overlay').forEach(el => el.remove())");
        } catch (Throwable e) { logger.debug("fan-profile-overlay removal failed: {}", e.getMessage()); }
        waitVisible(backControl(), ConfigReader.getShortTimeout());
        boolean navigated = false;
        try {
            backControl().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout()));
            navigated = true;
        } catch (Exception e) {
            logger.warn("Normal back click failed ({}); trying overlay dismiss + retry", e.getMessage());
        }
        if (!navigated) {
            try {
                page.keyboard().press("Escape");
                waitForUiToSettle();
                backControl().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout()));
                navigated = true;
            } catch (Exception e) {
                logger.warn("Overlay dismiss + back click failed ({}); trying force click", e.getMessage());
            }
        }
        if (!navigated) {
            try {
                backControl().click(new Locator.ClickOptions().setForce(true).setTimeout(ConfigReader.getShortTimeout()));
                navigated = true;
            } catch (Exception e) {
                logger.warn("Force back click failed ({}); falling back to browser back", e.getMessage());
            }
        }
        if (!navigated) {
            page.goBack(new Page.GoBackOptions().setTimeout(ConfigReader.getShortTimeout()));
        }
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**",
                new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
        waitForUiToSettle();
    }

    @Step("Verify 'Recent' text is displayed")
    public boolean isRecentTextVisible() {
        logger.info("Verifying 'Recent' text is displayed");

        Locator recentText = page.getByText("Recent");
        boolean visible = safeIsVisible(recentText.first());

        logger.info("'Recent' text visible: {}", visible);
        return visible;
    }

    @Step("Get count of recent search items")
    public int getRecentSearchCount() {
        waitForUiToSettle();

        // Use exact codegen locator: page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Remove"))
        Locator removeIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("Remove"));
        int count = removeIcons.count();
        
        logger.info("Found {} recent search items (Remove icons)", count);
        return count;
    }

    @Step("Remove one recent search item")
    public boolean removeOneRecentSearch() {
        logger.info("Removing one recent search item");
        
        // Use exact codegen locator: page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Remove")).first().click()
        Locator removeIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("Remove"));
        
        int count = removeIcons.count();
        logger.info("Found {} Remove icons", count);
        
        if (count == 0) {
            logger.info("No recent searches to remove");
            return false;
        }
        
        try {
            Locator firstRemove = removeIcons.first();
            clickWithRetry(firstRemove, 1, ConfigReader.getElementRetryDelay());

            waitForUiToSettle();
            logger.info("Removed one recent search item");
            return true;
        } catch (Throwable e) {
            logger.warn("Failed to remove recent search: {}", e.getMessage());
            return false;
        }
    }

    @Step("Clear all recent searches")
    public int clearAllRecentSearches() {
        logger.info("Clearing all recent searches");
        
        int removedCount = 0;
        int maxAttempts = 20; // Safety limit
        
        for (int i = 0; i < maxAttempts; i++) {
            // Get current count of Remove icons
            int currentCount = getRecentSearchCount();
            
            if (currentCount == 0) {
                logger.info("All recent searches cleared");
                break;
            }
            
            // Remove one search
            boolean removed = removeOneRecentSearch();
            if (removed) {
                removedCount++;
                logger.info("Removed search #{}", removedCount);
            } else {
                logger.warn("Failed to remove search, stopping");
                break;
            }

            waitForAnimation();
        }
        
        logger.info("Total recent searches cleared: {}", removedCount);
        return removedCount;
    }

    @Step("Verify all recent searches are cleared")
    public boolean verifyAllSearchesCleared() {
        logger.info("Verifying all recent searches are cleared");

        waitForUiToSettle();

        int remainingCount = getRecentSearchCount();
        boolean allCleared = remainingCount == 0;
        
        logger.info("All searches cleared: {} (remaining: {})", allCleared, remainingCount);
        return allCleared;
    }
}
