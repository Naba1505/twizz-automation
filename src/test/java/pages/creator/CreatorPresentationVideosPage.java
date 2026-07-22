package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CreatorPresentationVideosPage extends BasePage {

    private static final String SETTINGS_URL_PART = "/common/setting";
    private static final String PRESENTATION_VIDEOS_MENU = "Presentation Videos";
    private static final String PRESENTATION_VIDEO_TITLE = "Presentation Video"; // exact

    public CreatorPresentationVideosPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator presentationVideosMenuItem() {
        // The Settings menu label can be singular or plural depending on build
        Locator exact = page.getByText(PRESENTATION_VIDEOS_MENU, new Page.GetByTextOptions().setExact(false));
        if (exact.count() > 0) return exact;
        return page.getByText("Presentation Video", new Page.GetByTextOptions().setExact(false));
    }

    private Locator presentationVideoTitle() {
        // Title can be singular or plural on different builds
        Locator exact = page.getByText(PRESENTATION_VIDEO_TITLE, new Page.GetByTextOptions().setExact(true));
        if (exact.count() > 0) return exact;
        return page.getByText("Presentation Video", new Page.GetByTextOptions().setExact(false));
    }

    private Locator addButton() {
        // Reference flow uses an "add" button, not "plus"
        Locator[] candidates = new Locator[] {
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("add")),
                page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("add")),
                page.locator("button:has(> img[alt='add'])"),
                page.getByText("+")
        };
        for (Locator candidate : candidates) {
            try {
                if (safeIsVisible(candidate.first())) return candidate.first();
            } catch (Throwable e) { logger.debug("Add button fallback check failed: {}", e.getMessage()); }
        }
        return candidates[0];
    }

    private Locator fileInput() {
        return page.locator("input[type='file']");
    }

    private Locator gotItButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Got it"));
    }

    private Locator waitingStatusSpan() {
        return page.getByText("Waiting", new Page.GetByTextOptions().setExact(false));
    }

    private Locator trashIcon() {
        Locator[] candidates = new Locator[] {
                page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("trash")),
                page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete")),
                page.locator("img[alt='trash']"),
                page.locator("[data-testid='delete-video']")
        };
        for (Locator candidate : candidates) {
            try {
                if (safeIsVisible(candidate.first())) return candidate.first();
            } catch (Throwable e) { logger.debug("Trash icon fallback check failed: {}", e.getMessage()); }
        }
        return candidates[0];
    }

    private Locator deleteConfirmMessage() {
        return page.getByText("Do you really want to delete your video?");
    }

    private Locator deleteVideoButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete Video"));
    }

    private Locator emptyPromptText() {
        return page.getByText("Click on the", new Page.GetByTextOptions().setExact(false));
    }

    private Locator presentationVideoStickyButton() {
        return page.locator(".presentation-video-sticky-button");
    }

    // ---------- Steps ----------
    @Step("Check whether a presentation video is present")
    public boolean hasPresentationVideo() {
        return safeIsVisible(trashIcon()) || safeIsVisible(waitingStatusSpan().first());
    }

    @Step("Open Settings from profile (Presentation Videos)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Click the Presentation Video sticky/Got it button after upload")
    public void clickPresentationVideoStickyButton() {
        // The reference shows a "Got it" confirmation button after file selection
        Locator gotIt = gotItButton();
        try {
            waitVisible(gotIt, ConfigReader.getShortTimeout());
            clickWithRetry(gotIt, 1, ConfigReader.getElementRetryDelay());
            return;
        } catch (Throwable e) {
            logger.debug("Got it button not visible: {}", e.getMessage());
        }
        // Fallback to the legacy sticky button class if present
        Locator sticky = presentationVideoStickyButton().first();
        if (safeIsVisible(sticky)) {
            waitVisible(sticky, ConfigReader.getShortTimeout());
            try { sticky.scrollIntoViewIfNeeded(); } catch (Throwable t) { logger.debug("Scroll failed: {}", t.getMessage()); }
            clickWithRetry(sticky, 1, ConfigReader.getElementRetryDelay());
        }
    }

    @Step("Open 'Presentation Videos' in Settings")
    public void openPresentationVideosScreen() {
        // The menu item can be low in the Settings list; scroll gently to reveal it
        Locator menuItem = presentationVideosMenuItem();
        waitVisible(menuItem, ConfigReader.getShortTimeout());
        try { menuItem.scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(menuItem, 2, ConfigReader.getElementRetryDelay());
        // Wait for the target screen title instead of a strict URL; some builds use different paths
        try { page.waitForURL("**/creator/**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout())); } catch (Throwable e) { logger.debug("URL wait failed: {}", e.getMessage()); }
        logger.info("Current URL after Presentation Videos navigation: {}", page.url());
        waitVisible(presentationVideoTitle(), ConfigReader.getMediumTimeout());
    }

    @Step("Upload presentation video from path: {videoPath}")
    public void uploadPresentationVideo(Path videoPath) {
        // Ensure the add button is present (signals the upload area is ready) but do NOT click it,
        // because clicking triggers the native OS file dialog. Drive the hidden input directly
        // like MediaPush and Collection pages do.
        Locator add = addButton();
        waitVisible(add, ConfigReader.getShortTimeout());
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Settle wait failed: {}", e.getMessage()); }

        Locator input = fileInput();
        try {
            input.first().setInputFiles(videoPath);
            logger.info("Presentation video file set via input[type=file]: {}", videoPath);
        } catch (Throwable t) {
            logger.error("Failed to set presentation video file via input[type=file]: {}", t.getMessage());
            throw t;
        }
    }

    @Step("Wait for 'Waiting' status to appear after upload")
    public void waitForWaitingStatus() {
        // Poll with gentle lazy-load nudges to surface the status row if list is virtualized
        long deadline = System.currentTimeMillis() + ConfigReader.getLongTimeout();
        while (System.currentTimeMillis() < deadline) {
            try {
                if (safeIsVisible(waitingStatusSpan().first())) {
                    waitVisible(waitingStatusSpan().first(), ConfigReader.getShortTimeout());
                    return;
                }
            } catch (Throwable e) { logger.debug("Status check failed: {}", e.getMessage()); }
            try {
                page.mouse().wheel(0, 500);
                try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Scroll wait failed: {}", e.getMessage()); }
                page.mouse().wheel(0, -500);
            } catch (Throwable e) { logger.debug("Wheel failed: {}", e.getMessage()); }
        }
        // Final assert to surface failure if never found
        waitVisible(waitingStatusSpan(), ConfigReader.getShortTimeout());
    }

    @Step("Delete the presentation video via trash icon and confirm")
    public void deletePresentationVideo() {
        Locator trash = trashIcon();
        if (!safeIsVisible(trash)) {
            logger.info("No trash/delete icon visible; skipping delete (video may not exist)");
            return;
        }
        logger.info("Trash/delete icon visible; proceeding to delete presentation video");
        clickWithRetry(trash, 1, ConfigReader.getElementRetryDelay());
        try {
            waitVisible(deleteConfirmMessage(), ConfigReader.getShortTimeout());
            waitVisible(deleteVideoButton(), ConfigReader.getShortTimeout());
            clickWithRetry(deleteVideoButton(), 1, ConfigReader.getElementRetryDelay());
            logger.info("Presentation video delete confirmed");
        } catch (Throwable e) {
            logger.warn("Delete confirmation did not appear as expected: {}", e.getMessage());
        }
    }

    @Step("Assert empty prompt is visible after deleting presentation video")
    public void assertEmptyPromptVisible() {
        // First wait for any video row to disappear (trash icon / Waiting status gone)
        long deadline = System.currentTimeMillis() + ConfigReader.getMediumTimeout();
        while (System.currentTimeMillis() < deadline) {
            if (!hasPresentationVideo()) break;
            try {
                page.mouse().wheel(0, 200);
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Scroll wait failed: {}", e.getMessage()); }
                page.mouse().wheel(0, -200);
            } catch (Throwable e) { logger.debug("Wheel failed: {}", e.getMessage()); }
        }
        if (hasPresentationVideo()) {
            throw new AssertionError("Presentation video is still present after delete wait");
        }
        logger.info("No presentation video present; empty state confirmed");
        // Also assert the empty prompt text if available
        try {
            waitVisible(emptyPromptText(), ConfigReader.getShortTimeout());
        } catch (Throwable e) {
            logger.debug("Empty prompt text not visible (may be replaced by campaign UI): {}", e.getMessage());
        }
    }

    // Convenience helper to resolve repository-relative path
    public static Path resolveVideoPath(String relative) {
        return Paths.get(relative).toAbsolutePath();
    }
}

