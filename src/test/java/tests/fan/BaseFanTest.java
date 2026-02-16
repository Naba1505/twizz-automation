package tests.fan;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import pages.common.BaseTestClass;
import pages.fan.FanLoginPage;
import utils.ConfigReader;

public class BaseFanTest extends BaseTestClass {

    @BeforeMethod(alwaysRun = true)
    public void fanLogin() {
        String username = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String password = ConfigReader.getProperty("fan.password", "Twizz$123");

        FanLoginPage loginPage = new FanLoginPage(page);
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header not visible on fan login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible on fan login screen");
        loginPage.login(username, password);
    }
}
