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
    private static final String BECOMING_A_CREATOR_BTN = "Becoming a creator";
    private static final String BECOMING_A_CREATOR_TEXT = "Becoming a creator ?";
    private static final String PUBLIC_DISCOVER_URL_PART = "/public-discover";
    private static final String HOME_ICON_NAME = "Home icon";
    private static final String DISCOVER_TWIZZ_BTN = "Discover TWIZZ";
    private static final String REGISTER_TO_SEE_CONTENT_TEXT = "Register to see the content";
    private static final String ITS_SIMPLE_TEXT = "It's simple, it's better.";
    private static final String OPEN_APP_BTN = "Open the application";
    private static final String AGENCY_TEXT = "Do you have an agency? Use";
    private static final String SMART_SECURITY_TEXT = "Smart and safe security";
    private static final String ALREADY_TAKING_TEXT = "THEY ARE ALREADY TAKING";
    private static final String USER1_LINK_NAME = "User 1";
    private static final String IF_YOU_HAVE_QUESTIONS_TEXT = "If you have any questions";
    private static final String TO_REGISTER_AS_USER_TEXT = "To register as a user:";
    private static final String START_BY_CREATING_TEXT = "Start by creating a creator";
    private static final String OUR_TEAM_ACTS_TEXT = "Our team acts quickly to";
    private static final String TO_WITHDRAW_MONEY_TEXT = "To withdraw your money on";
    private static final String EXPLORE_TWIZZ_BTN = "Explore Twizz arrow-up";
    private static final String HLS_PLAYER_LOCATOR = ".hls-video-player > div > div";
    private static final String CONTACT_ARROW_UP_LINK = "Contact arrow-up";
    private static final String LEGAL_NOTICES_LINK = "Legal notices arrow-up";
    private static final String LEGAL_INFORMATION_HEADING = "Legal information";
    private static final String LEGAL_NOTICES_URL_PART = "/legal-notices";
    private static final String CONTENT_POLICY_LINK = "Content Policy and Child";
    private static final String CONTENT_POLICY_URL_PART = "/content-policy-and-child-protection";
    private static final String CONTENT_POLICY_HEADING = "CONTENT POLICY v12.06.2024";
    private static final String TWIZZ_USERS_ENCOUNTER_TEXT = "Twizz users who encounter";
    private static final String CONFIDENTIALITY_LINK = "Confidentiality arrow-up";
    private static final String CONFIDENTIALITY_URL_PART = "/confidentiality-and-data-protection";
    private static final String CONFIDENTIALITY_HEADING = "v12.06.2024 made for ITDW by";
    private static final String OUR_PRIVACY_POLICY_TEXT = "Our Privacy Policy may be";
    private static final String GENERAL_CONDITIONS_LINK = "General Conditions of Sale";
    private static final String GENERAL_CONDITIONS_URL_PART = "/general-conditions-of-sale";
    private static final String GENERAL_CONDITIONS_TEXT = "(Other than personal data) only if it has been generated jointly with other";
    private static final String GENERAL_CONDITIONS_USE_LINK = "General Conditions of Use";
    private static final String GENERAL_CONDITIONS_USE_URL_PART = "/general-conditions-of-use";
    private static final String TWIZZ_RECOMMENDS_TEXT = "Twizz therefore recommends";
    private static final String BLOG_LINK = "Blog arrow-up";
    private static final String BLOG_CSS_LOCATOR = "a:nth-child(7) > .contact-item > .ant-btn";
    private static final String BLOGS_URL_PART = "/blogs";
    private static final String TWIZZ_BLOG_TEXT = "TwizzBlog";
    private static final String EARTH_IMG_NAME = "Earth";
    private static final String FRENCH_LANDING_TEXT = "Vous voulez monétiser votre contenu ? Essayez Twizz.Peu importe comment vous";
    private static final String ENGLISH_LANDING_TEXT = "Want to monetize your content? Try Twizz.No matter how you want to convert,";
    private static final String ARROW_UP_BTN = "arrow-up";
    private static final String CONTENT_BY_THOUSAND_TEXT = "Content by the thousand. Free";
    private static final String INTRO_URL_PART = "/auth/intro";
    private static final String GO_TO_TWIZZ_BTN = "Go to Twizz";
    private static final String SIGN_UP_BTN = "Sign up";
    private static final String REGISTER_AS_CREATOR_BTN = "Register as a creator";
    private static final String TO_START_UP_BTN = "To start up";
    private static final String DESIGNED_FOR_MANAGERS_TEXT = "Designed for managers";
    private static final String BUSINESS_URL_PART = "business.twizz.com";
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

    private boolean waitForUrlContains(String urlPart, String label, long timeoutMs) {
        try {
            page.waitForURL("**" + urlPart + "**", new Page.WaitForURLOptions().setTimeout(timeoutMs));
            logger.info("{} URL confirmed: {}", label, page.url());
            return true;
        } catch (Exception e) {
            logger.debug("{} URL wait failed: {}", label, e.getMessage());
        }
        boolean result = page.url().contains(urlPart);
        logger.info("{} URL fallback check - url={}, result={}", label, page.url(), result);
        return result;
    }

    private boolean waitForRegistrationUrl(String currentTab, String label) {
        String pattern = "**/auth/signUp**currentTab=" + currentTab + "**";
        try {
            page.waitForURL(pattern, new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("{} registration URL confirmed: {}", label, page.url());
            return true;
        } catch (Exception e) {
            logger.debug("{} registration URL wait failed: {}", label, e.getMessage());
        }
        boolean result = page.url().contains("/auth/signUp") && page.url().contains("currentTab=" + currentTab);
        logger.info("{} registration URL fallback check - url={}, result={}", label, page.url(), result);
        return result;
    }

    private void clickArrowUpButton(int nth, boolean exact, String logLabel) {
        clickByRole(AriaRole.BUTTON, ARROW_UP_BTN, exact, nth, "Clicked " + logLabel + " 'arrow-up' button");
    }

    private void clickByRole(AriaRole role, String name, boolean exact, int nth, String logMessage) {
        Locator element = page.getByRole(role, new Page.GetByRoleOptions().setName(name).setExact(exact)).nth(nth);
        element.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        element.scrollIntoViewIfNeeded();
        element.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("{}. Current URL: {}", logMessage, page.url());
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
        clickByRole(AriaRole.BUTTON, LOGIN_BTN_TEXT, false, 0, "Clicked on Login button");
    }

    public void clickFansButton() {
        clickByRole(AriaRole.BUTTON, FANS_BTN_NAME, false, 0, "Clicked 'Fans' button");
    }

    public void clickDiscoverTwizzButton() {
        clickByRole(AriaRole.BUTTON, DISCOVER_TWIZZ_BTN, false, 0, "Clicked 'Discover TWIZZ' button");
    }

    public boolean isPublicDiscoverUrlCorrect() {
        return waitForUrlContains(PUBLIC_DISCOVER_URL_PART, "Public discover", ConfigReader.getMediumTimeout());
    }

    public boolean isHomeIconVisible() {
        Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(HOME_ICON_NAME));
        try {
            icon.first().scrollIntoViewIfNeeded();
            icon.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Home icon wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(icon);
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
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Register to see content text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Register to see the content' visible: {}", visible);
        return visible;
    }

    public boolean isVideoVisible() {
        Locator video = page.locator("video");
        try {
            video.first().scrollIntoViewIfNeeded();
            video.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Video wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(video);
        logger.info("Video visible: {}", visible);
        return visible;
    }

    public boolean isItsSimpleTextVisible() {
        Locator text = page.getByText(ITS_SIMPLE_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Its simple text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'It's simple, it's better.' text visible: {}", visible);
        return visible;
    }

    public void clickOpenApplicationButton() {
        clickByRole(AriaRole.BUTTON, OPEN_APP_BTN, false, 0, "Clicked 'Open the application' button");
    }

    public boolean isAgencyTextVisible() {
        Locator text = page.getByText(AGENCY_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Agency text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("Agency text visible: {}", visible);
        return visible;
    }

    public boolean isSmartSecurityTextVisible() {
        Locator text = page.getByText(SMART_SECURITY_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Smart security text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Smart and safe security' text visible: {}", visible);
        return visible;
    }

    public boolean isAlreadyTakingTextVisible() {
        Locator text = page.getByText(ALREADY_TAKING_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Already taking text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'THEY ARE ALREADY TAKING' text visible: {}", visible);
        return visible;
    }

    public boolean isIfYouHaveQuestionsTextVisible() {
        Locator text = page.getByText(IF_YOU_HAVE_QUESTIONS_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("If you have questions text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'If you have any questions' text visible: {}", visible);
        return visible;
    }

    public boolean isToRegisterAsUserTextVisible() {
        Locator text = page.getByText(TO_REGISTER_AS_USER_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("To register as user text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'To register as a user:' text visible: {}", visible);
        return visible;
    }

    public boolean isStartByCreatingTextVisible() {
        Locator text = page.getByText(START_BY_CREATING_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Start by creating text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Start by creating a creator' text visible: {}", visible);
        return visible;
    }

    public boolean isOurTeamActsTextVisible() {
        Locator text = page.getByText(OUR_TEAM_ACTS_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Our team acts text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Our team acts quickly to' text visible: {}", visible);
        return visible;
    }

    public boolean isContentByThousandTextVisible() {
        Locator text = page.getByText(CONTENT_BY_THOUSAND_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Content by thousand text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Content by the thousand. Free' text visible: {}", visible);
        return visible;
    }

    public boolean isIntroUrlCorrect() {
        return waitForUrlContains(INTRO_URL_PART, "Intro", ConfigReader.getMediumTimeout());
    }

    public boolean isToWithdrawMoneyTextVisible() {
        Locator text = page.getByText(TO_WITHDRAW_MONEY_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("To withdraw money text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'To withdraw your money on' text visible: {}", visible);
        return visible;
    }

    public boolean isContactArrowUpLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CONTACT_ARROW_UP_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Contact arrow-up link wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(link);
        logger.info("'Contact arrow-up' link visible: {}", visible);
        return visible;
    }

    public boolean isLegalNoticesLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(LEGAL_NOTICES_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Legal notices link wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(link);
        logger.info("'Legal notices arrow-up' link visible: {}", visible);
        return visible;
    }

    public boolean isLegalInformationHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(LEGAL_INFORMATION_HEADING));
        try {
            heading.first().scrollIntoViewIfNeeded();
            heading.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Legal information heading wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(heading);
        logger.info("'Legal information' heading visible: {}", visible);
        return visible;
    }

    public boolean isLegalNoticesUrlCorrect() {
        return waitForUrlContains(LEGAL_NOTICES_URL_PART, "Legal notices", ConfigReader.getMediumTimeout());
    }

    public boolean isContentPolicyLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CONTENT_POLICY_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Content policy link wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(link);
        logger.info("'Content Policy and Child' link visible: {}", visible);
        return visible;
    }

    public boolean isConfidentialityLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CONFIDENTIALITY_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Confidentiality link wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(link);
        logger.info("'Confidentiality arrow-up' link visible: {}", visible);
        return visible;
    }

    public boolean isGeneralConditionsLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(GENERAL_CONDITIONS_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("General conditions link wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(link);
        logger.info("'General Conditions of Sale' link visible: {}", visible);
        return visible;
    }

    public boolean isGeneralConditionsUseLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(GENERAL_CONDITIONS_USE_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("General conditions of use link wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(link);
        logger.info("'General Conditions of Use' link visible: {}", visible);
        return visible;
    }

    public boolean isEarthImageVisible() {
        Locator img = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(EARTH_IMG_NAME));
        try {
            img.first().scrollIntoViewIfNeeded();
            img.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Earth image wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(img);
        logger.info("'Earth' image visible: {}", visible);
        return visible;
    }

    public boolean isLanguageSelectorVisible(String language) {
        Locator selector = page.locator("div").filter(new Locator.FilterOptions().setHasText(language)).nth(4);
        try {
            selector.scrollIntoViewIfNeeded();
            selector.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Language selector ({}) wait failed: {}", language, e.getMessage()); }
        boolean visible = safeIsVisible(selector);
        logger.info("Language selector '{}' visible: {}", language, visible);
        return visible;
    }

    public void openLanguageDropdown(String currentLanguage) {
        page.locator("div").filter(new Locator.FilterOptions().setHasText(currentLanguage)).nth(5).click();
        logger.info("Opened language dropdown for: {}", currentLanguage);
    }

    public void selectLanguage(String language) {
        page.getByText(language).click();
        logger.info("Selected language: {}", language);
    }

    public boolean isFrenchLandingTextVisible() {
        Locator text = page.getByText(FRENCH_LANDING_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("French landing text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("French landing text visible: {}", visible);
        return visible;
    }

    public boolean isEnglishLandingTextVisible() {
        Locator text = page.getByText(ENGLISH_LANDING_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("English landing text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("English landing text visible: {}", visible);
        return visible;
    }

    public boolean isLanguageSelectorNth5Visible(String language) {
        Locator selector = page.locator("div").filter(new Locator.FilterOptions().setHasText(language)).nth(5);
        try {
            selector.scrollIntoViewIfNeeded();
            selector.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Language selector nth5 ({}) wait failed: {}", language, e.getMessage()); }
        boolean visible = safeIsVisible(selector);
        logger.info("Language selector nth5 '{}' visible: {}", language, visible);
        return visible;
    }

    public boolean isBlogLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(BLOG_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Blog link wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(link);
        logger.info("'Blog arrow-up' link visible: {}", visible);
        return visible;
    }

    public void clickBlogArrowUpButton() {
        Locator btn = page.locator(BLOG_CSS_LOCATOR);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Blog arrow-up' button. Current URL: {}", page.url());
    }

    public boolean isBlogsUrlCorrect() {
        return waitForUrlContains(BLOGS_URL_PART, "Blogs", ConfigReader.getMediumTimeout());
    }

    public boolean isTwizzBlogTextVisible() {
        Locator text = page.getByText(TWIZZ_BLOG_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("TwizzBlog text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'TwizzBlog' text visible: {}", visible);
        return visible;
    }

    public void clickSixthArrowUpButtonExact() {
        clickArrowUpButton(5, true, "sixth exact");
    }

    public boolean isGeneralConditionsUseUrlCorrect() {
        return waitForUrlContains(GENERAL_CONDITIONS_USE_URL_PART, "General conditions of use", ConfigReader.getMediumTimeout());
    }

    public boolean isTwizzRecommendsTextVisible() {
        Locator text = page.getByText(TWIZZ_RECOMMENDS_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Twizz recommends text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Twizz therefore recommends' text visible: {}", visible);
        return visible;
    }

    public void clickSixthArrowUpButton() {
        clickArrowUpButton(5, false, "sixth");
    }

    public boolean isGeneralConditionsUrlCorrect() {
        return waitForUrlContains(GENERAL_CONDITIONS_URL_PART, "General conditions", ConfigReader.getMediumTimeout());
    }

    public boolean isGeneralConditionsTextVisible() {
        Locator text = page.getByText(GENERAL_CONDITIONS_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("General conditions text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("General conditions footer text visible: {}", visible);
        return visible;
    }

    public void clickFifthArrowUpButton() {
        clickArrowUpButton(4, false, "fifth");
    }

    public boolean isConfidentialityUrlCorrect() {
        return waitForUrlContains(CONFIDENTIALITY_URL_PART, "Confidentiality", ConfigReader.getMediumTimeout());
    }

    public boolean isConfidentialityHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(CONFIDENTIALITY_HEADING));
        try {
            heading.first().scrollIntoViewIfNeeded();
            heading.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Confidentiality heading wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(heading);
        logger.info("'v12.06.2024 made for ITDW by' heading visible: {}", visible);
        return visible;
    }

    public boolean isOurPrivacyPolicyTextVisible() {
        Locator text = page.getByText(OUR_PRIVACY_POLICY_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Our privacy policy text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Our Privacy Policy may be' text visible: {}", visible);
        return visible;
    }

    public void clickFourthArrowUpButton() {
        clickArrowUpButton(3, false, "fourth");
    }

    public boolean isContentPolicyUrlCorrect() {
        return waitForUrlContains(CONTENT_POLICY_URL_PART, "Content policy", ConfigReader.getMediumTimeout());
    }

    public boolean isContentPolicyHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(CONTENT_POLICY_HEADING));
        try {
            heading.first().scrollIntoViewIfNeeded();
            heading.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Content policy heading wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(heading);
        logger.info("'CONTENT POLICY v12.06.2024' heading visible: {}", visible);
        return visible;
    }

    public boolean isTwizzUsersEncounterTextVisible() {
        Locator text = page.getByText(TWIZZ_USERS_ENCOUNTER_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Twizz users encounter text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Twizz users who encounter' text visible: {}", visible);
        return visible;
    }

    public void clickThirdArrowUpButton() {
        clickArrowUpButton(2, false, "third");
    }

    public void clickSecondArrowUpButton() {
        clickArrowUpButton(1, false, "second");
    }

    public boolean isExploreTwizzButtonVisible() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(EXPLORE_TWIZZ_BTN));
        try {
            btn.first().scrollIntoViewIfNeeded();
            btn.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Explore Twizz button wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(btn);
        logger.info("'Explore Twizz arrow-up' button visible: {}", visible);
        return visible;
    }

    public void clickExploreTwizzButton() {
        clickByRole(AriaRole.BUTTON, EXPLORE_TWIZZ_BTN, false, 0, "Clicked 'Explore Twizz' button");
    }

    public boolean isHlsVideoPlayerVisible() {
        Locator player = page.locator(HLS_PLAYER_LOCATOR);
        try {
            player.first().scrollIntoViewIfNeeded();
            player.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("HLS video player wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(player);
        logger.info("HLS video player visible: {}", visible);
        return visible;
    }

    public void clickThirdGoToTwizzButton() {
        clickByRole(AriaRole.BUTTON, GO_TO_TWIZZ_BTN, false, 2, "Clicked third 'Go to Twizz' button");
    }

    public void clickSecondGoToTwizzButton() {
        clickByRole(AriaRole.BUTTON, GO_TO_TWIZZ_BTN, false, 1, "Clicked second 'Go to Twizz' button");
    }

    public void clickGoToTwizzButton() {
        clickByRole(AriaRole.BUTTON, GO_TO_TWIZZ_BTN, false, 0, "Clicked 'Go to Twizz' button");
    }

    public void clickSignUpButton() {
        clickByRole(AriaRole.BUTTON, SIGN_UP_BTN, false, 0, "Clicked 'Sign up' button");
    }

    public void clickRegisterAsCreatorButton() {
        clickByRole(AriaRole.BUTTON, REGISTER_AS_CREATOR_BTN, false, 0, "Clicked 'Register as a creator' button");
    }

    public void clickUser1Link() {
        clickByRole(AriaRole.LINK, USER1_LINK_NAME, false, 0, "Clicked 'User 1' link");
    }

    public void clickThirdToStartUpButton() {
        clickByRole(AriaRole.BUTTON, TO_START_UP_BTN, false, 2, "Clicked third 'To start up' button");
    }

    public void clickSecondToStartUpButton() {
        clickByRole(AriaRole.BUTTON, TO_START_UP_BTN, false, 1, "Clicked second 'To start up' button");
    }

    public void clickToStartUpButton() {
        clickByRole(AriaRole.BUTTON, TO_START_UP_BTN, false, 0, "Clicked 'To start up' button");
    }

    public boolean isDesignedForManagersTextVisible() {
        Locator text = page.getByText(DESIGNED_FOR_MANAGERS_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Designed for managers text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Designed for managers' text visible: {}", visible);
        return visible;
    }

    public boolean isBusinessUrlCorrect() {
        return waitForUrlContains(BUSINESS_URL_PART, "Business", ConfigReader.getShortTimeout());
    }

    public void clickBecomeAFanButton() {
        clickByRole(AriaRole.BUTTON, BECOME_A_FAN_BTN, false, 0, "Clicked 'Become a fan' button");
    }

    public boolean isFanTabVisible() {
        Locator tab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(FAN_TAB_NAME));
        try {
            tab.first().scrollIntoViewIfNeeded();
            tab.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Fan tab wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(tab);
        logger.info("Fan tab visible: {}", visible);
        return visible;
    }

    public boolean isFanRegistrationUrlCorrect() {
        return waitForRegistrationUrl("fan", "Fan registration");
    }

    public void clickSecondContactUsButton() {
        clickByRole(AriaRole.BUTTON, CONTACT_US_BTN, false, 1, "Clicked second 'Contact us' button");
    }

    public void clickContactUsButton() {
        clickByRole(AriaRole.BUTTON, CONTACT_US_BTN, false, 0, "Clicked 'Contact us' button");
    }

    public boolean isContactUsTextVisible() {
        Locator text = page.getByText(CONTACT_US_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Contact us text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("Contact us text visible: {}", visible);
        return visible;
    }

    public boolean isContactUsUrlCorrect() {
        return waitForUrlContains(CONTACT_US_URL_PART, "Contact us", ConfigReader.getMediumTimeout());
    }

    public boolean isBecomingACreatorTextVisible() {
        Locator text = page.getByText(BECOMING_A_CREATOR_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Becoming a creator text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("'Becoming a creator ?' text visible: {}", visible);
        return visible;
    }

    public void clickBecomingACreatorButton() {
        clickByRole(AriaRole.BUTTON, BECOMING_A_CREATOR_BTN, false, 0, "Clicked 'Becoming a creator' button");
    }

    public void clickBecomeACreatorButton() {
        clickByRole(AriaRole.BUTTON, BECOME_A_CREATOR_BTN, false, 0, "Clicked 'Become a creator' button");
    }

    public boolean isCreatorTabVisible() {
        Locator tab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName(CREATOR_TAB_NAME));
        try {
            tab.first().scrollIntoViewIfNeeded();
            tab.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Creator tab wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(tab);
        logger.info("Creator tab visible: {}", visible);
        return visible;
    }

    public boolean isCreatorRegistrationUrlCorrect() {
        return waitForRegistrationUrl("creator", "Creator registration");
    }

    public boolean isLoginTextVisible() {
        Locator text = page.getByText(LOGIN_BTN_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Login text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("Login page text visible: {}", visible);
        return visible;
    }

    public boolean isLoginUrlCorrect() {
        return waitForUrlContains(LOGIN_URL_PART, "Login", ConfigReader.getMediumTimeout());
    }

    public void clickSecondLoginButton() {
        clickByRole(AriaRole.BUTTON, LOGIN_BTN_TEXT, false, 1, "Clicked second Login button");
    }

    public void clickRegisterButton() {
        clickByRole(AriaRole.BUTTON, REGISTER_BTN_TEXT, true, 0, "Clicked 'Register' button");
    }

    public boolean isRegistrationTextVisible() {
        Locator text = page.getByText(REGISTRATION_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Registration text wait failed: {}", e.getMessage()); }
        boolean visible = safeIsVisible(text);
        logger.info("Registration text visible: {}", visible);
        return visible;
    }

    public boolean isSignUpUrlCorrect() {
        return waitForUrlContains(SIGNUP_URL_PART, "SignUp", ConfigReader.getMediumTimeout());
    }

}
