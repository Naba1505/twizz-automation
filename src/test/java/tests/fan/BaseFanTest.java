package tests.fan;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import pages.common.BaseTestClass;
import pages.fan.FanLoginPage;
import utils.ConfigReader;

public class BaseFanTest extends BaseTestClass {

    private static final int LOGIN_MAX_RETRIES = 3;

    @BeforeMethod(alwaysRun = true)
    public void fanLogin() {
        String username = ConfigReader.getProperty("fan.username", null);
        String password = ConfigReader.getProperty("fan.password", null);
        if (username == null || password == null) throw new RuntimeException("fan.username / fan.password not set in config.properties");

        Exception lastException = null;

        for (int attempt = 1; attempt <= LOGIN_MAX_RETRIES; attempt++) {
            try {
                FanLoginPage loginPage = new FanLoginPage(page);
                loginPage.navigate();

                if (!loginPage.isLoginHeaderVisible() || !loginPage.isLoginFormVisible()) {
                    throw new RuntimeException("Fan login page not fully visible on attempt " + attempt);
                }

                loginPage.login(username, password);
                return;

            } catch (Exception e) {
                lastException = e;
                if (attempt < LOGIN_MAX_RETRIES) {
                    try { page.waitForTimeout(2000); } catch (Exception ignored) {}
                }
            }
        }

        Assert.fail("Fan login failed after " + LOGIN_MAX_RETRIES + " attempts: " + lastException.getMessage());
    }
}
