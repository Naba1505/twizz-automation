package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Structured logging helper that includes test context in log messages.
 * Provides consistent formatting across all test classes.
 * 
 * Usage:
 * <pre>
 *   TestLogger log = new TestLogger("CreatorLoginTest");
 *   log.info("Starting login flow");
 *   log.step("Entering credentials");
 *   log.pass("Login successful");
 *   log.fail("Expected element not found");
 * </pre>
 */
public class TestLogger {
    private final Logger logger;
    private final String testContext;
    private int stepNumber = 0;

    public TestLogger(String testContext) {
        this.testContext = testContext;
        this.logger = LoggerFactory.getLogger(testContext);
    }

    public TestLogger(Class<?> testClass) {
        this.testContext = testClass.getSimpleName();
        this.logger = LoggerFactory.getLogger(testClass);
    }

    /**
     * Log info message with test context
     */
    public void info(String message) {
        logger.info("[{}] {}", testContext, message);
    }

    /**
     * Log info message with format args
     */
    public void info(String format, Object... args) {
        logger.info("[" + testContext + "] " + format, args);
    }

    /**
     * Log a numbered test step
     */
    public void step(String description) {
        stepNumber++;
        logger.info("[{}] Step {}: {}", testContext, stepNumber, description);
    }

    /**
     * Log a successful action
     */
    public void pass(String message) {
        logger.info("[{}] ✓ PASS: {}", testContext, message);
    }

    /**
     * Log a failed action (without throwing)
     */
    public void fail(String message) {
        logger.error("[{}] ✗ FAIL: {}", testContext, message);
    }

    /**
     * Log a warning
     */
    public void warn(String message) {
        logger.warn("[{}] ⚠ {}", testContext, message);
    }

    /**
     * Log a warning with format args
     */
    public void warn(String format, Object... args) {
        logger.warn("[" + testContext + "] ⚠ " + format, args);
    }

    /**
     * Log debug message
     */
    public void debug(String message) {
        logger.debug("[{}] {}", testContext, message);
    }

    /**
     * Log debug message with format args
     */
    public void debug(String format, Object... args) {
        logger.debug("[" + testContext + "] " + format, args);
    }

    /**
     * Log error with exception
     */
    public void error(String message, Throwable t) {
        logger.error("[{}] {}", testContext, message, t);
    }

    /**
     * Log action start
     */
    public void startAction(String action) {
        logger.info("[{}] → Starting: {}", testContext, action);
    }

    /**
     * Log action complete
     */
    public void endAction(String action) {
        logger.info("[{}] ← Completed: {}", testContext, action);
    }

    /**
     * Log element interaction
     */
    public void interact(String element, String action) {
        logger.debug("[{}] {} → {}", testContext, action, element);
    }

    /**
     * Log navigation
     */
    public void navigate(String destination) {
        logger.info("[{}] Navigating to: {}", testContext, destination);
    }

    /**
     * Log assertion
     */
    public void asserting(String what) {
        logger.debug("[{}] Asserting: {}", testContext, what);
    }

    /**
     * Reset step counter (call at start of each test method if reusing logger)
     */
    public void resetSteps() {
        stepNumber = 0;
    }

    /**
     * Get current step number
     */
    public int getCurrentStep() {
        return stepNumber;
    }
}
