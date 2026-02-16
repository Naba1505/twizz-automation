package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.fan.FanTermsAndPoliciesPage;

/**
 * Test class for Fan Terms and Policies verification.
 * Covers: Terms and Conditions of Sale, Community Regulations, Content Policy.
 */
@Epic("Fan")
@Feature("Terms and Policies")
public class FanTermsAndPoliciesTest extends BaseFanTest {

    private static final Logger logger = LoggerFactory.getLogger(FanTermsAndPoliciesTest.class);

    /**
     * Test: Verify Terms and Conditions, Community Regulations, and Content Policy
     * 
     * Flow:
     * 1. Fan login and land on Home screen
     * 2. Click Settings icon
     * 3. Assert on Settings screen (title visible)
     * 4. Verify Terms and Conditions of Sale:
     *    - Click menu item → Assert title → Scroll to end → Scroll back to top → Navigate back
     * 5. Verify Community Regulations:
     *    - Click menu item → Assert title → Scroll to end → Scroll back to top → Navigate back
     * 6. Verify Content Policy:
     *    - Click menu item → Assert title → Scroll to end → Scroll back to top → Navigate back
     * 7. Assert back on Settings screen
     */
    @Story("Fan verifies Terms and Conditions, Community Regulations, and Content Policy")
    @Test(priority = 1, description = "Fan navigates to Settings and verifies all Terms and Policies")
    public void fanCanVerifyAllTermsAndPolicies() {
        logger.info("[FanTermsAndPolicies] Starting test: Verify all Terms and Policies");

        // ==================== NAVIGATE TO SETTINGS ====================
        logger.info("[FanTermsAndPolicies] Step 2: Navigate to Settings");
        FanTermsAndPoliciesPage termsAndPolicies = new FanTermsAndPoliciesPage(page);
        termsAndPolicies.navigateToSettings();
        logger.info("[FanTermsAndPolicies] On Settings screen");

        // ==================== VERIFY TERMS AND CONDITIONS ====================
        logger.info("[FanTermsAndPolicies] Step 3: Verify Terms and Conditions of Sale");
        termsAndPolicies.verifyTermsAndConditions();
        logger.info("[FanTermsAndPolicies] Terms and Conditions verified");

        // ==================== VERIFY COMMUNITY REGULATIONS ====================
        logger.info("[FanTermsAndPolicies] Step 4: Verify Community Regulations");
        termsAndPolicies.verifyCommunityRegulations();
        logger.info("[FanTermsAndPolicies] Community Regulations verified");

        // ==================== VERIFY CONTENT POLICY ====================
        logger.info("[FanTermsAndPolicies] Step 5: Verify Content Policy");
        termsAndPolicies.verifyContentPolicy();
        logger.info("[FanTermsAndPolicies] Content Policy verified");

        logger.info("[FanTermsAndPolicies] Test completed successfully: All Terms and Policies verified");
    }
}
