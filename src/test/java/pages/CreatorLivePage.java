package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class CreatorLivePage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(CreatorLivePage.class);

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
    public void openPlusMenu() {
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg, 15000);
        clickWithRetry(plusImg, 2, 300);
        handleConversionPromptIfPresent();
        logger.info("Opened plus menu");
    }

    public void navigateToLive() {
        // Either after plus or direct
        if (page.getByText(CONVERSION_TOOLS_TEXT).count() > 0) {
            clickIUnderstandIfPresent();
        }
        Locator liveExact = page.getByText(LIVE_TEXT, new Page.GetByTextOptions().setExact(true));
        if (liveExact.count() > 0) {
            clickWithRetry(liveExact.first(), 2, 200);
        }
        if (page.getByText(LIVE_ONBOARDING_TEXT).count() > 0) {
            clickIUnderstandIfPresent();
        }
        ensureLiveScreen();
    }

    public void ensureLiveScreen() {
        Locator live = page.getByText(LIVE_TEXT);
        waitVisible(live, 20000);
        logger.info("Live screen visible");
    }

    // Form steps
    public void setAccessEveryone() {
        clickLabelByText(ACCESS_EVERYONE);
        logger.info("Set access to Everyone");
    }

    public void setAccessSubscribers() {
        clickLabelByText(ACCESS_SUBSCRIBERS);
        logger.info("Set access to Subscribers");
    }

    public void setPriceEuro(int euro) {
        page.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^" + euro + "€$"))).click();
        logger.info("Set price to {}€", euro);
    }

    public void setPriceFree() {
        clickLabelByText(PRICE_FREE);
        logger.info("Set price to Free");
    }

    public void enableChatEveryoneIfPresent() {
        Locator chatBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ACCESS_EVERYONE));
        if (chatBtn.count() > 0) {
            clickWithRetry(chatBtn.first(), 2, 200);
            logger.info("Enabled chat with Everyone");
        }
    }

    public void enableChatSubscribersIfPresent() {
        Locator chatBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(ACCESS_SUBSCRIBERS));
        if (chatBtn.count() > 0) {
            clickWithRetry(chatBtn.first(), 2, 200);
            logger.info("Enabled chat with Subscribers");
        }
    }

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

    public void openEditEvent() {
        Locator edit = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(EDIT_BTN));
        waitVisible(edit.first(), 10000);
        clickWithRetry(edit.first(), 2, 200);
        logger.info("Clicked Edit on event");
    }

    public void ensureScheduledLiveScreen() {
        Locator txt = page.getByText(SCHEDULED_LIVE_TEXT);
        waitVisible(txt.first(), 15000);
        logger.info("Scheduled Live screen visible");
    }

    public void clickDeleteEvent() {
        Locator del = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(DELETE_EVENT_BTN));
        waitVisible(del.first(), 10000);
        clickWithRetry(del.first(), 2, 200);
        logger.info("Clicked Delete event");
    }

    public void confirmDeleteYes() {
        // Ensure confirm text appears
        Locator confirmTxt = page.getByText(DELETE_CONFIRM_TEXT);
        waitVisible(confirmTxt.first(), 10000);
        // Click Yes delete
        Locator yes = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(YES_DELETE_BTN));
        clickWithRetry(yes.first(), 2, 200);
        logger.info("Confirmed delete");
    }

    public void verifyDeleteSuccessToast() {
        Locator toast = page.getByText(DELETE_SUCCESS_TOAST);
        waitVisible(toast.first(), 15000);
        logger.info("Delete success toast visible");
    }

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

    public void chooseSchedule() {
        page.locator("label").filter(new Locator.FilterOptions().setHasText(WHEN_SCHEDULE)).click();
        logger.info("Selected Schedule option");
    }

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

    public void pickTime(LocalDateTime when) {
        // If selecting today but time is in the past, try the next sensible future slots
        boolean today = when.toLocalDate().isEqual(LocalDate.now());
        LocalDateTime now = LocalDateTime.now();

        String hhmm12 = when.format(DateTimeFormatter.ofPattern("hh:mm"));
        String hhmm24 = when.format(DateTimeFormatter.ofPattern("HH:mm"));

        // Some envs use rc_select_0, others rc_select_1
        String[] timeSelectors = new String[]{"#rc_select_0", "#rc_select_1"};
        boolean opened = false;
        for (String sel : timeSelectors) {
            Locator dd = page.locator(sel);
            if (dd.count() > 0) {
                dd.first().click();
                opened = true;
                break;
            }
        }

        if (!opened) {
            logger.warn("Time dropdown not found by known selectors. Trying to click Date field to reveal");
            page.getByPlaceholder(DATE_PLACEHOLDER).click();
        }

        // Try to pick time by exact text (12h then 24h)
        Locator opt = page.getByText(hhmm12, new Page.GetByTextOptions().setExact(true));
        if (opt.count() == 0) opt = page.getByText(hhmm24, new Page.GetByTextOptions().setExact(true));
        // Fallback: sometimes options are inside #root overlay
        if (opt.count() == 0)
            opt = page.locator("#root").getByText(hhmm12, new Locator.GetByTextOptions().setExact(true));
        if (opt.count() == 0)
            opt = page.locator("#root").getByText(hhmm24, new Locator.GetByTextOptions().setExact(true));

        if (opt.count() > 0) {
            clickWithRetry(opt.first(), 2, 150);
            logger.info("Picked time {}", opt.first().innerText());
            return;
        }

        // If not found or invalid (past time today), try future candidates
        if (today && when.isBefore(now)) {
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
    public void pickTimeCandidates(String... times) {
        String[] selectors = new String[]{"#rc_select_0", "#rc_select_1"};
        for (String sel : selectors) {
            Locator dd = page.locator(sel);
            if (dd.count() > 0) {
                dd.first().click();
                break;
            }
        }
        for (String t : times) {
            if (t == null || t.isEmpty()) continue;
            Locator opt = page.getByText(t, new Page.GetByTextOptions().setExact(true));
            if (opt.count() == 0)
                opt = page.locator("#root").getByText(t, new Locator.GetByTextOptions().setExact(true));
            if (opt.count() > 0) {
                clickWithRetry(opt.first(), 2, 150);
                logger.info("Picked time {}", t);
                return;
            }
        }
        logger.warn("No provided time candidates matched: {}", String.join(", ", times));
    }

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
}
