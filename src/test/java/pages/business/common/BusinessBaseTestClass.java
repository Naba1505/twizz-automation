package pages.business.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import io.qameta.allure.Allure;
import pages.business.employee.BusinessEmployeeLoginPage;
import pages.business.employee.BusinessEmployeeSignUpPage;
import pages.business.employee.EmployeeSettingsPage;
import pages.business.manager.BusinessManagerAddCreatorPage;
import pages.business.manager.BusinessManagerAddEmployeePage;
import pages.business.manager.BusinessManagerDeleteCreatorPage;
import pages.business.manager.BusinessManagerDeleteEmployeePage;
import pages.business.manager.BusinessManagerLanguagePage;
import pages.business.manager.BusinessManagerLoginPage;
import pages.business.manager.BusinessManagerSettingsPage;
import pages.business.manager.BusinessManagerSignUpPage;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorManagerPage;
import utils.BrowserFactory;
import utils.ConfigReader;

/**
 * Base Test Class for Twizz Business App tests
 * Provides setup/teardown for Business app testing
 */
public class BusinessBaseTestClass {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
    protected Page page;
    protected BusinessLandingPage businessLandingPage;
    protected BusinessManagerSignUpPage businessManagerSignUpPage;
    protected BusinessManagerLoginPage businessManagerLoginPage;
    protected BusinessManagerAddCreatorPage businessManagerAddCreatorPage;
    protected BusinessManagerAddEmployeePage businessManagerAddEmployeePage;
    protected BusinessManagerDeleteCreatorPage businessManagerDeleteCreatorPage;
    protected BusinessManagerDeleteEmployeePage businessManagerDeleteEmployeePage;
    protected BusinessManagerLanguagePage businessManagerLanguagePage;
    protected BusinessManagerSettingsPage businessManagerSettingsPage;
    protected BusinessEmployeeSignUpPage businessEmployeeSignUpPage;
    protected BusinessEmployeeLoginPage businessEmployeeLoginPage;
    protected EmployeeSettingsPage employeeSettingsPage;
    protected CreatorLoginPage creatorLoginPage;
    protected CreatorManagerPage creatorManagerPage;

    @BeforeMethod
    public void setUp() {
        BrowserFactory.initialize();
        page = BrowserFactory.getPage();
        page.setDefaultNavigationTimeout(ConfigReader.getNavigationTimeout());
        page.setDefaultTimeout(ConfigReader.getDefaultTimeout());

        String landingPageUrl = ConfigReader.getBusinessLandingPageUrl();
        try {
            page.navigate(landingPageUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            page.waitForLoadState(LoadState.LOAD);
        } catch (Exception first) {
            try {
                page.navigate(landingPageUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                page.waitForLoadState(LoadState.LOAD);
            } catch (Exception second) {
                throw first;
            }
        }
        businessLandingPage = new BusinessLandingPage(page);
        businessManagerSignUpPage = new BusinessManagerSignUpPage(page);
        businessManagerLoginPage = new BusinessManagerLoginPage(page);
        businessManagerAddCreatorPage = new BusinessManagerAddCreatorPage(page);
        businessManagerAddEmployeePage = new BusinessManagerAddEmployeePage(page);
        businessManagerDeleteCreatorPage = new BusinessManagerDeleteCreatorPage(page);
        businessManagerDeleteEmployeePage = new BusinessManagerDeleteEmployeePage(page);
        businessManagerLanguagePage = new BusinessManagerLanguagePage(page);
        businessManagerSettingsPage = new BusinessManagerSettingsPage(page);
        businessEmployeeSignUpPage = new BusinessEmployeeSignUpPage(page);
        businessEmployeeLoginPage = new BusinessEmployeeLoginPage(page);
        employeeSettingsPage = new EmployeeSettingsPage(page);
        creatorLoginPage = new CreatorLoginPage(page);
        creatorManagerPage = new CreatorManagerPage(page);
        businessLandingPage.waitForPageToLoad();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            try {
                String screenshotDir = ConfigReader.getProperty("screenshot.dir", "screenshots");
                Files.createDirectories(Paths.get(screenshotDir));
                String timestamp = LocalDateTime.now().format(dateFormat);
                String screenshotPath = screenshotDir + "/Business_" + result.getName() + "_" + timestamp + ".png";
                byte[] png = page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
                Allure.addAttachment("Failure Screenshot", "image/png", new ByteArrayInputStream(png), ".png");
            } catch (IOException | RuntimeException e) {
                // swallow attachment errors
            }
            try {
                String html = page.content();
                Allure.addAttachment("Page HTML", "text/html", new ByteArrayInputStream(html.getBytes()), ".html");
            } catch (RuntimeException ignored) {}
            try {
                boolean traceEnabled = Boolean.parseBoolean(ConfigReader.getProperty("trace.enable", "true"));
                if (traceEnabled) {
                    String traceDir = ConfigReader.getProperty("trace.dir", "traces");
                    Files.createDirectories(Paths.get(traceDir));
                    String timestamp = LocalDateTime.now().format(dateFormat);
                    Path tracePath = Paths.get(traceDir, "Business_" + result.getName() + "_" + timestamp + ".zip");
                    page.context().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                    Allure.addAttachment("Playwright Trace", "application/zip", Files.newInputStream(tracePath), ".zip");
                }
            } catch (IOException | RuntimeException e) { }
        } else if (ITestResult.SUCCESS == result.getStatus() && Boolean.parseBoolean(ConfigReader.getProperty("screenshot.on.success", "false"))) {
            try {
                String screenshotDir = ConfigReader.getProperty("screenshot.dir", "screenshots");
                Files.createDirectories(Paths.get(screenshotDir));
                String timestamp = LocalDateTime.now().format(dateFormat);
                String screenshotPath = screenshotDir + "/Business_" + result.getName() + "_SUCCESS_" + timestamp + ".png";
                byte[] png = page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
                Allure.addAttachment("Success Screenshot", "image/png", new ByteArrayInputStream(png), ".png");
            } catch (IOException | RuntimeException e) {
                // swallow attachment errors
            }
        }
        BrowserFactory.close();
    }
}
