package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

/**
 * Page Object for Fan Free Subscription flow.
 * Flow: Register → Search creator → Subscribe (free) → Buy collection → Payment → Verify
 */
public class FanFreeSubscriptionPage extends BasePage {

    public FanFreeSubscriptionPage(Page page) {
        super(page);
    }

    // ===== Locators =====

    private Locator searchIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search icon"));
    }

    private Locator searchBox() {
        return page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search"));
    }

    private Locator subscribeButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscribe"));
    }

    private Locator freeSubscriptionText() {
        return page.getByText("Subscription is free when");
    }

    private Locator buyCollectionButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Buy a collection"));
    }

    private Locator payToSeeButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pay to see"));
    }

    private Locator securePaymentText() {
        return page.getByText("Secure payment");
    }

    private Locator cardNumberField() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("1234 1234"));
    }

    private Locator expiryField() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("MM/YY"));
    }

    private Locator cvcField() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("CVC"));
    }

    private Locator confirmButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm"));
    }

    private Locator collectionText() {
        return page.getByText("Collection", new Page.GetByTextOptions().setExact(true));
    }

    private Locator backIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("back"));
    }

    private Locator subscriberButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscriber"));
    }

    // ===== Helpers =====

    private void dismissOverlay() {
        try {
            page.evaluate("document.querySelectorAll('.fan-profile-overlay').forEach(el => el.remove())");
        } catch (Throwable ignored) {}
        try { page.waitForTimeout(300); } catch (Throwable ignored) {}
    }

    // ===== Actions & Asserts =====

    @Step("Assert Search icon is visible on dashboard")
    public void assertSearchIconVisible() {
        waitVisible(searchIcon(), ConfigReader.getVisibilityTimeout());
        logger.info("[FanFreeSub] Search icon is visible on dashboard");
    }

    @Step("Click on Search icon to open search")
    public void clickSearchIcon() {
        Locator icon = page.locator("div").nth(5);
        clickWithRetry(icon, 1, 150);
        logger.info("[FanFreeSub] Clicked on search icon");
    }

    @Step("Search for creator: {creatorUsername}")
    public void searchCreator(String creatorUsername) {
        Locator search = searchBox();
        waitVisible(search, ConfigReader.getShortTimeout());
        search.fill(creatorUsername);
        page.waitForTimeout(1500);
        logger.info("[FanFreeSub] Filled search with: {}", creatorUsername);
    }

    @Step("Click on creator search result: {creatorUsername}")
    public void clickCreatorResult(String creatorUsername) {
        Locator result = page.getByText(creatorUsername);
        waitVisible(result.first(), ConfigReader.getShortTimeout());
        clickWithRetry(result.first(), 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanFreeSub] Clicked on creator result: {}", creatorUsername);
    }

    @Step("Skip intro screens by clicking on hint texts")
    public void skipIntroScreens() {
        // Click "Click here to see the creator" text to skip first intro
        try {
            Locator clickHere = page.getByText("Click here to see the creator");
            if (clickHere.count() > 0 && safeIsVisible(clickHere.first())) {
                clickWithRetry(clickHere.first(), 1, 150);
                logger.info("[FanFreeSub] Clicked 'Click here to see the creator'");
                page.waitForTimeout(1000);
            }
        } catch (Throwable ignored) {}

        // Click "And here to see their" text to skip second intro
        try {
            Locator andHere = page.getByText("And here to see their");
            if (andHere.count() > 0 && safeIsVisible(andHere.first())) {
                clickWithRetry(andHere.first(), 1, 150);
                logger.info("[FanFreeSub] Clicked 'And here to see their'");
                page.waitForTimeout(1000);
            }
        } catch (Throwable ignored) {}

        logger.info("[FanFreeSub] Intro screens skipped");
    }

    @Step("Assert Subscribe button visible and click")
    public void clickSubscribe() {
        Locator btn = subscribeButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        try { btn.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        dismissOverlay();
        clickWithRetry(btn, 2, 300);
        page.waitForTimeout(2000);
        logger.info("[FanFreeSub] Clicked Subscribe button");
    }

    @Step("Assert 'Subscription is free when' text is visible")
    public void assertFreeSubscriptionTextVisible() {
        waitVisible(freeSubscriptionText(), ConfigReader.getShortTimeout());
        logger.info("[FanFreeSub] 'Subscription is free when' text is visible");
    }

    @Step("Click 'Continue' button for direct free subscription")
    public void clickContinue() {
        dismissOverlay();
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanFreeSub] Clicked 'Continue' button");
    }

    @Step("Click 'Buy a collection' button")
    public void clickBuyCollection() {
        dismissOverlay();
        Locator btn = buyCollectionButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanFreeSub] Clicked 'Buy a collection' button");
    }

    @Step("Click on visible collection item")
    public void clickCollectionItem() {
        dismissOverlay();
        Locator collection = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Smithphoto hot"));
        waitVisible(collection, ConfigReader.getShortTimeout());
        clickWithRetry(collection, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanFreeSub] Clicked on collection item");
    }

    @Step("Click 'Pay to see' button")
    public void clickPayToSee() {
        dismissOverlay();
        Locator btn = payToSeeButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanFreeSub] Clicked 'Pay to see' button");
    }

    @Step("Assert 'Secure payment' screen is visible")
    public void assertSecurePaymentVisible() {
        waitVisible(securePaymentText(), ConfigReader.getVisibilityTimeout());
        logger.info("[FanFreeSub] 'Secure payment' screen is visible");
    }

    @Step("Fill card number: {cardNumber}")
    public void fillCardNumber(String cardNumber) {
        Locator field = cardNumberField();
        waitVisible(field, ConfigReader.getShortTimeout());
        field.click();
        field.fill(cardNumber);
        logger.info("[FanFreeSub] Filled card number");
    }

    @Step("Click on Expiration date area")
    public void clickExpirationDateArea() {
        Locator area = page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Expiration date$"))).nth(2);
        clickWithRetry(area, 1, 150);
        logger.info("[FanFreeSub] Clicked on Expiration date area");
    }

    @Step("Fill expiry: {expiry}")
    public void fillExpiry(String expiry) {
        Locator field = expiryField();
        waitVisible(field, ConfigReader.getShortTimeout());
        field.click();
        field.fill(expiry);
        logger.info("[FanFreeSub] Filled expiry");
    }

    @Step("Fill CVC: {cvc}")
    public void fillCvc(String cvc) {
        Locator field = cvcField();
        waitVisible(field, ConfigReader.getShortTimeout());
        field.click();
        field.fill(cvc);
        logger.info("[FanFreeSub] Filled CVC");
    }

    @Step("Fill payment card details")
    public void fillPaymentDetails(String cardNumber, String expiry, String cvc) {
        fillCardNumber(cardNumber);
        clickExpirationDateArea();
        fillExpiry(expiry);
        fillCvc(cvc);
        logger.info("[FanFreeSub] Payment details filled");
    }

    @Step("Click Confirm button")
    public void clickConfirm() {
        Locator btn = confirmButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanFreeSub] Clicked Confirm button");
    }

    @Step("Complete 3DS verification (Submit + Everything is OK)")
    public void complete3DSVerification() {
        logger.info("[FanFreeSub] Starting 3DS verification flow");
        Page appPage = page;

        // Wait for 3DS popup page
        Page threeDSPage = null;
        for (int i = 0; i < 20 && threeDSPage == null; i++) {
            for (Page p : appPage.context().pages()) {
                try {
                    if (p != appPage && p.url() != null && p.url().toLowerCase().contains("securionpay")) {
                        threeDSPage = p;
                        break;
                    }
                } catch (Throwable ignored) {}
            }
            if (threeDSPage != null) break;
            try { appPage.waitForTimeout(500); } catch (Throwable ignored) {}
        }

        if (threeDSPage != null) {
            logger.info("[FanFreeSub] 3DS page found: {}", threeDSPage.url());
            try { threeDSPage.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED); } catch (Throwable ignored) {}
            try { threeDSPage.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE); } catch (Throwable ignored) {}

            // Click Submit button
            boolean submitted = false;
            for (int i = 0; i < 3 && !submitted; i++) {
                try {
                    if (threeDSPage.isClosed()) break;
                    Locator submitBtn = threeDSPage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
                    if (submitBtn.count() > 0 && safeIsVisible(submitBtn.first())) {
                        try { submitBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                        submitBtn.first().click(new Locator.ClickOptions().setTimeout(3000));
                        submitted = true;
                        logger.info("[FanFreeSub] Clicked Submit on 3DS page");
                    }
                } catch (Throwable ignored) {}
                if (!submitted) {
                    try {
                        Locator xpathSubmit = threeDSPage.locator("xpath=//div//input[@value='Submit']");
                        if (xpathSubmit.count() > 0) {
                            xpathSubmit.first().click(new Locator.ClickOptions().setTimeout(3000));
                            submitted = true;
                            logger.info("[FanFreeSub] Clicked Submit (xpath) on 3DS page");
                        }
                    } catch (Throwable ignored) {}
                }
                if (!submitted) {
                    try { threeDSPage.waitForTimeout(500); } catch (Throwable ignored) {}
                }
            }

            // Click "Everything is OK" button
            try {
                if (!threeDSPage.isClosed()) {
                    Locator okBtn = threeDSPage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Everything is OK"));
                    okBtn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
                    if (okBtn.count() > 0 && safeIsVisible(okBtn.first())) {
                        clickWithRetry(okBtn.first(), 1, 150);
                        logger.info("[FanFreeSub] Clicked 'Everything is OK' on 3DS page");
                    }
                }
            } catch (Throwable ignored) {
                logger.warn("[FanFreeSub] 'Everything is OK' button not found on 3DS page");
            }

            // Close 3DS page if still open
            try {
                if (!threeDSPage.isClosed() && appPage.context().pages().size() > 1) {
                    threeDSPage.close();
                }
            } catch (Throwable ignored) {}
        } else {
            logger.warn("[FanFreeSub] 3DS page not found, checking iframes");
            // Fallback: check iframes
            for (com.microsoft.playwright.Frame fr : appPage.frames()) {
                try {
                    if (fr.url() != null && fr.url().toLowerCase().contains("securionpay")) {
                        try {
                            Locator frSubmit = fr.getByRole(AriaRole.BUTTON, new com.microsoft.playwright.Frame.GetByRoleOptions().setName("Submit"));
                            if (frSubmit.count() > 0) { frSubmit.first().click(); }
                        } catch (Throwable ignored) {}
                        try {
                            Locator frOk = fr.getByRole(AriaRole.BUTTON, new com.microsoft.playwright.Frame.GetByRoleOptions().setName("Everything is OK"));
                            if (frOk.count() > 0) { frOk.first().click(); }
                        } catch (Throwable ignored) {}
                    }
                } catch (Throwable ignored) {}
            }
        }

        // Ensure focus back on app page
        try { appPage.bringToFront(); } catch (Throwable ignored) {}
        try { appPage.waitForTimeout(3000); } catch (Throwable ignored) {}
        logger.info("[FanFreeSub] 3DS verification flow completed");
    }

    @Step("Assert 'Collection' text visible (collection buy success)")
    public void assertCollectionBuySuccess() {
        waitVisible(collectionText(), ConfigReader.getVisibilityTimeout());
        logger.info("[FanFreeSub] 'Collection' text visible - collection buy success");
    }

    @Step("Click back icon to navigate back")
    public void clickBack() {
        dismissOverlay();
        Locator back = backIcon();
        waitVisible(back, ConfigReader.getShortTimeout());
        clickWithRetry(back, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanFreeSub] Clicked back icon");
    }

    @Step("Assert 'Subscriber' button visible (subscription confirmed)")
    public void assertSubscriberVisible() {
        Locator btn = subscriberButton();
        long end = System.currentTimeMillis() + 20_000;
        while (System.currentTimeMillis() < end) {
            if (btn.count() > 0 && safeIsVisible(btn.first())) {
                logger.info("[FanFreeSub] 'Subscriber' button visible - free subscription confirmed!");
                return;
            }
            try { page.waitForTimeout(500); } catch (Throwable ignored) {}
        }
        // Final check with assertion
        waitVisible(btn, 5_000);
        logger.info("[FanFreeSub] 'Subscriber' button visible - free subscription confirmed!");
    }
}
