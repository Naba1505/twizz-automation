package pages.business;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Page Object for Twizz Business Landing Page
 * URL: https://devbusiness.twizz.app/
 */
public class BusinessLandingPage {
    private static final Logger logger = LoggerFactory.getLogger(BusinessLandingPage.class);
    private final Page page;

    public BusinessLandingPage(Page page) {
        this.page = page;
    }

    @Step("Navigate to Twizz Business landing page")
    public void navigate() {
        String landingPageUrl = ConfigReader.getBusinessLandingPageUrl();
        page.navigate(landingPageUrl);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("[Business] Navigated to landing page: {}", landingPageUrl);
    }

    @Step("Wait for Business landing page to load")
    public void waitForPageToLoad() {
        // Wait for the main heading to be visible
        Locator heading = page.getByText("Designed for managers");
        heading.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        logger.info("[Business] Landing page loaded successfully");
    }

    @Step("Verify Twizz Business logo is visible")
    public boolean isLogoVisible() {
        // The logo is the second image on the page (nth(1) = index 1)
        Locator logo = page.getByRole(AriaRole.IMG).nth(1);
        boolean isVisible = logo.isVisible();
        logger.info("[Business] Logo visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify 'Designed for managers' heading is visible")
    public boolean isMainHeadingVisible() {
        Locator heading = page.getByText("Designed for managers");
        boolean isVisible = heading.isVisible();
        logger.info("[Business] 'Designed for managers' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Contact Us link")
    public void clickContactUs() {
        Locator contactUsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Contact us").setExact(true));
        contactUsLink.getByRole(AriaRole.BUTTON).click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Contact Us link");
    }

    @Step("Verify user is on Contact page")
    public boolean isOnContactPage() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("We are in different places"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business] Contact page heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Login button")
    public void clickLogin() {
        Locator loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));
        loginButton.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Login button");
    }

    @Step("Verify user is on Login page")
    public boolean isOnLoginPage() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Connection"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business] Login page heading 'Connection' visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Register button")
    public void clickRegister() {
        Locator registerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register"));
        registerButton.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Register button");
    }

    @Step("Verify user is on Registration page")
    public boolean isOnRegistrationPage() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Inscription"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business] Registration page heading 'Inscription' visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Manager tab on Registration page")
    public void clickManagerTab() {
        Locator managerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Manager"));
        managerButton.click();
        logger.info("[Business] Clicked Manager tab on Registration page");
    }

    @Step("Navigate back to landing page")
    public void navigateBack() {
        page.goBack();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Navigated back");
    }
}
