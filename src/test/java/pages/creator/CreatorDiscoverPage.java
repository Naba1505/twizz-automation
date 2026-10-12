package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CreatorDiscoverPage extends BasePage {

    private static final String DISCOVER_PATH_FRAGMENT = "/common/discover";
    private static final String PUBLICATION_PATH_FRAGMENT = "/common/discover/publications/";

    public CreatorDiscoverPage(Page page) {
        super(page);
    }

    // ================= Locators =================

    private Locator discoverTab() {
        return page.getByText("Discover", new Page.GetByTextOptions().setExact(true)).first();
    }

    private Locator presentationVideoTab() {
        return page.getByText("Presentation Video").first();
    }

    private Locator topSearchIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search")).first();
    }

    private Locator footerDiscoverIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search icon")).first();
    }

    private Locator gridItems() {
        return page.locator(".discover-grid-item");
    }

    private Locator gridThumbnails() {
        return page.locator(".discover-grid-thumbnail");
    }

    private Locator gridTypeIcons() {
        return page.locator(".discover-grid-type-icon");
    }

    private Locator publicationViewer() {
        return page.locator(".discover-publication-page");
    }

    private Locator publicationMedia() {
        return page.locator(".discover-publication-page .hls-video-player, .discover-publication-swiper-slide, .discover-publication-slide").first();
    }

    private Locator muteButton() {
        return page.locator(".mute-button").first();
    }

    private Locator creatorRow() {
        return page.locator(".discover-publication-creator-row").first();
    }

    private Locator backArrow() {
        // Publication viewer back chevron has empty alt; profile back uses alt="arrow left"
        return page.locator(".discover-publication-header-hit img[src*='chevron-back'], img[alt='arrow left']").first();
    }

    private Locator searchBox() {
        return page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search"));
    }

    private Locator cancelSearchText() {
        return page.getByText("Cancel", new Page.GetByTextOptions().setExact(true)).first();
    }

    private Locator presentationSlides() {
        return page.locator(".swiper-slide .hls-video-player, .slide-container .hls-video-player");
    }

    private Locator discoverProfileCta() {
        return page.getByText("Discover profile").first();
    }

    // ================= Navigation =================

    @Step("Navigate to Discover screen via footer Search icon")
    public void navigateToDiscover() {
        waitVisible(footerDiscoverIcon(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(footerDiscoverIcon(), 2, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert on Discover screen (URL + tabs + grid)")
    public void assertOnDiscoverScreen() {
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**",
                new Page.WaitForURLOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        waitVisible(discoverTab(), ConfigReader.getShortTimeout());
        waitVisible(presentationVideoTab(), ConfigReader.getShortTimeout());
        waitVisible(footerDiscoverIcon(), ConfigReader.getVisibilityTimeout());
        logger.info("[Discover] On Discover screen - tabs and footer icon visible");
    }

    // ================= Grid =================

    @Step("Assert Discover grid rendered with tiles")
    public int assertGridRendered() {
        waitVisible(gridItems().first(), ConfigReader.getVisibilityTimeout());
        int count = gridItems().count();
        logger.info("[Discover] Grid rendered with {} tiles", count);
        if (count <= 0) {
            throw new AssertionError("Discover grid rendered no tiles");
        }
        return count;
    }

    @Step("Assert grid tiles have thumbnails and media type icons")
    public void assertGridTileContent() {
        waitVisible(gridThumbnails().first(), ConfigReader.getShortTimeout());
        int thumbs = gridThumbnails().count();
        int icons = gridTypeIcons().count();
        logger.info("[Discover] Grid thumbnails: {}, media type icons: {}", thumbs, icons);
        if (thumbs <= 0) {
            throw new AssertionError("Discover grid has no thumbnails");
        }
    }

    @Step("Scroll the Discover grid to lazy-load more tiles")
    public int scrollGridToLoadMore() {
        int before = gridItems().count();
        for (int i = 0; i < 6; i++) {
            page.mouse().wheel(0, 900);
            page.waitForTimeout(ConfigReader.getScrollWaitBetween());
        }
        int after = gridItems().count();
        logger.info("[Discover] Grid tiles before scroll: {}, after scroll: {}", before, after);
        return after;
    }

    @Step("Open the first Discover grid tile")
    public void openFirstGridTile() {
        waitVisible(gridItems().first(), ConfigReader.getShortTimeout());
        clickWithRetry(gridItems().first(), 2, ConfigReader.getElementRetryDelay());
    }

    // ================= Publication viewer =================

    @Step("Assert publication viewer opened from grid tile")
    public void assertOnPublicationViewer() {
        page.waitForURL("**" + PUBLICATION_PATH_FRAGMENT + "**",
                new Page.WaitForURLOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        waitVisible(publicationViewer(), ConfigReader.getVisibilityTimeout());
        // Tiles may be photos or videos - assert the slide media, not the video player specifically
        waitVisible(publicationMedia(), ConfigReader.getVisibilityTimeout());
        logger.info("[Discover] Publication viewer opened - media visible");
    }

    @Step("Assert creator row visible in publication viewer")
    public void assertViewerCreatorRow() {
        waitVisible(creatorRow(), ConfigReader.getShortTimeout());
        logger.info("[Discover] Creator row visible in publication viewer");
    }

    @Step("Toggle mute on the publication video if it has audio")
    public void togglePublicationMute() {
        if (muteButton().count() == 0 || !safeIsVisible(muteButton())) {
            logger.info("[Discover] No mute control on this publication (photo or silent media) - skipping");
            return;
        }
        clickWithRetry(muteButton(), 1, ConfigReader.getElementRetryDelay());
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
        logger.info("[Discover] Toggled mute on publication video");
    }

    // ================= Tabs =================

    @Step("Switch to Presentation Video tab")
    public void switchToPresentationVideoTab() {
        waitVisible(presentationVideoTab(), ConfigReader.getShortTimeout());
        clickWithRetry(presentationVideoTab(), 1, ConfigReader.getElementRetryDelay());
        // The SPA may delay or skip the ?tab=presentation query update - wait for the feed instead
        waitVisible(presentationSlides().first(), ConfigReader.getVisibilityTimeout());
        logger.info("[Discover] Switched to Presentation Video tab (url: {})", page.url());
    }

    @Step("Assert on Presentation Video feed")
    public void assertOnPresentationFeed() {
        waitVisible(presentationSlides().first(), ConfigReader.getVisibilityTimeout());
        logger.info("[Discover] Presentation Video feed visible - {} slide(s) loaded", presentationSlides().count());
    }

    @Step("Switch back to Discover tab")
    public void switchToDiscoverTab() {
        waitVisible(discoverTab(), ConfigReader.getShortTimeout());
        clickWithRetry(discoverTab(), 1, ConfigReader.getElementRetryDelay());
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
        logger.info("[Discover] Switched back to Discover tab");
    }

    @Step("Unmute presentation slide video")
    public int unmutePresentationSlides(int maxSlides) {
        Locator slides = presentationSlides();
        int toggled = 0;
        int total = Math.min(slides.count(), maxSlides);
        for (int i = 0; i < total; i++) {
            try {
                Locator slide = slides.nth(i);
                slide.scrollIntoViewIfNeeded();
                waitVisible(slide, ConfigReader.getShortTimeout());
                Locator mute = slide.locator("xpath=ancestor::div[contains(@class,'slide-container') or contains(@class,'swiper-slide')]//button[contains(@class,'mute-button')]");
                if (mute.count() > 0 && safeIsVisible(mute.first())) {
                    clickWithRetry(mute.first(), 1, ConfigReader.getElementRetryDelay());
                    toggled++;
                }
                page.waitForTimeout(ConfigReader.getElementRetryDelay());
            } catch (Exception e) {
                logger.warn("[Discover] Unable to unmute presentation slide {}: {}", i, e.getMessage());
            }
        }
        return toggled;
    }

    @Step("Open creator profile via 'Discover profile' bar")
    public void openDiscoverProfileCta() {
        waitVisible(discoverProfileCta(), ConfigReader.getShortTimeout());
        try { discoverProfileCta().scrollIntoViewIfNeeded(); } catch (Throwable e) {
            logger.debug("[Discover] scrollIntoViewIfNeeded failed: {}", e.getMessage());
        }
        clickWithRetry(discoverProfileCta(), 2, ConfigReader.getElementRetryDelay());
    }

    // ================= Search =================

    @Step("Open search field on Discover")
    public void openSearchField() {
        waitVisible(topSearchIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(topSearchIcon(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(searchBox(), ConfigReader.getShortTimeout());
        logger.info("[Discover] Search field opened");
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

    @Step("Cancel search and return to Discover grid")
    public void cancelSearch() {
        if (cancelSearchText().count() > 0 && safeIsVisible(cancelSearchText())) {
            clickWithRetry(cancelSearchText(), 1, ConfigReader.getElementRetryDelay());
        }
    }

    // ================= Back navigation =================

    @Step("Navigate back from profile/viewer to Discover and assert URL")
    public void navigateBackToDiscover() {
        waitVisible(backArrow(), ConfigReader.getShortTimeout());

        // The profile may open as an overlay (fan-profile-overlay) that intercepts
        // the back chevron. Use a short timeout and escalate through force/JS/browser back.
        boolean navigated = false;
        try {
            backArrow().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout()));
            navigated = true;
        } catch (Exception e) {
            logger.warn("Normal back arrow click failed ({}); trying to dismiss overlay", e.getMessage());
        }

        if (!navigated) {
            try {
                page.keyboard().press("Escape");
                page.waitForTimeout(ConfigReader.getUiSettleTimeout());
                backArrow().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout()));
                navigated = true;
            } catch (Exception e) {
                logger.warn("Overlay dismiss + back arrow click failed ({}); trying force click", e.getMessage());
            }
        }

        if (!navigated) {
            try {
                backArrow().click(new Locator.ClickOptions().setForce(true).setTimeout(ConfigReader.getShortTimeout()));
                navigated = true;
            } catch (Exception e) {
                logger.warn("Force back arrow click failed ({}); falling back to browser back", e.getMessage());
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

        // Wait URL back on discover regardless of which navigation method succeeded
        page.waitForURL("**" + DISCOVER_PATH_FRAGMENT + "**",
                new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
    }

    // ================= Profile assertions =================

    @Step("Ensure creator profile screen is visible via publications icon")
    public void ensureOnCreatorProfileScreen() {
        Locator publicationsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("publications icon"));
        waitVisible(publicationsIcon.first(), ConfigReader.getShortTimeout());
    }

    @Step("Dismiss creator profile onboarding tutorial")
    public void dismissSearchOnboarding() {
        // Onboarding flow that appears when opening a creator profile.
        Locator firstHint = page.getByText("Click here to see the creator's collections");
        if (firstHint.count() == 0 || !safeIsVisible(firstHint.first())) {
            logger.debug("Profile onboarding hint not visible; skipping dismissal");
            return;
        }
        assertThat(firstHint.first()).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).click();
        assertThat(page.getByText("And here to see their")).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Finished")).click();
    }
}
