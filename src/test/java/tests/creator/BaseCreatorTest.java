package tests.creator;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.common.BaseTestClass;
import pages.creator.CreatorLivePage;
import pages.creator.CreatorLoginPage;
import utils.ConfigReader;

public class BaseCreatorTest extends BaseTestClass {

    private static final int LOGIN_MAX_RETRIES = 3;

    @BeforeMethod(alwaysRun = true)
    public void creatorLogin() {
        String username = ConfigReader.getProperty("creator.username", null);
        String password = ConfigReader.getProperty("creator.password", null);
        if (username == null || password == null) throw new RuntimeException("creator.username / creator.password not set in config.properties");

        Exception lastException = null;

        for (int attempt = 1; attempt <= LOGIN_MAX_RETRIES; attempt++) {
            try {
                if (attempt == 1) {
                    // First attempt: use landing page click
                    landingPage.clickLoginButton();
                    try {
                        page.waitForURL("**/auth/signIn**", new com.microsoft.playwright.Page.WaitForURLOptions()
                            .setTimeout(ConfigReader.getShortTimeout()));
                    } catch (Exception e) {
                        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Exception ignored) {}
                    }
                } else {
                    // Retry: navigate directly to login URL
                    page.navigate(ConfigReader.getLoginUrl(), new com.microsoft.playwright.Page.NavigateOptions()
                        .setTimeout(ConfigReader.getNavigationTimeout()));
                    page.waitForLoadState(com.microsoft.playwright.options.LoadState.DOMCONTENTLOADED,
                        new com.microsoft.playwright.Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getDefaultTimeout()));
                }

                CreatorLoginPage loginPage = new CreatorLoginPage(page);

                if (!loginPage.isLoginHeaderVisible() || !loginPage.isLoginFormVisible()) {
                    throw new RuntimeException("Login page not fully visible on attempt " + attempt);
                }

                loginPage.login(username, password);

                if (page.url().contains("/auth/intro")) {
                    page.navigate(ConfigReader.getBaseUrl() + "/auth/signIn");
                    if (!loginPage.isLoginHeaderVisible() || !loginPage.isLoginFormVisible()) {
                        throw new RuntimeException("Login page not visible after onboarding redirect on attempt " + attempt);
                    }
                    loginPage.login(username, password);
                }

                // Login succeeded
                return;

            } catch (Exception e) {
                lastException = e;
                if (attempt < LOGIN_MAX_RETRIES) {
                    try { page.waitForTimeout(2000); } catch (Exception ignored) {}
                }
            }
        }

        // All retries exhausted
        Assert.fail("Creator login failed after " + LOGIN_MAX_RETRIES + " attempts: " + lastException.getMessage());
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupDeleteIfAny() {
        try {
            CreatorLivePage live = new CreatorLivePage(page);
            live.tryDeleteLatestLiveEvent();
        } catch (Exception e) {
            // best-effort cleanup; ignore errors to not mask test results
        }
    }
}
