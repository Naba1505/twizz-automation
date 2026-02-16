package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.fan.FanHelpAndContactPage;

/**
 * Test class for Fan Help and Contact functionality.
 * Tests fan submitting help and contact form from Settings.
 */
@Epic("Fan")
@Feature("Help and Contact")
public class FanHelpAndContactTest extends BaseFanTest {

    private static final Logger logger = LoggerFactory.getLogger(FanHelpAndContactTest.class);

    /**
     * Test: Fan submits Help and Contact form
     * 
     * Flow:
     * 1. Fan login and land on Home screen
     * 2. Click Settings icon
     * 3. Scroll to and click "Help and contact"
     * 4. Assert on Help and Contact screen
     * 5. Fill Subject field (with timestamp)
     * 6. Fill Description field (with timestamp)
     * 7. Click Send button
     * 8. Assert success message "Your message has been sent"
     */
    @Story("Fan submits Help and Contact form")
    @Test(priority = 1, description = "Fan navigates to Help and Contact and submits a message")
    public void fanCanSubmitHelpAndContactForm() {
        logger.info("[FanHelpAndContact] Starting test: Fan submits Help and Contact form");

        // ==================== NAVIGATE TO HELP AND CONTACT ====================
        logger.info("[FanHelpAndContact] Step 2: Navigate to Help and Contact");
        FanHelpAndContactPage helpAndContact = new FanHelpAndContactPage(page);
        helpAndContact.navigateToHelpAndContact();
        logger.info("[FanHelpAndContact] On Help and Contact screen");

        // ==================== SUBMIT FORM ====================
        logger.info("[FanHelpAndContact] Step 3: Submit Help and Contact form");
        helpAndContact.submitHelpAndContactForm(
            "This Is Twizz Automation Subject",
            "Twizz Automation Description"
        );
        logger.info("[FanHelpAndContact] Form submitted successfully");

        logger.info("[FanHelpAndContact] Test completed successfully: Fan submitted Help and Contact form");
    }
}
