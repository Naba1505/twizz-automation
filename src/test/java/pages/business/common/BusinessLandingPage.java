package pages.business.common;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

/**
 * Page Object for Twizz Business Landing Page
 * URL: https://devbusiness.twizz.app/
 */
public class BusinessLandingPage {
    private static final Logger logger = LoggerFactory.getLogger(BusinessLandingPage.class);
    private final Page page;

    public BusinessLandingPage(Page page) {
        this.page = page;
    }

    @Step("Navigate to Twizz Business landing page")
    public void navigate() {
        String landingPageUrl = ConfigReader.getBusinessLandingPageUrl();
        page.navigate(landingPageUrl);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("[Business] Navigated to landing page: {}", landingPageUrl);
    }

    @Step("Wait for Business landing page to load")
    public void waitForPageToLoad() {
        // Wait for the main heading to be visible
        Locator heading = page.getByText("Designed for managers");
        heading.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        logger.info("[Business] Landing page loaded successfully");
    }

    @Step("Verify Twizz Business logo is visible")
    public boolean isLogoVisible() {
        // The logo is the second image on the page (nth(1) = index 1)
        Locator logo = page.getByRole(AriaRole.IMG).nth(1);
        boolean isVisible = logo.isVisible();
        logger.info("[Business] Logo visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify 'Designed for managers' heading is visible")
    public boolean isMainHeadingVisible() {
        Locator heading = page.getByText("Designed for managers");
        boolean isVisible = heading.isVisible();
        logger.info("[Business] 'Designed for managers' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Contact Us link")
    public void clickContactUs() {
        Locator contactUsLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Contact us").setExact(true));
        contactUsLink.getByRole(AriaRole.BUTTON).click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Contact Us link");
    }

    @Step("Verify user is on Contact page")
    public boolean isOnContactPage() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("We are in different places"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business] Contact page heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Login button")
    public void clickLogin() {
        Locator loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));
        loginButton.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Login button");
    }

    @Step("Verify user is on Login page")
    public boolean isOnLoginPage() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Connection"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business] Login page heading 'Connection' visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Register button")
    public void clickRegister() {
        Locator registerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register"));
        registerButton.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Register button");
    }

    @Step("Verify user is on Registration page")
    public boolean isOnRegistrationPage() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Inscription"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business] Registration page heading 'Inscription' visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Manager tab on Registration page")
    public void clickManagerTab() {
        Locator managerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Manager"));
        managerButton.click();
        logger.info("[Business] Clicked Manager tab on Registration page");
    }

    @Step("Navigate back to landing page")
    public void navigateBack() {
        page.goBack();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Navigated back");
    }

    // Footer Links Navigation Methods
    
    @Step("Scroll to Payment Methods element")
    public void scrollToPaymentMethods() {
        Locator paymentMethods = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Payment Methods"));
        paymentMethods.scrollIntoViewIfNeeded();
        logger.info("[Business] Scrolled to Payment Methods element");
    }

    @Step("Click Contact Us footer link")
    public void clickContactUsFooter() {
        Locator contactUsFooter = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Contact us arrow-up"));
        contactUsFooter.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Contact Us footer link");
    }

    @Step("Verify email is visible on Contact page")
    public boolean isEmailVisible() {
        Locator email = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("creators@twizz.app"));
        boolean isVisible = email.isVisible();
        logger.info("[Business] Email visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Legal notices link")
    public void clickLegalNotices() {
        Locator legalNotices = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Legal notices arrow-up"));
        legalNotices.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Legal notices link");
    }

    @Step("Scroll to 'The site is hosted by OVH' text")
    public void scrollToSiteHostedByText() {
        Locator siteHosted = page.getByText("The site is hosted by OVH.");
        siteHosted.scrollIntoViewIfNeeded();
        logger.info("[Business] Scrolled to 'The site is hosted by OVH' text");
    }

    @Step("Click Twizz link")
    public void clickTwizzLink() {
        Locator twizzLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Twizz").setExact(true));
        twizzLink.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Twizz link");
    }

    @Step("Click Content Policy link")
    public void clickContentPolicy() {
        Locator contentPolicy = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Content Policy and Child"));
        contentPolicy.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Content Policy link");
    }

    @Step("Scroll to 'Twizz users who encounter' text")
    public void scrollToTwizzUsersText() {
        Locator twizzUsers = page.getByText("Twizz users who encounter");
        twizzUsers.scrollIntoViewIfNeeded();
        logger.info("[Business] Scrolled to 'Twizz users who encounter' text");
    }

    @Step("Click Confidentiality link")
    public void clickConfidentiality() {
        Locator confidentiality = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Confidentiality arrow-up"));
        confidentiality.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Confidentiality link");
    }

    @Step("Scroll to 'Our Privacy Policy may be' text")
    public void scrollToPrivacyPolicyText() {
        Locator privacyPolicy = page.getByText("Our Privacy Policy may be");
        privacyPolicy.scrollIntoViewIfNeeded();
        logger.info("[Business] Scrolled to 'Our Privacy Policy may be' text");
    }

    @Step("Click General Conditions of Sale link")
    public void clickGeneralConditionsOfSale() {
        Locator conditionsOfSale = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("General Conditions of Sale"));
        conditionsOfSale.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked General Conditions of Sale link");
    }

    @Step("Scroll to 'The Creator undertakes not to' text")
    public void scrollToCreatorUndertakesText() {
        Locator creatorUndertakes = page.getByText("The Creator undertakes not to");
        creatorUndertakes.scrollIntoViewIfNeeded();
        logger.info("[Business] Scrolled to 'The Creator undertakes not to' text");
    }

    @Step("Click General Conditions of Use link")
    public void clickGeneralConditionsOfUse() {
        Locator conditionsOfUse = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("General Conditions of Use"));
        conditionsOfUse.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked General Conditions of Use link");
    }

    @Step("Scroll to 'Twizz therefore recommends' text")
    public void scrollToTwizzRecommendsText() {
        Locator twizzRecommends = page.getByText("Twizz therefore recommends");
        twizzRecommends.scrollIntoViewIfNeeded();
        logger.info("[Business] Scrolled to 'Twizz therefore recommends' text");
    }

    @Step("Click Blog link")
    public void clickBlog() {
        Locator blog = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Blog arrow-up"));
        blog.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Blog link");
    }

    @Step("Scroll to Twizz blog link")
    public void scrollToTwizzBlogLink() {
        Locator twizzBlog = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Twizz"));
        twizzBlog.scrollIntoViewIfNeeded();
        logger.info("[Business] Scrolled to Twizz blog link");
    }

    // Language Switching Methods
    
    @Step("Click language dropdown")
    public void clickLanguageDropdown() {
        // Scroll to bottom of page first
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);
        
        // Try to find the language dropdown with different nth values
        Locator languageDropdown = null;
        for (int i = 0; i < 10; i++) {
            Locator candidate = page.locator("div").filter(new Locator.FilterOptions().setHasText("English")).nth(i);
            if (candidate.count() > 0) {
                try {
                    if (candidate.isVisible()) {
                        languageDropdown = candidate;
                        logger.info("[Business] Found language dropdown at index {}", i);
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next index
                }
            }
        }
        
        if (languageDropdown != null) {
            languageDropdown.click(new Locator.ClickOptions().setForce(true));
            page.waitForTimeout(1000);
            logger.info("[Business] Clicked language dropdown");
        } else {
            throw new RuntimeException("Language dropdown not found");
        }
    }

    @Step("Click French language")
    public void clickFrenchLanguage() {
        // Wait for dropdown options to be visible
        page.waitForTimeout(1000);
        
        // Find Français in the dropdown options
        Locator french = page.locator(".ant-select-item-option-content").filter(new Locator.FilterOptions().setHasText("Français"));
        if (french.count() == 0) {
            french = page.getByText("Français").first();
        }
        
        french.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked French language");
    }

    @Step("Verify French heading is visible")
    public boolean isFrenchHeadingVisible() {
        Locator frenchHeading = page.getByText("Conçu pour les manageurs");
        frenchHeading.scrollIntoViewIfNeeded();
        boolean isVisible = frenchHeading.isVisible();
        logger.info("[Business] French heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Spanish language")
    public void clickSpanishLanguage() {
        // Scroll to bottom and find dropdown with "Français" text (current language)
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);
        
        Locator languageDropdown = null;
        for (int i = 0; i < 10; i++) {
            Locator candidate = page.locator("div").filter(new Locator.FilterOptions().setHasText("Français")).nth(i);
            if (candidate.count() > 0) {
                try {
                    if (candidate.isVisible()) {
                        languageDropdown = candidate;
                        logger.info("[Business] Found French language dropdown at index {}", i);
                        break;
                    }
                } catch (Exception e) {
                    // Continue
                }
            }
        }
        
        if (languageDropdown != null) {
            languageDropdown.click(new Locator.ClickOptions().setForce(true));
            page.waitForTimeout(1000);
        }
        
        Locator spanish = page.getByText("Español");
        spanish.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked Spanish language");
    }

    @Step("Verify Spanish heading is visible")
    public boolean isSpanishHeadingVisible() {
        Locator spanishHeading = page.getByText("Diseñado para los managers");
        spanishHeading.scrollIntoViewIfNeeded();
        boolean isVisible = spanishHeading.isVisible();
        logger.info("[Business] Spanish heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click English language")
    public void clickEnglishLanguage() {
        // Scroll to bottom and find dropdown with "Español" text (current language)
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);
        
        Locator languageDropdown = null;
        for (int i = 0; i < 10; i++) {
            Locator candidate = page.locator("div").filter(new Locator.FilterOptions().setHasText("Español")).nth(i);
            if (candidate.count() > 0) {
                try {
                    if (candidate.isVisible()) {
                        languageDropdown = candidate;
                        logger.info("[Business] Found Spanish language dropdown at index {}", i);
                        break;
                    }
                } catch (Exception e) {
                    // Continue
                }
            }
        }
        
        if (languageDropdown != null) {
            languageDropdown.click(new Locator.ClickOptions().setForce(true));
            page.waitForTimeout(1000);
        }
        
        Locator english = page.getByText("English");
        english.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business] Clicked English language");
    }

    // Wrapper methods for complete language switching
    
    @Step("Switch to French language")
    public void switchToFrench() {
        // Scroll to bottom
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);
        
        // Click language dropdown using exact locator from specification
        page.locator("div").filter(new Locator.FilterOptions().setHasText("English")).nth(5).click();
        page.waitForTimeout(500);
        
        // Click French option
        page.getByText("Français").click();
        page.waitForTimeout(500);
        
        // Verify by scrolling to French heading
        page.getByText("Conçu pour les manageurs").scrollIntoViewIfNeeded();
        logger.info("[Business] Switched to French language");
    }

    @Step("Switch to Spanish language")
    public void switchToSpanish() {
        // Scroll to bottom
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);
        
        // Click language dropdown (now showing Français)
        page.locator("div").filter(new Locator.FilterOptions().setHasText("Français")).nth(5).click();
        page.waitForTimeout(500);
        
        // Click Spanish option
        page.getByText("Español").click();
        page.waitForTimeout(500);
        
        // Verify by scrolling to Spanish heading
        page.getByText("Diseñado para los managers").scrollIntoViewIfNeeded();
        logger.info("[Business] Switched to Spanish language");
    }

    @Step("Switch to English language")
    public void switchToEnglish() {
        // Scroll to bottom
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);
        
        // Click language dropdown (now showing Español)
        page.locator("div").filter(new Locator.FilterOptions().setHasText("Español")).nth(5).click();
        page.waitForTimeout(500);
        
        // Click English option
        page.getByText("English").click();
        page.waitForTimeout(500);
        
        // Verify by scrolling to English heading
        page.getByText("Designed for managers").scrollIntoViewIfNeeded();
        logger.info("[Business] Switched to English language");
    }
}
