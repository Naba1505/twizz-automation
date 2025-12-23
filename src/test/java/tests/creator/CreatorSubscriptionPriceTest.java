package tests.creator;

import com.microsoft.playwright.options.AriaRole;
import org.testng.annotations.Test;
import pages.creator.CreatorMonetizationPage;

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

        // Wait for page to settle
        try { page.waitForTimeout(2_000); } catch (Throwable ignored) {}

        // Get the quarterly toggle (switch index 1)
        var quarterlySwitch = page.getByRole(AriaRole.SWITCH).nth(1);
        
        // Check if quarterly is currently enabled (aria-checked="true")
        String ariaChecked = quarterlySwitch.getAttribute("aria-checked");
        boolean isEnabled = "true".equals(ariaChecked);
        
        if (!isEnabled) {
            // Quarterly is already disabled - enable it first, save, then disable
            // Enable quarterly
            quarterlySwitch.click();
            try { page.waitForTimeout(500); } catch (Throwable ignored) {}
            
            // Set a price (required when enabling)
            monetization.setQuarterlyPrice("5");
            
            // Save changes
            monetization.clickContinue();
            monetization.waitForMonetizationUpdatedToast();
            
            // Navigate back to profile first using browser back or back arrow
            try { page.waitForTimeout(1_000); } catch (Throwable ignored) {}
            try {
                var backArrow = page.getByRole(AriaRole.IMG, new com.microsoft.playwright.Page.GetByRoleOptions().setName("arrow left"));
                if (backArrow.count() > 0 && backArrow.first().isVisible()) {
                    backArrow.first().click();
                    try { page.waitForTimeout(500); } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
            // Go back again to profile
            try { page.goBack(); } catch (Throwable ignored) {}
            try { page.waitForTimeout(1_000); } catch (Throwable ignored) {}
            
            // Re-open subscription price screen
            monetization.openSubscriptionPriceFromProfile();
            monetization.assertQuarterlyOfferVisible();
            try { page.waitForTimeout(1_000); } catch (Throwable ignored) {}
            
            // Re-get the switch reference
            quarterlySwitch = page.getByRole(AriaRole.SWITCH).nth(1);
        }
        
        // Now disable quarterly toggle
        quarterlySwitch.click();
        try { page.waitForTimeout(500); } catch (Throwable ignored) {}

        // Click Continue to save using page object method (has wait logic)
        monetization.clickContinue();

        // Expect success toast
        monetization.waitForMonetizationUpdatedToast();
    }
}
