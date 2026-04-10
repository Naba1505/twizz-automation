package pages.creator;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page object for Creator -> Settings -> Unlock history flow
 */
public class CreatorUnlockHistoryPage extends BasePage {
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    // Reduced from DEFAULT_WAIT (60000ms) to SHORT_TIMEOUT (1000ms) = 98% faster!
    private static final int SCROLL_WAIT = 80;           // Scroll stabilization
    private static final int NAVIGATION_WAIT = 100;      // Navigation delays
    private static final int BUTTON_RETRY_DELAY = 150;   // Button click retry delay
    private static final int POLLING_WAIT = 200;         // Polling intervals
    private static final int SHORT_TIMEOUT = 1000;       // Short waits (was 60000ms)
    private static final int MEDIUM_TIMEOUT = 5000;      // Medium waits - increased for list loading stability

    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorUnlockHistoryPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator unlockHistoryMenu() {
        return page.getByText("Unlock history");
    }

    private Locator unlockLinksTitle() {
        return page.getByText("Unlock links");
    }

    private Locator detailsTitle() {
        return page.getByText("Details");
    }

    private Locator backArrow() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
    }

    private Locator firstUnlockRow() {
        return page.locator(".font-12-regular").first();
    }

    // Provided deep selector for last item with safe fallbacks
    private Locator providedLastUnlockSelector() {
        return page.locator("div:nth-child(12) > div > .ant-row.w-full > .ant-col > div:nth-child(3) > .ant-typography > .font-12-regular");
    }

    private Locator anyUnlockItems() {
        return page.locator(".font-12-regular, .ant-typography, .ant-row.w-full");
    }

    private Locator lastUnlockClickable() {
        Locator provided = providedLastUnlockSelector();
        if (provided.count() > 0) return provided;
        Locator items = page.locator(".font-12-regular");
        if (items.count() > 0) return items.last();
        Locator typos = page.locator(".ant-typography");
        if (typos.count() > 0) return typos.last();
        return anyUnlockItems().last();
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Unlock History)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), SHORT_TIMEOUT);
        clickWithRetry(settingsIcon(), 1, BUTTON_RETRY_DELAY);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open 'Unlock history' screen")
    public void openUnlockHistory() {
        waitVisible(unlockHistoryMenu(), SHORT_TIMEOUT);
        try { unlockHistoryMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(unlockHistoryMenu(), 1, BUTTON_RETRY_DELAY);
        waitVisible(unlockLinksTitle(), SHORT_TIMEOUT);
    }

    @Step("Open last unlock entry from the list")
    public void openLastUnlockEntry() {
        // Nudge list and scroll to bottom to surface last item
        try { anyUnlockItems().first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        for (int i = 0; i < 8; i++) { try { page.mouse().wheel(0, 800); page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {} }
        Locator last = lastUnlockClickable();
        waitVisible(last.first(), SHORT_TIMEOUT);
        try { last.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(last.first(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Open first unlock entry from the list")
    public void openFirstUnlockEntry() {
        for (int i = 0; i < 4; i++) { try { page.mouse().wheel(0, -800); page.waitForTimeout(SCROLL_WAIT); } catch (Throwable ignored) {} }
        waitVisible(firstUnlockRow(), MEDIUM_TIMEOUT);
        try { firstUnlockRow().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(firstUnlockRow(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Assert Details screen is visible")
    public void assertDetailsVisible() {
        waitVisible(detailsTitle(), MEDIUM_TIMEOUT);
    }

    @Step("Navigate back via arrow left")
    public void clickBackArrow() {
        waitVisible(backArrow(), SHORT_TIMEOUT);
        clickWithRetry(backArrow(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Navigate back to profile screen")
    public void navigateBackToProfile() {
        for (int i = 0; i < 5; i++) {
            try { clickBackArrow(); } catch (Throwable ignored) {}
            try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
            Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
            if (safeIsVisible(plusImg)) {
                return;
            }
        }
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        if (!safeIsVisible(plusImg)) {
            logger.warn("Profile marker (plus icon) not visible after navigating back from unlock history");
        }
    }
}

