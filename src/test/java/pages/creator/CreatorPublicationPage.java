package pages.creator;

import pages.common.BasePage;

import java.nio.file.Files;
import java.nio.file.Path;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;

import utils.ConfigReader;
import utils.WaitUtils;
 

public class CreatorPublicationPage extends BasePage {

    // All timeouts now use ConfigReader for consistency

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
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg, ConfigReader.getVisibilityTimeout());
        clickWithRetry(plusImg, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
        handleConversionPromptIfPresent();
        logger.info("Opened plus menu");
    }

    public void selectPostEntry() {
        // Some users see prompt before post, handle again just in case
        handleConversionPromptIfPresent();
        // Click on Post entry - use Playwright auto-wait instead of manual polling
        Locator post = page.getByText(POST_TEXT, new Page.GetByTextOptions().setExact(true));
        waitVisible(post, ConfigReader.getMediumTimeout());
        clickWithRetry(post, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
        logger.info("Clicked Post entry");

        // If the understand button appears again, scroll and click
        clickIUnderstandIfPresent();
    }

    public void ensurePublicationsScreen() {
        Locator pubs = page.getByText(PUBLICATIONS_TEXT);
        waitVisible(pubs, ConfigReader.getVisibilityTimeout());
        logger.info("Publications screen visible");
    }

    public void uploadMedia(Path mediaPath) {
        if (!Files.exists(mediaPath)) {
            throw new RuntimeException("Media file not found: " + mediaPath.toAbsolutePath());
        }
        // Try direct hidden file input first
        Locator input = page.locator("input[type='file']").first();
        try {
            input.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(ConfigReader.getShortTimeout()));
            input.setInputFiles(mediaPath);
            logger.info("Uploaded media via input[type=file]: {}", mediaPath);
            return;
        } catch (Exception e) {
            logger.debug("Direct file input not available, using FileChooser: {}", e.getMessage());
        }
        // Otherwise, click plus button and use FileChooser
        Locator plusButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusButton, ConfigReader.getVisibilityTimeout());
        FileChooser chooser = page.waitForFileChooser(() -> plusButton.click());
        chooser.setFiles(mediaPath);
        logger.info("Uploaded media via FileChooser: {}", mediaPath);
    }

    public void openCaptionEditor() {
        Locator openCaption = page.getByText(CAPTION_PLACEHOLDER);
        waitVisible(openCaption, ConfigReader.getVisibilityTimeout());
        clickWithRetry(openCaption, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
        logger.info("Opened caption editor");
    }

    public void ensureCaptionPopupVisible() {
        Locator captionTitle = page.getByText(CAPTION_TITLE_TEXT, new Page.GetByTextOptions().setExact(true));
        waitVisible(captionTitle, ConfigReader.getVisibilityTimeout());
        logger.info("Caption popup is visible");
    }

    public void fillCaption(String caption) {
        page.getByPlaceholder(CAPTION_PLACEHOLDER).fill(caption);
        logger.info("Filled caption");
    }

    public void clickCaptionOk() {
        Locator ok = page.getByText(CAPTION_OK_TEXT, new Page.GetByTextOptions().setExact(true));
        waitVisible(ok, ConfigReader.getShortTimeout());
        clickWithRetry(ok, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
        logger.info("Clicked CaptionOK");
    }

    public void ensureBlurSwitchEnabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH);
        waitVisible(sw, ConfigReader.getShortTimeout());
        String ariaChecked = sw.getAttribute("aria-checked");
        if (!"true".equalsIgnoreCase(ariaChecked)) {
            logger.warn("Blur switch not enabled by default. Enabling now.");
            clickWithRetry(sw, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
        } else {
            logger.info("Blur switch is enabled by default");
        }
    }

    private boolean getBlurSwitchState() {
        Locator sw = page.getByRole(AriaRole.SWITCH);
        waitVisible(sw, ConfigReader.getShortTimeout());
        String ariaChecked = sw.getAttribute("aria-checked");
        return "true".equalsIgnoreCase(ariaChecked);
    }

    public void setBlurEnabled(boolean enabled) {
        Locator sw = page.getByRole(AriaRole.SWITCH);
        waitVisible(sw, ConfigReader.getShortTimeout());
        boolean current = getBlurSwitchState();
        if (current != enabled) {
            logger.info("Toggling blur switch to {}", enabled);
            clickWithRetry(sw, 2, ConfigReader.getElementRetryDelay());
        } else {
            logger.info("Blur switch already {}", enabled ? "enabled" : "disabled");
        }
    }

    public void publish() {
        Locator publishBtn = getPublishButton();
        // Ensure button is in view and enabled before clicking
        try {
            publishBtn.scrollIntoViewIfNeeded();
        } catch (Exception e) {
            logger.debug("Scroll into view failed: {}", e.getMessage());
        }
        // Use a shorter enable wait with retries
        waitForPublishEnabled(publishBtn, ConfigReader.getVisibilityTimeout());
        clickWithRetry(publishBtn, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
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
        Locator toast = exact
                ? page.getByText(text, new Page.GetByTextOptions().setExact(true))
                : page.getByText(text);
        return safeIsVisible(toast);
    }

    public boolean waitForSuccessToast(long timeoutMs) {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (isSuccessToastVisible()) return true;
            try { 
                page.waitForTimeout(ConfigReader.getAnimationTimeout()); 
            } catch (Exception e) {
                logger.debug("Wait interrupted: {}", e.getMessage());
            }
        }
        return false;
    }

    // Consider composer closed when publish UI is no longer visible
    private boolean isComposerOpen() {
        return safeIsVisible(getPublishButton())
                || safeIsVisible(page.getByText(CAPTION_TITLE_TEXT, new Page.GetByTextOptions().setExact(true)))
                || safeIsVisible(page.getByPlaceholder(CAPTION_PLACEHOLDER));
    }

    

    // Try to close composer if still open (best-effort, non-fatal)
    private void closeComposerIfOpen() {
        if (!isComposerOpen()) return;

        Locator closeBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close"));
        if (safeIsVisible(closeBtn)) {
            clickWithRetry(closeBtn, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
            return;
        }

        Locator cancelBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
        if (safeIsVisible(cancelBtn)) {
            clickWithRetry(cancelBtn, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
            return;
        }

        Locator x = page.locator(".close, button[aria-label='Close'], [data-testid='close']").first();
        if (safeIsVisible(x)) {
            clickWithRetry(x, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
        }
    }

    // Force-exit composer quickly without waiting on background uploads
    private void forceExitComposer() {
        // If already closed, nothing to do
        if (!isComposerOpen()) return;

        // Try ESC key
        try { page.keyboard().press("Escape"); } catch (Exception e) { logger.debug("Escape key failed: {}", e.getMessage()); }
        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Exception e) { logger.debug("Wait failed: {}", e.getMessage()); }
        if (!isComposerOpen()) return;

        // Use existing close attempts
        closeComposerIfOpen();
        if (!isComposerOpen()) return;

        // Click outside the dialog area (top-left corner)
        try { page.mouse().click(5, 5); } catch (Exception e) { logger.debug("Click outside failed: {}", e.getMessage()); }
        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Exception e) { logger.debug("Wait failed: {}", e.getMessage()); }
        if (!isComposerOpen()) return;

        // Quick back navigation as last resort (short timeout)
        try { page.goBack(new Page.GoBackOptions().setTimeout(ConfigReader.getLongTimeout())); } catch (Exception e) { logger.debug("GoBack failed: {}", e.getMessage()); }
    }

    private Locator getPublishButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(PUBLISH_BTN).setExact(true));
    }

    private void waitForPublishEnabled(Locator btn, long timeoutMs) {
        if (!WaitUtils.waitForEnabled(btn, timeoutMs)) {
            logger.warn("Publish button did not become enabled within {} ms", timeoutMs);
        }
    }

    private void handleConversionPromptIfPresent() {
        if (safeIsVisible(page.getByText(CONVERSION_TOOLS_TEXT))) {
            clickIUnderstandIfPresent();
        }
    }

    private void clickIUnderstandIfPresent() {
        Locator understand = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(I_UNDERSTAND_BTN));
        if (safeIsVisible(understand)) {
            try {
                page.evaluate("window.scrollBy(0, document.body.scrollHeight)");
            } catch (Exception e) {
                logger.debug("Scroll failed: {}", e.getMessage());
            }
            clickWithRetry(understand, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
            logger.info("Clicked 'I understand' button");
        }
    }

    // Navigation from profile to Publications
    public void openProfilePublicationsIcon() {
        if (isPublicationsScreen()) {
            logger.info("Already on Publications screen; no need to click icon");
            return;
        }

        Locator icon = page.getByRole(AriaRole.TABPANEL, new Page.GetByRoleOptions().setName("publications icon")).locator("img").first();
        if (safeIsVisible(icon)) {
            clickWithRetry(icon, 2, ConfigReader.getAnimationTimeout());
            logger.info("Clicked publications icon from profile");
            return;
        }

        Locator connectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Connect").setExact(true));
        if (safeIsVisible(connectBtn)) {
            clickWithRetry(connectBtn, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
            if (safeIsVisible(icon)) {
                clickWithRetry(icon, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
                logger.info("Clicked publications icon after Connect");
                return;
            }
        }
        logger.warn("Publications icon not visible and not on Publications screen");
    }

// Verify Publications screen by presence of 'Publications' text
public void verifyPublicationsScreen() {
    Locator publicationsTitle = page.getByText(PUBLICATIONS_TEXT);
    waitVisible(publicationsTitle, ConfigReader.getVisibilityTimeout());
    logger.info("Publications screen visible");
}

private boolean isPublicationsScreen() {
    return safeIsVisible(page.getByText(PUBLICATIONS_TEXT));
}

// Delete a single publication via available UI entry points
public void deleteOnePublication() {
    page.waitForTimeout(ConfigReader.getElementRetryDelay());

    // Some flows require clicking 'Connect' first
    Locator connectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Connect").setExact(true));
    if (safeIsVisible(connectBtn)) {
        clickWithRetry(connectBtn, 2, ConfigReader.getElementRetryDelay());
        logger.info("Clicked 'Connect' before deletion flow");
        page.waitForTimeout(ConfigReader.getElementRetryDelay());
    }

    // Primary path: menu beside post '.dots-wrapper'
    Locator dotsWrapper = page.locator(".dots-wrapper").first();
    if (safeIsVisible(dotsWrapper)) {
        dotsWrapper.scrollIntoViewIfNeeded();
        waitVisible(dotsWrapper, ConfigReader.getLongTimeout());
        clickWithRetry(dotsWrapper, 2, ConfigReader.getElementRetryDelay());
        confirmDeletionPopup();
        return;
    }

    // Alternate menu icon: role IMG name 'dot'
    Locator dotImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("dot")).first();
    if (safeIsVisible(dotImg)) {
        dotImg.scrollIntoViewIfNeeded();
        clickWithRetry(dotImg, 2, ConfigReader.getElementRetryDelay());
        confirmDeletionPopup();
        return;
    }

    // Fallback path: open first post and use action button '.d-flex'
    Locator fanPost = page.locator(".fanProfilePost").first();
    Locator actionBtn = page.locator(".d-flex").first();
    if (safeIsVisible(fanPost)) {
        clickWithRetry(fanPost, 2, ConfigReader.getElementRetryDelay());
        logger.info("Opened first fan profile post");
        waitVisible(actionBtn, ConfigReader.getShortTimeout());
        clickWithRetry(actionBtn, 2, ConfigReader.getElementRetryDelay());
        confirmDeletionPopup();
        return;
    }

    logger.warn("No known delete entry point found for publication");
}

private void confirmDeletionPopup() {
    Locator confirmText = page.getByText("Do you really want to delete a publication?");
    waitVisible(confirmText, ConfigReader.getShortTimeout());
    Locator yesDelete = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, delete"));
    waitVisible(yesDelete, ConfigReader.getShortTimeout());
    clickWithRetry(yesDelete, 2, ConfigReader.getElementRetryDelay());
    logger.info("Confirmed publication deletion");
    try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Exception e) { logger.debug("Post-confirm wait failed: {}", e.getMessage()); }
}

// Loop delete until none remain
    public void deleteAllPublicationsLoop() {
        int deleted = 0;
        while (true) {
            if (!isPublicationsScreen()) {
                openProfilePublicationsIcon();
                if (!isPublicationsScreen()) {
                    logger.warn("Could not navigate to Publications screen; stopping loop");
                    break;
                }
            }

            Locator dotsWrapper = page.locator(".dots-wrapper").first();
            Locator dotImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("dot")).first();
            Locator fanPost = page.locator(".fanProfilePost").first();

            boolean anyPresent = safeIsVisible(dotsWrapper) || safeIsVisible(dotImg) || safeIsVisible(fanPost);
            if (!anyPresent) {
                logger.info("No publications found on Publications screen");
                break;
            }

            logger.info("Found publications on Publications screen");
            deleteOnePublication();
            deleted++;

            try {
                logger.info("Reloading page to refresh Publications list after deletion");
                page.reload(new Page.ReloadOptions()
                        .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                        .setTimeout(ConfigReader.getNavigationTimeout()));
                page.waitForLoadState(LoadState.NETWORKIDLE,
                        new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getShortTimeout()));
                page.waitForTimeout(ConfigReader.getUiSettleTimeout());
            } catch (Exception e) {
                logger.warn("Failed to reload page after deletion: {}", e.getMessage());
            }
        }
        logger.info("Deleted {} publications (loop until none left)", deleted);
    }

    // Helper to check remaining publication menus
    public int getPublicationMenuCount() {
        return page.locator(".dots-wrapper").count();
    }

    public boolean completePublicationFlow(Path mediaPath, String caption, boolean blurEnabled) {
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
        boolean success = waitForSuccessToast(ConfigReader.getMediumTimeout());
        forceExitComposer();
        return success;
    }
}

