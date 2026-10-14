package cucumber.steps;

import org.testng.Assert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import cucumber.context.TestContext;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import utils.ConfigReader;

/**
 * Step definitions specific to the landing page.
 */
public class LandingPageSteps {

    private final TestContext context;

    public LandingPageSteps(TestContext context) {
        this.context = context;
    }

    @Then("the Twizz logo should be visible")
    public void theTwizzLogoShouldBeVisible() {
        Assert.assertTrue(context.getLandingPage().isTwizzLogoVisible(),
                "Twizz logo is not visible");
    }

    @Then("the hero hand image should be visible")
    public void theHeroHandImageShouldBeVisible() {
        Page page = context.getPage();
        Locator handImg = page.locator("img[alt='Hand']");
        try {
            handImg.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) {
            // Fall through
        }
        Assert.assertTrue(handImg.isVisible(), "Hero hand image is not visible");
    }

    @Then("the Twizz logo should link to {string}")
    public void theTwizzLogoShouldLinkTo(String href) {
        Page page = context.getPage();
        Locator logoLink = page.getByRole(AriaRole.LINK).filter(
                new Locator.FilterOptions().setHas(
                        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Twizz"))
                )
        );
        String linkHref = logoLink.first().getAttribute("href");
        Assert.assertTrue(linkHref != null && linkHref.contains(href),
                "Expected logo to link to '" + href + "' but href was: " + linkHref);
    }

    @Then("the Home icon should be visible on discover page")
    public void theHomeIconShouldBeVisibleOnDiscoverPage() {
        Assert.assertTrue(context.getLandingPage().isHomeIconVisible(),
                "Home icon is not visible");
    }

    @Then("the language selector should show {string}")
    public void theLanguageSelectorShouldShow(String language) {
        Assert.assertTrue(context.getLandingPage().isLanguageSelectorVisible(language),
                "Language selector does not show: " + language);
    }

    @When("I switch language from {string} to {string}")
    public void iSwitchLanguage(String from, String to) {
        context.getLandingPage().openLanguageDropdown(from);
        context.getLandingPage().selectLanguage(to);
        // Wait for page to re-render with new language
        try { context.getPage().waitForTimeout(ConfigReader.getUiSettleTimeout()); }
        catch (Exception ignored) {}
    }

    @Then("the French landing text should be visible")
    public void theFrenchLandingTextShouldBeVisible() {
        Assert.assertTrue(context.getLandingPage().isFrenchLandingTextVisible(),
                "French landing text is not visible");
    }

    @Then("the English landing text should be visible")
    public void theEnglishLandingTextShouldBeVisible() {
        Assert.assertTrue(context.getLandingPage().isEnglishLandingTextVisible(),
                "English landing text is not visible");
    }
}
