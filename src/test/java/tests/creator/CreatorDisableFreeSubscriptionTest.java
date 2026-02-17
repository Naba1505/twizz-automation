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

        // Assert Free subscription visible and disable toggle
        freeSub.assertFreeSubscriptionVisible();
        freeSub.disableFreeSubscriptionToggle();

        // Assert Featured collection visible and disable toggle
        freeSub.assertFeaturedCollectionVisible();
        freeSub.disableFeaturedCollectionToggle();

        // Click Register to save
        freeSub.clickRegister();

        // Assert success toast
        freeSub.assertUpdatedPersonalInfoToast();
    }
}
