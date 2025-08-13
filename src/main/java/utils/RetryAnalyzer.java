package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int maxRetryCount = Integer.parseInt(ConfigReader.getProperty("retry.max", "2"));
    private static final long retryDelayMs = Long.parseLong(ConfigReader.getProperty("retry.delay.ms", "0"));

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            logger.warn("Retrying test '{}' (attempt {}/{})", result.getName(), retryCount, maxRetryCount);
            if (result.getThrowable() != null) {
                logger.info("Last failure: {}", result.getThrowable().getMessage());
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