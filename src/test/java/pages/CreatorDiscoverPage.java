package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

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
        int seen = 0;
        for (int i = 0; i < total; i++) {
            Locator feed = feeds.nth(i);
            try {
                feed.scrollIntoViewIfNeeded();
                waitVisible(feed, 10000);
                page.waitForTimeout(150);
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
        Locator muteButtons = page.locator("xpath=" + MUTE_BTN_XPATH);
        Locator feeds = page.locator("xpath=" + FEED_XPATH);
        int toggled = 0;
        int totalFeeds = feeds.count();
        for (int i = 0; i < totalFeeds; i++) {
            Locator feed = feeds.nth(i);
            try {
                feed.scrollIntoViewIfNeeded();
                waitVisible(feed, 10000);
                // For each feed area, click the nearest mute button in view
                int btnCount = muteButtons.count();
                for (int b = 0; b < btnCount; b++) {
                    Locator btn = muteButtons.nth(b);
                    if (safeIsVisible(btn)) {
                        try {
                            btn.scrollIntoViewIfNeeded();
                        } catch (Exception ignored) {}
                        clickWithRetry(btn, 1, 120);
                        toggled++;
                        break; // move to next feed after one toggle
                    }
                }
                page.waitForTimeout(150);
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
                page.waitForTimeout(120);
            }
        } catch (Exception ignored) {}
    }

    @Step("Open a random visible Discover profile from a feed")
    public void openRandomVisibleDiscoverProfile() {
        // Find candidate elements matching provided selector
        Locator candidates = page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^creatorDiscover profile$"))).locator("span");
        int count = Math.max(0, candidates.count());
        if (count == 0) {
            // Try to scroll to reveal more
            try { page.mouse().wheel(0, 1200); page.waitForTimeout(200); } catch (Exception ignored) {}
            count = candidates.count();
        }
        if (count == 0) throw new RuntimeException("No discover profile elements found on Discover feed");

        // Collect visible indices (prefer the second span .nth(1) pattern per spec if available per group)
        List<Integer> visibleIdx = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Locator el = candidates.nth(i);
            if (safeIsVisible(el)) visibleIdx.add(i);
        }
        if (visibleIdx.isEmpty()) {
            // Fallback: try to bring first few into view and mark visible
            int lim = Math.min(5, count);
            for (int i = 0; i < lim; i++) {
                Locator el = candidates.nth(i);
                try { el.scrollIntoViewIfNeeded(); page.waitForTimeout(100); } catch (Exception ignored) {}
                if (safeIsVisible(el)) visibleIdx.add(i);
            }
        }
        if (visibleIdx.isEmpty()) throw new RuntimeException("No visible discover profile element to click");

        int pick = visibleIdx.get(new Random().nextInt(visibleIdx.size()));
        Locator target = candidates.nth(pick);
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
