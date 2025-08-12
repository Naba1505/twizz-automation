package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();
    private static Path reportFilePath; // holds the final report path

    public static synchronized void initReports() {
        // Idempotent init to avoid double-initialization when TestNG runs in parallel
        if (extent != null) {
            return;
        }
        String environment = ConfigReader.getProperty("environment", "stage");
        String browser = ConfigReader.getProperty("browser", "chrome");
        String author = ConfigReader.getProperty("author", "unknown");
        String reportPath = ConfigReader.getProperty("report.path", "extent-reports/extent-report.html");

        // Append timestamp to report file name and ensure directory exists
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        Path originalPath = Paths.get(reportPath);
        String fileName = originalPath.getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        String baseName = dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
        String extension = dotIndex > 0 ? fileName.substring(dotIndex) : "";
        String stampedFileName = baseName + "_" + timestamp + extension;
        Path parentDir = originalPath.getParent();
        Path finalPath = parentDir != null ? parentDir.resolve(stampedFileName) : Paths.get(stampedFileName);
        try {
            Path finalParent = finalPath.getParent();
            if (finalParent != null) {
                Files.createDirectories(finalParent);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create report directory: " + parentDir, e);
        }
        reportFilePath = finalPath;

        ExtentSparkReporter spark = new ExtentSparkReporter(finalPath.toString());
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("Twizz UI Test Report");
        spark.config().setReportName("Automation Execution Results");

        extent = new ExtentReports();
        extent.attachReporter(spark);

        extent.setSystemInfo("Project Name", "Twizz Automation");
        extent.setSystemInfo("QA", author);
        extent.setSystemInfo("Environment", environment);
        extent.setSystemInfo("Browser", browser);
    }

    public static Path getReportFilePath() {
        return reportFilePath;
    }

    public static synchronized void flushReports() {
        if (extent != null) {
            extent.flush();
        }
        // Clear thread-local reference to avoid leaking tests between runs
        testThread.remove();
        extent = null;
    }

    public static void createTest(String testName, String author, String category, String device) {
        if (extent == null) {
            // Defensive: initialize if not already done
            initReports();
        }
        ExtentTest test = extent.createTest(testName)
                .assignAuthor(author)
                .assignCategory(category)
                .assignDevice(device);
        testThread.set(test);
    }

    public static ExtentTest getTest() {
        return testThread.get();
    }
}