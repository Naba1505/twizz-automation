package pages.admin;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.options.AriaRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

public class AdminCreatorApprovalPage extends BasePage {
    private static final Logger log = LoggerFactory.getLogger(AdminCreatorApprovalPage.class);

    // Locators
    private final Locator loaderOverlay;
    private final Locator spinDots;
    private final String adminBaseUrl;

    public AdminCreatorApprovalPage(Page page) {
        super(page);
        this.loaderOverlay = page.locator(".loader-overlay");
        this.spinDots = page.locator(".ant-spin-dot");
        String env = ConfigReader.getEnvironment();
        String envSpecific = ConfigReader.getProperty(env + ".admin.baseUrl", "");
        this.adminBaseUrl = (envSpecific != null && !envSpecific.isBlank())
                ? envSpecific
                : ConfigReader.getProperty("admin.baseUrl", "https://moly-admin-staging.vercel.app");
    }

    public void navigateToLogin() {
        log.info("Navigating to admin login page");
        String url = adminBaseUrl.endsWith("/") ? adminBaseUrl + "auth/login" : adminBaseUrl + "/auth/login";
        page.navigate(url);
    }

    public void loginAs(String username, String password) {
        log.info("Logging in as admin user: {}", username);
        Locator userInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email address or Username"));
        Locator passInput = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Password"));

        // Ensure fields are visible before interacting
        waitVisible(userInput, DEFAULT_WAIT);
        userInput.click();
        userInput.fill("");
        userInput.fill(username);

        waitVisible(passInput, DEFAULT_WAIT);
        passInput.click();
        passInput.fill("");
        passInput.fill(password);

        // Prefer role-based button by accessible name, with fallbacks
        Locator loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in"));
        if (loginButton.count() == 0) {
            loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login"));
        }
        if (loginButton.count() == 0) {
            loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign in"));
        }
        if (loginButton.count() == 0) {
            loginButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Sign In"));
        }

        if (loginButton.count() > 0) {
            try {
                waitVisible(loginButton.first(), DEFAULT_WAIT);
                clickWithRetry(loginButton.first(), 2, 500);
            } catch (RuntimeException e) {
                logger.warn("Primary click on login button failed: {}. Falling back to Enter key.", e.getMessage());
                passInput.press("Enter");
            }
        } else {
            logger.warn("Login button not found by role or text. Pressing Enter to submit.");
            passInput.press("Enter");
        }
    }

    public void loginWithConfig() {
        String adminUser = ConfigReader.getProperty("admin.username", "manager");
        String adminPass = ConfigReader.getProperty("admin.password", "M0lyF@n.2o24!");
        loginAs(adminUser, adminPass);
    }

    public void waitForHeavyLoadToSettle() {
        log.info("Waiting for loaders/spinners to settle (no cap)");
        int seconds = 0;
        while (true) {
            boolean overlayVisible = false;
            boolean dotsVisible = false;
            try { overlayVisible = (loaderOverlay.count() > 0) && safeIsVisible(loaderOverlay.first()); } catch (Throwable ignored) {}
            try { dotsVisible = (spinDots.count() > 0) && safeIsVisible(spinDots.first()); } catch (Throwable ignored) {}
            if (!overlayVisible && !dotsVisible) break;
            if (seconds % 30 == 0) { log.info("...still waiting: overlayVisible={} dotsVisible={}", overlayVisible, dotsVisible); }
            try { page.waitForTimeout(1000); } catch (Throwable ignored) {}
            seconds++;
        }
        waitForIdle();
    }

    public void waitForOverlayGoneExact() {
        log.info("Waiting explicitly for .loader-overlay to become hidden or detached (no cap)");
        int seconds = 0;
        while (true) {
            boolean gone = false;
            try {
                if (loaderOverlay.count() == 0) {
                    gone = true;
                } else {
                    gone = !safeIsVisible(loaderOverlay.first());
                }
            } catch (Throwable ignored) {
                gone = true;
            }
            if (gone) break;
            if (seconds % 30 == 0) { log.info("...overlay still visible, waiting"); }
            try { page.waitForTimeout(1000); } catch (Throwable ignored) {}
            seconds++;
        }
    }

    public void waitUntilDashboardStable() {
        log.info("Waiting until admin dashboard is stable (up to 180s): logo + Creators visible and no spinners");
        long deadline = System.currentTimeMillis() + 180_000;
        Locator logo = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("logo"));
        Locator creatorsMenu = page.locator("span").filter(new Locator.FilterOptions().setHasText("Creators")).first();
        // First explicit waits for overlay after login navigation (no cap)
        waitForOverlayGoneExact();
        while (System.currentTimeMillis() < deadline) {
            try {
                // Let transient loaders settle a bit each loop
                waitForHeavyLoadToSettle();
            } catch (Throwable ignored) {}
            boolean spinnersGone = (spinDots.count() == 0 || !safeIsVisible(spinDots.first()))
                    && (loaderOverlay.count() == 0 || !safeIsVisible(loaderOverlay.first()));
            boolean logoVisible = safeIsVisible(logo.first());
            boolean creatorsVisible = safeIsVisible(creatorsMenu);
            if (spinnersGone && logoVisible && creatorsVisible) {
                waitForIdle();
                return;
            }
            try { page.waitForTimeout(1000); } catch (Exception ignored) {}
        }
        // Final attempt to ensure elements
        try { waitVisible(logo.first(), ConfigReader.getVisibilityTimeout()); } catch (Throwable ignored) {}
        try { waitVisible(creatorsMenu, ConfigReader.getVisibilityTimeout()); } catch (Throwable ignored) {}
    }

    public void waitForCreatorsShellVisible() {
        log.info("Waiting for Creators menu and core container to be visible (no cap)");
        Locator creatorsMenu = page.locator("span").filter(new Locator.FilterOptions().setHasText("Creators")).first();
        Locator container = page.locator("div").nth(2);
        int seconds = 0;
        while (!safeIsVisible(creatorsMenu)) {
            if (seconds % 30 == 0) { log.info("...waiting for Creators menu"); }
            try { page.waitForTimeout(1000); } catch (Throwable ignored) {}
            seconds++;
        }
        seconds = 0;
        while (!safeIsVisible(container.first())) {
            if (seconds % 30 == 0) { log.info("...waiting for core container div:nth(2)"); }
            try { page.waitForTimeout(1000); } catch (Throwable ignored) {}
            seconds++;
        }
        waitForHeavyLoadToSettle();
    }

    public void waitForDashboardReady() {
        log.info("Waiting for admin dashboard to be ready (Creators menu visible)");
        Locator creatorsMenu = page.locator("span").filter(new Locator.FilterOptions().setHasText("Creators")).first();
        waitVisible(creatorsMenu, ConfigReader.getVisibilityTimeout());
        waitForIdle();
    }

    public void waitForLogo() {
        log.info("Waiting for admin logo to be visible");
        Locator logo = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("logo"));
        waitVisible(logo.first(), ConfigReader.getVisibilityTimeout());
        waitForHeavyLoadToSettle();
    }

    public void openCreatorsAll() {
        log.info("Navigating to Creators > All creators");
        Locator creatorsMenu = page.locator("span").filter(new Locator.FilterOptions().setHasText("Creators")).first();
        waitVisible(creatorsMenu, ConfigReader.getVisibilityTimeout());
        clickWithRetry(creatorsMenu, 2, 400);
        Locator allCreators = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("All creators"));
        waitVisible(allCreators.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(allCreators.first(), 2, 300);
        waitForHeavyLoadToSettle();
        waitForIdle();
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
        waitForHeavyLoadToSettle();
        // Resolve the search textbox using the provided XPath first, then fallbacks
        Locator search = page.locator("//input[@placeholder='Enter keyword']").first();
        if (search.count() == 0) {
            search = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Enter keyword"));
        }
        if (search.count() == 0) {
            search = page.getByPlaceholder("Enter keyword");
        }
        if (search.count() == 0) {
            search = page.locator("input[placeholder*='Enter keyword' i]").first();
        }
        if (search.count() == 0) {
            // Fallback: any textbox inside main content area
            search = page.getByRole(AriaRole.TEXTBOX).first();
        }
        // If still not found, attempt within any visible iframes
        if (search.count() == 0) {
            for (Frame f : page.frames()) {
                try {
                    Locator iframeSearch = f.locator("//input[@placeholder='Enter keyword']").first();
                    if (iframeSearch.count() > 0) {
                        search = iframeSearch;
                        break;
                    }
                } catch (Throwable ignored) {}
            }
        }
        if (search.count() == 0) {
            throw new RuntimeException("Search textbox with placeholder 'Enter keyword' not found (page or iframes). Check UI changes or adjust locator.");
        }
        waitVisible(search, ConfigReader.getVisibilityTimeout());
        try { search.scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        // Wait until enabled
        int guard = 0;
        while (guard < 60) {
            try { if (search.isEnabled()) break; } catch (Throwable ignored) {}
            try { page.waitForTimeout(500); } catch (Throwable ignored) {}
            guard++;
        }
        // Robust clear + fill with retry
        RuntimeException last = null;
        for (int i = 0; i < 2; i++) {
            try {
                try {
                    clickWithRetry(search, 1, 100);
                } catch (Throwable clickErr) {
                    // Force focus if click is intercepted
                    try { search.evaluate("el => el.focus()"); } catch (Throwable ignored) {}
                }
                try { search.press("Control+A"); } catch (Throwable ignored) {}
                try { search.press("Delete"); } catch (Throwable ignored) {}
                search.fill("");
                try {
                    search.fill(toUse);
                } catch (Throwable fillErr) {
                    try { search.evaluate("el => el.focus()"); } catch (Throwable ignored) {}
                    try { page.keyboard().insertText(toUse); } catch (Throwable ignored) {}
                }
                String current = "";
                try { current = search.inputValue(); } catch (Throwable ignored) {}
                if (!toUse.equals(current)) {
                    log.warn("Search value mismatch. Expected='{}' Actual='{}'. Retrying if attempts left.", toUse, current);
                    continue;
                }
                last = null;
                break;
            } catch (RuntimeException e) {
                last = e;
                try { page.waitForTimeout(500); } catch (Throwable ignored) {}
            }
        }
        if (last != null) throw last;
        // Trigger search (if uses Enter or debounce)
        try { search.press("Enter"); } catch (Exception ignored) {}
        waitForHeavyLoadToSettle();
        page.waitForTimeout(800);
    }

    // Backward-compatible: global Action > Edit (not row-scoped)
    public void openActionEdit() {
        log.info("Opening Action menu (global) and clicking Edit/Update");
        Locator action = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Action down"));
        waitVisible(action.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(action.first(), 2, 300);
        Locator edit = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("edit Update"));
        waitVisible(edit.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(edit.first(), 2, 300);
    }

    public void waitForCreatorInResults(String username) {
        log.info("Waiting for creator row to be visible for: {}", username);
        // Ensure any spinners are gone before asserting rows
        waitForHeavyLoadToSettle();
        // Try table row first
        Locator row = page.locator("tr").filter(new Locator.FilterOptions().setHasText(username)).first();
        try {
            waitVisible(row, ConfigReader.getVisibilityTimeout());
        } catch (RuntimeException e) {
            // Fallback to role-based row
            row = page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName(username)).first();
            waitVisible(row, ConfigReader.getVisibilityTimeout());
        }
        waitForIdle();
    }

    public void openActionEditForCreator(String username) {
        log.info("Opening Action > Edit for creator: {}", username);
        Locator row = page.locator("tr").filter(new Locator.FilterOptions().setHasText(username)).first();
        if (row.count() == 0 || !safeIsVisible(row)) {
            row = page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName(username)).first();
        }
        waitVisible(row, ConfigReader.getVisibilityTimeout());
        Locator actionBtn = row.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Action down"));
        if (actionBtn.count() == 0) {
            actionBtn = row.locator("button").filter(new Locator.FilterOptions().setHasText("Action"));
        }
        waitVisible(actionBtn.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(actionBtn.first(), 2, 400);

        Locator editLink = row.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("edit Update"));
        if (editLink.count() == 0) {
            editLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("edit Update"));
        }
        waitVisible(editLink.first(), ConfigReader.getVisibilityTimeout());
        clickWithRetry(editLink.first(), 2, 400);
    }

    public void toggleVerificationAndStatus() {
        log.info("Toggling Verified Email and Verified Account, and setting status Registered");
        Locator emailSwitch = page.getByRole(AriaRole.SWITCH, new Page.GetByRoleOptions().setName("Verified Email?"));
        Locator accountSwitch = page.getByRole(AriaRole.SWITCH, new Page.GetByRoleOptions().setName("Verified Account?"));
        waitVisible(emailSwitch.first(), ConfigReader.getVisibilityTimeout());
        try { emailSwitch.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(emailSwitch.first(), 2, 200);
        waitVisible(accountSwitch.first(), ConfigReader.getVisibilityTimeout());
        try { accountSwitch.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
        clickWithRetry(accountSwitch.first(), 2, 200);
        // Open the status dropdown by clicking the current value (e.g., "Pending")
        Locator statusCurrent = page.locator("//div[contains(@class,'ant-select')]//div[contains(@class,'ant-select-selector')]//*[text()='Pending']").first();
        if (statusCurrent.count() == 0) {
            // Fallback to any visible element with text Pending
            statusCurrent = page.getByText("Pending").first();
        }
        waitVisible(statusCurrent, ConfigReader.getVisibilityTimeout());
        clickWithRetry(statusCurrent, 2, 200);

        // Wait for dropdown panel and click the Registered option within the open dropdown
        Locator dropdown = page.locator("//div[contains(@class,'ant-select-dropdown') and contains(@class,'ant-select-dropdown-placement-bottom')]");
        try { waitVisible(dropdown.first(), ConfigReader.getVisibilityTimeout()); } catch (Throwable ignored) {}
        Locator registeredOption = page.locator("//div[contains(@class,'ant-select-dropdown')]//div[contains(@class,'ant-select-item')][.//div[normalize-space(text())='Registered' or contains(normalize-space(.), 'Registered')]]").first();
        if (registeredOption.count() == 0) {
            // Fallback by role if available
            registeredOption = page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Registered")).first();
        }
        waitVisible(registeredOption, ConfigReader.getVisibilityTimeout());
        clickWithRetry(registeredOption, 2, 200);
        waitForHeavyLoadToSettle();
    }

    public void submitAndAssertUpdated() {
        log.info("Submitting the update and verifying success toast");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit")).click();
        // Prefer any toast/popup containing the success message without relying on fixed index
        Locator successAny = page.locator("//*[contains(normalize-space(text()), 'Updated successfully')] ");
        try {
            waitVisible(successAny.first(), ConfigReader.getDefaultTimeout());
        } catch (RuntimeException e) {
            Locator successFallback = page.getByText("Updated successfully");
            waitVisible(successFallback.first(), ConfigReader.getDefaultTimeout());
        }
    }
}

