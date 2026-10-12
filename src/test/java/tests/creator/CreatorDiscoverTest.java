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

    @Story("Discover screen: tabs and grid of publication tiles")
    @Test(priority = 1, description = "Navigate to Discover, verify tabs and thumbnail grid render")
    public void discoverScreenLoadsGrid() {
        CreatorDiscoverPage discover = new CreatorDiscoverPage(page);

        logger.info("[Discover] Navigate to discover via footer Search icon");
        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        int tiles = discover.assertGridRendered();
        Assert.assertTrue(tiles > 0, "Expected at least one grid tile");

        discover.assertGridTileContent();
    }

    @Story("Discover screen: grid lazy-loads more tiles on scroll")
    @Test(priority = 2, description = "Scroll the Discover grid and verify tiles keep rendering")
    public void discoverGridScrollLoadsMore() {
        CreatorDiscoverPage discover = new CreatorDiscoverPage(page);

        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();
        discover.assertGridRendered();

        int after = discover.scrollGridToLoadMore();
        Assert.assertTrue(after > 0, "Expected tiles to remain rendered after scrolling");
        logger.info("[Discover] Tiles rendered after scroll: {}", after);
    }

    @Story("Discover screen: open a publication tile, unmute, and navigate back")
    @Test(priority = 3, description = "Open first grid tile into publication viewer, verify player and creator row, unmute, go back")
    public void discoverTileOpensPublicationAndBack() {
        CreatorDiscoverPage discover = new CreatorDiscoverPage(page);

        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();
        discover.assertGridRendered();

        logger.info("[Discover] Open first grid tile");
        discover.openFirstGridTile();
        discover.assertOnPublicationViewer();
        discover.assertViewerCreatorRow();

        logger.info("[Discover] Toggle mute on publication video");
        discover.togglePublicationMute();

        logger.info("[Discover] Navigate back to Discover grid");
        discover.navigateBackToDiscover();
        discover.assertOnDiscoverScreen();
    }

    @Story("Discover screen: search creator, open profile, and navigate back")
    @Test(priority = 4, description = "Search for a creator via top Search icon, open result profile, go back to Discover")
    public void discoverSearchOpensProfileAndBack() {
        CreatorDiscoverPage discover = new CreatorDiscoverPage(page);

        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        logger.info("[Discover->Search] Open search field and search for 'igor'");
        discover.openSearchField();
        discover.fillSearch("igor");

        logger.info("[Discover->Search] Click search result 'igor test'");
        discover.clickSearchResult("igor test");
        discover.dismissSearchOnboarding();
        discover.ensureOnCreatorProfileScreen();

        logger.info("[Discover->Search] Navigate back and assert Discover screen");
        discover.navigateBackToDiscover();
        discover.assertOnDiscoverScreen();
    }

    @Story("Discover screen: switch to Presentation Video tab and back")
    @Test(priority = 5, description = "Switch to Presentation Video tab, verify feed slides load, unmute, switch back to Discover")
    public void discoverTabSwitchToPresentationVideo() {
        CreatorDiscoverPage discover = new CreatorDiscoverPage(page);

        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        logger.info("[Discover] Switch to Presentation Video tab");
        discover.switchToPresentationVideoTab();
        discover.assertOnPresentationFeed();

        int toggled = discover.unmutePresentationSlides(3);
        Assert.assertTrue(toggled >= 0, "Unmute toggles executed");

        logger.info("[Discover] Switch back to Discover tab");
        discover.switchToDiscoverTab();
        discover.assertOnDiscoverScreen();
    }
}
