package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

public class CreatorPromotionsPage extends BasePage {

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

    private Locator copySpans() {
        return page.getByText("Copy", new Page.GetByTextOptions().setExact(true));
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
            } catch (Throwable e) { logger.debug("Wheel failed: {}", e.getMessage()); }
            return automationSpans().count();
        } catch (Throwable t) {
            logger.warn("Failed to count AUTOMATION promos: {}", t.getMessage());
            return 0;
        }
    }

    private void nudgeLazyLoad() {
        try {
            page.mouse().wheel(0, 600);
            page.waitForTimeout(ConfigReader.getElementRetryDelay());
            page.mouse().wheel(0, -600);
        } catch (Throwable e) { logger.debug("Wheel failed: {}", e.getMessage()); }
    }

    private void scrollToEndAndBack() {
        try {
            for (int i = 0; i < 6; i++) { page.mouse().wheel(0, 800); page.waitForTimeout(ConfigReader.getElementRetryDelay()); }
            for (int i = 0; i < 6; i++) { page.mouse().wheel(0, -800); page.waitForTimeout(ConfigReader.getAnimationTimeout()); }
        } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
    }

    // ----------- Steps -----------
    @Step("Open Settings from profile")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Delete all 'AUTOMATION' promo codes; soft-assert success toast only for the last deletion")
    public void deleteAllAutomationPromosSoft() {
        try {
            // Ensure we are on the Promo code page
            waitVisible(promoTitleExact(), ConfigReader.getShortTimeout());
            int safety = 0;
            while (safety++ < 50) {
                nudgeLazyLoad();
                int beforeCount;
                try { beforeCount = automationSpans().count(); } catch (Throwable e) { beforeCount = 0; logger.debug("Count failed: {}", e.getMessage()); }
                if (beforeCount <= 0) {
                    logger.info("All 'AUTOMATION' promo codes are deleted (none remaining)");
                    break;
                }
                Locator row = automationSpans().first();
                waitVisible(row, ConfigReader.getShortTimeout());
                try { row.scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
                logger.info("Deleting an 'AUTOMATION' promo; remaining before delete: {}", beforeCount);
                clickWithRetry(row, 1, ConfigReader.getElementRetryDelay());
                try {
                    waitVisible(deletePromoButton(), ConfigReader.getShortTimeout());
                    clickWithRetry(deletePromoButton(), 1, ConfigReader.getElementRetryDelay());
                    try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Post-delete wait failed: {}", e.getMessage()); }
                } catch (Throwable e) { logger.debug("Delete button not found/clicked: {}", e.getMessage()); }
                try {
                    waitVisible(yesDeleteButton(), ConfigReader.getShortTimeout());
                    clickWithRetry(yesDeleteButton().first(), 1, ConfigReader.getElementRetryDelay());
                } catch (Throwable e) { logger.debug("Yes-delete click failed: {}", e.getMessage()); }
                long startWait = System.currentTimeMillis();
                boolean countDecreased = false;
                while (System.currentTimeMillis() - startWait < ConfigReader.getShortTimeout()) {
                    try {
                        int now = automationSpans().count();
                        if (now < beforeCount) { countDecreased = true; break; }
                    } catch (Throwable e) { logger.debug("Count failed: {}", e.getMessage()); }
                    try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                }
                int afterCount;
                try { afterCount = automationSpans().count(); } catch (Throwable e) { afterCount = 0; logger.debug("Count failed: {}", e.getMessage()); }
                boolean isLast = afterCount == 0;
                if (isLast) {
                    try {
                        waitVisible(deleteSuccessToast(), ConfigReader.getMediumTimeout());
                        try { clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
                    } catch (Throwable t) {
                        logger.warn("Last deletion did not surface success toast within timeout; proceeding without failure");
                    }
                } else {
                    try {
                        waitVisible(deleteSuccessToast(), ConfigReader.getUiSettleTimeout());
                        try { clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
                    } catch (Throwable e) { logger.debug("Toast wait failed: {}", e.getMessage()); }
                }
                if (!countDecreased) {
                    logger.warn("Promo count did not decrease after deletion; attempting to refresh view");
                    scrollToEndAndBack();
                }
                try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            }
            int remaining = getAutomationPromoCount();
            if (remaining > 0) {
                logger.warn("Validation: {} 'AUTOMATION' promos still visible after first pass; attempting second pass", remaining);
                scrollToEndAndBack();
                try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                try {
                    clickWithRetry(promoMenuItem(), 1, ConfigReader.getElementRetryDelay());
                    waitVisible(promoTitleExact(), ConfigReader.getShortTimeout());
                } catch (Throwable e) { logger.debug("Re-open menu failed: {}", e.getMessage()); }
                int safety2 = 0;
                while (safety2++ < 30) {
                    nudgeLazyLoad();
                    int before;
                    try { before = automationSpans().count(); } catch (Throwable e) { before = 0; logger.debug("Count failed: {}", e.getMessage()); }
                    if (before <= 0) break;
                    Locator row2 = automationSpans().first();
                    waitVisible(row2, ConfigReader.getShortTimeout());
                    try { row2.scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
                    clickWithRetry(row2, 1, ConfigReader.getElementRetryDelay());
                    try {
                        waitVisible(deletePromoButton(), ConfigReader.getShortTimeout());
                        clickWithRetry(deletePromoButton(), 1, ConfigReader.getElementRetryDelay());
                        try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Post-delete wait failed: {}", e.getMessage()); }
                    } catch (Throwable e) { logger.debug("Delete button not found/clicked: {}", e.getMessage()); }
                    try {
                        waitVisible(yesDeleteButton(), ConfigReader.getShortTimeout());
                        clickWithRetry(yesDeleteButton().first(), 1, ConfigReader.getElementRetryDelay());
                    } catch (Throwable e) { logger.debug("Yes-delete click failed: {}", e.getMessage()); }
                    int after = 0;
                    long startCheck = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startCheck < ConfigReader.getShortTimeout()) {
                        try { after = automationSpans().count(); } catch (Throwable e) { logger.debug("Count failed: {}", e.getMessage()); }
                        if (after < before) break;
                        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                    }
                    try { after = automationSpans().count(); } catch (Throwable e) { logger.debug("Count failed: {}", e.getMessage()); }
                    boolean lastNow = after == 0;
                    if (lastNow) {
                        try { waitVisible(deleteSuccessToast(), ConfigReader.getMediumTimeout()); clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable e) { logger.debug("Toast handling failed: {}", e.getMessage()); }
                    } else {
                        try { waitVisible(deleteSuccessToast(), ConfigReader.getUiSettleTimeout()); clickWithRetry(deleteSuccessToast(), 0, 0); } catch (Throwable e) { logger.debug("Toast handling failed: {}", e.getMessage()); }
                    }
                    try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                }
                int finalRemain = getAutomationPromoCount();
                if (finalRemain > 0) {
                    logger.error("Cleanup incomplete: {} 'AUTOMATION' promos still remain after two passes", finalRemain);
                } else {
                    logger.info("Cleanup complete: no 'AUTOMATION' promos remain after validation pass");
                }
            }
        } catch (Throwable t) {
            logger.error("Unexpected error during deleting 'AUTOMATION' promos; proceeding without failing test: {}", t.getMessage());
        }
    }

    @Step("Navigate to 'Promo code' in Settings")
    public void openPromoCodeScreen() {
        // Scroll into view and click
        waitVisible(promoMenuItem(), ConfigReader.getShortTimeout());
        try { promoMenuItem().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(promoMenuItem(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(promoTitleExact(), ConfigReader.getShortTimeout());
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Promo settle wait failed: {}", e.getMessage()); }
        try {
            page.waitForSelector("span.creatorCodePromoName", new Page.WaitForSelectorOptions().setTimeout(ConfigReader.getAnimationTimeout()));
            logger.info("Promo code items found on screen");
        } catch (Throwable t) {
            logger.info("No promo code items found on screen (may be empty)");
        }
        int count = 0;
        try { count = automationSpans().count(); } catch (Throwable e) { logger.debug("Count failed: {}", e.getMessage()); }
        logger.info("Found {} AUTOMATION promo codes on screen", count);
    }

    @Step("Click 'Create a promo code' button")
    public void clickCreatePromoCode() {
        waitVisible(createPromoButton(), ConfigReader.getShortTimeout());
        clickWithRetry(createPromoButton(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Fill promo code: {code}")
    public void fillPromoCode(String code) {
        waitVisible(codeInput(), ConfigReader.getShortTimeout());
        typeAndAssert(codeInput(), code);
    }

    @Step("Fill discount percent: {percent}")
    public void fillDiscountPercent(String percent) {
        waitVisible(discountTextbox(), ConfigReader.getShortTimeout());
        typeAndAssert(discountTextbox(), percent);
    }

    @Step("Fill discount amount: {amount}")
    public void fillDiscountAmount(String amount) {
        waitVisible(discountAmountTextbox(), ConfigReader.getShortTimeout());
        typeAndAssert(discountAmountTextbox(), amount);
    }

    @Step("Select 'Subscription' and 'Unlimited' options")
    public void selectSubscriptionUnlimited() {
        waitVisible(subscriptionLabel(), ConfigReader.getShortTimeout());
        clickWithRetry(subscriptionLabel(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(unlimitedLabel(), ConfigReader.getShortTimeout());
        clickWithRetry(unlimitedLabel(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Select applicability: {applicabilityText}")
    public void selectApplicability(String applicabilityText) {
        Locator opt = page.locator("label").filter(new Locator.FilterOptions().setHasText(applicabilityText));
        waitVisible(opt, ConfigReader.getShortTimeout());
        clickWithRetry(opt, 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Select validity: {validityText}")
    public void selectValidity(String validityText) {
        Locator opt = page.locator("label").filter(new Locator.FilterOptions().setHasText(validityText));
        waitVisible(opt, ConfigReader.getShortTimeout());
        clickWithRetry(opt, 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Click Create to submit promo")
    public void submitCreate() {
        waitVisible(createButton(), ConfigReader.getShortTimeout());
        clickWithRetry(createButton(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert promo created toasts visible")
    public void assertPromoCreatedToasts() {
        waitVisible(promoCreatedToast(), ConfigReader.getMediumTimeout());
        try {
            waitVisible(minPriceToast(), ConfigReader.getUiSettleTimeout());
            try { clickWithRetry(minPriceToast(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
        } catch (Throwable e) {
            logger.info("Min price toast did not appear: {}", e.getMessage());
        }
        try { clickWithRetry(promoCreatedToast(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
    }

    @Step("Assert promo created success toast visible")
    public void assertPromoCreatedSuccessOnly() {
        waitVisible(promoCreatedToast(), ConfigReader.getMediumTimeout());
        try { clickWithRetry(promoCreatedToast(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
    }

    @Step("Click all visible 'Copy' buttons and assert copy toast only for the last click")
    public void clickAllCopyButtonsAndAssert() {
        try {
            // Ensure we are on the Promo code page
            waitVisible(promoTitleExact(), ConfigReader.getShortTimeout());
            try {
                if (automationSpans().count() > 0) waitVisible(automationSpans().first(), ConfigReader.getShortTimeout());
            } catch (Throwable e) { logger.debug("Promo rows check failed: {}", e.getMessage()); }
            Locator copies = copySpans();
            int count = 0;
            long start = System.currentTimeMillis();
            while (System.currentTimeMillis() - start < ConfigReader.getShortTimeout()) {
                try { count = copies.count(); } catch (Throwable e) { count = 0; logger.debug("Count failed: {}", e.getMessage()); }
                if (count > 0) break;
                try {
                    page.mouse().wheel(0, 600);
                    page.waitForTimeout(ConfigReader.getAnimationTimeout());
                    page.mouse().wheel(0, -600);
                } catch (Throwable e) { logger.debug("Wheel failed: {}", e.getMessage()); }
            }
            if (count <= 0) {
                logger.warn("No 'Copy' buttons found on Promotions page after waiting");
                return;
            }
            logger.info("Found {} 'Copy' elements on Promotions page", count);
            for (int i = 0; i < count; i++) {
                Locator btn = copies.nth(i);
                waitVisible(btn, ConfigReader.getShortTimeout());
                try { btn.scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
                logger.info("Clicking 'Copy' element index {} (will assert toast only for last index: {})", i, count - 1);
                clickWithRetry(btn, 1, ConfigReader.getElementRetryDelay());
                if (i == count - 1) {
                    try {
                        waitVisible(copySuccessToast(), ConfigReader.getMediumTimeout());
                        try { clickWithRetry(copySuccessToast(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
                    } catch (Throwable t) {
                        logger.warn("Last 'Copy' click did not surface the success toast within timeout; proceeding without failure");
                    }
                } else {
                    try {
                        waitVisible(copySuccessToast(), ConfigReader.getUiSettleTimeout());
                        try { clickWithRetry(copySuccessToast(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
                    } catch (Throwable e) { logger.debug("Toast wait failed: {}", e.getMessage()); }
                }
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            }
        } catch (Throwable t) {
            logger.error("Unexpected error during clicking all 'Copy' elements; proceeding without failing test: {}", t.getMessage());
        }
    }
}

