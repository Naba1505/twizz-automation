package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorPushHistoryPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorPushHistoryTest extends BaseCreatorTest {
    private static final Logger log = LoggerFactory.getLogger(CreatorPushHistoryTest.class);

    @Test(priority = 1, description = "Verify History Of Media Pushes navigation and details")
    public void verifyHistoryOfMediaPushes() {
        CreatorPushHistoryPage historyPage = new CreatorPushHistoryPage(page);

        // Open Settings and ensure URL contains settings path
        historyPage.openSettingsFromProfile();
        String url = page.url();
        log.info("Settings URL after click: {}", url);
        Assert.assertTrue(url.contains("/common/setting"), "Did not land on Settings screen");

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
