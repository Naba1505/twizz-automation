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

        // Assert Total income is visible
        historyPage.assertTotalIncomeVisible();

        // Assert Media Push image is visible
        historyPage.assertMediaPushImageVisible();

        // Scroll down to view more content
        historyPage.scrollDown();

        // Assert loader is visible at end
        historyPage.assertLoaderVisible();

        // Scroll back to top
        historyPage.scrollToTop();

        // Click on first visible Media Push image
        historyPage.clickFirstMediaPushImage();

        // Assert dialog elements are visible
        historyPage.assertPricePerUnitVisible();
        historyPage.assertSummaryOfPushVisible();

        // Close the dialog
        historyPage.closeDialog();

        // Assert back on History Media push screen
        historyPage.assertBackOnHistoryMediaPushScreen();
    }
}
