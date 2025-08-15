package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

public class CreatorLoginPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(CreatorLoginPage.class);

    private final String usernamePlaceholder = "Email address or username";
    private final String passwordPlaceholder = "Password";
    private final String connectButtonName = "Connect";
    private final String twizzLogoRoleName = "Twizz"; // used with AriaRole.IMG
    private final String loginTextExact = "Login";    // exact text on screen

    public CreatorLoginPage(Page page) {
        super(page);
    }

    public void navigate() {
        // Use env-specific login URL with safe fallback
        String url = ConfigReader.getLoginUrl();
        page.navigate(url);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        logger.info("Navigated to Creator Login page: {}", url);
    }

    public boolean isLoginFormVisible() {
        Locator user = page.getByPlaceholder(usernamePlaceholder);
        Locator pass = page.getByPlaceholder(passwordPlaceholder);
        boolean visible = user.isVisible() && pass.isVisible();
        logger.info("Login form visible: {}", visible);
        return visible;
    }

    /**
     * Verifies the user is on the login screen by ensuring Twizz logo and Login text are visible.
     */
    public boolean isLoginHeaderVisible() {
        try {
            Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(twizzLogoRoleName));
            Locator loginText = page.getByText(loginTextExact, new Page.GetByTextOptions().setExact(true));
            waitVisible(logo, 20000);
            waitVisible(loginText, 20000);
            boolean ok = logo.isVisible() && loginText.isVisible();
            logger.info("Login header visible (logo and text): {}", ok);
            return ok;
        } catch (Exception e) {
            logger.warn("Login header not visible: {}", e.getMessage());
            return false;
        }
    }

    public void login(String username, String password) {
        logger.info("Attempting login for user: {}", username);
        fillByPlaceholder(usernamePlaceholder, username);
        fillByPlaceholder(passwordPlaceholder, password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(connectButtonName).setExact(true)).click();
        // Avoid NETWORKIDLE due to potential long-polling; wait for a reliable post-login marker instead
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        boolean visible = false;
        try {
            waitVisible(plusImg, 20000);
            visible = true;
        } catch (Exception ignored) { }
        if (!visible) {
            // Fallback to a lighter load-state to not hang
            try { page.waitForLoadState(LoadState.DOMCONTENTLOADED); } catch (Exception ignored) {}
        }
        logger.info("Clicked Connect; post-login UI visible: {}", visible);
    }

    public boolean isHandleVisible(String handleWithAt) {
        try {
            Locator handle = page.getByText(handleWithAt, new Page.GetByTextOptions().setExact(true));
            waitVisible(handle, 20000);
            boolean visible = handle.isVisible();
            logger.info("Handle '{}' visible: {}", handleWithAt, visible);
            return visible;
        } catch (Exception e) {
            logger.warn("Handle '{}' not visible: {}", handleWithAt, e.getMessage());
            return false;
        }
    }
}
