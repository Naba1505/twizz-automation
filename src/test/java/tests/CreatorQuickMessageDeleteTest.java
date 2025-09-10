package tests;

import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorQuickMessagePage;
import utils.ConfigReader;

public class CreatorQuickMessageDeleteTest extends BaseTestClass {

    @Test(priority = 1, description = "Creator deletes all Quick messages for cleanup")
    public void creatorCanDeleteAllQuickMessages() {
        // Arrange: credentials and expected display name
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");
        String displayName = ConfigReader.getProperty("creator.displayName", "john_smith");

        // Login as Creator and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Quick Message cleanup flow
        CreatorQuickMessagePage qm = new CreatorQuickMessagePage(page);
        qm.openSettingsFromProfile(displayName);
        qm.goToQuickMessage();
        qm.deleteAllQuickMessages();
    }
}
