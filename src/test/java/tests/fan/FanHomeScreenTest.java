package tests.fan;

import org.testng.annotations.Test;
import pages.fan.FanHomePage;
import utils.ConfigReader;

public class FanHomeScreenTest extends BaseFanTest {

    @Test(priority = 1, description = "Fan Home screen validations: logo, feed scroll/play, like/bookmark, three-dots, search and back")
    public void fanHomeScreenValidations() {
        String firstFeedUsername = ConfigReader.getProperty("fan.home.firstFeedUsername", "badrzt");
        String searchHandle = ConfigReader.getProperty("fan.home.search.handle", "john_smith");
        String searchLastName = ConfigReader.getProperty("fan.home.search.lastNameExact", "Smith");

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
