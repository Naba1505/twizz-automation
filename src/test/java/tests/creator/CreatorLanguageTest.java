package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorLanguagePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorLanguageTest extends BaseCreatorTest {
    private static final Logger log = LoggerFactory.getLogger(CreatorLanguageTest.class);

    @Test(priority = 1, description = "Verify Switch Language functionality in Creator account")
    public void verifySwitchLanguage() {
        CreatorLanguagePage languagePage = new CreatorLanguagePage(page);

        // Open Settings and ensure URL contains settings path
        languagePage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Language screen
        languagePage.openLanguageScreen();

        // Switch to Français -> verify title 'Langue'
        languagePage.switchToFrenchAndVerify();

        // Switch to Español -> verify title 'Idioma'
        languagePage.switchToSpanishAndVerify();

        // Switch back to English -> verify title 'Language'
        languagePage.switchToEnglishAndVerify();

        // Navigate back to profile
        languagePage.navigateBackToProfile();
    }
}
