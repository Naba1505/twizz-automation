package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorAutomaticMessagePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorAutomaticMessageTest extends BaseCreatorTest {
    private static final Logger log = LoggerFactory.getLogger(CreatorAutomaticMessageTest.class);

    @Test(priority = 1, description = "Verify Automatic Message - New subscriber create and enable")
    public void verifyNewSubscriberAutoMessageCreateAndEnable() {
        String imagePath = "src/test/resources/Images/AutoMessageImage.png";
        String message = "Message";

        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Open Automatic Message
        amPage.openAutomaticMessage();
        amPage.assertNewSubscriberHeaderAndInfo();

        // Modify flow
        amPage.clickModifyFirst();
        amPage.addMediaFromMyDevice(imagePath);
        amPage.clickNext();
        amPage.fillMessageAndSetPrice(message);
        amPage.clickSaveOnly();
        try { page.waitForTimeout(1500); } catch (Throwable ignored) {}
    }

    @Test(priority = 2, description = "Verify Automatic Message - Renew subscriber modify with free price")
    public void verifyRenewSubscriberAutoMessageModifyWithFreePrice() {
        String imagePath = "src/test/resources/Images/AutoMessageImageA.png";
        String message = "Message";

        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Open Automatic Message
        amPage.openAutomaticMessage();
        amPage.assertRenewSubscriberHeaderAndInfo();

        // Modify flow for Renew subscriber
        amPage.clickModifySecond();
        amPage.addMediaFromMyDevice(imagePath);
        amPage.clickNext();
        amPage.fillMessageAndSetPriceFree(message);
        amPage.clickSaveOnly();

        // Wait briefly for upload; then ensure 'Automation' title is visible again
        try { page.waitForTimeout(2000); } catch (Throwable ignored) {}
        amPage.assertAutomationTitleVisible();
        try { page.waitForTimeout(500); } catch (Throwable ignored) {}
    }

        @Test(priority = 3, description = "Verify Automatic Message - Unsubscribe modify with free price")
    public void verifyUnsubscribeAutoMessageModifyWithFreePrice() {
        String imagePath = "src/test/resources/Images/AutoMessageImageB.jpg";
        String message = "Message";

        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

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

        // Wait some time for upload and ensure Automation title is visible again
        try { page.waitForTimeout(2000); } catch (Throwable ignored) {}
        amPage.assertAutomationTitleVisible();
        try { page.waitForTimeout(500); } catch (Throwable ignored) {}
    }

        @Test(priority = 4, description = "Verify Automatic Message - Re-subscription modify with 15€ and promotion")
    public void verifyResubscriptionAutoMessageModifyWithPromotion() {
        String imagePath = "src/test/resources/Images/AutoMessageImageD.jpg";
        String message = "Message";

        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

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

        // Wait some time for upload and ensure Automation title is visible again
        try { page.waitForTimeout(2000); } catch (Throwable ignored) {}
        amPage.assertAutomationTitleVisible();
        try { page.waitForTimeout(500); } catch (Throwable ignored) {}
    }

        @Test(priority = 5, description = "Verify Automatic Message - Delete added media and disable all messages")
    public void verifyDeleteMediaAndDisableAllAutoMessages() {
        CreatorAutomaticMessagePage amPage = new CreatorAutomaticMessagePage(page);

        // Open Settings and ensure URL contains settings path
        amPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Open Automatic Message and ensure title visible
        amPage.openAutomaticMessage();
        amPage.assertAutomationTitleVisible();

        // 1) First Modify (New subscriber)
        amPage.clickModifyFirst();
        amPage.deleteAllVisibleMedia();
        amPage.clearMessageToSpace();
        amPage.clickSaveOnly();
        try { page.waitForTimeout(800); } catch (Throwable ignored) {}

        // 2) Second Modify (Renew subscriber)
        amPage.clickModifySecond();
        amPage.deleteAllVisibleMedia();
        amPage.clearMessageToSpace();
        amPage.clickSaveOnly();
        try { page.waitForTimeout(800); } catch (Throwable ignored) {}

        // 3) Third Modify (Unsubscribe)
        amPage.clickModifyThird();
        amPage.deleteAllVisibleMedia();
        amPage.clearMessageToSpace();
        amPage.clickSaveOnly();
        try { page.waitForTimeout(800); } catch (Throwable ignored) {}

        // 4) Fourth Modify (Re-subscription)
        amPage.clickModifyFourth();
        amPage.deleteAllVisibleMedia();
        amPage.clearMessageToSpace();
        amPage.clickSaveOnly();
        try { page.waitForTimeout(800); } catch (Throwable ignored) {}

        // Finally, disable all enabled toggles
        amPage.assertAutomationTitleVisible();
        amPage.disableAllFirstFourToggles();
        try { page.waitForTimeout(500); } catch (Throwable ignored) {}
    }
}
