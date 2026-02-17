package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorProfilePage;
import pages.creator.CreatorScriptsPage;

/**
 * Test class for cleaning up scripts and bookmarks created by CreatorScriptsTest.
 * Deleting a bookmark also deletes all related scripts, providing efficient cleanup.
 */
public class CreatorScriptsCleanupTest extends BaseCreatorTest {

    @Test(priority = 1, 
          description = "Creator can delete all QA bookmarks and their associated scripts")
    public void creatorCanDeleteAllQABookmarksAndScripts() {
        // Navigate to profile landing
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Use Scripts page object to delete all QA bookmarks
        CreatorScriptsPage scripts = new CreatorScriptsPage(page);
        scripts.deleteAllQABookmarks();
    }
}
