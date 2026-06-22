package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorLegalPages;

public class CreatorLegalPagesTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Verify Terms & Conditions of Sale and Community Regulations screens")
    public void verifyLegalPages() {
        CreatorLegalPages legal = new CreatorLegalPages(page);

        // Open Settings and ensure URL contains settings path
        legal.openSettingsFromProfile();
        legal.assertOnSettingsUrl();

        // Terms and conditions of sale
        legal.openTermsAndConditionsOfSale();
        legal.assertOnSaleTermsPage();
        legal.scrollDownToSaleBottomAndBackToTitle();
        legal.clickBackArrow();

        // Community regulations
        legal.openCommunityRegulations();
        legal.assertOnCommunityRegulationsPage();
        legal.scrollDownToCommunityBottomAndBackToTitle();
        legal.clickBackArrow();

        // Content Policy
        legal.openContentPolicy();
        legal.assertOnContentPolicyPage();
        legal.scrollDownToContentPolicyBottomAndBackToTitle();
        legal.clickBackArrow();
    }
}
