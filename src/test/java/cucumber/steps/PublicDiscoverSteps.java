package cucumber.steps;

import org.testng.Assert;

import cucumber.context.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for the public discover page.
 */
public class PublicDiscoverSteps {

    private final TestContext context;

    public PublicDiscoverSteps(TestContext context) {
        this.context = context;
    }

    @Then("the search bar should be visible on discover page")
    public void theSearchBarShouldBeVisible() {
        Assert.assertTrue(context.getPublicDiscoverPage().isSearchBarVisible(),
                "Search bar is not visible on discover page");
    }

    @Then("the creator feed should be visible")
    public void theCreatorFeedShouldBeVisible() {
        Assert.assertTrue(context.getPublicDiscoverPage().isCreatorFeedVisible(),
                "Creator feed is not visible");
    }

    @Then("at least {int} creator card(s) should be displayed")
    public void atLeastCreatorCardsShouldBeDisplayed(int minCount) {
        int count = context.getPublicDiscoverPage().getCreatorCardCount();
        Assert.assertTrue(count >= minCount,
                "Expected at least " + minCount + " creator cards but found: " + count);
    }

    @Then("the unmute button should be visible")
    public void theUnmuteButtonShouldBeVisible() {
        Assert.assertTrue(context.getPublicDiscoverPage().isUnmuteButtonVisible(),
                "Unmute button is not visible");
    }

    @Then("the Search icon should be visible")
    public void theSearchIconShouldBeVisible() {
        Assert.assertTrue(context.getPublicDiscoverPage().isSearchIconVisible(),
                "Search icon is not visible");
    }

    @Then("the Messaging icon should be visible")
    public void theMessagingIconShouldBeVisible() {
        Assert.assertTrue(context.getPublicDiscoverPage().isMessagingIconVisible(),
                "Messaging icon is not visible");
    }

    @Then("the Live icon should be visible")
    public void theLiveIconShouldBeVisible() {
        Assert.assertTrue(context.getPublicDiscoverPage().isLiveIconVisible(),
                "Live icon is not visible");
    }

    @Then("the Settings icon should be visible")
    public void theSettingsIconShouldBeVisible() {
        Assert.assertTrue(context.getPublicDiscoverPage().isSettingsIconVisible(),
                "Settings icon is not visible");
    }

    @When("I click on the first creator card")
    public void iClickOnTheFirstCreatorCard() {
        String name = context.getPublicDiscoverPage().clickFirstCreatorCard();
        context.setCurrentCreatorHandle(name);
    }

    @When("I click the Search icon")
    public void iClickTheSearchIcon() {
        context.getPublicDiscoverPage().clickSearchIcon();
    }

    @When("I click the Live icon")
    public void iClickTheLiveIcon() {
        context.getPublicDiscoverPage().clickLiveIcon();
    }

    @When("I click the Messaging icon")
    public void iClickTheMessagingIcon() {
        context.getPublicDiscoverPage().clickMessagingIcon();
    }

    @When("I click the Home icon on discover page")
    public void iClickTheHomeIconOnDiscoverPage() {
        context.getPublicDiscoverPage().clickHomeIcon();
    }
}
