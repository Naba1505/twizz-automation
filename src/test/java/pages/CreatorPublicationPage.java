package pages;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreatorPublicationPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(CreatorPublicationPage.class);

    // UI strings (some localized)
    private static final String CONVERSION_TOOLS_TEXT = "Vos meilleurs outils de conversion";
    private static final String I_UNDERSTAND_BTN = "I understand";
    private static final String POST_TEXT = "Post";
    private static final String PUBLICATIONS_TEXT = "Publications";
    private static final String PUBLISH_BTN = "Publish";
    private static final String CAPTION_PLACEHOLDER = "Write a caption and tag other creators...";
    private static final String CAPTION_TITLE_TEXT = "Caption";
    private static final String CAPTION_OK_TEXT = "OK";

    public CreatorPublicationPage(Page page) {
        super(page);
    }

    public void openPlusMenu() {
        // Click plus icon at top
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg, 15000);
        clickWithRetry(plusImg, 2, 300);
        logger.info("Opened plus menu");

        // Dismiss conversion tools prompt if present
        handleConversionPromptIfPresent();
    }

    public void selectPostEntry() {
        // Some users see prompt before post, handle again just in case
        handleConversionPromptIfPresent();
        // Click on Post entry
        Locator post = page.getByText(POST_TEXT, new Page.GetByTextOptions().setExact(true));
        waitVisible(post, 15000);
        clickWithRetry(post, 2, 300);
        logger.info("Clicked Post entry");

        // If the understand button appears again, scroll and click
        clickIUnderstandIfPresent();
    }

    public void ensurePublicationsScreen() {
        Locator pubs = page.getByText(PUBLICATIONS_TEXT);
        waitVisible(pubs, 20000);
        logger.info("Publications screen visible");
    }

    public void uploadMedia(Path mediaPath) {
        if (!Files.exists(mediaPath)) {
            throw new RuntimeException("Media file not found: " + mediaPath.toAbsolutePath());
        }
        // Try direct input first
        Locator input = page.locator("input[type='file']");
        if (input.count() > 0) {
            input.first().setInputFiles(mediaPath);
            logger.info("Uploaded media via input[type=file]: {}", mediaPath);
            return;
        }
        // Otherwise, click plus button and use FileChooser
        Locator plusButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusButton, 15000);
        FileChooser chooser = page.waitForFileChooser(() -> plusButton.click());
        chooser.setFiles(mediaPath);
        logger.info("Uploaded media via FileChooser: {}", mediaPath);
    }

    public void openCaptionEditor() {
        Locator openCaption = page.getByText(CAPTION_PLACEHOLDER);
        waitVisible(openCaption, 15000);
        clickWithRetry(openCaption, 2, 200);
        logger.info("Opened caption editor");
    }

    public void ensureCaptionPopupVisible() {
        Locator captionTitle = page.getByText(CAPTION_TITLE_TEXT, new Page.GetByTextOptions().setExact(true));
        waitVisible(captionTitle, 20000);
        logger.info("Caption popup is visible");
    }

    public void fillCaption(String caption) {
        page.getByPlaceholder(CAPTION_PLACEHOLDER).fill(caption);
        logger.info("Filled caption");
    }

    public void clickCaptionOk() {
        Locator ok = page.getByText(CAPTION_OK_TEXT, new Page.GetByTextOptions().setExact(true));
        waitVisible(ok, 10000);
        clickWithRetry(ok, 2, 200);
        logger.info("Clicked CaptionOK");
    }

    public void ensureBlurSwitchEnabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH);
        waitVisible(sw, 10000);
        String ariaChecked = sw.getAttribute("aria-checked");
        if (!"true".equalsIgnoreCase(ariaChecked)) {
            logger.warn("Blur switch not enabled by default. Enabling now.");
            clickWithRetry(sw, 2, 200);
        } else {
            logger.info("Blur switch is enabled by default");
        }
    }

    private boolean getBlurSwitchState() {
        Locator sw = page.getByRole(AriaRole.SWITCH);
        waitVisible(sw, 10000);
        String ariaChecked = sw.getAttribute("aria-checked");
        return "true".equalsIgnoreCase(ariaChecked);
    }

    public void setBlurEnabled(boolean enabled) {
        Locator sw = page.getByRole(AriaRole.SWITCH);
        waitVisible(sw, 10000);
        boolean current = getBlurSwitchState();
        if (current != enabled) {
            logger.info("Toggling blur switch to {}", enabled);
            clickWithRetry(sw, 2, 200);
        } else {
            logger.info("Blur switch already {}", enabled ? "enabled" : "disabled");
        }
    }

    public void publish() {
        Locator publishBtn = getPublishButton();
        // Ensure button is in view and enabled before clicking
        try {
            publishBtn.scrollIntoViewIfNeeded();
        } catch (Exception ignored) {
        }
        waitForPublishEnabled(publishBtn, 60000);
        clickWithRetry(publishBtn, 2, 300);
        // Do not wait for page load state here; SPA may not trigger it. Success toast wait happens later.
        logger.info("Clicked Publish");
    }

    public boolean isSuccessToastVisible() {
        String primary = ConfigReader.getProperty("publication.success.text", "Feed is created successfully");
        String alts = ConfigReader.getProperty("publication.success.alt", ""); // pipe-separated alternatives
        // Try primary exact match first
        if (isTextVisible(primary, true)) return true;
        // Try case-insensitive contains for robustness
        if (isTextVisible(primary, false)) return true;
        // Try alternatives
        if (!alts.isEmpty()) {
            for (String alt : alts.split("\\|")) {
                String t = alt.trim();
                if (t.isEmpty()) continue;
                if (isTextVisible(t, true) || isTextVisible(t, false)) return true;
            }
        }
        return false;
    }

    private boolean isTextVisible(String text, boolean exact) {
        try {
            Locator toast = exact ? page.getByText(text, new Page.GetByTextOptions().setExact(true)) : page.getByText(text);
            waitVisible(toast, 5000);
            return toast.isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean waitForSuccessToast(long timeoutMs) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (isSuccessToastVisible()) return true;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        return false;
    }

    private Locator getPublishButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(PUBLISH_BTN).setExact(true));
    }

    private void waitForPublishEnabled(Locator btn, long timeoutMs) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                String disabled = btn.getAttribute("disabled");
                String ariaDisabled = btn.getAttribute("aria-disabled");
                if ((disabled == null || disabled.isEmpty()) && (ariaDisabled == null || !"true".equalsIgnoreCase(ariaDisabled))) {
                    return;
                }
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
        }
        logger.warn("Publish button did not become enabled within {} ms", timeoutMs);
    }

    private void handleConversionPromptIfPresent() {
        if (page.getByText(CONVERSION_TOOLS_TEXT).isVisible()) {
            clickIUnderstandIfPresent();
        }
    }

    private void clickIUnderstandIfPresent() {
        Locator understand = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(I_UNDERSTAND_BTN));
        if (understand.isVisible()) {
            try {
                page.evaluate("window.scrollBy(0, document.body.scrollHeight)");
            } catch (Exception ignored) {
            }
            clickWithRetry(understand, 2, 300);
            logger.info("Clicked 'I understand' button");
        }
    }

    public void completePublicationFlow(Path mediaPath, String caption, boolean blurEnabled) {
        openPlusMenu();
        selectPostEntry();
        ensurePublicationsScreen();
        uploadMedia(mediaPath);
        openCaptionEditor();
        ensureCaptionPopupVisible();
        fillCaption(caption);
        clickCaptionOk();
        setBlurEnabled(blurEnabled);
        publish();
        if (!waitForSuccessToast(90000)) {
            throw new RuntimeException("Publication success toast not visible");
        }
    }
}
