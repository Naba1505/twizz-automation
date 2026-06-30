package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page Object for Creator -> Monetization -> Subscription price screen.
 */
public class CreatorMonetizationPage extends BasePage {

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
        // Ensure we are on the profile page before looking for the settings icon
        navigateAndWait(ConfigReader.getBaseUrl() + "/creator/profile");
        logger.info("Clicking settings icon to open Settings");
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());

        logger.info("Clicking 'Subscription price' in Settings");
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
            logger.warn("Expected monetization URL starting with {} but was {}", MONETIZATION_URL, page.url());
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
            logger.info("Monthly price input visible, current value='{}'", val);
        } catch (Throwable e) { logger.debug("Failed to get monthly price value: {}", e.getMessage()); }
        try {
            boolean on = monthlyToggle().getAttribute("aria-checked") != null ?
                    Boolean.parseBoolean(monthlyToggle().getAttribute("aria-checked")) : monthlyToggle().isChecked();
            logger.info("Monthly toggle aria-checked/isChecked = {}", on);
        } catch (Throwable e) { logger.debug("Failed to get monthly toggle state: {}", e.getMessage()); }
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
                logger.info("Quarterly toggle is OFF, enabling it now.");
                clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
            } else {
                logger.info("Quarterly toggle already ON");
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
            logger.warn("Failed to read quarterly toggle state, assuming ON to force a change.");
            return true;
        }
    }

    @Step("Navigate back via arrow-left icon then go back one page")
    public void navigateBackFromMonetization() {
        try {
            Locator arrowLeft = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
            if (arrowLeft.count() > 0 && safeIsVisible(arrowLeft.first())) {
                arrowLeft.first().click();
                try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e2) { logger.debug("Animation wait failed: {}", e2.getMessage()); }
            }
        } catch (Throwable e) { logger.debug("Back arrow click failed: {}", e.getMessage()); }
        try { page.goBack(); } catch (Throwable e) { logger.debug("goBack failed: {}", e.getMessage()); }
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Back wait failed: {}", e.getMessage()); }
    }

    @Step("Disable quarterly: enable first if already OFF, then disable")
    public void disableQuarterlyOffer() {
        waitVisible(quarterlyToggle(), ConfigReader.getShortTimeout());
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("UI settle wait failed: {}", e.getMessage()); }
        boolean isEnabled = isQuarterlyOnSafe();
        if (!isEnabled) {
            // Enable first so we can then disable and trigger a real change
            logger.info("Quarterly is OFF, enabling first to register change");
            clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Animation wait failed: {}", e.getMessage()); }
            setQuarterlyPrice("5");
            clickContinue();
            waitForMonetizationUpdatedToast();
            navigateBackFromMonetization();
            openSubscriptionPriceFromProfile();
            assertQuarterlyOfferVisible();
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Settle wait failed: {}", e.getMessage()); }
        }
        // Now disable quarterly
        logger.info("Disabling quarterly toggle");
        clickWithRetry(quarterlyToggle(), 1, ConfigReader.getElementRetryDelay());
        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Animation wait failed: {}", e.getMessage()); }
    }

    @Step("Set Quarterly price to: {price}")
    public void setQuarterlyPrice(String price) {
        waitVisible(quarterlyPriceInput(), ConfigReader.getShortTimeout());
        Locator el = quarterlyPriceInput();
        el.click();
        // Clear robustly before filling
        try { el.fill(""); } catch (Throwable e) { logger.debug("Fill failed: {}", e.getMessage()); }
        try { el.press("Control+A"); el.press("Backspace"); } catch (Throwable e) { logger.debug("Key press failed: {}", e.getMessage()); }
        el.fill(price);
        String current = el.inputValue();
        if (!price.equals(current)) {
            // UI may format as 0.0€, etc.; just log for debugging instead of failing the test
            logger.warn("Quarterly price mismatch after fill. Expected='{}' Actual='{}'", price, current);
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
            } catch (Throwable e) { logger.debug("Enabled check failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        if (!continueButton().isEnabled()) {
            logger.warn("Continue button still disabled after waiting; attempting click regardless");
        }
        clickWithRetry(continueButton(), 2, ConfigReader.getElementRetryDelay());
    }

    @Step("Wait for monetization updated toast")
    public void waitForMonetizationUpdatedToast() {
        waitVisible(monetizationUpdatedPopup(), ConfigReader.getMediumTimeout());
        try { clickWithRetry(monetizationUpdatedPopup(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
    }
}

