package tests;

import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorProfilePage;
import pages.CreatorScriptsPage;
import utils.ConfigReader;

public class CreatorScriptsTest extends BaseTestClass {

    @Test(priority = 1, description = "Creator can create a script using image media with two uploads and bookmark")
    public void creatorCanCreateScriptWithImages() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

        // Navigate to profile landing (common entry for creator account features)
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Use Scripts page object to execute full creation flow
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.createScriptWithTwoImagesFromDevice();
    }

    @Test(priority = 2, description = "Creator can create a script using video media with custom price and promo")
    public void creatorCanCreateScriptWithVideosAndPromo() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Use Scripts page object to execute video + promo flow
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.createScriptWithTwoVideosAndPromo();
    }

    @Test(priority = 3, description = "Creator can create a script using audio media with price 50 and 7 days promo")
    public void creatorCanCreateScriptWithAudiosAndPromo() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Use Scripts page object to execute audio + promo (7 days) flow
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.createScriptWithTwoAudiosAndPromo();
    }

    @Test(priority = 4, description = "Creator can create a script with mixed media (image, video, audio) and free price")
    public void creatorCanCreateScriptWithMixedMediaFree() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Use Scripts page object to execute mixed-media free-price flow
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.createScriptWithMixedMediaFree();
    }

    @Test(priority = 5, description = "Creator Scripts screen search functionality validation with multiple keywords")
    public void creatorCanUseScriptsSearchWithMultipleKeywords() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Open settings and Scripts, then validate search flow
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.openSettingsFromProfile();
        scripts.openScriptsFromSettings();
        scripts.validateScriptsSearchFlow();
    }

    @Test(priority = 6,
            description = "Creator can edit an existing image script by adding extra media and updating text")
    public void creatorCanEditImageScript() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Edit first image script
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.editFirstImageScriptAddExtraMediaAndUpdateText();
    }

    @Test(priority = 7,
            description = "Creator can edit an existing video script by adding extra media and updating text")
    public void creatorCanEditVideoScript() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Edit first video script
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.editFirstVideoScriptAddExtraMediaAndUpdateText();
    }

    @Test(priority = 8,
            description = "Creator can edit an existing audio script by adding extra media and updating text")
    public void creatorCanEditAudioScript() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Edit first audio script
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.editFirstAudioScriptAddExtraMediaAndUpdateText();
    }

    @Test(priority = 9,
            description = "Creator can edit an existing mixed media script by adding extra media and updating text")
    public void creatorCanEditMixedMediaScript() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Edit first mixed media script
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.editFirstMixedScriptAddExtraMediaAndUpdateText();
    }
}
