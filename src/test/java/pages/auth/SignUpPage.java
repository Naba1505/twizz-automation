package pages.auth;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import pages.common.BasePage;
import utils.ConfigReader;

/**
 * Page object for the sign-up/registration page (/auth/signUp).
 * Supports both Fan and Creator tabs with full registration form.
 */
public class SignUpPage extends BasePage {

    private static final String SIGNUP_URL_PART = "/auth/signUp";
    private static final String HEADING_TEXT = "Sign Up on Twizz";
    private static final String REGISTRATION_LABEL = "Registration";
    private static final String CREATOR_TAB = "Creator";
    private static final String FAN_TAB = "Fan";
    private static final String GOOGLE_BTN = "Continue with Google";
    private static final String OR_SEPARATOR = "OR";
    private static final String FIRST_NAME_PLACEHOLDER = "First name";
    private static final String LAST_NAME_PLACEHOLDER = "Last name";
    private static final String EMAIL_PLACEHOLDER = "Email address";
    private static final String USERNAME_PLACEHOLDER = "User name";
    private static final String PASSWORD_PLACEHOLDER = "Password";
    private static final String REGISTRATION_BTN = "Registration";
    private static final String TERMS_CHECKBOX_TEXT = "By registering, you declare that";
    private static final String EYE_ICON = "eye-invisible";
    private static final String ARROW_LEFT = "arrow left";

    public SignUpPage(Page page) {
        super(page);
    }

    public boolean isOnSignUpPage() {
        return page.url().contains(SIGNUP_URL_PART);
    }

    public boolean isHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(HEADING_TEXT));
        try {
            heading.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Sign up heading wait failed: {}", e.getMessage()); }
        return safeIsVisible(heading);
    }

    public boolean isRegistrationLabelVisible() {
        return safeIsVisible(page.getByText(REGISTRATION_LABEL).first());
    }

    public boolean isCreatorTabVisible() {
        return safeIsVisible(page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(CREATOR_TAB)));
    }

    public boolean isFanTabVisible() {
        return safeIsVisible(page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(FAN_TAB)));
    }

    public boolean isFanTabSelected() {
        Locator tab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(FAN_TAB));
        try {
            String selected = tab.getAttribute("aria-selected");
            return "true".equals(selected);
        } catch (Exception e) {
            logger.debug("Fan tab selected check failed: {}", e.getMessage());
            return false;
        }
    }

    public void clickCreatorTab() {
        page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(CREATOR_TAB)).click();
        logger.info("Clicked Creator tab");
    }

    public void clickFanTab() {
        page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(FAN_TAB)).click();
        logger.info("Clicked Fan tab");
    }

    public boolean isGoogleButtonVisible() {
        return safeIsVisible(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(GOOGLE_BTN)));
    }

    public boolean isOrSeparatorVisible() {
        return safeIsVisible(page.getByText(OR_SEPARATOR));
    }

    public boolean isFirstNameFieldVisible() {
        return safeIsVisible(page.getByPlaceholder(FIRST_NAME_PLACEHOLDER));
    }

    public boolean isLastNameFieldVisible() {
        return safeIsVisible(page.getByPlaceholder(LAST_NAME_PLACEHOLDER));
    }

    public boolean isEmailFieldVisible() {
        return safeIsVisible(page.getByPlaceholder(EMAIL_PLACEHOLDER));
    }

    public boolean isUsernameFieldVisible() {
        return safeIsVisible(page.getByPlaceholder(USERNAME_PLACEHOLDER));
    }

    public boolean isPasswordFieldVisible() {
        return safeIsVisible(page.getByPlaceholder(PASSWORD_PLACEHOLDER));
    }

    public boolean isRegistrationButtonVisible() {
        return safeIsVisible(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(REGISTRATION_BTN)));
    }

    public boolean isRegistrationButtonDisabled() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(REGISTRATION_BTN));
        try {
            return btn.isDisabled();
        } catch (Exception e) {
            logger.debug("Registration button disabled check failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTermsCheckboxVisible() {
        return safeIsVisible(page.getByText(TERMS_CHECKBOX_TEXT));
    }

    public boolean isEyeIconVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(EYE_ICON)));
    }

    public boolean isLanguageSelectorVisible() {
        return safeIsVisible(page.getByText("English"));
    }

    public boolean isBackArrowVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(ARROW_LEFT)));
    }

    // -- Form interaction for fan registration --

    public void fillFirstName(String firstName) {
        page.getByPlaceholder(FIRST_NAME_PLACEHOLDER).fill(firstName);
        logger.info("Filled first name: {}", firstName);
    }

    public void fillLastName(String lastName) {
        page.getByPlaceholder(LAST_NAME_PLACEHOLDER).fill(lastName);
        logger.info("Filled last name: {}", lastName);
    }

    public void fillEmail(String email) {
        page.getByPlaceholder(EMAIL_PLACEHOLDER).fill(email);
        logger.info("Filled email: {}", email);
    }

    public void fillUsername(String username) {
        page.getByPlaceholder(USERNAME_PLACEHOLDER).fill(username);
        logger.info("Filled username: {}", username);
    }

    public void fillPassword(String password) {
        page.getByPlaceholder(PASSWORD_PLACEHOLDER).fill(password);
        logger.info("Filled password");
    }

    public void clickRegistrationButton() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(REGISTRATION_BTN)).click();
        page.waitForLoadState();
        logger.info("Clicked Registration button. URL: {}", page.url());
    }

    public void ensureTermsChecked() {
        Locator checkbox = page.getByRole(AriaRole.CHECKBOX);
        try {
            if (!checkbox.isChecked()) {
                checkbox.click();
                logger.info("Checked terms and conditions");
            }
        } catch (Exception e) {
            logger.debug("Terms checkbox interaction failed: {}", e.getMessage());
        }
    }

    /**
     * Complete fan registration form.
     */
    public void registerFan(String firstName, String lastName, String email, String username, String password) {
        clickFanTab();
        fillFirstName(firstName);
        fillLastName(lastName);
        fillEmail(email);
        fillUsername(username);
        fillPassword(password);
        ensureTermsChecked();
        // Small wait for form validation
        try { page.waitForTimeout(500); } catch (Exception ignored) {}
        clickRegistrationButton();
        logger.info("Fan registration submitted for: {} ({})", username, email);
    }
}
