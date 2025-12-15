package utils;

import com.microsoft.playwright.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class BrowserFactory {
    private static final Logger logger = LoggerFactory.getLogger(BrowserFactory.class);
    // Thread-local Playwright, Browser, Context, and Page for full isolation
    private static final ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> tlContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> tlPage = new ThreadLocal<>();

    public static synchronized void initialize() {
        Playwright pw = tlPlaywright.get();
        if (pw == null) {
            try {
                pw = Playwright.create();
                tlPlaywright.set(pw);
                logger.info("Playwright initialized for thread {}", Thread.currentThread().getName());
            } catch (Exception e) {
                logger.error("Failed to initialize Playwright", e);
                throw new RuntimeException("Playwright initialization failed", e);
            }
        }

        Browser browser = tlBrowser.get();
        if (browser == null) {
            String browserType = ConfigReader.getBrowserType();
            boolean headless = ConfigReader.isHeadless();
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions().setHeadless(headless);
            try {
                switch (browserType.toLowerCase()) {
                    case "chrome":
                        browser = pw.chromium().launch(launchOptions.setChannel("chrome"));
                        break;
                    case "edge":
                        browser = pw.chromium().launch(launchOptions.setChannel("msedge"));
                        break;
                    case "safari":
                    case "webkit":
                        browser = pw.webkit().launch(launchOptions);
                        break;
                    case "firefox":
                        browser = pw.firefox().launch(launchOptions);
                        break;
                    case "chromium":
                    default:
                        browser = pw.chromium().launch(launchOptions);
                        break;
                }
                tlBrowser.set(browser);
                logger.info("Browser {} launched in {} mode", browserType, headless ? "headless" : "headed");
            } catch (Exception e) {
                logger.error("Failed to launch browser: {}", browserType, e);
                throw new RuntimeException("Browser launch failed", e);
            }
        }

        // Ensure a context exists for this thread
        BrowserContext ctx = tlContext.get();
        if (ctx == null) {
            Browser.NewContextOptions options = new Browser.NewContextOptions()
                    .setIgnoreHTTPSErrors(true)
                    .setViewportSize(
                            Integer.parseInt(ConfigReader.getProperty("viewport.width", "1280")),
                            Integer.parseInt(ConfigReader.getProperty("viewport.height", "720"))
                    )
                    // Grant microphone and camera permissions for live streaming
                    .setPermissions(Arrays.asList("microphone", "camera"));
            ctx = browser.newContext(options);
            tlContext.set(ctx);
            logger.info("New browser context created for thread {} (incognito: {})", Thread.currentThread().getName(), ConfigReader.isIncognito());

            // Optionally start tracing for Allure/diagnostics
            boolean traceEnabled = Boolean.parseBoolean(ConfigReader.getProperty("trace.enable", "true"));
            if (traceEnabled) {
                try {
                    ctx.tracing().start(new Tracing.StartOptions()
                            .setScreenshots(true)
                            .setSnapshots(true)
                            .setSources(true));
                    logger.info("Playwright tracing started for thread {}", Thread.currentThread().getName());
                } catch (Exception e) {
                    logger.warn("Failed to start tracing: {}", e.getMessage());
                }
            }
        }
    }

    public static BrowserContext getContext() {
        if (tlBrowser.get() == null || tlContext.get() == null) {
            initialize();
        }
        return tlContext.get();
    }

    public static Page getPage() {
        Page p = tlPage.get();
        if (p == null) {
            p = getContext().newPage();
            tlPage.set(p);
        }
        return p;
    }

    /**
     * Create a new isolated browser context with microphone and camera permissions.
     * Useful for tests that need multiple user sessions (e.g., creator + fan).
     */
    public static BrowserContext createNewContext() {
        Browser browser = tlBrowser.get();
        if (browser == null) {
            initialize();
            browser = tlBrowser.get();
        }
        Browser.NewContextOptions options = new Browser.NewContextOptions()
                .setIgnoreHTTPSErrors(true)
                .setViewportSize(
                        Integer.parseInt(ConfigReader.getProperty("viewport.width", "1280")),
                        Integer.parseInt(ConfigReader.getProperty("viewport.height", "720"))
                )
                .setPermissions(Arrays.asList("microphone", "camera"));
        BrowserContext newCtx = browser.newContext(options);
        logger.info("Created new isolated browser context with microphone and camera permissions");
        return newCtx;
    }

    public static void close() {
        // Close only this thread's page and context (and its browser/playwright)
        Page p = tlPage.get();
        if (p != null) {
            try {
                p.close();
            } catch (Exception ignore) {
            }
            tlPage.remove();
        }
        BrowserContext ctx = tlContext.get();
        if (ctx != null) {
            try {
                ctx.close();
            } catch (Exception ignore) {
            }
            logger.info("Browser context closed for thread {}", Thread.currentThread().getName());
            tlContext.remove();
        }
        Browser br = tlBrowser.get();
        if (br != null) {
            try {
                br.close();
            } catch (Exception ignore) {
            }
            logger.info("Browser closed for thread {}", Thread.currentThread().getName());
            tlBrowser.remove();
        }
        Playwright pw = tlPlaywright.get();
        if (pw != null) {
            try {
                pw.close();
            } catch (Exception ignore) {
            }
            logger.info("Playwright closed for thread {}", Thread.currentThread().getName());
            tlPlaywright.remove();
        }
    }
    // No closeAll() needed with per-thread resources
}