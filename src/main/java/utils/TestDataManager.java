package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

/**
 * TestDataManager - Utility for sharing test data between tests.
 * Supports both ThreadLocal storage (for parallel execution) and file persistence (for cross-test communication).
 */
public class TestDataManager {
    private static final Logger logger = LoggerFactory.getLogger(TestDataManager.class);
    private static final String DATA_DIR = "target/test-data";

    // ThreadLocal storage for parallel execution
    private static final ThreadLocal<String> creatorUsername = new ThreadLocal<>();
    private static final ThreadLocal<String> approvedUsername = new ThreadLocal<>();
    private static final ThreadLocal<String> fanUsername = new ThreadLocal<>();

    // ==================== Creator Data ====================

    public static void saveCreatorUsername(String username) {
        creatorUsername.set(username);
        persistToFile("creator-username.txt", username);
        logger.info("Saved creator username: {}", username);
    }

    public static String getCreatorUsername() {
        String username = creatorUsername.get();
        if (username == null) {
            username = readFromFile("creator-username.txt");
            if (username != null) {
                creatorUsername.set(username);
            }
        }
        return username;
    }

    public static void clearCreatorUsername() {
        creatorUsername.remove();
        deleteFile("creator-username.txt");
    }

    // ==================== Approved Creator Data ====================

    public static void saveApprovedUsername(String username) {
        approvedUsername.set(username);
        persistToFile("approved-username.txt", username);
        logger.info("Saved approved username: {}", username);
    }

    public static String getApprovedUsername() {
        String username = approvedUsername.get();
        if (username == null) {
            username = readFromFile("approved-username.txt");
            if (username != null) {
                approvedUsername.set(username);
            }
        }
        return username;
    }

    public static void clearApprovedUsername() {
        approvedUsername.remove();
        deleteFile("approved-username.txt");
    }

    // ==================== Fan Data ====================

    public static void saveFanUsername(String username) {
        fanUsername.set(username);
        persistToFile("fan-username.txt", username);
        logger.info("Saved fan username: {}", username);
    }

    public static String getFanUsername() {
        String username = fanUsername.get();
        if (username == null) {
            username = readFromFile("fan-username.txt");
            if (username != null) {
                fanUsername.set(username);
            }
        }
        return username;
    }

    public static void clearFanUsername() {
        fanUsername.remove();
        deleteFile("fan-username.txt");
    }

    // ==================== Generic Key-Value Storage ====================

    public static void save(String key, String value) {
        persistToFile(key + ".txt", value);
        logger.info("Saved data with key '{}': {}", key, value);
    }

    public static String get(String key) {
        return readFromFile(key + ".txt");
    }

    public static void clear(String key) {
        deleteFile(key + ".txt");
    }

    // ==================== Private Helper Methods ====================

    private static void persistToFile(String filename, String value) {
        if (value == null) return;
        try {
            Path dir = Paths.get(DATA_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
            Path file = dir.resolve(filename);
            Files.writeString(file, value, StandardCharsets.UTF_8);
            logger.debug("Persisted to file: {}", file);
        } catch (Exception e) {
            logger.warn("Failed to persist data to file '{}': {}", filename, e.getMessage());
        }
    }

    private static String readFromFile(String filename) {
        try {
            Path file = Paths.get(DATA_DIR, filename);
            if (Files.exists(file)) {
                return Files.readString(file, StandardCharsets.UTF_8).trim();
            }
        } catch (Exception e) {
            logger.debug("Failed to read data from file '{}': {}", filename, e.getMessage());
        }
        return null;
    }

    private static void deleteFile(String filename) {
        try {
            Path file = Paths.get(DATA_DIR, filename);
            Files.deleteIfExists(file);
        } catch (Exception e) {
            logger.debug("Failed to delete file '{}': {}", filename, e.getMessage());
        }
    }

    // ==================== Cleanup ====================

    public static void clearAll() {
        creatorUsername.remove();
        approvedUsername.remove();
        fanUsername.remove();
        try {
            Path dir = Paths.get(DATA_DIR);
            if (Files.exists(dir)) {
                Files.walk(dir)
                    .sorted(java.util.Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.deleteIfExists(p);
                        } catch (Exception ignored) {}
                    });
            }
        } catch (Exception e) {
            logger.warn("Failed to clear all test data: {}", e.getMessage());
        }
        logger.info("Cleared all test data");
    }
}
