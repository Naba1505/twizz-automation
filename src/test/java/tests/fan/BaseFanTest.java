package tests.fan;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import pages.common.BaseTestClass;
import pages.fan.FanLoginPage;
import utils.ConfigReader;

public class BaseFanTest extends BaseTestClass {

    @BeforeMethod(alwaysRun = true)
    public void fanLogin() {
        String username = ConfigReader.getProperty("fan.username", null);
        String password = ConfigReader.getProperty("fan.password", null);
        if (username == null || password == null) throw new RuntimeException("fan.username / fan.password not set in config.properties");

        FanLoginPage loginPage = new FanLoginPage(page);
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header not visible on fan login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible on fan login screen");
        loginPage.login(username, password);
    }
}
