package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

public class CreatorDiscoverPage extends BasePage {

    private static final String DISCOVER_PATH_FRAGMENT = "/common/discover";
    private static final String FEED_XPATH = "//div[@class='hls-video-player']";

    public CreatorDiscoverPage(Page page) {
        super(page);
    }


    @Step("Navigate to Discover screen via Search icon")
    public void navigateToDiscover() {
        Locator searchIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search icon"));
        waitVisible(searchIcon.first(), ConfigReader.getShortTimeout());
        clickWithRetry(searchIcon.first(), 2, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert on Discover screen (URL contains /common/discover)")
    public void assertOnDiscoverScreen() {
        // Wait for URL to include the discover path
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        // And for at least one feed container to appear
        Locator feeds = page.locator("xpath=" + FEED_XPATH);
        waitVisible(feeds.first(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Scroll through feeds top-to-bottom, ensuring each is visible")
    public int scrollDownEnsureFeeds() {
        Locator feeds = page.locator("xpath=" + FEED_XPATH);
        int total = Math.max(0, feeds.count());
        int seen = 0;
        for (int i = 0; i < total; i++) {
            Locator feed = feeds.nth(i);
            try {
                feed.scrollIntoViewIfNeeded();
                waitVisible(feed, ConfigReader.getShortTimeout());
                page.waitForTimeout(ConfigReader.getElementRetryDelay());
                seen++;
            } catch (Exception e) {
                logger.warn("Feed {} not confirmed visible: {}", i, e.toString());
            }
        }
        try { page.mouse().wheel(0, 1200); } catch (Exception e) { logger.debug("Scroll wheel failed: {}", e.getMessage()); }
        return seen;
    }

    @Step("Unmute every visible feed by clicking its mute button while scrolling down")
    public int unmuteAllFeedsWhileScrolling() {
        Locator feeds = page.locator("xpath=" + FEED_XPATH);
        int toggled = 0;
        int totalFeeds = feeds.count();
        for (int i = 0; i < totalFeeds; i++) {
            Locator feed = feeds.nth(i);
            try {
                feed.scrollIntoViewIfNeeded();
                waitVisible(feed, ConfigReader.getShortTimeout());
                // Scope mute button search to this feed's parent container for O(1) lookup
                Locator feedMuteBtn = feed.locator("xpath=ancestor::div//button[@class='mute-button']");
                if (feedMuteBtn.count() > 0 && safeIsVisible(feedMuteBtn.first())) {
                    clickWithRetry(feedMuteBtn.first(), 1, ConfigReader.getElementRetryDelay());
                    toggled++;
                }
                page.waitForTimeout(ConfigReader.getElementRetryDelay());
            } catch (Exception e) {
                logger.warn("Unable to unmute feed {}: {}", i, e.toString());
            }
        }
        return toggled;
    }

    @Step("Scroll up to the top of the Discover feed")
    public void scrollUpToTop() {
        try {
            for (int i = 0; i < 6; i++) {
                page.mouse().wheel(0, -1200);
                page.waitForTimeout(ConfigReader.getElementRetryDelay());
            }
        } catch (Exception e) { logger.debug("ScrollUpToTop failed: {}", e.getMessage()); }
    }

    @Step("Open a random visible Discover profile from a feed")
    public void openRandomVisibleDiscoverProfile() {
        // Codegen: page.getByText("Discover profile").first().click();
        // Try to surface a "Discover profile" element by scrolling a few times
        Locator profileText = page.getByText("Discover profile");
        int attempts = 0;
        while (!safeIsVisible(profileText.first()) && attempts++ < 6) {
            try { page.mouse().wheel(0, 1200); } catch (Exception e) { logger.debug("Scroll failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Exception e) { logger.debug("Wait failed: {}", e.getMessage()); }
            profileText = page.getByText("Discover profile");
        }

        if (profileText.count() == 0) {
            throw new RuntimeException("No 'Discover profile' text found on Discover feed");
        }

        Locator target = profileText.first();
        try { target.scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("ScrollIntoView failed: {}", e.getMessage()); }
        clickWithRetry(target, 2, ConfigReader.getElementRetryDelay());
    }

    @Step("Ensure creator profile screen is visible via publications icon")
    public void ensureOnCreatorProfileScreen() {
        Locator publicationsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("publications icon"));
        waitVisible(publicationsIcon.first(), ConfigReader.getShortTimeout());
    }

    @Step("Navigate back from profile to Discover and assert URL")
    public void navigateBackToDiscover() {
        Locator back = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
        waitVisible(back.first(), ConfigReader.getShortTimeout());
        clickWithRetry(back.first(), 1, ConfigReader.getElementRetryDelay());
        // Wait URL back on discover
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
    }

    @Step("Open search field on Discover")
    public void openSearchField() {
        Locator searchFieldActivator = page.getByText("Search").first();
        waitVisible(searchFieldActivator, ConfigReader.getShortTimeout());
        clickWithRetry(searchFieldActivator, 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Fill search query: {query}")
    public void fillSearch(String query) {
        Locator input = page.getByPlaceholder("Search");
        waitVisible(input.first(), ConfigReader.getShortTimeout());
        input.first().fill(query == null ? "" : query);
    }

    @Step("Click search result by text: {resultText}")
    public void clickSearchResult(String resultText) {
        Locator res = page.getByText(resultText);
        waitVisible(res.first(), ConfigReader.getShortTimeout());
        clickWithRetry(res.first(), 1, ConfigReader.getElementRetryDelay());
    }
}

