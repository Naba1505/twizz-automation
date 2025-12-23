package pages.fan;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page object for Fan Terms and Policies screens in Settings.
 * Covers: Terms and Conditions of Sale, Community Regulations, Content Policy.
 */
public class FanTermsAndPoliciesPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanTermsAndPoliciesPage.class);
    private static final int DEFAULT_WAIT = 10000;

    public FanTermsAndPoliciesPage(Page page) {
        super(page);
    }

    // ================= Locators =================

    // Settings
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon"));
    }

    private Locator settingsTitle() {
        return page.getByText("Settings");
    }

    private Locator backArrow() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
    }

    // Terms and Conditions of Sale
    private Locator termsAndConditionsMenuItem() {
        return page.getByText("Terms and conditions of sale");
    }

    private Locator termsAndConditionsTitle() {
        return page.getByText("General Conditions of Sale", new Page.GetByTextOptions().setExact(true));
    }

    private Locator termsAndConditionsEndText() {
        return page.getByText("(Other than personal data) only if it has been generated jointly with other");
    }

    // Community Regulations
    private Locator communityRegulationsMenuItem() {
        return page.getByText("Community regulations");
    }

    private Locator communityRegulationsTitle() {
        return page.getByText("General Conditions of Use", new Page.GetByTextOptions().setExact(true));
    }

    private Locator communityRegulationsEndText() {
        return page.getByText("Twizz therefore recommends");
    }

    // Content Policy
    private Locator contentPolicyMenuItem() {
        return page.getByText("Content Policy");
    }

    private Locator contentPolicyTitle() {
        return page.getByText("Content Policy and Child");
    }

    private Locator contentPolicyEndText() {
        return page.getByText("Twizz users who encounter");
    }

    // ================= Navigation Methods =================

    @Step("Click Settings icon")
    public void clickSettingsIcon() {
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 2, 200);
        logger.info("[Fan][TermsAndPolicies] Clicked Settings icon");
    }

    @Step("Assert on Settings screen by viewing title")
    public void assertOnSettingsScreen() {
        waitVisible(settingsTitle(), DEFAULT_WAIT);
        logger.info("[Fan][TermsAndPolicies] On Settings screen - title visible");
    }

    @Step("Click back arrow to navigate back")
    public void clickBackArrow() {
        waitVisible(backArrow(), DEFAULT_WAIT);
        clickWithRetry(backArrow(), 2, 200);
        page.waitForTimeout(500);
        logger.info("[Fan][TermsAndPolicies] Clicked back arrow");
    }

    /**
     * Navigate from Fan home to Settings screen.
     */
    @Step("Navigate to Settings from Fan home")
    public void navigateToSettings() {
        clickSettingsIcon();
        assertOnSettingsScreen();
        logger.info("[Fan][TermsAndPolicies] Successfully navigated to Settings screen");
    }

    // ================= Terms and Conditions of Sale =================

    @Step("Click Terms and conditions of sale menu item")
    public void clickTermsAndConditions() {
        Locator menuItem = termsAndConditionsMenuItem();
        waitVisible(menuItem, DEFAULT_WAIT);
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, 200);
        logger.info("[Fan][TermsAndPolicies] Clicked 'Terms and conditions of sale' menu item");
    }

    @Step("Assert on Terms and Conditions screen by viewing title")
    public void assertOnTermsAndConditionsScreen() {
        waitVisible(termsAndConditionsTitle(), DEFAULT_WAIT);
        logger.info("[Fan][TermsAndPolicies] On Terms and Conditions screen - title visible");
    }

    @Step("Scroll to end of Terms and Conditions")
    public void scrollToEndOfTermsAndConditions() {
        Locator endText = termsAndConditionsEndText();
        endText.scrollIntoViewIfNeeded();
        waitVisible(endText, DEFAULT_WAIT);
        page.waitForTimeout(500);
        logger.info("[Fan][TermsAndPolicies] Scrolled to end of Terms and Conditions");
    }

    @Step("Scroll back to top of Terms and Conditions")
    public void scrollToTopOfTermsAndConditions() {
        Locator title = termsAndConditionsTitle();
        title.scrollIntoViewIfNeeded();
        waitVisible(title, DEFAULT_WAIT);
        page.waitForTimeout(500);
        logger.info("[Fan][TermsAndPolicies] Scrolled back to top of Terms and Conditions");
    }

    /**
     * Complete flow: Open Terms and Conditions, scroll to end, scroll back to top, navigate back.
     */
    @Step("Verify Terms and Conditions of Sale")
    public void verifyTermsAndConditions() {
        clickTermsAndConditions();
        assertOnTermsAndConditionsScreen();
        scrollToEndOfTermsAndConditions();
        scrollToTopOfTermsAndConditions();
        clickBackArrow();
        assertOnSettingsScreen();
        logger.info("[Fan][TermsAndPolicies] Terms and Conditions verified successfully");
    }

    // ================= Community Regulations =================

    @Step("Click Community regulations menu item")
    public void clickCommunityRegulations() {
        Locator menuItem = communityRegulationsMenuItem();
        waitVisible(menuItem, DEFAULT_WAIT);
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, 200);
        logger.info("[Fan][TermsAndPolicies] Clicked 'Community regulations' menu item");
    }

    @Step("Assert on Community Regulations screen by viewing title")
    public void assertOnCommunityRegulationsScreen() {
        waitVisible(communityRegulationsTitle(), DEFAULT_WAIT);
        logger.info("[Fan][TermsAndPolicies] On Community Regulations screen - title visible");
    }

    @Step("Scroll to end of Community Regulations")
    public void scrollToEndOfCommunityRegulations() {
        Locator endText = communityRegulationsEndText();
        endText.scrollIntoViewIfNeeded();
        waitVisible(endText, DEFAULT_WAIT);
        page.waitForTimeout(500);
        logger.info("[Fan][TermsAndPolicies] Scrolled to end of Community Regulations");
    }

    @Step("Scroll back to top of Community Regulations")
    public void scrollToTopOfCommunityRegulations() {
        Locator title = communityRegulationsTitle();
        title.scrollIntoViewIfNeeded();
        waitVisible(title, DEFAULT_WAIT);
        page.waitForTimeout(500);
        logger.info("[Fan][TermsAndPolicies] Scrolled back to top of Community Regulations");
    }

    /**
     * Complete flow: Open Community Regulations, scroll to end, scroll back to top, navigate back.
     */
    @Step("Verify Community Regulations")
    public void verifyCommunityRegulations() {
        clickCommunityRegulations();
        assertOnCommunityRegulationsScreen();
        scrollToEndOfCommunityRegulations();
        scrollToTopOfCommunityRegulations();
        clickBackArrow();
        assertOnSettingsScreen();
        logger.info("[Fan][TermsAndPolicies] Community Regulations verified successfully");
    }

    // ================= Content Policy =================

    @Step("Click Content Policy menu item")
    public void clickContentPolicy() {
        Locator menuItem = contentPolicyMenuItem();
        waitVisible(menuItem, DEFAULT_WAIT);
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, 200);
        logger.info("[Fan][TermsAndPolicies] Clicked 'Content Policy' menu item");
    }

    @Step("Assert on Content Policy screen by viewing title")
    public void assertOnContentPolicyScreen() {
        waitVisible(contentPolicyTitle(), DEFAULT_WAIT);
        logger.info("[Fan][TermsAndPolicies] On Content Policy screen - title visible");
    }

    @Step("Scroll to end of Content Policy")
    public void scrollToEndOfContentPolicy() {
        Locator endText = contentPolicyEndText();
        endText.scrollIntoViewIfNeeded();
        waitVisible(endText, DEFAULT_WAIT);
        page.waitForTimeout(500);
        logger.info("[Fan][TermsAndPolicies] Scrolled to end of Content Policy");
    }

    @Step("Scroll back to top of Content Policy")
    public void scrollToTopOfContentPolicy() {
        Locator title = contentPolicyTitle();
        title.scrollIntoViewIfNeeded();
        waitVisible(title, DEFAULT_WAIT);
        page.waitForTimeout(500);
        logger.info("[Fan][TermsAndPolicies] Scrolled back to top of Content Policy");
    }

    /**
     * Complete flow: Open Content Policy, scroll to end, scroll back to top, navigate back.
     */
    @Step("Verify Content Policy")
    public void verifyContentPolicy() {
        clickContentPolicy();
        assertOnContentPolicyScreen();
        scrollToEndOfContentPolicy();
        scrollToTopOfContentPolicy();
        clickBackArrow();
        assertOnSettingsScreen();
        logger.info("[Fan][TermsAndPolicies] Content Policy verified successfully");
    }

    // ================= Complete Flow =================

    /**
     * Verify all three policies: Terms and Conditions, Community Regulations, Content Policy.
     */
    @Step("Verify all Terms and Policies")
    public void verifyAllTermsAndPolicies() {
        verifyTermsAndConditions();
        verifyCommunityRegulations();
        verifyContentPolicy();
        logger.info("[Fan][TermsAndPolicies] All Terms and Policies verified successfully");
    }
}

