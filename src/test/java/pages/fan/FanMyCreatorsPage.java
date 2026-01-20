package pages.fan;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;
import utils.ConfigReader;


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
        waitVisible(settingsIcon.first(), DEFAULT_WAIT);
        settingsIcon.click();
        
        waitForAnimation();
        logger.info("Clicked Settings icon");
    }

    @Step("Click My creators tile")
    public void clickMyCreatorsTile() {
        logger.info("Clicking My creators tile");
        
        Locator myCreatorsTile = page.getByText("My creators");
        
        // Scroll to make element visible if needed
        if (myCreatorsTile.count() > 0) {
            myCreatorsTile.first().scrollIntoViewIfNeeded();
        }
        
        waitVisible(myCreatorsTile.first(), DEFAULT_WAIT);
        myCreatorsTile.first().click();
        
        waitForAnimation();
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
        try { page.waitForTimeout(2000); } catch (Throwable ignored) { }
        
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
        arrowRight.click();
        
        try { page.waitForTimeout(1000); } catch (Throwable ignored) { }
        logger.info("Clicked on first creator arrow");
    }

    @Step("Click Cancel button to navigate back")
    public void clickCancelButton() {
        logger.info("Clicking Cancel button");
        
        Locator cancelButton = page.getByRole(AriaRole.BUTTON, 
                new Page.GetByRoleOptions().setName("Cancel").setExact(true));
        waitVisible(cancelButton.first(), DEFAULT_WAIT);
        cancelButton.click();
        
        waitForAnimation();
        logger.info("Clicked Cancel button");
    }

    @Step("Click See all results to load more creators (optional)")
    public void clickSeeAllResults() {
        logger.info("Attempting to click 'See all results' if present");
        
        Locator seeAllResults = page.getByText("See all results");
        
        // Scroll down until element is visible using configurable parameters
        int maxScrollAttempts = ConfigReader.getMaxScrollAttempts();
        int scrollStep = ConfigReader.getScrollStepSize();
        int waitBetween = ConfigReader.getScrollWaitBetween();
        
        for (int i = 0; i < maxScrollAttempts; i++) {
            if (seeAllResults.count() > 0 && safeIsVisible(seeAllResults.first())) {
                break;
            }
            page.mouse().wheel(0, scrollStep);
            try { page.waitForTimeout(waitBetween); } catch (Throwable ignored) { }
        }
        
        // Try to click if visible, but don't fail if not found
        try {
            if (seeAllResults.count() > 0 && safeIsVisible(seeAllResults.first())) {
                seeAllResults.first().click();
                waitForPageLoad();
                logger.info("Clicked 'See all results' - loading more creators");
            } else {
                logger.info("'See all results' button not found - continuing without it (may not be needed)");
            }
        } catch (Exception e) {
            logger.info("'See all results' button not available or not needed - continuing test");
        }
    }

    @Step("Scroll to last creator avatar in the list")
    public void scrollToLastCreatorAvatar() {
        logger.info("Scrolling to last creator avatar in the list");
        
        // Scroll down until we can't find any more creator avatars
        int maxScrollAttempts = ConfigReader.getMaxScrollAttempts();
        int scrollStep = ConfigReader.getScrollStepSize();
        int waitBetween = ConfigReader.getScrollWaitBetween();
        
        int lastVisibleIndex = 0;
        for (int i = 0; i < maxScrollAttempts; i++) {
            // Check for creator avatars with increasing index
            for (int idx = lastVisibleIndex + 1; idx <= 50; idx++) {
                Locator avatar = page.locator("div:nth-child(" + idx + ") > .fanSubscriptionPageAvatarImg");
                if (avatar.count() > 0 && safeIsVisible(avatar.first())) {
                    lastVisibleIndex = idx;
                }
            }
            
            // Scroll down
            page.mouse().wheel(0, scrollStep);
            try { page.waitForTimeout(waitBetween); } catch (Throwable ignored) { }
            
            // Check if we've reached the bottom (no new avatars appearing)
            int newLastIndex = lastVisibleIndex;
            for (int idx = lastVisibleIndex + 1; idx <= 50; idx++) {
                Locator avatar = page.locator("div:nth-child(" + idx + ") > .fanSubscriptionPageAvatarImg");
                if (avatar.count() > 0 && safeIsVisible(avatar.first())) {
                    newLastIndex = idx;
                }
            }
            if (newLastIndex == lastVisibleIndex && i > 3) {
                logger.info("Reached end of list at creator index {} after {} scroll(s)", lastVisibleIndex, i);
                break;
            }
            lastVisibleIndex = newLastIndex;
        }
        
        waitForUiToSettle();
        logger.info("Scrolled to last creator avatar (index: {})", lastVisibleIndex);
    }

    @Step("Scroll to first creator avatar in the list")
    public void scrollToFirstCreatorAvatar() {
        logger.info("Scrolling back to first creator avatar");
        
        int maxScrollAttempts = ConfigReader.getMaxScrollAttempts();
        int scrollStep = ConfigReader.getScrollStepSize();
        int waitBetween = ConfigReader.getScrollWaitBetween();
        
        // First, scroll all the way to the top
        for (int i = 0; i < maxScrollAttempts; i++) {
            page.mouse().wheel(0, -scrollStep);
            try { page.waitForTimeout(waitBetween); } catch (Throwable ignored) { }
            logger.info("Scroll up attempt {}", i + 1);
        }
        
        // Verify first creator avatar (div:nth-child(1) or div:nth-child(2)) is visible
        Locator firstCreatorAvatar = page.locator("div:nth-child(2) > .fanSubscriptionPageAvatarImg");
        if (firstCreatorAvatar.count() > 0 && safeIsVisible(firstCreatorAvatar.first())) {
            logger.info("First creator avatar is now visible");
        } else {
            // Try alternative locator
            firstCreatorAvatar = page.locator(".fanSubscriptionPageAvatarImg").first();
            if (firstCreatorAvatar.count() > 0 && safeIsVisible(firstCreatorAvatar)) {
                logger.info("First creator avatar (alternative locator) is now visible");
            }
        }
        
        waitForUiToSettle();
        logger.info("Scrolled back to first creator avatar");
    }

    @Step("Click arrow left to navigate back")
    public void clickArrowLeft() {
        logger.info("Clicking arrow left to navigate back");
        
        Locator arrowLeft = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("arrow left"));
        waitVisible(arrowLeft.first(), DEFAULT_WAIT);
        arrowLeft.click();
        
        waitForAnimation();
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
        try { page.waitForTimeout(milliseconds); } catch (Throwable ignored) { }
    }
}

