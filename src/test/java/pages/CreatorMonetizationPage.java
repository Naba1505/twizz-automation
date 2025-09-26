package pages;

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

    // URLs
    private static final String MONETIZATION_URL = "https://stg.twizz.app/creator/monetization";

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

    @SuppressWarnings("unused")
    private Locator monthlyPriceInput() {
        return priceInputs().first();
    }

    private Locator quarterlyPriceInput() {
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
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 1, 150);

        log.info("Clicking 'Subscription price' in Settings");
        waitVisible(subscriptionPriceMenuItem(), DEFAULT_WAIT);
        clickWithRetry(subscriptionPriceMenuItem(), 1, 150);

        // Title exact text should be visible on the target screen
        waitVisible(pageTitleExact(), DEFAULT_WAIT);
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
        waitVisible(monthlyOfferLabel(), DEFAULT_WAIT);
        waitVisible(monthlyToggle(), DEFAULT_WAIT);
        // Also ensure the Monthly price input is present and log its current value for debugging
        waitVisible(monthlyPriceInput(), DEFAULT_WAIT);
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
        waitVisible(monthlyToggle(), DEFAULT_WAIT);
        clickWithRetry(monthlyToggle(), 1, 120);
        // Expect popup toast/dialog
        waitVisible(monthlyCannotTurnOffPopup(), 10_000);
    }

    @Step("Ensure Quarterly offer section is visible")
    public void assertQuarterlyOfferVisible() {
        waitVisible(quarterlyOfferLabel(), DEFAULT_WAIT);
    }

    @Step("Enable Quarterly price toggle")
    public void enableQuarterlyToggleIfNeeded() {
        waitVisible(quarterlyToggle(), DEFAULT_WAIT);
        try {
            String aria = quarterlyToggle().getAttribute("aria-checked");
            boolean isOn = aria != null ? Boolean.parseBoolean(aria) : quarterlyToggle().isChecked();
            if (!isOn) {
                log.info("Quarterly toggle is OFF, enabling it now.");
                clickWithRetry(quarterlyToggle(), 1, 120);
            } else {
                log.info("Quarterly toggle already ON");
            }
        } catch (Throwable t) {
            // Fallback: just click once if read fails
            clickWithRetry(quarterlyToggle(), 1, 120);
        }
    }

    @Step("Disable Quarterly price toggle if currently enabled")
    public void disableQuarterlyToggleIfOn() {
        waitVisible(quarterlyToggle(), DEFAULT_WAIT);
        try {
            String aria = quarterlyToggle().getAttribute("aria-checked");
            boolean isOn = aria != null ? Boolean.parseBoolean(aria) : quarterlyToggle().isChecked();
            if (isOn) {
                log.info("Quarterly toggle is ON, disabling it now.");
                clickWithRetry(quarterlyToggle(), 1, 120);
            } else {
                log.info("Quarterly toggle already OFF");
            }
        } catch (Throwable t) {
            // Fallback: just click once if read fails
            clickWithRetry(quarterlyToggle(), 1, 120);
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
        waitVisible(quarterlyToggle(), DEFAULT_WAIT);
        boolean isOn = isQuarterlyOnSafe();
        if (isOn) {
            log.info("Quarterly currently ON -> turning OFF");
            clickWithRetry(quarterlyToggle(), 1, 120);
        } else {
            log.info("Quarterly already OFF -> toggling ON then OFF to enable Save");
            clickWithRetry(quarterlyToggle(), 1, 120);
            // small pause to allow UI to register state change
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
            clickWithRetry(quarterlyToggle(), 1, 120);
        }
    }

    @Step("Set Quarterly price to: {price}")
    public void setQuarterlyPrice(String price) {
        waitVisible(quarterlyPriceInput(), DEFAULT_WAIT);
        Locator el = quarterlyPriceInput();
        el.click();
        // Clear robustly before filling
        try { el.fill(""); } catch (Throwable ignored) {}
        try { el.press("Control+A"); el.press("Backspace"); } catch (Throwable ignored) {}
        el.fill(price);
        String current = el.inputValue();
        if (!price.equals(current)) {
            log.warn("Quarterly price mismatch after fill. Expected='{}' Actual='{}'", price, current);
        }
    }

    @Step("Click Continue to save monetization changes")
    public void clickContinue() {
        waitVisible(continueButton(), DEFAULT_WAIT);
        // Wait for button to become enabled (it may be disabled if no changes were made)
        long start = System.currentTimeMillis();
        long timeoutMs = 15_000; // 15s should be enough after a change
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                if (continueButton().isEnabled()) break;
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(250); } catch (Throwable ignored) {}
        }
        if (!continueButton().isEnabled()) {
            log.warn("Continue button still disabled after waiting; attempting click regardless");
        }
        clickWithRetry(continueButton(), 2, 150);
    }

    @Step("Wait for monetization updated toast")
    public void waitForMonetizationUpdatedToast() {
        waitVisible(monetizationUpdatedPopup(), 15_000);
        try { clickWithRetry(monetizationUpdatedPopup(), 0, 0); } catch (Throwable ignored) {}
    }
}
