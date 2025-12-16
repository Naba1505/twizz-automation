package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.FanHelpAndContactPage;
import pages.FanLoginPage;
import utils.ConfigReader;

/**
 * Test class for Fan Help and Contact functionality.
 * Tests fan submitting help and contact form from Settings.
 */
@Epic("Fan")
@Feature("Help and Contact")
public class FanHelpAndContactTest extends BaseTestClass {

    private static final Logger logger = LoggerFactory.getLogger(FanHelpAndContactTest.class);

    // Fan credentials
    private String fanUsername;
    private String fanPassword;

    /**
     * Load fan credentials from config.
     */
    private void loadCredentials() {
        fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        logger.info("[FanHelpAndContact] Loaded credentials - Fan: {}", fanUsername);
    }

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
        // Load credentials
        loadCredentials();

        logger.info("[FanHelpAndContact] Starting test: Fan submits Help and Contact form");

        // ==================== FAN LOGIN ====================
        logger.info("[FanHelpAndContact] Step 1: Fan login");
        FanLoginPage fanLogin = new FanLoginPage(page);
        fanLogin.navigate();
        Assert.assertTrue(fanLogin.isLoginFormVisible(), "Fan login form not visible");
        fanLogin.login(fanUsername, fanPassword);
        logger.info("[FanHelpAndContact] Fan logged in and on Home screen");

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
