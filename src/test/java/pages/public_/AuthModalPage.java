package pages.public_;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import pages.common.BasePage;
import utils.ConfigReader;

/**
 * Page object for the registration gate modal that appears when unauthenticated fans
 * try to interact with restricted features (Messaging, Settings, Subscribe).
 * Shows "Register to see/chat on Twizz" with email/Google/sign-in options.
 */
public class AuthModalPage extends BasePage {

    private static final String REGISTER_TO_SEE = "Register to see the content";
    private static final String REGISTER_TO_CHAT = "Register to chat on Twizz";
    private static final String WITH_MY_MAIL_BTN = "With my mail";
    private static final String WITH_GOOGLE_BTN = "With Google";
    private static final String SIGN_IN_TEXT = "Sign in";
    private static final String ALREADY_ACCOUNT = "Already have an account?";

    public AuthModalPage(Page page) {
        super(page);
    }

    public boolean isRegistrationModalVisible() {
        Locator seeContent = page.getByText(REGISTER_TO_SEE);
        Locator chatContent = page.getByText(REGISTER_TO_CHAT);
        try {
            // Wait for either modal to appear
            page.waitForTimeout(ConfigReader.getUiSettleTimeout());
        } catch (Exception e) { logger.debug("Modal settle wait: {}", e.getMessage()); }
        return safeIsVisible(seeContent) || safeIsVisible(chatContent);
    }

    public boolean isRegisterToSeeContentVisible() {
        Locator text = page.getByText(REGISTER_TO_SEE);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Register to see content wait failed: {}", e.getMessage()); }
        return safeIsVisible(text);
    }

    public boolean isRegisterToChatVisible() {
        Locator text = page.getByText(REGISTER_TO_CHAT);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Register to chat wait failed: {}", e.getMessage()); }
        return safeIsVisible(text);
    }

    public boolean isWithMyMailButtonVisible() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(WITH_MY_MAIL_BTN));
        return safeIsVisible(btn);
    }

    public boolean isWithGoogleButtonVisible() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(WITH_GOOGLE_BTN));
        return safeIsVisible(btn);
    }

    public boolean isSignInLinkVisible() {
        Locator signIn = page.getByText(SIGN_IN_TEXT);
        return safeIsVisible(signIn);
    }

    public boolean isAlreadyHaveAccountVisible() {
        Locator text = page.getByText(ALREADY_ACCOUNT);
        return safeIsVisible(text);
    }

    public void clickWithMyMailButton() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(WITH_MY_MAIL_BTN)).click();
        page.waitForLoadState();
        logger.info("Clicked 'With my mail'. URL: {}", page.url());
    }

    public void clickSignInLink() {
        page.getByText(SIGN_IN_TEXT).last().click();
        page.waitForLoadState();
        logger.info("Clicked 'Sign in' link. URL: {}", page.url());
    }

    public String getModalTitle() {
        Locator seeContent = page.getByText(REGISTER_TO_SEE);
        Locator chatContent = page.getByText(REGISTER_TO_CHAT);
        if (safeIsVisible(seeContent)) return seeContent.first().textContent().trim();
        if (safeIsVisible(chatContent)) return chatContent.first().textContent().trim();
        return "";
    }
}
