package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorAutomaticMessagePage;

public class CreatorAutomaticMessageTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Verify Automatic Message - New subscriber create and enable")
    public void verifyNewSubscriberAutoMessageCreateAndEnable() {
        String imagePath = "src/test/resources/Images/AutoMessageImage.png";
        String message = "Message";

        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        amPage.assertOnSettingsUrl();

        // Open Automatic Message
        amPage.openAutomaticMessage();
        amPage.assertNewSubscriberHeaderAndInfo();

        // Modify flow
        amPage.clickModifyFirst();
        amPage.addMediaFromMyDevice(imagePath);
        amPage.clickNext();
        amPage.fillMessageAndSetPrice(message);
        amPage.clickSaveOnly();
        amPage.waitBriefly();
    }

    @Test(priority = 2, description = "Verify Automatic Message - Renew subscriber modify with free price")
    public void verifyRenewSubscriberAutoMessageModifyWithFreePrice() {
        String imagePath = "src/test/resources/Images/AutoMessageImageA.png";
        String message = "Message";

        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        amPage.assertOnSettingsUrl();

        // Open Automatic Message
        amPage.openAutomaticMessage();
        amPage.assertRenewSubscriberHeaderAndInfo();

        // Modify flow for Renew subscriber
        amPage.clickModifySecond();
        amPage.addMediaFromMyDevice(imagePath);
        amPage.clickNext();
        amPage.fillMessageAndSetPriceFree(message);
        amPage.clickSaveOnly();
        amPage.waitBriefly();
        amPage.assertAutomationTitleVisible();
        amPage.waitBriefly();
    }

        @Test(priority = 3, description = "Verify Automatic Message - Unsubscribe modify with free price")
    public void verifyUnsubscribeAutoMessageModifyWithFreePrice() {
        String imagePath = "src/test/resources/Images/AutoMessageImageB.jpg";
        String message = "Message";

        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        amPage.assertOnSettingsUrl();

        // Open Automatic Message and ensure title visible
        amPage.openAutomaticMessage();
        amPage.assertAutomationTitleVisible();
        amPage.assertUnsubscribeHeaderAndInfo();

        // Modify flow for Unsubscribe
        amPage.clickModifyThird();
        amPage.addMediaViaPlusFromMyDevice(imagePath);
        amPage.clickNext();
        amPage.fillMessageAndSetPriceFree(message);
        amPage.clickSaveOnly();
        amPage.waitBriefly();
        amPage.assertAutomationTitleVisible();
        amPage.waitBriefly();
    }

        @Test(priority = 4, description = "Verify Automatic Message - Re-subscription modify with 15€ and promotion")
    public void verifyResubscriptionAutoMessageModifyWithPromotion() {
        String imagePath = "src/test/resources/Images/AutoMessageImageD.jpg";
        String message = "Message";

        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        amPage.assertOnSettingsUrl();

        // Open Automatic Message and ensure section visible
        amPage.openAutomaticMessage();
        amPage.assertAutomationTitleVisible();
        amPage.assertResubscriptionHeaderAndInfo();

        // Modify flow for Re-subscription
        amPage.clickModifyFourth();
        amPage.addMediaViaPlusFromMyDevice(imagePath);
        amPage.clickNext();
        amPage.fillMessageAndSetPrice(message); // sets 15€
        amPage.enablePromotionAndFillDiscount("10");
        amPage.clickSaveOnly();
        amPage.waitBriefly();
        amPage.assertAutomationTitleVisible();
        amPage.waitBriefly();
    }

        @Test(priority = 5, description = "Verify Automatic Message - Delete added media and disable all messages")
    public void verifyDeleteMediaAndDisableAllAutoMessages() {
        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        amPage.assertOnSettingsUrl();

        // Open Automatic Message and ensure title visible
        amPage.openAutomaticMessage();
        amPage.assertAutomationTitleVisible();

        // 1) First Modify (New subscriber)
        amPage.clickModifyFirst();
        amPage.deleteAllVisibleMedia();
        amPage.clearMessageToSpace();
        amPage.clickSaveOnly();
        amPage.waitBriefly();

        // 2) Second Modify (Renew subscriber)
        amPage.clickModifySecond();
        amPage.deleteAllVisibleMedia();
        amPage.clearMessageToSpace();
        amPage.clickSaveOnly();
        amPage.waitBriefly();

        // 3) Third Modify (Unsubscribe)
        amPage.clickModifyThird();
        amPage.deleteAllVisibleMedia();
        amPage.clearMessageToSpace();
        amPage.clickSaveOnly();
        amPage.waitBriefly();

        // 4) Fourth Modify (Re-subscription)
        amPage.clickModifyFourth();
        amPage.deleteAllVisibleMedia();
        amPage.clearMessageToSpace();
        amPage.clickSaveOnly();
        amPage.waitBriefly();

        // Finally, disable all enabled toggles
        amPage.assertAutomationTitleVisible();
        amPage.disableAllFirstFourToggles();
        amPage.waitBriefly();
    }
}
