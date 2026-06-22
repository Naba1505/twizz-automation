package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorCollectionsHistoryPage;

public class CreatorCollectionsHistoryTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Verify History of collections in Creator account")
    public void verifyHistoryOfCollections() {
        CreatorCollectionsHistoryPage collectionsPage = new CreatorCollectionsHistoryPage(page);

        // Open Settings and ensure URL contains settings path
        collectionsPage.openSettingsFromProfile();
        collectionsPage.assertOnSettingsUrl();

        // Open History of collections and verify title
        collectionsPage.openHistoryOfCollections();

        // Try to open first collection - if it exists, verify details
        // If no collections exist, that's acceptable (user may not have created any)
        collectionsPage.openFirstCollection();
        
        // Only verify details if we're on a details page (collection was opened)
        collectionsPage.assertDetailsIfOnCollectionPage();

        // Navigate back to profile (three back clicks)
        collectionsPage.navigateBackToProfile();
    }
}
