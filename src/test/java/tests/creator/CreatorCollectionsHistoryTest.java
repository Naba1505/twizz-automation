package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorCollectionsHistoryPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorCollectionsHistoryTest extends BaseCreatorTest {
    private static final Logger log = LoggerFactory.getLogger(CreatorCollectionsHistoryTest.class);

    @Test(priority = 1, description = "Verify History of collections in Creator account")
    public void verifyHistoryOfCollections() {
        CreatorCollectionsHistoryPage collectionsPage = new CreatorCollectionsHistoryPage(page);

        // Open Settings and ensure URL contains settings path
        collectionsPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Open History of collections and verify title
        collectionsPage.openHistoryOfCollections();

        // Open first collection and assert Details screen then short wait
        collectionsPage.openFirstCollection();
        collectionsPage.assertDetailsVisibleAndWait();

        // Navigate back to profile (three back clicks)
        collectionsPage.navigateBackToProfile();
    }
}
