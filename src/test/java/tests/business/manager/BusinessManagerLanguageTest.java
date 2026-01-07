package tests.business.manager;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.business.common.BusinessBaseTestClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Test class for Business Manager Language Settings
 * Tests switching between available languages in Settings screen
 */
public class BusinessManagerLanguageTest extends BusinessBaseTestClass {
    
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerLanguageTest.class);

    @Test(priority = 1, description = "Manager can switch languages in settings screen")
    public void managerCanSwitchLanguages() {
        logger.info("[Manager Language] Starting test: Switch languages in settings screen");
        
        // Get credentials from config
        String username = ConfigReader.getProperty("business.manager.username", "TwizzManager@proton.me");
        String password = ConfigReader.getProperty("business.manager.password", "Twizz$123");
        
        logger.info("[Manager Language] Using manager username: {}", username);
        
        // Login as Manager
        businessManagerLoginPage.login(username, password);
        
        // Verify on manager dashboard
        Assert.assertTrue(businessManagerLoginPage.isOnManagerDashboard(), 
            "Not on manager dashboard");
        logger.info("[Manager Language] Successfully logged in as Manager");
        
        // Verify Settings icon is visible
        Assert.assertTrue(businessManagerLanguagePage.isSettingsIconVisible(), 
            "Settings icon is not visible");
        logger.info("[Manager Language] Settings icon is visible");
        
        // Click on Settings icon
        businessManagerLanguagePage.clickSettingsIcon();
        logger.info("[Manager Language] Clicked on Settings icon");
        
        // Click on Language Go button
        businessManagerLanguagePage.clickLanguageGoButton();
        logger.info("[Manager Language] Clicked on 'Language Go' button");
        
        // Verify on Language screen (English)
        Assert.assertTrue(businessManagerLanguagePage.isLanguageHeadingVisible(), 
            "'Language' heading is not visible");
        logger.info("[Manager Language] On Language screen - English heading visible");
        
        // Verify English is selected by default
        Assert.assertTrue(businessManagerLanguagePage.isEnglishSelectedByDefault(), 
            "English is not selected by default");
        logger.info("[Manager Language] English is selected by default");
        
        // Switch to French
        businessManagerLanguagePage.switchToFrench();
        logger.info("[Manager Language] Switched to French");
        
        // Verify language changed to French
        Assert.assertTrue(businessManagerLanguagePage.isLangueHeadingVisible(), 
            "'Langue' heading is not visible");
        logger.info("[Manager Language] Language changed to French - 'Langue' heading visible");
        
        // Switch to Spanish
        businessManagerLanguagePage.switchToSpanish();
        logger.info("[Manager Language] Switched to Spanish");
        
        // Verify language changed to Spanish
        Assert.assertTrue(businessManagerLanguagePage.isIdiomaHeadingVisible(), 
            "'Idioma' heading is not visible");
        logger.info("[Manager Language] Language changed to Spanish - 'Idioma' heading visible");
        
        // Switch back to English
        businessManagerLanguagePage.switchToEnglish();
        logger.info("[Manager Language] Switched back to English");
        
        // Verify language changed back to English
        Assert.assertTrue(businessManagerLanguagePage.isLanguageHeadingVisible(), 
            "'Language' heading is not visible");
        logger.info("[Manager Language] Language changed back to English - 'Language' heading visible");
        
        logger.info("[Manager Language] Test completed successfully");
    }
}
