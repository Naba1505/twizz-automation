package pages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import utils.WaitUtils;

public class CreatorLivePage extends BasePage {

    // UI strings
    private static final String CONVERSION_TOOLS_TEXT = "Vos meilleurs outils de conversion";
    private static final String I_UNDERSTAND_BTN = "I understand";
    private static final String LIVE_TEXT = "Live";
    private static final String LIVE_ONBOARDING_TEXT = "How to use lives";
    private static final String ACCESS_EVERYONE = "Everyone";
    private static final String ACCESS_SUBSCRIBERS = "Subscribers";
    private static final String PRICE_FREE = "Free";
    private static final String WHEN_SCHEDULE = "Schedule";
    private static final String DATE_PLACEHOLDER = "Date";
    private static final String DESC_PLACEHOLDER = "Your message....";
    private static final String REGISTER_BTN = "Register";
    private static final String SUCCESS_TOAST = "Live scheduled successfully";

    // Delete flow strings
    private static final String EDIT_BTN = "Edit";
    private static final String SCHEDULED_LIVE_TEXT = "Scheduled Live";
    private static final String DELETE_EVENT_BTN = "Delete event";
    private static final String DELETE_CONFIRM_TEXT = "Do you want to delete this event ? Fans will be refunded";
    private static final String YES_DELETE_BTN = "Yes delete";
    private static final String DELETE_SUCCESS_TOAST = "Deleted the live successfully";

    public CreatorLivePage(Page page) {
        super(page);
    }

    // Navigation and guards
    @Step("Open plus menu")
    public void openPlusMenu() {
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg, 15000);
        clickWithRetry(plusImg, 2, 300);
        handleConversionPromptIfPresent();
        logger.info("Opened plus menu");
    }

    @Step("Navigate to Live screen")
    public void navigateToLive() {
        // Always clear any blocking overlay first
        clickIUnderstandIfPresent();

        // Click Live reliably
        clickLiveExactWithRetry();

        // If onboarding shows up after clicking Live, dismiss and retry click
        if (page.getByText(LIVE_ONBOARDING_TEXT).count() > 0) {
            clickIUnderstandIfPresent();
            clickLiveExactWithRetry();
        }

        ensureLiveScreen();
    }

    @Step("Ensure Live screen visible")
    public void ensureLiveScreen() {
        Locator live = page.getByText(LIVE_TEXT);
        waitVisible(live, 20000);
        logger.info("Live screen visible");
    }

    // Form steps
    @Step("Set access: Everyone")
    public void setAccessEveryone() {
        clickLabelByText(ACCESS_EVERYONE);
        logger.info("Set access to Everyone");
    }

    @Step("Set access: Subscribers")
    public void setAccessSubscribers() {
        clickLabelByText(ACCESS_SUBSCRIBERS);
        logger.info("Set access to Subscribers");
    }

    @Step("Set price: {euro}€")
    public void setPriceEuro(int euro) {
        page.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^" + euro + "€$"))).click();
        logger.info("Set price to {}€", euro);
    }

    @Step("Set price: Free")
    public void setPriceFree() {
        clickLabelByText(PRICE_FREE);
        logger.info("Set price to Free");
    }

    @Step("Enable chat with Everyone if present")
    public void enableChatEveryoneIfPresent() {
        Locator chatBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ACCESS_EVERYONE));
        if (chatBtn.count() > 0) {
            clickWithRetry(chatBtn.first(), 2, 200);
            logger.info("Enabled chat with Everyone");
        }
    }

    @Step("Enable chat with Subscribers if present")
    public void enableChatSubscribersIfPresent() {
        Locator chatBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ACCESS_SUBSCRIBERS));
        if (chatBtn.count() > 0) {
            clickWithRetry(chatBtn.first(), 2, 200);
            logger.info("Enabled chat with Subscribers");
        }
    }

    @Step("Set custom price: {amount}€")
    public void setCustomPriceEuro(String amount) {
        // Click the editable price field, then fill the spinbutton
        Locator priceDisplay = page.getByText("0.00 €");
        if (priceDisplay.count() > 0) {
            priceDisplay.first().click();
        }
        page.getByRole(AriaRole.SPINBUTTON).fill(amount);
        logger.info("Set custom price to {}€", amount);
    }

    // ================= Delete Flow =================
    @Step("Check if live logo visible on profile")
    public boolean isLiveLogoVisibleOnProfile() {
        Locator logo = page.locator(".ant-col > img");
        try {
            waitVisible(logo.first(), 15000);
            logger.info("Live logo visible on profile");
            return true;
        } catch (Exception e) {
            logger.warn("Live logo not visible on profile: {}", e.getMessage());
            return false;
        }
    }

    @Step("Open Edit on latest event")
    public void openEditEvent() {
        // Try to reveal edit actions by hovering the first live card/logo
        try {
            Locator firstImg = page.locator(".ant-col > img").first();
            if (firstImg.count() > 0) {
                firstImg.scrollIntoViewIfNeeded();
                firstImg.hover();
            }
        } catch (Exception ignored) {}

        // Strategy 1: direct button named "Edit"
        Locator editBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(EDIT_BTN));
        if (editBtn.count() > 0) {
            try {
                waitVisible(editBtn.first(), 10000);
                clickWithRetry(editBtn.first(), 2, 200);
                logger.info("Clicked Edit via BUTTON locator");
                return;
            } catch (Exception ignored) {}
        }

        // Strategy 2: link named "Edit"
        Locator editLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(EDIT_BTN));
        if (editLink.count() > 0) {
            try {
                waitVisible(editLink.first(), 8000);
                clickWithRetry(editLink.first(), 2, 200);
                logger.info("Clicked Edit via LINK locator");
                return;
            } catch (Exception ignored) {}
        }

        // Strategy 3: menu item named "Edit" (if actions are under a menu)
        try {
            Locator menuItem = page.getByRole(AriaRole.MENUITEM, new Page.GetByRoleOptions().setName(EDIT_BTN));
            if (menuItem.count() > 0) {
                waitVisible(menuItem.first(), 8000);
                clickWithRetry(menuItem.first(), 2, 200);
                logger.info("Clicked Edit via MENUITEM locator");
                return;
            }
        } catch (Exception ignored) {}

        // Strategy 4: visible text "Edit" scoped under an open dropdown/menu/dialog
        try {
            Locator popup = page.locator(".ant-dropdown:visible, .ant-popover:visible, .ant-modal:visible, body");
            Locator editTxt = popup.getByText(EDIT_BTN, new Locator.GetByTextOptions().setExact(true));
            if (editTxt.count() > 0) {
                clickWithRetry(editTxt.first(), 2, 200);
                logger.info("Clicked Edit via visible text fallback");
                return;
            }
        } catch (Exception ignored) {}

        // As a last attempt, try clicking any visible 'Edit' text on the page
        try {
            Locator anyEdit = page.getByText(EDIT_BTN, new Page.GetByTextOptions().setExact(true));
            if (anyEdit.count() > 0 && anyEdit.first().isVisible()) {
                clickWithRetry(anyEdit.first(), 2, 200);
                logger.info("Clicked Edit via global text fallback");
                return;
            }
        } catch (Exception ignored) {}

        // If all strategies fail, log a warning for non-fatal cleanup skip
        logger.warn("Unable to locate clickable 'Edit' action for live event after multiple strategies");
    }

    @Step("Ensure Scheduled Live screen visible")
    public void ensureScheduledLiveScreen() {
        Locator txt = page.getByText(SCHEDULED_LIVE_TEXT);
        waitVisible(txt.first(), 15000);
        logger.info("Scheduled Live screen visible");
    }

    @Step("Click Delete event")
    public void clickDeleteEvent() {
        Locator del = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(DELETE_EVENT_BTN));
        waitVisible(del.first(), 10000);
        clickWithRetry(del.first(), 2, 200);
        logger.info("Clicked Delete event");
    }

    @Step("Confirm delete")
    public void confirmDeleteYes() {
        // Ensure confirm text appears
        Locator confirmTxt = page.getByText(DELETE_CONFIRM_TEXT);
        waitVisible(confirmTxt.first(), 10000);
        // Click Yes delete
        Locator yes = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(YES_DELETE_BTN));
        clickWithRetry(yes.first(), 2, 200);
        logger.info("Confirmed delete");
    }

    @Step("Verify delete success toast")
    public void verifyDeleteSuccessToast() {
        Locator toast = page.getByText(DELETE_SUCCESS_TOAST);
        waitVisible(toast.first(), 15000);
        logger.info("Delete success toast visible");
    }

    @Step("Delete latest scheduled live event")
    public void deleteLatestLiveEvent() {
        if (!isLiveLogoVisibleOnProfile()) {
            throw new AssertionError("Expected live logo on profile indicating a live event exists");
        }
        openEditEvent();
        ensureScheduledLiveScreen();
        clickDeleteEvent();
        confirmDeleteYes();
        verifyDeleteSuccessToast();
    }

    /**
     * Attempts to delete the latest scheduled live event if present.
     * Returns true if a delete was performed, false if no event was found.
     */
    @Step("Try delete latest scheduled live event if present")
    public boolean tryDeleteLatestLiveEvent() {
        if (!isLiveLogoVisibleOnProfile()) {
            logger.info("No live event found on profile; skipping cleanup delete");
            return false;
        }
        try {
            openEditEvent();
            ensureScheduledLiveScreen();
            clickDeleteEvent();
            confirmDeleteYes();
            verifyDeleteSuccessToast();
            return true;
        } catch (Exception e) {
            logger.warn("Cleanup delete encountered an issue: {}", e.getMessage());
            return false;
        }
    }

    // ================= Submit & Verify (Create flow) =================
    private Locator registerButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(REGISTER_BTN));
    }

    private void waitUntilRegisterEnabled(int timeoutMs) {
        Locator reg = registerButton();
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try {
                if (reg.first().isEnabled()) return;
            } catch (Exception ignored) { }
            page.waitForTimeout(200);
        }
        throw new RuntimeException("Register button did not become enabled within timeout");
    }

    @Step("Choose Schedule option")
    public void chooseSchedule() {
        page.locator("label").filter(new Locator.FilterOptions().setHasText(WHEN_SCHEDULE)).click();
        logger.info("Selected Schedule option");
    }

    @Step("Pick date: {when}")
    public void pickDate(LocalDateTime when) {
        // Prevent choosing past dates: clamp to today if needed
        if (when.toLocalDate().isBefore(LocalDate.now())) {
            when = LocalDate.now().atTime(when.getHour(), when.getMinute());
            logger.info("Requested past date; clamped to today: {}", when.toLocalDate());
        }

        // Open the date picker panel
        Locator dateField = page.getByPlaceholder(DATE_PLACEHOLDER);
        dateField.first().click();

        // Ensure the dropdown/panel is visible
        Locator panel = visibleDatePanel();
        waitVisible(panel, 5000);

        // 1) Select Year using header button, then the year cell
        String yearStr = String.valueOf(when.getYear());
        Locator yearBtn = panel.locator(".ant-picker-year-btn");
        if (yearBtn.count() > 0) {
            clickWithRetry(yearBtn.first(), 2, 150);
            Locator yearCell = page.locator(".ant-picker-year-panel").getByText(yearStr, new Locator.GetByTextOptions().setExact(true));
            if (yearCell.count() == 0) {
                // fallback within the panel
                yearCell = panel.getByText(yearStr, new Locator.GetByTextOptions().setExact(true));
            }
            if (yearCell.count() > 0) {
                clickWithRetry(yearCell.first(), 2, 150);
                logger.info("Picked year {}", yearStr);
            } else {
                logger.warn("Could not locate target year {}", yearStr);
            }
        }

        // 2) Select Month using header button, then the month cell (e.g., "Aug")
        String monStr = when.format(DateTimeFormatter.ofPattern("MMM"));
        Locator monthBtn = panel.locator(".ant-picker-month-btn");
        if (monthBtn.count() > 0) {
            clickWithRetry(monthBtn.first(), 2, 150);
            Locator monthCell = page.locator(".ant-picker-month-panel").getByText(monStr, new Locator.GetByTextOptions().setExact(true));
            if (monthCell.count() == 0) {
                monthCell = panel.getByText(monStr, new Locator.GetByTextOptions().setExact(true));
            }
            if (monthCell.count() > 0) {
                clickWithRetry(monthCell.first(), 2, 150);
                logger.info("Picked month {}", monStr);
            } else {
                logger.warn("Could not locate target month {}", monStr);
            }
        }

        // 3) Click a non-disabled in-view day cell matching the target day
        String dayText = String.valueOf(when.getDayOfMonth());
        Locator dayCell = panel.locator("td.ant-picker-cell-in-view:not(.ant-picker-cell-disabled) .ant-picker-cell-inner")
                .getByText(dayText, new Locator.GetByTextOptions().setExact(true));
        if (dayCell.count() == 0) {
            // Fallback: look globally within any open picker dropdown
            dayCell = page.locator(".ant-picker-dropdown td.ant-picker-cell-in-view:not(.ant-picker-cell-disabled) .ant-picker-cell-inner")
                    .getByText(dayText, new Locator.GetByTextOptions().setExact(true));
        }
        if (dayCell.count() > 0) {
            clickWithRetry(dayCell.first(), 2, 150);
            logger.info("Picked date day {}", dayText);
        } else {
            logger.warn("Could not find enabled day cell for {}", dayText);
        }
    }

    private Locator visibleDatePanel() {
        // Ant Design renders a dropdown; ensure we target the visible panel
        Locator dropdown = page.locator(".ant-picker-dropdown:visible .ant-picker-panel");
        if (dropdown.count() > 0) return dropdown.first();
        Locator panel = page.locator(".ant-picker-panel");
        return panel.count() > 0 ? panel.first() : page.locator("body");
    }

    @Step("Pick time for {when}")
    public void pickTime(LocalDateTime when) {
        // Normalize target: if selecting today and time is in the past, clamp to next sensible future slot first
        boolean today = when.toLocalDate().isEqual(LocalDate.now());
        LocalDateTime now = LocalDateTime.now();
        if (today && !when.isAfter(now.plusMinutes(1))) {
            int minute = now.getMinute();
            int toNext30 = ((minute < 30) ? (30 - minute) : (60 - minute));
            when = now.plusMinutes(toNext30);
            logger.info("Requested past/near-past time; clamped to next slot: {}", when.format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        String hhmm12 = when.format(DateTimeFormatter.ofPattern("hh:mm"));
        String hhmm12AmPm = when.format(DateTimeFormatter.ofPattern("hh:mm a"));
        String hhmm24 = when.format(DateTimeFormatter.ofPattern("HH:mm"));

        // If a date dropdown is still open, close it to avoid overlay blocking time dropdown
        if (page.locator(".ant-picker-dropdown:visible").count() > 0) {
            try { page.keyboard().press("Escape"); } catch (Exception ignored) {}
            page.waitForTimeout(200);
        }

        // Try opening the time select near the Date field (robust against rc_select_* changes)
        boolean opened = false;
        try {
            Locator dateInput = page.getByPlaceholder(DATE_PLACEHOLDER).first();
            // First try: the next ant-select after the Date form item
            Locator timeSelector = dateInput.locator("xpath=ancestor::div[contains(@class,'ant-form-item')][1]//following::div[contains(@class,'ant-select')][1]//div[contains(@class,'ant-select-selector')]");
            if (timeSelector.count() > 0) {
                timeSelector.first().click();
                opened = true;
            }
            // Second try: any visible select selector on the page
            if (!opened) {
                Locator anySelector = page.locator(".ant-select-selector");
                if (anySelector.count() > 0) {
                    anySelector.first().click();
                    opened = true;
                }
            }
        } catch (Exception ignored) {}
        if (!opened) {
            logger.warn("Time dropdown not found via relative selector; clicking Date field to reveal");
            page.getByPlaceholder(DATE_PLACEHOLDER).click();
        }

        // Wait for dropdown to become visible
        WaitUtils.waitForDropdownVisible(page, 3000);

        // Try to pick time within the visible dropdown by role/name or text/title (12h, 12h AM/PM, then 24h)
        Locator dropdown = page.locator(".ant-select-dropdown:visible").first();
        Locator opt = dropdown.getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(hhmm12).setExact(true));
        if (opt.count() == 0)
            opt = dropdown.getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(hhmm12AmPm).setExact(true));
        if (opt.count() == 0)
            opt = dropdown.getByRole(AriaRole.OPTION, new Locator.GetByRoleOptions().setName(hhmm24).setExact(true));
        if (opt.count() == 0)
            opt = dropdown.getByText(hhmm12, new Locator.GetByTextOptions().setExact(true));
        if (opt.count() == 0)
            opt = dropdown.getByText(hhmm12AmPm, new Locator.GetByTextOptions().setExact(true));
        if (opt.count() == 0)
            opt = dropdown.getByText(hhmm24, new Locator.GetByTextOptions().setExact(true));
        if (opt.count() == 0)
            opt = dropdown.locator("[title='" + hhmm12 + "']");
        if (opt.count() == 0)
            opt = dropdown.locator("[title='" + hhmm12AmPm + "']");
        if (opt.count() == 0)
            opt = dropdown.locator("[title='" + hhmm24 + "']");

        if (opt.count() > 0) {
            // ensure visible
            WaitUtils.waitForVisible(opt.first(), 2000);
            clickWithRetry(opt.first(), 2, 150);
            logger.info("Picked time {}", opt.first().innerText());
            return;
        }

        // If not found, try future candidates derived from the adjusted base time
        if (today) {
            String[] candidates = buildFutureTimeCandidates(now);
            logger.info("Initial time in the past; trying candidates: {}", String.join(", ", candidates));
            pickTimeCandidates(candidates);
        } else {
            logger.warn("Time option not found for {} or {}", hhmm12, hhmm24);
        }
    }

    private String[] buildFutureTimeCandidates(LocalDateTime base) {
        // Build a few future slots (rounded to next 30 minutes) so that Register can enable
        LocalDateTime t0 = base.plusMinutes(5);
        int minute = t0.getMinute();
        int toNext30 = ((minute < 30) ? (30 - minute) : (60 - minute));
        LocalDateTime s1 = t0.plusMinutes(toNext30);
        LocalDateTime s2 = s1.plusMinutes(30);
        LocalDateTime s3 = s2.plusMinutes(30);
        DateTimeFormatter f24 = DateTimeFormatter.ofPattern("HH:mm");
        return new String[]{s1.format(f24), s2.format(f24), s3.format(f24)};
    }

    // Optional helper if callers want to specify explicit time strings (e.g., 18:30 -> 19:00 fallback)
    @Step("Pick time candidates: {times}")
    public void pickTimeCandidates(String... times) {
        // Open the time select near the Date field (robust against rc_select_* changes)
        boolean opened = false;
        try {
            Locator dateInput = page.getByPlaceholder(DATE_PLACEHOLDER).first();
            Locator timeSelector = dateInput.locator("xpath=ancestor::div[contains(@class,'ant-form-item')][1]//following::div[contains(@class,'ant-select')][1]//div[contains(@class,'ant-select-selector')]");
            if (timeSelector.count() > 0) {
                timeSelector.first().click();
                opened = true;
            }
            if (!opened) {
                Locator anySelector = page.locator(".ant-select-selector");
                if (anySelector.count() > 0) {
                    anySelector.first().click();
                    logger.debug("Opened time selector using fallback method");
                }
            }
        } catch (Exception ignored) {}
        // 3) Ensure the date picker overlay is not blocking the time dropdown
        Locator visibleDropdown = page.locator(".ant-select-dropdown:visible");
        Locator dateDropdown = page.locator(".ant-picker-dropdown:visible");
        if (dateDropdown.count() > 0) {
            try { page.keyboard().press("Escape"); } catch (Exception ignored) {}
            page.waitForTimeout(150);
        }
        // If time dropdown still not visible, explicitly click the time selector again
        if (visibleDropdown.count() == 0) {
            try {
                Locator dateInput = page.getByPlaceholder(DATE_PLACEHOLDER).first();
                Locator timeSelector = dateInput.locator("xpath=ancestor::div[contains(@class,'ant-form-item')][1]//following::div[contains(@class,'ant-select')][1]//div[contains(@class,'ant-select-selector')]");
                if (timeSelector.count() > 0) {
                    timeSelector.first().click();
                } else {
                    page.locator(".ant-select-selector").first().click();
                }
            } catch (Exception ignored) {}
        }
        // Wait for dropdown to appear
        WaitUtils.waitForDropdownVisible(page, 3000);
        // Re-resolve visible dropdown after potential state changes
        visibleDropdown = page.locator(".ant-select-dropdown:visible");

        // 4) Try provided candidates against the visible dropdown first
        for (String t : times) {
            if (t == null || t.isEmpty()) continue;
            Locator opt = (visibleDropdown.count() > 0)
                    ? visibleDropdown.first().getByText(t, new Locator.GetByTextOptions().setExact(true))
                    : page.getByText(t, new Page.GetByTextOptions().setExact(true));
            if (opt.count() == 0) {
                opt = page.locator("#root").getByText(t, new Locator.GetByTextOptions().setExact(true));
            }
            if (opt.count() > 0) {
                try { opt.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                WaitUtils.waitForVisible(opt.first(), 2000);
                clickWithRetry(opt.first(), 2, 200);
                logger.info("Picked time {}", t);
                return;
            }
            // Typing fallback into combobox input then Enter
            Locator input = (visibleDropdown.count() > 0)
                    ? visibleDropdown.first().locator("input[role='combobox'], input")
                    : page.locator("input[role='combobox'], .ant-select-selector input");
            if (input.count() > 0) {
                try {
                    input.first().click();
                    input.first().fill("");
                    input.first().fill(t);
                    page.keyboard().press("Enter");
                    logger.info("Typed time {} and pressed Enter", t);
                    return;
                } catch (Exception ignored) {}
            }
        }

        // 5) Fallback: try a few future time candidates derived from now
        DateTimeFormatter f24 = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        int minute = now.getMinute();
        int toNext30 = ((minute < 30) ? (30 - minute) : (60 - minute));
        String[] fallback = new String[]{
                now.plusMinutes(toNext30).format(f24),
                now.plusMinutes(toNext30 + 30).format(f24),
                now.plusMinutes(toNext30 + 60).format(f24)
        };
        for (String t : fallback) {
            Locator opt = (visibleDropdown.count() > 0)
                    ? visibleDropdown.first().getByText(t, new Locator.GetByTextOptions().setExact(true))
                    : page.getByText(t, new Page.GetByTextOptions().setExact(true));
            if (opt.count() == 0) {
                opt = page.locator("#root").getByText(t, new Locator.GetByTextOptions().setExact(true));
            }
            if (opt.count() > 0) {
                try { opt.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
                WaitUtils.waitForVisible(opt.first(), 2000);
                clickWithRetry(opt.first(), 2, 200);
                logger.info("Picked fallback time {}", t);
                return;
            }
        }

        // 6) Final attempt: type a candidate directly and press Enter
        String candidate = null;
        for (String t : times) { if (t != null && !t.isEmpty()) { candidate = t; break; } }
        if (candidate == null) { candidate = fallback[0]; }

        // Re-evaluate dropdown/input in case it changed
        visibleDropdown = page.locator(".ant-select-dropdown:visible");
        Locator input = (visibleDropdown.count() > 0)
                ? visibleDropdown.first().locator("input[role='combobox'], input")
                : page.locator("input[role='combobox'], .ant-select-selector input");
        if (input.count() > 0) {
            try {
                input.first().click();
                input.first().fill("");
                input.first().fill(candidate);
                page.keyboard().press("Enter");
                page.waitForTimeout(200);
                logger.info("Final fallback: typed time '{}' and pressed Enter", candidate);
                return;
            } catch (Exception e) {
                logger.warn("Final typing fallback failed: {}", e.getMessage());
            }
        }

        logger.warn("No time option matched. Provided: {}. Fallback tried: {} / {} / {}",
                String.join(", ", times), fallback[0], fallback[1], fallback[2]);
    }

    @Step("Upload coverage image if provided")
    public void uploadCoverage(Path imagePath) {
        Locator section = page.locator("div").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^CoverageDescription$")));
        Locator btn = section.getByRole(AriaRole.BUTTON);
        if (imagePath == null || !Files.exists(imagePath)) {
            logger.warn("Coverage image not found, skipping upload: {}", imagePath);
            return;
        }

        // Prefer uploading to an actual input[type=file] inside the section (Ant Design wraps it inside .ant-upload)
        Locator fileInput = section.locator("input[type='file']");
        if (fileInput.count() > 0) {
            fileInput.first().setInputFiles(imagePath);
            try {
                Allure.addAttachment("Coverage image", Files.newInputStream(imagePath));
            } catch (IOException ignored) {
            }
            logger.info("Uploaded coverage image via input[type=file]: {}", imagePath);
            return;
        }

        // Fallback: click the upload button to trigger file chooser
        if (btn.count() > 0) {
            try {
                page.waitForFileChooser(() -> clickWithRetry(btn.first(), 2, 200)).setFiles(imagePath);
                try {
                    Allure.addAttachment("Coverage image", Files.newInputStream(imagePath));
                } catch (IOException ignored) {
                }
                logger.info("Uploaded coverage image via FileChooser: {}", imagePath);
                return;
            } catch (Exception e) {
                logger.warn("FileChooser upload failed: {}", e.getMessage());
            }
        }

        // Last attempt: try a global input[type=file]
        Locator anyInput = page.locator("input[type='file']");
        if (anyInput.count() > 0) {
            anyInput.first().setInputFiles(imagePath);
            try {
                Allure.addAttachment("Coverage image", Files.newInputStream(imagePath));
            } catch (IOException ignored) {
            }
            logger.info("Uploaded coverage image via global input[type=file]: {}", imagePath);
        } else {
            logger.warn("No file input found to upload coverage image");
        }
    }

    @Step("Set description")
    public void setDescription(String message) {
        Locator desc = page.getByPlaceholder(DESC_PLACEHOLDER);
        if (desc.count() > 0) {
            desc.first().click();
            String txt = message != null ? message : "Test";
            // Some forms require a minimum length; pad to at least 15 chars
            if (txt.length() < 15) {
                txt = txt + " - automated run";
            }
            desc.first().fill(txt);
            // Blur to trigger validation
            desc.first().press("Tab");
            logger.info("Filled description");
        }
    }

    @Step("Submit and verify success")
    public void submitAndVerify() {
        // Wait for Register to become enabled
        waitUntilRegisterEnabled(20000);
        Locator reg = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(REGISTER_BTN));
        // Extra guard: scroll into view and click with retry
        reg.scrollIntoViewIfNeeded();
        clickWithRetry(reg.first(), 3, 250);
        // Wait for success
        waitVisible(page.getByText(SUCCESS_TOAST), 20000);
        Locator firstImg = page.locator(".ant-col > img").first();
        if (firstImg.count() > 0) {
            waitVisible(firstImg, 10000);
        }
        logger.info("Live scheduled successfully");
    }

    // ================= Instant Live Flow =================

    @Step("Choose 'Start now' option for instant live")
    public void chooseStartNow() {
        page.locator("label").filter(new Locator.FilterOptions().setHasText("Start now")).click();
        logger.info("Selected 'Start now' option for instant live");
    }

    @Step("Click Register button to create live event")
    public void clickRegister() {
        Locator registerBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(REGISTER_BTN));
        waitVisible(registerBtn.first(), 15000);
        clickWithRetry(registerBtn.first(), 2, 200);
        logger.info("Clicked Register button");
    }

    @Step("Verify live started successfully")
    public void verifyLiveStarted() {
        Locator successText = page.getByText("Your live will be started");
        waitVisible(successText.first(), 20000);
        logger.info("Live started successfully - success message visible");
    }

    @Step("Ensure Access field is displayed")
    public void assertAccessFieldVisible() {
        Locator accessText = page.getByText("Access");
        waitVisible(accessText.first(), 10000);
        logger.info("Access field is displayed");
    }

    @Step("Ensure Price field is displayed")
    public void assertPriceFieldVisible() {
        Locator priceText = page.getByText("Price");
        waitVisible(priceText.first(), 10000);
        logger.info("Price field is displayed");
    }

    @Step("Ensure Chat field is displayed")
    public void assertChatFieldVisible() {
        Locator chatText = page.getByText("Chat");
        waitVisible(chatText.first(), 10000);
        logger.info("Chat field is displayed");
    }

    @Step("Ensure 'When ?' field is displayed")
    public void assertWhenFieldVisible() {
        Locator whenText = page.getByText("When ?");
        waitVisible(whenText.first(), 10000);
        logger.info("'When ?' field is displayed");
    }

    @Step("Create instant live event with Everyone access and 15€ price")
    public void createInstantLiveEveryone15Euro() {
        assertAccessFieldVisible();
        setAccessEveryone();
        assertPriceFieldVisible();
        setPriceEuro(15);
        assertChatFieldVisible();
        enableChatEveryoneIfPresent();
        assertWhenFieldVisible();
        chooseStartNow();
        clickRegister();
        verifyLiveStarted();
        logger.info("Instant live created successfully with Everyone access and 15€ price");
    }

    // ================= End Live Flow (Creator) =================

    @Step("Click close button to end live (creator side)")
    public void clickCloseLive() {
        Locator closeIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("close"));
        waitVisible(closeIcon.first(), 10000);
        clickWithRetry(closeIcon.first(), 2, 200);
        logger.info("Clicked close button to end live");
    }

    @Step("Verify end live confirmation dialog")
    public void assertEndLiveConfirmationVisible() {
        Locator confirmText = page.getByText("Do you want to end the live ?");
        waitVisible(confirmText.first(), 10000);
        logger.info("End live confirmation dialog visible");
    }

    @Step("Confirm end live by clicking 'Yes, end the live'")
    public void confirmEndLive() {
        Locator yesEndBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes, end the live"));
        waitVisible(yesEndBtn.first(), 10000);
        clickWithRetry(yesEndBtn.first(), 2, 200);
        logger.info("Confirmed end live");
    }

    @Step("End live stream (creator side)")
    public void endLiveStream() {
        clickCloseLive();
        assertEndLiveConfirmationVisible();
        confirmEndLive();
        logger.info("Live stream ended successfully");
    }

    // Helpers
    private void handleConversionPromptIfPresent() {
        if (page.getByText(CONVERSION_TOOLS_TEXT).count() > 0) {
            clickIUnderstandIfPresent();
        }
    }

    private void clickIUnderstandIfPresent() {
        Locator understand = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(I_UNDERSTAND_BTN));
        if (understand.count() > 0 && understand.first().isVisible()) {
            clickWithRetry(understand.first(), 2, 200);
        }
    }

    private void clickLiveExactWithRetry() {
        Locator liveExact = page.getByText(LIVE_TEXT, new Page.GetByTextOptions().setExact(true));
        // Wait briefly for it to be attached/visible, then click with retry
        waitVisible(liveExact.first(), 10000);
        try { liveExact.first().scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        clickWithRetry(liveExact.first(), 3, 250);
    }
}
