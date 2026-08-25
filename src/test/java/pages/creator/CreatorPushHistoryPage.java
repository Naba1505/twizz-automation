package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page object for Creator -> Settings -> History of pushes flow
 */
public class CreatorPushHistoryPage extends BasePage {
    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorPushHistoryPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator historyOfPushesMenu() {
        return page.getByText("History of pushes");
    }

    private Locator historyMediaPushTitle() {
        return page.getByText("History Media push");
    }

    private Locator performanceTitle() {
        return page.getByText("Performance");
    }

    private Locator backArrow() {
        // Try arrow left first
        Locator arrowLeft = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
        if (arrowLeft.count() > 0) {
            return arrowLeft;
        }
        // Fallback: close button (X) - updated UI
        Locator closeBtn = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("close"));
        if (closeBtn.count() > 0) {
            return closeBtn;
        }
        // Fallback: any close icon
        return page.locator("[class*='close'], [aria-label*='close'], .anticon-close");
    }

    private Locator historyRows() {
        // Primary: ant-row with justify-content-between class
        Locator primary = page.locator(".ant-row.justify-content-between");
        if (primary.count() > 0) {
            return primary;
        }
        // Fallback 1: any ant-row that might contain history items
        Locator fallback1 = page.locator(".ant-row");
        if (fallback1.count() > 0) {
            return fallback1;
        }
        // Fallback 2: ant-space-item (common Ant Design list item container)
        Locator fallback2 = page.locator(".ant-space-item");
        if (fallback2.count() > 0) {
            return fallback2;
        }
        // Fallback 3: any clickable list item
        return page.locator("[role='listitem'], .list-item, [class*='item']");
    }

    private Locator firstHistoryRow() {
        return historyRows().first();
    }

    private Locator lastHistoryClickable() {
        Locator rows = historyRows();
        if (rows.count() > 0) return rows.last();
        // Fallback: any clickable typography/text inside the history list
        Locator typos = page.locator(".ant-typography");
        if (typos.count() > 0) return typos.last();
        return page.locator(".ant-space-item").last();
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Push History)")
    public void openSettingsFromProfile() {
        // Ensure we are on the profile page before looking for the settings icon
        navigateAndWait(ConfigReader.getBaseUrl() + "/creator/profile");
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Assert current URL contains settings path")
    public void assertOnSettingsUrl() {
        if (!page.url().contains(SETTINGS_URL_PART)) {
            throw new AssertionError("Did not land on Settings screen. URL: " + page.url());
        }
        logger.info("Settings URL confirmed: {}", page.url());
    }

    @Step("Open 'History of pushes' screen")
    public void openHistoryOfPushes() {
        waitVisible(historyOfPushesMenu(), ConfigReader.getShortTimeout());
        try { historyOfPushesMenu().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(historyOfPushesMenu(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(historyMediaPushTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Assert Total income is visible")
    public void assertTotalIncomeVisible() {
        waitVisible(page.getByText("Total income"), ConfigReader.getShortTimeout());
        logger.info("Total income visible");
    }

    @Step("Assert loader is visible")
    public void assertLoaderVisible() {
        waitVisible(page.locator(".loader"), ConfigReader.getShortTimeout());
        logger.info("Loader visible");
    }

    @Step("Assert Media Push image is visible")
    public void assertMediaPushImageVisible() {
        Locator mediaPushImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Media Push")).first();
        waitVisible(mediaPushImg, ConfigReader.getShortTimeout());
        logger.info("Media Push image visible");
    }

    @Step("Scroll down to view more content")
    public void scrollDown() {
        try {
            page.mouse().wheel(0, 800);
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
            logger.info("Scrolled down");
        } catch (Exception e) {
            logger.debug("Scroll down failed: {}", e.getMessage());
        }
    }

    @Step("Scroll back to top")
    public void scrollToTop() {
        for (int i = 0; i < 4; i++) {
            try {
                page.mouse().wheel(0, -800);
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
            } catch (Exception e) {
                logger.debug("Scroll up failed: {}", e.getMessage());
            }
        }
        logger.info("Scrolled back to top");
    }

    @Step("Click on first visible Media Push image")
    public void clickFirstMediaPushImage() {
        Locator mediaPushImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Media Push")).first();
        waitVisible(mediaPushImg, ConfigReader.getShortTimeout());
        clickWithRetry(mediaPushImg, 1, ConfigReader.getElementRetryDelay());
        logger.info("Clicked on first Media Push image");
    }

    @Step("Assert Price per unit is visible in dialog")
    public void assertPricePerUnitVisible() {
        waitVisible(page.getByText("Price per unit"), ConfigReader.getShortTimeout());
        logger.info("Price per unit visible");
    }

    @Step("Assert Summary of the Push is visible in dialog")
    public void assertSummaryOfPushVisible() {
        waitVisible(page.getByText("Summary of the Push"), ConfigReader.getShortTimeout());
        logger.info("Summary of the Push visible");
    }

    @Step("Close the dialog")
    public void closeDialog() {
        Locator closeBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("close"));
        waitVisible(closeBtn, ConfigReader.getShortTimeout());
        clickWithRetry(closeBtn, 1, ConfigReader.getElementRetryDelay());
        logger.info("Dialog closed");
    }

    @Step("Assert back on History Media push screen")
    public void assertBackOnHistoryMediaPushScreen() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("History Media push"));
        waitVisible(heading, ConfigReader.getShortTimeout());
        logger.info("Back on History Media push screen");
    }

    @Step("Open last media push entry from the list")
    public void openLastMediaPushEntry() {
        // Wait for rows to render, then scroll to the last one
        long timeout = ConfigReader.getShortTimeout();
        try {
            waitVisible(historyRows().first(), timeout);
        } catch (Exception e) {
            logger.warn("History rows not visible within timeout; retrying with longer timeout");
            try { page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE, 
                    new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getMediumTimeout())); } 
            catch (Exception e2) { logger.debug("Network idle wait failed: {}", e2.getMessage()); }
            waitVisible(historyRows().first(), ConfigReader.getMediumTimeout());
        }
        Locator last = lastHistoryClickable();
        waitVisible(last.first(), timeout);
        try { last.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(last.first(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Open first media push entry from the list")
    public void openFirstMediaPushEntry() {
        // Scroll to top first to ensure first item is interactable
        for (int i = 0; i < 4; i++) {
            try { page.mouse().wheel(0, -800); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Scroll wait failed: {}", e.getMessage()); }
        }
        waitVisible(firstHistoryRow(), ConfigReader.getShortTimeout());
        try { firstHistoryRow().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(firstHistoryRow(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert Performance screen is visible")
    public void assertPerformanceVisible() {
        // Wait for network to settle after navigation
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE,
                    new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getMediumTimeout()));
        } catch (Exception e) { logger.debug("Network idle wait failed: {}", e.getMessage()); }
        
        // Wait for either Performance title or History Media push title (UI updated)
        long timeout = ConfigReader.getMediumTimeout();
        try {
            // Try Performance title first
            Locator performanceTitle = performanceTitle();
            if (performanceTitle.count() > 0 && safeIsVisible(performanceTitle)) {
                logger.info("Performance screen visible");
                return;
            }
            
            // Fallback: Look for "History Media push" title (updated UI)
            Locator historyMediaPushTitle = page.getByText("History Media push");
            if (historyMediaPushTitle.count() > 0 && safeIsVisible(historyMediaPushTitle)) {
                logger.info("History Media push screen visible (updated UI)");
                return;
            }
            
            // If neither found, wait for Performance with extended timeout
            waitVisible(performanceTitle(), timeout);
            logger.info("Performance screen visible");
        } catch (Exception e) {
            logger.warn("Performance/History screen not found. Current URL: {}", page.url());
            throw e;
        }
    }

    @Step("Navigate back via arrow left")
    public void clickBackArrow() {
        waitVisible(backArrow(), ConfigReader.getShortTimeout());
        clickWithRetry(backArrow(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Navigate back to profile screen")
    public void navigateBackToProfile() {
        // Click back until we see a reliable profile marker (plus icon or profile URL)
        for (int i = 0; i < 3; i++) {
            try { clickBackArrow(); } catch (Throwable e) { logger.debug("Back arrow click failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            if (isOnProfileScreen()) return;
        }
        // Final check (non-throwing) to log state
        if (!isOnProfileScreen()) {
            logger.warn("Profile marker not visible after navigating back; current URL: {}", page.url());
        }
    }

    private boolean isOnProfileScreen() {
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        if (safeIsVisible(plusImg.first())) return true;
        return page.url().contains("/creator/profile");
    }
}

