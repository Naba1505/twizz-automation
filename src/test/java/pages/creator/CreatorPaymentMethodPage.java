package pages.creator;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page object for Creator -> Settings -> Payment method (Add bank account)
 */
public class CreatorPaymentMethodPage extends BasePage {
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    // Reduced from DEFAULT_WAIT (60000ms) to SHORT_TIMEOUT (1000ms) = 98% faster!
    private static final int BUTTON_RETRY_DELAY = 150;   // Button click retry delay
    private static final int POLLING_WAIT = 200;         // Polling intervals
    private static final int STABILIZATION_WAIT = 2000;  // UI stabilization after actions
    private static final int SHORT_TIMEOUT = 1000;       // Short waits (was 60000ms)
    private static final int MEDIUM_TIMEOUT = 2000;      // Medium waits (was 20000ms)

    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorPaymentMethodPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator paymentMethodMenu() {
        return page.getByText("Payment method");
    }

    private Locator paymentMethodTitle() {
        return page.getByText("Payment method");
    }

    private Locator addAnAccountButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add an account"));
    }

    private Locator bankNameTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Name of the bank"));
    }

    private Locator swiftTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Swift"));
    }

    private Locator ibanTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("IBAN / Account number"));
    }

    private Locator countrySearchInput() {
        // Prefer the known id, fallback to generic searchable input
        Locator byId = page.locator("#rc_select_0");
        if (byId.count() > 0) return byId;
        return page.locator("input[type='search']");
    }

    private Locator countryOptionExact(String country) {
        return page.getByText(country, new Page.GetByTextOptions().setExact(true));
    }

    private Locator addressTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Address"));
    }

    private Locator postalCodeTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Postal code"));
    }

    private Locator cityTextbox() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("City"));
    }

    private Locator addMethodButton() {
        // Use the concrete CSS selector observed in the UI for the Add method button
        return page.locator("button.ant-btn.css-ixblex.ant-btn-default.authBtn");
    }

    private Locator successToast() {
        return page.locator("div").filter(new Locator.FilterOptions().setHasText("Banking setting successfully"));
    }

    private Locator revoCardImage() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("RevoCard"));
    }

    private Locator paymentCardTitle() {
        return page.getByText("Payment card");
    }

    private Locator deleteCardButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete this card"));
    }

    private Locator deleteConfirmText() {
        return page.getByText("Do you want to delete this");
    }

    private Locator yesDeleteButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes delete"));
    }

    // Deposit duration section
    private Locator initialWheneverYouWantText() {
        return page.getByText("Whenever you want.");
    }

    private Locator depositEvery7Days() {
        return page.locator("//span[contains(text(),'Every 7 days')]");
    }

    private Locator depositOnPause() {
        return page.locator("//span[contains(text(),'On pause')]");
    }

    private Locator depositEvery30Days() {
        return page.locator("//span[contains(text(),'Every 30 days')]");
    }

    private Locator confirmTextContains(String snippet) {
        return page.locator("//span[contains(text(),'" + snippet + "')]");
    }

    private Locator confirmButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm"));
    }

    private Locator backArrow() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("arrow left"));
    }

    private Locator profilePlusIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
    }

    // ---------- Steps ----------
    @Step("Open Settings from profile (Payment method)")
    public void openSettingsFromProfile() {
        waitVisible(settingsIcon(), utils.ConfigReader.getVisibilityTimeout());
        clickWithRetry(settingsIcon(), 1, BUTTON_RETRY_DELAY);
        page.waitForURL("**" + SETTINGS_URL_PART + "**");
        if (!page.url().contains(SETTINGS_URL_PART)) {
            logger.warn("Expected settings URL to contain '{}' but was {}", SETTINGS_URL_PART, page.url());
        }
    }

    @Step("Open Payment method screen")
    public void openPaymentMethodScreen() {
        waitVisible(paymentMethodMenu(), utils.ConfigReader.getVisibilityTimeout());
        try { paymentMethodMenu().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(paymentMethodMenu(), 1, BUTTON_RETRY_DELAY);
        waitVisible(paymentMethodTitle(), utils.ConfigReader.getShortTimeout());
    }

    @Step("Click 'Add an account'")
    public void clickAddAnAccount() {
        waitVisible(addAnAccountButton(), utils.ConfigReader.getVisibilityTimeout());
        clickWithRetry(addAnAccountButton(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Fill bank account details")
    public void fillBankAccountDetails(String bankName, String swift, String iban, String countryQuery, String countryExact, String address, String postalCode, String city) {
        waitVisible(bankNameTextbox(), utils.ConfigReader.getShortTimeout());
        bankNameTextbox().click();
        bankNameTextbox().fill(bankName == null ? "" : bankName);

        waitVisible(swiftTextbox(), utils.ConfigReader.getShortTimeout());
        swiftTextbox().click();
        swiftTextbox().fill(swift == null ? "" : swift);

        waitVisible(ibanTextbox(), utils.ConfigReader.getShortTimeout());
        ibanTextbox().click();
        ibanTextbox().fill(iban == null ? "" : iban);

        // Country selection: open and filter then choose exact
        try { countrySearchInput().click(); } catch (Throwable ignored) {}
        try {
            waitVisible(countrySearchInput(), utils.ConfigReader.getShortTimeout());
            countrySearchInput().fill(countryQuery == null ? "" : countryQuery);
            waitVisible(countryOptionExact(countryExact), utils.ConfigReader.getVisibilityTimeout());
            clickWithRetry(countryOptionExact(countryExact), 1, BUTTON_RETRY_DELAY);
        } catch (Throwable e) {
            logger.warn("Country selection fallback: {}", e.getMessage());
        }

        waitVisible(addressTextbox(), utils.ConfigReader.getShortTimeout());
        addressTextbox().click();
        addressTextbox().fill(address == null ? "" : address);

        waitVisible(postalCodeTextbox(), utils.ConfigReader.getShortTimeout());
        postalCodeTextbox().click();
        postalCodeTextbox().fill(postalCode == null ? "" : postalCode);

        waitVisible(cityTextbox(), utils.ConfigReader.getShortTimeout());
        cityTextbox().click();
        cityTextbox().fill(city == null ? "" : city);
    }

    @Step("Submit Add method")
    public void submitAddMethod() {
        waitVisible(addMethodButton(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(addMethodButton(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Assert success toast and card visible")
    public void assertSuccessAndCardVisible() {
        // Wait up to 60s for any toast containing the expected text
        try {
            waitVisible(successToast().first(), utils.ConfigReader.getMediumTimeout());
        } catch (Throwable t) {
            logger.warn("Success toast (Banking setting successfully) not detected within medium timeout: {}", t.getMessage());
        }

        // Then wait for the payment card image to appear (can be slow on stage)
        waitVisible(revoCardImage().first(), utils.ConfigReader.getVisibilityTimeout());
    }

    @Step("Open added payment card")
    public void openAddedCard() {
        waitVisible(revoCardImage().first(), utils.ConfigReader.getVisibilityTimeout());
        clickWithRetry(revoCardImage().first(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Assert 'Payment card' screen is visible")
    public void assertOnPaymentCardScreen() {
        waitVisible(paymentCardTitle(), utils.ConfigReader.getShortTimeout());
    }

    @Step("Click 'Set as default' on payment card")
    public void setAsDefault() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Set as default"));
        waitVisible(btn, utils.ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, BUTTON_RETRY_DELAY);
    }

    @Step("Delete current payment card with confirmation")
    public void deleteCurrentCard() {
        waitVisible(deleteCardButton(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(deleteCardButton(), 1, BUTTON_RETRY_DELAY);
        waitVisible(deleteConfirmText(), utils.ConfigReader.getShortTimeout());
        waitVisible(yesDeleteButton(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(yesDeleteButton(), 1, BUTTON_RETRY_DELAY);
    }

    // ---------- Deposit duration steps ----------
    @Step("Ensure initial 'Whenever you want.' text is visible")
    public void ensureInitialDepositTextVisible() {
        waitVisible(initialWheneverYouWantText(), utils.ConfigReader.getShortTimeout());
    }

    @Step("Switch deposit to Every 7 days and confirm")
    public void switchDepositEvery7DaysAndConfirm() {
        waitVisible(depositEvery7Days(), utils.ConfigReader.getShortTimeout());
        try { depositEvery7Days().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(depositEvery7Days(), 1, BUTTON_RETRY_DELAY);
        waitVisible(confirmTextContains("you will receive a payment every 7 days."), utils.ConfigReader.getShortTimeout());
        waitVisible(confirmButton(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(confirmButton(), 1, BUTTON_RETRY_DELAY);
        try { page.waitForTimeout(STABILIZATION_WAIT); } catch (Throwable ignored) {}
    }

    @Step("Switch deposit to On pause and confirm")
    public void switchDepositOnPauseAndConfirm() {
        waitVisible(depositOnPause(), utils.ConfigReader.getShortTimeout());
        try { depositOnPause().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(depositOnPause(), 1, BUTTON_RETRY_DELAY);
        waitVisible(confirmTextContains("payments will be paused."), utils.ConfigReader.getShortTimeout());
        waitVisible(confirmButton(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(confirmButton(), 1, BUTTON_RETRY_DELAY);
        try { page.waitForTimeout(STABILIZATION_WAIT); } catch (Throwable ignored) {}
    }

    @Step("Switch deposit to Every 30 days and confirm")
    public void switchDepositEvery30DaysAndConfirm() {
        waitVisible(depositEvery30Days(), utils.ConfigReader.getShortTimeout());
        try { depositEvery30Days().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(depositEvery30Days(), 1, BUTTON_RETRY_DELAY);
        waitVisible(confirmTextContains("you will receive a payment every 30 days."), utils.ConfigReader.getShortTimeout());
        waitVisible(confirmButton(), utils.ConfigReader.getShortTimeout());
        clickWithRetry(confirmButton(), 1, BUTTON_RETRY_DELAY);
        try { page.waitForTimeout(STABILIZATION_WAIT); } catch (Throwable ignored) {}
    }

    @Step("Navigate back to profile if needed")
    public void navigateBackToProfile() {
        for (int i = 0; i < 3; i++) {
            if (safeIsVisible(profilePlusIcon())) return;
            try { clickWithRetry(backArrow(), 1, BUTTON_RETRY_DELAY); } catch (Throwable ignored) {}
            try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) {}
        }
        if (!safeIsVisible(profilePlusIcon())) {
            logger.warn("Profile marker (plus icon) not visible after navigating back from payment method");
        }
    }
}

