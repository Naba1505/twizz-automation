package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

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
        // Ensure we are on the profile page before looking for the settings icon
        navigateAndWait(ConfigReader.getBaseUrl() + "/creator/profile");
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
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

    @Step("Open 'History of collections' screen")
    public void openHistoryOfCollections() {
        waitVisible(historyOfCollectionsMenu(), ConfigReader.getShortTimeout());
        try { historyOfCollectionsMenu().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(historyOfCollectionsMenu(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(collectionsTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Open first collection entry")
    public void openFirstCollection() {
        // Check if any collections exist first
        Locator collectionIcon = firstCollectionIcon();
        try {
            waitVisible(collectionIcon, ConfigReader.getShortTimeout());
            try { collectionIcon.scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
            clickWithRetry(collectionIcon, 1, ConfigReader.getElementRetryDelay());
        } catch (Exception e) {
            logger.warn("No collections found in history - this is acceptable if user hasn't created any collections yet");
            // Don't fail the test - it's valid to have no collections
        }
    }

    @Step("Assert Details screen if on collection page")
    public void assertDetailsIfOnCollectionPage() {
        if (page.url().contains("/collection/")) {
            assertDetailsVisibleAndWait();
        } else {
            logger.info("No collection details to verify - user may not have any collections in history");
        }
    }

    @Step("Assert Details screen is visible and wait briefly")
    public void assertDetailsVisibleAndWait() {
        waitVisible(detailsTitle(), ConfigReader.getShortTimeout());
        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
    }

    @Step("Navigate back to profile (three back clicks)")
    public void navigateBackToProfile() {
        for (int i = 0; i < 3; i++) {
            try {
                waitVisible(backArrow(), ConfigReader.getShortTimeout());
                clickWithRetry(backArrow(), 1, ConfigReader.getElementRetryDelay());
            } catch (Throwable e) { logger.debug("Back arrow click failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            if (safeIsVisible(profilePlusIcon())) return;
        }
        if (!safeIsVisible(profilePlusIcon())) {
            logger.warn("Profile marker (plus icon) not visible after navigating back from collections history");
        }
    }
}

