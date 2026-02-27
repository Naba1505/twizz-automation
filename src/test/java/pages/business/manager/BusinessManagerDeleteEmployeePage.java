package pages.business.manager;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;

/**
 * Page Object for Business Manager Delete Employee
 * Flow: Manager Dashboard → Agency → Employee Details → Delete Employee
 */
public class BusinessManagerDeleteEmployeePage extends BasePage {

    public BusinessManagerDeleteEmployeePage(Page page) {
        super(page);
    }

    @Step("Click on Agency icon")
    public void clickAgencyIcon() {
        Locator agencyIcon = page.locator("img[alt='Agency']");
        agencyIcon.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Delete Employee] Clicked on Agency icon");
    }

    @Step("Verify agency content element is visible")
    public boolean isAgencyContentElementVisible() {
        Locator contentElement = page.locator("body div[id='root'] div[class='app'] div[class='ant-layout manager-layout css-1m2bkf9'] main[class='ant-layout-content manager-content css-1m2bkf9'] div[class='manager-agency-page manager-agency-desktop'] div[class='manager-agency-desktop-content'] div:nth-child(1) div:nth-child(1) div:nth-child(1) p:nth-child(1)");
        boolean isVisible = contentElement.isVisible();
        logger.info("[Manager Delete Employee] Agency content element visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click on employee info element")
    public void clickEmployeeInfo() {
        Locator employeeInfo = page.locator(".manager-agency-team-user-info");
        employeeInfo.first().click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Delete Employee] Clicked on employee info element");
    }

    @Step("Verify 'Twizz identity Card' heading is visible")
    public boolean isTwizzIdentityCardHeadingVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Twizz identity Card"));
        boolean isVisible = heading.isVisible();
        logger.info("[Manager Delete Employee] 'Twizz identity Card' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click on 'Delete this account' text")
    public void clickDeleteAccountText() {
        Locator deleteText = page.getByText("Delete this account");
        deleteText.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Delete Employee] Clicked on 'Delete this account' text");
    }

    @Step("Verify delete confirmation dialog is visible")
    public boolean isDeleteConfirmationDialogVisible() {
        Locator confirmationText = page.getByText("Do you really want to delete");
        boolean isVisible = confirmationText.isVisible();
        logger.info("[Manager Delete Employee] Delete confirmation dialog visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click 'Validate' button to confirm deletion")
    public void clickValidateButton() {
        Locator validateButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Validate"));
        validateButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Delete Employee] Clicked 'Validate' button");
    }

    @Step("Verify 'Employee deleted successfully' message is visible")
    public boolean isEmployeeDeletedSuccessMessageVisible() {
        Locator successMessage = page.getByText("Employee deleted successfully");
        try {
            successMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Manager Delete Employee] Employee deleted success message did not appear within timeout");
        }
        boolean isVisible = successMessage.isVisible();
        logger.info("[Manager Delete Employee] 'Employee deleted successfully' message visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Complete delete employee flow")
    public void deleteEmployee() {
        clickAgencyIcon();
        clickEmployeeInfo();
        clickDeleteAccountText();
        clickValidateButton();
        logger.info("[Manager Delete Employee] Completed delete employee flow");
    }
}
