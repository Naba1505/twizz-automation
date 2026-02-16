package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.fan.FanPersonalInfoPage;
import utils.ConfigReader;

/**
 * Test class for Fan Personal Information settings.
 * Tests viewing and updating personal information fields.
 */
@Epic("Fan")
@Feature("Personal Information")
public class FanPersonalInfoTest extends BaseFanTest {

    private static final Logger logger = LoggerFactory.getLogger(FanPersonalInfoTest.class);

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
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");

        logger.info("[FanPersonalInfo] Starting test: View and update personal information");

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
