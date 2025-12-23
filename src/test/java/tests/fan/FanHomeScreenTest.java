package tests.fan;

import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.fan.FanHomePage;
import pages.fan.FanLoginPage;
import utils.ConfigReader;

public class FanHomeScreenTest extends BaseTestClass {

    @Test(priority = 1, description = "Fan Home screen validations: logo, feed scroll/play, like/bookmark, three-dots, search and back")
    public void fanHomeScreenValidations() {
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        String firstFeedUsername = ConfigReader.getProperty("fan.home.firstFeedUsername", "badrzt");
        String searchHandle = ConfigReader.getProperty("fan.home.search.handle", "john_smith");
        String searchLastName = ConfigReader.getProperty("fan.home.search.lastNameExact", "Smith");

        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

        FanHomePage home = new FanHomePage(page);
        home.assertOnHomeUrl();
        home.assertUsernameBadgeLogo();
        home.scrollToFirstVideoAndPlay();
        home.scrollToTopFirstFeed(firstFeedUsername);
        home.likeFirstFeed();
        home.bookmarkFirstVisibleFeed();
        home.openThreeDotsAndCancel();
        home.searchSubscriberAndBack(searchHandle, searchLastName);
        home.assertOnHomeUrl();
    }
}
