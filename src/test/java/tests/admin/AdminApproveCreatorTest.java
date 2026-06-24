package tests.admin;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.microsoft.playwright.Tracing;

import pages.admin.AdminCreatorApprovalPage;
import pages.common.BaseTestClass;
import utils.BrowserFactory;
import utils.ConfigReader;
import utils.TestDataManager;

public class AdminApproveCreatorTest extends BaseTestClass {
    private static final Logger log = LoggerFactory.getLogger(AdminApproveCreatorTest.class);
    public static String approvedUsername;

    @BeforeMethod
    @Override
    public void setUp() {
        // Custom setup: same as BaseTestClass but WITHOUT navigating to Twizz landing page
        BrowserFactory.initialize();
        page = BrowserFactory.getPage();
        page.setDefaultNavigationTimeout(ConfigReader.getNavigationTimeout());
        page.setDefaultTimeout(ConfigReader.getVisibilityTimeout() * 3);

        try {
            boolean traceEnabled = Boolean.parseBoolean(ConfigReader.getProperty("trace.enable", "true"));
            if (traceEnabled) {
                page.context().tracing().start(new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true));
            }
        } catch (Exception e) {
            log.debug("Trace setup failed: {}", e.getMessage());
        }
        // No navigation here; admin test handles its own navigation to admin domain
    }

    @Test(description = "Approve newly registered creator from admin dashboard")
    public void testApproveCreatorAccount() {
        AdminCreatorApprovalPage admin = new AdminCreatorApprovalPage(page);

        // Navigate to login, login, then go to Users > Creators
        admin.navigateToLogin();
        admin.loginWithConfig();
        admin.openCreatorsAll();

        // Get username from TestDataManager (handles both memory and file fallback)
        String candidate = TestDataManager.getCreatorUsername();
        // If still null, let the page resolve via config to keep behavior consistent
        String resolvedUsername = admin.resolveUsername(candidate);
        if (resolvedUsername == null || resolvedUsername.isBlank()) {
            throw new RuntimeException("No creator username available. Run CreatorRegistrationTest first or pass -Dapproval.username=<username> or ensure target/created-username.txt exists.");
        }
        log.info("Using resolved creator username: {}", resolvedUsername);
        admin.searchCreator(resolvedUsername);
        admin.waitForCreatorInResults(resolvedUsername);

        // Edit and approve
        admin.openActionEditForCreator(resolvedUsername);
        admin.toggleVerificationAndStatus();
        admin.submitAndAssertUpdated();

        approvedUsername = resolvedUsername;
        try {
            java.nio.file.Path out = java.nio.file.Paths.get("target", "approved-username.txt");
            java.nio.file.Files.createDirectories(out.getParent());
            java.nio.file.Files.writeString(out, approvedUsername, java.nio.charset.StandardCharsets.UTF_8);
            log.info("Persisted approved username to {}", out);
        } catch (IOException e) {
            log.debug("Failed to persist approved username: {}", e.getMessage());
        }
        log.info("Creator '{}' approved successfully via admin dashboard", resolvedUsername);
    }
}
