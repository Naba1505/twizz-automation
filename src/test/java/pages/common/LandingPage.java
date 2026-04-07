package pages.common;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import utils.ConfigReader;

public class LandingPage extends BasePage {

    private final String twizzLogo = "role=img[name='Twizz']";
    private final String creatorRegistrationButton = "role=button[name='Creator']";
    private final String fansRegistrationButton = "role=button[name='Fans']";
    private final String loginButton = "text='Login'";

    public LandingPage(Page page) {
        super(page);
    }

    public void navigate() {
        String landingPageUrl = ConfigReader.getLandingPageUrl();
        page.navigate(landingPageUrl);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getDefaultTimeout()));
        logger.info("Navigated to landing page: {}", landingPageUrl);
    }

    public void waitForPageToLoad() {
        page.waitForSelector(twizzLogo, new Page.WaitForSelectorOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        logger.info("Landing page loaded successfully.");
    }

    public boolean isTwizzLogoVisible() {
        boolean isVisible = page.isVisible(twizzLogo);
        logger.info("Twizz logo visibility: {}", isVisible);
        return isVisible;
    }

    public void clickCreatorRegistrationButton() {
        page.click(creatorRegistrationButton);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        logger.info("Clicked on Creator Registration button.");
    }

    public void clickFansRegistrationButton() {
        page.click(fansRegistrationButton);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        logger.info("Clicked on Fans Registration button.");
    }

    public void clickLoginButton() {
        // Wait for login button to be visible and clickable
        page.waitForSelector(loginButton, new Page.WaitForSelectorOptions()
            .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
            .setTimeout(ConfigReader.getVisibilityTimeout()));
        
        // Small stabilization wait to ensure button is fully interactive
        page.waitForTimeout(300);
        
        page.click(loginButton);
        
        // Wait for navigation to complete
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        
        logger.info("Clicked on Login button.");
    }

    public boolean isOnCreatorRegistrationPage() {
        return page.url().contains("/auth/signUp?currentTab=creator");
    }

    public boolean isOnFanRegistrationPage() {
        return page.url().contains("/auth/signUp") && !page.url().contains("currentTab=creator");
    }

    public boolean isOnLoginPage() {
        return page.url().contains("/auth/signIn");
    }
}
