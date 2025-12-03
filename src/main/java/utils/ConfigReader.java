package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigReader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static Properties properties = new Properties();
    private static String env;

    static {
        try (InputStream input = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.error("Unable to find config.properties");
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
            env = properties.getProperty("environment", "dev").toLowerCase();
            if (!env.matches("dev|stage|prod")) {
                logger.error("Invalid environment: {}. Allowed values: dev, stage, prod", env);
                throw new RuntimeException("Invalid environment: " + env);
            }
            logger.info("Selected environment: {}", env);
            logger.info("Loaded properties: {}", properties);
        } catch (IOException e) {
            logger.error("Failed to load config.properties", e);
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getLandingPageUrl() {
        String url = properties.getProperty(env + ".landingPageUrl");
        if (url == null) {
            logger.error("Landing URL not found for environment: {}", env);
            throw new RuntimeException("Landing URL not found for environment: " + env);
        }
        logger.info("Loaded base URL: {}", url);
        return url;
    }

    public static String getCreatorRegistrationUrl() {
        String url = properties.getProperty(env + ".creatorRegistrationUrl");
        if (url == null) {
            logger.error("Creator Registration URL not found for environment: {}", env);
            throw new RuntimeException("Creator Registration URL not found for environment: " + env);
        }
        logger.info("Loaded Creator Registration URL: {}", url);
        return url;
    }

    /**
     * Returns the environment-specific login URL, falling back to base landing URL + /auth/signIn
     */
    public static String getLoginUrl() {
        String key = env + ".loginUrl";
        String url = properties.getProperty(key);
        if (url == null) {
            String base = properties.getProperty(env + ".landingPageUrl");
            if (base == null) {
                logger.error("Neither {} nor {} present in config.properties", key, env + ".landingPageUrl");
                throw new RuntimeException("Login URL not configured for environment: " + env);
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
        String key = env + ".fanSignupUrl";
        String url = properties.getProperty(key);
        if (url == null) {
            // Fallback to generic property if env-specific is not provided
            url = properties.getProperty("fan.signup.url");
        }
        if (url == null) {
            logger.error("Fan Signup URL not found for environment: {}", env);
            throw new RuntimeException("Fan Signup URL not found for environment: " + env);
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
        return env;
    }

    public static Properties getProperties() {
        return properties;
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
}