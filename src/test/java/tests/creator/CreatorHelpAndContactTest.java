package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorHelpAndContactPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorHelpAndContactTest extends BaseCreatorTest {
    private static final Logger log = LoggerFactory.getLogger(CreatorHelpAndContactTest.class);

    @Test(priority = 1, description = "Verify Help and Contact form submission")
    public void verifyHelpAndContact() {
        CreatorHelpAndContactPage helpPage = new CreatorHelpAndContactPage(page);

        // Open Settings and ensure URL contains settings path
        helpPage.openSettingsFromProfile();
        String url = page.url();
        log.info("Settings URL after click: {}", url);
        Assert.assertTrue(url.contains("/common/setting"), "Did not land on Settings screen");

        // Open Help and contact and verify title
        helpPage.openHelpAndContact();

        // Fill Subject and Message
        helpPage.fillSubject("QA Test");
        helpPage.fillMessage("QA Automation Message");

        // Send and assert success toast
        helpPage.clickSend();
        helpPage.assertSuccessToastVisible();
    }
}
