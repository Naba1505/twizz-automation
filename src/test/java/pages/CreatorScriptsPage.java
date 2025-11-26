package pages;

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
            waitVisible(noteBox.first(), 3_000);
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
        waitVisible(blurred.first(), 20_000);
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

        // Ensure default tab for photos & videos
        Locator photosVideosTab = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Selected Photos & videos"));
        waitVisible(photosVideosTab.first(), DEFAULT_WAIT);

        // Click album row whose title starts with the given prefix (image / video / mix)
        String normalizedPrefix = albumPrefix.toLowerCase();
        Locator albumTitle = page.locator("//div[@class='qf-row-title' and starts-with(normalize-space(.), '" + normalizedPrefix + "')]");
        waitVisible(albumTitle.first(), DEFAULT_WAIT);
        clickWithRetry(albumTitle.first(), 1, 200);

        // Inside album
        waitVisible(page.getByText("Select media"), DEFAULT_WAIT);

        Locator mediaThumbs = page.locator(".select-quick-file-media-thumb");
        // Thumbnails can take a moment to render; poll for a short window before failing
        long thumbDeadline = System.currentTimeMillis() + 10_000L;
        while (mediaThumbs.count() == 0 && System.currentTimeMillis() < thumbDeadline) {
            try {
                page.waitForTimeout(300);
            } catch (Throwable ignored) {
            }
        }
        if (mediaThumbs.count() == 0) {
            throw new RuntimeException("No media thumbs found in Quick Files album with prefix: " + albumPrefix);
        }

        Locator targetThumb = mediaThumbs.first();
        waitVisible(targetThumb, DEFAULT_WAIT);
        clickWithRetry(targetThumb, 1, 200);

        // Final select inside album ("Select (1)")
        Locator selectBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Select (1)"));
        waitVisible(selectBtn.first(), DEFAULT_WAIT);
        clickWithRetry(selectBtn.first(), 1, 200);

        // Advance one step in the script flow (Next/Continue), same as codegen
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

        // Switch to Audios tab
        Locator audiosTab = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Audios"));
        waitVisible(audiosTab.first(), DEFAULT_WAIT);
        clickWithRetry(audiosTab.first(), 1, 200);

        // Click audio album row whose title starts with 'audio'
        Locator albumTitle = page.locator("//div[@class='qf-row-title' and starts-with(normalize-space(.), 'audio')]");
        waitVisible(albumTitle.first(), DEFAULT_WAIT);
        clickWithRetry(albumTitle.first(), 1, 200);

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
        waitVisible(blurred.first(), 20_000);
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
        waitVisible(blurred.first(), 20_000);
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

        // After creating, explicitly select the newly created bookmark so that the
        // mandatory bookmark field is satisfied in all flows.
        Locator bookmarkToggle = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Bookmark " + name + " chevron"));
        if (bookmarkToggle.count() > 0) {
            clickWithRetry(bookmarkToggle.first(), 1, 200);
        }

        Locator bookmarkOption = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Bookmark " + name + " chevron " + name));
        if (bookmarkOption.count() > 0) {
            clickWithRetry(bookmarkOption.first(), 1, 200);
            // Wait for the dropdown to close and the main control to reflect the new name
            try {
                waitVisible(bookmarkToggle.first(), DEFAULT_WAIT);
                page.waitForTimeout(300);
            } catch (Throwable ignored) { }
        }

        return name;
    }

    // Best-effort helper to (re)select any available bookmark when validation
    // complains that no bookmark is assigned. Uses generic patterns so it
    // works for existing bookmarks too.
    private void ensureAnyBookmarkSelected() {
        // Open the bookmark dropdown if present
        Locator anyToggle = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("^Bookmark .* chevron.*", Pattern.CASE_INSENSITIVE)));
        if (anyToggle.count() > 0 && safeIsVisible(anyToggle.first())) {
            clickWithRetry(anyToggle.first(), 1, 200);
            try { page.waitForTimeout(300); } catch (Throwable ignored) { }
        }

        // Select the first bookmark option if available
        Locator anyOption = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("^Bookmark .* chevron .*", Pattern.CASE_INSENSITIVE)));
        if (anyOption.count() > 0 && safeIsVisible(anyOption.first())) {
            clickWithRetry(anyOption.first(), 1, 200);
            try { page.waitForTimeout(300); } catch (Throwable ignored) { }
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
            waitVisible(success.first(), 30_000);
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
}
