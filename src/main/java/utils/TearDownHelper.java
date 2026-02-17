package utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.testng.ITestResult;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;

import io.qameta.allure.Allure;

/**
 * Shared teardown logic for capturing screenshots, HTML, and Playwright traces on test completion.
 * Used by both BaseTestClass and BusinessBaseTestClass to eliminate duplication.
 */
public final class TearDownHelper {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");

    private TearDownHelper() {}

    /**
     * Handles post-test artifact capture (screenshots, HTML, traces) and Allure attachments.
     *
     * @param page       the Playwright page instance
     * @param result     the TestNG test result
     * @param filePrefix prefix for artifact file names (e.g. "" for main app, "Business_" for business app)
     */
    public static void handleTestResult(Page page, ITestResult result, String filePrefix) {
        if (ITestResult.FAILURE == result.getStatus()) {
            captureScreenshot(page, result, filePrefix, "Failure Screenshot");
            captureHtml(page);
            captureTrace(page, result, filePrefix, "Playwright Trace");
        } else if (ITestResult.SUCCESS == result.getStatus()
                && Boolean.parseBoolean(ConfigReader.getProperty("screenshot.on.success", "false"))) {
            captureScreenshot(page, result, filePrefix + "SUCCESS_", "Success Screenshot");
            captureTraceOnSuccess(page, result, filePrefix);
        }
    }

    private static void captureScreenshot(Page page, ITestResult result, String filePrefix, String attachmentName) {
        try {
            String screenshotDir = ConfigReader.getProperty("screenshot.dir", "screenshots");
            Files.createDirectories(Paths.get(screenshotDir));
            String timestamp = LocalDateTime.now().format(DATE_FORMAT);
            String screenshotPath = screenshotDir + "/" + filePrefix + result.getName() + "_" + timestamp + ".png";
            byte[] png = page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
            Allure.addAttachment(attachmentName, "image/png", new ByteArrayInputStream(png), ".png");
        } catch (IOException | RuntimeException e) {
            // swallow attachment errors
        }
    }

    private static void captureHtml(Page page) {
        try {
            String html = page.content();
            Allure.addAttachment("Page HTML", "text/html", new ByteArrayInputStream(html.getBytes()), ".html");
        } catch (RuntimeException ignored) {}
    }

    private static void captureTrace(Page page, ITestResult result, String filePrefix, String attachmentName) {
        try {
            boolean traceEnabled = Boolean.parseBoolean(ConfigReader.getProperty("trace.enable", "true"));
            if (traceEnabled) {
                String traceDir = ConfigReader.getProperty("trace.dir", "traces");
                Files.createDirectories(Paths.get(traceDir));
                String timestamp = LocalDateTime.now().format(DATE_FORMAT);
                Path tracePath = Paths.get(traceDir, filePrefix + result.getName() + "_" + timestamp + ".zip");
                page.context().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                Allure.addAttachment(attachmentName, "application/zip", Files.newInputStream(tracePath), ".zip");
            }
        } catch (IOException | RuntimeException e) { }
    }

    private static void captureTraceOnSuccess(Page page, ITestResult result, String filePrefix) {
        try {
            boolean traceExportOnSuccess = Boolean.parseBoolean(ConfigReader.getProperty("trace.export.on.success", "false"));
            if (traceExportOnSuccess) {
                String traceDir = ConfigReader.getProperty("trace.dir", "traces");
                Files.createDirectories(Paths.get(traceDir));
                String timestamp = LocalDateTime.now().format(DATE_FORMAT);
                Path tracePath = Paths.get(traceDir, filePrefix + result.getName() + "_SUCCESS_" + timestamp + ".zip");
                page.context().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
                Allure.addAttachment("Playwright Trace (Success)", "application/zip", Files.newInputStream(tracePath), ".zip");
            }
        } catch (IOException | RuntimeException e) { }
    }
}
