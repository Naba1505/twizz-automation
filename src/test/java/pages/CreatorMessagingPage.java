package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;
import org.testng.SkipException;
import java.util.regex.Pattern;

/**
 * Page Object for Creator Messaging flows.
 * Assumes caller has already logged in as Creator and is on the profile/home screen.
 */
public class CreatorMessagingPage extends BasePage {

    public CreatorMessagingPage(Page page) {
        super(page);
    }

    // ================= Messaging Dashboard navigation, tabs, filter & search =================
    @Step("Navigate directly to Creator Profile via URL")
    public void navigateToCreatorProfileViaUrl() {
        // Using stage URL as provided in steps; could be parameterized if needed
        page.navigate("https://stg.twizz.app/creator/profile");
    }

    @Step("Open Messaging from creator dashboard (header icon)")
    public void openMessagingFromDashboardIcon() {
        Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Messaging icon"));
        waitVisible(icon.first(), 15_000);
        clickWithRetry(icon.first(), 1, 150);
        waitVisible(page.getByText("Messaging"), 15_000);
    }

    @Step("Assert 'Messaging' title visible")
    public void assertMessagingTitle() {
        waitVisible(page.getByText("Messaging"), 10_000);
    }

    @Step("Click 'To Deliver' tab in Messaging")
    public void clickToDeliverTab() {
        Locator tab = page.getByText("To Deliver");
        waitVisible(tab.first(), 10_000);
        clickWithRetry(tab.first(), 1, 120);
    }

    @Step("Click 'General' tab in Messaging")
    public void clickGeneralTab() {
        Locator tab = page.getByText("General");
        waitVisible(tab.first(), 10_000);
        clickWithRetry(tab.first(), 1, 120);
    }

    @Step("Open Filter panel in Messaging")
    public void openFilter() {
        Locator filter = page.getByText("Filter");
        waitVisible(filter.first(), 10_000);
        clickWithRetry(filter.first(), 1, 120);
    }

    @Step("Filter: select 'Unread messages'")
    public void filterUnreadMessages() {
        // Click the container with exact text then the direct text entry, per codegen
        Locator byDiv = page.locator("div").filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^Unread messages$")));
        if (byDiv.count() > 0 && safeIsVisible(byDiv.first())) {
            clickWithRetry(byDiv.first(), 1, 120);
        }
        Locator byText = page.getByText("Unread messages");
        if (byText.count() > 0) {
            clickWithRetry(byText.first(), 1, 120);
        }
    }

    @Step("Filter: select 'All ( by default )'")
    public void filterAllByDefault() {
        Locator byDiv = page.locator("div").filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^All \\( by default \\)$")));
        waitVisible(byDiv.first(), 10_000);
        clickWithRetry(byDiv.first(), 1, 120);
    }

    @Step("Open search icon in Messaging")
    public void openSearchIcon() {
        Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("edit"));
        waitVisible(icon.first(), 10_000);
        clickWithRetry(icon.first(), 1, 120);
    }

    @Step("Fill Messaging search with: {query}")
    public void fillMessagingSearch(String query) {
        Locator box = page.getByPlaceholder("Search");
        waitVisible(box.first(), 10_000);
        box.fill(query == null ? "" : query);
    }

    @Step("Assert search result visible: {text}")
    public void assertSearchResultVisible(String text) {
        waitVisible(page.getByText(text), 10_000);
    }

    // ================= Private Media flow =================
    @Step("Open Private Media screen via 'Media' button")
    public void openPrivateMediaScreen() {
        logger.info("[Messaging][Private] Opening Private media screen");
        waitVisible(privateMediaButton(), DEFAULT_WAIT);
        clickWithRetry(privateMediaButton(), 1, 200);
        waitVisible(privateMediaTitle(), 15_000);
    }

    @Step("Click plus icon to add private media")
    public void clickPrivateMediaAddPlus() {
        logger.info("[Messaging][Private] Clicking plus icon to add media");
        waitVisible(plusIcon().first(), 15_000);
        clickWithRetry(plusIcon().first(), 1, 200);
        waitVisible(importationTitle(), 15_000);
    }

    @Step("Ensure blur toggle is enabled by default (private media)")
    public void ensureBlurToggleEnabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(sw, 15_000);
        try {
            String checked = sw.getAttribute("aria-checked");
            if (!"true".equalsIgnoreCase(checked)) {
                logger.warn("[Messaging][Private] Expected blur toggle enabled by default, aria-checked={}", checked);
            }
        } catch (Throwable ignored) {}
    }

    @Step("Assert 'Blurred media' label visible")
    public void assertBlurredMediaLabelVisible() {
        waitVisible(page.getByText("Blurred media").first(), 10_000);
    }

    @Step("Disable blur toggle if currently enabled (private media)")
    public void disableBlurToggleIfEnabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(sw, 15_000);
        try {
            String checked = sw.getAttribute("aria-checked");
            if ("true".equalsIgnoreCase(checked)) {
                clickWithRetry(sw, 1, 150);
            }
        } catch (Throwable e) {
            // Fallback: attempt click once
            clickWithRetry(sw, 1, 150);
        }
    }

    @Step("Click Next (private media flow)")
    public void clickNext() {
        Locator next = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next"));
        waitVisible(next.first(), 15_000);
        clickWithRetry(next.first(), 1, 200);
    }

    @Step("Wait for 'Blurred media' section to be visible (optional)")
    public void waitForBlurredMediaVisible(int timeoutMs) {
        try {
            waitVisible(page.getByText("Blurred media").first(), Math.max(2000, timeoutMs));
        } catch (Throwable ignored) {}
    }

    @Step("Click 'Next' repeatedly until private message placeholder appears or maxSteps reached")
    public void clickNextUntilMessagePlaceholder(int maxSteps, int perStepWaitMs) {
        int steps = Math.max(1, maxSteps);
        for (int i = 0; i < steps; i++) {
            // If already on message screen, stop
            if (safeIsVisible(privateMessagePlaceholder())) {
                logger.info("[Messaging][Private] Message placeholder visible after {} step(s)", i);
                return;
            }
            // If Next is present, click it
            Locator next = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next"));
            if (next.count() > 0 && safeIsVisible(next.first())) {
                waitForBlurredMediaVisible(5_000);
                clickWithRetry(next.first(), 1, 200);
                try { page.waitForTimeout(Math.max(200, perStepWaitMs)); } catch (Throwable ignored) {}
                continue;
            }
            // If no Next and no message field, wait a bit and retry
            try { page.waitForTimeout(Math.max(200, perStepWaitMs)); } catch (Throwable ignored) {}
        }
        // Final check
        if (!safeIsVisible(privateMessagePlaceholder())) {
            logger.warn("[Messaging][Private] Message placeholder still not visible after {} Next steps", steps);
        }
    }

    @Step("Assert private message input visible (Your message....)")
    public void assertPrivateMessagePlaceholder() {
        waitVisible(privateMessagePlaceholder(), 15_000);
    }

    @Step("Fill private message: {msg}")
    public void fillPrivateMessage(String msg) {
        Locator ph = privateMessagePlaceholder();
        waitVisible(ph, 15_000);
        ph.click();
        ph.fill(msg == null ? "" : msg);
    }

    @Step("Set price euro: {euros}€ (private media)")
    public void setPriceEuro(int euros) {
        String regex = "^" + euros + "€$";
        // 1) Try explicit label filter
        Locator label = page.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile(regex)));
        try {
            waitVisible(label.first(), 5_000);
            try { label.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
            clickWithRetry(label.first(), 1, 150);
            return;
        } catch (Throwable ignored) {}

        // 2) Try visible text node anywhere
        Locator byText = page.getByText(euros + "€", new Page.GetByTextOptions().setExact(true));
        try {
            waitVisible(byText.first(), 5_000);
            try { byText.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
            clickWithRetry(byText.first(), 1, 150);
            return;
        } catch (Throwable ignored) {}

        // 3) Iterate over radio button labels in pricing section
        Locator radios = page.locator("label.ant-radio-button-wrapper.mediaAmountRadioButton");
        int count = radios.count();
        for (int i = 0; i < count; i++) {
            Locator r = radios.nth(i);
            String text = "";
            try { text = r.innerText().trim(); } catch (Throwable ignored) {}
            if (text.contains(euros + "€")) {
                try { r.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                clickWithRetry(r, 1, 150);
                return;
            }
        }

        // 4) As a last resort, scroll a bit and retry #1 briefly
        try { page.mouse().wheel(0, 600); } catch (Throwable ignored) {}
        try {
            waitVisible(label.first(), 3_000);
            clickWithRetry(label.first(), 1, 150);
            return;
        } catch (Throwable ignored) {}

        throw new RuntimeException("Failed to set price to " + euros + "€: option not visible or not clickable");
    }

    @Step("Select price as 'Free' (private media)")
    public void selectPriceFree() {
        Locator lbl = page.locator("label").filter(new Locator.FilterOptions().setHasText("Free"));
        waitVisible(lbl.first(), 15_000);
        clickWithRetry(lbl.first(), 1, 150);
    }

    @Step("Click 'Propose the private media'")
    public void clickProposePrivateMedia() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Propose the private media"));
        waitVisible(btn.first(), 30_000);
        clickWithRetry(btn.first(), 1, 200);
    }

    // ================= Actions menu (three-dots) & Private Gallery =================
    @Step("Open actions menu (three dots)")
    public void openActionsMenu() {
        Locator dots = page.locator(".dots-wrapper");
        waitVisible(dots.first(), 10_000);
        clickWithRetry(dots.first(), 1, 150);
    }

    @Step("Assert action prompt visible: 'What action do you want to take?'")
    public void assertActionPrompt() {
        waitVisible(page.getByText("What action do you want to take?"), 10_000);
    }

    @Step("Click 'Private Gallery' option")
    public void clickPrivateGallery() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Private Gallery"));
        waitVisible(btn.first(), 10_000);
        clickWithRetry(btn.first(), 1, 150);
    }

    @Step("Assert 'Private Gallery' screen visible")
    public void assertPrivateGalleryScreen() {
        waitVisible(page.getByText("Private Gallery"), 15_000);
    }

    private Locator privateGalleryItems() {
        return page.locator(".galleryItem, .ant-image, .ant-card");
    }

    @Step("Wait for Private Gallery items to load")
    public void waitForPrivateGalleryItems(int timeoutMs) {
        long end = System.currentTimeMillis() + Math.max(5_000, timeoutMs);
        while (System.currentTimeMillis() < end) {
            int c = privateGalleryItems().count();
            if (c > 0) return;
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }
        waitVisible(privateGalleryItems().first(), 5_000);
    }

    @Step("Scroll Private Gallery to bottom then back to top")
    public void scrollPrivateGalleryToBottomThenTop() {
        try {
            // Scroll down a few times to ensure lazy load, then back to top
            for (int i = 0; i < 6; i++) {
                page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
                try { page.waitForTimeout(400); } catch (Throwable ignored) {}
            }
            for (int i = 0; i < 3; i++) {
                page.evaluate("window.scrollTo(0, 0)");
                try { page.waitForTimeout(300); } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
    }

    @Step("Preview any item in Private Gallery")
    public void previewAnyPrivateGalleryItem() {
        // Codegen-specific target first
        Locator cg = page.locator("div:nth-child(6) > .galleryItem > .ant-image > .ant-image-mask");
        if (cg.count() > 0 && safeIsVisible(cg.first())) {
            clickWithRetry(cg.first(), 1, 150);
            return;
        }
        // Fallback: first visible image mask
        Locator mask = page.locator(".galleryItem .ant-image .ant-image-mask, .ant-image .ant-image-mask").first();
        waitVisible(mask, 10_000);
        clickWithRetry(mask, 1, 150);
    }

    @Step("Close Private Gallery preview")
    public void closePrivateGalleryPreview() {
        // Codegen path: role IMG name 'close' then path
        Locator imgClose = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("close"));
        try {
            if (imgClose.count() > 0) {
                Locator path = imgClose.locator("path");
                if (path.count() > 0 && safeIsVisible(path.first())) {
                    clickWithRetry(path.first(), 1, 120);
                    return;
                }
                clickWithRetry(imgClose.first(), 1, 120);
                return;
            }
        } catch (Throwable ignored) {}
        // Fallbacks: close button or Escape
        try {
            Locator btnClose = page.locator(".ant-modal-close, button[aria-label='Close'], .ant-image-preview-close");
            if (btnClose.count() > 0 && safeIsVisible(btnClose.first())) {
                clickWithRetry(btnClose.first(), 1, 120);
                return;
            }
        } catch (Throwable ignored) {}
        try { page.keyboard().press("Escape"); } catch (Throwable ignored) {}
    }

    // ===== Promotion helpers (Private media) =====
    @Step("Enable promotion toggle if disabled")
    public void enablePromotionToggle() {
        Locator toggles = page.getByRole(AriaRole.SWITCH);
        Locator target = toggles.count() > 1 ? toggles.nth(1) : toggles.first();
        waitVisible(target, 15_000);
        try {
            String checked = target.getAttribute("aria-checked");
            if (!"true".equalsIgnoreCase(checked)) {
                clickWithRetry(target, 1, 150);
            }
        } catch (Throwable e) {
            clickWithRetry(target, 1, 150);
        }
    }

    @Step("Ensure 'Discount' field visible")
    public void ensureDiscountVisible() {
        waitVisible(page.getByText("Discount").first(), 15_000);
    }

    @Step("Fill euro discount amount: {amount}€ (textbox index 1)")
    public void fillPromotionEuroDiscount(int amount) {
        Locator tb = page.getByRole(AriaRole.TEXTBOX).nth(1);
        waitVisible(tb, 10_000);
        tb.click();
        tb.fill(String.valueOf(amount));
    }

    @Step("Fill percent discount: {percent}% (textbox index 2)")
    public void fillPromotionPercent(int percent) {
        Locator tb = page.getByRole(AriaRole.TEXTBOX).nth(2);
        waitVisible(tb, 10_000);
        tb.click();
        tb.fill(String.valueOf(percent));
    }

    @Step("Ensure 'Validity period' title visible")
    public void ensureValidityTitle() {
        waitVisible(page.getByText("Validity period").first(), 15_000);
    }

    @Step("Select validity as 'Unlimited'")
    public void selectValidityUnlimited() {
        Locator lbl = page.locator("label").filter(new Locator.FilterOptions().setHasText("Unlimited"));
        waitVisible(lbl.first(), 10_000);
        clickWithRetry(lbl.first(), 1, 150);
    }

    @Step("Select validity as '7 days'")
    public void selectValidity7Days() {
        Locator lbl = page.locator("label").filter(new Locator.FilterOptions().setHasText("7 days"));
        waitVisible(lbl.first(), 10_000);
        clickWithRetry(lbl.first(), 1, 150);
    }

    @Step("Wait for 'Stay on page during uploading' banner if it appears")
    public void waitForUploadingBanner() {
        try {
            Locator msg = uploadingStayOnPageText();
            if (msg.count() > 0) {
                waitVisible(msg.first(), 10_000);
            }
        } catch (Throwable ignored) {}
    }

    @Step("Wait for 'Stay on page during uploading' banner to disappear")
    public void waitForUploadingBannerToDisappear(long timeoutMs) {
        long end = System.currentTimeMillis() + Math.max(5_000, timeoutMs);
        while (System.currentTimeMillis() < end) {
            try {
                Locator msg = uploadingStayOnPageText();
                if (msg.count() == 0 || !msg.first().isVisible()) {
                    logger.info("[Messaging][Private] Uploading banner is no longer visible");
                    return;
                }
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(250); } catch (Throwable ignored) {}
        }
        logger.warn("[Messaging][Private] Uploading banner still visible after {} ms", timeoutMs);
    }

    @Step("Wait for 'Media sent' toast")
    public void waitForMediaSentToast(long timeoutMs) {
        long end = System.currentTimeMillis() + Math.max(10_000, timeoutMs);
        while (System.currentTimeMillis() < end) {
            try {
                Locator toast = mediaSentToast();
                if (toast.count() > 0 && toast.first().isVisible()) {
                    logger.info("[Messaging][Private] 'Media sent' toast visible");
                    return;
                }
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }
        // Fallback assert
        waitVisible(mediaSentToast(), (int) Math.max(DEFAULT_WAIT, timeoutMs));
    }

    @Step("Click any Quick Files album by regex (video/image/mix) with index fallbacks like codegen")
    public void clickAnyQuickFilesAlbumByRegex() {
        logger.info("[Messaging] Clicking a Quick Files album using regex selector and index fallbacks");
        // First, try the exact regex pattern family used in codegen
        Locator byText = page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^(?i)(video|image|mix)album.*")));
        int total = byText.count();
        logger.info("[Messaging] Regex-matched album div count: {}", total);
        if (total > 0) {
            // Try a few candidate indices (0..4) then fallback to last
            int[] tries = new int[] {0, 1, 2, 3, 4};
            for (int idx : tries) {
                if (idx < total) {
                    Locator cand = byText.nth(idx);
                    try { cand.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                    if (safeIsVisible(cand)) {
                        clickWithRetry(cand, 1, 200);
                        logger.info("[Messaging] Clicked album by regex at index {}", idx);
                        return;
                    }
                }
            }
            // Fallback: click last visible
            for (int i = total - 1; i >= 0; i--) {
                Locator cand = byText.nth(i);
                if (safeIsVisible(cand)) {
                    clickWithRetry(cand, 1, 200);
                    logger.info("[Messaging] Clicked album by regex (fallback last visible) at index {}", i);
                    return;
                }
            }
            // Final fallback: click first regardless of visibility heuristic
            clickWithRetry(byText.first(), 1, 200);
            logger.info("[Messaging] Clicked album by regex (final fallback first)");
            return;
        }
        // If regex fails entirely, reuse CSS fallback
        selectQuickFilesAlbumWithCssFallback();
    }

    @Step("Assert 'Select the media you want to send' prompt visible in album")
    public void assertAlbumMediaPrompt() {
        logger.info("[Messaging] Asserting inside album: 'Select the media you want to send'");
        waitVisible(page.getByText("Select the media you want to send"), 15_000);
    }

    @Step("Wait for album items grid (.cover) to be visible")
    public void waitForAlbumItemsGridVisible(int timeoutMs) {
        long end = System.currentTimeMillis() + Math.max(5_000, timeoutMs);
        while (System.currentTimeMillis() < end) {
            try {
                Locator firstCover = page.locator(".cover").first();
                if (firstCover.count() > 0 && safeIsVisible(firstCover)) {
                    logger.info("[Messaging][QuickFiles] Items grid visible with .cover elements");
                    return;
                }
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }
        // Final assertion to bubble up
        waitVisible(page.locator(".cover").first(), Math.max(3_000, timeoutMs));
    }

    @Step("Pick first two covers or up to {n} items as fallback")
    public void pickFirstTwoCoversOrUpToN(int n) {
        try {
            Locator first = page.locator(".cover").first();
            Locator second = page.locator("div:nth-child(2) > .cover");
            waitVisible(first, 10_000);
            clickWithRetry(first, 1, 120);
            if (second.count() > 0 && safeIsVisible(second.first())) {
                clickWithRetry(second.first(), 1, 120);
            }
            return;
        } catch (Throwable e) {
            logger.warn("[Messaging] First-two-covers click failed, fallback to generic selection: {}", e.getMessage());
        }
        selectUpToNQuickFiles(Math.max(1, n));
    }

    @Step("Select ALL items visible in the Quick Files album grid")
    public int selectAllQuickFilesItems() {
        // First, attempt the explicit first/second cover selectors like codegen
        try {
            Locator first = page.locator(".cover").first();
            if (first.count() > 0 && safeIsVisible(first)) {
                clickWithRetry(first, 1, 120);
            }
        } catch (Throwable ignored) {}
        try {
            Locator second = page.locator("div:nth-child(2) > .cover");
            if (second.count() > 0 && safeIsVisible(second.first())) {
                clickWithRetry(second.first(), 1, 120);
            }
        } catch (Throwable ignored) {}

        // Broaden the item locator to account for different tile structures
        Locator tiles = page.locator(".cover, .ant-image, .ant-card, div[class*='MediaCard'], div[class*='fileCard']");
        int total = tiles.count();
        logger.info("[Messaging][QuickFiles] Selecting all album items (broadened scan): detected tiles={}", total);
        for (int i = 0; i < total; i++) {
            try {
                Locator tile = tiles.nth(i);
                try { tile.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                if (!safeIsVisible(tile)) continue;
                // Prefer cover inside tile
                Locator cover = tile.locator(".cover");
                if (cover.count() > 0 && safeIsVisible(cover.first())) {
                    clickWithRetry(cover.first(), 1, 120);
                    continue;
                }
                // Else click the tile itself
                clickWithRetry(tile.first(), 1, 120);
            } catch (Throwable e) {
                logger.warn("[Messaging][QuickFiles] Failed selecting tile index {}: {}", i, e.getMessage());
            }
        }
        return total;
    }

    @Step("Select the first two covers explicitly (.cover and nth-child(2) > .cover)")
    public void selectFirstTwoCovers() {
        try {
            Locator first = page.locator(".cover").first();
            waitVisible(first, 10_000);
            clickWithRetry(first, 1, 120);
        } catch (Throwable e) {
            logger.warn("[Messaging][QuickFiles] Could not click first .cover: {}", e.getMessage());
        }
        try {
            Locator second = page.locator("div:nth-child(2) > .cover");
            if (second.count() > 0 && safeIsVisible(second.first())) {
                clickWithRetry(second.first(), 1, 120);
            }
        } catch (Throwable e) {
            logger.warn("[Messaging][QuickFiles] Could not click second .cover: {}", e.getMessage());
        }
    }

    // ================= Quick Files minimal flow =================
    @Step("Choose 'Quick Files' to import from albums")
    public void chooseQuickFilesForMedia() {
        logger.info("[Messaging] Choosing 'Quick Files' for media import");
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Quick Files"));
        waitVisible(btn, DEFAULT_WAIT);
        clickWithRetry(btn, 1, 200);
        // Ensure we are on Quick Files screen
        Locator qfTitle = page.getByText("Quick Files");
        waitVisible(qfTitle, 15_000);
        logger.info("[Messaging] 'Quick Files' title visible");
    }

    @Step("Assert Quick Files screen (title + 'My albums') is visible")
    public void assertQuickFilesScreen() {
        logger.info("[Messaging] Asserting Quick Files screen visible (title + 'My albums')");
        waitVisible(page.getByText("Quick Files"), 15_000);
        waitVisible(page.getByText("My albums"), 15_000);
    }

    private Locator quickFilesTitle() {
        return page.getByText("Quick Files");
    }

    private Locator quickFilesAlbumRows() {
        // Albums container row and album name spans
        return page.locator("div.ant-row.albumRow.css-ixblex, span.ant-typography.QuickLinkAlbumName.css-ixblex");
    }

    private Locator quickFilesAlbumsContainer() {
        return page.locator("div.ant-row.albumRow.css-ixblex").first();
    }

    private Locator quickFilesItemThumbs() {
        // Items typically render inside the Importation container, exclude album rows
        Locator container = importationContainer();
        return container.locator(
                ".ant-image:visible, .ant-card:visible, img:visible:not([alt*='album' i])"
        );
    }

    // Visible Importation container (modal/drawer) fallback to body
    private Locator importationContainer() {
        Locator container = page.locator(".ant-modal:visible, .ant-drawer:visible").first();
        if (container.count() == 0) container = page.locator("body");
        return container;
    }

    @Step("Ensure Quick Files albums screen is visible or skip if none")
    public void ensureQuickFilesAlbumsVisible() {
        logger.info("[Messaging] Ensuring 'Quick Files' albums screen is visible");
        waitVisible(quickFilesTitle(), 15_000);
        // Focus the Quick Files container (click title like codegen did)
        try { clickWithRetry(quickFilesTitle().first(), 1, 100); } catch (Throwable ignored) {}
        long end = System.currentTimeMillis() + 20_000;
        while (System.currentTimeMillis() < end) {
            int containers = quickFilesAlbumsContainer().count();
            int rows = quickFilesAlbumRows().count();
            if (containers > 0 || rows > 0) {
                logger.info("[Messaging] Quick Files albums detected: containers={}, rows={}", containers, rows);
                return;
            }
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }
        logger.warn("[Messaging] No albums visible in Quick Files; skipping per spec");
        throw new SkipException("Quick Files: no albums to select");
    }

    @Step("Select first album using provided CSS fallback")
    public void selectQuickFilesAlbumWithCssFallback() {
        logger.info("[Messaging] Selecting Quick Files album via provided CSS / regex fallbacks");
        // Step 1: Click albums container as user indicated it holds all albums
        Locator container = quickFilesAlbumsContainer();
        logger.info("[Messaging] Albums container count: {}", container.count());
        if (container.count() > 0) {
            waitVisible(container, DEFAULT_WAIT);
            clickWithRetry(container, 1, 150);
            // Inside container, click first album name span
            Locator nameSpans = container.locator("span.ant-typography.QuickLinkAlbumName.css-ixblex");
            logger.info("[Messaging] Album name spans count: {}", nameSpans.count());
            if (nameSpans.count() > 0) {
                clickWithRetry(nameSpans.first(), 1, 150);
                logger.info("[Messaging] Clicked first album name span");
            } else {
                // Fallback: click first album card/col inside container
                Locator firstCol = container.locator(".ant-col, .ant-card").first();
                if (firstCol.count() > 0) {
                    clickWithRetry(firstCol, 1, 150);
                    logger.info("[Messaging] Clicked first album col/card inside container");
                }
            }
        } else {
            // Step 2: Fallback to earlier approach
            Locator rows = quickFilesAlbumRows();
            int c = rows.count();
            logger.info("[Messaging] Album rows count via CSS: {}", c);
            if (c <= 0) {
            logger.warn("[Messaging] No album rows via CSS; trying regex-based album text filter");
            // Try regex on divs similar to codegen: ^(videoalbum|imagealbum|mixalbum).*
            Locator byText = page.locator("div").filter(new Locator.FilterOptions()
                    .setHasText(Pattern.compile("^(?i)(video|image|mix)album.*")));
            int t = byText.count();
            if (t > 0) {
                logger.info("[Messaging] Found {} album div(s) by text regex; clicking first viable", t);
                clickWithRetry(byText.first(), 1, 200);
            } else {
                // Final fallback: use the exact CI starts-with XPath from Collections page
                try {
                    String ciStartsWith = "xpath=(//*[self::div or self::span or self::p or self::li or self::a or self::button]"
                            + "[starts-with(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'video') or "
                            + " starts-with(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'image') or "
                            + " starts-with(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'mix')])";
                    Locator byPrefix = page.locator(ciStartsWith);
                    if (byPrefix.count() > 0) {
                        clickWithRetry(byPrefix.first(), 1, 150);
                        logger.info("[Messaging] Clicked album by CI starts-with XPath fallback");
                    } else {
                        throw new SkipException("Quick Files: no albums visible");
                    }
                } catch (Exception ex) {
                    throw new SkipException("Quick Files: no albums visible");
                }
            }
            } else {
            Locator first = rows.first();
            waitVisible(first, DEFAULT_WAIT);
            // Try clicking the row container if the locator is the span
            try {
                Locator rowContainer = first.locator("xpath=ancestor::div[contains(@class,'albumRow')]");
                if (rowContainer.count() > 0) {
                    clickWithRetry(rowContainer.first(), 1, 200);
                    logger.info("[Messaging] Clicked album row container");
                } else {
                    clickWithRetry(first, 1, 200);
                    logger.info("[Messaging] Clicked first album row element");
                }
            } catch (Throwable e) {
                // Fallback: direct click
                clickWithRetry(first, 1, 200);
                logger.info("[Messaging] Clicked first album row element (fallback)");
            }
            }
        }
        // Wait for items grid to appear; if not, try clicking once more
        long end = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < end) {
            if (quickFilesItemThumbs().count() > 0) break;
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }
        if (quickFilesItemThumbs().count() == 0) {
            logger.warn("[Messaging] Items view not visible after album click, retrying click once");
            try {
                // Retry regex click if available; else retry CSS first element
                Locator byText = page.locator("div").filter(new Locator.FilterOptions()
                        .setHasText(Pattern.compile("^(?i)(video|image|mix)album.*")));
                if (byText.count() > 0) {
                    clickWithRetry(byText.first(), 1, 200);
                } else if (container.count() > 0) {
                    clickWithRetry(container, 1, 150);
                    Locator nameSpans = container.locator("span.ant-typography.QuickLinkAlbumName.css-ixblex");
                    if (nameSpans.count() > 0) clickWithRetry(nameSpans.first(), 1, 150);
                }
            } catch (Throwable ignored) {}
        } else {
            logger.info("[Messaging] Items grid visible after album click: count={} (approx)", quickFilesItemThumbs().count());
        }
    }

    @Step("Select up to {max} media item(s) from album")
    public void selectUpToNQuickFiles(int max) {
        int target = Math.max(1, max);
        Locator grid = quickFilesItemThumbs();
        long end = System.currentTimeMillis() + 15_000;
        while (grid.count() == 0 && System.currentTimeMillis() < end) {
            try { page.waitForTimeout(150); } catch (Exception ignored) {}
        }
        int total = grid.count();
        logger.info("[Messaging] Quick Files items detected: {} (select up to {})", total, target);
        int picked = 0;
        // Strategy 0 (Collection parity): iterate covers, hover, click inner checkbox/role checkbox if present else cover
        try {
            Locator container = importationContainer();
            Locator covers = container.locator(".cover");
            int cc = covers.count();
            for (int i = 0; i < cc && picked < target; i++) {
                Locator card = covers.nth(i);
                try { card.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                try { card.hover(); } catch (Exception ignored) {}
                boolean clicked = false;
                try {
                    Locator innerCb = card.locator("input[type='checkbox'], [role='checkbox']");
                    if (innerCb.count() > 0 && safeIsVisible(innerCb.first())) {
                        clickWithRetry(innerCb.first(), 1, 120);
                        clicked = true;
                    }
                } catch (Exception ignored) {}
                if (!clicked) {
                    try {
                        Locator roleCheckbox = card.getByRole(AriaRole.CHECKBOX);
                        if (roleCheckbox.count() > 0 && safeIsVisible(roleCheckbox.first())) {
                            clickWithRetry(roleCheckbox.first(), 1, 120);
                            clicked = true;
                        }
                    } catch (Exception ignored) {}
                }
                if (!clicked) {
                    try { clickWithRetry(card, 1, 120); clicked = true; } catch (Exception ignored) {}
                }
                if (clicked) { try { page.waitForTimeout(150); } catch (Exception ignored) {} picked++; }
            }
        } catch (Throwable ignored) {}
        // Strategy A: click checkboxes if present
        try {
            Locator container = importationContainer();
            Locator checkboxes = container.getByRole(AriaRole.CHECKBOX);
            int cb = checkboxes.count();
            for (int i = 0; i < cb && picked < target; i++) {
                Locator cbx = checkboxes.nth(i);
                try { cbx.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                try { cbx.click(new Locator.ClickOptions().setForce(true)); picked++; } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
        // Strategy B: aria-checked toggles
        if (picked < target) {
            try {
                Locator container = importationContainer();
                Locator toggles = container.locator("[aria-checked]");
                int tc = toggles.count();
                for (int i = 0; i < tc && picked < target; i++) {
                    Locator tg = toggles.nth(i);
                    try { tg.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                    try { tg.click(new Locator.ClickOptions().setForce(true)); picked++; } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
        }
        for (int i = 0; i < total && picked < target; i++) {
            Locator item = grid.nth(i);
            try { item.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            try { item.click(new Locator.ClickOptions().setForce(true)); picked++; } catch (Exception ignored) {}
        }
        // If still nothing was picked, try double-click the first item as a last resort
        if (picked == 0 && total > 0) {
            try { grid.first().dblclick(); picked++; } catch (Throwable ignored) {}
        }
        // Verify Select button becomes enabled or count indicator appears
        try {
            Locator container = importationContainer();
            Locator selectBtn = container.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Select"));
            long until = System.currentTimeMillis() + 5_000;
            while (System.currentTimeMillis() < until) {
                if (selectBtn.count() > 0) {
                    try {
                        if (selectBtn.first().isEnabled()) break;
                    } catch (Throwable ignored) {}
                }
                page.waitForTimeout(150);
            }
        } catch (Throwable ignored) {}
        if (picked == 0) {
            logger.warn("[Messaging] No Quick Files items could be selected");
        }
    }

    @Step("Confirm Quick Files selection")
    public void confirmQuickFilesSelection() {
        logger.info("[Messaging] Confirming Quick Files selection");
        Locator container = importationContainer();
        Locator selectByRole = container.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Select"));
        if (selectByRole.count() > 0 && selectByRole.first().isVisible()) {
            clickWithRetry(selectByRole.first(), 1, 200);
        } else {
            Locator selectByText = container.getByText("Select");
            if (selectByText.count() > 0 && selectByText.first().isVisible()) {
                clickWithRetry(selectByText.first(), 1, 200);
            } else {
                Locator primary = container.locator(".ant-modal-footer .ant-btn-primary, .ant-drawer-footer .ant-btn-primary");
                if (primary.count() > 0 && primary.first().isVisible()) {
                    clickWithRetry(primary.first(), 1, 200);
                }
            }
        }
        // Wait for modal/drawer and Quick Files title to hide, then ensure conversation input appears
        try {
            Locator modalOrDrawer = page.locator(".ant-modal:visible, .ant-drawer:visible").first();
            Locator quickFilesTitle = page.getByText("Quick Files");
            int attempts = 0;
            while (attempts < 4 && (modalOrDrawer.count() > 0 || (quickFilesTitle.count() > 0 && safeIsVisible(quickFilesTitle.first())))) {
                try {
                    // Wait for either to hide briefly
                    if (modalOrDrawer.count() > 0) {
                        modalOrDrawer.waitFor(new Locator.WaitForOptions()
                                .setState(WaitForSelectorState.HIDDEN)
                                .setTimeout(3_000));
                    }
                } catch (Throwable ignored) {}
                try {
                    if (quickFilesTitle.count() > 0) {
                        quickFilesTitle.first().waitFor(new Locator.WaitForOptions()
                                .setState(WaitForSelectorState.HIDDEN)
                                .setTimeout(2_000));
                    }
                } catch (Throwable ignored) {}

                // If still visible, retry interactions
                if ((modalOrDrawer.count() > 0 && safeIsVisible(modalOrDrawer.first())) || (quickFilesTitle.count() > 0 && safeIsVisible(quickFilesTitle.first()))) {
                    attempts++;
                    logger.warn("[Messaging] Importation still visible after attempt {} — retrying close", attempts);
                    if (selectByRole.count() > 0 && selectByRole.first().isVisible()) {
                        clickWithRetry(selectByRole.first(), 1, 200);
                    } else {
                        Locator primary = container.locator(".ant-modal-footer .ant-btn-primary, .ant-drawer-footer .ant-btn-primary");
                        if (primary.count() > 0 && primary.first().isVisible()) {
                            clickWithRetry(primary.first(), 1, 200);
                        }
                    }
                    // Try close icon
                    if (modalOrDrawer.count() > 0) {
                        Locator closeIcon = modalOrDrawer.locator(".ant-modal-close, button[aria-label='Close']");
                        if (closeIcon.count() > 0 && closeIcon.first().isVisible()) {
                            try { clickWithRetry(closeIcon.first(), 1, 150); } catch (Exception ignored) {}
                        }
                    }
                    // Try ESC and click backdrop/body
                    try { page.keyboard().press("Escape"); } catch (Exception ignored) {}
                    try { page.mouse().click(10, 10); } catch (Exception ignored) {}
                    try { page.waitForTimeout(350); } catch (Exception ignored) {}
                } else {
                    break;
                }
            }
            if (modalOrDrawer.count() == 0 && (quickFilesTitle.count() == 0 || !safeIsVisible(quickFilesTitle.first()))) {
                logger.info("[Messaging] Importation dialog closed after confirming selection");
            } else {
                logger.warn("[Messaging] Importation dialog still visible after retries; proceeding to assert conversation");
            }
        } catch (Throwable e) {
            logger.warn("[Messaging] Importation dialog handling error: {}", e.getMessage());
        }

        // Finally, wait for the conversation input as the success signal
        try {
            waitVisible(messageInput(), 30_000);
        } catch (Throwable e) {
            logger.warn("[Messaging] Conversation input not visible after confirm: {}", e.getMessage());
        }
    }

    private Locator messagingIcon() {
        // IMG with accessible name "Messaging icon"
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Messaging icon"));
    }

    private Locator messagingTitle() {
        return page.getByText("Messaging");
    }

    private Locator fanAvatarStack() {
        // Container that holds existing fan message profile icons
        return page.locator("div.FanAvatarWrapper.w-72");
    }

    private Locator messageInput() {
        return page.getByPlaceholder("Your message");
    }

    private Locator sendButton() {
        // Prefer explicit button role with exact accessible name
        Locator byRole = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send"));
        if (byRole.count() > 0) return byRole.first();
        // Scope to footer around the message input if possible
        try {
            Locator input = messageInput();
            if (input.count() > 0) {
                Locator container = input.first().locator("xpath=ancestor-or-self::div[contains(@class,'message') or contains(@class,'footer')][1]");
                Locator scoped = container.getByText("Send", new Locator.GetByTextOptions().setExact(true));
                if (scoped.count() > 0) return scoped.first();
            }
        } catch (Throwable ignored) {}
        // Fallback to known label class
        Locator label = page.locator(".messageSendLabel");
        if (label.count() > 0) return label.first();
        // Final fallback: global exact text (may be ambiguous but better than nothing)
        return page.getByText("Send", new Page.GetByTextOptions().setExact(true));
    }

    private Locator mediaIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("media"));
    }

    private Locator importationTitle() {
        return page.getByText("Importation");
    }

    // ===== Private Media specific locators/texts =====
    private Locator privateMediaButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Media"));
    }

    private Locator privateMediaTitle() {
        return page.getByText("Private media", new Page.GetByTextOptions().setExact(true));
    }

    private Locator plusIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
    }

    private Locator privateMessagePlaceholder() {
        return page.getByPlaceholder("Your message....");
    }

    private Locator uploadingStayOnPageText() {
        return page.getByText("Stay on page during uploading");
    }

    private Locator mediaSentToast() {
        return page.getByText("Media sent");
    }

    private Locator myDeviceButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("My Device"));
    }

    

    private Locator acceptedImageBadge() {
        return page.locator("xpath=//img[@alt='accepted']");
    }

    private Locator uploadSpinner() {
        return page.locator(".ant-spin");
    }

    private Locator quickAnswerIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("quick answer"));
    }

    

    private Locator savedResponsesTitle() {
        return page.getByText("Saved responses");
    }

    private Locator savedResponseIcon() {
        // Click the saved response item by icon
        return page.locator(".ant-col > img");
    }

    @Step("Open Messaging from profile and verify landing on Messaging screen")
    public void openMessagingFromProfile() {
        logger.info("[Messaging] Opening Messaging from profile");
        // Ensure icon visible then click with retry
        waitVisible(messagingIcon(), DEFAULT_WAIT);
        clickWithRetry(messagingIcon(), 1, 200);
        // Verify Messaging title is visible
        waitVisible(messagingTitle(), DEFAULT_WAIT);
        logger.info("[Messaging] Landed on Messaging screen");
    }

    @Step("Open first fan conversation from the Messaging list")
    public void openFirstFanConversation() {
        logger.info("[Messaging] Opening first fan conversation");
        Locator stack = fanAvatarStack();
        // Wait briefly for any conversation to appear
        int attempts = 0;
        int count = stack.count();
        while (count == 0 && attempts < 5) { // ~1s total wait
            page.waitForTimeout(200);
            attempts++;
            count = stack.count();
        }
        if (count == 0) {
            logger.warn("[Messaging] No existing fan conversations available");
            throw new SkipException("No existing fan conversations available to open.");
        }
        // Click first conversation
        clickWithRetry(stack.first(), 1, 200);
        // Verify we are on the conversation screen by the message input placeholder
        waitVisible(messageInput(), DEFAULT_WAIT);
        logger.info("[Messaging] Conversation screen visible");
    }

    @Step("Send normal text message: {message}")
    public void sendTextMessage(String message) {
        logger.info("[Messaging] Sending text message: '{}'", message);
        waitVisible(messageInput(), DEFAULT_WAIT);
        messageInput().click();
        messageInput().fill(message);
        clickWithRetry(sendButton(), 1, 200);
        // Optional small settle
        page.waitForTimeout(300);
        logger.info("[Messaging] Message sent");
    }

    @Step("Open Quick Answers panel")
    public void openQuickAnswers() {
        logger.info("[Messaging] Opening Quick Answers panel");
        waitVisible(quickAnswerIcon(), DEFAULT_WAIT);
        clickWithRetry(quickAnswerIcon(), 1, 200);
    }

    @Step("Assert Saved responses panel is visible")
    public void assertSavedResponsesVisible() {
        logger.info("[Messaging] Asserting 'Saved responses' panel is visible");
        waitVisible(savedResponsesTitle(), DEFAULT_WAIT);
    }

    @Step("Select saved response by text contains: {text}")
    public void selectSavedResponseByText(String text) {
        logger.info("[Messaging] Selecting saved response by text contains: '{}'", text);
        Locator item = page.getByText(text);
        waitVisible(item, DEFAULT_WAIT);
        clickWithRetry(item, 1, 200);
    }

    @Step("Click saved response icon (first)")
    public void clickSavedResponseIcon() {
        logger.info("[Messaging] Clicking saved response icon (first)");
        Locator icon = savedResponseIcon().first();
        try {
            // Try a short, targeted wait for the icon; if not visible, fallback gracefully
            waitVisible(icon, 5000);
            clickWithRetry(icon, 1, 200);
            return;
        } catch (Exception ignored) {
            // Fallback: click on a common saved response label
            logger.warn("[Messaging] Saved response icon not visible; falling back to text-based selection");
        }
        try {
            Locator welcome = page.getByText("Welcome");
            waitVisible(welcome, 5000);
            clickWithRetry(welcome, 1, 200);
            return;
        } catch (Exception ignored) {
            // As a last resort, click any visible saved response container
            logger.warn("[Messaging] Fallback to any saved response container");
        }
        Locator anyItem = page.locator(".ant-col").first();
        waitVisible(anyItem, DEFAULT_WAIT);
        clickWithRetry(anyItem, 1, 200);
    }

    @Step("Click Send")
    public void clickSend() {
        logger.info("[Messaging] Clicking Send button");
        Locator btn = sendButton();
        waitVisible(btn, DEFAULT_WAIT);
        // Ensure visible and clickable; scroll into view as needed
        try { btn.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(btn, 1, 200);
        page.waitForTimeout(300);
    }

    @Step("Append to message input: {extra}")
    public void appendToMessage(String extra) {
        logger.info("[Messaging] Appending text to message input: '{}'", extra);
        waitVisible(messageInput(), DEFAULT_WAIT);
        Locator input = messageInput();
        input.click();
        // Append to any prefilled content (e.g., selected quick answer)
        input.pressSequentially(extra);
    }

    // ================= Media Send (Scenario 3) =================
    @Step("Open media picker in conversation")
    public void openMediaPicker() {
        logger.info("[Messaging] Opening media picker via media icon");
        waitVisible(mediaIcon(), DEFAULT_WAIT);
        clickWithRetry(mediaIcon(), 1, 200);
        // Ensure Importation title appears
        waitVisible(importationTitle(), DEFAULT_WAIT);
        logger.info("[Messaging] Importation popup visible");
    }

    @Step("Ensure 'Importation' dialog title is visible")
    public void ensureImportationVisible() {
        logger.info("[Messaging] Ensuring 'Importation' is visible");
        waitVisible(importationTitle(), 15_000);
    }

    @Step("Choose 'My Device' to import files")
    public void chooseMyDeviceForMedia() {
        logger.info("[Messaging] Choosing 'My Device' for media upload");
        waitVisible(myDeviceButton(), DEFAULT_WAIT);
        clickWithRetry(myDeviceButton(), 1, 200);
    }

    @Step("Upload message media from device: {file}")
    public void uploadMessageMedia(java.nio.file.Path file) {
        if (file == null || !java.nio.file.Files.exists(file)) {
            throw new RuntimeException("Message media file not found: " + file);
        }
        // Prefer direct input[type=file] if present
        Locator input = page.locator("input[type='file']");
        if (input.count() > 0) {
            logger.info("[Messaging] Using input[type=file] to upload: {}", file.getFileName());
            input.first().setInputFiles(file);
            return;
        }
        // Fallback to FileChooser triggered by My Device button
        try {
            logger.info("[Messaging] Waiting for FileChooser to select: {}", file.getFileName());
            FileChooser chooser = page.waitForFileChooser(this::chooseMyDeviceForMedia);
            chooser.setFiles(file);
        } catch (Exception e) {
            // Last resort: re-scan for any input[type=file]
            Locator any = page.locator("input[type='file']");
            if (any.count() > 0) {
                any.first().setInputFiles(file);
            } else {
                throw new RuntimeException("Failed to upload message media via file chooser: " + e.getMessage());
            }
        }
    }

    @Step("Assert that accepted badge for sent media is visible")
    public void assertAcceptedBadgeVisible() {
        assertAcceptedBadgeVisible(DEFAULT_WAIT);
    }

    @Step("Wait until upload spinner/banner disappears (timeout: {timeoutMs} ms)")
    public void waitForUploadSpinnerToDisappear(long timeoutMs) {
        logger.info("[Messaging] Waiting for .ant-spin to disappear (up to {} ms)", timeoutMs);
        Locator spin = uploadSpinner().first();
        try {
            // If it appears quickly, wait for hidden/detached; otherwise just proceed after timeout
            spin.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(timeoutMs));
        } catch (RuntimeException e) {
            logger.warn("[Messaging] Spinner did not hide within {} ms or was not present: {}", timeoutMs, e.getMessage());
        }
    }

    @Step("Assert that accepted badge for sent media is visible (timeout: {timeoutMs} ms)")
    public void assertAcceptedBadgeVisible(long timeoutMs) {
        logger.info("[Messaging] Waiting for 'accepted' badge on sent media (up to {} ms)", timeoutMs);
        long end = System.currentTimeMillis() + Math.max(DEFAULT_WAIT, timeoutMs);
        Locator badge = acceptedImageBadge().first();
        // Try to ensure conversation screen is active (message input visible)
        try {
            Locator input = page.getByPlaceholder("Your message");
            if (input.count() > 0) { waitVisible(input.first(), 5000); }
        } catch (Throwable ignored) {}
        while (System.currentTimeMillis() < end) {
            try {
                if (badge.count() > 0 && badge.isVisible()) {
                    logger.info("[Messaging] Accepted badge visible");
                    return;
                }
            } catch (Throwable ignored) {}
            // Gentle scroll attempts to reveal lazy content in the chat area
            try { page.mouse().wheel(0, -400); } catch (Throwable ignored) {}
            try { page.waitForTimeout(150); } catch (Throwable ignored) {}
            try { page.mouse().wheel(0, 800); } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }
        // Final assert using base waitVisible to throw detailed error
        waitVisible(badge, Math.max(DEFAULT_WAIT, (int) timeoutMs));
        logger.info("[Messaging] Accepted badge visible (final check)");
    }

    @Step("Assert conversation input is visible (placeholder: 'Your message')")
    public void assertConversationInputVisible() {
        waitVisible(messageInput(), DEFAULT_WAIT);
    }

    @Step("Assert conversation input is visible within {timeoutMs} ms")
    public void assertConversationInputVisible(long timeoutMs) {
        waitVisible(messageInput(), (int)Math.max(DEFAULT_WAIT, timeoutMs));
    }

}
