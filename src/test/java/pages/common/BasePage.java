package pages.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

import utils.ConfigReader;

public class BasePage {
    protected static final int DEFAULT_WAIT = ConfigReader.getShortTimeout();
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

    protected void clickLabelByText(String text) {
        page.locator("label").filter(new Locator.FilterOptions().setHasText(text)).click();
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
                try { page.waitForTimeout(sleepMs); } catch (Exception ignored) {}
            }
        }
        throw last != null ? last : new RuntimeException("Click failed after retries");
    }

    // Enhanced methods using configurable timeouts
    protected void clickWithConfigurableRetry(Locator locator) {
        clickWithRetry(locator, ConfigReader.getElementRetryMax(), ConfigReader.getElementRetryDelay());
    }

    protected void waitForAnimation() {
        try { 
            page.waitForTimeout(ConfigReader.getAnimationTimeout()); 
        } catch (Exception ignored) {}
    }

    protected void waitForUiToSettle() {
        try { 
            page.waitForTimeout(ConfigReader.getUiSettleTimeout()); 
        } catch (Exception ignored) {}
    }

    protected void waitForPageLoad() {
        try { 
            page.waitForTimeout(ConfigReader.getPageLoadTimeout()); 
        } catch (Exception ignored) {}
    }

    protected void smartScroll(int direction, String targetDescription) {
        int maxAttempts = ConfigReader.getMaxScrollAttempts();
        int stepSize = ConfigReader.getScrollStepSize();
        int waitBetween = ConfigReader.getScrollWaitBetween();
        
        logger.info("Smart scrolling {} for: {}", direction > 0 ? "down" : "up", targetDescription);
        
        for (int i = 0; i < maxAttempts; i++) {
            page.mouse().wheel(0, direction * stepSize);
            try { 
                page.waitForTimeout(waitBetween); 
            } catch (Exception ignored) {}
        }
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

    // Non-throwing visibility check used by page objects for optional elements
    protected boolean safeIsVisible(Locator locator) {
        try {
            if (locator == null) return false;
            if (locator.count() == 0) return false;
            return locator.first().isVisible();
        } catch (Throwable ignored) {
            return false;
        }
    }
}
