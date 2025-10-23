package tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.AdminCreatorApprovalPage;
import pages.BaseTestClass;
import org.testng.annotations.BeforeMethod;
import com.microsoft.playwright.Tracing;
import utils.BrowserFactory;
import utils.ConfigReader;

public class AdminApproveCreatorTest extends BaseTestClass {
    private static final Logger log = LoggerFactory.getLogger(AdminApproveCreatorTest.class);

    @BeforeMethod
    @Override
    public void setUp() {
        // Custom setup: same as BaseTestClass but WITHOUT navigating to Twizz landing page
        BrowserFactory.initialize();
        page = BrowserFactory.getPage();
        page.setDefaultNavigationTimeout(180000);
        page.setDefaultTimeout(180000);

        try {
            boolean traceEnabled = Boolean.parseBoolean(ConfigReader.getProperty("trace.enable", "true"));
            if (traceEnabled) {
                page.context().tracing().start(new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true));
            }
        } catch (Exception ignored) {
        }
        // No navigation here; admin test handles its own navigation to admin domain
    }

    @Test(description = "Approve newly registered creator from admin dashboard")
    public void testApproveCreatorAccount() {
        AdminCreatorApprovalPage admin = new AdminCreatorApprovalPage(page);

        // Always navigate and login first so the test doesn't skip before hitting the URL
        admin.navigateToLogin();
        admin.loginWithConfig();
        admin.waitUntilDashboardStable();
        admin.waitForCreatorsShellVisible();

        // Navigate to Creators even if username is not configured, per user's request to proceed after login
        admin.openCreatorsAll();
        admin.waitForHeavyLoadToSettle();

        // Prefer the in-memory username from the registration test; else try file fallback
        String candidate = CreatorRegistrationTest.createdUsername;
        if (candidate == null || candidate.isBlank()) {
            try {
                java.nio.file.Path p = java.nio.file.Paths.get("target", "created-username.txt");
                if (java.nio.file.Files.exists(p)) {
                    String fileVal = java.nio.file.Files.readString(p, java.nio.charset.StandardCharsets.UTF_8).trim();
                    if (!fileVal.isBlank()) {
                        candidate = fileVal;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        // If still null, let the page resolve via config to keep behavior consistent
        String resolvedUsername = admin.resolveUsername(candidate);
        if (resolvedUsername == null || resolvedUsername.isBlank()) {
            throw new RuntimeException("No creator username available. Run CreatorRegistrationTest first or pass -Dapproval.username=<username> or ensure target/created-username.txt exists.");
        }
        log.info("Using resolved creator username: {}", resolvedUsername);
        admin.searchCreator(resolvedUsername);
        admin.waitForCreatorInResults(resolvedUsername);

        // Edit and approve (row-scoped for correctness)
        admin.openActionEditForCreator(resolvedUsername);
        admin.waitForHeavyLoadToSettle();
        admin.toggleVerificationAndStatus();
        admin.submitAndAssertUpdated();

        log.info("Creator '{}' approved successfully via admin dashboard", resolvedUsername);
    }
}
