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
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
    }

    private Locator historyRows() {
        return page.locator(".ant-row.justify-content-between");
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

    @Step("Open last media push entry from the list")
    public void openLastMediaPushEntry() {
        // Wait for rows to render, then scroll to the last one
        waitVisible(historyRows().first(), ConfigReader.getShortTimeout());
        Locator last = lastHistoryClickable();
        waitVisible(last.first(), ConfigReader.getShortTimeout());
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
        waitVisible(performanceTitle(), ConfigReader.getShortTimeout());
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

