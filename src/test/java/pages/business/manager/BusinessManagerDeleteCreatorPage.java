package pages.business.manager;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;

/**
 * Page Object for Business Manager Delete Creator
 * Flow: Manager Dashboard → Agency → Creator Details → Delete Creator
 */
public class BusinessManagerDeleteCreatorPage extends BasePage {

    public BusinessManagerDeleteCreatorPage(Page page) {
        super(page);
    }

    @Step("Click on Agency icon")
    public void clickAgencyIcon() {
        Locator agencyIcon = page.locator("img[alt='Agency']");
        agencyIcon.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Delete Creator] Clicked on Agency icon");
    }

    @Step("Verify agency content element is visible")
    public boolean isAgencyContentElementVisible() {
        Locator contentElement = page.locator("body div[id='root'] div[class='app'] div[class='ant-layout manager-layout css-1m2bkf9'] main[class='ant-layout-content manager-content css-1m2bkf9'] div[class='manager-agency-page manager-agency-desktop'] div[class='manager-agency-desktop-content'] div:nth-child(1) div:nth-child(1) div:nth-child(1) p:nth-child(1)");
        boolean isVisible = contentElement.isVisible();
        logger.info("[Manager Delete Creator] Agency content element visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click on creator info element")
    public void clickCreatorInfo() {
        Locator creatorInfo = page.locator(".manager-agency-team-user-info");
        creatorInfo.first().click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Delete Creator] Clicked on creator info element");
    }

    @Step("Verify 'Twizz identity Card' heading is visible")
    public boolean isTwizzIdentityCardHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Twizz identity Card"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Delete Creator] 'Twizz identity Card' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click on 'Delete the creator' text")
    public void clickDeleteCreatorText() {
        Locator deleteText = page.getByText("Delete the creator");
        deleteText.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Delete Creator] Clicked on 'Delete the creator' text");
    }

    @Step("Verify delete confirmation dialog is visible")
    public boolean isDeleteConfirmationDialogVisible() {
        Locator confirmationText = page.getByText("Do you really want to delete");
        boolean isVisible = confirmationText.isVisible();
        logger.info("[Manager Delete Creator] Delete confirmation dialog visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click 'Validate' button to confirm deletion")
    public void clickValidateButton() {
        Locator validateButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Validate"));
        validateButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Delete Creator] Clicked 'Validate' button");
    }

    @Step("Verify 'Creator deleted successfully' message is visible")
    public boolean isCreatorDeletedSuccessMessageVisible() {
        Locator successMessage = page.getByText("Creator deleted successfully");
        try {
            successMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Manager Delete Creator] Creator deleted success message did not appear within timeout");
        }
        boolean isVisible = successMessage.isVisible();
        logger.info("[Manager Delete Creator] 'Creator deleted successfully' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Complete delete creator flow")
    public void deleteCreator() {
        clickAgencyIcon();
        clickCreatorInfo();
        clickDeleteCreatorText();
        clickValidateButton();
        logger.info("[Manager Delete Creator] Completed delete creator flow");
    }
}
