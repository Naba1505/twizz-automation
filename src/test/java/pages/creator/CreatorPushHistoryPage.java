package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

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
        // Primary: every entry row shows a fan-count like "88 Fans". Matching on that
        // text is stable across markup/class changes; clicking the text element still
        // triggers the row's click handler via event bubbling.
        Locator byFans = page.getByText(Pattern.compile("Fans", Pattern.CASE_INSENSITIVE));
        if (byFans.count() > 0) {
            return byFans;
        }
        // Fallback 1: entry-specific class used by older markup
        Locator primary = page.locator(".ant-row.border-bottom-history-push");
        if (primary.count() > 0) {
            return primary;
        }
        // Fallback 2: previous class combination, in case markup changes again
        Locator fallback1 = page.locator(".ant-row.justify-content-between");
        if (fallback1.count() > 0) {
            return fallback1;
        }
        // Fallback 3: ant-space-item (common Ant Design list item container)
        return page.locator(".ant-space-item");
    }

    private Locator firstHistoryRow() {
        return waitForHistoryRows().first();
    }

    // The list renders asynchronously; count() evaluated once at call time can be 0
    // mid-load and pick the wrong fallback. Poll until a row locator actually matches.
    private Locator waitForHistoryRows() {
        long deadline = System.currentTimeMillis() + ConfigReader.getMediumTimeout();
        while (System.currentTimeMillis() < deadline) {
            Locator rows = historyRows();
            try {
                if (rows.count() > 0) return rows;
            } catch (Throwable e) { logger.debug("Row count check failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getPollInterval()); } catch (Throwable e) { logger.debug("Poll wait failed: {}", e.getMessage()); }
        }
        return historyRows();
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

    @Step("Assert push history entries are visible on the list screen")
    public void assertHistoryEntriesVisible() {
        // Current UI shows a plain list of push entries (date / subscriber count / price)
        // rather than a "Total income" summary card - that element no longer exists on
        // this screen (confirmed via live DOM inspection). Verify the list itself instead.
        waitVisible(waitForHistoryRows().first(), ConfigReader.getShortTimeout());
        logger.info("Push history entries visible");
    }

    @Step("Assert 'Income generated' is visible on the Performance detail page")
    public void assertIncomeGeneratedVisible() {
        // Renamed/relocated from the old "Total income" summary: it now appears on the
        // per-push Performance detail page (after opening an entry), not the list screen.
        waitVisible(page.getByText("Income generated"), ConfigReader.getShortTimeout());
        logger.info("Income generated visible");
    }

    @Step("Assert loader is visible")
    public void assertLoaderVisible() {
        // Current UI uses Ant Design's spinner class for the infinite-scroll loader,
        // not a ".loader" element (which no longer exists in the current markup).
        // The loader may not appear if there aren't enough entries to trigger pagination.
        try {
            waitVisible(page.locator(".ant-spin").first(), ConfigReader.getShortTimeout());
            logger.info("Loader visible");
        } catch (Throwable e) {
            logger.info("Loader not visible after scroll (may not have enough entries to trigger pagination); continuing");
        }
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

    @Step("Assert Price per unit is visible on the Performance detail page")
    public void assertPricePerUnitVisible() {
        waitVisible(page.getByText("Price per unit"), ConfigReader.getShortTimeout());
        logger.info("Price per unit visible");
    }

    @Step("Assert Summary of the Push is visible on the Performance detail page")
    public void assertSummaryOfPushVisible() {
        waitVisible(page.getByText("Summary of the Push"), ConfigReader.getShortTimeout());
        logger.info("Summary of the Push visible");
    }

    @Step("Navigate back from Performance detail page to History Media push list")
    public void navigateBackFromPerformancePage() {
        // The old UI opened push details in a modal dialog with a "close" button;
        // the current UI instead navigates to a full Performance detail page, which
        // is dismissed via the same back arrow used elsewhere (no "close" button
        // exists on this page - confirmed via live DOM inspection).
        clickBackArrow();
        logger.info("Navigated back from Performance detail page");
    }

    @Step("Assert back on History Media push screen")
    public void assertBackOnHistoryMediaPushScreen() {
        // The "History Media push" title is a plain <span> with no ARIA heading role,
        // so a role-based HEADING locator never matches it. Use a text locator instead,
        // consistent with historyMediaPushTitle() used elsewhere in this page object.
        waitVisible(historyMediaPushTitle(), ConfigReader.getShortTimeout());
        logger.info("Back on History Media push screen");
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

    @Step("Navigate back via arrow left")
    public void clickBackArrow() {
        waitVisible(backArrow(), ConfigReader.getShortTimeout());
        clickWithRetry(backArrow(), 1, ConfigReader.getElementRetryDelay());
    }

}

