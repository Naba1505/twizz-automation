package pages.admin;

import pages.common.BasePage;
import utils.ConfigReader;
import utils.TestDataManager;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminCreatorApprovalPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(AdminCreatorApprovalPage.class);

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
        waitVisible(userInput, ConfigReader.getMediumTimeout());
        clickWithRetry(userInput, 1, ConfigReader.getElementRetryDelay());
        userInput.fill("");
        userInput.fill(username);

        waitVisible(passInput, ConfigReader.getMediumTimeout());
        clickWithRetry(passInput, 1, ConfigReader.getElementRetryDelay());
        passInput.fill("");
        passInput.fill(password);

        Locator loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));
        if (loginButton.count() == 0) {
            loginButton = page.locator("button[type='submit']").first();
        }
        waitVisible(loginButton, ConfigReader.getMediumTimeout());
        clickWithRetry(loginButton, 2, ConfigReader.getElementRetryDelay());

        page.waitForURL("**/home", new Page.WaitForURLOptions().setTimeout(ConfigReader.getShortTimeout()));
        log.info("Successfully logged in and navigated to home page");
        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
    }

    public void loginWithConfig() {
        String adminUser = ConfigReader.getProperty("admin.username", "manager");
        String adminPass = ConfigReader.getProperty("admin.password", "M0lyF@n.2o24!");
        loginAs(adminUser, adminPass);
    }

    
    public void navigateToUsers() {
        log.info("Navigating to Users section");
        Locator usersLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Users"));
        waitVisible(usersLink, ConfigReader.getMediumTimeout());
        clickWithRetry(usersLink, 2, ConfigReader.getElementRetryDelay());

        Locator creatorsTab = page.getByRole(AriaRole.TAB, new Page.GetByRoleOptions().setName("Creators"));
        waitVisible(creatorsTab, ConfigReader.getMediumTimeout());
        log.info("Successfully navigated to Users > Creators tab");
    }

    public void openCreatorsAll() {
        log.info("Navigating to Creators > All creators");
        navigateToUsers();
    }

    public String resolveUsername(String provided) {
        String resolved = provided;
        if (resolved == null || resolved.isBlank()) {
            // Try TestDataManager first (handles both memory and file fallback)
            resolved = TestDataManager.getCreatorUsername();
            if (resolved == null || resolved.isBlank()) {
                resolved = ConfigReader.getProperty("approval.username", "");
            }
        }
        return resolved;
    }

    public void searchCreator(String username) {
        String toUse = resolveUsername(username);
        log.info("Searching creator by username: {}", toUse);
        if (toUse == null || toUse.isBlank()) {
            throw new RuntimeException("No creator username available. Run CreatorRegistrationTest first or set approval.username in config.properties.");
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
        clickWithRetry(searchField, 2, ConfigReader.getElementRetryDelay());
        searchField.fill("");
        searchField.fill(toUse);

        try {
            searchField.press("Enter");
            page.waitForTimeout(ConfigReader.getUiSettleTimeout());
        } catch (Exception e) {
            log.debug("Search trigger failed: {}", e.getMessage());
        }
        
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
        
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }

        Locator creatorRow = page.locator("tr").filter(new Locator.FilterOptions().setHasText(username)).first();
        Locator moreVertIcon = creatorRow.locator("[data-testid='MoreVertIcon']").first();

        clickWithRetry(moreVertIcon, 2, ConfigReader.getElementRetryDelay());

        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { log.debug("Wait failed: {}", e.getMessage()); }
        Locator editOption = page.getByText("Edit");
        waitVisible(editOption.first(), ConfigReader.getShortTimeout());
        clickWithRetry(editOption.first(), 1, ConfigReader.getElementRetryDelay());
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

        Locator statusDropdown = page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Registration Status Pending"));
        waitVisible(statusDropdown, ConfigReader.getShortTimeout());
        clickWithRetry(statusDropdown, 1, ConfigReader.getElementRetryDelay());

        Locator registeredOption = page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Registered"));
        waitVisible(registeredOption, ConfigReader.getShortTimeout());
        clickWithRetry(registeredOption, 1, ConfigReader.getElementRetryDelay());
    }

    public void submitAndAssertUpdated() {
        log.info("Submitting the update and verifying success toast");
        Locator submitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
        waitVisible(submitButton, ConfigReader.getMediumTimeout());
        clickWithRetry(submitButton, 2, ConfigReader.getElementRetryDelay());

        Locator successMessage = page.getByText("Creator updated successfully");
        waitVisible(successMessage, ConfigReader.getShortTimeout());
        log.info("Successfully updated creator");
    }
}

