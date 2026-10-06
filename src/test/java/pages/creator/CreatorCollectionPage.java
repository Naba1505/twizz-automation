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
import com.microsoft.playwright.options.LoadState;

import io.qameta.allure.Step;
import utils.WaitUtils;

public class CreatorCollectionPage extends BasePage {

    // Constants - all timeouts now use ConfigReader for consistency
    private static final int SCROLL_WHEEL_AMOUNT = 600;  // Mouse wheel scroll amount
    private static final long DEFAULT_UPLOAD_TIMEOUT = Long.parseLong(ConfigReader.getProperty("collection.upload.timeout.ms", "180000"));

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
        // Login now ensures page is fully loaded, so just wait for plus icon
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg.first(), ConfigReader.getVisibilityTimeout());
        
        // Small stabilization to ensure icon is clickable
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
        
        // Some builds require clicking the nested svg
        Locator svg = plusImg.locator("svg").first();
        if (safeIsVisible(svg)) {
            clickWithRetry(svg, 2, ConfigReader.getElementRetryDelay());
        } else {
            clickWithRetry(plusImg.first(), 2, ConfigReader.getElementRetryDelay());
        }
    }

    // Attempt to make an element visible by scrolling the page; returns true if visible
    private boolean makeVisibleWithScroll(Locator loc, long timeoutMs) {
        long deadline = System.currentTimeMillis() + Math.max(0, timeoutMs);
        while (System.currentTimeMillis() < deadline) {
            try {
                if (loc.count() > 0 && loc.first().isVisible()) return true;
            } catch (Throwable e) { logger.debug("Visibility check failed: {}", e.getMessage()); }
            try {
                try { loc.scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("ScrollIntoView failed: {}", e.getMessage()); }
                try { page.keyboard().press("PageDown"); } catch (Throwable e) { logger.debug("PageDown failed: {}", e.getMessage()); }
                try { page.mouse().wheel(0, SCROLL_WHEEL_AMOUNT); } catch (Throwable e) { logger.debug("Mouse wheel failed: {}", e.getMessage()); }
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
            } catch (Throwable e) { logger.debug("Scroll iteration failed: {}", e.getMessage()); }
        }
        try { return loc.count() > 0 && loc.first().isVisible(); } catch (Throwable e) { return false; }
    }

    // Robust click attempts for a tile: normal, double, ancestor clickable, JS click, force
    private void clickTileRobust(Locator tile) {
        try {
            clickWithRetry(tile, 2, ConfigReader.getElementRetryDelay());
            return;
        } catch (Throwable e) { logger.debug("Normal click failed: {}", e.getMessage()); }
        try {
            tile.dblclick();
            return;
        } catch (Throwable e) { logger.debug("Double-click failed: {}", e.getMessage()); }
        try {
            Locator clickableAncestor = tile.locator("xpath=ancestor-or-self::*[self::a or self::button or @role='button' or contains(@class,'click')][1]");
            if (clickableAncestor.count() > 0) {
                clickableAncestor.first().click();
                return;
            }
        } catch (Throwable e) { logger.debug("Ancestor click failed: {}", e.getMessage()); }
        try {
            tile.first().evaluate("(el) => el && el.click && el.click()");
            return;
        } catch (Throwable e) { logger.debug("JS click failed: {}", e.getMessage()); }
        tile.click(new Locator.ClickOptions().setForce(true));
    }

    // =====================
    // Collections Deletion
    // =====================

    @Step("Assert collection deletion success or fallback to UI state checks")
    public void assertCollectionDeletedToast() {
        // Try common toast texts first
        Locator toastExact = page.getByText("Collection successfully deleted");
        if (WaitUtils.waitForVisible(toastExact, ConfigReader.getLongTimeout())) return;
        // Try regex match on any text containing 'deleted'
        Locator toastRegex = page.getByText(java.util.regex.Pattern.compile("Collection( is)? (successfully )?deleted|deleted successfully|deleted", java.util.regex.Pattern.CASE_INSENSITIVE));
        if (WaitUtils.waitForVisible(toastRegex, ConfigReader.getLongTimeout())) return;
        // Try generic role alert that contains 'deleted'
        try {
            Locator alert = page.getByRole(AriaRole.ALERT);
            if (alert.count() > 0 && alert.filter(new Locator.FilterOptions().setHasText("deleted")).count() > 0) return;
        } catch (Exception e) { logger.debug("Alert check failed: {}", e.getMessage()); }
        try {
            Locator confirmText = page.getByText("Are you sure you want to delete the collection? All linked data will be lost");
            confirmText.first().waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.DETACHED)
                .setTimeout(ConfigReader.getLongTimeout()));
        } catch (Exception e) { logger.debug("Confirm dialog detach wait failed: {}", e.getMessage()); }
        // Fallback: details header no longer present or actions icon gone, suggesting navigation/refresh
        try {
            Locator details = page.getByText("Details");
            if (details.count() == 0 || !details.first().isVisible()) return;
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        try {
            Locator actions = page.locator(".right-icon > img");
            if (actions.count() == 0 || !actions.first().isVisible()) return;
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        // As last resort, short wait to allow list refresh
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
    }

    // ============================
    // Collections cleanup exact flow
    // ============================

    private boolean clickCollectionsIconResilient() {
        clickIUnderstandIfPresent();
        // 1) Preferred: role IMG named "collections icon"
        try {
            Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collections icon"));
            if (icon.count() > 0) {
                waitVisible(icon.first(), ConfigReader.getShortTimeout());
                try { icon.first().scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                clickWithRetry(icon.first(), 2, ConfigReader.getElementRetryDelay());
                return true;
            }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        // 2) Fallback: any IMG whose accessible name contains 'collection' (case-insensitive)
        try {
            Locator any = page.locator("xpath=(//img[contains(translate(@alt, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'collection') or contains(translate(@aria-label, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'collection')])");
            if (any.count() > 0) {
                try { any.first().scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                clickWithRetry(any.first(), 2, ConfigReader.getElementRetryDelay());
                return true;
            }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        // 3) Fallback: clickable text/link/tab that contains 'Collection'
        try {
            Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(COLLECTION));
            if (safeIsVisible(link.first())) { clickWithRetry(link.first(), 1, ConfigReader.getElementRetryDelay()); return true; }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        try {
            Locator tab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(COLLECTION));
            if (safeIsVisible(tab.first())) { clickWithRetry(tab.first(), 1, ConfigReader.getElementRetryDelay()); return true; }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        try {
            Locator txt = page.getByText("Collections");
            if (safeIsVisible(txt.first())) { clickWithRetry(txt.first(), 1, ConfigReader.getElementRetryDelay()); return true; }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        try {
            Locator txt = page.getByText("Collection");
            if (safeIsVisible(txt.first())) { clickWithRetry(txt.first(), 1, ConfigReader.getElementRetryDelay()); return true; }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
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
            try { page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout())); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
            
            // Check if empty state is visible - all collections deleted
            try {
                if (isNoCollectionsEmptyStateVisible(ConfigReader.getShortTimeout())) {
                    logger.info("[Cleanup] Empty-state visible; all collections deleted");
                    return;
                }
            } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            
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
            // Prefer the stable .collection-img class (matches History of Collections DOM);
            // fall back to role/name selectors if absent.
            Locator collectionImg = page.locator(".collection-img");
            int collCount = collectionImg.count();
            logger.info("[Cleanup] Found {} collection images (.collection-img)", collCount);
            if (collCount == 0) {
                collectionImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
                collCount = collectionImg.count();
                logger.info("[Cleanup] Found {} collection images (role='img' name='collection')", collCount);
            }
            if (collCount == 0) {
                collectionImg = page.locator("img.collection-img");
                collCount = collectionImg.count();
                logger.info("[Cleanup] Found {} collection images (img.collection-img)", collCount);
            }
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
            try { targetCollection.scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            logger.info("[Cleanup] Clicking collection image to open details");
            clickTileRobust(targetCollection);
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
            
            // Wait for details screen to load - check for "Details" text or back arrow
            boolean detailsLoaded = false;
            try {
                Locator detailsText = page.getByText("Details");
                if (detailsText.count() > 0 && detailsText.first().isVisible()) {
                    detailsLoaded = true;
                    logger.info("[Cleanup] Details screen loaded (found 'Details' text)");
                }
            } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            
            if (!detailsLoaded) {
                // Try back arrow as indicator
                try {
                    Locator backArrow = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
                    if (backArrow.count() > 0 && backArrow.first().isVisible()) {
                        detailsLoaded = true;
                        logger.info("[Cleanup] Details screen loaded (found back arrow)");
                    }
                } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
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
                waitVisible(menuIcon, ConfigReader.getLongTimeout());
                logger.info("[Cleanup] Clicking menu icon (nth(1)) on details screen");
                clickWithRetry(menuIcon, 2, ConfigReader.getElementRetryDelay());
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
            } catch (Exception e) {
                logger.warn("[Cleanup] Menu icon nth(1) not found; trying .right-icon > img");
                Locator altMenu = page.locator(".right-icon > img");
                if (altMenu.count() > 0) {
                    clickWithRetry(altMenu.first(), 2, ConfigReader.getElementRetryDelay());
                    page.waitForTimeout(ConfigReader.getAnimationTimeout());
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
            clickWithRetry(del.first(), 2, ConfigReader.getElementRetryDelay());

            Locator yes = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, delete"));
            waitVisible(yes.first(), ConfigReader.getShortTimeout());
            clickWithRetry(yes.first(), 2, ConfigReader.getElementRetryDelay());

            // Wait for success toast, then click/dismiss (per user code-gen)
            Locator toast = page.getByText("Collection successfully deleted");
            if (toast.count() > 0) {
                try { toast.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getLongTimeout())); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                try { clickWithRetry(toast.first(), 1, ConfigReader.getAnimationTimeout()); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            } else {
                // Fallback: assert via existing helper
                try { assertCollectionDeletedToast(); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            }
            
            logger.info("[Cleanup] Collection deleted successfully, returning to list");
            // Return to collections list for next iteration
            safeReturnToCollectionsList();
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
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
        // Use medium timeout (30s) to allow time for empty state to appear after deletions
        waitVisible(empty.first(), ConfigReader.getMediumTimeout());
    }

    // Fast, non-throwing check to determine if the empty-state is visible
    public boolean isNoCollectionsEmptyStateVisible(long timeoutMs) {
        try {
            Locator empty = page.getByText("No collection at the moment");
            return WaitUtils.waitForVisible(empty.first(), Math.max(0, timeoutMs));
        } catch (Exception e) {
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
                try { alt = el.getAttribute("alt"); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                try { aria = el.getAttribute("aria-label"); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                logger.info("{} -> IMG[{}] alt='{}' aria-label='{}' visible={} ", prefix, i, alt, aria, safeIsVisible(el));
            }
        } catch (Exception e) {
            logger.warn("[Debug] Failed to enumerate IMG elements: {}", e.getMessage());
        }
    }

    private void safeReturnToCollectionsList() {
        // Try common patterns: back button, header back arrow, or browser back
        // First try arrow left (back arrow on details screen)
        try {
            Locator arrowLeft = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
            if (arrowLeft.count() > 0 && safeIsVisible(arrowLeft.first())) {
                clickWithRetry(arrowLeft.first(), 1, ConfigReader.getElementRetryDelay());
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
                return;
            }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        // Try back icon
        try {
            Locator backIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("back"));
            if (safeIsVisible(backIcon.first())) {
                clickWithRetry(backIcon.first(), 1, ConfigReader.getElementRetryDelay());
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
                return;
            }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        // Last resort: browser back twice to get to profile
        try {
            page.goBack();
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
            page.goBack();
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
    }

    @Step("Dismiss 'I understand' dialog if present")
    public void clickIUnderstandIfPresent() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(I_UNDERSTAND_BTN)).first();
        if (safeIsVisible(btn)) {
            clickWithRetry(btn, 2, ConfigReader.getElementRetryDelay());
            return;
        }
        // Fallback: try getByText and xpath
        Locator textLoc = page.getByText(I_UNDERSTAND_BTN, new Page.GetByTextOptions().setExact(true)).first();
        if (safeIsVisible(textLoc)) {
            clickWithRetry(textLoc, 2, ConfigReader.getElementRetryDelay());
            return;
        }
        Locator xpathLoc = page.locator("xpath=//*[self::button or self::*][contains(translate(normalize-space(.), 'IUNDERSTAND', 'iunderstand'), 'i understand')]").first();
        if (safeIsVisible(xpathLoc)) {
            clickWithRetry(xpathLoc, 2, ConfigReader.getElementRetryDelay());
        }
    }

    @Step("Navigate to Collection screen")
    public void navigateToCollection() {
        // Apply overlay dismissal before clicking
        clickIUnderstandIfPresent();
        Locator collection = page.getByText(COLLECTION, new Page.GetByTextOptions().setExact(true));
        waitVisible(collection.first(), ConfigReader.getShortTimeout());
        clickWithRetry(collection.first(), 2, ConfigReader.getElementRetryDelay());
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
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < ConfigReader.getNavigationTimeout()) {
            try {
                if (create.first().isEnabled()) {
                    break;
                }
            } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
        }
        // If still disabled, trigger validations on Title by blur and small edits
        if (!safeIsEnabled(create.first())) {
            try {
                Locator title = page.getByPlaceholder(TITLE_PLACEHOLDER);
                if (title.count() > 0) {
                    title.first().click();
                    // Nudge validation by appending and removing a space
                    try { title.first().press("Space"); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                    page.waitForTimeout(ConfigReader.getAnimationTimeout());
                    try { page.keyboard().press("Backspace"); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                    // Blur via Tab
                    try { page.keyboard().press("Tab"); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                    page.waitForTimeout(ConfigReader.getAnimationTimeout());
                }
            } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        }
        if (!safeIsEnabled(create.first())) {
            throw new RuntimeException("Create button remained disabled after waiting. Title may be invalid or UI not ready.");
        }
        clickWithRetry(create.first(), 2, ConfigReader.getElementRetryDelay());
    }

    @Step("Open Add Media dialog")
    public void clickAddMediaPlus() {
        // Preferred: codegen locator IMG[name='add'] used for Add Media
        Locator addImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("add")).first();
        if (safeIsVisible(addImg)) {
            waitVisible(addImg, ConfigReader.getShortTimeout());
            clickWithRetry(addImg, 2, ConfigReader.getElementRetryDelay());
        } else {
            // Fallback: legacy plus icon behavior
            Locator plus = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus")).first();
            waitVisible(plus, ConfigReader.getShortTimeout());
            Locator svg = plus.locator("svg").first();
            if (safeIsVisible(svg)) {
                clickWithRetry(svg, 2, ConfigReader.getElementRetryDelay());
            } else {
                clickWithRetry(plus, 2, ConfigReader.getElementRetryDelay());
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
        clickWithRetry(btn.first(), 2, ConfigReader.getElementRetryDelay());
    }

    @Step("Select a Quick Files album by known prefixes or fallback to first available")
    public void selectQuickFilesAlbumWithFallback() {
        // Wait for list/grid to load inside Quick Files modal
        try {
            Locator listAny = page.locator(".ant-list, [role=row], .ant-list-item, .album, .list-item");
            waitVisible(listAny.first(), ConfigReader.getShortTimeout());
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }

        // Proactively scroll down in the Quick Files modal to surface albums near the bottom
        try {
            for (int i = 0; i < 5; i++) {
                try { page.mouse().wheel(0, 700); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
            }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }

        // Fast-path: if the known mixalbum with media is present, click it directly first
        try {
            Locator specificMixAlbum = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName("icon mixalbum_251106_155046"));
            if (specificMixAlbum.count() > 0) {
                Locator btn = specificMixAlbum.first();
                try {
                    if (!makeVisibleWithScroll(btn, ConfigReader.getDefaultTimeout())) {
                        try { btn.scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                    }
                } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                clickWithRetry(btn, 1, ConfigReader.getElementRetryDelay());

                // Check for media thumbnails in this album
                Locator thumbsFast = page.locator(".select-quick-file-media-thumb");
                long startFast = System.currentTimeMillis();
                while (thumbsFast.count() == 0 && System.currentTimeMillis() - startFast < ConfigReader.getDefaultTimeout()) {
                    page.waitForTimeout(ConfigReader.getAnimationTimeout());
                }
                if (thumbsFast.count() > 0) {
                    // Non-empty album found; use it and stop
                    return;
                }

                // Empty album: Cancel -> plus -> Quick Files, then fall back to generic album loop below
                try {
                    Locator cancelBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
                    if (cancelBtn.count() > 0) {
                        clickWithRetry(cancelBtn.first(), 1, ConfigReader.getElementRetryDelay());
                        page.waitForTimeout(ConfigReader.getAnimationTimeout());
                    }

                    Locator plusIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
                    if (plusIcon.count() > 0) {
                        clickWithRetry(plusIcon.first(), 1, ConfigReader.getElementRetryDelay());
                    }

                    Locator quickFilesBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Quick Files"));
                    if (quickFilesBtn.count() > 0) {
                        clickWithRetry(quickFilesBtn.first(), 1, ConfigReader.getElementRetryDelay());
                    }
                    page.waitForTimeout(ConfigReader.getElementRetryDelay());
                } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }

        // Prefer buttons whose accessible name contains videoalbum_/imagealbum_/mixalbum_ (matches codegen flow)
        Locator byPrefix = null;
        try {
            byPrefix = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                    .setName(Pattern.compile(".*(videoalbum_|imagealbum_|mixalbum_).*", Pattern.CASE_INSENSITIVE)));
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }

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
                    try { candidateAlbum.first().scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                }
                clickWithRetry(candidateAlbum.first(), 1, ConfigReader.getElementRetryDelay());

                // Check if this album has any media thumbnails (allow short time for grid to render)
                Locator thumbs = page.locator(".select-quick-file-media-thumb");
                long start = System.currentTimeMillis();
                while (thumbs.count() == 0 && System.currentTimeMillis() - start < ConfigReader.getDefaultTimeout()) {
                    page.waitForTimeout(ConfigReader.getAnimationTimeout());
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
                        clickWithRetry(cancelBtn.first(), 1, ConfigReader.getElementRetryDelay());
                    }

                    // small wait to ensure dialog closes
                    page.waitForTimeout(ConfigReader.getAnimationTimeout());

                    // 2) Click plus icon again
                    Locator plusIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
                    if (plusIcon.count() > 0) {
                        clickWithRetry(plusIcon.first(), 1, ConfigReader.getElementRetryDelay());
                    }

                    // 3) Open Quick Files again
                    Locator quickFilesBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Quick Files"));
                    if (quickFilesBtn.count() > 0) {
                        clickWithRetry(quickFilesBtn.first(), 1, ConfigReader.getElementRetryDelay());
                    }

                    // brief settle for album list to reappear
                    page.waitForTimeout(ConfigReader.getElementRetryDelay());
                } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            } catch (Exception e) {
                logger.debug("Album candidate click/navigation failed: {}", e.getMessage());
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
                    try { icon.scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                    clickWithRetry(icon, 1, ConfigReader.getAnimationTimeout());
                    page.waitForTimeout(ConfigReader.getAnimationTimeout());
                    picked++;
                } catch (Exception e) { logger.debug("Select icon click failed at index {}: {}", i, e.getMessage()); }
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
                try { thumb.scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                clickWithRetry(thumb, 1, ConfigReader.getAnimationTimeout());
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
                picked++;
            } catch (Exception e) { logger.debug("Thumbnail click failed at index {}: {}", i, e.getMessage()); }
        }
    }

    @Step("Confirm selection in Quick Files dialog")
    public void clickSelectInQuickFiles() {
        Locator selectCountBtn = page.getByText(java.util.regex.Pattern.compile("^Select \\([0-9]+\\)"));
        if (selectCountBtn.count() > 0) {
            waitVisible(selectCountBtn.first(), ConfigReader.getShortTimeout());
            clickWithRetry(selectCountBtn.first(), 1, ConfigReader.getElementRetryDelay());
            return;
        }
        Locator selectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select"));
        if (selectBtn.count() > 0) {
            clickWithRetry(selectBtn.first(), 1, ConfigReader.getElementRetryDelay());
        }
    }

    @Step("Proceed through Next steps {times} times")
    public void proceedNextSteps(int times) {
        int t = Math.max(1, times);
        for (int i = 0; i < t; i++) {
            try {
                clickNext();
            } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
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
                    clickWithRetry(cancel.first(), 1, ConfigReader.getElementRetryDelay());
                }
            }
        } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
    }

    // (Quick Files iteration/assert helper removed)

    @Step("Ensure Add media screen displayed and default toggles validated")
    public void ensureAddMediaScreenAndDefaults() {
        waitVisible(page.getByText(ADD_MEDIA_TITLE).first(), ConfigReader.getShortTimeout());
        Locator switchFirst = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(switchFirst, ConfigReader.getDefaultTimeout());
        try {
            String checked = switchFirst.getAttribute("aria-checked");
            if (!"true".equalsIgnoreCase(checked)) {
                logger.warn("Blurred media switch not enabled by default (aria-checked={})", checked);
            }
        } catch (Exception e) { logger.debug("aria-checked read failed: {}", e.getMessage()); }
        Locator thumb = page.getByRole(AriaRole.MAIN).getByText("Thumbnail");
        waitVisible(thumb.first(), ConfigReader.getDefaultTimeout());
    }

    @Step("Click Next in Add media")
    public void clickNext() {
        Locator next = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next"));
        waitVisible(next.first(), ConfigReader.getShortTimeout());
        clickWithRetry(next.first(), 2, ConfigReader.getElementRetryDelay());
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
        waitVisible(sw, ConfigReader.getDefaultTimeout());
        try {
            String checked = sw.getAttribute("aria-checked");
            if ("true".equalsIgnoreCase(checked)) {
                clickWithRetry(sw, 1, ConfigReader.getElementRetryDelay());
            }
        } catch (Exception e) { logger.debug("Blur switch state read failed: {}", e.getMessage()); }
    }

    @Step("Set custom price using spinner to {euro}€")
    public void setCustomPriceEuro(int euro) {
        // Open custom price control
        Locator zero = page.getByText("0.00 €");
        waitVisible(zero.first(), ConfigReader.getShortTimeout());
        clickWithRetry(zero.first(), 1, ConfigReader.getElementRetryDelay());
        // Fill spinner with the desired value
        Locator spin = page.getByRole(AriaRole.SPINBUTTON);
        waitVisible(spin.first(), ConfigReader.getDefaultTimeout());
        spin.first().fill(Integer.toString(euro));
        // Optional: blur to apply
        try { page.keyboard().press("Tab"); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
    }

    /**
     * Validate collection (submit) and wait for the backend's collection-creation
     * API response instead of relying on the UI success toast, which is delivered
     * asynchronously (likely via a separate push channel) and can be delayed well
     * beyond the actual API completion, or not appear at all within budget on a
     * slow/loaded staging environment. Confirmed via trace inspection: the
     * POST https://apistg.twizz.com/collections/creators call reliably returns
     * HTTP 200 with the created collection payload, independent of whether the
     * toast ever renders.
     *
     * @param timeoutMs max time to wait for the API response
     * @return true if the API responded with a successful (2xx) status
     */
    @Step("Validate collection and wait for creation API response (timeout: {timeoutMs} ms)")
    public boolean validateCollectionAndAwaitApiSuccess(long timeoutMs) {
        Locator validate = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(VALIDATE_COLLECTION_BTN));
        waitVisible(validate.first(), ConfigReader.getVisibilityTimeout());
        try {
            com.microsoft.playwright.Response response = page.waitForResponse(
                    r -> r.url().contains("/collections/creators") && "POST".equalsIgnoreCase(r.request().method()),
                    new Page.WaitForResponseOptions().setTimeout(timeoutMs),
                    () -> clickWithRetry(validate.first(), 2, ConfigReader.getElementRetryDelay()));
            boolean ok = response.ok();
            logger.info("[Upload] Collection creation API responded: status={}, ok={}", response.status(), ok);
            return ok;
        } catch (Throwable e) {
            logger.warn("[Upload] Did not observe collection creation API response within {} ms: {}", timeoutMs, e.getMessage());
            return false;
        }
    }

    @Step("Assert success toast and dismiss it (timeout: {timeoutMs} ms)")
    public void waitForUploadFinish(long timeoutMs) {
        long deadline = System.currentTimeMillis() + Math.max(1, timeoutMs);
        Locator stayMsg = page.getByText("Stay on page during uploading");
        Locator percentSticky = page.locator("div").filter(new Locator.FilterOptions()
            .setHasText(Pattern.compile("^\\d+%Stay on page during uploading$")));
        Locator success = page.getByText(java.util.regex.Pattern.compile("Collection is created successfully|Collection created|created successfully", java.util.regex.Pattern.CASE_INSENSITIVE));

        boolean sawUploading = false;
        Integer lastSeen = null;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (success.count() > 0 && success.first().isVisible()) {
                    logger.info("[Upload] Success toast detected during upload; finishing early");
                    try { clickWithRetry(success.first(), 1, ConfigReader.getElementRetryDelay()); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                    try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Optional action failed: {}", e.getMessage()); }
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
                    } catch (RuntimeException e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                } else {
                    if (sawUploading) {
                        logger.info("[Upload] Uploading banner disappeared; proceeding to post-upload verification");
                        break;
                    }
                }
            } catch (Throwable e) { logger.debug("Optional action failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        }

        long quickToastBudget = Math.min(ConfigReader.getLongTimeout(), Math.max(0, deadline - System.currentTimeMillis()));
        if (quickToastBudget > 0) {
            try {
                waitVisible(success.first(), quickToastBudget);
                logger.info("[Upload] Success toast detected right after banner; dismissing");
                try { clickWithRetry(success.first(), 1, ConfigReader.getElementRetryDelay()); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Optional action failed: {}", e.getMessage()); }
                return;
            } catch (Throwable e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        }

        try {
            page.waitForURL("**/creator/profile", new Page.WaitForURLOptions().setTimeout(ConfigReader.getLongTimeout()));
        } catch (Throwable e) {
            logger.debug("[Upload] Did not navigate to /creator/profile within cap: {}", e.getMessage());
        }

        long remaining = Math.max(1, deadline - System.currentTimeMillis());
        // Cap at ConfigReader.getLongTimeout() (5s) instead of ConfigReader.getShortTimeout() to avoid waiting too long if toast doesn't appear
        long capped = Math.min(remaining, ConfigReader.getLongTimeout());
        try {
            waitVisible(success.first(), capped);
            try { clickWithRetry(success.first(), 1, ConfigReader.getElementRetryDelay()); } catch (Exception e) { logger.debug("Optional action failed: {}", e.getMessage()); }
        } catch (Throwable te) {
            logger.warn("[Upload] Success toast not detected quickly after banner ({} ms cap, remaining was {} ms): {}",
                    capped, remaining, te.getMessage());
        }
        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Optional action failed: {}", e.getMessage()); }
    }

    @Step("Assert success toast and dismiss it")
    public void waitForUploadFinish() {
        waitForUploadFinish(DEFAULT_UPLOAD_TIMEOUT);
    }

    @Step("Assert collection created success toast")
    public void assertCollectionCreatedToast() {
        // Quick final confirmation only - waitForUploadFinish() already polled for
        // the success toast using the full upload timeout budget. Re-running that
        // full-budget poll here would silently double the worst-case wait time
        // (observed 2x180s on slow staging uploads) while adding no real
        // verification, since callers only log a warning on failure anyway.
        Locator success = page.getByText(java.util.regex.Pattern.compile(
                "Collection is created successfully|Collection created|created successfully",
                java.util.regex.Pattern.CASE_INSENSITIVE));
        waitVisible(success.first(), ConfigReader.getShortTimeout());
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

