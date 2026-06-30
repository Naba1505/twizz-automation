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
    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorHelpAndContactPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator helpAndContactMenuItem() {
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
        // Ensure we are on the profile page before looking for the settings icon
        navigateAndWait(ConfigReader.getBaseUrl() + "/creator/profile");
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
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

    @Step("Open 'Help and contact' screen")
    public void openHelpAndContact() {
        waitVisible(helpAndContactMenuItem(), ConfigReader.getShortTimeout());
        try { helpAndContactMenuItem().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(helpAndContactMenuItem(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(helpAndContactMenuItem(), ConfigReader.getShortTimeout());
    }

    @Step("Fill Subject: {subject}")
    public void fillSubject(String subject) {
        waitVisible(subjectTextbox(), ConfigReader.getShortTimeout());
        subjectTextbox().click();
        subjectTextbox().fill(subject == null ? "" : subject);
    }

    @Step("Fill Message: {message}")
    public void fillMessage(String message) {
        waitVisible(messageTextbox(), ConfigReader.getShortTimeout());
        messageTextbox().click();
        messageTextbox().fill(message == null ? "" : message);
    }

    @Step("Click Send button")
    public void clickSend() {
        waitVisible(sendButton(), ConfigReader.getShortTimeout());
        clickWithRetry(sendButton(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert success toast is visible")
    public void assertSuccessToastVisible() {
        waitVisible(successToast(), ConfigReader.getMediumTimeout());
    }
}
