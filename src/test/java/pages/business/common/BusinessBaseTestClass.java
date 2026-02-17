package pages.business.common;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import pages.business.employee.BusinessEmployeeLoginPage;
import pages.business.employee.BusinessEmployeeSignUpPage;
import pages.business.employee.EmployeeSettingsPage;
import pages.business.manager.BusinessManagerAddCreatorPage;
import pages.business.manager.BusinessManagerAddEmployeePage;
import pages.business.manager.BusinessManagerDeleteCreatorPage;
import pages.business.manager.BusinessManagerDeleteEmployeePage;
import pages.business.manager.BusinessManagerLanguagePage;
import pages.business.manager.BusinessManagerLoginPage;
import pages.business.manager.BusinessManagerSettingsPage;
import pages.business.manager.BusinessManagerSignUpPage;
import pages.creator.CreatorLoginPage;
import pages.creator.CreatorManagerPage;
import utils.BrowserFactory;
import utils.ConfigReader;
import utils.TearDownHelper;

/**
 * Base Test Class for Twizz Business App tests
 * Provides setup/teardown for Business app testing
 */
public class BusinessBaseTestClass {
    protected Page page;
    protected BusinessLandingPage businessLandingPage;
    protected BusinessManagerSignUpPage businessManagerSignUpPage;
    protected BusinessManagerLoginPage businessManagerLoginPage;
    protected BusinessManagerAddCreatorPage businessManagerAddCreatorPage;
    protected BusinessManagerAddEmployeePage businessManagerAddEmployeePage;
    protected BusinessManagerDeleteCreatorPage businessManagerDeleteCreatorPage;
    protected BusinessManagerDeleteEmployeePage businessManagerDeleteEmployeePage;
    protected BusinessManagerLanguagePage businessManagerLanguagePage;
    protected BusinessManagerSettingsPage businessManagerSettingsPage;
    protected BusinessEmployeeSignUpPage businessEmployeeSignUpPage;
    protected BusinessEmployeeLoginPage businessEmployeeLoginPage;
    protected EmployeeSettingsPage employeeSettingsPage;
    protected CreatorLoginPage creatorLoginPage;
    protected CreatorManagerPage creatorManagerPage;

    @BeforeMethod
    public void setUp() {
        BrowserFactory.initialize();
        page = BrowserFactory.getPage();
        page.setDefaultNavigationTimeout(ConfigReader.getNavigationTimeout());
        page.setDefaultTimeout(ConfigReader.getDefaultTimeout());

        String landingPageUrl = ConfigReader.getBusinessLandingPageUrl();
        try {
            page.navigate(landingPageUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            page.waitForLoadState(LoadState.LOAD);
        } catch (Exception first) {
            try {
                page.navigate(landingPageUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                page.waitForLoadState(LoadState.LOAD);
            } catch (Exception second) {
                throw first;
            }
        }
        businessLandingPage = new BusinessLandingPage(page);
        businessManagerSignUpPage = new BusinessManagerSignUpPage(page);
        businessManagerLoginPage = new BusinessManagerLoginPage(page);
        businessManagerAddCreatorPage = new BusinessManagerAddCreatorPage(page);
        businessManagerAddEmployeePage = new BusinessManagerAddEmployeePage(page);
        businessManagerDeleteCreatorPage = new BusinessManagerDeleteCreatorPage(page);
        businessManagerDeleteEmployeePage = new BusinessManagerDeleteEmployeePage(page);
        businessManagerLanguagePage = new BusinessManagerLanguagePage(page);
        businessManagerSettingsPage = new BusinessManagerSettingsPage(page);
        businessEmployeeSignUpPage = new BusinessEmployeeSignUpPage(page);
        businessEmployeeLoginPage = new BusinessEmployeeLoginPage(page);
        employeeSettingsPage = new EmployeeSettingsPage(page);
        creatorLoginPage = new CreatorLoginPage(page);
        creatorManagerPage = new CreatorManagerPage(page);
        businessLandingPage.waitForPageToLoad();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        TearDownHelper.handleTestResult(page, result, "Business_");
        BrowserFactory.close();
    }
}
