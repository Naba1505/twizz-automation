package pages.creator;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;
import utils.WaitUtils;

import java.nio.file.Paths;
import java.util.regex.Pattern;

public class CreatorRegistrationPage {
    private static final Logger logger = LoggerFactory.getLogger(CreatorRegistrationPage.class);
    private final Page page;

    // First Page Locators
    private final String nameInput = "input[name=\"name\"]";
    private final String usernameInput = "[placeholder='User name']";
    private final String firstNameInput = "[placeholder='First name']";
    private final String lastNameInput = "input[name=\"lastName\"]";
    private final String datePicker = "[placeholder='Date of birth']";
    private final String emailInput = "[placeholder='Email address']";
    private final String passwordInput = "[placeholder='Password']";
    private final String phoneNumberInput = "[placeholder='Number']";
    // Optional Instagram field (new)
    private final String instagramTextboxName = "Instagram";

    private final String registrationButton = "role=button[name='Registration']";

    // Second Page Locators
    private final String secondPageHeader = "What kind of content do you create?";

    // Third Page Locators
    private final String thirdPageHeader = "Subscription price";
    private final String subscriptionToggle = "text='euro per month' >> role=switch >> span.ant-switch-inner";
    private final String priceInput = "[placeholder='0.00€']";

    // Fourth Page Locators
    private final String fourthPageHeader = "Your status";
    private final String privateIndividualOption = "I am a private individual";
    private final String fourthPageContinueButton = "role=button[name='Continue']";

    // Fifth Page Locators
    private final String fifthPageHeader = "Identity verification";
    private final String firstImageUploadButton = "role=img[name='document'] >> nth=0";
    private final String finishButton = "role=button[name='FINISH']";
    // Final Confirmation
    private final String finalConfirmationText = "Thank you for your interest!";


    public CreatorRegistrationPage(Page page) {
        this.page = page;
    }

    public boolean isRegistrationFormVisible() {
        boolean isVisible = page.locator(nameInput).isVisible();
        logger.info("Registration form visibility: {}", isVisible);
        return isVisible;
    }

    public void navigate() {
        String creatorRegistrationUrl = ConfigReader.getCreatorRegistrationUrl();
        page.navigate(creatorRegistrationUrl);
        page.waitForLoadState();
        logger.info("Navigated to Creator Registration page: {}", creatorRegistrationUrl);
    }

    public void selectDateOfBirth(String dob) {
        String[] dateParts = dob.split("-");
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];

        // Map numeric month to German month name (as per Codegen)
        int monthIndex = Integer.parseInt(month) - 1; // 0-based index for AntD month grid
        String dayText = String.valueOf(Integer.parseInt(day)); // normalize no leading zero

        page.locator(datePicker).click();
        logger.info("Clicked date picker");

        // Wait for visible picker
        Locator picker = page.locator(".ant-picker-dropdown:visible");
        if (!WaitUtils.waitForVisible(picker, 5000)) {
            logger.warn("Date picker dropdown did not become visible; attempting typing fallback");
            typeDobFallback(day, month, year);
            return;
        }

        // Move to year panel using AntD header buttons (locale-agnostic)
        Locator yearBtn = picker.locator(".ant-picker-year-btn");
        if (yearBtn.count() > 0) {
            yearBtn.first().click();
            page.waitForTimeout(100);
            // Some versions need a second click to move to decade view
            if (picker.locator(".ant-picker-year-panel").count() == 0) {
                yearBtn.first().click();
            }
        } else {
            // Fallback to header view buttons
            Locator headerViewBtn = picker.locator(".ant-picker-header-view button");
            int toggleGuard = 0;
            while (picker.locator(".ant-picker-year-panel").count() == 0 && toggleGuard++ < 3) {
                if (headerViewBtn.count() > 0) headerViewBtn.first().click();
                page.waitForTimeout(100);
            }
        }

        // If year panel still not visible quickly, fall back to typing to avoid flakiness
        if (picker.locator(".ant-picker-year-panel").count() == 0) {
            logger.warn("Year panel not detected; using typing fallback for DOB");
            typeDobFallback(day, month, year);
            return;
        }
        if (!WaitUtils.waitForVisible(picker.locator(".ant-picker-year-panel"), 2000)) {
            logger.warn("Year panel not visible after toggle; using typing fallback for DOB");
            typeDobFallback(day, month, year);
            return;
        }

        // Navigate decades until target year is within the visible range
        int targetYear = Integer.parseInt(year);
        int navGuard = 0;
        while (navGuard++ < 24) { // up to ~24 decade moves
            Locator yCells = picker.locator(".ant-picker-year-panel .ant-picker-cell .ant-picker-cell-inner");
            if (yCells.count() == 0) break;
            String firstTxt = yCells.first().innerText().replaceAll("[^0-9]", "");
            String lastTxt = yCells.nth(yCells.count() - 1).innerText().replaceAll("[^0-9]", "");
            try {
                int minY = Integer.parseInt(firstTxt);
                int maxY = Integer.parseInt(lastTxt);
                if (targetYear >= minY && targetYear <= maxY) {
                    break; // target in view
                }
                if (targetYear < minY) {
                    Locator prev = picker.locator(".ant-picker-header-super-prev-btn");
                    if (prev.count() > 0) { prev.first().click(); page.waitForTimeout(100); } else { break; }
                } else {
                    Locator next = picker.locator(".ant-picker-header-super-next-btn");
                    if (next.count() > 0) { next.first().click(); page.waitForTimeout(100); } else { break; }
                }
            } catch (Exception ignored) { break; }
        }

        // Select desired year (click enabled cell)
        Locator yearCell = picker.locator(".ant-picker-year-panel .ant-picker-cell:not(.ant-picker-cell-disabled) .ant-picker-cell-inner").filter(new Locator.FilterOptions().setHasText(year));
        WaitUtils.waitForVisible(yearCell, 3000);
        yearCell.first().click();
        logger.info("Selected year: {}", year);

        // Wait for month panel and select month by index (locale-agnostic)
        WaitUtils.waitForVisible(picker.locator(".ant-picker-month-panel"), 3000);
        Locator monthCells = picker.locator(".ant-picker-month-panel .ant-picker-cell:not(.ant-picker-cell-disabled)");
        if (monthCells.count() >= monthIndex + 1) {
            monthCells.nth(monthIndex).locator(".ant-picker-cell-inner").click();
            logger.info("Selected month index: {}", monthIndex + 1);
        } else {
            logger.warn("Month cells not sufficient ({}), expected index {}. Falling back to first month.", monthCells.count(), monthIndex);
            monthCells.first().locator(".ant-picker-cell-inner").click();
        }

        // Wait for date panel and select day (enabled cell only)
        WaitUtils.waitForVisible(picker.locator(".ant-picker-date-panel"), 3000);
        Locator dayCell = picker.locator(".ant-picker-date-panel .ant-picker-cell-in-view:not(.ant-picker-cell-disabled) .ant-picker-cell-inner").filter(new Locator.FilterOptions().setHasText(dayText));
        WaitUtils.waitForVisible(dayCell, 3000);
        dayCell.first().click();
        logger.info("Selected day: {}", dayText);

        // Close picker robustly and verify input is populated
        try {
            page.keyboard().press("Escape");
            page.waitForTimeout(100);
        } catch (Exception ignored) {}
        if (page.locator(".ant-picker-dropdown:visible").count() > 0) {
            page.getByRole(AriaRole.MAIN).click();
        }
        // Wait until dropdown is hidden (best-effort)
        try {
            Locator visible = page.locator(".ant-picker-dropdown:visible");
            if (visible.count() > 0) {
                visible.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(2000));
            }
        } catch (Exception ignored) {}

        // Ensure the date input has a value, otherwise fallback to typing
        try {
            Locator input = page.locator(datePicker).first();
            String v = input.inputValue();
            if (v == null || v.isEmpty()) {
                logger.warn("Picker selection did not populate DOB field; using typing fallback");
                typeDobFallback(day, month, year);
            }
        } catch (Exception ignored) {}
        logger.info("Closed date picker");

        // Verify picker closed and input populated; fallback if not
        try {
            boolean closed = page.locator(".ant-picker-dropdown:visible").count() == 0;
            boolean hasValue = !page.locator(datePicker).first().inputValue().isEmpty();
            if (!closed || !hasValue) {
                logger.warn("Picker not closed or value empty -> using typing fallback");
                typeDobFallback(day, month, year);
            }
            // Move focus away from date input to avoid reopening the dropdown
            try {
                page.keyboard().press("Tab");
                page.waitForTimeout(100);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    private void typeDobFallback(String day, String month, String year) {
        // Try common formats accepted by AntD depending on locale
        String dd = String.format("%02d", Integer.parseInt(day));
        String mm = String.format("%02d", Integer.parseInt(month));
        String yyyy = year;
        String[] formats = new String[] {
                String.format("%s.%s.%s", dd, mm, yyyy),    // dd.MM.yyyy
                String.format("%s/%s/%s", dd, mm, yyyy),    // dd/MM/yyyy
                String.format("%s-%s-%s", dd, mm, yyyy),    // dd-MM-yyyy
                String.format("%s-%s-%s", yyyy, mm, dd)     // yyyy-MM-dd
        };
        Locator input = page.locator(datePicker);
        input.click();
        for (String fmt : formats) {
            try {
                input.fill("");
                input.fill(fmt);
                input.press("Enter");
                page.waitForTimeout(200);
                String v = input.inputValue();
                logger.info("DOB typing attempt '{}' -> '{}'", fmt, v);
                if (v != null && !v.isEmpty()) {
                    // Close dropdown if open
                    if (page.locator(".ant-picker-dropdown:visible").count() > 0) {
                        page.getByRole(AriaRole.MAIN).click();
                    }
                    return;
                }
            } catch (Exception ignored) {}
        }
        logger.warn("DOB typing fallback did not populate value; proceeding with flow");
    }

    public void selectGender(String gender) {
        String normalizedGender = gender.substring(0, 1).toUpperCase() + gender.substring(1).toLowerCase();
        if ("Female".equals(normalizedGender)) {
            page.getByText("Female", new Page.GetByTextOptions().setExact(true)).click();
        } else {
            page.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^" + normalizedGender + "$"))).click();
        }
        logger.info("Selected gender: {}", normalizedGender);
    }

    public void fillRegistrationForm(String name, String username, String firstName, String lastName,
                                     String dob, String email, String password, String phoneNumber,
                                     String instagramUrl, String gender) {

        page.locator(nameInput).fill(name);
        logger.info("Filled name: {}", name);

        page.locator(usernameInput).fill(username);
        logger.info("Filled username: {}", username);

        page.locator(firstNameInput).fill(firstName);
        logger.info("Filled first name: {}", firstName);

        page.locator(lastNameInput).fill(lastName);
        logger.info("Filled last name: {}", lastName);

        selectDateOfBirth(dob);
        logger.info("Filled date of birth: {}", dob);
        // Ensure focus is on email field and picker is not obstructing
        try {
            Locator emailLoc = page.locator(emailInput).first();
            emailLoc.click();
            // Best-effort ensure no dropdown overlays
            Locator dd = page.locator(".ant-picker-dropdown:visible");
            if (dd.count() > 0) {
                page.keyboard().press("Escape");
                page.waitForTimeout(100);
            }
            emailLoc.fill(email);
        } catch (Exception e) {
            logger.warn("Email fill encountered overlay; retrying after blur: {}", e.getMessage());
            try { page.evaluate("() => document.activeElement && document.activeElement.blur() "); } catch (Exception ignored) {}
            page.locator(emailInput).first().click();
            page.locator(emailInput).first().fill(email);
        }
        logger.info("Filled email: {}", email);

        page.locator(passwordInput).fill(password);
        logger.info("Filled password: [HIDDEN]");

        page.locator(phoneNumberInput).fill(phoneNumber);
        logger.info("Filled phone number: {}", phoneNumber);

        // Optional Instagram field – fill only when value is provided
        if (instagramUrl != null && !instagramUrl.isEmpty()) {
            try {
                Locator insta = page.getByRole(AriaRole.TEXTBOX,
                        new Page.GetByRoleOptions().setName(instagramTextboxName));
                insta.click();
                insta.fill(instagramUrl);
                logger.info("Filled Instagram: {}", instagramUrl);
            } catch (Exception e) {
                logger.warn("Failed to fill Instagram field, proceeding without it: {}", e.getMessage());
            }
        } else {
            logger.info("Instagram value not provided; skipping optional Instagram field");
        }

        selectGender(gender);
    }

    public void nextPageSubmit() {
        page.locator(registrationButton).click();
        logger.info("Clicked Registration button");
        // Allow the next page to render/load before checks
        try {
            page.waitForLoadState();
            // Small breathing time to allow UI render if needed
            page.waitForTimeout(1000);
        } catch (Exception e) {
            logger.warn("Post-submit load wait timed out or not applicable: {}", e.getMessage());
        }
    }

    public boolean isSecondPageVisible() {
        try {
            Locator header = page.getByText(secondPageHeader, new Page.GetByTextOptions().setExact(true));
            header.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
            boolean isVisible = header.isVisible();
            logger.info("Second page visibility: {}", isVisible);
            return isVisible;
        } catch (Exception primary) {
            logger.warn("Exact header not immediately visible: {}. Trying fallback...", primary.getMessage());
            try {
                Locator headingRole = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(secondPageHeader));
                headingRole.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                logger.info("Second page visibility via heading role: true");
                return true;
            } catch (Exception fallback) {
                logger.warn("Second page not visible after fallback: {}", fallback.getMessage());
                // Final fallback: wait for a known option label to appear (e.g., 'Model')
                try {
                    Locator optionModel = page.getByText("Model", new Page.GetByTextOptions().setExact(true));
                    optionModel.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                    logger.info("Second page visibility via known option label: true");
                    return true;
                } catch (Exception last) {
                    logger.warn("Second page not visible after all fallbacks: {}", last.getMessage());
                    return false;
                }
            }
        }
    }

    public void fillSecondPageForm(String[] contentTypes) {
        for (String contentType : contentTypes) {
            page.getByText(contentType, new Page.GetByTextOptions().setExact(true)).click();
            logger.info("Selected content type: {}", contentType);
        }
    }

    public void submitSecondPage() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue").setExact(true)).click();
        logger.info("Clicked Continue button on second page");
        // Wait for transition to third page
        try {
            page.waitForLoadState();
            page.waitForTimeout(1000);
        } catch (Exception e) {
            logger.warn("Post second-page submit wait encountered issue: {}", e.getMessage());
        }
    }

    public boolean isThirdPageVisible() {
        try {
            Locator header = page.getByText(thirdPageHeader, new Page.GetByTextOptions().setExact(true));
            header.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
            boolean isVisible = header.isVisible();
            logger.info("Third page visibility: {}", isVisible);
            return isVisible;
        } catch (Exception primary) {
            logger.warn("Exact third-page header not immediately visible: {}. Trying fallbacks...", primary.getMessage());
            // Fallback 1: heading role
            try {
                Locator headingRole = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(thirdPageHeader));
                headingRole.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                logger.info("Third page visibility via heading role: true");
                return true;
            } catch (Exception fallback1) {
                logger.warn("Heading role check failed: {}. Trying control fallbacks...", fallback1.getMessage());
                // Fallback 2: check for price input
                try {
                    Locator price = page.locator(priceInput).first();
                    price.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                    logger.info("Third page visibility via price input: true");
                    return true;
                } catch (Exception fallback2) {
                    // Fallback 3: check for subscription toggle
                    try {
                        Locator toggle = page.locator(subscriptionToggle);
                        toggle.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                        logger.info("Third page visibility via subscription toggle: true");
                        return true;
                    } catch (Exception last) {
                        logger.warn("Third page not visible after all fallbacks: {}", last.getMessage());
                        return false;
                    }
                }
            }
        }
    }

    public void fillThirdPageForm(String subscriptionPrice) {
        page.locator(priceInput).first().fill(subscriptionPrice);
        logger.info("Filled subscription price: {}", subscriptionPrice);
    }

    public void submitThirdPage() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue").setExact(true)).click();
        logger.info("Clicked Continue button on third page");
        // Wait for transition to fourth page
        try {
            page.waitForLoadState();
            page.waitForTimeout(1000);
        } catch (Exception e) {
            logger.warn("Post third-page submit wait encountered issue: {}", e.getMessage());
        }
    }

    public boolean isFourthPageVisible() {
        try {
            Locator header = page.getByText(fourthPageHeader, new Page.GetByTextOptions().setExact(true));
            header.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
            boolean isVisible = header.isVisible();
            logger.info("Fourth page visibility: {}", isVisible);
            return isVisible;
        } catch (Exception primary) {
            logger.warn("Exact fourth-page header not immediately visible: {}. Trying fallbacks...", primary.getMessage());
            // Fallback 1: heading role
            try {
                Locator headingRole = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(fourthPageHeader));
                headingRole.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                logger.info("Fourth page visibility via heading role: true");
                return true;
            } catch (Exception fallback1) {
                logger.warn("Heading role check failed: {}. Trying control fallbacks...", fallback1.getMessage());
                // Fallback 2: known option label
                try {
                    Locator option = page.getByText(privateIndividualOption, new Page.GetByTextOptions().setExact(true));
                    option.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                    logger.info("Fourth page visibility via known option label: true");
                    return true;
                } catch (Exception fallback2) {
                    // Fallback 3: Continue button on page
                    try {
                        Locator cont = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue").setExact(true));
                        cont.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                        logger.info("Fourth page visibility via Continue button: true");
                        return true;
                    } catch (Exception last) {
                        logger.warn("Fourth page not visible after all fallbacks: {}", last.getMessage());
                        return false;
                    }
                }
            }
        }
    }

    public void fillFourthPageForm() {
        try {
            Locator option = page.getByText(privateIndividualOption, new Page.GetByTextOptions().setExact(true));
            option.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
            option.click();
            logger.info("Selected status: {}", privateIndividualOption);
        } catch (Exception e) {
            logger.warn("Failed to click known status option directly: {}. Trying label fallback...", e.getMessage());
            page.locator("label").filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^" + java.util.regex.Pattern.quote(privateIndividualOption) + "$"))).click();
            logger.info("Selected status via label filter: {}", privateIndividualOption);
        }
    }

    public void submitFourthPage() {
        page.waitForLoadState();
        page.locator(fourthPageContinueButton).click();
        logger.info("Clicked Continue button on fourth page");
        // Wait for transition to fifth page
        try {
            page.waitForLoadState();
            page.waitForTimeout(1000);
        } catch (Exception e) {
            logger.warn("Post fourth-page submit wait encountered issue: {}", e.getMessage());
        }
    }

    public boolean isFifthPageVisible() {
        try {
            Locator header = page.getByText(fifthPageHeader, new Page.GetByTextOptions().setExact(true));
            header.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
            boolean isVisible = header.isVisible();
            logger.info("Fifth page visibility: {}", isVisible);
            return isVisible;
        } catch (Exception primary) {
            logger.warn("Exact fifth-page header not immediately visible: {}. Trying fallbacks...", primary.getMessage());
            // Fallback 1: heading role
            try {
                Locator headingRole = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(fifthPageHeader));
                headingRole.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                logger.info("Fifth page visibility via heading role: true");
                return true;
            } catch (Exception fallback1) {
                logger.warn("Heading role check failed: {}. Trying control fallbacks...", fallback1.getMessage());
                // Fallback 2: presence of first upload image placeholder
                try {
                    Locator firstImg = page.locator(firstImageUploadButton);
                    firstImg.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                    logger.info("Fifth page visibility via first upload image: true");
                    return true;
                } catch (Exception fallback2) {
                    // Fallback 3: FINISH button visible
                    try {
                        Locator finish = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("FINISH").setExact(true));
                        finish.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                        logger.info("Fifth page visibility via FINISH button: true");
                        return true;
                    } catch (Exception last) {
                        logger.warn("Fifth page not visible after all fallbacks: {}", last.getMessage());
                        return false;
                    }
                }
            }
        }
    }

    public void uploadDocuments(String identityFilePath, String selfieFilePath) {
        // Ensure fifth page is fully loaded
        isFifthPageVisible();
        page.waitForTimeout(2000); // brief stabilization

        // Drive the underlying Ant Upload file inputs directly to avoid native OS dialogs.
        Locator inputs = page.locator(".ant-upload input[type='file']");

        long deadline = System.currentTimeMillis() + 5_000L;
        while (inputs.count() < 2 && System.currentTimeMillis() < deadline) {
            try { page.waitForTimeout(200); } catch (Throwable ignored) { }
        }

        int count = inputs.count();
        if (count < 2) {
            throw new RuntimeException("Expected two Ant Upload file inputs for identity/selfie, but found " + count);
        }

        // Upload identity document via first input
        Locator firstInput = inputs.nth(0);
        firstInput.setInputFiles(Paths.get(identityFilePath));
        logger.info("Uploaded identity document via input[type=file]: {}", identityFilePath);

        // Upload selfie document via second input
        Locator secondInput = inputs.nth(1);
        secondInput.setInputFiles(Paths.get(selfieFilePath));
        logger.info("Uploaded selfie document via input[type=file]: {}", selfieFilePath);
    }

    public void submitFifthPage() {
        page.waitForLoadState();
        page.locator(finishButton).click();
        logger.info("Clicked FINISH button on fifth page");
    }

    public boolean isFinalConfirmationVisible() {
        try {
            Locator thankYou = page.getByText(finalConfirmationText, new Page.GetByTextOptions().setExact(true));
            thankYou.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(20000));
            boolean visible = thankYou.isVisible();
            logger.info("Final confirmation visibility: {}", visible);
            return visible;
        } catch (Exception primary) {
            logger.warn("Exact final confirmation text not immediately visible: {}. Trying heading fallback...", primary.getMessage());
            try {
                Locator heading = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(finalConfirmationText));
                heading.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                logger.info("Final confirmation visible via heading role");
                return true;
            } catch (Exception last) {
                logger.warn("Final confirmation not visible after fallbacks: {}", last.getMessage());
                return false;
            }
        }
    }

    /**
     * Orchestrates the full creator registration flow using existing step methods.
     */
    public void completeRegistrationFlow(
            String name,
            String username,
            String firstName,
            String lastName,
            String dob,
            String email,
            String password,
            String phoneNumber,
            String instagramUrl,
            String gender,
            String[] contentTypes,
            String subscriptionPrice,
            String identityFilePath,
            String selfieFilePath
    ) {
        logger.info("Starting complete creator registration flow");

        // Page 1
        navigate();
        if (!isRegistrationFormVisible()) {
            throw new IllegalStateException("Registration form is not visible on page 1");
        }
        fillRegistrationForm(name, username, firstName, lastName, dob, email, password, phoneNumber, instagramUrl, gender);

        nextPageSubmit();

        // Page 2
        if (!isSecondPageVisible()) {
            throw new IllegalStateException("Second page is not visible after submitting page 1");
        }
        fillSecondPageForm(contentTypes);
        submitSecondPage();

        // Page 3
        if (!isThirdPageVisible()) {
            throw new IllegalStateException("Third page is not visible after submitting page 2");
        }
        fillThirdPageForm(subscriptionPrice);
        submitThirdPage();

        // Page 4
        if (!isFourthPageVisible()) {
            throw new IllegalStateException("Fourth page is not visible after submitting page 3");
        }
        fillFourthPageForm();
        submitFourthPage();

        // Page 5
        if (!isFifthPageVisible()) {
            throw new IllegalStateException("Fifth page is not visible after submitting page 4");
        }
        uploadDocuments(identityFilePath, selfieFilePath);
        submitFifthPage();

        // Confirm final thank-you message appears
        if (!isFinalConfirmationVisible()) {
            throw new IllegalStateException("Final confirmation message not visible after FINISH submission");
        }
        logger.info("Creator registration flow completed successfully");
    }
}
