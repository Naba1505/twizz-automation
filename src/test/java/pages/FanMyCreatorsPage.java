package pages;

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

    @Step("Click See all results to load more creators")
    public void clickSeeAllResults() {
        logger.info("Clicking See all results");
        
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
        
        waitVisible(seeAllResults.first(), DEFAULT_WAIT);
        seeAllResults.click();
        
        // Wait for screen to buffer and load remaining creators
        waitForPageLoad();
        logger.info("Clicked See all results - loading more creators");
    }

    @Step("Scroll to end of creators list")
    public void scrollToEndOfList() {
        logger.info("Scrolling to end of creators list");
        smartScroll(1, "end of creators list");
        waitForUiToSettle();
        logger.info("Scrolled to end of creators list");
    }

    @Step("Click on last creator to view details")
    public void clickLastCreatorArrow() {
        logger.info("Clicking on last creator arrow to view details");
        
        // Use robust approach: find all arrow right icons and click the last one
        Locator arrowRights = page.getByRole(AriaRole.IMG, 
                new Page.GetByRoleOptions().setName("arrow right"));
        int count = arrowRights.count();
        
        if (count > 0) {
            // Click the last available arrow
            Locator lastArrow = arrowRights.nth(count - 1);
            waitVisible(lastArrow, DEFAULT_WAIT);
            clickWithConfigurableRetry(lastArrow);
            logger.info("Clicked on last creator arrow (index {} of {})", count - 1, count);
        } else {
            logger.warn("No arrow right icons found for creators");
            throw new RuntimeException("No creators available to interact with");
        }
        
        waitForUiToSettle();
    }

    @Step("Scroll to top of creators list")
    public void scrollToTop() {
        logger.info("Scrolling to top of creators list");
        
        // Scroll up multiple times
        for (int i = 0; i < 15; i++) {
            page.mouse().wheel(0, -500);
            try { page.waitForTimeout(200); } catch (Throwable ignored) { }
        }
        
        // Verify title is visible
        Locator myCreatorsTitle = page.getByText("My creators");
        try {
            waitVisible(myCreatorsTitle.first(), DEFAULT_WAIT);
            logger.info("Scrolled to top - My creators title visible");
        } catch (Throwable e) {
            logger.warn("Could not verify My creators title after scrolling to top");
        }
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
