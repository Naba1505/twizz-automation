package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorHelpAndContactPage;

public class CreatorHelpAndContactTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Verify Help and Contact form submission")
    public void verifyHelpAndContact() {
        CreatorHelpAndContactPage helpPage = new CreatorHelpAndContactPage(page);

        // Open Settings and ensure URL contains settings path
        helpPage.openSettingsFromProfile();
        helpPage.assertOnSettingsUrl();

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
