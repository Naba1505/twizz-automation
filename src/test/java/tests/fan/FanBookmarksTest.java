package tests.fan;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.fan.FanBookmarksPage;
import pages.fan.FanHomePage;

/**
 * Test class for Fan Bookmarks functionality.
 * Tests bookmarking feeds, viewing bookmarked feeds, and unbookmarking.
 */
public class FanBookmarksTest extends BaseFanTest {

    private static final int FEEDS_TO_BOOKMARK = 3;

    @Test(priority = 1, 
          description = "Fan can bookmark multiple feeds from home screen")
    public void fanCanBookmarkMultipleFeeds() {
        // Verify on home screen and click Home icon to navigate to feed screen
        FanHomePage home = new FanHomePage(page);
        home.assertOnHomeUrl();
        home.clickHomeIcon();

        // Bookmark multiple feeds using img.slideItemBookmark
        FanBookmarksPage bookmarks = new FanBookmarksPage(page);
        bookmarks.bookmarkMultipleFeeds(FEEDS_TO_BOOKMARK);

        // Verify at least 3 bookmarks are highlighted (bookmarkFill icons)
        boolean allHighlighted = bookmarks.verifyAllBookmarksHighlighted(FEEDS_TO_BOOKMARK);
        Assert.assertTrue(allHighlighted, 
                "Expected " + FEEDS_TO_BOOKMARK + " bookmarks to be highlighted after selection");
    }

    @Test(priority = 2, 
          description = "Fan can view bookmarked feeds in saved screen",
          dependsOnMethods = "fanCanBookmarkMultipleFeeds")
    public void fanCanViewBookmarkedFeeds() {
        // Verify on home screen
        FanHomePage home = new FanHomePage(page);
        home.assertOnHomeUrl();

        // Navigate to bookmarked feeds screen via Settings > Bookmarks
        FanBookmarksPage bookmarks = new FanBookmarksPage(page);
        bookmarks.navigateToBookmarkedFeeds();

        // Verify bookmarked feeds count matches (using img[alt='watermarked'])
        boolean countMatches = bookmarks.verifyBookmarkedFeedsCount(FEEDS_TO_BOOKMARK);
        Assert.assertTrue(countMatches, 
                "Expected " + FEEDS_TO_BOOKMARK + " bookmarked feeds on bookmarks screen");
    }

    @Test(priority = 3, 
          description = "Fan can unbookmark all feeds and verify 'No bookmarks found!' message",
          dependsOnMethods = "fanCanViewBookmarkedFeeds")
    public void fanCanUnbookmarkFeeds() {
        // Verify on home screen
        FanHomePage home = new FanHomePage(page);
        home.assertOnHomeUrl();

        // Navigate to bookmarked feeds screen
        FanBookmarksPage bookmarks = new FanBookmarksPage(page);
        bookmarks.navigateToBookmarkedFeeds();

        // Get initial count of bookmarked feeds
        int initialCount = bookmarks.getWatermarkedFeedsCount();
        
        // If no bookmarks exist, skip this test
        if (initialCount == 0) {
            throw new org.testng.SkipException("No bookmarks found to unbookmark. Skipping test.");
        }
        
        // Unbookmark all visible feeds (scroll down to find more)
        int unbookmarked = 0;
        int maxAttempts = 10; // Safety limit
        
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            // Get current count of watermarked feeds
            int currentCount = bookmarks.getWatermarkedFeedsCount();
            
            if (currentCount == 0) {
                // No more bookmarks visible, break
                break;
            }
            
            // Unbookmark one feed
            int result = bookmarks.unbookmarkFeedsFromScreen(1);
            if (result > 0) {
                unbookmarked++;
            }
            
            // Navigate back to settings page
            bookmarks.clickArrowLeft();
            
            // Hard refresh (stays on settings page)
            bookmarks.hardRefreshBrowser();
            
            // Click Bookmarks tile directly (already on settings page)
            bookmarks.clickBookmarksTile();
        }
        
        // Verify we unbookmarked at least 1 feed
        Assert.assertTrue(unbookmarked >= 1, 
                "Expected to unbookmark at least 1 feed but only unbookmarked " + unbookmarked);
        
        // Navigate back and forth to bookmarks screen until "No bookmarks found!" message appears
        boolean noBookmarksMessageVisible = false;
        int maxRetries = 5;
        
        for (int retry = 0; retry < maxRetries; retry++) {
            // Check if "No bookmarks found!" message is visible
            noBookmarksMessageVisible = bookmarks.verifyNoBookmarksFoundText();
            
            if (noBookmarksMessageVisible) {
                break;
            }
            
            // Navigate back to settings and refresh
            bookmarks.clickArrowLeft();
            bookmarks.hardRefreshBrowser();
            
            // Click Bookmarks tile again
            bookmarks.clickBookmarksTile();
        }
        
        Assert.assertTrue(noBookmarksMessageVisible, 
                "Expected 'No bookmarks found!' message to be visible after unbookmarking all feeds");
    }
}
