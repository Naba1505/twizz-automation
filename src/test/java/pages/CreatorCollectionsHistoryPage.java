package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page object for Creator -> Settings -> History of collections
 */
public class CreatorCollectionsHistoryPage extends BasePage {
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
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 1, 150);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open 'History of collections' screen")
    public void openHistoryOfCollections() {
        waitVisible(historyOfCollectionsMenu(), DEFAULT_WAIT);
        try { historyOfCollectionsMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(historyOfCollectionsMenu(), 1, 150);
        waitVisible(collectionsTitle(), DEFAULT_WAIT);
    }

    @Step("Open first collection entry")
    public void openFirstCollection() {
        waitVisible(firstCollectionIcon(), DEFAULT_WAIT);
        try { firstCollectionIcon().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(firstCollectionIcon(), 1, 150);
    }

    @Step("Assert Details screen is visible and wait briefly")
    public void assertDetailsVisibleAndWait() {
        waitVisible(detailsTitle(), 30_000);
        try { page.waitForTimeout(500); } catch (Throwable ignored) {}
    }

    @Step("Navigate back to profile (three back clicks)")
    public void navigateBackToProfile() {
        for (int i = 0; i < 3; i++) {
            try {
                waitVisible(backArrow(), DEFAULT_WAIT);
                clickWithRetry(backArrow(), 1, 150);
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
            if (safeIsVisible(profilePlusIcon())) return;
        }
        if (!safeIsVisible(profilePlusIcon())) {
            logger.warn("Profile marker (plus icon) not visible after navigating back from collections history");
        }
    }
}
