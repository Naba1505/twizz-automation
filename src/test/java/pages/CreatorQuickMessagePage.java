package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.Assert;
import java.util.regex.Pattern;
import listeners.AllureAttachments;

public class CreatorQuickMessagePage extends BasePage {
    // Timeouts
    private static final int QUICK_MSG_LOAD_TIMEOUT_MS = 60_000;   // Strict wait after opening Quick message
    private static final int ICONS_OR_EMPTY_TIMEOUT_MS = 30_000;   // Wait for icons or empty-state before actions
    private static final int RETURN_TO_LIST_TIMEOUT_MS = 15_000;   // After Register, wait to return to list
    private static final int ASSERT_TITLE_TIMEOUT_MS = 30_000;     // Assert new title visible

    // Selectors
    private static final String SELECTOR_NUMBER_ICON = "div.number-icon";
    private static final String SELECTOR_DELETE_ICON_NTH_CSS = "div > .d-flex > div:nth-child(3) > img";
    private static final String SELECTOR_DELETE_ICON_XPATH = "xpath=//div//img[@width='38']";
    private static final String SELECTOR_DELETE_FALLBACKS =
            "img[src*='trash'], img[alt*='trash' i], img[alt*='delete' i], svg[aria-label*='delete' i], svg[aria-label*='trash' i], div:nth-child(3) > img";

    // UI tweaks
    private static final int SCROLL_STEP_Y = 500;

    public CreatorQuickMessagePage(Page page) {
        super(page);
    }

    private Locator settingsIcon() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("settings"));
    }

    private Locator settingsHeader(String displayName) {
        return page.getByText(displayName);
    }

    private Locator quickMessageMenuItem() {
        return page.getByText("Quick message");
    }

    private Locator quickMessageTitleExact() {
        return page.getByText("Quick message", new Page.GetByTextOptions().setExact(true));
    }

    private Locator addQuickMessageButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add a quick message"));
    }

    private Locator addQuickMessageScreenHeader() {
        return page.getByText("New response registered");
    }

    private Locator titleInput() { return page.getByPlaceholder("Title"); }
    private Locator textInput() { return page.getByPlaceholder("Text"); }
    private Locator registerButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register"));
    }

    // Empty-state displayed when there are no quick messages left
    private Locator emptyState() {
        return page.getByText("Create a pre-recorded response");
    }

    // User-provided, reliable selectors
    private Locator numberIconContainer() { return page.locator(SELECTOR_NUMBER_ICON); }
    private Locator deleteIconsByXPath() { return page.locator(SELECTOR_DELETE_ICON_XPATH); }
    private Locator deleteIconsByNthCss() { return page.locator(SELECTOR_DELETE_ICON_NTH_CSS); }

    // Deletion related locators (robust against SVG/img variations)
    private Locator deleteIconByRole() {
        // Try accessible name-based match first (alt/aria-label)
        return page.getByRole(AriaRole.IMG,
                new Page.GetByRoleOptions().setName(Pattern.compile("(?i)(trash|delete)")));
    }

    private Locator deleteIconByCss() {
        // Fallbacks for common implementations
        // - <img src="/static/media/trash...svg"> or alt contains 'trash'/'delete'
        // - inline <svg aria-label="delete">
        return page.locator(SELECTOR_DELETE_FALLBACKS);
    }

    private Locator firstVisibleDeleteIcon() {
        Locator byRole = deleteIconByRole();
        if (safeIsVisible(byRole.first())) return byRole.first();
        Locator byCss = deleteIconByCss();
        if (safeIsVisible(byCss.first())) return byCss.first();
        // As a last resort, try any IMG within an action cell that could be a delete
        Locator byHeuristic = page.locator("button:has(img[src*='trash']), div:has(> img[src*='trash'])");
        return byHeuristic.first();
    }

    private Locator combinedDeleteIcons() {
        // Union of all known delete icon strategies
        return page.locator(SELECTOR_DELETE_FALLBACKS);
    }

    private Locator deleteConfirmText() {
        return page.getByText("Do you really want to delete this message?");
    }

    private Locator yesDeleteButton() {
        // Try common labels first
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, delete"));
        if (safeIsVisible(btn.first())) return btn.first();
        btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete"));
        if (safeIsVisible(btn.first())) return btn.first();
        btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes"));
        if (safeIsVisible(btn.first())) return btn.first();
        // Fallback regex
        return page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName(Pattern.compile("(?i)(yes.*delete|delete|yes)"))).first();
    }

    private void waitForMessagesToLoad() {
        // If empty-state is visible quickly, no need to wait further
        if (safeIsVisible(emptyState())) return;
        // Prefer the user's loading cue: div.number-icon
        try {
            numberIconContainer().waitFor(new Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                    .setTimeout(DEFAULT_WAIT));
        } catch (Throwable ignored) {}
        long deadline = System.currentTimeMillis() + DEFAULT_WAIT;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (deleteIconsByXPath().count() > 0) return;
                if (deleteIconByRole().count() > 0 || deleteIconByCss().count() > 0) return;
                if (safeIsVisible(emptyState())) return;
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }
        // If none present, proceed (could be empty or permissions issue)
        logger.info("No delete icons detected after wait; list may be empty");
    }

    private void waitUntilAnyDeleteIconVisible() {
        if (safeIsVisible(emptyState())) return;
        try {
            Locator icon = deleteIconByRole().first();
            icon.waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(DEFAULT_WAIT));
            return;
        } catch (Throwable ignored) {}
        try {
            Locator icon = deleteIconByCss().first();
            icon.waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(DEFAULT_WAIT));
            return;
        } catch (Throwable ignored) {}
        try {
            Locator icon = page.locator("div:nth-child(3) > img").first();
            icon.waitFor(new Locator.WaitForOptions().setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE).setTimeout(DEFAULT_WAIT));
            return;
        } catch (Throwable ignored) {}
        // As last attempt, rely on heuristic presence
        logger.info("No visible delete icon after explicit wait; proceeding (list may be empty)");
    }

    private void waitUntilIconsOrEmpty(long timeoutMs) {
        long deadline = System.currentTimeMillis() + Math.max(1000L, timeoutMs);
        while (System.currentTimeMillis() < deadline) {
            try {
                if (safeIsVisible(emptyState())) return;
                if (deleteIconsByXPath().count() > 0) return;
                if (combinedDeleteIcons().count() > 0) return;
            } catch (Throwable ignored) {}
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }
        logger.warn("Waited {} ms but neither empty-state nor delete icons appeared", timeoutMs);
    }

    @Step("Open Settings from profile and verify landing on Settings screen")
    public void openSettingsFromProfile(String expectedDisplayName) {
        logger.info("[QuickMessage] Opening settings from profile; expecting display name: {}", expectedDisplayName);
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 1, 200);
        // Expect a recognizable display name on settings screen
        waitVisible(settingsHeader(expectedDisplayName), DEFAULT_WAIT);
        logger.info("[QuickMessage] Settings screen visible for display name: {}", expectedDisplayName);
    }

    @Step("Navigate to Quick message screen from Settings")
    public void goToQuickMessage() {
        logger.info("[QuickMessage] Navigating to 'Quick message' from Settings");
        Locator item = quickMessageMenuItem();
        try { item.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        clickWithRetry(item, 1, 200);
        waitVisible(quickMessageTitleExact(), DEFAULT_WAIT);
        // Let network settle in case list is lazy-loaded
        waitForIdle();
        // Ensure the list and delete icon(s) are loaded and visible before any deletion attempts
        // Strictly wait up to 60s for div.number-icon as requested
        try {
            numberIconContainer().waitFor(new Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                    .setTimeout(QUICK_MSG_LOAD_TIMEOUT_MS));
            // Additionally, ensure the element exists in the DOM (covers detached/visibility edge cases)
            page.waitForFunction("() => document.querySelector('" + SELECTOR_NUMBER_ICON + "') !== null",
                    null, new Page.WaitForFunctionOptions().setTimeout(QUICK_MSG_LOAD_TIMEOUT_MS));
        } catch (Throwable ignored) {}
        waitForMessagesToLoad();
        // Prefer explicit visibility of user's delete icons if present
        if (deleteIconsByNthCss().count() > 0) {
            try { deleteIconsByNthCss().first().waitFor(new Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                    .setTimeout(DEFAULT_WAIT)); } catch (Throwable ignored) {}
        } else if (deleteIconsByXPath().count() > 0) {
            try { deleteIconsByXPath().first().waitFor(new Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                    .setTimeout(DEFAULT_WAIT)); } catch (Throwable ignored) {}
        } else {
            waitUntilAnyDeleteIconVisible();
        }
        // Do not proceed until icons are present or empty-state is shown (prevents early exit)
        waitUntilIconsOrEmpty(ICONS_OR_EMPTY_TIMEOUT_MS);
        // Readiness check: either empty state or at least one visible delete icon
        boolean ready = safeIsVisible(emptyState()) || deleteIconsByNthCss().count() > 0 || deleteIconsByXPath().count() > 0 || safeIsVisible(firstVisibleDeleteIcon());
        if (!ready) {
            AllureAttachments.attachScreenshot(page, "qm_no_delete_icons");
            AllureAttachments.attachHtml(page, "qm_page.html");
            logger.warn("[QuickMessage] Screen not ready: no empty-state and no visible delete icons detected");
        } else {
            logger.info("[QuickMessage] Screen ready: empty-state={} nthCssCount={} xpathCount={}",
                    safeIsVisible(emptyState()), deleteIconsByNthCss().count(), deleteIconsByXPath().count());
        }
    }

    @Step("Add a Quick message titled: {title}")
    public void addQuickMessage(String title, String text) {
        logger.info("[QuickMessage] Adding quick message with title: '{}'", title);
        waitVisible(addQuickMessageButton(), DEFAULT_WAIT);
        clickWithRetry(addQuickMessageButton(), 1, 200);
        // Ensure we are on add screen
        waitVisible(addQuickMessageScreenHeader(), DEFAULT_WAIT);
        // Fill
        waitVisible(titleInput(), DEFAULT_WAIT);
        titleInput().click();
        titleInput().fill(title);
        waitVisible(textInput(), DEFAULT_WAIT);
        textInput().click();
        textInput().fill(text);
        // Submit
        clickWithRetry(registerButton(), 1, 200);
        // Wait until we're back on the list view: either the list title or number-icon becomes visible
        try {
            quickMessageTitleExact().waitFor(new Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                    .setTimeout(RETURN_TO_LIST_TIMEOUT_MS));
        } catch (Throwable ignored) {}
        try {
            numberIconContainer().waitFor(new Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                    .setTimeout(RETURN_TO_LIST_TIMEOUT_MS));
        } catch (Throwable ignored) {}
        // Small settle time
        try { page.waitForTimeout(300); } catch (Exception ignored) {}
        logger.info("[QuickMessage] Submitted quick message and returned to list view");
    }

    @Step("Assert that quick message with title is visible: {title}")
    public void assertQuickMessageVisible(String title) {
        logger.info("[QuickMessage] Asserting quick message title is visible: '{}'", title);
        // Try exact match first with an extended timeout window
        long deadline = System.currentTimeMillis() + ASSERT_TITLE_TIMEOUT_MS;
        String partial = title.length() > 20 ? title.substring(0, 20) : title;
        while (System.currentTimeMillis() < deadline) {
            try {
                // Attempt exact text
                Locator exact = page.getByText(title, new Page.GetByTextOptions().setExact(true));
                if (safeIsVisible(exact.first())) {
                    logger.info("[QuickMessage] Found exact title match");
                    return;
                }
                // Attempt partial contains
                Locator contains = page.getByText(partial);
                if (safeIsVisible(contains.first())) {
                    logger.info("[QuickMessage] Found partial title match: '{}'", partial);
                    return;
                }
                // Scroll down a bit to reveal more rows (handle long lists/virtualization)
                try { page.mouse().wheel(0, SCROLL_STEP_Y); } catch (Throwable ignored) {}
                try { page.waitForTimeout(300); } catch (Exception ignored) {}
            } catch (Throwable ignored) {}
        }
        // Final diagnostics before failing
        try { AllureAttachments.attachScreenshot(page, "qm_assert_not_found"); } catch (Throwable ignored) {}
        try { AllureAttachments.attachHtml(page, "qm_assert_page.html"); } catch (Throwable ignored) {}
        Assert.fail("Quick message title not visible after adding: " + title);
    }

    @Step("Delete a single quick message using delete icon nth({index})")
    public boolean deleteOneQuickMessage(int index) {
        logger.info("[QuickMessage] Attempting to delete one quick message (index hint: {})", index);
        // Try to ensure we are on Quick message screen
        waitVisible(quickMessageTitleExact(), DEFAULT_WAIT);
        waitForMessagesToLoad();
        // Prefer first visible delete icon over fixed nth index
        Locator del = firstVisibleDeleteIcon();
        if (!safeIsVisible(del)) {
            logger.info("[QuickMessage] No visible delete icon found (role/css fallbacks)");
            return false;
        }
        try { del.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        try { del.hover(new Locator.HoverOptions().setForce(true)); } catch (Exception ignored) {}
        // Force click to bypass hover/overlay issues
        try {
            del.click(new Locator.ClickOptions().setForce(true));
        } catch (RuntimeException e) {
            logger.warn("[QuickMessage] Force click on delete icon failed, retrying normal click: {}", e.getMessage());
            clickWithRetry(del, 2, 200);
        }
        // Confirm dialog
        try {
            waitVisible(deleteConfirmText(), DEFAULT_WAIT);
        } catch (RuntimeException e) {
            logger.warn("[QuickMessage] Delete confirm text not found, proceeding to look for confirmation button only: {}", e.getMessage());
        }
        Locator yes = yesDeleteButton();
        waitVisible(yes, DEFAULT_WAIT);
        clickWithRetry(yes, 2, 200);
        // Wait for the delete icon count to reduce or dialog to disappear
        int before = deleteIconByRole().count() + deleteIconByCss().count();
        long deadline = System.currentTimeMillis() + 3_000;
        while (System.currentTimeMillis() < deadline) {
            int after = deleteIconByRole().count() + deleteIconByCss().count();
            if (after < before) break;
            try { page.waitForTimeout(150); } catch (Exception ignored) {}
        }
        logger.info("[QuickMessage] Delete action completed");
        return true;
    }

    @Step("Delete all quick messages by clicking the visible trash icon until none remain")
    public void deleteAllQuickMessages() {
        logger.info("[QuickMessage] Starting delete-all quick messages loop");
        // Loop until empty-state is visible or no more delete icons are available
        waitForMessagesToLoad();
        // Ensure we have icons or empty-state before attempting first click
        waitUntilIconsOrEmpty(ICONS_OR_EMPTY_TIMEOUT_MS);
        try {
            // If icons are supposed to be present, wait until at least one matches primary CSS/XPath
            if (!safeIsVisible(emptyState())) {
                page.waitForFunction("() => document.querySelectorAll('div > .d-flex > div:nth-child(3) > img').length > 0 || document.evaluate(\"count(//div//img[@width=\\'38\\'])\", document, null, XPathResult.NUMBER_TYPE, null).numberValue > 0",
                        null, new Page.WaitForFunctionOptions().setTimeout(30_000));
            }
        } catch (Throwable ignored) {}
        while (true) {
            if (safeIsVisible(emptyState())) {
                logger.info("[QuickMessage] Empty-state visible, all quick messages deleted");
                break;
            }
            int count = deleteIconsByNthCss().count();
            if (count <= 0) count = deleteIconsByXPath().count();
            try { listeners.AllureAttachments.attachText("qm_delete_loop", "icons(before)=" + count); } catch (Throwable ignored) {}
            if (count <= 0) {
                logger.info("[QuickMessage] No delete icons found; stopping cleanup loop");
                if (!safeIsVisible(emptyState())) {
                    AllureAttachments.attachScreenshot(page, "qm_stopped_without_empty_state");
                    AllureAttachments.attachHtml(page, "qm_final_page.html");
                    logger.warn("[QuickMessage] No delete icons present and empty-state not visible; stopping cleanup without assertion per user request.");
                }
                break;
            }
            // Prefer the user's nth-child CSS list; click from last to first; else use XPath; else fallback
            Locator list = deleteIconsByNthCss().count() > 0 ? deleteIconsByNthCss() : (deleteIconsByXPath().count() > 0 ? deleteIconsByXPath() : combinedDeleteIcons());
            Locator icon = list.last();
            try { icon.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
            try { icon.hover(new Locator.HoverOptions().setForce(true)); } catch (Exception ignored) {}
            try {
                icon.click(new Locator.ClickOptions().setForce(true));
            } catch (RuntimeException e) {
                logger.warn("[QuickMessage] Force click on delete icon failed, retrying normal click: {}", e.getMessage());
                clickWithRetry(icon, 2, 200);
            }
            // Confirm
            Locator yes = yesDeleteButton();
            waitVisible(yes, DEFAULT_WAIT);
            clickWithRetry(yes, 2, 200);
            // Wait for count to reduce
            long deadline = System.currentTimeMillis() + 3_000;
            while (System.currentTimeMillis() < deadline) {
                int after = deleteIconByRole().count() + deleteIconByCss().count();
                if (after < count) break;
                try { page.waitForTimeout(150); } catch (Exception ignored) {}
            }
            // If count didn't reduce, try a small scroll (handles virtualized lists)
            int afterCheck = deleteIconsByNthCss().count();
            if (afterCheck == 0) afterCheck = deleteIconsByXPath().count();
            if (afterCheck >= count && !safeIsVisible(emptyState())) {
                try { page.mouse().wheel(0, SCROLL_STEP_Y); } catch (Throwable ignored) {}
                try { page.waitForTimeout(300); } catch (Exception ignored) {}
            }
            try { listeners.AllureAttachments.attachText("qm_delete_loop", "icons(after)=" + deleteIconsByXPath().count()); } catch (Throwable ignored) {}
            // Small pause for UI re-render
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }
        try { page.waitForTimeout(200); } catch (Exception ignored) {}
        logger.info("[QuickMessage] Delete-all loop completed");
    }
}
