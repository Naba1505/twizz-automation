package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorLanguagePage;

public class CreatorLanguageTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Verify Switch Language functionality in Creator account")
    public void verifySwitchLanguage() {
        CreatorLanguagePage languagePage = new CreatorLanguagePage(page);

        // Open Settings and ensure URL contains settings path
        languagePage.openSettingsFromProfile();
        languagePage.assertOnSettingsUrl();

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
