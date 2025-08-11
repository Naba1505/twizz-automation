package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

public class FanRegistrationPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(FanRegistrationPage.class);

    // Locators
    private final String firstNameInput = "[placeholder='First name']";
    private final String lastNameInput = "[placeholder='Last name']";
    private final String userNameInput = "[placeholder='User name']";
    private final String emailInput = "[placeholder='Email address']";
    private final String passwordInput = "[placeholder='Password']";
    private final String registrationButton = "role=button[name='Registration']";

    public FanRegistrationPage(Page page) { super(page); }

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
            boolean btnVisible = page.locator(registrationButton).isVisible();
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
        // Use ONLY the exact "LIVE" text to assert home screen per user request
        try {
            Locator live = getByTextExact("LIVE");
            waitVisible(live, 20000);
            boolean visible = live.isVisible();
            logger.info("Home visible via 'LIVE' marker for user '{}': {}", username, visible);
            return visible;
        } catch (Exception e) {
            logger.warn("'LIVE' marker not visible on home for user '{}': {}", username, e.getMessage());
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
        if (!isHomeVisibleForUser(username)) {
            throw new IllegalStateException("Home screen not visible for user '" + username + "' after registration");
        }
        logger.info("Fan registration flow completed successfully for username: {}", username);
    }
}
