package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorMediaPushPage;
import pages.CreatorPresentationVideosPage;
import utils.ConfigReader;

import java.nio.file.Path;

public class CreatorPresentationVideosTest extends BaseTestClass {

    @Test(priority = 1, description = "Creator can upload a Presentation Video (<60s) and see 'Waiting' status")
    public void creatorCanUploadPresentationVideo() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPresentationVideosPage pvPage = new CreatorPresentationVideosPage(page);
        CreatorMediaPushPage mediaPushUtils = new CreatorMediaPushPage(page); // for waitForUploadingMessageIfFast()

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Navigate to Presentation Videos page
        pvPage.openSettingsFromProfile();
        pvPage.openPresentationVideosScreen();

        // Upload presentation video
        Path video = CreatorPresentationVideosPage.resolveVideoPath("src/test/resources/Videos/PresentationVideoA.mp4");
        pvPage.uploadPresentationVideo(video);

        // Reuse Media Push helper to optionally wait for uploading message if it appears
        mediaPushUtils.waitForUploadingMessageIfFast();

        // Click the sticky button on the Presentation Video screen
        pvPage.clickPresentationVideoStickyButton();

        // Assert status becomes 'Waiting'
        pvPage.waitForWaitingStatus();
    }

    @Test(priority = 2, description = "Creator can delete the created Presentation Video and see empty prompt")
    public void creatorCanDeletePresentationVideo() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPresentationVideosPage pvPage = new CreatorPresentationVideosPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Navigate to Presentation Videos page
        pvPage.openSettingsFromProfile();
        pvPage.openPresentationVideosScreen();

        // Delete and assert empty state
        pvPage.deletePresentationVideo();
        pvPage.assertEmptyPromptVisible();
    }
}
