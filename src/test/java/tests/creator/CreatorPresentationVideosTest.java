package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorDiscoverPage;
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

    @Test(priority = 3, description = "Creator can view the Presentation Video feed on Discover and open a creator profile")
    public void creatorCanViewPresentationVideoFeed() {
        CreatorDiscoverPage discover = new CreatorDiscoverPage(page);

        // Open Discover and switch to the Presentation Video tab
        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();
        discover.switchToPresentationVideoTab();
        discover.assertOnPresentationFeed();

        // Unmute visible presentation slides
        discover.unmutePresentationSlides(3);

        // Open the creator profile behind the current presentation video
        discover.openDiscoverProfileCta();
        discover.dismissSearchOnboarding();
        discover.ensureOnCreatorProfileScreen();

        // Navigate back to Discover
        discover.navigateBackToDiscover();
        discover.assertOnDiscoverScreen();
    }
}
