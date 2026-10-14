package cucumber.steps;

import org.testng.Assert;

import cucumber.context.TestContext;
import io.cucumber.java.en.Then;

/**
 * Step definitions for the public explore page.
 */
public class PublicExploreSteps {

    private final TestContext context;

    public PublicExploreSteps(TestContext context) {
        this.context = context;
    }

    @Then("the search bar should be visible on explore page")
    public void theSearchBarShouldBeVisibleOnExplorePage() {
        Assert.assertTrue(context.getPublicExplorePage().isSearchBarVisible(),
                "Search bar is not visible on explore page");
    }

    @Then("the thumbnail grid should be visible")
    public void theThumbnailGridShouldBeVisible() {
        Assert.assertTrue(context.getPublicExplorePage().isThumbnailGridVisible(),
                "Thumbnail grid is not visible on explore page");
    }
}
