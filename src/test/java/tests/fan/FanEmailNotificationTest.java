package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.fan.FanEmailNotificationPage;

/**
 * Test class for Fan Email Notification settings.
 * Tests disabling and enabling email notification toggles.
 */
@Epic("Fan")
@Feature("Email Notification")
public class FanEmailNotificationTest extends BaseFanTest {

    private static final Logger logger = LoggerFactory.getLogger(FanEmailNotificationTest.class);

    /**
     * Test 1: Disable all email notification toggles
     * 
     * Flow:
     * 1. Fan login and land on Home screen
     * 2. Click Settings icon
     * 3. Assert on Settings screen (title visible)
     * 4. Scroll to and click "Email notification"
     * 5. Assert on Email Notification screen (title visible)
     * 6. Disable all 5 toggles:
     *    - Push media from a creator
     *    - Live reminder
     *    - Scheduling a live
     *    - Direct live
     *    - Marketing
     */
    @Story("Fan disables all email notification toggles")
    @Test(priority = 1, description = "Fan navigates to Email Notification and disables all toggles")
    public void fanCanDisableAllEmailNotificationToggles() {
        logger.info("[FanEmailNotification] Starting test: Disable all email notification toggles");

        // ==================== NAVIGATE TO EMAIL NOTIFICATION ====================
        logger.info("[FanEmailNotification] Step 2: Navigate to Email Notification");
        FanEmailNotificationPage emailNotification = new FanEmailNotificationPage(page);
        emailNotification.navigateToEmailNotification();
        logger.info("[FanEmailNotification] On Email Notification screen");

        // ==================== DISABLE ALL TOGGLES ====================
        logger.info("[FanEmailNotification] Step 3: Disable all toggles");
        emailNotification.disableAllToggles();
        logger.info("[FanEmailNotification] All toggles disabled successfully");

        logger.info("[FanEmailNotification] Test completed successfully: All email notification toggles disabled");
    }

    /**
     * Test 2: Enable all email notification toggles
     * 
     * Flow:
     * 1. Fan login and land on Home screen
     * 2. Click Settings icon
     * 3. Assert on Settings screen (title visible)
     * 4. Scroll to and click "Email notification"
     * 5. Assert on Email Notification screen (title visible)
     * 6. Enable all 5 toggles (simple click, no confirmation):
     *    - Push media from a creator
     *    - Live reminder
     *    - Scheduling a live
     *    - Direct live
     *    - Marketing
     */
    @Story("Fan enables all email notification toggles")
    @Test(priority = 2, description = "Fan navigates to Email Notification and enables all toggles")
    public void fanCanEnableAllEmailNotificationToggles() {
        logger.info("[FanEmailNotification] Starting test: Enable all email notification toggles");

        // ==================== NAVIGATE TO EMAIL NOTIFICATION ====================
        logger.info("[FanEmailNotification] Step 2: Navigate to Email Notification");
        FanEmailNotificationPage emailNotification = new FanEmailNotificationPage(page);
        emailNotification.navigateToEmailNotification();
        logger.info("[FanEmailNotification] On Email Notification screen");

        // ==================== ENABLE ALL TOGGLES ====================
        logger.info("[FanEmailNotification] Step 3: Enable all toggles");
        emailNotification.enableAllToggles();
        logger.info("[FanEmailNotification] All toggles enabled successfully");

        logger.info("[FanEmailNotification] Test completed successfully: All email notification toggles enabled");
    }
}
