package tests.fan;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.fan.FanBookmarksPage;
import pages.fan.FanHomePage;
import pages.fan.FanLoginPage;
import utils.ConfigReader;

/**
 * Test class for Fan Bookmarks functionality.
 * Tests bookmarking feeds, viewing bookmarked feeds, and unbookmarking.
 */
public class FanBookmarksTest extends BaseTestClass {

    private static final int FEEDS_TO_BOOKMARK = 3;

    @Test(priority = 1, 
          description = "Fan can bookmark multiple feeds from home screen")
    public void fanCanBookmarkMultipleFeeds() {
        // Arrange: credentials
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");

        // Login as Fan
        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

        // Verify on home screen
        FanHomePage home = new FanHomePage(page);
        home.assertOnHomeUrl();

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
        // Arrange: credentials
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");

        // Login as Fan
        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

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
          description = "Fan can unbookmark feeds from saved screen",
          dependsOnMethods = "fanCanViewBookmarkedFeeds")
    public void fanCanUnbookmarkFeeds() {
        // Arrange: credentials
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");

        // Login as Fan
        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

        // Verify on home screen
        FanHomePage home = new FanHomePage(page);
        home.assertOnHomeUrl();

        // Navigate to bookmarked feeds screen
        FanBookmarksPage bookmarks = new FanBookmarksPage(page);
        bookmarks.navigateToBookmarkedFeeds();

        // Get initial count of bookmarked feeds
        int initialCount = bookmarks.getWatermarkedFeedsCount();
        
        // Unbookmark feeds from bookmarks screen (at least FEEDS_TO_BOOKMARK)
        int unbookmarked = bookmarks.unbookmarkFeedsFromScreen(FEEDS_TO_BOOKMARK);
        
        // Verify we unbookmarked at least the expected number of feeds
        Assert.assertTrue(unbookmarked >= FEEDS_TO_BOOKMARK, 
                "Expected to unbookmark at least " + FEEDS_TO_BOOKMARK + " feeds but only unbookmarked " + unbookmarked);
        
        // Navigate back and verify count decreased
        bookmarks.clickArrowLeft();
        bookmarks.hardRefreshBrowser();
        bookmarks.clickBookmarksTile();
        
        int finalCount = bookmarks.getWatermarkedFeedsCount();
        Assert.assertTrue(finalCount < initialCount, 
                "Expected bookmark count to decrease. Initial: " + initialCount + ", Final: " + finalCount);
    }
}
