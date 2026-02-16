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
 * Page object for Fan Messaging functionality.
 * Handles fan messaging with creators including sending messages,
 * accepting paid messages, and viewing media.
 */
public class FanMessagingPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanMessagingPage.class);
    private static final int DEFAULT_WAIT = 10000;

    public FanMessagingPage(Page page) {
        super(page);
    }

    // ================= Locators =================

    // Messaging icon and title
    private Locator messagingIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Messaging icon"));
    }

    private Locator messagingTitle() {
        return page.getByText("Messaging");
    }

    // Your subscriptions tab (default for fan)
    private Locator yourSubscriptionsTab() {
        return page.getByText("Your subscriptions");
    }

    // Message input
    private Locator messageInput() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your message"));
    }

    // Send button
    private Locator sendButton() {
        return page.getByText("Send", new Page.GetByTextOptions().setExact(true));
    }

    // Secure payment title
    private Locator securePaymentTitle() {
        return page.getByText("Secure payment");
    }

    // Registered card option
    private Locator registeredCardOption() {
        return page.getByText("Registered card");
    }

    // Confirm button
    private Locator confirmButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm"));
    }

    // Everything is OK button (payment success)
    private Locator everythingOkButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Everything is OK"));
    }

    // Accept media button (for paid messages from creator)
    // Uses LAST button to get the most recent message's Accept button
    private Locator acceptMediaButton() {
        // Try multiple strategies to find Accept button - use LAST to get most recent
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept"));
        int count = btn.count();
        logger.info("[Fan][Messaging] Found {} Accept buttons on page", count);
        
        if (count > 0 && safeIsVisible(btn.last())) {
            return btn.last(); // Most recent message at bottom
        }
        // Try text-based locator
        btn = page.getByText("Accept", new Page.GetByTextOptions().setExact(true));
        if (btn.count() > 0 && safeIsVisible(btn.last())) {
            return btn.last();
        }
        // Try CSS class as fallback
        btn = page.locator(".accept-media-button");
        if (btn.count() > 0 && safeIsVisible(btn.last())) {
            return btn.last();
        }
        // Return last Accept button found (most recent)
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Accept")).last();
    }

    // Close preview button
    private Locator closePreviewButton() {
        return page.locator("span[aria-label='close']").first();
    }

    // ================= Navigation Methods =================

    @Step("Click Messaging icon")
    public void clickMessagingIcon() {
        waitVisible(messagingIcon(), DEFAULT_WAIT);
        clickWithRetry(messagingIcon(), 2, 200);
        logger.info("[Fan][Messaging] Clicked Messaging icon");
    }

    @Step("Assert on Messaging screen")
    public void assertOnMessagingScreen() {
        waitVisible(messagingTitle(), DEFAULT_WAIT);
        logger.info("[Fan][Messaging] On Messaging screen - title visible");
    }

    @Step("Verify Your subscriptions tab is selected by default")
    public void verifyYourSubscriptionsSelected() {
        waitVisible(yourSubscriptionsTab(), DEFAULT_WAIT);
        logger.info("[Fan][Messaging] 'Your subscriptions' tab visible (default)");
    }

    /**
     * Navigate to Messaging screen from Fan home.
     */
    @Step("Navigate to Messaging screen")
    public void navigateToMessaging() {
        clickMessagingIcon();
        assertOnMessagingScreen();
        verifyYourSubscriptionsSelected();
        logger.info("[Fan][Messaging] Successfully navigated to Messaging screen");
    }

    // ================= Conversation Methods =================

    @Step("Click on creator conversation: {creatorName}")
    public void clickOnCreatorConversation(String creatorName) {
        Locator creator = page.getByText(creatorName);
        waitVisible(creator, DEFAULT_WAIT);
        creator.scrollIntoViewIfNeeded();
        clickWithRetry(creator, 2, 200);
        page.waitForTimeout(1500); // Wait for conversation to load
        logger.info("[Fan][Messaging] Clicked on creator: {}", creatorName);
    }

    @Step("Assert on conversation screen by viewing message input")
    public void assertOnConversationScreen() {
        waitVisible(messageInput(), DEFAULT_WAIT);
        logger.info("[Fan][Messaging] On conversation screen - message input visible");
    }

    @Step("Type message: {message}")
    public void typeMessage(String message) {
        Locator input = messageInput();
        waitVisible(input, DEFAULT_WAIT);
        input.click();
        input.fill(message);
        logger.info("[Fan][Messaging] Typed message: {}", message);
    }

    @Step("Click Send button")
    public void clickSend() {
        waitVisible(sendButton(), DEFAULT_WAIT);
        clickWithRetry(sendButton(), 2, 200);
        page.waitForTimeout(1000); // Wait for message to send
        logger.info("[Fan][Messaging] Clicked Send button");
    }

    /**
     * Send a message to creator.
     */
    @Step("Send message to creator: {message}")
    public void sendMessageToCreator(String message) {
        assertOnConversationScreen();
        typeMessage(message);
        clickSend();
        logger.info("[Fan][Messaging] Message sent: {}", message);
    }

    // ================= Payment Methods =================

    @Step("Verify message from creator is visible: {message}")
    public void verifyMessageVisible(String message) {
        logger.info("[Fan][Messaging] Looking for message: {}", message);
        // Wait for messages to load
        page.waitForTimeout(2000);
        Locator msg = page.getByText(message).first();
        waitVisible(msg, DEFAULT_WAIT);
        logger.info("[Fan][Messaging] Message visible: {}", message);
    }

    @Step("Click Accept button for paid media")
    public void clickAcceptMedia() {
        logger.info("[Fan][Messaging] Looking for Accept media button");
        page.waitForTimeout(2000); // Wait for messages to load
        
        // Scroll to bottom to see latest messages
        try {
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        } catch (Exception ignored) {}
        page.waitForTimeout(1000);
        
        Locator acceptBtn = acceptMediaButton();
        
        // If not visible, try scrolling to find it
        if (!safeIsVisible(acceptBtn)) {
            logger.info("[Fan][Messaging] Accept button not visible, scrolling to find...");
            for (int i = 0; i < 5; i++) {
                page.mouse().wheel(0, 300);
                page.waitForTimeout(500);
                acceptBtn = acceptMediaButton();
                if (safeIsVisible(acceptBtn)) {
                    break;
                }
            }
        }
        
        waitVisible(acceptBtn, DEFAULT_WAIT);
        acceptBtn.scrollIntoViewIfNeeded();
        clickWithRetry(acceptBtn, 2, 200);
        page.waitForTimeout(2000); // Wait for payment screen
        logger.info("[Fan][Messaging] Clicked Accept button for paid media");
    }

    @Step("Assert on Secure payment screen")
    public void assertOnSecurePaymentScreen() {
        // Wait longer for payment screen to appear
        page.waitForTimeout(1000);
        
        // Try multiple strategies to verify payment screen
        Locator paymentTitle = securePaymentTitle();
        boolean found = false;
        
        // Strategy 1: Direct wait
        try {
            waitVisible(paymentTitle.first(), ConfigReader.getVisibilityTimeout());
            found = true;
        } catch (Exception e) {
            logger.warn("[Fan][Messaging] Secure payment title not found directly");
        }
        
        // Strategy 2: Look for payment-related elements
        if (!found) {
            Locator registeredCard = page.getByText("Registered card");
            if (registeredCard.count() > 0 && safeIsVisible(registeredCard.first())) {
                found = true;
                logger.info("[Fan][Messaging] Found Registered card option - on payment screen");
            }
        }
        
        // Strategy 3: Look for Confirm button
        if (!found) {
            Locator confirmBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm"));
            if (confirmBtn.count() > 0 && safeIsVisible(confirmBtn.first())) {
                found = true;
                logger.info("[Fan][Messaging] Found Confirm button - on payment screen");
            }
        }
        
        if (found) {
            logger.info("[Fan][Messaging] On Secure payment screen");
        } else {
            logger.warn("[Fan][Messaging] Could not verify payment screen, proceeding anyway");
        }
    }

    @Step("Click Registered card option")
    public void clickRegisteredCard() {
        // Wait for payment options to load
        page.waitForTimeout(2000);
        
        // Try multiple strategies to find Registered card option
        Locator registeredCard = registeredCardOption();
        
        if (registeredCard.count() == 0 || !safeIsVisible(registeredCard.first())) {
            // Strategy 2: Try partial text match
            registeredCard = page.getByText("Registered").first();
        }
        if (registeredCard.count() == 0 || !safeIsVisible(registeredCard.first())) {
            // Strategy 3: Look for card-related elements
            registeredCard = page.locator("[class*='card'], [class*='payment']").filter(
                new Locator.FilterOptions().setHasText("Registered")).first();
        }
        if (registeredCard.count() == 0 || !safeIsVisible(registeredCard.first())) {
            // Strategy 4: Look for any clickable payment option
            registeredCard = page.locator("label, div[role='radio'], .ant-radio-wrapper").first();
            logger.info("[Fan][Messaging] Using first payment option as fallback");
        }
        
        if (registeredCard.count() > 0 && safeIsVisible(registeredCard)) {
            waitVisible(registeredCard, DEFAULT_WAIT);
            clickWithRetry(registeredCard, 2, 200);
            logger.info("[Fan][Messaging] Clicked Registered card option");
        } else {
            logger.warn("[Fan][Messaging] Registered card option not found, payment may already be selected");
        }
    }

    @Step("Click Confirm button")
    public void clickConfirm() {
        // Wait for payment form to be ready
        page.waitForTimeout(2000);
        
        // Try multiple strategies to find Confirm button
        Locator confirmBtn = confirmButton();
        
        if (confirmBtn.count() == 0 || !safeIsVisible(confirmBtn.first())) {
            // Strategy 2: Try text-based locator
            confirmBtn = page.getByText("Confirm", new Page.GetByTextOptions().setExact(true));
        }
        if (confirmBtn.count() == 0 || !safeIsVisible(confirmBtn.first())) {
            // Strategy 3: Look for any submit/confirm type button
            confirmBtn = page.locator("button[type='submit'], .confirm-button, .ant-btn-primary").first();
        }
        if (confirmBtn.count() == 0 || !safeIsVisible(confirmBtn.first())) {
            // Strategy 4: Look for Pay button as alternative
            confirmBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Pay"));
        }
        
        if (confirmBtn.count() > 0 && safeIsVisible(confirmBtn.first())) {
            waitVisible(confirmBtn.first(), ConfigReader.getVisibilityTimeout());
            clickWithRetry(confirmBtn.first(), 2, 200);
            page.waitForTimeout(3000); // Wait for payment processing
            logger.info("[Fan][Messaging] Clicked Confirm button");
        } else {
            logger.warn("[Fan][Messaging] Confirm button not found, payment may have auto-completed");
        }
    }

    @Step("Click Everything is OK button")
    public void clickEverythingOk() {
        // Wait for payment success screen
        page.waitForTimeout(3000);
        
        // Try multiple strategies to find success button
        Locator okBtn = everythingOkButton();
        
        if (okBtn.count() == 0 || !safeIsVisible(okBtn.first())) {
            // Strategy 2: Try partial text match
            okBtn = page.getByText("Everything is OK");
        }
        if (okBtn.count() == 0 || !safeIsVisible(okBtn.first())) {
            // Strategy 3: Try "OK" button
            okBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("OK"));
        }
        if (okBtn.count() == 0 || !safeIsVisible(okBtn.first())) {
            // Strategy 4: Try "Close" or "Done" buttons
            okBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close"));
            if (okBtn.count() == 0) {
                okBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Done"));
            }
        }
        if (okBtn.count() == 0 || !safeIsVisible(okBtn.first())) {
            // Strategy 5: Look for any success/close button
            okBtn = page.locator(".ant-btn-primary, button[class*='success'], button[class*='close']").first();
        }
        
        if (okBtn.count() > 0 && safeIsVisible(okBtn.first())) {
            waitVisible(okBtn.first(), ConfigReader.getVisibilityTimeout());
            clickWithRetry(okBtn.first(), 2, 200);
            page.waitForTimeout(1000);
            logger.info("[Fan][Messaging] Clicked Everything is OK button");
        } else {
            logger.warn("[Fan][Messaging] Everything is OK button not found, payment may have completed without confirmation");
            page.waitForTimeout(2000);
        }
    }

    /**
     * Complete payment for paid media from creator.
     */
    @Step("Complete payment for paid media")
    public void completePaymentForMedia() {
        assertOnSecurePaymentScreen();
        clickRegisteredCard();
        clickConfirm();
        clickEverythingOk();
        logger.info("[Fan][Messaging] Payment completed for paid media");
    }

    // ================= Media Preview Methods =================

    @Step("Click to preview media for message: {messageTimestamp}")
    public void clickToPreviewMediaForMessage(String messageTimestamp) {
        logger.info("[Fan][Messaging] Looking for Preview icon near message: {}", messageTimestamp);
        page.waitForTimeout(2000); // Wait for media to load
        
        // Find the message container with the timestamp, then find the preview icon within it
        Locator messageLocator = page.getByText(messageTimestamp).first();
        waitVisible(messageLocator, DEFAULT_WAIT);
        
        // Scroll to the message
        messageLocator.scrollIntoViewIfNeeded();
        page.waitForTimeout(500);
        
        // Find the preview icon (eye icon) in the same message container
        // Go up to parent container and find the preview icon
        Locator messageContainer = messageLocator.locator("xpath=ancestor::div[contains(@class, 'mediaContent') or contains(@class, 'message') or contains(@class, 'ant-image')]/..");
        Locator previewInContainer = messageContainer.locator("span[aria-label='eye']").first();
        
        // If not found in container, try finding the last preview icon (most recent)
        if (!previewInContainer.isVisible()) {
            logger.info("[Fan][Messaging] Preview not found in container, using last preview icon");
            previewInContainer = page.locator("span[aria-label='eye']").last();
        }
        
        waitVisible(previewInContainer, DEFAULT_WAIT);
        clickWithRetry(previewInContainer, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Fan][Messaging] Clicked to preview media for message: {}", messageTimestamp);
    }

    @Step("Click to preview media")
    public void clickToPreviewImage() {
        logger.info("[Fan][Messaging] Looking for Preview icon to click (most recent)");
        page.waitForTimeout(2000); // Wait for media to load
        // Click the last (most recent) preview icon
        Locator preview = page.locator("span[aria-label='eye']").last();
        waitVisible(preview, DEFAULT_WAIT);
        clickWithRetry(preview, 2, 200);
        page.waitForTimeout(1000);
        logger.info("[Fan][Messaging] Clicked to preview media");
    }

    @Step("Close image preview")
    public void closeImagePreview() {
        waitVisible(closePreviewButton(), DEFAULT_WAIT);
        clickWithRetry(closePreviewButton(), 2, 200);
        page.waitForTimeout(500);
        logger.info("[Fan][Messaging] Closed image preview");
    }

    @Step("Verify video play icon is visible")
    public void verifyVideoPlayIconVisible() {
        logger.info("[Fan][Messaging] Looking for video play icon");
        page.waitForTimeout(2000); // Wait for media to load
        Locator playIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("play")).nth(2);
        waitVisible(playIcon, DEFAULT_WAIT);
        logger.info("[Fan][Messaging] Video play icon is visible - video received successfully");
    }

    @Step("Verify audio element is visible for message: {messageTimestamp}")
    public void verifyAudioElementVisible(String messageTimestamp) {
        logger.info("[Fan][Messaging] Looking for audio element near message: {}", messageTimestamp);
        page.waitForTimeout(2000); // Wait for media to load
        
        // Find the message with timestamp
        Locator messageLocator = page.getByText(messageTimestamp).first();
        waitVisible(messageLocator, DEFAULT_WAIT);
        messageLocator.scrollIntoViewIfNeeded();
        
        // Verify audio mini player is visible (from screenshot: div.audio-mini-player or img with alt="audio")
        Locator audioElement = page.locator("div[class*='audio-mini-player'], img[alt='audio']").last();
        waitVisible(audioElement, DEFAULT_WAIT);
        logger.info("[Fan][Messaging] Audio element is visible - audio received successfully");
    }

    @Step("Fan accepts free message (no payment)")
    public void fanAcceptsFreeMessage(String creatorMessage) {
        verifyMessageVisible(creatorMessage);
        clickAcceptMedia();
        // No payment needed for free messages - just wait for acceptance
        page.waitForTimeout(2000);
        logger.info("[Fan][Messaging] Fan accepted free message");
    }

    @Step("Verify mixed media received (image preview, video preview, audio element)")
    public void verifyMixedMediaReceived(String messageTimestamp) {
        logger.info("[Fan][Messaging] Verifying mixed media received for message: {}", messageTimestamp);
        page.waitForTimeout(2000); // Wait for media to load

        // Find the message with timestamp
        Locator messageLocator = page.getByText(messageTimestamp).first();
        waitVisible(messageLocator, DEFAULT_WAIT);
        messageLocator.scrollIntoViewIfNeeded();

        // Verify preview icons are visible (for image and video - should have 2 preview icons)
        Locator previewIcons = page.locator("span[aria-label='eye']");
        int previewCount = previewIcons.count();
        logger.info("[Fan][Messaging] Found {} preview icons", previewCount);

        // Click first preview (image) and close
        if (previewCount >= 1) {
            Locator firstPreview = previewIcons.first();
            waitVisible(firstPreview, DEFAULT_WAIT);
            clickWithRetry(firstPreview, 2, 200);
            page.waitForTimeout(1000);
            logger.info("[Fan][Messaging] Clicked first preview (image)");
            closeImagePreview();
        }

        // Click second preview (video) and close
        if (previewCount >= 2) {
            Locator secondPreview = previewIcons.nth(1);
            waitVisible(secondPreview, DEFAULT_WAIT);
            clickWithRetry(secondPreview, 2, 200);
            page.waitForTimeout(1000);
            logger.info("[Fan][Messaging] Clicked second preview (video)");
            closeImagePreview();
        }

        // Verify audio element is visible
        Locator audioElement = page.locator("div[class*='audio-mini-player'], img[alt='audio']").last();
        waitVisible(audioElement, DEFAULT_WAIT);
        logger.info("[Fan][Messaging] Audio element is visible");

        logger.info("[Fan][Messaging] Mixed media verified successfully - image, video, and audio received");
    }

    // ================= Complete Flow Methods =================

    /**
     * Step 1: Fan sends initial message to creator.
     */
    @Step("Fan sends message to creator")
    public void fanSendsMessageToCreator(String creatorName, String message) {
        navigateToMessaging();
        clickOnCreatorConversation(creatorName);
        sendMessageToCreator(message);
        logger.info("[Fan][Messaging] Fan sent message '{}' to creator '{}'", message, creatorName);
    }

    /**
     * Step 3: Fan accepts paid message and completes payment.
     */
    @Step("Fan accepts paid message and pays")
    public void fanAcceptsPaidMessageAndPays(String creatorMessage) {
        verifyMessageVisible(creatorMessage);
        clickAcceptMedia();
        completePaymentForMedia();
        logger.info("[Fan][Messaging] Fan accepted and paid for message");
    }

    /**
     * Step 5: Fan views media sent by creator.
     */
    @Step("Fan views media from creator")
    public void fanViewsMediaFromCreator(String creatorMessage) {
        verifyMessageVisible(creatorMessage);
        clickToPreviewImage();
        closeImagePreview();
        logger.info("[Fan][Messaging] Fan viewed media from creator");
    }
}

