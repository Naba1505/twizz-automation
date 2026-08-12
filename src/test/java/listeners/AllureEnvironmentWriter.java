package listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IExecutionListener;
import utils.ConfigReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * TestNG listener that writes environment information to Allure reports.
 * This provides context about the test execution environment in the Allure report.
 */
public class AllureEnvironmentWriter implements IExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(AllureEnvironmentWriter.class);

    @Override
    public void onExecutionStart() {
        logger.info("Writing environment properties for Allure report");
        writeEnvironmentProperties();
    }

    @Override
    public void onExecutionFinish() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::snapshotCurrentResults, "allure-snapshot"));
    }

    /**
     * Copies the current run's allure-results to a timestamped snapshot directory
     * so every run is permanently preserved for future reference.
     * <p>
     * The snapshot is written outside {@code target/} (default: project-root
     * {@code allure-history/}) so that {@code mvn clean} cannot delete historical
     * results. Override the location with {@code -Dallure.history.dir=<path>}.
     */
    private void snapshotCurrentResults() {
        try {
            String allureResultsPath = System.getProperty("allure.results.directory", "target/allure-results");
            Path resultsDir = Paths.get(allureResultsPath);
            if (!Files.exists(resultsDir) || !Files.isDirectory(resultsDir)) {
                return;
            }
            try (var files = Files.list(resultsDir)) {
                if (files.findAny().isEmpty()) return;
            }
            String timestamp = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String historyDirPath = System.getProperty("allure.history.dir", "allure-history");
            Path snapshotDir = Paths.get(historyDirPath, "allure-results_" + timestamp);
            Files.createDirectories(snapshotDir);
            try (var files = Files.list(resultsDir)) {
                files.forEach(file -> {
                    try {
                        Files.copy(file, snapshotDir.resolve(file.getFileName()),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        logger.warn("Failed to copy allure result file '{}': {}", file, e.getMessage());
                    }
                });
            }
            logger.info("Snapshot of current allure-results saved to: {}", snapshotDir);
        } catch (Exception e) {
            logger.warn("Failed to snapshot current allure-results: {}", e.getMessage());
        }
    }

    /**
     * Writes environment.properties file to allure-results directory.
     * This file is picked up by Allure and displayed in the report.
     */
    private void writeEnvironmentProperties() {
        Properties envProperties = new Properties();

        try {
            // Environment information
            envProperties.setProperty("Environment", ConfigReader.getEnvironment().toUpperCase());
            envProperties.setProperty("Base URL", ConfigReader.getLandingPageUrl());
            
            // Browser information
            envProperties.setProperty("Browser", ConfigReader.getBrowserType());
            envProperties.setProperty("Headless Mode", String.valueOf(ConfigReader.isHeadless()));
            envProperties.setProperty("Incognito Mode", String.valueOf(ConfigReader.isIncognito()));
            
            // System information
            envProperties.setProperty("Java Version", System.getProperty("java.version"));
            envProperties.setProperty("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            envProperties.setProperty("OS Architecture", System.getProperty("os.arch"));
            
            // Framework information
            envProperties.setProperty("Playwright Version", "1.54.0");
            envProperties.setProperty("TestNG Version", "7.11.0");
            
            // Test configuration
            String author = System.getProperty("user.name", ConfigReader.getProperty("author", "Unknown"));
            envProperties.setProperty("Test Executed By", author);
            envProperties.setProperty("Default Timeout", ConfigReader.getDefaultTimeout() + "ms");
            envProperties.setProperty("Retry Max Count", ConfigReader.getProperty("retry.max", "2"));
            
            // Viewport information
            envProperties.setProperty("Viewport", 
                ConfigReader.getProperty("viewport.width", "1280") + "x" + 
                ConfigReader.getProperty("viewport.height", "720"));

            // Create allure-results directory if it doesn't exist
            // Use system property if set, otherwise default to target/allure-results
            String allureResultsPath = System.getProperty("allure.results.directory", "target/allure-results");
            Path allureResultsDir = Paths.get(allureResultsPath);
            if (!Files.exists(allureResultsDir)) {
                Files.createDirectories(allureResultsDir);
                logger.info("Created allure-results directory: {}", allureResultsDir);
            }

            // Write environment.properties file
            Path envPropertiesPath = allureResultsDir.resolve("environment.properties");
            try (FileOutputStream fos = new FileOutputStream(envPropertiesPath.toFile())) {
                envProperties.store(fos, "Allure Environment Properties - Generated by AllureEnvironmentWriter");
                logger.info("Successfully wrote environment properties to: {}", envPropertiesPath);
            }

        } catch (IOException e) {
            logger.error("Failed to write environment properties for Allure", e);
            // Don't fail the test execution, just log the error
        } catch (Exception e) {
            logger.error("Unexpected error while writing environment properties", e);
        }
    }
}
