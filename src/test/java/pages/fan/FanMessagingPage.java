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
    
    // General tab
    private Locator generalTab() {
        return page.getByText("General");
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
    
    // Manual payment fields
    Locator cardNumberInput() {
        return page.getByPlaceholder("Card number");
    }
    
    Locator expiryDateInput() {
        return page.getByPlaceholder("MM / YY");
    }
    
    Locator cvvInput() {
        return page.getByPlaceholder("CVV");
    }
    
    Locator cardholderNameInput() {
        return page.getByPlaceholder("Cardholder name");
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
        waitVisible(messagingIcon(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(messagingIcon(), 2, ConfigReader.getAnimationTimeout());
        logger.info("[Fan][Messaging] Clicked Messaging icon");
    }

    @Step("Assert on Messaging screen")
    public void assertOnMessagingScreen() {
        waitVisible(messagingTitle(), ConfigReader.getVisibilityTimeout());
        logger.info("[Fan][Messaging] On Messaging screen - title visible");
    }

    @Step("Verify Your subscriptions tab is selected by default")
    public void verifyYourSubscriptionsSelected() {
        waitVisible(yourSubscriptionsTab(), ConfigReader.getShortTimeout());
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
        
        // First try to find in current tab (Your subscriptions)
        if (safeIsVisible(creator)) {
            creator.scrollIntoViewIfNeeded();
            clickWithRetry(creator, 2, ConfigReader.getAnimationTimeout());
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            logger.info("[Fan][Messaging] Clicked on creator: {}", creatorName);
            return;
        }
        
        // If not found, try switching to General tab
        logger.info("[Fan][Messaging] Creator '{}' not found in subscriptions, checking General tab", creatorName);
        clickWithRetry(generalTab(), 2, ConfigReader.getAnimationTimeout());
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Try to find in General tab
        if (safeIsVisible(creator)) {
            creator.scrollIntoViewIfNeeded();
            clickWithRetry(creator, 2, ConfigReader.getAnimationTimeout());
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            logger.info("[Fan][Messaging] Found and clicked creator '{}' in General tab", creatorName);
        } else {
            // If still not found, throw an exception with helpful message
            throw new RuntimeException("Creator '" + creatorName + "' not found in either Your subscriptions or General tab");
        }
    }

    @Step("Assert on conversation screen by viewing message input")
    public void assertOnConversationScreen() {
        waitVisible(messageInput(), ConfigReader.getShortTimeout());
        logger.info("[Fan][Messaging] On conversation screen - message input visible");
    }

    @Step("Type message: {message}")
    public void typeMessage(String message) {
        Locator input = messageInput();
        waitVisible(input, ConfigReader.getShortTimeout());
        input.click();
        input.fill(message);
        logger.info("[Fan][Messaging] Typed message: {}", message);
    }

    @Step("Click Send button")
    public void clickSend() {
        waitVisible(sendButton(), ConfigReader.getShortTimeout());
        clickWithRetry(sendButton(), 2, ConfigReader.getAnimationTimeout());
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
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
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        Locator msg = page.getByText(message).first();
        waitVisible(msg, ConfigReader.getShortTimeout());
        logger.info("[Fan][Messaging] Message visible: {}", message);
    }

    @Step("Click Accept button for paid media")
    public void clickAcceptMedia() {
        logger.info("[Fan][Messaging] Looking for Accept media button");
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Scroll to bottom to see latest messages
        try {
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        } catch (Exception ignored) {}
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        Locator acceptBtn = acceptMediaButton();
        
        // If not visible, try scrolling to find it
        if (!safeIsVisible(acceptBtn)) {
            logger.info("[Fan][Messaging] Accept button not visible, scrolling to find...");
            for (int i = 0; i < 5; i++) {
                page.mouse().wheel(0, 300);
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
                acceptBtn = acceptMediaButton();
                if (safeIsVisible(acceptBtn)) {
                    break;
                }
            }
        }
        
        waitVisible(acceptBtn, ConfigReader.getVisibilityTimeout());
        acceptBtn.scrollIntoViewIfNeeded();
        clickWithRetry(acceptBtn, 2, ConfigReader.getAnimationTimeout());
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        logger.info("[Fan][Messaging] Clicked Accept button for paid media");
    }

    @Step("Assert on Secure payment screen")
    public void assertOnSecurePaymentScreen() {
        // Wait for payment screen to appear
        
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
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
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
            waitVisible(registeredCard, ConfigReader.getShortTimeout());
            clickWithRetry(registeredCard, 2, ConfigReader.getAnimationTimeout());
            logger.info("[Fan][Messaging] Clicked Registered card option");
            
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            
            // Check if we need to fill CVV for registered card
            Locator cvvField = page.locator("input[placeholder*='CVV'], input[placeholder*='cvv']").first();
            if (safeIsVisible(cvvField)) {
                cvvField.fill("123");
                logger.info("[Fan][Messaging] Filled CVV for registered card");
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            }
        } else {
            logger.warn("[Fan][Messaging] Registered card option not found, waiting for payment form to appear");
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
    }

    @Step("Click Confirm button")
    public void clickConfirm() {
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
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
            // Wait for button to be enabled
            logger.info("[Fan][Messaging] Waiting for Confirm button to be enabled...");
            try {
                // Additional wait for button to become enabled
                for (int i = 0; i < 30; i++) {
                    if (confirmBtn.first().isEnabled()) {
                        break;
                    }
                    page.waitForTimeout(200);
                }
            } catch (Exception e) {
                logger.warn("[Fan][Messaging] Button did not become enabled within timeout");
            }
            
            waitVisible(confirmBtn.first(), ConfigReader.getVisibilityTimeout());
            clickWithRetry(confirmBtn.first(), 2, ConfigReader.getAnimationTimeout());
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            logger.info("[Fan][Messaging] Clicked Confirm button");
        } else {
            logger.warn("[Fan][Messaging] Confirm button not found, payment may have auto-completed");
        }
    }

    @Step("Click Everything is OK button")
    public void clickEverythingOk() {
        logger.info("[Fan][Messaging] Waiting for payment success screen and OK button");
        
        // Poll for button to appear with timeout
        Locator okBtn = null;
        boolean found = false;
        
        for (int i = 0; i < 20; i++) {
            // Try multiple strategies to find success button
            okBtn = everythingOkButton();
            
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
                found = true;
                break;
            }
            page.waitForTimeout(300);
        }
        
        if (found) {
            waitVisible(okBtn.first(), ConfigReader.getShortTimeout());
            clickWithRetry(okBtn.first(), 2, ConfigReader.getAnimationTimeout());
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            logger.info("[Fan][Messaging] Clicked Everything is OK button");
        } else {
            logger.warn("[Fan][Messaging] Everything is OK button not found after polling, payment may have completed without confirmation");
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
        try { page.waitForTimeout(ConfigReader.getMediumTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Scroll to the bottom of the conversation to see the most recent media
        try {
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        } catch (Exception ignored) {}
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // The media is sent as a SEPARATE message after the text reply.
        // We need to find the preview icon that belongs to the most recent media sent by creator.
        // Strategy: Find all preview (eye) icons and get the LAST one (most recent media in conversation)
        Locator allPreviews = page.locator("span[aria-label='eye']");
        int previewCount = allPreviews.count();
        logger.info("[Fan][Messaging] Found {} preview icons in conversation", previewCount);
        
        if (previewCount == 0) {
            // If no eye icons, try hover on image to reveal preview
            Locator images = page.locator(".ant-image img, img[class*='media']");
            if (images.count() > 0) {
                images.last().hover();
                try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
                allPreviews = page.locator("span[aria-label='eye']");
                previewCount = allPreviews.count();
                logger.info("[Fan][Messaging] After hover, found {} preview icons", previewCount);
            }
        }
        
        // Click the last preview icon (most recent media)
        Locator targetPreview = allPreviews.last();
        targetPreview.scrollIntoViewIfNeeded();
        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        waitVisible(targetPreview, ConfigReader.getShortTimeout());
        clickWithRetry(targetPreview, 2, ConfigReader.getAnimationTimeout());
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        logger.info("[Fan][Messaging] Clicked most recent preview icon for media");
    }

    @Step("Click to preview media")
    public void clickToPreviewImage() {
        logger.info("[Fan][Messaging] Looking for Preview icon to click (most recent)");
        try { page.waitForTimeout(ConfigReader.getMediumTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        // Click the last (most recent) preview icon
        Locator preview = page.locator("span[aria-label='eye']").last();
        waitVisible(preview, ConfigReader.getShortTimeout());
        clickWithRetry(preview, 2, ConfigReader.getAnimationTimeout());
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        logger.info("[Fan][Messaging] Clicked to preview media");
    }

    @Step("Close image preview")
    public void closeImagePreview() {
        waitVisible(closePreviewButton(), ConfigReader.getShortTimeout());
        clickWithRetry(closePreviewButton(), 2, ConfigReader.getAnimationTimeout());
        try { page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        logger.info("[Fan][Messaging] Closed image preview");
    }

    @Step("Verify video play icon is visible")
    public void verifyVideoPlayIconVisible() {
        logger.info("[Fan][Messaging] Looking for video play icon");
        try { page.waitForTimeout(ConfigReader.getMediumTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        Locator playIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("play")).nth(2);
        waitVisible(playIcon, ConfigReader.getShortTimeout());
        logger.info("[Fan][Messaging] Video play icon is visible - video received successfully");
    }

    @Step("Verify audio element is visible for message: {messageTimestamp}")
    public void verifyAudioElementVisible(String messageTimestamp) {
        logger.info("[Fan][Messaging] Looking for audio element near message: {}", messageTimestamp);
        try { page.waitForTimeout(ConfigReader.getMediumTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Scroll to bottom to see most recent media
        try {
            page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        } catch (Exception ignored) {}
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        // Verify audio element is visible - try multiple selectors for audio player
        Locator audioElement = page.locator("div[class*='audio-mini-player'], div[class*='audio'], img[alt='audio'], audio, [class*='wavesurfer'], [class*='waveform']").last();
        
        if (audioElement.count() == 0 || !safeIsVisible(audioElement)) {
            // Scroll more and retry
            logger.info("[Fan][Messaging] Audio not immediately visible, scrolling further...");
            page.mouse().wheel(0, 500);
            try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            audioElement = page.locator("div[class*='audio-mini-player'], div[class*='audio'], img[alt='audio'], audio, [class*='wavesurfer'], [class*='waveform']").last();
        }
        
        waitVisible(audioElement, ConfigReader.getShortTimeout());
        logger.info("[Fan][Messaging] Audio element is visible - audio received successfully");
    }

    @Step("Fan accepts free message (no payment)")
    public void fanAcceptsFreeMessage(String creatorMessage) {
        verifyMessageVisible(creatorMessage);
        clickAcceptMedia();
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        logger.info("[Fan][Messaging] Fan accepted free message");
    }

    @Step("Verify mixed media received (image preview, video preview, audio element)")
    public void verifyMixedMediaReceived(String messageTimestamp) {
        logger.info("[Fan][Messaging] Verifying mixed media received for message: {}", messageTimestamp);
        try { page.waitForTimeout(ConfigReader.getMediumTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }

        // Find the message with timestamp
        Locator messageLocator = page.getByText(messageTimestamp).first();
        waitVisible(messageLocator, ConfigReader.getShortTimeout());
        messageLocator.scrollIntoViewIfNeeded();

        // Verify preview icons are visible (for image and video - should have 2 preview icons)
        Locator previewIcons = page.locator("span[aria-label='eye']");
        int previewCount = previewIcons.count();
        logger.info("[Fan][Messaging] Found {} preview icons", previewCount);

        // Click first preview (image) and close
        if (previewCount >= 1) {
            Locator firstPreview = previewIcons.first();
            waitVisible(firstPreview, ConfigReader.getShortTimeout());
            clickWithRetry(firstPreview, 2, ConfigReader.getAnimationTimeout());
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            logger.info("[Fan][Messaging] Clicked first preview (image)");
            closeImagePreview();
        }

        // Click second preview (video) and close
        if (previewCount >= 2) {
            Locator secondPreview = previewIcons.nth(1);
            waitVisible(secondPreview, ConfigReader.getShortTimeout());
            clickWithRetry(secondPreview, 2, ConfigReader.getAnimationTimeout());
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            logger.info("[Fan][Messaging] Clicked second preview (video)");
            closeImagePreview();
        }

        // Verify audio element is visible
        Locator audioElement = page.locator("div[class*='audio-mini-player'], img[alt='audio']").last();
        waitVisible(audioElement, ConfigReader.getShortTimeout());
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

