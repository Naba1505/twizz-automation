package pages.fan;

import pages.common.BasePage;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for Fan Clear Recent Searches functionality.
 * Handles clearing recent search history from the discover/search screen.
 */
public class FanClearSearchPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanClearSearchPage.class);
    private static final String DISCOVER_PATH_FRAGMENT = "/common/discover";
    
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    private static final int UI_UPDATE_WAIT = 200;        // Wait for UI to update after click
    private static final int VISIBILITY_TIMEOUT = 20000;  // Element visibility timeout
    private static final int STABILIZATION_WAIT = 1000;   // Wait for page to stabilize
    private static final int SHORT_WAIT = 500;            // Short wait for UI updates

    public FanClearSearchPage(Page page) {
        super(page);
    }

    @Step("Navigate to Discover screen via Search icon")
    public void navigateToDiscover() {
        logger.info("Navigating to Discover screen");
        
        // Check if already on discover screen
        String currentUrl = page.url();
        if (currentUrl.contains(DISCOVER_PATH_FRAGMENT)) {
            logger.info("Already on Discover screen, skipping navigation");
            return;
        }
        
        // Click Search icon to navigate to discover
        Locator searchIcon = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("Search icon"));
        waitVisible(searchIcon.first(), DEFAULT_WAIT);
        clickWithRetry(searchIcon.first(), 2, UI_UPDATE_WAIT);
        
        // Wait for discover screen to load
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**", 
                new Page.WaitForURLOptions().setTimeout(VISIBILITY_TIMEOUT));
        
        logger.info("Navigated to Discover screen");
    }

    @Step("Click on search field to open search interface")
    public void clickSearchField() {
        logger.info("Clicking on search field");
        
        // Click on the search field (5th div element)
        Locator searchField = page.locator("div").nth(5);
        waitVisible(searchField, DEFAULT_WAIT);
        clickWithRetry(searchField, 1, UI_UPDATE_WAIT);
        
        try { page.waitForTimeout(SHORT_WAIT); } catch (Throwable ignored) { }
        logger.info("Clicked on search field");
    }

    @Step("Verify 'Recent' text is displayed")
    public boolean isRecentTextVisible() {
        logger.info("Verifying 'Recent' text is displayed");
        
        Locator recentText = page.getByText("Recent");
        boolean visible = recentText.count() > 0 && safeIsVisible(recentText.first());
        
        logger.info("'Recent' text visible: {}", visible);
        return visible;
    }

    @Step("Get count of recent search items")
    public int getRecentSearchCount() {
        // Wait for search interface to fully load
        try { page.waitForTimeout(STABILIZATION_WAIT); } catch (Throwable ignored) { }
        
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
            // Click the first Remove icon
            Locator firstRemove = removeIcons.first();
            firstRemove.click();
            
            // Wait for UI to update
            try { page.waitForTimeout(STABILIZATION_WAIT); } catch (Throwable ignored) { }
            
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
            
            // Wait between removals
            try { page.waitForTimeout(UI_UPDATE_WAIT); } catch (Throwable ignored) { }
        }
        
        logger.info("Total recent searches cleared: {}", removedCount);
        return removedCount;
    }

    @Step("Verify all recent searches are cleared")
    public boolean verifyAllSearchesCleared() {
        logger.info("Verifying all recent searches are cleared");
        
        // Wait a bit for UI to update
        try { page.waitForTimeout(STABILIZATION_WAIT); } catch (Throwable ignored) { }
        
        int remainingCount = getRecentSearchCount();
        boolean allCleared = remainingCount == 0;
        
        logger.info("All searches cleared: {} (remaining: {})", allCleared, remainingCount);
        return allCleared;
    }
}
