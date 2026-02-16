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
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 1, 150);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open Language screen")
    public void openLanguageScreen() {
        waitVisible(languageMenu(), DEFAULT_WAIT);
        try { languageMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(languageMenu(), 1, 150);
        waitVisible(languageTitleEnglish(), DEFAULT_WAIT);
    }

    @Step("Switch to Français and verify")
    public void switchToFrenchAndVerify() {
        waitVisible(optionFrancais(), DEFAULT_WAIT);
        clickWithRetry(optionFrancais(), 1, 150);
        waitVisible(languageTitleFrench(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Switch to Español and verify")
    public void switchToSpanishAndVerify() {
        waitVisible(optionEspanol(), DEFAULT_WAIT);
        clickWithRetry(optionEspanol(), 1, 150);
        waitVisible(languageTitleSpanish(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Switch to English and verify")
    public void switchToEnglishAndVerify() {
        waitVisible(optionEnglish(), DEFAULT_WAIT);
        clickWithRetry(optionEnglish(), 1, 150);
        waitVisible(languageTitleEnglish(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Navigate back to profile (two back arrows)")
    public void navigateBackToProfile() {
        for (int i = 0; i < 2; i++) {
            try {
                waitVisible(backArrow(), DEFAULT_WAIT);
                clickWithRetry(backArrow(), 1, 150);
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }
        if (!safeIsVisible(profilePlusIcon())) {
            // Attempt additional back clicks if needed
            for (int i = 0; i < 2 && !safeIsVisible(profilePlusIcon()); i++) {
                try { clickWithRetry(backArrow(), 1, 150); } catch (Throwable ignored) {}
                try { page.waitForTimeout(200); } catch (Throwable ignored) {}
            }
        }
    }
}

