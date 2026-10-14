package pages.auth;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import pages.common.BasePage;
import utils.ConfigReader;

/**
 * Page object for the intro/welcome page (/auth/intro).
 * Shows Connect, Continue with Google, and Free registration options.
 */
public class IntroPage extends BasePage {

    private static final String INTRO_URL_PART = "/auth/intro";
    private static final String HEADING_TEXT = "Join Twizz";
    private static final String CONTENT_TEXT = "Content by the thousand";
    private static final String CONNECT_BTN = "Connect";
    private static final String GOOGLE_BTN_TEXT = "Continue with Google";
    private static final String FREE_REGISTRATION_LINK = "Free registration";
    private static final String TWIZZ_LOGO = "Twizz";

    public IntroPage(Page page) {
        super(page);
    }

    public boolean isOnIntroPage() {
        return page.url().contains(INTRO_URL_PART);
    }

    public boolean isHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(HEADING_TEXT));
        try {
            heading.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Intro heading wait failed: {}", e.getMessage()); }
        return safeIsVisible(heading);
    }

    public boolean isTwizzLogoVisible() {
        Locator logo = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(TWIZZ_LOGO));
        try {
            logo.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Intro logo wait failed: {}", e.getMessage()); }
        return safeIsVisible(logo);
    }

    public boolean isContentTextVisible() {
        return safeIsVisible(page.getByText(CONTENT_TEXT));
    }

    public boolean isConnectButtonVisible() {
        return safeIsVisible(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONNECT_BTN)));
    }

    public boolean isGoogleButtonVisible() {
        return safeIsVisible(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(GOOGLE_BTN_TEXT)));
    }

    public boolean isFreeRegistrationLinkVisible() {
        return safeIsVisible(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(FREE_REGISTRATION_LINK)));
    }

    public void clickConnectButton() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONNECT_BTN)).click();
        page.waitForLoadState();
        logger.info("Clicked Connect. URL: {}", page.url());
    }

    public void clickFreeRegistration() {
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(FREE_REGISTRATION_LINK)).click();
        page.waitForLoadState();
        logger.info("Clicked Free registration. URL: {}", page.url());
    }

    public boolean isLanguageSelectorVisible() {
        Locator selector = page.getByText("English");
        return safeIsVisible(selector);
    }
}
