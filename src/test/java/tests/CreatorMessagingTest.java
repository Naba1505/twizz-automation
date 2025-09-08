package tests;

import org.testng.annotations.Test;
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
}
