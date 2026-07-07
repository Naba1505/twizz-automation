package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page object for Creator -> Settings -> Logout flow
 */
public class CreatorLogoutPage extends BasePage {
    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorLogoutPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator disconnectMenu() {
        return page.getByText("Disconnect");
    }

    private Locator twizzLogoOnIntro() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Twizz"));
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Logout)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Click 'Disconnect' to logout")
    public void clickDisconnect() {
        waitVisible(disconnectMenu(), ConfigReader.getShortTimeout());
        try { disconnectMenu().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(disconnectMenu(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert user is logged out and on intro screen (Twizz logo visible)")
    public void assertLoggedOutToIntro() {
        waitVisible(twizzLogoOnIntro(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Assert current URL contains settings path")
    public void assertOnSettingsUrl() {
        if (!page.url().contains(SETTINGS_URL_PART)) {
            throw new AssertionError("Did not land on Settings screen. URL: " + page.url());
        }
        logger.info("Settings URL confirmed: {}", page.url());
    }
}
