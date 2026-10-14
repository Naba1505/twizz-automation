package cucumber.steps;

import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import cucumber.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.ConfigReader;

/**
 * Shared step definitions for navigation, URL checks, and generic element assertions.
 * Reused across multiple feature files.
 */
public class CommonSteps {

    private final TestContext context;

    public CommonSteps(TestContext context) {
        this.context = context;
    }

    // -- Navigation steps --

    @Given("I am on the Twizz landing page")
    public void iAmOnTheLandingPage() {
        context.navigateToLandingPage();
        context.getLandingPage().waitForPageToLoad();
    }

    @Given("I am on the public discover page")
    public void iAmOnThePublicDiscoverPage() {
        context.navigateToPublicDiscover();
    }

    @Given("I am on the public explore page")
    public void iAmOnThePublicExplorePage() {
        context.navigateToPublicExplore();
    }

    @Given("I am on the lives page")
    public void iAmOnTheLivesPage() {
        context.navigateToLives();
    }

    @Given("I am on the intro page")
    public void iAmOnTheIntroPage() {
        context.navigateToIntro();
    }

    @Given("I am on the sign-in page")
    public void iAmOnTheSignInPage() {
        context.navigateToSignIn();
    }

    @Given("I am on the sign-up page")
    public void iAmOnTheSignUpPage() {
        context.navigateToSignUp();
    }

    // -- URL assertions --

    @Then("the URL should contain {string}")
    public void theUrlShouldContain(String urlPart) {
        Page page = context.getPage();
        try {
            page.waitForURL("**" + urlPart + "**",
                    new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
        } catch (Exception e) {
            // Fallback to direct check
        }
        Assert.assertTrue(page.url().contains(urlPart),
                "Expected URL to contain '" + urlPart + "' but was: " + page.url());
    }

    // -- Generic element assertions --

    @Then("the text {string} should be visible")
    public void theTextShouldBeVisible(String text) {
        Page page = context.getPage();
        Locator locator = page.getByText(text);
        try {
            locator.first().scrollIntoViewIfNeeded();
            locator.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) {
            // Fall through to assertion
        }
        Assert.assertTrue(locator.first().isVisible(),
                "Expected text '" + text + "' to be visible");
    }

    @Then("the heading {string} should be visible")
    public void theHeadingShouldBeVisible(String heading) {
        Page page = context.getPage();
        Locator locator = page.getByRole(AriaRole.HEADING,
                new Page.GetByRoleOptions().setName(heading));
        try {
            locator.first().scrollIntoViewIfNeeded();
            locator.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) {
            // Fall through to assertion
        }
        Assert.assertTrue(locator.first().isVisible(),
                "Expected heading '" + heading + "' to be visible");
    }

    @Then("the image {string} should be visible")
    public void theImageShouldBeVisible(String imgName) {
        Page page = context.getPage();
        Locator locator = page.getByRole(AriaRole.IMG,
                new Page.GetByRoleOptions().setName(imgName));
        try {
            locator.first().scrollIntoViewIfNeeded();
            locator.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) {
            // Fall through to assertion
        }
        Assert.assertTrue(locator.first().isVisible(),
                "Expected image '" + imgName + "' to be visible");
    }

    @Then("the {string} button should be visible")
    public void theButtonShouldBeVisible(String buttonName) {
        Page page = context.getPage();
        Locator btn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(buttonName));
        try {
            btn.first().scrollIntoViewIfNeeded();
            btn.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) {
            // Fall through to assertion
        }
        Assert.assertTrue(btn.first().isVisible(),
                "Expected button '" + buttonName + "' to be visible");
    }

    @Then("the {string} tab should be visible")
    public void theTabShouldBeVisible(String tabName) {
        Page page = context.getPage();
        Locator tab = page.getByRole(AriaRole.TAB,
                new Page.GetByRoleOptions().setName(tabName));
        Assert.assertTrue(tab.isVisible(),
                "Expected tab '" + tabName + "' to be visible");
    }

    @Then("the {string} link should be visible")
    public void theLinkShouldBeVisible(String linkName) {
        Page page = context.getPage();
        Locator link = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName(linkName));
        Assert.assertTrue(link.first().isVisible(),
                "Expected link '" + linkName + "' to be visible");
    }

    // -- Click steps --

    @When("I click the {string} button")
    public void iClickTheButton(String buttonName) {
        Page page = context.getPage();
        Locator btn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(buttonName));
        btn.first().scrollIntoViewIfNeeded();
        btn.first().click();
        page.waitForLoadState();
    }

    @When("I click the {string} button at index {int}")
    public void iClickTheButtonAtIndex(String buttonName, int index) {
        Page page = context.getPage();
        Locator btn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(buttonName)).nth(index);
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState();
    }

    @When("I click the {string} image link")
    public void iClickTheImageLink(String imgName) {
        Page page = context.getPage();
        Locator img = page.getByRole(AriaRole.IMG,
                new Page.GetByRoleOptions().setName(imgName));
        img.first().scrollIntoViewIfNeeded();
        img.first().click();
        page.waitForLoadState();
    }

    @When("I click the {string} footer link")
    public void iClickTheFooterLink(String linkName) {
        Page page = context.getPage();
        Locator link = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName(linkName));
        link.first().scrollIntoViewIfNeeded();
        link.first().click();
        page.waitForLoadState();
    }

    @When("I click the {string} link")
    public void iClickTheLink(String linkName) {
        Page page = context.getPage();
        Locator link = page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName(linkName));
        link.first().scrollIntoViewIfNeeded();
        link.first().click();
        page.waitForLoadState();
    }
}
