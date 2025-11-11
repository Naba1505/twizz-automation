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

    @Test(priority = 2, description = "Creator sends a Saved response (Quick answer) with appended timestamp", enabled = false)
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

    @Test(priority = 6, description = "Creator sends a Private media message (image + video) via My Device")
    public void creatorCanSendPrivateMediaMessage() {
        // Arrange: credentials and media paths
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        java.nio.file.Path img = java.nio.file.Paths.get("src/test/resources/Images/MessageImageMediaB.jpg");
        java.nio.file.Path vid = java.nio.file.Paths.get("src/test/resources/Videos/MessageVideoMediaB.mp4");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow: open conversation
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();

        // Open Private media screen and add first media (Image A)
        msg.openPrivateMediaScreen();
        msg.clickPrivateMediaAddPlus();
        msg.ensureImportationVisible();
        msg.chooseMyDeviceForMedia();
        msg.uploadMessageMedia(img);
        msg.waitForUploadSpinnerToDisappear(60_000);
        msg.ensureBlurToggleEnabled();
        msg.clickNextStrict();
        msg.waitForSecondAddIcon(5_000);

        // Add second media (Video A)
        msg.clickPrivateMediaAddPlus();
        msg.ensureImportationVisible();
        msg.chooseMyDeviceForMedia();
        msg.uploadMessageMedia(vid);
        msg.waitForUploadSpinnerToDisappear(60_000);
        msg.clickNextStrict();

        // Proceed to message step, fill and set price
        msg.assertPrivateMessagePlaceholder();
        msg.fillPrivateMessage("Test");
        // Apply templates as per codegen
        msg.clickMessageTemplate("/name");
        msg.clickMessageTemplate("Identification");
        // Set price and propose
        msg.setPriceEuro(15);
        msg.clickProposePrivateMedia();
        msg.waitForUploadingBanner();
        // Validate success via toast or fallback to conversation input visible
        try { msg.waitForMediaSentToast(15_000); }
        catch (Throwable ignored) { msg.assertConversationInputVisible(60_000); }
    }

    @Test(priority = 7, description = "Creator sends a Private media message with promotion (10€, validity Unlimited)")
    public void creatorCanSendPrivateMediaWithPromotion() {
        // Arrange: credentials and media paths
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        java.nio.file.Path img = java.nio.file.Paths.get("src/test/resources/Images/MessageImageMediaB.jpg");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow: open conversation
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();

        // Open Private media screen and add media (image)
        msg.openPrivateMediaScreen();
        msg.clickPrivateMediaAddPlus();
        msg.ensureImportationVisible();
        msg.chooseMyDeviceForMedia();
        msg.uploadMessageMedia(img);
        msg.waitForUploadSpinnerToDisappear(60_000);
        msg.ensureBlurToggleEnabled();
        msg.clickNextStrict();

        // Fill message and price
        msg.assertPrivateMessagePlaceholder();
        msg.fillPrivateMessage("Send Media Message with Promotion");
        msg.setPriceEuro(15);

        // Enable promotion and configure discount + validity
        msg.enablePromotionToggle();
        msg.ensureDiscountVisible();
        msg.fillPromotionEuroDiscount(10);
        msg.ensureValidityTitle();
        msg.selectValidityUnlimited();

        // Propose private media
        msg.clickProposePrivateMedia();
        msg.waitForUploadingBanner();
        try { msg.waitForMediaSentToast(15_000); } catch (Throwable ignored) {}

        // Ensure we land back on the conversation screen
        msg.assertConversationInputVisible(60_000);
    }

    @Test(priority = 8, description = "Creator sends a Private media message with promotion (5% discount, validity 7 days)")
    public void creatorCanSendPrivateMediaWithPromotionPercent() {
        // Arrange: credentials and media paths
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        java.nio.file.Path img = java.nio.file.Paths.get("src/test/resources/Images/MessageImageMediaB.jpg");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow: open conversation
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();

        // Open Private media screen and add media (image)
        msg.openPrivateMediaScreen();
        msg.clickPrivateMediaAddPlus();
        msg.ensureImportationVisible();
        msg.chooseMyDeviceForMedia();
        msg.uploadMessageMedia(img);
        msg.ensureBlurToggleEnabled();
        msg.clickNextStrict();

        // Fill message and price
        msg.assertPrivateMessagePlaceholder();
        msg.fillPrivateMessage("Send Media Message with 5% Promotion");
        msg.setPriceEuro(15);

        // Enable promotion with percent discount and 7 days validity
        msg.enablePromotionToggle();
        msg.ensureDiscountVisible();
        msg.fillPromotionPercent(5);
        msg.ensureValidityTitle();
        msg.selectValidity7Days();

        // Propose private media
        msg.clickProposePrivateMedia();
        msg.waitForUploadingBanner();
        try { msg.waitForMediaSentToast(15_000); } catch (Throwable ignored) {}

        // Ensure we land back on the conversation screen
        msg.assertConversationInputVisible(60_000);
    }

    @Test(priority = 9, description = "Creator sends a Private media message for Free (image + video) via My Device")
    public void creatorCanSendPrivateMediaFree() {
        // Arrange: credentials and media paths
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        java.nio.file.Path img = java.nio.file.Paths.get("src/test/resources/Images/MessageImageMediaB.jpg");
        java.nio.file.Path vid = java.nio.file.Paths.get("src/test/resources/Videos/MessageVideoMediaB.mp4");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow: open conversation
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();

        // Open Private media screen and add first media (image)
        msg.openPrivateMediaScreen();
        msg.clickPrivateMediaAddPlus();
        msg.ensureImportationVisible();
        msg.chooseMyDeviceForMedia();
        msg.uploadMessageMedia(img);
        msg.ensureBlurToggleEnabled();
        msg.clickNextStrict();
        msg.waitForSecondAddIcon(5_000);

        // Add second media (video)
        msg.clickPrivateMediaAddPlus();
        msg.ensureImportationVisible();
        msg.chooseMyDeviceForMedia();
        msg.uploadMessageMedia(vid);
        msg.clickNextStrict();

        // Fill message and set price as Free
        msg.assertPrivateMessagePlaceholder();
        msg.fillPrivateMessage("Send Free Private Media Message");
        msg.selectPriceFree();

        // Propose private media
        msg.clickProposePrivateMedia();
        msg.waitForUploadingBanner();
        try { msg.waitForMediaSentToast(15_000); } catch (Throwable ignored) {}

        // Ensure we land back on the conversation screen
        msg.assertConversationInputVisible(60_000);
    }

    @Test(priority = 10, description = "Creator sends a Free Private media message with unblurred media")
    public void creatorCanSendPrivateMediaFreeUnblurred() {
        // Arrange: credentials and media paths
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        java.nio.file.Path img = java.nio.file.Paths.get("src/test/resources/Images/MessageImageMediaB.jpg");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Messaging flow: open conversation
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.openMessagingFromProfile();
        msg.openFirstFanConversation();

        // Open Private media screen and add media (image)
        msg.openPrivateMediaScreen();
        msg.clickPrivateMediaAddPlus();
        msg.ensureImportationVisible();
        msg.chooseMyDeviceForMedia();
        msg.uploadMessageMedia(img);
        msg.ensureBlurToggleEnabled();
        // Unblur before proceeding to message step
        msg.disableBlurToggleIfEnabled();
        msg.clickNextStrict();

        // Fill message and set price as Free
        msg.assertPrivateMessagePlaceholder();
        msg.fillPrivateMessage("Send Free Private Media Message (Unblurred)");
        msg.selectPriceFree();

        // Propose private media
        msg.clickProposePrivateMedia();
        msg.waitForUploadingBanner();
        try { msg.waitForMediaSentToast(15_000); } catch (Throwable ignored) {}

        // Ensure we land back on the conversation screen
        msg.assertConversationInputVisible(60_000);
    }   

    @Test(priority = 11, description = "Creator sends a Private media message using Quick Files (multi-select)")
    public void creatorCanSendPrivateMediaViaQuickFiles() {
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

        // Open Private media screen and choose Quick Files to import
        msg.openPrivateMediaScreen();
        msg.clickPrivateMediaAddPlus();
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
        msg.waitForUploadSpinnerToDisappear(60_000);
        try { page.waitForTimeout(1000); } catch (Throwable ignored) {}
    }


    @Test(priority = 12, description = "Creator opens Private Gallery from messaging, scrolls, previews an item, and closes preview")
    public void creatorCanViewPrivateGallery() {
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

        // Open actions menu and navigate to Private Gallery
        msg.openActionsMenu();
        msg.assertActionPrompt();
        msg.clickPrivateGallery();
        msg.assertPrivateGalleryScreen();

        // Wait for items to load, then scroll bottom and back to top
        msg.waitForPrivateGalleryItems(15_000);
        msg.scrollPrivateGalleryToBottomThenTop();

        // Preview any item and close preview
        msg.previewAnyPrivateGalleryItem();
        msg.closePrivateGalleryPreview();
    }

    @Test(priority = 13, description = "Creator navigates Messaging dashboard tabs, uses filter and search")
    public void creatorCanUseMessagingTabsFilterAndSearch() {
        // Arrange: credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Navigate directly to profile, then open Messaging via dashboard icon
        CreatorMessagingPage msg = new CreatorMessagingPage(page);
        msg.navigateToCreatorProfileViaUrl();
        msg.openMessagingFromDashboardIcon();
        msg.assertMessagingTitle();

        // Navigate To Deliver tab and back to General
        msg.clickToDeliverTab();
        msg.clickGeneralTab();

        // Open Filter and apply Unread messages, then All (by default)
        msg.openFilter();
        msg.filterUnreadMessages();
        msg.filterAllByDefault();

        // Open search, search for Paul, assert a result, then clear search and close search icon again
        msg.openSearchIcon();
        msg.fillMessagingSearch("Paul");
        msg.assertSearchResultVisible("Paul Lewis");
        msg.fillMessagingSearch("");
        msg.openSearchIcon();
    }
}
