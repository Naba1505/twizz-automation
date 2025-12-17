package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.FanLogoutPage;
import pages.FanLoginPage;
import utils.ConfigReader;

/**
 * Test class for Fan Logout functionality.
 * Tests that fan can successfully logout from the app.
 */
@Epic("Fan")
@Feature("Logout")
public class FanLogoutTest extends BaseTestClass {

    private static final Logger logger = LoggerFactory.getLogger(FanLogoutTest.class);

    // Fan credentials
    private String fanUsername;
    private String fanPassword;

    /**
     * Load fan credentials from config.
     */
    private void loadCredentials() {
        fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        logger.info("[FanLogout] Loaded credentials - Fan: {}", fanUsername);
    }

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
        // Load credentials
        loadCredentials();

        logger.info("[FanLogout] Starting test: Fan logout");

        // ==================== FAN LOGIN ====================
        logger.info("[FanLogout] Step 1: Fan login");
        FanLoginPage fanLogin = new FanLoginPage(page);
        fanLogin.navigate();
        Assert.assertTrue(fanLogin.isLoginFormVisible(), "Fan login form not visible");
        fanLogin.login(fanUsername, fanPassword);
        logger.info("[FanLogout] Fan logged in and on Home screen");

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
