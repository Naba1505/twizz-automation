package cucumber.steps;

import org.testng.Assert;

import cucumber.context.TestContext;
import io.cucumber.java.en.Then;

/**
 * Step definitions for the fan lives page.
 */
public class LivesPageSteps {

    private final TestContext context;

    public LivesPageSteps(TestContext context) {
        this.context = context;
    }

    @Then("the {string} heading should be visible on lives page")
    public void theHeadingShouldBeVisibleOnLivesPage(String heading) {
        Assert.assertTrue(context.getLivesPage().isLivesHeadingVisible(),
                "'" + heading + "' heading is not visible on lives page");
    }

    @Then("the Live tab should be visible")
    public void theLiveTabShouldBeVisible() {
        Assert.assertTrue(context.getLivesPage().isLiveTabVisible(),
                "Live tab is not visible");
    }

    @Then("the Events tab should be visible")
    public void theEventsTabShouldBeVisible() {
        Assert.assertTrue(context.getLivesPage().isEventsTabVisible(),
                "Events tab is not visible");
    }

    @Then("the Replay tab should be visible")
    public void theReplayTabShouldBeVisible() {
        Assert.assertTrue(context.getLivesPage().isReplayTabVisible(),
                "Replay tab is not visible");
    }

    @Then("the {string} section should be visible")
    public void theSectionShouldBeVisible(String section) {
        if (section.contains("Today")) {
            Assert.assertTrue(context.getLivesPage().isTodaysEventsVisible(),
                    "Today's events section is not visible");
        } else if (section.contains("Upcoming")) {
            Assert.assertTrue(context.getLivesPage().isUpcomingVisible(),
                    "Upcoming section is not visible");
        }
    }
}
