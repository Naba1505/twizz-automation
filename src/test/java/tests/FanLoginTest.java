package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.FanLoginPage;
import utils.ConfigReader;

public class FanLoginTest extends BaseTestClass {

    @Test(priority = 1, description = "Verify fan can login and sees LIVE on home screen")
    public void testFanLogin() {
        String username = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String password = ConfigReader.getProperty("fan.password", ConfigReader.getProperty("fan.default.password", "Twizz$123"));

        FanLoginPage pageObj = new FanLoginPage(page);
        pageObj.navigate();
        Assert.assertTrue(pageObj.isLoginHeaderVisible(), "Login header not visible on fan login screen");
        Assert.assertTrue(pageObj.isLoginFormVisible(), "Login form is not visible on fan login screen");
        pageObj.login(username, password);
        Assert.assertTrue(pageObj.isLiveVisible(), "LIVE indicator not visible after fan login");
    }
}
