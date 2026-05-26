package utils.exceptions;

/**
 * Exception thrown when configuration validation fails.
 * This is a runtime exception that indicates invalid or missing configuration properties.
 */
public class ConfigurationException extends RuntimeException {
    
    public ConfigurationException(String message) {
        super(message);
    }
    
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
