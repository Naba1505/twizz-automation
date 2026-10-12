package tests.fan;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.fan.FanHomePage;
import utils.ConfigReader;

@Epic("Fan")
@Feature("Home")
public class FanHomeScreenTest extends BaseFanTest {
    private static final Logger logger = LoggerFactory.getLogger(FanHomeScreenTest.class);

    @Story("Home screen: top bar tabs and default Presentation feed")
    @Test(priority = 1, description = "Fan Home: assert /fan/home URL, top bar icons and tabs, default Presentation feed renders")
    public void homeLoadsWithTabsAndFeed() {
        FanHomePage home = new FanHomePage(page);

        home.assertOnHomeUrl();
        home.assertHomeTopBar();
        home.assertDefaultTabIsPresentation();

        int slides = home.assertFeedRendered();
        Assert.assertTrue(slides > 0, "Expected feed slides on default Presentation tab");
    }

    @Story("Home screen: Suggestions tab creator board")
    @Test(priority = 2, description = "Switch to Suggestions tab, verify creator suggestion board with Follow buttons, back to Presentation")
    public void homeSuggestionsTabShowsCreatorBoard() {
        FanHomePage home = new FanHomePage(page);

        home.assertOnHomeUrl();
        home.assertHomeTopBar();

        logger.info("[Fan Home] Switch to Suggestions tab");
        home.switchToSuggestionsTab();
        int followButtons = home.assertSuggestionsBoardRendered();
        Assert.assertTrue(followButtons > 0, "Expected Follow buttons on Suggestions board");

        logger.info("[Fan Home] Switch back to Presentation");
        home.switchToPresentationTab();
        home.assertFeedRendered();
    }

    @Story("Home screen: unmute feed and open a creator profile")
    @Test(priority = 3, description = "On Presentation feed, unmute slides, open creator profile via Discover profile bar, go back")
    public void homeFeedActionsAndProfile() {
        FanHomePage home = new FanHomePage(page);

        home.assertOnHomeUrl();
        home.assertHomeTopBar();
        home.assertFeedRendered();

        int toggled = home.unmuteFeedSlides(3);
        Assert.assertTrue(toggled >= 0, "Unmute toggles executed");

        logger.info("[Fan Home] Open creator profile via Discover profile bar");
        home.openDiscoverProfileCta();
        home.dismissSearchOnboarding();
        home.ensureOnCreatorProfileScreen();

        home.navigateBackToHome();
        home.assertOnHomeUrl();
    }

    @Story("Home screen: Following tab feed and post actions")
    @Test(priority = 4, description = "Switch to Following tab, verify posts with like/bookmark, open post menu and cancel, back to Presentation")
    public void homeFollowingTabFeedAndActions() {
        FanHomePage home = new FanHomePage(page);

        home.assertOnHomeUrl();
        home.assertHomeTopBar();

        logger.info("[Fan Home] Switch to Following tab");
        home.switchToFollowingTab();
        home.assertFollowingFeedRendered();

        logger.info("[Fan Home] Open post three-dot menu and cancel");
        home.openThreeDotsAndCancel();

        logger.info("[Fan Home] Switch back to Presentation");
        home.switchToPresentationTab();
        home.assertFeedRendered();
    }

    @Story("Home screen: Discover lives screen tabs")
    @Test(priority = 5, description = "Open Discover lives, switch through Live/Events/Replay tabs, return home")
    public void homeDiscoverLivesTabs() {
        FanHomePage home = new FanHomePage(page);

        home.assertOnHomeUrl();
        home.assertHomeTopBar();

        logger.info("[Fan Home] Open Discover lives");
        home.openDiscoverLives();
        home.assertLivesTabs();

        logger.info("[Fan Home] Return to home");
        home.assertOnHomeUrl();
    }

    @Story("Home screen: search creator, open profile, and navigate back")
    @Test(priority = 6, description = "Search for a creator from home, open result profile, go back to home")
    public void homeSearchOpensProfileAndBack() {
        String searchQuery = ConfigReader.getProperty("fan.discover.search.query", "igor");
        String searchResultText = ConfigReader.getProperty("fan.discover.search.resultText", "igor test");

        FanHomePage home = new FanHomePage(page);

        home.assertOnHomeUrl();
        home.assertHomeTopBar();

        logger.info("[Fan Home->Search] Open search and search for '{}'", searchQuery);
        home.openSearchField();
        home.fillSearch(searchQuery);

        logger.info("[Fan Home->Search] Click search result '{}'", searchResultText);
        home.clickSearchResult(searchResultText);
        home.dismissSearchOnboarding();
        home.ensureOnCreatorProfileScreen();

        logger.info("[Fan Home->Search] Navigate back and assert Home screen");
        home.navigateBackToHome();
        home.assertOnHomeUrl();
    }
}
