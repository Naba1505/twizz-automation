package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.FanPersonalInfoPage;
import pages.FanLoginPage;
import utils.ConfigReader;

/**
 * Test class for Fan Personal Information settings.
 * Tests viewing and updating personal information fields.
 */
@Epic("Fan")
@Feature("Personal Information")
public class FanPersonalInfoTest extends BaseTestClass {

    private static final Logger logger = LoggerFactory.getLogger(FanPersonalInfoTest.class);

    // Fan credentials
    private String fanUsername;
    private String fanPassword;

    /**
     * Load fan credentials from config.
     */
    private void loadCredentials() {
        fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        logger.info("[FanPersonalInfo] Loaded credentials - Fan: {}", fanUsername);
    }

    /**
     * Test: Verify Personal Information fields and update email/phone
     * 
     * Flow:
     * 1. Fan login and land on Home screen
     * 2. Click Settings icon
     * 3. Assert on Settings screen (title visible)
     * 4. Click Personal Information menu
     * 5. Assert on Personal Information screen
     * 6. Verify Identity field visible with lock icons
     * 7. Verify User name field visible with lock icon
     * 8. Verify Date of birth field visible
     * 9. Verify Account type field visible with Fan selected
     * 10. Update email field
     * 11. Update phone number field
     * 12. Click Register button
     * 13. Verify success message
     */
    @Story("Fan views and updates personal information")
    @Test(priority = 1, description = "Fan navigates to Personal Information, verifies fields, and updates info")
    public void fanCanViewAndUpdatePersonalInfo() {
        // Load credentials
        loadCredentials();

        logger.info("[FanPersonalInfo] Starting test: View and update personal information");

        // ==================== FAN LOGIN ====================
        logger.info("[FanPersonalInfo] Step 1: Fan login");
        FanLoginPage fanLogin = new FanLoginPage(page);
        fanLogin.navigate();
        Assert.assertTrue(fanLogin.isLoginFormVisible(), "Fan login form not visible");
        fanLogin.login(fanUsername, fanPassword);
        logger.info("[FanPersonalInfo] Fan logged in and on Home screen");

        // ==================== NAVIGATE TO SETTINGS ====================
        logger.info("[FanPersonalInfo] Step 2: Navigate to Settings");
        FanPersonalInfoPage personalInfoPage = new FanPersonalInfoPage(page);
        personalInfoPage.navigateToSettings();
        logger.info("[FanPersonalInfo] On Settings screen");

        // ==================== NAVIGATE TO PERSONAL INFO ====================
        logger.info("[FanPersonalInfo] Step 3: Navigate to Personal Information");
        personalInfoPage.clickPersonalInfoMenu();
        personalInfoPage.assertOnPersonalInfoScreen();
        logger.info("[FanPersonalInfo] On Personal Information screen");

        // ==================== VERIFY ALL FIELDS ====================
        logger.info("[FanPersonalInfo] Step 4: Verify all fields visible");
        personalInfoPage.verifyAllFieldsVisible();
        logger.info("[FanPersonalInfo] All fields verified");

        // ==================== UPDATE AND SAVE ====================
        logger.info("[FanPersonalInfo] Step 5: Update email and phone number");
        personalInfoPage.updateEmail(fanUsername); // Use same email
        personalInfoPage.updatePhoneNumber("9912301188");
        logger.info("[FanPersonalInfo] Fields updated");

        logger.info("[FanPersonalInfo] Step 6: Click Register to save");
        personalInfoPage.clickRegisterButton();
        logger.info("[FanPersonalInfo] Register clicked");

        // ==================== VERIFY SUCCESS ====================
        logger.info("[FanPersonalInfo] Step 7: Verify success message");
        personalInfoPage.verifySuccessMessage();
        logger.info("[FanPersonalInfo] Success message verified");

        logger.info("[FanPersonalInfo] Test completed successfully: Personal information viewed and updated");
    }
}
