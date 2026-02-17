package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorProfilePage;
import pages.creator.CreatorScriptsPage;

public class CreatorScriptsTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Creator can create a script using image media with two uploads and bookmark")
    public void creatorCanCreateScriptWithImages() {
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

    @Test(priority = 6, description = "Creator can edit an existing image script by adding extra media and updating text")
    public void creatorCanEditImageScript() {
        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Edit first image script
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.editFirstImageScriptAddExtraMediaAndUpdateText();
    }

    @Test(priority = 7, description = "Creator can edit an existing video script by adding extra media and updating text")
    public void creatorCanEditVideoScript() {
        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Edit first video script
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.editFirstVideoScriptAddExtraMediaAndUpdateText();
    }

    @Test(priority = 8, description = "Creator can edit an existing audio script by adding extra media and updating text")
    public void creatorCanEditAudioScript() {
        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Edit first audio script
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.editFirstAudioScriptAddExtraMediaAndUpdateText();
    }

    @Test(priority = 9, description = "Creator can edit an existing mixed media script by adding extra media and updating text")
    public void creatorCanEditMixedMediaScript() {
        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Edit first mixed media script
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.editFirstMixedScriptAddExtraMediaAndUpdateText();
    }

    @Test(priority = 10,
            description = "Creator can create an image script using Quick Files album (requires CreatorQuickFilesTest image album)")
    public void creatorCanCreateImageScriptFromQuickFiles() {
        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Use Quick Files images album to create script
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.createImageScriptFromQuickFiles();
    }

    @Test(priority = 11,
            description = "Creator can create a video script using Quick Files album with promo (requires CreatorQuickFilesTest video album)")
    public void creatorCanCreateVideoScriptFromQuickFilesWithPromo() {
        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Use Quick Files videos album to create script with promo
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.createVideoScriptFromQuickFilesWithPromo();
    }

    @Test(priority = 12,
            description = "Creator can create an audio script using Quick Files album with promo (requires CreatorQuickFilesTest audio album)")
    public void creatorCanCreateAudioScriptFromQuickFilesWithPromo() {
        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Use Quick Files audio album to create script with promo
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.createAudioScriptFromQuickFilesWithPromo();
    }

    @Test(priority = 13, 
          description = "Creator can change the order of scripts using drag and drop",
          enabled = false)  // DISABLED: Playwright drag-and-drop not compatible with this DnD library
    public void creatorCanChangeScriptOrder() {
        /*
         * KNOWN LIMITATION:
         * This test is currently disabled because Playwright's drag-and-drop automation
         * does not work with the specific drag-and-drop library used in the application.
         * 
         * Attempted solutions:
         * 1. Playwright's dragTo() method - does not trigger the library's event handlers
         * 2. Manual mouse operations with slow movement - not recognized by the library
         * 3. JavaScript HTML5 DnD event simulation - arguments passing issues
         * 
         * The drag-and-drop functionality works correctly in manual testing but cannot
         * be automated with current Playwright capabilities.
         * 
         * Navigation and UI verification steps work correctly:
         * - Navigate to Scripts page ✓
         * - Click edit icon ✓
         * - Click "Change order" button ✓
         * - Verify "Hold the button on the right" heading ✓
         * - Locate reorder handles ✓
         * 
         * Re-enable this test when one of the following conditions is met:
         * - Application updates to a Playwright-compatible drag-and-drop library, OR
         * - Playwright adds better support for complex drag-and-drop interactions, OR
         * - A working JavaScript-based drag simulation is found
         */
        
        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Change script order
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.changeScriptOrder();
    }
}
