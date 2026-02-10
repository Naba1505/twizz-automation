package tests.creator;

import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorFreeSubscriptionPage;
import pages.creator.CreatorLoginPage;
import utils.ConfigReader;

public class CreatorEnableFreeSubscriptionTest extends BaseTestClass {

    @Test(priority = 1, description = "Enable Free Subscription and Featured Collection toggles")
    public void creatorCanEnableFreeSubscription() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);

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
