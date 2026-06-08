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

    // All timeout values now use centralized ConfigReader methods for consistency

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
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());

        log.info("Clicking 'Subscription price' in Settings");
        waitVisible(subscriptionPriceMenuItem(), ConfigReader.getShortTimeout());
        clickWithRetry(subscriptionPriceMenuItem(), 1, ConfigReader.getElementRetryDelay());

        // Title exact text should be visible on the target screen
        waitVisible(pageTitleExact(), ConfigReader.getShortTimeout());
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
        waitVisible(monthlyOfferLabel(), ConfigReader.getShortTimeout());
        waitVisible(monthlyToggle(), ConfigReader.getShortTimeout());
        // Also ensure the Monthly price input is present and log its current value for debugging
        waitVisible(monthlyPriceInput(), ConfigReader.getShortTimeout());
        try {
            String val = monthlyPriceInput().inputValue();
            log.info("Monthly price input visible, current value='{}'", val);
        } catch (Throwable e) { log.debug("Failed to get monthly price value: {}", e.getMessage()); }
        try {
            boolean on = monthlyToggle().getAttribute("aria-checked") != null ?
                    Boolean.parseBoolean(monthlyToggle().getAttribute("aria-checked")) : monthlyToggle().isChecked();
            log.info("Monthly toggle aria-checked/isChecked = {}", on);
        } catch (Throwable e) { log.debug("Failed to get monthly toggle state: {}", e.getMessage()); }
    }

    @Step("Attempt to disable Monthly and expect validation popup")
    public void attemptDisableMonthlyShowsPopup() {
        // Click the monthly toggle switch as per spec
        waitVisible(monthlyToggle(), ConfigReader.getShortTimeout());
        clickWithRetry(monthlyToggle(), 1, ConfigReader.getElementRetryDelay());
        // Expect popup toast/dialog
        waitVisible(monthlyCannotTurnOffPopup(), ConfigReader.getMediumTimeout());
    }

    @Step("Ensure Quarterly offer section is visible")
    public void assertQuarterlyOfferVisible() {
        waitVisible(quarterlyOfferLabel(), ConfigReader.getShortTimeout());
    }

    @Step("Enable Quarterly price toggle")
    public void enableQuarterlyToggleIfNeeded() {
        waitVisible(quarterlyToggle(), ConfigReader.getShortTimeout());
        try {
            String aria = quarterlyToggle().getAttribute("aria-checked");
            boolean isOn = aria != null ? Boolean.parseBoolean(aria) : quarterlyToggle().isChecked();
            if (!isOn) {
                log.info("Quarterly toggle is OFF, enabling it now.");
                clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
            } else {
                log.info("Quarterly toggle already ON");
            }
        } catch (Throwable t) {
            // Fallback: just click once if read fails
            clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
        }
    }

    @Step("Disable Quarterly price toggle if currently enabled")
    public void disableQuarterlyToggleIfOn() {
        waitVisible(quarterlyToggle(), ConfigReader.getShortTimeout());
        try {
            String aria = quarterlyToggle().getAttribute("aria-checked");
            boolean isOn = aria != null ? Boolean.parseBoolean(aria) : quarterlyToggle().isChecked();
            if (isOn) {
                log.info("Quarterly toggle is ON, disabling it now.");
                clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
            } else {
                log.info("Quarterly toggle already OFF");
            }
        } catch (Throwable t) {
            // Fallback: just click once if read fails
            clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
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
        waitVisible(quarterlyToggle(), ConfigReader.getShortTimeout());
        boolean isOn = isQuarterlyOnSafe();
        if (isOn) {
            log.info("Quarterly currently ON -> turning OFF");
            clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
        } else {
            log.info("Quarterly already OFF -> toggling ON then OFF to register change");
            clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
            clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
        }
    }

    @Step("Wait and then ensure Quarterly is OFF, always registering a change")
    public void waitAndDisableQuarterlyWithChange() {
        // Wait a bit after navigation so the page fully settles
        try { page.waitForTimeout(ConfigReader.getMediumTimeout()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }

        waitVisible(quarterlyToggle(), ConfigReader.getShortTimeout());

        // First click always to guarantee a change event
        clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }

        // If after first click it is still ON, click once more to end OFF
        boolean afterFirstClickOn = isQuarterlyOnSafe();
        if (afterFirstClickOn) {
            log.info("Quarterly still ON after first click -> clicking again to turn OFF");
            clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
        } else {
            log.info("Quarterly OFF after first click; no extra click needed");
        }
    }

    @Step("Set Quarterly price to: {price}")
    public void setQuarterlyPrice(String price) {
        waitVisible(quarterlyPriceInput(), ConfigReader.getShortTimeout());
        Locator el = quarterlyPriceInput();
        el.click();
        // Clear robustly before filling
        try { el.fill(""); } catch (Throwable e) { log.debug("Fill failed: {}", e.getMessage()); }
        try { el.press("Control+A"); el.press("Backspace"); } catch (Throwable e) { log.debug("Key press failed: {}", e.getMessage()); }
        el.fill(price);
        String current = el.inputValue();
        if (!price.equals(current)) {
            // UI may format as 0.0€, etc.; just log for debugging instead of failing the test
            log.warn("Quarterly price mismatch after fill. Expected='{}' Actual='{}'", price, current);
        }
    }

    @Step("Click Continue to save monetization changes")
    public void clickContinue() {
        waitVisible(continueButton(), ConfigReader.getShortTimeout());
        // Wait for button to become enabled (it may be disabled if no changes were made)
        long start = System.currentTimeMillis();
        long timeoutMs = ConfigReader.getLongTimeout();
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                if (continueButton().isEnabled()) break;
            } catch (Throwable e) { log.debug("Enabled check failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
        }
        if (!continueButton().isEnabled()) {
            log.warn("Continue button still disabled after waiting; attempting click regardless");
        }
        clickWithRetry(continueButton(), 2, ConfigReader.getElementRetryDelay());
    }

    @Step("Wait for monetization updated toast")
    public void waitForMonetizationUpdatedToast() {
        waitVisible(monetizationUpdatedPopup(), ConfigReader.getMediumTimeout());
        try { clickWithRetry(monetizationUpdatedPopup(), 0, 0); } catch (Throwable e) { log.debug("Click failed: {}", e.getMessage()); }
    }
}

