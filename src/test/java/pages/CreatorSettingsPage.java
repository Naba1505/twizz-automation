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

    // Locators/labels used in steps
    private static final String SETTINGS_ICON_NAME = "settings"; // role=img name
    private static final String QUICK_FILES_TEXT = "Quick Files";
    private static final String PLUS_ICON_NAME = "plus"; // role=img name or button name in some screens
    private static final String CREATE_BUTTON = "Create"; // role=button name
    private static final String TERMINATE_BUTTON = "Terminate"; // role=button name
    private static final String NEW_ALBUM_TITLE = "New album";
    private static final String MEDIA_TEXT = "Media"; // exact text

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
        waitVisible(page.getByText(NEW_ALBUM_TITLE), DEFAULT_WAIT);
        log.info("Create New Album screen visible, url={}", page.url());
    }

    @Step("Fill album name '{albumName}' and create")
    public void fillAlbumNameAndCreate(String albumName) {
        // Use provided albumName as-is (caller ensures uniqueness). Avoid appending extra timestamp here.
        log.info("Filling album name placeholder 'My name' with value: {}", albumName);
        fillByPlaceholder("My name", albumName);
        log.info("Clicking Create button");
        clickButtonByName(CREATE_BUTTON);
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
            allInputs.first().setInputFiles(filePaths.toArray(new Path[0]));
            logSelectedFilesCount(allInputs.first(), total);
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
                input.setInputFiles(combined.toArray(new Path[0]));
                logSelectedFilesCount(input, combined.size());
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
            input.setInputFiles(files.toArray(new Path[0]));
            logSelectedFilesCount(input, files.size());
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
                    current.setInputFiles(new Path[]{f});
                    // brief settle; real UIs usually queue the file instantly
                    try { page.waitForTimeout(SEQUENTIAL_PAUSE_MS); } catch (Exception ignored) {}
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
                    log.info("PLUS button not available for tab '{}'; aborting sequential PLUS path", tabName);
                    return false;
                }
                clickWithRetry(plus, 1, 150);
                log.info("[PLUS] Sequential ({}/{}): {} in tab '{}'", i + 1, files.size(), f.getFileName(), tabName);
                plus.setInputFiles(new Path[]{f});
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
        Locator tab = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(tabName));
        if (tab.count() > 0) {
            log.info("Switching to '{}' tab", tabName);
            clickWithRetry(tab.first(), 2, 300);
            return true;
        }
        // Fallback: plain text tab
        Locator textTab = page.getByText(tabName);
        if (textTab.count() > 0) {
            log.info("Switching to '{}' section via text", tabName);
            clickWithRetry(textTab.first(), 2, 300);
            return true;
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
        log.info("No input matched acceptKeyword '{}'; using first input index 0 as fallback", acceptKeyword);
        return 0;
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

    // Convenience helper to build Paths from resource-relative strings
    public static List<Path> resourcePaths(String... relativePaths) {
        return Arrays.stream(relativePaths).map(Paths::get).toList();
    }
}
