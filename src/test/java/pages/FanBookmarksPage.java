package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;


/**
 * Page Object for Fan Bookmarks functionality.
 * Handles bookmarking feeds from home screen and managing bookmarked feeds.
 */
public class FanBookmarksPage extends BasePage {

    public FanBookmarksPage(Page page) {
        super(page);
    }

    // ===== Home Feed Bookmark Methods =====

    @Step("Scroll down to ensure feeds are visible")
    public void scrollToFeeds() {
        logger.info("Scrolling to ensure feeds are visible");
        for (int i = 0; i < 5; i++) {
            page.mouse().wheel(0, 400);
            try { page.waitForTimeout(300); } catch (Throwable ignored) { }
        }
    }

    @Step("Bookmark feed at index {index}")
    public void bookmarkFeedAtIndex(int index) {
        logger.info("Bookmarking feed at index {}", index);
        
        Locator bookmarkIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmark"));
        
        // Scroll until we have enough bookmark icons visible
        int maxScrollAttempts = 10;
        for (int i = 0; i < maxScrollAttempts && bookmarkIcons.count() <= index; i++) {
            page.mouse().wheel(0, 500);
            try { page.waitForTimeout(300); } catch (Throwable ignored) { }
            bookmarkIcons = page.getByRole(AriaRole.IMG, 
                    new Page.GetByRoleOptions().setName("bookmark"));
        }
        
        if (bookmarkIcons.count() > index) {
            Locator targetBookmark = bookmarkIcons.nth(index);
            waitVisible(targetBookmark, DEFAULT_WAIT);
            
            // Use force click to bypass any overlay
            try {
                targetBookmark.click(new Locator.ClickOptions().setForce(true));
            } catch (Throwable e) {
                logger.warn("Force click failed, retrying with standard click: {}", e.getMessage());
                clickWithRetry(targetBookmark, 1, 200);
            }
            
            logger.info("Clicked bookmark icon at index {}", index);
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        } else {
            throw new RuntimeException("Could not find bookmark icon at index " + index);
        }
    }

    @Step("Bookmark multiple feeds (first {count} feeds)")
    public int bookmarkMultipleFeeds(int count) {
        logger.info("Bookmarking first {} feeds", count);
        
        // First scroll to ensure feeds are loaded
        scrollToFeeds();
        
        // Wait for page to stabilize
        try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
        
        // Get all bookmark icons (unbookmarked ones)
        Locator bookmarkIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmark"));
        
        int availableCount = bookmarkIcons.count();
        logger.info("Found {} unbookmarked bookmark icons", availableCount);
        
        // Click first 3 bookmark icons directly
        int bookmarked = 0;
        
        if (availableCount >= 1) {
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("bookmark")).first().click();
            bookmarked++;
            logger.info("Bookmarked feed #1");
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        }
        
        if (availableCount >= 2 && count > 1) {
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("bookmark")).nth(1).click();
            bookmarked++;
            logger.info("Bookmarked feed #2");
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        }
        
        if (availableCount >= 3 && count > 2) {
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("bookmark")).nth(2).click();
            bookmarked++;
            logger.info("Bookmarked feed #3");
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        }
        
        // Verify bookmarkFill icons count
        Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmarkFill"));
        int bookmarkedCount = bookmarkFillIcons.count();
        logger.info("Total bookmarkFill icons after bookmarking: {}", bookmarkedCount);
        
        logger.info("Completed bookmarking - {} feeds bookmarked", bookmarked);
        return bookmarked;
    }

    @Step("Verify bookmark is highlighted/selected at index {index}")
    public boolean isBookmarkHighlightedAtIndex(int index) {
        // After bookmarking, the icon might change to a filled/highlighted state
        // Check for bookmarkFill or similar indicator
        Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmarkFill"));
        
        if (bookmarkFillIcons.count() > index) {
            boolean visible = safeIsVisible(bookmarkFillIcons.nth(index));
            logger.info("Bookmark at index {} is highlighted: {}", index, visible);
            return visible;
        }
        
        // Alternative: check if the bookmark icon has a different state/class
        Locator allBookmarks = page.locator("[alt='bookmark'], [alt='bookmarkFill'], img[alt*='bookmark']");
        if (allBookmarks.count() > index) {
            try {
                String alt = allBookmarks.nth(index).getAttribute("alt");
                boolean isHighlighted = alt != null && alt.contains("Fill");
                logger.info("Bookmark at index {} alt='{}', highlighted: {}", index, alt, isHighlighted);
                return isHighlighted;
            } catch (Throwable ignored) { }
        }
        
        return false;
    }

    @Step("Verify all {count} bookmarks are highlighted")
    public boolean verifyAllBookmarksHighlighted(int count) {
        logger.info("Verifying {} bookmarks are highlighted", count);
        
        // Check bookmarkFill icons count
        Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmarkFill"));
        int highlightedCount = bookmarkFillIcons.count();
        
        logger.info("Found {} bookmarkFill icons (expected {})", highlightedCount, count);
        
        if (highlightedCount >= count) {
            logger.info("All {} bookmarks are highlighted", count);
            return true;
        }
        
        logger.warn("Expected {} highlighted bookmarks but found {}", count, highlightedCount);
        return false;
    }

    @Step("Get count of highlighted bookmarks")
    public int getHighlightedBookmarkCount() {
        Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmarkFill"));
        int count = bookmarkFillIcons.count();
        logger.info("Found {} highlighted bookmarks", count);
        return count;
    }

    // ===== Bookmarked Feeds Screen Methods =====

    @Step("Click Settings icon from home screen")
    public void clickSettingsIcon() {
        logger.info("Clicking Settings icon");
        
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon")).click();
        
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        logger.info("Clicked Settings icon");
    }

    @Step("Click Bookmarks tile to navigate to bookmarks screen")
    public void clickBookmarksTile() {
        logger.info("Clicking Bookmarks tile");
        
        Locator bookmarksTile = page.getByText("Bookmarks");
        waitVisible(bookmarksTile.first(), DEFAULT_WAIT);
        clickWithRetry(bookmarksTile.first(), 1, 200);
        
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        logger.info("Clicked Bookmarks tile");
    }

    @Step("Verify on Bookmarks screen")
    public void verifyOnBookmarksScreen() {
        logger.info("Verifying on Bookmarks screen");
        
        Locator bookmarksTitle = page.getByText("Bookmarks");
        waitVisible(bookmarksTitle.first(), DEFAULT_WAIT);
        
        logger.info("Verified on Bookmarks screen - title visible");
    }

    @Step("Navigate to bookmarked feeds screen via Settings")
    public void navigateToBookmarkedFeeds() {
        logger.info("Navigating to bookmarked feeds screen");
        
        clickSettingsIcon();
        clickBookmarksTile();
        verifyOnBookmarksScreen();
        
        logger.info("Navigated to bookmarked feeds screen");
    }

    @Step("Click on watermarked feed at index {index}")
    public void clickWatermarkedFeedAtIndex(int index) {
        logger.info("Clicking watermarked feed at index {}", index);
        
        Locator watermarkedFeeds = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("watermarked"));
        
        if (watermarkedFeeds.count() > index) {
            Locator targetFeed = watermarkedFeeds.nth(index);
            waitVisible(targetFeed, DEFAULT_WAIT);
            clickWithRetry(targetFeed, 1, 200);
            
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
            logger.info("Clicked watermarked feed at index {}", index);
        } else {
            throw new RuntimeException("Could not find watermarked feed at index " + index + 
                    ". Found only " + watermarkedFeeds.count() + " feeds.");
        }
    }

    @Step("Verify bookmark icon is highlighted at index {index}")
    public boolean verifyBookmarkHighlightedAtIndex(int index) {
        logger.info("Verifying bookmark is highlighted at index {}", index);
        
        Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmarkFill"));
        
        if (bookmarkFillIcons.count() > index) {
            boolean visible = safeIsVisible(bookmarkFillIcons.nth(index));
            logger.info("Bookmark at index {} is highlighted: {}", index, visible);
            return visible;
        }
        
        logger.warn("Could not find bookmarkFill icon at index {}", index);
        return false;
    }

    @Step("Verify bookmarked feeds count matches {expectedCount}")
    public boolean verifyBookmarkedFeedsCount(int expectedCount) {
        logger.info("Verifying {} bookmarked feeds are displayed", expectedCount);
        
        // Wait for page to load
        try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
        
        // Use //img[@alt='watermarked'] to count bookmarked feeds
        Locator watermarkedFeeds = page.locator("img[alt='watermarked']");
        int feedCount = watermarkedFeeds.count();
        logger.info("Found {} watermarked feeds on bookmarks screen (expected {})", feedCount, expectedCount);
        
        if (feedCount == expectedCount) {
            logger.info("Bookmark count matches: {} feeds", feedCount);
            return true;
        }
        
        logger.warn("Bookmark count mismatch: expected {} but found {}", expectedCount, feedCount);
        return false;
    }

    @Step("Verify bookmarked feeds are displayed")
    public boolean verifyBookmarkedFeedsDisplayed() {
        logger.info("Verifying bookmarked feeds are displayed");
        
        // Check if there are any feed items on the bookmarked screen
        Locator feedItems = page.locator(".feed-item, [class*='feed'], [class*='post']");
        
        if (feedItems.count() > 0) {
            logger.info("Found {} bookmarked feed items", feedItems.count());
            return true;
        }
        
        // Alternative: check for bookmark fill icons (indicating saved items)
        Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmarkFill"));
        
        if (bookmarkFillIcons.count() > 0) {
            logger.info("Found {} bookmarked items via bookmarkFill icons", bookmarkFillIcons.count());
            return true;
        }
        
        logger.warn("No bookmarked feeds found");
        return false;
    }

    @Step("Unbookmark feed at index {index}")
    public void unbookmarkFeedAtIndex(int index) {
        logger.info("Unbookmarking feed at index {}", index);
        
        // Look for bookmarkFill icons (already bookmarked)
        Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmarkFill"));
        
        if (bookmarkFillIcons.count() > index) {
            Locator targetBookmark = bookmarkFillIcons.nth(index);
            waitVisible(targetBookmark, DEFAULT_WAIT);
            
            try {
                targetBookmark.click(new Locator.ClickOptions().setForce(true));
            } catch (Throwable e) {
                logger.warn("Force click failed, retrying: {}", e.getMessage());
                clickWithRetry(targetBookmark, 1, 200);
            }
            
            logger.info("Clicked to unbookmark at index {}", index);
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        } else {
            throw new RuntimeException("Could not find bookmarkFill icon at index " + index);
        }
    }

    @Step("Unbookmark all visible bookmarked feeds")
    public int unbookmarkAllFeeds() {
        logger.info("Unbookmarking all visible bookmarked feeds");
        
        int unbookmarkedCount = 0;
        int maxAttempts = 20; // Safety limit
        
        for (int i = 0; i < maxAttempts; i++) {
            Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                    new Page.GetByRoleOptions().setName("bookmarkFill"));
            
            if (bookmarkFillIcons.count() == 0) {
                logger.info("No more bookmarked feeds to unbookmark");
                break;
            }
            
            // Always click the first one (as list shifts after unbookmark)
            try {
                bookmarkFillIcons.first().click(new Locator.ClickOptions().setForce(true));
                unbookmarkedCount++;
                logger.info("Unbookmarked feed #{}", unbookmarkedCount);
                try { page.waitForTimeout(500); } catch (Throwable ignored) { }
            } catch (Throwable e) {
                logger.warn("Failed to unbookmark: {}", e.getMessage());
                break;
            }
        }
        
        logger.info("Total feeds unbookmarked: {}", unbookmarkedCount);
        return unbookmarkedCount;
    }

    @Step("Verify no bookmarked feeds remain")
    public boolean verifyNoBookmarkedFeeds() {
        logger.info("Verifying no bookmarked feeds remain");
        
        Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmarkFill"));
        
        int count = bookmarkFillIcons.count();
        boolean noBookmarks = count == 0;
        
        logger.info("Bookmarked feeds remaining: {}, verification: {}", count, noBookmarks);
        return noBookmarks;
    }

    @Step("Unbookmark all feeds from bookmarks screen")
    public void unbookmarkAllFromBookmarksScreen() {
        logger.info("Unbookmarking all feeds from bookmarks screen");
        
        // Unbookmark feed #1: Click watermarked feed, then click bookmarkFill
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("watermarked")).first().click();
        logger.info("Clicked on watermarked feed #1");
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("bookmarkFill")).first().click();
        logger.info("Unbookmarked feed #1");
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        
        // Unbookmark feed #2: Click next bookmarkFill
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("bookmarkFill")).first().click();
        logger.info("Unbookmarked feed #2");
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        
        // Unbookmark feed #3: Click next bookmarkFill
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("bookmarkFill")).click();
        logger.info("Unbookmarked feed #3");
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        
        logger.info("Completed unbookmarking from bookmarks screen");
    }

    @Step("Click arrow left to navigate back")
    public void clickArrowLeft() {
        logger.info("Clicking arrow left to navigate back");
        
        Locator arrowLeft = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("arrow left"));
        waitVisible(arrowLeft.first(), DEFAULT_WAIT);
        clickWithRetry(arrowLeft.first(), 1, 200);
        
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        logger.info("Clicked arrow left");
    }

    @Step("Hard refresh browser")
    public void hardRefreshBrowser() {
        logger.info("Hard refreshing browser");
        page.reload(new Page.ReloadOptions().setWaitUntil(com.microsoft.playwright.options.WaitUntilState.LOAD));
        try { page.waitForTimeout(2000); } catch (Throwable ignored) { }
        logger.info("Browser refreshed");
    }

    @Step("Verify 'No bookmarks found!' text is displayed")
    public boolean verifyNoBookmarksFoundText() {
        logger.info("Verifying 'No bookmarks found!' text is displayed");
        
        // Wait for page to load
        try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
        
        Locator noBookmarksText = page.getByText("No bookmarks found!");
        boolean visible = noBookmarksText.count() > 0 && noBookmarksText.isVisible();
        
        logger.info("'No bookmarks found!' text visible: {}", visible);
        return visible;
    }

    // ===== Navigation Helpers =====

    @Step("Navigate back to home")
    public void navigateBackToHome() {
        logger.info("Navigating back to home");
        
        Locator backArrow = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("arrow left"));
        
        if (backArrow.count() > 0 && safeIsVisible(backArrow.first())) {
            clickWithRetry(backArrow.first(), 1, 200);
        } else {
            // Try home icon
            Locator homeIcon = page.getByRole(AriaRole.IMG, 
                    new Page.GetByRoleOptions().setName("home"));
            if (homeIcon.count() > 0 && safeIsVisible(homeIcon.first())) {
                clickWithRetry(homeIcon.first(), 1, 200);
            }
        }
        
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
    }

    @Step("Scroll to top of feed")
    public void scrollToTop() {
        logger.info("Scrolling to top of feed");
        for (int i = 0; i < 10; i++) {
            page.mouse().wheel(0, -1200);
            try { page.waitForTimeout(200); } catch (Throwable ignored) { }
        }
    }
}
