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
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        String firstName = ConfigReader.getProperty("fan.card.firstName", "QA");
        String lastName = ConfigReader.getProperty("fan.card.lastName", "Test");
        String cardNumber = ConfigReader.getProperty("fan.card.number", "4242 4242 4242 4242");
        String cardExpiry = ConfigReader.getProperty("fan.card.expiry", "12/30");
        String cardCvv = ConfigReader.getProperty("fan.card.cvv", "123");
        String holderFullName = firstName + " " + lastName;

        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

        FanSavedCardsPage cards = new FanSavedCardsPage(page);
        logger.info("[Saved Cards] Navigate to Settings > Saved Cards");
        cards.navigateToSavedCards();

        logger.info("[Saved Cards] Click Add a card and fill details");
        cards.clickAddCard();
        cards.fillFirstName(firstName);
        cards.fillLastName(lastName);
        cards.fillCardNumber(cardNumber);
        cards.fillExpiry(cardExpiry);
        cards.fillCvv(cardCvv);
        cards.acceptTermsIfPresent();

        logger.info("[Saved Cards] Submit Add new card");
        cards.submitAddCard();

        logger.info("[Saved Cards] Assert saved card visible by holder name: {}", holderFullName);
        cards.assertSavedCardVisible(holderFullName);
    }

    @Story("Delete an existing saved card in Settings > Saved Cards")
    @Test(priority = 2, description = "Navigate to Saved Cards, delete the saved card, and verify it's removed")
    public void fanDeleteSavedCard() {
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");
        String firstName = ConfigReader.getProperty("fan.card.firstName", "QA");
        String lastName = ConfigReader.getProperty("fan.card.lastName", "Test");
        String holderFullName = firstName + " " + lastName;

        FanLoginPage login = new FanLoginPage(page);
        login.navigate();
        login.login(fanUsername, fanPassword);

        FanSavedCardsPage cards = new FanSavedCardsPage(page);
        logger.info("[Saved Cards-Delete] Navigate to Settings > Saved Cards");
        cards.navigateToSavedCards();

        logger.info("[Saved Cards-Delete] Delete saved card for holder: {}", holderFullName);
        cards.deleteCard(holderFullName);

        logger.info("[Saved Cards-Delete] Verify card is not present");
        cards.assertCardNotPresent(holderFullName);
    }
}
