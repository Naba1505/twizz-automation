package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

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
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    public boolean isLiveVisible() {
        String liveText = ConfigReader.getProperty("fan.live.text", "LIVE");
        try {
            Locator live = page.getByText(liveText, new Page.GetByTextOptions().setExact(true));
            waitVisible(live, 20000);
            return live.isVisible();
        } catch (Exception e) {
            logger.warn("LIVE indicator not visible: {}", e.getMessage());
            return false;
        }
    }
}
