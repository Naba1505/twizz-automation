package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import io.qameta.allure.Allure;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import utils.BrowserFactory;
import utils.ConfigReader;

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
        // Increase default timeouts for slow networks and heavy pages
        page.setDefaultNavigationTimeout(60000);
        page.setDefaultTimeout(60000);

        // Optionally start tracing early if enabled
        try {
            boolean traceEnabled = Boolean.parseBoolean(ConfigReader.getProperty("trace.enable", "true"));
            if (traceEnabled) {
                page.context().tracing().start(new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true));
            }
        } catch (Exception ignored) {
        }

        String landingPageUrl = ConfigReader.getLandingPageUrl();
        // Robust navigation with waitUntil + extra load-state wait and a single retry
        try {
            page.navigate(landingPageUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            // Some Playwright versions don't expose NETWORKIDLE reliably; LOAD is safer across versions
            page.waitForLoadState(LoadState.LOAD);
        } catch (Exception first) {
            // Retry once with a clean attempt
            try {
                page.navigate(landingPageUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                page.waitForLoadState(LoadState.LOAD);
            } catch (Exception second) {
                throw first;
            }
        }
        landingPage = new LandingPage(page);
        // Ensure the landing page is fully interactive before tests
        try {
            landingPage.waitForPageToLoad();
        } catch (Exception e) {
            // do not swallow setup issues silently; rethrow to mark config failure
            throw e;
        }
        // Author metadata can be added to Allure via labels if desired
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
                // Allure attachment
                Allure.addAttachment("Failure Screenshot", "image/png", new ByteArrayInputStream(png), ".png");
            } catch (Exception e) {
                // swallow attachment errors, already failing test
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
            } catch (Exception e) { }
        } else if (ITestResult.SUCCESS == result.getStatus() && Boolean.parseBoolean(ConfigReader.getProperty("screenshot.on.success", "false"))) {
            try {
                String screenshotDir = ConfigReader.getProperty("screenshot.dir", "screenshots");
                Files.createDirectories(Paths.get(screenshotDir));
                String timestamp = dateFormat.format(System.currentTimeMillis());
                String screenshotPath = screenshotDir + "/" + result.getName() + "_SUCCESS_" + timestamp + ".png";
                byte[] png = page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
                // Allure attachment
                Allure.addAttachment("Success Screenshot", "image/png", new ByteArrayInputStream(png), ".png");
            } catch (Exception e) {
                // swallow attachment errors
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
            } catch (Exception e) { }
        }
        BrowserFactory.close();
    }
}