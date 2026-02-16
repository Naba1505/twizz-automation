package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

/**
 * Page Object for Fan Private Media Subscription flow.
 * Flow: Register → Search creator → Subscribe → Request private media → Send message →
 *       (Creator accepts & sends price) → Fan accepts & pays → Verify subscription
 */
public class FanPrivateMediaSubscriptionPage extends BasePage {

    public FanPrivateMediaSubscriptionPage(Page page) {
        super(page);
    }

    // ===== Helpers =====

    private void dismissOverlay() {
        try {
            page.evaluate("document.querySelectorAll('.fan-profile-overlay').forEach(el => el.remove())");
        } catch (Throwable ignored) {}
        try { page.waitForTimeout(300); } catch (Throwable ignored) {}
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

    private Locator requestPrivateMediaButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Request private media"));
    }

    private Locator yourMessageTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your message"));
    }

    private Locator acceptButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept"));
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

    private Locator twizzMessagesIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Twizz messages"));
    }

    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon"));
    }

    // ===== Fan Actions =====

    @Step("Assert Search icon is visible on dashboard")
    public void assertSearchIconVisible() {
        waitVisible(searchIcon(), ConfigReader.getVisibilityTimeout());
        logger.info("[FanPrivMedia] Search icon is visible on dashboard");
    }

    @Step("Click on Search icon to open search")
    public void clickSearchIcon() {
        Locator icon = page.locator("div").nth(5);
        clickWithRetry(icon, 1, 150);
        logger.info("[FanPrivMedia] Clicked on search icon");
    }

    @Step("Search for creator: {creatorUsername}")
    public void searchCreator(String creatorUsername) {
        Locator search = searchBox();
        waitVisible(search, ConfigReader.getShortTimeout());
        search.fill(creatorUsername);
        page.waitForTimeout(1500);
        logger.info("[FanPrivMedia] Filled search with: {}", creatorUsername);
    }

    @Step("Click on creator search result: {creatorUsername}")
    public void clickCreatorResult(String creatorUsername) {
        Locator result = page.getByText(creatorUsername);
        waitVisible(result.first(), ConfigReader.getShortTimeout());
        clickWithRetry(result.first(), 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Clicked on creator result: {}", creatorUsername);
    }

    @Step("Skip intro screens by clicking on hint texts")
    public void skipIntroScreens() {
        try {
            Locator clickHere = page.getByText("Click here to see the creator");
            if (clickHere.count() > 0 && safeIsVisible(clickHere.first())) {
                clickWithRetry(clickHere.first(), 1, 150);
                logger.info("[FanPrivMedia] Clicked 'Click here to see the creator'");
                page.waitForTimeout(1000);
            }
        } catch (Throwable ignored) {}

        try {
            Locator andHere = page.getByText("And here to see their");
            if (andHere.count() > 0 && safeIsVisible(andHere.first())) {
                clickWithRetry(andHere.first(), 1, 150);
                logger.info("[FanPrivMedia] Clicked 'And here to see their'");
                page.waitForTimeout(1000);
            }
        } catch (Throwable ignored) {}

        logger.info("[FanPrivMedia] Intro screens skipped");
    }

    @Step("Click Subscribe button")
    public void clickSubscribe() {
        Locator btn = subscribeButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        try { btn.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        dismissOverlay();
        clickWithRetry(btn, 2, 300);
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Clicked Subscribe button");
    }

    @Step("Assert 'Subscription is free when' text is visible")
    public void assertFreeSubscriptionTextVisible() {
        waitVisible(page.getByText("Subscription is free when"), ConfigReader.getShortTimeout());
        logger.info("[FanPrivMedia] 'Subscription is free when' text is visible");
    }

    @Step("Click 'Request private media' button")
    public void clickRequestPrivateMedia() {
        dismissOverlay();
        Locator btn = requestPrivateMediaButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Clicked 'Request private media' button");
    }

    @Step("Assert on creator message conversation screen and click message box")
    public void assertOnMessageScreen() {
        Locator msgBox = yourMessageTextbox();
        waitVisible(msgBox, ConfigReader.getVisibilityTimeout());
        msgBox.click();
        logger.info("[FanPrivMedia] On creator message conversation screen - message box visible");
    }

    @Step("Send message: {message}")
    public void sendMessage(String message) {
        Locator msgBox = yourMessageTextbox();
        waitVisible(msgBox, ConfigReader.getShortTimeout());
        msgBox.fill(message);
        // Press Enter to send
        msgBox.press("Enter");
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Sent message: {}", message);
    }

    @Step("Assert price row visible and click Accept")
    public void assertPriceRowAndAccept() {
        // Wait for price row to appear (creator has sent the price)
        Locator priceRow = page.locator(".ant-row.priceRow");
        waitVisible(priceRow.first(), ConfigReader.getVisibilityTimeout());
        logger.info("[FanPrivMedia] Price row is visible");

        // Click Accept
        Locator accept = acceptButton();
        waitVisible(accept, ConfigReader.getShortTimeout());
        clickWithRetry(accept, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Clicked Accept on price from creator");
    }

    @Step("Assert 'Secure payment' screen is visible")
    public void assertSecurePaymentVisible() {
        waitVisible(securePaymentText(), ConfigReader.getVisibilityTimeout());
        logger.info("[FanPrivMedia] 'Secure payment' screen is visible");
    }

    @Step("Fill card number: {cardNumber}")
    public void fillCardNumber(String cardNumber) {
        Locator field = cardNumberField();
        waitVisible(field, ConfigReader.getShortTimeout());
        field.click();
        field.fill(cardNumber);
        logger.info("[FanPrivMedia] Filled card number");
    }

    @Step("Click on Expiration date area")
    public void clickExpirationDateArea() {
        Locator area = page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Expiration date$"))).nth(2);
        clickWithRetry(area, 1, 150);
        logger.info("[FanPrivMedia] Clicked on Expiration date area");
    }

    @Step("Fill expiry: {expiry}")
    public void fillExpiry(String expiry) {
        Locator field = expiryField();
        waitVisible(field, ConfigReader.getShortTimeout());
        field.click();
        field.fill(expiry);
        logger.info("[FanPrivMedia] Filled expiry");
    }

    @Step("Fill CVC: {cvc}")
    public void fillCvc(String cvc) {
        Locator field = cvcField();
        waitVisible(field, ConfigReader.getShortTimeout());
        field.click();
        field.fill(cvc);
        logger.info("[FanPrivMedia] Filled CVC");
    }

    @Step("Fill payment card details")
    public void fillPaymentDetails(String cardNumber, String expiry, String cvc) {
        fillCardNumber(cardNumber);
        clickExpirationDateArea();
        fillExpiry(expiry);
        fillCvc(cvc);
        logger.info("[FanPrivMedia] Payment details filled");
    }

    @Step("Click Confirm button")
    public void clickConfirm() {
        Locator btn = confirmButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Clicked Confirm button");
    }

    @Step("Complete 3DS verification (Submit + Everything is OK)")
    public void complete3DSVerification() {
        logger.info("[FanPrivMedia] Starting 3DS verification flow");
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
            logger.info("[FanPrivMedia] 3DS page found: {}", threeDSPage.url());
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
                        logger.info("[FanPrivMedia] Clicked Submit on 3DS page");
                    }
                } catch (Throwable ignored) {}
                if (!submitted) {
                    try {
                        Locator xpathSubmit = threeDSPage.locator("xpath=//div//input[@value='Submit']");
                        if (xpathSubmit.count() > 0) {
                            xpathSubmit.first().click(new Locator.ClickOptions().setTimeout(3000));
                            submitted = true;
                            logger.info("[FanPrivMedia] Clicked Submit (xpath) on 3DS page");
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
                        logger.info("[FanPrivMedia] Clicked 'Everything is OK' on 3DS page");
                    }
                }
            } catch (Throwable ignored) {
                logger.warn("[FanPrivMedia] 'Everything is OK' button not found on 3DS page");
            }

            // Close 3DS page if still open
            try {
                if (!threeDSPage.isClosed() && appPage.context().pages().size() > 1) {
                    threeDSPage.close();
                }
            } catch (Throwable ignored) {}
        } else {
            logger.warn("[FanPrivMedia] 3DS page not found, checking iframes");
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
        logger.info("[FanPrivMedia] 3DS verification flow completed");
    }

    @Step("Click Twizz messages icon")
    public void clickTwizzMessagesIcon() {
        dismissOverlay();
        Locator icon = twizzMessagesIcon();
        waitVisible(icon, ConfigReader.getShortTimeout());
        clickWithRetry(icon, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Clicked Twizz messages icon");
    }

    @Step("Click Settings icon")
    public void clickSettingsIcon() {
        dismissOverlay();
        Locator icon = settingsIcon();
        waitVisible(icon, ConfigReader.getShortTimeout());
        clickWithRetry(icon, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Clicked Settings icon");
    }

    @Step("Click 'My creators' menu item")
    public void clickMyCreators() {
        Locator myCreators = page.getByText("My creators");
        waitVisible(myCreators, ConfigReader.getShortTimeout());
        clickWithRetry(myCreators, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Clicked 'My creators'");
    }

    @Step("Assert creator '{creatorName}' is displayed in My creators")
    public void assertCreatorDisplayed(String creatorName) {
        Locator creator = page.getByText(creatorName);
        waitVisible(creator.first(), ConfigReader.getVisibilityTimeout());
        logger.info("[FanPrivMedia] Creator '{}' is displayed in My creators - subscription confirmed!", creatorName);
    }
}
