package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

/**
 * Page Object for Creator-side actions in the Private Media Subscription flow.
 * Creator logs in, opens messaging, accepts fan request, sets amount, and sends price.
 */
public class CreatorPrivateMediaPage extends BasePage {

    public CreatorPrivateMediaPage(Page page) {
        super(page);
    }

    // ===== Locators =====

    private Locator messagingIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Messaging icon"));
    }

    private Locator acceptButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept"));
    }

    private Locator amountText() {
        return page.getByText("Amount", new Page.GetByTextOptions().setExact(true));
    }

    private Locator customAmountInput() {
        return page.locator("input[name=\"customAmount\"]");
    }

    private Locator creatorMessageBox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your message..."));
    }

    private Locator sendButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send"));
    }

    private Locator pendingText() {
        return page.getByText("Pending");
    }

    // ===== Actions =====

    @Step("Click Messaging icon")
    public void clickMessagingIcon() {
        Locator icon = messagingIcon();
        waitVisible(icon, ConfigReader.getVisibilityTimeout());
        clickWithRetry(icon, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[CreatorPrivMedia] Clicked Messaging icon");
    }

    @Step("Click on received message: {messageText}")
    public void clickReceivedMessage(String messageText) {
        Locator msg = page.getByText(messageText, new Page.GetByTextOptions().setExact(true));
        waitVisible(msg.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(msg.first(), 1, 150);
        page.waitForTimeout(2000);
        logger.info("[CreatorPrivMedia] Clicked on received message: {}", messageText);
    }

    @Step("Click Accept button on fan request")
    public void clickAccept() {
        Locator btn = acceptButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[CreatorPrivMedia] Clicked Accept button");
    }

    @Step("Assert Amount popup is displayed")
    public void assertAmountPopupVisible() {
        waitVisible(amountText(), ConfigReader.getShortTimeout());
        logger.info("[CreatorPrivMedia] Amount popup is displayed");
    }

    @Step("Set custom amount to: {amount}")
    public void setCustomAmount(String amount) {
        Locator input = customAmountInput();
        waitVisible(input, ConfigReader.getShortTimeout());
        input.click();
        input.fill(amount);
        logger.info("[CreatorPrivMedia] Set custom amount to: {}", amount);
    }

    @Step("Type reply message: {message}")
    public void typeReplyMessage(String message) {
        Locator msgBox = creatorMessageBox();
        waitVisible(msgBox, ConfigReader.getShortTimeout());
        msgBox.click();
        msgBox.fill(message);
        logger.info("[CreatorPrivMedia] Typed reply message: {}", message);
    }

    @Step("Click Send button")
    public void clickSend() {
        Locator btn = sendButton();
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, 150);
        page.waitForTimeout(2000);
        logger.info("[CreatorPrivMedia] Clicked Send button");
    }

    @Step("Assert 'Pending' text is displayed")
    public void assertPendingVisible() {
        waitVisible(pendingText(), ConfigReader.getShortTimeout());
        logger.info("[CreatorPrivMedia] 'Pending' text is displayed - price sent to fan");
    }
}
