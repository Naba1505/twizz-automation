package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page object for Creator -> Settings -> Legal pages (Terms & Conditions of Sale, Community Regulations, Content Policy)
 */
public class CreatorLegalPages extends BasePage {
    private static final String SETTINGS_URL_PART = "/common/setting";
    private static final String SALE_TITLE_EXACT = "General Conditions of Sale";
    private static final String SALE_URL_PATH = "/common/general-conditions-of-sale";
    private static final String SALE_BOTTOM_SNIPPET = "(Other than personal data) only if it has been generated Jointly with other";
    private static final String COMMUNITY_TITLE_EXACT = "General Conditions of Use";
    private static final String COMMUNITY_URL_PATH = "/common/general-conditions-of-use";
    private static final String COMMUNITY_BOTTOM_SNIPPET = "Twizz therefore recommends";

    // Content Policy
    private static final String CONTENT_POLICY_MENU = "Content Policy";
    private static final String CONTENT_POLICY_TITLE = "Content Policy and Child";
    private static final String CONTENT_POLICY_BOTTOM_SNIPPET = "Twizz users who encounter";

    public CreatorLegalPages(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator termsAndConditionsOfSaleMenu() {
        return page.getByText("Terms and conditions of sale");
    }

    private Locator communityRegulationsMenu() {
        return page.getByText("Community regulations");
    }

    private Locator contentPolicyMenu() {
        return page.getByText(CONTENT_POLICY_MENU);
    }

    private Locator saleTitleExact() {
        return page.getByText(SALE_TITLE_EXACT, new Page.GetByTextOptions().setExact(true));
    }

    private Locator saleBottomSnippet() {
        return page.getByText(SALE_BOTTOM_SNIPPET);
    }

    private Locator communityTitleExact() {
        return page.getByText(COMMUNITY_TITLE_EXACT, new Page.GetByTextOptions().setExact(true));
    }

    private Locator communityBottomSnippet() {
        return page.getByText(COMMUNITY_BOTTOM_SNIPPET);
    }

    private Locator contentPolicyTitle() {
        // Non-exact matching to be resilient to minor text changes/whitespace
        return page.getByText(CONTENT_POLICY_TITLE);
    }

    private Locator contentPolicyBottomSnippet() {
        return page.getByText(CONTENT_POLICY_BOTTOM_SNIPPET);
    }

    private Locator backArrow() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Legal Pages)")
    public void openSettingsFromProfile() {
        Locator settingsIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
        waitVisible(settingsIcon, ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon, 1, ConfigReader.getElementRetryDelay());
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Assert current URL contains settings path")
    public void assertOnSettingsUrl() {
        if (!page.url().contains(SETTINGS_URL_PART)) {
            throw new AssertionError("Did not land on Settings screen. URL: " + page.url());
        }
        logger.info("Settings URL confirmed: {}", page.url());
    }

    @Step("Open Terms and conditions of sale")
    public void openTermsAndConditionsOfSale() {
        waitVisible(termsAndConditionsOfSaleMenu(), ConfigReader.getShortTimeout());
        try { termsAndConditionsOfSaleMenu().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(termsAndConditionsOfSaleMenu(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(saleTitleExact(), ConfigReader.getShortTimeout());
    }

    @Step("Assert on Sale terms page (title and URL)")
    public void assertOnSaleTermsPage() {
        waitVisible(saleTitleExact(), ConfigReader.getShortTimeout());
        String url = page.url();
        if (!url.contains(SALE_URL_PATH)) {
            logger.warn("Expected Sale terms URL to contain '{}' but was {}", SALE_URL_PATH, url);
        }
    }

    @Step("Scroll to bottom sale snippet and back to title")
    public void scrollDownToSaleBottomAndBackToTitle() {
        // Scroll down until bottom snippet is visible
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(saleBottomSnippet())) break;
            try { page.mouse().wheel(0, 800); page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); }
        }
        waitVisible(saleBottomSnippet(), ConfigReader.getShortTimeout());
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(saleTitleExact())) break;
            try { page.mouse().wheel(0, -800); page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); }
        }
        waitVisible(saleTitleExact(), ConfigReader.getShortTimeout());
    }

    @Step("Open Community regulations")
    public void openCommunityRegulations() {
        waitVisible(communityRegulationsMenu(), ConfigReader.getShortTimeout());
        try { communityRegulationsMenu().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(communityRegulationsMenu(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(communityTitleExact(), ConfigReader.getShortTimeout());
    }

    @Step("Assert on Community regulations page (title and URL)")
    public void assertOnCommunityRegulationsPage() {
        waitVisible(communityTitleExact(), ConfigReader.getShortTimeout());
        String url = page.url();
        if (!url.contains(COMMUNITY_URL_PATH)) {
            logger.warn("Expected Community regulations URL to contain '{}' but was {}", COMMUNITY_URL_PATH, url);
        }
    }

    @Step("Scroll to bottom community snippet and back to title")
    public void scrollDownToCommunityBottomAndBackToTitle() {
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(communityBottomSnippet())) break;
            try { page.mouse().wheel(0, 800); page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); }
        }
        waitVisible(communityBottomSnippet(), ConfigReader.getShortTimeout());
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(communityTitleExact())) break;
            try { page.mouse().wheel(0, -800); page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); }
        }
        waitVisible(communityTitleExact(), ConfigReader.getShortTimeout());
    }

    @Step("Open Content Policy")
    public void openContentPolicy() {
        waitVisible(contentPolicyMenu(), ConfigReader.getShortTimeout());
        try { contentPolicyMenu().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(contentPolicyMenu(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(contentPolicyTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Assert on Content Policy page (title visible)")
    public void assertOnContentPolicyPage() {
        waitVisible(contentPolicyTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Scroll to bottom Content Policy snippet and back to title")
    public void scrollDownToContentPolicyBottomAndBackToTitle() {
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(contentPolicyBottomSnippet())) break;
            try { page.mouse().wheel(0, 800); page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); }
        }
        waitVisible(contentPolicyBottomSnippet(), ConfigReader.getShortTimeout());
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(contentPolicyTitle())) break;
            try { page.mouse().wheel(0, -800); page.waitForTimeout(ConfigReader.getAnimationTimeout()); } catch (Throwable e) { logger.debug("Wheel scroll failed: {}", e.getMessage()); }
        }
        waitVisible(contentPolicyTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Click back arrow")
    public void clickBackArrow() {
        waitVisible(backArrow(), ConfigReader.getShortTimeout());
        clickWithRetry(backArrow(), 1, ConfigReader.getElementRetryDelay());
    }
}

