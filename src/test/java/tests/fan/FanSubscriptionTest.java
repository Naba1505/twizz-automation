package tests.fan;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import pages.fan.FanSubscriptionPage;
import utils.ConfigReader;
import utils.TestDataManager;
import tests.admin.AdminApproveCreatorTest;

public class FanSubscriptionTest extends BaseFanTest {
    private static final Logger logger = LoggerFactory.getLogger(FanSubscriptionTest.class);

    @Test(priority = 1, description = "Fan subscribes to a creator from search with 3DS flow")
    public void fanCanSubscribeToCreator() {
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
            } catch (IOException | RuntimeException e) {
                logger.debug("Failed to read approved username file: {}", e.getMessage());
            }
        }
        if (candidate == null || candidate.isBlank()) {
            // Fallback 2: try freshly created username from registration flow (TestDataManager handles file fallback)
            candidate = TestDataManager.getCreatorUsername();
        }
        if (candidate == null || candidate.isBlank()) {
            // Final fallback: configured username
            candidate = ConfigReader.getProperty("approval.username", "twizzcreator20251030121513143");
        }
        String creatorUsername = candidate != null && !candidate.isBlank() ? candidate : ConfigReader.getProperty("approval.username", "twizzcreator20251030121513143");

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
            sub.handlePaymentScreen(cardNumber, cardExpiry, cardCvc);
            sub.selectCountryIfNeeded();
            sub.confirmAndComplete3DS();
        }
        // For FREE creators, subscription is already complete
        sub.assertSubscriberVisible();
    }
}
