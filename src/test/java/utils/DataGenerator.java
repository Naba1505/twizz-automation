package utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

final class TestDataGenerator {
    private static final SecureRandom RAND = new SecureRandom();
    private static final String ALPHA = "abcdefghijklmnopqrstuvwxyz";
    private static final String ALPHANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private TestDataGenerator() {}

    public static String generateUniqueFirstName() {
        return cap("auto" + randLetters(5));
    }

    public static String generateUniqueLastName() {
        return cap("user" + randLetters(6));
    }

    public static String generateUniqueUsername(String prefix) {
        String base = prefix == null || prefix.isEmpty() ? "user" : prefix;
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return sanitize(base) + "_" + ts + randDigits(3);
    }

    public static String generateUniqueEmail(String prefix) {
        String base = prefix == null || prefix.isEmpty() ? "user" : prefix;
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return sanitize(base).toLowerCase(Locale.ROOT) + "+" + ts + randDigits(3) + "@automation.com";
    }

    public static String generateRandomString(int length) {
        int len = Math.max(6, Math.min(64, length));
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPHANUM.charAt(RAND.nextInt(ALPHANUM.length())));
        return sb.toString();
    }

    public static String generateRandomPhoneNumber() {
        // Simple 10-digit numeric string starting with non-zero
        StringBuilder sb = new StringBuilder(10);
        sb.append(1 + RAND.nextInt(9));
        for (int i = 1; i < 10; i++) sb.append(RAND.nextInt(10));
        return sb.toString();
    }

    private static String cap(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String randLetters(int n) {
        int len = Math.max(1, n);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(ALPHA.charAt(RAND.nextInt(ALPHA.length())));
        return sb.toString();
    }

    private static String randDigits(int n) {
        int len = Math.max(1, n);
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) sb.append(RAND.nextInt(10));
        return sb.toString();
    }

    private static String sanitize(String s) {
        return s.replaceAll("[^A-Za-z0-9]", "");
    }
}
