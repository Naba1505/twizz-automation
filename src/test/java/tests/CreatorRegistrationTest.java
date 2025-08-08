package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorRegistrationPage;
import utils.ConfigReader;
import utils.DataGenerator;

public class CreatorRegistrationTest extends BaseTestClass {

    @Test(priority = 1, description = "Complete creator registration flow using valid dynamic data")
    public void testCreatorRegistration() {
        // Initialize page object (BaseTestClass sets up 'page')
        CreatorRegistrationPage creatorRegistrationPage = new CreatorRegistrationPage(page);

        // Dynamic test data
        String firstName = DataGenerator.generateUniqueFirstName();
        String lastName = DataGenerator.generateUniqueLastName();
        String name = firstName + " " + lastName;
        String username = DataGenerator.generateUniqueUsername("TwizzCreator");
        String email = DataGenerator.generateUniqueEmail("TwizzCreator");
        String password = DataGenerator.generateRandomString(12);
        String phone = DataGenerator.generateRandomPhoneNumber();

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
                gender,
                contentTypes,
                subscriptionPrice,
                identityPath,
                selfiePath
        );

        // Explicitly verify final confirmation message
        Assert.assertTrue(creatorRegistrationPage.isFinalConfirmationVisible(),
                "Final confirmation message 'Thank you for your interest!' was not visible");
    }
}
