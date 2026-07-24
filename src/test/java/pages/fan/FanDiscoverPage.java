package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

public class FanDiscoverPage extends BasePage {

    private static final int SCROLL_DOWN_LARGE = 1200;
    private static final int SCROLL_UP_LARGE = 1200;

    private static final String DISCOVER_PATH_FRAGMENT = "/common/discover";
    private static final String FEED_XPATH = "//div[@class='hls-video-player']";
    private static final String SEARCH_TEXT = "Search";

    public FanDiscoverPage(Page page) {
        super(page);
    }

    @Step("Navigate to Discover screen via Search icon (Fan)")
    public void navigateToDiscover() {
        Locator searchIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search icon"));
        waitVisible(searchIcon.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(searchIcon.first(), 2, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert on Discover screen (URL contains /common/discover)")
    public void assertOnDiscoverScreen() {
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        // And for at least one feed container / Search icon to appear
        Locator searchIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search icon"));
        waitVisible(searchIcon.first(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Scroll through feeds top-to-bottom, ensuring each is visible")
    public int scrollDownEnsureFeeds() {
        Locator visibleFeeds = page.locator(".hls-video-player:visible");
        waitVisible(visibleFeeds.first(), ConfigReader.getVisibilityTimeout());
        Locator feeds = page.locator("xpath=" + FEED_XPATH);
        int total = Math.max(0, feeds.count());
        int seen = 0;
        for (int i = 0; i < total; i++) {
            Locator feed = feeds.nth(i);
            try {
                feed.scrollIntoViewIfNeeded();
                waitVisible(feed, ConfigReader.getShortTimeout());
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Exception e2) { logger.debug("Feed scroll wait failed: {}", e2.getMessage()); }
                seen++;
            } catch (Exception e) {
                logger.warn("Feed {} not confirmed visible: {}", i, e.toString());
            }
        }
        try { page.mouse().wheel(0, SCROLL_DOWN_LARGE); } catch (Exception e) { logger.debug("Scroll wheel failed: {}", e.getMessage()); }
        logger.info("Scrolled through {} feeds", seen);
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
                // Scope mute button search to this feed's parent container
                Locator feedMuteBtn = feed.locator("xpath=ancestor::div//button[@class='mute-button']");
                if (feedMuteBtn.count() > 0 && safeIsVisible(feedMuteBtn.first())) {
                    clickWithRetry(feedMuteBtn.first(), 1, ConfigReader.getElementRetryDelay());
                    toggled++;
                }
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Exception e2) { logger.debug("Unmute wait failed: {}", e2.getMessage()); }
            } catch (Exception e) {
                logger.warn("Unable to unmute feed {}: {}", i, e.toString());
            }
        }
        logger.info("Unmuted {} feeds", toggled);
        return toggled;
    }

    @Step("Scroll up to the top of the Discover feed")
    public void scrollUpToTop() {
        for (int i = 0; i < 6; i++) {
            try { page.mouse().wheel(0, -SCROLL_UP_LARGE); } catch (Exception e) { logger.debug("Scroll failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getScrollWaitBetween()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
    }

    @Step("Open a random visible Discover profile from a feed")
    public void openRandomVisibleDiscoverProfile() {
        Locator visibleFeeds = page.locator(".hls-video-player:visible");
        waitVisible(visibleFeeds.first(), ConfigReader.getVisibilityTimeout());

        Locator target = null;
        int attempts = 0;
        while (target == null && attempts++ < ConfigReader.getMaxScrollAttempts()) {
            Locator profileText = page.getByText("Discover profile");
            for (int i = 0; i < profileText.count(); i++) {
                Locator candidate = profileText.nth(i);
                if (safeIsVisible(candidate)) {
                    target = candidate;
                    break;
                }
            }
            if (target == null) {
                try { page.mouse().wheel(0, ConfigReader.getScrollStepSize()); } catch (Exception e) { logger.debug("Scroll failed: {}", e.getMessage()); }
                try { page.waitForTimeout(ConfigReader.getScrollWaitBetween()); } catch (Exception e) { logger.debug("Wait failed: {}", e.getMessage()); }
            }
        }

        if (target == null) {
            throw new RuntimeException("No visible 'Discover profile' text found on Fan Discover feed");
        }

        try { target.scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("ScrollIntoView failed: {}", e.getMessage()); }
        clickWithRetry(target, 2, ConfigReader.getElementRetryDelay());
    }

    @Step("Ensure creator profile screen is visible via publications icon")
    public void ensureOnCreatorProfileScreen() {
        Locator publicationsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("publications icon"));
        waitVisible(publicationsIcon.first(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Navigate back from profile to Discover and assert URL")
    public void navigateBackToDiscover() {
        Locator back = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
        waitVisible(back.first(), ConfigReader.getShortTimeout());
        clickWithRetry(back.first(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
    }

    @Step("Open search field on Discover")
    public void openSearchField() {
        Locator searchFieldActivator = page.getByText(SEARCH_TEXT).first();
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

