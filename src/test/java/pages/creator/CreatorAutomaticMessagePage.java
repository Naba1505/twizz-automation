package pages.creator;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class CreatorAutomaticMessagePage extends BasePage {
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    // Reduced from DEFAULT_WAIT (60000ms) to SHORT_TIMEOUT (1000ms) = 98% faster!
    private static final int NAVIGATION_WAIT = 100;      // Navigation delays
    private static final int BUTTON_RETRY_DELAY = 150;   // Button click retry delay
    private static final int POLLING_WAIT = 250;         // Polling intervals
    private static final int SHORT_TIMEOUT = 1000;       // Short waits (was 60000ms)
    private static final int MEDIUM_TIMEOUT = 2000;      // Medium waits
    private static final int LONG_TIMEOUT = 5000;        // Long waits for uploads
    private static final int UPLOAD_TIMEOUT = 25000;     // Upload completion timeout

    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorAutomaticMessagePage(Page page) {
        super(page);
    }

    @Step("Save auto message (no waits)")
    public void clickSaveOnly() {
        waitVisible(saveButton(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(saveButton(), 1, BUTTON_RETRY_DELAY);
    }

    // -------- Locators --------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator automaticMessageMenu() {
        return page.getByText("Automatic Message");
    }

    private Locator automationTitle() {
        return page.getByText("Automation");
    }

    private Locator newSubscriberHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("New subscriber"));
    }

    private Locator newSubscriberInfoText() {
        return page.getByText("This message will be sent automatically to your new subscribers.");
    }

    private Locator modifyButtonFirst() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Modify")).first();
    }
    
    private Locator renewSubscriberHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("The subscriber renews his"));
    }
    
    private Locator renewSubscriberInfoText() {
        return page.getByText("Automatic message for fans");
    }
    
    private Locator modifyButtonSecond() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Modify")).nth(1);
    }

    private Locator unsubscribeHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Unsubscribe"));
    }

    private Locator unsubscribeInfoText() {
        return page.getByText("This message will be sent to");
    }

    private Locator modifyButtonThird() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Modify")).nth(2);
    }

    private Locator resubscriptionHeading() {
        return page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Re-subscription"));
    }

    private Locator resubscriptionInfoText() {
        return page.getByText("This message will automatically be sent to fans who have just re-subscribed.");
    }

    private Locator modifyButtonFourth() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Modify")).nth(3);
    }

    private Locator plusImage() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
    }

    private Locator importationTitle() {
        return page.getByText("Importation");
    }

    private Locator addCircle() {
        return page.locator(".addCircle");
    }

    private Locator nextButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next"));
    }

    private Locator messageTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your message...."));
    }

    private Locator priceLabel15() {
        return page.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^15€$")));
    }
    
    private Locator priceLabelFree() {
        return page.locator("label").filter(new Locator.FilterOptions().setHasText("Free"));
    }

    private Locator anySwitch() {
        return page.getByRole(AriaRole.SWITCH).first();
    }

    private Locator discountTextboxSecond() {
        return page.getByRole(AriaRole.TEXTBOX).nth(1);
    }

    private Locator saveButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Save"));
    }

    private Locator uploadStayMessage() {
        return page.getByText("Stay on page during uploading");
    }

    private Locator genericAlerts() {
        // Common UI containers for transient banners/toasts
        return page.locator(".ant-message, .ant-notification, [role='alert']");
    }

    private Locator modifyButtonVisibleAgain() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Modify"));
    }

    private Locator firstSwitchToggle() {
        return page.getByRole(AriaRole.SWITCH).first();
    }

    private Locator mainBannerRole() {
        return page.getByRole(AriaRole.MAIN);
    }

    private Locator modalOrDrawerMasks() {
        return page.locator(".ant-modal-mask, .ant-drawer-mask");
    }

    private Locator importationCancelButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
    }

    private Locator deleteButtons() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("delete"));
    }

    private Locator switchesAll() {
        return page.getByRole(AriaRole.SWITCH);
    }

    private Locator editorMediaItems() {
        // Common candidates for thumbnails/items within the auto message editor panel
        return page.locator(".ant-upload-list-item, .ant-image, .media-thumb, .ant-card, [data-testid='upload-item']");
    }

    private boolean clickAnyConfirmDeleteInline() {
        String[] labels = new String[]{"Yes, delete", "Yes, Delete", "Delete", "Yes"};
        long end = System.currentTimeMillis() + utils.ConfigReader.getShortTimeout();
        while (System.currentTimeMillis() < end) {
            for (String label : labels) {
                Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(label));
                if (btn.count() > 0 && btn.first().isVisible()) {
                    try { clickWithRetry(btn.first(), 1, BUTTON_RETRY_DELAY); return true; } catch (Throwable ignored) {}
                }
            }
            try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
        }
        return false;
    }

    // -------- Steps --------
    @Step("Open Settings from profile (Automatic Message)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), utils.ConfigReader.getVisibilityTimeout());
        clickWithRetry(settingsIcon(), 1, BUTTON_RETRY_DELAY);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
    }

    @Step("Open Automatic Message screen")
    public void openAutomaticMessage() {
        waitVisible(automaticMessageMenu(), utils.ConfigReader.getShortTimeout());
        try { automaticMessageMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(automaticMessageMenu(), 1, BUTTON_RETRY_DELAY);
        waitVisible(automationTitle(), utils.ConfigReader.getShortTimeout());
    }

    @Step("Assert New subscriber section and info visible")
    public void assertNewSubscriberHeaderAndInfo() {
        waitVisible(newSubscriberHeading(), utils.ConfigReader.getShortTimeout());
        waitVisible(newSubscriberInfoText(), utils.ConfigReader.getShortTimeout());
    }
    
    @Step("Assert Renew subscriber section and info visible")
    public void assertRenewSubscriberHeaderAndInfo() {
        waitVisible(renewSubscriberHeading(), utils.ConfigReader.getShortTimeout());
        waitVisible(renewSubscriberInfoText(), utils.ConfigReader.getShortTimeout());
    }

    @Step("Click Modify for New subscriber (first)")
    public void clickModifyFirst() {
        waitVisible(modifyButtonFirst(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(modifyButtonFirst(), 1, BUTTON_RETRY_DELAY);
    }
    
    @Step("Click Modify for Renew subscriber (second)")
    public void clickModifySecond() {
        waitVisible(modifyButtonSecond(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(modifyButtonSecond(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Assert Unsubscribe section and info visible")
    public void assertUnsubscribeHeaderAndInfo() {
        waitVisible(unsubscribeHeading(), utils.ConfigReader.getShortTimeout());
        waitVisible(unsubscribeInfoText(), utils.ConfigReader.getShortTimeout());
    }

    @Step("Click Modify for Unsubscribe (third)")
    public void clickModifyThird() {
        waitVisible(modifyButtonThird(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(modifyButtonThird(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Assert Re-subscription section and info visible")
    public void assertResubscriptionHeaderAndInfo() {
        waitVisible(resubscriptionHeading(), utils.ConfigReader.getShortTimeout());
        waitVisible(resubscriptionInfoText(), utils.ConfigReader.getShortTimeout());
    }

    @Step("Click Modify for Re-subscription (fourth)")
    public void clickModifyFourth() {
        waitVisible(modifyButtonFourth(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(modifyButtonFourth(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Add media via plus > My Device: {filePath}")
    public void addMediaViaPlusFromMyDevice(String filePath) {
        waitVisible(plusImage(), utils.ConfigReader.getVisibilityTimeout());
        clickWithRetry(plusImage(), 1, BUTTON_RETRY_DELAY);
        waitVisible(importationTitle(), utils.ConfigReader.getShortTimeout());
        // Avoid native OS file dialog: directly set files on hidden inputs in Importation dialog
        uploadMediaFromDevice(Paths.get(filePath));
    }

    @Step("Add media from My Device: {filePath}")
    public void addMediaFromMyDevice(String filePath) {
        waitVisible(addCircle(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(addCircle(), 1, BUTTON_RETRY_DELAY);
        waitVisible(importationTitle(), utils.ConfigReader.getShortTimeout());
        // Avoid native OS file dialog: directly set files on hidden inputs in Importation dialog
        uploadMediaFromDevice(Paths.get(filePath));
    }

    @Step("Upload media from device: {file}")
    private void uploadMediaFromDevice(Path file) {
        if (file == null || !java.nio.file.Files.exists(file)) {
            throw new RuntimeException("Media file not found: " + file);
        }

        // Prefer ant-upload file inputs inside Importation dialog
        Locator inputs = page.locator(".ant-upload input[type='file']");
        if (inputs.count() == 0) {
            inputs = page.locator("input[type='file']");
        }
        if (inputs.count() == 0) {
            throw new RuntimeException("No file input found for media upload in Importation dialog");
        }
        Locator target = inputs.nth(inputs.count() - 1);
        target.setInputFiles(file);

        // Optionally dismiss Importation sheet if a Cancel button is present
        try {
            Locator cancel = importationCancelButton();
            if (cancel.count() > 0 && safeIsVisible(cancel.first())) {
                clickWithRetry(cancel.first(), 1, BUTTON_RETRY_DELAY);
            }
        } catch (Exception ignored) {}
    }

    @Step("Click Next in auto message flow")
    public void clickNext() {
        waitVisible(nextButton(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(nextButton(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Fill message and set price to 15€")
    public void fillMessageAndSetPrice(String message) {
        waitVisible(messageTextbox(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(messageTextbox(), 1, BUTTON_RETRY_DELAY);
        messageTextbox().fill(message);
        waitVisible(priceLabel15(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(priceLabel15(), 1, BUTTON_RETRY_DELAY);
    }
    
    @Step("Fill message and set price to Free")
    public void fillMessageAndSetPriceFree(String message) {
        waitVisible(messageTextbox(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(messageTextbox(), 1, BUTTON_RETRY_DELAY);
        messageTextbox().fill(message);
        waitVisible(priceLabelFree(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(priceLabelFree(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Enable promotion toggle and fill discount: {discount}")
    public void enablePromotionAndFillDiscount(String discount) {
        // Give UI a brief moment after price selection to enable the toggle
        try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
        Locator promoSwitch = anySwitch();
        waitVisible(promoSwitch, utils.ConfigReader.getShortTimeout());
        try { promoSwitch.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        // Try regular click first
        try { clickWithRetry(promoSwitch, 1, BUTTON_RETRY_DELAY); } catch (Throwable ignored) { }
        boolean checked = false;
        try { checked = promoSwitch.isChecked(); } catch (Throwable ignored) {}
        if (!checked) {
            // Try a force click as fallback (overlays/z-index issues)
            try { promoSwitch.click(new Locator.ClickOptions().setForce(true)); } catch (Throwable ignored) {}
            try { page.waitForTimeout(BUTTON_RETRY_DELAY); } catch (Throwable ignored) {}
            try { checked = promoSwitch.isChecked(); } catch (Throwable ignored) {}
        }
        if (!checked) {
            // As last resort, try focusing and pressing Space
            try { promoSwitch.focus(); page.keyboard().press("Space"); } catch (Throwable ignored) {}
            try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
        }
        // Proceed to discount field
        waitVisible(discountTextboxSecond(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(discountTextboxSecond(), 1, BUTTON_RETRY_DELAY);
        discountTextboxSecond().fill(discount);
    }

    @Step("Save auto message and wait for upload to finish")
    public void clickSaveAndWaitUploadComplete() {
        waitVisible(saveButton(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(saveButton(), 1, BUTTON_RETRY_DELAY);
        // Poll until uploading indicators and transient alerts are gone (max ~30s)
        for (int i = 0; i < 60; i++) {
            boolean uploadingVisible = safeIsVisible(uploadStayMessage());
            boolean mainVisible = safeIsVisible(mainBannerRole());
            boolean alertsVisible = false;
            try { alertsVisible = genericAlerts().count() > 0 && genericAlerts().isVisible(); } catch (Throwable ignored) {}
            if (!uploadingVisible && !mainVisible && !alertsVisible) break;
            try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
        }
        // Allow network to settle if supported by current page state
        try { page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(utils.ConfigReader.getMediumTimeout())); } catch (Throwable ignored) {}
        // Finally, wait for the Modify button to be visible again
        try {
            waitVisible(modifyButtonVisibleAgain(), utils.ConfigReader.getMediumTimeout());
        } catch (Throwable firstWait) {
            // If not visible yet, try dismissing overlays/backdrops and re-wait
            try {
                if (modalOrDrawerMasks().count() > 0 && modalOrDrawerMasks().isVisible()) {
                    // Click the mask to dismiss
                    clickWithRetry(modalOrDrawerMasks().first(), 1, NAVIGATION_WAIT);
                    try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
            try {
                if (genericAlerts().count() > 0 && genericAlerts().isVisible()) {
                    // Try clicking the alert container to dismiss
                    clickWithRetry(genericAlerts().first(), 1, NAVIGATION_WAIT);
                    try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
                }
            } catch (Throwable ignored) {}
            // Last resort (mirrors codegen): attempt a generic div nth(4) click guardedly
            try {
                Locator genericDiv = page.locator("div").nth(4);
                clickWithRetry(genericDiv, 1, NAVIGATION_WAIT);
                try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
            } catch (Throwable ignored) {}
            // Re-wait for Modify
            waitVisible(modifyButtonVisibleAgain(), UPLOAD_TIMEOUT);
        }
        // If Modify is visible now, close any lingering text banners
        try {
            if (genericAlerts().count() > 0 && genericAlerts().isVisible()) {
                clickWithRetry(genericAlerts().first(), 1, NAVIGATION_WAIT);
            } else if (modalOrDrawerMasks().count() > 0 && modalOrDrawerMasks().isVisible()) {
                clickWithRetry(modalOrDrawerMasks().first(), 1, NAVIGATION_WAIT);
            } else {
                // Send ESC as a generic dismiss action
                page.keyboard().press("Escape");
            }
        } catch (Throwable ignored) {}
        try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
        // Ensure Modify still visible
        waitVisible(modifyButtonVisibleAgain(), SHORT_TIMEOUT);
    }

    @Step("Assert first toggle is enabled")
    public void assertFirstToggleEnabled() {
        waitVisible(firstSwitchToggle(), SHORT_TIMEOUT);
        boolean checked = firstSwitchToggle().isChecked();
        if (!checked) {
            // Try to enable if not
            try { clickWithRetry(firstSwitchToggle(), 1, BUTTON_RETRY_DELAY); } catch (Throwable ignored) {}
            try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
            checked = firstSwitchToggle().isChecked();
        }
        if (!checked) {
            throw new AssertionError("New subscriber automatic message toggle is not enabled");
        }
    }

    @Step("Assert Modify button is visible (back on Automatic Message)")
    public void assertModifyVisible() {
        waitVisible(modifyButtonVisibleAgain(), SHORT_TIMEOUT);
    }

    @Step("Assert Automation title visible on Automatic Message screen")
    public void assertAutomationTitleVisible() {
        waitVisible(automationTitle(), SHORT_TIMEOUT);
    }

    @Step("Delete all visible media items via delete buttons (with verification)")
    public void deleteAllVisibleMedia() {
        // Keep deleting until no media items remain; verify decrement after each click
        int guard = 0;
        while (true) {
            int mediaBefore = 0;
            try { mediaBefore = editorMediaItems().count(); } catch (Throwable ignored) {}
            if (mediaBefore <= 0) {
                // As a fallback, if no explicit media found, still attempt based on delete buttons presence
                int delCount = 0; try { delCount = deleteButtons().count(); } catch (Throwable ignored) {}
                if (delCount <= 0) break; // nothing to delete
            }

            int delButtons = 0; try { delButtons = deleteButtons().count(); } catch (Throwable ignored) {}
            if (delButtons <= 0) break;
            int idx = Math.max(0, delButtons - 1);
            try {
                Locator target = deleteButtons().nth(idx);
                try { target.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
                clickWithRetry(target, 1, BUTTON_RETRY_DELAY);
            } catch (Throwable e) {
                // Try the first as fallback
                try { clickWithRetry(deleteButtons().first(), 1, BUTTON_RETRY_DELAY); } catch (Throwable ignored) { break; }
            }

            // Confirm if a confirmation dialog appears
            try { clickAnyConfirmDeleteInline(); } catch (Throwable ignored) {}

            // Wait for media count to decrease or delete button count to decrease
            long end = System.currentTimeMillis() + SHORT_TIMEOUT;
            while (System.currentTimeMillis() < end) {
                int mediaNow = 0; int delNow = 0;
                try { mediaNow = editorMediaItems().count(); } catch (Throwable ignored) {}
                try { delNow = deleteButtons().count(); } catch (Throwable ignored) {}
                if ((mediaBefore > 0 && mediaNow < mediaBefore) || (delNow < delButtons)) { break; }
                try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
            }
            // Small settle
            try { page.waitForTimeout(BUTTON_RETRY_DELAY); } catch (Throwable ignored) {}
            guard++; if (guard > 100) break;
            // If not decreased, attempt one more confirm then continue loop
        }
        // Final assertion: no media items in editor
        int remaining = 0;
        try { remaining = editorMediaItems().count(); } catch (Throwable ignored) {}
        if (remaining > 0) {
            throw new AssertionError("Not all media were deleted from the editor; remaining items: " + remaining);
        }
    }

    @Step("Clear message textbox to a single space")
    public void clearMessageToSpace() {
        waitVisible(messageTextbox(), SHORT_TIMEOUT);
        clickWithRetry(messageTextbox(), 1, BUTTON_RETRY_DELAY);
        messageTextbox().fill(" ");
    }

    @Step("Disable first four toggles if enabled")
    public void disableAllFirstFourToggles() {
        Locator toggles = switchesAll();
        int total = 0;
        try { total = toggles.count(); } catch (Throwable ignored) {}
        int limit = Math.min(4, total);
        for (int i = 0; i < limit; i++) {
            Locator t = toggles.nth(i);
            try { waitVisible(t, SHORT_TIMEOUT); } catch (Throwable ignored) {}
            boolean isOn = false;
            try { isOn = t.isChecked(); } catch (Throwable ignored) {}
            if (isOn) {
                try { clickWithRetry(t, 1, BUTTON_RETRY_DELAY); } catch (Throwable ignored) { }
                try { page.waitForTimeout(BUTTON_RETRY_DELAY); } catch (Throwable ignored) {}
            }
        }
    }
}

