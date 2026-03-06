package tests.fan;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.fan.FanLoginPage;
import utils.ConfigReader;

public class FanLoginTest extends BaseTestClass {
    
    // Timeout constants (in milliseconds) - Standardized values (optimized)
    private static final int HOME_ICON_TIMEOUT = 5000;    // Home icon visibility timeout

    @Test(priority = 1, description = "Verify fan can login and lands on fan home URL")
    public void testFanLogin() {
        String username = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String password = ConfigReader.getProperty("fan.password", ConfigReader.getProperty("fan.default.password", "Twizz$123"));

        FanLoginPage pageObj = new FanLoginPage(page);
        pageObj.navigate();
        Assert.assertTrue(pageObj.isLoginHeaderVisible(), "Login header not visible on fan login screen");
        Assert.assertTrue(pageObj.isLoginFormVisible(), "Login form is not visible on fan login screen");
        pageObj.login(username, password);
        // Login method already asserts Home icon visibility
        // Verify Home icon is visible as success indicator (fan may land on /fan/home or /common/discover)
        Assert.assertTrue(pageObj.isHomeIconVisible(HOME_ICON_TIMEOUT), "Fan login failed - Home icon not visible. Actual URL: " + page.url());
    }
}
