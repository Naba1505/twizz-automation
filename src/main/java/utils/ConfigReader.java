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
}