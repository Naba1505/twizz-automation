package pages;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.WaitUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class CreatorCollectionPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(CreatorCollectionPage.class);

    // Strings
    private static final String I_UNDERSTAND_BTN = "I understand";
    private static final String COLLECTION = "Collection";
    private static final String TITLE_PLACEHOLDER = "Title";
    private static final String CREATE_BTN = "Create";
    private static final String IMPORTATION = "Importation";
    private static final String MY_DEVICE_BTN = "My Device";
    private static final String ADD_MEDIA_TITLE = "Add media";
    private static final String DESC_PLACEHOLDER = "Your message....";
    private static final String VALIDATE_COLLECTION_BTN = "Validate the collection";
    private static final String STAY_ON_PAGE_MSG = "Stay on page during uploading";

    public CreatorCollectionPage(Page page) {
        super(page);
    }


    @Step("Open plus menu on creator dashboard")
    public void openPlusMenu() {
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg.first(), 15000);
        // Some builds require clicking the nested svg
        Locator svg = plusImg.locator("svg");
        if (svg.count() > 0 && svg.first().isVisible()) {
            clickWithRetry(svg.first(), 2, 200);
        } else {
            clickWithRetry(plusImg.first(), 2, 200);
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
                try { page.waitForTimeout(300); } catch (Exception ignored) {}
                // Quick check: are we on a details-like screen?
                if (!isDetailsMarkersPresentQuick(3000)) {
                    logger.warn("[Cleanup] Details markers not present after opening tile index {}; going back", i);
                    safeReturnToCollectionsList();
                    continue;
                }
                // Try opening actions menu quickly; if successful, proceed to delete flow
                if (!openActionsMenuQuick(3000)) {
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
                try { page.mouse().wheel(0, 600); } catch (Throwable ignored) {}
                page.waitForTimeout(150);
            } catch (Throwable ignored) {}
        }
        try { return loc.count() > 0 && loc.first().isVisible(); } catch (Throwable e) { return false; }
    }

    // Robust click attempts for a tile: normal, double, ancestor clickable, JS click, force
    private void clickTileRobust(Locator tile) {
        // 1) Normal click with retry
        try {
            clickWithRetry(tile, 2, 200);
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
        // Strategy 1: dedicated collections icon (preferred)
        try {
            Locator collectionsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collections icon"));
            waitVisible(collectionsIcon.first(), 30000);
            clickWithRetry(collectionsIcon.first(), 2, 200);
            // Wait for grid to render at least one tile
            Locator tiles = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
            if (!WaitUtils.waitForVisible(tiles, 10000)) {
                logger.warn("Collections tiles not visible after clicking collections icon; continuing with fallbacks");
            } else {
                return;
            }
        } catch (Exception e) {
            logger.warn("Collections icon not visible within timeout; trying fallback strategies");
        }

        // Strategy 2: click by visible text 'Collection'
        try {
            Locator txt = page.getByText(COLLECTION, new Page.GetByTextOptions().setExact(true));
            waitVisible(txt.first(), 15000);
            clickWithRetry(txt.first(), 2, 200);
            waitForIdle();
            return;
        } catch (RuntimeException ignored) {}

        // Strategy 3: Link or Tab role named 'Collection'
        try {
            Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(COLLECTION));
            if (safeIsVisible(link.first())) {
                clickWithRetry(link.first(), 1, 150);
                Locator tiles = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
                if (WaitUtils.waitForVisible(tiles, 8000)) return;
            }
        } catch (Exception ignored) {}
        try {
            Locator tab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(COLLECTION));
            if (safeIsVisible(tab.first())) {
                clickWithRetry(tab.first(), 1, 150);
                Locator tiles = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
                if (WaitUtils.waitForVisible(tiles, 8000)) return;
            }
        } catch (Exception ignored) {}
        try {
            Locator text = page.getByText("Collection");
            waitVisible(text.first(), 15000);
            clickWithRetry(text.first(), 1, 150);
            Locator tiles = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("collection"));
            if (WaitUtils.waitForVisible(tiles, 8000)) return;
        } catch (Exception ignored) {}
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
        waitVisible(candidate, 10000);
        try { candidate.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        clickWithRetry(candidate, 2, 200);
        // brief wait for navigation/content transition
        try { page.waitForTimeout(300); } catch (Exception ignored) {}
        return true;
    }

    @Step("Ensure collection details screen is visible")
    public void ensureDetailsScreen() {
        // Primary marker: 'Details' text
        try {
            Locator details = page.getByText("Details");
            waitVisible(details.first(), 10000);
            return;
        } catch (RuntimeException primary) {
            logger.warn("Details text not visible yet; trying alternate markers");
        }
        // Alternate marker 1: back arrow present on details
        try {
            Locator backArrow = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
            waitVisible(backArrow.first(), 10000);
            return;
        } catch (RuntimeException ignored) {}
        // Alternate marker 2: actions menu icon present on details header
        Locator menuIcon = page.locator(".right-icon > img");
        waitVisible(menuIcon.first(), 10000);
    }

    @Step("Open actions menu (three dots) on collection details")
    public void openActionsMenu() {
        Locator menuIcon = page.locator(".right-icon > img");
        waitVisible(menuIcon.first(), 10000);
        clickWithRetry(menuIcon.first(), 2, 200);
        // Ensure popup
        Locator popupTitle = page.getByText("What do you want to do?");
        waitVisible(popupTitle.first(), 10000);
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
                clickWithRetry(menuIcon.first(), 2, 200);
                Locator popupTitle = page.getByText("What do you want to do?");
                return WaitUtils.waitForVisible(popupTitle, 3000);
            } catch (Exception ignored) {}
        }
        return false;
    }

    @Step("Choose Delete collection option")
    public void chooseDeleteCollection() {
        Locator deleteBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete collection"));
        waitVisible(deleteBtn.first(), 10000);
        clickWithRetry(deleteBtn.first(), 2, 200);
    }

    @Step("Confirm deletion in confirmation dialog")
    public void confirmDeletion() {
        Locator confirmText = page.getByText("Are you sure you want to delete the collection? All linked data will be lost");
        waitVisible(confirmText.first(), 10000);
        Locator yesDelete = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, delete"));
        waitVisible(yesDelete.first(), 10000);
        clickWithRetry(yesDelete.first(), 2, 200);
    }

    @Step("Assert collection deletion success toast")
    public void assertCollectionDeletedToast() {
        Locator success = page.getByText("Collection successfully deleted");
        waitVisible(success.first(), 30000);
        try { clickWithRetry(success.first(), 1, 100); } catch (Exception ignored) {}
    }

    @Step("Delete currently opened collection via actions menu and confirmation")
    public void deleteOpenedCollection() {
        ensureDetailsScreen();
        openActionsMenu();
        chooseDeleteCollection();
        confirmDeletion();
        assertCollectionDeletedToast();
        // Try to navigate back to list if not auto-returned
        safeReturnToCollectionsList();
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
            // Delete the opened collection
            try {
                deleteOpenedCollection();
            } catch (Exception e) {
                logger.warn("[Cleanup] Deletion flow encountered an issue: {}", e.getMessage());
                // Attempt to return to list and continue
                safeReturnToCollectionsList();
            }
            // Small wait to allow list to refresh
            try { page.waitForTimeout(500); } catch (Exception ignored) {}
            openCollectionsList();
        }
    }

    private void safeReturnToCollectionsList() {
        // Try common patterns: back button, header back arrow, or browser back
        try {
            Locator backIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("back"));
            if (safeIsVisible(backIcon.first())) {
                clickWithRetry(backIcon.first(), 1, 150);
                return;
            }
        } catch (Exception ignored) {}
        try {
            page.goBack();
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
            try { page.waitForTimeout(500); } catch (Exception ignored) {}
        }
        logger.warn("[Cleanup] Guard exhausted while waiting for contentinfo to appear");
    }

    @Step("Dismiss 'I understand' dialog if present")
    public void clickIUnderstandIfPresent() {
        Locator understand = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(I_UNDERSTAND_BTN));
        if (understand.count() > 0 && understand.first().isVisible()) {
            clickWithRetry(understand.first(), 2, 200);
        }
    }

    @Step("Navigate to Collection screen")
    public void navigateToCollection() {
        // Apply overlay dismissal before clicking
        clickIUnderstandIfPresent();
        Locator collection = page.getByText(COLLECTION, new Page.GetByTextOptions().setExact(true));
        waitVisible(collection.first(), 10000);
        clickWithRetry(collection.first(), 2, 200);
        ensureCollectionScreen();
    }

    @Step("Ensure Collection screen visible")
    public void ensureCollectionScreen() {
        Locator title = page.getByText(COLLECTION, new Page.GetByTextOptions().setExact(true));
        waitVisible(title.first(), 10000);
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
        waitVisible(create.first(), 15000);
        // Wait up to 90s for button to become enabled (form validations etc.)
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 90000) {
            try {
                if (create.first().isEnabled()) {
                    break;
                }
            } catch (Exception ignored) {}
            try { page.waitForTimeout(250); } catch (Exception ignored) {}
        }
        // If still disabled, trigger validations on Title by blur and small edits
        if (!safeIsEnabled(create.first())) {
            try {
                Locator title = page.getByPlaceholder(TITLE_PLACEHOLDER);
                if (title.count() > 0) {
                    title.first().click();
                    // Nudge validation by appending and removing a space
                    try { title.first().press("Space"); } catch (Exception ignored) {}
                    try { page.waitForTimeout(100); } catch (Exception ignored) {}
                    try { page.keyboard().press("Backspace"); } catch (Exception ignored) {}
                    // Blur via Tab
                    try { page.keyboard().press("Tab"); } catch (Exception ignored) {}
                    try { page.waitForTimeout(600); } catch (Exception ignored) {}
                }
            } catch (Exception ignored) {}
        }
        if (!safeIsEnabled(create.first())) {
            throw new RuntimeException("Create button remained disabled after waiting. Title may be invalid or UI not ready.");
        }
        clickWithRetry(create.first(), 2, 200);
    }

    @Step("Open Add Media dialog")
    public void clickAddMediaPlus() {
        Locator plus = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plus.first(), 10000);
        Locator svg = plus.locator("svg");
        if (svg.count() > 0 && svg.first().isVisible()) {
            clickWithRetry(svg.first(), 2, 200);
        } else {
            clickWithRetry(plus.first(), 2, 200);
        }
        // Ensure Importation is displayed
        waitVisible(page.getByText(IMPORTATION).first(), 10000);
    }

    // (Quick Files helpers removed)


    @Step("Choose 'My Device' in Importation dialog")
    public void chooseMyDevice() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(MY_DEVICE_BTN));
        waitVisible(btn.first(), 10000);
        clickWithRetry(btn.first(), 2, 200);
    }

    @Step("Upload media from device: {file}")
    public void uploadMediaFromDevice(Path file) {
        if (file == null || !Files.exists(file)) {
            throw new RuntimeException("Media file not found: " + file);
        }
        // Try direct input[type=file]
        Locator input = page.locator("input[type='file']");
        if (input.count() > 0) {
            input.first().setInputFiles(file);
            return;
        }
        // Otherwise rely on FileChooser triggered by 'My Device'
        try {
            FileChooser chooser = page.waitForFileChooser(this::chooseMyDevice);
            chooser.setFiles(file);
        } catch (Exception e) {
            // Last attempt: global setInputFiles on any visible file input that may appear late
            Locator any = page.locator("input[type='file']");
            if (any.count() > 0) {
                any.first().setInputFiles(file);
            } else {
                throw new RuntimeException("Failed to upload media via file chooser: " + e.getMessage());
            }
        }
    }

    // (Quick Files iteration/assert helper removed)

    @Step("Ensure Add media screen displayed and default toggles validated")
    public void ensureAddMediaScreenAndDefaults() {
        waitVisible(page.getByText(ADD_MEDIA_TITLE).first(), 10000);
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
        waitVisible(next.first(), 10000);
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
                clickWithRetry(sw, 1, 150);
            }
        } catch (Exception ignored) {}
    }

    @Step("Set custom price using spinner to {euro}€")
    public void setCustomPriceEuro(int euro) {
        // Open custom price control
        Locator zero = page.getByText("0.00 €");
        waitVisible(zero.first(), 10000);
        clickWithRetry(zero.first(), 1, 150);
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
        waitVisible(validate.first(), 15000);
        clickWithRetry(validate.first(), 2, 250);
    }

    @Step("Wait for upload message to appear and disappear")
    public void waitForUploadFinish() {
        Locator stay = page.getByText(STAY_ON_PAGE_MSG);
        // appear
        waitVisible(stay.first(), 30000);
        // disappear
        stay.first().waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.DETACHED)
                .setTimeout(120000));
    }

    @Step("Assert collection created success toast and click it")
    public void assertCollectionCreatedToast() {
        Locator toast = page.getByText("Collection is created successfully");
        waitVisible(toast.first(), 60000);
        try { clickWithRetry(toast.first(), 1, 100); } catch (Exception ignored) {}
    }

    // Safe wrapper to check enabled state without throwing exceptions
    private boolean safeIsEnabled(Locator loc) {
        try {
            return loc != null && loc.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // Safe wrapper to check visible state without throwing exceptions
    private boolean safeIsVisible(Locator loc) {
        try {
            return loc != null && loc.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

}
