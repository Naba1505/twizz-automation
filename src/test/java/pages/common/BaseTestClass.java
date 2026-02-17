package pages.common;

import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import utils.BrowserFactory;
import utils.ConfigReader;
import utils.TearDownHelper;

public class BaseTestClass {
    protected Page page;
    protected LandingPage landingPage;

    @BeforeMethod
    public void setUp() {
        BrowserFactory.initialize();
        page = BrowserFactory.getPage();
        // Increase default timeouts for slow networks and heavy pages
        page.setDefaultNavigationTimeout(ConfigReader.getNavigationTimeout());
        page.setDefaultTimeout(ConfigReader.getDefaultTimeout());

        // Tracing is started in BrowserFactory.initialize() - no need to start again here

        String landingPageUrl = ConfigReader.getLandingPageUrl();
        // Robust navigation with waitUntil + extra load-state wait and a single retry
        try {
            page.navigate(landingPageUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
            // Some Playwright versions don't expose NETWORKIDLE reliably; LOAD is safer across versions
            page.waitForLoadState(LoadState.LOAD);
        } catch (Exception first) {
            // Retry once with a clean attempt
            try {
                page.navigate(landingPageUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
                page.waitForLoadState(LoadState.LOAD);
            } catch (Exception second) {
                throw first;
            }
        }
        landingPage = new LandingPage(page);
        // Ensure the landing page is fully interactive before tests
        try {
            landingPage.waitForPageToLoad();
        } catch (Exception e) {
            // do not swallow setup issues silently; rethrow to mark config failure
            throw e;
        }
        // Author metadata can be added to Allure via labels if desired
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        TearDownHelper.handleTestResult(page, result, "");
        BrowserFactory.close();
    }
}
