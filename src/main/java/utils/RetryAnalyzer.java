package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LoggerFactory.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int maxRetryCount = Integer.parseInt(ConfigReader.getProperty("retry.max", "2"));
    private static final long retryDelayMs = Long.parseLong(ConfigReader.getProperty("retry.delay.ms", "0"));
    
    // Absolute safety limits to prevent infinite retry loops
    private static final int ABSOLUTE_MAX_RETRIES = 3;  // Never retry more than 3 times total
    private static final long MAX_RETRY_DURATION_MS = 300000;  // 5 minutes max retry time per test
    
    // Track test execution times and retry counts across all instances
    private static final Map<String, Long> testStartTimes = new ConcurrentHashMap<>();
    private static final Map<String, Integer> testRetryCountMap = new ConcurrentHashMap<>();

    @Override
    public boolean retry(ITestResult result) {
        String testKey = result.getMethod().getMethodName();
        
        // Track start time for this test (first failure)
        testStartTimes.putIfAbsent(testKey, System.currentTimeMillis());
        
        // Track total retry count for this test across all RetryAnalyzer instances
        int totalRetries = testRetryCountMap.getOrDefault(testKey, 0);
        testRetryCountMap.put(testKey, totalRetries + 1);
        
        // Check absolute safety limits first
        long elapsed = System.currentTimeMillis() - testStartTimes.get(testKey);
        
        if (totalRetries >= ABSOLUTE_MAX_RETRIES) {
            logger.error("Test '{}' exceeded absolute maximum retries ({}). Failing permanently to prevent infinite loop.", 
                         testKey, ABSOLUTE_MAX_RETRIES);
            logger.error("Total time spent retrying: {}ms", elapsed);
            // Clean up tracking maps
            testStartTimes.remove(testKey);
            testRetryCountMap.remove(testKey);
            return false;
        }
        
        if (elapsed > MAX_RETRY_DURATION_MS) {
            logger.error("Test '{}' exceeded maximum retry duration ({}ms). Failing permanently to prevent infinite loop.", 
                         testKey, MAX_RETRY_DURATION_MS);
            logger.error("Total retries attempted: {}", totalRetries);
            // Clean up tracking maps
            testStartTimes.remove(testKey);
            testRetryCountMap.remove(testKey);
            return false;
        }
        
        // Normal retry logic
        if (retryCount < maxRetryCount) {
            retryCount++;
            logger.warn("Retrying test '{}' (attempt {}/{}, total attempts: {})", 
                        testKey, retryCount, maxRetryCount, totalRetries + 1);
            if (result.getThrowable() != null) {
                logger.info("Last failure: {}", result.getThrowable().getMessage());
            }
            logger.info("Time elapsed since first failure: {}ms (max allowed: {}ms)", elapsed, MAX_RETRY_DURATION_MS);

            if (retryDelayMs > 0) {
                try {
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }

            return true;
        }
        
        // Clean up tracking maps when test exhausts normal retries
        testStartTimes.remove(testKey);
        testRetryCountMap.remove(testKey);
        return false;
    }
}