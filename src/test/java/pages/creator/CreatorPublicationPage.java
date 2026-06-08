package pages.creator;

import pages.common.BasePage;

import java.nio.file.Files;
import java.nio.file.Path;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

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
        // Try direct input first
        Locator input = page.locator("input[type='file']");
        if (input.count() > 0) {
            input.first().setInputFiles(mediaPath);
            logger.info("Uploaded media via input[type=file]: {}", mediaPath);
            return;
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
        try {
            Locator toast = exact ? page.getByText(text, new Page.GetByTextOptions().setExact(true)) : page.getByText(text);
            waitVisible(toast, ConfigReader.getLongTimeout());
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
                page.waitForTimeout(ConfigReader.getAnimationTimeout()); 
            } catch (Exception e) {
                logger.debug("Wait interrupted: {}", e.getMessage());
            }
        }
        return false;
    }

    // Consider composer closed when publish UI is no longer visible
    private boolean isComposerOpen() {
        try {
            // If publish button or caption placeholder visible, composer is open
            if (getPublishButton().isVisible()) return true;
        } catch (Exception e) {
            logger.debug("Publish button check failed: {}", e.getMessage());
        }
        try {
            if (page.getByText(CAPTION_TITLE_TEXT, new Page.GetByTextOptions().setExact(true)).isVisible()) return true;
        } catch (Exception e) {
            logger.debug("Caption title check failed: {}", e.getMessage());
        }
        try {
            if (page.getByPlaceholder(CAPTION_PLACEHOLDER).isVisible()) return true;
        } catch (Exception e) {
            logger.debug("Caption placeholder check failed: {}", e.getMessage());
        }
        return false;
    }

    

    // Try to close composer if still open (best-effort, non-fatal)
    private void closeComposerIfOpen() {
        if (!isComposerOpen()) return;
        try {
            Locator closeBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close"));
            if (closeBtn.isVisible()) {
                clickWithRetry(closeBtn, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
                return;
            }
        } catch (Exception e) { logger.debug("Close btn strategy 1 failed: {}", e.getMessage()); }
        try {
            Locator cancelBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
            if (cancelBtn.isVisible()) {
                clickWithRetry(cancelBtn, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
                return;
            }
        } catch (Exception e) { logger.debug("Close btn strategy 2 failed: {}", e.getMessage()); }
        try {
            // Generic .close icon or top-right X in dialogs
            Locator x = page.locator(".close, button[aria-label='Close'], [data-testid='close']").first();
            if (x != null && x.count() > 0 && x.isVisible()) {
                clickWithRetry(x, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
            }
        } catch (Exception e) { logger.debug("Close btn strategy 3 failed: {}", e.getMessage()); }
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
        if (page.getByText(CONVERSION_TOOLS_TEXT).isVisible()) {
            clickIUnderstandIfPresent();
        }
    }

    private void clickIUnderstandIfPresent() {
        Locator understand = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(I_UNDERSTAND_BTN));
        if (understand.isVisible()) {
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
        Locator icon = page.getByRole(AriaRole.TABPANEL, new Page.GetByRoleOptions().setName("publications icon")).locator("img").first();
        if (icon.count() > 0 && icon.isVisible()) {
            clickWithRetry(icon, 2, ConfigReader.getAnimationTimeout());
            logger.info("Clicked publications icon from profile");
            return;
        }
        if (isPublicationsScreen()) {
            logger.info("Already on Publications screen; no need to click icon");
            return;
        }
        Locator connectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Connect").setExact(true));
        if (connectBtn.isVisible()) {
            clickWithRetry(connectBtn, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
            try { 
                page.waitForTimeout(ConfigReader.getAnimationTimeout()); 
            } catch (Exception e) {
                logger.debug("Wait failed: {}", e.getMessage());
            }
            if (icon.count() > 0 && icon.isVisible()) {
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
    try {
        Locator publicationsTitle = page.getByText(PUBLICATIONS_TEXT);
        return publicationsTitle.isVisible();
    } catch (Exception e) {
        logger.debug("isPublicationsScreen check failed: {}", e.getMessage());
        return false;
    }
}

// Delete a single publication via available UI entry points
public void deleteOnePublication() {
    try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Exception e) { logger.debug("Pre-delete wait failed: {}", e.getMessage()); }

    // 1) Some flows require clicking 'Connect' first
    Locator connectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Connect").setExact(true));
    if (connectBtn.isVisible()) {
      clickWithRetry(connectBtn, 2, ConfigReader.getElementRetryDelay());
      logger.info("Clicked 'Connect' before deletion flow");
      try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Exception e) { logger.debug("Post-connect wait failed: {}", e.getMessage()); }
    }

    // 2) Primary path: menu beside post '.dots-wrapper'
    Locator dotsWrapper = page.locator(".dots-wrapper");
    if (dotsWrapper.count() > 0) {
      try { dotsWrapper.first().scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("ScrollIntoView failed: {}", e.getMessage()); }
      // Wait for dots wrapper to be visible before clicking
      waitVisible(dotsWrapper.first(), ConfigReader.getLongTimeout());
      clickWithRetry(dotsWrapper.first(), 2, ConfigReader.getElementRetryDelay());
      confirmDeletionPopup();
      return;
    }

    // 3) Alternate menu icon: role IMG name 'dot'
    Locator dotImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("dot")).first();
    if (dotImg != null && dotImg.count() > 0) {
      try { dotImg.scrollIntoViewIfNeeded(); } catch (Exception e) { logger.debug("ScrollIntoView failed: {}", e.getMessage()); }
      clickWithRetry(dotImg, 2, ConfigReader.getElementRetryDelay());
      confirmDeletionPopup();
      return;
    }

    // 4) Fallback path: open first post and use action button '.d-flex'
    Locator fanPost = page.locator(".fanProfilePost").first();
    Locator actionBtn = page.locator(".d-flex").first();
    if (fanPost.count() > 0 && fanPost.isVisible()) {
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
      // Open publications only if not already there
      if (!isPublicationsScreen()) {
        openProfilePublicationsIcon();
        // If still not on publications, try a brief wait and check again
        if (!isPublicationsScreen()) {
          try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Exception e) { logger.debug("Navigation wait failed: {}", e.getMessage()); }
          if (!isPublicationsScreen()) {
            logger.warn("Could not navigate to Publications screen; stopping loop");
            break;
          }
        }
      }

      // Aggressive scroll to trigger lazy load immediately
      try {
        page.evaluate("window.scrollTo(0, 0)");
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
        page.evaluate("window.scrollBy(0, 500)");
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
        page.evaluate("window.scrollBy(0, -500)");
        page.waitForTimeout(ConfigReader.getUiSettleTimeout());
      } catch (Exception e) { logger.debug("Scroll trigger failed: {}", e.getMessage()); }

      // Wait for list to load: try multiple selectors with increased timeout
      long start = System.currentTimeMillis();
      boolean anyPresent = false;
      while (System.currentTimeMillis() - start < 10000) {
        int dots = page.locator(".dots-wrapper").count();
        int dotsImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("dot")).count();
        int posts = page.locator(".fanProfilePost").count();
        if (dots > 0 || dotsImg > 0 || posts > 0) { 
          anyPresent = true; 
          logger.info("Found {} publications (dots: {}, dotsImg: {}, posts: {})", 
                     Math.max(Math.max(dots, dotsImg), posts), dots, dotsImg, posts);
          break; 
        }
        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Exception e) { logger.debug("Poll wait failed: {}", e.getMessage()); }
      }

      if (!anyPresent) {
        logger.info("No publications found on Publications screen");
        break;
      }

      // Delete one and loop
      deleteOnePublication();
      deleted++;
      
      // Force a full page reload to ensure the Publications list is completely refreshed
      try {
        logger.info("Reloading page to refresh Publications list after deletion");
        page.reload();
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
        // Extra wait for UI to stabilize and lazy loading to complete
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Exception e) { logger.debug("Post-reload wait failed: {}", e.getMessage()); }
        logger.info("Page reloaded, waiting for Publications list to refresh");
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

// ... (rest of the code remains the same)
    

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
        // Optimized: reduced toast wait from 6000ms to 2000ms for faster execution
        long toastWait = Long.parseLong(ConfigReader.getProperty("publication.toast.wait.ms", "2000"));
        boolean success = waitForSuccessToast(toastWait);
        if (!success) {
            logger.warn("Success toast not detected within {} ms; exiting composer anyway", toastWait);
        }
        forceExitComposer();
    }
}

