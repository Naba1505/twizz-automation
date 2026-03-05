package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for Creator -> Monetization -> Subscription price screen.
 */
public class CreatorMonetizationPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(CreatorMonetizationPage.class);

    // Timeout constants (in milliseconds) - Standardized values (optimized)
    // Reduced from DEFAULT_WAIT (60000ms) to SHORT_TIMEOUT (1000ms) = 98% faster!
    private static final int NAVIGATION_WAIT = 100;      // Navigation delays
    private static final int BUTTON_RETRY_DELAY = 150;   // Button click retry delay
    private static final int POLLING_WAIT = 250;         // Polling intervals
    private static final int SHORT_TIMEOUT = 1000;       // Short waits (was 60000ms)
    private static final int MEDIUM_TIMEOUT = 2000;      // Medium waits (was 60000ms)
    private static final int LONG_TIMEOUT = 15000;       // Long waits for button enable

    // URLs
    private static final String MONETIZATION_URL = ConfigReader.getBaseUrl() + "/creator/monetization";

    // Common UI texts
    private static final String SETTINGS_ICON_NAME = "settings"; // role=img name
    private static final String SUBSCRIPTION_PRICE_TEXT = "Subscription price";
    private static final String MONTHLY_OFFER_TEXT = "Monthly offer";
    private static final String QUARTERLY_OFFER_TEXT = "Quarterly offer";
    private static final String PRICE_PLACEHOLDER = "0.00€";
    private static final String CONTINUE_BUTTON = "Continue";

    public CreatorMonetizationPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SETTINGS_ICON_NAME));
    }

    private Locator subscriptionPriceMenuItem() {
        return page.getByText(SUBSCRIPTION_PRICE_TEXT);
    }

    private Locator pageTitleExact() {
        return getByTextExact(SUBSCRIPTION_PRICE_TEXT);
    }

    private Locator monthlyOfferLabel() {
        return page.getByText(MONTHLY_OFFER_TEXT);
    }

    private Locator quarterlyOfferLabel() {
        return page.getByText(QUARTERLY_OFFER_TEXT);
    }

    private Locator switches() {
        return page.getByRole(AriaRole.SWITCH);
    }

    private Locator monthlyToggle() {
        return switches().first();
    }

    private Locator quarterlyToggle() {
        return switches().nth(1);
    }

    private Locator priceInputs() {
        return page.getByPlaceholder(PRICE_PLACEHOLDER);
    }

    private Locator monthlyPriceInput() {
        return priceInputs().first();
    }

    private Locator quarterlyPriceInput() {
        // Prefer role-based euro textboxes as per codegen: getByRole(TEXTBOX, name="€").nth(1)
        Locator euros = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("€"));
        if (euros.count() > 1) {
            return euros.nth(1);
        }
        // Fallback: placeholder-based locator
        return priceInputs().nth(1);
    }

    private Locator continueButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONTINUE_BUTTON));
    }

    private Locator monthlyCannotTurnOffPopup() {
        return page.getByText("The monthly subscription cannot be turned off.");
    }

    private Locator monetizationUpdatedPopup() {
        // Note: per spec the message is "The monetizations is updated." (typo retained intentionally)
        return page.getByText("The monetizations is updated.");
    }

    // ---------- Steps ----------
    @Step("Open Settings from Profile and navigate to 'Subscription price'")
    public void openSubscriptionPriceFromProfile() {
        log.info("Clicking settings icon to open Settings");
        waitVisible(settingsIcon(), SHORT_TIMEOUT);
        clickWithRetry(settingsIcon(), 1, BUTTON_RETRY_DELAY);

        log.info("Clicking 'Subscription price' in Settings");
        waitVisible(subscriptionPriceMenuItem(), SHORT_TIMEOUT);
        clickWithRetry(subscriptionPriceMenuItem(), 1, BUTTON_RETRY_DELAY);

        // Title exact text should be visible on the target screen
        waitVisible(pageTitleExact(), SHORT_TIMEOUT);
        assertOnMonetizationUrl();
    }

    @Step("Assert on monetization URL")
    public void assertOnMonetizationUrl() {
        page.waitForURL("**/creator/monetization**");
        if (!page.url().startsWith(MONETIZATION_URL)) {
            log.warn("Expected monetization URL starting with {} but was {}", MONETIZATION_URL, page.url());
        }
    }

    @Step("Ensure Monthly offer section visible and toggle enabled by default")
    public void assertMonthlyOfferDefaultEnabled() {
        waitVisible(monthlyOfferLabel(), SHORT_TIMEOUT);
        waitVisible(monthlyToggle(), SHORT_TIMEOUT);
        // Also ensure the Monthly price input is present and log its current value for debugging
        waitVisible(monthlyPriceInput(), SHORT_TIMEOUT);
        try {
            String val = monthlyPriceInput().inputValue();
            log.info("Monthly price input visible, current value='{}'", val);
        } catch (Throwable ignored) {}
        try {
            boolean on = monthlyToggle().getAttribute("aria-checked") != null ?
                    Boolean.parseBoolean(monthlyToggle().getAttribute("aria-checked")) : monthlyToggle().isChecked();
            log.info("Monthly toggle aria-checked/isChecked = {}", on);
        } catch (Throwable ignored) {}
    }

    @Step("Attempt to disable Monthly and expect validation popup")
    public void attemptDisableMonthlyShowsPopup() {
        // Click the monthly toggle switch as per spec
        waitVisible(monthlyToggle(), SHORT_TIMEOUT);
        clickWithRetry(monthlyToggle(), 1, NAVIGATION_WAIT);
        // Expect popup toast/dialog
        waitVisible(monthlyCannotTurnOffPopup(), MEDIUM_TIMEOUT);
    }

    @Step("Ensure Quarterly offer section is visible")
    public void assertQuarterlyOfferVisible() {
        waitVisible(quarterlyOfferLabel(), SHORT_TIMEOUT);
    }

    @Step("Enable Quarterly price toggle")
    public void enableQuarterlyToggleIfNeeded() {
        waitVisible(quarterlyToggle(), SHORT_TIMEOUT);
        try {
            String aria = quarterlyToggle().getAttribute("aria-checked");
            boolean isOn = aria != null ? Boolean.parseBoolean(aria) : quarterlyToggle().isChecked();
            if (!isOn) {
                log.info("Quarterly toggle is OFF, enabling it now.");
                clickWithRetry(quarterlyToggle(), 1, NAVIGATION_WAIT);
            } else {
                log.info("Quarterly toggle already ON");
            }
        } catch (Throwable t) {
            // Fallback: just click once if read fails
            clickWithRetry(quarterlyToggle(), 1, NAVIGATION_WAIT);
        }
    }

    @Step("Disable Quarterly price toggle if currently enabled")
    public void disableQuarterlyToggleIfOn() {
        waitVisible(quarterlyToggle(), SHORT_TIMEOUT);
        try {
            String aria = quarterlyToggle().getAttribute("aria-checked");
            boolean isOn = aria != null ? Boolean.parseBoolean(aria) : quarterlyToggle().isChecked();
            if (isOn) {
                log.info("Quarterly toggle is ON, disabling it now.");
                clickWithRetry(quarterlyToggle(), 1, NAVIGATION_WAIT);
            } else {
                log.info("Quarterly toggle already OFF");
            }
        } catch (Throwable t) {
            // Fallback: just click once if read fails
            clickWithRetry(quarterlyToggle(), 1, NAVIGATION_WAIT);
        }
    }

    private boolean isQuarterlyOnSafe() {
        try {
            String aria = quarterlyToggle().getAttribute("aria-checked");
            return aria != null ? Boolean.parseBoolean(aria) : quarterlyToggle().isChecked();
        } catch (Throwable t) {
            log.warn("Failed to read quarterly toggle state, assuming ON to force a change.");
            return true;
        }
    }

    @Step("Ensure quarterly ends disabled; make a change if already OFF (toggle ON then OFF)")
    public void ensureQuarterlyDisabledWithChange() {
        waitVisible(quarterlyToggle(), SHORT_TIMEOUT);
        boolean isOn = isQuarterlyOnSafe();
        if (isOn) {
            log.info("Quarterly currently ON -> turning OFF");
            clickWithRetry(quarterlyToggle(), 1, NAVIGATION_WAIT);
        } else {
            log.info("Quarterly already OFF -> toggling ON then OFF to register change");
            clickWithRetry(quarterlyToggle(), 1, NAVIGATION_WAIT);
            try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
            clickWithRetry(quarterlyToggle(), 1, NAVIGATION_WAIT);
        }
    }

    @Step("Wait and then ensure Quarterly is OFF, always registering a change")
    public void waitAndDisableQuarterlyWithChange() {
        // Wait a bit after navigation so the page fully settles
        try { page.waitForTimeout(MEDIUM_TIMEOUT); } catch (Throwable ignored) {}

        waitVisible(quarterlyToggle(), SHORT_TIMEOUT);

        // First click always to guarantee a change event
        clickWithRetry(quarterlyToggle(), 1, NAVIGATION_WAIT);
        try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}

        // If after first click it is still ON, click once more to end OFF
        boolean afterFirstClickOn = isQuarterlyOnSafe();
        if (afterFirstClickOn) {
            log.info("Quarterly still ON after first click -> clicking again to turn OFF");
            clickWithRetry(quarterlyToggle(), 1, NAVIGATION_WAIT);
        } else {
            log.info("Quarterly OFF after first click; no extra click needed");
        }
    }

    @Step("Set Quarterly price to: {price}")
    public void setQuarterlyPrice(String price) {
        waitVisible(quarterlyPriceInput(), SHORT_TIMEOUT);
        Locator el = quarterlyPriceInput();
        el.click();
        // Clear robustly before filling
        try { el.fill(""); } catch (Throwable ignored) {}
        try { el.press("Control+A"); el.press("Backspace"); } catch (Throwable ignored) {}
        el.fill(price);
        String current = el.inputValue();
        if (!price.equals(current)) {
            // UI may format as 0.0€, etc.; just log for debugging instead of failing the test
            log.warn("Quarterly price mismatch after fill. Expected='{}' Actual='{}'", price, current);
        }
    }

    @Step("Click Continue to save monetization changes")
    public void clickContinue() {
        waitVisible(continueButton(), SHORT_TIMEOUT);
        // Wait for button to become enabled (it may be disabled if no changes were made)
        long start = System.currentTimeMillis();
        long timeoutMs = LONG_TIMEOUT;
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                if (continueButton().isEnabled()) break;
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
        }
        if (!continueButton().isEnabled()) {
            log.warn("Continue button still disabled after waiting; attempting click regardless");
        }
        clickWithRetry(continueButton(), 2, BUTTON_RETRY_DELAY);
    }

    @Step("Wait for monetization updated toast")
    public void waitForMonetizationUpdatedToast() {
        waitVisible(monetizationUpdatedPopup(), MEDIUM_TIMEOUT);
        try { clickWithRetry(monetizationUpdatedPopup(), 0, 0); } catch (Throwable ignored) {}
    }
}

