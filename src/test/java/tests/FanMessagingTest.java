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
    private String fanDisplayName;

    // Creator credentials
    private String creatorUsername;
    private String creatorPassword;
    private String creatorDisplayName = "Smith";

    // Message content (will be appended with timestamp for uniqueness)
    private String fanMessage;
    private String creatorReply;
    private static final String PRICE = "15€";

    // Media paths
    private static final String IMAGE_MEDIA_PATH = "src/test/resources/Images/MessageImageMediaA.jpg";
    private static final String VIDEO_MEDIA_PATH = "src/test/resources/Videos/MessageVideoMediaA.mp4";
    private static final String AUDIO_MEDIA_PATH = "src/test/resources/Audios/A3.mp3";

    /**
     * Load credentials from config and generate unique messages with timestamp.
     */
    private void loadCredentials() {
        fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        fanDisplayName = ConfigReader.getProperty("fan.displayname", "Paul Lewis");
        creatorUsername = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        creatorPassword = ConfigReader.getProperty("creator.password", "Twizz$123");
        
        // Generate unique messages with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        fanMessage = "Hi Creator " + timestamp;
        creatorReply = "Hi Fan " + timestamp;
        
        logger.info("[FanMessaging] Loaded credentials - Fan: {}, Creator: {}", fanUsername, creatorUsername);
        logger.info("[FanMessaging] Fan display name: {}", fanDisplayName);
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
            Path mediaPath = Paths.get(IMAGE_MEDIA_PATH).toAbsolutePath();
            logger.info("[FanMessaging] Using media path: {}", mediaPath);

            // Creator sends media to fan
            creatorMessaging.sendMediaToFan(fanDisplayName, mediaPath);
            logger.info("[FanMessaging] Creator sent media to fan");

            // ==================== STEP 5: FAN VIEWS MEDIA ====================
            logger.info("[FanMessaging] Step 5: Fan views media from creator");

            // Reload fan page to see new media
            fanPage.reload();
            fanPage.waitForTimeout(3000);

            // Fan views media - click preview for message with timestamp
            fanMessaging.verifyMessageVisible(creatorReply);
            fanMessaging.clickToPreviewMediaForMessage(creatorReply);
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

    /**
     * Complete Fan-Creator Messaging Flow with Video and Custom Price
     * 
     * Flow:
     * Step 1: Fan login → Navigate to Messaging → Send message to creator (keep fan open)
     * Step 2: Creator login → Accept message → Set CUSTOM price (10) → Reply (keep creator open)
     * Step 3: Fan accepts paid message → Complete payment
     * Step 4: Creator navigates to To Deliver → Sends VIDEO to fan
     * Step 5: Fan views video from creator
     */
    @Story("Complete Fan-Creator messaging flow with custom price and video media")
    @Test(priority = 2, description = "Complete messaging flow: fan sends, creator replies with custom price, fan pays, creator sends video")
    public void completeMessagingFlowWithVideoAndCustomPrice() {
        // Load credentials
        loadCredentials();

        logger.info("[FanMessaging] Starting complete messaging flow test with video and custom price");

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

            // ==================== STEP 2: CREATOR ACCEPTS AND REPLIES WITH CUSTOM PRICE ====================
            logger.info("[FanMessaging] Step 2: Creator login, accept message, set CUSTOM price, and reply");

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

            // Creator accepts message, sets CUSTOM price (10), and replies
            creatorMessaging.acceptFanMessageAndReplyWithCustomPrice(fanMessage, "10", creatorReply);
            logger.info("[FanMessaging] Creator accepted message and replied with custom price 10");

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

            // ==================== STEP 4: CREATOR SENDS VIDEO ====================
            logger.info("[FanMessaging] Step 4: Creator sends VIDEO to fan");

            // Navigate creator to home/profile first, then to messaging
            creatorPage.navigate("https://stg.twizz.app/creator/profile");
            creatorPage.waitForTimeout(3000);
            logger.info("[FanMessaging] Creator navigated to profile");

            // Navigate to messaging screen on creator
            creatorMessaging.openMessagingFromProfile();
            logger.info("[FanMessaging] Creator back on Messaging screen");

            // Get VIDEO media path
            Path videoPath = Paths.get(VIDEO_MEDIA_PATH).toAbsolutePath();
            logger.info("[FanMessaging] Using video path: {}", videoPath);

            // Creator sends video to fan
            creatorMessaging.sendMediaToFan(fanDisplayName, videoPath);
            logger.info("[FanMessaging] Creator sent video to fan");

            // ==================== STEP 5: FAN VIEWS VIDEO ====================
            logger.info("[FanMessaging] Step 5: Fan views video from creator");

            // Reload fan page to see new media
            fanPage.reload();
            fanPage.waitForTimeout(3000);

            // Fan views video - click preview for message with timestamp
            fanMessaging.verifyMessageVisible(creatorReply);
            fanMessaging.clickToPreviewMediaForMessage(creatorReply);
            fanMessaging.closeImagePreview();
            logger.info("[FanMessaging] Fan viewed video from creator");

            logger.info("[FanMessaging] Test completed successfully: Full messaging flow with video and custom price verified");

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

    /**
     * Complete Fan-Creator Messaging Flow with Audio and FREE Price
     * 
     * Step 1: Fan sends message to creator
     * Step 2: Creator accepts message with FREE price and replies
     * Step 3: Fan accepts free message (no payment needed)
     * Step 4: Creator sends audio to fan
     * Step 5: Fan verifies audio received
     */
    @Test(priority = 3, description = "Complete messaging flow: fan sends, creator replies with FREE price, fan accepts, creator sends audio")
    @Story("Fan-Creator Messaging with Audio and Free Price")
    public void completeMessagingFlowWithAudioAndFreePrice() {
        loadCredentials();
        logger.info("[FanMessaging] Starting complete messaging flow test with audio and FREE price");

        // Get fan page from base class
        Page fanPage = page;
        BrowserContext creatorContext = null;
        Page creatorPage = null;

        try {
            // ==================== STEP 1: FAN LOGIN AND SEND MESSAGE ====================
            logger.info("[FanMessaging] Step 1: Fan login and send message to creator");

            // Fan login
            FanLoginPage fanLogin = new FanLoginPage(fanPage);
            fanLogin.navigate();
            fanLogin.login(fanUsername, fanPassword);
            logger.info("[FanMessaging] Fan logged in successfully");

            // Fan sends message to creator
            FanMessagingPage fanMessaging = new FanMessagingPage(fanPage);
            fanMessaging.navigateToMessaging();
            fanMessaging.clickOnCreatorConversation(creatorDisplayName);
            fanMessaging.sendMessageToCreator(fanMessage);
            logger.info("[FanMessaging] Fan sent message: '{}'", fanMessage);

            // ==================== STEP 2: CREATOR LOGIN AND ACCEPT WITH FREE PRICE ====================
            logger.info("[FanMessaging] Step 2: Creator login, accept message with FREE price, and reply");

            // Create separate browser context for creator
            creatorContext = BrowserFactory.createNewContext();
            creatorPage = creatorContext.newPage();
            logger.info("[FanMessaging] Created separate browser context for creator");

            CreatorLoginPage creatorLogin = new CreatorLoginPage(creatorPage);
            creatorLogin.navigate();
            creatorLogin.login(creatorUsername, creatorPassword);
            logger.info("[FanMessaging] Creator logged in successfully");

            CreatorMessagingPage creatorMessaging = new CreatorMessagingPage(creatorPage);

            // Creator opens messaging and accepts fan message with FREE price
            creatorMessaging.openMessagingFromProfile();
            logger.info("[FanMessaging] Creator on Messaging screen");

            creatorMessaging.clickOnFanConversation(fanDisplayName);
            logger.info("[FanMessaging] Creator opened conversation with fan");

            // Accept with FREE price (default)
            creatorMessaging.acceptFanMessageAndReplyFree(fanMessage, creatorReply);
            logger.info("[FanMessaging] Creator accepted message and replied with FREE price");

            // ==================== STEP 3: FAN ACCEPTS FREE MESSAGE (NO PAYMENT) ====================
            logger.info("[FanMessaging] Step 3: Fan accepts free message (no payment needed)");

            // Reload fan page to see creator's reply
            fanPage.reload();
            fanPage.waitForTimeout(3000);
            logger.info("[FanMessaging] Fan page reloaded");

            // Fan accepts free message - no payment needed
            fanMessaging.fanAcceptsFreeMessage(creatorReply);
            logger.info("[FanMessaging] Fan accepted free message");

            // ==================== STEP 4: CREATOR SENDS AUDIO ====================
            logger.info("[FanMessaging] Step 4: Creator sends AUDIO to fan");

            // Navigate creator back to messaging
            creatorPage.navigate("https://stg.twizz.app/creator/profile");
            creatorPage.waitForTimeout(3000);
            logger.info("[FanMessaging] Creator navigated to profile");

            creatorMessaging.openMessagingFromProfile();
            logger.info("[FanMessaging] Creator back on Messaging screen");

            // Get AUDIO media path
            Path audioPath = Paths.get(AUDIO_MEDIA_PATH).toAbsolutePath();
            logger.info("[FanMessaging] Using audio path: {}", audioPath);

            // Creator sends audio to fan
            creatorMessaging.sendMediaToFan(fanDisplayName, audioPath);
            logger.info("[FanMessaging] Creator sent audio to fan");

            // ==================== STEP 5: FAN VERIFIES AUDIO RECEIVED ====================
            logger.info("[FanMessaging] Step 5: Fan verifies audio received from creator");

            // Reload fan page to see new media
            fanPage.reload();
            fanPage.waitForTimeout(3000);

            // Fan verifies audio element is visible (no preview for audio)
            fanMessaging.verifyMessageVisible(creatorReply);
            fanMessaging.verifyAudioElementVisible(creatorReply);
            logger.info("[FanMessaging] Fan verified audio received from creator");

            logger.info("[FanMessaging] Test completed successfully: Full messaging flow with audio and FREE price verified");

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

    /**
     * Complete Fan-Creator Messaging Flow with Mixed Media via Quick Files
     * 
     * Step 1: Fan sends message to creator
     * Step 2: Creator accepts message with fixed price and replies
     * Step 3: Fan accepts paid message and completes payment
     * Step 4: Creator sends mixed media (image + video + audio) via Quick Files
     * Step 5: Fan verifies all media received (2 previews + audio element)
     */
    @Test(priority = 4, description = "Complete messaging flow: fan sends, creator replies, fan pays, creator sends mixed media via Quick Files")
    @Story("Fan-Creator Messaging with Mixed Media via Quick Files")
    public void completeMessagingFlowWithMixedMedia() {
        loadCredentials();
        logger.info("[FanMessaging] Starting complete messaging flow test with mixed media via Quick Files");

        // Get fan page from base class
        Page fanPage = page;
        BrowserContext creatorContext = null;
        Page creatorPage = null;

        try {
            // ==================== STEP 1: FAN LOGIN AND SEND MESSAGE ====================
            logger.info("[FanMessaging] Step 1: Fan login and send message to creator");

            // Fan login
            FanLoginPage fanLogin = new FanLoginPage(fanPage);
            fanLogin.navigate();
            fanLogin.login(fanUsername, fanPassword);
            logger.info("[FanMessaging] Fan logged in successfully");

            // Fan sends message to creator
            FanMessagingPage fanMessaging = new FanMessagingPage(fanPage);
            fanMessaging.navigateToMessaging();
            fanMessaging.clickOnCreatorConversation(creatorDisplayName);
            fanMessaging.sendMessageToCreator(fanMessage);
            logger.info("[FanMessaging] Fan sent message: '{}'", fanMessage);

            // ==================== STEP 2: CREATOR LOGIN AND ACCEPT WITH PRICE ====================
            logger.info("[FanMessaging] Step 2: Creator login, accept message, set price, and reply");

            // Create separate browser context for creator
            creatorContext = BrowserFactory.createNewContext();
            creatorPage = creatorContext.newPage();
            logger.info("[FanMessaging] Created separate browser context for creator");

            CreatorLoginPage creatorLogin = new CreatorLoginPage(creatorPage);
            creatorLogin.navigate();
            creatorLogin.login(creatorUsername, creatorPassword);
            logger.info("[FanMessaging] Creator logged in successfully");

            CreatorMessagingPage creatorMessaging = new CreatorMessagingPage(creatorPage);

            // Creator opens messaging and accepts fan message
            creatorMessaging.openMessagingFromProfile();
            creatorMessaging.verifyGeneralTabSelected();
            logger.info("[FanMessaging] Creator on Messaging screen");

            creatorMessaging.clickOnFanConversation(fanDisplayName);
            logger.info("[FanMessaging] Creator opened conversation with fan");

            // Accept with fixed price
            creatorMessaging.acceptFanMessageAndReply(fanMessage, PRICE, creatorReply);
            logger.info("[FanMessaging] Creator accepted message and replied with price {}", PRICE);

            // ==================== STEP 3: FAN ACCEPTS PAID MESSAGE ====================
            logger.info("[FanMessaging] Step 3: Fan accepts paid message and completes payment");

            // Reload fan page to see creator's reply
            fanPage.reload();
            fanPage.waitForTimeout(3000);
            logger.info("[FanMessaging] Fan page reloaded");

            // Fan accepts and pays
            fanMessaging.verifyMessageVisible(creatorReply);
            logger.info("[FanMessaging] Creator reply visible on fan screen");

            fanMessaging.clickAcceptMedia();
            fanMessaging.completePaymentForMedia();
            logger.info("[FanMessaging] Fan accepted and paid for message");

            // ==================== STEP 4: CREATOR SENDS MIXED MEDIA VIA QUICK FILES ====================
            logger.info("[FanMessaging] Step 4: Creator sends MIXED MEDIA (image + video + audio) via Quick Files");

            // Navigate creator back to messaging
            creatorPage.navigate("https://stg.twizz.app/creator/profile");
            creatorPage.waitForTimeout(3000);
            logger.info("[FanMessaging] Creator navigated to profile");

            creatorMessaging.openMessagingFromProfile();
            logger.info("[FanMessaging] Creator back on Messaging screen");

            // Creator sends mixed media via Quick Files
            creatorMessaging.sendMixedMediaToFanViaQuickFiles(fanDisplayName);
            logger.info("[FanMessaging] Creator sent mixed media to fan");

            // ==================== STEP 5: FAN VERIFIES MIXED MEDIA RECEIVED ====================
            logger.info("[FanMessaging] Step 5: Fan verifies mixed media received from creator");

            // Reload fan page to see new media
            fanPage.reload();
            fanPage.waitForTimeout(3000);

            // Fan verifies mixed media (image preview, video preview, audio element)
            fanMessaging.verifyMixedMediaReceived(creatorReply);
            logger.info("[FanMessaging] Fan verified mixed media received from creator");

            logger.info("[FanMessaging] Test completed successfully: Full messaging flow with mixed media via Quick Files verified");

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
