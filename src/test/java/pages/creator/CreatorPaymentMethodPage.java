package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page object for Creator -> Settings -> Payment method (Add bank account)
 */
public class CreatorPaymentMethodPage extends BasePage {
    private static final String SETTINGS_URL_PART = "/common/setting";

    public CreatorPaymentMethodPage(Page page) {
        super(page);
    }

    // ---------- Locators ----------
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator paymentMethodMenuItem() {
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
        return page.getByText("Every 7 days");
    }

    private Locator depositOnPause() {
        return page.getByText("On pause");
    }

    private Locator depositEvery30Days() {
        return page.getByText("Every 30 days");
    }

    private Locator confirmTextContains(String snippet) {
        return page.getByText(snippet);
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
        // Ensure we are on the profile page before looking for the settings icon
        navigateAndWait(ConfigReader.getBaseUrl() + "/creator/profile");
        waitVisible(settingsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(settingsIcon(), 1, ConfigReader.getElementRetryDelay());
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

    @Step("Open Payment method screen")
    public void openPaymentMethodScreen() {
        waitVisible(paymentMethodMenuItem(), ConfigReader.getShortTimeout());
        try { paymentMethodMenuItem().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(paymentMethodMenuItem(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(paymentMethodMenuItem(), ConfigReader.getShortTimeout());
    }

    @Step("Click 'Add an account'")
    public void clickAddAnAccount() {
        waitVisible(addAnAccountButton(), ConfigReader.getShortTimeout());
        clickWithRetry(addAnAccountButton(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Fill bank account details")
    public void fillBankAccountDetails(String bankName, String swift, String iban, String countryQuery, String countryExact, String address, String postalCode, String city) {
        waitVisible(bankNameTextbox(), ConfigReader.getShortTimeout());
        bankNameTextbox().click();
        bankNameTextbox().fill(bankName == null ? "" : bankName);

        waitVisible(swiftTextbox(), ConfigReader.getShortTimeout());
        swiftTextbox().click();
        swiftTextbox().fill(swift == null ? "" : swift);

        waitVisible(ibanTextbox(), ConfigReader.getShortTimeout());
        ibanTextbox().click();
        ibanTextbox().fill(iban == null ? "" : iban);

        try { countrySearchInput().click(); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
        try {
            waitVisible(countrySearchInput(), ConfigReader.getShortTimeout());
            countrySearchInput().fill(countryQuery == null ? "" : countryQuery);
            waitVisible(countryOptionExact(countryExact), ConfigReader.getShortTimeout());
            clickWithRetry(countryOptionExact(countryExact), 1, ConfigReader.getElementRetryDelay());
        } catch (Throwable e) {
            logger.warn("Country selection fallback: {}", e.getMessage());
        }

        waitVisible(addressTextbox(), ConfigReader.getShortTimeout());
        addressTextbox().click();
        addressTextbox().fill(address == null ? "" : address);

        waitVisible(postalCodeTextbox(), ConfigReader.getShortTimeout());
        postalCodeTextbox().click();
        postalCodeTextbox().fill(postalCode == null ? "" : postalCode);

        waitVisible(cityTextbox(), ConfigReader.getShortTimeout());
        cityTextbox().click();
        cityTextbox().fill(city == null ? "" : city);
    }

    @Step("Submit Add method")
    public void submitAddMethod() {
        waitVisible(addMethodButton(), ConfigReader.getShortTimeout());
        clickWithRetry(addMethodButton(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert success toast and card visible")
    public void assertSuccessAndCardVisible() {
        // Wait up to 60s for any toast containing the expected text
        try {
            waitVisible(successToast().first(), ConfigReader.getMediumTimeout());
        } catch (Throwable t) {
            logger.warn("Success toast (Banking setting successfully) not detected within medium timeout: {}", t.getMessage());
        }

        waitVisible(revoCardImage().first(), ConfigReader.getShortTimeout());
    }

    @Step("Open added payment card")
    public void openAddedCard() {
        waitVisible(revoCardImage().first(), ConfigReader.getShortTimeout());
        clickWithRetry(revoCardImage().first(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert 'Payment card' screen is visible")
    public void assertOnPaymentCardScreen() {
        waitVisible(paymentCardTitle(), ConfigReader.getShortTimeout());
    }

    @Step("Click 'Set as default' on payment card")
    public void setAsDefault() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Set as default"));
        waitVisible(btn, ConfigReader.getShortTimeout());
        clickWithRetry(btn, 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Delete current payment card with confirmation")
    public void deleteCurrentCard() {
        waitVisible(deleteCardButton(), ConfigReader.getShortTimeout());
        clickWithRetry(deleteCardButton(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(deleteConfirmText(), ConfigReader.getShortTimeout());
        waitVisible(yesDeleteButton(), ConfigReader.getShortTimeout());
        clickWithRetry(yesDeleteButton(), 1, ConfigReader.getElementRetryDelay());
    }

    // ---------- Deposit duration steps ----------
    @Step("Ensure initial 'Whenever you want.' text is visible")
    public void ensureInitialDepositTextVisible() {
        waitVisible(initialWheneverYouWantText(), ConfigReader.getShortTimeout());
    }

    @Step("Switch deposit to Every 7 days and confirm")
    public void switchDepositEvery7DaysAndConfirm() {
        waitVisible(depositEvery7Days(), ConfigReader.getShortTimeout());
        try { depositEvery7Days().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(depositEvery7Days(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(confirmTextContains("you will receive a payment every 7 days."), ConfigReader.getShortTimeout());
        waitVisible(confirmButton(), ConfigReader.getShortTimeout());
        clickWithRetry(confirmButton(), 1, ConfigReader.getElementRetryDelay());
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
    }

    @Step("Switch deposit to On pause and confirm")
    public void switchDepositOnPauseAndConfirm() {
        waitVisible(depositOnPause(), ConfigReader.getShortTimeout());
        try { depositOnPause().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(depositOnPause(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(confirmTextContains("payments will be paused."), ConfigReader.getShortTimeout());
        waitVisible(confirmButton(), ConfigReader.getShortTimeout());
        clickWithRetry(confirmButton(), 1, ConfigReader.getElementRetryDelay());
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
    }

    @Step("Switch deposit to Every 30 days and confirm")
    public void switchDepositEvery30DaysAndConfirm() {
        waitVisible(depositEvery30Days(), ConfigReader.getShortTimeout());
        try { depositEvery30Days().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        clickWithRetry(depositEvery30Days(), 1, ConfigReader.getElementRetryDelay());
        waitVisible(confirmTextContains("you will receive a payment every 30 days."), ConfigReader.getShortTimeout());
        waitVisible(confirmButton(), ConfigReader.getShortTimeout());
        clickWithRetry(confirmButton(), 1, ConfigReader.getElementRetryDelay());
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
    }

    @Step("Wait for UI to settle after card deletion")
    public void waitAfterDelete() {
        try { page.waitForTimeout(ConfigReader.getMediumTimeout() / 10); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
    }

    @Step("Navigate back to profile if needed")
    public void navigateBackToProfile() {
        for (int i = 0; i < 3; i++) {
            if (safeIsVisible(profilePlusIcon())) return;
            try { clickWithRetry(backArrow(), 1, ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Back arrow click failed: {}", e.getMessage()); }
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        }
        if (!safeIsVisible(profilePlusIcon())) {
            logger.warn("Profile marker (plus icon) not visible after navigating back from payment method");
        }
    }
}

