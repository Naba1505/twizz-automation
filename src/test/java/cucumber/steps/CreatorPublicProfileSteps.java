package cucumber.steps;

import org.testng.Assert;

import cucumber.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for the creator public profile page.
 */
public class CreatorPublicProfileSteps {

    private final TestContext context;

    public CreatorPublicProfileSteps(TestContext context) {
        this.context = context;
    }

    @Given("I navigate to a creator public profile from discover")
    public void iNavigateToACreatorPublicProfile() {
        context.navigateToPublicDiscover();
        String name = context.getPublicDiscoverPage().clickFirstCreatorCard();
        context.setCurrentCreatorHandle(name);
    }

    @Then("the creator name should be visible")
    public void theCreatorNameShouldBeVisible() {
        // The page is on /p/{handle}, creator name is the first text in the profile header
        Assert.assertTrue(context.getCreatorPublicProfilePage().isOnCreatorPublicProfile(),
                "Not on a creator public profile page");
    }

    @Then("the creator handle should be visible")
    public void theCreatorHandleShouldBeVisible() {
        Assert.assertTrue(context.getCreatorPublicProfilePage().isCreatorHandleVisible(),
                "Creator handle (@username) is not visible");
    }

    @Then("the Subscribe button should be visible")
    public void theSubscribeButtonShouldBeVisible() {
        Assert.assertTrue(context.getCreatorPublicProfilePage().isSubscribeButtonVisible(),
                "Subscribe button is not visible");
    }

    @Then("the pricing info should be visible")
    public void thePricingInfoShouldBeVisible() {
        Assert.assertTrue(context.getCreatorPublicProfilePage().isPricingInfoVisible(),
                "Pricing info (Free/amount) is not visible");
    }

    @Then("the publications count should be visible")
    public void thePublicationsCountShouldBeVisible() {
        Assert.assertTrue(context.getCreatorPublicProfilePage().isPublicationsCountVisible(),
                "Publications count is not visible");
    }

    @Then("the collections count should be visible")
    public void theCollectionsCountShouldBeVisible() {
        Assert.assertTrue(context.getCreatorPublicProfilePage().isCollectionsCountVisible(),
                "Collections count is not visible");
    }

    @Then("the publications tab should be visible")
    public void thePublicationsTabShouldBeVisible() {
        Assert.assertTrue(context.getCreatorPublicProfilePage().isPublicationsTabVisible(),
                "Publications tab is not visible");
    }

    @Then("the collections tab should be visible")
    public void theCollectionsTabShouldBeVisible() {
        Assert.assertTrue(context.getCreatorPublicProfilePage().isCollectionsTabVisible(),
                "Collections tab is not visible");
    }

    @Then("the follow icon should be visible")
    public void theFollowIconShouldBeVisible() {
        Assert.assertTrue(context.getCreatorPublicProfilePage().isFollowIconVisible(),
                "Follow icon is not visible");
    }

    @When("I click the Subscribe button")
    public void iClickTheSubscribeButton() {
        context.getCreatorPublicProfilePage().clickSubscribeButton();
    }
}
