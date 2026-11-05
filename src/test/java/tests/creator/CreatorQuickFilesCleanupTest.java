package tests.creator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.creator.CreatorProfilePage;
import pages.creator.CreatorSettingsPage;

/**
 * Cleanup class that deletes all Quick Files albums (photos, videos, audios)
 * created by CreatorQuickFilesTest and other suites. The management UI exposes
 * Delete via a long-press (context menu) on the album card.
 */
public class CreatorQuickFilesCleanupTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorQuickFilesCleanupTest.class);

    @Test(priority = 1,
          description = "Creator deletes all Quick Files albums (image/video/audio/mix) via long-press delete")
    public void creatorCanDeleteAllQuickFilesAlbums() {
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        int deleted = settings.deleteAllQuickFilesAlbums();
        logger.info("Quick Files cleanup finished: {} album(s) deleted", deleted);
    }
}
