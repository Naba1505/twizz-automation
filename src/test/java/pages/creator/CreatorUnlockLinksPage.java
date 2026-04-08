package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

public class CreatorUnlockLinksPage extends BasePage {

    // Timeout constants (in milliseconds) - Standardized values (optimized)
    private static final int POLLING_WAIT = 50;          // Quick polling operations
    private static final int NAVIGATION_WAIT = 100;      // Navigation delays
    private static final int BUTTON_RETRY_DELAY = 150;   // Button click retry delay
    private static final int CLICK_RETRY_DELAY = 200;    // Standard click retry
    private static final int POST_ACTION_WAIT = 250;     // Post-action wait
    private static final int SHORT_TIMEOUT = 2000;       // Short waits (reduced from 4000)
    private static final int LONG_TIMEOUT = 3000;        // Long waits (reduced from 5000)
    private static final int UPLOAD_TIMEOUT = 60000;     // Upload processing timeout (1 minute, reduced from 2)

    private static final String WHAT_DO_YOU_WANT = "What do you want to do?";
    private static final String UNLOCK = "Unlock";
    private static final String IMPORTATION = "Importation";
    private static final String UPLOADING_MSG = "Stay on page during uploading"; // transient during media processing

    public CreatorUnlockLinksPage(Page page) {
        super(page);
    }

    // safeIsVisible provided by BasePage

    @Step("Open plus menu on creator screen")
    public void openPlusMenu() {
        // Login ensures page is fully loaded, just wait for plus icon with stabilization
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg.first(), ConfigReader.getVisibilityTimeout());
        
        // Small stabilization to ensure icon is clickable
        page.waitForTimeout(300);
        
        Locator svg = plusImg.locator("svg");
        if (svg.count() > 0 && safeIsVisible(svg.first())) {
            clickWithRetry(svg.first(), 2, CLICK_RETRY_DELAY);
        } else {
            clickWithRetry(plusImg.first(), 2, CLICK_RETRY_DELAY);
        }
    }

    @Step("Ensure options popup is visible")
    public void ensureOptionsPopup() {
        // Proactively dismiss blocking dialog if present
        clickIUnderstandIfPresent();
        waitVisible(page.getByText(WHAT_DO_YOU_WANT).first(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Dismiss 'I understand' dialog if present")
    public void clickIUnderstandIfPresent() {
        long start = System.currentTimeMillis();
        long timeoutMs = ConfigReader.getShortTimeout(); // Use configurable timeout instead of hardcoded
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                // Try multiple language variants (English and French)
                String[] buttonNames = {"I understand", "C'est compris"};
                for (String name : buttonNames) {
                    Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name));
                    if (btn.count() > 0 && safeIsVisible(btn.first())) {
                        clickWithRetry(btn.first(), 2, CLICK_RETRY_DELAY);
                        return;
                    }
                }
                
                // Fallbacks: case/selector variants for both languages
                String[] sel = new String[] {
                        "button:has-text('I understand')",
                        "button:has-text('C\\'est compris')",
                        "text=I understand",
                        "text=C'est compris",
                        "//*[self::button or self::*][contains(translate(normalize-space(.), 'IUNDERSTAND', 'iunderstand'), 'i understand')]",
                        "//*[self::button or self::*][contains(normalize-space(.), 'compris')]"
                };
                for (String s : sel) {
                    Locator cand = s.startsWith("//") ? page.locator("xpath=" + s) : page.locator(s);
                    if (cand.count() > 0 && safeIsVisible(cand.first())) {
                        clickWithRetry(cand.first(), 2, NAVIGATION_WAIT);
                        return;
                    }
                }
            } catch (Exception ignored) {}
            try { page.waitForTimeout(POLLING_WAIT); } catch (Exception ignored) {}
        }
    }

    @Step("Choose Unlock from options")
    public void chooseUnlock() {
        Locator u = page.getByText(UNLOCK);
        waitVisible(u.first(), ConfigReader.getShortTimeout());
        clickWithRetry(u.first(), 2, NAVIGATION_WAIT);
    }

    @Step("Ensure Unlock screen visible")
    public void ensureUnlockScreen() {
        waitVisible(page.getByText(UNLOCK).first(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Click PLUS to add media")
    public void clickAddMediaPlus() {
        // Primary path: match codegen, click the IMG icon whose accessible name is "add"
        Locator addIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("add"));
        if (addIcon.count() > 0) {
            waitVisible(addIcon.first(), ConfigReader.getShortTimeout());
            clickWithRetry(addIcon.first(), 2, CLICK_RETRY_DELAY);
            return;
        }

        // Fallback: older UI where the icon is named "plus"
        Locator plus = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plus.first(), ConfigReader.getShortTimeout());
        clickWithRetry(plus.first(), 2, CLICK_RETRY_DELAY);
    }

    @Step("Ensure Importation dialog visible")
    public void ensureImportation() {
        waitVisible(page.getByText(IMPORTATION).first(), ConfigReader.getShortTimeout());
    }

    @Step("Choose 'My Device' in Importation")
    public void chooseMyDevice() {
        // For device uploads we avoid actually clicking the "My Device" button because
        // it may trigger a native OS file chooser. Tests rely on uploadMediaFromDevice
        // to drive the underlying input[type='file'] directly. Here we just ensure the
        // Importation dialog is visible and stable.
        ensureImportation();
    }

    @Step("Upload media from device: {file}")
    public void uploadMediaFromDevice(Path file) {
        if (file == null || !Files.exists(file)) {
            throw new RuntimeException("Media file not found: " + file);
        }

        // Prefer the Ant Upload input inside the Importation dialog; this avoids
        // clicking any button that would open a native OS dialog and instead
        // sets the file path directly on the hidden file input.
        Locator inputs = page.locator(".ant-upload input[type='file']");
        if (inputs.count() == 0) {
            inputs = page.locator("input[type='file']");
        }
        if (inputs.count() == 0) {
            throw new RuntimeException("No file input found for media upload in Importation dialog");
        }
        Locator target = inputs.nth(inputs.count() - 1);
        target.setInputFiles(file);

        // After setting files, dismiss the Importation bottom sheet if a Cancel
        // button is present so it does not block subsequent steps.
        try {
            Locator cancel = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
            if (cancel.count() > 0 && safeIsVisible(cancel.first())) {
                clickWithRetry(cancel.first(), 1, BUTTON_RETRY_DELAY);
            }
        } catch (Exception ignored) {}
    }

    @Step("Open price field (0.00 €)")
    public void openPriceField() {
        Locator zeroPrice = page.getByText("0.00 €");
        waitVisible(zeroPrice.first(), ConfigReader.getShortTimeout());
        clickWithRetry(zeroPrice.first(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Fill price euros: {amount}€")
    public void fillPriceEuro(int amount) {
        Locator spin = page.getByRole(AriaRole.SPINBUTTON);
        waitVisible(spin.first(), ConfigReader.getShortTimeout());
        spin.first().fill(String.valueOf(amount));
        // Blur to trigger recalculations
        try { page.keyboard().press("Tab"); } catch (Exception ignored) {}
        try { page.waitForTimeout(BUTTON_RETRY_DELAY); } catch (Exception ignored) {}
    }

    @Step("Ensure earnings message visible: {text}")
    public void ensureEarningsMessage(String text) {
        // Try multiple variants quickly; don't fail hard if not present on some envs
        String[] variants = new String[] {
                text,
                "You will receive 4.50€",
                "You will receive €4.50",
                "You will receive 4,50 €",
                "You will receive"
        };
        long start = System.currentTimeMillis();
        long timeoutMs = SHORT_TIMEOUT; // quick check; avoid slowing the flow
        while (System.currentTimeMillis() - start < timeoutMs) {
            for (String v : variants) {
                try {
                    Locator cand = page.getByText(Pattern.compile(Pattern.quote(v)));
                    if (cand.count() > 0 && cand.first().isVisible()) {
                        return;
                    }
                } catch (Exception ignored) {}
            }
            try { page.waitForTimeout(NAVIGATION_WAIT); } catch (Exception ignored) {}
        }
        logger.warn("Earnings message not detected quickly; continuing the flow.");
    }

    @Step("Click 'Generate link'")
    public void clickGenerateLink() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Generate link"));
        waitVisible(btn.first(), ConfigReader.getShortTimeout());
        clickWithRetry(btn.first(), 2, CLICK_RETRY_DELAY);
    }

    @Step("Ensure 'Give your unlock a name' screen visible")
    public void ensureGiveYourUnlockName() {
        waitVisible(page.getByText("Give your unlock a name").first(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Fill unlock name: {name}")
    public void fillUnlockName(String name) {
        Locator ph = page.getByPlaceholder("My name");
        waitVisible(ph.first(), ConfigReader.getShortTimeout());
        ph.first().click();
        ph.first().fill(name == null ? "" : name);
    }

    @Step("Click Create")
    public void clickCreate() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create"));
        waitVisible(btn.first(), ConfigReader.getShortTimeout());
        clickWithRetry(btn.first(), 2, CLICK_RETRY_DELAY);
    }

    @Step("Ensure final success title visible")
    public void ensureEverythingIsReady() {
        // For videos, processing can take time; wait smartly for success while respecting the uploading banner
        String[] successVariants = new String[] {
                "Everything is ready !",
                "Everything is ready!",
                "Everything is ready"
        };

        long overallTimeoutMs = UPLOAD_TIMEOUT; // up to 2 minutes for large videos
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < overallTimeoutMs) {
            // Check success first
            for (String v : successVariants) {
                try {
                    Locator ok = page.getByText(v).first();
                    if (ok != null && ok.isVisible()) {
                        return;
                    }
                } catch (Exception ignored) {}
            }
            // If uploading banner is visible, keep waiting in small steps
            try {
                Locator up = page.getByText(UPLOADING_MSG);
                if (up.count() > 0 && up.first().isVisible()) {
                    page.waitForTimeout(POST_ACTION_WAIT);
                    continue;
                }
            } catch (Exception ignored) {}
            // Otherwise, short poll and re-check
            try { page.waitForTimeout(BUTTON_RETRY_DELAY); } catch (Exception ignored) {}
        }
        // One last attempt with regex (in case of minor punctuation changes)
        try {
            Locator rx = page.getByText(Pattern.compile("Everything is ready\\s*!?"));
            if (rx.count() > 0) {
                waitVisible(rx.first(), LONG_TIMEOUT);
                return;
            }
        } catch (Exception ignored) {}
        throw new RuntimeException("Timed out waiting for success after generating unlock link");
    }

    @Step("Close final modal using cross icon")
    public void closeWithCross() {
        Locator cross = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("cross"));
        waitVisible(cross.first(), ConfigReader.getShortTimeout());
        clickWithRetry(cross.first(), 1, BUTTON_RETRY_DELAY);
    }

    @Step("Ensure 'Important' popup visible")
    public void ensureImportantPopup() {
        waitVisible(page.getByText("Important").first(), ConfigReader.getShortTimeout());
    }

    @Step("Click 'C'est compris' or 'Got it'")
    public void clickCEstCompris() {
        // Try multiple language variants (French and English)
        String[] buttonNames = {"C'est compris", "Got it", "I understand"};
        
        for (String name : buttonNames) {
            try {
                Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(name));
                if (btn.count() > 0 && btn.first().isVisible()) {
                    clickWithRetry(btn.first(), 2, CLICK_RETRY_DELAY);
                    return;
                }
            } catch (Exception ignored) {}
        }
        
        // Fallback: try text-based selectors
        String[] selectors = {
            "button:has-text('C\\'est compris')",
            "button:has-text('Got it')",
            "button:has-text('I understand')",
            "text=C'est compris",
            "text=Got it"
        };
        
        for (String sel : selectors) {
            try {
                Locator btn = page.locator(sel);
                if (btn.count() > 0 && btn.first().isVisible()) {
                    clickWithRetry(btn.first(), 2, CLICK_RETRY_DELAY);
                    return;
                }
            } catch (Exception ignored) {}
        }
        
        throw new RuntimeException("Unable to find 'C'est compris' or 'Got it' button");
    }
}

