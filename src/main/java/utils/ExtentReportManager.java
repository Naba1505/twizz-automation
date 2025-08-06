package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();
    private static ConfigReader configReader;

    public static void initReports() {
        configReader = new ConfigReader();
        String environment = configReader.getProperty("environment", "stage");
        String browser = configReader.getProperty("browser", "chrome");
        String author = configReader.getProperty("author", "unknown");
        String reportPath = configReader.getProperty("report.path", "extent-reports/extent-report.html");

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
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

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static void createTest(String testName, String author, String category, String device) {
        ExtentTest test = extent.createTest(testName).assignAuthor(author).assignCategory(category)
                .assignDevice(device);
        testThread.set(test);
    }

    public static ExtentTest getTest() {
        return testThread.get();
    }
}