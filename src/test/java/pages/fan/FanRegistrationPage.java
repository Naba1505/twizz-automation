package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;
import utils.TestDataManager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

public class FanRegistrationPage extends BasePage {

    // Locators (using helpers by placeholder/button name when needed)

    public FanRegistrationPage(Page page) {
        super(page);
    }

    public void navigate() {
        String url = ConfigReader.getFanSignupUrl();
        page.navigate(url, new Page.NavigateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        page.waitForLoadState();
        logger.info("Navigated to Fan Registration page: {}", url);
    }

    public boolean isFanRegistrationFormVisible() {
        try {
            // Primary: look for visible text 'Registration'
            Locator regText = getByTextExact("Registration");
            waitVisible(regText, ConfigReader.getShortTimeout());
            logger.info("Fan registration form visible via text 'Registration'");
            return true;
        } catch (Exception e) {
            // Fallback: registration button visibility
            Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Registration"));
            boolean btnVisible = btn.count() > 0 && safeIsVisible(btn.first());
            logger.info("Fan registration form button visibility: {}", btnVisible);
            return btnVisible;
        }
    }

    public void fillFanRegistrationForm(String firstName, String lastName, String username, String email, String password) {
        fillByPlaceholder("First name", firstName);
        fillByPlaceholder("Last name", lastName);
        fillByPlaceholder("User name", username);
        fillByPlaceholder("Email address", email);
        fillByPlaceholder("Password", password);

        // Try toggling the eye icon if present (non-blocking)
        try {
            page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("eye-invisible")).locator("svg").click();
            logger.info("Clicked password visibility toggle (eye-invisible)");
        } catch (Exception e) {
            logger.debug("Password eye-invisible toggle not clickable/visible: {}", e.getMessage());
        }
    }

    public void submitFanRegistration() {
        clickButtonByName("Registration");
        logger.info("Clicked Registration button (fan)");
        page.waitForLoadState();
    }

    public boolean isHomeVisibleForUser(String username) {
        // Use Home icon visibility as success indicator (fan may land on /fan/home or /common/discover)
        try {
            Locator homeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Home icon"));
            waitVisible(homeIcon.first(), ConfigReader.getShortTimeout());
            boolean visible = safeIsVisible(homeIcon.first());
            logger.info("Fan '{}' registration successful - Home icon visible: {} (URL: {})", username, visible, page.url());
            return visible;
        } catch (Exception e) {
            logger.warn("Fan '{}' Home icon not visible within timeout: {} (actual URL: {})", username, e.getMessage(), page.url());
            return false;
        }
    }

    public void assertHomeVisible() {
        Locator homeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Home icon"));
        waitVisible(homeIcon.first(), ConfigReader.getShortTimeout());
        if (!safeIsVisible(homeIcon.first())) {
            throw new AssertionError("Fan did not land on home after registration - Home icon not visible. Actual URL: " + page.url());
        }
        logger.info("Home icon visible after registration (URL: {})", page.url());
    }

    public void completeFanRegistrationFlow(String firstName, String lastName, String username, String email, String password) {
        logger.info("Starting fan registration flow for username: {}", username);
        navigate();
        if (!isFanRegistrationFormVisible()) {
            throw new IllegalStateException("Fan registration form not visible");
        }
        fillFanRegistrationForm(firstName, lastName, username, email, password);
        submitFanRegistration();
        if (!isHomeVisibleForUser(username)) {
            throw new IllegalStateException("Fan registration failed - Home icon not visible. Actual URL: " + page.url());
        }
        TestDataManager.saveFanUsername(username);
        logger.info("Fan registration flow completed successfully for username: {}", username);
    }
}
