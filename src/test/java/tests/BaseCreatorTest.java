package tests;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import pages.BaseTestClass;
import pages.CreatorLivePage;
import pages.CreatorLoginPage;
import utils.ConfigReader;

public class BaseCreatorTest extends BaseTestClass {

    @BeforeMethod(alwaysRun = true)
    public void creatorLogin() {
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
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
