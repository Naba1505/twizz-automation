package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.fan.FanSpottedBugPage;

/**
 * Test class for Fan "I've spotted a bug" functionality.
 * Tests fan submitting bug report from Settings.
 */
@Epic("Fan")
@Feature("Spotted Bug")
public class FanSpottedBugTest extends BaseFanTest {

    private static final Logger logger = LoggerFactory.getLogger(FanSpottedBugTest.class);

    /**
     * Test: Fan submits "I've spotted a bug" form
     * 
     * Flow:
     * 1. Fan login and land on Home screen
     * 2. Click Settings icon
     * 3. Assert on Settings screen (title visible)
     * 4. Scroll to and click "I've spotted a bug"
     * 5. Assert on "I've spotted a bug" screen (title visible)
     * 6. Fill Subject field (with timestamp)
     * 7. Fill Description field (with timestamp)
     * 8. Click Send button
     * 9. Assert success message "Your message has been sent"
     */
    @Story("Fan submits bug report via 'I've spotted a bug'")
    @Test(priority = 1, description = "Fan navigates to 'I've spotted a bug' and submits a bug report")
    public void fanCanSubmitSpottedBugForm() {
        logger.info("[FanSpottedBug] Starting test: Fan submits bug report");

        // ==================== NAVIGATE TO SPOTTED BUG ====================
        logger.info("[FanSpottedBug] Step 2: Navigate to 'I've spotted a bug'");
        FanSpottedBugPage spottedBug = new FanSpottedBugPage(page);
        spottedBug.navigateToSpottedBug();
        logger.info("[FanSpottedBug] On 'I've spotted a bug' screen");

        // ==================== SUBMIT FORM ====================
        logger.info("[FanSpottedBug] Step 3: Submit bug report form");
        spottedBug.submitBugReportForm(
            "This Is Automation script bug",
            "QA Test"
        );
        logger.info("[FanSpottedBug] Bug report submitted successfully");

        logger.info("[FanSpottedBug] Test completed successfully: Fan submitted bug report");
    }
}
