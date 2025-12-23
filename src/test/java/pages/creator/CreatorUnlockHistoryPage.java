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
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 1, 150);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open 'Unlock history' screen")
    public void openUnlockHistory() {
        waitVisible(unlockHistoryMenu(), DEFAULT_WAIT);
        try { unlockHistoryMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(unlockHistoryMenu(), 1, 150);
        waitVisible(unlockLinksTitle(), DEFAULT_WAIT);
    }

    @Step("Open last unlock entry from the list")
    public void openLastUnlockEntry() {
        // Nudge list and scroll to bottom to surface last item
        try { anyUnlockItems().first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        for (int i = 0; i < 8; i++) { try { page.mouse().wheel(0, 800); page.waitForTimeout(120); } catch (Throwable ignored) {} }
        Locator last = lastUnlockClickable();
        waitVisible(last.first(), DEFAULT_WAIT);
        try { last.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(last.first(), 1, 150);
    }

    @Step("Open first unlock entry from the list")
    public void openFirstUnlockEntry() {
        for (int i = 0; i < 4; i++) { try { page.mouse().wheel(0, -800); page.waitForTimeout(80); } catch (Throwable ignored) {} }
        waitVisible(firstUnlockRow(), DEFAULT_WAIT);
        try { firstUnlockRow().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(firstUnlockRow(), 1, 150);
    }

    @Step("Assert Details screen is visible")
    public void assertDetailsVisible() {
        waitVisible(detailsTitle(), 30_000);
    }

    @Step("Navigate back via arrow left")
    public void clickBackArrow() {
        waitVisible(backArrow(), DEFAULT_WAIT);
        clickWithRetry(backArrow(), 1, 150);
    }

    @Step("Navigate back to profile screen")
    public void navigateBackToProfile() {
        for (int i = 0; i < 5; i++) {
            try { clickBackArrow(); } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
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

