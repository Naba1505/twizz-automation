package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

/**
 * Page object for Fan Live Events functionality.
 * Handles navigation to lives screen, joining live events, payment, and interaction.
 */
public class FanLivePage extends BasePage {

    // Navigation
    private static final String LIVE_ICON_NAME = "Live icon";
    private static final String LIVES_TITLE = "Lives";
    private static final String LIVE_TEXT_EXACT = "Live";

    // Live interaction
    private static final String GO_TO_LIVE_BTN = "Go to live";
    private static final String SELECT_BTN = "Select";
    private static final String CONFIRM_BTN = "Confirm";
    private static final String EVERYTHING_OK_BTN = "Everything is OK";

    // Chat
    private static final String COMMENT_TEXTBOX = "Comment";
    private static final String SEND_ICON = "send";
    private static final String CLOSE_ICON = "close";

    public FanLivePage(Page page) {
        super(page);
    }

    // ================= Navigation =================

    @Step("Click on Live icon from home screen")
    public void clickLiveIcon() {
        Locator liveIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(LIVE_ICON_NAME));
        waitVisible(liveIcon.first(), 15_000);
        clickWithRetry(liveIcon.first(), 2, 200);
        logger.info("[Fan][Live] Clicked on Live icon");
    }

    @Step("Ensure on Lives screen by verifying title")
    public void assertOnLivesScreen() {
        Locator livesTitle = page.getByText(LIVES_TITLE);
        waitVisible(livesTitle.first(), 15_000);
        logger.info("[Fan][Live] On Lives screen - title visible");
    }

    @Step("Click on Live tab")
    public void clickLiveTab() {
        Locator liveTab = page.getByText(LIVE_TEXT_EXACT, new Page.GetByTextOptions().setExact(true));
        waitVisible(liveTab.first(), 10_000);
        clickWithRetry(liveTab.first(), 2, 200);
        logger.info("[Fan][Live] Clicked on Live tab");
    }

    @Step("Click on Events tab")
    public void clickEventsTab() {
        Locator eventsTab = page.getByText("Events", new Page.GetByTextOptions().setExact(true));
        waitVisible(eventsTab.first(), 10_000);
        clickWithRetry(eventsTab.first(), 2, 200);
        page.waitForTimeout(2000); // Wait for events to load
        logger.info("[Fan][Live] Clicked on Events tab");
    }

    @Step("Navigate to Lives screen from home")
    public void navigateToLivesScreen() {
        clickLiveIcon();
        assertOnLivesScreen();
        clickLiveTab();
        logger.info("[Fan][Live] Successfully navigated to Lives screen");
    }

    // ================= Live Tile Interaction =================

    @Step("Verify creator name '{creatorName}' is displayed on live tile")
    public void assertCreatorOnLiveTile(String creatorName) {
        Locator creatorText = page.getByText(creatorName).first();
        waitVisible(creatorText, 15_000);
        logger.info("[Fan][Live] Creator '{}' visible on live tile", creatorName);
    }

    @Step("Click 'Go to live' button")
    public void clickGoToLive() {
        Locator goToLiveBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(GO_TO_LIVE_BTN)).first();
        waitVisible(goToLiveBtn, 15_000);
        clickWithRetry(goToLiveBtn, 2, 200);
        logger.info("[Fan][Live] Clicked 'Go to live' button");
    }

    // ================= Payment Flow =================

    @Step("Select payment card")
    public void selectPaymentCard() {
        Locator selectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(SELECT_BTN));
        waitVisible(selectBtn.first(), 15_000);
        clickWithRetry(selectBtn.first(), 2, 200);
        logger.info("[Fan][Live] Selected payment card");
    }

    @Step("Confirm payment")
    public void confirmPayment() {
        Locator confirmBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONFIRM_BTN));
        waitVisible(confirmBtn.first(), 15_000);
        clickWithRetry(confirmBtn.first(), 2, 200);
        logger.info("[Fan][Live] Confirmed payment");
    }

    @Step("Click 'Everything is OK' if displayed")
    public void clickEverythingOkIfPresent() {
        try {
            Locator okBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(EVERYTHING_OK_BTN));
            if (waitVisibleSafe(okBtn.first(), 10_000)) {
                clickWithRetry(okBtn.first(), 2, 200);
                logger.info("[Fan][Live] Clicked 'Everything is OK' - payment confirmed for live access");
            }
        } catch (Exception e) {
            logger.info("[Fan][Live] 'Everything is OK' button not displayed, continuing...");
        }
    }

    @Step("Complete payment flow for live access")
    public void completePaymentForLive() {
        selectPaymentCard();
        confirmPayment();
        clickEverythingOkIfPresent();
        logger.info("[Fan][Live] Payment flow completed for live access");
    }

    // ================= Live Chat Interaction =================

    @Step("Click on comment textbox")
    public void clickCommentTextbox() {
        Locator commentBox = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(COMMENT_TEXTBOX));
        waitVisible(commentBox.first(), 15_000);
        clickWithRetry(commentBox.first(), 2, 200);
        logger.info("[Fan][Live] Clicked on comment textbox");
    }

    @Step("Type comment: {message}")
    public void typeComment(String message) {
        Locator commentBox = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName(COMMENT_TEXTBOX));
        waitVisible(commentBox.first(), 10_000);
        commentBox.first().fill(message);
        logger.info("[Fan][Live] Typed comment: {}", message);
    }

    @Step("Send comment")
    public void sendComment() {
        Locator sendIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SEND_ICON));
        waitVisible(sendIcon.first(), 10_000);
        clickWithRetry(sendIcon.first(), 2, 200);
        logger.info("[Fan][Live] Sent comment");
    }

    @Step("Post comment in live chat: {message}")
    public void postComment(String message) {
        clickCommentTextbox();
        typeComment(message);
        sendComment();
        logger.info("[Fan][Live] Posted comment: {}", message);
    }

    // ================= Close Live =================

    @Step("Close live stream (fan side)")
    public void closeLive() {
        Locator closeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(CLOSE_ICON));
        waitVisible(closeIcon.first(), 10_000);
        clickWithRetry(closeIcon.first(), 2, 200);
        logger.info("[Fan][Live] Closed live stream");
    }

    // ================= Scheduled Live / Ticket Purchase =================

    private static final String GET_TICKET_BTN = "Get a ticket";
    private static final String SECURE_PAYMENT_TEXT = "Secure payment";
    private static final String EXCLUSIVE_LIVE_TEXT = "For an exclusive live show.";

    @Step("Click on creator tile by name: {creatorName}")
    public void clickCreatorTile(String creatorName) {
        Locator creatorTile = page.getByText(creatorName);
        waitVisible(creatorTile.first(), 15_000);
        clickWithRetry(creatorTile.first(), 2, 200);
        logger.info("[Fan][Live] Clicked on creator tile: {}", creatorName);
    }

    @Step("Verify exclusive live show text is displayed")
    public void assertExclusiveLiveTextVisible() {
        Locator exclusiveText = page.getByText(EXCLUSIVE_LIVE_TEXT);
        waitVisible(exclusiveText.first(), 10_000);
        logger.info("[Fan][Live] Exclusive live show text visible");
    }

    @Step("Click 'Get a ticket' button")
    public void clickGetTicket() {
        Locator getTicketBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(GET_TICKET_BTN));
        waitVisible(getTicketBtn.first(), 15_000);
        clickWithRetry(getTicketBtn.first(), 2, 200);
        logger.info("[Fan][Live] Clicked 'Get a ticket' button");
    }

    @Step("Verify secure payment screen is displayed")
    public void assertSecurePaymentVisible() {
        Locator securePayment = page.getByText(SECURE_PAYMENT_TEXT);
        waitVisible(securePayment.first(), 15_000);
        logger.info("[Fan][Live] Secure payment screen visible");
    }

    @Step("Complete ticket payment flow")
    public void completeTicketPayment() {
        assertSecurePaymentVisible();
        selectPaymentCard();
        confirmPayment();
        clickEverythingOkIfPresent();
        logger.info("[Fan][Live] Ticket payment completed");
    }

    @Step("Buy ticket for scheduled live event - creator: {creatorName}")
    public void buyTicketForScheduledLive(String creatorName) {
        clickEventsTab(); // Switch to Events tab for scheduled lives
        clickCreatorTile(creatorName);
        assertExclusiveLiveTextVisible();
        clickGetTicket();
        completeTicketPayment();
        logger.info("[Fan][Live] Successfully purchased ticket for scheduled live by: {}", creatorName);
    }

    // ================= Helper Methods =================

    /**
     * Safe wait for visibility that returns boolean instead of throwing.
     */
    private boolean waitVisibleSafe(Locator locator, long timeoutMs) {
        try {
            waitVisible(locator, timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Join a live event - complete flow from lives screen.
     * Assumes fan is already on Lives screen.
     */
    @Step("Join live event for creator: {creatorName}")
    public void joinLiveEvent(String creatorName) {
        assertCreatorOnLiveTile(creatorName);
        clickGoToLive();
        completePaymentForLive();
        logger.info("[Fan][Live] Successfully joined live event for creator: {}", creatorName);
    }

    /**
     * Full flow: Navigate to lives, join live, interact, and close.
     */
    @Step("Complete live event flow - navigate, join, comment, close")
    public void completeLiveEventFlow(String creatorName, String comment) {
        navigateToLivesScreen();
        joinLiveEvent(creatorName);
        postComment(comment);
        closeLive();
        logger.info("[Fan][Live] Completed full live event flow");
    }
}
