package pages.auth;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import pages.common.BasePage;
import utils.ConfigReader;

/**
 * Page object for the sign-in page (/auth/signIn).
 * Email/password login with Google OAuth alternative.
 */
public class SignInPage extends BasePage {

    private static final String SIGNIN_URL_PART = "/auth/signIn";
    private static final String HEADING_TEXT = "Sign In to Twizz";
    private static final String LOGIN_TEXT = "Login";
    private static final String EMAIL_PLACEHOLDER = "Email address or username";
    private static final String PASSWORD_PLACEHOLDER = "Password";
    private static final String CONNECT_BTN = "Connect";
    private static final String GOOGLE_BTN = "Connection with Google";
    private static final String FORGOT_PASSWORD = "Forgot your password?";
    private static final String REMEMBER_ME = "Remember me";
    private static final String SIGN_UP_LINK = "Sign up";
    private static final String NOT_ACCOUNT = "Not account yet";
    private static final String OR_SEPARATOR = "OR";
    private static final String EYE_ICON = "eye-invisible";
    private static final String ARROW_LEFT = "arrow left";

    public SignInPage(Page page) {
        super(page);
    }

    public boolean isOnSignInPage() {
        return page.url().contains(SIGNIN_URL_PART);
    }

    public boolean isHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(HEADING_TEXT));
        try {
            heading.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Sign in heading wait failed: {}", e.getMessage()); }
        return safeIsVisible(heading);
    }

    public boolean isLoginLabelVisible() {
        return safeIsVisible(page.getByText(LOGIN_TEXT).first());
    }

    public boolean isEmailFieldVisible() {
        return safeIsVisible(page.getByPlaceholder(EMAIL_PLACEHOLDER));
    }

    public boolean isPasswordFieldVisible() {
        return safeIsVisible(page.getByPlaceholder(PASSWORD_PLACEHOLDER));
    }

    public boolean isConnectButtonVisible() {
        return safeIsVisible(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONNECT_BTN)));
    }

    public boolean isGoogleButtonVisible() {
        return safeIsVisible(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(GOOGLE_BTN)));
    }

    public boolean isForgotPasswordVisible() {
        return safeIsVisible(page.getByText(FORGOT_PASSWORD));
    }

    public boolean isRememberMeVisible() {
        return safeIsVisible(page.getByText(REMEMBER_ME));
    }

    public boolean isRememberMeChecked() {
        Locator checkbox = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName(REMEMBER_ME));
        try {
            return checkbox.isChecked();
        } catch (Exception e) {
            logger.debug("Remember me check failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isOrSeparatorVisible() {
        Locator separator = page.getByText(OR_SEPARATOR, new Page.GetByTextOptions().setExact(true));
        return safeIsVisible(separator);
    }

    public boolean isEyeIconVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(EYE_ICON)));
    }

    public boolean isSignUpLinkVisible() {
        return safeIsVisible(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(SIGN_UP_LINK)));
    }

    public boolean isNotAccountYetVisible() {
        return safeIsVisible(page.getByText(NOT_ACCOUNT));
    }

    public boolean isBackArrowVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(ARROW_LEFT)));
    }

    public void fillEmail(String email) {
        page.getByPlaceholder(EMAIL_PLACEHOLDER).fill(email);
        logger.info("Filled email: {}", email);
    }

    public void fillPassword(String password) {
        page.getByPlaceholder(PASSWORD_PLACEHOLDER).fill(password);
        logger.info("Filled password");
    }

    public void clickConnect() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONNECT_BTN)).click();
        page.waitForLoadState();
        logger.info("Clicked Connect. URL: {}", page.url());
    }

    public void clickSignUpLink() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(SIGN_UP_LINK)).click();
        page.waitForLoadState();
        logger.info("Clicked Sign up link. URL: {}", page.url());
    }

    public void clickBackArrow() {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(ARROW_LEFT)).click();
        page.waitForLoadState();
        logger.info("Clicked back arrow. URL: {}", page.url());
    }
}
