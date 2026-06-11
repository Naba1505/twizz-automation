package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for Fan Language settings screen.
 * Supports switching between English, Français, and Español.
 */
public class FanLanguagePage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanLanguagePage.class);

    public FanLanguagePage(Page page) {
        super(page);
    }

    // ================= Locators =================

    // Settings
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon"));
    }

    private Locator settingsTitle() {
        return page.getByText("Settings");
    }

    private Locator backArrow() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
    }

    // Language menu items (in different languages)
    private Locator languageMenuEnglish() {
        return page.getByText("Language");
    }

    private Locator languageMenuFrench() {
        return page.getByText("Langue");
    }

    private Locator languageMenuSpanish() {
        return page.getByText("Idioma");
    }

    // Language options - use text locator with exact match
    private Locator englishOption() {
        return page.getByText("English", new Page.GetByTextOptions().setExact(true));
    }

    private Locator frenchOption() {
        return page.getByText("Français", new Page.GetByTextOptions().setExact(true));
    }

    private Locator spanishOption() {
        return page.getByText("Español", new Page.GetByTextOptions().setExact(true));
    }

    // Checkbox indicator (appears when language is selected)
    private Locator languageCheckbox() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Twizz check"));
    }

    // ================= Navigation Methods =================

    @Step("Click Settings icon")
    public void clickSettingsIcon() {
        waitVisible(settingsIcon(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(settingsIcon(), 2, ConfigReader.getAnimationTimeout());
        logger.info("[Fan][Language] Clicked Settings icon");
    }

    @Step("Assert on Settings screen by viewing title")
    public void assertOnSettingsScreen() {
        waitVisible(settingsTitle(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][Language] On Settings screen - title visible");
    }

    @Step("Click back arrow to navigate back")
    public void clickBackArrow() {
        waitVisible(backArrow(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(backArrow(), 2, ConfigReader.getAnimationTimeout());
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
        logger.info("[Fan][Language] Clicked back arrow");
    }

    /**
     * Navigate from Fan home to Settings screen.
     */
    @Step("Navigate to Settings from Fan home")
    public void navigateToSettings() {
        clickSettingsIcon();
        assertOnSettingsScreen();
        logger.info("[Fan][Language] Successfully navigated to Settings screen");
    }

    // ================= Language Menu Click Methods =================

    @Step("Click Language menu item (English)")
    public void clickLanguageMenuEnglish() {
        Locator menuItem = languageMenuEnglish();
        waitVisible(menuItem, ConfigReader.getVisibilityTimeout());
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, ConfigReader.getAnimationTimeout());
        page.waitForTimeout(ConfigReader.getPageLoadTimeout()); // Wait for language screen to fully load
        logger.info("[Fan][Language] Clicked 'Language' menu item");
    }

    @Step("Click Langue menu item (French)")
    public void clickLanguageMenuFrench() {
        Locator menuItem = languageMenuFrench();
        waitVisible(menuItem, ConfigReader.getVisibilityTimeout());
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, ConfigReader.getAnimationTimeout());
        page.waitForTimeout(ConfigReader.getPageLoadTimeout()); // Wait for language screen to fully load
        logger.info("[Fan][Language] Clicked 'Langue' menu item");
    }

    @Step("Click Idioma menu item (Spanish)")
    public void clickLanguageMenuSpanish() {
        Locator menuItem = languageMenuSpanish();
        waitVisible(menuItem, ConfigReader.getVisibilityTimeout());
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, ConfigReader.getAnimationTimeout());
        page.waitForTimeout(ConfigReader.getPageLoadTimeout()); // Wait for language screen to fully load
        logger.info("[Fan][Language] Clicked 'Idioma' menu item");
    }

    // ================= Language Title Assertions =================

    @Step("Assert Language title visible (English)")
    public void assertLanguageTitleEnglish() {
        waitVisible(languageMenuEnglish(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][Language] Language title visible (English)");
    }

    @Step("Assert Langue title visible (French)")
    public void assertLanguageTitleFrench() {
        waitVisible(languageMenuFrench(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][Language] Langue title visible (French)");
    }

    @Step("Assert Idioma title visible (Spanish)")
    public void assertLanguageTitleSpanish() {
        waitVisible(languageMenuSpanish(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][Language] Idioma title visible (Spanish)");
    }

    // ================= Language Selection Methods =================

    @Step("Select Français language")
    public void selectFrench() {
        waitVisible(frenchOption(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(frenchOption(), 2, ConfigReader.getAnimationTimeout());
        // Wait for checkbox to appear confirming language selection
        waitVisible(languageCheckbox(), ConfigReader.getVisibilityTimeout());
        page.waitForTimeout(ConfigReader.getUiSettleTimeout()); // Wait for language change to take effect
        logger.info("[Fan][Language] Selected Français language - checkbox visible");
    }

    @Step("Select Español language")
    public void selectSpanish() {
        waitVisible(spanishOption(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(spanishOption(), 2, ConfigReader.getAnimationTimeout());
        // Wait for checkbox to appear confirming language selection
        waitVisible(languageCheckbox(), ConfigReader.getVisibilityTimeout());
        page.waitForTimeout(ConfigReader.getUiSettleTimeout()); // Wait for language change to take effect
        logger.info("[Fan][Language] Selected Español language - checkbox visible");
    }

    @Step("Select English language")
    public void selectEnglish() {
        waitVisible(englishOption(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(englishOption(), 2, ConfigReader.getAnimationTimeout());
        // Wait for checkbox to appear confirming language selection
        waitVisible(languageCheckbox(), ConfigReader.getVisibilityTimeout());
        page.waitForTimeout(ConfigReader.getUiSettleTimeout()); // Wait for language change to take effect
        logger.info("[Fan][Language] Selected English language - checkbox visible");
    }

    // ================= Complete Language Switch Flows =================

    /**
     * Switch from English to French.
     * Click Language → Select Français → Assert Langue title → Navigate back
     */
    @Step("Switch language from English to French")
    public void switchToFrench() {
        clickLanguageMenuEnglish();
        assertLanguageTitleEnglish();
        selectFrench();
        assertLanguageTitleFrench();
        clickBackArrow();
        logger.info("[Fan][Language] Switched to French successfully");
    }

    /**
     * Switch from French to Spanish.
     * Click Langue → Select Español → Assert Idioma title → Navigate back
     */
    @Step("Switch language from French to Spanish")
    public void switchToSpanish() {
        clickLanguageMenuFrench();
        selectSpanish();
        assertLanguageTitleSpanish();
        clickBackArrow();
        logger.info("[Fan][Language] Switched to Spanish successfully");
    }

    /**
     * Switch from Spanish to English.
     * Click Idioma → Select English → Assert Language title → Navigate back
     */
    @Step("Switch language from Spanish to English")
    public void switchToEnglish() {
        clickLanguageMenuSpanish();
        selectEnglish();
        assertLanguageTitleEnglish();
        clickBackArrow();
        logger.info("[Fan][Language] Switched to English successfully");
    }

    /**
     * Assert back on Settings screen with Language menu visible (English).
     */
    @Step("Assert on Settings screen with English language")
    public void assertOnSettingsScreenEnglish() {
        waitVisible(languageMenuEnglish(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][Language] On Settings screen with English language");
    }

    /**
     * Complete flow: Switch through all languages (English → French → Spanish → English).
     */
    @Step("Switch through all languages")
    public void switchThroughAllLanguages() {
        switchToFrench();
        switchToSpanish();
        switchToEnglish();
        assertOnSettingsScreenEnglish();
        logger.info("[Fan][Language] All language switches completed successfully");
    }
}

