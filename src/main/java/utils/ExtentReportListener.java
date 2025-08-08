package utils;

import com.aventstack.extentreports.Status;
import org.testng.*;

import java.util.Optional;

public class ExtentReportListener extends ExtentReportManager implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        initReports();
    }

    @Override
    public void onFinish(ITestContext context) {
        flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String methodName = result.getMethod().getMethodName();
        String description = Optional.ofNullable(result.getMethod().getDescription()).orElse("");
        String testName = description.isEmpty() ? methodName : methodName + " - " + description;

        String author = ConfigReader.getProperty("author", "unknown");
        String category = result.getTestClass().getRealClass().getSimpleName();
        String device = ConfigReader.getProperty("browser", "chrome") + " - " + System.getProperty("os.name", "OS");

        createTest(testName, author, category, device);
        getTest().log(Status.INFO, "Test Started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        getTest().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (result.getThrowable() != null) {
            getTest().fail(result.getThrowable());
        } else {
            getTest().log(Status.FAIL, "Test Failed");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String reason = result.getThrowable() != null ? result.getThrowable().getMessage() : "No reason provided";
        getTest().log(Status.SKIP, "Test Skipped: " + reason);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }
}