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

        // Assert push history entries are visible in the list
        historyPage.assertHistoryEntriesVisible();

        // Scroll down to view more content
        historyPage.scrollDown();

        // Assert loader is visible at end
        historyPage.assertLoaderVisible();

        // Scroll back to top
        historyPage.scrollToTop();

        // Open the first media push entry (navigates to its Performance detail page)
        historyPage.openFirstMediaPushEntry();

        // Assert Performance detail page elements are visible
        historyPage.assertIncomeGeneratedVisible();
        historyPage.assertPricePerUnitVisible();
        historyPage.assertSummaryOfPushVisible();

        // Navigate back to the History Media push list
        historyPage.navigateBackFromPerformancePage();

        // Assert back on History Media push screen
        historyPage.assertBackOnHistoryMediaPushScreen();
    }
}
