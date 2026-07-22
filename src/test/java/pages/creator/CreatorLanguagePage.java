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

    private Locator languageMenuItem() {
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
        // Prefer the visible selectable row; fall back to the second div match
        Locator row = page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Español$")));
        if (row.count() > 1) return row.nth(1);
        return row.first();
    }

    private Locator optionEnglish() {
        Locator row = page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^English$")));
        if (row.count() > 1) return row.nth(1);
        return row.first();
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
        // Ensure we are on the profile page before looking for the settings icon
        navigateAndWait(ConfigReader.getBaseUrl() + "/creator/profile");
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Assert current URL contains settings path")
    public void assertOnSettingsUrl() {
        if (!page.url().contains(SETTINGS_URL_PART)) {
            throw new AssertionError("Did not land on Settings screen. URL: " + page.url());
        }
        logger.info("Settings URL confirmed: {}", page.url());
    }

    @Step("Open Language screen")
    public void openLanguageScreen() {
        Locator menu = languageMenuItem();
        waitVisible(menu, ConfigReader.getShortTimeout());
        try { menu.scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(menu, 1, ConfigReader.getElementRetryDelay());
        waitVisible(languageTitleEnglish(), ConfigReader.getShortTimeout());
    }

    private Locator languageTitleEnglish() {
        return page.getByText("Language");
    }

    @Step("Switch to Français and verify")
    public void switchToFrenchAndVerify() {
        waitVisible(optionFrancais(), ConfigReader.getShortTimeout());
        clickWithRetry(optionFrancais(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(languageTitleFrench(), ConfigReader.getShortTimeout());
    }

    @Step("Switch to Español and verify")
    public void switchToSpanishAndVerify() {
        waitVisible(optionEspanol(), ConfigReader.getShortTimeout());
        clickWithRetry(optionEspanol(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(languageTitleSpanish(), ConfigReader.getShortTimeout());
    }

    @Step("Switch to English and verify")
    public void switchToEnglishAndVerify() {
        waitVisible(optionEnglish(), ConfigReader.getShortTimeout());
        clickWithRetry(optionEnglish(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(languageTitleEnglish(), ConfigReader.getShortTimeout());
    }

    @Step("Navigate back to profile (two back arrows)")
    public void navigateBackToProfile() {
        for (int i = 0; i < 2; i++) {
            try {
                waitVisible(backArrow(), ConfigReader.getShortTimeout());
                clickWithRetry(backArrow(), 1, ConfigReader.getElementRetryDelay());
            } catch (Throwable e) { logger.debug("Back arrow click failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        if (!safeIsVisible(profilePlusIcon().first())) {
            for (int i = 0; i < 2 && !safeIsVisible(profilePlusIcon().first()); i++) {
                try { clickWithRetry(backArrow(), 1, ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Back arrow click failed: {}", e.getMessage()); }
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            }
        }
    }
}

