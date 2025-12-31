package pages.business.manager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Page Object for Twizz Business Manager Sign Up
 * URL: https://devbusiness.twizz.app/auth/sign-up
 */
public class BusinessManagerSignUpPage {
    private static final Logger logger = LoggerFactory.getLogger(BusinessManagerSignUpPage.class);
    private final Page page;
    
    // Test data paths
    private static final String MANAGER_PROFILE_IMAGE = "src/test/resources/Images/ManagerC.jpg";
    private static final String MANAGER_IDENTITY_DOC = "src/test/resources/Images/ManagerA.jpg";
    private static final String MANAGER_SELFIE_DOC = "src/test/resources/Images/ManagerB.jpg";

    public BusinessManagerSignUpPage(Page page) {
        this.page = page;
    }

    @Step("Navigate to Business Sign In page")
    public void navigateToSignIn() {
        String signInUrl = ConfigReader.getBusinessLoginUrl();
        page.navigate(signInUrl);
        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("[Business Manager] Navigated to Sign In page: {}", signInUrl);
    }

    @Step("Click Sign up link")
    public void clickSignUpLink() {
        Locator signUpLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sign up"));
        signUpLink.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business Manager] Clicked Sign up link");
    }

    @Step("Verify registration page heading")
    public boolean isRegistrationPageVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Inscription"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business Manager] Registration page heading 'Inscription' visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click Manager tab")
    public void clickManagerTab() {
        Locator managerButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Manager"));
        managerButton.click();
        page.waitForTimeout(500);
        logger.info("[Business Manager] Clicked Manager tab");
    }

    @Step("Fill last name: {lastName}")
    public void fillLastName(String lastName) {
        Locator lastNameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Last name"));
        lastNameField.click();
        lastNameField.fill(lastName);
        logger.info("[Business Manager] Filled last name: {}", lastName);
    }

    @Step("Fill first name: {firstName}")
    public void fillFirstName(String firstName) {
        Locator firstNameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("First name"));
        firstNameField.click();
        firstNameField.fill(firstName);
        logger.info("[Business Manager] Filled first name: {}", firstName);
    }

    @Step("Fill username: {username}")
    public void fillUsername(String username) {
        Locator usernameField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Username"));
        usernameField.click();
        usernameField.fill(username);
        logger.info("[Business Manager] Filled username: {}", username);
    }

    @Step("Fill email: {email}")
    public void fillEmail(String email) {
        Locator emailField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address"));
        emailField.click();
        emailField.fill(email);
        logger.info("[Business Manager] Filled email: {}", email);
    }

    @Step("Select birth date: {day}")
    public void selectBirthDate(String day) {
        Locator birthDateField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Birth date"));
        birthDateField.click();
        page.waitForTimeout(500);
        
        Locator dayOption = page.getByText(day, new Page.GetByTextOptions().setExact(true));
        dayOption.click();
        logger.info("[Business Manager] Selected birth date: {}", day);
    }

    @Step("Confirm birth date")
    public void confirmBirthDate() {
        Locator confirmButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm"));
        confirmButton.click();
        page.waitForTimeout(500);
        logger.info("[Business Manager] Confirmed birth date");
    }

    @Step("Click Continue button on page 1")
    public void clickContinuePageOne() {
        Locator continueButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue").setExact(true));
        continueButton.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business Manager] Clicked Continue button on page 1");
    }

    @Step("Fill password")
    public void fillPassword(String password) {
        Locator passwordField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password"));
        passwordField.click();
        passwordField.fill(password);
        logger.info("[Business Manager] Filled password: [HIDDEN]");
    }

    @Step("Fill agency name: {agencyName}")
    public void fillAgencyName(String agencyName) {
        Locator agencyField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Agency name"));
        agencyField.click();
        agencyField.fill(agencyName);
        logger.info("[Business Manager] Filled agency name: {}", agencyName);
    }

    @Step("Upload profile image")
    public void uploadProfileImage() {
        Path imagePath = Paths.get(MANAGER_PROFILE_IMAGE);
        
        // Try to find file input first
        Locator fileInput = page.locator("input[type='file']");
        if (fileInput.count() > 0 && fileInput.first().isVisible()) {
            fileInput.first().setInputFiles(imagePath);
            page.waitForTimeout(1000);
            logger.info("[Business Manager] Uploaded profile image via file input: {}", MANAGER_PROFILE_IMAGE);
            return;
        }
        
        // Use FileChooser if no visible file input
        Locator addButton = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Add"));
        com.microsoft.playwright.FileChooser fileChooser = page.waitForFileChooser(() -> addButton.click());
        fileChooser.setFiles(imagePath);
        page.waitForTimeout(1000);
        logger.info("[Business Manager] Uploaded profile image via FileChooser: {}", MANAGER_PROFILE_IMAGE);
    }

    @Step("Fill phone number: {phoneNumber}")
    public void fillPhoneNumber(String phoneNumber) {
        Locator phoneField = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Phone number"));
        phoneField.click();
        phoneField.fill(phoneNumber);
        logger.info("[Business Manager] Filled phone number: {}", phoneNumber);
    }

    @Step("Select gender")
    public void selectGender() {
        Locator genderOption = page.locator("span").nth(3);
        genderOption.click();
        page.waitForTimeout(500);
        logger.info("[Business Manager] Selected gender");
    }

    @Step("Click Continue button on page 2")
    public void clickContinuePageTwo() {
        Locator continueButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        continueButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(2000); // Wait for page transition
        logger.info("[Business Manager] Clicked Continue button on page 2");
    }

    @Step("Verify 'Your status' heading")
    public boolean isYourStatusPageVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Your status"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business Manager] 'Your status' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Select private individual status")
    public void selectPrivateIndividual() {
        Locator privateButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I'm a private individual"));
        privateButton.click();
        page.waitForTimeout(500);
        logger.info("[Business Manager] Selected 'I'm a private individual'");
    }

    @Step("Click Continue button on page 3")
    public void clickContinuePageThree() {
        Locator continueButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        continueButton.click();
        page.waitForLoadState(LoadState.LOAD);
        logger.info("[Business Manager] Clicked Continue button on page 3");
    }

    @Step("Verify 'Identity verification' heading")
    public boolean isIdentityVerificationPageVisible() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Identity verification"));
        boolean isVisible = heading.isVisible();
        logger.info("[Business Manager] 'Identity verification' heading visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Upload identity document")
    public void uploadIdentityDocument() {
        Path docPath = Paths.get(MANAGER_IDENTITY_DOC);
        
        // Try to find file input first
        Locator fileInput = page.locator("input[type='file']").first();
        if (fileInput.count() > 0) {
            fileInput.setInputFiles(docPath);
            page.waitForTimeout(1000);
            logger.info("[Business Manager] Uploaded identity document via file input: {}", MANAGER_IDENTITY_DOC);
            return;
        }
        
        // Use FileChooser if no file input
        Locator cameraIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("camera")).first();
        com.microsoft.playwright.FileChooser fileChooser = page.waitForFileChooser(() -> cameraIcon.click());
        fileChooser.setFiles(docPath);
        page.waitForTimeout(1000);
        logger.info("[Business Manager] Uploaded identity document via FileChooser: {}", MANAGER_IDENTITY_DOC);
    }

    @Step("Upload selfie document")
    public void uploadSelfieDocument() {
        Path selfiePath = Paths.get(MANAGER_SELFIE_DOC);
        
        // Try to find file input (second one)
        Locator fileInputs = page.locator("input[type='file']");
        if (fileInputs.count() > 1) {
            fileInputs.nth(1).setInputFiles(selfiePath);
            page.waitForTimeout(1000);
            logger.info("[Business Manager] Uploaded selfie document via file input: {}", MANAGER_SELFIE_DOC);
            return;
        }
        
        // Use FileChooser if no file input
        Locator cameraIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("camera")).nth(1);
        com.microsoft.playwright.FileChooser fileChooser = page.waitForFileChooser(() -> cameraIcon.click());
        fileChooser.setFiles(selfiePath);
        page.waitForTimeout(1000);
        logger.info("[Business Manager] Uploaded selfie document via FileChooser: {}", MANAGER_SELFIE_DOC);
    }

    @Step("Click Continue button on page 4")
    public void clickContinuePageFour() {
        Locator continueButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue"));
        continueButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(2000); // Wait for success page to load
        logger.info("[Business Manager] Clicked Continue button on page 4");
    }

    @Step("Verify success message")
    public boolean isSuccessMessageVisible() {
        Locator successMessage = page.getByText("Thank you for your interest!");
        // Wait for the message to appear
        try {
            successMessage.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Business Manager] Success message did not appear within timeout");
        }
        boolean isVisible = successMessage.isVisible();
        logger.info("[Business Manager] Success message 'Thank you for your interest!' visibility: {}", isVisible);
        return isVisible;
    }

    @Step("Click 'I understand' button")
    public void clickIUnderstand() {
        Locator understandButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I understand"));
        understandButton.click();
        page.waitForLoadState(LoadState.LOAD);
        page.waitForTimeout(2000); // Wait for navigation to sign-in page
        logger.info("[Business Manager] Clicked 'I understand' button");
    }

    @Step("Verify back on sign in page")
    public boolean isBackOnSignInPage() {
        Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Connection"));
        // Wait for the heading to appear
        try {
            heading.waitFor(new Locator.WaitForOptions().setTimeout(5000));
        } catch (Exception e) {
            logger.warn("[Business Manager] Connection heading did not appear within timeout");
        }
        boolean isVisible = heading.isVisible();
        logger.info("[Business Manager] 'Connection' heading visibility: {}", isVisible);
        return isVisible;
    }

    /**
     * Generate unique username with timestamp
     */
    public String generateUniqueUsername() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String username = "twizzmanager_" + timestamp;
        logger.info("[Business Manager] Generated unique username: {}", username);
        return username;
    }

    /**
     * Generate unique email based on username
     */
    public String generateUniqueEmail(String username) {
        String email = username + "@twizzautomation.com";
        logger.info("[Business Manager] Generated unique email: {}", email);
        return email;
    }
}
