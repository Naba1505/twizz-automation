package tests.creator;

import org.testng.annotations.Test;
import pages.common.BaseTestClass;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorRevenuesPage;
import utils.ConfigReader;

public class CreatorRevenuesTest extends BaseTestClass {

    @Test(priority = 1, description = "Creator can view Revenues and info popovers")
    public void creatorCanViewRevenues() {
        // Arrange credentials
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        // Login and land on profile
        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        // Revenues flow
        CreatorRevenuesPage revenues = new CreatorRevenuesPage(page);
        revenues.openRevenues();
        revenues.assertRevenuesScreen();
        revenues.checkValidatedInfo();
        revenues.checkWaitingInfo();
    }

    @Test(priority = 2, description = "Revenues Today tab shows chart, correct title, bank receipt text and two prices")
    public void revenuesTodayChartBasics() {
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        CreatorRevenuesPage revenues = new CreatorRevenuesPage(page);
        revenues.openRevenues();
        revenues.viewToday();
    }

    @Test(priority = 3, description = "Revenues This week tab shows chart, correct title, bank receipt text and two prices")
    public void revenuesThisWeekChartBasics() {
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        CreatorRevenuesPage revenues = new CreatorRevenuesPage(page);
        revenues.openRevenues();
        revenues.viewThisWeek();
    }

    @Test(priority = 4, description = "Revenues This month tab shows chart, correct title, bank receipt text and two prices")
    public void revenuesThisMonthChartBasics() {
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        CreatorRevenuesPage revenues = new CreatorRevenuesPage(page);
        revenues.openRevenues();
        revenues.viewThisMonth();
    }

    @Test(priority = 5, description = "Revenues All tab shows chart, correct title, bank receipt text and two prices")
    public void revenuesAllChartBasics() {
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        CreatorRevenuesPage revenues = new CreatorRevenuesPage(page);
        revenues.openRevenues();
        revenues.viewAll();
    }

    @Test(priority = 6, description = "Revenues Last report and Filter flows (Daily, Monthly, Detailed) with scroll and validations")
    public void revenuesLastReportAndFilterFlows() {
        String username = ConfigReader.getProperty("creator.username", "TwizzCreator@proton.me");
        String password = ConfigReader.getProperty("creator.password", "Twizz$123");

        CreatorLoginPage loginPage = new CreatorLoginPage(page);
        loginPage.navigate();
        loginPage.login(username, password);

        CreatorRevenuesPage revenues = new CreatorRevenuesPage(page);
        revenues.openRevenues();
        // Execute the composite flow: scroll to Last report, change: Daily -> Monthly -> Detailed, run Filter options, return to top
        revenues.runLastReportAndFilterFlow();
    }
}
