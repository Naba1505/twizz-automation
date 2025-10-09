package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorPaymentMethodPage;
import utils.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CreatorPaymentMethodTest extends BaseTestClass {
    private static final Logger log = LoggerFactory.getLogger(CreatorPaymentMethodTest.class);

    @Test(priority = 1, description = "Verify Payment Method - Add Bank Account")
    public void creatorCanAddBankAccount() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPaymentMethodPage pmPage = new CreatorPaymentMethodPage(page);

        // Use real bank data; make IBAN unique by appending 11 random digits
        String bankName = "State Bank of India (SBI)";
        String swift = "SBININBBXXX";
        String iban = "IN29SBIN000000" + generate11DigitNumber();
        String countryQuery = "Indi";
        String countryExact = "India";
        String address = "123, MG Road, Mumbai, India";
        String postal = "400001";
        String city = "Mumbai";

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Open Settings and ensure URL contains settings path
        pmPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Open Payment method screen
        pmPage.openPaymentMethodScreen();

        // Click Add an account
        pmPage.clickAddAnAccount();

        // Fill details
        pmPage.fillBankAccountDetails(bankName, swift, iban, countryQuery, countryExact, address, postal, city);

        // Submit
        pmPage.submitAddMethod();

        // Assert success + card
        pmPage.assertSuccessAndCardVisible();

        // Optional: navigate back to profile
        pmPage.navigateBackToProfile();
    }

    @Test(priority = 2, description = "Verify Payment Method - Set bank account as default")
    public void creatorCanSetBankAccountAsDefault() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPaymentMethodPage pmPage = new CreatorPaymentMethodPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Open Settings and ensure URL contains settings path
        pmPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Open Payment method screen
        pmPage.openPaymentMethodScreen();

        // Open the added RevoCard and set as default
        pmPage.openAddedCard();
        pmPage.assertOnPaymentCardScreen();
        pmPage.setAsDefault();
    }

    @Test(priority = 3, description = "Verify Payment Method - Delete bank account")
    public void creatorCanDeleteBankAccount() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPaymentMethodPage pmPage = new CreatorPaymentMethodPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Open Settings and ensure URL contains settings path
        pmPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

        // Open Payment method screen
        pmPage.openPaymentMethodScreen();

        // Open the added RevoCard and delete it with confirmation
        pmPage.openAddedCard();
        pmPage.assertOnPaymentCardScreen();
        pmPage.deleteCurrentCard();
        // Wait 5 seconds after deleting to allow UI/state to settle
        try { page.waitForTimeout(5000); } catch (Exception ignored) {}
    }

    @Test(priority = 4, description = "Verify Payment Method - Switch deposit durations")
    public void creatorCanSwitchDepositDurations() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPaymentMethodPage pmPage = new CreatorPaymentMethodPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Open Settings and ensure URL contains settings path
        pmPage.openSettingsFromProfile();
        String settingsUrl = page.url();
        log.info("Settings URL after click: {}", settingsUrl);
        Assert.assertTrue(settingsUrl.contains("/common/setting"), "Did not land on Settings screen");

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
