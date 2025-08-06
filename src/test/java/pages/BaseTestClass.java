package pages;

import com.microsoft.playwright.Page;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.BrowserFactory;
import utils.ConfigReader;
import utils.ExtentReportManager;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

public class BaseTestClass {
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SSS");
    protected Page page;
    protected LandingPage landingPage;

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
                page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
                ExtentReportManager.getTest().fail("Screenshot on failure:")
                        .addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                ExtentReportManager.getTest().warning("Screenshot not captured: " + e.getMessage());
            }
        }
        BrowserFactory.close();
    }
}