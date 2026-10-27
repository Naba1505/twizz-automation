package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorFreeSubscriptionPage;

public class CreatorDisableFreeSubscriptionTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Disable Free Subscription and Featured Collection toggles")
    public void creatorCanDisableFreeSubscription() {
        // Navigate to profile
        CreatorFreeSubscriptionPage freeSub = new CreatorFreeSubscriptionPage(page);
        freeSub.navigateToProfile();

        // Open Settings → Profile settings
        freeSub.clickSettingsIcon();
        freeSub.clickProfileSettings();

        // Ensure a real state change happens first — the "Updated Personal Information"
        // toast only fires when settings are actually modified. If toggles are already
        // off, enabling then disabling still produces a change for the save.
        freeSub.assertFreeSubscriptionVisible();
        freeSub.enableFreeSubscriptionToggle();
        freeSub.disableFreeSubscriptionToggle();

        // Assert Featured collection visible and disable toggle
        freeSub.assertFeaturedCollectionVisible();
        freeSub.enableFeaturedCollectionToggle();
        freeSub.disableFeaturedCollectionToggle();

        // Click Register to save
        freeSub.clickRegister();

        // Assert success toast
        freeSub.assertUpdatedPersonalInfoToast();
    }
}
