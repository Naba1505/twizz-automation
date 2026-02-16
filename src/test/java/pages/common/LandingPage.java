package pages.common;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

public class LandingPage {
    private static final Logger logger = LoggerFactory.getLogger(LandingPage.class);
    private final Page page;

    private final String twizzLogo = "role=img[name='Twizz']";
    private final String creatorRegistrationButton = "role=button[name='Creator']";
    private final String fansRegistrationButton = "role=button[name='Fans']";
    private final String loginButton = "text='Login'";

    public LandingPage(Page page) {
        this.page = page;
    }

    public void navigate() {
        String landingPageUrl = ConfigReader.getLandingPageUrl();
        page.navigate(landingPageUrl);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getDefaultTimeout()));
        logger.info("Navigated to landing page: {}", landingPageUrl);
    }

    public void waitForPageToLoad() {
        page.waitForSelector(twizzLogo, new Page.WaitForSelectorOptions().setTimeout(ConfigReader.getDefaultTimeout()));
        logger.info("Landing page loaded successfully.");
    }

    public boolean isTwizzLogoVisible() {
        boolean isVisible = page.isVisible(twizzLogo);
        logger.info("Twizz logo visibility: {}", isVisible);
        return isVisible;
    }

    public void clickCreatorRegistrationButton() {
        page.click(creatorRegistrationButton);
        logger.info("Clicked on Creator Registration button.");
    }

    public void clickFansRegistrationButton() {
        page.click(fansRegistrationButton);
        logger.info("Clicked on Fans Registration button.");
    }

    public void clickLoginButton() {
        page.click(loginButton);
        logger.info("Clicked on Login button.");
    }
}
