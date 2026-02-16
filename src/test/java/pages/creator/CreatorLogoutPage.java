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
        waitVisible(settingsIcon(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(settingsIcon(), 1, 150);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Click 'Disconnect' to logout")
    public void clickDisconnect() {
        waitVisible(disconnectMenu(), ConfigReader.getVisibilityTimeout());
        try { disconnectMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(disconnectMenu(), 1, 150);
    }

    @Step("Assert user is logged out and on intro screen (Twizz logo visible)")
    public void assertLoggedOutToIntro() {
        waitVisible(twizzLogoOnIntro(), ConfigReader.getVisibilityTimeout());
    }
}
