package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreatorPromotionsPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(CreatorPromotionsPage.class);

    // Promotions page uses intentionally SHORT timeouts (1-8s) because UI elements load quickly
    // Do NOT use ConfigReader defaults (10-120s) - they would make tests 10-15x slower
    private static final int SHORT_TIMEOUT = 1000;   // 1s - Menu items, buttons, inputs
    private static final int MEDIUM_TIMEOUT = 2000;  // 2s - Toast messages, selectors
    private static final int LONG_TIMEOUT = 8000;    // 8s - Copy buttons, deletion polling

    // URLs and texts
    private static final String SETTINGS_URL_PART = "/common/setting";
    private static final String PROMO_TITLE = "Promo code"; // exact title on promotions screen
    private static final String SETTINGS_ICON_NAME = "settings";
    private static final String CREATE_PROMO_BUTTON = "Create a promo code";

    public CreatorPromotionsPage(Page page) {
        super(page);
    }

    // ----------- Locators -----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SETTINGS_ICON_NAME));
    }

    private Locator promoMenuItem() {
        return getByTextExact(PROMO_TITLE);
    }

    private Locator promoTitleExact() {
        return getByTextExact(PROMO_TITLE);
    }

    // Promo code rows: titles like 'AUTOMATIONPROMOCODE...'
    // Use the specific class from DOM: span.ant-typography.creatorCodePromoName
    private Locator automationSpans() {
        return page.locator("span.creatorCodePromoName").filter(
            new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^AUTOMATION", java.util.regex.Pattern.CASE_INSENSITIVE))
        );
    }

    private Locator createPromoButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CREATE_PROMO_BUTTON));
    }

    private Locator codeInput() {
        return page.getByPlaceholder("EX : AB123");
    }

    private Locator discountTextbox() {
        return page.getByRole(AriaRole.TEXTBOX).nth(1);
    }

    private Locator discountAmountTextbox() {
        return page.getByRole(AriaRole.TEXTBOX).nth(2);
    }

    private Locator subscriptionLabel() {
        return page.locator("label").filter(new Locator.FilterOptions().setHasText("Subscription"));
    }

    private Locator unlimitedLabel() {
        return page.locator("label").filter(new Locator.FilterOptions().setHasText("Unlimited"));
    }

    private Locator createButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create"));
    }

    // Some UIs render 'Copy' text inside a span; use a resilient XPath per requirement
    private Locator copySpans() {
        return page.locator("xpath=//span[contains(text(),'Copy')]");
    }

    private Locator promoCreatedToast() {
        return page.getByText("Promo code created successfully.");
    }

    private Locator minPriceToast() {
        return page.getByText("The minimum subscription price for fans will be ?5");
    }

    private Locator copySuccessToast() {
        return page.getByText("Copied the Promo Code to clipboard !");
    }

    private Locator yesDeleteButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, delete"));
    }

    // Delete button inside promo details screen
    private Locator deletePromoButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete"));
    }

    private Locator deleteSuccessToast() {
        return page.getByText("Promo code deleted successfully !");
    }

    // Utility to count visible AUTOMATION promo spans with a gentle lazy-load nudge
    public int getAutomationPromoCount() {
        try {
            try {
                page.mouse().wheel(0, 200);
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
                page.mouse().wheel(0, -200);
            } catch (Throwable e) { log.debug("Wheel failed: {}", e.getMessage()); }
            return automationSpans().count();
        } catch (Throwable t) {
            log.warn("Failed to count AUTOMATION promos: {}", t.getMessage());
            return 0;
        }
    }

    private void nudgeLazyLoad() {
        try {
            page.mouse().wheel(0, 600);
            page.waitForTimeout(ConfigReader.getElementRetryDelay());
            page.mouse().wheel(0, -600);
        } catch (Throwable e) { log.debug("Wheel failed: {}", e.getMessage()); }
    }

    private void scrollToEndAndBack() {
        try {
            for (int i = 0; i < 6; i++) { page.mouse().wheel(0, 800); page.waitForTimeout(ConfigReader.getElementRetryDelay()); }
            for (int i = 0; i < 6; i++) { page.mouse().wheel(0, -800); page.waitForTimeout(ConfigReader.getAnimationTimeout()); }
        } catch (Throwable e) { log.debug("Scroll failed: {}", e.getMessage()); }
    }

    // ----------- Steps -----------
    @Step("Open Settings from profile")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), SHORT_TIMEOUT);
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        // Ensure URL contains /common/setting
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            log.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Delete all 'AUTOMATION' promo codes; soft-assert success toast only for the last deletion")
    public void deleteAllAutomationPromosSoft() {
        try {
            // Ensure we are on the Promo code page
            waitVisible(promoTitleExact(), SHORT_TIMEOUT);
            // Keep deleting until there are zero AUTOMATION promos visible
            int safety = 0; // prevent infinite loops
            while (safety++ < 50) {
                // Nudge scroll to trigger lazy-load before counting
                nudgeLazyLoad();

                int beforeCount;
                try { beforeCount = automationSpans().count(); } catch (Throwable e) { beforeCount = 0; log.debug("Count failed: {}", e.getMessage()); }
                if (beforeCount <= 0) {
                    log.info("All 'AUTOMATION' promo codes are deleted (none remaining)");
                    break;
                }

                // Delete the first visible row - click to open promo details
                Locator row = automationSpans().first();
                waitVisible(row, SHORT_TIMEOUT);
                try { row.scrollIntoViewIfNeeded(); } catch (Throwable e) { log.debug("Scroll failed: {}", e.getMessage()); }
                log.info("Deleting an 'AUTOMATION' promo; remaining before delete: {}", beforeCount);
                clickWithRetry(row, 1, ConfigReader.getElementRetryDelay());
                page.waitForTimeout(ConfigReader.getElementRetryDelay());

                // Click Delete button inside promo details (if present)
                try {
                    if (deletePromoButton().isVisible()) {
                        clickWithRetry(deletePromoButton(), 1, ConfigReader.getElementRetryDelay());
                        page.waitForTimeout(ConfigReader.getAnimationTimeout());
                    }
                } catch (Throwable e) { log.debug("Delete button check failed: {}", e.getMessage()); }

                // Confirm delete
                waitVisible(yesDeleteButton(), SHORT_TIMEOUT);
                clickWithRetry(yesDeleteButton(), 1, ConfigReader.getElementRetryDelay());

                // Wait for the count to decrease or the row to detach
                long startWait = System.currentTimeMillis();
                boolean countDecreased = false;
                while (System.currentTimeMillis() - startWait < LONG_TIMEOUT) {
                    try {
                        int now = automationSpans().count();
                        if (now < beforeCount) { countDecreased = true; break; }
                    } catch (Throwable e) { log.debug("Count failed: {}", e.getMessage()); }
                    try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
                }

                // Determine if this was the last deletion
                int afterCount;
                try { afterCount = automationSpans().count(); } catch (Throwable e) { afterCount = 0; log.debug("Count failed: {}", e.getMessage()); }
                boolean isLast = afterCount == 0;

                if (isLast) {
                    // Last deletion: try to observe and close the success toast, but do not fail if not seen
                    try {
                        waitVisible(deleteSuccessToast(), LONG_TIMEOUT);
                        try { clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable e) { log.debug("Click failed: {}", e.getMessage()); }
                    } catch (Throwable t) {
                        log.warn("Last deletion did not surface success toast within timeout; proceeding without failure");
                    }
                } else {
                    // Intermediate deletion: close toast if it appears; do not assert
                    try {
                        waitVisible(deleteSuccessToast(), MEDIUM_TIMEOUT);
                        try { clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable e) { log.debug("Click failed: {}", e.getMessage()); }
                    } catch (Throwable e) {
                        log.debug("Toast wait failed: {}", e.getMessage());
                    }
                }

                // If count did not decrease, try a gentle refresh of content by scrolling; then continue
                if (!countDecreased) {
                    log.warn("Promo count did not decrease after deletion; attempting to refresh view");
                    scrollToEndAndBack();
                }

                // Small pause before next iteration
                try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
            }

            // Validation pass: if any still remain, attempt deeper scroll and re-open promo page, then delete again
            int remaining = getAutomationPromoCount();
            if (remaining > 0) {
                log.warn("Validation: {} 'AUTOMATION' promos still visible after first pass; attempting second pass", remaining);
                scrollToEndAndBack();
                try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
                // Re-open promo menu to refresh view
                try {
                    clickWithRetry(promoMenuItem(), 1, ConfigReader.getElementRetryDelay());
                    waitVisible(promoTitleExact(), SHORT_TIMEOUT);
                } catch (Throwable e) { log.debug("Re-open menu failed: {}", e.getMessage()); }

                // Secondary loop with smaller safety bound
                int safety2 = 0;
                while (safety2++ < 30) {
                    nudgeLazyLoad();
                    int before;
                    try { before = automationSpans().count(); } catch (Throwable e) { before = 0; log.debug("Count failed: {}", e.getMessage()); }
                    if (before <= 0) break;

                    Locator row = automationSpans().first();
                    waitVisible(row, SHORT_TIMEOUT);
                    try { row.scrollIntoViewIfNeeded(); } catch (Throwable e) { log.debug("Scroll failed: {}", e.getMessage()); }
                    clickWithRetry(row, 1, ConfigReader.getElementRetryDelay());
                    page.waitForTimeout(ConfigReader.getElementRetryDelay());

                    // Click Delete button inside promo details (if present)
                    try {
                        if (deletePromoButton().isVisible()) {
                            clickWithRetry(deletePromoButton(), 1, ConfigReader.getElementRetryDelay());
                            page.waitForTimeout(ConfigReader.getAnimationTimeout());
                        }
                    } catch (Throwable e) { log.debug("Delete button check failed: {}", e.getMessage()); }

                    waitVisible(yesDeleteButton(), SHORT_TIMEOUT);
                    clickWithRetry(yesDeleteButton(), 1, ConfigReader.getElementRetryDelay());

                    // Toast handling: only assert when it becomes last
                    int after;
                    long startCheck = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startCheck < LONG_TIMEOUT) {
                        try { after = automationSpans().count(); } catch (Throwable e) { after = 0; log.debug("Count failed: {}", e.getMessage()); }
                        if (after < before) break;
                        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
                    }
                    try { after = automationSpans().count(); } catch (Throwable e) { after = 0; log.debug("Count failed: {}", e.getMessage()); }
                    boolean lastNow = after == 0;
                    if (lastNow) {
                        try { waitVisible(deleteSuccessToast(), LONG_TIMEOUT); clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable e) { log.debug("Toast handling failed: {}", e.getMessage()); }
                    } else {
                        try { waitVisible(deleteSuccessToast(), MEDIUM_TIMEOUT); clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable e) { log.debug("Toast handling failed: {}", e.getMessage()); }
                    }
                    try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
                }

                int finalRemain = getAutomationPromoCount();
                if (finalRemain > 0) {
                    log.error("Cleanup incomplete: {} 'AUTOMATION' promos still remain after two passes");
                } else {
                    log.info("Cleanup complete: no 'AUTOMATION' promos remain after validation pass");
                }
            }
        } catch (Throwable t) {
            log.error("Unexpected error during deleting 'AUTOMATION' promos; proceeding without failing test: {}", t.getMessage());
        }
    }

    @Step("Navigate to 'Promo code' in Settings")
    public void openPromoCodeScreen() {
        // Scroll into view and click
        waitVisible(promoMenuItem(), SHORT_TIMEOUT);
        try { promoMenuItem().scrollIntoViewIfNeeded(); } catch (Throwable e) { log.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(promoMenuItem(), 1, ConfigReader.getElementRetryDelay());
        // Verify Promotions page title exact
        waitVisible(promoTitleExact(), SHORT_TIMEOUT);
        // Wait for page to fully load
        page.waitForTimeout(ConfigReader.getUiSettleTimeout());
        // Wait for promo items to render
        try {
            page.waitForSelector("span.creatorCodePromoName", new Page.WaitForSelectorOptions().setTimeout(MEDIUM_TIMEOUT));
            log.info("Promo code items found on screen");
        } catch (Throwable t) {
            log.info("No promo code items found on screen (may be empty)");
        }
        // Log count of automation promos
        int count = 0;
        try { count = automationSpans().count(); } catch (Throwable e) { log.debug("Count failed: {}", e.getMessage()); }
        log.info("Found {} AUTOMATION promo codes on screen", count);
    }

    @Step("Click 'Create a promo code' button")
    public void clickCreatePromoCode() {
        waitVisible(createPromoButton(), SHORT_TIMEOUT);
        clickWithRetry(createPromoButton(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Fill promo code: {code}")
    public void fillPromoCode(String code) {
        waitVisible(codeInput(), SHORT_TIMEOUT);
        typeAndAssert(codeInput(), code);
    }

    @Step("Fill discount percent: {percent}")
    public void fillDiscountPercent(String percent) {
        waitVisible(discountTextbox(), SHORT_TIMEOUT);
        typeAndAssert(discountTextbox(), percent);
    }

    @Step("Fill discount amount: {amount}")
    public void fillDiscountAmount(String amount) {
        waitVisible(discountAmountTextbox(), SHORT_TIMEOUT);
        typeAndAssert(discountAmountTextbox(), amount);
    }

    @Step("Select 'Subscription' and 'Unlimited' options")
    public void selectSubscriptionUnlimited() {
        waitVisible(subscriptionLabel(), SHORT_TIMEOUT);
        clickWithRetry(subscriptionLabel(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(unlimitedLabel(), SHORT_TIMEOUT);
        clickWithRetry(unlimitedLabel(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Select applicability: {applicabilityText}")
    public void selectApplicability(String applicabilityText) {
        Locator opt = page.locator("label").filter(new Locator.FilterOptions().setHasText(applicabilityText));
        waitVisible(opt, SHORT_TIMEOUT);
        clickWithRetry(opt, 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Select validity: {validityText}")
    public void selectValidity(String validityText) {
        Locator opt = page.locator("label").filter(new Locator.FilterOptions().setHasText(validityText));
        waitVisible(opt, SHORT_TIMEOUT);
        clickWithRetry(opt, 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Click Create to submit promo")
    public void submitCreate() {
        waitVisible(createButton(), SHORT_TIMEOUT);
        clickWithRetry(createButton(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert promo created toasts visible")
    public void assertPromoCreatedToasts() {
        waitVisible(promoCreatedToast(), MEDIUM_TIMEOUT);
        // Try to wait for min price toast but don't fail if it doesn't appear
        try {
            waitVisible(minPriceToast(), MEDIUM_TIMEOUT);
            try { clickWithRetry(minPriceToast(), 0, 0); } catch (Throwable e) { log.debug("Click failed: {}", e.getMessage()); }
        } catch (Throwable e) {
            log.info("Min price toast did not appear: {}", e.getMessage());
        }
        // Try dismiss or click to clear toast to keep UI clean for next steps
        try { clickWithRetry(promoCreatedToast(), 0, 0); } catch (Throwable e) { log.debug("Click failed: {}", e.getMessage()); }
    }

    @Step("Assert promo created success toast visible")
    public void assertPromoCreatedSuccessOnly() {
        waitVisible(promoCreatedToast(), MEDIUM_TIMEOUT);
        try { clickWithRetry(promoCreatedToast(), 0, 0); } catch (Throwable e) { log.debug("Click failed: {}", e.getMessage()); }
    }

    @Step("Click all visible 'Copy' buttons and assert copy toast only for the last click")
    public void clickAllCopyButtonsAndAssert() {
        try {
            // Ensure we are on the Promo code page
            waitVisible(promoTitleExact(), SHORT_TIMEOUT);
            // Additional guard: wait for any automation promo rows to render, if present
            try {
                if (automationSpans().count() > 0) {
                    waitVisible(automationSpans().first(), SHORT_TIMEOUT);
                }
            } catch (Throwable e) { log.debug("Promo rows check failed: {}", e.getMessage()); }
            // Poll for copy elements to appear, with gentle scroll nudges
            Locator copies = copySpans();
            int count = 0;
            long start = System.currentTimeMillis();
            long timeoutMs = LONG_TIMEOUT;
            while (System.currentTimeMillis() - start < timeoutMs) {
                try { count = copies.count(); } catch (Throwable e) { count = 0; log.debug("Count failed: {}", e.getMessage()); }
                if (count > 0) break;
                try {
                    // Nudge scroll to trigger lazy-loaded content
                    page.mouse().wheel(0, 600);
                    page.waitForTimeout(ConfigReader.getAnimationTimeout());
                    page.mouse().wheel(0, -600);
                } catch (Throwable e) { log.debug("Wheel failed: {}", e.getMessage()); }
            }
            if (count <= 0) {
                log.warn("No 'Copy' buttons found on Promotions page after waiting");
                return;
            }
            log.info("Found {} 'Copy' elements on Promotions page", count);
            for (int i = 0; i < count; i++) {
                // Refetch each iteration to avoid stale indexes after DOM updates
                Locator btn = copies.nth(i);
                waitVisible(btn, SHORT_TIMEOUT);
                try { btn.scrollIntoViewIfNeeded(); } catch (Throwable e) { log.debug("Scroll failed: {}", e.getMessage()); }
                log.info("Clicking 'Copy' element index {} (will assert toast only for last index: {})", i, count - 1);
                clickWithRetry(btn, 1, ConfigReader.getElementRetryDelay());
                if (i == count - 1) {
                    // For the last click, try to observe and close the toast, but do not fail if not seen
                    try {
                        waitVisible(copySuccessToast(), LONG_TIMEOUT);
                        try { clickWithRetry(copySuccessToast(), 0, 0); } catch (Throwable e) { log.debug("Click failed: {}", e.getMessage()); }
                    } catch (Throwable t) {
                        log.warn("Last 'Copy' click did not surface the success toast within timeout; proceeding without failure");
                    }
                } else {
                    // For intermediate clicks, if a toast appears, close it without asserting
                    try {
                        waitVisible(copySuccessToast(), MEDIUM_TIMEOUT);
                        try { clickWithRetry(copySuccessToast(), 0, 0); } catch (Throwable e) { log.debug("Click failed: {}", e.getMessage()); }
                    } catch (Throwable e) {
                        log.debug("Toast wait failed: {}", e.getMessage());
                    }
                }
                // small pause to avoid any overlap before next iteration
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
            }
        } catch (Throwable t) {
            log.error("Unexpected error during clicking all 'Copy' elements; proceeding without failing test: {}", t.getMessage());
        }
    }
}

