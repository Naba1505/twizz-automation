package tests;

import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import pages.CreatorMonetizationPage;

public class CreatorSubscriptionPriceTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Update Subscription Price: validate monthly lock, enable quarterly, set price and save")
    public void creatorCanUpdateSubscriptionPrices() {
        // Precondition: creatorLogin() from BaseCreatorTest already logged us in

        CreatorMonetizationPage monetization = new CreatorMonetizationPage(page);

        // Navigate to Subscription price screen from Settings
        monetization.openSubscriptionPriceFromProfile();

        // Ensure we are on correct screen and monthly offer default state
        monetization.assertMonthlyOfferDefaultEnabled();

        // Try to disable monthly (should show validation)
        monetization.attemptDisableMonthlyShowsPopup();

        // Ensure Quarterly section visible and enable toggle if needed
        monetization.assertQuarterlyOfferVisible();
        monetization.enableQuarterlyToggleIfNeeded();

        // Set quarterly price and save
        monetization.setQuarterlyPrice("5");
        monetization.clickContinue();

        // Expect success toast
        monetization.waitForMonetizationUpdatedToast();
    }

    @Test(priority = 2, description = "Disable Quarterly Offer: ensure section visible, disable toggle, save and verify")
    public void creatorCanDisableQuarterlyOffer() {
        // Precondition: creatorLogin() from BaseCreatorTest already logged us in

        CreatorMonetizationPage monetization = new CreatorMonetizationPage(page);

        // Navigate to Subscription price screen from Settings (stop at title visible)
        monetization.openSubscriptionPriceFromProfile();

        // Ensure Quarterly section visible (same as previous test)
        monetization.assertQuarterlyOfferVisible();

        // Wait for page to settle, then disable Quarterly like codegen
        try { page.waitForTimeout(5_000); } catch (Throwable ignored) {}

        // Disable quarterly toggle (switch index 1) once
        page.getByRole(AriaRole.SWITCH).nth(1).click();

        // Click Continue to save
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Continue")).click();

        // Expect success toast
        monetization.waitForMonetizationUpdatedToast();
    }
}
