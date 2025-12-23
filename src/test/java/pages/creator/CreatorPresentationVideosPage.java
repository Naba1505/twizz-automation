package pages.creator;

import pages.common.BasePage;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CreatorPresentationVideosPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(CreatorPresentationVideosPage.class);

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
        return page.getByText(PRESENTATION_VIDEOS_MENU);
    }

    private Locator presentationVideoTitleExact() {
        return getByTextExact(PRESENTATION_VIDEO_TITLE);
    }

    private Locator plusButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("plus"));
    }

    private Locator waitingStatusSpan() {
        return page.locator("xpath=//span[contains(text(),'Waiting')]");
    }

    private Locator trashIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("trash"));
    }

    private Locator deleteConfirmMessage() {
        return page.getByText("Do you really want to delete your video?");
    }

    private Locator deleteVideoButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete Video"));
    }

    private Locator emptyPromptText() {
        return page.getByText("Click on the \"+\" button");
    }

    private Locator presentationVideoStickyButton() {
        return page.locator(".presentation-video-sticky-button");
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Presentation Videos)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 1, 150);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            log.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Click the Presentation Video sticky button after upload")
    public void clickPresentationVideoStickyButton() {
        // Wait for the sticky button to appear and be interactable, then click
        waitVisible(presentationVideoStickyButton().first(), DEFAULT_WAIT);
        try { presentationVideoStickyButton().first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(presentationVideoStickyButton().first(), 1, 150);
    }

    @Step("Open 'Presentation Videos' in Settings")
    public void openPresentationVideosScreen() {
        waitVisible(presentationVideosMenuItem(), DEFAULT_WAIT);
        try { presentationVideosMenuItem().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(presentationVideosMenuItem(), 1, 150);
        waitVisible(presentationVideoTitleExact(), DEFAULT_WAIT);
    }

    @Step("Upload presentation video from path: {videoPath}")
    public void uploadPresentationVideo(Path videoPath) {
        waitVisible(plusButton(), DEFAULT_WAIT);
        boolean set = false;
        // Primary: use FileChooser flow around clicking the plus button
        try {
            FileChooser chooser = page.waitForFileChooser(() -> {
                clickWithRetry(plusButton(), 1, 120);
            });
            chooser.setFiles(videoPath);
            set = true;
            log.info("Presentation video file set via FileChooser: {}", videoPath);
        } catch (Throwable fcEx) {
            log.warn("FileChooser did not trigger or failed, will try direct setInputFiles. Reason: {}", fcEx.getMessage());
        }
        // Secondary: try to set files directly on the plus element (some wrappers proxy to an input)
        if (!set) {
            try {
                clickWithRetry(plusButton(), 1, 120);
                plusButton().setInputFiles(videoPath);
                set = true;
                log.info("Presentation video file set via plus button: {}", videoPath);
            } catch (Throwable t) {
                log.warn("Failed to set file via plus button; will try input[type=file]: {}", t.getMessage());
            }
        }
        // Tertiary: use a plain file input in DOM (even if hidden)
        if (!set) {
            Locator fileInput = page.locator("input[type='file']");
            try {
                // Do not require visible; directly set files on the first input
                fileInput.first().setInputFiles(videoPath);
                set = true;
                log.info("Presentation video file set via input[type=file]: {}", videoPath);
            } catch (Throwable t2) {
                log.error("Failed to set presentation video file via any known control: {}", t2.getMessage());
                throw t2;
            }
        }
    }

    @Step("Wait for 'Waiting' status to appear after upload")
    public void waitForWaitingStatus() {
        // Poll with gentle lazy-load nudges to surface the status row if list is virtualized
        long start = System.currentTimeMillis();
        long timeoutMs = 90_000;
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                if (waitingStatusSpan().count() > 0) {
                    waitVisible(waitingStatusSpan().first(), 5_000);
                    return;
                }
            } catch (Throwable ignored) {}
            try {
                page.mouse().wheel(0, 500);
                page.waitForTimeout(200);
                page.mouse().wheel(0, -500);
            } catch (Throwable ignored) {}
        }
        // Final assert to surface failure if never found
        waitVisible(waitingStatusSpan(), 1_000);
    }

    @Step("Delete the presentation video via trash icon and confirm")
    public void deletePresentationVideo() {
        waitVisible(trashIcon(), DEFAULT_WAIT);
        clickWithRetry(trashIcon(), 1, 120);
        waitVisible(deleteConfirmMessage(), DEFAULT_WAIT);
        waitVisible(deleteVideoButton(), DEFAULT_WAIT);
        clickWithRetry(deleteVideoButton(), 1, 150);
    }

    @Step("Assert empty prompt is visible after deleting presentation video")
    public void assertEmptyPromptVisible() {
        // wait with small nudges in case content re-renders
        long start = System.currentTimeMillis();
        long timeoutMs = 30_000;
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                if (emptyPromptText().count() > 0) {
                    waitVisible(emptyPromptText().first(), 5_000);
                    return;
                }
            } catch (Throwable ignored) {}
            try {
                page.mouse().wheel(0, 200);
                page.waitForTimeout(120);
                page.mouse().wheel(0, -200);
            } catch (Throwable ignored) {}
        }
        waitVisible(emptyPromptText(), 1_000);
    }

    // Convenience helper to resolve repository-relative path
    public static Path resolveVideoPath(String relative) {
        return Paths.get(relative).toAbsolutePath();
    }
}

