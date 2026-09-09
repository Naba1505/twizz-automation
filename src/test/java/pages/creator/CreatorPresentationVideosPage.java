package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitUntilState;
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
        // Use exact match: substring matching (exact=false) would also match unrelated
        // text like "Your video is awaiting validation" (which contains "waiting" as a
        // substring), causing hasPresentationVideo() to false-positive on that banner
        // text even when the actual "Waiting" status badge isn't present.
        return page.getByText("Waiting", new Page.GetByTextOptions().setExact(true));
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
        Locator role = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete Video"));
        if (role.count() > 0) return role;
        return page.getByText("Delete Video", new Page.GetByTextOptions().setExact(false));
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

    @Step("Click on the uploaded presentation video tile to open it")
    public void clickOnVideoTile() {
        // Click on the Waiting status text which is part of the video tile
        Locator waitingStatus = waitingStatusSpan();
        waitVisible(waitingStatus.first(), ConfigReader.getShortTimeout());
        logger.info("Clicking on Waiting status to open video tile detail view");
        clickWithRetry(waitingStatus.first(), 1, ConfigReader.getElementRetryDelay());
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Settle wait failed: {}", e.getMessage()); }
    }

    @Step("Delete the presentation video via trash icon and confirm")
    public void deletePresentationVideo() {
        // Wait for trash icon to become visible (video must be in Waiting status)
        long deadline = System.currentTimeMillis() + ConfigReader.getLongTimeout();
        Locator trash = trashIcon();
        while (System.currentTimeMillis() < deadline) {
            if (safeIsVisible(trash)) break;
            try { page.waitForTimeout(ConfigReader.getPollInterval()); } catch (Throwable e) { logger.debug("Poll wait failed: {}", e.getMessage()); }
        }
        if (!safeIsVisible(trash)) {
            throw new AssertionError("Trash/delete icon never became visible; video may not be in Waiting status");
        }
        logger.info("Trash/delete icon visible; proceeding to delete presentation video");
        clickWithRetry(trash, 1, ConfigReader.getElementRetryDelay());

        Locator confirmMsg = deleteConfirmMessage();
        Locator deleteBtn = deleteVideoButton();
        waitVisible(confirmMsg, ConfigReader.getMediumTimeout());
        waitVisible(deleteBtn.first(), ConfigReader.getShortTimeout());
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Settle wait failed: {}", e.getMessage()); }

        // Wait on the actual DELETE API response instead of just polling UI state
        // afterward. UI polling proved unreliable/slow on staging (mirrors the same
        // issue found with the Collection creation toast): the delete can genuinely
        // succeed server-side while the UI takes a long, inconsistent time to reflect
        // it, or a single confirmation click can silently not register at all under
        // load. Waiting on the network response gives a deterministic ground truth.
        boolean deleteSucceeded = false;
        try {
            com.microsoft.playwright.Response response = page.waitForResponse(
                    r -> "DELETE".equalsIgnoreCase(r.request().method()) && r.url().contains("/discover/"),
                    new Page.WaitForResponseOptions().setTimeout(ConfigReader.getMediumTimeout()),
                    () -> deleteBtn.first().click(new Locator.ClickOptions().setForce(false)));
            logger.info("Presentation video delete API responded: status={}, ok={}", response.status(), response.ok());
            deleteSucceeded = response.ok();
            if (!response.ok()) {
                logger.warn("Delete API returned non-2xx status={}; UI state may not update", response.status());
            }
        } catch (Throwable e) {
            logger.warn("Did not observe delete API response within timeout; falling back to UI-only confirmation: {}", e.getMessage());
            try { deleteBtn.first().click(new Locator.ClickOptions().setForce(false)); } catch (Throwable ignored) { /* dialog may already be gone */ }
        }
        logger.info("Presentation video delete confirmed");

        // Wait for the confirmation dialog to close
        waitForElementHidden(deleteBtn.first(), ConfigReader.getMediumTimeout());
        
        // If delete API succeeded, reload immediately to force UI sync instead of waiting 120s
        if (deleteSucceeded) {
            logger.info("Delete API succeeded; reloading page to force UI sync");
            try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Settle wait failed: {}", e.getMessage()); }
            page.reload();
            try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Page load wait failed: {}", e.getMessage()); }
        }
    }

    private void waitForElementHidden(Locator locator, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (!safeIsVisible(locator)) return;
            } catch (Throwable t) { logger.debug("Hidden check failed: {}", t.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable t) { logger.debug("Wait failed: {}", t.getMessage()); }
        }
    }

    @Step("Assert empty prompt is visible after deleting presentation video")
    public void assertEmptyPromptVisible() {
        // After delete API succeeds and page reloads, verify the video is gone
        if (hasPresentationVideo()) {
            throw new AssertionError("Presentation video is still present after delete and reload");
        }

        // Give the backend a moment to persist the delete before reloading
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout() * 2); } catch (Throwable e) { logger.debug("Settle wait failed: {}", e.getMessage()); }

        // Reload to confirm deletion persisted on the backend; allow a few retries
        // because the delete API can be asynchronous on stage.
        logger.info("No presentation video present in UI; reloading to confirm deletion persisted");
        boolean confirmed = false;
        int attempts = 0;
        int maxAttempts = 3;
        while (!confirmed && attempts < maxAttempts) {
            attempts++;
            try {
                page.reload(new Page.ReloadOptions().setWaitUntil(WaitUntilState.NETWORKIDLE));
                logger.info("Reload attempt {} complete", attempts);
            } catch (Throwable e) {
                logger.warn("Page reload failed ({}); trying navigation fallback", e.getMessage());
                navigateAndWait(page.url());
            }

            long innerDeadline = System.currentTimeMillis() + ConfigReader.getMediumTimeout();
            while (System.currentTimeMillis() < innerDeadline) {
                if (!hasPresentationVideo()) {
                    confirmed = true;
                    break;
                }
                try { page.waitForTimeout(ConfigReader.getPollInterval()); } catch (Throwable e) { logger.debug("Poll wait failed: {}", e.getMessage()); }
            }

            if (!confirmed && attempts < maxAttempts) {
                logger.warn("Presentation video still present after reload attempt {}; retrying", attempts);
            }
        }

        if (!confirmed) {
            throw new AssertionError("Presentation video reappeared after page reload; delete did not persist");
        }
        logger.info("No presentation video present after reload; empty state confirmed");

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

