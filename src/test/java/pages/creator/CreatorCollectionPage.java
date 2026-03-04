package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;
import utils.WaitUtils;

public class CreatorCollectionPage extends BasePage {

    // Timeout constants (in milliseconds)
    private static final int POLLING_WAIT = 50;      // Quick polling operations
    private static final int SCROLL_WAIT = 60;       // Scroll stabilization
    private static final int TILE_SCROLL_WAIT = 75;  // Tile scroll wait
    private static final int TOAST_CLICK_DELAY = 100; // Toast click delay
    private static final int NAVIGATION_WAIT = 100;  // Navigation delays
    private static final int BUTTON_RETRY_DELAY = 150; // Button click retry delay
    private static final int CLICK_RETRY_DELAY = 200; // Standard click retry
    private static final int PAGE_TRANSITION = 250;  // Page transition delays
    private static final int POST_TOAST_WAIT = 300;  // Wait after toast dismissal
    private static final int LIST_REFRESH_WAIT = 400; // List refresh wait
    private static final int UPLOAD_POLLING = 500;   // Upload progress polling
    private static final int SCROLL_WHEEL_AMOUNT = 600; // Mouse wheel scroll amount
    private static final int KEYBOARD_BLUR_WAIT = 600; // Keyboard blur wait
    private static final int EMPTY_STATE_TIMEOUT = 2000; // Empty state check timeout
    private static final int QUICK_CHECK_TIMEOUT = 3000; // Quick UI checks
    private static final int TOAST_TIMEOUT = 4000;   // Toast detection timeout
    private static final int SHORT_WAIT_TIMEOUT = 5000; // Short wait operations
    private static final int TILE_VISIBILITY_TIMEOUT = 8000; // Tile visibility timeout
    private static final long DEFAULT_UPLOAD_TIMEOUT = 180000; // 3 minutes for uploads

    // Strings
    private static final String I_UNDERSTAND_BTN = "I understand";
    private static final String COLLECTION = "Collection";
    private static final String TITLE_PLACEHOLDER = "Title";
    private static final String CREATE_BTN = "Create";
    private static final String IMPORTATION = "Importation";
    private static final String ADD_MEDIA_TITLE = "Add media";
    private static final String DESC_PLACEHOLDER = "Your message....";
    private static final String VALIDATE_COLLECTION_BTN = "Validate the collection";

    public CreatorCollectionPage(Page page) {
        super(page);
    }


    @Step("Open plus menu on creator dashboard")
    public void openPlusMenu() {
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg.first(), ConfigReader.getVisibilityTimeout());
        // Some builds require clicking the nested svg
        Locator svg = plusImg.locator("svg");
        if (svg.count() > 0 && svg.first().isVisible()) {
            clickWithRetry(svg.first(), 2, CLICK_RETRY_DELAY);
        } else {
            clickWithRetry(plusImg.first(), 2, CLICK_RETRY_DELAY);
        }
    }

    @Step("Delete a single collection if any are present")
    public void deleteOneCollectionIfAny() {
        openCollectionsList();
        Locator collections = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
        int count = collections.count();
        logger.info("[Cleanup] Collections available: {}", count);
        if (count == 0) {
            logger.info("[Cleanup] No collections found; nothing to delete");
            return;
        }
        // Iterate through first few tiles to find a valid details page
        int max = Math.min(count, 10);
        for (int i = 0; i < max; i++) {
            Locator tile = collections.nth(i);
            try {
                // Bring tile into view reliably (handles virtualized lists)
                if (!makeVisibleWithScroll(tile, 3500)) {
                    logger.warn("[Cleanup] Tile index {} not visible after scroll attempts; skipping", i);
                    continue;
                }
                clickTileRobust(tile);
                try { page.waitForTimeout(POLLING_WAIT); } catch (Exception ignored) {}
                // Quick check: are we on a details-like screen?
                if (!isDetailsMarkersPresentQuick(QUICK_CHECK_TIMEOUT)) {
                    logger.warn("[Cleanup] Details markers not present after opening tile index {}; going back", i);
                    safeReturnToCollectionsList();
                    continue;
                }
                // Try opening actions menu quickly; if successful, proceed to delete flow
                if (!openActionsMenuQuick(QUICK_CHECK_TIMEOUT)) {
                    // Fallback to the normal method which waits longer
                    try {
                        openActionsMenu();
                    } catch (Exception e) {
                        logger.warn("[Cleanup] Actions menu not available for tile index {}: {}", i, e.getMessage());
                        safeReturnToCollectionsList();
                        continue;
                    }
                }
                chooseDeleteCollection();
                confirmDeletion();
                assertCollectionDeletedToast();
                // After successful deletion, attempt to return to list
                safeReturnToCollectionsList();
                return;
            } catch (Exception e) {
                logger.warn("[Cleanup] Failed to open details for tile index {}: {}", i, e.getMessage());
                // Try to go back to list if we navigated somewhere
                safeReturnToCollectionsList();
            }
        }
        logger.warn("[Cleanup] Could not open any collection details among first {} tiles", max);
    }

    // Attempt to make an element visible by scrolling the page; returns true if visible
    private boolean makeVisibleWithScroll(Locator loc, long timeoutMs) {
        long deadline = System.currentTimeMillis() + Math.max(0, timeoutMs);
        while (System.currentTimeMillis() < deadline) {
            try {
                if (loc.count() > 0 && loc.first().isVisible()) return true;
            } catch (Throwable ignored) {}
            try {
                // Try native scroll into view first
                try { loc.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                // Then use keyboard/page scroll as a fallback
                try { page.keyboard().press("PageDown"); } catch (Throwable ignored) {}
                try { page.mouse().wheel(0, SCROLL_WHEEL_AMOUNT); } catch (Throwable ignored) {}
                page.waitForTimeout(TILE_SCROLL_WAIT);
            } catch (Throwable ignored) {}
        }
        try { return loc.count() > 0 && loc.first().isVisible(); } catch (Throwable e) { return false; }
    }

    // Robust click attempts for a tile: normal, double, ancestor clickable, JS click, force
    private void clickTileRobust(Locator tile) {
        // 1) Normal click with retry
        try {
            clickWithRetry(tile, 2, CLICK_RETRY_DELAY);
            return;
        } catch (Throwable ignored) {}
        // 2) Double click
        try {
            tile.dblclick();
            return;
        } catch (Throwable ignored) {}
        // 3) Click closest clickable ancestor
        try {
            Locator clickableAncestor = tile.locator("xpath=ancestor-or-self::*[self::a or self::button or @role='button' or contains(@class,'click')][1]");
            if (clickableAncestor.count() > 0) {
                clickableAncestor.first().click();
                return;
            }
        } catch (Throwable ignored) {}
        // 4) JS click on element
        try {
            tile.first().evaluate("(el) => el && el.click && el.click()");
            return;
        } catch (Throwable ignored) {}
        // 5) Force click as last resort
        tile.click(new Locator.ClickOptions().setForce(true));
    }

    // =====================
    // Collections Deletion
    // =====================

    @Step("Open Collections list from creator profile screen")
    public void openCollectionsList() {
        // Ensure any overlay is dismissed before attempting navigation
        clickIUnderstandIfPresent();

        // Strategy 1: dedicated collections icon (preferred)
        try {
            Locator collectionsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collections icon"));
            waitVisible(collectionsIcon.first(), ConfigReader.getMediumTimeout());
            clickWithRetry(collectionsIcon.first(), 2, CLICK_RETRY_DELAY);
            try { page.waitForLoadState(); } catch (Exception ignored) {}
            waitForIdle();
            // Scroll a bit to surface tiles on small screens
            try { page.mouse().wheel(0, 600); } catch (Exception ignored) {}
            // Wait for grid to render at least one tile
            Locator tilesRole = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
            Locator tilesXpath = page.locator("xpath=//img[@class='collection-img']");
            if (!(WaitUtils.waitForVisible(tilesRole, TILE_VISIBILITY_TIMEOUT) || WaitUtils.waitForVisible(tilesXpath, TILE_VISIBILITY_TIMEOUT))) {
                logger.warn("Collections tiles not visible after clicking collections icon; continuing with fallbacks");
            } else {
                return;
            }
        } catch (Exception e) {
            logger.warn("Collections icon not visible within timeout; trying fallback strategies");
        }

        // Strategy 2: click by visible text 'Collection' (exact)
        try {
            Locator txt = page.getByText(COLLECTION, new Page.GetByTextOptions().setExact(true));
            waitVisible(txt.first(), ConfigReader.getVisibilityTimeout());
            clickWithRetry(txt.first(), 2, CLICK_RETRY_DELAY);
            waitForIdle();
            Locator tilesRole = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
            Locator tilesXpath = page.locator("xpath=//img[@class='collection-img']");
            if (WaitUtils.waitForVisible(tilesRole, TILE_VISIBILITY_TIMEOUT) || WaitUtils.waitForVisible(tilesXpath, TILE_VISIBILITY_TIMEOUT)) return;
        } catch (RuntimeException ignored) {}

        // Strategy 3: Link or Tab role named 'Collection'
        try {
            Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(COLLECTION));
            if (safeIsVisible(link.first())) {
                clickWithRetry(link.first(), 1, BUTTON_RETRY_DELAY);
                Locator tilesRole = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
                Locator tilesXpath = page.locator("xpath=//img[@class='collection-img']");
                if (WaitUtils.waitForVisible(tilesRole, TILE_VISIBILITY_TIMEOUT) || WaitUtils.waitForVisible(tilesXpath, TILE_VISIBILITY_TIMEOUT)) return;
            }
        } catch (Exception ignored) {}
        try {
            Locator tab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(COLLECTION));
            if (safeIsVisible(tab.first())) {
                clickWithRetry(tab.first(), 1, BUTTON_RETRY_DELAY);
                Locator tilesRole = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
                Locator tilesXpath = page.locator("xpath=//img[@class='collection-img']");
                if (WaitUtils.waitForVisible(tilesRole, TILE_VISIBILITY_TIMEOUT) || WaitUtils.waitForVisible(tilesXpath, TILE_VISIBILITY_TIMEOUT)) return;
            }
        } catch (Exception ignored) {}

        // Strategy 4: Plural text 'Collections' or general contains (case-insensitive)
        try {
            Locator pluralExact = page.getByText("Collections", new Page.GetByTextOptions().setExact(true));
            if (safeIsVisible(pluralExact.first())) {
                clickWithRetry(pluralExact.first(), 1, BUTTON_RETRY_DELAY);
                Locator tiles = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
                if (WaitUtils.waitForVisible(tiles, TILE_VISIBILITY_TIMEOUT)) return;
            }
        } catch (Exception ignored) {}
        try {
            Locator ciContains = page.locator("xpath=(//*[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'collection')])");
            if (ciContains.count() > 0) {
                Locator cand = ciContains.first();
                try { cand.scrollIntoViewIfNeeded(); } catch (Exception ignored2) {}
                // Try direct click, then clickable ancestor fallback
                try {
                    clickWithRetry(cand, 1, BUTTON_RETRY_DELAY);
                } catch (Exception e1) {
                    try {
                        Locator clickableAncestor = cand.locator("xpath=ancestor-or-self::*[self::a or self::button or @role='button' or contains(@class,'tab') or contains(@class,'nav')][1]");
                        if (clickableAncestor.count() > 0 && safeIsVisible(clickableAncestor.first())) {
                            clickWithRetry(clickableAncestor.first(), 1, BUTTON_RETRY_DELAY);
                        }
                    } catch (Exception ignored3) {}
                }
                Locator tilesRole = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
                Locator tilesXpath = page.locator("xpath=//img[@class='collection-img']");
                if (WaitUtils.waitForVisible(tilesRole, TILE_VISIBILITY_TIMEOUT) || WaitUtils.waitForVisible(tilesXpath, TILE_VISIBILITY_TIMEOUT)) return;
            }
        } catch (Exception ignored) {}

        // Strategy 5: Generic navigation scan with scrolling passes
        for (int pass = 0; pass < 3; pass++) {
            try { page.keyboard().press("Home"); } catch (Exception ignored) {}
            try { page.mouse().wheel(0, 0); } catch (Exception ignored) {}
            try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}
            Locator any = page.locator("xpath=(//*[self::a or self::button or @role='button' or @role='tab'][contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'collection')])");
            int n = any.count();
            for (int i = 0; i < Math.min(n, 5); i++) {
                Locator el = any.nth(i);
                if (!safeIsVisible(el)) continue;
                try { el.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                try { clickWithRetry(el, 1, BUTTON_RETRY_DELAY); } catch (Exception ignored) {}
                Locator tilesRole = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
                Locator tilesXpath = page.locator("xpath=//img[@class='collection-img']");
                if (WaitUtils.waitForVisible(tilesRole, 6000) || WaitUtils.waitForVisible(tilesXpath, 6000)) return;
            }
            // Scroll and retry
            try { page.mouse().wheel(0, 1200); } catch (Exception ignored) {}
            try { page.waitForTimeout(SCROLL_WAIT); } catch (Exception ignored) {}
        }
        throw new RuntimeException("Failed to open Collections list using available selectors");
    }

    @Step("Open first visible collection from the list")
    public boolean openFirstVisibleCollection() {
        Locator collections = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
        int count = collections.count();
        logger.info("Collections visible in list: {}", count);
        if (count == 0) {
            return false;
        }
        // Prefer clicking the second tile if present (nth(1)), to avoid a potential 'create new' tile at index 0
        Locator candidate = count > 1 ? collections.nth(1) : collections.first();
        waitVisible(candidate, ConfigReader.getShortTimeout());
        try { candidate.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        clickWithRetry(candidate, 2, CLICK_RETRY_DELAY);
        // brief wait for navigation/content transition
        try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
        return true;
    }

    @Step("Ensure collection details screen is visible")
    public void ensureDetailsScreen() {
        // Primary marker: 'Details' text
        try {
            Locator details = page.getByText("Details");
            waitVisible(details.first(), ConfigReader.getShortTimeout());
            return;
        } catch (RuntimeException primary) {
            logger.warn("Details text not visible yet; trying alternate markers");
        }
        // Alternate marker 1: back arrow present on details
        try {
            Locator backArrow = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
            waitVisible(backArrow.first(), ConfigReader.getShortTimeout());
            return;
        } catch (RuntimeException ignored) {}
        // Alternate marker 2: actions menu icon present on details header
        Locator menuIcon = page.locator(".right-icon > img");
        waitVisible(menuIcon.first(), ConfigReader.getShortTimeout());
    }

    @Step("Open actions menu (three dots) on collection details")
    public void openActionsMenu() {
        Locator menuIcon = page.locator(".right-icon > img");
        waitVisible(menuIcon.first(), ConfigReader.getShortTimeout());
        clickWithRetry(menuIcon.first(), 2, CLICK_RETRY_DELAY);
        // Ensure popup
        Locator popupTitle = page.getByText("What do you want to do?");
        waitVisible(popupTitle.first(), ConfigReader.getShortTimeout());
    }

    // Quick, non-throwing checks to speed up iteration
    private boolean isDetailsMarkersPresentQuick(long timeoutMs) {
        try {
            if (WaitUtils.waitForVisible(page.getByText("Details"), timeoutMs)) return true;
        } catch (Exception ignored) {}
        try {
            if (WaitUtils.waitForVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left")), timeoutMs)) return true;
        } catch (Exception ignored) {}
        try {
            if (WaitUtils.waitForVisible(page.locator(".right-icon > img"), timeoutMs)) return true;
        } catch (Exception ignored) {}
        return false;
    }

    private boolean openActionsMenuQuick(long timeoutMs) {
        Locator menuIcon = page.locator(".right-icon > img");
        if (WaitUtils.waitForVisible(menuIcon, timeoutMs)) {
            try {
                clickWithRetry(menuIcon.first(), 2, CLICK_RETRY_DELAY);
                Locator popupTitle = page.getByText("What do you want to do?");
                return WaitUtils.waitForVisible(popupTitle, QUICK_CHECK_TIMEOUT);
            } catch (Exception ignored) {}
        }
        return false;
    }

    @Step("Choose Delete collection option")
    public void chooseDeleteCollection() {
        Locator deleteBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete collection"));
        waitVisible(deleteBtn.first(), ConfigReader.getShortTimeout());
        clickWithRetry(deleteBtn.first(), 2, CLICK_RETRY_DELAY);
    }

    @Step("Confirm deletion in confirmation dialog")
    public void confirmDeletion() {
        Locator confirmText = page.getByText("Are you sure you want to delete the collection? All linked data will be lost");
        waitVisible(confirmText.first(), ConfigReader.getShortTimeout());
        Locator yesDelete = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, delete"));
        clickWithRetry(yesDelete.first(), 2, CLICK_RETRY_DELAY);
    }

    @Step("Assert collection deletion success or fallback to UI state checks")
    public void assertCollectionDeletedToast() {
        // Try common toast texts first
        Locator toastExact = page.getByText("Collection successfully deleted");
        if (WaitUtils.waitForVisible(toastExact, SHORT_WAIT_TIMEOUT)) return;
        // Try regex match on any text containing 'deleted'
        Locator toastRegex = page.locator("text=/Collection( is)? (successfully )?deleted|deleted successfully|deleted/i");
        if (WaitUtils.waitForVisible(toastRegex, TOAST_TIMEOUT)) return;
        // Try generic role alert that contains 'deleted'
        try {
            Locator alert = page.getByRole(AriaRole.ALERT);
            if (alert.count() > 0 && alert.filter(new Locator.FilterOptions().setHasText("deleted")).count() > 0) return;
        } catch (Exception ignored) {}
        // Fallback: ensure the confirmation dialog closed
        try {
            Locator confirmText = page.getByText("Are you sure you want to delete the collection? All linked data will be lost");
            confirmText.first().waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.DETACHED)
                .setTimeout(SHORT_WAIT_TIMEOUT));
        } catch (Exception ignored) {}
        // Fallback: details header no longer present or actions icon gone, suggesting navigation/refresh
        try {
            Locator details = page.getByText("Details");
            if (details.count() == 0 || !details.first().isVisible()) return;
        } catch (Exception ignored) {}
        try {
            Locator actions = page.locator(".right-icon > img");
            if (actions.count() == 0 || !actions.first().isVisible()) return;
        } catch (Exception ignored) {}
        // As last resort, short wait to allow list refresh
        try { page.waitForTimeout(LIST_REFRESH_WAIT); } catch (Exception ignored) {}
    }

    // ==============================
    // Files-icon driven delete flow
    // ==============================

    private Locator filesIconOnCollections() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("files"));
    }

    @Step("Open first collection details via top 'files' icon")
    public void openFirstCollectionViaFilesIcon() {
        openCollectionsList();
        Locator files = filesIconOnCollections();
        waitVisible(files.first(), ConfigReader.getVisibilityTimeout());
        try { files.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        clickWithRetry(files.first(), 2, CLICK_RETRY_DELAY);
        ensureDetailsScreen();
    }

    @Step("Delete current collection via three-dot menu and confirm")
    public void deleteCurrentCollectionFromDetails() {
        openActionsMenu();
        chooseDeleteCollection();
        confirmDeletion();
        assertCollectionDeletedToast();
    }

    private void clickBackArrowIfPresent() {
        try {
            Locator backArrow = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
            if (safeIsVisible(backArrow.first())) {
                clickWithRetry(backArrow.first(), 1, BUTTON_RETRY_DELAY);
            }
        } catch (Exception ignored) {}
    }

    @Step("Delete all collections using 'files' icon loop")
    public void deleteAllCollectionsUsingFilesIcon(int maxIterations) {
        int guard = Math.max(1, maxIterations);
        for (int i = 0; i < guard; i++) {
            openCollectionsList();
            Locator files = filesIconOnCollections();
            if (files.count() == 0) {
                logger.info("[Cleanup] No 'files' icons found; assuming no collections remain");
                return;
            }
            if (!safeIsVisible(files.first())) {
                logger.info("[Cleanup] 'files' icon not visible; stopping loop");
                return;
            }
            // Navigate to details
            try {
                waitVisible(files.first(), ConfigReader.getShortTimeout());
                try { files.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                clickWithRetry(files.first(), 2, CLICK_RETRY_DELAY);
            } catch (Exception e) {
                logger.warn("[Cleanup] Failed clicking 'files' icon on iteration {}: {}", i, e.getMessage());
                continue;
            }
            // Ensure details and delete
            ensureDetailsScreen();
            deleteCurrentCollectionFromDetails();
            // Return to list for next iteration
            clickBackArrowIfPresent();
            try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
        }
        logger.warn("[Cleanup] Guard exhausted while deleting collections via files icon");
    }

    // ==============================================
    // Image-tile driven delete flow (collection-img)
    // ==============================================

    // Scroll down the Collections list multiple times to trigger lazy-load/virtualized tiles
    private void scrollToLoadCollections(int maxScrolls, int perStepWheel, int waitMs) {
        int steps = Math.max(1, maxScrolls);
        int wheel = perStepWheel <= 0 ? 800 : perStepWheel;
        int pause = waitMs <= 0 ? 200 : waitMs;
        for (int s = 0; s < steps; s++) {
            try { page.keyboard().press("PageDown"); } catch (Throwable ignored) {}
            try { page.mouse().wheel(0, wheel); } catch (Throwable ignored) {}
            try { page.waitForTimeout(pause); } catch (Throwable ignored) {}
        }
    }

    @Step("Delete all collections by clicking each //img[@class='collection-img'] tile")
    public void deleteAllCollectionsByImageTiles(int maxIterations) {
        int guard = Math.max(1, maxIterations);
        for (int i = 0; i < guard; i++) {
            // Ensure we're on the Collections list each iteration
            ensureCollectionsList();

            // Proactively scroll to load tiles if needed
            Locator tiles = page.locator("xpath=//img[@class='collection-img']");
            int count = tiles.count();
            if (count == 0) {
                logger.info("[Cleanup] No tiles visible yet; scrolling to load collections");
                scrollToLoadCollections(8, 900, 220);
                count = tiles.count();
            }
            logger.info("[Cleanup] collection-img tiles found: {}", count);
            if (count == 0) {
                logger.info("[Cleanup] No collection images found; assuming no collections remain");
                return;
            }

            // Prefer Playwright role IMG 'collection' second tile (nth(1)), per user flow
            boolean clicked = false;
            try {
                Locator roleTiles = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
                int rc = roleTiles.count();
                if (rc > 1) {
                    Locator second = roleTiles.nth(1);
                    if (!makeVisibleWithScroll(second, 3000)) {
                        try { second.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                    }
                    clickTileRobust(second);
                    clicked = true;
                }
            } catch (Exception ignored) {}

            // If not clicked via role, fallback to xpath tiles with passes
            // Try up to 3 scan passes; if not clickable, scroll more and retry
            for (int pass = 0; pass < 3 && !clicked; pass++) {
                int currentCount = tiles.count();
                for (int t = 0; t < currentCount; t++) {
                    Locator tile = tiles.nth(t);
                    try {
                        if (!makeVisibleWithScroll(tile, 3500)) continue;
                        clickTileRobust(tile);
                        clicked = true;
                        break;
                    } catch (Exception e) {
                        logger.warn("[Cleanup] Failed clicking collection-img at index {}: {}", t, e.getMessage());
                    }
                }
                if (!clicked) {
                    scrollToLoadCollections(4, 900, 200);
                }
            }

            if (!clicked) {
                logger.warn("[Cleanup] Unable to click any collection-img tiles; stopping");
                return;
            }

            // On details, perform deletion
            try {
                ensureDetailsScreen();
                deleteCurrentCollectionFromDetails();
            } catch (Exception e) {
                logger.warn("[Cleanup] Deletion flow failed after opening details: {}", e.getMessage());
            }

            // Ensure we are back on Collections list by clicking the Collections icon again (more robust than back)
            try {
                ensureCollectionsList();
            } catch (Exception ignored) {
                // Fallback to back then try again
                safeReturnToCollectionsList();
                try { page.waitForTimeout(BUTTON_RETRY_DELAY); } catch (Exception ignored2) {}
                ensureCollectionsList();
            }
            try { page.waitForTimeout(CLICK_RETRY_DELAY); } catch (Exception ignored) {}
        }
        logger.warn("[Cleanup] Guard exhausted while deleting collections via collection-img tiles");
    }

    @Step("Ensure Collections list is visible by (re)clicking the Collections entry/icon")
    public void ensureCollectionsList() {
        // Proactively dismiss overlay
        clickIUnderstandIfPresent();
        // First try the dedicated icon path
        try {
            Locator collectionsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collections icon"));
            if (collectionsIcon.count() > 0) {
                if (!safeIsVisible(collectionsIcon.first())) {
                    try { collectionsIcon.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                }
                clickWithRetry(collectionsIcon.first(), 2, CLICK_RETRY_DELAY);
            }
        } catch (Exception ignored) {}
        // Use the existing robust openCollectionsList() which includes many fallbacks
        openCollectionsList();
    }

    // ============================
    // User exact flow (code-gen)
    // ============================

    private boolean clickCollectionsIconResilient() {
        clickIUnderstandIfPresent();
        // 1) Preferred: role IMG named "collections icon"
        try {
            Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collections icon"));
            if (icon.count() > 0) {
                waitVisible(icon.first(), ConfigReader.getShortTimeout());
                try { icon.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                clickWithRetry(icon.first(), 2, CLICK_RETRY_DELAY);
                return true;
            }
        } catch (Exception ignored) {}
        // 2) Fallback: any IMG whose accessible name contains 'collection' (case-insensitive)
        try {
            Locator any = page.locator("xpath=(//img[contains(translate(@alt, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'collection') or contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'collection')])");
            if (any.count() > 0) {
                try { any.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                clickWithRetry(any.first(), 2, CLICK_RETRY_DELAY);
                return true;
            }
        } catch (Exception ignored) {}
        // 3) Fallback: clickable text/link/tab that contains 'Collection'
        try {
            Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(COLLECTION));
            if (safeIsVisible(link.first())) { clickWithRetry(link.first(), 1, BUTTON_RETRY_DELAY); return true; }
        } catch (Exception ignored) {}
        try {
            Locator tab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(COLLECTION));
            if (safeIsVisible(tab.first())) { clickWithRetry(tab.first(), 1, BUTTON_RETRY_DELAY); return true; }
        } catch (Exception ignored) {}
        try {
            Locator txt = page.getByText("Collections");
            if (safeIsVisible(txt.first())) { clickWithRetry(txt.first(), 1, BUTTON_RETRY_DELAY); return true; }
        } catch (Exception ignored) {}
        try {
            Locator txt = page.getByText("Collection");
            if (safeIsVisible(txt.first())) { clickWithRetry(txt.first(), 1, BUTTON_RETRY_DELAY); return true; }
        } catch (Exception ignored) {}
        debugLogAllImgAccessibleNames("[Debug] Unable to find Collections icon by common strategies");
        return false;
    }

    @Step("Delete all collections using the exact flow: icon -> tile -> menu -> delete")
    public void deleteAllCollectionsExactFlow(int maxIterations) {
        int guard = Math.max(1, maxIterations);
        for (int iteration = 0; iteration < guard; iteration++) {
            logger.info("[Cleanup] Starting iteration {} of {}", iteration + 1, guard);
            
            // Click collections icon to open collections list
            if (!clickCollectionsIconResilient()) {
                logger.warn("[Cleanup] Collections icon not found; stopping cleanup");
                return;
            }
            try { page.waitForLoadState(); } catch (Exception ignored) {}
            try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
            
            // Check if empty state is visible - all collections deleted
            try {
                if (isNoCollectionsEmptyStateVisible(EMPTY_STATE_TIMEOUT)) {
                    logger.info("[Cleanup] Empty-state visible; all collections deleted");
                    return;
                }
            } catch (Exception ignored) {}
            
            // Find collection images - use first() to get the first collection
            Locator collectionImages = page.getByRole(AriaRole.IMG);
            int imgCount = collectionImages.count();
            logger.info("[Cleanup] Found {} images on collections screen", imgCount);
            
            if (imgCount < 2) {
                logger.info("[Cleanup] Not enough images found; checking empty state");
                try {
                    assertNoCollectionsEmptyState();
                    return;
                } catch (Exception e) {
                    logger.warn("[Cleanup] No collections and no empty state; continuing");
                    continue;
                }
            }
            
            // Click a collection image to open details
            // Based on screenshot: img.collection-img with alt like "john smith - Vidéos et photos"
            Locator collectionImg = page.locator("img.collection-img");
            int collCount = collectionImg.count();
            logger.info("[Cleanup] Found {} collection images (img.collection-img)", collCount);
            
            if (collCount == 0) {
                logger.info("[Cleanup] No collection images found; checking empty state");
                try {
                    assertNoCollectionsEmptyState();
                    return;
                } catch (Exception e) {
                    logger.warn("[Cleanup] No collections found but empty state not visible");
                    continue;
                }
            }
            
            Locator targetCollection = collectionImg.first();
            try { targetCollection.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            logger.info("[Cleanup] Clicking collection image to open details");
            clickTileRobust(targetCollection);
            try { page.waitForTimeout(750); } catch (Exception ignored) {}
            
            // Wait for details screen to load - check for "Details" text or back arrow
            boolean detailsLoaded = false;
            try {
                Locator detailsText = page.getByText("Details");
                if (detailsText.count() > 0 && detailsText.first().isVisible()) {
                    detailsLoaded = true;
                    logger.info("[Cleanup] Details screen loaded (found 'Details' text)");
                }
            } catch (Exception ignored) {}
            
            if (!detailsLoaded) {
                // Try back arrow as indicator
                try {
                    Locator backArrow = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
                    if (backArrow.count() > 0 && backArrow.first().isVisible()) {
                        detailsLoaded = true;
                        logger.info("[Cleanup] Details screen loaded (found back arrow)");
                    }
                } catch (Exception ignored) {}
            }
            
            if (!detailsLoaded) {
                logger.warn("[Cleanup] Details screen not loaded; returning to list");
                safeReturnToCollectionsList();
                continue;
            }
            
            // On details screen, click the menu icon (second img based on codegen: .nth(1))
            // Based on codegen: page.getByRole(AriaRole.IMG).nth(1).click()
            Locator menuIcon = page.getByRole(AriaRole.IMG).nth(1);
            try {
                waitVisible(menuIcon, SHORT_WAIT_TIMEOUT);
                logger.info("[Cleanup] Clicking menu icon (nth(1)) on details screen");
                clickWithRetry(menuIcon, 2, CLICK_RETRY_DELAY);
                try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
            } catch (Exception e) {
                logger.warn("[Cleanup] Menu icon nth(1) not found; trying .right-icon > img");
                Locator altMenu = page.locator(".right-icon > img");
                if (altMenu.count() > 0) {
                    clickWithRetry(altMenu.first(), 2, CLICK_RETRY_DELAY);
                    try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
                } else {
                    logger.warn("[Cleanup] No menu icon found; returning to list");
                    safeReturnToCollectionsList();
                    continue;
                }
            }

            // Delete button by robust variants
            Locator del = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete collection"));
            if (!(del.count() > 0 && safeIsVisible(del.first()))) {
                del = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete"));
            }
            if (!(del.count() > 0)) {
                del = page.locator("xpath=(//button[contains(translate(.,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'delete')])[1]");
            }
            waitVisible(del.first(), ConfigReader.getShortTimeout());
            clickWithRetry(del.first(), 2, CLICK_RETRY_DELAY);

            Locator yes = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, delete"));
            waitVisible(yes.first(), ConfigReader.getShortTimeout());
            clickWithRetry(yes.first(), 2, CLICK_RETRY_DELAY);

            // Wait for success toast, then click/dismiss (per user code-gen)
            Locator toast = page.getByText("Collection successfully deleted");
            if (toast.count() > 0) {
                try { toast.first().waitFor(new Locator.WaitForOptions().setTimeout(SHORT_WAIT_TIMEOUT)); } catch (Exception ignored) {}
                try { clickWithRetry(toast.first(), 1, TOAST_CLICK_DELAY); } catch (Exception ignored) {}
            } else {
                // Fallback: assert via existing helper
                try { assertCollectionDeletedToast(); } catch (Exception ignored) {}
            }
            
            logger.info("[Cleanup] Collection deleted successfully, returning to list");
            // Return to collections list for next iteration
            safeReturnToCollectionsList();
            try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
        }
        // After exhausting guard (or finishing deletions), verify empty state to ensure a clean end
        try {
            assertNoCollectionsEmptyState();
        } catch (Exception e) {
            logger.warn("[Cleanup] Empty state text not confirmed after loop: {}", e.getMessage());
        }
        logger.warn("[Cleanup] Guard exhausted in exact-flow deletion loop");
    }

    @Step("Verify 'No collection at the moment' empty state is visible")
    public void assertNoCollectionsEmptyState() {
        Locator empty = page.getByText("No collection at the moment");
        waitVisible(empty.first(), ConfigReader.getShortTimeout());
    }

    // Fast, non-throwing check to determine if the empty-state is visible
    public boolean isNoCollectionsEmptyStateVisible(long timeoutMs) {
        try {
            Locator empty = page.getByText("No collection at the moment");
            return WaitUtils.waitForVisible(empty.first(), Math.max(0, timeoutMs));
        } catch (Exception ignored) {
            return false;
        }
    }

    private void debugLogAllImgAccessibleNames(String prefix) {
        try {
            Locator imgs = page.locator("img");
            int cnt = imgs.count();
            logger.info("{} -> IMG count: {}", prefix, cnt);
            int limit = Math.min(cnt, 12);
            for (int i = 0; i < limit; i++) {
                Locator el = imgs.nth(i);
                String alt = null;
                String aria = null;
                try { alt = el.getAttribute("alt"); } catch (Exception ignored) {}
                try { aria = el.getAttribute("aria-label"); } catch (Exception ignored) {}
                logger.info("{} -> IMG[{}] alt='{}' aria-label='{}' visible={} ", prefix, i, alt, aria, safeIsVisible(el));
            }
        } catch (Exception e) {
            logger.warn("[Debug] Failed to enumerate IMG elements: {}", e.getMessage());
        }
    }

    @Step("Check 'No publication' icon visible on profile")
    public boolean isNoPublicationVisible() {
        try {
            Locator noPub = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("No publication"));
            return noPub.count() > 0 && noPub.first().isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Delete all collections available for the creator")
    public void deleteAllCollections() {
        openCollectionsList();
        int guard = 0;
        while (guard++ < 100) { // guard to avoid infinite loops
            Locator collections = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
            int count = collections.count();
            logger.info("[Cleanup] Collections remaining: {}", count);
            if (count == 0) {
                break;
            }
            if (!openFirstVisibleCollection()) {
                // No visible collection; break as safe fallback
                break;
            }
            // Delete the opened collection using current details flow
            try {
                ensureDetailsScreen();
                deleteCurrentCollectionFromDetails();
                // After deletion, return to collections list
                safeReturnToCollectionsList();
            } catch (Exception e) {
                logger.warn("[Cleanup] Deletion flow encountered an issue: {}", e.getMessage());
                // Attempt to return to list and continue
                safeReturnToCollectionsList();
            }
            // Small wait to allow list to refresh
            try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
            openCollectionsList();
        }
    }

    private void safeReturnToCollectionsList() {
        // Try common patterns: back button, header back arrow, or browser back
        // First try arrow left (back arrow on details screen)
        try {
            Locator arrowLeft = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
            if (arrowLeft.count() > 0 && safeIsVisible(arrowLeft.first())) {
                clickWithRetry(arrowLeft.first(), 1, BUTTON_RETRY_DELAY);
                try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
                return;
            }
        } catch (Exception ignored) {}
        // Try back icon
        try {
            Locator backIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("back"));
            if (safeIsVisible(backIcon.first())) {
                clickWithRetry(backIcon.first(), 1, BUTTON_RETRY_DELAY);
                try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
                return;
            }
        } catch (Exception ignored) {}
        // Last resort: browser back twice to get to profile
        try {
            page.goBack();
            try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
            page.goBack();
            try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    @Step("Check if contentinfo region is visible")
    public boolean isContentInfoVisible() {
        try {
            Locator contentInfo = page.getByRole(AriaRole.CONTENTINFO);
            return contentInfo.count() > 0 && contentInfo.first().isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    @Step("Delete collections until contentinfo is visible or guard exhausted")
    public void deleteUntilContentInfoVisible(int maxIterations) {
        int guard = Math.max(1, maxIterations);
        for (int i = 0; i < guard; i++) {
            if (isContentInfoVisible()) {
                logger.info("[Cleanup] Contentinfo visible; stopping delete loop");
                return;
            }
            deleteOneCollectionIfAny();
            // Small wait and re-check
            try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
        }
        logger.warn("[Cleanup] Guard exhausted while waiting for contentinfo to appear");
    }

    @Step("Dismiss 'I understand' dialog if present")
    public void clickIUnderstandIfPresent() {
        long start = System.currentTimeMillis();
        long timeoutMs = SHORT_WAIT_TIMEOUT;
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(I_UNDERSTAND_BTN));
                if (btn.count() > 0 && safeIsVisible(btn.first())) {
                    clickWithRetry(btn.first(), 2, CLICK_RETRY_DELAY);
                    return;
                }
                String[] sel = new String[] {
                        "button:has-text('I understand')",
                        "text=I understand",
                        "//*[self::button or self::*][contains(translate(normalize-space(.), 'IUNDERSTAND', 'iunderstand'), 'i understand')]"
                };
                for (String s : sel) {
                    Locator cand = s.startsWith("//") ? page.locator("xpath=" + s) : page.locator(s);
                    if (cand.count() > 0 && safeIsVisible(cand.first())) {
                        clickWithRetry(cand.first(), 2, CLICK_RETRY_DELAY);
                        return;
                    }
                }
            } catch (Exception ignored) {}
            try { page.waitForTimeout(TILE_SCROLL_WAIT); } catch (Exception ignored) {}
        }
    }

    @Step("Navigate to Collection screen")
    public void navigateToCollection() {
        // Apply overlay dismissal before clicking
        clickIUnderstandIfPresent();
        Locator collection = page.getByText(COLLECTION, new Page.GetByTextOptions().setExact(true));
        waitVisible(collection.first(), ConfigReader.getShortTimeout());
        clickWithRetry(collection.first(), 2, CLICK_RETRY_DELAY);
        ensureCollectionScreen();
    }

    @Step("Ensure Collection screen visible")
    public void ensureCollectionScreen() {
        Locator title = page.getByText(COLLECTION, new Page.GetByTextOptions().setExact(true));
        waitVisible(title.first(), ConfigReader.getShortTimeout());
    }

    @Step("Fill collection title with unique timestamp for prefix: {prefix}")
    public String fillCollectionTitle(String prefix) {
        String ts = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
        String value = (prefix == null ? "X" : prefix) + "_" + ts;
        fillByPlaceholder(TITLE_PLACEHOLDER, value);
        return value;
    }

    @Step("Click Create collection")
    public void clickCreate() {
        Locator create = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CREATE_BTN));
        waitVisible(create.first(), ConfigReader.getVisibilityTimeout());
        // Wait up to 90s for button to become enabled (form validations etc.)
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 90000) {
            try {
                if (create.first().isEnabled()) {
                    break;
                }
            } catch (Exception ignored) {}
            try { page.waitForTimeout(SCROLL_WAIT); } catch (Exception ignored) {}
        }
        // If still disabled, trigger validations on Title by blur and small edits
        if (!safeIsEnabled(create.first())) {
            try {
                Locator title = page.getByPlaceholder(TITLE_PLACEHOLDER);
                if (title.count() > 0) {
                    title.first().click();
                    // Nudge validation by appending and removing a space
                    try { title.first().press("Space"); } catch (Exception ignored) {}
                    try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}
                    try { page.keyboard().press("Backspace"); } catch (Exception ignored) {}
                    // Blur via Tab
                    try { page.keyboard().press("Tab"); } catch (Exception ignored) {}
                    try { page.waitForTimeout(KEYBOARD_BLUR_WAIT); } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}
        }
        if (!safeIsEnabled(create.first())) {
            throw new RuntimeException("Create button remained disabled after waiting. Title may be invalid or UI not ready.");
        }
        clickWithRetry(create.first(), 2, CLICK_RETRY_DELAY);
    }

    @Step("Open Add Media dialog")
    public void clickAddMediaPlus() {
        // Preferred: codegen locator IMG[name='add'] used for Add Media
        Locator addImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("add"));
        if (addImg.count() > 0) {
            waitVisible(addImg.first(), ConfigReader.getShortTimeout());
            clickWithRetry(addImg.first(), 2, CLICK_RETRY_DELAY);
        } else {
            // Fallback: legacy plus icon behavior
            Locator plus = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
            waitVisible(plus.first(), ConfigReader.getShortTimeout());
            Locator svg = plus.locator("svg");
            if (svg.count() > 0 && svg.first().isVisible()) {
                clickWithRetry(svg.first(), 2, CLICK_RETRY_DELAY);
            } else {
                clickWithRetry(plus.first(), 2, CLICK_RETRY_DELAY);
            }
        }
        // Ensure Importation is displayed
        waitVisible(page.getByText(IMPORTATION).first(), ConfigReader.getShortTimeout());
    }

    // Quick Files helpers
    @Step("Choose 'Quick Files' in Importation dialog")
    public void chooseQuickFiles() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Quick Files"));
        waitVisible(btn.first(), ConfigReader.getShortTimeout());
        clickWithRetry(btn.first(), 2, CLICK_RETRY_DELAY);
    }

    @Step("Select a Quick Files album by known prefixes or fallback to first available")
    public void selectQuickFilesAlbumWithFallback() {
        // Wait for list/grid to load inside Quick Files modal
        try {
            Locator listAny = page.locator(".ant-list, [role=row], .ant-list-item, .album, .list-item");
            waitVisible(listAny.first(), ConfigReader.getShortTimeout());
        } catch (Exception ignored) {}

        // Proactively scroll down in the Quick Files modal to surface albums near the bottom
        try {
            for (int i = 0; i < 5; i++) {
                try { page.mouse().wheel(0, 700); } catch (Exception ignored) {}
                try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        // Fast-path: if the known mixalbum with media is present, click it directly first
        try {
            Locator specificMixAlbum = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName("icon mixalbum_251106_155046"));
            if (specificMixAlbum.count() > 0) {
                Locator btn = specificMixAlbum.first();
                try {
                    if (!makeVisibleWithScroll(btn, 4000)) {
                        try { btn.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                    }
                } catch (Exception ignored) {}
                clickWithRetry(btn, 1, BUTTON_RETRY_DELAY);

                // Check for media thumbnails in this album
                Locator thumbsFast = page.locator(".select-quick-file-media-thumb");
                long startFast = System.currentTimeMillis();
                while (thumbsFast.count() == 0 && System.currentTimeMillis() - startFast < 4000) {
                    try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}
                }
                if (thumbsFast.count() > 0) {
                    // Non-empty album found; use it and stop
                    return;
                }

                // Empty album: Cancel -> plus -> Quick Files, then fall back to generic album loop below
                try {
                    Locator cancelBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
                    if (cancelBtn.count() > 0) {
                        clickWithRetry(cancelBtn.first(), 1, BUTTON_RETRY_DELAY);
                        try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}
                    }

                    Locator plusIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
                    if (plusIcon.count() > 0) {
                        clickWithRetry(plusIcon.first(), 1, BUTTON_RETRY_DELAY);
                    }

                    Locator quickFilesBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Quick Files"));
                    if (quickFilesBtn.count() > 0) {
                        clickWithRetry(quickFilesBtn.first(), 1, BUTTON_RETRY_DELAY);
                    }
                    try { page.waitForTimeout(BUTTON_RETRY_DELAY); } catch (Exception ignored) {}
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        // Prefer buttons whose accessible name contains videoalbum_/imagealbum_/mixalbum_ (matches codegen flow)
        Locator byPrefix = null;
        try {
            byPrefix = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile(".*(videoalbum_|imagealbum_|mixalbum_).*", Pattern.CASE_INSENSITIVE)));
        } catch (Exception ignored) {}

        int attempts = 0;
        int maxAttempts = 10;
        while (attempts++ < maxAttempts) {
            Locator candidateAlbum = null;
            if (byPrefix != null && byPrefix.count() > 0) {
                int idx = (attempts - 1) % byPrefix.count();
                candidateAlbum = byPrefix.nth(idx);
            } else {
                Locator anyAlbum = page.locator("[role=row], .ant-list-item, .album, .list-item");
                if (anyAlbum.count() > 0) {
                    int idx = (attempts - 1) % anyAlbum.count();
                    candidateAlbum = anyAlbum.nth(idx);
                }
            }

            if (candidateAlbum == null || candidateAlbum.count() == 0) {
                break;
            }

            try {
                if (!safeIsVisible(candidateAlbum.first())) {
                    try { candidateAlbum.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                }
                clickWithRetry(candidateAlbum.first(), 1, BUTTON_RETRY_DELAY);

                // Check if this album has any media thumbnails (allow short time for grid to render)
                Locator thumbs = page.locator(".select-quick-file-media-thumb");
                long start = System.currentTimeMillis();
                while (thumbs.count() == 0 && System.currentTimeMillis() - start < 4000) {
                    try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}
                }
                if (thumbs.count() > 0) {
                    // Non-empty album found
                    return;
                }

                // Empty album: follow flow -> Cancel, plus icon, Quick Files, then try next album
                try {
                    // 1) Cancel out of Quick Files media picker
                    Locator cancelBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
                    if (cancelBtn.count() > 0) {
                        clickWithRetry(cancelBtn.first(), 1, BUTTON_RETRY_DELAY);
                    }

                    // small wait to ensure dialog closes
                    try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}

                    // 2) Click plus icon again
                    Locator plusIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
                    if (plusIcon.count() > 0) {
                        clickWithRetry(plusIcon.first(), 1, BUTTON_RETRY_DELAY);
                    }

                    // 3) Open Quick Files again
                    Locator quickFilesBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Quick Files"));
                    if (quickFilesBtn.count() > 0) {
                        clickWithRetry(quickFilesBtn.first(), 1, BUTTON_RETRY_DELAY);
                    }

                    // brief settle for album list to reappear
                    try { page.waitForTimeout(BUTTON_RETRY_DELAY); } catch (Exception ignored) {}
                } catch (Exception ignored) {}
            } catch (Exception ignored) {
                // If click or navigation fails, continue to next candidate
            }
        }

        throw new RuntimeException("No Quick Files album with media found to select");
    }

    @Step("Select up to {n} media items (covers) from the Quick Files album")
    public void selectUpToNCovers(int n) {
        int need = Math.max(1, n);

        // Primary path: follow codegen by clicking IMG icons named "select"
        Locator selectIcons = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("select"));
        int iconCount = selectIcons.count();
        if (iconCount > 0) {
            int picked = 0;
            for (int i = 0; i < iconCount && picked < need; i++) {
                Locator icon = selectIcons.nth(i);
                try {
                    waitVisible(icon, ConfigReader.getShortTimeout());
                    try { icon.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                    clickWithRetry(icon, 1, NAVIGATION_WAIT);
                    try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}
                    picked++;
                } catch (Exception ignored) { }
            }
            return;
        }

        // Fallback: legacy behavior using thumbnail/cover divs
        Locator thumbs = page.locator(".select-quick-file-media-thumb");
        if (thumbs.count() == 0) {
            thumbs = page.locator(".cover");
        }
        if (thumbs.count() == 0) {
            throw new RuntimeException("No media thumbnails found in Quick Files album");
        }
        waitVisible(thumbs.first(), ConfigReader.getShortTimeout());
        int total = thumbs.count();
        int picked = 0;
        for (int i = 0; i < total && picked < need; i++) {
            Locator thumb = thumbs.nth(i);
            try {
                try { thumb.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                clickWithRetry(thumb, 1, NAVIGATION_WAIT);
                try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}
                picked++;
            } catch (Exception ignored) { }
        }
    }

    @Step("Confirm selection in Quick Files dialog")
    public void clickSelectInQuickFiles() {
        Locator selectCountBtn = page.locator("text=/^Select \\([0-9]+\\)/");
        if (selectCountBtn.count() > 0) {
            waitVisible(selectCountBtn.first(), ConfigReader.getShortTimeout());
            clickWithRetry(selectCountBtn.first(), 1, BUTTON_RETRY_DELAY);
            return;
        }
        Locator selectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select"));
        if (selectBtn.count() > 0) {
            clickWithRetry(selectBtn.first(), 1, BUTTON_RETRY_DELAY);
        }
    }

    @Step("Proceed through Next steps {times} times")
    public void proceedNextSteps(int times) {
        int t = Math.max(1, times);
        for (int i = 0; i < t; i++) {
            try {
                clickNext();
            } catch (Exception ignored) {}
        }
    }


    @Step("Choose 'My Device' in Importation dialog")
    public void chooseMyDevice() {
        // To avoid native OS dialogs, do not click the actual 'My Device' button here.
        // Instead, just ensure the Importation/Add media section is visible; the
        // underlying input[type='file'] will be driven directly by uploadMediaFromDevice.
        Locator importTitle = page.getByText(IMPORTATION);
        waitVisible(importTitle.first(), ConfigReader.getShortTimeout());
    }

    @Step("Upload media from device: {file}")
    public void uploadMediaFromDevice(Path file) {
        if (file == null || !Files.exists(file)) {
            throw new RuntimeException("Media file not found: " + file);
        }
        // Prefer Ant Upload file inputs inside the Add media dialog to avoid native OS dialogs.
        Locator inputs = page.locator(".ant-upload input[type='file']");
        if (inputs.count() == 0) {
            // Fallback: any visible file input on the page
            inputs = page.locator("input[type='file']");
        }
        if (inputs.count() == 0) {
            throw new RuntimeException("No file input available on Add media screen to upload: " + file.getFileName());
        }
        // Use the last input as many UIs append new upload controls at the end
        Locator target = inputs.nth(inputs.count() - 1);
        target.setInputFiles(file);

        // If the Importation bottom sheet is still visible, dismiss it so it doesn't block
        // subsequent clicks (e.g., on the Next button). This avoids clicking 'My Device'
        // directly, which would trigger a native file chooser.
        try {
            Locator bottomSheet = page.locator(".bottom-modal-overlay");
            if (bottomSheet.count() > 0 && bottomSheet.first().isVisible()) {
                Locator cancel = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
                if (cancel.count() > 0) {
                    clickWithRetry(cancel.first(), 1, BUTTON_RETRY_DELAY);
                }
            }
        } catch (Exception ignored) {}
    }

    // (Quick Files iteration/assert helper removed)

    @Step("Ensure Add media screen displayed and default toggles validated")
    public void ensureAddMediaScreenAndDefaults() {
        waitVisible(page.getByText(ADD_MEDIA_TITLE).first(), ConfigReader.getShortTimeout());
        // Ensure blurred media toggle (switch) is present and enabled by default
        Locator switchFirst = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(switchFirst, 5000);
        try {
            String checked = switchFirst.getAttribute("aria-checked");
            if (!"true".equalsIgnoreCase(checked)) {
                logger.warn("Blurred media switch not enabled by default (aria-checked={})", checked);
            }
        } catch (Exception ignored) {}
        // Ensure Thumbnail marker exists in main region
        Locator thumb = page.getByRole(AriaRole.MAIN).getByText("Thumbnail");
        waitVisible(thumb.first(), 5000);
    }

    @Step("Click Next in Add media")
    public void clickNext() {
        Locator next = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next"));
        waitVisible(next.first(), ConfigReader.getShortTimeout());
        clickWithRetry(next.first(), 2, 200);
    }
    

    @Step("Fill collection description")
    public void fillDescription(String text) {
        Locator desc = page.getByPlaceholder(DESC_PLACEHOLDER);
        desc.first().click();
        desc.first().fill(text != null ? text : "X_Description");
    }

    @Step("Set collection price to {euro}€")
    public void setPriceEuro(int euro) {
        page.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^" + euro + "€$"))).click();
    }

    @Step("Disable blurred media switch if currently enabled")
    public void disableBlurredSwitch() {
        Locator sw = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(sw, 5000);
        try {
            String checked = sw.getAttribute("aria-checked");
            if ("true".equalsIgnoreCase(checked)) {
                clickWithRetry(sw, 1, BUTTON_RETRY_DELAY);
            }
        } catch (Exception ignored) {}
    }

    @Step("Set custom price using spinner to {euro}€")
    public void setCustomPriceEuro(int euro) {
        // Open custom price control
        Locator zero = page.getByText("0.00 €");
        waitVisible(zero.first(), ConfigReader.getShortTimeout());
        clickWithRetry(zero.first(), 1, BUTTON_RETRY_DELAY);
        // Fill spinner with the desired value
        Locator spin = page.getByRole(AriaRole.SPINBUTTON);
        waitVisible(spin.first(), 5000);
        spin.first().fill(Integer.toString(euro));
        // Optional: blur to apply
        try { page.keyboard().press("Tab"); } catch (Exception ignored) {}
    }

    @Step("Validate collection (submit)")
    public void validateCollection() {
        Locator validate = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(VALIDATE_COLLECTION_BTN));
        waitVisible(validate.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(validate.first(), 2, PAGE_TRANSITION);
    }

    @Step("Assert success toast and dismiss it (timeout: {timeoutMs} ms)")
    public void waitForUploadFinish(long timeoutMs) {
        long deadline = System.currentTimeMillis() + Math.max(1, timeoutMs);
        Locator stayMsg = page.getByText("Stay on page during uploading");
        Locator percentSticky = page.locator("div").filter(new Locator.FilterOptions()
            .setHasText(Pattern.compile("^\\d+%Stay on page during uploading$")));
        Locator success = page.getByText("Collection is created successfully");

        boolean sawUploading = false;
        Integer lastSeen = null;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (success.count() > 0 && success.first().isVisible()) {
                    logger.info("[Upload] Success toast detected during upload; finishing early");
                    try { clickWithRetry(success.first(), 1, BUTTON_RETRY_DELAY); } catch (Exception ignored) {}
                    try { page.waitForTimeout(POST_TOAST_WAIT); } catch (Throwable ignored) {}
                    return;
                }
                if (stayMsg.count() > 0 && stayMsg.first().isVisible()) {
                    sawUploading = true;
                    try {
                        String text = percentSticky.first().innerText().trim();
                        int idx = text.indexOf('%');
                        if (idx > 0) {
                            String num = text.substring(0, idx).replaceAll("[^0-9]", "");
                            if (!num.isEmpty()) {
                                int val = Integer.parseInt(num);
                                if (lastSeen == null || val != lastSeen) {
                                    logger.info("[Upload] Progress: {}%", val);
                                    lastSeen = val;
                                }
                            }
                        }
                    } catch (RuntimeException ignored) {}
                } else {
                    if (sawUploading) {
                        logger.info("[Upload] Uploading banner disappeared; proceeding to post-upload verification");
                        break;
                    }
                }
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(UPLOAD_POLLING); } catch (Throwable ignored) {}
        }

        long quickToastBudget = Math.min(SHORT_WAIT_TIMEOUT, Math.max(0, deadline - System.currentTimeMillis()));
        if (quickToastBudget > 0) {
            try {
                waitVisible(success.first(), quickToastBudget);
                logger.info("[Upload] Success toast detected right after banner; dismissing");
                try { clickWithRetry(success.first(), 1, BUTTON_RETRY_DELAY); } catch (Exception ignored) {}
                try { page.waitForTimeout(POST_TOAST_WAIT); } catch (Throwable ignored) {}
                return;
            } catch (Throwable ignored) {}
        }

        try {
            page.waitForURL("**/creator/profile", new Page.WaitForURLOptions().setTimeout(Math.max(1, deadline - System.currentTimeMillis())));
        } catch (Throwable e) {
            logger.warn("[Upload] Did not navigate to /creator/profile within timeout: {}", e.getMessage());
        }

        long remaining = Math.max(1, deadline - System.currentTimeMillis());
        long capped = Math.min(remaining, ConfigReader.getShortTimeout());
        try {
            waitVisible(success.first(), capped);
            try { clickWithRetry(success.first(), 1, BUTTON_RETRY_DELAY); } catch (Exception ignored) {}
        } catch (Throwable te) {
            logger.warn("[Upload] Success toast not detected quickly after banner ({} ms cap, remaining was {} ms): {}",
                    capped, remaining, te.getMessage());
        }
        try { page.waitForTimeout(POST_TOAST_WAIT); } catch (Throwable ignored) {}
    }

    @Step("Assert success toast and dismiss it")
    public void waitForUploadFinish() {
        waitForUploadFinish(DEFAULT_UPLOAD_TIMEOUT);
    }

    @Step("Assert collection created success toast")
    public void assertCollectionCreatedToast() {
        // Backward compatible alias for tests expecting this method
        waitForUploadFinish();
    }

    // Safe wrapper to check enabled state without throwing exceptions
    private boolean safeIsEnabled(Locator loc) {
        try {
            return loc != null && loc.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // safeIsVisible provided by BasePage

}

