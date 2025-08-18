package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;
import java.util.regex.Pattern;

public class FanLoginPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(FanLoginPage.class);

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
        logger.info("Navigated to Fan Login page: {}", url);
    }

    public boolean isLoginHeaderVisible() {
        try {
            Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(twizzLogoRoleName));
            Locator loginText = page.getByText(loginTextExact, new Page.GetByTextOptions().setExact(true));
            waitVisible(logo, 20000);
            waitVisible(loginText, 20000);
            return logo.isVisible() && loginText.isVisible();
        } catch (Exception e) {
            logger.warn("Login header not visible: {}", e.getMessage());
            return false;
        }
    }

    public boolean isLoginFormVisible() {
        return page.getByPlaceholder(usernamePlaceholder).isVisible()
                && page.getByPlaceholder(passwordPlaceholder).isVisible();
    }

    public void login(String username, String password) {
        logger.info("Fan login attempt for: {}", username);
        fillByPlaceholder(usernamePlaceholder, username);
        fillByPlaceholder(passwordPlaceholder, password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(connectButtonName).setExact(true)).click();
        // Do not rely on NETWORKIDLE; wait for destination URL instead
        waitForFanHomeUrl(20_000);
    }

    public boolean isOnFanHomeUrl(long timeoutMs) {
        try {
            page.waitForURL(Pattern.compile(".*/fan/home.*"), new Page.WaitForURLOptions().setTimeout(timeoutMs));
            boolean ok = page.url().contains("/fan/home");
            logger.info("Fan login landed on URL: {} (ok={})", page.url(), ok);
            return ok;
        } catch (Exception e) {
            logger.warn("Did not reach /fan/home within {} ms: {} (actual URL: {})", timeoutMs, e.getMessage(), page.url());
            return false;
        }
    }

    public void waitForFanHomeUrl(long timeoutMs) {
        if (!isOnFanHomeUrl(timeoutMs)) {
            throw new IllegalStateException("Fan did not land on /fan/home after login. Actual URL: " + page.url());
        }
    }
}
