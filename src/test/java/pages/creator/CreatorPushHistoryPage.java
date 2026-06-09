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
    // Push History page uses optimized short timeouts for fast-loading UI elements
    private static final int SHORT_TIMEOUT = 2000;       // 2s - Fast UI elements (settings, menus)
    private static final int MEDIUM_TIMEOUT = 8000;      // 8s - History items, performance screen

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
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
    }

    private Locator firstHistoryRow() {
        return page.locator(".ant-row.justify-content-between").first();
    }

    // Provided deep selector for the last item (will try first), with a safer fallback
    private Locator providedLastItemSelector() {
        return page.locator("div:nth-child(50) > .ant-space.css-ixblex.ant-space-horizontal.ant-space-align-center.gap-15 > .ant-space-item > .ant-typography");
    }

    private Locator anyHistoryItems() {
        // Common containers that show items
        return page.locator(".ant-space, .ant-list-item, .ant-row.justify-content-between");
    }

    private Locator lastHistoryClickable() {
        // Try the provided selector first; otherwise click the last of common rows/typographies
        Locator provided = providedLastItemSelector();
        if (provided.count() > 0) return provided;
        Locator rows = page.locator(".ant-row.justify-content-between");
        if (rows.count() > 0) return rows.last();
        Locator typos = page.locator(".ant-typography");
        if (typos.count() > 0) return typos.last();
        return anyHistoryItems().last();
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Push History)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), SHORT_TIMEOUT);
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open 'History of pushes' screen")
    public void openHistoryOfPushes() {
        waitVisible(historyOfPushesMenu(), SHORT_TIMEOUT);
        try { historyOfPushesMenu().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(historyOfPushesMenu(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(historyMediaPushTitle(), SHORT_TIMEOUT);
    }

    @Step("Open last media push entry from the list")
    public void openLastMediaPushEntry() {
        // Nudge the list to ensure items render
        try { anyHistoryItems().first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        // Scroll to bottom by repeated wheel to surface last item
        for (int i = 0; i < 8; i++) {
            try { page.mouse().wheel(0, 800); page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); }
        }
        Locator last = lastHistoryClickable();
        waitVisible(last.first(), SHORT_TIMEOUT);
        try { last.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(last.first(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Open first media push entry from the list")
    public void openFirstMediaPushEntry() {
        // Scroll to top first to ensure first item is interactable
        for (int i = 0; i < 4; i++) { try { page.mouse().wheel(0, -800); page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); } }
        waitVisible(firstHistoryRow(), SHORT_TIMEOUT);
        try { firstHistoryRow().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(firstHistoryRow(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert Performance screen is visible")
    public void assertPerformanceVisible() {
        waitVisible(performanceTitle(), MEDIUM_TIMEOUT);
    }

    @Step("Navigate back via arrow left")
    public void clickBackArrow() {
        waitVisible(backArrow(), SHORT_TIMEOUT);
        clickWithRetry(backArrow(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Navigate back to profile screen")
    public void navigateBackToProfile() {
        // Click back until we see a reliable profile marker (plus icon)
        for (int i = 0; i < 3; i++) {
            try { clickBackArrow(); } catch (Throwable e) { logger.debug("Back arrow click failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
            if (safeIsVisible(plusImg)) {
                return;
            }
        }
        // Final check (non-throwing) to log state
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        if (!safeIsVisible(plusImg)) {
            logger.warn("Profile marker (plus icon) not visible after navigating back");
        }
    }
}

