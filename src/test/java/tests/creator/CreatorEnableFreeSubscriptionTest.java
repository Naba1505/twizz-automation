package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorFreeSubscriptionPage;

public class CreatorEnableFreeSubscriptionTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Enable Free Subscription and Featured Collection toggles")
    public void creatorCanEnableFreeSubscription() {
        // Navigate to profile
        CreatorFreeSubscriptionPage freeSub = new CreatorFreeSubscriptionPage(page);
        freeSub.navigateToProfile();

        // Open Settings → Profile settings
        freeSub.clickSettingsIcon();
        freeSub.clickProfileSettings();

        // Assert Free subscription visible and enable toggle
        freeSub.assertFreeSubscriptionVisible();
        freeSub.enableFreeSubscriptionToggle();

        // Assert Featured collection visible and enable toggle
        freeSub.assertFeaturedCollectionVisible();
        freeSub.enableFeaturedCollectionToggle();

        // Click Register to save
        freeSub.clickRegister();

        // Assert success toast
        freeSub.assertUpdatedPersonalInfoToast();
    }
}
