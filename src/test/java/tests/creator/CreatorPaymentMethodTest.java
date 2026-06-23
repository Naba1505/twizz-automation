package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorPaymentMethodPage;

public class CreatorPaymentMethodTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Verify Payment Method - Add Bank Account")
    public void creatorCanAddBankAccount() {
        CreatorPaymentMethodPage pmPage = new CreatorPaymentMethodPage(page);

        // Use real bank data; make bankName, SWIFT, and IBAN unique per run
        String uniq = "_" + System.currentTimeMillis();
        String bankName = "State Bank of India (SBI)" + uniq;
        String swift = "SBININBBXXX" + uniq;
        String iban = "IN29SBIN000000" + generate11DigitNumber();
        String countryQuery = "Indi";
        String countryExact = "India";
        String address = "123, MG Road, Mumbai, India";
        String postal = "400001";
        String city = "Mumbai";

        // Open Settings and ensure URL contains settings path
        pmPage.openSettingsFromProfile();
        pmPage.assertOnSettingsUrl();

        // Open Payment method screen
        pmPage.openPaymentMethodScreen();

        // Click Add an account
        pmPage.clickAddAnAccount();

        // Fill details
        pmPage.fillBankAccountDetails(bankName, swift, iban, countryQuery, countryExact, address, postal, city);

        // Submit
        pmPage.submitAddMethod();

        // Optional: navigate back to profile
        pmPage.navigateBackToProfile();
    }

    @Test(priority = 2, description = "Verify Payment Method - Set bank account as default")
    public void creatorCanSetBankAccountAsDefault() {
        CreatorPaymentMethodPage pmPage = new CreatorPaymentMethodPage(page);

        // Open Settings and ensure URL contains settings path
        pmPage.openSettingsFromProfile();
        pmPage.assertOnSettingsUrl();

        // Open Payment method screen
        pmPage.openPaymentMethodScreen();

        // Open the added RevoCard and set as default
        pmPage.openAddedCard();
        pmPage.assertOnPaymentCardScreen();
        pmPage.setAsDefault();
    }

    @Test(priority = 3, description = "Verify Payment Method - Delete bank account")
    public void creatorCanDeleteBankAccount() {
        CreatorPaymentMethodPage pmPage = new CreatorPaymentMethodPage(page);

        // Open Settings and ensure URL contains settings path
        pmPage.openSettingsFromProfile();
        pmPage.assertOnSettingsUrl();

        // Open Payment method screen
        pmPage.openPaymentMethodScreen();

        // Open the added RevoCard and delete it with confirmation
        pmPage.openAddedCard();
        pmPage.assertOnPaymentCardScreen();
        pmPage.deleteCurrentCard();
        pmPage.waitAfterDelete();
    }

    @Test(priority = 4, description = "Verify Payment Method - Switch deposit durations")
    public void creatorCanSwitchDepositDurations() {
        CreatorPaymentMethodPage pmPage = new CreatorPaymentMethodPage(page);

        // Open Settings and ensure URL contains settings path
        pmPage.openSettingsFromProfile();
        pmPage.assertOnSettingsUrl();

        // Open Payment method screen
        pmPage.openPaymentMethodScreen();

        // Ensure initial guidance text
        pmPage.ensureInitialDepositTextVisible();

        // Switch to Every 7 days
        pmPage.switchDepositEvery7DaysAndConfirm();

        // Switch to On pause
        pmPage.switchDepositOnPauseAndConfirm();

        // Switch to Every 30 days (default)
        pmPage.switchDepositEvery30DaysAndConfirm();
    }

    // Helper: generate unique 11-digit number (no leading zero)
    private String generate11DigitNumber() {
        long n = java.util.concurrent.ThreadLocalRandom.current()
                .nextLong(10_000_000_000L, 100_000_000_000L); // [10^10, 10^11)
        return Long.toString(n);
    }
}
