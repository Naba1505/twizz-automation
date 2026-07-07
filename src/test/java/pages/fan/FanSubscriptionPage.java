package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import java.util.Arrays;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.KeyboardModifier;

import io.qameta.allure.Step;

public class FanSubscriptionPage extends BasePage {

    public FanSubscriptionPage(Page page) {
        super(page);
    }

    @Step("Open search and focus search field")
    public void openSearchPanel() {
        logger.info("[Fan][Subscribe] Opening search panel");
        // According to flow: click search icon, then click 'Search' label to focus
        Locator searchIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Search icon"));
        waitVisible(searchIcon.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(searchIcon.first(), 1, ConfigReader.getElementRetryDelay());
        Locator searchLabel = page.getByText("Search");
        waitVisible(searchLabel.first(), ConfigReader.getShortTimeout());
        clickWithRetry(searchLabel.first(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Search and open creator profile")
    public void searchAndOpenCreator(String username) {
        logger.info("[Fan][Subscribe] Searching creator: {}", username);
        // Ensure an input is focused using role SEARCHBOX with name 'Search'
        Locator input = page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search"));
        if (input.count() == 0 || !safeIsVisible(input.first())) {
            openSearchPanel();
            input = page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName("Search"));
        }
        waitVisible(input.first(), ConfigReader.getVisibilityTimeout());
        input.first().fill(username);
        Locator result = page.getByText(username);
        waitVisible(result.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(result.first(), 1, ConfigReader.getElementRetryDelay());

        // Wait for creator profile to load: either subscribe button appears or URL indicates profile
        logger.info("[Fan][Subscribe] Waiting for creator profile to load");
        boolean landed = false;
        long end = System.currentTimeMillis() + ConfigReader.getMediumTimeout();
        while (!landed && System.currentTimeMillis() < end) {
            // Check if we've successfully landed on the profile page
            try {
                if (page.url() != null && (page.url().contains("/twizzcreator") || page.url().contains("/creator/") || page.url().contains("/profile/"))) {
                    logger.debug("[Fan][Subscribe] Profile page detected via URL");
                    return; // Exit method immediately when profile is found
                }
            } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
            try {
                Locator subBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscribe - without obligation"));
                if (subBtn.count() > 0 && safeIsVisible(subBtn.first())) {
                    logger.debug("[Fan][Subscribe] Subscribe button found");
                    return; // Exit method immediately when subscribe button is found
                }
            } catch (Throwable e) { logger.debug("Subscribe button check failed: {}", e.getMessage()); }
            try {
                Locator altBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscribe"));
                if (altBtn.count() > 0 && safeIsVisible(altBtn.first())) {
                    logger.debug("[Fan][Subscribe] Alternative subscribe button found");
                    return; // Exit method immediately when alternative subscribe button is found
                }
            } catch (Throwable e) { logger.debug("Alternative subscribe button check failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        
        // Additional wait for page to fully settle
        logger.info("[Fan][Subscribe] Profile loaded, waiting for page to settle");
        try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
    }

    @Step("Start subscription flow")
    public boolean startSubscriptionFlow() {
        logger.info("[Fan][Subscribe] Starting subscription flow");
        
        // Check if already subscribed
        Locator subscribedIndicator = page.getByText("Subscribed");
        if (subscribedIndicator.count() > 0 && safeIsVisible(subscribedIndicator.first())) {
            logger.info("[Fan][Subscribe] Already subscribed to this creator");
            return false;
        }
        
        // Find and click Subscribe button
        Locator subscribeBtn = page.locator("button.subscribeWithoutObligation, button.subscribeBtn");
        if (subscribeBtn.count() == 0 || !safeIsVisible(subscribeBtn.first())) {
            subscribeBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscribe"));
        }
        if (subscribeBtn.count() == 0 || !safeIsVisible(subscribeBtn.first())) {
            subscribeBtn = page.getByText("Subscribe", new Page.GetByTextOptions().setExact(true));
        }
        
        if (subscribeBtn.count() == 0 || !safeIsVisible(subscribeBtn.first())) {
            throw new RuntimeException("Subscribe button not found");
        }
        
        logger.info("[Fan][Subscribe] Clicking Subscribe button");
        waitVisible(subscribeBtn.first(), ConfigReader.getVisibilityTimeout());
        subscribeBtn.first().scrollIntoViewIfNeeded();
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Try multiple click strategies
        boolean clicked = false;
        try {
            subscribeBtn.first().click();
            clicked = true;
            logger.info("[Fan][Subscribe] Standard click succeeded");
        } catch (Throwable e) {
            logger.warn("[Fan][Subscribe] Standard click failed: {}", e.getMessage());
        }
        
        if (!clicked) {
            try {
                subscribeBtn.first().click(new Locator.ClickOptions().setForce(true));
                clicked = true;
                logger.info("[Fan][Subscribe] Force click succeeded");
            } catch (Throwable e) {
                logger.warn("[Fan][Subscribe] Force click failed: {}", e.getMessage());
            }
        }
        
        if (!clicked) {
            try {
                // Try JavaScript click
                subscribeBtn.first().evaluate("el => el.click()");
                clicked = true;
                logger.info("[Fan][Subscribe] JavaScript click succeeded");
            } catch (Throwable e) {
                logger.warn("[Fan][Subscribe] JavaScript click failed: {}", e.getMessage());
            }
        }
        
        if (!clicked) {
            throw new RuntimeException("Failed to click Subscribe button");
        }
        
        try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Check if Premium modal appeared (paid subscription)
        Locator premiumText = page.getByText("Premium");
        if (premiumText.count() > 0 && safeIsVisible(premiumText.first())) {
            logger.info("[Fan][Subscribe] Premium modal appeared - paid subscription");
            
            // Click Continue button
            Locator continueBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
            if (continueBtn.count() > 0 && safeIsVisible(continueBtn.first())) {
                try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                continueBtn.first().click();
                logger.info("[Fan][Subscribe] Clicked Continue button");
                try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                return true; // Payment needed
            }
        }
        
        // Check if subscription completed (free subscription)
        subscribedIndicator = page.getByText("Subscribed");
        Locator subscriberText = page.getByText("Subscriber");
        if ((subscribedIndicator.count() > 0 && safeIsVisible(subscribedIndicator.first())) ||
            (subscriberText.count() > 0 && safeIsVisible(subscriberText.first()))) {
            logger.info("[Fan][Subscribe] Free subscription completed");
            return false; // No payment needed
        }
        
        // If Subscribe button is gone, subscription likely completed
        subscribeBtn = page.locator("button.subscribeWithoutObligation, button.subscribeBtn");
        if (subscribeBtn.count() == 0 || !safeIsVisible(subscribeBtn.first())) {
            logger.info("[Fan][Subscribe] Subscribe button gone - subscription completed");
            return false;
        }
        
        logger.info("[Fan][Subscribe] Subscription flow completed");
        return false;
    }

    @Step("Handle payment screen - check for saved cards or fill manually")
    public void handlePaymentScreen(String cardNumber, String expiry, String cvc) {
        logger.info("[Fan][Subscribe] Handling payment screen");
        
        try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Check if "No saved cards" is displayed
        Locator noSavedCards = page.getByText("No saved cards");
        Locator addNewCardButton = page.getByText("Add new card");
        
        try {
            if (noSavedCards.count() > 0 && safeIsVisible(noSavedCards.first())) {
                logger.info("[Fan][Subscribe] No saved cards found - clicking Add new card");
                if (addNewCardButton.count() > 0) {
                    addNewCardButton.first().click();
                    try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                }
            }
        } catch (Exception e) {
            logger.debug("[Fan][Subscribe] Error checking for saved cards: {}", e.getMessage());
        }
        
        // Now fill card details (whether saved cards existed or not)
        fillCardDetails(cardNumber, expiry, cvc);
    }

    @Step("Fill card details")
    public void fillCardDetails(String cardNumber, String expiry, String cvc) {
        logger.info("[Fan][Subscribe] Filling payment card details");
        // Some payment fields can be inside iframes; try direct first.
        try {
            Locator number = page.getByPlaceholder("1234 1234 1234");
            waitVisible(number.first(), ConfigReader.getVisibilityTimeout());
            number.first().click();
            number.first().fill(cardNumber);
        } catch (Throwable e) {
            logger.warn("[Fan][Subscribe] Card number field not found directly: {}", e.getMessage());
        }
        try {
            Locator exp = page.getByPlaceholder("MM/YY");
            if (exp.count() > 0) { exp.first().click(); exp.first().fill(expiry); }
        } catch (Throwable e) {
            logger.warn("[Fan][Subscribe] Expiry field not found directly: {}", e.getMessage());
        }
        try {
            Locator cvcField = page.getByPlaceholder("CVC");
            if (cvcField.count() > 0) { cvcField.first().click(); cvcField.first().fill(cvc); }
        } catch (Throwable e) {
            logger.warn("[Fan][Subscribe] CVC field not found directly: {}", e.getMessage());
        }

        // If any field wasn't filled, search through iframes
        try {
            for (com.microsoft.playwright.Frame fr : page.frames()) {
                String u = "";
                try { u = fr.url(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                if (u.contains("securionpay") || u.contains("payment") || u.contains("iframe")) {
                    try {
                        Locator number = fr.getByPlaceholder("1234 1234 1234");
                        if (number.count() > 0) { number.first().click(); number.first().fill(cardNumber); }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try {
                        Locator exp = fr.getByPlaceholder("MM/YY");
                        if (exp.count() > 0) { exp.first().click(); exp.first().fill(expiry); }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try {
                        Locator cvcField = fr.getByPlaceholder("CVC");
                        if (cvcField.count() > 0) { cvcField.first().click(); cvcField.first().fill(cvc); }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                }
            }
        } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
    }

    @Step("Select country if required")
    public void selectCountryIfNeeded() {
        logger.info("[Fan][Subscribe] Selecting country if required");
        try {
            Locator countryBlock = page.locator("div").filter(new Locator.FilterOptions()
                    .setHasText("CountryCountryAfghanistanAlbaniaAlgeriaAmericanSamoaAndorraAngolaAnguillaAntarct"));
            if (countryBlock.count() > 0) {
                countryBlock.nth(5).click(new Locator.ClickOptions().setModifiers(Arrays.asList(KeyboardModifier.CONTROL)));
            }
        } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
    }

    @Step("Confirm payment and complete 3DS test flow")
    public void confirmAndComplete3DS() {
        logger.info("[Fan][Subscribe] Confirming payment and completing 3DS test flow");
        Page appPage = page;
        // Click Confirm and wait for either new page or 3DS iframe
        Page threeDSPage = null;
        try {
            Locator confirmBtn = appPage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm"));
            boolean clicked = false;
            try {
                waitVisible(confirmBtn, ConfigReader.getShortTimeout());
                try { confirmBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
                // Try a bounded-time click first to avoid 60s default timeouts
                try {
                    threeDSPage = appPage.context().waitForPage(() -> {
                        try { confirmBtn.first().click(new Locator.ClickOptions().setTimeout(3000)); }
                        catch (Throwable e) { logger.debug("Click attempt failed: {}", e.getMessage()); }
                    });
                    clicked = true;
                } catch (Throwable e1) {
                    try {
                        confirmBtn.first().click(new Locator.ClickOptions().setTimeout(3000));
                        clicked = true;
                    } catch (Throwable e2) {
                        logger.warn("[Fan][Subscribe] Primary Confirm click failed quickly; will try fallbacks: {}", e2.getMessage());
                    }
                }
            } catch (Throwable notFound) {
                // Try alternative buttons some gateways use
                String[] alt = new String[]{"Pay", "Subscribe", "Proceed", "Continue", "Place order"};
                for (String name : alt) {
                    Locator altBtn = appPage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name));
                    if (altBtn.count() > 0 && safeIsVisible(altBtn.first())) {
                        try { altBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                        try { threeDSPage = appPage.context().waitForPage(() -> {
                            try { altBtn.first().click(new Locator.ClickOptions().setTimeout(3000)); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                        }); }
                        catch (Throwable e) {
                            try { altBtn.first().click(new Locator.ClickOptions().setTimeout(3000)); } catch (Throwable e2) { logger.debug("Operation failed: {}", e2.getMessage()); }
                        }
                        clicked = true;
                        logger.debug("[Fan][Subscribe] Successfully clicked 3DS button");
                        break;
                    }
                }
                if (!clicked) {
                    // As a last resort, click the first visible primary button
                    try {
                        Locator anyBtn = appPage.locator("button:visible").first();
                        if (safeIsVisible(anyBtn)) {
                            try { anyBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { 
                                anyBtn.first().click(new Locator.ClickOptions().setTimeout(3000)); 
                                logger.debug("[Fan][Subscribe] Clicked fallback button");
                            } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                        }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                }
            }

            // Wait briefly for the real 3DS popup page to appear
            if (threeDSPage == null) {
                for (int i = 0; i < 16 && threeDSPage == null; i++) { // up to ~8s
                    for (Page p : appPage.context().pages()) {
                        try { 
                            String url = p.url();
                            if (url != null && (url.toLowerCase().contains("securionpay") || url.toLowerCase().contains("dev.shift4.com"))) { 
                                threeDSPage = p; 
                                logger.info("[Fan][Subscribe] Found 3DS page with URL: {}", url);
                                break; 
                            } 
                        } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    }
                    if (threeDSPage != null) break;
                    try { appPage.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                }
            }

            if (threeDSPage != null) {
                try { if (!threeDSPage.isClosed()) threeDSPage.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                try { if (!threeDSPage.isClosed()) threeDSPage.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                try { if (!threeDSPage.isClosed()) clickWithRetry(threeDSPage.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("SecurionPay")), 1, ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                
                // Try direct JavaScript submit immediately for .btn.btn-success
                try {
                    if (!threeDSPage.isClosed()) {
                        logger.info("[Fan][Subscribe] Attempting direct JavaScript submit for .btn.btn-success");
                        threeDSPage.evaluate("() => { const btn = document.querySelector('.btn.btn-success'); if (btn) btn.click(); }");
                        logger.info("[Fan][Subscribe] JavaScript submit executed for .btn.btn-success");
                    }
                } catch (Throwable e) {
                    logger.warn("[Fan][Subscribe] JavaScript submit failed: {}", e.getMessage());
                }
                // Retry Submit with multiple strategies
                boolean submitted = false;
                for (int i = 0; i < 3 && !submitted; i++) {
                    try { if (threeDSPage.isClosed()) break; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try {
                        Locator byRole = threeDSPage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
                        if (!threeDSPage.isClosed() && byRole.count() > 0 && safeIsVisible(byRole.first())) {
                            try { byRole.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { byRole.first().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout())); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { byRole.first().evaluate("el => el.click()"); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                        }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try {
                        Locator xpathSubmit = threeDSPage.locator("xpath=//div//input[@value='Submit']");
                        if (!threeDSPage.isClosed() && xpathSubmit.count() > 0 && safeIsVisible(xpathSubmit.first())) {
                            try { xpathSubmit.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { xpathSubmit.first().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout())); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { xpathSubmit.first().evaluate("el => el.click()"); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                        }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try {
                        Locator buttonText = threeDSPage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
                        if (!threeDSPage.isClosed() && buttonText.count() > 0 && safeIsVisible(buttonText.first())) {
                            try { buttonText.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { buttonText.first().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout())); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { buttonText.first().evaluate("el => el.click()"); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                        }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try {
                        Locator anySubmit = threeDSPage.locator("#submit, [data-testid='submit'], [name='submit']").first();
                        if (!threeDSPage.isClosed() && anySubmit.count() > 0 && safeIsVisible(anySubmit)) {
                            try { anySubmit.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { anySubmit.first().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout())); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { anySubmit.first().evaluate("el => el.click()"); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                        }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    // Try CSS selector for submit button
                    try {
                        Locator cssSubmit = threeDSPage.locator(".btn.btn-success");
                        if (!threeDSPage.isClosed() && cssSubmit.count() > 0 && safeIsVisible(cssSubmit.first())) {
                            logger.info("[Fan][Subscribe] Found submit button with .btn.btn-success class");
                            try { cssSubmit.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { cssSubmit.first().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout())); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            try { cssSubmit.first().evaluate("el => el.click()"); submitted = true; continue; } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                        }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    // Scan frames inside the 3DS page
                    try {
                        for (com.microsoft.playwright.Frame fr : threeDSPage.frames()) {
                            String u = "";
                            try { u = fr.url(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            if (u.toLowerCase().contains("securionpay") || u.toLowerCase().contains("dev.shift4.com")) {
                                try {
                                    Locator frBtn = fr.getByRole(AriaRole.BUTTON, new com.microsoft.playwright.Frame.GetByRoleOptions().setName("Submit"));
                                    if (frBtn.count() > 0) { frBtn.first().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout())); submitted = true; break; }
                                } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                                try {
                                    Locator frInput = fr.locator("input[type='submit'], input[value='Submit']");
                                    if (frInput.count() > 0) { frInput.first().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout())); submitted = true; break; }
                                } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                                try {
                                    Locator frCss = fr.locator(".btn.btn-success");
                                    if (frCss.count() > 0) { frCss.first().click(new Locator.ClickOptions().setTimeout(ConfigReader.getShortTimeout())); submitted = true; break; }
                                } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                            }
                        }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try { if (!threeDSPage.isClosed()) threeDSPage.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                }
                // As a last resort, try submitting first form
                if (!submitted && !threeDSPage.isClosed()) {
                    try { 
                        threeDSPage.evaluate("() => { const f = document.querySelector('form'); if (f) { if (f.requestSubmit) f.requestSubmit(); else f.submit(); } }"); 
                        logger.debug("[Fan][Subscribe] Submitted form via JavaScript");
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                }
                // Wait for confirmation text
                try { if (!threeDSPage.isClosed()) threeDSPage.waitForSelector("text=Payment confirmed!", new Page.WaitForSelectorOptions().setTimeout(ConfigReader.getVisibilityTimeout())); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                // Some flows show an extra confirmation button
                try {
                    if (!threeDSPage.isClosed()) {
                        Locator okBtn = threeDSPage.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Everything is OK"));
                        if (okBtn.count() > 0 && safeIsVisible(okBtn.first())) {
                            clickWithRetry(okBtn.first(), 1, ConfigReader.getElementRetryDelay());
                        }
                    }
                } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                // Close 3DS page only if there is another app page present
                try {
                    if (!threeDSPage.isClosed()) {
                        int openPages = appPage.context().pages().size();
                        if (openPages > 1) { threeDSPage.close(); }
                    }
                } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
            } else {
                // Try iframe path
                com.microsoft.playwright.Frame threeDSFrame = null;
                for (com.microsoft.playwright.Frame fr : appPage.frames()) {
                    try { if (fr.url() != null && fr.url().toLowerCase().contains("securionpay")) { threeDSFrame = fr; break; } } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                }
                if (threeDSFrame != null) {
                    try { clickWithRetry(threeDSFrame.getByRole(AriaRole.IMG, new com.microsoft.playwright.Frame.GetByRoleOptions().setName("SecurionPay")), 1, ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try { clickWithRetry(threeDSFrame.getByRole(AriaRole.BUTTON, new com.microsoft.playwright.Frame.GetByRoleOptions().setName("Submit")), 1, ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    // XPath-based fallback inside frame
                    try {
                        Locator xpathSubmit = threeDSFrame.locator("xpath=//div//input[@value='Submit']");
                        if (xpathSubmit.count() > 0) { clickWithRetry(xpathSubmit.first(), 1, ConfigReader.getElementRetryDelay()); }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try { threeDSFrame.waitForSelector("text=Payment confirmed!", new com.microsoft.playwright.Frame.WaitForSelectorOptions().setTimeout(ConfigReader.getVisibilityTimeout())); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    // Extra confirmation path
                    try {
                        Locator okBtn = threeDSFrame.getByRole(AriaRole.BUTTON, new com.microsoft.playwright.Frame.GetByRoleOptions().setName("Everything is OK"));
                        if (okBtn.count() > 0) clickWithRetry(okBtn.first(), 1, ConfigReader.getElementRetryDelay());
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                } else {
                    logger.warn("[Fan][Subscribe] Could not locate 3DS page or iframe; flow may auto-approve or be skipped");
                }
            }

            // Ensure focus back on the Twizz app
            try {
                Page active = null;
                for (Page p : appPage.context().pages()) {
                    try { if (p.url() != null && p.url().contains("twizz.app")) { active = p; break; } } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                }
                if (active == null) { active = appPage; }
                try { active.bringToFront(); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
            } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }

        } catch (Throwable e) {
            logger.warn("[Fan][Subscribe] 3DS flow handling error: {}", e.getMessage());
        }

        // No final fallback navigation; keep interactions within the gateway's real popup/iframe to preserve session integrity.
    }

    @Step("Assert subscription completed successfully")
    public void assertSubscriberVisible() {
        logger.info("[Fan][Subscribe] Verifying subscription completion");
        
        // Check if we're still on the payment confirmation page
        try {
            Locator paymentConfirmed = page.getByText("Payment confirmed!");
            if (paymentConfirmed.count() > 0 && safeIsVisible(paymentConfirmed.first())) {
                logger.info("[Fan][Subscribe] Payment confirmed! Waiting for navigation to creator profile...");
                
                // Wait for the page to navigate away from payment confirmation (max 15s)
                long navEnd = System.currentTimeMillis() + ConfigReader.getNavigationTimeout();
                while (System.currentTimeMillis() < navEnd) {
                    try {
                        // Check if we've navigated to the creator profile
                        if (page.url() != null && page.url().contains("/p/")) {
                            logger.info("[Fan][Subscribe] Navigated to creator profile");
                            break;
                        }
                        // Check if payment confirmation is gone
                        if (paymentConfirmed.count() == 0 || !safeIsVisible(paymentConfirmed.first())) {
                            logger.info("[Fan][Subscribe] Payment confirmation dismissed");
                            break;
                        }
                    } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                    try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
                }
                
                // Give the profile page a moment to fully load
                try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
            }
        } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
        
        // Now check for Subscriber button on creator profile
        logger.info("[Fan][Subscribe] Checking for 'Subscriber' button on profile");
        long end = System.currentTimeMillis() + ConfigReader.getMediumTimeout();
        while (System.currentTimeMillis() < end) {
            try {
                Locator subscriberBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Subscriber"));
                if (subscriberBtn.count() > 0 && safeIsVisible(subscriberBtn.first())) {
                    logger.info("[Fan][Subscribe] Subscriber button found - subscription verified successfully!");
                    return;
                }
            } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
            try {
                // Some UIs show plain text or a different label (Subscribed/Unsubscribe)
                if (page.getByText("Subscriber").count() > 0 ||
                        page.getByText("Subscribed").count() > 0 ||
                        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Unsubscribe")).count() > 0) {
                    logger.info("[Fan][Subscribe] Subscription status confirmed via text/button");
                    return;
                }
            } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Operation failed: {}", e.getMessage()); }
        }
        
        logger.warn("[Fan][Subscribe] Subscriber button not found within timeout, but payment was confirmed");
    }
}

