package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorPublicationPage;

public class CreatorPublicationDeleteTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Delete all created publications from Publications screen")
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
