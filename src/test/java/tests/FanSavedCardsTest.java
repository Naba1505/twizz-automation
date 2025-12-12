package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.FanLoginPage;
import pages.FanSavedCardsPage;
import utils.ConfigReader;

@Epic("Fan")
@Feature("Saved Cards")
public class FanSavedCardsTest extends BaseTestClass {
    private static final Logger logger = LoggerFactory.getLogger(FanSavedCardsTest.class);

    @Story("Create/Save a new card in Settings > Saved Cards")
    @Test(priority = 1, description = "Navigate to Saved Cards, add a new card, and verify it's visible")
    public void fanAddSavedCard() {
        // Arrange
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        String firstName = ConfigReader.getProperty("fan.card.firstName", "Test");
        String lastName = ConfigReader.getProperty("fan.card.lastName", "Card");
        String cardNumber = ConfigReader.getProperty("fan.card.number", "4242 4242 4242 4242");
        String cardExpiry = ConfigReader.getProperty("fan.card.expiry", "08/30");
        String cardCvv = ConfigReader.getProperty("fan.card.cvv", "314");
        String holderFullName = firstName + " " + lastName;

        logger.info("[Saved Cards] Starting test: Add saved card with holder name '{}'", holderFullName);

        // Act - Login to Fan account and land on Home screen
        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        logger.info("[Saved Cards] Logging in as Fan user: {}", fanUsername);
        login.login(fanUsername, fanPassword);
        logger.info("[Saved Cards] Successfully logged in and landed on Fan home screen");

        // Navigate to Settings > Saved Cards
        FanSavedCardsPage cards = new FanSavedCardsPage(page);
        logger.info("[Saved Cards] Navigating to Settings > Saved Cards");
        cards.navigateToSavedCards();

        // Click Add a card button
        logger.info("[Saved Cards] Clicking 'Add a card' button");
        cards.clickAddCard();

        // Assert on Card information screen
        logger.info("[Saved Cards] Verifying 'Card information' heading is visible");
        cards.assertOnCardInformationScreen();

        // Fill card details
        logger.info("[Saved Cards] Filling card details - First name: {}, Last name: {}", firstName, lastName);
        cards.fillFirstName(firstName);
        cards.fillLastName(lastName);

        logger.info("[Saved Cards] Filling card number: {}", cardNumber);
        cards.fillCardNumber(cardNumber);

        logger.info("[Saved Cards] Filling card expiry: {}", cardExpiry);
        cards.fillExpiry(cardExpiry);

        logger.info("[Saved Cards] Filling card CVV");
        cards.fillCvv(cardCvv);

        logger.info("[Saved Cards] Accepting terms and conditions if present");
        cards.acceptTermsIfPresent();

        // Submit Add new card
        logger.info("[Saved Cards] Submitting 'Add new card'");
        cards.submitAddCard();

        // Assert - Verify card is saved and visible
        logger.info("[Saved Cards] Verifying saved card is visible with holder name: {}", holderFullName);
        cards.assertSavedCardVisible(holderFullName);
        logger.info("[Saved Cards] Test passed: Card '{}' successfully added and visible", holderFullName);
    }

    @Story("Delete an existing saved card in Settings > Saved Cards")
    @Test(priority = 2, description = "Navigate to Saved Cards, delete the saved card, and verify it's removed", dependsOnMethods = "fanAddSavedCard")
    public void fanDeleteSavedCard() {
        // Arrange
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        String firstName = ConfigReader.getProperty("fan.card.firstName", "Test");
        String lastName = ConfigReader.getProperty("fan.card.lastName", "Card");
        String holderFullName = firstName + " " + lastName;

        logger.info("[Saved Cards-Delete] Starting test: Delete saved card with holder name '{}'", holderFullName);

        // Act - Login to Fan account
        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        logger.info("[Saved Cards-Delete] Logging in as Fan user: {}", fanUsername);
        login.login(fanUsername, fanPassword);
        logger.info("[Saved Cards-Delete] Successfully logged in and landed on Fan home screen");

        // Navigate to Settings > Saved Cards
        FanSavedCardsPage cards = new FanSavedCardsPage(page);
        logger.info("[Saved Cards-Delete] Navigating to Settings > Saved Cards");
        cards.navigateToSavedCards();

        // Delete the saved card
        logger.info("[Saved Cards-Delete] Deleting saved card for holder: {}", holderFullName);
        cards.deleteCard(holderFullName);

        // Assert - Verify card is removed
        logger.info("[Saved Cards-Delete] Verifying card is no longer present");
        cards.assertCardNotPresent(holderFullName);
        logger.info("[Saved Cards-Delete] Test passed: Card '{}' successfully deleted", holderFullName);
    }
}
