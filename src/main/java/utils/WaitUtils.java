package utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import java.util.function.BooleanSupplier;

public final class WaitUtils {
    private WaitUtils() {}

    public static boolean waitForVisible(Locator locator, long timeoutMs) {
        long deadline = System.currentTimeMillis() + Math.max(0, timeoutMs);
        while (System.currentTimeMillis() < deadline) {
            try {
                if (locator.count() > 0 && locator.first().isVisible()) return true;
            } catch (Throwable ignored) {}
            try { locator.page().waitForTimeout(100); } catch (Throwable ignored) {}
        }
        return locator.count() > 0 && locator.first().isVisible();
    }

    public static boolean waitForHidden(Locator locator, long timeoutMs) {
        long deadline = System.currentTimeMillis() + Math.max(0, timeoutMs);
        while (System.currentTimeMillis() < deadline) {
            try {
                if (locator.count() == 0 || !locator.first().isVisible()) return true;
            } catch (Throwable ignored) {}
            try { locator.page().waitForTimeout(100); } catch (Throwable ignored) {}
        }
        return locator.count() == 0 || !locator.first().isVisible();
    }

    public static boolean waitForDropdownVisible(Page page, long timeoutMs) {
        return waitForVisible(page.locator(".ant-select-dropdown:visible"), timeoutMs);
    }

    public static boolean waitForEnabled(Locator locator, long timeoutMs) {
        long deadline = System.currentTimeMillis() + Math.max(0, timeoutMs);
        while (System.currentTimeMillis() < deadline) {
            try {
                if (locator.count() > 0) {
                    Locator first = locator.first();
                    String disabled = first.getAttribute("disabled");
                    String ariaDisabled = first.getAttribute("aria-disabled");
                    if ((disabled == null || disabled.isEmpty()) && (ariaDisabled == null || !"true".equalsIgnoreCase(ariaDisabled))) {
                        return true;
                    }
                }
            } catch (Throwable ignored) {}
            try { locator.page().waitForTimeout(100); } catch (Throwable ignored) {}
        }
        return false;
    }

    public static boolean waitUntil(BooleanSupplier condition, Page page, long timeoutMs, long pollMs) {
        long deadline = System.currentTimeMillis() + Math.max(0, timeoutMs);
        long poll = Math.max(10, pollMs);
        while (System.currentTimeMillis() < deadline) {
            try {
                if (condition.getAsBoolean()) return true;
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(poll); } catch (Throwable ignored) {}
        }
        return condition.getAsBoolean();
    }
}
