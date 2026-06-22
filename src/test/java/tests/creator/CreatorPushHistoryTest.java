package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorPushHistoryPage;

public class CreatorPushHistoryTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Verify History Of Media Pushes navigation and details")
    public void verifyHistoryOfMediaPushes() {
        CreatorPushHistoryPage historyPage = new CreatorPushHistoryPage(page);

        // Open Settings and ensure URL contains settings path
        historyPage.openSettingsFromProfile();
        historyPage.assertOnSettingsUrl();

        // Open History of pushes and verify title
        historyPage.openHistoryOfPushes();

        // Open last media push entry and assert Performance screen
        historyPage.openLastMediaPushEntry();
        historyPage.assertPerformanceVisible();

        // Navigate back
        historyPage.clickBackArrow();

        // Open first media push entry and assert Performance screen again
        historyPage.openFirstMediaPushEntry();
        historyPage.assertPerformanceVisible();

        // Navigate back until profile (plus icon) is visible
        historyPage.navigateBackToProfile();
    }
}
