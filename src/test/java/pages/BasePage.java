package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasePage {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    protected Locator getByTextExact(String text) {
        return page.getByText(text, new Page.GetByTextOptions().setExact(true));
    }

    protected void waitVisible(Locator locator, double timeoutMs) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
    }

    protected void fillByPlaceholder(String placeholder, String value) {
        Locator input = page.getByPlaceholder(placeholder);
        input.click();
        input.fill(value);
    }

    protected void clickButtonByName(String name) {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name)).click();
    }

    protected void clickWithRetry(Locator locator, int retries, long sleepMs) {
        RuntimeException last = null;
        for (int i = 0; i <= retries; i++) {
            try {
                locator.click();
                return;
            } catch (RuntimeException e) {
                last = e;
                logger.warn("Click failed (attempt {}/{}): {}", i + 1, retries + 1, e.getMessage());
                try { Thread.sleep(sleepMs); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
            }
        }
        throw last != null ? last : new RuntimeException("Click failed after retries");
    }

    protected void typeAndAssert(Locator locator, String value) {
        locator.click();
        locator.fill(value);
        String current = locator.inputValue();
        if (!value.equals(current)) {
            logger.warn("Value mismatch after fill. Expected='{}' Actual='{}'", value, current);
        }
    }

    protected void waitForIdle() {
        page.waitForLoadState();
        page.waitForLoadState();
    }
}
