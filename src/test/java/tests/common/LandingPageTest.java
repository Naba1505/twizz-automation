package tests.common;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;

public class LandingPageTest extends BaseTestClass {

    @Test(priority = 1, description = "Verify landing page loads and logo is visible")
    public void testLandingPageLoad() {
        Assert.assertTrue(landingPage.isTwizzLogoVisible(), "Twizz logo is not visible on the landing page.");
    }

    @Test(priority = 2, description = "Verify Fan Registration navigation via Become a fan button")
    public void testFanRegistrationNavigation() {
        landingPage.clickBecomeAFanButton();
        Assert.assertTrue(landingPage.isFanTabVisible(), "Fan tab is not visible on registration page");
        Assert.assertTrue(landingPage.isFanRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=fan");
    }

    @Test(priority = 3, description = "Verify Creator Registration navigation via Become a creator button")
    public void testCreatorRegistrationNavigation() {
        landingPage.clickBecomeACreatorButton();
        Assert.assertTrue(landingPage.isCreatorTabVisible(), "Creator tab is not visible on registration page");
        Assert.assertTrue(landingPage.isCreatorRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=creator");
    }

    @Test(priority = 4, description = "Verify Contact Us navigation and page content")
    public void testContactUsNavigation() {
        landingPage.clickContactUsButton();
        Assert.assertTrue(landingPage.isContactUsTextVisible(), "Contact us text 'We have offices and teams' is not visible");
        Assert.assertTrue(landingPage.isContactUsUrlCorrect(), "URL does not contain /contact");
    }

    @Test(priority = 5, description = "Verify Login navigation via Login button")
    public void testLoginNavigation() {
        landingPage.clickLoginButton();
        Assert.assertTrue(landingPage.isLoginTextVisible(), "Login text is not visible on login page");
        Assert.assertTrue(landingPage.isLoginUrlCorrect(), "URL does not contain /auth/signIn");
    }

    @Test(priority = 6, description = "Verify Register navigation via Register button")
    public void testRegisterNavigation() {
        landingPage.clickRegisterButton();
        Assert.assertTrue(landingPage.isRegistrationTextVisible(), "Registration text is not visible on sign up page");
        Assert.assertTrue(landingPage.isSignUpUrlCorrect(), "URL does not contain /auth/signUp");
    }

    @Test(priority = 7, description = "Verify Login navigation via second Login button (scroll to visible)")
    public void testSecondLoginButtonNavigation() {
        landingPage.clickSecondLoginButton();
        Assert.assertTrue(landingPage.isLoginTextVisible(), "Login text is not visible on login page");
        Assert.assertTrue(landingPage.isLoginUrlCorrect(), "URL does not contain /auth/signIn");
    }

    @Test(priority = 8, description = "Verify Fans button navigates to public discover and Home icon shows register prompt")
    public void testFansPublicDiscoverNavigation() {
        landingPage.clickFansButton();
        Assert.assertTrue(landingPage.isPublicDiscoverUrlCorrect(), "URL does not contain /public-discover");
        Assert.assertTrue(landingPage.isHomeIconVisible(), "Home icon is not visible on public discover page");
        landingPage.clickHomeIcon();
        Assert.assertTrue(landingPage.isRegisterToSeeContentVisible(), "'Register to see the content' text is not visible");
    }

    @Test(priority = 9, description = "Verify Discover TWIZZ button navigates to public discover and Home icon shows register prompt")
    public void testDiscoverTwizzNavigation() {
        landingPage.clickDiscoverTwizzButton();
        Assert.assertTrue(landingPage.isPublicDiscoverUrlCorrect(), "URL does not contain /public-discover");
        Assert.assertTrue(landingPage.isHomeIconVisible(), "Home icon is not visible on public discover page");
        landingPage.clickHomeIcon();
        Assert.assertTrue(landingPage.isRegisterToSeeContentVisible(), "'Register to see the content' text is not visible");
    }

    @Test(priority = 10, description = "Verify Becoming a creator section text and button navigates to creator registration")
    public void testBecomingACreatorNavigation() {
        Assert.assertTrue(landingPage.isBecomingACreatorTextVisible(), "'Becoming a creator ?' text is not visible");
        landingPage.clickBecomingACreatorButton();
        Assert.assertTrue(landingPage.isCreatorTabVisible(), "Creator tab is not visible on registration page");
        Assert.assertTrue(landingPage.isCreatorRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=creator");
    }

    @Test(priority = 11, description = "Verify Open the application button navigates to creator registration")
    public void testOpenApplicationNavigation() {
        Assert.assertTrue(landingPage.isVideoVisible(), "Video is not visible on landing page");
        Assert.assertTrue(landingPage.isItsSimpleTextVisible(), "'It's simple, it's better.' text is not visible");
        landingPage.clickOpenApplicationButton();
        Assert.assertTrue(landingPage.isCreatorTabVisible(), "Creator tab is not visible on registration page");
        Assert.assertTrue(landingPage.isCreatorRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=creator");
    }

    @Test(priority = 12, description = "Verify To start up button navigates to business site")
    public void testAgencyToStartUpNavigation() {
        Assert.assertTrue(landingPage.isAgencyTextVisible(), "'Do you have an agency? Use' text is not visible");
        landingPage.clickToStartUpButton();
        Assert.assertTrue(landingPage.isDesignedForManagersTextVisible(), "'Designed for managers' text is not visible");
        Assert.assertTrue(landingPage.isBusinessUrlCorrect(), "URL does not contain business.twizz.com");
    }

    @Test(priority = 13, description = "Verify second Contact us button in agency section navigates to contact page")
    public void testAgencyContactUsNavigation() {
        Assert.assertTrue(landingPage.isAgencyTextVisible(), "'Do you have an agency? Use' text is not visible");
        landingPage.clickSecondContactUsButton();
        Assert.assertTrue(landingPage.isContactUsTextVisible(), "'We have offices and teams' text is not visible");
        Assert.assertTrue(landingPage.isContactUsUrlCorrect(), "URL does not contain /contact");
    }

    @Test(priority = 14, description = "Verify second To start up button in smart security section navigates to creator registration")
    public void testSmartSecurityToStartUpNavigation() {
        Assert.assertTrue(landingPage.isSmartSecurityTextVisible(), "'Smart and safe security' text is not visible");
        landingPage.clickSecondToStartUpButton();
        Assert.assertTrue(landingPage.isRegistrationTextVisible(), "Registration text is not visible on sign up page");
        Assert.assertTrue(landingPage.isCreatorTabVisible(), "Creator tab is not visible on registration page");
        Assert.assertTrue(landingPage.isCreatorRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=creator");
    }

    @Test(priority = 15, description = "Verify third To start up button in already taking section navigates to creator registration")
    public void testAlreadyTakingToStartUpNavigation() {
        Assert.assertTrue(landingPage.isAlreadyTakingTextVisible(), "'THEY ARE ALREADY TAKING' text is not visible");
        landingPage.clickThirdToStartUpButton();
        Assert.assertTrue(landingPage.isRegistrationTextVisible(), "Registration text is not visible on sign up page");
        Assert.assertTrue(landingPage.isCreatorTabVisible(), "Creator tab is not visible on registration page");
        Assert.assertTrue(landingPage.isCreatorRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=creator");
    }

    @Test(priority = 16, description = "Verify User 1 link in already taking section navigates to public discover")
    public void testAlreadyTakingUser1Navigation() {
        Assert.assertTrue(landingPage.isAlreadyTakingTextVisible(), "'THEY ARE ALREADY TAKING' text is not visible");
        landingPage.clickUser1Link();
        Assert.assertTrue(landingPage.isPublicDiscoverUrlCorrect(), "URL does not contain /public-discover");
        Assert.assertTrue(landingPage.isHomeIconVisible(), "Home icon is not visible on public discover page");
        landingPage.clickHomeIcon();
        Assert.assertTrue(landingPage.isRegisterToSeeContentVisible(), "'Register to see the content' text is not visible");
    }

    @Test(priority = 17, description = "Verify Register as a creator button in footer navigates to creator registration")
    public void testFooterRegisterAsCreatorNavigation() {
        Assert.assertTrue(landingPage.isIfYouHaveQuestionsTextVisible(), "'If you have any questions' text is not visible");
        landingPage.clickRegisterAsCreatorButton();
        Assert.assertTrue(landingPage.isRegistrationTextVisible(), "Registration text is not visible on sign up page");
        Assert.assertTrue(landingPage.isCreatorTabVisible(), "Creator tab is not visible on registration page");
        Assert.assertTrue(landingPage.isCreatorRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=creator");
    }

    @Test(priority = 18, description = "Verify Sign up button in footer navigates to fan registration")
    public void testFooterSignUpNavigation() {
        Assert.assertTrue(landingPage.isIfYouHaveQuestionsTextVisible(), "'If you have any questions' text is not visible");
        Assert.assertTrue(landingPage.isToRegisterAsUserTextVisible(), "'To register as a user:' text is not visible");
        landingPage.clickSignUpButton();
        Assert.assertTrue(landingPage.isRegistrationTextVisible(), "Registration text is not visible on sign up page");
        Assert.assertTrue(landingPage.isFanTabVisible(), "Fan tab is not visible on registration page");
        Assert.assertTrue(landingPage.isSignUpUrlCorrect(), "URL does not contain /auth/signUp");
    }

    @Test(priority = 19, description = "Verify Go to Twizz button in footer navigates to creator registration")
    public void testFooterGoToTwizzNavigation() {
        Assert.assertTrue(landingPage.isIfYouHaveQuestionsTextVisible(), "'If you have any questions' text is not visible");
        Assert.assertTrue(landingPage.isStartByCreatingTextVisible(), "'Start by creating a creator' text is not visible");
        landingPage.clickGoToTwizzButton();
        Assert.assertTrue(landingPage.isRegistrationTextVisible(), "Registration text is not visible on sign up page");
        Assert.assertTrue(landingPage.isCreatorTabVisible(), "Creator tab is not visible on registration page");
        Assert.assertTrue(landingPage.isCreatorRegistrationUrlCorrect(), "URL does not contain /auth/signUp?currentTab=creator");
    }

    @Test(priority = 20, description = "Verify second Go to Twizz button in footer navigates to intro page")
    public void testFooterGoToTwizzIntroNavigation() {
        Assert.assertTrue(landingPage.isIfYouHaveQuestionsTextVisible(), "'If you have any questions' text is not visible");
        Assert.assertTrue(landingPage.isOurTeamActsTextVisible(), "'Our team acts quickly to' text is not visible");
        landingPage.clickSecondGoToTwizzButton();
        Assert.assertTrue(landingPage.isContentByThousandTextVisible(), "'Content by the thousand. Free' text is not visible");
        Assert.assertTrue(landingPage.isIntroUrlCorrect(), "URL does not contain /auth/intro");
    }

    @Test(priority = 21, description = "Verify third Go to Twizz button in footer navigates to intro page")
    public void testFooterGoToTwizzThirdNavigation() {
        Assert.assertTrue(landingPage.isIfYouHaveQuestionsTextVisible(), "'If you have any questions' text is not visible");
        Assert.assertTrue(landingPage.isToWithdrawMoneyTextVisible(), "'To withdraw your money on' text is not visible");
        landingPage.clickThirdGoToTwizzButton();
        Assert.assertTrue(landingPage.isContentByThousandTextVisible(), "'Content by the thousand. Free' text is not visible");
        Assert.assertTrue(landingPage.isIntroUrlCorrect(), "URL does not contain /auth/intro");
    }

    @Test(priority = 22, description = "Verify Explore Twizz button navigates to public discover and Home icon shows register prompt")
    public void testExploreTwizzNavigation() {
        Assert.assertTrue(landingPage.isExploreTwizzButtonVisible(), "'Explore Twizz arrow-up' button is not visible");
        landingPage.clickExploreTwizzButton();
        Assert.assertTrue(landingPage.isPublicDiscoverUrlCorrect(), "URL does not contain /public-discover");
        Assert.assertTrue(landingPage.isHlsVideoPlayerVisible(), "HLS video player is not visible on public discover page");
        Assert.assertTrue(landingPage.isHomeIconVisible(), "Home icon is not visible on public discover page");
        landingPage.clickHomeIcon();
        Assert.assertTrue(landingPage.isRegisterToSeeContentVisible(), "'Register to see the content' text is not visible");
    }

    @Test(priority = 23, description = "Verify Contact arrow-up link in footer navigates to contact page")
    public void testFooterContactArrowUpNavigation() {
        Assert.assertTrue(landingPage.isContactArrowUpLinkVisible(), "'Contact arrow-up' link is not visible");
        landingPage.clickSecondArrowUpButton();
        Assert.assertTrue(landingPage.isContactUsTextVisible(), "'We have offices and teams' text is not visible");
        Assert.assertTrue(landingPage.isContactUsUrlCorrect(), "URL does not contain /contact");
    }

    @Test(priority = 24, description = "Verify Legal notices arrow-up link in footer navigates to legal notices page")
    public void testFooterLegalNoticesNavigation() {
        Assert.assertTrue(landingPage.isLegalNoticesLinkVisible(), "'Legal notices arrow-up' link is not visible");
        landingPage.clickThirdArrowUpButton();
        Assert.assertTrue(landingPage.isLegalInformationHeadingVisible(), "'Legal information' heading is not visible");
        Assert.assertTrue(landingPage.isLegalNoticesUrlCorrect(), "URL does not contain /legal-notices");
    }

    @Test(priority = 25, description = "Verify Content Policy arrow-up link in footer navigates to content policy page")
    public void testFooterContentPolicyNavigation() {
        Assert.assertTrue(landingPage.isContentPolicyLinkVisible(), "'Content Policy and Child' link is not visible");
        landingPage.clickFourthArrowUpButton();
        Assert.assertTrue(landingPage.isContentPolicyUrlCorrect(), "URL does not contain /content-policy-and-child-protection");
        Assert.assertTrue(landingPage.isContentPolicyHeadingVisible(), "'CONTENT POLICY v12.06.2024' heading is not visible");
        Assert.assertTrue(landingPage.isTwizzUsersEncounterTextVisible(), "'Twizz users who encounter' text is not visible");
    }

    @Test(priority = 26, description = "Verify Confidentiality arrow-up link in footer navigates to confidentiality page")
    public void testFooterConfidentialityNavigation() {
        Assert.assertTrue(landingPage.isConfidentialityLinkVisible(), "'Confidentiality arrow-up' link is not visible");
        landingPage.clickFifthArrowUpButton();
        Assert.assertTrue(landingPage.isConfidentialityUrlCorrect(), "URL does not contain /confidentiality-and-data-protection");
        Assert.assertTrue(landingPage.isConfidentialityHeadingVisible(), "'v12.06.2024 made for ITDW by' heading is not visible");
        Assert.assertTrue(landingPage.isOurPrivacyPolicyTextVisible(), "'Our Privacy Policy may be' text is not visible");
    }

    @Test(priority = 27, description = "Verify General Conditions of Sale arrow-up link in footer navigates to general conditions page")
    public void testFooterGeneralConditionsNavigation() {
        Assert.assertTrue(landingPage.isGeneralConditionsLinkVisible(), "'General Conditions of Sale' link is not visible");
        landingPage.clickSixthArrowUpButton();
        Assert.assertTrue(landingPage.isGeneralConditionsUrlCorrect(), "URL does not contain /general-conditions-of-sale");
        Assert.assertTrue(landingPage.isConfidentialityHeadingVisible(), "'v12.06.2024 made for ITDW by' heading is not visible");
        Assert.assertTrue(landingPage.isGeneralConditionsTextVisible(), "General conditions footer text is not visible");
    }

    @Test(priority = 28, description = "Verify General Conditions of Use arrow-up link in footer navigates to general conditions of use page")
    public void testFooterGeneralConditionsOfUseNavigation() {
        Assert.assertTrue(landingPage.isGeneralConditionsUseLinkVisible(), "'General Conditions of Use' link is not visible");
        landingPage.clickSixthArrowUpButtonExact();
        Assert.assertTrue(landingPage.isGeneralConditionsUseUrlCorrect(), "URL does not contain /general-conditions-of-use");
        Assert.assertTrue(landingPage.isConfidentialityHeadingVisible(), "'v12.06.2024 made for ITDW by' heading is not visible");
        Assert.assertTrue(landingPage.isTwizzRecommendsTextVisible(), "'Twizz therefore recommends' text is not visible");
    }

    @Test(priority = 29, description = "Verify Blog arrow-up link in footer navigates to blogs page")
    public void testFooterBlogNavigation() {
        Assert.assertTrue(landingPage.isBlogLinkVisible(), "'Blog arrow-up' link is not visible");
        landingPage.clickBlogArrowUpButton();
        Assert.assertTrue(landingPage.isBlogsUrlCorrect(), "URL does not contain /blogs");
        Assert.assertTrue(landingPage.isTwizzBlogTextVisible(), "'TwizzBlog' text is not visible");
    }

    @Test(priority = 30, description = "Verify language switcher cycles English → French → Spanish → English")
    public void testLanguageSwitcher() {
        Assert.assertTrue(landingPage.isEarthImageVisible(), "'Earth' image is not visible");
        Assert.assertTrue(landingPage.isLanguageSelectorVisible("English"), "English language selector (nth4) is not visible");
        landingPage.openLanguageDropdown("English");
        landingPage.selectLanguage("Français");
        Assert.assertTrue(landingPage.isLanguageSelectorVisible("Français"), "Français language selector (nth4) is not visible");
        Assert.assertTrue(landingPage.isFrenchLandingTextVisible(), "French landing text is not visible");
        landingPage.openLanguageDropdown("Français");
        landingPage.selectLanguage("Español");
        Assert.assertTrue(landingPage.isLanguageSelectorVisible("Español"), "Español language selector (nth4) is not visible");
        Assert.assertTrue(landingPage.isFrenchLandingTextVisible(), "French landing text is not visible after switching to Español");
        landingPage.openLanguageDropdown("Español");
        landingPage.selectLanguage("English");
        Assert.assertTrue(landingPage.isLanguageSelectorNth5Visible("English"), "English language selector (nth5) is not visible");
        Assert.assertTrue(landingPage.isEnglishLandingTextVisible(), "English landing text is not visible");
    }

}
