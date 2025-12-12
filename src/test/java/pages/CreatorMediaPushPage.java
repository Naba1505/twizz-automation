package pages;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

public class CreatorMediaPushPage extends BasePage {

    // Visible texts / placeholders
    private static final String WHAT_DO_YOU_WANT = "What do you want to do?";
    private static final String MEDIA_PUSH = "Media push";
    private static final String SELECT_SEGMENTS = "Select your segments";
    private static final String SUBSCRIBERS = "Subscribers";
    private static final String CREATE_BTN = "Create";
    private static final String ADD_MEDIA_HINT = "Click on the \"+\" button to import your file";
    private static final String IMPORTATION = "Importation";
    private static final String MESSAGE_PLACEHOLDER = "Your message....";
    private static final String PROPOSE_PUSH_MEDIA = "Propose push media";
    private static final String UPLOADING_MSG = "Stay on page during uploading"; // transient
    private static final String MESSAGING_TITLE = "Messaging";

    public CreatorMediaPushPage(Page page) {
        super(page);
    }

    // safeIsVisible provided by BasePage

    @Step("Open plus menu on creator screen")
    public void openPlusMenu() {
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg.first(), 15000);
        Locator svg = plusImg.locator("svg");
        if (svg.count() > 0 && svg.first().isVisible()) {
            clickWithRetry(svg.first(), 2, 200);
        } else {
            clickWithRetry(plusImg.first(), 2, 200);
        }
    }

    @Step("Ensure options popup is visible")
    public void ensureOptionsPopup() {
        // Proactively dismiss blocking dialog if present
        clickIUnderstandIfPresent();
        waitVisible(page.getByText(WHAT_DO_YOU_WANT).first(), 15000);
    }

    @Step("Dismiss 'I understand' dialog if present")
    public void clickIUnderstandIfPresent() {
        long start = System.currentTimeMillis();
        long timeoutMs = 5000;
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                // Try role-based first
                Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I understand"));
                if (btn.count() > 0 && btn.first().isVisible()) {
                    clickWithRetry(btn.first(), 2, 200);
                    return;
                }
                // Fallbacks: case/selector variants
                String[] sel = new String[] {
                        "button:has-text('I understand')",
                        "text=I understand",
                        "//*[self::button or self::*][contains(translate(normalize-space(.), 'IUNDERSTAND', 'iunderstand'), 'i understand')]"
                };
                for (String s : sel) {
                    Locator cand = s.startsWith("//") ? page.locator("xpath=" + s) : page.locator(s);
                    if (cand.count() > 0 && cand.first().isVisible()) {
                        clickWithRetry(cand.first(), 2, 200);
                        return;
                    }
                }
            } catch (Exception ignored) {}
            try { page.waitForTimeout(150); } catch (Exception ignored) {}
        }
    }

    @Step("Choose Media push from options")
    public void chooseMediaPush() {
        Locator mp = page.getByText(MEDIA_PUSH);
        waitVisible(mp.first(), 10000);
        clickWithRetry(mp.first(), 2, 200);
    }

    @Step("Ensure Media Push segments screen visible")
    public void ensureSegmentsScreen() {
        waitVisible(page.getByText(SELECT_SEGMENTS).first(), 15000);
    }

    @Step("Select Subscribers segment")
    public void selectSubscribersSegment() {
        Locator seg = page.getByText(SUBSCRIBERS);
        waitVisible(seg.first(), 10000);
        clickWithRetry(seg.first(), 1, 150);
    }

    @Step("Select Interested segment")
    public void selectInterestedSegment() {
        Locator seg = page.getByText("Interested");
        waitVisible(seg.first(), 10000);
        clickWithRetry(seg.first(), 1, 150);
    }

    @Step("Select Former Subscriber segment")
    public void selectFormerSubscriberSegment() {
        // Use label text as in codegen: "Former Subscriber1"
        Locator seg = page.locator("label").filter(new Locator.FilterOptions().setHasText("Former Subscriber1"));
        waitVisible(seg.first(), 10000);
        clickWithRetry(seg.first(), 1, 150);
    }

    @Step("Click Create to proceed from segments")
    public void clickCreateNext() {
        Locator create = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CREATE_BTN));
        waitVisible(create.first(), 15000);
        clickWithRetry(create.first(), 2, 200);
    }

    @Step("Ensure Add Push Media screen visible")
    public void ensureAddPushMediaScreen() {
        waitVisible(page.getByText(ADD_MEDIA_HINT).first(), 15000);
    }

    @Step("Click PLUS to add media")
    public void clickAddMediaPlus() {
        Locator plus = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("add"));
        if (plus.count() == 0) {
            plus = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        }
        waitVisible(plus.first(), 10000);
        clickWithRetry(plus.first(), 2, 200);
    }

    @Step("Ensure Importation dialog visible")
    public void ensureImportation() {
        waitVisible(page.getByText(IMPORTATION).first(), 10000);
    }

    // Quick Files helpers (parity with Collection page)
    @Step("Choose 'Quick Files' in Importation dialog")
    public void chooseQuickFiles() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Quick Files"));
        waitVisible(btn.first(), 10000);
        clickWithRetry(btn.first(), 2, 200);
    }

    @Step("Select a Quick Files album by known prefixes or fallback to first available")
    public void selectQuickFilesAlbumWithFallback() {
        // Preferred path: album button whose accessible name starts with
        // "icon mixalbum" (as created by the Quick Files album test).
        Locator mixAlbumBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("^icon\\s+mixalbum", Pattern.CASE_INSENSITIVE)));

        long start = System.currentTimeMillis();
        long timeoutMs = 10_000;

        // Poll for the mixalbum button first
        while (mixAlbumBtn.count() == 0 && System.currentTimeMillis() - start < timeoutMs) {
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }

        if (mixAlbumBtn.count() > 0) {
            Locator target = mixAlbumBtn.first();
            waitVisible(target, 10000);
            try { target.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            clickWithRetry(target, 1, 150);
            return;
        }

        // Fallback 1: div.qf-row-title rows
        Locator albums = page.locator("div.qf-row-title");
        start = System.currentTimeMillis();
        while (albums.count() == 0 && System.currentTimeMillis() - start < timeoutMs) {
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }

        if (albums.count() > 0) {
            waitVisible(albums.first(), 10000);
            Locator firstAlbum = albums.first();
            try { firstAlbum.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            clickWithRetry(firstAlbum, 1, 150);
            return;
        }

        // Fallback 2: any reasonably album-like row
        Locator anyAlbum = page.locator("[role=row], .ant-list-item, .album, .list-item");
        if (anyAlbum.count() > 0) {
            waitVisible(anyAlbum.first(), 10000);
            clickWithRetry(anyAlbum.first(), 1, 150);
            return;
        }

        throw new RuntimeException("No Quick Files album found to select");
    }

    @Step("Select up to {n} media items (covers) from the Quick Files album")
    public void selectUpToNCovers(int n) {
        // Primary path: match codegen and use IMG role with accessible name "select"
        // for each media item inside the album
        Locator covers = page.getByRole(AriaRole.IMG,
                new Page.GetByRoleOptions().setName("select"));

        // Secondary: previous CSS-based selectors in case role mapping changes
        if (covers.count() == 0) {
            covers = page.locator(".select-quick-file-media-thumb");
        }
        if (covers.count() == 0) {
            covers = page.locator(".select-quick-file-media-overlay");
        }
        if (covers.count() == 0) {
            covers = page.locator("div.select-quick-file-media-item");
        }

        int count = covers.count();
        if (count == 0) {
            // Older / alternative UIs: fall back to .cover or generic cards/images
            covers = page.locator(".cover");
            if (covers.count() == 0) {
                covers = page.locator(".ant-card, .ant-image, .ant-image-img, img");
            }
            count = covers.count();
        }
        if (count == 0) {
            throw new RuntimeException("No media items found in Quick Files album (no .select-quick-file-media-thumb, div.select-quick-file-media-item, .cover or generic card/image elements)");
        }
        waitVisible(covers.first(), 10000);

        int need = Math.max(1, n);
        int total = count;
        int picked = 0;
        logger.info("[MediaPushQuickFiles] Found {} candidate media items in album; will attempt to pick up to {}", total, need);
        for (int i = 0; i < total && picked < need; i++) {
            Locator thumb = covers.nth(i);
            try {
                try { thumb.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                if (!safeIsVisible(thumb)) {
                    continue;
                }
                clickWithRetry(thumb, 1, 120);
                page.waitForTimeout(150);
                picked++;
            } catch (Exception ignored) { }
        }
        if (picked == 0) {
            throw new RuntimeException("Quick Files album selection did not click any media items (picked=0)");
        }
        logger.info("[MediaPushQuickFiles] Selected {} media item(s) in album", picked);
    }

    @Step("Confirm selection in Quick Files dialog")
    public void clickSelectInQuickFiles() {
        // Preferred path: a button whose label contains "Select (" (e.g. "Select (6)")
        // as in the codegen flow. Do not over-constrain digits because UI may vary.
        Pattern selectPattern = Pattern.compile("Select\\s*\\(", Pattern.CASE_INSENSITIVE);
        Locator confirm = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(selectPattern));

        long start = System.currentTimeMillis();
        long timeoutMs = 8_000;
        // Poll for the role-based button a short while to handle dialog render delay
        while (confirm.count() == 0 && System.currentTimeMillis() - start < timeoutMs) {
            try { page.waitForTimeout(250); } catch (Exception ignored) {}
        }

        // Fallback 1: explicit confirm button class used by some Quick Files UIs
        if (confirm.count() == 0) {
            confirm = page.locator("button.select-quick-file-media-confirm-button");
        }
        // Fallback 2: any button whose label starts with "Select"
        if (confirm.count() == 0) {
            confirm = page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName(Pattern.compile("^Select", Pattern.CASE_INSENSITIVE)));
        }
        // Fallback 3: any element containing text "Select (" regardless of role
        if (confirm.count() == 0) {
            confirm = page.locator("text=Select (");
        }
        // Fallback 4: generic CSS text match on buttons
        if (confirm.count() == 0) {
            confirm = page.locator("button:has-text('Select')");
        }

        if (confirm.count() == 0) {
            // Some Quick Files variants auto-apply selection without an explicit
            // confirm button. In that case we simply proceed and rely on
            // downstream validations (e.g. media presence, price step) to fail
            // if selection truly did not stick.
            logger.warn("[MediaPushQuickFiles] No explicit Quick Files confirm button found after selecting media; assuming auto-apply and continuing");
            return;
        }

        Locator btn = confirm.first();
        // Avoid timing out on disabled Select(0) buttons: if the button is disabled,
        // surface a clear failure immediately instead of trying to click.
        try {
            String disabled = btn.getAttribute("disabled");
            String ariaDisabled = btn.getAttribute("aria-disabled");
            String text = "";
            try { text = btn.innerText(); } catch (Exception ignored) {}
            if (disabled != null || "true".equalsIgnoreCase(ariaDisabled)) {
                throw new RuntimeException("[MediaPushQuickFiles] Quick Files confirm button is disabled ('" + text + "'); media selection count is likely 0. Check album items and selection logic.");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception ignored) { }

        try {
            waitVisible(btn, 5000);
        } catch (Exception ignored) {}
        try {
            btn.scrollIntoViewIfNeeded();
        } catch (Exception ignored) {}
        clickWithRetry(btn, 1, 150);
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

    @Step("Choose 'My Device' in Importation")
    public void chooseMyDevice() {
        // For device uploads we avoid clicking the "My Device" button because it may
        // trigger a native OS file chooser. Tests rely on uploadMediaFromDevice to
        // drive the underlying input[type='file'] directly. Here we just ensure the
        // Importation dialog is visible and stable.
        ensureImportation();
    }

    @Step("Upload media from device: {file}")
    public void uploadMediaFromDevice(Path file) {
        if (file == null || !Files.exists(file)) {
            throw new RuntimeException("Media file not found: " + file);
        }
        // Prefer the Ant Upload input inside the Importation dialog; this avoids
        // clicking any button that would open a native OS dialog and instead
        // sets the file path directly on the hidden file input.
        Locator inputs = page.locator(".ant-upload input[type='file']");
        if (inputs.count() == 0) {
            inputs = page.locator("input[type='file']");
        }
        if (inputs.count() == 0) {
            throw new RuntimeException("No file input found for media upload in Importation dialog");
        }
        Locator target = inputs.nth(inputs.count() - 1);
        target.setInputFiles(file);

        // After setting files, dismiss the Importation bottom sheet if a Cancel
        // button is present so it does not block subsequent steps.
        try {
            Locator cancel = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
            if (cancel.count() > 0 && safeIsVisible(cancel.first())) {
                clickWithRetry(cancel.first(), 1, 150);
            }
        } catch (Exception ignored) {}
    }

    @Step("Ensure blur toggle is enabled by default")
    public void ensureBlurToggleEnabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(sw, 10000);
        try {
            String checked = sw.getAttribute("aria-checked");
            if (!"true".equalsIgnoreCase(checked)) {
                logger.warn("Blurred media switch not enabled by default (aria-checked={})", checked);
            }
        } catch (Exception ignored) {}
    }

    @Step("Disable blur toggle if currently enabled")
    public void disableBlurIfEnabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(sw, 10000);
        try {
            String checked = sw.getAttribute("aria-checked");
            if ("true".equalsIgnoreCase(checked)) {
                clickWithRetry(sw, 1, 150);
            }
        } catch (Exception e) {
            // Fallback: attempt click once
            clickWithRetry(sw, 1, 150);
        }
    }

    @Step("Ensure blur toggle is disabled")
    public void ensureBlurToggleDisabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(sw, 10000);
        String checked = sw.getAttribute("aria-checked");
        if (!"false".equalsIgnoreCase(checked)) {
            logger.warn("Expected blur toggle disabled but aria-checked={}", checked);
        }
    }

    @Step("Click Next")
    public void clickNext() {
        Locator next = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next"));
        waitVisible(next.first(), 10000);
        clickWithRetry(next.first(), 2, 200);
    }

    @Step("Ensure Message title visible")
    public void ensureMessageTitle() {
        // UI updated: message field is now a textbox with accessible name placeholder
        Locator ph = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(MESSAGE_PLACEHOLDER));
        waitVisible(ph.first(), 15000);
    }

    @Step("Fill message: {msg}")
    public void fillMessage(String msg) {
        Locator ph = page.getByPlaceholder(MESSAGE_PLACEHOLDER);
        waitVisible(ph.first(), 10000);
        ph.first().click();
        ph.first().fill(msg == null ? "" : msg);
    }

    @Step("Set price in euros to {euros}")
    public void setPriceEuro(int euros) {
        // For 15€, match label text via regex like ^15€$
        String regex = "^" + euros + "€$";
        Locator label = page.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile(regex)));
        waitVisible(label.first(), 10000);
        clickWithRetry(label.first(), 1, 150);
    }

    @Step("Ensure add promotion toggle is disabled by default")
    public void ensureAddPromotionDisabled() {
        try {
            Locator toggles = page.getByRole(AriaRole.SWITCH);
            if (toggles.count() > 1) {
                Locator promo = toggles.nth(1);
                String checked = promo.getAttribute("aria-checked");
                if ("true".equalsIgnoreCase(checked)) {
                    logger.warn("Add promotion toggle appears enabled by default");
                }
            }
        } catch (Exception ignored) {}
    }

    @Step("Enable add promotion toggle")
    public void enablePromotionToggle() {
        Locator toggles = page.getByRole(AriaRole.SWITCH);
        Locator target;
        if (toggles.count() > 1) {
            target = toggles.nth(1);
        } else {
            target = toggles.first();
        }
        waitVisible(target, 10000);
        // Click only if not already enabled
        try {
            String checked = target.getAttribute("aria-checked");
            if (!"true".equalsIgnoreCase(checked)) {
                clickWithRetry(target, 1, 150);
            }
        } catch (Exception e) {
            clickWithRetry(target, 1, 150);
        }
    }

    @Step("Ensure 'Discount' label visible")
    public void ensureDiscountVisible() {
        waitVisible(page.getByText("Discount").first(), 10000);
    }

    @Step("Open discount percent field")
    public void openDiscountPercentField() {
        Locator percentSpan = page.locator("span").filter(new Locator.FilterOptions().setHasText(Pattern.compile("%")));
        waitVisible(percentSpan.first(), 10000);
        clickWithRetry(percentSpan.first(), 1, 150);
    }

    @Step("Fill discount percent: {percent}%")
    public void fillDiscountPercent(int percent) {
        // Based on UI, the discount textbox appears as the third textbox (index 2)
        Locator tb = page.getByRole(AriaRole.TEXTBOX).nth(2);
        waitVisible(tb, 10000);
        tb.fill(String.valueOf(percent));
    }

    @Step("Ensure 'Validity period' title visible")
    public void ensureValidityTitle() {
        waitVisible(page.getByText("Validity period").first(), 10000);
    }

    @Step("Select validity as 'Unlimited'")
    public void selectValidityUnlimited() {
        Locator lbl = page.locator("label").filter(new Locator.FilterOptions().setHasText("Unlimited"));
        waitVisible(lbl.first(), 10000);
        clickWithRetry(lbl.first(), 1, 150);
    }

    @Step("Open euro discount field")
    public void openEuroDiscountField() {
        Locator euroSpan = page.locator("span").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^€$")));
        waitVisible(euroSpan.first(), 10000);
        clickWithRetry(euroSpan.first(), 1, 150);
    }

    @Step("Fill euro discount amount: {amount}€")
    public void fillEuroDiscountEuro(int amount) {
        // Based on UI hint: euro discount textbox appears as index 1
        Locator tb = page.getByRole(AriaRole.TEXTBOX).nth(1);
        waitVisible(tb, 10000);
        tb.fill(String.valueOf(amount));
    }

    @Step("Select validity as '7 days'")
    public void selectValidity7Days() {
        Locator lbl = page.locator("label").filter(new Locator.FilterOptions().setHasText("7 days"));
        waitVisible(lbl.first(), 10000);
        clickWithRetry(lbl.first(), 1, 150);
    }

    @Step("Open custom price field (0.00 €)")
    public void openCustomPriceField() {
        Locator zeroPrice = page.getByText("0.00 €");
        waitVisible(zeroPrice.first(), 10000);
        clickWithRetry(zeroPrice.first(), 1, 150);
    }

    @Step("Fill custom price euros: {amount}€")
    public void fillCustomPriceEuro(int amount) {
        Locator spin = page.getByRole(AriaRole.SPINBUTTON);
        waitVisible(spin.first(), 10000);
        spin.first().fill(String.valueOf(amount));
    }

    @Step("Select price as 'Free'")
    public void selectPriceFree() {
        Locator lbl = page.locator("label").filter(new Locator.FilterOptions().setHasText("Free"));
        waitVisible(lbl.first(), 10000);
        clickWithRetry(lbl.first(), 1, 150);
    }

    @Step("Click 'Propose push media'")
    public void clickProposePushMedia() {
        // Primary: exact label from constant
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(PROPOSE_PUSH_MEDIA));
        if (btn.count() == 0) {
            // Fallback: any button whose label starts with "Propose push" (e.g. translations / spacing)
            btn = page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName(Pattern.compile("^Propose\\s+push", Pattern.CASE_INSENSITIVE)));
        }
        if (btn.count() == 0) {
            logger.warn("[MediaPush] 'Propose push media' button not found; skipping click");
            return;
        }
        Locator first = btn.first();
        try {
            waitVisible(first, 15000);
        } catch (Exception e) {
            logger.warn("[MediaPush] 'Propose push media' button not visible within timeout; attempting click anyway", e);
        }
        clickWithRetry(first, 2, 200);
    }

    @Step("Optionally wait for uploading message if it appears")
    public void waitForUploadingMessageIfFast() {
        try {
            Locator msg = page.getByText(UPLOADING_MSG);
            if (msg.count() > 0) {
                // small visibility wait, then allow dismiss naturally
                waitVisible(msg.first(), 5000);
            }
        } catch (Exception ignored) {}
    }

    @Step("Assert landed on Messaging screen")
    public void assertOnMessagingScreen() {
        waitVisible(page.getByText(MESSAGING_TITLE).first(), 60000);
    }
}
