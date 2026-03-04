package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorPublicationPage;
import utils.TestAssets;

import java.nio.file.Path;

public class CreatorPublicationTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Publish video with blurred media enabled")
    public void testPublishVideoBlurred() {
        Path media = TestAssets.videoOrNull(TestAssets.TOAST_VIDEO);
        String caption = "video with blurred media enabled";
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        pub.completePublicationFlow(media, caption, true);
    }

    @Test(priority = 2, description = "Publish video with blurred media disabled")
    public void testPublishVideoUnblurred() {
        Path media = TestAssets.videoOrNull(TestAssets.HIDE_AND_SEEK);
        String caption = "video with blurred media disabled";
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        pub.completePublicationFlow(media, caption, false);
    }

    @Test(priority = 3, description = "Publish image with blurred media enabled")
    public void testPublishImageBlurred() {
        Path media = TestAssets.imageOrNull(TestAssets.HOME_IMAGE);
        String caption = "image with blurred media enabled";
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        pub.completePublicationFlow(media, caption, true);
    }

    @Test(priority = 4, description = "Publish image with blurred media disabled")
    public void testPublishImageUnblurred() {
        Path media = TestAssets.imageOrNull(TestAssets.LANDSCAPE_IMAGE);
        String caption = "image with blurred media disabled";
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        pub.completePublicationFlow(media, caption, false);
    }
}
