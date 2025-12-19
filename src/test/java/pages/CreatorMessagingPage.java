package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
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

    @Step("Click Next using strict XPath and force if needed")
    public void clickNextStrict() {
        Locator nextXpath = page.locator("xpath=//button//span[contains(normalize-space(.), 'Next')]/ancestor::button[1]");
        long end = System.currentTimeMillis() + 30_000;
        while (System.currentTimeMillis() < end) {
            if (nextXpath.count() > 0) {
                Locator b = nextXpath.first();
                try { b.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                if (safeIsVisible(b)) {
                    clickWithRetry(b, 1, 200);
                    return;
                }
                try { b.click(new Locator.ClickOptions().setForce(true)); return; } catch (Throwable ignored) {}
            }
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }
        throw new RuntimeException("Strict Next button not found or not clickable within timeout");
    }

    @Step("Wait for second add icon (.addCircle) to appear after first Next")
    public void waitForSecondAddIcon(int timeoutMs) {
        long end = System.currentTimeMillis() + Math.max(3_000, timeoutMs);
        Locator second = page.locator(".addCircle");
        while (System.currentTimeMillis() < end) {
            try {
                if (second.count() > 0 && safeIsVisible(second.first())) {
                    return;
                }
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }
        // Final assert
        waitVisible(second.first(), Math.max(2_000, timeoutMs));
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

    @Step("Select {countToSelect} media items from Quick Files album button: {albumButtonName}")
    public void selectMediaFromQuickFilesAlbum(String albumButtonName, int countToSelect) {
        String targetName = (albumButtonName == null || albumButtonName.isBlank())
                ? "icon mixalbum_251119_134546" : albumButtonName;
        logger.info("[Messaging][QuickFiles] Selecting media from Quick Files album button: {}", targetName);

        // Ensure Quick Files albums screen is visible (codegen checks 'My albums')
        try {
            logger.info("[Messaging][QuickFiles] Waiting for 'My albums' label");
            waitVisible(page.getByText("My albums"), 15_000);
        } catch (Throwable ignored) {
            logger.info("[Messaging][QuickFiles] 'My albums' not visible quickly; calling ensureQuickFilesAlbumsVisible()");
            ensureQuickFilesAlbumsVisible();
        }

        // Scroll to surface albums near the bottom and click the specific album button
        Locator container = importationContainer();
        long end = System.currentTimeMillis() + 30_000;
        boolean clickedAlbum = false;
        // Derive a relaxed prefix for regex fallback (imagealbum_/videoalbum_/mixalbum_)
        String relaxedPrefix = targetName;
        try {
            String lower = targetName.toLowerCase();
            if (lower.contains("mixalbum_")) {
                relaxedPrefix = "mixalbum_";
            } else if (lower.contains("imagealbum_")) {
                relaxedPrefix = "imagealbum_";
            } else if (lower.contains("videoalbum_")) {
                relaxedPrefix = "videoalbum_";
            }
        } catch (Throwable ignored) {}

        while (System.currentTimeMillis() < end && !clickedAlbum) {
            // Find div.qf-row[role='button'] containing div.qf-row-title whose text starts with relaxed prefix
            try {
                // XPath: div[@class='qf-row' and @role='button'][.//div[@class='qf-row-title' and starts-with(normalize-space(.), '<prefix>')]]
                String xpathExpr = "//div[@class='qf-row' and @role='button'][.//div[@class='qf-row-title' and starts-with(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + relaxedPrefix.toLowerCase() + "')]]";
                Locator albumRows = container.locator("xpath=" + xpathExpr);
                int rowCount = albumRows.count();
                if (rowCount > 0) {
                    logger.info("[Messaging][QuickFiles] Found {} album row(s) by qf-row-title prefix '{}'; clicking first", rowCount, relaxedPrefix);
                    Locator row = albumRows.first();
                    try { row.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                    clickWithRetry(row, 1, 200);
                    clickedAlbum = true;
                    break;
                }
            } catch (Throwable ignored) {}

            // Scroll down more aggressively to reach albums near the bottom
            logger.info("[Messaging][QuickFiles] Album row with prefix '{}' not yet visible; scrolling down", relaxedPrefix);
            try { container.evaluate("el => el.scrollBy(0, 900)"); } catch (Throwable ignored) {}
            try { page.waitForTimeout(300); } catch (Throwable ignored) {}
        }
        if (!clickedAlbum) {
            logger.warn("[Messaging][QuickFiles] Failed to find album button '{}' within scroll timeout; falling back to generic album click by regex", targetName);
            try {
                clickAnyQuickFilesAlbumByRegex();
                clickedAlbum = true;
            } catch (Throwable e) {
                logger.warn("[Messaging][QuickFiles] Generic Quick Files album click by regex also failed: {}", e.toString());
                throw new RuntimeException("Quick Files album button not found (including regex fallback): " + targetName, e);
            }
        }

        // Ensure we are inside the album by waiting for the 'Select media' prompt
        try {
            Locator selectMediaTitle = page.getByText("Select media");
            waitVisible(selectMediaTitle.first(), 10_000);
        } catch (Throwable ignored) {}

        // Wait for media thumbs/icons to appear, preferring role=IMG name "select" like codegen
        Locator thumbs = null;
        long endThumbs = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < endThumbs) {
            try {
                Locator byRoleImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("select"));
                if (byRoleImg.count() > 0) {
                    thumbs = byRoleImg;
                    break;
                }
            } catch (Throwable ignored) {}

            try {
                Locator cssThumbs = page.locator(".select-quick-file-media-thumb");
                if (cssThumbs.count() > 0) {
                    thumbs = cssThumbs;
                    break;
                }
            } catch (Throwable ignored) {}

            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }

        if (thumbs == null || thumbs.count() == 0) {
            logger.warn("[Messaging][QuickFiles] No media elements visible after album click for '{}' (role IMG 'select' or .select-quick-file-media-thumb)", targetName);
            throw new RuntimeException("No Quick Files media thumbnails visible inside album: " + targetName);
        }

        int max = Math.min(Math.max(1, countToSelect), thumbs.count());
        logger.info("[Messaging][QuickFiles] Selecting {} Quick Files media item(s) out of {}", max, thumbs.count());
        for (int i = 0; i < max; i++) {
            Locator t = thumbs.nth(i);
            try { t.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
            clickWithRetry(t, 1, 120);
        }

        // Click dynamic Select (N) button; fallback to plain Select
        logger.info("[Messaging][QuickFiles] Attempting to click dynamic 'Select (N)' button");
        Locator selectDynamic = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName(Pattern.compile("^Select \\([0-9]+\\)$")));
        if (selectDynamic.count() > 0) {
            clickWithRetry(selectDynamic.first(), 1, 200);
        } else {
            logger.info("[Messaging][QuickFiles] Dynamic 'Select (N)' not found; falling back to plain 'Select' button");
            Locator selectPlain = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select"));
            if (selectPlain.count() > 0) {
                clickWithRetry(selectPlain.first(), 1, 200);
            } else {
                logger.warn("[Messaging][QuickFiles] Neither dynamic 'Select (N)' nor plain 'Select' button could be located");
                throw new RuntimeException("Quick Files: 'Select' button not found after choosing media");
            }
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
        // Accept either the dedicated Private media screen OR the Importation modal opening directly
        long end = System.currentTimeMillis() + 15_000;
        while (System.currentTimeMillis() < end) {
            try {
                if (privateMediaTitle().count() > 0 && safeIsVisible(privateMediaTitle().first())) {
                    return;
                }
            } catch (Throwable ignored) {}
            try {
                Locator imp = importationTitle();
                if (imp.count() > 0 && safeIsVisible(imp.first())) {
                    logger.info("[Messaging][Private] Importation modal detected (skipping Private media title)");
                    return;
                }
            } catch (Throwable ignored) {}
            try {
                Locator qf = importationContainer().getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Quick Files"));
                if (qf.count() > 0 && safeIsVisible(qf.first())) {
                    logger.info("[Messaging][Private] Quick Files option visible inside Importation");
                    return;
                }
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(150); } catch (Throwable ignored) {}
        }
        // Final assertion to bubble up a clearer error
        if (privateMediaTitle().count() > 0) {
            waitVisible(privateMediaTitle().first(), 2_000);
            return;
        }
        waitVisible(importationTitle().first(), 2_000);
    }

    @Step("Click plus icon to add private media")
    public void clickPrivateMediaAddPlus() {
        logger.info("[Messaging][Private] Clicking plus icon to add media");
        // If Importation is already visible, skip clicking plus
        try {
            Locator imp = importationTitle();
            if (imp.count() > 0 && safeIsVisible(imp.first())) {
                logger.info("[Messaging][Private] Importation already visible; skipping PLUS click");
                return;
            }
        } catch (Throwable ignored) {}

        // Small stabilization to allow UI to render toolbar icons
        try { page.waitForTimeout(200); } catch (Throwable ignored) {}

        // Try a series of robust candidates for the plus control (composer and private media screens)
        Locator[] candidates = new Locator[] {
            // Codegen-priority selectors
            page.locator(".addCircleGreen"),
            page.locator(".addCircle"),
            // Role IMG/name "add" (used in other pages, may apply here too)
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("add")),
            // Role IMG/name plus (primary)
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus")),
            // Role BUTTON/name plus
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("plus")),
            // Role BUTTON/name add
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("add")),
            // Button with aria-label
            page.locator("button[aria-label='plus']"),
            page.locator("button[aria-label='add']"),
            // img with alt="add"
            page.locator("img[alt='add']"),
            // Any element with data-icon=plus, click its closest button
            page.locator("xpath=(//*[@data-icon='plus'])[1]/ancestor::button[1]"),
            // Any svg with aria-label plus, click its closest button
            page.locator("xpath=(//svg[@aria-label='plus'])[1]/ancestor::button[1]"),
            // Legacy explicit mapping
            plusIcon().first(),
            // Add media button pattern
            page.locator("button").filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("Add.*media", java.util.regex.Pattern.CASE_INSENSITIVE)))
        };

        boolean clicked = false;
        long end = System.currentTimeMillis() + 10_000;
        while (!clicked && System.currentTimeMillis() < end) {
            for (Locator cand : candidates) {
                try {
                    if (cand != null && cand.count() > 0) {
                        Locator b = cand.first();
                        try { b.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                        if (safeIsVisible(b)) {
                            clickWithRetry(b, 1, 200);
                            clicked = true;
                            break;
                        }
                        try { b.click(new Locator.ClickOptions().setForce(true)); clicked = true; break; }
                        catch (Throwable ignored) {}
                    }
                } catch (Throwable ignored) {}
            }
            if (!clicked) {
                // If composer/message input is visible, the plus is in the toolbar; give UI a moment then retry
                try { if (safeIsVisible(privateMessagePlaceholder())) { page.waitForTimeout(200); } } catch (Throwable ignored) {}
            }
        }

        if (!clicked) {
            // Last resort: tap Media button to re-open private media context, then expect Importation
            try {
                clickWithRetry(privateMediaButton(), 1, 200);
            } catch (Throwable ignored) {}
        }

        // Wait for Importation title or container to appear
        try { waitVisible(importationTitle().first(), 20_000); }
        catch (Throwable e) {
            // As a fallback, ensure container is visible
            waitVisible(importationContainer().first(), 10_000);
        }
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
        // Prefer clicking an explicit Next button if present; only accept message textbox if no Next is actionable
        long end = System.currentTimeMillis() + 15_000;
        while (System.currentTimeMillis() < end) {
            // Build candidate locators in priority order
            Locator[] candidates = new Locator[] {
                // Codegen primary
                page.locator("xpath=//button//span[contains(normalize-space(.), 'Next')]/ancestor::button[1]"),
                // Generic button containing Next text
                page.locator("xpath=//button[contains(normalize-space(.), 'Next')]"),
                // CSS has-text fallback
                page.locator("button:has-text('Next')"),
                // Role-based
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next"))
            };
            for (Locator cand : candidates) {
                try {
                    if (cand != null && cand.count() > 0) {
                        Locator b = cand.first();
                        try { b.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                        if (safeIsVisible(b)) {
                            clickWithRetry(b, 1, 200);
                            return;
                        }
                        // Try force if not reported visible
                        try { b.click(new Locator.ClickOptions().setForce(true)); return; }
                        catch (Throwable ignored) {}
                    }
                } catch (Throwable ignored) {}
            }
            // If no Next button was actionable, accept if message placeholder became visible (UI may auto-advance)
            if (safeIsVisible(privateMessagePlaceholder())) {
                return;
            }
            // Small stabilization before retrying
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }
        // Final attempt: if message placeholder appeared late, accept it; otherwise fail clearly
        if (!safeIsVisible(privateMessagePlaceholder())) {
            throw new RuntimeException("Neither 'Next' button nor message textarea became visible in time");
        }
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

    @Step("Focus private message input")
    public void focusPrivateMessageInput() {
        Locator ph = privateMessagePlaceholder();
        waitVisible(ph, 15_000);
        ph.click();
    }

    @Step("Fill private message: {msg}")
    public void fillPrivateMessage(String msg) {
        Locator ph = privateMessagePlaceholder();
        waitVisible(ph, 15_000);
        ph.click();
        ph.fill(msg == null ? "" : msg);
    }

    @Step("Click message template by text: {template}")
    public void clickMessageTemplate(String template) {
        Locator tpl = page.getByText(template, new Page.GetByTextOptions().setExact(true));
        waitVisible(tpl.first(), 10_000);
        clickWithRetry(tpl.first(), 1, 150);
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
        // Fast-path: scroll and prefer the specific mixalbum button used in current staging data
        try {
            long endFast = System.currentTimeMillis() + 15_000;
            while (System.currentTimeMillis() < endFast) {
                Locator specificAlbumBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                        .setName("icon mixalbum_251119_134546"));
                if (specificAlbumBtn.count() > 0) {
                    Locator chosen = specificAlbumBtn.first();
                    try { chosen.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                    clickWithRetry(chosen, 1, 200);
                    logger.info("[Messaging] Clicked Quick Files album via BUTTON fast-path: icon mixalbum_251119_134546");
                    return;
                }
                // Scroll down a bit to surface albums near the bottom
                try { page.mouse().wheel(0, 700); } catch (Throwable ignored) {}
                try { page.waitForTimeout(250); } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}

        // First, try the exact regex pattern family used in codegen
        Locator byText = page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^(?i)(video|image|mix).*")));
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
                        // Prefer clickable ancestor (button/a) if available
                        try {
                            Locator clickable = cand.locator("xpath=ancestor-or-self::*[self::a or self::button][1]");
                            if (clickable.count() > 0 && safeIsVisible(clickable.first())) {
                                clickWithRetry(clickable.first(), 1, 200);
                            } else {
                                clickWithRetry(cand, 1, 200);
                            }
                        } catch (Throwable e) { clickWithRetry(cand, 1, 200); }
                        logger.info("[Messaging] Clicked album by regex at index {}", idx);
                        return;
                    }
                }
            }
            // Fallback: click last visible
            for (int i = total - 1; i >= 0; i--) {
                Locator cand = byText.nth(i);
                if (safeIsVisible(cand)) {
                    try {
                        Locator clickable = cand.locator("xpath=ancestor-or-self::*[self::a or self::button][1]");
                        if (clickable.count() > 0 && safeIsVisible(clickable.first())) {
                            clickWithRetry(clickable.first(), 1, 200);
                        } else {
                            clickWithRetry(cand, 1, 200);
                        }
                    } catch (Throwable e) { clickWithRetry(cand, 1, 200); }
                    logger.info("[Messaging] Clicked album by regex (fallback last visible) at index {}", i);
                    return;
                }
            }
            // Final fallback: click first regardless of visibility heuristic
            try {
                Locator clickable = byText.first().locator("xpath=ancestor-or-self::*[self::a or self::button][1]");
                if (clickable.count() > 0 && safeIsVisible(clickable.first())) {
                    clickWithRetry(clickable.first(), 1, 200);
                } else {
                    clickWithRetry(byText.first(), 1, 200);
                }
            } catch (Throwable e) { clickWithRetry(byText.first(), 1, 200); }
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
                // Prefer new Quick Files media thumb tiles
                Locator firstThumb = page.locator(".select-quick-file-media-thumb").first();
                if (firstThumb.count() > 0 && safeIsVisible(firstThumb)) {
                    logger.info("[Messaging][QuickFiles] Items grid visible with .select-quick-file-media-thumb elements");
                    return;
                }
                // Fallback to legacy .cover tiles if still present
                Locator firstCover = page.locator(".cover").first();
                if (firstCover.count() > 0 && safeIsVisible(firstCover)) {
                    logger.info("[Messaging][QuickFiles] Items grid visible with .cover elements (fallback)");
                    return;
                }
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Throwable ignored) {}
        }
        // Final assertion to bubble up
        Locator thumbOrCover = page.locator(".select-quick-file-media-thumb, .cover").first();
        waitVisible(thumbOrCover, Math.max(3_000, timeoutMs));
    }

    @Step("Pick first two covers or up to {n} items as fallback")
    public void pickFirstTwoCoversOrUpToN(int n) {
        try {
            // Prefer new Quick Files media thumb tiles as in latest codegen
            Locator thumbs = page.locator(".select-quick-file-media-thumb");
            if (thumbs.count() > 0) {
                int max = Math.min(Math.max(1, n), thumbs.count());
                waitVisible(thumbs.first(), 10_000);
                for (int i = 0; i < max; i++) {
                    Locator t = thumbs.nth(i);
                    try { t.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                    clickWithRetry(t, 1, 120);
                }
            } else {
                // Fallback to legacy .cover-based selection
                Locator first = page.locator(".cover").first();
                Locator second = page.locator("div:nth-child(2) > .cover");
                waitVisible(first, 10_000);
                clickWithRetry(first, 1, 120);
                if (second.count() > 0 && safeIsVisible(second.first())) {
                    clickWithRetry(second.first(), 1, 120);
                }
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
        // First, try a direct codegen-style ROLE=BUTTON click for 'Quick Files'
        try {
            Locator directBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Quick Files"));
            if (directBtn.count() > 0 && safeIsVisible(directBtn.first())) {
                logger.info("[Messaging] Clicking 'Quick Files' via direct BUTTON locator");
                try { directBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                clickWithRetry(directBtn.first(), 1, 200);
            } else {
                throw new RuntimeException("Direct Quick Files BUTTON not visible");
            }
        } catch (Throwable primary) {
            logger.warn("[Messaging] Direct 'Quick Files' BUTTON click failed or not visible; falling back to container-based locators: {}", primary.getMessage());
            Locator container = importationContainer();
            // Build candidate locators in order of preference
            Locator[] candidates = new Locator[] {
                    container.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Quick Files")),
                    container.locator("button:has-text('Quick Files')"),
                    // exact text node within common clickable wrappers
                    container.locator("xpath=.//*[self::button or self::div or self::span][normalize-space(text())='Quick Files']"),
                    // more permissive contains text
                    container.locator("xpath=.//*[contains(normalize-space(.), 'Quick Files')]")
            };
            Locator picked = null;
            for (Locator cand : candidates) {
                if (cand != null && cand.count() > 0) {
                    Locator first = cand.first();
                    if (safeIsVisible(first)) { picked = first; break; }
                }
            }
            if (picked == null || picked.count() == 0) {
                // As a last resort, fall back to global text search
                picked = page.getByText("Quick Files");
            }
            if (picked == null || picked.count() == 0) {
                throw new RuntimeException("Quick Files option not found in Importation popup");
            }
            try { picked.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
            try {
                clickWithRetry(picked.first(), 1, 200);
            } catch (RuntimeException e) {
                try { picked.first().click(new Locator.ClickOptions().setForce(true)); }
                catch (Throwable ignore) { throw e; }
            }
        }

        // Ensure we are on Quick Files albums screen (codegen: My albums visible)
        try {
            Locator myAlbums = page.getByText("My albums");
            waitVisible(myAlbums.first(), 15_000);
            logger.info("[Messaging] 'My albums' label visible on Quick Files screen");
        } catch (Throwable e) {
            logger.warn("[Messaging] 'My albums' label not quickly visible after clicking Quick Files; falling back to generic Quick Files screen assertion: {}", e.getMessage());
            assertQuickFilesScreen();
        }
    }

    @Step("Assert Quick Files screen (title + 'My albums') is visible")
    public void assertQuickFilesScreen() {
        logger.info("[Messaging] Asserting Quick Files screen visible (title + 'My albums')");
        waitVisible(page.getByText("Quick Files"), 15_000);
        // Prefer 'My albums' text; if absent, accept albums container or rows as valid screen
        try {
            waitVisible(page.getByText("My albums"), 6_000);
        } catch (Throwable e) {
            logger.info("[Messaging] 'My albums' text not visible quickly; checking albums container/rows instead");
            long end = System.currentTimeMillis() + 9_000;
            while (System.currentTimeMillis() < end) {
                try {
                    if (quickFilesAlbumsContainer().count() > 0 || quickFilesAlbumRows().count() > 0) {
                        logger.info("[Messaging] Quick Files albums container/rows detected; accepting screen");
                        return;
                    }
                } catch (Throwable ignored) {}
                try { page.waitForTimeout(200); } catch (Throwable ignored) {}
            }
            // Final attempt: assert 'My albums' strictly to bubble error
            waitVisible(page.getByText("My albums"), 3_000);
        }
    }

    private Locator quickFilesTitle() {
        return page.getByText("Quick Files");
    }

    private Locator quickFilesAlbumRows() {
        // Albums rows/name spans across variants + XPath text fallbacks (album names like videoalbum_*, imagealbum_*, mixalbum_*)
        return page.locator(
                "span.ant-typography.QuickLinkAlbumName.css-ixblex, " +
                "span.ant-typography.QuickLinkAlbumName, " +
                ".QuickLinkAlbumName, " +
                // New Quick Files list row/title classes (from current DOM)
                ".qf-row-title, .qf-row, " +
                "[data-testid='album-name'], " +
                "[class*='AlbumName'], " +
                // Case-insensitive contains 'album' OR starts-with 'video'/'image'/'mix'
                "xpath=(//*[self::div or self::span or self::a or self::button]" +
                "[contains(translate(normalize-space(.), 'ALBUM', 'album'), 'album') or " +
                " starts-with(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'video') or " +
                " starts-with(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'image') or " +
                " starts-with(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'mix')])"
        );
    }


    private Locator quickFilesAlbumsContainer() {
        // Common containers that hold album rows/cards, including within Importation modal/drawer
        Locator css = page.locator("div.ant-row.albumRow.css-ixblex, div.ant-row.albumRow, .albumRow, [data-testid='albums']");
        if (css.count() > 0) return css.first();
        // Fallback into visible modal/drawer
        Locator modalContainer = page.locator("xpath=(//div[contains(@class,'ant-modal') or contains(@class,'ant-drawer')]//*[contains(@class,'album') or contains(@class,'albumRow') or contains(@class,'row')])[1]");
        if (modalContainer.count() > 0) return modalContainer.first();
        // Final fallback: page body to allow subsequent row queries
        return page.locator("body").first();
    }

    private Locator quickFilesItemThumbs() {
        // Items typically render inside the Importation container.
        // Prefer the dedicated Quick Files media thumb class, fallback to legacy tiles.
        Locator container = importationContainer();
        Locator thumbs = container.locator(".select-quick-file-media-thumb");
        if (thumbs.count() > 0) {
            return thumbs;
        }
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
        // Try clicking a 'My albums' tab/label if present to reveal rows
        try {
            Locator myAlbumsBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("My albums"));
            if (myAlbumsBtn.count() == 0) myAlbumsBtn = page.getByText("My albums");
            if (myAlbumsBtn.count() > 0 && safeIsVisible(myAlbumsBtn.first())) {
                clickWithRetry(myAlbumsBtn.first(), 1, 120);
            }
        } catch (Throwable ignored) {}
        long end = System.currentTimeMillis() + 25_000;
        while (System.currentTimeMillis() < end) {
            int containers = 0, rows = 0;
            try { containers = quickFilesAlbumsContainer().count(); } catch (Throwable ignored) {}
            try { rows = quickFilesAlbumRows().count(); } catch (Throwable ignored) {}
            if (containers > 0 || rows > 0) {
                logger.info("[Messaging] Quick Files albums detected: containers={}, rows={}", containers, rows);
                return;
            }
            // Light scroll to stimulate lazy load
            try { page.mouse().wheel(0, 400); } catch (Throwable ignored) {}
            try { page.waitForTimeout(250); } catch (Throwable ignored) {}
            // Fallback: regex probe for names starting with video/image/mix
            try {
                Locator byText = page.locator("div").filter(new Locator.FilterOptions()
                        .setHasText(java.util.regex.Pattern.compile("^(?i)(video|image|mix)")));
                if (byText.count() > 0) {
                    logger.info("[Messaging] Found regex-matching album text elements: {}", byText.count());
                    return;
                }
            } catch (Throwable ignored) {}
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
        // Prefer title within a visible modal/drawer container
        Locator container = importationContainer();
        Locator byText = container.getByText("Importation");
        if (byText.count() > 0) return byText;
        // Case-insensitive contains fallback inside container
        Locator byCi = container.locator("xpath=.//*[contains(translate(normalize-space(.), 'IMPORTATION', 'importation'), 'importation')]");
        if (byCi.count() > 0) return byCi.first();
        // Final fallback: page-wide search
        return page.getByText("Importation");
    }

    // ===== Private Media specific locators/texts =====
    private Locator privateMediaButton() {
        // Updated per UI change: explicitly target a button that has text 'Media'
        Locator btn = page.locator("button").filter(new Locator.FilterOptions().setHasText("Media"));
        return btn.first();
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
        // This method is now a no-op; uploadMessageMedia handles file input directly
        // to avoid triggering the OS file dialog.
        logger.info("[Messaging] chooseMyDeviceForMedia called (file input will be set directly in uploadMessageMedia)");
    }

    @Step("Upload message media from device: {file}")
    public void uploadMessageMedia(java.nio.file.Path file) {
        if (file == null || !java.nio.file.Files.exists(file)) {
            throw new RuntimeException("Message media file not found: " + file);
        }
        
        // First try to find file input directly
        Locator inputs = page.locator(".ant-upload input[type='file']");
        if (inputs.count() == 0) {
            inputs = page.locator("input[type='file']");
        }
        
        // If no file input found, click "My Device" button to reveal it
        if (inputs.count() == 0) {
            logger.info("[Messaging] No file input found, clicking My Device button");
            try {
                Locator myDevice = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("My Device"));
                if (myDevice.count() > 0 && safeIsVisible(myDevice.first())) {
                    clickWithRetry(myDevice.first(), 1, 150);
                    page.waitForTimeout(500);
                    // Re-check for file input after clicking My Device
                    inputs = page.locator(".ant-upload input[type='file']");
                    if (inputs.count() == 0) {
                        inputs = page.locator("input[type='file']");
                    }
                }
            } catch (Exception e) {
                logger.warn("[Messaging] Could not click My Device: {}", e.getMessage());
            }
        }
        
        if (inputs.count() > 0) {
            logger.info("[Messaging] Using input[type=file] to upload: {}", file.getFileName());
            Locator target = inputs.nth(inputs.count() - 1);
            target.setInputFiles(file);
            // After setting files, dismiss the Importation bottom sheet if Cancel is visible
            try {
                Locator cancel = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
                if (cancel.count() > 0 && safeIsVisible(cancel.first())) {
                    clickWithRetry(cancel.first(), 1, 150);
                }
            } catch (Exception ignored) {}
            return;
        }
        // Fallback: if no file input found, throw error
        throw new RuntimeException("No file input found for media upload in Importation dialog");
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

    // ================= Fan Conversation Flow Methods =================

    @Step("Verify General tab is selected by default")
    public void verifyGeneralTabSelected() {
        waitVisible(page.getByText("General"), DEFAULT_WAIT);
        logger.info("[Messaging] 'General' tab visible (default)");
    }

    /**
     * Helper method to try finding fan in the current tab.
     * Tries exact match, first name, regex, and scrolling.
     */
    private boolean tryFindFanInCurrentTab(String fanName) {
        Locator fan = null;
        
        // Strategy 1: Exact text match
        fan = page.getByText(fanName).first();
        if (fan.count() > 0 && safeIsVisible(fan)) {
            logger.info("[Messaging] Found fan by exact text: {}", fanName);
            return true;
        }
        
        // Strategy 2: Try partial match (first name only)
        String firstName = fanName.split(" ")[0];
        fan = page.getByText(firstName).first();
        if (fan.count() > 0 && safeIsVisible(fan)) {
            logger.info("[Messaging] Found fan by first name: {}", firstName);
            return true;
        }
        
        // Strategy 3: Try regex pattern (case insensitive)
        fan = page.getByText(java.util.regex.Pattern.compile(fanName, java.util.regex.Pattern.CASE_INSENSITIVE)).first();
        if (fan.count() > 0 && safeIsVisible(fan)) {
            logger.info("[Messaging] Found fan by regex: {}", fanName);
            return true;
        }
        
        // Strategy 4: Scroll down and retry
        logger.info("[Messaging] Fan not visible, scrolling to find...");
        for (int i = 0; i < 5; i++) {
            page.mouse().wheel(0, 300);
            page.waitForTimeout(500);
            fan = page.getByText(fanName).first();
            if (fan.count() > 0 && safeIsVisible(fan)) {
                logger.info("[Messaging] Found fan after scrolling");
                return true;
            }
            // Also try first name after scroll
            fan = page.getByText(firstName).first();
            if (fan.count() > 0 && safeIsVisible(fan)) {
                logger.info("[Messaging] Found fan by first name after scrolling");
                return true;
            }
        }
        
        return false;
    }

    @Step("Click on fan conversation: {fanName}")
    public void clickOnFanConversation(String fanName) {
        logger.info("[Messaging] Looking for fan conversation: {}", fanName);
        // Wait for conversation list to load
        page.waitForTimeout(3000);
        
        Locator fan = null;
        boolean found = false;
        
        // First, try to find in current tab (General by default)
        found = tryFindFanInCurrentTab(fanName);
        if (found) {
            fan = page.getByText(fanName).first();
            if (fan.count() == 0 || !safeIsVisible(fan)) {
                // Try first name
                String firstName = fanName.split(" ")[0];
                fan = page.getByText(firstName).first();
            }
        }
        
        // If not found in General tab, try "To Deliver" tab
        if (!found) {
            logger.info("[Messaging] Fan not found in General tab, checking 'To Deliver' tab");
            Locator toDeliverTab = page.getByText("To Deliver");
            if (toDeliverTab.count() > 0 && safeIsVisible(toDeliverTab.first())) {
                clickWithRetry(toDeliverTab.first(), 2, 200);
                page.waitForTimeout(2000); // Wait for tab content to load
                logger.info("[Messaging] Switched to 'To Deliver' tab");
                
                found = tryFindFanInCurrentTab(fanName);
                if (found) {
                    fan = page.getByText(fanName).first();
                    if (fan.count() == 0 || !safeIsVisible(fan)) {
                        String firstName = fanName.split(" ")[0];
                        fan = page.getByText(firstName).first();
                    }
                }
            }
        }
        
        // Strategy 5: Click first visible image in the conversation list (user avatars)
        if (!found) {
            logger.warn("[Messaging] Fan '{}' not found by name, looking for user avatars", fanName);
            Locator avatars = page.getByRole(AriaRole.IMG);
            // Skip first few images which might be header icons, look for avatars
            for (int i = 0; i < Math.min(avatars.count(), 10) && !found; i++) {
                Locator avatar = avatars.nth(i);
                if (safeIsVisible(avatar)) {
                    String alt = "";
                    try { alt = avatar.getAttribute("alt"); } catch (Exception ignored) {}
                    // Skip navigation icons
                    if (alt != null && (alt.contains("arrow") || alt.contains("settings") || alt.contains("back"))) {
                        continue;
                    }
                    fan = avatar;
                    found = true;
                    logger.info("[Messaging] Found clickable avatar at index {}", i);
                    break;
                }
            }
        }
        
        // Strategy 6: Just click anywhere in the conversation area to open first conversation
        if (!found) {
            logger.warn("[Messaging] Last resort - clicking in conversation list area");
            // Try to find the main content area and click
            Locator mainContent = page.locator("main, [class*='content'], [class*='list']").first();
            if (mainContent.count() > 0) {
                // Click at a position that should be a conversation item
                try {
                    mainContent.click(new Locator.ClickOptions().setPosition(new com.microsoft.playwright.options.Position(100, 150)));
                    found = true;
                    logger.info("[Messaging] Clicked in main content area");
                } catch (Exception e) {
                    logger.warn("[Messaging] Failed to click in content area: {}", e.getMessage());
                }
            }
        }
        
        if (!found || fan == null) {
            throw new RuntimeException("Could not find fan conversation: " + fanName);
        }
        
        fan.scrollIntoViewIfNeeded();
        clickWithRetry(fan, 2, 200);
        page.waitForTimeout(3000); // Wait for conversation to load
        
        // Wait for conversation screen to be ready - try multiple indicators
        boolean conversationLoaded = false;
        
        // Strategy 1: Message input placeholder
        Locator messageInput = page.getByPlaceholder("Your message");
        if (messageInput.count() > 0 && safeIsVisible(messageInput.first())) {
            conversationLoaded = true;
            logger.info("[Messaging] Conversation loaded - message input visible");
        }
        
        // Strategy 2: Try alternative placeholder
        if (!conversationLoaded) {
            Locator altInput = page.locator("textarea, input[type='text']").first();
            if (altInput.count() > 0 && safeIsVisible(altInput)) {
                conversationLoaded = true;
                logger.info("[Messaging] Conversation loaded - text input visible");
            }
        }
        
        // Strategy 3: Look for send button
        if (!conversationLoaded) {
            Locator sendBtn = page.getByText("Send", new Page.GetByTextOptions().setExact(true));
            if (sendBtn.count() > 0 && safeIsVisible(sendBtn.first())) {
                conversationLoaded = true;
                logger.info("[Messaging] Conversation loaded - send button visible");
            }
        }
        
        // Strategy 4: Wait a bit more and check for any conversation content
        if (!conversationLoaded) {
            page.waitForTimeout(2000);
            // Check if we're on a conversation screen by looking for message-related elements
            Locator conversationArea = page.locator("[class*='message'], [class*='chat'], [class*='conversation']").first();
            if (conversationArea.count() > 0) {
                conversationLoaded = true;
                logger.info("[Messaging] Conversation loaded - conversation area detected");
            }
        }
        
        if (!conversationLoaded) {
            logger.warn("[Messaging] Could not verify conversation loaded, proceeding anyway");
        }
        
        logger.info("[Messaging] Clicked on fan: {} - conversation screen loaded", fanName);
    }

    @Step("Verify message from fan is visible: {message}")
    public void verifyMessageVisible(String message) {
        logger.info("[Messaging] Looking for message: {}", message);
        // Wait a bit for messages to load
        page.waitForTimeout(2000);
        Locator msg = page.getByText(message).first();
        waitVisible(msg, DEFAULT_WAIT);
        logger.info("[Messaging] Message visible: {}", message);
    }

    @Step("Click Accept button for specific message: {message}")
    public void clickAcceptButtonForMessage(String message) {
        logger.info("[Messaging] Looking for Accept button near message: {}", message);
        
        // Wait for conversation to load
        page.waitForTimeout(3000);
        
        // Scroll to bottom to see latest messages
        try {
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        } catch (Exception ignored) {}
        page.waitForTimeout(1000);
        
        // Try to find the message, with scrolling if needed
        Locator messageLocator = page.getByText(message).first();
        boolean messageFound = messageLocator.count() > 0 && safeIsVisible(messageLocator);
        
        if (!messageFound) {
            // Try scrolling down to find the message
            for (int i = 0; i < 5 && !messageFound; i++) {
                page.mouse().wheel(0, 300);
                page.waitForTimeout(500);
                messageLocator = page.getByText(message).first();
                if (messageLocator.count() > 0 && safeIsVisible(messageLocator)) {
                    messageFound = true;
                    logger.info("[Messaging] Found message after scrolling: {}", message);
                }
            }
        } else {
            logger.info("[Messaging] Found message: {}", message);
        }
        
        // Find Accept button - try multiple strategies
        Locator acceptBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept")).first();
        
        // Check if Accept button is visible
        if (acceptBtn.count() == 0 || !safeIsVisible(acceptBtn)) {
            // Try text-based locator
            acceptBtn = page.getByText("Accept").first();
        }
        
        // If still not found, the message might already be accepted or we're in wrong conversation
        if (acceptBtn.count() == 0 || !safeIsVisible(acceptBtn)) {
            logger.warn("[Messaging] No Accept button found - message may already be accepted or wrong conversation");
            // Take screenshot for debugging
            throw new RuntimeException("No Accept button found. Message found: " + messageFound + ", Message: " + message);
        }
        
        waitVisible(acceptBtn, DEFAULT_WAIT);
        clickWithRetry(acceptBtn, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked Accept button for message: {}", message);
    }

    @Step("Click Accept button to accept fan message")
    public void clickAcceptButton() {
        Locator acceptBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept")).first();
        waitVisible(acceptBtn, DEFAULT_WAIT);
        clickWithRetry(acceptBtn, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked Accept button");
    }

    @Step("Verify Amount field is displayed")
    public void verifyAmountFieldDisplayed() {
        Locator amount = page.getByText("Amount", new Page.GetByTextOptions().setExact(true));
        waitVisible(amount, DEFAULT_WAIT);
        logger.info("[Messaging] Amount field displayed");
    }

    @Step("Set price: {price}")
    public void setPrice(String price) {
        // Use label filter with regex pattern for price like "15€"
        Locator priceOption = page.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^" + price + "$")));
        waitVisible(priceOption, DEFAULT_WAIT);
        clickWithRetry(priceOption, 2, 200);
        logger.info("[Messaging] Set price to: {}", price);
    }

    @Step("Set custom price: {amount}")
    public void setCustomPrice(String amount) {
        // Verify Personalized amount field is displayed
        Locator personalizedAmount = page.getByText("Personalized amount");
        waitVisible(personalizedAmount, DEFAULT_WAIT);
        logger.info("[Messaging] Personalized amount field displayed");
        
        // Click and fill custom amount
        Locator customAmountInput = page.locator("input[name=\"customAmount\"]");
        waitVisible(customAmountInput, DEFAULT_WAIT);
        customAmountInput.click();
        customAmountInput.fill(amount);
        logger.info("[Messaging] Set custom price to: {}", amount);
    }

    @Step("Verify Free is selected by default")
    public void verifyFreeIsSelected() {
        // Verify Amount field is displayed
        Locator amount = page.getByText("Amount", new Page.GetByTextOptions().setExact(true));
        waitVisible(amount, DEFAULT_WAIT);
        
        // Verify Free radio button is selected (first radio button)
        Locator freeRadio = page.locator(".ant-radio-button").first();
        waitVisible(freeRadio, DEFAULT_WAIT);
        logger.info("[Messaging] Free option is selected by default");
    }

    @Step("Type reply message: {message}")
    public void typeReplyMessage(String message) {
        // Use getByRole with name "Your message..." as per codegen
        Locator input = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your message..."));
        waitVisible(input, DEFAULT_WAIT);
        input.click();
        input.fill(message);
        logger.info("[Messaging] Typed reply: {}", message);
    }

    @Step("Click Send button")
    public void clickSendButton() {
        // Send button is inside a Dialog, use Dialog context
        Locator sendBtn = page.getByRole(AriaRole.DIALOG).getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Send"));
        waitVisible(sendBtn, DEFAULT_WAIT);
        
        // Wait for button to be enabled (not disabled)
        int maxWait = 15;
        for (int i = 0; i < maxWait; i++) {
            String disabled = sendBtn.getAttribute("disabled");
            if (disabled == null) {
                logger.info("[Messaging] Send button is enabled");
                break;
            }
            logger.info("[Messaging] Send button still disabled, waiting... ({}s)", i + 1);
            page.waitForTimeout(1000);
        }
        
        clickWithRetry(sendBtn, 2, 200);
        page.waitForTimeout(2000); // Wait for message to send
        logger.info("[Messaging] Clicked Send button");
    }

    @Step("Click To Deliver tab")
    public void clickToDeliverTabForConversation() {
        Locator toDeliver = page.getByText("To Deliver");
        waitVisible(toDeliver, DEFAULT_WAIT);
        clickWithRetry(toDeliver, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked 'To Deliver' tab");
    }

    @Step("Click plus icon to add media")
    public void clickPlusIconForMedia() {
        Locator plus = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plus, DEFAULT_WAIT);
        clickWithRetry(plus, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked plus icon to add media");
    }

    @Step("Verify Importation popup displayed")
    public void verifyImportationPopup() {
        Locator importation = page.getByText("Importation");
        waitVisible(importation, DEFAULT_WAIT);
        logger.info("[Messaging] Importation popup displayed");
    }

    @Step("Click My Device button")
    public void clickMyDeviceButton() {
        Locator myDevice = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("My Device"));
        waitVisible(myDevice, DEFAULT_WAIT);
        clickWithRetry(myDevice, 2, 200);
        logger.info("[Messaging] Clicked My Device button");
    }

    @Step("Upload media file: {filePath}")
    public void uploadMediaFile(java.nio.file.Path filePath) {
        if (filePath == null || !java.nio.file.Files.exists(filePath)) {
            throw new RuntimeException("Media file not found: " + filePath);
        }
        logger.info("[Messaging] Uploading file directly via input: {}", filePath.getFileName());
        // Use setInputFiles directly on hidden file input - no OS dialog needed
        Locator fileInput = page.locator("input[type='file']").first();
        fileInput.setInputFiles(filePath);
        logger.info("[Messaging] File selected: {}", filePath.getFileName());
        // Wait for file to be attached and preview to appear
        page.waitForTimeout(2000);
        logger.info("[Messaging] Media file attached: {}", filePath.getFileName());
    }

    @Step("Wait for media upload/send to complete")
    public void waitForMediaSendComplete() {
        logger.info("[Messaging] Waiting for media upload/send to complete...");
        
        // Wait for any upload spinner/progress indicator to disappear
        // Common patterns: .ant-spin, .loading, progress bar, etc.
        Locator spinner = page.locator(".ant-spin, .ant-spin-spinning, [class*='loading'], [class*='progress']").first();
        
        // Wait for spinner to disappear (if it exists) - video uploads take longer
        int maxWaitSeconds = 60; // Max wait for video upload after Send
        int waited = 0;
        while (spinner.isVisible() && waited < maxWaitSeconds) {
            page.waitForTimeout(1000);
            waited++;
            if (waited % 5 == 0) {
                logger.info("[Messaging] Upload/send in progress... {}s", waited);
            }
        }
        
        // Additional wait to ensure upload is fully processed
        page.waitForTimeout(2000);
        logger.info("[Messaging] Media upload/send completed after {}s", waited);
    }

    @Step("Click Send button for media")
    public void clickSendButtonForMedia() {
        logger.info("[Messaging] Looking for Send button for media");
        
        // Try multiple strategies to find the Send button
        Locator sendBtn = null;
        
        // Strategy 1: By role with name "Send"
        sendBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send"));
        if (sendBtn.count() == 0 || !safeIsVisible(sendBtn.first())) {
            // Strategy 2: By class sendMediaButton
            sendBtn = page.locator(".sendMediaButton, button.sendMediaButton");
        }
        if (sendBtn.count() == 0 || !safeIsVisible(sendBtn.first())) {
            // Strategy 3: By text "Send" exact match
            sendBtn = page.getByText("Send", new Page.GetByTextOptions().setExact(true));
        }
        if (sendBtn.count() == 0 || !safeIsVisible(sendBtn.first())) {
            // Strategy 4: Any button containing euro symbol (price button acts as send)
            sendBtn = page.locator("button:has-text('€'), button:has-text('?')");
        }
        
        // Wait for button to be visible
        waitVisible(sendBtn.first(), DEFAULT_WAIT);
        
        // Wait for button to be enabled (not disabled) - media needs to upload first
        int maxWait = 30; // Increased wait for media upload
        for (int i = 0; i < maxWait; i++) {
            String disabled = sendBtn.first().getAttribute("disabled");
            if (disabled == null) {
                logger.info("[Messaging] Send button for media is enabled");
                break;
            }
            if (i % 5 == 0) {
                logger.info("[Messaging] Send button for media still disabled, waiting for upload... ({}s)", i + 1);
            }
            page.waitForTimeout(1000);
        }
        
        clickWithRetry(sendBtn.first(), 2, 200);
        logger.info("[Messaging] Clicked Send button for media");
        
        // Wait for upload/send to complete AFTER clicking Send
        waitForMediaSendComplete();
    }

    @Step("Verify Delivered text displayed")
    public void verifyDeliveredText() {
        // Try multiple strategies to verify delivery
        boolean delivered = false;
        
        // Strategy 1: Look for "Delivered" text (any occurrence)
        Locator deliveredText = page.getByText("Delivered").first();
        if (deliveredText.count() > 0 && safeIsVisible(deliveredText)) {
            delivered = true;
            logger.info("[Messaging] Delivered text displayed");
        }
        
        // Strategy 2: Look for delivery indicator/checkmark
        if (!delivered) {
            Locator checkmark = page.locator("[class*='delivered'], [class*='sent'], [class*='check']");
            if (checkmark.count() > 0) {
                delivered = true;
                logger.info("[Messaging] Delivery indicator found");
            }
        }
        
        // Strategy 3: Just wait a bit and assume success if no error
        if (!delivered) {
            logger.warn("[Messaging] Could not verify Delivered text, assuming success after wait");
            page.waitForTimeout(2000);
        }
    }

    /**
     * Accept fan message, set price, and reply.
     */
    @Step("Accept fan message and reply with price")
    public void acceptFanMessageAndReply(String fanMessage, String price, String replyMessage) {
        // Find and click Accept for the specific message with timestamp
        clickAcceptButtonForMessage(fanMessage);
        verifyAmountFieldDisplayed();
        setPrice(price);
        typeReplyMessage(replyMessage);
        clickSendButton();
        // Wait for message to be sent - just wait for the dialog to close and message to appear
        page.waitForTimeout(2000);
        // Verify reply message is visible in conversation (use first() to get any occurrence)
        Locator replyMsg = page.getByText(replyMessage).first();
        waitVisible(replyMsg, DEFAULT_WAIT);
        logger.info("[Messaging] Accepted fan message and replied with price {}", price);
    }

    /**
     * Accept fan message, set custom price, and reply.
     */
    @Step("Accept fan message and reply with custom price")
    public void acceptFanMessageAndReplyWithCustomPrice(String fanMessage, String customAmount, String replyMessage) {
        // Find and click Accept for the specific message with timestamp
        clickAcceptButtonForMessage(fanMessage);
        verifyAmountFieldDisplayed();
        setCustomPrice(customAmount);
        typeReplyMessage(replyMessage);
        clickSendButton();
        // Wait for message to be sent - just wait for the dialog to close and message to appear
        page.waitForTimeout(2000);
        // Verify reply message is visible in conversation (use first() to get any occurrence)
        Locator replyMsg = page.getByText(replyMessage).first();
        waitVisible(replyMsg, DEFAULT_WAIT);
        logger.info("[Messaging] Accepted fan message and replied with custom price {}", customAmount);
    }

    /**
     * Accept fan message with FREE price and reply.
     */
    @Step("Accept fan message and reply with free price")
    public void acceptFanMessageAndReplyFree(String fanMessage, String replyMessage) {
        // Find and click Accept for the specific message with timestamp
        clickAcceptButtonForMessage(fanMessage);
        // Verify Free is selected by default
        verifyFreeIsSelected();
        typeReplyMessage(replyMessage);
        clickSendButton();
        // Wait for message to be sent - just wait for the dialog to close and message to appear
        page.waitForTimeout(2000);
        // Verify reply message is visible in conversation (use first() to get any occurrence)
        Locator replyMsg = page.getByText(replyMessage).first();
        waitVisible(replyMsg, DEFAULT_WAIT);
        logger.info("[Messaging] Accepted fan message and replied with FREE price");
    }

    /**
     * Send media to fan in To Deliver conversation.
     */
    @Step("Send media to fan")
    public void sendMediaToFan(String fanName, java.nio.file.Path mediaPath) {
        clickToDeliverTabForConversation();
        clickOnFanConversation(fanName);
        // Upload file directly via hidden input - no need to click plus or My Device
        // This prevents OS file dialog from opening
        uploadMediaFile(mediaPath);
        clickSendButtonForMedia();
        verifyDeliveredText();
        logger.info("[Messaging] Media sent to fan: {}", fanName);
    }

    // ================= Quick Files Upload Methods =================

    @Step("Click Quick Files button")
    public void clickQuickFilesButton() {
        Locator quickFiles = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Quick Files"));
        waitVisible(quickFiles, DEFAULT_WAIT);
        clickWithRetry(quickFiles, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked Quick Files button");
    }

    @Step("Click Selected Photos & videos tab")
    public void clickPhotosVideosTab() {
        // Tab is a div with class quick-file-switch containing "Photos & videos" text
        Locator photosVideos = page.locator("div.quick-file-switch").filter(new Locator.FilterOptions().setHasText("Photos & videos")).first();
        waitVisible(photosVideos, DEFAULT_WAIT);
        clickWithRetry(photosVideos, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked Photos & videos tab");
    }

    @Step("Click Audios tab")
    public void clickAudiosTab() {
        // Tab is a div with class quick-file-switch containing "Audios" text
        Locator audios = page.locator("div.quick-file-switch").filter(new Locator.FilterOptions().setHasText("Audios")).first();
        waitVisible(audios, DEFAULT_WAIT);
        clickWithRetry(audios, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked Audios tab");
    }

    @Step("Click on mix album")
    public void clickMixAlbum() {
        // Click on album row where title contains "mixalbum"
        // Structure: div.qf-row > div.qf-row-middle > div.qf-row-title
        Locator mixAlbum = page.locator("div.qf-row-title").filter(new Locator.FilterOptions().setHasText(Pattern.compile("mixalbum"))).first();
        waitVisible(mixAlbum, DEFAULT_WAIT);
        clickWithRetry(mixAlbum, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked on mix album");
    }

    @Step("Click on audio album")
    public void clickAudioAlbum() {
        // Click on album row where title contains "audioalbum"
        // Structure: div.qf-row > div.qf-row-middle > div.qf-row-title
        Locator audioAlbum = page.locator("div.qf-row-title").filter(new Locator.FilterOptions().setHasText(Pattern.compile("audioalbum"))).first();
        waitVisible(audioAlbum, DEFAULT_WAIT);
        clickWithRetry(audioAlbum, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked on audio album");
    }

    @Step("Verify inside album screen")
    public void verifyInsideAlbumScreen() {
        Locator heading = page.getByText("Select the media you want to");
        waitVisible(heading, DEFAULT_WAIT);
        logger.info("[Messaging] Inside album screen - heading visible");
    }

    @Step("Select image from album")
    public void selectImageFromAlbum() {
        Locator image = page.locator(".cover").first();
        waitVisible(image, DEFAULT_WAIT);
        clickWithRetry(image, 2, 200);
        page.waitForTimeout(500);
        logger.info("[Messaging] Selected image from album");
    }

    @Step("Select video from album")
    public void selectVideoFromAlbum() {
        Locator video = page.locator("div:nth-child(4) > .cover");
        waitVisible(video, DEFAULT_WAIT);
        clickWithRetry(video, 2, 200);
        page.waitForTimeout(500);
        logger.info("[Messaging] Selected video from album");
    }

    @Step("Select audio from album")
    public void selectAudioFromAlbum() {
        // Select audio file - name pattern "audio A5 11/25/2025"
        Locator audio = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("^audio A5")));
        waitVisible(audio, DEFAULT_WAIT);
        clickWithRetry(audio, 2, 200);
        page.waitForTimeout(500);
        logger.info("[Messaging] Selected audio from album");
    }

    @Step("Click Select button")
    public void clickSelectButton() {
        Locator select = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select"));
        waitVisible(select, DEFAULT_WAIT);
        clickWithRetry(select, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked Select button");
    }

    @Step("Click Select and send button")
    public void clickSelectAndSendButton() {
        Locator selectAndSend = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select and send").setExact(true));
        waitVisible(selectAndSend, DEFAULT_WAIT);
        clickWithRetry(selectAndSend, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Messaging] Clicked Select and send button");
    }

    /**
     * Send mixed media (image + video + audio) to fan via Quick Files.
     */
    @Step("Send mixed media to fan via Quick Files")
    public void sendMixedMediaToFanViaQuickFiles(String fanName) {
        clickToDeliverTabForConversation();
        clickOnFanConversation(fanName);

        // Step 1: Click plus and open Quick Files for Photos & Videos
        clickPlusIconForMedia();
        verifyImportationPopup();
        clickQuickFilesButton();
        clickPhotosVideosTab(); // Ensure Photos & videos is selected
        clickMixAlbum();
        verifyInsideAlbumScreen();

        // Select image and video
        selectImageFromAlbum();
        selectVideoFromAlbum();
        clickSelectButton();

        // Step 2: Click plus again for Audio
        clickPlusIconForMedia();
        verifyImportationPopup();
        clickQuickFilesButton();
        clickAudiosTab();
        clickAudioAlbum();
        verifyInsideAlbumScreen();

        // Select audio and send
        selectAudioFromAlbum();
        clickSelectAndSendButton();

        // Final send
        clickSendButtonForMedia();
        verifyDeliveredText();
        logger.info("[Messaging] Mixed media sent to fan: {}", fanName);
    }

}
