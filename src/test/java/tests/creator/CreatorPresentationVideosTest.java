package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorMediaPushPage;
import pages.creator.CreatorPresentationVideosPage;

import java.nio.file.Path;

public class CreatorPresentationVideosTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Creator can upload a Presentation Video and see 'Waiting' status")
    public void creatorCanUploadPresentationVideo() {
        CreatorPresentationVideosPage pvPage = new CreatorPresentationVideosPage(page);
        CreatorMediaPushPage mediaPushUtils = new CreatorMediaPushPage(page);

        // Navigate to Presentation Videos page
        pvPage.openSettingsFromProfile();
        pvPage.openPresentationVideosScreen();

        // Upload presentation video
        Path video = CreatorPresentationVideosPage.resolveVideoPath("src/test/resources/Videos/PresentationVideoA.mp4");
        pvPage.uploadPresentationVideo(video);

        // Wait for uploading message if it appears
        mediaPushUtils.waitForUploadingMessageIfFast();

        // Click the sticky button
        pvPage.clickPresentationVideoStickyButton();

        // Assert status becomes 'Waiting'
        pvPage.waitForWaitingStatus();
    }

    @Test(priority = 2, description = "Creator can delete the Presentation Video")
    public void creatorCanDeletePresentationVideo() {
        CreatorPresentationVideosPage pvPage = new CreatorPresentationVideosPage(page);

        // Navigate to Presentation Videos page
        pvPage.openSettingsFromProfile();
        pvPage.openPresentationVideosScreen();

        // Delete the video (trash icon is visible on the campaign card)
        pvPage.deletePresentationVideo();

        // Assert empty state
        pvPage.assertEmptyPromptVisible();
    }
}
