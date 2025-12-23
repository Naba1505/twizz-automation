package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.fan.FanLanguagePage;
import pages.fan.FanLoginPage;
import utils.ConfigReader;

/**
 * Test class for Fan Language settings.
 * Tests switching between available languages: English, Français, Español.
 */
@Epic("Fan")
@Feature("Language")
public class FanLanguageTest extends BaseTestClass {

    private static final Logger logger = LoggerFactory.getLogger(FanLanguageTest.class);

    // Fan credentials
    private String fanUsername;
    private String fanPassword;

    /**
     * Load fan credentials from config.
     */
    private void loadCredentials() {
        fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        logger.info("[FanLanguage] Loaded credentials - Fan: {}", fanUsername);
    }

    /**
     * Test: Switch language through all available options
     * 
     * Flow:
     * 1. Fan login and land on Home screen
     * 2. Click Settings icon
     * 3. Assert on Settings screen (title visible)
     * 4. Click Language → Select Français → Assert Langue title → Navigate back
     * 5. Click Langue → Select Español → Assert Idioma title → Navigate back
     * 6. Click Idioma → Select English → Assert Language title → Navigate back
     * 7. Assert Language menu visible (back to English)
     */
    @Story("Fan switches language through all available options")
    @Test(priority = 1, description = "Fan navigates to Settings and switches language: English → French → Spanish → English")
    public void fanCanSwitchLanguages() {
        // Load credentials
        loadCredentials();

        logger.info("[FanLanguage] Starting test: Switch through all languages");

        // ==================== FAN LOGIN ====================
        logger.info("[FanLanguage] Step 1: Fan login");
        FanLoginPage fanLogin = new FanLoginPage(page);
        fanLogin.navigate();
        Assert.assertTrue(fanLogin.isLoginFormVisible(), "Fan login form not visible");
        fanLogin.login(fanUsername, fanPassword);
        logger.info("[FanLanguage] Fan logged in and on Home screen");

        // ==================== NAVIGATE TO SETTINGS ====================
        logger.info("[FanLanguage] Step 2: Navigate to Settings");
        FanLanguagePage languagePage = new FanLanguagePage(page);
        languagePage.navigateToSettings();
        logger.info("[FanLanguage] On Settings screen");

        // ==================== SWITCH TO FRENCH ====================
        logger.info("[FanLanguage] Step 3: Switch to French");
        languagePage.switchToFrench();
        logger.info("[FanLanguage] Language switched to French");

        // ==================== SWITCH TO SPANISH ====================
        logger.info("[FanLanguage] Step 4: Switch to Spanish");
        languagePage.switchToSpanish();
        logger.info("[FanLanguage] Language switched to Spanish");

        // ==================== SWITCH BACK TO ENGLISH ====================
        logger.info("[FanLanguage] Step 5: Switch back to English");
        languagePage.switchToEnglish();
        logger.info("[FanLanguage] Language switched back to English");

        // ==================== VERIFY ENGLISH LANGUAGE ====================
        logger.info("[FanLanguage] Step 6: Verify back on Settings with English");
        languagePage.assertOnSettingsScreenEnglish();
        logger.info("[FanLanguage] Verified: Settings screen with English language");

        logger.info("[FanLanguage] Test completed successfully: All language switches verified");
    }
}
