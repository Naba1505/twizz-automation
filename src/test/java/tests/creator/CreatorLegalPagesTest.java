package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorLegalPages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorLegalPagesTest extends BaseCreatorTest {
    private static final Logger log = LoggerFactory.getLogger(CreatorLegalPagesTest.class);

    @Test(priority = 1, description = "Verify Terms & Conditions of Sale and Community Regulations screens")
    public void verifyLegalPages() {
        CreatorLegalPages legal = new CreatorLegalPages(page);

        // Open Settings and ensure URL contains settings path
        legal.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

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
