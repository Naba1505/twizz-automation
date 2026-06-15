package pages.common;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import utils.ConfigReader;

public class LandingPage extends BasePage {

    private static final String TWIZZ_LOGO_NAME = "Twizz";
    private static final String CREATOR_BTN_NAME = "Creator";
    private static final String FANS_BTN_NAME = "Fans";
    private static final String LOGIN_BTN_TEXT = "Login";

    public LandingPage(Page page) {
        super(page);
    }

    public void navigate() {
        String landingPageUrl = ConfigReader.getLandingPageUrl();
        page.navigate(landingPageUrl);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Navigated to landing page: {}", landingPageUrl);
    }

    public void waitForPageToLoad() {
        Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(TWIZZ_LOGO_NAME));
        logo.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        logger.info("Landing page loaded successfully.");
    }

    public boolean isTwizzLogoVisible() {
        Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(TWIZZ_LOGO_NAME));
        try {
            logo.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getShortTimeout()));
        } catch (Exception e) { logger.debug("Logo wait failed: {}", e.getMessage()); }
        boolean isVisible = logo.isVisible();
        logger.info("Twizz logo visibility: {}", isVisible);
        return isVisible;
    }

    public void clickCreatorRegistrationButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CREATOR_BTN_NAME)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked on Creator Registration button.");
    }

    public void clickFansRegistrationButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(FANS_BTN_NAME)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked on Fans Registration button.");
    }

    public void clickLoginButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(LOGIN_BTN_TEXT)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked on Login button.");
    }

    public boolean isOnCreatorRegistrationPage() {
        try {
            page.waitForURL("**/auth/signUp**currentTab=creator**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
        } catch (Exception e) { logger.debug("URL wait failed: {}", e.getMessage()); }
        return page.url().contains("/auth/signUp?currentTab=creator");
    }

    public boolean isOnFanRegistrationPage() {
        try {
            page.waitForURL("**/auth/signUp**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
        } catch (Exception e) { logger.debug("URL wait failed: {}", e.getMessage()); }
        return page.url().contains("/auth/signUp") && !page.url().contains("currentTab=creator");
    }

    public boolean isOnLoginPage() {
        try {
            page.waitForURL("**/auth/signIn**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
        } catch (Exception e) { logger.debug("URL wait failed: {}", e.getMessage()); }
        return page.url().contains("/auth/signIn");
    }
}
