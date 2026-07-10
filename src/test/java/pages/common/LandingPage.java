package pages.common;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import utils.ConfigReader;

public class LandingPage extends BasePage {

    private static final String TWIZZ_LOGO_NAME = "Twizz";
    private static final String FANS_BTN_NAME = "Fans";
    private static final String BECOME_A_FAN_BTN = "Become a fan";
    private static final String BECOME_A_CREATOR_BTN = "Become a creator";
    private static final String PUBLIC_DISCOVER_URL_PART = "/public-discover";
    private static final String HOME_ICON_NAME = "Home icon";
    private static final String SEARCH_ICON_NAME = "Search icon";
    private static final String DISCOVER_TWIZZ_BTN = "Discover TWIZZ";
    private static final String REGISTER_TO_SEE_CONTENT_TEXT = "Register to see the content";
    private static final String CONTACT_US_BTN = "Contact us";
    private static final String CONTACT_US_TEXT = "We have offices and teams";
    private static final String CONTACT_US_URL_PART = "/contact";
    private static final String LOGIN_BTN_TEXT = "Login";
    private static final String LOGIN_URL_PART = "/auth/signIn";
    private static final String REGISTER_BTN_TEXT = "Register";
    private static final String SIGNUP_URL_PART = "/auth/signUp";
    private static final String REGISTRATION_TEXT = "Registration";
    private static final String FAN_TAB_NAME = "Fan";
    private static final String CREATOR_TAB_NAME = "Creator";

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

    public void clickLoginButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(LOGIN_BTN_TEXT)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked on Login button.");
    }

    public void clickFansButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(FANS_BTN_NAME)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Fans' button. Current URL: {}", page.url());
    }

    public void clickDiscoverTwizzButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(DISCOVER_TWIZZ_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Discover TWIZZ' button. Current URL: {}", page.url());
    }

    public boolean isSearchIconVisible() {
        Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SEARCH_ICON_NAME));
        try {
            icon.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Search icon wait failed: {}", e.getMessage()); }
        boolean visible = icon.count() > 0 && icon.first().isVisible();
        logger.info("Search icon visible: {}", visible);
        return visible;
    }

    public void clickSearchIcon() {
        Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SEARCH_ICON_NAME));
        icon.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        icon.first().click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked Search icon. Current URL: {}", page.url());
    }

    public boolean isPublicDiscoverUrlCorrect() {
        try {
            page.waitForURL("**" + PUBLIC_DISCOVER_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Public discover URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Public discover URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(PUBLIC_DISCOVER_URL_PART);
        logger.info("isPublicDiscoverUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public boolean isHomeIconVisible() {
        Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(HOME_ICON_NAME));
        try {
            icon.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Home icon wait failed: {}", e.getMessage()); }
        boolean visible = icon.count() > 0 && icon.first().isVisible();
        logger.info("Home icon visible: {}", visible);
        return visible;
    }

    public void clickHomeIcon() {
        Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(HOME_ICON_NAME));
        icon.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        icon.first().click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked Home icon. Current URL: {}", page.url());
    }

    public boolean isRegisterToSeeContentVisible() {
        Locator text = page.getByText(REGISTER_TO_SEE_CONTENT_TEXT);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Register to see content text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Register to see the content' visible: {}", visible);
        return visible;
    }

    public void clickBecomeAFanButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(BECOME_A_FAN_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Become a fan' button. Current URL: {}", page.url());
    }

    public boolean isFanTabVisible() {
        Locator tab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(FAN_TAB_NAME));
        try {
            tab.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Fan tab wait failed: {}", e.getMessage()); }
        boolean visible = tab.count() > 0 && tab.first().isVisible();
        logger.info("Fan tab visible: {}", visible);
        return visible;
    }

    public boolean isFanRegistrationUrlCorrect() {
        try {
            page.waitForURL("**/auth/signUp**currentTab=fan**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Fan registration URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Fan registration URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains("/auth/signUp") && page.url().contains("currentTab=fan");
        logger.info("isFanRegistrationUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public void clickContactUsButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONTACT_US_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Contact us' button. Current URL: {}", page.url());
    }

    public boolean isContactUsTextVisible() {
        Locator text = page.getByText(CONTACT_US_TEXT);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Contact us text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("Contact us text visible: {}", visible);
        return visible;
    }

    public boolean isContactUsUrlCorrect() {
        try {
            page.waitForURL("**" + CONTACT_US_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Contact us URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Contact us URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(CONTACT_US_URL_PART);
        logger.info("isContactUsUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public void clickBecomeACreatorButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(BECOME_A_CREATOR_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Become a creator' button. Current URL: {}", page.url());
    }

    public boolean isCreatorTabVisible() {
        Locator tab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(CREATOR_TAB_NAME));
        try {
            tab.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Creator tab wait failed: {}", e.getMessage()); }
        boolean visible = tab.count() > 0 && tab.first().isVisible();
        logger.info("Creator tab visible: {}", visible);
        return visible;
    }

    public boolean isCreatorRegistrationUrlCorrect() {
        try {
            page.waitForURL("**/auth/signUp**currentTab=creator**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Creator registration URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Creator registration URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains("/auth/signUp") && page.url().contains("currentTab=creator");
        logger.info("isCreatorRegistrationUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public boolean isLoginTextVisible() {
        Locator text = page.getByText(LOGIN_BTN_TEXT);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Login text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("Login page text visible: {}", visible);
        return visible;
    }

    public boolean isLoginUrlCorrect() {
        try {
            page.waitForURL("**" + LOGIN_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Login URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Login URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(LOGIN_URL_PART);
        logger.info("isLoginUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public void clickSecondLoginButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(LOGIN_BTN_TEXT)).nth(1);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked second Login button. Current URL: {}", page.url());
    }

    public void clickRegisterButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(REGISTER_BTN_TEXT).setExact(true)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Register' button. Current URL: {}", page.url());
    }

    public boolean isRegistrationTextVisible() {
        Locator text = page.getByText(REGISTRATION_TEXT).first();
        try {
            text.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Registration text wait failed: {}", e.getMessage()); }
        boolean visible = text.isVisible();
        logger.info("Registration text visible: {}", visible);
        return visible;
    }

    public boolean isSignUpUrlCorrect() {
        try {
            page.waitForURL("**" + SIGNUP_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("SignUp URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("SignUp URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(SIGNUP_URL_PART);
        logger.info("isSignUpUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

}
