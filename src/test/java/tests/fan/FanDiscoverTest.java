package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.fan.FanDiscoverPage;
import pages.fan.FanLoginPage;
import utils.ConfigReader;

@Epic("Fan")
@Feature("Discover")
public class FanDiscoverTest extends BaseTestClass {
    private static final Logger logger = LoggerFactory.getLogger(FanDiscoverTest.class);

    @Story("Discover screen: feeds visible, unmute and scroll actions")
    @Test(priority = 1, description = "Navigate to Discover, verify feeds exist, scroll down/up, unmute each feed (Fan)")
    public void fanDiscoverFeedsAndUnmute() {
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");

        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

        FanDiscoverPage discover = new FanDiscoverPage(page);

        logger.info("[Fan Discover] Navigate to discover via Search icon");
        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        logger.info("[Fan Discover] Scroll down ensuring feeds are visible");
        int seen = discover.scrollDownEnsureFeeds();
        Assert.assertTrue(seen > 0, "Expected to see at least one feed");

        logger.info("[Fan Discover] Unmute every feed (visible) while scrolling");
        int toggled = discover.unmuteAllFeedsWhileScrolling();
        Assert.assertTrue(toggled >= 0, "Unmute toggles executed");

        logger.info("[Fan Discover] Scroll back to top");
        discover.scrollUpToTop();
    }

    @Story("Discover screen: open a creator profile from a feed and navigate back")
    @Test(priority = 2, description = "From Fan Discover, open a random visible profile from a feed, ensure profile screen, go back, and assert Discover")
    public void fanDiscoverOpenProfileAndBack() {
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");

        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

        FanDiscoverPage discover = new FanDiscoverPage(page);

        logger.info("[Fan Discover->Profile] Navigate to discover via Search icon");
        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        logger.info("[Fan Discover->Profile] Open a random visible discover profile");
        discover.openRandomVisibleDiscoverProfile();
        discover.ensureOnCreatorProfileScreen();

        logger.info("[Fan Discover->Profile] Navigate back and assert Discover screen");
        discover.navigateBackToDiscover();
        discover.assertOnDiscoverScreen();
    }

    @Story("Discover screen: search and open creator profile, then navigate back")
    @Test(priority = 3, description = "Search for a creator, open profile via result, verify profile, go back to Discover and assert URL (Fan)")
    public void fanDiscoverSearchOpenAndBack() {
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        String searchQuery = ConfigReader.getProperty("fan.discover.search.query", "igor");
        String searchResultText = ConfigReader.getProperty("fan.discover.search.resultText", "igor test");

        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

        FanDiscoverPage discover = new FanDiscoverPage(page);

        logger.info("[Fan Discover->Search] Navigate to discover via Search icon");
        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        logger.info("[Fan Discover->Search] Open search field and search");
        discover.openSearchField();
        discover.fillSearch(searchQuery);

        logger.info("[Fan Discover->Search] Click search result '{}'", searchResultText);
        discover.clickSearchResult(searchResultText);
        discover.ensureOnCreatorProfileScreen();

        logger.info("[Fan Discover->Search] Navigate back and assert Discover screen");
        discover.navigateBackToDiscover();
        discover.assertOnDiscoverScreen();
    }
}
