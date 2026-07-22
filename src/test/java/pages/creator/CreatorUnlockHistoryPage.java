package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

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

    private Locator unlockRows() {
        return page.locator(".font-12-regular");
    }

    private Locator firstUnlockRow() {
        return unlockRows().first();
    }

    private Locator lastUnlockClickable() {
        Locator rows = unlockRows();
        if (rows.count() > 0) return rows.last();
        // Fallback: any clickable text inside the unlock list
        Locator typos = page.locator(".ant-typography");
        if (typos.count() > 0) return typos.last();
        return page.locator(".ant-space-item").last();
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Unlock History)")
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

    @Step("Open 'Unlock history' screen")
    public void openUnlockHistory() {
        waitVisible(unlockHistoryMenu(), ConfigReader.getShortTimeout());
        try { unlockHistoryMenu().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(unlockHistoryMenu(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(unlockLinksTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Open last unlock entry from the list")
    public void openLastUnlockEntry() {
        // Wait for rows to render, then click the last one
        waitVisible(unlockRows().first(), ConfigReader.getShortTimeout());
        Locator last = lastUnlockClickable();
        waitVisible(last.first(), ConfigReader.getShortTimeout());
        try { last.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(last.first(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Open first unlock entry from the list")
    public void openFirstUnlockEntry() {
        for (int i = 0; i < 4; i++) {
            try { page.mouse().wheel(0, -800); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Scroll wait failed: {}", e.getMessage()); }
        }
        waitVisible(firstUnlockRow(), ConfigReader.getShortTimeout());
        try { firstUnlockRow().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(firstUnlockRow(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert Details screen is visible")
    public void assertDetailsVisible() {
        waitVisible(detailsTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Navigate back via arrow left")
    public void clickBackArrow() {
        waitVisible(backArrow(), ConfigReader.getShortTimeout());
        clickWithRetry(backArrow(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Navigate back to profile screen")
    public void navigateBackToProfile() {
        for (int i = 0; i < 5; i++) {
            try { clickBackArrow(); } catch (Throwable e) { logger.debug("Back arrow click failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            if (isOnProfileScreen()) return;
        }
        if (!isOnProfileScreen()) {
            logger.warn("Profile marker not visible after navigating back from unlock history; current URL: {}", page.url());
        }
    }

    private boolean isOnProfileScreen() {
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        if (safeIsVisible(plusImg.first())) return true;
        return page.url().contains("/creator/profile");
    }
}

