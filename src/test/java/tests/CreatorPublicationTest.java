package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CreatorPublicationPage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CreatorPublicationTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Publish video with blurred media enabled")
    public void testPublishVideoBlurred() {
        Path media = Paths.get("src", "test", "resources", "Videos", "TeamWork.mp4");
        String caption = "video with blurred media enabled";
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        pub.completePublicationFlow(media, caption, true);
    }

    @Test(priority = 2, description = "Publish video with blurred media disabled")
    public void testPublishVideoUnblurred() {
        Path media = Paths.get("src", "test", "resources", "Videos", "HideAndSeek.mp4");
        String caption = "video with blurred media disabled";
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        pub.completePublicationFlow(media, caption, false);
    }

    @Test(priority = 3, description = "Publish image with blurred media enabled")
    public void testPublishImageBlurred() {
        Path media = Paths.get("src", "test", "resources", "Images", "Home.jpg");
        String caption = "image with blurred media enabled";
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        pub.completePublicationFlow(media, caption, true);
    }

    @Test(priority = 4, description = "Publish image with blurred media disabled")
    public void testPublishImageUnblurred() {
        Path media = Paths.get("src", "test", "resources", "Images", "landscape.jpg");
        String caption = "image with blurred media disabled";
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        pub.completePublicationFlow(media, caption, false);
    }

    @Test(priority = 5, description = "Delete all created publications from Publications screen")
    public void testDeleteAllPublications() {
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        // Navigate to Publications via profile icon
        pub.openProfilePublicationsIcon();
        pub.verifyPublicationsScreen();

        // Loop delete until no publications remain
        pub.deleteAllPublicationsLoop();

        // Assert no publication menus remain
        int remaining = pub.getPublicationMenuCount();
        Assert.assertEquals(remaining, 0, "Expected no publications remaining, but found: " + remaining);
    }
}
