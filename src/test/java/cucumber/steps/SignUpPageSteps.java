package cucumber.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import cucumber.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.ConfigReader;
import utils.DataGenerator;

/**
 * Step definitions for the sign-up page and fan registration flow.
 */
public class SignUpPageSteps {

    private static final Logger logger = LoggerFactory.getLogger(SignUpPageSteps.class);
    private final TestContext context;
    private String generatedEmail;
    private String generatedUsername;

    public SignUpPageSteps(TestContext context) {
        this.context = context;
    }

    @Then("the {string} label should be visible on sign-up page")
    public void theLabelShouldBeVisibleOnSignUp(String label) {
        if (label.equals("Registration")) {
            Assert.assertTrue(context.getSignUpPage().isRegistrationLabelVisible(),
                    "Registration label is not visible");
        }
    }

    @Then("the {string} tab should be selected")
    public void theTabShouldBeSelected(String tabName) {
        if (tabName.equals("Fan")) {
            Assert.assertTrue(context.getSignUpPage().isFanTabSelected(),
                    "Fan tab is not selected by default");
        }
    }

    @Then("the {string} separator should be visible on sign-up page")
    public void theSeparatorShouldBeVisibleOnSignUp(String separator) {
        Assert.assertTrue(context.getSignUpPage().isOrSeparatorVisible(),
                "'" + separator + "' separator is not visible on sign-up page");
    }

    @Then("the first name field should be visible")
    public void theFirstNameFieldShouldBeVisible() {
        Assert.assertTrue(context.getSignUpPage().isFirstNameFieldVisible(),
                "First name field is not visible");
    }

    @Then("the last name field should be visible")
    public void theLastNameFieldShouldBeVisible() {
        Assert.assertTrue(context.getSignUpPage().isLastNameFieldVisible(),
                "Last name field is not visible");
    }

    @Then("the email field should be visible on sign-up page")
    public void theEmailFieldShouldBeVisibleOnSignUp() {
        Assert.assertTrue(context.getSignUpPage().isEmailFieldVisible(),
                "Email field is not visible on sign-up page");
    }

    @Then("the username field should be visible")
    public void theUsernameFieldShouldBeVisible() {
        Assert.assertTrue(context.getSignUpPage().isUsernameFieldVisible(),
                "Username field is not visible on sign-up page");
    }

    @Then("the password field should be visible on sign-up page")
    public void thePasswordFieldShouldBeVisibleOnSignUp() {
        Assert.assertTrue(context.getSignUpPage().isPasswordFieldVisible(),
                "Password field is not visible on sign-up page");
    }

    @Then("the password eye toggle should be visible on sign-up page")
    public void thePasswordEyeToggleShouldBeVisibleOnSignUp() {
        Assert.assertTrue(context.getSignUpPage().isEyeIconVisible(),
                "Password eye toggle is not visible on sign-up page");
    }

    @Then("the terms and conditions checkbox should be visible")
    public void theTermsAndConditionsCheckboxShouldBeVisible() {
        Assert.assertTrue(context.getSignUpPage().isTermsCheckboxVisible(),
                "Terms and conditions checkbox is not visible");
    }

    @Then("the {string} button should be disabled")
    public void theButtonShouldBeDisabled(String buttonName) {
        if (buttonName.equals("Registration")) {
            Assert.assertTrue(context.getSignUpPage().isRegistrationButtonDisabled(),
                    "Registration button is not disabled");
        }
    }

    @Then("the language selector should be visible on sign-up page")
    public void theLanguageSelectorShouldBeVisibleOnSignUp() {
        Assert.assertTrue(context.getSignUpPage().isLanguageSelectorVisible(),
                "Language selector is not visible on sign-up page");
    }

    @Then("the back arrow should be visible on sign-up page")
    public void theBackArrowShouldBeVisibleOnSignUp() {
        Assert.assertTrue(context.getSignUpPage().isBackArrowVisible(),
                "Back arrow is not visible on sign-up page");
    }

    // -- Fan registration flow --

    @Given("I am on the Fan tab")
    public void iAmOnTheFanTab() {
        context.getSignUpPage().clickFanTab();
    }

    @When("I fill in the fan registration form with random data")
    public void iFillInTheFanRegistrationFormWithRandomData() {
        String firstName = DataGenerator.generateUniqueFirstName();
        String lastName = DataGenerator.generateUniqueLastName();
        generatedEmail = DataGenerator.generateUniqueEmail("fan");
        generatedUsername = DataGenerator.generateUniqueUsername("fan");
        String password = ConfigReader.getProperty("fan.default.password", "Twizz$123");

        context.getSignUpPage().fillFirstName(firstName);
        context.getSignUpPage().fillLastName(lastName);
        context.getSignUpPage().fillEmail(generatedEmail);
        context.getSignUpPage().fillUsername(generatedUsername);
        context.getSignUpPage().fillPassword(password);
        context.getSignUpPage().ensureTermsChecked();

        logger.info("Fan registration form filled: email={}, username={}", generatedEmail, generatedUsername);
    }

    @When("I click the Registration button")
    public void iClickTheRegistrationButton() {
        // Wait for form validation to enable the button
        try { context.getPage().waitForTimeout(1000); } catch (Exception ignored) {}
        context.getSignUpPage().clickRegistrationButton();
    }

    @Then("the registration should proceed successfully")
    public void theRegistrationShouldProceedSuccessfully() {
        // After registration, the user should be redirected away from the sign-up page
        // This could be to a welcome page, profile setup, or the discover page
        try {
            context.getPage().waitForURL(url -> !url.contains("/auth/signUp"),
                    new com.microsoft.playwright.Page.WaitForURLOptions()
                            .setTimeout(ConfigReader.getMediumTimeout()));
        } catch (Exception e) {
            logger.warn("Post-registration redirect wait: {}", e.getMessage());
        }

        String currentUrl = context.getPage().url();
        logger.info("Post-registration URL: {}", currentUrl);
        // Verify we're no longer on the sign-up page OR a success indicator is present
        boolean leftSignUp = !currentUrl.contains("/auth/signUp");
        boolean hasSuccessIndicator = currentUrl.contains("/discover") ||
                currentUrl.contains("/profile") ||
                currentUrl.contains("/auth/intro") ||
                currentUrl.contains("/home");

        Assert.assertTrue(leftSignUp || hasSuccessIndicator,
                "Registration did not proceed. Still on: " + currentUrl);
    }
}
