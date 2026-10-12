package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

public class FanHomePage extends BasePage {

    private static final String HOME_PATH_FRAGMENT = "/fan/home";
    private static final String LIVES_PATH_FRAGMENT = "/p/fan/lives";

    public FanHomePage(Page page) { super(page); }

    // ================= Locators =================

    private Locator footerHomeIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Home icon")).first();
    }

    private Locator discoverLivesIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Discover lives")).first();
    }

    private Locator topSearchIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search")).first();
    }

    private Locator searchBox() {
        return page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search"));
    }

    private Locator homeTab(String name) {
        // Tabs render as .fan-home-tab-item inside .fan-home-tabs-header-tabs - scoped class selector
        return page.locator(".fan-home-tabs-header-tabs .fan-home-tab-item",
                new Page.LocatorOptions().setHasText(name)).first();
    }

    private Locator activeHomeTab(String name) {
        // The selected tab gets the 'active' class - reliable signal regardless of URL params
        return page.locator(".fan-home-tabs-header-tabs .fan-home-tab-item.active",
                new Page.LocatorOptions().setHasText(name)).first();
    }

    private Locator suggestionsTab() {
        return homeTab("Suggestions");
    }

    private Locator followingTab() {
        return homeTab("Following");
    }

    private Locator presentationTab() {
        return homeTab("Presentation");
    }

    private Locator feedSlides() {
        return page.locator(".swiper-slide .hls-video-player, .slide-container .hls-video-player");
    }

    private Locator discoverProfileCta() {
        return page.getByText("Discover profile").first();
    }

    private Locator likeIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("like")).first();
    }

    private Locator bookmarkIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("bookmark")).first();
    }

    private Locator dotMenu() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("dot")).first();
    }

    private Locator backControl() {
        // Publication viewer back chevron has empty alt; profile back uses alt="arrow left"
        return page.locator(".discover-publication-header-hit img[src*='chevron-back'], img[alt='arrow left']").first();
    }

    private Locator livesTablist() {
        return page.getByRole(AriaRole.TABLIST).first();
    }

    private Locator livesTab(String name) {
        return page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(name));
    }

    // ================= Home navigation =================

    @Step("Ensure fan is on Home screen (click Home icon and assert /fan/home URL)")
    public void assertOnHomeUrl() {
        waitVisible(footerHomeIcon(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(footerHomeIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL(Pattern.compile(".*/fan/home.*"),
                new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        logger.info("[Fan] On home screen URL: {}", page.url());
    }

    @Step("Click Home icon to navigate to home feed screen")
    public void clickHomeIcon() {
        waitVisible(footerHomeIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(footerHomeIcon(), 1, ConfigReader.getElementRetryDelay());
        logger.info("[Fan] Clicked Home icon to navigate to home feed screen");
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.LOAD,
                    new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        } catch (Exception e) {
            logger.debug("[Fan] Home icon click load wait: {}", e.getMessage());
        }
    }

    @Step("Assert home top bar: Suggestions/Following/Presentation tabs, Discover lives, Search")
    public void assertHomeTopBar() {
        waitVisible(discoverLivesIcon(), ConfigReader.getShortTimeout());
        // Tab bar can remount during the app's initial tab redirect - allow visibility timeout
        waitVisible(suggestionsTab(), ConfigReader.getVisibilityTimeout());
        waitVisible(followingTab(), ConfigReader.getVisibilityTimeout());
        waitVisible(presentationTab(), ConfigReader.getVisibilityTimeout());
        waitVisible(topSearchIcon(), ConfigReader.getShortTimeout());
        logger.info("[Fan Home] Top bar verified - lives icon, 3 tabs, search icon");
    }

    // ================= Feed (Suggestions/Presentation) =================

    @Step("Assert feed slides rendered")
    public int assertFeedRendered() {
        waitVisible(feedSlides().first(), ConfigReader.getVisibilityTimeout());
        int count = feedSlides().count();
        logger.info("[Fan Home] Feed rendered with {} slides", count);
        if (count <= 0) {
            throw new AssertionError("Home feed rendered no slides");
        }
        return count;
    }

    @Step("Unmute visible feed slides")
    public int unmuteFeedSlides(int maxSlides) {
        int toggled = 0;
        int total = Math.min(feedSlides().count(), maxSlides);
        for (int i = 0; i < total; i++) {
            try {
                Locator slide = feedSlides().nth(i);
                slide.scrollIntoViewIfNeeded();
                waitVisible(slide, ConfigReader.getShortTimeout());
                Locator mute = slide.locator("xpath=ancestor::div[contains(@class,'slide-container') or contains(@class,'swiper-slide')]//button[contains(@class,'mute-button')]");
                if (mute.count() > 0 && safeIsVisible(mute.first())) {
                    clickWithRetry(mute.first(), 1, ConfigReader.getElementRetryDelay());
                    toggled++;
                }
                page.waitForTimeout(ConfigReader.getElementRetryDelay());
            } catch (Exception e) {
                logger.warn("[Fan Home] Unable to unmute slide {}: {}", i, e.getMessage());
            }
        }
        return toggled;
    }

    @Step("Open creator profile via 'Discover profile' bar")
    public void openDiscoverProfileCta() {
        waitVisible(discoverProfileCta(), ConfigReader.getShortTimeout());
        try { discoverProfileCta().scrollIntoViewIfNeeded(); } catch (Throwable e) {
            logger.debug("[Fan Home] scrollIntoViewIfNeeded failed: {}", e.getMessage());
        }
        clickWithRetry(discoverProfileCta(), 2, ConfigReader.getElementRetryDelay());
    }

    // ================= Tabs =================

    @Step("Switch to Following tab")
    public void switchToFollowingTab() {
        // App can auto-land on this tab without a URL param - click only if not already active
        if (!safeIsVisible(activeHomeTab("Following"))) {
            waitVisible(followingTab(), ConfigReader.getVisibilityTimeout());
            clickWithRetry(followingTab(), 1, ConfigReader.getElementRetryDelay());
        }
        waitVisible(activeHomeTab("Following"), ConfigReader.getShortTimeout());
        logger.info("[Fan Home] Switched to Following tab");
    }

    @Step("Switch to Presentation tab")
    public void switchToPresentationTab() {
        if (!safeIsVisible(activeHomeTab("Presentation"))) {
            waitVisible(presentationTab(), ConfigReader.getVisibilityTimeout());
            clickWithRetry(presentationTab(), 1, ConfigReader.getElementRetryDelay());
        }
        waitVisible(activeHomeTab("Presentation"), ConfigReader.getShortTimeout());
        logger.info("[Fan Home] Switched to Presentation tab");
    }

    @Step("Switch back to Suggestions tab")
    public void switchToSuggestionsTab() {
        if (!safeIsVisible(activeHomeTab("Suggestions"))) {
            waitVisible(suggestionsTab(), ConfigReader.getVisibilityTimeout());
            clickWithRetry(suggestionsTab(), 1, ConfigReader.getElementRetryDelay());
        }
        waitVisible(activeHomeTab("Suggestions"), ConfigReader.getShortTimeout());
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
        logger.info("[Fan Home] Switched back to Suggestions tab");
    }

    // ================= Suggestions board =================

    private Locator suggestionBoard() {
        return page.locator("main").getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Follow")).first();
    }

    @Step("Assert Suggestions tab rendered the creator suggestion board")
    public int assertSuggestionsBoardRendered() {
        // Suggestions = board of creator cards with Follow buttons + section headers
        waitVisible(suggestionBoard(), ConfigReader.getVisibilityTimeout());
        int followButtons = page.locator("main").getByRole(AriaRole.BUTTON,
                new Locator.GetByRoleOptions().setName("Follow")).count();
        logger.info("[Fan Home] Suggestions board rendered - {} Follow buttons", followButtons);
        if (followButtons <= 0) {
            throw new AssertionError("Suggestions board rendered no Follow buttons");
        }
        return followButtons;
    }

    @Step("Assert the default landing tab is Presentation")
    public void assertDefaultTabIsPresentation() {
        waitVisible(activeHomeTab("Presentation"), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan Home] Default active tab is Presentation");
    }

    // ================= Following feed =================

    @Step("Assert Following feed rendered with post actions")
    public void assertFollowingFeedRendered() {
        waitVisible(likeIcon(), ConfigReader.getVisibilityTimeout());
        waitVisible(bookmarkIcon(), ConfigReader.getShortTimeout());
        logger.info("[Fan Home] Following feed rendered - like/bookmark icons visible");
    }

    @Step("Open post three-dot menu and cancel")
    public void openThreeDotsAndCancel() {
        waitVisible(dotMenu(), ConfigReader.getShortTimeout());
        clickWithRetry(dotMenu(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(page.getByText("What action do you want to").first(), ConfigReader.getShortTimeout());
        clickWithRetry(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel")).first(),
                1, ConfigReader.getElementRetryDelay());
        logger.info("[Fan Home] Opened post menu and cancelled");
    }

    // ================= Discover lives =================

    @Step("Open Discover lives screen")
    public void openDiscoverLives() {
        waitVisible(discoverLivesIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(discoverLivesIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + LIVES_PATH_FRAGMENT + "**",
                new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        logger.info("[Fan Home] On Discover lives screen");
    }

    @Step("Assert lives tablist and switch through Live/Events/Replay")
    public void assertLivesTabs() {
        waitVisible(livesTablist(), ConfigReader.getShortTimeout());
        for (String tab : new String[] { "Flame Live", "Popcorn Events", "Popcorn Replay" }) {
            Locator t = livesTab(tab);
            waitVisible(t.first(), ConfigReader.getShortTimeout());
            clickWithRetry(t.first(), 1, ConfigReader.getElementRetryDelay());
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
            logger.info("[Fan Home] Lives tab switched to: {}", tab);
        }
    }

    // ================= Search =================

    @Step("Open search field from home")
    public void openSearchField() {
        waitVisible(topSearchIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(topSearchIcon(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(searchBox(), ConfigReader.getShortTimeout());
        logger.info("[Fan Home] Search field opened");
    }

    @Step("Fill search query: {query}")
    public void fillSearch(String query) {
        waitVisible(searchBox(), ConfigReader.getShortTimeout());
        searchBox().fill(query == null ? "" : query);
        page.waitForTimeout(ConfigReader.getUiSettleTimeout());
    }

    @Step("Click search result by text: {resultText}")
    public void clickSearchResult(String resultText) {
        Locator res = page.getByText(resultText);
        waitVisible(res.first(), ConfigReader.getShortTimeout());
        clickWithRetry(res.first(), 1, ConfigReader.getElementRetryDelay());
    }

    // ================= Back / profile =================

    @Step("Navigate back from profile to Home and assert URL")
    public void navigateBackToHome() {
        waitVisible(backControl(), ConfigReader.getShortTimeout());
        boolean navigated = false;
        try {
            backControl().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout()));
            navigated = true;
        } catch (Exception e) {
            logger.warn("Normal back click failed ({}); trying overlay dismiss + retry", e.getMessage());
        }
        if (!navigated) {
            try {
                page.keyboard().press("Escape");
                page.waitForTimeout(ConfigReader.getUiSettleTimeout());
                backControl().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout()));
                navigated = true;
            } catch (Exception e) {
                logger.warn("Overlay dismiss + back click failed ({}); trying force click", e.getMessage());
            }
        }
        if (!navigated) {
            try {
                backControl().click(new Locator.ClickOptions().setForce(true).setTimeout(ConfigReader.getShortTimeout()));
                navigated = true;
            } catch (Exception e) {
                logger.warn("Force back click failed ({}); falling back to browser back", e.getMessage());
            }
        }
        if (!navigated) {
            try {
                page.goBack(new Page.GoBackOptions().setTimeout(ConfigReader.getShortTimeout()));
                navigated = true;
            } catch (Exception e) {
                logger.warn("Browser back navigation failed: {}", e.getMessage());
            }
        }
        page.waitForURL("**" + HOME_PATH_FRAGMENT + "**",
                new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
    }

    @Step("Ensure creator profile screen is visible via publications icon")
    public void ensureOnCreatorProfileScreen() {
        Locator publicationsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("publications icon"));
        waitVisible(publicationsIcon.first(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Dismiss creator profile onboarding tutorial")
    public void dismissSearchOnboarding() {
        Locator firstHint = page.getByText("Click here to see the creator's collections");
        if (firstHint.count() == 0 || !safeIsVisible(firstHint.first())) {
            logger.debug("Profile onboarding hint not visible; skipping dismissal");
            return;
        }
        com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(firstHint.first()).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).click();
        com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat(page.getByText("And here to see their")).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Finished")).click();
    }
}
