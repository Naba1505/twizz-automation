package pages.business.manager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Page Object for Twizz Business Manager Login
 * URL: https://devbusiness.twizz.app/auth/sign-in
 */
public class BusinessManagerLoginPage {
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerLoginPage.class);
    private final Page page;

    public BusinessManagerLoginPage(Page page) {
        this.page = page;
    }

    @Step("Navigate to Business Sign In page")
    public void navigateToSignIn() {
        String signInUrl = ConfigReader.getBusinessLoginUrl();
        page.navigate(signInUrl);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("[Business Manager Login] Navigated to Sign In page: {}", signInUrl);
    }

    @Step("Verify login page heading")
    public boolean isLoginPageVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Connection"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business Manager Login] Login page heading 'Connection' visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Fill username: {username}")
    public void fillUsername(String username) {
        Locator usernameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address or username"));
        usernameField.click();
        usernameField.fill(username);
        logger.info("[Business Manager Login] Filled username: {}", username);
    }

    @Step("Fill password")
    public void fillPassword(String password) {
        Locator passwordField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password"));
        passwordField.click();
        passwordField.fill(password);
        logger.info("[Business Manager Login] Filled password: [HIDDEN]");
    }

    @Step("Click Login button")
    public void clickLogin() {
        Locator loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login").setExact(true));
        loginButton.click();
        page.waitForLoadState(LoadState.LOAD);
        
        // Wait for URL to change to manager dashboard
        try {
            page.waitForURL("**/manager**", new Page.WaitForURLOptions().setTimeout(10000));
        } catch (Exception e) {
            logger.warn("[Business Manager Login] Did not navigate to manager dashboard within timeout. Current URL: {}", page.url());
        }
        
        page.waitForTimeout(2000); // Additional wait for page to settle
        logger.info("[Business Manager Login] Clicked Login button");
    }

    @Step("Verify manager dashboard URL")
    public boolean isOnManagerDashboard() {
        String currentUrl = page.url();
        boolean isCorrectUrl = currentUrl.contains("/manager");
        logger.info("[Business Manager Login] Current URL: {}, Expected to contain: /manager, Match: {}", 
            currentUrl, isCorrectUrl);
        return isCorrectUrl;
    }

    @Step("Verify welcome message is visible")
    public boolean isWelcomeMessageVisible(String expectedName) {
        Locator welcomeMessage = page.getByText("Hello " + expectedName);
        // Wait for the welcome message to appear
        try {
            welcomeMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Business Manager Login] Welcome message did not appear within timeout");
        }
        boolean isVisible = welcomeMessage.isVisible();
        logger.info("[Business Manager Login] Welcome message 'Hello {}' visibility: {}", expectedName, isVisible);
        return isVisible;
    }

    @Step("Perform complete login flow")
    public void login(String username, String password) {
        navigateToSignIn();
        fillUsername(username);
        fillPassword(password);
        clickLogin();
        logger.info("[Business Manager Login] Completed login flow for user: {}", username);
    }
}
