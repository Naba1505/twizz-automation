package pages.business.manager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Page Object for Business Manager Delete Creator
 * Flow: Manager Dashboard → Agency → Creator Details → Delete Creator
 */
public class BusinessManagerDeleteCreatorPage {
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerDeleteCreatorPage.class);
    private final Page page;

    public BusinessManagerDeleteCreatorPage(Page page) {
        this.page = page;
    }

    @Step("Click on Agency icon")
    public void clickAgencyIcon() {
        Locator agencyIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Agency").setExact(true));
        agencyIcon.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(1000);
        logger.info("[Manager Delete Creator] Clicked on Agency icon");
    }

    @Step("Verify 'Your creators' text is visible")
    public boolean isYourCreatorsTextVisible() {
        Locator yourCreatorsText = page.getByText("Your creators");
        boolean isVisible = yourCreatorsText.isVisible();
        logger.info("[Manager Delete Creator] 'Your creators' text visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click on creator card")
    public void clickCreatorCard() {
        Locator creatorCard = page.locator("div").filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^smith · @john_smithNo employees at present\\.$"))).nth(1);
        creatorCard.click();
        page.waitForLoadState(LoadState.NETWORKIDLE);
        page.waitForTimeout(2000);
        logger.info("[Manager Delete Creator] Clicked on creator card");
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
        clickCreatorCard();
        clickDeleteCreatorText();
        clickValidateButton();
        logger.info("[Manager Delete Creator] Completed delete creator flow");
    }
}
