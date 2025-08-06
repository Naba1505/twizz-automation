package utils;

import com.aventstack.extentreports.Status;
import org.testng.*;

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
        createTest(result.getMethod().getMethodName(), "Teja Naba", "Smoke", "Chrome - Windows 11");
        getTest().log(Status.INFO, "Test Started");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        getTest().log(Status.PASS, "Test Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        getTest().log(Status.FAIL, "Test Failed: " + result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        getTest().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }
}