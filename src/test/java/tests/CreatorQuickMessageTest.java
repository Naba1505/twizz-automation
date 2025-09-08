package tests;

import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorQuickMessagePage;
import utils.ConfigReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CreatorQuickMessageTest extends BaseTestClass {

    @Test(priority = 1, description = "Creator adds a Quick message via Settings (with timestamp)")
    public void creatorCanAddQuickMessage() {
        // Arrange: credentials and expected display name
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        String displayName = ConfigReader.getProperty("creator.displayName", "john_smith");
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String title = "Welcome - " + ts;
        String body = "Hello From Automation Script Fan - " + ts;

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Quick Message flow
        CreatorQuickMessagePage qm = new CreatorQuickMessagePage(page);
        qm.openSettingsFromProfile(displayName);
        qm.goToQuickMessage();
        qm.addQuickMessage(title, body);
        qm.assertQuickMessageVisible(title);
    }
}
