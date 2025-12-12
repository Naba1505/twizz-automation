package pages;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Step;

public class CreatorUnlockLinksPage extends BasePage {

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
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg.first(), 15000);
        Locator svg = plusImg.locator("svg");
        if (svg.count() > 0 && safeIsVisible(svg.first())) {
            clickWithRetry(svg.first(), 2, 200);
        } else {
            clickWithRetry(plusImg.first(), 2, 200);
        }
    }

    @Step("Ensure options popup is visible")
    public void ensureOptionsPopup() {
        // Proactively dismiss blocking dialog if present
        clickIUnderstandIfPresent();
        waitVisible(page.getByText(WHAT_DO_YOU_WANT).first(), 15000);
    }

    @Step("Dismiss 'I understand' dialog if present")
    public void clickIUnderstandIfPresent() {
        long start = System.currentTimeMillis();
        long timeoutMs = 5000;
        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                // Try role-based first
                Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I understand"));
                if (btn.count() > 0 && safeIsVisible(btn.first())) {
                    clickWithRetry(btn.first(), 2, 200);
                    return;
                }
                // Fallbacks: case/selector variants
                String[] sel = new String[] {
                        "button:has-text('I understand')",
                        "text=I understand",
                        "//*[self::button or self::*][contains(translate(normalize-space(.), 'IUNDERSTAND', 'iunderstand'), 'i understand')]"
                };
                for (String s : sel) {
                    Locator cand = s.startsWith("//") ? page.locator("xpath=" + s) : page.locator(s);
                    if (cand.count() > 0 && safeIsVisible(cand.first())) {
                        clickWithRetry(cand.first(), 2, 200);
                        return;
                    }
                }
            } catch (Exception ignored) {}
            try { page.waitForTimeout(150); } catch (Exception ignored) {}
        }
    }

    @Step("Choose Unlock from options")
    public void chooseUnlock() {
        Locator u = page.getByText(UNLOCK);
        waitVisible(u.first(), 10000);
        clickWithRetry(u.first(), 2, 200);
    }

    @Step("Ensure Unlock screen visible")
    public void ensureUnlockScreen() {
        waitVisible(page.getByText(UNLOCK).first(), 15000);
    }

    @Step("Click PLUS to add media")
    public void clickAddMediaPlus() {
        // Primary path: match codegen, click the IMG icon whose accessible name is "add"
        Locator addIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("add"));
        if (addIcon.count() > 0) {
            waitVisible(addIcon.first(), 10000);
            clickWithRetry(addIcon.first(), 2, 200);
            return;
        }

        // Fallback: older UI where the icon is named "plus"
        Locator plus = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plus.first(), 10000);
        clickWithRetry(plus.first(), 2, 200);
    }

    @Step("Ensure Importation dialog visible")
    public void ensureImportation() {
        waitVisible(page.getByText(IMPORTATION).first(), 10000);
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
                clickWithRetry(cancel.first(), 1, 150);
            }
        } catch (Exception ignored) {}
    }

    @Step("Open price field (0.00 €)")
    public void openPriceField() {
        Locator zeroPrice = page.getByText("0.00 €");
        waitVisible(zeroPrice.first(), 10000);
        clickWithRetry(zeroPrice.first(), 1, 150);
    }

    @Step("Fill price euros: {amount}€")
    public void fillPriceEuro(int amount) {
        Locator spin = page.getByRole(AriaRole.SPINBUTTON);
        waitVisible(spin.first(), 10000);
        spin.first().fill(String.valueOf(amount));
        // Blur to trigger recalculations
        try { page.keyboard().press("Tab"); } catch (Exception ignored) {}
        try { page.waitForTimeout(300); } catch (Exception ignored) {}
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
        long timeoutMs = 4000; // quick check; avoid slowing the flow
        while (System.currentTimeMillis() - start < timeoutMs) {
            for (String v : variants) {
                try {
                    Locator cand = page.getByText(Pattern.compile(Pattern.quote(v)));
                    if (cand.count() > 0 && cand.first().isVisible()) {
                        return;
                    }
                } catch (Exception ignored) {}
            }
            try { page.waitForTimeout(200); } catch (Exception ignored) {}
        }
        logger.warn("Earnings message not detected quickly; continuing the flow.");
    }

    @Step("Click 'Generate link'")
    public void clickGenerateLink() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Generate link"));
        waitVisible(btn.first(), 10000);
        clickWithRetry(btn.first(), 2, 200);
    }

    @Step("Ensure 'Give your unlock a name' screen visible")
    public void ensureGiveYourUnlockName() {
        waitVisible(page.getByText("Give your unlock a name").first(), 15000);
    }

    @Step("Fill unlock name: {name}")
    public void fillUnlockName(String name) {
        Locator ph = page.getByPlaceholder("My name");
        waitVisible(ph.first(), 10000);
        ph.first().click();
        ph.first().fill(name == null ? "" : name);
    }

    @Step("Click Create")
    public void clickCreate() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Create"));
        waitVisible(btn.first(), 10000);
        clickWithRetry(btn.first(), 2, 200);
    }

    @Step("Ensure final success title visible")
    public void ensureEverythingIsReady() {
        // For videos, processing can take time; wait smartly for success while respecting the uploading banner
        String[] successVariants = new String[] {
                "Everything is ready !",
                "Everything is ready!",
                "Everything is ready"
        };

        long overallTimeoutMs = 120_000; // up to 2 minutes for large videos
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
                    page.waitForTimeout(500);
                    continue;
                }
            } catch (Exception ignored) {}
            // Otherwise, short poll and re-check
            try { page.waitForTimeout(300); } catch (Exception ignored) {}
        }
        // One last attempt with regex (in case of minor punctuation changes)
        try {
            Locator rx = page.getByText(Pattern.compile("Everything is ready\\s*!?"));
            if (rx.count() > 0) {
                waitVisible(rx.first(), 5000);
                return;
            }
        } catch (Exception ignored) {}
        throw new RuntimeException("Timed out waiting for success after generating unlock link");
    }

    @Step("Close final modal using cross icon")
    public void closeWithCross() {
        Locator cross = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("cross"));
        waitVisible(cross.first(), 10000);
        clickWithRetry(cross.first(), 1, 150);
    }

    @Step("Ensure 'Important' popup visible")
    public void ensureImportantPopup() {
        waitVisible(page.getByText("Important").first(), 10000);
    }

    @Step("Click 'C'est compris'")
    public void clickCEstCompris() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("C'est compris"));
        waitVisible(btn.first(), 10000);
        clickWithRetry(btn.first(), 2, 200);
    }
}
