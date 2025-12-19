package tests;

import java.io.IOException;

import org.testng.annotations.Test;

import pages.BaseTestClass;
import pages.FanLoginPage;
import pages.FanSubscriptionPage;
import utils.ConfigReader;

public class FanSubscriptionTest extends BaseTestClass {

    @Test(priority = 1, description = "Fan subscribes to a creator from search with 3DS flow")
    public void fanCanSubscribeToCreator() {
        // Arrange: credentials
        String fanUsername = ConfigReader.getProperty("fan.username", "TwizzFan@proton.me");
        String fanPassword = ConfigReader.getProperty("fan.password", "Twizz$123");

        // Resolve the creator username from previous steps (prefer most recent approved)
        String candidate = AdminApproveCreatorTest.approvedUsername;
        if (candidate == null || candidate.isBlank()) {
            // Fallback 1: read from approved-username.txt (persisted by admin approval test)
            try {
                java.nio.file.Path p = java.nio.file.Paths.get("target", "approved-username.txt");
                if (java.nio.file.Files.exists(p)) {
                    String fileVal = java.nio.file.Files.readString(p, java.nio.charset.StandardCharsets.UTF_8).trim();
                    if (!fileVal.isBlank()) { candidate = fileVal; }
                }
            } catch (IOException | RuntimeException ignored) {}
        }
        if (candidate == null || candidate.isBlank()) {
            // Fallback 2: try freshly created username from registration flow (same JVM run)
            candidate = CreatorRegistrationTest.createdUsername;
        }
        if (candidate == null || candidate.isBlank()) {
            // Fallback 3: read from created-username.txt (persisted by registration test)
            try {
                java.nio.file.Path p = java.nio.file.Paths.get("target", "created-username.txt");
                if (java.nio.file.Files.exists(p)) {
                    String fileVal = java.nio.file.Files.readString(p, java.nio.charset.StandardCharsets.UTF_8).trim();
                    if (!fileVal.isBlank()) { candidate = fileVal; }
                }
            } catch (IOException | RuntimeException ignored) {}
        }
        if (candidate == null || candidate.isBlank()) {
            // Final fallback: configured username
            candidate = ConfigReader.getProperty("approval.username", "twizzcreator20251030121513143");
        }
        String creatorUsername = candidate != null && !candidate.isBlank() ? candidate : ConfigReader.getProperty("approval.username", "twizzcreator20251030121513143");

        // Fan login and land on home
        FanLoginPage fanLogin = new FanLoginPage(page);
        fanLogin.navigate();
        fanLogin.login(fanUsername, fanPassword);

        // Subscription flow via search
        FanSubscriptionPage sub = new FanSubscriptionPage(page);
        sub.openSearchPanel();
        sub.searchAndOpenCreator(creatorUsername);
        
        // startSubscriptionFlow returns true if payment is needed, false if FREE subscription completed
        boolean paymentNeeded = sub.startSubscriptionFlow();
        
        if (paymentNeeded) {
            // Paid creator - complete payment flow
            String cardNumber = ConfigReader.getProperty("payment.card.number", "4012 0018 0000 0016");
            String cardExpiry = ConfigReader.getProperty("payment.card.expiry", "07/34");
            String cardCvc = ConfigReader.getProperty("payment.card.cvc", "657");
            sub.fillCardDetails(cardNumber, cardExpiry, cardCvc);
            sub.selectCountryIfNeeded();
            sub.confirmAndComplete3DS();
        }
        // For FREE creators, subscription is already complete
        sub.assertSubscriberVisible();
    }
}
