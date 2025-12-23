package pages.fan;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for Fan -> Settings -> Email notification
 */
public class FanEmailNotificationPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanEmailNotificationPage.class);

    public FanEmailNotificationPage(Page page) {
        super(page);
    }

    // ================= Locators =================

    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon"));
    }

    private Locator settingsTitle() {
        return page.getByText("Settings");
    }

    private Locator emailNotificationMenuText() {
        return page.getByText("Email notification");
    }

    private Locator emailNotificationTitle() {
        return page.getByText("Email notification");
    }

    private Locator pushMediaHeading() {
        return page.getByText("Push media from a creator");
    }

    private Locator liveReminderHeading() {
        return page.getByText("Live reminder");
    }

    private Locator schedulingLiveHeading() {
        return page.getByText("Scheduling a live");
    }

    private Locator directLiveHeading() {
        return page.getByText("Direct live");
    }

    private Locator marketingHeading() {
        return page.getByText("Marketing");
    }

    private Locator toggleSwitch(int index) {
        return page.getByRole(AriaRole.SWITCH).nth(index);
    }

    /**
     * Check if toggle at index is currently enabled (checked).
     * Checks multiple attributes and CSS classes to determine state.
     */
    private boolean isToggleEnabled(int index) {
        Locator toggle = toggleSwitch(index);
        waitVisible(toggle, DEFAULT_WAIT);
        
        // Try aria-checked first
        String ariaChecked = toggle.getAttribute("aria-checked");
        if (ariaChecked != null) {
            boolean enabled = "true".equals(ariaChecked);
            logger.info("[Fan][EmailNotification] Toggle at index {} aria-checked={} -> {}", index, ariaChecked, enabled ? "ENABLED" : "DISABLED");
            return enabled;
        }
        
        // Try data-state attribute (common in modern UI frameworks)
        String dataState = toggle.getAttribute("data-state");
        if (dataState != null) {
            boolean enabled = "checked".equals(dataState);
            logger.info("[Fan][EmailNotification] Toggle at index {} data-state={} -> {}", index, dataState, enabled ? "ENABLED" : "DISABLED");
            return enabled;
        }
        
        // Try checked attribute
        String checked = toggle.getAttribute("checked");
        if (checked != null) {
            logger.info("[Fan][EmailNotification] Toggle at index {} checked attr present -> ENABLED", index);
            return true;
        }
        
        // Log all attributes for debugging
        logger.warn("[Fan][EmailNotification] Toggle at index {} - could not determine state, assuming DISABLED", index);
        return false;
    }

    private Locator disableConfirmationDialog() {
        return page.getByText("Do you want to disable this");
    }

    private Locator enableConfirmationDialog() {
        return page.getByText("Do you want to enable this");
    }

    private Locator yesDisableButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, disable"));
    }

    private Locator yesEnableButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, enable"));
    }

    // ================= Navigation =================

    @Step("Click Settings icon from Fan home")
    public void clickSettingsIcon() {
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 2, 200);
        logger.info("[Fan][EmailNotification] Clicked Settings icon");
    }

    @Step("Assert on Settings screen by viewing title")
    public void assertOnSettingsScreen() {
        waitVisible(settingsTitle(), DEFAULT_WAIT);
        logger.info("[Fan][EmailNotification] On Settings screen - title visible");
    }

    @Step("Scroll to and click 'Email notification' menu item")
    public void clickEmailNotificationMenu() {
        Locator menuItem = emailNotificationMenuText();
        // Scroll to make it visible if needed
        for (int i = 0; i < 5 && !safeIsVisible(menuItem); i++) {
            page.mouse().wheel(0, 300);
            page.waitForTimeout(200);
        }
        waitVisible(menuItem, DEFAULT_WAIT);
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, 200);
        logger.info("[Fan][EmailNotification] Clicked 'Email notification' menu item");
    }

    @Step("Assert on Email Notification screen by viewing title")
    public void assertOnEmailNotificationScreen() {
        waitVisible(emailNotificationTitle(), DEFAULT_WAIT);
        // Wait for page to fully load and toggles to be ready
        page.waitForTimeout(1500);
        logger.info("[Fan][EmailNotification] On Email Notification screen - title visible");
    }

    /**
     * Navigate from Fan home to Email Notification screen.
     */
    @Step("Navigate to Email Notification from Fan home")
    public void navigateToEmailNotification() {
        clickSettingsIcon();
        assertOnSettingsScreen();
        clickEmailNotificationMenu();
        assertOnEmailNotificationScreen();
        logger.info("[Fan][EmailNotification] Successfully navigated to Email Notification screen");
    }

    // ================= Toggle Interactions =================

    @Step("Assert 'Push media from a creator' heading visible")
    public void assertPushMediaHeadingVisible() {
        waitVisible(pushMediaHeading(), DEFAULT_WAIT);
        logger.info("[Fan][EmailNotification] 'Push media from a creator' heading visible");
    }

    @Step("Assert 'Live reminder' heading visible")
    public void assertLiveReminderHeadingVisible() {
        waitVisible(liveReminderHeading(), DEFAULT_WAIT);
        logger.info("[Fan][EmailNotification] 'Live reminder' heading visible");
    }

    @Step("Assert 'Scheduling a live' heading visible")
    public void assertSchedulingLiveHeadingVisible() {
        waitVisible(schedulingLiveHeading(), DEFAULT_WAIT);
        logger.info("[Fan][EmailNotification] 'Scheduling a live' heading visible");
    }

    @Step("Assert 'Direct live' heading visible")
    public void assertDirectLiveHeadingVisible() {
        waitVisible(directLiveHeading(), DEFAULT_WAIT);
        logger.info("[Fan][EmailNotification] 'Direct live' heading visible");
    }

    @Step("Assert 'Marketing' heading visible")
    public void assertMarketingHeadingVisible() {
        waitVisible(marketingHeading(), DEFAULT_WAIT);
        logger.info("[Fan][EmailNotification] 'Marketing' heading visible");
    }

    @Step("Click toggle switch at index {index}")
    public void clickToggle(int index) {
        Locator toggle = toggleSwitch(index);
        waitVisible(toggle, DEFAULT_WAIT);
        clickWithRetry(toggle, 2, 200);
        logger.info("[Fan][EmailNotification] Clicked toggle at index {}", index);
    }

    @Step("Assert disable confirmation dialog visible")
    public void assertDisableConfirmationVisible() {
        waitVisible(disableConfirmationDialog(), DEFAULT_WAIT);
        logger.info("[Fan][EmailNotification] Disable confirmation dialog visible");
    }

    @Step("Assert enable confirmation dialog visible")
    public void assertEnableConfirmationVisible() {
        waitVisible(enableConfirmationDialog(), DEFAULT_WAIT);
        logger.info("[Fan][EmailNotification] Enable confirmation dialog visible");
    }

    @Step("Click 'Yes, disable' button")
    public void clickYesDisable() {
        waitVisible(yesDisableButton(), DEFAULT_WAIT);
        clickWithRetry(yesDisableButton(), 2, 200);
        page.waitForTimeout(1000); // Wait for toggle state to update and dialog to close
        logger.info("[Fan][EmailNotification] Clicked 'Yes, disable' button");
    }

    @Step("Click 'Yes, enable' button")
    public void clickYesEnable() {
        waitVisible(yesEnableButton(), DEFAULT_WAIT);
        clickWithRetry(yesEnableButton(), 2, 200);
        page.waitForTimeout(500); // Wait for toggle state to update
        logger.info("[Fan][EmailNotification] Clicked 'Yes, enable' button");
    }

    // ================= Disable Toggle Helper =================

    @Step("Disable toggle at index {index} with confirmation")
    public void disableToggle(int index) {
        // Check if toggle is already disabled - skip if so
        if (!isToggleEnabled(index)) {
            logger.info("[Fan][EmailNotification] Toggle at index {} already disabled, skipping", index);
            return;
        }
        clickToggle(index);
        assertDisableConfirmationVisible();
        clickYesDisable();
        logger.info("[Fan][EmailNotification] Toggle at index {} disabled", index);
    }

    // ================= Enable Toggle Helper =================

    @Step("Enable toggle at index {index} with confirmation")
    public void enableToggle(int index) {
        // Check if toggle is already enabled - skip if so
        if (isToggleEnabled(index)) {
            logger.info("[Fan][EmailNotification] Toggle at index {} already enabled, skipping", index);
            return;
        }
        clickToggle(index);
        assertEnableConfirmationVisible();
        clickYesEnable();
        logger.info("[Fan][EmailNotification] Toggle at index {} enabled", index);
    }

    // ================= Complete Flows =================

    /**
     * Disable all 5 email notification toggles.
     * Toggle indices: 0=Push media, 1=Live reminder, 2=Scheduling live, 3=Direct live, 4=Marketing
     */
    @Step("Disable all email notification toggles")
    public void disableAllToggles() {
        // Toggle 0: Push media from a creator
        assertPushMediaHeadingVisible();
        forceDisableToggle(0);

        // Toggle 1: Live reminder
        assertLiveReminderHeadingVisible();
        forceDisableToggle(1);

        // Toggle 2: Scheduling a live
        assertSchedulingLiveHeadingVisible();
        forceDisableToggle(2);

        // Toggle 3: Direct live
        assertDirectLiveHeadingVisible();
        forceDisableToggle(3);

        // Toggle 4: Marketing
        assertMarketingHeadingVisible();
        forceDisableToggle(4);

        logger.info("[Fan][EmailNotification] All 5 toggles disabled successfully");
    }

    /**
     * Enable all 5 email notification toggles (simple click, no confirmation).
     * Toggle indices: 0=Push media, 1=Live reminder, 2=Scheduling live, 3=Direct live, 4=Marketing
     */
    @Step("Enable all email notification toggles")
    public void enableAllToggles() {
        // Toggle 0: Push media from a creator
        assertPushMediaHeadingVisible();
        forceEnableToggle(0);

        // Toggle 1: Live reminder
        assertLiveReminderHeadingVisible();
        forceEnableToggle(1);

        // Toggle 2: Scheduling a live
        assertSchedulingLiveHeadingVisible();
        forceEnableToggle(2);

        // Toggle 3: Direct live
        assertDirectLiveHeadingVisible();
        forceEnableToggle(3);

        // Toggle 4: Marketing
        assertMarketingHeadingVisible();
        forceEnableToggle(4);

        logger.info("[Fan][EmailNotification] All 5 toggles enabled successfully");
    }

    /**
     * Simple enable toggle - just click without confirmation dialog.
     */
    @Step("Simple enable toggle at index {index}")
    public void simpleEnableToggle(int index) {
        // Check if toggle is already enabled - skip if so
        if (isToggleEnabled(index)) {
            logger.info("[Fan][EmailNotification] Toggle at index {} already enabled, skipping", index);
            return;
        }
        clickToggle(index);
        page.waitForTimeout(1000); // Wait for toggle state to update
        logger.info("[Fan][EmailNotification] Toggle at index {} enabled (simple click)", index);
    }

    /**
     * Force disable toggle - click regardless of current state, handle confirmation if it appears.
     */
    @Step("Force disable toggle at index {index}")
    public void forceDisableToggle(int index) {
        clickToggle(index);
        // Check if disable confirmation appears
        if (safeIsVisible(disableConfirmationDialog())) {
            clickYesDisable();
            logger.info("[Fan][EmailNotification] Toggle at index {} force disabled with confirmation", index);
        } else {
            // Toggle was already disabled, clicking it enabled it - need to click again
            page.waitForTimeout(500);
            clickToggle(index);
            if (safeIsVisible(disableConfirmationDialog())) {
                clickYesDisable();
            }
            logger.info("[Fan][EmailNotification] Toggle at index {} force disabled (was already off)", index);
        }
    }

    /**
     * Force enable toggle - click regardless of current state.
     */
    @Step("Force enable toggle at index {index}")
    public void forceEnableToggle(int index) {
        clickToggle(index);
        // If disable confirmation appears, we clicked an enabled toggle - cancel and it's already enabled
        if (safeIsVisible(disableConfirmationDialog())) {
            // Click outside or press escape to cancel
            page.keyboard().press("Escape");
            page.waitForTimeout(500);
            logger.info("[Fan][EmailNotification] Toggle at index {} already enabled", index);
        } else {
            // Toggle was disabled, now enabled
            page.waitForTimeout(1000);
            logger.info("[Fan][EmailNotification] Toggle at index {} force enabled", index);
        }
    }
}

