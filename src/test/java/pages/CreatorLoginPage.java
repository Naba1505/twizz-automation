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
        // If already logged-in marker is visible, skip login
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        try {
            if (plusImg.count() > 0 && plusImg.first().isVisible()) {
                logger.info("Already logged in; skipping credential entry");
                return;
            }
        } catch (Throwable ignored) {}

        // Ensure we are on login screen (form or header visible)
        try {
            if (!isLoginFormVisible()) {
                isLoginHeaderVisible();
            }
        } catch (Throwable ignored) {}

        // Fill credentials with robust clear
        Locator user = page.getByPlaceholder(usernamePlaceholder);
        Locator pass = page.getByPlaceholder(passwordPlaceholder);
        waitVisible(user, 15000);
        waitVisible(pass, 15000);
        try {
            user.click(); user.fill(""); user.press("Control+A"); user.press("Backspace"); user.fill(username);
        } catch (Throwable t) { fillByPlaceholder(usernamePlaceholder, username); }
        try {
            pass.click(); pass.fill(""); pass.press("Control+A"); pass.press("Backspace"); pass.fill(password);
        } catch (Throwable t) { fillByPlaceholder(passwordPlaceholder, password); }

        // Click Connect with a light retry
        Locator connect = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(connectButtonName).setExact(true));
        try { clickWithRetry(connect, 1, 200); } catch (Throwable ignored) { connect.click(); }

        // Avoid NETWORKIDLE; wait for a reliable post-login marker instead
        boolean visible = false;
        try {
            waitVisible(plusImg, 20000);
            visible = true;
        } catch (Exception ignored) {
            // Broaden detection: URL or common dashboard markers
            try { page.waitForURL("**/creator/**", new Page.WaitForURLOptions().setTimeout(10000)); visible = true; } catch (Exception e) { /* ignore */ }
        }
        if (!visible) {
            // Final fallback to a lighter load-state to not hang
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
