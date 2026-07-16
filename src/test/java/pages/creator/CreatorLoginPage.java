package pages.creator;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import utils.ConfigReader;

public class CreatorLoginPage extends BasePage {

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
        
        // Try navigation with retry for slow network conditions
        int maxRetries = 3;
        for (int i = 0; i < maxRetries; i++) {
            try {
                page.navigate(url, new Page.NavigateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
                page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getDefaultTimeout()));
                logger.info("Navigated to Creator Login page: {}", url);
                return;
            } catch (Exception e) {
                if (i == maxRetries - 1) throw e;
                logger.warn("Navigation attempt {} failed, retrying...", i + 1);
                page.waitForTimeout(ConfigReader.getRetryDelay());
            }
        }
    }

    /**
     * Clear session cookies and reload page - use this explicitly when needed for retry scenarios
     */
    public void clearSessionAndReload() {
        try {
            page.context().clearCookies();
            page.reload(new Page.ReloadOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            page.waitForTimeout(ConfigReader.getUiSettleTimeout());
            logger.info("Cleared session and reloaded page for clean state");
        } catch (Exception e) {
            logger.warn("Failed to clear session/refresh: {}", e.getMessage());
        }
    }

    public boolean isLoginFormVisible() {
        Locator user = page.getByPlaceholder(usernamePlaceholder);
        Locator pass = page.getByPlaceholder(passwordPlaceholder);
        boolean visible = safeIsVisible(user) && safeIsVisible(pass);
        logger.info("Login form visible: {}", visible);
        return visible;
    }

    /**
     * Verifies the user is on the login screen by ensuring Twizz logo and Login text are visible.
     */
    public boolean isLoginHeaderVisible() {
        Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(twizzLogoRoleName));
        Locator loginText = page.getByText(loginTextExact, new Page.GetByTextOptions().setExact(true));
        boolean ok = safeIsVisible(logo) && safeIsVisible(loginText);
        logger.info("Login header visible (logo and text): {}", ok);
        return ok;
    }

    public void login(String username, String password) {
        logger.info("Attempting login for user: {}", username);
        
        // If already logged-in marker is visible, skip login
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        if (safeIsVisible(plusImg)) {
            logger.info("Already logged in; skipping credential entry");
            return;
        }

        // Fill credentials with robust waits to prevent race conditions
        Locator user = page.getByPlaceholder(usernamePlaceholder);
        Locator pass = page.getByPlaceholder(passwordPlaceholder);
        
        // Wait for fields to be visible and interactive
        waitVisible(user, ConfigReader.getVisibilityTimeout());
        waitVisible(pass, ConfigReader.getVisibilityTimeout());

        // Fill username
        try {
            user.click();
            user.fill(username);
        } catch (Exception e) {
            logger.warn("Username fill failed, using fallback: {}", e.getMessage());
            fillByPlaceholder(usernamePlaceholder, username);
        }

        // Fill password
        try {
            pass.click();
            pass.fill(password);
        } catch (Exception e) {
            logger.warn("Password fill failed, using fallback: {}", e.getMessage());
            fillByPlaceholder(passwordPlaceholder, password);
        }

        // Click Connect with robust wait and retry
        Locator connect = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(connectButtonName).setExact(true));
        waitVisible(connect, ConfigReader.getVisibilityTimeout());
        try {
            clickWithRetry(connect, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
        } catch (Exception e) {
            logger.warn("Click with retry failed, trying direct click: {}", e.getMessage());
            connect.click();
        }

        // Wait for post-login page to fully load with multiple strategies
        boolean visible = false;
        
        // Strategy 1: Wait for network to settle
        try {
            page.waitForLoadState(LoadState.NETWORKIDLE, 
                new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getShortTimeout()));
        } catch (Exception e) {
            logger.debug("Network idle timeout, continuing with other checks");
        }
        
        // Strategy 2: Wait for plus icon (primary success indicator)
        try {
            waitVisible(plusImg, ConfigReader.getVisibilityTimeout());
            visible = true;
        } catch (Exception e) {
            logger.debug("Plus icon wait failed, trying URL check: {}", e.getMessage());
            // Strategy 3: Check URL pattern
            try { 
                page.waitForURL("**/creator/**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout())); 
                visible = true; 
            } catch (Exception e2) { 
                logger.debug("URL check failed, using fallback: {}", e2.getMessage());
            }
        }
        
        if (!visible) {
            // Final fallback: at least wait for DOM
            try {
                page.waitForLoadState(LoadState.DOMCONTENTLOADED,
                        new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
            } catch (Exception e) { logger.debug("DOM load fallback failed: {}", e.getMessage()); }
        }
        
        // Additional stabilization wait to ensure page is fully settled
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Exception ignored) {}
        
        logger.info("Clicked Connect; post-login UI visible: {}", visible);
    }

    public boolean isHandleVisible(String handleWithAt) {
        Locator handle = page.getByText(handleWithAt, new Page.GetByTextOptions().setExact(true));
        boolean visible = safeIsVisible(handle);
        logger.info("Handle '{}' visible: {}", handleWithAt, visible);
        return visible;
    }
}

