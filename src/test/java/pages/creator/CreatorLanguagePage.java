package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

/**
 * Page object for Creator -> Settings -> Language switch
 */
public class CreatorLanguagePage extends BasePage {
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    // Reduced from DEFAULT_WAIT (60000ms) to SHORT_TIMEOUT (1000ms) = 98% faster!
    private static final int BUTTON_RETRY_DELAY = 150;   // Button click retry delay
    private static final int POLLING_WAIT = 200;         // Polling intervals
    private static final int SHORT_TIMEOUT = 1000;       // Short waits (was 60000ms)
    private static final int MEDIUM_TIMEOUT = 2000;      // Medium waits (was 20000ms)

    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorLanguagePage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator languageMenu() {
        return page.getByText("Language");
    }

    private Locator languageTitleEnglish() {
        return page.getByText("Language");
    }

    private Locator languageTitleFrench() {
        return page.getByText("Langue");
    }

    private Locator languageTitleSpanish() {
        return page.getByText("Idioma");
    }

    private Locator optionFrancais() {
        return page.getByText("Français");
    }

    private Locator optionEspanol() {
        // As per provided selector idea: a div with exact text "Español", choose the second match (nth(1))
        return page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Español$"))).nth(1);
    }

    private Locator optionEnglish() {
        return page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^English$"))).nth(1);
    }

    private Locator backArrow() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
    }

    private Locator profilePlusIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Language)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), SHORT_TIMEOUT);
        clickWithRetry(settingsIcon(), 1, BUTTON_RETRY_DELAY);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open Language screen")
    public void openLanguageScreen() {
        waitVisible(languageMenu(), SHORT_TIMEOUT);
        try { languageMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(languageMenu(), 1, BUTTON_RETRY_DELAY);
        waitVisible(languageTitleEnglish(), SHORT_TIMEOUT);
    }

    @Step("Switch to Français and verify")
    public void switchToFrenchAndVerify() {
        waitVisible(optionFrancais(), SHORT_TIMEOUT);
        clickWithRetry(optionFrancais(), 1, BUTTON_RETRY_DELAY);
        waitVisible(languageTitleFrench(), MEDIUM_TIMEOUT);
    }

    @Step("Switch to Español and verify")
    public void switchToSpanishAndVerify() {
        waitVisible(optionEspanol(), SHORT_TIMEOUT);
        clickWithRetry(optionEspanol(), 1, BUTTON_RETRY_DELAY);
        waitVisible(languageTitleSpanish(), MEDIUM_TIMEOUT);
    }

    @Step("Switch to English and verify")
    public void switchToEnglishAndVerify() {
        waitVisible(optionEnglish(), SHORT_TIMEOUT);
        clickWithRetry(optionEnglish(), 1, BUTTON_RETRY_DELAY);
        waitVisible(languageTitleEnglish(), MEDIUM_TIMEOUT);
    }

    @Step("Navigate back to profile (two back arrows)")
    public void navigateBackToProfile() {
        for (int i = 0; i < 2; i++) {
            try {
                waitVisible(backArrow(), SHORT_TIMEOUT);
                clickWithRetry(backArrow(), 1, BUTTON_RETRY_DELAY);
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
        }
        if (!safeIsVisible(profilePlusIcon())) {
            // Attempt additional back clicks if needed
            for (int i = 0; i < 2 && !safeIsVisible(profilePlusIcon()); i++) {
                try { clickWithRetry(backArrow(), 1, BUTTON_RETRY_DELAY); } catch (Throwable ignored) {}
                try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
            }
        }
    }
}

