package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

public class CreatorFormationPage extends BasePage {

    // Formation page uses intentionally SHORT timeouts (1-4s) because help topics load instantly
    // Do NOT use ConfigReader defaults (10-120s) - they would make tests 10-30x slower
    private static final int SHORT_TIMEOUT = 1000;   // 1s - Formation tiles, topic titles
    private static final int MEDIUM_TIMEOUT = 2000; // 2s - URL waits, navigation
    private static final int LONG_TIMEOUT = 4000;     // 4s - Settings icon (may need polling)

    public CreatorFormationPage(Page page) {
        super(page);
    }

    // ===== Navigation: Settings -> Formation =====
    @Step("Open Settings from Profile via header settings icon")
    public void openSettingsFromProfile() {
        // Try multiple reasonable selectors for settings
        Locator[] candidates = new Locator[] {
                page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings")),
                page.locator("img[alt='settings']"),
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("settings")),
                page.locator("[data-testid='settings']"),
                page.locator("img[alt*='setting' i]"),
        };
        boolean clicked = false;
        for (Locator cand : candidates) {
            try {
                if (cand != null && cand.count() > 0) {
                    waitVisible(cand.first(), LONG_TIMEOUT);
                    try { cand.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
                    clickWithRetry(cand.first(), 1, ConfigReader.getElementRetryDelay());
                    clicked = true;
                    break;
                }
            } catch (Throwable t) {
                // try next
            }
        }
        if (!clicked) {
            logger.warn("Settings icon not found; navigating directly to Settings URL");
            // Fallback: direct navigate to settings
            navigateAndWait(ConfigReader.getBaseUrl() + "/common/setting");
        }
        assertOnSettingsUrl();
    }

    @Step("Assert on Settings URL (/common/setting)")
    public void assertOnSettingsUrl() {
        page.waitForURL("**/common/setting**", new Page.WaitForURLOptions().setTimeout(MEDIUM_TIMEOUT));
    }

    @Step("Open Formation tile from Settings")
    public void openFormationTile() {
        // Try exact text first
        Locator formation = getByTextExact("Formation");
        if (formation.count() == 0) {
            // Fallback: contains text
            formation = page.getByText("Formation");
        }
        if (formation.count() == 0) {
            // Fallback: link by href
            formation = page.locator("a[href*='/creator/formation']");
        }
        waitVisible(formation.first(), SHORT_TIMEOUT);
        clickWithRetry(formation.first(), 1, ConfigReader.getElementRetryDelay());
        // Ensure formation screen is actually loaded
        assertOnFormationScreen();
    }

    @Step("Assert on Formation screen (\"How can we help you?\" visible)")
    public void assertOnFormationScreen() {
        Locator help = page.getByText("How can we help you?");
        waitVisible(help.first(), SHORT_TIMEOUT);
        // Also ensure we're on the correct URL
        assertOnFormationUrl();
    }

    // ===== Topic flows =====
    @Step("Open topic: {title}")
    public void openTopic(String title) {
        // Prefer exact match when available
        Locator tile = getByTextExact(title);
        if (tile.count() == 0) {
            // Fallback: contains text
            tile = page.getByText(title);
        }
        if (tile.count() == 0) {
            // Fallback: role-based clickable items
            tile = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(title));
            if (tile.count() == 0) {
                tile = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(title));
            }
        }
        waitVisible(tile.first(), SHORT_TIMEOUT);
        try { tile.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(tile.first(), 1, ConfigReader.getElementRetryDelay());
        // After navigation, assert topic title
        assertTopicTitle(title.equals("Presentation Video") ? "Presentation Videos" : title);
    }

    @Step("Assert topic title visible: {exactTitle}")
    public void assertTopicTitle(String exactTitle) {
        // Try exact title first
        Locator heading = getByTextExact(exactTitle);
        if (heading.count() > 0) {
            waitVisible(heading.first(), SHORT_TIMEOUT);
            return;
        }
        // Fallback: contains-based span selector useful for cases like 'Profile'
        Locator containsSpan = page.locator("//span[contains(text(),'" + exactTitle + "')]");
        waitVisible(containsSpan.first(), SHORT_TIMEOUT);
    }

    @Step("Scroll to 'Do you have any other questions?' section")
    public void scrollToQuestionsFooter() {
        Locator q = page.getByText("Do you have any other questions?");
        try {
            waitVisible(q.first(), LONG_TIMEOUT);
            try { q.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        } catch (Throwable t) {
            logger.warn("Questions footer not found quickly; continuing. Title assertions will validate page.");
        }
    }

    @Step("Scroll to topic title: {exactTitle}")
    public void scrollToTopicTitle(String exactTitle) {
        Locator heading = getByTextExact(exactTitle);
        waitVisible(heading.first(), SHORT_TIMEOUT);
        try { heading.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
    }

    @Step("Navigate back (topic -> formation) via back icon")
    public void backToFormationFromTopic() {
        Locator back = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("back"));
        waitVisible(back.first(), SHORT_TIMEOUT);
        clickWithRetry(back.first(), 1, ConfigReader.getElementRetryDelay());
        // Ensure we returned to Formation
        assertOnFormationUrl();
    }

    @Step("Assert on Formation URL (/creator/formation)")
    public void assertOnFormationUrl() {
        page.waitForURL("**/creator/formation**", new Page.WaitForURLOptions().setTimeout(MEDIUM_TIMEOUT));
    }

    @Step("Navigate back to Settings via arrow left icon")
    public void backToSettingsFromFormation() {
        Locator arrowLeft = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
        waitVisible(arrowLeft.first(), SHORT_TIMEOUT);
        clickWithRetry(arrowLeft.first(), 1, ConfigReader.getElementRetryDelay());
        assertOnSettingsUrl();
    }
}

