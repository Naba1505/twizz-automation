package tests;

import org.testng.annotations.Test;
import org.testng.SkipException;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorMessagingPage;
import utils.ConfigReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreatorMessagingTest extends BaseTestClass {

    @Test(priority = 1, description = "Creator sends a normal text message to a fan from Messaging (with timestamp)")
    public void creatorCanSendNormalTextMessageToFan() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();
        msg.sendTextMessage("Hello Fan This Is Test Message - " + ts);

        // Rely on absence of exceptions and visible conversation input as success criteria for now.
    }

    @Test(priority = 2, description = "Creator sends a Saved response (Quick answer) with appended timestamp")
    public void creatorCanSendQuickAnswerWithTimestamp() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();
        // Open Quick Answers and pick saved response
        msg.openQuickAnswers();
        msg.assertSavedResponsesVisible();
        msg.clickSavedResponseIcon();
        // Append timestamp to differentiate message
        msg.appendToMessage(" - " + ts);
        // Send
        msg.clickSend();
    }

    @Test(priority = 3, description = "Creator sends an image media message from Messaging using Importation -> My Device")
    public void creatorCanSendImageMediaMessage() {
        // Arrange: credentials and media path
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        java.nio.file.Path media = java.nio.file.Paths.get("src/test/resources/Images/MessageImageMediaB.jpg");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow: open conversation and send media
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();
        msg.openMediaPicker();
        msg.chooseMyDeviceForMedia();
        msg.uploadMessageMedia(media);
        // Wait until upload spinner disappears before asserting accepted
        msg.waitForUploadSpinnerToDisappear(15_000);
        // Assert accepted badge appears for sent media (allow longer post-processing)
        msg.assertAcceptedBadgeVisible(60_000);
    }

    @Test(priority = 4, description = "Creator sends a video media message from Messaging using Importation -> My Device")
    public void creatorCanSendVideoMediaMessage() {
        // Arrange: credentials and video path (choose one from repo)
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        // You can switch between A/B as needed; using B by default
        java.nio.file.Path video = java.nio.file.Paths.get("src/test/resources/Videos/MessageVideoMediaB.mp4");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow: open conversation and send media
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();
        msg.openMediaPicker();
        msg.chooseMyDeviceForMedia();
        msg.uploadMessageMedia(video);
        // Video can take longer; wait a longer time window for spinner to disappear
        msg.waitForUploadSpinnerToDisappear(60_000);
        // Assert accepted badge appears for sent media
        msg.assertAcceptedBadgeVisible();
    }

    @Test(priority = 5, description = "Creator sends media message from Messaging using Importation -> Quick Files (albums)")
    public void creatorCanSendMediaFromQuickFilesInMessaging() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow: open conversation
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();

        // Open media picker and switch to Quick Files
        msg.openMediaPicker();
        msg.ensureImportationVisible();
        msg.chooseQuickFilesForMedia();
        // Assert Quick Files screen like codegen: title + My albums
        msg.assertQuickFilesScreen();
        try {
            msg.ensureQuickFilesAlbumsVisible();
        } catch (SkipException se) {
            throw se; // skip if no albums
        }

        // Click a Quick Files album similar to codegen (regex + index), fallback to CSS
        try {
            msg.clickAnyQuickFilesAlbumByRegex();
        } catch (RuntimeException e) {
            // Secondary fallback
            try {
                msg.selectQuickFilesAlbumWithCssFallback();
            } catch (RuntimeException e2) {
                if (e2.getMessage() != null && e2.getMessage().toLowerCase().contains("no quick files")) {
                    throw new SkipException("No Quick Files albums available; skipping test");
                }
                throw e2;
            }
        }

        // Ensure album inner prompt is visible and choose items like codegen
        msg.assertAlbumMediaPrompt();
        msg.pickFirstTwoCoversOrUpToN(3);
        msg.confirmQuickFilesSelection();

        // Allow any post-selection processing/spinners to settle
        msg.waitForUploadSpinnerToDisappear(30_000);

        // Success criteria: back on conversation screen, input visible
        msg.assertConversationInputVisible(30_000);
    }

}
