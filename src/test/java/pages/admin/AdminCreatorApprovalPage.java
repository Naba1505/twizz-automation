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
    
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    private static final int BUTTON_RETRY_DELAY = 200;    // Button click retry delay
    private static final int BUTTON_RETRY_COUNT = 2;      // Number of retries for buttons
    private static final int SEARCH_RETRY_COUNT = 1;      // Search field retry count
    private static final int SHORT_TIMEOUT = 1000;        // Short waits (was ConfigReader.getShortTimeout)
    private static final int MEDIUM_TIMEOUT = 3000;       // Medium waits - for visibility
    private static final int LONG_TIMEOUT = 5000;         // Long waits - for success messages
    private static final int NAVIGATION_WAIT = 500;       // Navigation stabilization
    private static final int DROPDOWN_WAIT = 300;         // Dropdown menu wait

    public AdminCreatorApprovalPage(Page page) {
        super(page);
    }

    public void navigateToLogin() {
        log.info("Navigating to admin login page");
        String url = ConfigReader.getProperty("admin.url", "https://twizz-admin-staging.vercel.app/login");
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
        waitVisible(userInput, MEDIUM_TIMEOUT);
        userInput.click();
        userInput.fill("");
        userInput.fill(username);

        waitVisible(passInput, MEDIUM_TIMEOUT);
        passInput.click();
        passInput.fill("");
        passInput.fill(password);

        // Click Login button
        Locator loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));
        if (loginButton.count() == 0) {
            loginButton = page.locator("button[type='submit']").first();
        }
        waitVisible(loginButton, MEDIUM_TIMEOUT);
        clickWithRetry(loginButton, BUTTON_RETRY_COUNT, BUTTON_RETRY_DELAY);
        
        // Wait for successful login - check URL
        page.waitForURL("**/home", new Page.WaitForURLOptions().setTimeout(LONG_TIMEOUT));
        log.info("Successfully logged in and navigated to home page");
        
        // Wait for home page to fully load
        page.waitForTimeout(NAVIGATION_WAIT);
    }

    public void loginWithConfig() {
        String adminUser = ConfigReader.getProperty("admin.username", "manager");
        String adminPass = ConfigReader.getProperty("admin.password", "M0lyF@n.2o24!");
        loginAs(adminUser, adminPass);
    }

    
    public void navigateToUsers() {
        log.info("Navigating to Users section");
        Locator usersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Users"));
        waitVisible(usersLink, MEDIUM_TIMEOUT);
        clickWithRetry(usersLink, BUTTON_RETRY_COUNT, BUTTON_RETRY_DELAY);
        
        // Wait for Creators tab to be visible
        Locator creatorsTab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("Creators"));
        waitVisible(creatorsTab, MEDIUM_TIMEOUT);
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
        
        // Try multiple locator strategies for search field
        Locator[] searchLocators = {
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Search")),
            page.getByPlaceholder("Search"),
            page.getByPlaceholder("search"),
            page.locator("input[type='text']:visible"),
            page.locator("input[placeholder*='Search']"),
            page.locator("input[placeholder*='search']"),
            page.locator(".ant-input"),
            page.locator("input"),
            page.locator("*:has-text('Search')"),
            page.locator("[data-testid*='search']"),
            page.locator("[class*='search'] input"),
            page.locator("div:has(input)"),
            page.getByRole(AriaRole.TEXTBOX)
        };
        
        Locator searchField = null;
        for (Locator locator : searchLocators) {
            try {
                if (locator.count() > 0) {
                    log.info("Found search field with count: {} using locator: {}", locator.count(), locator);
                    locator.first().scrollIntoViewIfNeeded();
                    waitVisible(locator.first(), ConfigReader.getMediumTimeout());
                    searchField = locator.first();
                    break;
                }
            } catch (Exception e) {
                log.debug("Search locator strategy failed: {}", e.getMessage());
                continue;
            }
        }
        
        if (searchField == null) {
            throw new RuntimeException("Unable to locate search field in admin interface");
        }
        
        // Clear and fill search
        clickWithRetry(searchField, 2, BUTTON_RETRY_DELAY);
        searchField.fill("");
        searchField.fill(toUse);
        
        // Trigger search and wait
        try { 
            searchField.press("Enter"); 
            page.waitForTimeout(1000); // Wait for search to process
        } catch (Exception ignored) {}
        
        log.info("Search completed for username: {}", toUse);
    }

    
    public void waitForCreatorInResults(String username) {
        log.info("Waiting for creator row to be visible for: {}", username);
        
        // Try multiple locator strategies with longer timeout
        Locator[] rowLocators = {
            page.locator("tr").filter(new Locator.FilterOptions().setHasText(username)).first(),
            page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName(username)).first(),
            page.locator("tr:has-text('" + username + "')").first(),
            page.locator("*:has-text('" + username + "')").first(),
            page.locator("td:has-text('" + username + "')").first()
        };
        
        for (Locator row : rowLocators) {
            try {
                log.info("Trying locator strategy for creator row: {}", username);
                // Use longer timeout for search results
                waitVisible(row, ConfigReader.getMediumTimeout());
                log.info("Found creator row using locator strategy");
                return;
            } catch (Exception e) {
                log.debug("Row locator strategy failed: {}", e.getMessage());
                continue;
            }
        }
        
        throw new RuntimeException("Unable to find creator row for username: " + username);
    }

    public void openActionEditForCreator(String username) {
        log.info("Opening Action > Edit for creator: {}", username);
        waitForCreatorInResults(username);
        
        // Wait before clicking the 3-dot icon
        page.waitForTimeout(DROPDOWN_WAIT);
        
        // Find the specific creator row and click its MoreVertIcon
        Locator creatorRow = page.locator("tr").filter(new Locator.FilterOptions().setHasText(username)).first();
        Locator moreVertIcon = creatorRow.locator("[data-testid='MoreVertIcon']").first();
        
        // Click the 3-dot icon within the specific creator row
        moreVertIcon.click();
        
        // Wait for dropdown and click Edit
        page.waitForTimeout(DROPDOWN_WAIT);
        page.getByText("Edit").click();
    }

    public void toggleVerificationAndStatus() {
        log.info("Toggling Verified Email and Verified Account, and setting status Registered");
        
        // Check the checkboxes
        Locator emailCheckbox = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Verified Email"));
        Locator accountCheckbox = page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Verified Account"));
        
        waitVisible(emailCheckbox, SHORT_TIMEOUT);
        emailCheckbox.check();
        log.info("Checked Verified Email");
        
        waitVisible(accountCheckbox, SHORT_TIMEOUT);
        accountCheckbox.check();
        log.info("Checked Verified Account");
        
        // Change registration status from Pending to Registered
        Locator statusDropdown = page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Registration Status Pending"));
        waitVisible(statusDropdown, SHORT_TIMEOUT);
        clickWithRetry(statusDropdown, SEARCH_RETRY_COUNT, BUTTON_RETRY_DELAY);
        
        // Select Registered option
        Locator registeredOption = page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Registered"));
        waitVisible(registeredOption, SHORT_TIMEOUT);
        clickWithRetry(registeredOption, SEARCH_RETRY_COUNT, BUTTON_RETRY_DELAY);
    }

    public void submitAndAssertUpdated() {
        log.info("Submitting the update and verifying success toast");
        Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
        waitVisible(submitButton, MEDIUM_TIMEOUT);
        clickWithRetry(submitButton, BUTTON_RETRY_COUNT, BUTTON_RETRY_DELAY);
        
        // Wait for success message to be visually displayed
        Locator successMessage = page.getByText("Creator updated successfully");
        waitVisible(successMessage, LONG_TIMEOUT);
        log.info("Successfully updated creator");
    }
}

