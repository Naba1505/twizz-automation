package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page object for Creator -> Settings -> Help and contact
 */
public class CreatorHelpAndContactPage extends BasePage {
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    // Reduced from DEFAULT_WAIT (60000ms) to SHORT_TIMEOUT (1000ms) = 98% faster!
    private static final int BUTTON_RETRY_DELAY = 150;   // Button click retry delay
    private static final int SHORT_TIMEOUT = 1000;       // Short waits (was 60000ms)
    private static final int MEDIUM_TIMEOUT = 2000;      // Medium waits (was 20000ms)

    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorHelpAndContactPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator helpAndContactMenu() {
        return page.getByText("Help and contact");
    }

    private Locator helpAndContactTitle() {
        return page.getByText("Help and contact");
    }

    private Locator subjectTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Subject"));
    }

    private Locator messageTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your message..."));
    }

    private Locator sendButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send"));
    }

    private Locator successToast() {
        return page.getByText("Your message has been sent");
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Help and contact)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), MEDIUM_TIMEOUT);
        clickWithRetry(settingsIcon(), 1, BUTTON_RETRY_DELAY);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open 'Help and contact' screen")
    public void openHelpAndContact() {
        waitVisible(helpAndContactMenu(), MEDIUM_TIMEOUT);
        try { helpAndContactMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(helpAndContactMenu(), 1, BUTTON_RETRY_DELAY);
        waitVisible(helpAndContactTitle(), SHORT_TIMEOUT);
    }

    @Step("Fill Subject: {subject}")
    public void fillSubject(String subject) {
        waitVisible(subjectTextbox(), SHORT_TIMEOUT);
        subjectTextbox().click();
        subjectTextbox().fill(subject == null ? "" : subject);
    }

    @Step("Fill Message: {message}")
    public void fillMessage(String message) {
        waitVisible(messageTextbox(), SHORT_TIMEOUT);
        messageTextbox().click();
        messageTextbox().fill(message == null ? "" : message);
    }

    @Step("Click Send button")
    public void clickSend() {
        waitVisible(sendButton(), SHORT_TIMEOUT);
        clickWithRetry(sendButton(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Assert success toast is visible")
    public void assertSuccessToastVisible() {
        waitVisible(successToast(), MEDIUM_TIMEOUT);
    }
}
