package tests.fan;

import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.AriaRole;

import pages.common.BaseTestClass;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorPrivateMediaPage;
import pages.fan.FanFreeSubscriptionPage;
import pages.fan.FanPrivateMediaSubscriptionPage;
import pages.fan.FanRegistrationPage;
import utils.BrowserFactory;
import utils.ConfigReader;
import utils.DataGenerator;

public class FanFreeSubscriptionTest extends BaseTestClass {

    private static final Logger logger = LoggerFactory.getLogger(FanFreeSubscriptionTest.class);

    @Test(priority = 1, description = "New fan registers, searches creator john_smith, does free subscription by buying a collection")
    public void fanCanDoFreeSubscriptionByBuyingCollection() {
        // Step 1: Register a new fan
        FanRegistrationPage fanReg = new FanRegistrationPage(page);

        String firstName = DataGenerator.generateUniqueFirstName();
        String lastName = DataGenerator.generateUniqueLastName();
        String username = DataGenerator.generateUniqueUsername("TwizzFan");
        String email = DataGenerator.generateUniqueEmail("TwizzFan");
        String password = ConfigReader.getProperty("fan.default.password", "Twizz$123");

        fanReg.completeFanRegistrationFlow(firstName, lastName, username, email, password);

        // Switch language to English (default is French from SEO team)
        page.waitForTimeout(2000); // Wait for registration to complete
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon")).click();
        page.waitForTimeout(1000);
        page.getByText("Langue").click();
        page.waitForTimeout(1000);
        page.locator("div:nth-child(2) > .ant-row > .ant-col.circle").click();
        page.waitForTimeout(1000);
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).click();
        page.waitForTimeout(500);
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).click();
        page.waitForTimeout(3000); // Wait for language change to take effect
        page.waitForLoadState(LoadState.NETWORKIDLE); // Wait for network to be idle
        page.waitForTimeout(2000); // Additional stabilization for UI re-render
        logger.info("Switched language to English after registration");

        // Step 2: Search for creator and subscribe
        FanFreeSubscriptionPage freeSub = new FanFreeSubscriptionPage(page);

        // Assert search icon visible on dashboard
        freeSub.assertSearchIconVisible();

        // Click search icon and search for creator
        freeSub.clickSearchIcon();
        freeSub.searchCreator("john_smith");
        freeSub.clickCreatorResult("john_smith");

        // Skip intro screens by clicking hint texts
        freeSub.skipIntroScreens();

        // Click Subscribe
        freeSub.clickSubscribe();

        // Assert free subscription message visible
        freeSub.assertFreeSubscriptionTextVisible();

        // Click Buy a collection
        freeSub.clickBuyCollection();

        // Click on visible collection
        freeSub.clickCollectionItem();

        // Click Pay to see
        freeSub.clickPayToSee();

        // Assert on Secure payment screen
        freeSub.assertSecurePaymentVisible();

        // Fill payment details
        String cardNumber = ConfigReader.getProperty("payment.card.number", "4012 0018 0000 0016");
        String cardExpiry = ConfigReader.getProperty("payment.card.expiry", "07/34");
        String cardCvc = ConfigReader.getProperty("payment.card.cvc", "657");
        freeSub.fillPaymentDetails(cardNumber, cardExpiry, cardCvc);

        // Use robust payment selection and confirmation
        freeSub.selectPaymentCard();
        freeSub.confirmPayment();

        // Complete 3DS verification (Submit + Everything is OK)
        freeSub.complete3DSVerification();

        // Assert collection buy success
        freeSub.assertCollectionBuySuccess();

        // Navigate back
        freeSub.clickBack();

        // Assert Subscriber button visible (subscription confirmed)
        freeSub.assertSubscriberVisible();
    }

    @Test(priority = 2, description = "New fan registers, searches creator john_smith, subscribes via private media request with payment")
    public void fanCanSubscribeViaPrivateMediaRequest() {
        // ===== STEP 1: Register a new fan =====
        FanRegistrationPage fanReg = new FanRegistrationPage(page);

        String firstName = DataGenerator.generateUniqueFirstName();
        String lastName = DataGenerator.generateUniqueLastName();
        String username = DataGenerator.generateUniqueUsername("TwizzFan");
        String email = DataGenerator.generateUniqueEmail("TwizzFan");
        String password = ConfigReader.getProperty("fan.default.password", "Twizz$123");

        fanReg.completeFanRegistrationFlow(firstName, lastName, username, email, password);

        // Switch language to English (default is French from SEO team)
        page.waitForTimeout(2000); // Wait for registration to complete
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon")).click();
        page.waitForTimeout(1000);
        page.getByText("Langue").click();
        page.waitForTimeout(1000);
        page.locator("div:nth-child(2) > .ant-row > .ant-col.circle").click();
        page.waitForTimeout(1000);
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).click();
        page.waitForTimeout(500);
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).click();
        page.waitForTimeout(3000); // Wait for language change to take effect
        page.waitForLoadState(LoadState.NETWORKIDLE); // Wait for network to be idle
        page.waitForTimeout(2000); // Additional stabilization for UI re-render
        logger.info("Switched language to English after registration");

        // ===== STEP 2: Fan searches for creator and subscribes =====
        FanPrivateMediaSubscriptionPage fanPage = new FanPrivateMediaSubscriptionPage(page);

        fanPage.assertSearchIconVisible();
        fanPage.clickSearchIcon();
        fanPage.searchCreator("john_smith");
        fanPage.clickCreatorResult("john_smith");

        // Skip intro screens
        fanPage.skipIntroScreens();

        // Click Subscribe
        fanPage.clickSubscribe();

        // Assert free subscription message
        fanPage.assertFreeSubscriptionTextVisible();

        // ===== STEP 3: Fan clicks "Request private media" =====
        fanPage.clickRequestPrivateMedia();

        // Assert on message conversation screen
        fanPage.assertOnMessageScreen();

        // Send "Hi" message to creator
        fanPage.sendMessage("Hi");

        // ===== STEP 4: Open creator session in a separate browser context =====
        BrowserContext creatorContext = BrowserFactory.createNewContext();
        Page creatorPage = creatorContext.newPage();
        creatorPage.setDefaultNavigationTimeout(ConfigReader.getNavigationTimeout());
        creatorPage.setDefaultTimeout(ConfigReader.getDefaultTimeout());

        try {
            // Navigate to login and log in as creator
            String loginUrl = ConfigReader.getLoginUrl();
            creatorPage.navigate(loginUrl);
            creatorPage.waitForLoadState(LoadState.DOMCONTENTLOADED);

            CreatorLoginPage creatorLogin = new CreatorLoginPage(creatorPage);
            String creatorUsername = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
            String creatorPassword = ConfigReader.getProperty("creator.password", "Twizz$123");
            creatorLogin.login(creatorUsername, creatorPassword);

            // Creator actions: open messaging, accept request, set amount, send
            CreatorPrivateMediaPage creatorActions = new CreatorPrivateMediaPage(creatorPage);

            creatorActions.clickMessagingIcon();
            creatorActions.clickReceivedMessage("Hi");
            creatorActions.clickAccept();
            creatorActions.assertAmountPopupVisible();
            creatorActions.setCustomAmount("5");
            creatorActions.typeReplyMessage("subscribe me");
            creatorActions.clickSend();
            creatorActions.assertPendingVisible();

        } finally {
            // Close creator context
            try { creatorContext.close(); } catch (Throwable ignored) {}
        }

        // ===== STEP 5: Back to fan - accept creator's price and pay =====
        page.bringToFront();
        page.waitForTimeout(3000);

        // Assert price row visible and click Accept
        fanPage.assertPriceRowAndAccept();

        // Assert on Secure payment screen
        fanPage.assertSecurePaymentVisible();

        // Fill payment details
        String cardNumber = ConfigReader.getProperty("payment.card.number", "4012 0018 0000 0016");
        String cardExpiry = ConfigReader.getProperty("payment.card.expiry", "07/34");
        String cardCvc = ConfigReader.getProperty("payment.card.cvc", "657");
        fanPage.fillPaymentDetails(cardNumber, cardExpiry, cardCvc);

        // Use robust payment selection and confirmation
        fanPage.selectPaymentCard();
        fanPage.confirmPayment();

        // Complete 3DS verification
        fanPage.complete3DSVerification();

        // ===== STEP 6: Verify subscription via My creators =====
        // Try direct navigation to Settings instead of going through messages
        try {
            fanPage.clickSettingsIcon();
            fanPage.clickMyCreators();
            fanPage.assertCreatorDisplayed("Smith");
        } catch (Exception e) {
            logger.warn("[FanPrivMedia] Could not verify via My creators: {}", e.getMessage());
            // Alternative verification - check if we're back on creator profile with Subscribe button gone
            try {
                page.goBack();
                page.waitForTimeout(2000);
                Locator subscribeBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscribe"));
                if (subscribeBtn.count() == 0 || !subscribeBtn.first().isVisible()) {
                    logger.info("[FanPrivMedia] Subscription confirmed - Subscribe button is gone");
                } else {
                    throw new RuntimeException("Subscription verification failed - Subscribe button still visible");
                }
            } catch (Exception ex) {
                throw new RuntimeException("Unable to verify subscription: " + ex.getMessage());
            }
        }
    }

    @Test(priority = 3, description = "New fan registers, searches creator john_smith, does direct free subscription via Continue + payment")
    public void fanCanDoDirectFreeSubscription() {
        // Step 1: Register a new fan
        FanRegistrationPage fanReg = new FanRegistrationPage(page);

        String firstName = DataGenerator.generateUniqueFirstName();
        String lastName = DataGenerator.generateUniqueLastName();
        String username = DataGenerator.generateUniqueUsername("TwizzFan");
        String email = DataGenerator.generateUniqueEmail("TwizzFan");
        String password = ConfigReader.getProperty("fan.default.password", "Twizz$123");

        fanReg.completeFanRegistrationFlow(firstName, lastName, username, email, password);

        // Switch language to English (default is French from SEO team)
        page.waitForTimeout(2000); // Wait for registration to complete
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon")).click();
        page.waitForTimeout(1000);
        page.getByText("Langue").click();
        page.waitForTimeout(1000);
        page.locator("div:nth-child(2) > .ant-row > .ant-col.circle").click();
        page.waitForTimeout(1000);
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).click();
        page.waitForTimeout(500);
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")).click();
        page.waitForTimeout(3000); // Wait for language change to take effect
        page.waitForLoadState(LoadState.NETWORKIDLE); // Wait for network to be idle
        page.waitForTimeout(2000); // Additional stabilization for UI re-render
        logger.info("Switched language to English after registration");

        // Step 2: Search for creator and subscribe
        FanFreeSubscriptionPage freeSub = new FanFreeSubscriptionPage(page);

        freeSub.assertSearchIconVisible();
        freeSub.clickSearchIcon();
        freeSub.searchCreator("john_smith");
        freeSub.clickCreatorResult("john_smith");

        // Skip intro screens
        freeSub.skipIntroScreens();

        // Click Subscribe
        freeSub.clickSubscribe();

        // Assert free subscription message visible
        freeSub.assertFreeSubscriptionTextVisible();

        // Step 3: Click Continue directly (no collection or private media)
        freeSub.clickContinue();

        // Step 4: Payment
        freeSub.assertSecurePaymentVisible();

        String cardNumber = ConfigReader.getProperty("payment.card.number", "4012 0018 0000 0016");
        String cardExpiry = ConfigReader.getProperty("payment.card.expiry", "07/34");
        String cardCvc = ConfigReader.getProperty("payment.card.cvc", "657");
        freeSub.fillPaymentDetails(cardNumber, cardExpiry, cardCvc);

        // Use robust payment selection and confirmation
        freeSub.selectPaymentCard();
        freeSub.confirmPayment();

        freeSub.complete3DSVerification();

        // Step 5: Assert Subscriber button visible (subscription confirmed)
        freeSub.assertSubscriberVisible();
    }
}
