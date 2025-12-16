package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.FanLoginPage;
import pages.FanSpottedBugPage;
import utils.ConfigReader;

/**
 * Test class for Fan "I've spotted a bug" functionality.
 * Tests fan submitting bug report from Settings.
 */
@Epic("Fan")
@Feature("Spotted Bug")
public class FanSpottedBugTest extends BaseTestClass {

    private static final Logger logger = LoggerFactory.getLogger(FanSpottedBugTest.class);

    // Fan credentials
    private String fanUsername;
    private String fanPassword;

    /**
     * Load fan credentials from config.
     */
    private void loadCredentials() {
        fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        logger.info("[FanSpottedBug] Loaded credentials - Fan: {}", fanUsername);
    }

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
        // Load credentials
        loadCredentials();

        logger.info("[FanSpottedBug] Starting test: Fan submits bug report");

        // ==================== FAN LOGIN ====================
        logger.info("[FanSpottedBug] Step 1: Fan login");
        FanLoginPage fanLogin = new FanLoginPage(page);
        fanLogin.navigate();
        Assert.assertTrue(fanLogin.isLoginFormVisible(), "Fan login form not visible");
        fanLogin.login(fanUsername, fanPassword);
        logger.info("[FanSpottedBug] Fan logged in and on Home screen");

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
