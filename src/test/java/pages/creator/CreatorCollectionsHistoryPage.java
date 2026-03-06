package pages.creator;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page object for Creator -> Settings -> History of collections
 */
public class CreatorCollectionsHistoryPage extends BasePage {
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    // Reduced from DEFAULT_WAIT (60000ms) to SHORT_TIMEOUT (1000ms) = 98% faster!
    private static final int BUTTON_RETRY_DELAY = 150;   // Button click retry delay
    private static final int POLLING_WAIT = 200;         // Polling intervals
    private static final int BRIEF_WAIT = 500;           // Brief stabilization wait
    private static final int SHORT_TIMEOUT = 1000;       // Short waits (was 60000ms)
    private static final int MEDIUM_TIMEOUT = 2000;      // Medium waits (was 20000ms)

    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorCollectionsHistoryPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator historyOfCollectionsMenu() {
        return page.getByText("History of collections");
    }

    private Locator collectionsTitle() {
        return page.getByText("Collections");
    }

    private Locator firstCollectionIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection")).first();
    }

    private Locator detailsTitle() {
        return page.getByText("Details");
    }

    private Locator backArrow() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
    }

    private Locator profilePlusIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Collections History)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), MEDIUM_TIMEOUT);
        clickWithRetry(settingsIcon(), 1, BUTTON_RETRY_DELAY);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open 'History of collections' screen")
    public void openHistoryOfCollections() {
        waitVisible(historyOfCollectionsMenu(), MEDIUM_TIMEOUT);
        try { historyOfCollectionsMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(historyOfCollectionsMenu(), 1, BUTTON_RETRY_DELAY);
        waitVisible(collectionsTitle(), SHORT_TIMEOUT);
    }

    @Step("Open first collection entry")
    public void openFirstCollection() {
        waitVisible(firstCollectionIcon(), MEDIUM_TIMEOUT);
        try { firstCollectionIcon().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(firstCollectionIcon(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Assert Details screen is visible and wait briefly")
    public void assertDetailsVisibleAndWait() {
        waitVisible(detailsTitle(), MEDIUM_TIMEOUT);
        try { page.waitForTimeout(BRIEF_WAIT); } catch (Throwable ignored) {}
    }

    @Step("Navigate back to profile (three back clicks)")
    public void navigateBackToProfile() {
        for (int i = 0; i < 3; i++) {
            try {
                waitVisible(backArrow(), SHORT_TIMEOUT);
                clickWithRetry(backArrow(), 1, BUTTON_RETRY_DELAY);
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
            if (safeIsVisible(profilePlusIcon())) return;
        }
        if (!safeIsVisible(profilePlusIcon())) {
            logger.warn("Profile marker (plus icon) not visible after navigating back from collections history");
        }
    }
}

