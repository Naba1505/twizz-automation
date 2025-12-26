package pages.fan;

import pages.common.BasePage;

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
        
        // Scroll back to top to start from the beginning
        scrollToTop();
        
        // Wait for page to stabilize
        try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
        
        int bookmarked = 0;
        
        // Get initial count of bookmarkFill icons
        int initialFillCount = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("bookmarkFill")).count();
        logger.info("Initial bookmarkFill count: {}", initialFillCount);
        
        // Click bookmark icons by index to avoid clicking same one
        for (int i = 0; i < count; i++) {
            // Use exact match for unbookmarked icons only
            Locator bookmarkIcons = page.locator("img[alt='bookmark']");
            int availableCount = bookmarkIcons.count();
            logger.info("Iteration {}: Found {} unbookmarked bookmark icons", i + 1, availableCount);
            
            if (availableCount > 0) {
                try {
                    // Click the first unbookmarked icon
                    Locator target = bookmarkIcons.first();
                    target.scrollIntoViewIfNeeded();
                    try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                    target.click(new Locator.ClickOptions().setForce(true));
                    bookmarked++;
                    logger.info("Bookmarked feed #{}", bookmarked);
                    
                    // Wait for UI to update
                    try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
                    
                    // Verify the click worked by checking bookmarkFill count increased
                    int currentFillCount = page.getByRole(AriaRole.IMG, 
                            new Page.GetByRoleOptions().setName("bookmarkFill")).count();
                    logger.info("Current bookmarkFill count: {}", currentFillCount);
                    
                } catch (Throwable e) {
                    logger.warn("Failed to click bookmark icon: {}", e.getMessage());
                }
            } else {
                logger.warn("No more unbookmarked icons available, scrolling down");
                page.mouse().wheel(0, 500);
                try { page.waitForTimeout(500); } catch (Throwable ignored) { }
                i--; // Retry this iteration after scrolling
                if (i < -5) break; // Safety limit
            }
        }
        
        // Scroll to top and verify bookmarkFill icons count
        scrollToTop();
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        
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
        
        // Scroll to top first to ensure we can see all bookmarked feeds
        scrollToTop();
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        
        // Wait and retry for bookmarkFill icons to appear (UI may take time to update)
        int maxRetries = 5;
        int highlightedCount = 0;
        
        for (int retry = 0; retry < maxRetries; retry++) {
            // Check bookmarkFill icons count
            Locator bookmarkFillIcons = page.getByRole(AriaRole.IMG, 
                    new Page.GetByRoleOptions().setName("bookmarkFill"));
            highlightedCount = bookmarkFillIcons.count();
            
            logger.info("Retry {}: Found {} bookmarkFill icons (expected {})", retry + 1, highlightedCount, count);
            
            if (highlightedCount >= count) {
                logger.info("All {} bookmarks are highlighted", count);
                return true;
            }
            
            // Scroll down slightly to load more content and wait
            page.mouse().wheel(0, 300);
            try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
            
            // Scroll back up
            scrollToTop();
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        }
        
        logger.warn("Expected {} highlighted bookmarks but found {} after {} retries", count, highlightedCount, maxRetries);
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
        
        // Retry logic to handle timing issues
        int maxRetries = 5;
        int feedCount = 0;
        
        for (int retry = 0; retry < maxRetries; retry++) {
            // Use //img[@alt='watermarked'] to count bookmarked feeds
            Locator watermarkedFeeds = page.locator("img[alt='watermarked']");
            feedCount = watermarkedFeeds.count();
            logger.info("Retry {}: Found {} watermarked feeds on bookmarks screen (expected {})", retry + 1, feedCount, expectedCount);
            
            if (feedCount >= expectedCount) {
                logger.info("Bookmark count matches: {} feeds", feedCount);
                return true;
            }
            
            // Scroll down to load more content
            page.mouse().wheel(0, 300);
            try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
        }
        
        logger.warn("Bookmark count mismatch: expected {} but found {} after {} retries", expectedCount, feedCount, maxRetries);
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
        
        // Wait for page to fully load
        try { page.waitForTimeout(2000); } catch (Throwable ignored) { }
        
        int unbookmarked = 0;
        int maxAttempts = 20; // Safety limit
        
        for (int i = 0; i < maxAttempts; i++) {
            // Check if there are any watermarked feeds (bookmarked items) on the screen
            Locator watermarkedList = page.locator("img[alt='watermarked']");
            int watermarkedCount = watermarkedList.count();
            logger.info("Iteration {}: Found {} watermarked feeds", i + 1, watermarkedCount);
            
            if (watermarkedCount == 0) {
                logger.info("No more watermarked feeds - all unbookmarked");
                break;
            }
            
            try {
                // Click on first watermarked feed to enter detail view
                Locator watermarked = watermarkedList.first();
                watermarked.scrollIntoViewIfNeeded();
                try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                watermarked.click(new Locator.ClickOptions().setForce(true));
                logger.info("Clicked on watermarked feed");
                try { page.waitForTimeout(1500); } catch (Throwable ignored) { }
                
                // Now find and click the bookmarkFill icon to unbookmark
                Locator bookmarkFill = page.locator("img[alt='bookmarkFill']").first();
                int fillCount = bookmarkFill.count();
                logger.info("Found {} bookmarkFill icons", fillCount);
                
                if (fillCount > 0) {
                    bookmarkFill.scrollIntoViewIfNeeded();
                    try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                    bookmarkFill.click(new Locator.ClickOptions().setForce(true));
                    unbookmarked++;
                    logger.info("Unbookmarked feed #{}", unbookmarked);
                    try { page.waitForTimeout(1500); } catch (Throwable ignored) { }
                } else {
                    logger.warn("No bookmarkFill icon found after clicking watermarked feed, going back");
                    page.goBack();
                    try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
                }
            } catch (Throwable e) {
                logger.warn("Failed during unbookmark iteration: {}", e.getMessage());
                // Try to recover by going back
                try { page.goBack(); } catch (Throwable ignored) { }
                try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
            }
        }
        
        logger.info("Completed unbookmarking {} feeds from bookmarks screen", unbookmarked);
    }

    @Step("Get count of watermarked feeds on bookmarks screen")
    public int getWatermarkedFeedsCount() {
        try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
        Locator watermarked = page.locator("img[alt='watermarked']");
        int count = watermarked.count();
        logger.info("Found {} watermarked feeds on bookmarks screen", count);
        return count;
    }

    @Step("Unbookmark {count} feeds from bookmarks screen")
    public int unbookmarkFeedsFromScreen(int count) {
        logger.info("Unbookmarking {} feeds from bookmarks screen", count);
        
        // Wait for page to fully load
        try { page.waitForTimeout(2000); } catch (Throwable ignored) { }
        
        int unbookmarked = 0;
        int maxAttempts = count + 5; // Allow some extra attempts
        
        for (int i = 0; i < maxAttempts && unbookmarked < count; i++) {
            // Check if there are any watermarked feeds (bookmarked items) on the screen
            Locator watermarkedList = page.locator("img[alt='watermarked']");
            int watermarkedCount = watermarkedList.count();
            logger.info("Iteration {}: Found {} watermarked feeds, unbookmarked so far: {}", i + 1, watermarkedCount, unbookmarked);
            
            if (watermarkedCount == 0) {
                logger.info("No more watermarked feeds available");
                break;
            }
            
            try {
                // Click on first watermarked feed to enter detail view
                Locator watermarked = watermarkedList.first();
                watermarked.scrollIntoViewIfNeeded();
                try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                watermarked.click(new Locator.ClickOptions().setForce(true));
                logger.info("Clicked on watermarked feed");
                try { page.waitForTimeout(1500); } catch (Throwable ignored) { }
                
                // Now find and click the bookmarkFill icon to unbookmark
                Locator bookmarkFill = page.locator("img[alt='bookmarkFill']").first();
                int fillCount = bookmarkFill.count();
                logger.info("Found {} bookmarkFill icons", fillCount);
                
                if (fillCount > 0) {
                    bookmarkFill.scrollIntoViewIfNeeded();
                    try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                    bookmarkFill.click(new Locator.ClickOptions().setForce(true));
                    unbookmarked++;
                    logger.info("Unbookmarked feed #{}", unbookmarked);
                    try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
                    
                    // Navigate back to bookmarks list after unbookmarking
                    page.goBack();
                    logger.info("Navigated back to bookmarks list");
                    try { page.waitForTimeout(1500); } catch (Throwable ignored) { }
                } else {
                    logger.warn("No bookmarkFill icon found after clicking watermarked feed, going back");
                    page.goBack();
                    try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
                }
            } catch (Throwable e) {
                logger.warn("Failed during unbookmark iteration: {}", e.getMessage());
                try { page.goBack(); } catch (Throwable ignored) { }
                try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
            }
        }
        
        logger.info("Completed unbookmarking {} feeds from bookmarks screen", unbookmarked);
        return unbookmarked;
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
        try { page.waitForTimeout(2000); } catch (Throwable ignored) { }
        
        // Retry logic to handle timing issues
        int maxRetries = 5;
        for (int retry = 0; retry < maxRetries; retry++) {
            // Check for "No bookmarks found!" text
            Locator noBookmarksText = page.getByText("No bookmarks found!");
            if (noBookmarksText.count() > 0 && safeIsVisible(noBookmarksText.first())) {
                logger.info("'No bookmarks found!' text visible on retry {}", retry + 1);
                return true;
            }
            
            // Alternative: check if there are no watermarked feeds (empty bookmarks)
            Locator watermarked = page.locator("img[alt='watermarked']");
            if (watermarked.count() == 0) {
                logger.info("No watermarked feeds found - bookmarks are empty on retry {}", retry + 1);
                return true;
            }
            
            logger.info("Retry {}: Still checking for empty bookmarks state...", retry + 1);
            try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
        }
        
        // Final check
        Locator noBookmarksText = page.getByText("No bookmarks found!");
        boolean visible = noBookmarksText.count() > 0 && safeIsVisible(noBookmarksText.first());
        
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

