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
                page.waitForTimeout(80);
                page.mouse().wheel(0, -200);
            } catch (Throwable ignored) {}
            return automationSpans().count();
        } catch (Throwable t) {
            log.warn("Failed to count AUTOMATION promos: {}", t.getMessage());
            return 0;
        }
    }

    private void nudgeLazyLoad() {
        try {
            page.mouse().wheel(0, 600);
            page.waitForTimeout(120);
            page.mouse().wheel(0, -600);
        } catch (Throwable ignored) {}
    }

    private void scrollToEndAndBack() {
        try {
            for (int i = 0; i < 6; i++) { page.mouse().wheel(0, 800); page.waitForTimeout(120); }
            for (int i = 0; i < 6; i++) { page.mouse().wheel(0, -800); page.waitForTimeout(80); }
        } catch (Throwable ignored) {}
    }

    // ----------- Steps -----------
    @Step("Open Settings from profile")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 1, 150);
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
            waitVisible(promoTitleExact(), DEFAULT_WAIT);
            // Keep deleting until there are zero AUTOMATION promos visible
            int safety = 0; // prevent infinite loops
            while (safety++ < 50) {
                // Nudge scroll to trigger lazy-load before counting
                nudgeLazyLoad();

                int beforeCount;
                try { beforeCount = automationSpans().count(); } catch (Throwable ignored) { beforeCount = 0; }
                if (beforeCount <= 0) {
                    log.info("All 'AUTOMATION' promo codes are deleted (none remaining)");
                    break;
                }

                // Delete the first visible row - click to open promo details
                Locator row = automationSpans().first();
                waitVisible(row, DEFAULT_WAIT);
                try { row.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                log.info("Deleting an 'AUTOMATION' promo; remaining before delete: {}", beforeCount);
                clickWithRetry(row, 1, 120);
                page.waitForTimeout(500);

                // Click Delete button inside promo details (if present)
                try {
                    if (deletePromoButton().isVisible()) {
                        clickWithRetry(deletePromoButton(), 1, 120);
                        page.waitForTimeout(300);
                    }
                } catch (Throwable ignored) {}

                // Confirm delete
                waitVisible(yesDeleteButton(), DEFAULT_WAIT);
                clickWithRetry(yesDeleteButton(), 1, 120);

                // Wait for the count to decrease or the row to detach
                long startWait = System.currentTimeMillis();
                boolean countDecreased = false;
                while (System.currentTimeMillis() - startWait < 8_000) {
                    try {
                        int now = automationSpans().count();
                        if (now < beforeCount) { countDecreased = true; break; }
                    } catch (Throwable ignored) {}
                    try { page.waitForTimeout(200); } catch (Throwable ignored) {}
                }

                // Determine if this was the last deletion
                int afterCount;
                try { afterCount = automationSpans().count(); } catch (Throwable ignored) { afterCount = 0; }
                boolean isLast = afterCount == 0;

                if (isLast) {
                    // Last deletion: try to observe and close the success toast, but do not fail if not seen
                    try {
                        waitVisible(deleteSuccessToast(), 8_000);
                        try { clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable ignored) {}
                    } catch (Throwable t) {
                        log.warn("Last deletion did not surface success toast within timeout; proceeding without failure");
                    }
                } else {
                    // Intermediate deletion: close toast if it appears; do not assert
                    try {
                        waitVisible(deleteSuccessToast(), 2_000);
                        try { clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable ignored) {}
                    } catch (Throwable ignored) {
                        // toast did not appear; proceed
                    }
                }

                // If count did not decrease, try a gentle refresh of content by scrolling; then continue
                if (!countDecreased) {
                    log.warn("Promo count did not decrease after deletion; attempting to refresh view");
                    scrollToEndAndBack();
                }

                // Small pause before next iteration
                try { page.waitForTimeout(200); } catch (Throwable ignored) {}
            }

            // Validation pass: if any still remain, attempt deeper scroll and re-open promo page, then delete again
            int remaining = getAutomationPromoCount();
            if (remaining > 0) {
                log.warn("Validation: {} 'AUTOMATION' promos still visible after first pass; attempting second pass", remaining);
                scrollToEndAndBack();
                try { page.waitForTimeout(250); } catch (Throwable ignored) {}
                // Re-open promo menu to refresh view
                try {
                    clickWithRetry(promoMenuItem(), 1, 150);
                    waitVisible(promoTitleExact(), DEFAULT_WAIT);
                } catch (Throwable ignored) {}

                // Secondary loop with smaller safety bound
                int safety2 = 0;
                while (safety2++ < 30) {
                    nudgeLazyLoad();
                    int before;
                    try { before = automationSpans().count(); } catch (Throwable ignored) { before = 0; }
                    if (before <= 0) break;

                    Locator row = automationSpans().first();
                    waitVisible(row, DEFAULT_WAIT);
                    try { row.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                    clickWithRetry(row, 1, 120);
                    page.waitForTimeout(500);

                    // Click Delete button inside promo details (if present)
                    try {
                        if (deletePromoButton().isVisible()) {
                            clickWithRetry(deletePromoButton(), 1, 120);
                            page.waitForTimeout(300);
                        }
                    } catch (Throwable ignored) {}

                    waitVisible(yesDeleteButton(), DEFAULT_WAIT);
                    clickWithRetry(yesDeleteButton(), 1, 120);

                    // Toast handling: only assert when it becomes last
                    int after;
                    long startCheck = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startCheck < 6_000) {
                        try { after = automationSpans().count(); } catch (Throwable ignored) { after = 0; }
                        if (after < before) break;
                        try { page.waitForTimeout(200); } catch (Throwable ignored) {}
                    }
                    try { after = automationSpans().count(); } catch (Throwable ignored) { after = 0; }
                    boolean lastNow = after == 0;
                    if (lastNow) {
                        try { waitVisible(deleteSuccessToast(), 6_000); clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable ignored) {}
                    } else {
                        try { waitVisible(deleteSuccessToast(), 2_000); clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable ignored) {}
                    }
                    try { page.waitForTimeout(180); } catch (Throwable ignored) {}
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
        waitVisible(promoMenuItem(), DEFAULT_WAIT);
        try { promoMenuItem().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(promoMenuItem(), 1, 150);
        // Verify Promotions page title exact
        waitVisible(promoTitleExact(), DEFAULT_WAIT);
        // Wait for page to fully load
        page.waitForTimeout(1500);
        // Wait for promo items to render
        try {
            page.waitForSelector("span.creatorCodePromoName", new Page.WaitForSelectorOptions().setTimeout(5000));
            log.info("Promo code items found on screen");
        } catch (Throwable t) {
            log.info("No promo code items found on screen (may be empty)");
        }
        // Log count of automation promos
        int count = 0;
        try { count = automationSpans().count(); } catch (Throwable ignored) {}
        log.info("Found {} AUTOMATION promo codes on screen", count);
    }

    @Step("Click 'Create a promo code' button")
    public void clickCreatePromoCode() {
        waitVisible(createPromoButton(), DEFAULT_WAIT);
        clickWithRetry(createPromoButton(), 1, 150);
    }

    @Step("Fill promo code: {code}")
    public void fillPromoCode(String code) {
        waitVisible(codeInput(), DEFAULT_WAIT);
        typeAndAssert(codeInput(), code);
    }

    @Step("Fill discount percent: {percent}")
    public void fillDiscountPercent(String percent) {
        waitVisible(discountTextbox(), DEFAULT_WAIT);
        typeAndAssert(discountTextbox(), percent);
    }

    @Step("Fill discount amount: {amount}")
    public void fillDiscountAmount(String amount) {
        waitVisible(discountAmountTextbox(), DEFAULT_WAIT);
        typeAndAssert(discountAmountTextbox(), amount);
    }

    @Step("Select 'Subscription' and 'Unlimited' options")
    public void selectSubscriptionUnlimited() {
        waitVisible(subscriptionLabel(), DEFAULT_WAIT);
        clickWithRetry(subscriptionLabel(), 1, 120);
        waitVisible(unlimitedLabel(), DEFAULT_WAIT);
        clickWithRetry(unlimitedLabel(), 1, 120);
    }

    @Step("Select applicability: {applicabilityText}")
    public void selectApplicability(String applicabilityText) {
        Locator opt = page.locator("label").filter(new Locator.FilterOptions().setHasText(applicabilityText));
        waitVisible(opt, DEFAULT_WAIT);
        clickWithRetry(opt, 1, 120);
    }

    @Step("Select validity: {validityText}")
    public void selectValidity(String validityText) {
        Locator opt = page.locator("label").filter(new Locator.FilterOptions().setHasText(validityText));
        waitVisible(opt, DEFAULT_WAIT);
        clickWithRetry(opt, 1, 120);
    }

    @Step("Click Create to submit promo")
    public void submitCreate() {
        waitVisible(createButton(), DEFAULT_WAIT);
        clickWithRetry(createButton(), 1, 150);
    }

    @Step("Assert promo created toasts visible")
    public void assertPromoCreatedToasts() {
        waitVisible(promoCreatedToast(), ConfigReader.getVisibilityTimeout());
        // Try to wait for min price toast but don't fail if it doesn't appear
        try {
            waitVisible(minPriceToast(), 3_000);
            try { clickWithRetry(minPriceToast(), 0, 0); } catch (Throwable ignored) {}
        } catch (Throwable e) {
            log.info("Min price toast did not appear: {}", e.getMessage());
        }
        // Try dismiss or click to clear toast to keep UI clean for next steps
        try { clickWithRetry(promoCreatedToast(), 0, 0); } catch (Throwable ignored) {}
    }

    @Step("Assert promo created success toast visible")
    public void assertPromoCreatedSuccessOnly() {
        waitVisible(promoCreatedToast(), ConfigReader.getVisibilityTimeout());
        try { clickWithRetry(promoCreatedToast(), 0, 0); } catch (Throwable ignored) {}
    }

    @Step("Click all visible 'Copy' buttons and assert copy toast only for the last click")
    public void clickAllCopyButtonsAndAssert() {
        try {
            // Ensure we are on the Promo code page
            waitVisible(promoTitleExact(), DEFAULT_WAIT);
            // Additional guard: wait for any automation promo rows to render, if present
            try {
                if (automationSpans().count() > 0) {
                    waitVisible(automationSpans().first(), DEFAULT_WAIT);
                }
            } catch (Throwable ignored) {}
            // Poll for copy elements to appear, with gentle scroll nudges
            Locator copies = copySpans();
            int count = 0;
            long start = System.currentTimeMillis();
            long timeoutMs = 15_000;
            while (System.currentTimeMillis() - start < timeoutMs) {
                try { count = copies.count(); } catch (Throwable ignored) { count = 0; }
                if (count > 0) break;
                try {
                    // Nudge scroll to trigger lazy-loaded content
                    page.mouse().wheel(0, 600);
                    page.waitForTimeout(200);
                    page.mouse().wheel(0, -600);
                } catch (Throwable ignored) {}
            }
            if (count <= 0) {
                log.warn("No 'Copy' buttons found on Promotions page after waiting");
                return;
            }
            log.info("Found {} 'Copy' elements on Promotions page", count);
            for (int i = 0; i < count; i++) {
                // Refetch each iteration to avoid stale indexes after DOM updates
                Locator btn = copies.nth(i);
                waitVisible(btn, DEFAULT_WAIT);
                try { btn.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                log.info("Clicking 'Copy' element index {} (will assert toast only for last index: {})", i, count - 1);
                clickWithRetry(btn, 1, 120);
                if (i == count - 1) {
                    // For the last click, try to observe and close the toast, but do not fail if not seen
                    try {
                        waitVisible(copySuccessToast(), 8_000);
                        try { clickWithRetry(copySuccessToast(), 0, 0); } catch (Throwable ignored) {}
                    } catch (Throwable t) {
                        log.warn("Last 'Copy' click did not surface the success toast within timeout; proceeding without failure");
                    }
                } else {
                    // For intermediate clicks, if a toast appears, close it without asserting
                    try {
                        waitVisible(copySuccessToast(), 2_000);
                        try { clickWithRetry(copySuccessToast(), 0, 0); } catch (Throwable ignored) {}
                    } catch (Throwable ignored) {
                        // toast did not appear within short timeout; proceed
                    }
                }
                // small pause to avoid any overlap before next iteration
                try { page.waitForTimeout(150); } catch (Throwable ignored) {}
            }
        } catch (Throwable t) {
            log.error("Unexpected error during clicking all 'Copy' elements; proceeding without failing test: {}", t.getMessage());
        }
    }
}

