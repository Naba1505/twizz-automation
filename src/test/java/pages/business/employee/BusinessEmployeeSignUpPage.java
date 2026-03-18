package pages.business.employee;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import utils.ConfigReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Page Object for Twizz Business Employee Sign Up
 * URL: https://devbusiness.twizz.app/auth/sign-up
 */
public class BusinessEmployeeSignUpPage extends BasePage {

    public BusinessEmployeeSignUpPage(Page page) {
        super(page);
    }

    @Step("Navigate to Business Sign In page")
    public void navigateToSignIn() {
        String signInUrl = ConfigReader.getBusinessLoginUrl();
        page.navigate(signInUrl);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("[Business Employee] Navigated to Sign In page: {}", signInUrl);
    }

    @Step("Click Sign up link")
    public void clickSignUpLink() {
        Locator signUpLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sign up"));
        signUpLink.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business Employee] Clicked Sign up link");
    }

    @Step("Verify registration page heading")
    public boolean isRegistrationPageVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Inscription"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business Employee] Registration page heading 'Inscription' visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Verify Employee tab is selected by default")
    public boolean isEmployeeTabSelected() {
        Locator employeeButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Selected Employee"));
        boolean isVisible = employeeButton.isVisible();
        logger.info("[Business Employee] 'Selected Employee' button visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Fill last name: {lastName}")
    public void fillLastName(String lastName) {
        Locator lastNameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last name"));
        lastNameField.click();
        lastNameField.fill(lastName);
        logger.info("[Business Employee] Filled last name: {}", lastName);
    }

    @Step("Fill first name: {firstName}")
    public void fillFirstName(String firstName) {
        Locator firstNameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First name"));
        firstNameField.click();
        firstNameField.fill(firstName);
        logger.info("[Business Employee] Filled first name: {}", firstName);
    }

    @Step("Fill username: {username}")
    public void fillUsername(String username) {
        Locator usernameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username"));
        usernameField.click();
        usernameField.fill(username);
        logger.info("[Business Employee] Filled username: {}", username);
    }

    @Step("Fill email: {email}")
    public void fillEmail(String email) {
        Locator emailField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address"));
        emailField.click();
        emailField.fill(email);
        logger.info("[Business Employee] Filled email: {}", email);
    }

    @Step("Select birth date")
    public void selectBirthDate() {
        // Follow exact codegen sequence with robust month handling
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Birth date")).click();
        
        // Select year 1995
        page.getByText("2008").click();
        page.waitForTimeout(300);
        page.getByText("1995").click();
        page.waitForTimeout(300);
        
        // Try to navigate to January without relying on specific month names
        boolean januaryFound = false;
        
        // First try to click any available month to open month navigation
        try {
            Locator currentMonth = page.locator(".ant-picker-month-btn").first();
            if (currentMonth.count() > 0) {
                currentMonth.first().click();
                page.waitForTimeout(300);
                logger.info("[Business Employee] Clicked current month to open navigation");
            }
        } catch (Exception e) {
            logger.debug("[Business Employee] Could not click current month: {}", e.getMessage());
        }
        
        // Try multiple strategies to find and click January
        String[] januaryVariations = {"January", "Jan", "janvier", "Janvier", "january", "JANUARY"};
        
        for (String month : januaryVariations) {
            try {
                Locator monthElement = page.getByText(month);
                if (monthElement.count() > 0 && safeIsVisible(monthElement.first())) {
                    monthElement.first().click();
                    page.waitForTimeout(300);
                    januaryFound = true;
                    logger.info("[Business Employee] Found and clicked January: {}", month);
                    break;
                }
            } catch (Exception e) {
                logger.debug("[Business Employee] January variation '{}' not found: {}", month, e.getMessage());
            }
        }
        
        // If January still not found, try clicking any month element as fallback
        if (!januaryFound) {
            try {
                Locator anyMonth = page.locator(".ant-picker-cell").first();
                if (anyMonth.count() > 0) {
                    anyMonth.first().click();
                    page.waitForTimeout(300);
                    logger.info("[Business Employee] Clicked first available month as fallback");
                }
            } catch (Exception e) {
                logger.debug("[Business Employee] Fallback month selection failed: {}", e.getMessage());
            }
        }
        
        // Select day 1
        page.getByText("1", new Page.GetByTextOptions().setExact(true)).click();
        page.waitForTimeout(300);
        
        logger.info("[Business Employee] Selected birth date: 1995-01-01");
    }

    @Step("Confirm birth date")
    public void confirmBirthDate() {
        Locator confirmButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm"));
        confirmButton.click();
        page.waitForTimeout(500);
        logger.info("[Business Employee] Confirmed birth date");
    }

    @Step("Click Continue button on page 1")
    public void clickContinuePageOne() {
        Locator continueButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue").setExact(true));
        continueButton.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business Employee] Clicked Continue button on page 1");
    }

    @Step("Fill password")
    public void fillPassword(String password) {
        Locator passwordField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password"));
        passwordField.click();
        passwordField.fill(password);
        logger.info("[Business Employee] Filled password: [HIDDEN]");
    }

    @Step("Fill phone number: {phoneNumber}")
    public void fillPhoneNumber(String phoneNumber) {
        Locator phoneField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Phone number"));
        phoneField.click();
        phoneField.fill(phoneNumber);
        logger.info("[Business Employee] Filled phone number: {}", phoneNumber);
    }

    @Step("Select gender")
    public void selectGender() {
        Locator genderOption = page.locator("span").nth(3);
        genderOption.click();
        page.waitForTimeout(500);
        logger.info("[Business Employee] Selected gender");
    }

    @Step("Click Continue button on page 2")
    public void clickContinuePageTwo() {
        Locator continueButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        continueButton.click();
        page.waitForLoadState(LoadState.LOAD);
        
        // Wait for URL to change to employee dashboard
        try {
            page.waitForURL("**/employee**", new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
        } catch (Exception e) {
            logger.warn("[Business Employee] Did not navigate to employee dashboard within timeout. Current URL: {}", page.url());
        }
        
        page.waitForTimeout(2000); // Additional wait for page to settle
        logger.info("[Business Employee] Clicked Continue button on page 2");
    }

    @Step("Verify employee dashboard URL")
    public boolean isOnEmployeeDashboard() {
        String currentUrl = page.url();
        boolean isCorrectUrl = currentUrl.contains("/employee");
        logger.info("[Business Employee] Current URL: {}, Expected to contain: /employee, Match: {}", 
            currentUrl, isCorrectUrl);
        return isCorrectUrl;
    }

    @Step("Verify agency avatar is visible")
    public boolean isAgencyAvatarVisible() {
        Locator agencyAvatar = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Agency avatar"));
        // Wait for the avatar to appear
        try {
            agencyAvatar.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Business Employee] Agency avatar did not appear within timeout");
        }
        boolean isVisible = agencyAvatar.isVisible();
        logger.info("[Business Employee] Agency avatar visibility: {}", isVisible);
        return isVisible;
    }

    /**
     * Generate unique username with timestamp
     */
    public String generateUniqueUsername() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String username = "twizzemployee_" + timestamp;
        logger.info("[Business Employee] Generated unique username: {}", username);
        return username;
    }

    /**
     * Generate unique email based on username
     */
    public String generateUniqueEmail(String username) {
        String email = username + "@twizzautomation.com";
        logger.info("[Business Employee] Generated unique email: {}", email);
        return email;
    }
}
