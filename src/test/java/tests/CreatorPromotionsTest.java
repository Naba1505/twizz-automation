package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BaseTestClass;
import pages.CreatorLoginPage;
import pages.CreatorPromotionsPage;
import utils.ConfigReader;

public class CreatorPromotionsTest extends BaseTestClass {

    @Test(priority = 1, description = "Creator can add a promo code with 10% discount on subscriptions (unlimited)")
    public void testAddPromoCode() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPromotionsPage promotions = new CreatorPromotionsPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Next: open settings and navigate to Promotions
        promotions.openSettingsFromProfile();
        promotions.openPromoCodeScreen();

        // Create promo
        promotions.clickCreatePromoCode();
        String uniqueCode = "AutomationPromoCodeA_" + System.currentTimeMillis();
        promotions.fillPromoCode(uniqueCode);
        promotions.fillDiscountPercent("10");
        promotions.selectSubscriptionUnlimited();
        promotions.submitCreate();

        // Assert toast messages
        promotions.assertPromoCreatedToasts();
    }

    @Test(priority = 2, description = "Creator can add a promo code for Media push / Collection with 7 days validity")
    public void testAddPromoCodeForMediaPush() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPromotionsPage promotions = new CreatorPromotionsPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Next: open settings and navigate to Promotions
        promotions.openSettingsFromProfile();
        promotions.openPromoCodeScreen();

        // Create promo with different applicability and validity
        promotions.clickCreatePromoCode();
        String uniqueCode = "AutomationPromoCodeB_" + System.currentTimeMillis();
        promotions.fillPromoCode(uniqueCode);
        promotions.fillDiscountPercent("10");
        promotions.selectApplicability("Media push / Collection");
        promotions.selectValidity("7 days");
        promotions.submitCreate();

        // Assert only the generic success toast for this flow
        promotions.assertPromoCreatedSuccessOnly();
    }

    @Test(priority = 3, description = "Creator can add a promo code with fixed euro discount for Subscription (unlimited)")
    public void testAddPromoCodeFixedAmount() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPromotionsPage promotions = new CreatorPromotionsPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Next: open settings and navigate to Promotions
        promotions.openSettingsFromProfile();
        promotions.openPromoCodeScreen();

        // Create promo with fixed amount (euro) discount
        promotions.clickCreatePromoCode();
        String uniqueCode = "AutomationPromoCodeC_" + System.currentTimeMillis();
        promotions.fillPromoCode(uniqueCode);
        promotions.fillDiscountAmount("5");
        promotions.selectSubscriptionUnlimited();
        promotions.submitCreate();

        // Assert both toasts for subscription flow
        promotions.assertPromoCreatedToasts();
    }

    @Test(priority = 4, description = "Creator can add a promo code with fixed euro discount for Media push / Collection (7 days)")
    public void testAddPromoCodeFixedAmountMediaPush() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPromotionsPage promotions = new CreatorPromotionsPage(page);

        // Act: login and land on profile
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        // Next: open settings and navigate to Promotions
        promotions.openSettingsFromProfile();
        promotions.openPromoCodeScreen();

        // Create promo with fixed amount for Media push / Collection and 7 days validity
        promotions.clickCreatePromoCode();
        String uniqueCode = "AutomationPromoCodeD_" + System.currentTimeMillis();
        promotions.fillPromoCode(uniqueCode);
        promotions.fillDiscountAmount("5");
        promotions.selectApplicability("Media push / Collection");
        promotions.selectValidity("7 days");
        promotions.submitCreate();

        // Assert only generic success toast for non-subscription flow
        promotions.assertPromoCreatedSuccessOnly();
    }

    @Test(priority = 5, description = "Creator clicks all 'Copy' buttons on Promo code page; assert success popup only for the last click")
    public void testCopyAllPromoLinks() {
        // Arrange
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        CreatorPromotionsPage promotions = new CreatorPromotionsPage(page);

        // Act: login and go to Promo code page
        loginPage.navigate();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Login header (logo/text) not visible on login screen");
        Assert.assertTrue(loginPage.isLoginFormVisible(), "Login form is not visible");
        loginPage.login(username, password);

        promotions.openSettingsFromProfile();
        promotions.openPromoCodeScreen(); // waits for exact title visibility

        // Click all Copy buttons and assert toast each time
        promotions.clickAllCopyButtonsAndAssert();
    }
}
