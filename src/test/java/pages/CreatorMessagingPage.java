package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.SkipException;

/**
 * Page Object for Creator Messaging flows.
 * Assumes caller has already logged in as Creator and is on the profile/home screen.
 */
public class CreatorMessagingPage extends BasePage {

    public CreatorMessagingPage(Page page) {
        super(page);
    }

    private Locator messagingIcon() {
        // IMG with accessible name "Messaging icon"
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Messaging icon"));
    }

    private Locator messagingTitle() {
        return page.getByText("Messaging");
    }

    private Locator fanAvatarStack() {
        // Container that holds existing fan message profile icons
        return page.locator("div.FanAvatarWrapper.w-72");
    }

    private Locator messageInput() {
        return page.getByPlaceholder("Your message");
    }

    private Locator sendButton() {
        return page.getByText("Send");
    }

    private Locator quickAnswerIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("quick answer"));
    }

    private Locator savedResponsesTitle() {
        return page.getByText("Saved responses");
    }

    private Locator savedResponseIcon() {
        // Click the saved response item by icon
        return page.locator(".ant-col > img");
    }

    @Step("Open Messaging from profile and verify landing on Messaging screen")
    public void openMessagingFromProfile() {
        logger.info("[Messaging] Opening Messaging from profile");
        // Ensure icon visible then click with retry
        waitVisible(messagingIcon(), DEFAULT_WAIT);
        clickWithRetry(messagingIcon(), 1, 200);
        // Verify Messaging title is visible
        waitVisible(messagingTitle(), DEFAULT_WAIT);
        logger.info("[Messaging] Landed on Messaging screen");
    }

    @Step("Open first fan conversation from the Messaging list")
    public void openFirstFanConversation() {
        logger.info("[Messaging] Opening first fan conversation");
        Locator stack = fanAvatarStack();
        // Wait briefly for any conversation to appear
        int attempts = 0;
        int count = stack.count();
        while (count == 0 && attempts < 5) { // ~1s total wait
            page.waitForTimeout(200);
            attempts++;
            count = stack.count();
        }
        if (count == 0) {
            logger.warn("[Messaging] No existing fan conversations available");
            throw new SkipException("No existing fan conversations available to open.");
        }
        // Click first conversation
        clickWithRetry(stack.first(), 1, 200);
        // Verify we are on the conversation screen by the message input placeholder
        waitVisible(messageInput(), DEFAULT_WAIT);
        logger.info("[Messaging] Conversation screen visible");
    }

    @Step("Send normal text message: {message}")
    public void sendTextMessage(String message) {
        logger.info("[Messaging] Sending text message: '{}'", message);
        waitVisible(messageInput(), DEFAULT_WAIT);
        messageInput().click();
        messageInput().fill(message);
        clickWithRetry(sendButton(), 1, 200);
        // Optional small settle
        page.waitForTimeout(300);
        logger.info("[Messaging] Message sent");
    }

    @Step("Open Quick Answers panel")
    public void openQuickAnswers() {
        logger.info("[Messaging] Opening Quick Answers panel");
        waitVisible(quickAnswerIcon(), DEFAULT_WAIT);
        clickWithRetry(quickAnswerIcon(), 1, 200);
    }

    @Step("Assert Saved responses panel is visible")
    public void assertSavedResponsesVisible() {
        logger.info("[Messaging] Asserting 'Saved responses' panel is visible");
        waitVisible(savedResponsesTitle(), DEFAULT_WAIT);
    }

    @Step("Select saved response by text contains: {text}")
    public void selectSavedResponseByText(String text) {
        logger.info("[Messaging] Selecting saved response by text contains: '{}'", text);
        Locator item = page.getByText(text);
        waitVisible(item, DEFAULT_WAIT);
        clickWithRetry(item, 1, 200);
    }

    @Step("Click saved response icon (first)")
    public void clickSavedResponseIcon() {
        logger.info("[Messaging] Clicking saved response icon (first)");
        Locator icon = savedResponseIcon().first();
        try {
            // Try a short, targeted wait for the icon; if not visible, fallback gracefully
            waitVisible(icon, 5000);
            clickWithRetry(icon, 1, 200);
            return;
        } catch (Exception ignored) {
            // Fallback: click on a common saved response label
            logger.warn("[Messaging] Saved response icon not visible; falling back to text-based selection");
        }
        try {
            Locator welcome = page.getByText("Welcome");
            waitVisible(welcome, 5000);
            clickWithRetry(welcome, 1, 200);
            return;
        } catch (Exception ignored) {
            // As a last resort, click any visible saved response container
            logger.warn("[Messaging] Fallback to any saved response container");
        }
        Locator anyItem = page.locator(".ant-col").first();
        waitVisible(anyItem, DEFAULT_WAIT);
        clickWithRetry(anyItem, 1, 200);
    }

    @Step("Click Send")
    public void clickSend() {
        logger.info("[Messaging] Clicking Send button");
        waitVisible(sendButton(), DEFAULT_WAIT);
        clickWithRetry(sendButton(), 1, 200);
        page.waitForTimeout(300);
    }

    @Step("Append to message input: {extra}")
    public void appendToMessage(String extra) {
        logger.info("[Messaging] Appending text to message input: '{}'", extra);
        waitVisible(messageInput(), DEFAULT_WAIT);
        Locator input = messageInput();
        input.click();
        // Append to any prefilled content (e.g., selected quick answer)
        input.pressSequentially(extra);
    }
}
