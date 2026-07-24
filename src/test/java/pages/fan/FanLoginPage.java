package pages.fan;

import pages.common.BasePage;

import java.util.List;

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
        Locator connectBtn = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(connectButtonName).setExact(true))
                .filter(new Locator.FilterOptions().setVisible(true)).first();
        clickConnectButton(connectBtn);
        waitForFanDiscoverUrl(ConfigReader.getMediumTimeout());
        if (!isHomeIconVisible(ConfigReader.getShortTimeout())) {
            throw new IllegalStateException("Fan login succeeded but Home icon not visible. Actual URL: " + page.url());
        }
    }

    private void clickConnectButton(Locator connectBtn) {
        List<Locator> buttons = connectBtn.locator("xpath=..")
                .locator("xpath=.")
                .page()
                .getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(connectButtonName).setExact(true))
                .filter(new Locator.FilterOptions().setVisible(true))
                .all();
        if (buttons.isEmpty()) {
            buttons = java.util.Collections.singletonList(connectBtn);
        }
        for (int i = 0; i < buttons.size(); i++) {
            try {
                buttons.get(i).evaluate("el => { el.scrollIntoView({behavior: 'instant', block: 'center'}); el.click(); }");
                logger.info("[Fan] Clicked Connect button #{} via JS", i + 1);
                waitForAnimation();
                if (!page.url().contains("/auth/signIn")) {
                    return;
                }
            } catch (Throwable e) {
                logger.debug("[Fan] Connect button #{} JS click failed: {}", i + 1, e.getMessage());
            }
        }
        if (page.url().contains("/auth/signIn")) {
            logger.warn("[Fan] Falling back to pressing Enter to submit login");
            try {
                Locator passField = page.getByPlaceholder(passwordPlaceholder).first();
                passField.focus();
                page.keyboard().press("Enter");
            } catch (Throwable e) {
                logger.debug("[Fan] Enter fallback failed: {}", e.getMessage());
            }
        }
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

    public boolean isOnFanDiscoverUrl(long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (page.url().contains("/common/discover")) {
                logger.info("[Fan] Login landed on URL: {} (ok=true)", page.url());
                return true;
            }
            waitForAnimation();
        }
        logger.warn("[Fan] Did not reach /common/discover within {} ms (actual URL: {})", timeoutMs, page.url());
        return false;
    }

    public void waitForFanDiscoverUrl(long timeoutMs) {
        if (!isOnFanDiscoverUrl(timeoutMs)) {
            throw new IllegalStateException("Fan did not land on /common/discover after login. Actual URL: " + page.url());
        }
    }
}
