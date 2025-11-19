package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.qameta.allure.Step;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class CreatorSettingsPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(CreatorSettingsPage.class);
    private static final int LONG_WAIT = 30_000; // for heavy pages/uploads
    // Small timing constants to avoid magic numbers
    private static final int SHORT_PAUSE_MS = 300;   // brief settle between actions
    private static final int SEQUENTIAL_PAUSE_MS = 500; // settle after each file in sequential flows

    public CreatorSettingsPage(Page page) {
        super(page);
    }

    // Try to detect media items that appear in the queue/list on the upload page
    private Locator getQueuedMediaItems() {
        // Common patterns: Ant Upload list items, image thumbnails, generic cards in the media area
        return page.locator(".ant-upload-list-item, [data-testid='upload-item'], .ant-image, .ql-media-thumb, .media-thumb, .ant-card");
    }

    private void waitForQueuedItemsIncrement(int before, int expectedIncrement, int timeoutMs) {
        int target = before + expectedIncrement;
        long end = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < end) {
            int now = 0;
            try { now = getQueuedMediaItems().count(); } catch (RuntimeException ignored) {}
            if (now >= target) {
                log.info("Queued items reached target {} (now={})", target, now);
                return;
            }
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }
        int finalCount = getQueuedMediaItems().count();
        log.warn("Queued items did not reach target {} within {} ms (final={})", target, timeoutMs, finalCount);
    }

    // Locators/labels used in steps
    private static final String SETTINGS_ICON_NAME = "settings"; // role=img name
    private static final String QUICK_FILES_TEXT = "Quick Files";
    private static final String PLUS_ICON_NAME = "plus"; // role=img name or button name in some screens
    private static final String CREATE_BUTTON = "Create"; // role=button name
    private static final String TERMINATE_BUTTON = "Terminate"; // role=button name
    private static final String NEW_ALBUM_TITLE = "New album";
    private static final String MEDIA_TEXT = "Media"; // exact text
    private static final String AUDIO_SUCCESS_TEXT = "Audio uploaded successfully";

    // URLs to assert against
    private static final String SETTINGS_URL = "https://stg.twizz.app/common/setting";
    private static final String QUICK_LINK_URL = "https://stg.twizz.app/creator/quickLink";
    private static final String CREATE_NEW_ALBUM_URL = "https://stg.twizz.app/creator/createNewAlbum";

    @Step("Open Settings from profile")
    public void openSettingsFromProfile() {
        log.info("Opening Settings from profile via IMG[name='{}']", SETTINGS_ICON_NAME);
        Locator settingsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SETTINGS_ICON_NAME));
        waitVisible(settingsIcon, DEFAULT_WAIT);
        settingsIcon.click();
        waitForUrlContains(SETTINGS_URL);
        log.info("Landed on Settings, url={}", page.url());
    }

    @Step("Open Quick Files screen")
    public void openQuickFiles() {
        log.info("Navigating to Quick Files by clicking text='{}'", QUICK_FILES_TEXT);
        Locator quickFiles = page.getByText(QUICK_FILES_TEXT);
        waitVisible(quickFiles, DEFAULT_WAIT);
        quickFiles.click();
        waitForUrlContains(QUICK_LINK_URL);
        // Verify title text present
        waitVisible(page.getByText(QUICK_FILES_TEXT), DEFAULT_WAIT);
        log.info("Quick Files screen visible, url={}", page.url());
    }

    @Step("Ensure Quick Files URL and title are visible")
    public void ensureOnQuickFiles() {
        waitForUrlContains(QUICK_LINK_URL);
        waitVisible(page.getByText(QUICK_FILES_TEXT), DEFAULT_WAIT);
    }

    @Step("Delete all Quick Files albums via trash icon with confirmation")
    public void deleteAllQuickFileAlbums() {
        // Navigate to Quick Files first to ensure correct context
        navigateToQuickFilesDirect();
        ensureOnQuickFiles();
        waitForAlbumGrid();

        int guard = 0;
        while (true) {
            Locator trashes = getTrashIcons();
            int count = trashes.count();
            log.info("Found {} trash icon(s) on Quick Files page", count);
            if (count == 0) {
                // Try per-card hover-and-delete as a fallback
                if (!tryDeleteViaCards()) {
                    log.info("No trash icons found on page or within cards; nothing to delete.");
                    break;
                }
                // After per-card attempt, continue loop to re-count
                continue;
            }
            // Click the last icon (deleting from bottom up is often more stable)
            try {
                Locator target = trashes.nth(count - 1);
                // Some UIs render the trash only on hover; ensure visibility
                try { target.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                try { target.hover(); } catch (Exception ignored) {}
                try {
                    clickWithRetry(target, 2, 300);
                } catch (RuntimeException primary) {
                    // force click as fallback
                    try { target.click(new Locator.ClickOptions().setForce(true)); }
                    catch (Exception forceErr) { throw primary; }
                }
            } catch (RuntimeException e) {
                log.warn("Failed to click trash icon: {}", e.getMessage());
                break;
            }

            // Confirm modal
            boolean confirmed = clickAnyConfirmDelete();
            if (!confirmed) {
                log.warn("Could not find a known confirm delete button; aborting deletion loop.");
                break;
            }

            // brief settle for DOM update
            try { page.waitForTimeout(SHORT_PAUSE_MS); } catch (Exception ignored) {}
            // Wait for the number of trash icons to decrease
            long end = System.currentTimeMillis() + DEFAULT_WAIT;
            while (System.currentTimeMillis() < end) {
                int now = getTrashIcons().count();
                if (now < count) break;
                try { page.waitForTimeout(100); } catch (Exception ignored) {}
            }

            guard++;
            if (guard > 100) {
                log.warn("Stopping delete loop after {} iterations to avoid infinite loop.", guard);
                break;
            }
        }
    }

    @Step("Directly navigate to Quick Files URL and ensure page is visible")
    public void navigateToQuickFilesDirect() {
        page.navigate(QUICK_LINK_URL);
        ensureOnQuickFiles();
    }

    public int quickFilesTrashIconCount() {
        return getTrashIcons().count();
    }

    private Locator getTrashIcons() {
        // Try role=img name=trash (exact)
        Locator imgTrash = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("trash").setExact(true));
        if (imgTrash.count() > 0) return imgTrash;
        // Try role=img name=Trash (capitalized)
        Locator imgTrashCap = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Trash").setExact(true));
        if (imgTrashCap.count() > 0) return imgTrashCap;
        // Try role=button name=trash
        Locator btnTrash = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("trash").setExact(true));
        if (btnTrash.count() > 0) return btnTrash;
        // Try role=button name=Trash
        Locator btnTrashCap = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Trash").setExact(true));
        if (btnTrashCap.count() > 0) return btnTrashCap;
        // Fallback: CSS class often present on the icon
        return page.locator(".trashIcon");
    }

    private boolean clickAnyConfirmDelete() {
        String[] labels = new String[]{"Yes, delete", "Yes, Delete", "Delete", "Yes"};
        long end = System.currentTimeMillis() + DEFAULT_WAIT;
        while (System.currentTimeMillis() < end) {
            for (String label : labels) {
                Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(label));
                if (btn.count() > 0 && btn.first().isVisible()) {
                    try {
                        clickWithRetry(btn.first(), 2, 200);
                        return true;
                    } catch (Exception ignored) {}
                }
            }
            try { page.waitForTimeout(100); } catch (Exception ignored) {}
        }
        return false;
    }

    private void waitForAlbumGrid() {
        // Wait for any of the expected containers or any trash icon to appear
        long end = System.currentTimeMillis() + DEFAULT_WAIT;
        while (System.currentTimeMillis() < end) {
            if (getTrashIcons().count() > 0) return;
            if (getAlbumCards().count() > 0) return;
            try { page.waitForTimeout(150); } catch (Exception ignored) {}
        }
    }

    private Locator getAlbumCards() {
        // Common Ant Design patterns and potential testid
        Locator cards = page.locator("[data-testid='album-card'], .ant-card, .ant-card-body, .ql-card, .albumCard");
        return cards;
    }

    private boolean tryDeleteViaCards() {
        Locator cards = getAlbumCards();
        int total = cards.count();
        if (total == 0) return false;
        for (int i = total - 1; i >= 0; i--) {
            Locator card = cards.nth(i);
            try { card.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            try { card.hover(); } catch (Exception ignored) {}
            Locator trash = card.getByRole(AriaRole.IMG, new Locator.GetByRoleOptions().setName("trash").setExact(true));
            if (trash.count() == 0) {
                trash = card.getByRole(AriaRole.IMG, new Locator.GetByRoleOptions().setName("Trash").setExact(true));
            }
            if (trash.count() == 0) {
                trash = card.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("trash").setExact(true));
            }
            if (trash.count() == 0) {
                trash = card.locator(".trashIcon");
            }
            if (trash.count() > 0) {
                Locator target = trash.first();
                try { target.hover(); } catch (Exception ignored) {}
                try {
                    clickWithRetry(target, 2, 200);
                } catch (Exception e) {
                    try { target.click(new Locator.ClickOptions().setForce(true)); } catch (Exception ignored) { continue; }
                }
                // Confirm
                if (!clickAnyConfirmDelete()) {
                    continue;
                }
                // Wait for at least one album/trash to disappear
                long end = System.currentTimeMillis() + DEFAULT_WAIT;
                while (System.currentTimeMillis() < end) {
                    if (getTrashIcons().count() < total || getAlbumCards().count() < total) {
                        return true;
                    }
                    try { page.waitForTimeout(100); } catch (Exception ignored) {}
                }
                return true; // best effort
            }
        }
        return false;
    }

    @Step("Start creating a new album")
    public void startCreateNewAlbum() {
        log.info("Starting New Album creation using 'plus' trigger");
        // Some screens may render plus as IMG or BUTTON; try IMG first, then BUTTON
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
        if (plusImg.count() > 0) {
            log.info("Found plus as IMG (count={}), clicking", plusImg.count());
            clickWithRetry(plusImg.first(), 2, 500);
        } else {
            Locator plusBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
            log.info("IMG not found; trying BUTTON (count={})", plusBtn.count());
            clickWithRetry(plusBtn.first(), 2, 500);
        }
        waitForUrlContains(CREATE_NEW_ALBUM_URL);
        // Enhancement: If a type selection screen is shown first, ensure title and choose Photos & videos, then Continue
        try {
            Locator typeTitle = page.getByText("What type of album do you");
            if (typeTitle.count() > 0) {
                log.info("Type selection screen detected. Ensuring title is visible and choosing 'Photos & videos'.");
                waitVisible(typeTitle.first(), DEFAULT_WAIT);
                Locator photosAndVideos = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Photos & videos"));
                waitVisible(photosAndVideos.first(), DEFAULT_WAIT);
                clickWithRetry(photosAndVideos.first(), 2, 300);
                Locator continueBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
                waitVisible(continueBtn.first(), DEFAULT_WAIT);
                clickWithRetry(continueBtn.first(), 2, 300);
            }
        } catch (RuntimeException e) {
            log.info("Type selection screen not present or skipped: {}", e.getMessage());
        }
        waitVisible(page.getByText(NEW_ALBUM_TITLE), DEFAULT_WAIT);
        log.info("Create New Album screen visible, url={}", page.url());
    }

    @Step("Fill album name '{albumName}' and create")
    public void fillAlbumNameAndCreate(String albumName) {
        // Use provided albumName as-is (caller ensures uniqueness). Avoid appending extra timestamp here.
        log.info("Filling album name placeholder 'My name' with value: {}", albumName);
        fillByPlaceholder("My name", albumName);
        // Click Create or Continue depending on UI variant
        Locator createBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CREATE_BUTTON));
        if (createBtn.count() > 0 && createBtn.first().isVisible()) {
            log.info("Clicking 'Create' button");
            try { createBtn.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            clickWithRetry(createBtn.first(), 2, 250);
        } else {
            log.info("'Create' not visible; trying 'Continue' button");
            Locator continueBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
            if (continueBtn.count() == 0) {
                // Fallback to text locator
                continueBtn = page.getByText("Continue");
            }
            waitVisible(continueBtn.first(), DEFAULT_WAIT);
            try { continueBtn.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            clickWithRetry(continueBtn.first(), 2, 250);
        }
        // After create, we should be on add media screen
        Locator mediaText = getByTextExact(MEDIA_TEXT);
        waitVisible(mediaText, LONG_WAIT);
        log.info("Media screen visible after create");
    }

    @Step("Add media files: {filePaths}")
    public void addMediaFiles(List<Path> filePaths) {
        int total = filePaths != null ? filePaths.size() : 0;
        log.info("Adding media files ({} files)", total);
        if (filePaths == null || filePaths.isEmpty()) return;
        int beforeTotalQueued = getQueuedMediaItems().count();

        // Split by type early so we can choose appropriate tab before using PLUS
        List<Path> images = filePaths.stream().filter(this::isImage).collect(Collectors.toList());
        List<Path> videos = filePaths.stream().filter(this::isVideo).collect(Collectors.toList());
        List<Path> others = filePaths.stream().filter(p -> !isImage(p) && !isVideo(p)).collect(Collectors.toList());

        boolean didPlusForImages = false;
        boolean didPlusForVideos = false;

        // Prefer PLUS uploads with correct tab selection (force sequential per-file as requested)
        Locator plusProbe = getPlusButton();
        if (plusProbe != null) {
            if (!videos.isEmpty()) {
                didPlusForVideos = uploadSequentialViaPlus("Videos", videos);
            }
            if (!images.isEmpty()) {
                didPlusForImages = uploadSequentialViaPlus("Images", images);
            }
            // If we attempted both categories via PLUS, we can skip input path for those categories
            if ((videos.isEmpty() || didPlusForVideos) && (images.isEmpty() || didPlusForImages)) {
                if (!others.isEmpty()) {
                    log.info("PLUS path does not handle 'others'; falling back to input path for {} file(s)", others.size());
                    uploadToCategory(null, null, others);
                }
                return;
            }
        }

        // Inspect available file inputs as fallback
        Locator allInputs = page.locator("input[type=file]");
        int inputCount = allInputs.count();
        log.info("Detected {} file input(s) on page", inputCount);
        if (inputCount == 1) {
            String accept = null;
            try { accept = allInputs.first().getAttribute("accept"); } catch (RuntimeException ignored) {}
            log.info("Single input accept='{}'", accept);
            log.info("Uploading all files in one batch on the single input to avoid replacement.");
            int before = getQueuedMediaItems().count();
            allInputs.first().setInputFiles(filePaths.toArray(new Path[0]));
            logSelectedFilesCount(allInputs.first(), total);
            waitForQueuedItemsIncrement(before, total, LONG_WAIT);
            return;
        }

        if (!images.isEmpty() || !videos.isEmpty()) {
            // Decide inputs by index so we can detect if both categories map to the same input
            Integer imgIdx = images.isEmpty() ? null : findFileInputIndexForType("image");
            Integer vidIdx = videos.isEmpty() ? null : findFileInputIndexForType("video");

            // If both categories resolve to the same input index, upload them together once to avoid replacement
            if (imgIdx != null && vidIdx != null && imgIdx.equals(vidIdx)) {
                clickTabIfPresent("Media"); // generic fallback section if exists
                revealUploadTrigger();
                Locator all = page.locator("input[type=file]");
                if (all.count() == 0) throw new RuntimeException("No file input found for combined upload");
                Locator input = all.nth(imgIdx);
                List<Path> combined = new java.util.ArrayList<>();
                combined.addAll(images);
                combined.addAll(videos);
                log.info("Images and videos target the same input (index {}), uploading combined {} file(s)", imgIdx, combined.size());
                int before = getQueuedMediaItems().count();
                input.setInputFiles(combined.toArray(new Path[0]));
                logSelectedFilesCount(input, combined.size());
                waitForQueuedItemsIncrement(before, combined.size(), LONG_WAIT);
            } else {
                if (!images.isEmpty()) {
                    log.info("Uploading {} image(s): {}", images.size(), images);
                    uploadToCategory("Images", "image", images);
                }
                if (!videos.isEmpty()) {
                    log.info("Uploading {} video(s): {}", videos.size(), videos);
                    uploadToCategory("Videos", "video", videos);
                }
            }
        }
        if (!others.isEmpty()) {
            log.info("Uploading {} other file(s) (unknown type): {}", others.size(), others);
            // Fallback to any available input
            uploadToCategory(null, null, others);
        }

        // Final guard: ensure at least 'total' new items were queued
        int afterTotalQueued = getQueuedMediaItems().count();
        int delta = afterTotalQueued - beforeTotalQueued;
        log.info("Queued items before: {} after: {} delta: {} (expected >= {})", beforeTotalQueued, afterTotalQueued, delta, total);
        Assert.assertTrue(delta >= total, "Not all files were queued. Expected at least " + total + ", but only saw " + delta + " new item(s).");
    }

    private void uploadToCategory(String tabName, String acceptKeyword, List<Path> files) {
        if (files == null || files.isEmpty()) return;
        // Try switching tab if present
        if (tabName != null) {
            clickTabIfPresent(tabName);
        }
        // Prefer direct input with matching accept
        Locator input = null;
        Integer idx = findFileInputIndexForType(acceptKeyword);
        if (idx != null) {
            input = page.locator("input[type=file]").nth(idx);
        }
        if (input == null) {
            // Try to reveal input via plus, then search again
            revealUploadTrigger();
            idx = findFileInputIndexForType(acceptKeyword);
            if (idx != null) input = page.locator("input[type=file]").nth(idx);
        }
        if (input == null) {
            // Final fallback: any file input
            Locator any = page.locator("input[type=file]");
            if (any.count() == 0) {
                throw new RuntimeException("No file input found for upload after trying to reveal it");
            }
            input = any.first();
        }
        String multipleAttr = null;
        try { multipleAttr = input.getAttribute("multiple"); } catch (RuntimeException ignored) {}
        boolean supportsMultiple = multipleAttr != null; // presence of attribute indicates multi-select
        log.info("Uploading {} file(s) to input (accept='{}', multiple='{}')", files.size(), acceptKeyword, supportsMultiple);
        if (supportsMultiple) {
            int before = getQueuedMediaItems().count();
            input.setInputFiles(files.toArray(new Path[0]));
            logSelectedFilesCount(input, files.size());
            waitForQueuedItemsIncrement(before, files.size(), LONG_WAIT);
        } else {
            // Upload sequentially one by one; re-find input each time to avoid stale locators
            for (int i = 0; i < files.size(); i++) {
                Path f = files.get(i);
                try {
                    // Some UIs hide/remove input after selection; re-find and re-reveal each iteration
                    Integer idx2 = findFileInputIndexForType(acceptKeyword);
                    Locator current = null;
                    if (idx2 != null) {
                        current = page.locator("input[type=file]").nth(idx2);
                    }
                    if (current == null || current.count() == 0) {
                        revealUploadTrigger();
                        idx2 = findFileInputIndexForType(acceptKeyword);
                        if (idx2 != null) current = page.locator("input[type=file]").nth(idx2);
                    }
                    if (current == null || current.count() == 0) {
                        throw new RuntimeException("No file input available for sequential upload of: " + f.getFileName());
                    }
                    log.info("Sequential upload ({}/{}): {}", i + 1, files.size(), f.getFileName());
                    int before = getQueuedMediaItems().count();
                    current.setInputFiles(new Path[]{f});
                    // brief settle; real UIs usually queue the file instantly
                    try { page.waitForTimeout(SEQUENTIAL_PAUSE_MS); } catch (Exception ignored) {}
                    waitForQueuedItemsIncrement(before, 1, DEFAULT_WAIT);
                } catch (RuntimeException e) {
                    log.warn("Failed sequential upload for {}: {}", f.getFileName(), e.getMessage());
                    throw e;
                }
            }
        }
    }

    private void logSelectedFilesCount(Locator input, int expected) {
        try {
            Object result = input.evaluate("e => e && e.files ? e.files.length : 0");
            int len = 0;
            if (result instanceof Number) {
                len = ((Number) result).intValue();
            } else if (result != null) {
                try { len = Integer.parseInt(result.toString()); } catch (Exception ignored) {}
            }
            log.info("Input now holds {} file(s) (expected in this batch: {})", len, expected);
        } catch (RuntimeException e) {
            log.info("Could not read input.files.length: {}", e.getMessage());
        }
    }

    private void revealUploadTrigger() {
        Locator plusBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
        if (plusBtn.count() > 0) {
            log.info("Revealing file input via BUTTON plus");
            clickWithRetry(plusBtn.first(), 2, 500);
            return;
        }
        Locator anyPlus = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
        if (anyPlus.count() > 0) {
            log.info("Revealing file input via IMG plus");
            clickWithRetry(anyPlus.first(), 2, 500);
        } else {
            log.info("No explicit plus trigger found; proceeding to search inputs");
        }
    }

    private Locator getPlusButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
        if (btn.count() > 0) return btn.first();
        Locator img = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
        if (img.count() > 0) return img.first();
        return null;
    }

    // Always upload sequentially via PLUS within a specific tab/section; returns true if all succeeded
    private boolean uploadSequentialViaPlus(String tabName, List<Path> files) {
        if (files == null || files.isEmpty()) return true;
        clickTabIfPresent(tabName);
        for (int i = 0; i < files.size(); i++) {
            Path f = files.get(i);
            try {
                Locator plus = getPlusButton();
                if (plus == null) {
                    log.info("PLUS button not available for tab '{}' ; aborting sequential PLUS path", tabName);
                    return false;
                }
                // Click plus to reveal the hidden input, then set files on the actual input element
                clickWithRetry(plus, 1, 150);
                log.info("[PLUS] Sequential ({}/{}) : {} in tab '{}'", i + 1, files.size(), f.getFileName(), tabName);
                Locator input = page.locator("input[type=file]");
                // Prefer the last input as many UIs inject a new input per click
                if (input.count() == 0) {
                    // Try to reveal again explicitly and re-scan
                    revealUploadTrigger();
                    input = page.locator("input[type=file]");
                }
                if (input.count() == 0) {
                    log.warn("[PLUS] No file input found after plus click for '{}'", f.getFileName());
                    return false;
                }
                Locator targetInput = input.nth(input.count() - 1);
                targetInput.setInputFiles(new Path[]{f});
                try { page.waitForTimeout(SHORT_PAUSE_MS); } catch (Exception ignored) {}
            } catch (RuntimeException ex) {
                log.warn("[PLUS] Sequential failed for {} in tab '{}': {}", f.getFileName(), tabName, ex.getMessage());
                return false;
            }
        }
        return true;
    }

    private boolean clickTabIfPresent(String tabName) {
        if (tabName == null) return false;
        String[] candidates;
        if ("Images".equalsIgnoreCase(tabName)) {
            candidates = new String[]{"Images", "Image", "Photos", "Photo"};
        } else if ("Videos".equalsIgnoreCase(tabName)) {
            candidates = new String[]{"Videos", "Video"};
        } else {
            candidates = new String[]{tabName};
        }
        for (String name : candidates) {
            Locator tab = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name));
            if (tab.count() > 0) {
                log.info("Switching to '{}' tab", name);
                clickWithRetry(tab.first(), 2, 300);
                return true;
            }
            // Fallback: plain text tab
            Locator textTab = page.getByText(name);
            if (textTab.count() > 0) {
                log.info("Switching to '{}' section via text", name);
                clickWithRetry(textTab.first(), 2, 300);
                return true;
            }
        }
        log.info("'{}' tab not present; continuing without switch", tabName);
        return false;
    }

    private Integer findFileInputIndexForType(String acceptKeyword) {
        Locator all = page.locator("input[type=file]");
        int count = all.count();
        if (count == 0) return null;
        if (acceptKeyword == null) return 0;
        for (int i = 0; i < count; i++) {
            Locator candidate = all.nth(i);
            try {
                String accept = candidate.getAttribute("accept");
                if (accept != null && accept.toLowerCase().contains(acceptKeyword)) {
                    log.info("Matched file input by accept='{}' for keyword='{}' (index {})", accept, acceptKeyword, i);
                    return i;
                }
            } catch (RuntimeException ignored) { }
        }
        log.info("No input matched acceptKeyword '{}' ; returning null to allow smarter fallback", acceptKeyword);
        return null;
    }

    private boolean isImage(Path p) {
        String s = p.toString().toLowerCase();
        return s.endsWith(".png") || s.endsWith(".jpg") || s.endsWith(".jpeg") || s.endsWith(".gif") || s.endsWith(".webp");
    }

    private boolean isVideo(Path p) {
        String s = p.toString().toLowerCase();
        return s.endsWith(".mp4") || s.endsWith(".mov") || s.endsWith(".avi") || s.endsWith(".mkv") || s.endsWith(".webm");
    }

    @Step("Confirm upload and stay on page")
    public void confirmUploadAndStay() {
        log.info("Confirming upload: clicking exact 'Terminate' once");
        // Click the exact 'Terminate' button once
        Locator action = page.getByText(TERMINATE_BUTTON, new Page.GetByTextOptions().setExact(true));
        waitVisible(action, LONG_WAIT);
        action.click();
        // Optionally choose to stay on page during uploading if the prompt appears
        Locator stay = page.getByText("Stay on page during uploading");
        try {
            stay.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5_000));
            stay.click();
            log.info("Clicked 'Stay on page during uploading'.");
        } catch (RuntimeException e) {
            log.info("'Stay on page during uploading' prompt not shown within 5s; continuing without it.");
        }
        // Try to observe helper text briefly; do not fail if absent
        try {
            page.getByText("Files with a large size will take some time to be ready").waitFor(
                    new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5_000));
            log.info("Upload helper text visible.");
        } catch (RuntimeException ignored) {
            log.info("Upload helper text not visible within 5s; proceeding.");
        }
        // Allow the page to settle and handle cases where the app closes the page or navigates
        try {
            page.waitForLoadState();
        } catch (RuntimeException e) {
            log.info("Post-upload load state wait interrupted: {}", e.getMessage());
        }
        if (page.isClosed()) {
            log.info("Page/context closed by application after upload; skipping further actions.");
        }
    }

    @Step("Navigate back to profile {times} times")
    public void navigateBackToProfile(int times) {
        if (page.isClosed()) {
            log.info("Page is already closed; skipping navigateBackToProfile.");
            return;
        }
        Locator back = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
        for (int i = 0; i < times; i++) {
            if (page.isClosed()) {
                log.info("Page closed during back navigation (iteration {}); stopping.", i + 1);
                return;
            }
            if (back.count() == 0) {
                log.info("Back arrow not found on iteration {}; stopping.", i + 1);
                return;
            }
            try {
                clickWithRetry(back, 2, 300);
                page.waitForLoadState();
            } catch (RuntimeException e) {
                log.info("Back navigation failed on iteration {}: {}", i + 1, e.getMessage());
                return;
            }
        }
    }

    private void waitForUrlContains(String expected) {
        page.waitForURL(url -> url.toString().startsWith(expected), new Page.WaitForURLOptions().setTimeout(DEFAULT_WAIT));
        Assert.assertTrue(page.url().startsWith(expected), "Unexpected URL. Expected startsWith: " + expected + ", Actual: " + page.url());
    }

    /**
     * Creates a Quick Files album with a unique name using the provided prefix and a timestamp.
     * Flow: open settings -> Quick Files -> New album -> fill unique name -> Create -> assert Media screen.
     * @param prefix album name prefix, e.g., "videoalbum_"; if null/blank, defaults to "quickalbum_"
     * @return generated unique album name, e.g., videoalbum_250818_131200
     */
    @Step("Create quick album with unique name and custom prefix: {prefix}")
    public final String createQuickAlbum(String prefix) {
        String p = (prefix == null || prefix.isBlank()) ? "quickalbum_" : prefix;
        if (!p.endsWith("_")) p = p + "_";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");
        String uniqueName = p + LocalDateTime.now().format(fmt);
        log.info("Creating Quick Files album with name: {}", uniqueName);
        openSettingsFromProfile();
        openQuickFiles();
        startCreateNewAlbum();
        fillAlbumNameAndCreate(uniqueName);
        log.info("Album created and Media screen displayed: {}", uniqueName);
        return uniqueName;
    }

    /**
     * Backward-compatible default create using prefix 'quickalbum_'.
     */
    @Step("Create quick album with unique name")
    public final String createQuickAlbum() {
        return createQuickAlbum("quickalbum_");
    }

    /**
     * Creates an audio-only Quick Files album using the new Audios flow.
     * Flow: open settings -> Quick Files -> plus -> select Audios -> Continue -> unique album name -> Continue ->
     * import audio file -> wait for success toast.
     *
     * @param prefix    base prefix for album name (timestamp is appended for uniqueness)
     * @param audioFile path to a single audio file to upload
     * @return generated unique album name
     */
    @Step("Create audio Quick Files album with prefix: {prefix}")
    public final String createAudioAlbum(String prefix, Path audioFile) {
        if (audioFile == null) {
            throw new IllegalArgumentException("audioFile must not be null");
        }
        String p = (prefix == null || prefix.isBlank()) ? "audioalbum_" : prefix;
        if (!p.endsWith("_")) p = p + "_";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");
        String uniqueName = p + LocalDateTime.now().format(fmt);
        log.info("Creating audio Quick Files album with name: {} and audio file: {}", uniqueName, audioFile);

        // Navigate to Quick Files
        openSettingsFromProfile();
        openQuickFiles();

        // Start new album via plus icon (IMG preferred, then BUTTON)
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
        if (plusImg.count() > 0) {
            log.info("Found plus as IMG (count={}), clicking", plusImg.count());
            clickWithRetry(plusImg.first(), 2, 300);
        } else {
            Locator plusBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
            if (plusBtn.count() == 0) {
                throw new IllegalStateException("Plus trigger not found for creating audio album");
            }
            log.info("IMG plus not found; clicking BUTTON plus (count={})", plusBtn.count());
            clickWithRetry(plusBtn.first(), 2, 300);
        }

        // Ensure we are on the type selection / new album screen
        waitVisible(page.getByText(NEW_ALBUM_TITLE), DEFAULT_WAIT);
        Locator typeTitle = page.getByText("What type of album do you");
        waitVisible(typeTitle.first(), DEFAULT_WAIT);

        // Select Audios type and continue
        Locator audiosBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Audios"));
        waitVisible(audiosBtn.first(), DEFAULT_WAIT);
        clickWithRetry(audiosBtn.first(), 2, 300);
        Locator continueBtnType = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        waitVisible(continueBtnType.first(), DEFAULT_WAIT);
        clickWithRetry(continueBtnType.first(), 2, 300);

        // Album name screen
        Locator nameTitle = page.getByText("Give your album a name");
        waitVisible(nameTitle.first(), DEFAULT_WAIT);
        Locator nameTextbox = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("My name"));
        waitVisible(nameTextbox.first(), DEFAULT_WAIT);
        nameTextbox.first().click();
        nameTextbox.first().fill(uniqueName);
        log.info("Filled audio album name: {}", uniqueName);

        Locator continueBtnName = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        waitVisible(continueBtnName.first(), DEFAULT_WAIT);
        clickWithRetry(continueBtnName.first(), 2, 300);

        // Audio import screen
        Locator importMsg = page.getByText("Import or record an audio by");
        waitVisible(importMsg.first(), DEFAULT_WAIT);

        Locator importAudioBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Import an audio file"));
        waitVisible(importAudioBtn.first(), DEFAULT_WAIT);
        clickWithRetry(importAudioBtn.first(), 1, 200);

        // After clicking import, find the underlying input[type=file] and upload the audio file
        Locator inputs = page.locator("input[type=file]");
        if (inputs.count() == 0) {
            // Try to reveal again via plus/import button once more
            log.warn("No file input found immediately after clicking 'Import an audio file'; retrying once");
            clickWithRetry(importAudioBtn.first(), 1, 200);
            inputs = page.locator("input[type=file]");
        }
        if (inputs.count() == 0) {
            throw new IllegalStateException("No file input available for audio upload after 'Import an audio file'");
        }
        Locator audioInput = inputs.nth(inputs.count() - 1);
        audioInput.setInputFiles(audioFile);
        log.info("Set audio file on input: {}", audioFile);

        // Wait for success toast/text
        Locator success = page.getByText(AUDIO_SUCCESS_TEXT);
        waitVisible(success.first(), LONG_WAIT);
        log.info("Audio upload success message visible: '{}'", AUDIO_SUCCESS_TEXT);
        return uniqueName;
    }

    /**
     * Creates an audio-only Quick Files album by recording audio instead of importing a file.
     * Flow: open settings -> Quick Files -> plus -> select Audios -> Continue -> unique album name -> Continue ->
     * Record an audio -> rename recording -> start recording -> wait -> pause -> Confirm -> wait for success toast.
     *
     * @param albumPrefix       base prefix for album name (timestamp appended for uniqueness)
     * @param recordingNameBase base name for recording (timestamp appended for uniqueness)
     * @param recordDurationMs  duration in milliseconds to wait while recording
     * @return generated unique album name
     */
    @Step("Create audio Quick Files album by recording with prefix: {albumPrefix}")
    public final String createAudioAlbumByRecording(String albumPrefix, String recordingNameBase, long recordDurationMs) {
        String p = (albumPrefix == null || albumPrefix.isBlank()) ? "audioalbum_" : albumPrefix;
        if (!p.endsWith("_")) p = p + "_";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(fmt);
        String uniqueAlbumName = p + timestamp;
        String base = (recordingNameBase == null || recordingNameBase.isBlank()) ? "audioRecord" : recordingNameBase;
        String recordingName = base + "_" + timestamp;
        log.info("Creating audio-recording Quick Files album='{}', recordingName='{}' (duration={}ms)", uniqueAlbumName, recordingName, recordDurationMs);

        // Navigate to Quick Files
        openSettingsFromProfile();
        openQuickFiles();

        // Start new album via plus icon (reuse logic similar to createAudioAlbum)
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
        if (plusImg.count() > 0) {
            log.info("Found plus as IMG (count={}), clicking", plusImg.count());
            clickWithRetry(plusImg.first(), 2, 300);
        } else {
            Locator plusBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(PLUS_ICON_NAME));
            if (plusBtn.count() == 0) {
                throw new IllegalStateException("Plus trigger not found for creating audio recording album");
            }
            log.info("IMG plus not found; clicking BUTTON plus (count={})", plusBtn.count());
            clickWithRetry(plusBtn.first(), 2, 300);
        }

        // Ensure we are on the type selection / new album screen
        waitVisible(page.getByText(NEW_ALBUM_TITLE), DEFAULT_WAIT);
        Locator typeTitle = page.getByText("What type of album do you");
        waitVisible(typeTitle.first(), DEFAULT_WAIT);

        // Select Audios type and continue
        Locator audiosBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Audios"));
        waitVisible(audiosBtn.first(), DEFAULT_WAIT);
        clickWithRetry(audiosBtn.first(), 2, 300);
        Locator continueBtnType = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        waitVisible(continueBtnType.first(), DEFAULT_WAIT);
        clickWithRetry(continueBtnType.first(), 2, 300);

        // Album name screen
        Locator nameTitle = page.getByText("Give your album a name");
        waitVisible(nameTitle.first(), DEFAULT_WAIT);
        Locator nameTextbox = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("My name"));
        waitVisible(nameTextbox.first(), DEFAULT_WAIT);
        nameTextbox.first().click();
        nameTextbox.first().fill(uniqueAlbumName);
        log.info("Filled audio album (recording) name: {}", uniqueAlbumName);

        Locator continueBtnName = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        waitVisible(continueBtnName.first(), DEFAULT_WAIT);
        clickWithRetry(continueBtnName.first(), 2, 300);

        // Audio record screen
        Locator importMsg = page.getByText("Import or record an audio by");
        waitVisible(importMsg.first(), DEFAULT_WAIT);

        Locator recordAudioBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Record an audio"));
        waitVisible(recordAudioBtn.first(), DEFAULT_WAIT);
        clickWithRetry(recordAudioBtn.first(), 1, 200);

        // Ensure default title visible and then rename recording
        Locator untitled = page.getByText("Untitled");
        waitVisible(untitled.first(), DEFAULT_WAIT);

        Locator editIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("edit"));
        waitVisible(editIcon.first(), DEFAULT_WAIT);
        clickWithRetry(editIcon.first(), 1, 200);

        Locator recordingTextbox = page.getByRole(AriaRole.TEXTBOX);
        waitVisible(recordingTextbox.first(), DEFAULT_WAIT);
        recordingTextbox.first().fill(recordingName);
        log.info("Set recording name to: {}", recordingName);

        // Start recording
        Locator startRecording = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Start Recording"));
        waitVisible(startRecording.first(), DEFAULT_WAIT);
        clickWithRetry(startRecording.first(), 1, 200);

        Locator waveform = page.locator(".audio-wave-visualization");
        waitVisible(waveform.first(), DEFAULT_WAIT);
        log.info("Audio waveform visualization visible; waiting {}ms to record", recordDurationMs);

        try {
            page.waitForTimeout(recordDurationMs <= 0 ? 10_000 : recordDurationMs);
        } catch (RuntimeException e) {
            log.warn("Recording wait interrupted: {}", e.getMessage());
        }

        // Pause and confirm
        Locator pause = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Pause"));
        waitVisible(pause.first(), DEFAULT_WAIT);
        clickWithRetry(pause.first(), 1, 200);

        Locator confirm = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm"));
        waitVisible(confirm.first(), DEFAULT_WAIT);
        clickWithRetry(confirm.first(), 1, 200);

        // Wait for success toast/text
        Locator success = page.getByText(AUDIO_SUCCESS_TEXT);
        waitVisible(success.first(), LONG_WAIT);
        log.info("Audio-recording upload success message visible: '{}' for album '{}'", AUDIO_SUCCESS_TEXT, uniqueAlbumName);
        return uniqueAlbumName;
    }

    // Convenience helper to build Paths from resource-relative strings
    public static List<Path> resourcePaths(String... relativePaths) {
        return Arrays.stream(relativePaths).map(Paths::get).toList();
    }
}
