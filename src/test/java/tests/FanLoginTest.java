package tests;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.FanLoginPage;
import utils.ConfigReader;
import java.util.regex.Pattern;

public class FanLoginTest extends BaseTestClass {

    @Test(priority = 1, description = "Verify fan can login and lands on fan home URL")
    public void testFanLogin() {
        String username = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String password = ConfigReader.getProperty("fan.password", ConfigReader.getProperty("fan.default.password", "Twizz$123"));

        FanLoginPage pageObj = new FanLoginPage(page);
        pageObj.navigate();
        Assert.assertTrue(pageObj.isLoginHeaderVisible(), "Login header not visible on fan login screen");
        Assert.assertTrue(pageObj.isLoginFormVisible(), "Login form is not visible on fan login screen");
        pageObj.login(username, password);
        // Wait for redirect to fan home via page object helper and assert URL
        pageObj.waitForFanHomeUrl(15_000);
        Assert.assertTrue(pageObj.isOnFanHomeUrl(1_000), "Fan did not land on /fan/home. Actual: " + page.url());
    }
}
