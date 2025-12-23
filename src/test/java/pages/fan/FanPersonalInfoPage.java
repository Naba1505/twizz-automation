package pages.fan;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

/**
 * Page object for Fan Personal Information settings screen.
 * Supports viewing and updating personal information fields.
 */
public class FanPersonalInfoPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(FanPersonalInfoPage.class);
    private static final int DEFAULT_WAIT = 10000;

    public FanPersonalInfoPage(Page page) {
        super(page);
    }

    // ================= Locators =================

    // Settings
    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Settings icon"));
    }

    private Locator settingsTitle() {
        return page.getByText("Settings");
    }

    // Personal Information menu and title
    private Locator personalInfoMenu() {
        return page.getByText("Personal information");
    }

    private Locator personalInfoTitle() {
        return page.getByText("Personal information");
    }

    // Field labels
    private Locator identityLabel() {
        return page.getByText("Identity");
    }

    private Locator userNameLabel() {
        return page.getByText("User name");
    }

    private Locator dateOfBirthLabel() {
        return page.getByText("Date of birth");
    }

    private Locator accountTypeLabel() {
        return page.getByText("Account type");
    }

    // Lock icons (for locked fields)
    private Locator lockIconFirst() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("lock")).first();
    }

    private Locator lockIconSecond() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("lock")).nth(1);
    }

    private Locator lockIconThird() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("lock")).nth(2);
    }

    // Editable fields
    private Locator emailInput() {
        return page.locator("input[name=\"email\"]");
    }

    private Locator phoneNumberInput() {
        return page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Number"));
    }

    // Account type - Fan selected
    private Locator fanAccountType() {
        return page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Fan$"))).nth(1);
    }

    // Register button
    private Locator registerButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register"));
    }

    // Success message
    private Locator successMessage() {
        return page.getByText("Updated Personal Information");
    }

    // ================= Navigation Methods =================

    @Step("Click Settings icon")
    public void clickSettingsIcon() {
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 2, 200);
        logger.info("[Fan][PersonalInfo] Clicked Settings icon");
    }

    @Step("Assert on Settings screen by viewing title")
    public void assertOnSettingsScreen() {
        waitVisible(settingsTitle(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] On Settings screen - title visible");
    }

    /**
     * Navigate from Fan home to Settings screen.
     */
    @Step("Navigate to Settings from Fan home")
    public void navigateToSettings() {
        clickSettingsIcon();
        assertOnSettingsScreen();
        logger.info("[Fan][PersonalInfo] Successfully navigated to Settings screen");
    }

    // ================= Personal Info Navigation =================

    @Step("Click Personal Information menu item")
    public void clickPersonalInfoMenu() {
        Locator menuItem = personalInfoMenu();
        waitVisible(menuItem, DEFAULT_WAIT);
        menuItem.scrollIntoViewIfNeeded();
        clickWithRetry(menuItem, 2, 200);
        page.waitForTimeout(1500); // Wait for screen to load
        logger.info("[Fan][PersonalInfo] Clicked 'Personal information' menu item");
    }

    @Step("Assert on Personal Information screen")
    public void assertOnPersonalInfoScreen() {
        waitVisible(personalInfoTitle(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] On Personal Information screen - title visible");
    }

    // ================= Field Verification Methods =================

    @Step("Verify Identity field is visible")
    public void verifyIdentityFieldVisible() {
        waitVisible(identityLabel(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] Identity field is visible");
    }

    @Step("Verify User name field is visible")
    public void verifyUserNameFieldVisible() {
        waitVisible(userNameLabel(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] User name field is visible");
    }

    @Step("Verify Date of birth field is visible")
    public void verifyDateOfBirthFieldVisible() {
        waitVisible(dateOfBirthLabel(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] Date of birth field is visible");
    }

    @Step("Verify Account type field is visible")
    public void verifyAccountTypeFieldVisible() {
        waitVisible(accountTypeLabel(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] Account type field is visible");
    }

    @Step("Verify lock icons are visible for locked fields")
    public void verifyLockedFields() {
        waitVisible(lockIconFirst(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] First lock icon visible (Identity locked)");
        
        waitVisible(lockIconSecond(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] Second lock icon visible (Identity locked)");
        
        waitVisible(lockIconThird(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] Third lock icon visible (User name locked)");
    }

    @Step("Verify Fan account type is selected")
    public void verifyFanAccountTypeSelected() {
        waitVisible(fanAccountType(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] Fan account type is selected");
    }

    // ================= Field Update Methods =================

    @Step("Update email field")
    public void updateEmail(String email) {
        Locator emailField = emailInput();
        waitVisible(emailField, DEFAULT_WAIT);
        emailField.click();
        emailField.clear();
        emailField.fill(email);
        logger.info("[Fan][PersonalInfo] Updated email to: {}", email);
    }

    @Step("Update phone number field")
    public void updatePhoneNumber(String phoneNumber) {
        Locator phoneField = phoneNumberInput();
        waitVisible(phoneField, DEFAULT_WAIT);
        phoneField.click();
        phoneField.clear();
        phoneField.fill(phoneNumber);
        logger.info("[Fan][PersonalInfo] Updated phone number to: {}", phoneNumber);
    }

    @Step("Click Register button to save changes")
    public void clickRegisterButton() {
        Locator button = registerButton();
        waitVisible(button, DEFAULT_WAIT);
        button.scrollIntoViewIfNeeded();
        clickWithRetry(button, 2, 200);
        logger.info("[Fan][PersonalInfo] Clicked Register button");
    }

    @Step("Verify success message is displayed")
    public void verifySuccessMessage() {
        waitVisible(successMessage(), DEFAULT_WAIT);
        logger.info("[Fan][PersonalInfo] Success message displayed: 'Updated Personal Information'");
    }

    // ================= Complete Flow Methods =================

    /**
     * Verify all fields are visible on Personal Information screen.
     */
    @Step("Verify all fields are visible")
    public void verifyAllFieldsVisible() {
        verifyIdentityFieldVisible();
        verifyLockedFields();
        verifyUserNameFieldVisible();
        verifyDateOfBirthFieldVisible();
        verifyAccountTypeFieldVisible();
        verifyFanAccountTypeSelected();
        logger.info("[Fan][PersonalInfo] All fields verified visible");
    }

    /**
     * Update email and phone number, then save.
     */
    @Step("Update personal information and save")
    public void updateAndSavePersonalInfo(String email, String phoneNumber) {
        updateEmail(email);
        updatePhoneNumber(phoneNumber);
        clickRegisterButton();
        page.waitForTimeout(1000); // Wait for save to complete
        verifySuccessMessage();
        logger.info("[Fan][PersonalInfo] Personal information updated and saved successfully");
    }

    /**
     * Complete flow: Navigate to Personal Info, verify fields, update and save.
     */
    @Step("Complete personal information verification and update flow")
    public void completePersonalInfoFlow(String email, String phoneNumber) {
        clickPersonalInfoMenu();
        assertOnPersonalInfoScreen();
        verifyAllFieldsVisible();
        updateAndSavePersonalInfo(email, phoneNumber);
        logger.info("[Fan][PersonalInfo] Complete personal information flow finished");
    }
}

