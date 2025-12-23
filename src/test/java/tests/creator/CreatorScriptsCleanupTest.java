package tests.creator;

import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorProfilePage;
import pages.creator.CreatorScriptsPage;
import utils.ConfigReader;

/**
 * Test class for cleaning up scripts and bookmarks created by CreatorScriptsTest.
 * Deleting a bookmark also deletes all related scripts, providing efficient cleanup.
 */
public class CreatorScriptsCleanupTest extends BaseTestClass {

    @Test(priority = 1, 
          description = "Creator can delete all QA bookmarks and their associated scripts")
    public void creatorCanDeleteAllQABookmarksAndScripts() {
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

        // Use Scripts page object to delete all QA bookmarks
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.deleteAllQABookmarks();
    }
}
