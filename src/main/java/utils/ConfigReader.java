package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static final Properties properties;
    private static final String ENVIRONMENT;

    static {
        Properties tempProperties = new Properties();
        String tempEnv;
        
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.error("Unable to find config.properties");
                throw new RuntimeException("Unable to find config.properties");
            }
            tempProperties.load(input);
            tempEnv = tempProperties.getProperty("environment", "dev").toLowerCase();
            if (!tempEnv.matches("dev|stage|prod")) {
                logger.error("Invalid environment: {}. Allowed values: dev, stage, prod", tempEnv);
                throw new RuntimeException("Invalid environment: " + tempEnv);
            }
            logger.info("Selected environment: {}", tempEnv);
            logger.info("Loaded properties: {}", tempProperties);
        } catch (IOException e) {
            logger.error("Failed to load config.properties", e);
            throw new RuntimeException("Failed to load config.properties", e);
        }
        
        // Assign to final static variables
        properties = tempProperties;
        ENVIRONMENT = tempEnv;
    }

    public static String getLandingPageUrl() {
        String url = properties.getProperty(ENVIRONMENT + ".landingPageUrl");
        if (url == null) {
            logger.error("Landing URL not found for environment: {}", ENVIRONMENT);
            throw new RuntimeException("Landing URL not found for environment: " + ENVIRONMENT);
        }
        logger.info("Loaded base URL: {}", url);
        return url;
    }

    public static String getCreatorRegistrationUrl() {
        String url = properties.getProperty(ENVIRONMENT + ".creatorRegistrationUrl");
        if (url == null) {
            logger.error("Creator Registration URL not found for environment: {}", ENVIRONMENT);
            throw new RuntimeException("Creator Registration URL not found for environment: " + ENVIRONMENT);
        }
        logger.info("Loaded Creator Registration URL: {}", url);
        return url;
    }

    /**
     * Returns the environment-specific login URL, falling back to base landing URL + /auth/signIn
     */
    public static String getLoginUrl() {
        String key = ENVIRONMENT + ".loginUrl";
        String url = properties.getProperty(key);
        if (url == null) {
            String base = properties.getProperty(ENVIRONMENT + ".landingPageUrl");
            if (base == null) {
                logger.error("Neither {} nor {} present in config.properties", key, ENVIRONMENT + ".landingPageUrl");
                throw new RuntimeException("Login URL not configured for environment: " + ENVIRONMENT);
            }
            if (base.endsWith("/")) {
                base = base.substring(0, base.length() - 1);
            }
            url = base + "/auth/signIn";
        }
        logger.info("Loaded Login URL: {}", url);
        return url;
    }

    public static String getFanSignupUrl() {
        String key = ENVIRONMENT + ".fanSignupUrl";
        String url = properties.getProperty(key);
        if (url == null) {
            // Fallback to generic property if ENVIRONMENT-specific is not provided
            url = properties.getProperty("fan.signup.url");
        }
        if (url == null) {
            logger.error("Fan Signup URL not found for environment: {}", ENVIRONMENT);
            throw new RuntimeException("Fan Signup URL not found for environment: " + ENVIRONMENT);
        }
        logger.info("Loaded Fan Signup URL: {}", url);
        return url;
    }

    public static String getBrowserType() {
        String browser = properties.getProperty("browser", "chromium").toLowerCase();
        if (!browser.matches("chrome|chromium|firefox|webkit|safari|edge")) {
            logger.warn("Invalid browser type: {}, defaulting to chromium", browser);
            return "chromium";
        }
        logger.info("Loaded browser type: {}", browser);
        return browser;
    }

    public static boolean isHeadless() {
        String headless = properties.getProperty("headless", "true").toLowerCase();
        logger.info("Loaded headless mode: {}", headless);
        return Boolean.parseBoolean(headless);
    }

    public static boolean isIncognito() {
        String incognito = properties.getProperty("incognito", "false").toLowerCase();
        logger.info("Loaded incognito mode: {}", incognito);
        return Boolean.parseBoolean(incognito);
    }

    public static String getProperty(String key, String defaultValue) {
        // Prefer JVM system properties (e.g., -Dapproval.username=foo) over config.properties
        String sysVal = System.getProperty(key);
        if (sysVal != null) {
            logger.info("Loaded property {} from system: {}", key, sysVal);
            return sysVal;
        }
        String value = properties.getProperty(key, defaultValue);
        logger.info("Loaded property {}: {}", key, value);
        return value;
    }

    public static String getEnvironment() {
        return ENVIRONMENT;
    }

    public static Properties getProperties() {
        return properties;
    }

    /**
     * Returns the environment-aware base URL (e.g. https://stg.twizz.app) with no trailing slash.
     * Use this to build dynamic URLs: ConfigReader.getBaseUrl() + "/creator/profile"
     */
    public static String getBaseUrl() {
        String url = getLandingPageUrl();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    // ===== Centralized Timeout Constants =====

    /**
     * Default timeout for most operations (60 seconds)
     */
    public static int getDefaultTimeout() {
        return Integer.parseInt(getProperty("timeout.default", "60000"));
    }

    /**
     * Short timeout for quick checks (10 seconds)
     */
    public static int getShortTimeout() {
        return Integer.parseInt(getProperty("timeout.short", "10000"));
    }

    /**
     * Medium timeout for moderate waits like toasts, confirmations (30 seconds)
     */
    public static int getMediumTimeout() {
        return Integer.parseInt(getProperty("timeout.medium", "30000"));
    }

    /**
     * Long timeout for slow operations like uploads (120 seconds)
     */
    public static int getLongTimeout() {
        return Integer.parseInt(getProperty("timeout.long", "120000"));
    }

    /**
     * Navigation timeout (60 seconds)
     */
    public static int getNavigationTimeout() {
        return Integer.parseInt(getProperty("timeout.navigation", "60000"));
    }

    /**
     * Element visibility wait timeout (20 seconds)
     */
    public static int getVisibilityTimeout() {
        return Integer.parseInt(getProperty("timeout.visibility", "20000"));
    }

    /**
     * Animation timeout for UI transitions (500ms)
     */
    public static int getAnimationTimeout() {
        return Integer.parseInt(getProperty("timeout.animation", "500"));
    }

    /**
     * UI settling timeout (1 second)
     */
    public static int getUiSettleTimeout() {
        return Integer.parseInt(getProperty("timeout.ui.settle", "1000"));
    }

    /**
     * Page load timeout (2 seconds)
     */
    public static int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("timeout.page.load", "2000"));
    }

    /**
     * Polling interval for wait operations (100ms)
     */
    public static int getPollInterval() {
        return Integer.parseInt(getProperty("timeout.poll.interval", "100"));
    }

    /**
     * Maximum scroll attempts for finding elements
     */
    public static int getMaxScrollAttempts() {
        return Integer.parseInt(getProperty("scroll.max.attempts", "15"));
    }

    /**
     * Scroll step size in pixels
     */
    public static int getScrollStepSize() {
        return Integer.parseInt(getProperty("scroll.step.size", "500"));
    }

    /**
     * Wait between scroll attempts (300ms)
     */
    public static int getScrollWaitBetween() {
        return Integer.parseInt(getProperty("scroll.wait.between", "300"));
    }

    /**
     * Maximum retry attempts for element interactions
     */
    public static int getElementRetryMax() {
        return Integer.parseInt(getProperty("element.retry.max", "3"));
    }

    /**
     * Delay between element retry attempts (200ms)
     */
    public static int getElementRetryDelay() {
        return Integer.parseInt(getProperty("element.retry.delay", "200"));
    }

    // ===== Twizz Business App URLs =====

    /**
     * Returns the environment-specific Business landing page URL
     */
    public static String getBusinessLandingPageUrl() {
        String key = ENVIRONMENT + ".business.landingPageUrl";
        String url = properties.getProperty(key);
        if (url == null) {
            logger.error("Business Landing URL not found for environment: {}", ENVIRONMENT);
            throw new RuntimeException("Business Landing URL not found for environment: " + ENVIRONMENT);
        }
        logger.info("Loaded Business Landing URL: {}", url);
        return url;
    }

    /**
     * Returns the environment-specific Business login URL
     */
    public static String getBusinessLoginUrl() {
        String key = ENVIRONMENT + ".business.loginUrl";
        String url = properties.getProperty(key);
        if (url == null) {
            String base = getBusinessLandingPageUrl();
            url = base + "/auth/signIn";
        }
        logger.info("Loaded Business Login URL: {}", url);
        return url;
    }

    /**
     * Returns the environment-specific Business register URL
     */
    public static String getBusinessRegisterUrl() {
        String key = ENVIRONMENT + ".business.registerUrl";
        String url = properties.getProperty(key);
        if (url == null) {
            String base = getBusinessLandingPageUrl();
            url = base + "/auth/signUp";
        }
        logger.info("Loaded Business Register URL: {}", url);
        return url;
    }
}