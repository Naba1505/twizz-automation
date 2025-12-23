package pages.fan;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for Fan -> Settings -> I've spotted a bug
 */
public class FanSpottedBugPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanSpottedBugPage.class);

    public FanSpottedBugPage(Page page) {
        super(page);
    }

    // ================= Locators =================

    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon"));
    }

    private Locator settingsTitle() {
        return page.getByText("Settings");
    }

    private Locator spottedBugMenuText() {
        return page.getByText("I've spotted a bug");
    }

    private Locator spottedBugTitle() {
        return page.getByText("I've spotted a bug");
    }

    private Locator subjectHeading() {
        return page.getByText("Subject");
    }

    private Locator subjectTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Subject"));
    }

    private Locator descriptionHeading() {
        return page.getByText("Description");
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

    // ================= Navigation =================

    @Step("Click Settings icon from Fan home")
    public void clickSettingsIcon() {
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 2, 200);
        logger.info("[Fan][SpottedBug] Clicked Settings icon");
    }

    @Step("Assert on Settings screen by viewing title")
    public void assertOnSettingsScreen() {
        waitVisible(settingsTitle(), DEFAULT_WAIT);
        logger.info("[Fan][SpottedBug] On Settings screen - title visible");
    }

    @Step("Scroll to and click 'I've spotted a bug' menu item")
    public void clickSpottedBugMenu() {
        Locator menuItem = spottedBugMenuText();
        // Scroll to make it visible if needed
        for (int i = 0; i < 5 && !safeIsVisible(menuItem); i++) {
            page.mouse().wheel(0, 300);
            page.waitForTimeout(200);
        }
        waitVisible(menuItem, DEFAULT_WAIT);
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, 200);
        logger.info("[Fan][SpottedBug] Clicked 'I've spotted a bug' menu item");
    }

    @Step("Assert on 'I've spotted a bug' screen by viewing title")
    public void assertOnSpottedBugScreen() {
        waitVisible(spottedBugTitle(), DEFAULT_WAIT);
        logger.info("[Fan][SpottedBug] On 'I've spotted a bug' screen - title visible");
    }

    // ================= Form Interactions =================

    @Step("Assert Subject field heading visible")
    public void assertSubjectHeadingVisible() {
        waitVisible(subjectHeading(), DEFAULT_WAIT);
        logger.info("[Fan][SpottedBug] Subject heading visible");
    }

    @Step("Fill Subject field with: {subject}")
    public void fillSubject(String subject) {
        waitVisible(subjectTextbox(), DEFAULT_WAIT);
        subjectTextbox().click();
        subjectTextbox().fill(subject);
        logger.info("[Fan][SpottedBug] Filled Subject: {}", subject);
    }

    @Step("Assert Description field heading visible")
    public void assertDescriptionHeadingVisible() {
        waitVisible(descriptionHeading(), DEFAULT_WAIT);
        logger.info("[Fan][SpottedBug] Description heading visible");
    }

    @Step("Fill Description/Message field with: {message}")
    public void fillMessage(String message) {
        waitVisible(messageTextbox(), DEFAULT_WAIT);
        messageTextbox().click();
        messageTextbox().fill(message);
        logger.info("[Fan][SpottedBug] Filled Description: {}", message);
    }

    @Step("Click Send button")
    public void clickSendButton() {
        waitVisible(sendButton(), DEFAULT_WAIT);
        clickWithRetry(sendButton(), 2, 200);
        logger.info("[Fan][SpottedBug] Clicked Send button");
    }

    @Step("Assert success message 'Your message has been sent' is displayed")
    public void assertSuccessMessageVisible() {
        waitVisible(successToast(), 30_000);
        logger.info("[Fan][SpottedBug] Success message displayed: 'Your message has been sent'");
    }

    // ================= Complete Flow =================

    /**
     * Navigate from Fan home to 'I've spotted a bug' screen.
     */
    @Step("Navigate to 'I've spotted a bug' from Fan home")
    public void navigateToSpottedBug() {
        clickSettingsIcon();
        assertOnSettingsScreen();
        clickSpottedBugMenu();
        assertOnSpottedBugScreen();
        logger.info("[Fan][SpottedBug] Successfully navigated to 'I've spotted a bug' screen");
    }

    /**
     * Submit bug report form with subject and message.
     * Appends timestamp for reference.
     * 
     * @param subjectBase Base subject text (timestamp will be appended)
     * @param messageBase Base message text (timestamp will be appended)
     */
    @Step("Submit bug report form")
    public void submitBugReportForm(String subjectBase, String messageBase) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String subject = subjectBase + " - " + timestamp;
        String message = messageBase + " - " + timestamp;

        assertSubjectHeadingVisible();
        fillSubject(subject);

        assertDescriptionHeadingVisible();
        fillMessage(message);

        clickSendButton();
        assertSuccessMessageVisible();

        logger.info("[Fan][SpottedBug] Bug report submitted successfully with timestamp: {}", timestamp);
    }
}

