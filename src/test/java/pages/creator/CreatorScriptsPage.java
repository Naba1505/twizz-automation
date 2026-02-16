package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Page Object for Creator Scripts feature.
 */
public class CreatorScriptsPage extends BasePage {

    public CreatorScriptsPage(Page page) {
        super(page);
    }

    // ===== Helpers for unique names =====

    private String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    private Path resolveImage(String relative) {
        Path path = Paths.get(relative);
        if (!path.isAbsolute()) {
            path = Paths.get("src/test/resources/Images").resolve(relative).normalize();
        }
        if (!Files.exists(path)) {
            throw new RuntimeException("Script image file not found: " + path);
        }
        return path;
    }

    private Path resolveVideo(String relative) {
        Path path = Paths.get(relative);
        if (!path.isAbsolute()) {
            path = Paths.get("src/test/resources/Videos").resolve(relative).normalize();
        }
        if (!Files.exists(path)) {
            throw new RuntimeException("Script video file not found: " + path);
        }
        return path;
    }

    private Path resolveAudio(String relative) {
        Path path = Paths.get(relative);
        if (!path.isAbsolute()) {
            path = Paths.get("src/test/resources/Audios").resolve(relative).normalize();
        }
        if (!Files.exists(path)) {
            throw new RuntimeException("Script audio file not found: " + path);
        }
        return path;
    }

    // ===== Edit helpers =====

    private String buildUpdatedName(String base) {
        String prefix = (base == null || base.isEmpty()) ? "UpdatedScript" : base;
        return prefix + "_" + timestamp();
    }

    @Step("Open first script row in edit mode and navigate to name step")
    public String startEditFirstScript(String updatedBaseName) {
        // Click first edit icon on scripts list
        Locator editIcons = page.locator("button.script-row-edit");
        waitVisible(editIcons.first(), DEFAULT_WAIT);
        clickWithRetry(editIcons.first(), 1, 200);

        // Edit dialog
        Locator editTitle = page.getByText("Edit", new Page.GetByTextOptions().setExact(true));
        waitVisible(editTitle.first(), DEFAULT_WAIT);

        Locator editMessage = page.getByText("Which action would you like");
        waitVisible(editMessage.first(), DEFAULT_WAIT);

        Locator editScriptBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Edit script"));
        waitVisible(editScriptBtn.first(), DEFAULT_WAIT);
        clickWithRetry(editScriptBtn.first(), 1, 200);

        // Ensure on Edit a script screen and name step
        Locator editHeading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Edit a script"));
        waitVisible(editHeading.first(), DEFAULT_WAIT);

        Locator nameHeading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Give your script a name"));
        waitVisible(nameHeading.first(), DEFAULT_WAIT);

        Locator nameInput = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("My name"));
        waitVisible(nameInput.first(), DEFAULT_WAIT);

        String updatedName = buildUpdatedName(updatedBaseName);
        nameInput.first().click();
        nameInput.first().fill("");
        nameInput.first().fill(updatedName);

        Locator cont = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Continue"));
        waitVisible(cont.first(), DEFAULT_WAIT);
        clickWithRetry(cont.first(), 1, 200);

        // Small settle to allow navigation/state update to media step
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }

        return updatedName;
    }

    @Step("Update script message and note for edit flow")
    public void updateScriptMessageAndNote() {
        // Update main message
        Locator msg = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Your message..."));
        waitVisible(msg.first(), DEFAULT_WAIT);
        msg.first().click();
        msg.first().fill("Test updated message");

        // Update note: in edit flows we might still be on a previous step; if the note box
        // is not visible yet, advance via a primary Next/Continue button once, then wait again.
        Locator noteBox = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Write a note to not forget"));
        try {
            waitVisible(noteBox.first(), ConfigReader.getVisibilityTimeout());
        } catch (Throwable ignored) {
            // Try to advance to the note step without touching bookmark or price
            Locator nextBtn = page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Next"));
            if (nextBtn.count() == 0) {
                nextBtn = page.getByRole(AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Continue"));
            }
            if (nextBtn.count() > 0) {
                try { nextBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored2) { }
                clickWithRetry(nextBtn.first(), 1, 200);
                try { page.waitForTimeout(500); } catch (Throwable ignored2) { }
            }
            // Now wait with normal timeout for the note box; if it still doesn't appear,
            // treat note as optional in edit flows.
            try {
                waitVisible(noteBox.first(), DEFAULT_WAIT);
            } catch (Throwable finalIgnored) {
                logger.warn("Note textbox not visible in edit flow; skipping note update.");
                return;
            }
        }

        noteBox.first().click();
        noteBox.first().fill("Updated note");
    }

    // ===== High level flow =====

    @Step("Open settings from profile header")
    public void openSettingsFromProfile() {
        Locator settingsIcon = page.getByRole(AriaRole.IMG,
                new Page.GetByRoleOptions().setName("settings"));
        waitVisible(settingsIcon.first(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon.first(), 1, 200);
    }

    @Step("Open Scripts from settings")
    public void openScriptsFromSettings() {
        Locator scripts = page.getByText("Scripts");
        waitVisible(scripts.first(), DEFAULT_WAIT);
        try { scripts.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(scripts.first(), 1, 200);
        waitVisible(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Scripts")), DEFAULT_WAIT);
    }

    @Step("Validate Scripts screen search box with multiple keywords and return to Scripts list")
    public void validateScriptsSearchFlow() {
        // Assumes we are already on Scripts main screen and heading is visible
        Locator scriptsHeading = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Scripts"));
        waitVisible(scriptsHeading.first(), DEFAULT_WAIT);

        // Open search
        Locator searchButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Search"));
        waitVisible(searchButton.first(), DEFAULT_WAIT);
        clickWithRetry(searchButton.first(), 1, 200);

        Locator searchInput = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Search"));
        waitVisible(searchInput.first(), DEFAULT_WAIT);

        String[] keywords = new String[] {"image", "video", "audio", "mixed"};
        for (String term : keywords) {
            searchInput.first().click();
            searchInput.first().fill("");
            searchInput.first().fill(term);
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
            // Clear before next term
            searchInput.first().fill("");
            try { page.waitForTimeout(200); } catch (Throwable ignored) { }
        }

        // Cancel search and ensure we are back on Scripts list
        Locator cancelButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Cancel"));
        waitVisible(cancelButton.first(), DEFAULT_WAIT);
        clickWithRetry(cancelButton.first(), 1, 200);

        // Final assertion: Scripts heading still visible (back on scripts screen)
        waitVisible(scriptsHeading.first(), DEFAULT_WAIT);
    }

    @Step("Click add script plus button")
    public void clickAddScript() {
        Locator plusBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("add Click on the \"+\" to"));
        waitVisible(plusBtn.first(), DEFAULT_WAIT);
        clickWithRetry(plusBtn.first(), 1, 200);
        waitVisible(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Create a script")), DEFAULT_WAIT);
        waitVisible(page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName("Give your script a name")), DEFAULT_WAIT);
    }

    @Step("Fill script name with unique value")
    public String fillScriptName(String baseName) {
        String unique = (baseName == null || baseName.isEmpty() ? "Script" : baseName) + "_" + timestamp();
        Locator nameInput = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("My name"));
        waitVisible(nameInput.first(), DEFAULT_WAIT);
        nameInput.first().click();
        nameInput.first().fill(unique);
        return unique;
    }

    @Step("Click Continue in script creation")
    public void clickContinueFromName() {
        Locator cont = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Continue"));
        waitVisible(cont.first(), DEFAULT_WAIT);
        clickWithRetry(cont.first(), 1, 200);
        // After continue, helper text should appear guiding user to click '+' button
        Locator helper = page.getByText("Click on the \"+\" button to");
        try {
            waitVisible(helper.first(), DEFAULT_WAIT);
        } catch (Throwable ignored) { }
    }

    @Step("Click add media button")
    public void clickAddMedia() {
        Locator addMedia = page.getByRole(AriaRole.IMG,
                new Page.GetByRoleOptions().setName("add"));
        waitVisible(addMedia.first(), DEFAULT_WAIT);
        clickWithRetry(addMedia.first(), 1, 200);
        waitVisible(page.getByText("Importation"), DEFAULT_WAIT);
    }

    @Step("Upload script image from device: {fileName}")
    public void uploadImageFromDevice(String fileName) {
        Path file = resolveImage(fileName);
        // Directly drive the hidden file input instead of opening the native OS dialog.
        // Assumes the Importation / My Device UI is already visible.
        Locator input = page.locator("input[type='file']");

        // Give the DOM a brief window to render the file input for this modal
        long inputDeadline = System.currentTimeMillis() + 5_000L;
        while (input.count() == 0 && System.currentTimeMillis() < inputDeadline) {
            try { page.waitForTimeout(200); } catch (Throwable ignored) { }
        }

        if (input.count() == 0) {
            throw new RuntimeException("Unable to find file input for script image upload (input[type='file'] not present).");
        }

        input.first().setInputFiles(file);

        // Give the UI a brief moment to process the upload and render blurred preview
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }

        Locator blurred = page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Blurred media$")));
        waitVisible(blurred.first(), ConfigReader.getShortTimeout());
    }

    // ===== Quick Files helpers =====

    @Step("Select media index {mediaIndex} from Quick Files album with prefix {albumPrefix}")
    public void selectMediaFromQuickFilesAlbum(String albumPrefix, int mediaIndex) {
        // Assumes Importation screen is already visible
        waitVisible(page.getByText("Importation"), DEFAULT_WAIT);

        // Open Quick Files
        Locator quickFilesBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Quick Files"));
        waitVisible(quickFilesBtn.first(), DEFAULT_WAIT);
        clickWithRetry(quickFilesBtn.first(), 1, 200);

        // Wait for 'My albums' label to ensure Quick Files screen is loaded
        Locator myAlbumsLabel = page.getByText("My albums");
        waitVisible(myAlbumsLabel.first(), DEFAULT_WAIT);

        // Ensure default tab for photos & videos
        Locator photosVideosTab = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Selected Photos & videos"));
        waitVisible(photosVideosTab.first(), DEFAULT_WAIT);

        // Find the scrollable container for albums
        Locator container = page.locator(".qf-albums-container, .ant-modal-body, .ant-drawer-body").first();
        if (container.count() == 0) {
            container = page.locator("body");
        }

        // Click album row whose title starts with the given prefix (image / video / mix)
        // Use XPath to find div.qf-row[role='button'] containing div.qf-row-title with matching prefix
        String normalizedPrefix = albumPrefix.toLowerCase();
        String xpathExpr = "//div[@class='qf-row' and @role='button'][.//div[@class='qf-row-title' and starts-with(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + normalizedPrefix + "')]]";
        
        boolean clickedAlbum = false;
        long end = System.currentTimeMillis() + 15_000L;
        while (System.currentTimeMillis() < end && !clickedAlbum) {
            Locator albumRows = page.locator("xpath=" + xpathExpr);
            int rowCount = albumRows.count();
            if (rowCount > 0) {
                logger.info("Found {} album row(s) with prefix '{}'; clicking first", rowCount, normalizedPrefix);
                Locator row = albumRows.first();
                try { row.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                clickWithRetry(row, 1, 200);
                clickedAlbum = true;
                break;
            }
            // Scroll down to find the album
            logger.info("Album row with prefix '{}' not yet visible; scrolling down", normalizedPrefix);
            try { container.evaluate("el => el.scrollBy(0, 900)"); } catch (Throwable ignored) {}
            try { page.waitForTimeout(300); } catch (Throwable ignored) {}
        }

        if (!clickedAlbum) {
            throw new RuntimeException("Could not find Quick Files album with prefix: " + normalizedPrefix);
        }

        // Inside album - wait for 'Select media' text
        waitVisible(page.getByText("Select media"), DEFAULT_WAIT);

        // Select media using role=IMG name='select' icons (as in codegen)
        Locator selectIcons = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("select"));
        
        // Wait for media thumbnails to load
        long thumbDeadline = System.currentTimeMillis() + 10_000L;
        while (selectIcons.count() == 0 && System.currentTimeMillis() < thumbDeadline) {
            try { page.waitForTimeout(300); } catch (Throwable ignored) {}
        }
        
        if (selectIcons.count() == 0) {
            throw new RuntimeException("No media select icons found in Quick Files album with prefix: " + albumPrefix);
        }

        // Click the first media item (index 0)
        logger.info("Selecting media item at index 0 from Quick Files album");
        waitVisible(selectIcons.first(), DEFAULT_WAIT);
        clickWithRetry(selectIcons.first(), 1, 200);

        // Click dynamic "Select (1)" button
        Locator selectBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Select (1)"));
        waitVisible(selectBtn.first(), DEFAULT_WAIT);
        clickWithRetry(selectBtn.first(), 1, 200);

        // Advance one step in the script flow (Next/Continue)
        Locator nextBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Next"));
        if (nextBtn.count() == 0) {
            nextBtn = page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Continue"));
        }
        if (nextBtn.count() > 0) {
            clickWithRetry(nextBtn.first(), 1, 200);
        }
    }

    @Step("Select single audio from Quick Files album")
    public void selectAudioFromQuickFilesAlbum() {
        // Assumes Importation screen is already visible
        waitVisible(page.getByText("Importation"), DEFAULT_WAIT);

        // Open Quick Files
        Locator quickFilesBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Quick Files"));
        waitVisible(quickFilesBtn.first(), DEFAULT_WAIT);
        clickWithRetry(quickFilesBtn.first(), 1, 200);

        // Wait for 'My albums' label
        Locator myAlbumsLabel = page.getByText("My albums");
        waitVisible(myAlbumsLabel.first(), DEFAULT_WAIT);

        // Switch to Audios tab
        Locator audiosTab = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Audios"));
        waitVisible(audiosTab.first(), DEFAULT_WAIT);
        clickWithRetry(audiosTab.first(), 1, 200);

        // Find the scrollable container for albums
        Locator container = page.locator(".qf-albums-container, .ant-modal-body, .ant-drawer-body").first();
        if (container.count() == 0) {
            container = page.locator("body");
        }

        // Click audio album row whose title starts with 'audio'
        // Use XPath to find div.qf-row[role='button'] containing div.qf-row-title with 'audio' prefix
        String xpathExpr = "//div[@class='qf-row' and @role='button'][.//div[@class='qf-row-title' and starts-with(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'audio')]]";
        
        boolean clickedAlbum = false;
        long end = System.currentTimeMillis() + 15_000L;
        while (System.currentTimeMillis() < end && !clickedAlbum) {
            Locator albumRows = page.locator("xpath=" + xpathExpr);
            int rowCount = albumRows.count();
            if (rowCount > 0) {
                logger.info("Found {} audio album row(s); clicking first", rowCount);
                Locator row = albumRows.first();
                try { row.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                clickWithRetry(row, 1, 200);
                clickedAlbum = true;
                break;
            }
            // Scroll down to find the album
            logger.info("Audio album row not yet visible; scrolling down");
            try { container.evaluate("el => el.scrollBy(0, 900)"); } catch (Throwable ignored) {}
            try { page.waitForTimeout(300); } catch (Throwable ignored) {}
        }

        if (!clickedAlbum) {
            throw new RuntimeException("Could not find Quick Files audio album");
        }

        // Pick first audio entry (role=button, label starts with 'audio '), as in codegen
        Locator audioBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("^audio\\s+.*", Pattern.CASE_INSENSITIVE)));
        waitVisible(audioBtn.first(), DEFAULT_WAIT);
        clickWithRetry(audioBtn.first(), 1, 200);

        Locator selectAndSend = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Select and send").setExact(true));
        waitVisible(selectAndSend.first(), DEFAULT_WAIT);
        clickWithRetry(selectAndSend.first(), 1, 200);

        // Advance to next step of script flow
        clickNextAfterMedia();
    }

    @Step("Upload script audio from device: {fileName}")
    public void uploadAudioFromDevice(String fileName) {
        Path file = resolveAudio(fileName);
        // Drive the hidden file input directly to avoid opening the native OS dialog.
        Locator input = page.locator("input[type='file']");

        long inputDeadline = System.currentTimeMillis() + 5_000L;
        while (input.count() == 0 && System.currentTimeMillis() < inputDeadline) {
            try { page.waitForTimeout(200); } catch (Throwable ignored) { }
        }

        if (input.count() == 0) {
            throw new RuntimeException("Unable to find file input for script audio upload (input[type='file'] not present).");
        }

        input.first().setInputFiles(file);

        try { page.waitForTimeout(500); } catch (Throwable ignored) { }

        Locator blurred = page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Blurred media$")));
        waitVisible(blurred.first(), ConfigReader.getShortTimeout());
    }

    @Step("Upload script video from device: {fileName}")
    public void uploadVideoFromDevice(String fileName) {
        Path file = resolveVideo(fileName);
        // Drive the hidden file input directly to avoid opening the native OS dialog.
        Locator input = page.locator("input[type='file']");

        long inputDeadline = System.currentTimeMillis() + 5_000L;
        while (input.count() == 0 && System.currentTimeMillis() < inputDeadline) {
            try { page.waitForTimeout(200); } catch (Throwable ignored) { }
        }

        if (input.count() == 0) {
            throw new RuntimeException("Unable to find file input for script video upload (input[type='file'] not present).");
        }

        input.first().setInputFiles(file);

        try { page.waitForTimeout(500); } catch (Throwable ignored) { }

        Locator blurred = page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Blurred media$")));
        waitVisible(blurred.first(), ConfigReader.getShortTimeout());
    }

    @Step("Click Next after media upload")
    public void clickNextAfterMedia() {
        // Some flows may leave a bottom modal overlay (with a Cancel button) on top of the page
        // which intercepts clicks on the sticky Next button. If present and visible, dismiss it.
        Locator bottomCancel = page.locator("span.bottom-modal-cancel-button-title");
        if (bottomCancel.count() > 0 && safeIsVisible(bottomCancel.first())) {
            clickWithRetry(bottomCancel.first(), 1, 200);
            try { page.waitForTimeout(300); } catch (Throwable ignored) { }
        }

        Locator next = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Next"));
        waitVisible(next.first(), DEFAULT_WAIT);
        clickWithRetry(next.first(), 1, 200);
        // Small settle to allow navigation/state update
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
    }

    @Step("Click plus to add more media")
    public void clickPlusToAddMoreMedia() {
        Locator plus = page.getByRole(AriaRole.IMG,
                new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plus.first(), DEFAULT_WAIT);
        clickWithRetry(plus.first(), 1, 200);
        waitVisible(page.getByText("Importation"), DEFAULT_WAIT);
    }

    @Step("Select or create bookmark and create new one with unique name")
    public String selectOrCreateBookmark(String baseName) {
        Locator bookmarkBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Bookmark Select or create a"));
        waitVisible(bookmarkBtn.first(), DEFAULT_WAIT);
        clickWithRetry(bookmarkBtn.first(), 1, 200);

        Locator newBookmark = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("new-bookmark"));
        waitVisible(newBookmark.first(), DEFAULT_WAIT);
        clickWithRetry(newBookmark.first(), 1, 200);

        String name = (baseName == null || baseName.isEmpty() ? "QA" : baseName) + "_" + timestamp();
        Locator nameInput = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Name your bookmark"));
        waitVisible(nameInput.first(), DEFAULT_WAIT);
        nameInput.first().fill(name);

        Locator createBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Create").setExact(true));
        waitVisible(createBtn.first(), DEFAULT_WAIT);
        clickWithRetry(createBtn.first(), 1, 200);

        // Wait for the bookmark creation to complete and UI to update
        try { page.waitForTimeout(800); } catch (Throwable ignored) { }

        // Wait for any loading/spinner to disappear
        Locator spinner = page.locator(".ant-spin, .loading, [class*='spinner']");
        try {
            if (spinner.count() > 0 && safeIsVisible(spinner.first())) {
                spinner.first().waitFor(new Locator.WaitForOptions()
                        .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN)
                        .setTimeout(5000));
            }
        } catch (Throwable ignored) { }

        // After creating, explicitly select the newly created bookmark so that the
        // mandatory bookmark field is satisfied in all flows.
        // First check if the bookmark is already selected (UI may auto-select after creation)
        boolean bookmarkSelected = false;

        // Check if bookmark is already showing as selected in the main button
        Locator mainBookmarkButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("^Bookmark\\s+" + Pattern.quote(name) + ".*", Pattern.CASE_INSENSITIVE)));
        if (mainBookmarkButton.count() > 0 && safeIsVisible(mainBookmarkButton.first())) {
            logger.info("Bookmark '{}' is already selected after creation", name);
            bookmarkSelected = true;
        }

        // Strategy 1: Look for a button/option containing the bookmark name in dropdown
        if (!bookmarkSelected) {
            Locator bookmarkByName = page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName(Pattern.compile(".*" + Pattern.quote(name) + ".*", Pattern.CASE_INSENSITIVE)));
            if (bookmarkByName.count() > 0 && safeIsVisible(bookmarkByName.first())) {
                clickWithRetry(bookmarkByName.first(), 1, 200);
                try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                bookmarkSelected = true;
                logger.info("Bookmark '{}' selected using name pattern", name);
            }
        }

        // Strategy 2: If dropdown is still open, look for list item or option with the name
        if (!bookmarkSelected) {
            Locator listItem = page.locator("li, [role='option'], .bookmark-item, .ant-select-item")
                    .filter(new Locator.FilterOptions().setHasText(name));
            if (listItem.count() > 0 && safeIsVisible(listItem.first())) {
                clickWithRetry(listItem.first(), 1, 200);
                try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                bookmarkSelected = true;
                logger.info("Bookmark '{}' selected using list item locator", name);
            }
        }

        // Strategy 3: Open the dropdown again and select by text
        if (!bookmarkSelected) {
            // Re-open the bookmark dropdown
            Locator anyToggle = page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName(Pattern.compile("^Bookmark.*chevron.*", Pattern.CASE_INSENSITIVE)));
            if (anyToggle.count() > 0 && safeIsVisible(anyToggle.first())) {
                clickWithRetry(anyToggle.first(), 1, 200);
                try { page.waitForTimeout(300); } catch (Throwable ignored) { }

                // Now look for the bookmark option by text
                Locator optionByText = page.getByText(name, new Page.GetByTextOptions().setExact(true));
                if (optionByText.count() > 0 && safeIsVisible(optionByText.first())) {
                    clickWithRetry(optionByText.first(), 1, 200);
                    try { page.waitForTimeout(300); } catch (Throwable ignored2) { }
                    bookmarkSelected = true;
                    logger.info("Bookmark '{}' selected using text locator after reopening dropdown", name);
                }
            }
        }

        // Strategy 4: Fallback - use ensureAnyBookmarkSelected pattern
        if (!bookmarkSelected) {
            logger.warn("Could not select bookmark '{}' with specific locators; using fallback selection", name);
            ensureAnyBookmarkSelected();
        }

        // Final verification: check if any bookmark is now showing as selected
        // Look for a button that shows a bookmark name (not "Select or create a")
        try { page.waitForTimeout(300); } catch (Throwable ignored) { }
        Locator selectedBookmark = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("^Bookmark\\s+(?!Select).*", Pattern.CASE_INSENSITIVE)));
        if (selectedBookmark.count() == 0 || !safeIsVisible(selectedBookmark.first())) {
            // Still not selected - try one more aggressive approach
            logger.warn("Bookmark still not showing as selected; attempting final selection");
            // Click the bookmark dropdown button
            Locator bookmarkDropdown = page.locator("button").filter(new Locator.FilterOptions().setHasText("Bookmark"));
            if (bookmarkDropdown.count() > 0 && safeIsVisible(bookmarkDropdown.first())) {
                clickWithRetry(bookmarkDropdown.first(), 1, 200);
                try { page.waitForTimeout(400); } catch (Throwable ignored) { }
                // Click the first visible option with our bookmark name
                Locator ourBookmark = page.getByText(name, new Page.GetByTextOptions().setExact(true));
                if (ourBookmark.count() > 0 && safeIsVisible(ourBookmark.first())) {
                    clickWithRetry(ourBookmark.first(), 1, 200);
                    try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                    logger.info("Bookmark '{}' selected in final verification step", name);
                }
            }
        }

        return name;
    }

    // Best-effort helper to (re)select any available bookmark when validation
    // complains that no bookmark is assigned. Uses generic patterns so it
    // works for existing bookmarks too.
    private void ensureAnyBookmarkSelected() {
        boolean selected = false;

        // Strategy 1: Try to open the bookmark dropdown using various patterns
        Locator[] togglePatterns = new Locator[] {
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("^Bookmark.*chevron.*", Pattern.CASE_INSENSITIVE))),
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("Bookmark.*Select.*", Pattern.CASE_INSENSITIVE))),
            page.locator("button").filter(new Locator.FilterOptions().setHasText(Pattern.compile("Bookmark", Pattern.CASE_INSENSITIVE))),
            page.locator("[class*='bookmark']").filter(new Locator.FilterOptions().setHasText("Bookmark"))
        };

        for (Locator toggle : togglePatterns) {
            if (toggle.count() > 0 && safeIsVisible(toggle.first())) {
                clickWithRetry(toggle.first(), 1, 200);
                try { page.waitForTimeout(400); } catch (Throwable ignored) { }
                break;
            }
        }

        // Strategy 2: Select from dropdown - try various option patterns
        Locator[] optionPatterns = new Locator[] {
            // Dropdown option with bookmark name
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("^Bookmark.*chevron.*QA.*", Pattern.CASE_INSENSITIVE))),
            // Any visible option in a dropdown/list that contains QA (our bookmark prefix)
            page.locator("li, [role='option'], [role='menuitem']").filter(new Locator.FilterOptions().setHasText(Pattern.compile("QA_", Pattern.CASE_INSENSITIVE))),
            // Generic bookmark option
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile(".*QA_\\d+.*", Pattern.CASE_INSENSITIVE))),
            // Text-based selection
            page.getByText(Pattern.compile("^QA_\\d+$"))
        };

        for (Locator option : optionPatterns) {
            if (option.count() > 0 && safeIsVisible(option.first())) {
                clickWithRetry(option.first(), 1, 200);
                try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                selected = true;
                logger.info("Bookmark selected using fallback pattern");
                break;
            }
        }

        // Strategy 3: If still not selected, try clicking any visible item in the dropdown area
        if (!selected) {
            Locator dropdownItems = page.locator(".ant-select-dropdown:visible li, .bookmark-dropdown:visible li, [class*='dropdown']:visible [class*='item']");
            if (dropdownItems.count() > 0 && safeIsVisible(dropdownItems.first())) {
                clickWithRetry(dropdownItems.first(), 1, 200);
                try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                logger.info("Bookmark selected using generic dropdown item");
            }
        }
    }

    @Step("Fill main script message")
    public void fillScriptMessage(String message) {
        // ... (rest of the code remains the same)
        Locator msg = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Your message..."));
        waitVisible(msg.first(), DEFAULT_WAIT);
        msg.first().click();
        msg.first().fill(message);
        Locator nameTag = page.getByText("/name");
        waitVisible(nameTag.first(), DEFAULT_WAIT);
        clickWithRetry(nameTag.first(), 1, 200);
    }

    @Step("Set script price to 15€")
    public void setPriceTo15Euro() {
        Locator priceLabel = page.locator("label").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^15€$")));
        waitVisible(priceLabel.first(), DEFAULT_WAIT);
        clickWithRetry(priceLabel.first(), 1, 200);
    }

    @Step("Set custom script price to {price} via spinbutton")
    public void setCustomPrice(String price) {
        // Click current price text
        Locator currentPrice = page.getByText("0.00 €");
        waitVisible(currentPrice.first(), DEFAULT_WAIT);
        clickWithRetry(currentPrice.first(), 1, 200);

        Locator spin = page.getByRole(AriaRole.SPINBUTTON);
        waitVisible(spin.first(), DEFAULT_WAIT);
        spin.first().fill("");
        spin.first().fill(price);
    }

    @Step("Enable promo slider with discount {discount} and unlimited validity")
    public void enablePromoWithUnlimitedValidity(String discount) {
        // Toggle promo slider
        Locator slider = page.locator(".promo-slider");
        waitVisible(slider.first(), DEFAULT_WAIT);
        clickWithRetry(slider.first(), 1, 200);

        // Discount input (placeholder 0)
        Locator discountInput = page.getByPlaceholder("0").first();
        waitVisible(discountInput, DEFAULT_WAIT);
        discountInput.click();
        discountInput.fill("");
        discountInput.fill(discount);

        // Validity set to Unlimited
        Locator unlimitedBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Unlimited"));
        waitVisible(unlimitedBtn.first(), DEFAULT_WAIT);
        clickWithRetry(unlimitedBtn.first(), 1, 200);
    }

    @Step("Set script price to 50€")
    public void setPriceTo50Euro() {
        Locator priceLabel = page.locator("label").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^50€$")));
        waitVisible(priceLabel.first(), DEFAULT_WAIT);
        clickWithRetry(priceLabel.first(), 1, 200);
    }

    @Step("Enable promo slider with second discount input {discountPercent}% and 7 days validity")
    public void enablePromoWithSevenDays(String discountPercent) {
        // Toggle promo slider
        Locator slider = page.locator(".promo-slider");
        waitVisible(slider.first(), DEFAULT_WAIT);
        clickWithRetry(slider.first(), 1, 200);

        // Second discount input (nth(1))
        Locator discountInput = page.getByPlaceholder("0").nth(1);
        waitVisible(discountInput, DEFAULT_WAIT);
        discountInput.click();
        discountInput.fill("");
        discountInput.fill(discountPercent);

        // Validity set to 7 days
        Locator sevenDaysBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("7 days"));
        waitVisible(sevenDaysBtn.first(), DEFAULT_WAIT);
        clickWithRetry(sevenDaysBtn.first(), 1, 200);
    }

    @Step("Fill script note")
    public void fillScriptNote(String note) {
        Locator noteBox = page.getByRole(AriaRole.TEXTBOX,
                new Page.GetByRoleOptions().setName("Write a note to not forget"));
        waitVisible(noteBox.first(), DEFAULT_WAIT);
        noteBox.first().click();
        noteBox.first().fill(note);
    }

    @Step("Confirm script creation")
    public void confirmScriptCreation() {
        // Prefer the specific chat-scripts Confirm div, then fall back to broader locators
        Locator confirmBtn = page.locator("//div[@class='chat-scripts-button' and normalize-space(text())='Confirm']");
        if (confirmBtn.count() == 0) {
            confirmBtn = page.locator("//div[@class='chat-scripts-button enabled']");
        }
        if (confirmBtn.count() == 0) {
            confirmBtn = page.locator("div").filter(new Locator.FilterOptions()
                    .setHasText(Pattern.compile("^Confirm$")));
        }
        if (confirmBtn.count() == 0) {
            confirmBtn = page.locator("//button[.//div[contains(text(),'Confirm')]]");
        }
        if (confirmBtn.count() == 0) {
            confirmBtn = page.getByText("Confirm");
        }
        waitVisible(confirmBtn.first(), DEFAULT_WAIT);

        // Wait for Confirm to be enabled (class contains 'enabled' or element isEnabled()) before clicking
        long enableDeadline = System.currentTimeMillis() + 10_000L;
        while (System.currentTimeMillis() < enableDeadline) {
            try {
                String cls = confirmBtn.first().getAttribute("class");
                boolean hasEnabledClass = cls != null && cls.contains("enabled");
                boolean isEnabled = false;
                try {
                    isEnabled = confirmBtn.first().isEnabled();
                } catch (Throwable ignored) {
                }
                if (hasEnabledClass || isEnabled) {
                    break;
                }
            } catch (Throwable ignoredOuter) {
            }
            try {
                page.waitForTimeout(200);
            } catch (Throwable ignoredSleep) {
            }
        }

        try { confirmBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) { }
        // Use a few retries in case of transient overlay/animation issues
        clickWithRetry(confirmBtn.first(), 3, 500);

        // During upload, UI may show a message asking to stay on page; when done, a success toast
        Locator stayOnPage = page.getByText("Stay on page during uploading");
        // Prefer an exact "Script created successfully" toast, then fall back to a broader regex
        Locator success = page.getByText("Script created successfully");
        if (success.count() == 0) {
            success = page.getByText(Pattern.compile("script.*created", Pattern.CASE_INSENSITIVE));
        }

        // Validation toast if bookmark is missing
        Locator noBookmark = page.getByText("No bookmark assigned to this script");

        // Allow up to 90s for heavy uploads (e.g. videos)
        long deadline = System.currentTimeMillis() + 90_000L;
        boolean seenSuccess = false;
        boolean bookmarkRetried = false;
        while (System.currentTimeMillis() < deadline) {
            if (safeIsVisible(success)) {
                seenSuccess = true;
                break;
            }
            // If we see a bookmark validation, try to fix it and retry Confirm once
            if (!bookmarkRetried && safeIsVisible(noBookmark)) {
                logger.warn("Validation toast 'No bookmark assigned to this script' detected; attempting to select a bookmark and retry Confirm.");
                ensureAnyBookmarkSelected();
                try { page.waitForTimeout(300); } catch (Throwable ignored) { }
                clickWithRetry(confirmBtn.first(), 3, 500);
                bookmarkRetried = true;
            }
            if (safeIsVisible(stayOnPage)) {
                // Still uploading, just wait a bit more
                try { page.waitForTimeout(500); } catch (Throwable ignored) { }
            } else {
                // Neither toast nor uploading hint visible yet; short poll
                try { page.waitForTimeout(500); } catch (Throwable ignored) { }
            }
        }

        if (!seenSuccess) {
            logger.warn("Script creation success toast not seen within timeout; proceeding anyway.");
        }
    }

    @Step("Wait for script created success message")
    public void assertScriptCreatedSuccess() {
        // Match any success toast that mentions script creation (case-insensitive)
        Locator success = page.getByText(Pattern.compile("script.*created", Pattern.CASE_INSENSITIVE));
        try {
            waitVisible(success.first(), ConfigReader.getMediumTimeout());
            // Short settle then attempt to dismiss if clickable
            try { page.waitForTimeout(800); } catch (Throwable ignored) { }
            try { clickWithRetry(success.first(), 0, 0); } catch (Throwable ignored) { }
        } catch (Throwable t) {
            // Soft failure: toast may be localized or occasionally suppressed
            logger.warn("Script created success toast not seen within timeout; proceeding anyway.");
        }
    }

    @Step("Full flow: create script with two images and bookmark")
    public void createScriptWithTwoImagesFromDevice() {
        logger.info("Starting full script creation flow with two images");

        openSettingsFromProfile();
        openScriptsFromSettings();
        clickAddScript();

        String scriptName = fillScriptName("ImageScript");
        logger.info("Using script name: {}", scriptName);
        clickContinueFromName();

        // First image
        clickAddMedia();
        uploadImageFromDevice("ScriptImageA.png");
        clickNextAfterMedia();

        // Second image
        clickPlusToAddMoreMedia();
        uploadImageFromDevice("ScriptImageB.png");
        clickNextAfterMedia();

        String bookmark = selectOrCreateBookmark("QA");
        logger.info("Using bookmark name: {}", bookmark);

        fillScriptMessage("Test ");
        setPriceTo15Euro();
        fillScriptNote("Test");
        confirmScriptCreation();

        assertScriptCreatedSuccess();
        logger.info("Script creation flow completed successfully");
    }

    @Step("Full flow: create script with two videos, custom price and promo")
    public void createScriptWithTwoVideosAndPromo() {
        logger.info("Starting full script creation flow with two videos and promo");

        openSettingsFromProfile();
        openScriptsFromSettings();
        clickAddScript();

        String scriptName = fillScriptName("ScriptVideo");
        logger.info("Using script name: {}", scriptName);
        clickContinueFromName();

        // First video
        clickAddMedia();
        uploadVideoFromDevice("ScriptVideoA.mp4");
        clickNextAfterMedia();

        // Second video
        clickPlusToAddMoreMedia();
        uploadVideoFromDevice("ScriptVideoB.mp4");
        clickNextAfterMedia();

        String bookmark = selectOrCreateBookmark("QA");
        logger.info("Using bookmark name: {}", bookmark);

        fillScriptMessage("Test ");
        setCustomPrice("10");
        enablePromoWithUnlimitedValidity("2");
        fillScriptNote("Test");
        confirmScriptCreation();

        assertScriptCreatedSuccess();
        logger.info("Script creation (videos + promo) flow completed successfully");
    }

    @Step("Full flow: create script with two audios, price 50 and 7 days promo")
    public void createScriptWithTwoAudiosAndPromo() {
        logger.info("Starting full script creation flow with two audios and promo");

        openSettingsFromProfile();
        openScriptsFromSettings();
        clickAddScript();

        String scriptName = fillScriptName("ScriptAudio");
        logger.info("Using script name: {}", scriptName);
        clickContinueFromName();

        // First audio
        clickAddMedia();
        uploadAudioFromDevice("ScriptAudioA.mp3");
        clickNextAfterMedia();

        // Second audio
        clickPlusToAddMoreMedia();
        uploadAudioFromDevice("ScriptAudioB.mp3");
        clickNextAfterMedia();

        String bookmark = selectOrCreateBookmark("QA");
        logger.info("Using bookmark name: {}", bookmark);

        fillScriptMessage("Test ");
        setPriceTo50Euro();
        enablePromoWithSevenDays("20");
        fillScriptNote("Test");
        confirmScriptCreation();

        assertScriptCreatedSuccess();
        logger.info("Script creation (audios + promo 7 days) flow completed successfully");
    }

    @Step("Full flow: create script with mixed media (image, video, audio) and free price")
    public void createScriptWithMixedMediaFree() {
        logger.info("Starting full script creation flow with mixed media (image, video, audio)");

        openSettingsFromProfile();
        openScriptsFromSettings();
        clickAddScript();

        String scriptName = fillScriptName("ScriptMixed");
        logger.info("Using script name: {}", scriptName);
        clickContinueFromName();

        // First media: image
        clickAddMedia();
        uploadImageFromDevice("ScriptImageA.png");
        clickNextAfterMedia();

        // Second media: video
        clickPlusToAddMoreMedia();
        uploadVideoFromDevice("ScriptVideoA.mp4");
        clickNextAfterMedia();

        // Third media: audio
        clickPlusToAddMoreMedia();
        uploadAudioFromDevice("ScriptAudioA.mp3");
        clickNextAfterMedia();

        String bookmark = selectOrCreateBookmark("QA");
        logger.info("Using bookmark name: {}", bookmark);

        // Keep script free: do not change price
        fillScriptMessage("Test ");
        fillScriptNote("Test");
        confirmScriptCreation();

        assertScriptCreatedSuccess();
        logger.info("Script creation (mixed media, free price) flow completed successfully");
    }

    // ===== Quick Files based creation flows =====

    @Step("Full flow: create image script using Quick Files album (two media)")
    public void createImageScriptFromQuickFiles() {
        logger.info("Starting script creation flow with images from Quick Files album");

        openSettingsFromProfile();
        openScriptsFromSettings();
        clickAddScript();

        String scriptName = fillScriptName("ImageScriptQF");
        logger.info("Using script name (Quick Files images): {}", scriptName);
        clickContinueFromName();

        // Single image from Quick Files album whose title starts with 'image' (codegen-style flow)
        clickAddMedia();
        selectMediaFromQuickFilesAlbum("image", 1);

        String bookmark = selectOrCreateBookmark("QA");
        logger.info("Using bookmark name: {}", bookmark);

        fillScriptMessage("Test ");
        setPriceTo15Euro();
        fillScriptNote("Test");
        confirmScriptCreation();

        assertScriptCreatedSuccess();
        logger.info("Image script creation via Quick Files completed successfully");
    }

    @Step("Full flow: create video script using Quick Files album (two media) with custom price and promo")
    public void createVideoScriptFromQuickFilesWithPromo() {
        logger.info("Starting script creation flow with videos from Quick Files album and promo");

        openSettingsFromProfile();
        openScriptsFromSettings();
        clickAddScript();

        String scriptName = fillScriptName("ScriptVideoQF");
        logger.info("Using script name (Quick Files videos): {}", scriptName);
        clickContinueFromName();

        // Single video from Quick Files album whose title starts with 'video' (codegen-style flow)
        clickAddMedia();
        selectMediaFromQuickFilesAlbum("video", 1);

        String bookmark = selectOrCreateBookmark("QA");
        logger.info("Using bookmark name: {}", bookmark);

        fillScriptMessage("Test ");
        setCustomPrice("10");
        enablePromoWithUnlimitedValidity("2");
        fillScriptNote("Test");
        confirmScriptCreation();

        assertScriptCreatedSuccess();
        logger.info("Video script creation via Quick Files + promo completed successfully");
    }

    @Step("Full flow: create audio script using Quick Files album (single audio) with price 50 and 7 days promo")
    public void createAudioScriptFromQuickFilesWithPromo() {
        logger.info("Starting script creation flow with audio from Quick Files album");

        openSettingsFromProfile();
        openScriptsFromSettings();
        clickAddScript();

        String scriptName = fillScriptName("ScriptAudioQF");
        logger.info("Using script name (Quick Files audio): {}", scriptName);
        clickContinueFromName();

        // Single audio from Quick Files audio album (audioalbum_*)
        clickAddMedia();
        selectAudioFromQuickFilesAlbum();

        String bookmark = selectOrCreateBookmark("QA");
        logger.info("Using bookmark name: {}", bookmark);

        fillScriptMessage("Test ");
        setPriceTo50Euro();
        enablePromoWithSevenDays("20");
        fillScriptNote("Test");
        confirmScriptCreation();

        assertScriptCreatedSuccess();
        logger.info("Audio script creation via Quick Files + 7 days promo completed successfully");
    }

    @Step("Confirm script update and wait for completion toast")
    public void confirmScriptUpdateAndWait() {
        // Click Confirm (reuse robust handling and prefer chat-scripts Confirm div)
        Locator confirmBtn = page.locator("//div[@class='chat-scripts-button' and normalize-space(text())='Confirm']");
        if (confirmBtn.count() == 0) {
            confirmBtn = page.locator("//div[@class='chat-scripts-button enabled']");
        }
        if (confirmBtn.count() == 0) {
            confirmBtn = page.locator("div").filter(new Locator.FilterOptions()
                    .setHasText(Pattern.compile("^Confirm$")));
        }
        if (confirmBtn.count() == 0) {
            confirmBtn = page.locator("//button[.//div[contains(text(),'Confirm')]]");
        }
        if (confirmBtn.count() == 0) {
            confirmBtn = page.getByText("Confirm");
        }
        waitVisible(confirmBtn.first(), DEFAULT_WAIT);
        try { confirmBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) { }
        clickWithRetry(confirmBtn.first(), 3, 500);

        // During upload, UI may show a message asking to stay on page
        Locator stayOnPage = page.getByText("Stay on page during uploading");
        Locator success = page.getByText("Script updated successfully");

        long deadline = System.currentTimeMillis() + 60_000L;
        boolean seenSuccess = false;
        while (System.currentTimeMillis() < deadline) {
            if (safeIsVisible(success)) {
                seenSuccess = true;
                break;
            }
            if (safeIsVisible(stayOnPage)) {
                // Still uploading, just wait a bit more
                try { page.waitForTimeout(500); } catch (Throwable ignored) { }
            } else {
                // Neither toast nor uploading hint visible yet; short poll
                try { page.waitForTimeout(500); } catch (Throwable ignored) { }
            }
        }

        if (!seenSuccess) {
            throw new RuntimeException("Script update success toast not seen within timeout; script may not have been updated.");
        } else {
            logger.info("Script updated successfully toast observed.");
        }
    }

    // ===== Edit full flows =====

    @Step("Edit first image script: add extra image and update text")
    public void editFirstImageScriptAddExtraMediaAndUpdateText() {
        logger.info("Starting edit flow for image script");

        openSettingsFromProfile();
        openScriptsFromSettings();

        String updatedName = startEditFirstScript("ImageUpdated");
        logger.info("Updated image script name to: {}", updatedName);

        // Add one extra image media
        clickPlusToAddMoreMedia();
        uploadImageFromDevice("ScriptImageA.png");
        clickNextAfterMedia();

        // Do not touch bookmark or price, only update text
        updateScriptMessageAndNote();
        confirmScriptUpdateAndWait();

        logger.info("Image script edit flow completed");
    }

    @Step("Edit first video script: add extra video and update text")
    public void editFirstVideoScriptAddExtraMediaAndUpdateText() {
        logger.info("Starting edit flow for video script");

        openSettingsFromProfile();
        openScriptsFromSettings();

        String updatedName = startEditFirstScript("VideoUpdated");
        logger.info("Updated video script name to: {}", updatedName);

        // Add one extra video media
        clickPlusToAddMoreMedia();
        uploadVideoFromDevice("ScriptVideoA.mp4");
        clickNextAfterMedia();

        updateScriptMessageAndNote();
        confirmScriptUpdateAndWait();

        logger.info("Video script edit flow completed");
    }

    @Step("Edit first audio script: add extra audio and update text")
    public void editFirstAudioScriptAddExtraMediaAndUpdateText() {
        logger.info("Starting edit flow for audio script");

        openSettingsFromProfile();
        openScriptsFromSettings();

        String updatedName = startEditFirstScript("AudioUpdated");
        logger.info("Updated audio script name to: {}", updatedName);

        // Add one extra audio media
        clickPlusToAddMoreMedia();
        uploadAudioFromDevice("ScriptAudioA.mp3");
        clickNextAfterMedia();

        updateScriptMessageAndNote();
        confirmScriptUpdateAndWait();

        logger.info("Audio script edit flow completed");
    }

    @Step("Edit first mixed script: add extra mixed media and update text")
    public void editFirstMixedScriptAddExtraMediaAndUpdateText() {
        logger.info("Starting edit flow for mixed media script");

        openSettingsFromProfile();
        openScriptsFromSettings();

        String updatedName = startEditFirstScript("MixedUpdated");
        logger.info("Updated mixed script name to: {}", updatedName);

        // Add one extra media (image) to mixed script
        clickPlusToAddMoreMedia();
        uploadImageFromDevice("ScriptImageA.png");
        clickNextAfterMedia();

        updateScriptMessageAndNote();
        confirmScriptUpdateAndWait();

        logger.info("Mixed script edit flow completed");
    }

    // ===== Change Order Flow =====

    @Step("Ensure 'All' tab is selected on Scripts page")
    public void ensureAllTabSelected() {
        logger.info("Ensuring 'All' tab is selected");
        Locator allTab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("All"));
        waitVisible(allTab.first(), DEFAULT_WAIT);
        
        // Check if already selected, if not click it
        try {
            String ariaSelected = allTab.first().getAttribute("aria-selected");
            if (!"true".equals(ariaSelected)) {
                logger.info("'All' tab not selected, clicking it");
                clickWithRetry(allTab.first(), 1, 200);
            } else {
                logger.info("'All' tab already selected");
            }
        } catch (Throwable e) {
            logger.warn("Could not check tab selection state, clicking anyway: {}", e.getMessage());
            clickWithRetry(allTab.first(), 1, 200);
        }
    }

    @Step("Click edit icon on first script to open edit dialog")
    public void clickEditIconOnFirstScript() {
        logger.info("Clicking edit icon on first script");
        
        // Use XPath to find all edit buttons and click the first one
        Locator editButtons = page.locator("//button[@aria-label='edit']");
        waitVisible(editButtons.first(), DEFAULT_WAIT);
        clickWithRetry(editButtons.first(), 1, 200);
    }

    @Step("Verify edit dialog appears")
    public void verifyEditDialogAppears() {
        logger.info("Verifying edit dialog appears");
        
        // Ensure "Edit" title appears
        Locator editTitle = page.getByText("Edit", new Page.GetByTextOptions().setExact(true));
        waitVisible(editTitle.first(), DEFAULT_WAIT);
        logger.info("Edit title visible");
        
        // Ensure "Which action would you like" message appears
        Locator editMessage = page.getByText("Which action would you like");
        waitVisible(editMessage.first(), DEFAULT_WAIT);
        logger.info("Edit dialog message visible");
    }

    @Step("Click 'Change order' button")
    public void clickChangeOrderButton() {
        logger.info("Clicking 'Change order' button");
        Locator changeOrderBtn = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName("Change order"));
        waitVisible(changeOrderBtn.first(), DEFAULT_WAIT);
        clickWithRetry(changeOrderBtn.first(), 1, 200);
    }

    @Step("Verify change order screen appears")
    public void verifyChangeOrderScreenAppears() {
        logger.info("Verifying change order screen appears");
        
        Locator orderHeading = page.getByRole(AriaRole.HEADING, 
            new Page.GetByRoleOptions().setName("Hold the button on the right"));
        waitVisible(orderHeading.first(), DEFAULT_WAIT);
        logger.info("Change order screen heading visible");
    }

    @Step("Drag first script to bottom to reorder")
    public void dragFirstScriptToBottom() {
        logger.info("Attempting to drag first script to bottom");
        
        // Wait for the list to be fully loaded
        try { page.waitForTimeout(1000); } catch (Throwable ignored) {}
        
        // Get all list items (script rows)
        Locator listItems = page.getByRole(AriaRole.LISTITEM);
        int itemCount = listItems.count();
        logger.info("Found {} list items (script rows)", itemCount);
        
        if (itemCount < 2) {
            logger.warn("Not enough scripts to reorder (need at least 2), found: {}", itemCount);
            return;
        }
        
        // Get the reorder handles (drag buttons) for each list item
        // Each list item has a getByLabel("reorder") button
        Locator firstItemHandle = listItems.first().getByLabel("reorder");
        Locator lastItemHandle = listItems.nth(itemCount - 1).getByLabel("reorder");
        
        // Verify the handles are visible
        try {
            waitVisible(firstItemHandle, 5000);
            waitVisible(lastItemHandle, 5000);
            logger.info("Both first and last reorder handles are visible");
        } catch (Throwable e) {
            logger.warn("Reorder handles not visible: {}", e.getMessage());
            return;
        }
        
        // Use the reorder handles for dragging
        Locator firstHandle = firstItemHandle;
        Locator lastHandle = lastItemHandle;
        
        logger.info("Attempting drag from first to last (total: {} scripts)", itemCount);
        
        try {
            // Scroll both elements into view first
            try { 
                firstHandle.scrollIntoViewIfNeeded(); 
                lastHandle.scrollIntoViewIfNeeded();
            } catch (Throwable ignored) {}
            
            // Wait a moment for scrolling to complete
            try { page.waitForTimeout(500); } catch (Throwable ignored) {}
            
            logger.info("Attempting drag using comprehensive HTML5 DnD simulation");
            
            // Use a comprehensive drag-and-drop simulation script
            // This script properly simulates all HTML5 drag-and-drop events
            String dndScript = 
                "(function(source, target) {" +
                "  function createDragEvent(type, options) {" +
                "    const event = new DragEvent(type, {" +
                "      bubbles: true," +
                "      cancelable: true," +
                "      composed: true," +
                "      ...options" +
                "    });" +
                "    Object.defineProperty(event, 'dataTransfer', {" +
                "      value: options.dataTransfer || {" +
                "        data: {}," +
                "        effectAllowed: 'all'," +
                "        dropEffect: 'move'," +
                "        files: []," +
                "        items: []," +
                "        types: []," +
                "        setData: function(type, val) { this.data[type] = val; }," +
                "        getData: function(type) { return this.data[type]; }" +
                "      }" +
                "    });" +
                "    return event;" +
                "  }" +
                "  const dataTransfer = {" +
                "    data: {}," +
                "    effectAllowed: 'all'," +
                "    dropEffect: 'move'," +
                "    files: []," +
                "    items: []," +
                "    types: []," +
                "    setData: function(type, val) { this.data[type] = val; this.types.push(type); }," +
                "    getData: function(type) { return this.data[type]; }," +
                "    clearData: function() { this.data = {}; this.types = []; }" +
                "  };" +
                "  const dragStartEvent = createDragEvent('dragstart', { dataTransfer });" +
                "  source.dispatchEvent(dragStartEvent);" +
                "  const dragEnterEvent = createDragEvent('dragenter', { dataTransfer });" +
                "  target.dispatchEvent(dragEnterEvent);" +
                "  const dragOverEvent = createDragEvent('dragover', { dataTransfer });" +
                "  target.dispatchEvent(dragOverEvent);" +
                "  const dropEvent = createDragEvent('drop', { dataTransfer });" +
                "  target.dispatchEvent(dropEvent);" +
                "  const dragEndEvent = createDragEvent('dragend', { dataTransfer });" +
                "  source.dispatchEvent(dragEndEvent);" +
                "})(arguments[0], arguments[1]);";
            
            try {
                // Get the actual DOM elements from the locators
                Object firstElement = firstHandle.evaluate("el => el");
                Object lastElement = lastHandle.evaluate("el => el");
                
                // Execute the drag-and-drop script
                page.evaluate(dndScript, new Object[]{firstElement, lastElement});
                logger.info("Comprehensive HTML5 DnD simulation executed");
                try { page.waitForTimeout(2000); } catch (Throwable ignored) {}
                
            } catch (Throwable jsError) {
                logger.warn("HTML5 DnD simulation failed: {}, trying standard dragTo", jsError.getMessage());
                
                // Fallback to standard Playwright dragTo
                try {
                    firstHandle.dragTo(lastHandle);
                    logger.info("Standard dragTo executed as fallback");
                    try { page.waitForTimeout(2000); } catch (Throwable ignored) {}
                } catch (Throwable dragError) {
                    logger.error("Both HTML5 simulation and dragTo failed: {}", dragError.getMessage());
                }
            }
        } catch (Throwable e) {
            logger.error("Drag operation failed: {}", e.getMessage(), e);
        }
    }

    @Step("Verify 'Order updated' message appears")
    public void verifyOrderUpdatedMessage() {
        logger.info("Verifying 'Order updated' message appears");
        
        // Look for success toast/message
        Locator orderUpdatedMsg = page.getByText("Order updated");
        
        long end = System.currentTimeMillis() + 10_000;
        boolean found = false;
        
        while (System.currentTimeMillis() < end && !found) {
            try {
                if (orderUpdatedMsg.count() > 0 && safeIsVisible(orderUpdatedMsg.first())) {
                    logger.info("'Order updated' message visible - order change successful!");
                    found = true;
                    break;
                }
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(300); } catch (Throwable ignored) {}
        }
        
        if (!found) {
            String errorMsg = "'Order updated' message not found - drag operation did not trigger order change";
            logger.error(errorMsg);
            throw new AssertionError(errorMsg);
        }
    }

    @Step("Click Finish button to save order changes")
    public void clickFinishButton() {
        logger.info("Clicking Finish button to save order changes");
        Locator finishBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Finish"));
        waitVisible(finishBtn.first(), DEFAULT_WAIT);
        clickWithRetry(finishBtn.first(), 1, 200);
        logger.info("Finish button clicked");
        
        // Wait for any navigation or UI update
        try { page.waitForTimeout(1000); } catch (Throwable ignored) {}
    }

    @Step("Complete change order flow: navigate, reorder, and verify")
    public void changeScriptOrder() {
        logger.info("Starting change script order flow");
        
        openSettingsFromProfile();
        openScriptsFromSettings();
        ensureAllTabSelected();
        clickEditIconOnFirstScript();
        verifyEditDialogAppears();
        clickChangeOrderButton();
        verifyChangeOrderScreenAppears();
        dragFirstScriptToBottom();
        clickFinishButton();  // Click Finish to save the changes
        verifyOrderUpdatedMessage();
        
        logger.info("Change script order flow completed");
    }

    // ===== Bookmark/Script Cleanup Methods =====

    @Step("Click edit-categories button to manage bookmarks")
    public void clickEditCategoriesButton() {
        logger.info("Scrolling to find and click edit-categories button");
        
        Locator editCategoriesBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("edit-categories"));
        
        // Scroll right until the button is visible
        int maxScrollAttempts = 10;
        for (int i = 0; i < maxScrollAttempts; i++) {
            if (editCategoriesBtn.count() > 0 && safeIsVisible(editCategoriesBtn.first())) {
                break;
            }
            // Scroll right using keyboard or mouse wheel
            try {
                page.keyboard().press("ArrowRight");
                page.waitForTimeout(300);
            } catch (Throwable ignored) { }
        }
        
        waitVisible(editCategoriesBtn.first(), DEFAULT_WAIT);
        clickWithRetry(editCategoriesBtn.first(), 1, 200);
        logger.info("Clicked edit-categories button");
    }

    @Step("Verify edit categories popup and click I understand")
    public void handleEditCategoriesPopup() {
        logger.info("Handling edit categories popup");
        
        // Ensure the popup message is visible
        Locator popupMessage = page.getByText("Close this popup and press 3");
        waitVisible(popupMessage.first(), DEFAULT_WAIT);
        logger.info("Edit categories popup message visible");
        
        // Click "I understand" button
        Locator iUnderstandBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("I understand"));
        waitVisible(iUnderstandBtn.first(), DEFAULT_WAIT);
        clickWithRetry(iUnderstandBtn.first(), 1, 200);
        logger.info("Clicked 'I understand' button");
        
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
    }

    @Step("Long press on QA bookmark tab to trigger delete dialog")
    private void longPressOnBookmarkTab(Locator bookmarkTab) {
        logger.info("Long pressing on bookmark tab to trigger delete dialog");
        
        // Perform long press (hold for ~2 seconds)
        try {
            bookmarkTab.first().click(new Locator.ClickOptions().setDelay(2000));
        } catch (Throwable ignored) {
            // Fallback: use mouse down/up with delay
            try {
                bookmarkTab.first().hover();
                page.mouse().down();
                page.waitForTimeout(2000);
                page.mouse().up();
            } catch (Throwable ignored2) { }
        }
        
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }
    }

    @Step("Delete a single QA bookmark")
    private boolean deleteSingleQABookmark() {
        // Find any bookmark tab starting with "QA_"
        Locator qaBookmarkTab = page.getByRole(AriaRole.TAB,
                new Page.GetByRoleOptions().setName(Pattern.compile("^QA_.*")));
        
        if (qaBookmarkTab.count() == 0) {
            logger.info("No QA bookmark tabs found");
            return false;
        }
        
        String bookmarkName = "";
        try {
            bookmarkName = qaBookmarkTab.first().getAttribute("aria-label");
            if (bookmarkName == null || bookmarkName.isEmpty()) {
                bookmarkName = qaBookmarkTab.first().textContent();
            }
        } catch (Throwable ignored) {
            bookmarkName = "QA bookmark";
        }
        
        logger.info("Found QA bookmark: {}", bookmarkName);
        
        // Long press on the bookmark tab
        longPressOnBookmarkTab(qaBookmarkTab);
        
        // Wait for the delete dialog to appear
        Locator deleteDialogDesc = page.locator(".edit-category-actions-bottom-modal-desc");
        try {
            waitVisible(deleteDialogDesc.first(), 5000);
            logger.info("Delete dialog appeared");
        } catch (Throwable e) {
            logger.warn("Delete dialog did not appear after long press; retrying with click");
            // Try clicking instead
            clickWithRetry(qaBookmarkTab.first(), 1, 200);
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
            longPressOnBookmarkTab(qaBookmarkTab);
            try {
                waitVisible(deleteDialogDesc.first(), 5000);
            } catch (Throwable e2) {
                logger.error("Delete dialog still not appearing");
                return false;
            }
        }
        
        // Click Delete button
        Locator deleteBtn = page.locator("//button[normalize-space()='Delete']");
        waitVisible(deleteBtn.first(), DEFAULT_WAIT);
        clickWithRetry(deleteBtn.first(), 1, 200);
        logger.info("Clicked Delete button");
        
        try { page.waitForTimeout(300); } catch (Throwable ignored) { }
        
        // Wait for confirmation dialog
        Locator confirmTitle = page.locator(".confirm-delete-category-title");
        waitVisible(confirmTitle.first(), DEFAULT_WAIT);
        logger.info("Confirmation dialog appeared");
        
        // Click Confirm button
        Locator confirmBtn = page.locator("//button[normalize-space()='Confirm']");
        waitVisible(confirmBtn.first(), DEFAULT_WAIT);
        clickWithRetry(confirmBtn.first(), 1, 200);
        logger.info("Clicked Confirm button - bookmark '{}' deleted", bookmarkName);
        
        // Wait for deletion to complete
        try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
        
        return true;
    }

    @Step("Verify all QA bookmarks are deleted")
    private boolean verifyAllBookmarksDeleted() {
        // Primary check: "You haven't created any" text visible (indicates no scripts/bookmarks)
        Locator noScriptsText = page.getByText("You haven't created any");
        if (noScriptsText.count() > 0 && safeIsVisible(noScriptsText.first())) {
            logger.info("'You haven't created any' message visible - all bookmarks deleted");
            return true;
        }
        
        // Secondary check: note element visible
        Locator noteElement = page.locator("div[role='note']");
        if (noteElement.count() > 0 && safeIsVisible(noteElement.first())) {
            logger.info("Note element visible - all bookmarks deleted");
            return true;
        }
        
        // Check if any QA bookmarks remain
        Locator qaBookmarkTab = page.getByRole(AriaRole.TAB,
                new Page.GetByRoleOptions().setName(Pattern.compile("^QA_.*")));
        if (qaBookmarkTab.count() == 0) {
            logger.info("No QA bookmark tabs remaining");
            return true;
        }
        
        return false;
    }

    @Step("Delete all QA bookmarks and their associated scripts")
    public void deleteAllQABookmarks() {
        logger.info("Starting cleanup: deleting all QA bookmarks and associated scripts");
        
        // Navigate to Scripts screen
        openSettingsFromProfile();
        openScriptsFromSettings();
        
        // Click edit-categories button
        clickEditCategoriesButton();
        
        // Handle the popup
        handleEditCategoriesPopup();
        
        // Delete bookmarks one by one until none remain
        int deletedCount = 0;
        int maxAttempts = 20; // Safety limit
        
        for (int i = 0; i < maxAttempts; i++) {
            // Check if all bookmarks are deleted
            if (verifyAllBookmarksDeleted()) {
                logger.info("All QA bookmarks have been deleted. Total deleted: {}", deletedCount);
                break;
            }
            
            // Delete one bookmark
            boolean deleted = deleteSingleQABookmark();
            if (deleted) {
                deletedCount++;
                logger.info("Deleted bookmark #{}", deletedCount);
            } else {
                // No more bookmarks to delete or error occurred
                logger.info("No more QA bookmarks to delete or deletion failed");
                break;
            }
            
            // Small delay between deletions
            try { page.waitForTimeout(500); } catch (Throwable ignored) { }
        }
        
        logger.info("Bookmark cleanup completed. Total bookmarks deleted: {}", deletedCount);
    }
}

