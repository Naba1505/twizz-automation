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
        // Wait for redirect to fan home and assert URL instead of LIVE indicator
        page.waitForURL(Pattern.compile(".*/fan/home.*"), new Page.WaitForURLOptions().setTimeout(10000));
        Assert.assertTrue(page.url().contains("/fan/home"), "Fan did not land on /fan/home. Actual: " + page.url());
    }
}
