package tests.creator;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorRegistrationPage;
import utils.ConfigReader;
import utils.DataGenerator;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class CreatorRegistrationTest extends BaseTestClass {
    public static String createdUsername;
    @Test(priority = 1, description = "Complete creator registration flow using valid dynamic data", groups = {"registration.completed"})
    public void testCreatorRegistration() {
        // Initialize page object (BaseTestClass sets up 'page')
        CreatorRegistrationPage creatorRegistrationPage = new CreatorRegistrationPage(page);

        // Dynamic test data
        String firstName = DataGenerator.generateUniqueFirstName();
        String lastName = DataGenerator.generateUniqueLastName();
        String name = firstName + " " + lastName;
        String username = DataGenerator.generateUniqueUsername("TwizzCreator");
        createdUsername = username;
        String email = DataGenerator.generateUniqueEmail("TwizzCreator");
        String password = DataGenerator.generateRandomString(12);
        String phone = DataGenerator.generateRandomPhoneNumber();
        String instagramUrl = ConfigReader.getProperty("registration.instagramUrl", "https://www.instagram.com/");

        // Configurable fields (with safe defaults)
        String dob = ConfigReader.getProperty("registration.dob", "01-01-1995"); // dd-MM-yyyy
        String gender = ConfigReader.getProperty("registration.gender", "Female");
        String subscriptionPrice = ConfigReader.getProperty("registration.subscriptionPrice", "9.99");
        String contentTypesProp = ConfigReader.getProperty("registration.contentTypes", "Artist,Model");
        String[] contentTypes = java.util.Arrays.stream(contentTypesProp.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        // Test resources (images for upload)
        String identityPath = java.nio.file.Paths.get("src", "test", "resources", "Images", "Identity.png")
                .toAbsolutePath().toString();
        String selfiePath = java.nio.file.Paths.get("src", "test", "resources", "Images", "Selfie.jpg")
                .toAbsolutePath().toString();

        // Execute complete registration flow (throws if any step fails)
        creatorRegistrationPage.completeRegistrationFlow(
                name,
                username,
                firstName,
                lastName,
                dob,
                email,
                password,
                phone,
                instagramUrl,
                gender,
                contentTypes,
                subscriptionPrice,
                identityPath,
                selfiePath
        );

        // Explicitly verify final confirmation message
        Assert.assertTrue(creatorRegistrationPage.isFinalConfirmationVisible(),
                "Final confirmation message 'Thank you for your interest!' was not visible");

        // Persist created username for downstream tests (e.g., Admin approval) across separate Maven runs
        try {
            Path outDir = Paths.get("target");
            if (!Files.exists(outDir)) {
                Files.createDirectories(outDir);
            }
            Path outFile = outDir.resolve("created-username.txt");
            Files.writeString(outFile, createdUsername, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
        }
    }
}
