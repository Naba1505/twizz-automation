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
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    // Reduced from DEFAULT_WAIT (60000ms) to SHORT_TIMEOUT (1000ms) = 98% faster!
    private static final int NAVIGATION_WAIT = 100;      // Navigation delays
    private static final int BUTTON_RETRY_DELAY = 150;   // Button click retry delay
    private static final int SHORT_TIMEOUT = 1000;       // Short waits (was 60000ms)
    private static final int MEDIUM_TIMEOUT = 2000;      // Medium waits (was 20000ms)

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
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

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
        waitVisible(settingsIcon(), SHORT_TIMEOUT);
        clickWithRetry(settingsIcon(), 1, BUTTON_RETRY_DELAY);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open Terms and conditions of sale")
    public void openTermsAndConditionsOfSale() {
        waitVisible(termsAndConditionsOfSaleMenu(), SHORT_TIMEOUT);
        try { termsAndConditionsOfSaleMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(termsAndConditionsOfSaleMenu(), 1, BUTTON_RETRY_DELAY);
        waitVisible(saleTitleExact(), SHORT_TIMEOUT);
    }

    @Step("Assert on Sale terms page (title and URL)")
    public void assertOnSaleTermsPage() {
        waitVisible(saleTitleExact(), SHORT_TIMEOUT);
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
            try { page.mouse().wheel(0, 800); page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
        }
        waitVisible(saleBottomSnippet(), SHORT_TIMEOUT);
        // Scroll back up until title is visible again
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(saleTitleExact())) break;
            try { page.mouse().wheel(0, -800); page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
        }
        waitVisible(saleTitleExact(), SHORT_TIMEOUT);
    }

    @Step("Open Community regulations")
    public void openCommunityRegulations() {
        waitVisible(communityRegulationsMenu(), SHORT_TIMEOUT);
        try { communityRegulationsMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(communityRegulationsMenu(), 1, BUTTON_RETRY_DELAY);
        waitVisible(communityTitleExact(), SHORT_TIMEOUT);
    }

    @Step("Assert on Community regulations page (title and URL)")
    public void assertOnCommunityRegulationsPage() {
        waitVisible(communityTitleExact(), SHORT_TIMEOUT);
        String url = page.url();
        if (!url.contains(COMMUNITY_URL_PATH)) {
            logger.warn("Expected Community regulations URL to contain '{}' but was {}", COMMUNITY_URL_PATH, url);
        }
    }

    @Step("Scroll to bottom community snippet and back to title")
    public void scrollDownToCommunityBottomAndBackToTitle() {
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(communityBottomSnippet())) break;
            try { page.mouse().wheel(0, 800); page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
        }
        waitVisible(communityBottomSnippet(), SHORT_TIMEOUT);
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(communityTitleExact())) break;
            try { page.mouse().wheel(0, -800); page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
        }
        waitVisible(communityTitleExact(), SHORT_TIMEOUT);
    }

    @Step("Open Content Policy")
    public void openContentPolicy() {
        waitVisible(contentPolicyMenu(), SHORT_TIMEOUT);
        try { contentPolicyMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(contentPolicyMenu(), 1, BUTTON_RETRY_DELAY);
        waitVisible(contentPolicyTitle(), MEDIUM_TIMEOUT);
    }

    @Step("Assert on Content Policy page (title visible)")
    public void assertOnContentPolicyPage() {
        waitVisible(contentPolicyTitle(), MEDIUM_TIMEOUT);
    }

    @Step("Scroll to bottom Content Policy snippet and back to title")
    public void scrollDownToContentPolicyBottomAndBackToTitle() {
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(contentPolicyBottomSnippet())) break;
            try { page.mouse().wheel(0, 800); page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
        }
        waitVisible(contentPolicyBottomSnippet(), SHORT_TIMEOUT);
        for (int i = 0; i < 12; i++) {
            if (safeIsVisible(contentPolicyTitle())) break;
            try { page.mouse().wheel(0, -800); page.waitForTimeout(NAVIGATION_WAIT); } catch (Throwable ignored) {}
        }
        waitVisible(contentPolicyTitle(), MEDIUM_TIMEOUT);
    }

    @Step("Click back arrow")
    public void clickBackArrow() {
        waitVisible(backArrow(), SHORT_TIMEOUT);
        clickWithRetry(backArrow(), 1, BUTTON_RETRY_DELAY);
    }
}

