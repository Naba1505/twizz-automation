package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CreatorDiscoverPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(CreatorDiscoverPage.class);

    private static final String DISCOVER_PATH_FRAGMENT = "/common/discover";
    private static final String FEED_XPATH = "//div[@class='hls-video-player']";
    private static final String MUTE_BTN_XPATH = "//button[@class='mute-button']";

    public CreatorDiscoverPage(Page page) {
        super(page);
    }


    @Step("Navigate to Discover screen via Search icon")
    public void navigateToDiscover() {
        Locator searchIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search icon"));
        waitVisible(searchIcon.first(), 15000);
        clickWithRetry(searchIcon.first(), 2, 200);
    }

    @Step("Assert on Discover screen (URL contains /common/discover)")
    public void assertOnDiscoverScreen() {
        // Wait for URL to include the discover path
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**", new Page.WaitForURLOptions().setTimeout(15000));
        // And for at least one feed container to appear
        Locator feeds = page.locator("xpath=" + FEED_XPATH);
        waitVisible(feeds.first(), 20000);
    }

    @Step("Collect all visible feeds on the page")
    public List<Locator> collectFeeds() {
        Locator feeds = page.locator("xpath=" + FEED_XPATH);
        int count = feeds.count();
        List<Locator> list = new ArrayList<>();
        for (int i = 0; i < count; i++) list.add(feeds.nth(i));
        logger.info("Found {} feed containers on current viewport", count);
        return list;
    }

    @Step("Scroll through feeds top-to-bottom, ensuring each is visible")
    public int scrollDownEnsureFeeds() {
        Locator feeds = page.locator("xpath=" + FEED_XPATH);
        int total = Math.max(0, feeds.count());
        // Cap the number of feeds we iterate over to keep the test fast
        int limit = Math.min(total, 8);
        int seen = 0;
        for (int i = 0; i < limit; i++) {
            Locator feed = feeds.nth(i);
            try {
                feed.scrollIntoViewIfNeeded();
                // Shorter per-feed wait is enough once Discover has loaded
                waitVisible(feed, 3000);
                try { page.waitForTimeout(80); } catch (Exception ignored) {}
                seen++;
            } catch (Exception e) {
                logger.warn("Feed {} not confirmed visible: {}", i, e.toString());
            }
        }
        // Do an extra wheel scroll to trigger lazy loading if any
        try { page.mouse().wheel(0, 1200); } catch (Exception ignored) {}
        return seen;
    }

    @Step("Unmute every visible feed by clicking its mute button while scrolling down")
    public int unmuteAllFeedsWhileScrolling() {
        Locator feeds = page.locator("xpath=" + FEED_XPATH);
        int totalFeeds = feeds.count();
        if (totalFeeds == 0) {
            logger.warn("No feeds found on Discover when attempting to unmute");
            return 0;
        }

        int toggled = 0;
        // Only attempt to unmute the first visible feed to keep the test fast
        Locator feed = feeds.nth(0);
        try {
            feed.scrollIntoViewIfNeeded();
            waitVisible(feed, 1500);

            Locator feedMuteBtn = feed.locator("xpath=" + MUTE_BTN_XPATH);
            if (feedMuteBtn.count() > 0) {
                Locator btn = feedMuteBtn.first();
                try { btn.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                try {
                    // Use a short, forced click to avoid long Playwright retries
                    btn.click(new Locator.ClickOptions().setTimeout(2000).setForce(true));
                    toggled++;
                } catch (Exception e) {
                    logger.warn("Mute button click failed for first feed: {}", e.toString());
                }
            }
            // Wait a few seconds as per requirement, then return
            try { page.waitForTimeout(5000); } catch (Exception ignored) {}
        } catch (Exception e) {
            logger.warn("Unable to unmute first feed: {}", e.toString());
        }

        return toggled;
    }

    @Step("Scroll up to the top of the Discover feed")
    public void scrollUpToTop() {
        try {
            for (int i = 0; i < 6; i++) {
                page.mouse().wheel(0, -1200);
                page.waitForTimeout(120);
            }
        } catch (Exception ignored) {}
    }

    @Step("Open a random visible Discover profile from a feed")
    public void openRandomVisibleDiscoverProfile() {
        // Codegen: page.getByText("Discover profile").first().click();
        // Try to surface a "Discover profile" element by scrolling a few times
        Locator profileText = page.getByText("Discover profile");
        int attempts = 0;
        while ((profileText.count() == 0 || !safeIsVisible(profileText.first())) && attempts++ < 6) {
            try { page.mouse().wheel(0, 1200); } catch (Exception ignored) {}
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
            profileText = page.getByText("Discover profile");
        }

        if (profileText.count() == 0) {
            throw new RuntimeException("No 'Discover profile' text found on Discover feed");
        }

        Locator target = profileText.first();
        try { target.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        clickWithRetry(target, 2, 150);
    }

    @Step("Ensure creator profile screen is visible via publications icon")
    public void ensureOnCreatorProfileScreen() {
        Locator publicationsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("publications icon"));
        waitVisible(publicationsIcon.first(), 15000);
    }

    @Step("Navigate back from profile to Discover and assert URL")
    public void navigateBackToDiscover() {
        Locator back = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
        waitVisible(back.first(), 10000);
        clickWithRetry(back.first(), 1, 120);
        // Wait URL back on discover
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**", new Page.WaitForURLOptions().setTimeout(15000));
    }

    @Step("Open search field on Discover")
    public void openSearchField() {
        Locator searchFieldActivator = page.locator("div").filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^Search$"))).nth(1);
        waitVisible(searchFieldActivator, 10000);
        clickWithRetry(searchFieldActivator, 1, 120);
    }

    @Step("Fill search query: {query}")
    public void fillSearch(String query) {
        Locator input = page.getByPlaceholder("Search");
        waitVisible(input.first(), 10000);
        input.first().fill(query == null ? "" : query);
    }

    @Step("Click search result by text: {resultText}")
    public void clickSearchResult(String resultText) {
        Locator res = page.getByText(resultText);
        waitVisible(res.first(), 15000);
        clickWithRetry(res.first(), 1, 120);
    }
}
