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
import pages.CreatorLivePage;
import pages.CreatorLoginPage;
import pages.FanLivePage;
import pages.FanLoginPage;
import utils.BrowserFactory;
import utils.ConfigReader;
import utils.DateTimeUtils;
import utils.TestAssets;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Test class for Fan Live Events functionality.
 * Tests fan joining creator live events (instant and scheduled).
 * 
 * Note: These tests depend on creator account module for creating live events.
 */
@Epic("Fan")
@Feature("Live Events")
public class FanLiveTest extends BaseTestClass {

    private static final Logger logger = LoggerFactory.getLogger(FanLiveTest.class);

    // Creator credentials
    private String creatorUsername;
    private String creatorPassword;
    private String creatorDisplayName;

    // Fan credentials
    private String fanUsername;
    private String fanPassword;

    /**
     * Test 1: Creator creates instant live, Fan joins and interacts
     * 
     * Flow:
     * 1. Creator login and land on profile (in creator browser context)
     * 2. Creator navigates to Live screen and creates instant live (Everyone, 15€)
     * 3. Fan login and land on home screen (in separate fan browser context)
     * 4. Fan navigates to Lives screen and joins the live
     * 5. Fan pays for live access
     * 6. Fan posts a comment in live chat
     * 7. Fan closes the live
     * 8. Creator ends the live stream (back to creator context)
     */
    @Story("Creator creates instant live event and Fan joins")
    @Test(priority = 1, description = "Creator creates instant live with Everyone access, Fan joins, pays, comments, and closes")
    public void creatorCreatesInstantLiveFanJoins() {
        // Load credentials from config
        loadCredentials();

        logger.info("[FanLive] Starting test: Creator creates instant live, Fan joins");

        // Use the main page for creator (already initialized by BaseTestClass)
        Page creatorPage = page;

        // ==================== CREATOR FLOW ====================
        logger.info("[FanLive] Step 1: Creator login and navigate to Live screen");

        // Creator login
        CreatorLoginPage creatorLogin = new CreatorLoginPage(creatorPage);
        creatorLogin.navigate();
        Assert.assertTrue(creatorLogin.isLoginHeaderVisible(), "Creator login header not visible");
        Assert.assertTrue(creatorLogin.isLoginFormVisible(), "Creator login form not visible");
        creatorLogin.login(creatorUsername, creatorPassword);
        logger.info("[FanLive] Creator logged in successfully");

        // Navigate to Live screen
        CreatorLivePage creatorLive = new CreatorLivePage(creatorPage);
        creatorLive.openPlusMenu();
        creatorLive.navigateToLive();
        logger.info("[FanLive] Creator on Live screen");

        // Create instant live event
        logger.info("[FanLive] Step 2: Creator creating instant live event (Everyone, 15€)");
        creatorLive.createInstantLiveEveryone15Euro();
        logger.info("[FanLive] Creator instant live created and started");

        // Wait for live to fully initialize
        creatorPage.waitForTimeout(3000);

        // ==================== FAN FLOW (Separate Browser Context) ====================
        logger.info("[FanLive] Step 3: Fan login and navigate to Lives screen (separate context)");

        // Create a new browser context for fan to avoid session conflicts
        BrowserContext fanContext = BrowserFactory.createNewContext();
        Page fanPage = fanContext.newPage();
        logger.info("[FanLive] Created separate browser context for fan");

        try {
            // Fan login
            FanLoginPage fanLogin = new FanLoginPage(fanPage);
            fanLogin.navigate();
            Assert.assertTrue(fanLogin.isLoginHeaderVisible(), "Fan login header not visible");
            Assert.assertTrue(fanLogin.isLoginFormVisible(), "Fan login form not visible");
            fanLogin.login(fanUsername, fanPassword);
            logger.info("[FanLive] Fan logged in successfully");

            // Navigate to Lives screen
            FanLivePage fanLive = new FanLivePage(fanPage);
            fanLive.navigateToLivesScreen();
            logger.info("[FanLive] Fan on Lives screen");

            // Join the live event
            logger.info("[FanLive] Step 4: Fan joining live event for creator: {}", creatorDisplayName);
            fanLive.assertCreatorOnLiveTile(creatorDisplayName);
            fanLive.clickGoToLive();

            // Complete payment
            logger.info("[FanLive] Step 5: Fan completing payment for live access");
            fanLive.completePaymentForLive();
            logger.info("[FanLive] Fan payment completed, now in live");

            // Post a comment
            logger.info("[FanLive] Step 6: Fan posting comment in live chat");
            fanLive.postComment("Hi");
            logger.info("[FanLive] Fan comment posted");

            // Close live (fan side)
            logger.info("[FanLive] Step 7: Fan closing live");
            fanLive.closeLive();
            logger.info("[FanLive] Fan closed live");

        } finally {
            // Close fan context
            try {
                fanContext.close();
                logger.info("[FanLive] Fan browser context closed");
            } catch (Exception e) {
                logger.warn("[FanLive] Error closing fan context: {}", e.getMessage());
            }
        }

        // ==================== CREATOR END LIVE ====================
        logger.info("[FanLive] Step 8: Creator ending live stream");

        // End the live stream (creator page is still active)
        creatorLive.endLiveStream();
        logger.info("[FanLive] Creator ended live stream");

        logger.info("[FanLive] Test completed successfully: Creator instant live, Fan joined and interacted");
    }

    /**
     * Test 2: Creator schedules a live event, Fan buys a ticket, Creator cleans up
     * 
     * Flow:
     * 1. Creator login and land on profile (in creator browser context)
     * 2. Creator navigates to Live screen and schedules a live event (Everyone, 15€)
     * 3. Fan login and land on home screen (in separate fan browser context)
     * 4. Fan navigates to Lives screen and clicks on creator tile
     * 5. Fan buys a ticket for the scheduled live
     * 6. Fan closes (ticket purchased, no live to join yet)
     * 7. Creator deletes the scheduled live event for cleanup
     */
    @Story("Creator schedules live event and Fan buys ticket")
    @Test(priority = 2, description = "Creator schedules a live event, Fan buys ticket, Creator cleans up")
    public void creatorSchedulesLiveFanBuysTicket() {
        // Load credentials from config
        loadCredentials();

        logger.info("[FanLive] Starting test: Creator schedules live, Fan buys ticket");

        // Use the main page for creator (already initialized by BaseTestClass)
        Page creatorPage = page;

        // ==================== CREATOR FLOW ====================
        logger.info("[FanLive] Step 1: Creator login and navigate to Live screen");

        // Creator login
        CreatorLoginPage creatorLogin = new CreatorLoginPage(creatorPage);
        creatorLogin.navigate();
        Assert.assertTrue(creatorLogin.isLoginHeaderVisible(), "Creator login header not visible");
        Assert.assertTrue(creatorLogin.isLoginFormVisible(), "Creator login form not visible");
        creatorLogin.login(creatorUsername, creatorPassword);
        logger.info("[FanLive] Creator logged in successfully");

        // Navigate to Live screen
        CreatorLivePage creatorLive = new CreatorLivePage(creatorPage);
        creatorLive.openPlusMenu();
        creatorLive.navigateToLive();
        logger.info("[FanLive] Creator on Live screen");

        // Schedule live event
        logger.info("[FanLive] Step 2: Creator scheduling live event (Everyone, 15€)");

        // Prepare scheduling time using utility
        LocalDateTime when = DateTimeUtils.futureAtDaysHour(1, 3, 0);

        // Coverage image (optional if not present)
        Path coverage = TestAssets.imageOrNull("Live A.jpg");

        // Fill live form step-by-step
        creatorLive.setAccessEveryone();
        creatorLive.setPriceEuro(15);
        creatorLive.enableChatEveryoneIfPresent();
        creatorLive.chooseSchedule();
        creatorLive.pickDate(when);
        String[] timeCandidates = DateTimeUtils.futureTimeCandidates(when);
        creatorLive.pickTimeCandidates(timeCandidates);
        creatorLive.uploadCoverage(coverage);
        creatorLive.setDescription("Test scheduled live for fan ticket");
        creatorLive.submitAndVerify();
        logger.info("[FanLive] Creator scheduled live event successfully");

        // Wait for event to be fully created
        creatorPage.waitForTimeout(2000);

        // ==================== FAN FLOW (Separate Browser Context) ====================
        logger.info("[FanLive] Step 3: Fan login and navigate to Lives screen (separate context)");

        // Create a new browser context for fan to avoid session conflicts
        BrowserContext fanContext = BrowserFactory.createNewContext();
        Page fanPage = fanContext.newPage();
        logger.info("[FanLive] Created separate browser context for fan");

        try {
            // Fan login
            FanLoginPage fanLogin = new FanLoginPage(fanPage);
            fanLogin.navigate();
            Assert.assertTrue(fanLogin.isLoginHeaderVisible(), "Fan login header not visible");
            Assert.assertTrue(fanLogin.isLoginFormVisible(), "Fan login form not visible");
            fanLogin.login(fanUsername, fanPassword);
            logger.info("[FanLive] Fan logged in successfully");

            // Navigate to Lives screen (only click Live icon, not Live tab for scheduled events)
            FanLivePage fanLive = new FanLivePage(fanPage);
            fanLive.clickLiveIcon();
            fanLive.assertOnLivesScreen();
            logger.info("[FanLive] Fan on Lives screen");

            // Buy ticket for scheduled live - use "John_smith" as shown on tile (not handle)
            String creatorTileName = "John_smith";
            logger.info("[FanLive] Step 4: Fan buying ticket for scheduled live by: {}", creatorTileName);
            fanLive.buyTicketForScheduledLive(creatorTileName);
            logger.info("[FanLive] Fan successfully purchased ticket");

        } finally {
            // Close fan context
            try {
                fanContext.close();
                logger.info("[FanLive] Fan browser context closed");
            } catch (Exception e) {
                logger.warn("[FanLive] Error closing fan context: {}", e.getMessage());
            }
        }

        // ==================== CREATOR CLEANUP ====================
        logger.info("[FanLive] Step 5: Creator deleting scheduled live event for cleanup");

        // Delete the scheduled live event
        creatorLive.tryDeleteLatestLiveEvent();
        logger.info("[FanLive] Creator deleted scheduled live event");

        logger.info("[FanLive] Test completed successfully: Creator scheduled live, Fan bought ticket, Creator cleaned up");
    }

    /**
     * Load credentials from config properties
     */
    private void loadCredentials() {
        creatorUsername = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        creatorPassword = ConfigReader.getProperty("creator.password", "Twizz$123");
        creatorDisplayName = ConfigReader.getProperty("creator.displayName", "Smith");

        fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");

        logger.info("[FanLive] Loaded credentials - Creator: {}, Fan: {}", creatorUsername, fanUsername);
    }
}
