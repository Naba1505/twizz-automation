package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorPromotionsPage;
import utils.ConfigReader;

public class CleanupDeletePromoCodesTest extends BaseTestClass {

    @Test(priority = 1, description = "Cleanup: delete all 'AUTOMATION' promo codes with soft-assert of last success toast")
    public void deleteAutomationPromoCodes() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPromotionsPage promotions = new CreatorPromotionsPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Navigate to Promo code page
        promotions.openSettingsFromProfile();
        promotions.openPromoCodeScreen();

        // Delete all AUTOMATION promos; soft-assert toast on last deletion only
        promotions.deleteAllAutomationPromosSoft();

        // Final verification: ensure cleanup completed
        int remaining = promotions.getAutomationPromoCount();
        if (remaining > 0) {
            // Re-open view and attempt one more pass if anything lingers due to virtualization/pagination
            promotions.openPromoCodeScreen();
            promotions.deleteAllAutomationPromosSoft();
            remaining = promotions.getAutomationPromoCount();
        }
        Assert.assertEquals(remaining, 0, "Cleanup incomplete: 'AUTOMATION' promos remaining = " + remaining);
    }
}
