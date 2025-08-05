package tests;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.BrowserFactory;
import utils.ConfigReader;

public class ConfigReaderTest {
    private Page page;

    @BeforeClass
    public void setUp() {
        BrowserFactory.initialize();
        page = BrowserFactory.getPage();
    }

    @Test
    public void testConfigReader() {
        String baseUrl = ConfigReader.getBaseUrl();
        System.out.println("Environment: " + ConfigReader.getEnvironment());
        System.out.println("Base URL: " + baseUrl);
        System.out.println("Browser: " + ConfigReader.getBrowserType());
        System.out.println("Headless: " + ConfigReader.isHeadless());
        System.out.println("Incognito: " + ConfigReader.isIncognito());

        Assert.assertNotNull(baseUrl, "Base URL should not be null");
        Assert.assertFalse(baseUrl.isEmpty(), "Base URL should not be empty");
        Assert.assertEquals(baseUrl, "https://stg.twizz.app", "Base URL should match Stage environment");

        // Navigate to verify browser
        page.navigate(baseUrl);
        Assert.assertEquals(page.url(), baseUrl + "/", "Navigation should succeed");
    }

    @AfterClass
    public void tearDown() {
        BrowserFactory.close();
    }
}