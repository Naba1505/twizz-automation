package pages.fan;

import pages.common.BasePage;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import utils.ConfigReader;

public class FanLoginPage extends BasePage {

    private final String usernamePlaceholder = "Email address or username";
    private final String passwordPlaceholder = "Password";
    private final String connectButtonName = "Connect";
    private final String twizzLogoRoleName = "Twizz"; // used with AriaRole.IMG
    private final String loginTextExact = "Login";    // exact text on screen

    public FanLoginPage(Page page) {
        super(page);
    }

    public void navigate() {
        String url = ConfigReader.getLoginUrl();
        navigateAndWait(url);
    }

    public boolean isLoginHeaderVisible() {
        try {
            Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(twizzLogoRoleName));
            Locator loginText = page.getByText(loginTextExact, new Page.GetByTextOptions().setExact(true));
            waitVisible(logo, ConfigReader.getShortTimeout());
            waitVisible(loginText, ConfigReader.getShortTimeout());
            return safeIsVisible(logo) && safeIsVisible(loginText);
        } catch (Exception e) {
            logger.warn("[Fan] Login header not visible: {}", e.getMessage());
            return false;
        }
    }

    public boolean isLoginFormVisible() {
        Locator userField = page.getByPlaceholder(usernamePlaceholder).first();
        Locator passField = page.getByPlaceholder(passwordPlaceholder).first();
        try {
            waitVisible(userField, ConfigReader.getShortTimeout());
            waitVisible(passField, ConfigReader.getShortTimeout());
            return safeIsVisible(userField) && safeIsVisible(passField);
        } catch (Exception e) {
            logger.warn("[Fan] Login form not visible: {}", e.getMessage());
            return false;
        }
    }

    public void login(String username, String password) {
        logger.info("[Fan] Login attempt for: {}", username);
        typeAndAssert(page.getByPlaceholder(usernamePlaceholder).first(), username);
        typeAndAssert(page.getByPlaceholder(passwordPlaceholder).first(), password);
        Locator connectBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(connectButtonName).setExact(true)).first();
        clickWithRetry(connectBtn, 1, ConfigReader.getElementRetryDelay());
        waitForFanHomeUrl(ConfigReader.getMediumTimeout());
    }

    public boolean isHomeIconVisible(long timeoutMs) {
        Locator homeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Home icon")).first();
        try {
            waitVisible(homeIcon, timeoutMs);
            logger.info("[Fan] Login successful - Home icon visible (URL: {})", page.url());
            return true;
        } catch (Exception e) {
            logger.warn("[Fan] Home icon not visible within {} ms: {} (actual URL: {})", timeoutMs, e.getMessage(), page.url());
            return false;
        }
    }

    public void waitForHomeIconVisible(long timeoutMs) {
        if (!isHomeIconVisible(timeoutMs)) {
            throw new IllegalStateException("Fan login failed - Home icon not visible. Actual URL: " + page.url());
        }
    }

    public void assertHomeIconVisible() {
        if (!isHomeIconVisible(ConfigReader.getShortTimeout())) {
            throw new AssertionError("Fan login failed - Home icon not visible. Actual URL: " + page.url());
        }
    }

    public boolean isOnFanHomeUrl(long timeoutMs) {
        try {
            page.waitForURL(Pattern.compile(".*(/fan/home|/common/discover).*"), new Page.WaitForURLOptions().setTimeout(timeoutMs));
            boolean ok = page.url().contains("/fan/home") || page.url().contains("/common/discover");
            logger.info("[Fan] Login landed on URL: {} (ok={})", page.url(), ok);
            return ok;
        } catch (Exception e) {
            logger.warn("[Fan] Did not reach fan home within {} ms: {} (actual URL: {})", timeoutMs, e.getMessage(), page.url());
            return false;
        }
    }

    public void waitForFanHomeUrl(long timeoutMs) {
        if (!isOnFanHomeUrl(timeoutMs)) {
            throw new IllegalStateException("Fan did not land on /fan/home after login. Actual URL: " + page.url());
        }
    }
}
