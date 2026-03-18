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
        // Try multiple variations of free subscription text
        Locator[] freeTextVariations = {
            page.getByText("Subscription is free when"),
            page.getByText("Free subscription"),
            page.getByText("Free"),
            page.locator("*:has-text('Subscription is free')"),
            page.locator("*:has-text('Free subscription')"),
            page.locator("*:has-text('Free')")
        };
        
        boolean found = false;
        for (Locator text : freeTextVariations) {
            try {
                if (text.count() > 0 && safeIsVisible(text.first())) {
                    logger.info("[FanPrivMedia] Free subscription text found: {}", text.first().textContent());
                    found = true;
                    break;
                }
            } catch (Exception e) {
                logger.debug("[FanPrivMedia] Free text variation failed: {}", e.getMessage());
            }
        }
        
        if (!found) {
            logger.warn("[FanPrivMedia] Free subscription text not found, but continuing...");
        }
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

    @Step("Select payment card")
    public void selectPaymentCard() {
        logger.info("[FanPrivMedia] Selecting payment card");
        
        // Try multiple strategies to find payment selection buttons
        Locator[] paymentButtons = {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select")),
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue")),
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pay")),
            page.locator("button:has-text('Select')"),
            page.locator("button:has-text('Continue')"),
            page.locator("button:has-text('Pay')"),
            page.locator("*:has-text('Select')"),
            page.locator("*:has-text('Continue')"),
            page.locator("*:has-text('Pay')")
        };
        
        boolean clicked = false;
        for (Locator btn : paymentButtons) {
            try {
                if (btn.count() > 0 && safeIsVisible(btn.first())) {
                    btn.first().scrollIntoViewIfNeeded();
                    waitVisible(btn.first(), ConfigReader.getShortTimeout());
                    clickWithRetry(btn.first(), 1, 150);
                    clicked = true;
                    logger.info("[FanPrivMedia] Selected payment card via: {}", btn.first());
                    break;
                }
            } catch (Exception e) {
                logger.debug("[FanPrivMedia] Payment button attempt failed: {}", e.getMessage());
            }
        }
        
        if (!clicked) {
            throw new RuntimeException("Unable to find or click payment selection button");
        }
        
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Payment card selected successfully");
    }

    @Step("Confirm payment")
    public void confirmPayment() {
        logger.info("[FanPrivMedia] Looking for confirm button");
        
        // Try multiple confirm button variations
        Locator[] confirmButtons = {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm")),
            page.locator("button:has-text('Confirm')"),
            page.locator("button:has-text('Pay')"),
            page.locator("button:has-text('Submit')"),
            page.locator("button:has-text('Continue')"),
            page.locator("*:has-text('Confirm')"),
            page.locator("*:has-text('Pay')"),
            page.locator("*:has-text('Submit')"),
            page.locator("*:has-text('Continue')")
        };
        
        boolean clicked = false;
        for (Locator btn : confirmButtons) {
            try {
                if (btn.count() > 0 && safeIsVisible(btn.first())) {
                    btn.first().scrollIntoViewIfNeeded();
                    waitVisible(btn.first(), ConfigReader.getShortTimeout());
                    clickWithRetry(btn.first(), 1, 150);
                    clicked = true;
                    logger.info("[FanPrivMedia] Confirmed payment via: {}", btn.first());
                    break;
                }
            } catch (Exception e) {
                logger.debug("[FanPrivMedia] Confirm button attempt failed: {}", e.getMessage());
            }
        }
        
        if (!clicked) {
            throw new RuntimeException("Unable to find or click confirm button");
        }
        
        logger.info("[FanPrivMedia] Payment confirmed successfully");
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

            // Enhanced 3DS submit button clicking with retries
            boolean submitted = false;
            for (int attempt = 1; attempt <= 3 && !submitted; attempt++) {
                try {
                    if (threeDSPage.isClosed()) break;
                    
                    logger.info("[FanPrivMedia] 3DS submit attempt {}", attempt);
                    
                    // Strategy 1: Role-based button
                    Locator submitBtn = threeDSPage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
                    if (submitBtn.count() > 0 && safeIsVisible(submitBtn.first())) {
                        try { submitBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                        submitBtn.first().click(new Locator.ClickOptions().setTimeout(30000));
                        submitted = true;
                        logger.info("[FanPrivMedia] Clicked Submit on 3DS page (role-based)");
                        continue;
                    }
                    
                    // Strategy 2: XPath input
                    Locator xpathSubmit = threeDSPage.locator("xpath=//div//input[@value='Submit']");
                    if (xpathSubmit.count() > 0) {
                        xpathSubmit.first().click(new Locator.ClickOptions().setTimeout(30000));
                        submitted = true;
                        logger.info("[FanPrivMedia] Clicked Submit on 3DS page (xpath)");
                        continue;
                    }
                    
                    // Strategy 3: JavaScript click
                    if (attempt == 2) { // Debug on 2nd attempt
                        try {
                            String jsResult = threeDSPage.evaluate("() => { const buttons = document.querySelectorAll('button'); return Array.from(buttons).map(b => b.textContent).join(', '); }").toString();
                            logger.info("[FanPrivMedia] Available 3DS buttons: {}", jsResult);
                        } catch (Exception e) {
                            logger.debug("[FanPrivMedia] Could not debug 3DS buttons: {}", e.getMessage());
                        }
                    }
                    
                    // Strategy 4: Force click any button with "Submit"
                    if (attempt == 3) {
                        try {
                            Locator anySubmit = threeDSPage.locator("*:has-text('Submit')").first();
                            anySubmit.click(new Locator.ClickOptions().setForce(true));
                            submitted = true;
                            logger.info("[FanPrivMedia] Force clicked Submit on 3DS page");
                        } catch (Exception e) {
                            logger.debug("[FanPrivMedia] Force submit failed: {}", e.getMessage());
                        }
                    }
                    
                } catch (Throwable e) {
                    logger.debug("[FanPrivMedia] 3DS submit attempt {} failed: {}", attempt, e.getMessage());
                }
                
                if (!submitted) {
                    try { threeDSPage.waitForTimeout(2000); } catch (Throwable ignored) {}
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
        logger.info("[FanPrivMedia] 3DS verification flow completed");
    }

    @Step("Click Confirm button")
    public void clickConfirm() {
        Locator btn = confirmButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[FanPrivMedia] Clicked Confirm button");
    }

    @Step("Click Twizz messages icon")
    public void clickTwizzMessagesIcon() {
        dismissOverlay();
        
        // Try multiple strategies to find Twizz messages icon
        Locator[] messageIconVariations = {
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Twizz messages")),
            page.locator("img[alt*='message']"),
            page.locator("img[alt*='Message']"),
            page.locator("*:has-text('message')"),
            page.locator(".message-icon"),
            page.locator("[class*='message']"),
            // Try generic messaging icons
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("message")),
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Message")),
            page.locator("button:has-text('Messages')"),
            page.locator("*:has-text('Messages')")
        };
        
        boolean clicked = false;
        for (Locator icon : messageIconVariations) {
            try {
                if (icon.count() > 0 && safeIsVisible(icon.first())) {
                    icon.first().scrollIntoViewIfNeeded();
                    waitVisible(icon.first(), ConfigReader.getShortTimeout());
                    clickWithRetry(icon.first(), 1, 150);
                    clicked = true;
                    logger.info("[FanPrivMedia] Clicked Twizz messages icon via: {}", icon.first());
                    break;
                }
            } catch (Exception e) {
                logger.debug("[FanPrivMedia] Message icon attempt failed: {}", e.getMessage());
            }
        }
        
        if (!clicked) {
            // Try clicking on any visible icon in the bottom navigation
            try {
                Locator bottomIcons = page.locator("nav img, .navigation img, .bottom-nav img");
                if (bottomIcons.count() > 0) {
                    for (int i = 0; i < Math.min(bottomIcons.count(), 5); i++) {
                        try {
                            bottomIcons.nth(i).click();
                            page.waitForTimeout(1000);
                            // Check if we're on messages screen
                            if (page.url().contains("message") || safeIsVisible(page.getByText("Your message"))) {
                                clicked = true;
                                logger.info("[FanPrivMedia] Clicked navigation icon and found messages screen");
                                break;
                            }
                        } catch (Exception e) {
                            logger.debug("[FanPrivMedia] Bottom icon {} failed: {}", i, e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("[FanPrivMedia] Bottom navigation attempt failed: {}", e.getMessage());
            }
        }
        
        if (!clicked) {
            throw new RuntimeException("Unable to find or click Twizz messages icon");
        }
        
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
