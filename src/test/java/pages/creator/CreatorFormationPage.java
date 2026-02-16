package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

public class CreatorFormationPage extends BasePage {

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
                    waitVisible(cand.first(), 4_000);
                    try { cand.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                    clickWithRetry(cand.first(), 1, 120);
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
        page.waitForURL("**/common/setting**", new Page.WaitForURLOptions().setTimeout(DEFAULT_WAIT));
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
        waitVisible(formation.first(), DEFAULT_WAIT);
        clickWithRetry(formation.first(), 1, 120);
        // Ensure formation screen is actually loaded
        assertOnFormationScreen();
    }

    @Step("Assert on Formation screen (\"How can we help you?\" visible)")
    public void assertOnFormationScreen() {
        Locator help = page.getByText("How can we help you?");
        waitVisible(help.first(), DEFAULT_WAIT);
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
        waitVisible(tile.first(), DEFAULT_WAIT);
        try { tile.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(tile.first(), 1, 120);
        // After navigation, assert topic title
        assertTopicTitle(title.equals("Presentation Video") ? "Presentation Videos" : title);
    }

    @Step("Assert topic title visible: {exactTitle}")
    public void assertTopicTitle(String exactTitle) {
        // Try exact title first
        Locator heading = getByTextExact(exactTitle);
        if (heading.count() > 0) {
            waitVisible(heading.first(), DEFAULT_WAIT);
            return;
        }
        // Fallback: contains-based span selector useful for cases like 'Profile'
        Locator containsSpan = page.locator("//span[contains(text(),'" + exactTitle + "')]");
        waitVisible(containsSpan.first(), DEFAULT_WAIT);
    }

    @Step("Scroll to 'Do you have any other questions?' section")
    public void scrollToQuestionsFooter() {
        Locator q = page.getByText("Do you have any other questions?");
        try {
            waitVisible(q.first(), 6_000);
            try { q.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        } catch (Throwable t) {
            logger.warn("Questions footer not found quickly; continuing. Title assertions will validate page.");
        }
    }

    @Step("Scroll to topic title: {exactTitle}")
    public void scrollToTopicTitle(String exactTitle) {
        Locator heading = getByTextExact(exactTitle);
        waitVisible(heading.first(), DEFAULT_WAIT);
        try { heading.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
    }

    @Step("Navigate back (topic -> formation) via back icon")
    public void backToFormationFromTopic() {
        Locator back = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("back"));
        waitVisible(back.first(), DEFAULT_WAIT);
        clickWithRetry(back.first(), 1, 120);
        // Ensure we returned to Formation
        assertOnFormationUrl();
    }

    @Step("Assert on Formation URL (/creator/formation)")
    public void assertOnFormationUrl() {
        page.waitForURL("**/creator/formation**", new Page.WaitForURLOptions().setTimeout(DEFAULT_WAIT));
    }

    @Step("Navigate back to Settings via arrow left icon")
    public void backToSettingsFromFormation() {
        Locator arrowLeft = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
        waitVisible(arrowLeft.first(), DEFAULT_WAIT);
        clickWithRetry(arrowLeft.first(), 1, 120);
        assertOnSettingsUrl();
    }
}

