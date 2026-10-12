package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.fan.FanDiscoverPage;
import utils.ConfigReader;

@Epic("Fan")
@Feature("Discover")
public class FanDiscoverTest extends BaseFanTest {
    private static final Logger logger = LoggerFactory.getLogger(FanDiscoverTest.class);

    @Story("Discover screen: grid of publication tiles (Fan)")
    @Test(priority = 1, description = "Navigate to Fan Discover, verify thumbnail grid renders")
    public void fanDiscoverLoadsGrid() {
        FanDiscoverPage discover = new FanDiscoverPage(page);

        logger.info("[Fan Discover] Navigate to discover via footer Search icon");
        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        int tiles = discover.assertGridRendered();
        Assert.assertTrue(tiles > 0, "Expected at least one grid tile");

        discover.assertGridTileContent();
    }

    @Story("Discover screen: grid lazy-loads more tiles on scroll (Fan)")
    @Test(priority = 2, description = "Scroll the Fan Discover grid and verify tiles keep rendering")
    public void fanDiscoverGridScrollLoadsMore() {
        FanDiscoverPage discover = new FanDiscoverPage(page);

        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();
        discover.assertGridRendered();

        int after = discover.scrollGridToLoadMore();
        Assert.assertTrue(after > 0, "Expected tiles to remain rendered after scrolling");
        logger.info("[Fan Discover] Tiles rendered after scroll: {}", after);
    }

    @Story("Discover screen: open a publication tile, unmute, and navigate back (Fan)")
    @Test(priority = 3, description = "Open first grid tile into publication viewer, verify player and creator row, unmute, go back")
    public void fanDiscoverTileOpensPublicationAndBack() {
        FanDiscoverPage discover = new FanDiscoverPage(page);

        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();
        discover.assertGridRendered();

        logger.info("[Fan Discover] Open first grid tile");
        discover.openFirstGridTile();
        discover.assertOnPublicationViewer();
        discover.assertViewerCreatorRow();

        logger.info("[Fan Discover] Toggle mute on publication video");
        discover.togglePublicationMute();

        logger.info("[Fan Discover] Navigate back to Discover grid");
        discover.navigateBackToDiscover();
        discover.assertOnDiscoverScreen();
    }

    @Story("Discover screen: search creator, open profile, and navigate back (Fan)")
    @Test(priority = 4, description = "Search for a creator via top search icon, open result profile, go back to Discover")
    public void fanDiscoverSearchOpensProfileAndBack() {
        String searchQuery = ConfigReader.getProperty("fan.discover.search.query", "igor");
        String searchResultText = ConfigReader.getProperty("fan.discover.search.resultText", "igor test");

        FanDiscoverPage discover = new FanDiscoverPage(page);

        discover.navigateToDiscover();
        discover.assertOnDiscoverScreen();

        logger.info("[Fan Discover->Search] Open search field and search for '{}'", searchQuery);
        discover.openSearchField();
        discover.fillSearch(searchQuery);

        logger.info("[Fan Discover->Search] Click search result '{}'", searchResultText);
        discover.clickSearchResult(searchResultText);
        discover.dismissSearchOnboarding();
        discover.ensureOnCreatorProfileScreen();

        logger.info("[Fan Discover->Search] Navigate back and assert Discover screen");
        discover.navigateBackToDiscover();
        discover.assertOnDiscoverScreen();
    }
}
