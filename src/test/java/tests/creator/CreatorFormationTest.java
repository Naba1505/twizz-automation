package tests.creator;

import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorProfilePage;
import pages.creator.CreatorFormationPage;
import utils.ConfigReader;

public class CreatorFormationTest extends BaseTestClass {

    @Test(priority = 1, description = "Creator can navigate Formation help topics from Settings and back")
    public void creatorCanNavigateFormationTopics() {
        // Arrange credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login and land on profile (common pattern)
        CreatorLoginPage login = new CreatorLoginPage(page);
        login.navigate();
        login.login(username, password);
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Open Settings -> Formation
        CreatorFormationPage formation = new CreatorFormationPage(page);
        formation.openSettingsFromProfile();
        formation.openFormationTile();
        formation.assertOnFormationScreen();

        // Push media
        formation.openTopic("Push media");
        formation.assertTopicTitle("Push media");
        formation.scrollToQuestionsFooter();
        formation.scrollToTopicTitle("Push media");
        formation.backToFormationFromTopic();

        // Collections
        formation.openTopic("Collections");
        formation.assertTopicTitle("Collections");
        formation.scrollToQuestionsFooter();
        formation.scrollToTopicTitle("Collections");
        formation.backToFormationFromTopic();

        // Messaging
        formation.openTopic("Messaging");
        formation.assertTopicTitle("Messaging");
        formation.scrollToQuestionsFooter();
        formation.scrollToTopicTitle("Messaging");
        formation.backToFormationFromTopic();

        // Lives
        formation.openTopic("Lives");
        formation.assertTopicTitle("Lives");
        formation.scrollToQuestionsFooter();
        formation.scrollToTopicTitle("Lives");
        formation.backToFormationFromTopic();

        // Unlocks
        formation.openTopic("Unlocks");
        formation.assertTopicTitle("Unlocks");
        formation.scrollToQuestionsFooter();
        formation.scrollToTopicTitle("Unlocks");
        formation.backToFormationFromTopic();

        // Profile
        formation.openTopic("Profile");
        formation.assertTopicTitle("Profile");
        formation.scrollToQuestionsFooter();
        formation.scrollToTopicTitle("Profile");
        formation.backToFormationFromTopic();

        // Presentation Video(s)
        formation.openTopic("Presentation Video");
        formation.assertTopicTitle("Presentation Videos");
        // The spec mentions a content line; ensure visible
        formation.scrollToQuestionsFooter(); // If not present, page still scrolls to bottom safely
        formation.scrollToTopicTitle("Presentation Videos");
        formation.backToFormationFromTopic();

        // Payments
        formation.openTopic("Payments");
        formation.assertTopicTitle("Payments");
        formation.scrollToQuestionsFooter();
        formation.scrollToTopicTitle("Payments");
        formation.backToFormationFromTopic();

        // Back to Settings from Formation
        formation.assertOnFormationUrl();
        formation.backToSettingsFromFormation();
        formation.assertOnSettingsUrl();
    }
}
