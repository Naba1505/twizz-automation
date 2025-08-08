package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class DataGenerator {
    private static final Logger logger = LoggerFactory.getLogger(DataGenerator.class);
    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");

    public static String generateUniqueUsername(String prefix) {
        String timestamp = dateFormat.format(new Date());
        String uniqueUsername = prefix + timestamp;
        logger.info("Generated unique username: {}", uniqueUsername);
        return uniqueUsername;
    }

    public static String generateUniqueEmail(String prefix) {
        String domain = ConfigReader.getProperty("email.domain", "test.com");
        String timestamp = dateFormat.format(new Date());
        String uniqueEmail = prefix + timestamp + "@" + domain;
        logger.info("Generated unique email: {}", uniqueEmail);
        return uniqueEmail;
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALPHA_NUMERIC.charAt(random.nextInt(ALPHA_NUMERIC.length())));
        }
        String randomString = sb.toString();
        logger.info("Generated random string: {}", randomString);
        return randomString;
    }

    public static String generateRandomPhoneNumber() {
        Random random = new Random();
        StringBuilder phoneNumber = new StringBuilder("9");
        for (int i = 0; i < 9; i++) {
            phoneNumber.append(random.nextInt(10));
        }
        String phone = phoneNumber.toString();
        logger.info("Generated random phone number: {}", phone);
        return phone;
    }

    public static String generateUniqueFirstName() {
        String timestamp = dateFormat.format(new Date());
        String firstName = "First" + timestamp;
        logger.info("Generated unique first name: {}", firstName);
        return firstName;
    }

    public static String generateUniqueLastName() {
        String timestamp = dateFormat.format(new Date());
        String lastName = "Last" + timestamp;
        logger.info("Generated unique last name: {}", lastName);
        return lastName;
    }
}