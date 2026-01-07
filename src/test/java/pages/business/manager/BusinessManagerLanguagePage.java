package pages.business.manager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for Business Manager Language Settings
 * Flow: Manager Dashboard → Settings → Language → Switch Languages
 */
public class BusinessManagerLanguagePage {
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerLanguagePage.class);
    private final Page page;

    public BusinessManagerLanguagePage(Page page) {
        this.page = page;
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

    @Step("Switch to French language")
    public void switchToFrench() {
        Locator languageSelector = page.locator(".language-selector").first();
        languageSelector.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Language] Switched to French language");
    }

    @Step("Switch to Spanish language")
    public void switchToSpanish() {
        Locator languageSelector = page.locator("div:nth-child(3) > .language-selector");
        languageSelector.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Language] Switched to Spanish language");
    }

    @Step("Switch back to English language")
    public void switchToEnglish() {
        Locator languageSelector = page.locator("div:nth-child(2) > .language-selector");
        languageSelector.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Language] Switched back to English language");
    }

    @Step("Complete language switching flow")
    public void switchLanguages() {
        clickSettingsIcon();
        clickLanguageGoButton();
        logger.info("[Manager Language] Navigated to Language screen");
    }
}
