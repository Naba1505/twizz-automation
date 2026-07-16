package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorPublicationPage;
import testdata.PublicationData;

/**
 * Tests creator publication flow for videos and images with blur settings.
 * Uses PublicationData for test data generation.
 */
public class CreatorPublicationTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Publish video with blurred media enabled")
    public void testPublishVideoBlurred() {
        PublicationData data = PublicationData.videoBlurred();
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        
        Assert.assertTrue(pub.completePublicationFlow(data.mediaPath, data.caption, data.blurEnabled),
            "Success toast not visible after publishing " + data.mediaType);
    }

    @Test(priority = 2, description = "Publish video with blurred media disabled")
    public void testPublishVideoUnblurred() {
        PublicationData data = PublicationData.videoUnblurred();
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        Assert.assertTrue(pub.completePublicationFlow(data.mediaPath, data.caption, data.blurEnabled),
            "Success toast not visible after publishing " + data.mediaType);
    }

    @Test(priority = 3, description = "Publish image with blurred media enabled")
    public void testPublishImageBlurred() {
        PublicationData data = PublicationData.imageBlurred();
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        Assert.assertTrue(pub.completePublicationFlow(data.mediaPath, data.caption, data.blurEnabled),
            "Success toast not visible after publishing " + data.mediaType);
    }

    @Test(priority = 4, description = "Publish image with blurred media disabled")
    public void testPublishImageUnblurred() {
        PublicationData data = PublicationData.imageUnblurred();
        CreatorPublicationPage pub = new CreatorPublicationPage(page);
        Assert.assertTrue(pub.completePublicationFlow(data.mediaPath, data.caption, data.blurEnabled),
            "Success toast not visible after publishing " + data.mediaType);
    }
}
