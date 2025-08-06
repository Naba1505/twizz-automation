package utils;

import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserFactory {
    private static final Logger logger = LoggerFactory.getLogger(BrowserFactory.class);
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;

    public static synchronized void initialize() {
        if (playwright == null) {
            try {
                playwright = Playwright.create();
                logger.info("Playwright initialized");
            } catch (Exception e) {
                logger.error("Failed to initialize Playwright", e);
                throw new RuntimeException("Playwright initialization failed", e);
            }
        }

        if (browser == null) {
            String browserType = ConfigReader.getBrowserType();
            boolean headless = ConfigReader.isHeadless();
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);
            try {
                switch (browserType.toLowerCase()) {
                    case "chrome":
                        browser = playwright.chromium().launch(launchOptions.setChannel("chrome"));
                        break;
                    case "edge":
                        browser = playwright.chromium().launch(launchOptions.setChannel("msedge"));
                        break;
                    case "safari":
                    case "webkit":
                        browser = playwright.webkit().launch(launchOptions);
                        break;
                    case "firefox":
                        browser = playwright.firefox().launch(launchOptions);
                        break;
                    case "chromium":
                    default:
                        browser = playwright.chromium().launch(launchOptions);
                        break;
                }
                logger.info("Browser {} launched in {} mode", browserType, headless ? "headless" : "headed");
            } catch (Exception e) {
                logger.error("Failed to launch browser: {}", browserType, e);
                throw new RuntimeException("Browser launch failed", e);
            }
        }

        if (context == null || ConfigReader.isIncognito()) {
            Browser.NewContextOptions options = new Browser.NewContextOptions()
                    .setViewportSize(
                            Integer.parseInt(ConfigReader.getProperty("viewport.width", "1280")),
                            Integer.parseInt(ConfigReader.getProperty("viewport.height", "720"))
                    );
            context = browser.newContext(options);
            logger.info("New browser context created (incognito: {})", ConfigReader.isIncognito());
        }
    }

    public static BrowserContext getContext() {
        if (browser == null || context == null) {
            initialize();
        }
        return context;
    }

    public static Page getPage() {
        return getContext().newPage();
    }

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