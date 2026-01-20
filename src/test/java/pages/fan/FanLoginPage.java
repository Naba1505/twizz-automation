package pages.fan;

import pages.common.BasePage;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;

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
        page.navigate(url);
        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
        logger.info("[Fan] Navigated to Fan Login page: {}", url);
    }

    public boolean isLoginHeaderVisible() {
        try {
            Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(twizzLogoRoleName));
            Locator loginText = page.getByText(loginTextExact, new Page.GetByTextOptions().setExact(true));
            waitVisible(logo, 20000);
            waitVisible(loginText, 20000);
            return logo.isVisible() && loginText.isVisible();
        } catch (Exception e) {
            logger.warn("[Fan] Login header not visible: {}", e.getMessage());
            return false;
        }
    }

    public boolean isLoginFormVisible() {
        return page.getByPlaceholder(usernamePlaceholder).isVisible()
                && page.getByPlaceholder(passwordPlaceholder).isVisible();
    }

    public void login(String username, String password) {
        logger.info("[Fan] Login attempt for: {}", username);
        fillByPlaceholder(usernamePlaceholder, username);
        fillByPlaceholder(passwordPlaceholder, password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(connectButtonName).setExact(true)).click();
        // Wait for Home icon to be visible - this confirms successful login
        // Fan may land on /fan/home or /common/discover, so use Home icon as success indicator
        waitForHomeIconVisible(20_000);
    }

    public boolean isHomeIconVisible(long timeoutMs) {
        try {
            Locator homeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Home icon"));
            homeIcon.first().waitFor(new Locator.WaitForOptions().setTimeout(timeoutMs).setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
            boolean visible = homeIcon.first().isVisible();
            logger.info("[Fan] Login successful - Home icon visible: {} (URL: {})", visible, page.url());
            return visible;
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

    public boolean isOnFanHomeUrl(long timeoutMs) {
        try {
            page.waitForURL(Pattern.compile(".*/fan/home.*"), new Page.WaitForURLOptions().setTimeout(timeoutMs));
            boolean ok = page.url().contains("/fan/home");
            logger.info("[Fan] Login landed on URL: {} (ok={})", page.url(), ok);
            return ok;
        } catch (Exception e) {
            logger.warn("[Fan] Did not reach /fan/home within {} ms: {} (actual URL: {})", timeoutMs, e.getMessage(), page.url());
            return false;
        }
    }

    public void waitForFanHomeUrl(long timeoutMs) {
        if (!isOnFanHomeUrl(timeoutMs)) {
            throw new IllegalStateException("Fan did not land on /fan/home after login. Actual URL: " + page.url());
        }
    }
}
