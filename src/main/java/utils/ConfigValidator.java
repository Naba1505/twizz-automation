package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.exceptions.ConfigurationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validates configuration properties to ensure they are correct before tests run.
 * This prevents runtime failures due to misconfiguration and provides clear error messages.
 */
public class ConfigValidator {
    private static final Logger logger = LoggerFactory.getLogger(ConfigValidator.class);
    private static final List<String> VALID_ENVIRONMENTS = Arrays.asList("dev", "stage", "prod");
    private static final List<String> VALID_BROWSERS = Arrays.asList("chrome", "chromium", "firefox", "webkit", "safari", "edge");
    
    /**
     * Validates all configuration properties.
     * Throws ConfigurationException if any validation fails.
     */
    public static void validate() {
        logger.info("Starting configuration validation...");
        List<String> errors = new ArrayList<>();
        
        try {
            validateEnvironment(errors);
            validateBrowser(errors);
            validateTimeouts(errors);
            validateUrls(errors);
            validateBooleans(errors);
            validateViewport(errors);
            validateRetrySettings(errors);
            
            if (!errors.isEmpty()) {
                String errorMessage = buildErrorMessage(errors);
                logger.error("Configuration validation failed with {} error(s)", errors.size());
                throw new ConfigurationException(errorMessage);
            }
            
            logger.info("Configuration validation passed successfully");
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during configuration validation", e);
            throw new ConfigurationException("Unexpected error during configuration validation: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates environment property
     */
    private static void validateEnvironment(List<String> errors) {
        String env = ConfigReader.getProperty("environment", "").toLowerCase();
        
        if (env.isEmpty()) {
            errors.add("Property 'environment' is missing or empty. Must be one of: " + VALID_ENVIRONMENTS);
            return;
        }
        
        if (!VALID_ENVIRONMENTS.contains(env)) {
            errors.add(String.format(
                "Property 'environment' has invalid value '%s'. Must be one of: %s",
                env, VALID_ENVIRONMENTS
            ));
        }
    }
    
    /**
     * Validates browser property
     */
    private static void validateBrowser(List<String> errors) {
        String browser = ConfigReader.getProperty("browser", "chrome").toLowerCase();
        
        if (!VALID_BROWSERS.contains(browser)) {
            errors.add(String.format(
                "Property 'browser' has invalid value '%s'. Must be one of: %s",
                browser, VALID_BROWSERS
            ));
        }
    }
    
    /**
     * Validates all timeout properties
     */
    private static void validateTimeouts(List<String> errors) {
        validatePositiveInteger("timeout.default", "60000", errors);
        validatePositiveInteger("timeout.short", "10000", errors);
        validatePositiveInteger("timeout.medium", "30000", errors);
        validatePositiveInteger("timeout.long", "120000", errors);
        validatePositiveInteger("timeout.navigation", "60000", errors);
        validatePositiveInteger("timeout.visibility", "20000", errors);
        validatePositiveInteger("timeout.animation", "500", errors);
        validatePositiveInteger("timeout.ui.settle", "1000", errors);
        validatePositiveInteger("timeout.page.load", "2000", errors);
        validatePositiveInteger("timeout.poll.interval", "100", errors);
    }
    
    /**
     * Validates environment-specific URLs
     */
    private static void validateUrls(List<String> errors) {
        String env = ConfigReader.getProperty("environment", "stage").toLowerCase();
        
        // Validate main app URLs
        validateUrl(env + ".landingPageUrl", errors);
        validateUrl(env + ".creatorRegistrationUrl", errors);
        validateUrl(env + ".fanSignupUrl", errors);
        validateUrl(env + ".loginUrl", errors);
        
        // Validate business app URLs
        validateUrl(env + ".business.landingPageUrl", errors);
        validateUrl(env + ".business.loginUrl", errors);
        validateUrl(env + ".business.registerUrl", errors);
        
        // Validate admin URL
        validateUrl(env + ".admin.baseUrl", errors);
    }
    
    /**
     * Validates a single URL property
     */
    private static void validateUrl(String propertyKey, List<String> errors) {
        String url = ConfigReader.getProperty(propertyKey, "");
        
        if (url.isEmpty()) {
            errors.add(String.format(
                "Property '%s' is missing or empty. Must be a valid URL starting with http:// or https://",
                propertyKey
            ));
            return;
        }
        
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            errors.add(String.format(
                "Property '%s' has invalid value '%s'. Must start with http:// or https://",
                propertyKey, url
            ));
            return;
        }
        
        try {
            new URI(url);
        } catch (URISyntaxException e) {
            errors.add(String.format(
                "Property '%s' has malformed URL '%s': %s",
                propertyKey, url, e.getMessage()
            ));
        }
    }
    
    /**
     * Validates boolean properties
     */
    private static void validateBooleans(List<String> errors) {
        validateBoolean("headless", "false", errors);
        validateBoolean("incognito", "true", errors);
        validateBoolean("trace.enable", "true", errors);
        validateBoolean("screenshot.on.success", "false", errors);
        validateBoolean("trace.export.on.success", "false", errors);
    }
    
    /**
     * Validates a single boolean property
     */
    private static void validateBoolean(String propertyKey, String defaultValue, List<String> errors) {
        String value = ConfigReader.getProperty(propertyKey, defaultValue).toLowerCase();
        
        if (!value.equals("true") && !value.equals("false")) {
            errors.add(String.format(
                "Property '%s' has invalid value '%s'. Must be 'true' or 'false'",
                propertyKey, value
            ));
        }
    }
    
    /**
     * Validates viewport dimensions
     */
    private static void validateViewport(List<String> errors) {
        validatePositiveInteger("viewport.width", "1280", errors);
        validatePositiveInteger("viewport.height", "720", errors);
        
        // Check reasonable ranges
        try {
            int width = Integer.parseInt(ConfigReader.getProperty("viewport.width", "1280"));
            int height = Integer.parseInt(ConfigReader.getProperty("viewport.height", "720"));
            
            if (width < 320 || width > 7680) {
                errors.add(String.format(
                    "Property 'viewport.width' value %d is out of reasonable range (320-7680)",
                    width
                ));
            }
            
            if (height < 240 || height > 4320) {
                errors.add(String.format(
                    "Property 'viewport.height' value %d is out of reasonable range (240-4320)",
                    height
                ));
            }
        } catch (NumberFormatException e) {
            // Already caught by validatePositiveInteger
        }
    }
    
    /**
     * Validates retry settings
     */
    private static void validateRetrySettings(List<String> errors) {
        validateNonNegativeInteger("retry.max", "2", errors);
        validateNonNegativeInteger("retry.delay.ms", "0", errors);
        validatePositiveInteger("element.retry.max", "3", errors);
        validateNonNegativeInteger("element.retry.delay", "200", errors);
    }
    
    /**
     * Validates a property is a positive integer (> 0)
     */
    private static void validatePositiveInteger(String propertyKey, String defaultValue, List<String> errors) {
        String value = ConfigReader.getProperty(propertyKey, defaultValue);
        
        try {
            int intValue = Integer.parseInt(value);
            if (intValue <= 0) {
                errors.add(String.format(
                    "Property '%s' must be a positive integer (> 0), found: %d",
                    propertyKey, intValue
                ));
            }
        } catch (NumberFormatException e) {
            errors.add(String.format(
                "Property '%s' must be a valid integer, found: '%s'",
                propertyKey, value
            ));
        }
    }
    
    /**
     * Validates a property is a non-negative integer (>= 0)
     */
    private static void validateNonNegativeInteger(String propertyKey, String defaultValue, List<String> errors) {
        String value = ConfigReader.getProperty(propertyKey, defaultValue);
        
        try {
            int intValue = Integer.parseInt(value);
            if (intValue < 0) {
                errors.add(String.format(
                    "Property '%s' must be a non-negative integer (>= 0), found: %d",
                    propertyKey, intValue
                ));
            }
        } catch (NumberFormatException e) {
            errors.add(String.format(
                "Property '%s' must be a valid integer, found: '%s'",
                propertyKey, value
            ));
        }
    }
    
    /**
     * Builds a formatted error message from the list of errors
     */
    private static String buildErrorMessage(List<String> errors) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("  CONFIGURATION VALIDATION FAILED\n");
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("\n");
        sb.append(String.format("Found %d error(s) in config.properties:\n\n", errors.size()));
        
        for (int i = 0; i < errors.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, errors.get(i)));
        }
        
        sb.append("\n");
        sb.append("Please fix these errors in src/main/resources/config.properties\n");
        sb.append("and try again.\n");
        sb.append("═══════════════════════════════════════════════════════════════\n");
        
        return sb.toString();
    }
}
