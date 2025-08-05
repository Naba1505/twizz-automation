package utils;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class BrowserFactory {
    private static final Logger logger = LoggerFactory.getLogger(BrowserFactory.class);
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;

    // Initialize Playwright and browser
    public static void initialize() {
        if (playwright == null) {
            playwright = Playwright.create();
            logger.info("Playwright initialized");
        }

        String browserType = ConfigReader.getBrowserType();
        boolean headless = ConfigReader.isHeadless();

        if (browser == null) {
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);
            switch (browserType.toLowerCase()) {
                case "chrome":
                    browser = playwright.chromium().launch(launchOptions.setChannel("chrome"));
                    break;
                case "edge":
                    browser = playwright.chromium().launch(launchOptions.setChannel("msedge"));
                    break;
                case "safari":
                    browser = playwright.webkit().launch(launchOptions.setChannel("webkit"));
                    break;
                case "firefox":
                    browser = playwright.firefox().launch(launchOptions);
                    break;
                case "webkit":
                    browser = playwright.webkit().launch(launchOptions);
                    break;
                case "chromium":
                default:
                    browser = playwright.chromium().launch(launchOptions);
                    break;
            }
            logger.info("Browser {} launched in {} mode", browserType, headless ? "headless" : "headed");
        }

        // Create a new context if incognito is true or context is null
        if (context == null || ConfigReader.isIncognito()) {
            if (context != null) {
                context.close();
                logger.info("Previous context closed due to incognito mode");
            }
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int width = (int) screenSize.getWidth();
            int height = (int) screenSize.getHeight();
            context = browser.newContext(new Browser.NewContextOptions().setViewportSize(width, height));
            logger.info("New browser context created (incognito: {})", ConfigReader.isIncognito());
        }
    }

    // Get a new context
    public static BrowserContext getContext() {
        if (browser == null || context == null) {
            initialize();
        }
        return context;
    }

    // Get a new page
    public static com.microsoft.playwright.Page getPage() {
        if (browser == null || context == null) {
            initialize();
        }
        return context.newPage();
    }

    // Close browser and Playwright
    public static void close() {
        if (context != null) {
            context.close();
            logger.info("Browser context closed");
            context = null;
        }
        if (browser != null) {
            browser.close();
            logger.info("Browser closed");
            browser = null;
        }
        if (playwright != null) {
            playwright.close();
            logger.info("Playwright closed");
            playwright = null;
        }
    }
}
