package cucumber.hooks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cucumber.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import utils.BrowserFactory;

/**
 * Cucumber hooks for browser lifecycle management.
 * Each scenario gets a fresh browser context for test isolation.
 */
public class BrowserHooks {

    private static final Logger logger = LoggerFactory.getLogger(BrowserHooks.class);
    private final TestContext context;

    public BrowserHooks(TestContext context) {
        this.context = context;
    }

    @Before(order = 0)
    public void setUp(Scenario scenario) {
        logger.info("Starting scenario: {}", scenario.getName());
        // Eagerly initialize the page so it's ready for step definitions
        context.getPage();
    }

    @After(order = 0)
    public void tearDown(Scenario scenario) {
        logger.info("Finished scenario: {} — Status: {}", scenario.getName(), scenario.getStatus());
        if (scenario.isFailed()) {
            try {
                byte[] screenshot = context.getPage().screenshot();
                scenario.attach(screenshot, "image/png", scenario.getName());
                logger.info("Screenshot attached for failed scenario: {}", scenario.getName());
            } catch (Exception e) {
                logger.warn("Failed to capture screenshot: {}", e.getMessage());
            }
        }
        BrowserFactory.close();
    }
}
