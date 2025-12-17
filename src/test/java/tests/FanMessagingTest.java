package tests;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorMessagingPage;
import pages.FanLoginPage;
import pages.FanMessagingPage;
import utils.BrowserFactory;
import utils.ConfigReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Test class for Fan-Creator Messaging functionality.
 * Tests complete messaging flow between fan and creator including:
 * - Fan sends message to creator
 * - Creator accepts message, sets price, and replies
 * - Fan accepts paid message and completes payment
 * - Creator sends media to fan
 * - Fan views media from creator
 */
@Epic("Fan")
@Feature("Messaging")
public class FanMessagingTest extends BaseTestClass {

    private static final Logger logger = LoggerFactory.getLogger(FanMessagingTest.class);

    // Fan credentials
    private String fanUsername;
    private String fanPassword;
    private String fanDisplayName = "Paul Lewis";

    // Creator credentials
    private String creatorUsername;
    private String creatorPassword;
    private String creatorDisplayName = "Smith";

    // Message content (will be appended with timestamp for uniqueness)
    private String fanMessage;
    private String creatorReply;
    private static final String PRICE = "15€";

    // Media path
    private static final String MEDIA_PATH = "src/test/resources/Images/MessageImageMediaA.jpg";

    /**
     * Load credentials from config and generate unique messages with timestamp.
     */
    private void loadCredentials() {
        fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        creatorUsername = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        creatorPassword = ConfigReader.getProperty("creator.password", "Twizz$123");
        
        // Generate unique messages with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        fanMessage = "Hi Creator " + timestamp;
        creatorReply = "Hi Fan " + timestamp;
        
        logger.info("[FanMessaging] Loaded credentials - Fan: {}, Creator: {}", fanUsername, creatorUsername);
        logger.info("[FanMessaging] Messages - Fan: '{}', Creator: '{}'", fanMessage, creatorReply);
    }

    /**
     * Complete Fan-Creator Messaging Flow
     * 
     * Flow:
     * Step 1: Fan login → Navigate to Messaging → Send message to creator (keep fan open)
     * Step 2: Creator login → Accept message → Set price → Reply (keep creator open)
     * Step 3: Fan accepts paid message → Complete payment
     * Step 4: Creator navigates to To Deliver → Sends media to fan
     * Step 5: Fan views media from creator
     */
    @Story("Complete Fan-Creator messaging flow with payment and media")
    @Test(priority = 1, description = "Complete messaging flow: fan sends, creator replies with price, fan pays, creator sends media")
    public void completeMessagingFlow() {
        // Load credentials
        loadCredentials();

        logger.info("[FanMessaging] Starting complete messaging flow test");

        // Use main page for fan
        Page fanPage = page;
        BrowserContext creatorContext = null;
        Page creatorPage = null;

        try {
            // ==================== STEP 1: FAN SENDS MESSAGE ====================
            logger.info("[FanMessaging] Step 1: Fan login and send message to creator");

            // Fan login
            FanLoginPage fanLogin = new FanLoginPage(fanPage);
            fanLogin.navigate();
            Assert.assertTrue(fanLogin.isLoginFormVisible(), "Fan login form not visible");
            fanLogin.login(fanUsername, fanPassword);
            logger.info("[FanMessaging] Fan logged in successfully");

            // Fan navigates to messaging and sends message
            FanMessagingPage fanMessaging = new FanMessagingPage(fanPage);
            fanMessaging.navigateToMessaging();
            fanMessaging.clickOnCreatorConversation(creatorDisplayName);
            fanMessaging.sendMessageToCreator(fanMessage);
            logger.info("[FanMessaging] Fan sent message: '{}'", fanMessage);

            // ==================== STEP 2: CREATOR ACCEPTS AND REPLIES ====================
            logger.info("[FanMessaging] Step 2: Creator login, accept message, set price, and reply");

            // Create separate browser context for creator
            creatorContext = BrowserFactory.createNewContext();
            creatorPage = creatorContext.newPage();
            logger.info("[FanMessaging] Created separate browser context for creator");

            // Creator login
            CreatorLoginPage creatorLogin = new CreatorLoginPage(creatorPage);
            creatorLogin.navigate();
            Assert.assertTrue(creatorLogin.isLoginFormVisible(), "Creator login form not visible");
            creatorLogin.login(creatorUsername, creatorPassword);
            logger.info("[FanMessaging] Creator logged in successfully");

            // Creator navigates to messaging
            CreatorMessagingPage creatorMessaging = new CreatorMessagingPage(creatorPage);
            creatorMessaging.openMessagingFromProfile();
            creatorMessaging.verifyGeneralTabSelected();
            logger.info("[FanMessaging] Creator on Messaging screen");

            // Creator clicks on fan conversation
            creatorMessaging.clickOnFanConversation(fanDisplayName);
            logger.info("[FanMessaging] Creator opened conversation with fan");

            // Creator accepts message, sets price, and replies
            creatorMessaging.acceptFanMessageAndReply(fanMessage, PRICE, creatorReply);
            logger.info("[FanMessaging] Creator accepted message and replied with price {}", PRICE);

            // ==================== STEP 3: FAN ACCEPTS PAID MESSAGE ====================
            logger.info("[FanMessaging] Step 3: Fan accepts paid message and completes payment");

            // Reload fan page to see creator's reply
            fanPage.reload();
            fanPage.waitForTimeout(3000);
            logger.info("[FanMessaging] Fan page reloaded");

            // Verify creator's reply is visible and accept
            fanMessaging.verifyMessageVisible(creatorReply);
            logger.info("[FanMessaging] Creator reply visible on fan screen");
            
            fanMessaging.clickAcceptMedia();
            fanMessaging.completePaymentForMedia();
            logger.info("[FanMessaging] Fan accepted and paid for message");

            // ==================== STEP 4: CREATOR SENDS MEDIA ====================
            logger.info("[FanMessaging] Step 4: Creator sends media to fan");

            // Navigate creator to home/profile first, then to messaging
            creatorPage.navigate("https://stg.twizz.app/creator/profile");
            creatorPage.waitForTimeout(3000);
            logger.info("[FanMessaging] Creator navigated to profile");

            // Navigate to messaging screen on creator
            creatorMessaging.openMessagingFromProfile();
            logger.info("[FanMessaging] Creator back on Messaging screen");

            // Get media path
            Path mediaPath = Paths.get(MEDIA_PATH).toAbsolutePath();
            logger.info("[FanMessaging] Using media path: {}", mediaPath);

            // Creator sends media to fan
            creatorMessaging.sendMediaToFan(fanDisplayName, mediaPath);
            logger.info("[FanMessaging] Creator sent media to fan");

            // ==================== STEP 5: FAN VIEWS MEDIA ====================
            logger.info("[FanMessaging] Step 5: Fan views media from creator");

            // Reload fan page to see new media
            fanPage.reload();
            fanPage.waitForTimeout(3000);

            // Fan views media
            fanMessaging.verifyMessageVisible(creatorReply);
            fanMessaging.clickToPreviewImage();
            fanMessaging.closeImagePreview();
            logger.info("[FanMessaging] Fan viewed media from creator");

            logger.info("[FanMessaging] Test completed successfully: Full messaging flow verified");

        } finally {
            // Close creator context
            if (creatorContext != null) {
                try {
                    creatorContext.close();
                    logger.info("[FanMessaging] Creator browser context closed");
                } catch (Exception e) {
                    logger.warn("[FanMessaging] Error closing creator context: {}", e.getMessage());
                }
            }
        }
    }
}
