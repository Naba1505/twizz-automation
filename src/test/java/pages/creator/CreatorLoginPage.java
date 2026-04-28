package pages.creator;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import utils.ConfigReader;

public class CreatorLoginPage extends BasePage {

    // Timeout constants (in milliseconds) - Standardized values
    private static final int NAVIGATION_WAIT = 100; // Navigation delays

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
                page.navigate(url, new Page.NavigateOptions().setTimeout(60000)); // 60s timeout
                page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(30000));
                logger.info("Navigated to Creator Login page: {}", url);
                return;
            } catch (Exception e) {
                if (i == maxRetries - 1) throw e;
                logger.warn("Navigation attempt {} failed, retrying...", i + 1);
                page.waitForTimeout(2000);
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
            page.waitForTimeout(1000);
            logger.info("Cleared session and reloaded page for clean state");
        } catch (Exception e) {
            logger.warn("Failed to clear session/refresh: {}", e.getMessage());
        }
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
            waitVisible(logo, ConfigReader.getVisibilityTimeout());
            waitVisible(loginText, ConfigReader.getVisibilityTimeout());
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

        // Fill credentials with robust waits to prevent race conditions
        Locator user = page.getByPlaceholder(usernamePlaceholder);
        Locator pass = page.getByPlaceholder(passwordPlaceholder);
        
        // Use full visibility timeout and ensure fields are ready
        waitVisible(user, ConfigReader.getVisibilityTimeout());
        waitVisible(pass, ConfigReader.getVisibilityTimeout());
        
        // Wait for fields to be enabled (not disabled/readonly)
        user.waitFor(new Locator.WaitForOptions()
            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
            .setTimeout(ConfigReader.getVisibilityTimeout()));
        pass.waitFor(new Locator.WaitForOptions()
            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
            .setTimeout(ConfigReader.getVisibilityTimeout()));
        
        // Small stabilization wait to ensure fields are fully interactive
        page.waitForTimeout(500);
        
        // Fill username with proper clearing and slower typing
        try {
            user.click();
            page.waitForTimeout(200); // Wait after click
            user.fill(""); // Clear first
            page.waitForTimeout(100);
            user.type(username, new Locator.TypeOptions().setDelay(50)); // Type with 50ms delay between keys
        } catch (Throwable t) {
            logger.warn("Username fill failed, using fallback: {}", t.getMessage());
            fillByPlaceholder(usernamePlaceholder, username);
        }
        
        // Fill password with proper clearing and slower typing
        try {
            pass.click();
            page.waitForTimeout(200); // Wait after click
            pass.fill(""); // Clear first
            page.waitForTimeout(100);
            pass.type(password, new Locator.TypeOptions().setDelay(50)); // Type with 50ms delay between keys
        } catch (Throwable t) {
            logger.warn("Password fill failed, using fallback: {}", t.getMessage());
            fillByPlaceholder(passwordPlaceholder, password);
        }
        
        // Final stabilization before clicking connect
        page.waitForTimeout(300);

        // Click Connect with robust wait and retry
        Locator connect = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(connectButtonName).setExact(true));
        waitVisible(connect, ConfigReader.getVisibilityTimeout());
        try { 
            clickWithRetry(connect, 2, 500); // Increased retries and delay
        } catch (Throwable ignored) { 
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
        } catch (Exception ignored) {
            // Strategy 3: Check URL pattern
            try { 
                page.waitForURL("**/creator/**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout())); 
                visible = true; 
            } catch (Exception e) { 
                logger.debug("URL check failed, using fallback");
            }
        }
        
        if (!visible) {
            // Final fallback: at least wait for DOM
            try { 
                page.waitForLoadState(LoadState.DOMCONTENTLOADED); 
            } catch (Exception ignored) {}
        }
        
        // Additional stabilization wait to ensure page is fully settled
        page.waitForTimeout(1000);
        
        logger.info("Clicked Connect; post-login UI visible: {}", visible);
    }

    public boolean isHandleVisible(String handleWithAt) {
        try {
            Locator handle = page.getByText(handleWithAt, new Page.GetByTextOptions().setExact(true));
            waitVisible(handle, ConfigReader.getVisibilityTimeout());
            boolean visible = handle.isVisible();
            logger.info("Handle '{}' visible: {}", handleWithAt, visible);
            return visible;
        } catch (Exception e) {
            logger.warn("Handle '{}' not visible: {}", handleWithAt, e.getMessage());
            return false;
        }
    }
}

