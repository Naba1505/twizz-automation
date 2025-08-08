package utils;

import com.aventstack.extentreports.Status;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 0;
    private static final int maxRetryCount = Integer.parseInt(ConfigReader.getProperty("retry.max", "2"));
    private static final long retryDelayMs = Long.parseLong(ConfigReader.getProperty("retry.delay.ms", "0"));

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            ExtentReportManager.getTest().log(Status.WARNING,
                    "Retrying test '" + result.getName() + "' (attempt " + retryCount + "/" + maxRetryCount + ")");
            if (result.getThrowable() != null) {
                ExtentReportManager.getTest().log(Status.INFO, "Last failure: " + result.getThrowable().getMessage());
            }

            if (retryDelayMs > 0) {
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }

            return true;
        }
        return false;
    }
}