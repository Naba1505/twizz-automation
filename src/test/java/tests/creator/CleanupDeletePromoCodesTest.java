package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorPromotionsPage;

public class CleanupDeletePromoCodesTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Cleanup: delete all 'AUTOMATION' promo codes with soft-assert of last success toast")
    public void deleteAutomationPromoCodes() {
        CreatorPromotionsPage promotions = new CreatorPromotionsPage(page);

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
