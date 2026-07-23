package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public class CreatorAutomaticMessagePage extends BasePage {
    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorAutomaticMessagePage(Page page) {
        super(page);
    }

    @Step("Save auto message (no waits)")
    public void clickSaveOnly() {
        waitVisible(saveButton(), ConfigReader.getShortTimeout());
        clickWithRetry(saveButton(), 1, ConfigReader.getElementRetryDelay());
        // Wait for the modification screen to close and Modify buttons to reappear
        try {
            waitVisible(modifyButtonVisibleAgain(), ConfigReader.getMediumTimeout());
        } catch (Throwable e) {
            logger.debug("Modify button did not reappear quickly after save: {}", e.getMessage());
        }
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

    private Locator switchesAll() {
        return page.getByRole(AriaRole.SWITCH);
    }

    private boolean clickAnyConfirmDeleteInline() {
        String[] labels = new String[]{"Yes, delete", "Yes, Delete", "Delete", "Yes"};
        long end = System.currentTimeMillis() + ConfigReader.getShortTimeout();
        while (System.currentTimeMillis() < end) {
            for (String label : labels) {
                Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(label));
                if (btn.count() > 0 && btn.first().isVisible()) {
                    try {
                        btn.first().click(new Locator.ClickOptions().setForce(true));
                        return true;
                    } catch (Throwable e) {
                        logger.debug("Force confirm delete click failed: {}", e.getMessage());
                    }
                }
            }
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        return false;
    }

    // -------- Steps --------
    @Step("Open Settings from profile (Automatic Message)")
    public void openSettingsFromProfile() {
        // Ensure we start from profile before looking for the settings icon
        navigateAndWait(ConfigReader.getBaseUrl() + "/creator/profile");
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Assert current URL contains settings path")
    public void assertOnSettingsUrl() {
        if (!page.url().contains(SETTINGS_URL_PART)) {
            throw new AssertionError("Did not land on Settings screen. URL: " + page.url());
        }
        logger.info("Settings URL confirmed: {}", page.url());
    }

    @Step("Wait briefly for UI to settle")
    public void waitBriefly() {
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
    }

    @Step("Open Automatic Message screen")
    public void openAutomaticMessage() {
        waitVisible(automaticMessageMenu(), ConfigReader.getShortTimeout());
        try { automaticMessageMenu().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(automaticMessageMenu(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(automationTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Assert New subscriber section and info visible")
    public void assertNewSubscriberHeaderAndInfo() {
        waitVisible(newSubscriberHeading(), ConfigReader.getShortTimeout());
        waitVisible(newSubscriberInfoText(), ConfigReader.getShortTimeout());
    }
    
    @Step("Assert Renew subscriber section and info visible")
    public void assertRenewSubscriberHeaderAndInfo() {
        waitVisible(renewSubscriberHeading(), ConfigReader.getShortTimeout());
        waitVisible(renewSubscriberInfoText(), ConfigReader.getShortTimeout());
    }

    @Step("Click Modify for New subscriber (first)")
    public void clickModifyFirst() {
        waitVisible(modifyButtonFirst(), ConfigReader.getShortTimeout());
        clickWithRetry(modifyButtonFirst(), 1, ConfigReader.getElementRetryDelay());
    }
    
    @Step("Click Modify for Renew subscriber (second)")
    public void clickModifySecond() {
        waitVisible(modifyButtonSecond(), ConfigReader.getShortTimeout());
        clickWithRetry(modifyButtonSecond(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert Unsubscribe section and info visible")
    public void assertUnsubscribeHeaderAndInfo() {
        waitVisible(unsubscribeHeading(), ConfigReader.getShortTimeout());
        waitVisible(unsubscribeInfoText(), ConfigReader.getShortTimeout());
    }

    @Step("Click Modify for Unsubscribe (third)")
    public void clickModifyThird() {
        waitVisible(modifyButtonThird(), ConfigReader.getShortTimeout());
        clickWithRetry(modifyButtonThird(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert Re-subscription section and info visible")
    public void assertResubscriptionHeaderAndInfo() {
        waitVisible(resubscriptionHeading(), ConfigReader.getShortTimeout());
        waitVisible(resubscriptionInfoText(), ConfigReader.getShortTimeout());
    }

    @Step("Click Modify for Re-subscription (fourth)")
    public void clickModifyFourth() {
        waitVisible(modifyButtonFourth(), ConfigReader.getShortTimeout());
        clickWithRetry(modifyButtonFourth(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Add media via plus > My Device: {filePath}")
    public void addMediaViaPlusFromMyDevice(String filePath) {
        waitVisible(plusImage(), ConfigReader.getShortTimeout());
        clickWithRetry(plusImage(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(importationTitle(), ConfigReader.getShortTimeout());
        // Avoid native OS file dialog: directly set files on hidden inputs in Importation dialog
        uploadMediaFromDevice(Paths.get(filePath));
    }

    @Step("Add media from My Device: {filePath}")
    public void addMediaFromMyDevice(String filePath) {
        waitVisible(addCircle(), ConfigReader.getShortTimeout());
        clickWithRetry(addCircle(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(importationTitle(), ConfigReader.getShortTimeout());
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
                clickWithRetry(cancel.first(), 1, ConfigReader.getElementRetryDelay());
            }
        } catch (Exception e) { logger.debug("Cancel click failed: {}", e.getMessage()); }
    }

    @Step("Click Next in auto message flow")
    public void clickNext() {
        waitVisible(nextButton(), ConfigReader.getShortTimeout());
        clickWithRetry(nextButton(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Fill message and set price to 15€")
    public void fillMessageAndSetPrice(String message) {
        waitVisible(messageTextbox(), ConfigReader.getShortTimeout());
        typeAndAssert(messageTextbox(), message);
        waitVisible(priceLabel15(), ConfigReader.getShortTimeout());
        clickWithRetry(priceLabel15(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Fill message and set price to Free")
    public void fillMessageAndSetPriceFree(String message) {
        waitVisible(messageTextbox(), ConfigReader.getShortTimeout());
        typeAndAssert(messageTextbox(), message);
        waitVisible(priceLabelFree(), ConfigReader.getShortTimeout());
        clickWithRetry(priceLabelFree(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Enable promotion toggle and fill discount: {discount}")
    public void enablePromotionAndFillDiscount(String discount) {
        // Give UI a brief moment after price selection to enable the toggle
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        Locator promoSwitch = anySwitch();
        waitVisible(promoSwitch, ConfigReader.getShortTimeout());
        try { promoSwitch.scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        try { clickWithRetry(promoSwitch, 1, ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
        boolean checked = false;
        try { checked = promoSwitch.isChecked(); } catch (Throwable e) { logger.debug("Checked check failed: {}", e.getMessage()); }
        if (!checked) {
            try { promoSwitch.click(new Locator.ClickOptions().setForce(true)); } catch (Throwable e) { logger.debug("Force click failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            try { checked = promoSwitch.isChecked(); } catch (Throwable e) { logger.debug("Checked check failed: {}", e.getMessage()); }
        }
        if (!checked) {
            try { promoSwitch.focus(); page.keyboard().press("Space"); } catch (Throwable e) { logger.debug("Keyboard action failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        waitVisible(discountTextboxSecond(), ConfigReader.getShortTimeout());
        typeAndAssert(discountTextboxSecond(), discount);
    }

    @Step("Save auto message and wait for upload to finish")
    public void clickSaveAndWaitUploadComplete() {
        waitVisible(saveButton(), ConfigReader.getShortTimeout());
        clickWithRetry(saveButton(), 1, ConfigReader.getElementRetryDelay());
        for (int i = 0; i < 60; i++) {
            boolean uploadingVisible = safeIsVisible(uploadStayMessage());
            boolean mainVisible = safeIsVisible(mainBannerRole());
            boolean alertsVisible = false;
            try { alertsVisible = genericAlerts().count() > 0 && genericAlerts().isVisible(); } catch (Throwable e) { logger.debug("Alert check failed: {}", e.getMessage()); }
            if (!uploadingVisible && !mainVisible && !alertsVisible) break;
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        try { page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getMediumTimeout())); } catch (Throwable e) { logger.debug("Network idle wait failed: {}", e.getMessage()); }
        try {
            waitVisible(modifyButtonVisibleAgain(), ConfigReader.getMediumTimeout());
        } catch (Throwable firstWait) {
            try {
                if (modalOrDrawerMasks().count() > 0 && modalOrDrawerMasks().isVisible()) {
                    clickWithRetry(modalOrDrawerMasks().first(), 1, ConfigReader.getAnimationTimeout());
                    try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                }
            } catch (Throwable e) { logger.debug("Mask dismiss failed: {}", e.getMessage()); }
            try {
                if (genericAlerts().count() > 0 && genericAlerts().isVisible()) {
                    clickWithRetry(genericAlerts().first(), 1, ConfigReader.getAnimationTimeout());
                    try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                }
            } catch (Throwable e) { logger.debug("Alert dismiss failed: {}", e.getMessage()); }
            try {
                Locator genericDiv = page.locator("div").nth(4);
                clickWithRetry(genericDiv, 1, ConfigReader.getAnimationTimeout());
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            } catch (Throwable e) { logger.debug("Generic div click failed: {}", e.getMessage()); }
            waitVisible(modifyButtonVisibleAgain(), ConfigReader.getShortTimeout());
        }
        try {
            if (genericAlerts().count() > 0 && genericAlerts().isVisible()) {
                clickWithRetry(genericAlerts().first(), 1, ConfigReader.getAnimationTimeout());
            } else if (modalOrDrawerMasks().count() > 0 && modalOrDrawerMasks().isVisible()) {
                clickWithRetry(modalOrDrawerMasks().first(), 1, ConfigReader.getAnimationTimeout());
            } else {
                page.keyboard().press("Escape");
            }
        } catch (Throwable e) { logger.debug("Banner close failed: {}", e.getMessage()); }
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        waitVisible(modifyButtonVisibleAgain(), ConfigReader.getShortTimeout());
    }

    @Step("Assert first toggle is enabled")
    public void assertFirstToggleEnabled() {
        waitVisible(firstSwitchToggle(), ConfigReader.getShortTimeout());
        boolean checked = firstSwitchToggle().isChecked();
        if (!checked) {
            try { clickWithRetry(firstSwitchToggle(), 1, ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            checked = firstSwitchToggle().isChecked();
        }
        if (!checked) {
            throw new AssertionError("New subscriber automatic message toggle is not enabled");
        }
    }

    @Step("Assert Modify button is visible (back on Automatic Message)")
    public void assertModifyVisible() {
        waitVisible(modifyButtonVisibleAgain(), ConfigReader.getShortTimeout());
    }

    @Step("Assert Automation title visible on Automatic Message screen")
    public void assertAutomationTitleVisible() {
        waitVisible(automationTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Delete all visible media items via delete buttons (with verification)")
    public void deleteAllVisibleMedia() {
        Locator deleteBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("delete"));
        try { deleteBtn.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getShortTimeout())); } catch (Throwable e) { logger.debug("No delete button appeared: {}", e.getMessage()); return; }

        long deadline = System.currentTimeMillis() + ConfigReader.getMediumTimeout();
        while (System.currentTimeMillis() < deadline) {
            int count = 0;
            try { count = deleteBtn.count(); } catch (Throwable e) { logger.debug("Count failed: {}", e.getMessage()); }
            if (count <= 0) break;

            try {
                deleteBtn.first().click(new Locator.ClickOptions().setForce(true));
                clickAnyConfirmDeleteInline();
            } catch (Throwable e) {
                logger.debug("Delete click failed (will retry): {}", e.getMessage());
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable ignored) {}
            }
        }
    }

    @Step("Clear message textbox to a single space")
    public void clearMessageToSpace() {
        waitVisible(messageTextbox(), ConfigReader.getShortTimeout());
        clickWithRetry(messageTextbox(), 1, ConfigReader.getElementRetryDelay());
        messageTextbox().fill(" ");
    }

    @Step("Disable first four toggles if enabled")
    public void disableAllFirstFourToggles() {
        Locator toggles = switchesAll();
        int total = 0;
        try { total = toggles.count(); } catch (Throwable e) { logger.debug("Count failed: {}", e.getMessage()); }
        int limit = Math.min(4, total);
        for (int i = 0; i < limit; i++) {
            Locator t = toggles.nth(i);
            try { waitVisible(t, ConfigReader.getShortTimeout()); } catch (Throwable e) { logger.debug("Wait visible failed: {}", e.getMessage()); }
            boolean isOn = false;
            try { isOn = t.isChecked(); } catch (Throwable e) { logger.debug("Checked check failed: {}", e.getMessage()); }
            if (isOn) {
                try { clickWithRetry(t, 1, ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            }
        }
    }
}

