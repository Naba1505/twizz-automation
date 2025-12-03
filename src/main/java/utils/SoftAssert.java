package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Soft assertion utility that collects failures without stopping test execution.
 * Call assertAll() at the end to fail the test if any assertions failed.
 * 
 * Usage:
 * <pre>
 *   SoftAssert soft = new SoftAssert();
 *   soft.assertTrue(condition1, "First check failed");
 *   soft.assertEquals(actual, expected, "Values don't match");
 *   soft.assertAll(); // Throws if any assertion failed
 * </pre>
 */
public class SoftAssert {
    private static final Logger logger = LoggerFactory.getLogger(SoftAssert.class);
    private final List<String> failures = new ArrayList<>();
    private final String testContext;

    public SoftAssert() {
        this.testContext = null;
    }

    /**
     * Create with test context for better logging
     */
    public SoftAssert(String testContext) {
        this.testContext = testContext;
    }

    /**
     * Assert that a condition is true
     */
    public void assertTrue(boolean condition, String message) {
        if (!condition) {
            String failure = message != null ? message : "Expected true but was false";
            failures.add(failure);
            logFailure(failure);
        }
    }

    /**
     * Assert that a condition is false
     */
    public void assertFalse(boolean condition, String message) {
        if (condition) {
            String failure = message != null ? message : "Expected false but was true";
            failures.add(failure);
            logFailure(failure);
        }
    }

    /**
     * Assert that two objects are equal
     */
    public void assertEquals(Object actual, Object expected, String message) {
        boolean equal = (actual == null && expected == null) ||
                        (actual != null && actual.equals(expected));
        if (!equal) {
            String failure = String.format("%s - Expected: [%s] but was: [%s]",
                    message != null ? message : "Values not equal", expected, actual);
            failures.add(failure);
            logFailure(failure);
        }
    }

    /**
     * Assert that an object is not null
     */
    public void assertNotNull(Object object, String message) {
        if (object == null) {
            String failure = message != null ? message : "Expected non-null but was null";
            failures.add(failure);
            logFailure(failure);
        }
    }

    /**
     * Assert that an object is null
     */
    public void assertNull(Object object, String message) {
        if (object != null) {
            String failure = String.format("%s - Expected null but was: [%s]",
                    message != null ? message : "Expected null", object);
            failures.add(failure);
            logFailure(failure);
        }
    }

    /**
     * Assert that a string contains expected substring
     */
    public void assertContains(String actual, String expectedSubstring, String message) {
        if (actual == null || !actual.contains(expectedSubstring)) {
            String failure = String.format("%s - Expected [%s] to contain [%s]",
                    message != null ? message : "String does not contain expected", actual, expectedSubstring);
            failures.add(failure);
            logFailure(failure);
        }
    }

    /**
     * Record a failure directly
     */
    public void fail(String message) {
        String failure = message != null ? message : "Explicit failure";
        failures.add(failure);
        logFailure(failure);
    }

    /**
     * Check if any assertions have failed
     */
    public boolean hasFailures() {
        return !failures.isEmpty();
    }

    /**
     * Get count of failures
     */
    public int getFailureCount() {
        return failures.size();
    }

    /**
     * Get all failure messages
     */
    public List<String> getFailures() {
        return new ArrayList<>(failures);
    }

    /**
     * Verify all assertions passed. Throws AssertionError if any failed.
     */
    public void assertAll() {
        if (!failures.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Soft assertion failures (").append(failures.size()).append("):\n");
            for (int i = 0; i < failures.size(); i++) {
                sb.append("  ").append(i + 1).append(". ").append(failures.get(i)).append("\n");
            }
            String message = sb.toString();
            logger.error(message);
            throw new AssertionError(message);
        }
    }

    /**
     * Clear all recorded failures (useful for reuse)
     */
    public void clear() {
        failures.clear();
    }

    private void logFailure(String failure) {
        if (testContext != null) {
            logger.warn("[{}] Soft assertion failed: {}", testContext, failure);
        } else {
            logger.warn("Soft assertion failed: {}", failure);
        }
    }
}
