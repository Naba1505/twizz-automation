package tests.creator;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.common.BaseTestClass;
import pages.creator.CreatorLivePage;
import pages.creator.CreatorLoginPage;
import utils.ConfigReader;

public class BaseCreatorTest extends BaseTestClass {

    @BeforeMethod(alwaysRun = true)
    public void creatorLogin() {
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Use landing page already loaded by BaseTestClass instead of navigating directly
        landingPage.clickLoginButton();
        
        // Wait for navigation to complete after clicking login button
        try {
            page.waitForURL("**/auth/signIn**", new com.microsoft.playwright.Page.WaitForURLOptions()
                .setTimeout(ConfigReader.getShortTimeout()));
        } catch (Exception e) {
            // If URL wait fails, give a small stabilization wait
            page.waitForTimeout(2000);
        }
        
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header not visible on creator login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible on creator login screen");
        loginPage.login(username, password);
    }

    @AfterMethod(alwaysRun = true)
    public void cleanupDeleteIfAny() {
        try {
            CreatorLivePage live = new CreatorLivePage(page);
            live.tryDeleteLatestLiveEvent();
        } catch (Exception ignored) {
            // best-effort cleanup; ignore errors to not mask test results
        }
    }
}
