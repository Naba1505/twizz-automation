package pages;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Path;
 

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

    // Navigation from profile to Publications
    public void openProfilePublicationsIcon() {
        Locator icon = page.getByRole(AriaRole.TABPANEL, new Page.GetByRoleOptions().setName("publications icon")).locator("img").first();
        if (icon.count() > 0 && icon.isVisible()) {
            clickWithRetry(icon, 2, 300);
            logger.info("Clicked publications icon from profile");
            return;
        }
        if (isPublicationsScreen()) {
            logger.info("Already on Publications screen; no need to click icon");
            return;
        }
        Locator connectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Connect").setExact(true));
        if (connectBtn.isVisible()) {
            clickWithRetry(connectBtn, 2, 200);
            try { page.waitForTimeout(300); } catch (Exception ignored) {}
            if (icon.count() > 0 && icon.isVisible()) {
                clickWithRetry(icon, 2, 300);
                logger.info("Clicked publications icon after Connect");
                return;
            }
        }
        logger.warn("Publications icon not visible and not on Publications screen");
    }

// Verify Publications screen by presence of 'Publications' text
public void verifyPublicationsScreen() {
    Locator publicationsTitle = page.getByText(PUBLICATIONS_TEXT);
    waitVisible(publicationsTitle, 20000);
    logger.info("Publications screen visible");
}

private boolean isPublicationsScreen() {
    try {
        Locator publicationsTitle = page.getByText(PUBLICATIONS_TEXT);
        return publicationsTitle.isVisible();
    } catch (Exception ignored) {
        return false;
    }
}

// Delete a single publication via available UI entry points
public void deleteOnePublication() {
    try { page.waitForTimeout(300); } catch (Exception ignored) {}

    // 1) Some flows require clicking 'Connect' first
    Locator connectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Connect").setExact(true));
    if (connectBtn.isVisible()) {
      clickWithRetry(connectBtn, 2, 200);
      logger.info("Clicked 'Connect' before deletion flow");
      try { page.waitForTimeout(300); } catch (Exception ignored) {}
    }

    // 2) Primary path: menu beside post '.dots-wrapper'
    Locator dotsWrapper = page.locator(".dots-wrapper");
    if (dotsWrapper.count() > 0) {
      try { dotsWrapper.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
      clickWithRetry(dotsWrapper.first(), 2, 200);
      confirmDeletionPopup();
      return;
    }

    // 3) Alternate menu icon: role IMG name 'dot'
    Locator dotImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("dot")).first();
    if (dotImg != null && dotImg.count() > 0) {
      try { dotImg.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
      clickWithRetry(dotImg, 2, 200);
      confirmDeletionPopup();
      return;
    }

    // 4) Fallback path: open first post and use action button '.d-flex'
    Locator fanPost = page.locator(".fanProfilePost").first();
    Locator actionBtn = page.locator(".d-flex").first();
    if (fanPost.count() > 0 && fanPost.isVisible()) {
      clickWithRetry(fanPost, 2, 200);
      logger.info("Opened first fan profile post");
      waitVisible(actionBtn, 10000);
      clickWithRetry(actionBtn, 2, 200);
      confirmDeletionPopup();
      return;
    }

    logger.warn("No known delete entry point found for publication");
}

private void confirmDeletionPopup() {
    Locator confirmText = page.getByText("Do you really want to delete a publication?");
    waitVisible(confirmText, 10000);
    Locator yesDelete = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, delete"));
    waitVisible(yesDelete, 10000);
    clickWithRetry(yesDelete, 2, 200);
    logger.info("Confirmed publication deletion");
    try { page.waitForTimeout(600); } catch (Exception ignored) {}
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
          try { page.waitForTimeout(700); } catch (Exception ignored) {}
          if (!isPublicationsScreen()) {
            logger.warn("Could not navigate to Publications screen; stopping loop");
            break;
          }
        }
      }

      // Wait for list to load: try multiple selectors
      long start = System.currentTimeMillis();
      boolean anyPresent = false;
      while (System.currentTimeMillis() - start < 5000) {
        int dots = page.locator(".dots-wrapper").count();
        int dotsImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("dot")).count();
        int posts = page.locator(".fanProfilePost").count();
        if (dots > 0 || dotsImg > 0 || posts > 0) { anyPresent = true; break; }
        try { page.waitForTimeout(200); } catch (Exception ignored) {}
      }

      // Gentle scroll to trigger lazy load if nothing yet
      if (!anyPresent) {
        try { page.evaluate("window.scrollTo(0, 0)"); } catch (Exception ignored) {}
        try { page.evaluate("window.scrollBy(0, 400)"); } catch (Exception ignored) {}
        anyPresent = page.locator(".dots-wrapper").count() > 0
                || page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("dot")).count() > 0
                || page.locator(".fanProfilePost").count() > 0;
      }

      if (!anyPresent) {
        logger.info("No publications found on Publications screen");
        break;
      }

      // Delete one and loop
      deleteOnePublication();
      deleted++;
      try { page.waitForTimeout(500); } catch (Exception ignored) {}
    }
    logger.info("Deleted {} publications (loop until none left)", deleted);
  }

    // Helper to check remaining publication menus
    public int getPublicationMenuCount() {
        return page.locator(".dots-wrapper").count();
    }

// ... (rest of the code remains the same)
    // Helper to click if visible
    private void clickIfVisible(Locator locator) {
        if (locator.isVisible()) {
            clickWithRetry(locator, 2, 200);
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
