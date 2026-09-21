package pages.business.manager;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;

/**
 * Page Object for Business Manager Language Settings
 * Flow: Manager Dashboard → Settings → Language → Switch Languages
 */
public class BusinessManagerLanguagePage extends BasePage {

    public BusinessManagerLanguagePage(Page page) {
        super(page);
    }

    @Step("Click on Settings icon")
    public void clickSettingsIcon() {
        Locator settingsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings"));
        settingsIcon.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Language] Clicked on Settings icon");
    }

    @Step("Verify Settings icon is visible")
    public boolean isSettingsIconVisible() {
        Locator settingsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings"));
        boolean isVisible = settingsIcon.isVisible();
        logger.info("[Manager Language] Settings icon visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click on 'Language Go' button")
    public void clickLanguageGoButton() {
        Locator languageGoButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Language Go"));
        languageGoButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Language] Clicked on 'Language Go' button");
    }

    @Step("Verify 'Language' heading is visible (English)")
    public boolean isLanguageHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Language"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Language] 'Language' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify 'Langue' heading is visible (French)")
    public boolean isLangueHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Langue"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Language] 'Langue' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify 'Idioma' heading is visible (Spanish)")
    public boolean isIdiomaHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Idioma"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Language] 'Idioma' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify English is selected by default")
    public boolean isEnglishSelectedByDefault() {
        Locator englishOption = page.locator("div").filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^English$")));
        boolean isVisible = englishOption.isVisible();
        logger.info("[Manager Language] English selected by default: {}", isVisible);
        return isVisible;
    }

    private void switchLanguage(String language, String expectedHeading) {
        Locator languageItem = page.locator(".language-item")
                .filter(new Locator.FilterOptions().setHasText(language))
                .first();
        languageItem.waitFor();
        languageItem.click();
        page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(expectedHeading).setExact(true)).waitFor();
        logger.info("[Manager Language] Switched to {} language", language);
    }

    @Step("Switch to French language")
    public void switchToFrench() {
        switchLanguage("Français", "Langue");
    }

    @Step("Switch to Spanish language")
    public void switchToSpanish() {
        switchLanguage("Español", "Idioma");
    }

    @Step("Switch back to English language")
    public void switchToEnglish() {
        switchLanguage("English", "Language");
    }

    @Step("Complete language switching flow")
    public void switchLanguages() {
        clickSettingsIcon();
        clickLanguageGoButton();
        logger.info("[Manager Language] Navigated to Language screen");
    }
}
