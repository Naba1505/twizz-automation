package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.BrowserFactory;
import utils.ConfigReader;
import utils.ExtentReportManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.io.ByteArrayInputStream;
import java.nio.file.Path;

public class BaseTestClass {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
    protected Page page;
    protected LandingPage landingPage;
    protected CreatorRegistrationPage creatorRegistrationPage;

    @BeforeMethod
    public void setUp(ITestResult result) {
        BrowserFactory.initialize();
        page = BrowserFactory.getPage();
        String landingPageUrl = ConfigReader.getLandingPageUrl();
        page.navigate(landingPageUrl);
        landingPage = new LandingPage(page);
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().assignAuthor(ConfigReader.getProperty("author", "unknown"));
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            try {
                String screenshotDir = ConfigReader.getProperty("screenshot.dir", "screenshots");
                Files.createDirectories(Paths.get(screenshotDir));
                String timestamp = dateFormat.format(System.currentTimeMillis());
                String screenshotPath = screenshotDir + "/" + result.getName() + "_" + timestamp + ".png";
                byte[] png = page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
                ExtentReportManager.getTest().fail("Screenshot on failure:")
                        .addScreenCaptureFromPath(screenshotPath);
                // Allure attachment
                Allure.addAttachment("Failure Screenshot", "image/png", new ByteArrayInputStream(png), ".png");
            } catch (Exception e) {
                ExtentReportManager.getTest().warning("Screenshot not captured: " + e.getMessage());
            }
            // Export Playwright trace on failure
            try {
                boolean traceEnabled = Boolean.parseBoolean(ConfigReader.getProperty("trace.enable", "true"));
                if (traceEnabled) {
                    String traceDir = ConfigReader.getProperty("trace.dir", "traces");
                    Files.createDirectories(Paths.get(traceDir));
                    String timestamp = dateFormat.format(System.currentTimeMillis());
                    Path tracePath = Paths.get(traceDir, result.getName() + "_" + timestamp + ".zip");
                    page.context().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                    Allure.addAttachment("Playwright Trace", "application/zip", Files.newInputStream(tracePath), ".zip");
                }
            } catch (Exception e) {
                ExtentReportManager.getTest().warning("Trace export failed: " + e.getMessage());
            }
        } else if (ITestResult.SUCCESS == result.getStatus() && Boolean.parseBoolean(ConfigReader.getProperty("screenshot.on.success", "false"))) {
            try {
                String screenshotDir = ConfigReader.getProperty("screenshot.dir", "screenshots");
                Files.createDirectories(Paths.get(screenshotDir));
                String timestamp = dateFormat.format(System.currentTimeMillis());
                String screenshotPath = screenshotDir + "/" + result.getName() + "_SUCCESS_" + timestamp + ".png";
                byte[] png = page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
                ExtentReportManager.getTest().pass("Screenshot on success:")
                        .addScreenCaptureFromPath(screenshotPath);
                // Allure attachment
                Allure.addAttachment("Success Screenshot", "image/png", new ByteArrayInputStream(png), ".png");
            } catch (Exception e) {
                ExtentReportManager.getTest().warning("Success screenshot not captured: " + e.getMessage());
            }
            // Optionally export trace on success
            try {
                boolean traceExportOnSuccess = Boolean.parseBoolean(ConfigReader.getProperty("trace.export.on.success", "false"));
                if (traceExportOnSuccess) {
                    String traceDir = ConfigReader.getProperty("trace.dir", "traces");
                    Files.createDirectories(Paths.get(traceDir));
                    String timestamp = dateFormat.format(System.currentTimeMillis());
                    Path tracePath = Paths.get(traceDir, result.getName() + "_SUCCESS_" + timestamp + ".zip");
                    page.context().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                    Allure.addAttachment("Playwright Trace (Success)", "application/zip", Files.newInputStream(tracePath), ".zip");
                }
            } catch (Exception e) {
                ExtentReportManager.getTest().warning("Trace export on success failed: " + e.getMessage());
            }
        }
        BrowserFactory.close();
    }
}