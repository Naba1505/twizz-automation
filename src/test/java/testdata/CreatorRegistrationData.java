package testdata;

import utils.ConfigReader;
import utils.DataGenerator;
import utils.TestAssets;

/**
 * Immutable data holder for creator registration test data.
 * Generates all required data in one place.
 */
public class CreatorRegistrationData {
    public final String firstName;
    public final String lastName;
    public final String name;
    public final String username;
    public final String email;
    public final String password;
    public final String phone;
    public final String instagramUrl;
    public final String dob;
    public final String gender;
    public final String subscriptionPrice;
    public final String[] contentTypes;
    public final String identityPath;
    public final String selfiePath;

    private CreatorRegistrationData() {
        this.firstName = DataGenerator.generateUniqueFirstName();
        this.lastName = DataGenerator.generateUniqueLastName();
        this.name = firstName + " " + lastName;
        this.username = DataGenerator.generateUniqueUsername("TwizzCreator");
        this.email = DataGenerator.generateUniqueEmail("TwizzCreator");
        this.password = DataGenerator.generateRandomString(12);
        this.phone = DataGenerator.generateRandomPhoneNumber();
        this.instagramUrl = ConfigReader.getProperty("registration.instagramUrl", "https://www.instagram.com/");
        this.dob = ConfigReader.getProperty("registration.dob", "01-01-1995");
        this.gender = ConfigReader.getProperty("registration.gender", "Female");
        this.subscriptionPrice = ConfigReader.getProperty("registration.subscriptionPrice", "9.99");
        this.contentTypes = parseContentTypes(ConfigReader.getProperty("registration.contentTypes", "Artist,Model"));
        this.identityPath = TestAssets.imagePath("Identity.png");
        this.selfiePath = TestAssets.imagePath("Selfie.jpg");
    }

    public static CreatorRegistrationData generate() {
        return new CreatorRegistrationData();
    }

    private static String[] parseContentTypes(String contentTypesProp) {
        return java.util.Arrays.stream(contentTypesProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
    }
}
