package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page Object for Creator Free Subscription settings.
 * Flow: Profile → Settings → Profile settings → Free subscription toggle
 */
public class CreatorFreeSubscriptionPage extends BasePage {

    public CreatorFreeSubscriptionPage(Page page) {
        super(page);
    }

    // ===== Locators =====

    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator profileSettingsText() {
        return page.getByText("Profile settings");
    }

    private Locator freeSubscriptionText() {
        return page.getByText("Free subscription");
    }

    private Locator freeSubscriptionToggle() {
        return page.getByRole(AriaRole.SWITCH).nth(1);
    }

    private Locator featuredCollectionText() {
        return page.getByText("Featured collection");
    }

    private Locator featuredCollectionToggle() {
        return page.getByRole(AriaRole.SWITCH).first();
    }

    private Locator registerButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register"));
    }

    private Locator updatedPersonalInfoToast() {
        return page.getByText("Updated Personal Information");
    }

    // ===== Actions & Asserts =====

    @Step("Navigate to Creator Profile via URL")
    public void navigateToProfile() {
        navigateAndWait("https://stg.twizz.app/creator/profile");
        page.waitForURL("**/creator/profile**", new Page.WaitForURLOptions().setTimeout(DEFAULT_WAIT));
        logger.info("[FreeSubscription] Navigated to creator profile");
    }

    @Step("Click on Settings icon from profile")
    public void clickSettingsIcon() {
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 1, 150);
        logger.info("[FreeSubscription] Clicked settings icon");
    }

    @Step("Scroll to 'Profile settings' and click")
    public void clickProfileSettings() {
        Locator profileSettings = profileSettingsText();
        try {
            profileSettings.scrollIntoViewIfNeeded();
        } catch (Throwable ignored) {}
        waitVisible(profileSettings, DEFAULT_WAIT);
        clickWithRetry(profileSettings, 1, 150);
        logger.info("[FreeSubscription] Clicked 'Profile settings'");
    }

    @Step("Assert 'Free subscription' field is visible")
    public void assertFreeSubscriptionVisible() {
        waitVisible(freeSubscriptionText(), DEFAULT_WAIT);
        logger.info("[FreeSubscription] 'Free subscription' field is visible");
    }

    @Step("Enable Free subscription toggle")
    public void enableFreeSubscriptionToggle() {
        Locator toggle = freeSubscriptionToggle();
        waitVisible(toggle, DEFAULT_WAIT);
        String checkedBefore = toggle.getAttribute("aria-checked");
        logger.info("[FreeSubscription] Free subscription toggle state before click: aria-checked={}", checkedBefore);
        clickWithRetry(toggle, 1, 150);
        page.waitForTimeout(1000);
        String checkedAfter = toggle.getAttribute("aria-checked");
        logger.info("[FreeSubscription] Free subscription toggle state after click: aria-checked={}", checkedAfter);
        if (!"true".equals(checkedAfter)) {
            logger.warn("[FreeSubscription] Toggle may not have enabled, retrying click");
            clickWithRetry(toggle, 1, 300);
            page.waitForTimeout(1000);
            checkedAfter = toggle.getAttribute("aria-checked");
            logger.info("[FreeSubscription] Free subscription toggle state after retry: aria-checked={}", checkedAfter);
        }
    }

    @Step("Assert 'Featured collection' field is visible")
    public void assertFeaturedCollectionVisible() {
        waitVisible(featuredCollectionText(), DEFAULT_WAIT);
        logger.info("[FreeSubscription] 'Featured collection' field is visible");
    }

    @Step("Enable Featured collection toggle")
    public void enableFeaturedCollectionToggle() {
        Locator toggle = featuredCollectionToggle();
        waitVisible(toggle, DEFAULT_WAIT);
        String checkedBefore = toggle.getAttribute("aria-checked");
        logger.info("[FreeSubscription] Featured collection toggle state before click: aria-checked={}", checkedBefore);
        if ("true".equals(checkedBefore)) {
            logger.info("[FreeSubscription] Featured collection toggle already enabled, skipping");
            return;
        }
        clickWithRetry(toggle, 1, 150);
        page.waitForTimeout(1000);
        String checkedAfter = toggle.getAttribute("aria-checked");
        logger.info("[FreeSubscription] Featured collection toggle state after click: aria-checked={}", checkedAfter);
    }

    @Step("Disable Free subscription toggle")
    public void disableFreeSubscriptionToggle() {
        Locator toggle = freeSubscriptionToggle();
        waitVisible(toggle, DEFAULT_WAIT);
        String checkedBefore = toggle.getAttribute("aria-checked");
        logger.info("[FreeSubscription] Free subscription toggle state before disable: aria-checked={}", checkedBefore);
        if ("false".equals(checkedBefore)) {
            logger.info("[FreeSubscription] Free subscription toggle already disabled, skipping");
            return;
        }
        clickWithRetry(toggle, 1, 150);
        page.waitForTimeout(1000);
        String checkedAfter = toggle.getAttribute("aria-checked");
        logger.info("[FreeSubscription] Free subscription toggle state after disable: aria-checked={}", checkedAfter);
        if (!"false".equals(checkedAfter)) {
            logger.warn("[FreeSubscription] Toggle may not have disabled, retrying click");
            clickWithRetry(toggle, 1, 300);
            page.waitForTimeout(1000);
            checkedAfter = toggle.getAttribute("aria-checked");
            logger.info("[FreeSubscription] Free subscription toggle state after retry: aria-checked={}", checkedAfter);
        }
    }

    @Step("Disable Featured collection toggle")
    public void disableFeaturedCollectionToggle() {
        Locator toggle = featuredCollectionToggle();
        waitVisible(toggle, DEFAULT_WAIT);
        String checkedBefore = toggle.getAttribute("aria-checked");
        logger.info("[FreeSubscription] Featured collection toggle state before disable: aria-checked={}", checkedBefore);
        if ("false".equals(checkedBefore)) {
            logger.info("[FreeSubscription] Featured collection toggle already disabled, skipping");
            return;
        }
        clickWithRetry(toggle, 1, 150);
        page.waitForTimeout(1000);
        String checkedAfter = toggle.getAttribute("aria-checked");
        logger.info("[FreeSubscription] Featured collection toggle state after disable: aria-checked={}", checkedAfter);
    }

    @Step("Click 'Register' button to update profile settings")
    public void clickRegister() {
        Locator btn = registerButton();
        try {
            btn.scrollIntoViewIfNeeded();
        } catch (Throwable ignored) {}
        waitVisible(btn, DEFAULT_WAIT);
        clickWithRetry(btn, 1, 150);
        logger.info("[FreeSubscription] Clicked 'Register' button");
    }

    @Step("Assert 'Updated Personal Information' success toast is visible")
    public void assertUpdatedPersonalInfoToast() {
        waitVisible(updatedPersonalInfoToast(), ConfigReader.getVisibilityTimeout());
        logger.info("[FreeSubscription] 'Updated Personal Information' success toast visible");
        // Dismiss toast if clickable
        try { clickWithRetry(updatedPersonalInfoToast(), 0, 0); } catch (Throwable ignored) {}
    }
}
