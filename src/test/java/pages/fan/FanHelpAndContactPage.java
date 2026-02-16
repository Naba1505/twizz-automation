package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for Fan -> Settings -> Help and contact
 */
public class FanHelpAndContactPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanHelpAndContactPage.class);
    private static final String SETTINGS_URL_PART = "/common/setting";

    public FanHelpAndContactPage(Page page) {
        super(page);
    }

    // ================= Locators =================

    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon"));
    }

    private Locator helpAndContactMenuText() {
        return page.getByText("Help and contact");
    }

    private Locator helpAndContactTitle() {
        return page.getByText("Help and contact");
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
        return page.getByText("Your message has been sent").first();
    }

    // ================= Navigation =================

    @Step("Click Settings icon from Fan home")
    public void clickSettingsIcon() {
        waitVisible(settingsIcon(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(settingsIcon(), 2, 200);
        logger.info("[Fan][HelpAndContact] Clicked Settings icon");
    }

    @Step("Assert on Settings screen")
    public void assertOnSettingsScreen() {
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        logger.info("[Fan][HelpAndContact] On Settings screen - URL: {}", page.url());
    }

    @Step("Scroll to and click 'Help and contact' menu item")
    public void clickHelpAndContactMenu() {
        Locator menuItem = helpAndContactMenuText();
        // Scroll to make it visible if needed
        for (int i = 0; i < 5 && !safeIsVisible(menuItem); i++) {
            page.mouse().wheel(0, 300);
            page.waitForTimeout(200);
        }
        waitVisible(menuItem, ConfigReader.getVisibilityTimeout());
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, 200);
        logger.info("[Fan][HelpAndContact] Clicked 'Help and contact' menu item");
    }

    @Step("Assert on Help and Contact screen")
    public void assertOnHelpAndContactScreen() {
        waitVisible(helpAndContactTitle(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][HelpAndContact] On Help and Contact screen - title visible");
    }

    // ================= Form Interactions =================

    @Step("Assert Subject field heading visible")
    public void assertSubjectHeadingVisible() {
        waitVisible(subjectHeading(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][HelpAndContact] Subject heading visible");
    }

    @Step("Fill Subject field with: {subject}")
    public void fillSubject(String subject) {
        waitVisible(subjectTextbox(), ConfigReader.getVisibilityTimeout());
        subjectTextbox().click();
        subjectTextbox().fill(subject);
        logger.info("[Fan][HelpAndContact] Filled Subject: {}", subject);
    }

    @Step("Assert Description field heading visible")
    public void assertDescriptionHeadingVisible() {
        waitVisible(descriptionHeading(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][HelpAndContact] Description heading visible");
    }

    @Step("Fill Description/Message field with: {message}")
    public void fillMessage(String message) {
        waitVisible(messageTextbox(), ConfigReader.getVisibilityTimeout());
        messageTextbox().click();
        messageTextbox().fill(message);
        logger.info("[Fan][HelpAndContact] Filled Description: {}", message);
    }

    @Step("Click Send button")
    public void clickSendButton() {
        waitVisible(sendButton(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(sendButton(), 2, 200);
        logger.info("[Fan][HelpAndContact] Clicked Send button");
    }

    @Step("Assert success message 'Your message has been sent' is displayed")
    public void assertSuccessMessageVisible() {
        waitVisible(successToast(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][HelpAndContact] Success message displayed: 'Your message has been sent'");
    }

    // ================= Complete Flow =================

    /**
     * Navigate from Fan home to Help and Contact screen.
     */
    @Step("Navigate to Help and Contact from Fan home")
    public void navigateToHelpAndContact() {
        clickSettingsIcon();
        assertOnSettingsScreen();
        clickHelpAndContactMenu();
        assertOnHelpAndContactScreen();
        logger.info("[Fan][HelpAndContact] Successfully navigated to Help and Contact screen");
    }

    /**
     * Submit help and contact form with subject and message.
     * Appends timestamp for reference.
     * 
     * @param subjectBase Base subject text (timestamp will be appended)
     * @param messageBase Base message text (timestamp will be appended)
     */
    @Step("Submit Help and Contact form")
    public void submitHelpAndContactForm(String subjectBase, String messageBase) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String subject = subjectBase + " - " + timestamp;
        String message = messageBase + " - " + timestamp;

        assertSubjectHeadingVisible();
        fillSubject(subject);

        assertDescriptionHeadingVisible();
        fillMessage(message);

        clickSendButton();
        assertSuccessMessageVisible();

        logger.info("[Fan][HelpAndContact] Form submitted successfully with timestamp: {}", timestamp);
    }
}

