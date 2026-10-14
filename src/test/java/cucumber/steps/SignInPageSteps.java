package cucumber.steps;

import org.testng.Assert;

import cucumber.context.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for the sign-in page.
 */
public class SignInPageSteps {

    private final TestContext context;

    public SignInPageSteps(TestContext context) {
        this.context = context;
    }

    @Then("the {string} label should be visible")
    public void theLabelShouldBeVisible(String label) {
        if (label.equals("Login")) {
            Assert.assertTrue(context.getSignInPage().isLoginLabelVisible(),
                    "Login label is not visible");
        }
    }

    @Then("the email field should be visible on sign-in page")
    public void theEmailFieldShouldBeVisibleOnSignIn() {
        Assert.assertTrue(context.getSignInPage().isEmailFieldVisible(),
                "Email field is not visible on sign-in page");
    }

    @Then("the password field should be visible on sign-in page")
    public void thePasswordFieldShouldBeVisibleOnSignIn() {
        Assert.assertTrue(context.getSignInPage().isPasswordFieldVisible(),
                "Password field is not visible on sign-in page");
    }

    @Then("the {string} button should be visible on sign-in page")
    public void theButtonShouldBeVisibleOnSignIn(String buttonName) {
        Assert.assertTrue(context.getSignInPage().isConnectButtonVisible(),
                "'" + buttonName + "' button is not visible on sign-in page");
    }

    @Then("the {string} separator should be visible")
    public void theSeparatorShouldBeVisible(String separator) {
        Assert.assertTrue(context.getSignInPage().isOrSeparatorVisible(),
                "'" + separator + "' separator is not visible");
    }

    @Then("the password eye toggle should be visible")
    public void thePasswordEyeToggleShouldBeVisible() {
        Assert.assertTrue(context.getSignInPage().isEyeIconVisible(),
                "Password eye toggle is not visible");
    }

    @Then("the {string} checkbox should be visible")
    public void theCheckboxShouldBeVisible(String label) {
        if (label.equals("Remember me")) {
            Assert.assertTrue(context.getSignInPage().isRememberMeVisible(),
                    "Remember me checkbox is not visible");
        }
    }

    @Then("the {string} checkbox should be checked by default")
    public void theCheckboxShouldBeChecked(String label) {
        if (label.equals("Remember me")) {
            Assert.assertTrue(context.getSignInPage().isRememberMeChecked(),
                    "Remember me checkbox is not checked by default");
        }
    }

    @Then("the back arrow should be visible on sign-in page")
    public void theBackArrowShouldBeVisibleOnSignIn() {
        Assert.assertTrue(context.getSignInPage().isBackArrowVisible(),
                "Back arrow is not visible on sign-in page");
    }

    @When("I click the {string} link on sign-in page")
    public void iClickTheLinkOnSignInPage(String linkName) {
        if (linkName.equals("Sign up")) {
            context.getSignInPage().clickSignUpLink();
        }
    }
}
