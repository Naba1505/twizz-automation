package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorUnlockHistoryPage;

public class CreatorUnlockHistoryTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Verify Unlock History navigation and details")
    public void verifyUnlockHistory() {
        CreatorUnlockHistoryPage unlockPage = new CreatorUnlockHistoryPage(page);

        // Open Settings and ensure URL contains settings path
        unlockPage.openSettingsFromProfile();
        unlockPage.assertOnSettingsUrl();

        // Open Unlock history and verify title
        unlockPage.openUnlockHistory();

        // Open last unlock entry and assert Details screen
        unlockPage.openLastUnlockEntry();
        unlockPage.assertDetailsVisible();

        // Navigate back
        unlockPage.clickBackArrow();

        // Open first unlock entry and assert Details screen again
        unlockPage.openFirstUnlockEntry();
        unlockPage.assertDetailsVisible();

        // Navigate back until profile (plus icon) is visible
        unlockPage.navigateBackToProfile();
    }
}
