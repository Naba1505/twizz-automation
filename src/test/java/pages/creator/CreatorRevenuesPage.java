package pages.creator;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

public class CreatorRevenuesPage extends BasePage {
    // Selectors provided by user
    private static final String SELECTOR_CURRENCY_IMG = "div.currency-img";
    private static final String SELECTOR_VALIDATED_PRICE = "div span.ant-typography.font-32-bold.text-white-color.mr-10.css-ixblex";
    private static final String SELECTOR_WAITING_PRICE = "div span.ant-typography.font-24-bold.text-white-color.mr-10.css-ixblex";
    // Chart selectors
    private static final String SELECTOR_CHART_CONTAINER = "div.ant-row.chart-container.css-ixblex";
    private static final String SELECTOR_CHART_PRICE_TEXT = "div span.ant-typography.chart-price-text.css-ixblex";
    // Last report selectors
    private static final String SELECTOR_LAST_REPORT_CONTENT = "div.ant-row.last-report-content.css-ixblex";
    private static final String SELECTOR_DROPDOWN_UL = "ul.ant-dropdown-menu.ant-dropdown-menu-root.ant-dropdown-menu-vertical.ant-dropdown-menu-light.css-ixblex";
    // Filter dropdown (container -> ul -> li)
    private static final String SELECTOR_FILTER_DROPDOWN_CONTAINER = "div.ant-dropdown.css-ixblex.ant-dropdown-placement-topLeft";
    private static final String SELECTOR_FILTER_DROPDOWN_UL = SELECTOR_FILTER_DROPDOWN_CONTAINER + " ul";

    public CreatorRevenuesPage(Page page) {
        super(page);
    }

    private Locator revenuesIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Revenues icon"));
    }

    private Locator revenuesTitle() {
        return page.getByText("Revenues");
    }

    private Locator currencyImg() {
        return page.locator(SELECTOR_CURRENCY_IMG);
    }

    private Locator validatedPrice() {
        return page.locator(SELECTOR_VALIDATED_PRICE);
    }

    private Locator waitingPrice() {
        return page.locator(SELECTOR_WAITING_PRICE);
    }

    private Locator validatedInfoIcon() {
        return page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Validated for the next payment$")))
                .getByRole(AriaRole.IMG, new Locator.GetByRoleOptions().setName("info"));
    }

    private Locator waitingInfoIcon() {
        return page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(Pattern.compile("^Waiting for validation$")))
                .getByRole(AriaRole.IMG, new Locator.GetByRoleOptions().setName("info"));
    }

    private Locator infoCloseButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Close"));
    }

    private Locator validatedInfoText() {
        return page.getByText("Earnings are confirmed and will be paid to you with your next payment.");
    }

    private Locator waitingInfoText() {
        // Narrow CSS to avoid strict mode violation and uniquely match the modal description
        return page.locator("div span.ant-typography.info-modal-description.css-ixblex");
    }

    @Step("Open Revenues from dashboard")
    public void openRevenues() {
        logger.info("[Revenues] Waiting for Revenues icon to be visible");
        waitVisible(revenuesIcon(), DEFAULT_WAIT);
        logger.info("[Revenues] Clicking Revenues icon");
        clickWithRetry(revenuesIcon(), 1, 200);
        logger.info("[Revenues] Waiting for Revenues title to confirm navigation");
        waitVisible(revenuesTitle(), DEFAULT_WAIT);
        logger.info("[Revenues] Landed on Revenues screen");
    }

    @Step("Assert Revenues screen elements are visible")
    public void assertRevenuesScreen() {
        logger.info("[Revenues] Asserting screen elements: title, currency logo, validated & waiting price blocks");
        waitVisible(revenuesTitle(), DEFAULT_WAIT);
        waitVisible(currencyImg(), DEFAULT_WAIT);
        waitVisible(validatedPrice(), DEFAULT_WAIT);
        waitVisible(waitingPrice(), DEFAULT_WAIT);
        logger.info("[Revenues] Revenues screen elements are visible");
    }

    @Step("Check Validated info popover")
    public void checkValidatedInfo() {
        logger.info("[Revenues] Opening Validated info popover");
        clickWithRetry(validatedInfoIcon(), 1, 200);
        waitVisible(validatedInfoText(), DEFAULT_WAIT);
        logger.info("[Revenues] Validated info text is visible; closing popover");
        clickWithRetry(infoCloseButton(), 1, 200);
    }

    @Step("Check Waiting for validation info popover")
    public void checkWaitingInfo() {
        logger.info("[Revenues] Opening Waiting for validation info popover");
        clickWithRetry(waitingInfoIcon(), 1, 200);
        waitVisible(waitingInfoText(), DEFAULT_WAIT);
        logger.info("[Revenues] Waiting info text is visible; closing popover");
        clickWithRetry(infoCloseButton(), 1, 200);
    }

    // =========================
    // Tabs and chart assertions
    // =========================

    private Locator tabByText(String name) {
        return page.getByText(name, new Page.GetByTextOptions().setExact(true));
    }

    private Locator chartContainer() {
        return page.locator(SELECTOR_CHART_CONTAINER);
    }

    private Locator chartPriceTexts() {
        return page.locator(SELECTOR_CHART_PRICE_TEXT);
    }

    @Step("Select 'Today' tab and verify chart basics")
    public void viewToday() {
        logger.info("[Revenues] Selecting Today tab");
        waitVisible(tabByText("Today"), DEFAULT_WAIT);
        clickWithRetry(tabByText("Today"), 1, 150);
        assertChartVisible();
        assertChartTitle("Total of the day");
        ensureReceiptInBankAccountVisibleAndClick();
        assertTwoPricesVisible();
    }

    @Step("Select 'This week' tab and verify chart basics")
    public void viewThisWeek() {
        logger.info("[Revenues] Selecting This week tab");
        waitVisible(tabByText("This week"), DEFAULT_WAIT);
        clickWithRetry(tabByText("This week"), 1, 150);
        assertChartVisible();
        assertChartTitle("Total of the week");
        ensureReceiptInBankAccountVisibleAndClick();
        assertTwoPricesVisible();
    }

    @Step("Select 'This month' tab and verify chart basics")
    public void viewThisMonth() {
        logger.info("[Revenues] Selecting This month tab");
        waitVisible(tabByText("This month"), DEFAULT_WAIT);
        clickWithRetry(tabByText("This month"), 1, 150);
        assertChartVisible();
        assertChartTitle("Total of the month");
        ensureReceiptInBankAccountVisibleAndClick();
        assertTwoPricesVisible();
    }

    @Step("Select 'All' tab and verify chart basics")
    public void viewAll() {
        logger.info("[Revenues] Selecting All tab");
        waitVisible(tabByText("All"), DEFAULT_WAIT);
        clickWithRetry(tabByText("All"), 1, 150);
        assertChartVisible();
        assertChartTitle("Total since the creation");
        ensureReceiptInBankAccountVisibleAndClick();
        assertTwoPricesVisible();
    }

    @Step("Assert chart container is visible")
    public void assertChartVisible() {
        waitVisible(chartContainer(), DEFAULT_WAIT);
        logger.info("[Revenues] Chart container visible");
    }

    @Step("Assert chart title text: {title}")
    public void assertChartTitle(String title) {
        waitVisible(page.getByText(title), DEFAULT_WAIT);
        logger.info("[Revenues] Chart title visible: {}", title);
    }

    @Step("Ensure 'Receipt in your bank account' appears and click it")
    public void ensureReceiptInBankAccountVisibleAndClick() {
        Locator txt = page.getByText("Receipt in your bank account");
        waitVisible(txt, DEFAULT_WAIT);
        clickWithRetry(txt, 1, 150);
        logger.info("[Revenues] Clicked 'Receipt in your bank account'");
    }

    @Step("Assert two price texts are displayed on the chart")
    public void assertTwoPricesVisible() {
        Locator prices = chartPriceTexts();
        // Wait briefly until at least two appear
        long end = System.currentTimeMillis() + DEFAULT_WAIT;
        while (System.currentTimeMillis() < end && prices.count() < 2) {
            try { page.waitForTimeout(150); } catch (Exception ignored) {}
        }
        int count = prices.count();
        logger.info("[Revenues] Chart price text count: {}", count);
        if (count < 2) {
            throw new AssertionError("Expected 2 price texts on chart, found: " + count);
        }
        // Ensure first two are visible
        waitVisible(prices.nth(0), DEFAULT_WAIT);
        waitVisible(prices.nth(1), DEFAULT_WAIT);
    }

    // =========================
    // Last Report + Filters
    // =========================

    private Locator lastReportTitle() {
        return page.getByText("Last report", new Page.GetByTextOptions().setExact(true));
    }

    private Locator lastReportContent() {
        return page.locator(SELECTOR_LAST_REPORT_CONTENT);
    }

    private Locator changeIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("change"));
    }

    private Locator changeIconByXPath() {
        return page.locator("xpath=//div//img[@alt='change'][1]");
    }

    private Locator dropdownMenu() {
        // Use the provided UL selector and select the intended instance when multiple are present.
        // If there are 3 or more visible menus, prefer index 2 (0-based nth(2)). If 2, prefer nth(1). Else first.
        Locator menus = page.locator(SELECTOR_DROPDOWN_UL);
        int count = menus.count();
        if (count >= 3) return menus.nth(2);
        if (count >= 2) return menus.nth(1);
        return menus.first();
    }

    private Locator filterDropdownMenu() {
        // Target the exact filter dropdown container then its UL
        return page.locator(SELECTOR_FILTER_DROPDOWN_UL);
    }

    private Locator dropdownItemByText(String text) {
        // li item under the dropdown ul with exact text
        return dropdownMenu().locator("li").filter(new Locator.FilterOptions().setHasText(text));
    }

    private void scrollIntoViewWithAttempts(Locator target, int attempts, int deltaY) {
        int tries = Math.max(1, attempts);
        for (int i = 0; i < tries; i++) {
            try {
                if (target.first().isVisible()) return;
            } catch (Exception ignored) {}
            try { target.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            try { page.mouse().wheel(0, deltaY); } catch (Exception ignored) {}
            try { page.waitForTimeout(150); } catch (Exception ignored) {}
        }
    }

    @Step("Scroll to 'Last report' and ensure content visible")
    public void scrollToLastReportAndEnsureContent() {
        Locator title = lastReportTitle();
        // Attempt multiple scrolls until the title is visible
        scrollIntoViewWithAttempts(title, 10, 800);
        waitVisible(title, DEFAULT_WAIT);
        // Ensure the content container under last report is visible
        waitVisible(lastReportContent(), DEFAULT_WAIT);
        logger.info("[Revenues] 'Last report' title and content visible");
    }

    @Step("Open report type change dropdown")
    public void openChangeDropdown() {
        waitVisible(changeIcon(), DEFAULT_WAIT);
        clickWithRetry(changeIcon(), 1, 120);
        // Wait for the UL dropdown menu to appear
        waitVisible(dropdownMenu(), DEFAULT_WAIT);
        logger.info("[Revenues] Change dropdown opened");
    }

    @Step("Select report type: {type}")
    public void selectReportType(String type) {
        // Ensure dropdown is open
        if (dropdownMenu().count() == 0) {
            openChangeDropdown();
        }
        Locator item = dropdownItemByText(type);
        waitVisible(item.first(), DEFAULT_WAIT);
        clickWithRetry(item.first(), 1, 120);
        // After selection, dropdown typically closes; re-validate content still visible
        waitVisible(lastReportContent(), DEFAULT_WAIT);
        logger.info("[Revenues] Selected report type: {} and content visible", type);
    }

    @Step("Open Filter dropdown")
    public void openFilterDropdown() {
        // Ensure we are in the Last report area where Filter resides
        try { scrollToLastReportAndEnsureContent(); } catch (Exception ignored) {}

        Locator filter = getFilterActivator();
        // Try to bring it into view and click
        try { filter.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        waitVisible(filter.first(), DEFAULT_WAIT);
        try {
            clickWithRetry(filter.first(), 1, 150);
        } catch (RuntimeException e) {
            // Fallback: force click if overlapped
            try { filter.first().click(new Locator.ClickOptions().setForce(true)); } catch (Exception ignored) { throw e; }
        }
        // Wait for the specific filter dropdown
        waitVisible(filterDropdownMenu(), DEFAULT_WAIT);
        logger.info("[Revenues] Filter dropdown opened");
    }

    private void openFilterOrChangeDropdown() {
        // Prefer Filter if present/visible; else use change icon as fallback (per user spec)
        try {
            Locator filter = getFilterActivator();
            if (filter != null && filter.count() > 0 && safeIsVisible(filter.first())) {
                openFilterDropdown();
                return;
            }
        } catch (Exception ignored) {}
        // Fallback: use change icon (XPath) to reveal the list
        try { scrollToLastReportAndEnsureContent(); } catch (Exception ignored) {}
        Locator change = changeIconByXPath();
        if (change.count() == 0 || !change.first().isVisible()) {
            // try role-based as secondary
            change = changeIcon();
        }
        waitVisible(change.first(), DEFAULT_WAIT);
        try { change.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        clickWithRetry(change.first(), 1, 150);
        waitVisible(filterDropdownMenu(), DEFAULT_WAIT);
        logger.info("[Revenues] Opened dropdown via change icon");
    }

    private Locator getFilterActivator() {
        // Prefer a role button named Filter if present
        Locator byRole = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Filter"));
        if (byRole.count() > 0) return byRole;
        // Then try exact text 'Filter'
        Locator byTextExact = page.getByText("Filter", new Page.GetByTextOptions().setExact(true));
        if (byTextExact.count() > 0) return byTextExact;
        // Then try contains text (case-insensitive)
        Locator byTextCi = page.getByText(java.util.regex.Pattern.compile("\\bFilter\\b", java.util.regex.Pattern.CASE_INSENSITIVE));
        if (byTextCi.count() > 0) return byTextCi;
        // As a last resort, look within the last report content area
        Locator within = lastReportContent().getByText("Filter");
        if (within.count() > 0) return within;
        // Fallback to any element with title or aria-label Filter
        Locator byAttr = page.locator("[title='Filter'], [aria-label='Filter']");
        if (byAttr.count() > 0) return byAttr;
        // Default to global text which may become visible after scroll
        return byTextExact;
    }

    @Step("Iterate through all Filter options and click each")
    public void iterateFilterOptionsAndClickAll() {
        // Click in explicit order from bottom value up to top as requested
        String[] order = new String[]{
                "Decrypt",
                "Live",
                "Private medias",
                "Medias push",
                "Collection",
                "Stream",
                "Monthly subscription",
                "All"
        };
        for (String label : order) {
            // From second time, Filter may vanish; open via change icon if needed
            openFilterOrChangeDropdown();
            Locator list = filterDropdownMenu().locator("li");
            // Prefer exact text match within the li content
            Locator opt = list.filter(new Locator.FilterOptions().setHasText(label));
            // Ensure visibility; try scroll into view if needed
            if (opt.count() == 0) {
                // As a fallback, try case-insensitive contains using regex
                opt = list.filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile(java.util.regex.Pattern.quote(label), java.util.regex.Pattern.CASE_INSENSITIVE)));
            }
            if (opt.count() == 0) {
                throw new RuntimeException("Filter option not found: " + label);
            }
            Locator target = opt.first();
            try { target.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            waitVisible(target, DEFAULT_WAIT);
            clickWithRetry(target, 1, 120);
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }
        logger.info("[Revenues] Iterated filter options in requested order");
    }

    @Step("Scroll to top until 'Revenues' title visible")
    public void scrollToTopUntilRevenuesVisible() {
        Locator title = revenuesTitle();
        int guard = 0;
        while (!safeIsVisible(title) && guard++ < 20) {
            try { page.mouse().wheel(0, -800); } catch (Exception ignored) {}
            try { page.waitForTimeout(150); } catch (Exception ignored) {}
        }
        waitVisible(title, DEFAULT_WAIT);
        logger.info("[Revenues] Scrolled to top; 'Revenues' title visible");
    }

    @Step("Run Last Report and Filter flow (Daily, Monthly, Detailed)")
    public void runLastReportAndFilterFlow() {
        // 1) Scroll to Last report and ensure content
        scrollToLastReportAndEnsureContent();

        // 2) Use codegen-like flow for the "Change" dropdown inside Last report
        //    page.locator("div").filter(new Locator.FilterOptions().setHasText("Mensuel")).nth(5).click();
        logger.info("[Revenues] Opening 'Change' dropdown via Mensuel div inside Last report (codegen flow)");
        Locator mensuelDiv = page.locator("div").filter(new Locator.FilterOptions().setHasText("Mensuel"));
        if (mensuelDiv.count() == 0) {
            throw new RuntimeException("Could not locate Mensuel div for Last report change dropdown");
        }
        Locator mensuelTarget = mensuelDiv.nth(Math.min(5, mensuelDiv.count() - 1));
        try { mensuelTarget.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        waitVisible(mensuelTarget, DEFAULT_WAIT);
        clickWithRetry(mensuelTarget, 1, 150);

        // 2a) Then click Daily -> Journalier -> Monthly -> Mensuel -> Detailed, as per codegen
        logger.info("[Revenues] Selecting 'Daily' then 'Journalier' from Change dropdown");
        waitVisible(page.getByText("Daily"), DEFAULT_WAIT);
        clickWithRetry(page.getByText("Daily"), 1, 120);
        waitVisible(page.getByText("Journalier"), DEFAULT_WAIT);
        clickWithRetry(page.getByText("Journalier"), 1, 120);

        logger.info("[Revenues] Selecting 'Monthly' then 'Mensuel' from Change dropdown");
        waitVisible(page.getByText("Monthly"), DEFAULT_WAIT);
        clickWithRetry(page.getByText("Monthly"), 1, 120);
        waitVisible(page.getByText("Mensuel"), DEFAULT_WAIT);
        clickWithRetry(page.getByText("Mensuel"), 1, 120);

        logger.info("[Revenues] Selecting 'Detailed' from Change dropdown");
        waitVisible(page.getByText("Detailed"), DEFAULT_WAIT);
        clickWithRetry(page.getByText("Detailed"), 1, 120);

        // Ensure 'Filter' / Last report section is back in view after interactions
        scrollToLastReportAndEnsureContent();

        // 3) Run Filter flow using codegen-style locators
        // page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^Filter$"))).click();
        logger.info("[Revenues] Opening Filter dropdown via Filter div (codegen flow)");
        Locator filterDiv = page.locator("div").filter(new Locator.FilterOptions()
                .setHasText(java.util.regex.Pattern.compile("^Filter$")));
        if (filterDiv.count() == 0) {
            throw new RuntimeException("Filter activator div not found in Last report section");
        }
        Locator filterActivator = filterDiv.first();
        try { filterActivator.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        waitVisible(filterActivator, DEFAULT_WAIT);
        clickWithRetry(filterActivator, 1, 150);

        // First, in the menu, click All
        logger.info("[Revenues] Selecting 'All' from Filter menu");
        Locator menu = page.getByRole(AriaRole.MENU);
        waitVisible(menu.first(), DEFAULT_WAIT);
        Locator allOpt = menu.first().getByText("All");
        waitVisible(allOpt.first(), DEFAULT_WAIT);
        clickWithRetry(allOpt.first(), 1, 120);

        // Click Filter again and then 'Monthly subscription'
        logger.info("[Revenues] Selecting 'Monthly subscription' using Filter button");
        waitVisible(page.getByText("Filter"), DEFAULT_WAIT);
        clickWithRetry(page.getByText("Filter"), 1, 120);
        Locator monthlySub = page.getByText("Monthly subscription", new Page.GetByTextOptions().setExact(true));
        waitVisible(monthlySub, DEFAULT_WAIT);
        clickWithRetry(monthlySub, 1, 120);

        // Use .ant-dropdown-trigger first() to select Stream then Collection
        logger.info("[Revenues] Selecting 'Stream' then 'Collection' using ant-dropdown-trigger");
        Locator trigger = page.locator(".ant-dropdown-trigger").first();
        waitVisible(trigger, DEFAULT_WAIT);
        clickWithRetry(trigger, 1, 120);
        Locator streamOpt = page.getByText("Stream");
        waitVisible(streamOpt, DEFAULT_WAIT);
        clickWithRetry(streamOpt, 1, 120);

        trigger = page.locator(".ant-dropdown-trigger").first();
        waitVisible(trigger, DEFAULT_WAIT);
        clickWithRetry(trigger, 1, 120);
        Locator collectionOpt = page.getByText("Collection");
        waitVisible(collectionOpt, DEFAULT_WAIT);
        clickWithRetry(collectionOpt, 1, 120);

        // Now use change icon to iterate Medias push, Private medias, Live, Decrypt
        logger.info("[Revenues] Selecting 'Medias push' via change icon");
        Locator changeIconImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("change"));
        waitVisible(changeIconImg.first(), DEFAULT_WAIT);
        clickWithRetry(changeIconImg.first(), 1, 120);
        Locator mediasPush = page.getByText("Medias push");
        waitVisible(mediasPush, DEFAULT_WAIT);
        clickWithRetry(mediasPush, 1, 120);

        logger.info("[Revenues] Selecting 'Private medias' via change icon");
        waitVisible(changeIconImg.first(), DEFAULT_WAIT);
        clickWithRetry(changeIconImg.first(), 1, 120);
        Locator privateMedias = page.getByText("Private medias");
        waitVisible(privateMedias, DEFAULT_WAIT);
        clickWithRetry(privateMedias, 1, 120);

        logger.info("[Revenues] Selecting 'Live' via change icon");
        waitVisible(changeIconImg.first(), DEFAULT_WAIT);
        clickWithRetry(changeIconImg.first(), 1, 120);
        Locator liveOpt = page.getByText("Live");
        waitVisible(liveOpt, DEFAULT_WAIT);
        clickWithRetry(liveOpt, 1, 120);

        logger.info("[Revenues] Selecting 'Decrypt' via change icon");
        waitVisible(changeIconImg.first(), DEFAULT_WAIT);
        clickWithRetry(changeIconImg.first(), 1, 120);
        Locator decryptOpt = page.getByText("Decrypt");
        waitVisible(decryptOpt, DEFAULT_WAIT);
        clickWithRetry(decryptOpt, 1, 120);

        // 4) After completing filter interactions, scroll to top and assert Revenues visible
        scrollToTopUntilRevenuesVisible();
    }
}

