package pages;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import utils.ConfigReader;

public class FanRegistrationPage extends BasePage {

    // Locators (using helpers by placeholder/button name when needed)

    public FanRegistrationPage(Page page) {
        super(page);
    }

    public void navigate() {
        String url = ConfigReader.getFanSignupUrl();
        page.navigate(url);
        page.waitForLoadState();
        logger.info("Navigated to Fan Registration page: {}", url);
    }

    public boolean isFanRegistrationFormVisible() {
        try {
            // Primary: look for visible text 'Registration'
            Locator regText = getByTextExact("Registration");
            waitVisible(regText, 15000);
            logger.info("Fan registration form visible via text 'Registration'");
            return true;
        } catch (Exception e) {
            // Fallback: registration button visibility
            Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Registration"));
            boolean btnVisible = btn.count() > 0 && btn.first().isVisible();
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
        } catch (Exception ignore) {
            logger.info("Password eye-invisible toggle not clickable/visible, continuing");
        }
    }

    public void submitFanRegistration() {
        clickButtonByName("Registration");
        logger.info("Clicked Registration button (fan)");
        page.waitForLoadState();
    }

    public boolean isHomeVisibleForUser(String username) {
        // URL-based verification for reliability
        try {
            page.waitForURL(Pattern.compile(".*/fan/home.*"), new Page.WaitForURLOptions().setTimeout(20000));
            boolean onHome = page.url().contains("/fan/home");
            logger.info("Fan '{}' landed on URL: {} (onHome={})", username, page.url(), onHome);
            return onHome;
        } catch (Exception e) {
            logger.warn("Fan '{}' did not reach /fan/home within timeout: {} (actual URL: {})", username, e.getMessage(), page.url());
            return false;
        }
    }

    public void completeFanRegistrationFlow(String firstName, String lastName, String username, String email, String password) {
        logger.info("Starting fan registration flow for username: {}", username);
        navigate();
        if (!isFanRegistrationFormVisible()) {
            throw new IllegalStateException("Fan registration form not visible");
        }
        fillFanRegistrationForm(firstName, lastName, username, email, password);
        submitFanRegistration();
        // Wait for fan home URL instead of LIVE marker
        if (!isHomeVisibleForUser(username)) {
            throw new IllegalStateException("Fan did not land on /fan/home after registration. Actual URL: " + page.url());
        }
        logger.info("Fan registration flow completed successfully for username: {}", username);
    }
}
