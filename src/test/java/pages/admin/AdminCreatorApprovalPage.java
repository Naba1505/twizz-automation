package pages.admin;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

public class AdminCreatorApprovalPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(AdminCreatorApprovalPage.class);

    public AdminCreatorApprovalPage(Page page) {
        super(page);
    }

    public void navigateToLogin() {
        log.info("Navigating to admin login page");
        String url = "https://twizz-admin-staging.vercel.app/login";
        navigateAndWait(url);
    }

    public void loginAs(String username, String password) {
        log.info("Logging in as admin user: {}", username);
        
        // Try different locators for username field
        Locator userInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email or Username"));
        if (userInput.count() == 0) {
            userInput = page.getByPlaceholder("Email or Username");
        }
        if (userInput.count() == 0) {
            userInput = page.locator("input[type='email']").first();
        }
        if (userInput.count() == 0) {
            userInput = page.locator("input[placeholder*='Email']").first();
        }
        if (userInput.count() == 0) {
            userInput = page.locator("input[placeholder*='email']").first();
        }
        if (userInput.count() == 0) {
            userInput = page.locator(".ant-input").first();
        }
        
        // Try different locators for password field
        Locator passInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password"));
        if (passInput.count() == 0) {
            passInput = page.locator("input[type='password']").first();
        }

        // Ensure fields are visible before interacting
        waitVisible(userInput, ConfigReader.getVisibilityTimeout());
        userInput.click();
        userInput.fill("");
        userInput.fill(username);

        waitVisible(passInput, ConfigReader.getVisibilityTimeout());
        passInput.click();
        passInput.fill("");
        passInput.fill(password);

        // Click Login button
        Locator loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));
        if (loginButton.count() == 0) {
            loginButton = page.locator("button[type='submit']").first();
        }
        waitVisible(loginButton, ConfigReader.getVisibilityTimeout());
        clickWithRetry(loginButton, 2, 500);
        
        // Wait for successful login - check URL
        page.waitForURL("**/home", new Page.WaitForURLOptions().setTimeout(30000));
        log.info("Successfully logged in and navigated to home page");
        
        // Wait a bit for home page to fully load
        page.waitForTimeout(1000);
    }

    public void loginWithConfig() {
        String adminUser = ConfigReader.getProperty("admin.username", "manager");
        String adminPass = ConfigReader.getProperty("admin.password", "M0lyF@n.2o24!");
        loginAs(adminUser, adminPass);
    }

    
    public void navigateToUsers() {
        log.info("Navigating to Users section");
        Locator usersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Users"));
        waitVisible(usersLink, ConfigReader.getVisibilityTimeout());
        clickWithRetry(usersLink, 2, 400);
        
        // Wait for Creators tab to be visible
        Locator creatorsTab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("Creators"));
        waitVisible(creatorsTab, ConfigReader.getVisibilityTimeout());
        log.info("Successfully navigated to Users > Creators tab");
    }

    public void openCreatorsAll() {
        log.info("Navigating to Creators > All creators");
        navigateToUsers();
    }

    public String resolveUsername(String provided) {
        String resolved = provided;
        if (resolved == null || resolved.isBlank()) {
            try {
                resolved = tests.creator.CreatorRegistrationTest.createdUsername;
            } catch (Throwable ignored) {
                resolved = null;
            }
            if (resolved == null || resolved.isBlank()) {
                resolved = ConfigReader.getProperty("approval.username", "");
            }
            if (resolved == null || resolved.isBlank()) {
                try {
                    java.nio.file.Path p = java.nio.file.Paths.get("target", "created-username.txt");
                    if (java.nio.file.Files.exists(p)) {
                        String fileVal = java.nio.file.Files.readString(p, java.nio.charset.StandardCharsets.UTF_8).trim();
                        if (!fileVal.isBlank()) {
                            resolved = fileVal;
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }
        return resolved;
    }

    public void searchCreator(String username) {
        String toUse = resolveUsername(username);
        log.info("Searching creator by username: {}", toUse);
        if (toUse == null || toUse.isBlank()) {
            throw new RuntimeException("No creator username available. Ensure CreatorRegistrationTest.createdUsername is set or set approval.username in config.properties.");
        }
        
        // Use new search field
        Locator search = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search"));
        waitVisible(search, ConfigReader.getVisibilityTimeout());
        
        // Clear and fill search
        clickWithRetry(search, 1, 100);
        search.fill("");
        search.fill(toUse);
        
        // Trigger search and wait
        try { search.press("Enter"); } catch (Exception ignored) {}
        page.waitForTimeout(1000);
    }

    
    public void waitForCreatorInResults(String username) {
        log.info("Waiting for creator row to be visible for: {}", username);
        Locator row = page.locator("tr").filter(new Locator.FilterOptions().setHasText(username)).first();
        try {
            waitVisible(row, ConfigReader.getVisibilityTimeout());
        } catch (RuntimeException e) {
            row = page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName(username)).first();
            waitVisible(row, ConfigReader.getVisibilityTimeout());
        }
    }

    public void openActionEditForCreator(String username) {
        log.info("Opening Action > Edit for creator: {}", username);
        waitForCreatorInResults(username);
        
        // Wait a bit before clicking the 3-dot icon
        page.waitForTimeout(500);
        
        // Click the 3-dot icon using the specific CSS selector
        page.locator("[data-testid='MoreVertIcon']").click();
        
        // Wait for dropdown and click Edit
        page.waitForTimeout(500);
        page.getByText("Edit").click();
    }

    public void toggleVerificationAndStatus() {
        log.info("Toggling Verified Email and Verified Account, and setting status Registered");
        
        // Check the checkboxes
        Locator emailCheckbox = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Verified Email"));
        Locator accountCheckbox = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Verified Account"));
        
        waitVisible(emailCheckbox, ConfigReader.getShortTimeout());
        emailCheckbox.check();
        log.info("Checked Verified Email");
        
        waitVisible(accountCheckbox, ConfigReader.getShortTimeout());
        accountCheckbox.check();
        log.info("Checked Verified Account");
        
        // Change registration status from Pending to Registered
        Locator statusDropdown = page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Registration Status Pending"));
        waitVisible(statusDropdown, ConfigReader.getShortTimeout());
        clickWithRetry(statusDropdown, 1, 100);
        
        // Select Registered option
        Locator registeredOption = page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Registered"));
        waitVisible(registeredOption, ConfigReader.getShortTimeout());
        clickWithRetry(registeredOption, 1, 100);
    }

    public void submitAndAssertUpdated() {
        log.info("Submitting the update and verifying success toast");
        Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
        waitVisible(submitButton, ConfigReader.getVisibilityTimeout());
        clickWithRetry(submitButton, 2, 200);
        
        // Wait for success message to be visually displayed
        Locator successMessage = page.getByText("Creator updated successfully");
        waitVisible(successMessage, ConfigReader.getDefaultTimeout());
        log.info("Successfully updated creator");
    }
}

