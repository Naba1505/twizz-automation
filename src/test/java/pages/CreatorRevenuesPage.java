package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

public class CreatorRevenuesPage extends BasePage {
    // Selectors provided by user
    private static final String SELECTOR_CURRENCY_IMG = "div.currency-img";
    private static final String SELECTOR_VALIDATED_PRICE = "div span.ant-typography.font-32-bold.text-white-color.mr-10.css-ixblex";
    private static final String SELECTOR_WAITING_PRICE = "div span.ant-typography.font-24-bold.text-white-color.mr-10.css-ixblex";

    public CreatorRevenuesPage(Page page) {
        super(page);
    }

    private Locator revenuesIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Revenues icon"));
    }

    private Locator revenuesTitle() {
        return page.getByText("Revenues");
    }

    private Locator currencyImg() {
        return page.locator(SELECTOR_CURRENCY_IMG);
    }

    private Locator validatedPrice() {
        return page.locator(SELECTOR_VALIDATED_PRICE);
    }

    private Locator waitingPrice() {
        return page.locator(SELECTOR_WAITING_PRICE);
    }

    private Locator validatedInfoIcon() {
        return page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Validated for the next payment$")))
                .getByRole(AriaRole.IMG, new Locator.GetByRoleOptions().setName("info"));
    }

    private Locator waitingInfoIcon() {
        return page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Waiting for validation$")))
                .getByRole(AriaRole.IMG, new Locator.GetByRoleOptions().setName("info"));
    }

    private Locator infoCloseButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close"));
    }

    private Locator validatedInfoText() {
        return page.getByText("Earnings are confirmed and will be paid to you with your next payment.");
    }

    private Locator waitingInfoText() {
        // Narrow CSS to avoid strict mode violation and uniquely match the modal description
        return page.locator("div span.ant-typography.info-modal-description.css-ixblex");
    }

    @Step("Open Revenues from dashboard")
    public void openRevenues() {
        logger.info("[Revenues] Waiting for Revenues icon to be visible");
        waitVisible(revenuesIcon(), DEFAULT_WAIT);
        logger.info("[Revenues] Clicking Revenues icon");
        clickWithRetry(revenuesIcon(), 1, 200);
        logger.info("[Revenues] Waiting for Revenues title to confirm navigation");
        waitVisible(revenuesTitle(), DEFAULT_WAIT);
        logger.info("[Revenues] Landed on Revenues screen");
    }

    @Step("Assert Revenues screen elements are visible")
    public void assertRevenuesScreen() {
        logger.info("[Revenues] Asserting screen elements: title, currency logo, validated & waiting price blocks");
        waitVisible(revenuesTitle(), DEFAULT_WAIT);
        waitVisible(currencyImg(), DEFAULT_WAIT);
        waitVisible(validatedPrice(), DEFAULT_WAIT);
        waitVisible(waitingPrice(), DEFAULT_WAIT);
        logger.info("[Revenues] Revenues screen elements are visible");
    }

    @Step("Check Validated info popover")
    public void checkValidatedInfo() {
        logger.info("[Revenues] Opening Validated info popover");
        clickWithRetry(validatedInfoIcon(), 1, 200);
        waitVisible(validatedInfoText(), DEFAULT_WAIT);
        logger.info("[Revenues] Validated info text is visible; closing popover");
        clickWithRetry(infoCloseButton(), 1, 200);
    }

    @Step("Check Waiting for validation info popover")
    public void checkWaitingInfo() {
        logger.info("[Revenues] Opening Waiting for validation info popover");
        clickWithRetry(waitingInfoIcon(), 1, 200);
        waitVisible(waitingInfoText(), DEFAULT_WAIT);
        logger.info("[Revenues] Waiting info text is visible; closing popover");
        clickWithRetry(infoCloseButton(), 1, 200);
    }
}
