package utils;

import com.aventstack.extentreports.Status;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 0;
    private static final int maxRetryCount = Integer.parseInt(ConfigReader.getProperty("retry.max", "2"));

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            ExtentReportManager.getTest().log(Status.INFO,
                    "Retrying test " + result.getName() + " (Attempt " + retryCount + ")");
            return true;
        }
        return false;
    }
}