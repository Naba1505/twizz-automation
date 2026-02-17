package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorUnlockHistoryPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorUnlockHistoryTest extends BaseCreatorTest {
    private static final Logger log = LoggerFactory.getLogger(CreatorUnlockHistoryTest.class);

    @Test(priority = 1, description = "Verify Unlock History navigation and details")
    public void verifyUnlockHistory() {
        CreatorUnlockHistoryPage unlockPage = new CreatorUnlockHistoryPage(page);

        // Open Settings and ensure URL contains settings path
        unlockPage.openSettingsFromProfile();
        String url = page.url();
        log.info("Settings URL after click: {}", url);
        Assert.assertTrue(url.contains("/common/setting"), "Did not land on Settings screen");

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
