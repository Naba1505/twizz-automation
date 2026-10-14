package cucumber.steps;

import org.testng.Assert;

import cucumber.context.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for authentication gate modals.
 */
public class AuthModalSteps {

    private final TestContext context;

    public AuthModalSteps(TestContext context) {
        this.context = context;
    }

    @Then("the registration modal should be visible")
    public void theRegistrationModalShouldBeVisible() {
        Assert.assertTrue(context.getAuthModalPage().isRegistrationModalVisible(),
                "Registration modal is not visible");
    }

    @Then("the {string} text should be visible")
    public void theTextShouldBeVisibleOnModal(String text) {
        // This is for modal-specific text that might conflict with common "text should be visible"
        // The common step handles generic text; this handles modal context
        boolean visible = false;
        try {
            visible = context.getPage().getByText(text).first().isVisible();
        } catch (Exception e) {
            // Fall through
        }
        Assert.assertTrue(visible, "Expected text '" + text + "' to be visible on modal");
    }

    @Then("the {string} button should be visible on the modal")
    public void theButtonShouldBeVisibleOnModal(String buttonName) {
        if (buttonName.contains("With my mail")) {
            Assert.assertTrue(context.getAuthModalPage().isWithMyMailButtonVisible(),
                    "'With my mail' button is not visible on modal");
        } else if (buttonName.contains("With Google")) {
            Assert.assertTrue(context.getAuthModalPage().isWithGoogleButtonVisible(),
                    "'With Google' button is not visible on modal");
        }
    }

    @Then("the {string} text should be visible on the modal")
    public void theTextShouldBeVisibleOnTheModal(String text) {
        if (text.contains("Already have an account")) {
            Assert.assertTrue(context.getAuthModalPage().isAlreadyHaveAccountVisible(),
                    "'Already have an account?' is not visible on modal");
        }
    }

    @Then("the {string} link should be visible on the modal")
    public void theLinkShouldBeVisibleOnModal(String linkName) {
        if (linkName.contains("Sign in")) {
            Assert.assertTrue(context.getAuthModalPage().isSignInLinkVisible(),
                    "'Sign in' link is not visible on modal");
        }
    }

    @When("I click the Settings icon on bottom nav")
    public void iClickTheSettingsIcon() {
        context.getPublicDiscoverPage().clickSettingsIcon();
    }
}
