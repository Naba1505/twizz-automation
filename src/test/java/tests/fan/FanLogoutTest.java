package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.fan.FanLogoutPage;

/**
 * Test class for Fan Logout functionality.
 * Tests that fan can successfully logout from the app.
 */
@Epic("Fan")
@Feature("Logout")
public class FanLogoutTest extends BaseFanTest {

    private static final Logger logger = LoggerFactory.getLogger(FanLogoutTest.class);

    /**
     * Test: Fan can logout from the app
     * 
     * Flow:
     * 1. Fan login and land on Home screen
     * 2. Click Settings icon
     * 3. Assert on Settings screen (title visible)
     * 4. Click Disconnect button
     * 5. Verify Login text is displayed (user on login page)
     */
    @Story("Fan logs out from the app")
    @Test(priority = 1, description = "Fan navigates to Settings and logs out")
    public void fanCanLogout() {
        logger.info("[FanLogout] Starting test: Fan logout");

        // ==================== NAVIGATE TO SETTINGS ====================
        logger.info("[FanLogout] Step 2: Navigate to Settings");
        FanLogoutPage logoutPage = new FanLogoutPage(page);
        logoutPage.navigateToSettings();
        logger.info("[FanLogout] On Settings screen");

        // ==================== LOGOUT ====================
        logger.info("[FanLogout] Step 3: Click Disconnect to logout");
        logoutPage.clickDisconnect();
        logger.info("[FanLogout] Disconnect clicked");

        // ==================== VERIFY LOGOUT ====================
        logger.info("[FanLogout] Step 4: Verify on Login page");
        logoutPage.verifyOnLoginPage();
        logger.info("[FanLogout] Login page verified - logout successful");

        logger.info("[FanLogout] Test completed successfully: Fan logged out");
    }
}
