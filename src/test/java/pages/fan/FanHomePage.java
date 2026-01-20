package pages.fan;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

public class FanHomePage extends BasePage {

    public FanHomePage(Page page) { super(page); }

    @Step("Ensure fan is on Home screen (Home icon visible)")
    public void assertOnHomeUrl() {
        // Fan may land on /fan/home or /common/discover, so use Home icon visibility as success indicator
        Locator homeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Home icon"));
        homeIcon.first().waitFor(new Locator.WaitForOptions().setTimeout(20000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        logger.info("[Fan] On home screen - Home icon visible (URL: {})", page.url());
    }

    @Step("Click Home icon to navigate to home feed screen")
    public void clickHomeIcon() {
        Locator homeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Home icon"));
        homeIcon.first().waitFor(new Locator.WaitForOptions().setTimeout(10000).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
        homeIcon.first().click();
        logger.info("[Fan] Clicked Home icon to navigate to home feed screen");
        page.waitForLoadState();
    }

    @Step("Ensure popcorn logo displayed beside fan username")
    public void assertUsernameBadgeLogo() {
        Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("twizz-popcorn"));
        waitVisible(logo.first(), DEFAULT_WAIT);
    }

    @Step("Scroll down to first video and play it")
    public void scrollToFirstVideoAndPlay() {
        Locator firstThumb = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("video thumbnail")).first();
        // Scroll until visible
        for (int i = 0; i < 15 && !safeIsVisible(firstThumb); i++) {
            page.mouse().wheel(0, 800);
            page.waitForTimeout(200);
        }
        waitVisible(firstThumb, DEFAULT_WAIT);
        try {
            Locator playBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Play"));
            if (playBtn.count() > 0 && safeIsVisible(playBtn.first())) { playBtn.first().click(); return; }
        } catch (Throwable ignored) {}
        try { page.locator("path").nth(4).click(); } catch (Throwable ignored) {}
    }

    @Step("Scroll to top to the first feed and ensure it is visible by username {username}")
    public void scrollToTopFirstFeed(String username) {
        for (int i = 0; i < 10; i++) {
            page.mouse().wheel(0, -1200);
            page.waitForTimeout(200);
        }
        Locator topFeedByUser = page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^" + Pattern.quote(username) + "$")));
        if (topFeedByUser.count() > 0) {
            waitVisible(topFeedByUser.first(), DEFAULT_WAIT);
        }
    }

    @Step("Like or unlike the first feed")
    public void likeFirstFeed() {
        Locator likeBtn = firstVisibleIcon("likeFill", "like", "heart");
        int attempts = 0;
        while ((likeBtn == null || likeBtn.count() == 0 || !safeIsVisible(likeBtn.first())) && attempts < 8) {
            page.mouse().wheel(0, 600);
            page.waitForTimeout(150);
            likeBtn = firstVisibleIcon("likeFill", "like", "heart");
            attempts++;
        }
        if (likeBtn != null && likeBtn.count() > 0) {
            waitVisible(likeBtn.first(), DEFAULT_WAIT);
            // Use force click to bypass video container overlay interception
            try {
                likeBtn.first().click(new Locator.ClickOptions().setForce(true));
            } catch (Throwable e) {
                logger.warn("Force click failed, retrying with standard click: {}", e.getMessage());
                clickWithRetry(likeBtn.first(), 1, 150);
            }
        }
    }

    @Step("Bookmark first visible feed")
    public void bookmarkFirstVisibleFeed() {
        Locator bookmark = firstVisibleIcon("bookmark", "save");
        int attempts = 0;
        while ((bookmark == null || bookmark.count() == 0 || !safeIsVisible(bookmark.first())) && attempts < 8) {
            page.mouse().wheel(0, 600);
            page.waitForTimeout(150);
            bookmark = firstVisibleIcon("bookmark", "save");
            attempts++;
        }
        if (bookmark != null && bookmark.count() > 0) {
            waitVisible(bookmark.first(), DEFAULT_WAIT);
            // Use force click to bypass video container overlay interception
            try {
                bookmark.first().click(new Locator.ClickOptions().setForce(true));
            } catch (Throwable e) {
                logger.warn("Force click failed on bookmark, retrying with standard click: {}", e.getMessage());
                clickWithRetry(bookmark.first(), 1, 150);
            }
        }
    }

    @Step("Open feed action menu and cancel")
    public void openThreeDotsAndCancel() {
        Locator threeDots = page.locator(".d-flex.dots-gap").first();
        clickWithRetry(threeDots, 1, 150);
        waitVisible(page.getByText("What action do you want to").first(), DEFAULT_WAIT);
        clickWithRetry(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel")).first(), 1, 150);
    }

    @Step("Search from home and navigate back from a subscriber profile")
    public void searchFromHomeAndBack(String searchText, String expectedProfileText) {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("search").setExact(true)).click();
        page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search")).fill(searchText);
        Locator match = getByTextExact(expectedProfileText);
        waitVisible(match.first(), DEFAULT_WAIT);
        match.first().click();
        // Ensure Subscriber button then go back
        waitVisible(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscriber")).first(), DEFAULT_WAIT);
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).click();
    }

    @Step("Search by handle and open subscriber, verify last name exact and navigate back")
    public void searchSubscriberAndBack(String handle, String lastNameExact) {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("search").setExact(true)).click();
        page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search")).fill(handle);
        // Click exact handle text
        Locator handleText = getByTextExact(handle);
        waitVisible(handleText.first(), DEFAULT_WAIT);
        handleText.first().click();
        // Ensure on profile by exact last name text
        Locator lastName = page.getByText(lastNameExact, new Page.GetByTextOptions().setExact(true));
        waitVisible(lastName.first(), DEFAULT_WAIT);
        // Ensure Subscriber button
        waitVisible(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscriber")).first(), DEFAULT_WAIT);
        // Navigate back
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).click();
        // Back on home URL
        assertOnHomeUrl();
    }

    private Locator firstVisibleIcon(String primary, String... alternates) {
        try {
            Locator byRole = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(primary));
            if (byRole.count() > 0 && safeIsVisible(byRole.first())) return byRole.first();
            for (String alt : alternates) {
                Locator altLoc = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(alt));
                if (altLoc.count() > 0 && safeIsVisible(altLoc.first())) return altLoc.first();
            }
        } catch (Throwable ignored) {}
        return null;
    }
}

