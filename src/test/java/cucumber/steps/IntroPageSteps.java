package cucumber.steps;

import org.testng.Assert;

import cucumber.context.TestContext;
import io.cucumber.java.en.Then;

/**
 * Step definitions for the intro page.
 */
public class IntroPageSteps {

    private final TestContext context;

    public IntroPageSteps(TestContext context) {
        this.context = context;
    }

    @Then("the Twizz logo should be visible on intro page")
    public void theTwizzLogoShouldBeVisibleOnIntro() {
        Assert.assertTrue(context.getIntroPage().isTwizzLogoVisible(),
                "Twizz logo is not visible on intro page");
    }

    @Then("the language selector should be visible on intro page")
    public void theLanguageSelectorShouldBeVisibleOnIntro() {
        Assert.assertTrue(context.getIntroPage().isLanguageSelectorVisible(),
                "Language selector is not visible on intro page");
    }
}
