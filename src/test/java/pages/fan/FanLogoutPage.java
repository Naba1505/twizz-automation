package pages.fan;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for Fan Logout functionality.
 * Handles logout from Settings screen.
 */
public class FanLogoutPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanLogoutPage.class);
    private static final int DEFAULT_WAIT = 10000;

    public FanLogoutPage(Page page) {
        super(page);
    }

    // ================= Locators =================

    // Settings
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon"));
    }

    private Locator settingsTitle() {
        return page.getByText("Settings");
    }

    // Disconnect button
    private Locator disconnectButton() {
        return page.getByText("Disconnect");
    }

    // Login text (visible after logout)
    private Locator loginText() {
        return page.getByText("Login");
    }

    // ================= Navigation Methods =================

    @Step("Click Settings icon")
    public void clickSettingsIcon() {
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 2, 200);
        logger.info("[Fan][Logout] Clicked Settings icon");
    }

    @Step("Assert on Settings screen by viewing title")
    public void assertOnSettingsScreen() {
        waitVisible(settingsTitle(), DEFAULT_WAIT);
        logger.info("[Fan][Logout] On Settings screen - title visible");
    }

    /**
     * Navigate from Fan home to Settings screen.
     */
    @Step("Navigate to Settings from Fan home")
    public void navigateToSettings() {
        clickSettingsIcon();
        assertOnSettingsScreen();
        logger.info("[Fan][Logout] Successfully navigated to Settings screen");
    }

    // ================= Logout Methods =================

    @Step("Click Disconnect button to logout")
    public void clickDisconnect() {
        Locator disconnect = disconnectButton();
        waitVisible(disconnect, DEFAULT_WAIT);
        disconnect.scrollIntoViewIfNeeded();
        clickWithRetry(disconnect, 2, 200);
        page.waitForTimeout(2000); // Wait for logout to complete
        logger.info("[Fan][Logout] Clicked Disconnect button");
    }

    @Step("Verify user is on Login page after logout")
    public void verifyOnLoginPage() {
        waitVisible(loginText(), DEFAULT_WAIT);
        logger.info("[Fan][Logout] Login text visible - user logged out successfully");
    }

    /**
     * Complete logout flow: Click Disconnect and verify Login page.
     */
    @Step("Perform logout and verify")
    public void performLogout() {
        clickDisconnect();
        verifyOnLoginPage();
        logger.info("[Fan][Logout] Logout completed successfully");
    }
}

