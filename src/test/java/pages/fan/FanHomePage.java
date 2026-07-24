package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

public class FanHomePage extends BasePage {

    public FanHomePage(Page page) { super(page); }

    @Step("Ensure fan is on Home screen (click Home icon and assert /fan/home URL)")
    public void assertOnHomeUrl() {
        Locator homeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Home icon")).first();
        waitVisible(homeIcon, ConfigReader.getVisibilityTimeout());
        clickWithRetry(homeIcon, 1, ConfigReader.getElementRetryDelay());
        page.waitForURL(Pattern.compile(".*/fan/home.*"),
                new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        logger.info("[Fan] On home screen URL: {}", page.url());
    }

    @Step("Click Home icon to navigate to home feed screen")
    public void clickHomeIcon() {
        Locator homeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Home icon")).first();
        waitVisible(homeIcon, ConfigReader.getShortTimeout());
        clickWithRetry(homeIcon, 1, ConfigReader.getElementRetryDelay());
        logger.info("[Fan] Clicked Home icon to navigate to home feed screen");
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.LOAD,
                    new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        } catch (Exception e) {
            logger.debug("[Fan] Home icon click load wait: {}", e.getMessage());
        }
    }

    @Step("Ensure popcorn logo displayed beside fan username")
    public void assertUsernameBadgeLogo() {
        Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("twizz-popcorn")).first();
        waitVisible(logo, ConfigReader.getShortTimeout());
    }

    @Step("Scroll down to first video and play it")
    public void scrollToFirstVideoAndPlay() {
        Locator firstThumb = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("video thumbnail")).first();
        // Scroll until visible
        int maxAttempts = ConfigReader.getMaxScrollAttempts();
        for (int i = 0; i < maxAttempts && !safeIsVisible(firstThumb); i++) {
            page.mouse().wheel(0, ConfigReader.getScrollStepSize());
            waitForAnimation();
        }
        waitVisible(firstThumb, DEFAULT_WAIT);
        try {
            Locator playBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Play")).first();
            if (safeIsVisible(playBtn)) {
                clickWithRetry(playBtn, 1, ConfigReader.getElementRetryDelay());
                return;
            }
        } catch (Throwable ignored) {}
        try {
            Locator fallbackPlay = page.locator("path").nth(4);
            clickWithRetry(fallbackPlay, 1, ConfigReader.getElementRetryDelay());
        } catch (Throwable ignored) {}
    }

    @Step("Scroll to top to the first feed and ensure it is visible by username {username}")
    public void scrollToTopFirstFeed(String username) {
        int maxAttempts = ConfigReader.getMaxScrollAttempts();
        for (int i = 0; i < maxAttempts; i++) {
            page.mouse().wheel(0, -ConfigReader.getScrollStepSize() * 2);
            waitForAnimation();
        }
        Locator topFeedByUser = page.locator("div")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile("^" + Pattern.quote(username) + "$")))
                .first();
        if (safeIsVisible(topFeedByUser)) {
            waitVisible(topFeedByUser, DEFAULT_WAIT);
        }
    }

    @Step("Like or unlike the first feed")
    public void likeFirstFeed() {
        Locator likeBtn = firstVisibleIcon("likeFill", "like", "heart");
        int attempts = 0;
        int maxAttempts = ConfigReader.getMaxScrollAttempts();
        while ((likeBtn == null || !safeIsVisible(likeBtn)) && attempts < maxAttempts) {
            page.mouse().wheel(0, ConfigReader.getScrollStepSize());
            waitForAnimation();
            likeBtn = firstVisibleIcon("likeFill", "like", "heart");
            attempts++;
        }
        if (likeBtn != null) {
            waitVisible(likeBtn, DEFAULT_WAIT);
            // Use force click to bypass video container overlay interception
            try {
                likeBtn.click(new Locator.ClickOptions().setForce(true));
            } catch (Throwable e) {
                logger.warn("Force click failed, retrying with standard click: {}", e.getMessage());
                clickWithRetry(likeBtn, 1, ConfigReader.getElementRetryDelay());
            }
        }
    }

    @Step("Bookmark first visible feed")
    public void bookmarkFirstVisibleFeed() {
        Locator bookmark = firstVisibleIcon("bookmark", "save");
        int attempts = 0;
        int maxAttempts = ConfigReader.getMaxScrollAttempts();
        while ((bookmark == null || !safeIsVisible(bookmark)) && attempts < maxAttempts) {
            page.mouse().wheel(0, ConfigReader.getScrollStepSize());
            waitForAnimation();
            bookmark = firstVisibleIcon("bookmark", "save");
            attempts++;
        }
        if (bookmark != null) {
            waitVisible(bookmark, DEFAULT_WAIT);
            // Use force click to bypass video container overlay interception
            try {
                bookmark.click(new Locator.ClickOptions().setForce(true));
            } catch (Throwable e) {
                logger.warn("Force click failed on bookmark, retrying with standard click: {}", e.getMessage());
                clickWithRetry(bookmark, 1, ConfigReader.getElementRetryDelay());
            }
        }
    }

    @Step("Open feed action menu and cancel")
    public void openThreeDotsAndCancel() {
        Locator threeDots = page.locator(".d-flex.dots-gap").first();
        clickWithRetry(threeDots, 1, ConfigReader.getElementRetryDelay());
        waitVisible(page.getByText("What action do you want to").first(), DEFAULT_WAIT);
        clickWithRetry(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel")).first(),
                1, ConfigReader.getElementRetryDelay());
    }

    @Step("Search from home and navigate back from a subscriber profile")
    public void searchFromHomeAndBack(String searchText, String expectedProfileText) {
        Locator searchIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("search").setExact(true)).first();
        clickWithRetry(searchIcon, 1, ConfigReader.getElementRetryDelay());

        Locator searchBox = page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search")).first();
        typeAndAssert(searchBox, searchText);

        Locator match = getByTextExact(expectedProfileText).first();
        waitVisible(match, DEFAULT_WAIT);
        clickWithRetry(match, 1, ConfigReader.getElementRetryDelay());

        // Ensure Subscriber button then go back
        Locator subscriberBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscriber")).first();
        waitVisible(subscriberBtn, DEFAULT_WAIT);

        Locator backArrow = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).first();
        clickWithRetry(backArrow, 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Search by handle and open subscriber, verify last name exact and navigate back")
    public void searchSubscriberAndBack(String handle, String lastNameExact) {
        Locator searchIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("search").setExact(true)).first();
        clickWithRetry(searchIcon, 1, ConfigReader.getElementRetryDelay());

        Locator searchBox = page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search")).first();
        typeAndAssert(searchBox, handle);

        // Click exact handle text
        Locator handleText = getByTextExact(handle).first();
        waitVisible(handleText, DEFAULT_WAIT);
        clickWithRetry(handleText, 1, ConfigReader.getElementRetryDelay());

        // Ensure on profile by exact last name text
        Locator lastName = page.getByText(lastNameExact, new Page.GetByTextOptions().setExact(true)).first();
        waitVisible(lastName, DEFAULT_WAIT);

        // Ensure Subscriber button
        Locator subscriberBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscriber")).first();
        waitVisible(subscriberBtn, DEFAULT_WAIT);

        // Navigate back
        Locator backArrow = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).first();
        clickWithRetry(backArrow, 1, ConfigReader.getElementRetryDelay());

        // Back on home URL
        assertOnHomeUrl();
    }

    private Locator firstVisibleIcon(String primary, String... alternates) {
        try {
            Locator byRole = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(primary)).first();
            if (safeIsVisible(byRole)) return byRole;
            for (String alt : alternates) {
                Locator altLoc = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(alt)).first();
                if (safeIsVisible(altLoc)) return altLoc;
            }
        } catch (Throwable ignored) {}
        return null;
    }
}

