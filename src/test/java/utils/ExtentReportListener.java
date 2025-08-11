package utils;

import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Minimal no-op TestNG listener placeholder to unblock suite execution.
 * You can replace this with your real ExtentReports integration later.
 */
public class ExtentReportListener implements ITestListener, ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        // no-op
    }

    @Override
    public void onFinish(ISuite suite) {
        // no-op
    }

    @Override
    public void onTestStart(ITestResult result) {
        // no-op
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // no-op
    }

    @Override
    public void onTestFailure(ITestResult result) {
        // no-op
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // no-op
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // no-op
    }

    @Override
    public void onStart(ITestContext context) {
        // no-op
    }

    @Override
    public void onFinish(ITestContext context) {
        // no-op
    }
}
