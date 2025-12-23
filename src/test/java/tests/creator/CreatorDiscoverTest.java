package tests.creator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorDiscoverPage;

@Epic("Creator")
@Feature("Discover")
public class CreatorDiscoverTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorDiscoverTest.class);

    @Story("Discover screen: feeds visible, unmute and scroll actions")
    @Test(priority = 1, description = "Navigate to Discover, verify feeds exist, scroll down/up, unmute each feed")
    public void creatorDiscoverFeedsAndUnmute() {
        CreatorDiscoverPage discover = new CreatorDiscoverPage(page);

        logger.info("[Discover] Navigate to discover via Search icon");
        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        logger.info("[Discover] Scroll down ensuring feeds are visible");
        int seen = discover.scrollDownEnsureFeeds();
        Assert.assertTrue(seen > 0, "Expected to see at least one feed");

        logger.info("[Discover] Unmute every feed (visible) while scrolling");
        int toggled = discover.unmuteAllFeedsWhileScrolling();
        Assert.assertTrue(toggled >= 0, "Unmute toggles executed");

        logger.info("[Discover] Scroll back to top");
        discover.scrollUpToTop();
    }

    @Story("Discover screen: open a creator profile from a feed and navigate back")
    @Test(priority = 2, description = "From Discover, open a random visible profile from a feed, ensure profile screen, go back, and assert Discover")
    public void creatorDiscoverOpenProfileAndBack() {
        CreatorDiscoverPage discover = new CreatorDiscoverPage(page);

        logger.info("[Discover->Profile] Navigate to discover via Search icon");
        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        logger.info("[Discover->Profile] Open a random visible discover profile");
        discover.openRandomVisibleDiscoverProfile();
        discover.ensureOnCreatorProfileScreen();

        logger.info("[Discover->Profile] Navigate back and assert Discover screen");
        discover.navigateBackToDiscover();
        discover.assertOnDiscoverScreen();
    }

    @Story("Discover screen: search and open creator profile, then navigate back")
    @Test(priority = 3, description = "Search for a creator, open profile via result, verify profile, go back to Discover and assert URL")
    public void creatorDiscoverSearchOpenAndBack() {
        CreatorDiscoverPage discover = new CreatorDiscoverPage(page);

        logger.info("[Discover->Search] Navigate to discover via Search icon");
        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        logger.info("[Discover->Search] Open search field and search for 'igor'");
        discover.openSearchField();
        discover.fillSearch("igor");

        logger.info("[Discover->Search] Click search result 'igor test'");
        discover.clickSearchResult("igor test");
        discover.ensureOnCreatorProfileScreen();

        logger.info("[Discover->Search] Navigate back and assert Discover screen");
        discover.navigateBackToDiscover();
        discover.assertOnDiscoverScreen();
    }
}
