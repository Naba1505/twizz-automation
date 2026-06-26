package pages.fan;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;


/**
 * Page Object for Fan My Creators functionality.
 * Handles viewing subscribed creators and their details.
 */
public class FanMyCreatorsPage extends BasePage {

    public FanMyCreatorsPage(Page page) {
        super(page);
    }

    // ===== Navigation Methods =====

    @Step("Click Settings icon from home screen")
    public void clickSettingsIcon() {
        logger.info("Clicking Settings icon");
        
        Locator settingsIcon = page.getByRole(AriaRole.IMG,
                new Page.GetByRoleOptions().setName("Settings icon"));
        settingsIcon.first().click(new Locator.ClickOptions().setForce(true));
        try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        logger.info("Clicked Settings icon");
    }

    @Step("Click My creators tile")
    public void clickMyCreatorsTile() {
        logger.info("Clicking My creators tile");
        
        Locator myCreatorsTile = page.getByText("My creators");
        waitVisible(myCreatorsTile.first(), DEFAULT_WAIT);
        clickWithRetry(myCreatorsTile.first(), 1, ConfigReader.getElementRetryDelay());
        try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        logger.info("Clicked My creators tile");
    }

    @Step("Verify on My creators screen")
    public void verifyOnMyCreatorsScreen() {
        logger.info("Verifying on My creators screen");
        
        Locator myCreatorsTitle = page.getByText("My creators");
        waitVisible(myCreatorsTitle.first(), DEFAULT_WAIT);
        
        logger.info("Verified on My creators screen - title visible");
    }

    @Step("Navigate to My creators screen via Settings")
    public void navigateToMyCreators() {
        logger.info("Navigating to My creators screen");
        
        clickSettingsIcon();
        clickMyCreatorsTile();
        verifyOnMyCreatorsScreen();
        
        logger.info("Navigated to My creators screen");
    }

    // ===== Creator Details Methods =====

    @Step("Check if any creators are listed")
    public boolean hasCreatorsListed() {
        logger.info("Checking if any creators are listed");
        try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        
        Locator arrowRight = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("arrow right"));
        int count = arrowRight.count();
        logger.info("Found {} creator arrow(s) on My Creators screen", count);
        return count > 0;
    }

    @Step("Click on first creator to view details")
    public void clickFirstCreatorArrow() {
        logger.info("Clicking on first creator arrow to view details");
        
        Locator arrowRight = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("arrow right")).first();
        waitVisible(arrowRight, DEFAULT_WAIT);
        clickWithRetry(arrowRight, 1, ConfigReader.getElementRetryDelay());
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        logger.info("Clicked on first creator arrow");
    }

    @Step("Click Cancel button to navigate back")
    public void clickCancelButton() {
        logger.info("Clicking Cancel button");
        
        Locator cancelButton = page.getByRole(AriaRole.BUTTON, 
                new Page.GetByRoleOptions().setName("Cancel").setExact(true));
        waitVisible(cancelButton.first(), DEFAULT_WAIT);
        clickWithRetry(cancelButton.first(), 1, ConfigReader.getElementRetryDelay());
        waitForAnimation();
        logger.info("Clicked Cancel button");
    }

    @Step("Click See all results to load more creators (optional)")
    public void clickSeeAllResults() {
        logger.info("Attempting to click 'See all results' if present");
        
        Locator seeAllResults = page.getByText("See all results");
        for (int i = 0; i < ConfigReader.getMaxScrollAttempts(); i++) {
            if (seeAllResults.count() > 0 && safeIsVisible(seeAllResults.first())) break;
            page.mouse().wheel(0, ConfigReader.getScrollStepSize());
            try { page.waitForTimeout(ConfigReader.getScrollWaitBetween()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        try {
            if (seeAllResults.count() > 0 && safeIsVisible(seeAllResults.first())) {
                clickWithRetry(seeAllResults.first(), 1, ConfigReader.getElementRetryDelay());
                waitForPageLoad();
                logger.info("Clicked 'See all results' - loading more creators");
            } else {
                logger.info("'See all results' not found - continuing without it");
            }
        } catch (Exception e) {
            logger.info("'See all results' not available - continuing test");
        }
    }

    @Step("Scroll to last creator avatar in the list")
    public void scrollToLastCreatorAvatar() {
        logger.info("Scrolling to last creator avatar in the list");
        int lastCount = 0;
        int stableRounds = 0;
        for (int i = 0; i < ConfigReader.getMaxScrollAttempts(); i++) {
            int currentCount = page.locator(".fanSubscriptionPageAvatarImg").count();
            if (currentCount == lastCount) {
                stableRounds++;
                if (stableRounds > 2) {
                    logger.info("Reached end of list with {} creators after {} scroll(s)", currentCount, i);
                    break;
                }
            } else {
                stableRounds = 0;
            }
            lastCount = currentCount;
            page.mouse().wheel(0, ConfigReader.getScrollStepSize());
            try { page.waitForTimeout(ConfigReader.getScrollWaitBetween()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        waitForUiToSettle();
        logger.info("Scrolled to last creator avatar (total visible: {})", lastCount);
    }

    @Step("Scroll to first creator avatar in the list")
    public void scrollToFirstCreatorAvatar() {
        logger.info("Scrolling back to first creator avatar");
        for (int i = 0; i < ConfigReader.getMaxScrollAttempts(); i++) {
            page.mouse().wheel(0, -ConfigReader.getScrollStepSize());
            try { page.waitForTimeout(ConfigReader.getScrollWaitBetween()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        Locator firstAvatar = page.locator(".fanSubscriptionPageAvatarImg").first();
        if (safeIsVisible(firstAvatar)) {
            logger.info("First creator avatar is now visible");
        }
        waitForUiToSettle();
        logger.info("Scrolled back to first creator avatar");
    }

    @Step("Click arrow left to navigate back")
    public void clickArrowLeft() {
        logger.info("Clicking arrow left to navigate back");
        
        Locator arrowLeft = page.getByRole(AriaRole.IMG,
                new Page.GetByRoleOptions().setName("arrow left"));
        arrowLeft.first().click(new Locator.ClickOptions().setForce(true));
        try { page.waitForTimeout(ConfigReader.getPageLoadTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        logger.info("Clicked arrow left");
    }

    @Step("Navigate back to home screen")
    public void navigateBackToHome() {
        logger.info("Navigating back to home screen");
        
        // Click arrow left twice to go back to home
        clickArrowLeft();
        clickArrowLeft();
        
        logger.info("Navigated back to home screen");
    }

    @Step("Pause to view details")
    public void pauseToViewDetails(int milliseconds) {
        logger.info("Pausing for {} ms to view details", milliseconds);
        try { page.waitForTimeout(milliseconds); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
    }
}

