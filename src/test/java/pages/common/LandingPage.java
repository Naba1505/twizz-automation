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

    public boolean isVideoVisible() {
        Locator video = page.locator("video");
        try {
            video.first().scrollIntoViewIfNeeded();
            video.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Video wait failed: {}", e.getMessage()); }
        boolean visible = video.count() > 0 && video.first().isVisible();
        logger.info("Video visible: {}", visible);
        return visible;
    }

    public boolean isItsSimpleTextVisible() {
        Locator text = page.getByText(ITS_SIMPLE_TEXT);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Its simple text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'It's simple, it's better.' text visible: {}", visible);
        return visible;
    }

    public void clickOpenApplicationButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(OPEN_APP_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Open the application' button. Current URL: {}", page.url());
    }

    public boolean isAgencyTextVisible() {
        Locator text = page.getByText(AGENCY_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Agency text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("Agency text visible: {}", visible);
        return visible;
    }

    public boolean isSmartSecurityTextVisible() {
        Locator text = page.getByText(SMART_SECURITY_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Smart security text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Smart and safe security' text visible: {}", visible);
        return visible;
    }

    public boolean isAlreadyTakingTextVisible() {
        Locator text = page.getByText(ALREADY_TAKING_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Already taking text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'THEY ARE ALREADY TAKING' text visible: {}", visible);
        return visible;
    }

    public boolean isIfYouHaveQuestionsTextVisible() {
        Locator text = page.getByText(IF_YOU_HAVE_QUESTIONS_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("If you have questions text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'If you have any questions' text visible: {}", visible);
        return visible;
    }

    public boolean isToRegisterAsUserTextVisible() {
        Locator text = page.getByText(TO_REGISTER_AS_USER_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("To register as user text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'To register as a user:' text visible: {}", visible);
        return visible;
    }

    public boolean isStartByCreatingTextVisible() {
        Locator text = page.getByText(START_BY_CREATING_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Start by creating text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Start by creating a creator' text visible: {}", visible);
        return visible;
    }

    public boolean isOurTeamActsTextVisible() {
        Locator text = page.getByText(OUR_TEAM_ACTS_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Our team acts text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Our team acts quickly to' text visible: {}", visible);
        return visible;
    }

    public boolean isContentByThousandTextVisible() {
        Locator text = page.getByText(CONTENT_BY_THOUSAND_TEXT);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Content by thousand text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Content by the thousand. Free' text visible: {}", visible);
        return visible;
    }

    public boolean isIntroUrlCorrect() {
        try {
            page.waitForURL("**" + INTRO_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Intro URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Intro URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(INTRO_URL_PART);
        logger.info("isIntroUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public boolean isToWithdrawMoneyTextVisible() {
        Locator text = page.getByText(TO_WITHDRAW_MONEY_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("To withdraw money text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'To withdraw your money on' text visible: {}", visible);
        return visible;
    }

    public boolean isContactArrowUpLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CONTACT_ARROW_UP_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Contact arrow-up link wait failed: {}", e.getMessage()); }
        boolean visible = link.count() > 0 && link.first().isVisible();
        logger.info("'Contact arrow-up' link visible: {}", visible);
        return visible;
    }

    public boolean isLegalNoticesLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(LEGAL_NOTICES_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Legal notices link wait failed: {}", e.getMessage()); }
        boolean visible = link.count() > 0 && link.first().isVisible();
        logger.info("'Legal notices arrow-up' link visible: {}", visible);
        return visible;
    }

    public boolean isLegalInformationHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(LEGAL_INFORMATION_HEADING));
        try {
            heading.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Legal information heading wait failed: {}", e.getMessage()); }
        boolean visible = heading.count() > 0 && heading.first().isVisible();
        logger.info("'Legal information' heading visible: {}", visible);
        return visible;
    }

    public boolean isLegalNoticesUrlCorrect() {
        try {
            page.waitForURL("**" + LEGAL_NOTICES_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Legal notices URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Legal notices URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(LEGAL_NOTICES_URL_PART);
        logger.info("isLegalNoticesUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public boolean isContentPolicyLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CONTENT_POLICY_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Content policy link wait failed: {}", e.getMessage()); }
        boolean visible = link.count() > 0 && link.first().isVisible();
        logger.info("'Content Policy and Child' link visible: {}", visible);
        return visible;
    }

    public boolean isConfidentialityLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(CONFIDENTIALITY_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Confidentiality link wait failed: {}", e.getMessage()); }
        boolean visible = link.count() > 0 && link.first().isVisible();
        logger.info("'Confidentiality arrow-up' link visible: {}", visible);
        return visible;
    }

    public boolean isGeneralConditionsLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(GENERAL_CONDITIONS_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("General conditions link wait failed: {}", e.getMessage()); }
        boolean visible = link.count() > 0 && link.first().isVisible();
        logger.info("'General Conditions of Sale' link visible: {}", visible);
        return visible;
    }

    public boolean isGeneralConditionsUseLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(GENERAL_CONDITIONS_USE_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("General conditions of use link wait failed: {}", e.getMessage()); }
        boolean visible = link.count() > 0 && link.first().isVisible();
        logger.info("'General Conditions of Use' link visible: {}", visible);
        return visible;
    }

    public boolean isEarthImageVisible() {
        Locator img = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(EARTH_IMG_NAME));
        try {
            img.first().scrollIntoViewIfNeeded();
            img.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Earth image wait failed: {}", e.getMessage()); }
        boolean visible = img.count() > 0 && img.first().isVisible();
        logger.info("'Earth' image visible: {}", visible);
        return visible;
    }

    public boolean isLanguageSelectorVisible(String language) {
        Locator selector = page.locator("div").filter(new Locator.FilterOptions().setHasText(language)).nth(4);
        try {
            selector.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Language selector ({}) wait failed: {}", language, e.getMessage()); }
        boolean visible = selector.isVisible();
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
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("French landing text visible: {}", visible);
        return visible;
    }

    public boolean isEnglishLandingTextVisible() {
        Locator text = page.getByText(ENGLISH_LANDING_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("English landing text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("English landing text visible: {}", visible);
        return visible;
    }

    public boolean isLanguageSelectorNth5Visible(String language) {
        Locator selector = page.locator("div").filter(new Locator.FilterOptions().setHasText(language)).nth(5);
        try {
            selector.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Language selector nth5 ({}) wait failed: {}", language, e.getMessage()); }
        boolean visible = selector.isVisible();
        logger.info("Language selector nth5 '{}' visible: {}", language, visible);
        return visible;
    }

    public boolean isBlogLinkVisible() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(BLOG_LINK));
        try {
            link.first().scrollIntoViewIfNeeded();
            link.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Blog link wait failed: {}", e.getMessage()); }
        boolean visible = link.count() > 0 && link.first().isVisible();
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
        try {
            page.waitForURL("**" + BLOGS_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Blogs URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Blogs URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(BLOGS_URL_PART);
        logger.info("isBlogsUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public boolean isTwizzBlogTextVisible() {
        Locator text = page.getByText(TWIZZ_BLOG_TEXT);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("TwizzBlog text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'TwizzBlog' text visible: {}", visible);
        return visible;
    }

    public void clickSixthArrowUpButtonExact() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ARROW_UP_BTN).setExact(true)).nth(5);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked sixth exact 'arrow-up' button. Current URL: {}", page.url());
    }

    public boolean isGeneralConditionsUseUrlCorrect() {
        try {
            page.waitForURL("**" + GENERAL_CONDITIONS_USE_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("General conditions of use URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("General conditions of use URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(GENERAL_CONDITIONS_USE_URL_PART);
        logger.info("isGeneralConditionsUseUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public boolean isTwizzRecommendsTextVisible() {
        Locator text = page.getByText(TWIZZ_RECOMMENDS_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Twizz recommends text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Twizz therefore recommends' text visible: {}", visible);
        return visible;
    }

    public void clickSixthArrowUpButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ARROW_UP_BTN)).nth(5);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked sixth 'arrow-up' button. Current URL: {}", page.url());
    }

    public boolean isGeneralConditionsUrlCorrect() {
        try {
            page.waitForURL("**" + GENERAL_CONDITIONS_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("General conditions URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("General conditions URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(GENERAL_CONDITIONS_URL_PART);
        logger.info("isGeneralConditionsUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public boolean isGeneralConditionsTextVisible() {
        Locator text = page.getByText(GENERAL_CONDITIONS_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("General conditions text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("General conditions footer text visible: {}", visible);
        return visible;
    }

    public void clickFifthArrowUpButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ARROW_UP_BTN)).nth(4);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked fifth 'arrow-up' button. Current URL: {}", page.url());
    }

    public boolean isConfidentialityUrlCorrect() {
        try {
            page.waitForURL("**" + CONFIDENTIALITY_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Confidentiality URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Confidentiality URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(CONFIDENTIALITY_URL_PART);
        logger.info("isConfidentialityUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public boolean isConfidentialityHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(CONFIDENTIALITY_HEADING));
        try {
            heading.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Confidentiality heading wait failed: {}", e.getMessage()); }
        boolean visible = heading.count() > 0 && heading.first().isVisible();
        logger.info("'v12.06.2024 made for ITDW by' heading visible: {}", visible);
        return visible;
    }

    public boolean isOurPrivacyPolicyTextVisible() {
        Locator text = page.getByText(OUR_PRIVACY_POLICY_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Our privacy policy text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Our Privacy Policy may be' text visible: {}", visible);
        return visible;
    }

    public void clickFourthArrowUpButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ARROW_UP_BTN)).nth(3);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked fourth 'arrow-up' button. Current URL: {}", page.url());
    }

    public boolean isContentPolicyUrlCorrect() {
        try {
            page.waitForURL("**" + CONTENT_POLICY_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout()));
            logger.info("Content policy URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Content policy URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(CONTENT_POLICY_URL_PART);
        logger.info("isContentPolicyUrlCorrect - url={}, result={}", page.url(), result);
        return result;
    }

    public boolean isContentPolicyHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(CONTENT_POLICY_HEADING));
        try {
            heading.first().scrollIntoViewIfNeeded();
            heading.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Content policy heading wait failed: {}", e.getMessage()); }
        boolean visible = heading.count() > 0 && heading.first().isVisible();
        logger.info("'CONTENT POLICY v12.06.2024' heading visible: {}", visible);
        return visible;
    }

    public boolean isTwizzUsersEncounterTextVisible() {
        Locator text = page.getByText(TWIZZ_USERS_ENCOUNTER_TEXT);
        try {
            text.first().scrollIntoViewIfNeeded();
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Twizz users encounter text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Twizz users who encounter' text visible: {}", visible);
        return visible;
    }

    public void clickThirdArrowUpButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ARROW_UP_BTN)).nth(2);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked third 'arrow-up' button. Current URL: {}", page.url());
    }

    public void clickSecondArrowUpButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ARROW_UP_BTN)).nth(1);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked second 'arrow-up' button. Current URL: {}", page.url());
    }

    public boolean isExploreTwizzButtonVisible() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(EXPLORE_TWIZZ_BTN));
        try {
            btn.first().scrollIntoViewIfNeeded();
            btn.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Explore Twizz button wait failed: {}", e.getMessage()); }
        boolean visible = btn.count() > 0 && btn.first().isVisible();
        logger.info("'Explore Twizz arrow-up' button visible: {}", visible);
        return visible;
    }

    public void clickExploreTwizzButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(EXPLORE_TWIZZ_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Explore Twizz' button. Current URL: {}", page.url());
    }

    public boolean isHlsVideoPlayerVisible() {
        Locator player = page.locator(HLS_PLAYER_LOCATOR);
        try {
            player.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("HLS video player wait failed: {}", e.getMessage()); }
        boolean visible = player.count() > 0 && player.first().isVisible();
        logger.info("HLS video player visible: {}", visible);
        return visible;
    }

    public void clickThirdGoToTwizzButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(GO_TO_TWIZZ_BTN)).nth(2);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked third 'Go to Twizz' button. Current URL: {}", page.url());
    }

    public void clickSecondGoToTwizzButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(GO_TO_TWIZZ_BTN)).nth(1);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked second 'Go to Twizz' button. Current URL: {}", page.url());
    }

    public void clickGoToTwizzButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(GO_TO_TWIZZ_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Go to Twizz' button. Current URL: {}", page.url());
    }

    public void clickSignUpButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(SIGN_UP_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Sign up' button. Current URL: {}", page.url());
    }

    public void clickRegisterAsCreatorButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(REGISTER_AS_CREATOR_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Register as a creator' button. Current URL: {}", page.url());
    }

    public void clickUser1Link() {
        Locator link = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(USER1_LINK_NAME)).first();
        link.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        link.scrollIntoViewIfNeeded();
        link.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'User 1' link. Current URL: {}", page.url());
    }

    public void clickThirdToStartUpButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(TO_START_UP_BTN)).nth(2);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked third 'To start up' button. Current URL: {}", page.url());
    }

    public void clickSecondToStartUpButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(TO_START_UP_BTN)).nth(1);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked second 'To start up' button. Current URL: {}", page.url());
    }

    public void clickToStartUpButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(TO_START_UP_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'To start up' button. Current URL: {}", page.url());
    }

    public boolean isDesignedForManagersTextVisible() {
        Locator text = page.getByText(DESIGNED_FOR_MANAGERS_TEXT);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Designed for managers text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Designed for managers' text visible: {}", visible);
        return visible;
    }

    public boolean isBusinessUrlCorrect() {
        try {
            page.waitForURL("**" + BUSINESS_URL_PART + "**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
            logger.info("Business URL confirmed: {}", page.url());
            return true;
        } catch (Exception e) { logger.debug("Business URL wait failed: {}", e.getMessage()); }
        boolean result = page.url().contains(BUSINESS_URL_PART);
        logger.info("isBusinessUrlCorrect - url={}, result={}", page.url(), result);
        return result;
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

    public void clickSecondContactUsButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CONTACT_US_BTN)).nth(1);
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked second 'Contact us' button. Current URL: {}", page.url());
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

    public boolean isBecomingACreatorTextVisible() {
        Locator text = page.getByText(BECOMING_A_CREATOR_TEXT);
        try {
            text.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Becoming a creator text wait failed: {}", e.getMessage()); }
        boolean visible = text.count() > 0 && text.first().isVisible();
        logger.info("'Becoming a creator ?' text visible: {}", visible);
        return visible;
    }

    public void clickBecomingACreatorButton() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(BECOMING_A_CREATOR_BTN)).first();
        btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        btn.scrollIntoViewIfNeeded();
        btn.click();
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Clicked 'Becoming a creator' button. Current URL: {}", page.url());
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
