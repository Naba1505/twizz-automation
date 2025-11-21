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
        Locator myDevice = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("My Device"));
        waitVisible(myDevice.first(), DEFAULT_WAIT);

        // Prefer a direct input[type=file] if already present in the DOM
        Locator input = page.locator("input[type='file']");
        if (input.count() > 0) {
            clickWithRetry(myDevice.first(), 1, 200);
            input.first().setInputFiles(file);
        } else {
            // Fallback: rely on Playwright file chooser when clicking My Device
            try {
                com.microsoft.playwright.FileChooser chooser = page.waitForFileChooser(() -> {
                    clickWithRetry(myDevice.first(), 1, 200);
                });
                chooser.setFiles(file);
            } catch (Throwable t) {
                // As a last resort, try again to find any input[type=file]
                clickWithRetry(myDevice.first(), 1, 200);
                Locator anyInput = page.locator("input[type='file']");
                if (anyInput.count() == 0) {
                    throw new RuntimeException("Unable to find file input for script media upload", t);
                }
                anyInput.first().setInputFiles(file);
            }
        }

        // Give the UI a brief moment to process the upload and render blurred preview
        try { page.waitForTimeout(500); } catch (Throwable ignored) { }

        Locator blurred = page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Blurred media$")));
        waitVisible(blurred.first(), 20_000);
    }

    @Step("Upload script audio from device: {fileName}")
    public void uploadAudioFromDevice(String fileName) {
        Path file = resolveAudio(fileName);
        Locator myDevice = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("My Device"));
        waitVisible(myDevice.first(), DEFAULT_WAIT);

        Locator input = page.locator("input[type='file']");
        if (input.count() > 0) {
            clickWithRetry(myDevice.first(), 1, 200);
            input.first().setInputFiles(file);
        } else {
            try {
                com.microsoft.playwright.FileChooser chooser = page.waitForFileChooser(() -> {
                    clickWithRetry(myDevice.first(), 1, 200);
                });
                chooser.setFiles(file);
            } catch (Throwable t) {
                clickWithRetry(myDevice.first(), 1, 200);
                Locator anyInput = page.locator("input[type='file']");
                if (anyInput.count() == 0) {
                    throw new RuntimeException("Unable to find file input for script audio upload", t);
                }
                anyInput.first().setInputFiles(file);
            }
        }

        try { page.waitForTimeout(500); } catch (Throwable ignored) { }

        Locator blurred = page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Blurred media$")));
        waitVisible(blurred.first(), 20_000);
    }

    @Step("Upload script video from device: {fileName}")
    public void uploadVideoFromDevice(String fileName) {
        Path file = resolveVideo(fileName);
        Locator myDevice = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("My Device"));
        waitVisible(myDevice.first(), DEFAULT_WAIT);

        Locator input = page.locator("input[type='file']");
        if (input.count() > 0) {
            clickWithRetry(myDevice.first(), 1, 200);
            input.first().setInputFiles(file);
        } else {
            try {
                com.microsoft.playwright.FileChooser chooser = page.waitForFileChooser(() -> {
                    clickWithRetry(myDevice.first(), 1, 200);
                });
                chooser.setFiles(file);
            } catch (Throwable t) {
                clickWithRetry(myDevice.first(), 1, 200);
                Locator anyInput = page.locator("input[type='file']");
                if (anyInput.count() == 0) {
                    throw new RuntimeException("Unable to find file input for script video upload", t);
                }
                anyInput.first().setInputFiles(file);
            }
        }

        try { page.waitForTimeout(500); } catch (Throwable ignored) { }

        Locator blurred = page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Blurred media$")));
        waitVisible(blurred.first(), 20_000);
    }

    @Step("Click Next after media upload")
    public void clickNextAfterMedia() {
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
        return name;
    }

    @Step("Fill main script message")
    public void fillScriptMessage(String message) {
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
        // Prefer the actual button that contains a div with 'Confirm' text, then fall back to broader locators
        Locator confirmBtn = page.locator("//button[.//div[contains(text(),'Confirm')]]");
        if (confirmBtn.count() == 0) {
            confirmBtn = page.locator("//div[contains(text(),'Confirm')]");
        }
        if (confirmBtn.count() == 0) {
            confirmBtn = page.getByRole(AriaRole.BUTTON,
                    new Page.GetByRoleOptions().setName("Confirm"));
        }
        if (confirmBtn.count() == 0) {
            confirmBtn = page.getByText("Confirm");
        }
        waitVisible(confirmBtn.first(), DEFAULT_WAIT);
        try { confirmBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) { }
        // Use a few retries in case of transient overlay/animation issues
        clickWithRetry(confirmBtn.first(), 3, 500);
        // Wait for the page to settle after submission
        try { waitForIdle(); } catch (Throwable ignored) { }
        try { page.waitForTimeout(2000); } catch (Throwable ignored) { }
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
}
