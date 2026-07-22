package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorMediaPushPage;
import pages.creator.CreatorPresentationVideosPage;

import java.nio.file.Path;

public class CreatorPresentationVideosTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Creator can upload a Presentation Video (<60s) and see 'Waiting' status")
    public void creatorCanUploadPresentationVideo() {
        CreatorPresentationVideosPage pvPage = new CreatorPresentationVideosPage(page);
        CreatorMediaPushPage mediaPushUtils = new CreatorMediaPushPage(page); // for waitForUploadingMessageIfFast()

        // Navigate to Presentation Videos page
        pvPage.openSettingsFromProfile();
        pvPage.openPresentationVideosScreen();

        // Upload presentation video
        Path video = CreatorPresentationVideosPage.resolveVideoPath("src/test/resources/Videos/PresentationVideoA.mp4");
        pvPage.uploadPresentationVideo(video);

        // Reuse Media Push helper to optionally wait for uploading message if it appears
        mediaPushUtils.waitForUploadingMessageIfFast();

        // Click the sticky button on the Presentation Video screen
        pvPage.clickPresentationVideoStickyButton();

        // Assert status becomes 'Waiting'
        pvPage.waitForWaitingStatus();
    }

    @Test(priority = 2, description = "Creator can delete the created Presentation Video and see empty prompt")
    public void creatorCanDeletePresentationVideo() {
        CreatorPresentationVideosPage pvPage = new CreatorPresentationVideosPage(page);
        CreatorMediaPushPage mediaPushUtils = new CreatorMediaPushPage(page);

        // Navigate to Presentation Videos page
        pvPage.openSettingsFromProfile();
        pvPage.openPresentationVideosScreen();

        // Each test method gets a fresh browser context, so ensure a video exists to delete
        if (!pvPage.hasPresentationVideo()) {
            Path video = CreatorPresentationVideosPage.resolveVideoPath("src/test/resources/Videos/PresentationVideoA.mp4");
            pvPage.uploadPresentationVideo(video);
            mediaPushUtils.waitForUploadingMessageIfFast();
            pvPage.clickPresentationVideoStickyButton();
            pvPage.waitForWaitingStatus();
        }

        // Delete and assert empty state
        pvPage.deletePresentationVideo();
        pvPage.assertEmptyPromptVisible();
    }
}
