package pages.creator;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import pages.common.BasePage;
import utils.ConfigReader;
import utils.WaitUtils;

import java.nio.file.Paths;
import java.util.regex.Pattern;

public class CreatorRegistrationPage extends BasePage {


    // First Page Locators
    private final String nameInput = "input[name=\"name\"]";
    private final String usernameInput = "[placeholder='User name']";
    private final String firstNameInput = "[placeholder='First name']";
    private final String lastNameInput = "input[name=\"lastName\"]";
    private final String datePicker = "input.datePicker.darkDatePickerSignup";
    private final String emailInput = "[placeholder='Email address']";
    private final String passwordInput = "[placeholder='Password']";
    private final String phoneNumberInput = "[placeholder='Number']";
    // Optional Instagram field (new)
    private final String instagramTextboxName = "Instagram";

    // Second Page Locators
    private final String secondPageHeader = "What kind of content do you create?";

    // Third Page Locators
    private final String thirdPageHeader = "Subscription price";
    private final String subscriptionToggle = "text='euro per month' >> role=switch >> span.ant-switch-inner";
    private final String priceInput = "[placeholder='0.00€']";

    // Fourth Page Locators
    private final String fourthPageHeader = "Your status";
    private final String privateIndividualOption = "I am a private individual";

    // Fifth Page Locators
    private final String fifthPageHeader = "Identity verification";
    // Final Confirmation
    private final String finalConfirmationText = "Thank you for your interest!";

    private Locator registrationButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Registration"));
    }

    private Locator continueButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue").setExact(true));
    }

    private Locator finishButton() {
        return page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("FINISH").setExact(true));
    }

    private Locator firstImageUploadButton() {
        return page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("document")).first();
    }


    public CreatorRegistrationPage(Page page) {
        super(page);
    }

    private boolean isPageVisible(String headerText, Locator... fallbackLocators) {
        try {
            Locator header = page.getByText(headerText, new Page.GetByTextOptions().setExact(true));
            header.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(ConfigReader.getVisibilityTimeout()));
            boolean visible = header.isVisible();
            logger.info("Page '{}' visibility via exact header: {}", headerText, visible);
            return visible;
        } catch (Exception primary) {
            logger.warn("Exact header '{}' not visible: {}. Trying fallbacks...", headerText, primary.getMessage());
            for (int i = 0; i < fallbackLocators.length; i++) {
                try {
                    Locator fallback = fallbackLocators[i];
                    fallback.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(ConfigReader.getMediumTimeout()));
                    logger.info("Page '{}' visibility via fallback #{}: true", headerText, i + 1);
                    return true;
                } catch (Exception e) {
                    logger.warn("Fallback #{} failed: {}", i + 1, e.getMessage());
                }
            }
            logger.warn("Page '{}' not visible after all fallbacks", headerText);
            return false;
        }
    }

    public boolean isRegistrationFormVisible() {
        Locator nameField = page.locator(nameInput);
        try {
            nameField.first().scrollIntoViewIfNeeded();
            nameField.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Name input wait failed: {}", e.getMessage()); }
        boolean isVisible = safeIsVisible(nameField);
        logger.info("Registration form visibility: {}", isVisible);
        return isVisible;
    }

    public void navigate() {
        String creatorRegistrationUrl = ConfigReader.getCreatorRegistrationUrl();
        page.navigate(creatorRegistrationUrl);
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
        logger.info("Navigated to Creator Registration page: {}", creatorRegistrationUrl);
    }

    public void selectDateOfBirth(String dob) {
        String[] dateParts = dob.split("-");
        String day = dateParts[0];
        String month = dateParts[1];
        String year = dateParts[2];
        String monthName = monthNumberToName(Integer.parseInt(month));
        String dayText = String.valueOf(Integer.parseInt(day));

        if (!openDatePickerModal()) {
            typeDobFallback(day, month, year);
            return;
        }

        try {
            selectYearInCalendar(year);
            selectMonthInCalendar(monthName);
            selectDayInCalendar(dayText);
            confirmDateSelection();
            verifyDateInputOrFallback(day, month, year);
        } catch (Exception e) {
            logger.warn("Calendar date selection failed: {}. Falling back to typing.", e.getMessage());
            typeDobFallback(day, month, year);
        }
    }

    private String monthNumberToName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                               "July", "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
    }

    private boolean openDatePickerModal() {
        Locator input = page.locator(datePicker);
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                input.scrollIntoViewIfNeeded();
                input.click();
                logger.info("Clicked date picker (attempt {})", attempt);
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
                Locator overlay = page.locator(".calendar-modal-overlay");
                if (WaitUtils.waitForVisible(overlay, ConfigReader.getShortTimeout())) {
                    logger.info("Calendar modal opened successfully");
                    return true;
                }
            } catch (Exception e) {
                logger.warn("Attempt {} to open date picker failed: {}", attempt, e.getMessage());
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
            }
        }
        logger.warn("Failed to open calendar modal after 3 attempts");
        return false;
    }

    private void selectYearInCalendar(String year) {
        Locator yearHeader = page.locator(".calendar-header-text.clickable")
                .filter(new Locator.FilterOptions().setHasText(Pattern.compile("\\d{4}")));
        WaitUtils.waitForVisible(yearHeader, ConfigReader.getShortTimeout());
        yearHeader.first().click();
        logger.info("Clicked year header to open year selector");
        page.waitForTimeout(ConfigReader.getAnimationTimeout());

        Locator yearOption = page.getByText(year, new Page.GetByTextOptions().setExact(true));
        WaitUtils.waitForVisible(yearOption, ConfigReader.getShortTimeout());
        yearOption.click();
        logger.info("Selected year: {}", year);
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
    }

    private void selectMonthInCalendar(String monthName) {
        Locator monthHeader = page.locator(".calendar-header-text.clickable").first();
        WaitUtils.waitForVisible(monthHeader, ConfigReader.getShortTimeout());
        monthHeader.click();
        logger.info("Clicked month header to open month selector");
        page.waitForTimeout(ConfigReader.getAnimationTimeout());

        Locator monthOption = page.locator(".month-selector-item")
                .filter(new Locator.FilterOptions().setHasText(monthName));
        WaitUtils.waitForVisible(monthOption, ConfigReader.getShortTimeout());
        monthOption.click();
        logger.info("Selected month: {}", monthName);
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
    }

    private void selectDayInCalendar(String dayText) {
        Exception lastError = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                Locator dayOption = page.locator(".calendar-day")
                        .filter(new Locator.FilterOptions().setHasText(Pattern.compile("^" + dayText + "$")));
                if (dayOption.count() == 0) {
                    dayOption = page.getByText(dayText, new Page.GetByTextOptions().setExact(true));
                }
                WaitUtils.waitForVisible(dayOption, ConfigReader.getShortTimeout());
                dayOption.first().scrollIntoViewIfNeeded();
                dayOption.first().click();
                logger.info("Selected day: {} (attempt {})", dayText, attempt);
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
                return;
            } catch (Exception e) {
                lastError = e;
                logger.warn("Attempt {} to select day failed: {}", attempt, e.getMessage());
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
            }
        }
        throw new RuntimeException("Failed to select day: " + dayText, lastError);
    }

    private void confirmDateSelection() {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                Locator confirmBtn = page.getByRole(AriaRole.BUTTON,
                        new Page.GetByRoleOptions().setName("Confirm"));
                WaitUtils.waitForVisible(confirmBtn, ConfigReader.getShortTimeout());
                confirmBtn.scrollIntoViewIfNeeded();
                confirmBtn.click();
                logger.info("Clicked Confirm button (attempt {})", attempt);
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
                if (!page.locator(".calendar-modal-overlay").isVisible()) {
                    logger.info("Calendar modal closed successfully");
                    return;
                }
            } catch (Exception e) {
                logger.warn("Attempt {} to click Confirm failed: {}", attempt, e.getMessage());
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
            }
        }
        logger.warn("Failed to confirm date selection, attempting to close modal with Escape");
        try {
            page.keyboard().press("Escape");
            page.waitForTimeout(ConfigReader.getAnimationTimeout());
        } catch (Exception e) {
            logger.debug("Escape key press failed: {}", e.getMessage());
        }
    }

    private void verifyDateInputOrFallback(String day, String month, String year) {
        try {
            String inputValue = page.locator(datePicker).first().inputValue();
            if (inputValue == null || inputValue.isEmpty()) {
                logger.warn("Date input is empty after selection, using typing fallback");
                typeDobFallback(day, month, year);
            } else {
                logger.info("Date of birth selected successfully: {}-{}-{} (input value: {})", day, month, year, inputValue);
            }
        } catch (Exception e) {
            logger.warn("Could not verify date input value: {}", e.getMessage());
        }
    }

    private void typeDobFallback(String day, String month, String year) {
        String dd = String.format("%02d", Integer.parseInt(day));
        String mm = String.format("%02d", Integer.parseInt(month));
        String yyyy = year;
        String[] formats = new String[] {
                String.format("%s.%s.%s", dd, mm, yyyy),
                String.format("%s/%s/%s", dd, mm, yyyy),
                String.format("%s-%s-%s", dd, mm, yyyy),
                String.format("%s-%s-%s", yyyy, mm, dd)
        };
        Locator input = page.locator(datePicker);
        input.scrollIntoViewIfNeeded();
        input.click();
        for (String fmt : formats) {
            try {
                input.fill("");
                input.fill(fmt);
                input.press("Enter");
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
                String v = input.inputValue();
                logger.info("DOB typing attempt '{}' -> '{}'", fmt, v);
                if (v != null && !v.isEmpty()) {
                    Locator openDropdown = page.locator(".ant-picker-dropdown:visible");
                    if (safeIsVisible(openDropdown)) {
                        page.getByRole(AriaRole.MAIN).click();
                    }
                    return;
                }
            } catch (Exception e) {
                logger.debug("DOB typing fallback attempt failed: {}", e.getMessage());
            }
        }
        logger.warn("DOB typing fallback did not populate value; proceeding with flow");
    }

    public void selectGender(String gender) {
        String normalizedGender = gender.substring(0, 1).toUpperCase() + gender.substring(1).toLowerCase();
        Locator option = page.getByText(normalizedGender, new Page.GetByTextOptions().setExact(true));
        option.scrollIntoViewIfNeeded();
        option.click();
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
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
            }
            emailLoc.fill(email);
        } catch (Exception e) {
            logger.warn("Email fill encountered overlay; retrying after blur: {}", e.getMessage());
            try { page.evaluate("() => document.activeElement && document.activeElement.blur() "); } catch (Exception ex) { logger.debug("Blur evaluation failed: {}", ex.getMessage()); }
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
        Locator regButton = registrationButton();
        try {
            regButton.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(ConfigReader.getVisibilityTimeout()));

            long deadline = System.currentTimeMillis() + ConfigReader.getShortTimeout();
            while (!regButton.isEnabled() && System.currentTimeMillis() < deadline) {
                page.waitForTimeout(ConfigReader.getAnimationTimeout());
            }
            if (!regButton.isEnabled()) {
                logger.warn("Registration button still not enabled after wait, clicking anyway");
            }
        } catch (Exception e) {
            logger.warn("Registration button wait encountered issue: {}", e.getMessage());
        }

        regButton.scrollIntoViewIfNeeded();
        regButton.click();
        logger.info("Clicked Registration button");
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.LOAD,
                    new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
            page.waitForTimeout(ConfigReader.getElementRetryDelay());
        } catch (Exception e) {
            logger.warn("Post-submit load wait timed out or not applicable: {}", e.getMessage());
        }
    }

    public boolean isSecondPageVisible() {
        return isPageVisible(secondPageHeader,
                page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(secondPageHeader)),
                page.getByText("Model", new Page.GetByTextOptions().setExact(true)));
    }

    public void fillSecondPageForm(String[] contentTypes) {
        for (String contentType : contentTypes) {
            page.getByText(contentType, new Page.GetByTextOptions().setExact(true)).click();
            logger.info("Selected content type: {}", contentType);
        }
    }

    public void submitSecondPage() {
        Locator btn = continueButton();
        btn.scrollIntoViewIfNeeded();
        btn.click();
        logger.info("Clicked Continue button on second page");
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.LOAD,
                    new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
            page.waitForTimeout(ConfigReader.getElementRetryDelay());
        } catch (Exception e) {
            logger.warn("Post second-page submit wait encountered issue: {}", e.getMessage());
        }
    }

    public boolean isThirdPageVisible() {
        return isPageVisible(thirdPageHeader,
                page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(thirdPageHeader)),
                page.locator(priceInput).first(),
                page.locator(subscriptionToggle));
    }

    public void fillThirdPageForm(String subscriptionPrice) {
        page.locator(priceInput).first().fill(subscriptionPrice);
        logger.info("Filled subscription price: {}", subscriptionPrice);
    }

    public void submitThirdPage() {
        Locator btn = continueButton();
        btn.scrollIntoViewIfNeeded();
        btn.click();
        logger.info("Clicked Continue button on third page");
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.LOAD,
                    new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
            page.waitForTimeout(ConfigReader.getElementRetryDelay());
        } catch (Exception e) {
            logger.warn("Post third-page submit wait encountered issue: {}", e.getMessage());
        }
    }

    public boolean isFourthPageVisible() {
        return isPageVisible(fourthPageHeader,
                page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(fourthPageHeader)),
                page.getByText(privateIndividualOption, new Page.GetByTextOptions().setExact(true)),
                continueButton());
    }

    public void fillFourthPageForm() {
        try {
            Locator option = page.getByText(privateIndividualOption, new Page.GetByTextOptions().setExact(true));
            option.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(ConfigReader.getShortTimeout()));
            option.click();
            logger.info("Selected status: {}", privateIndividualOption);
        } catch (Exception e) {
            logger.warn("Failed to click known status option directly: {}. Trying label fallback...", e.getMessage());
            page.locator("label").filter(new Locator.FilterOptions().setHasText(java.util.regex.Pattern.compile("^" + java.util.regex.Pattern.quote(privateIndividualOption) + "$"))).click();
            logger.info("Selected status via label filter: {}", privateIndividualOption);
        }
    }

    public void submitFourthPage() {
        Locator btn = continueButton();
        btn.scrollIntoViewIfNeeded();
        btn.click();
        logger.info("Clicked Continue button on fourth page");
        try {
            page.waitForLoadState(com.microsoft.playwright.options.LoadState.LOAD,
                    new Page.WaitForLoadStateOptions().setTimeout(ConfigReader.getNavigationTimeout()));
            page.waitForTimeout(ConfigReader.getElementRetryDelay());
        } catch (Exception e) {
            logger.warn("Post fourth-page submit wait encountered issue: {}", e.getMessage());
        }
    }

    public boolean isFifthPageVisible() {
        return isPageVisible(fifthPageHeader,
                page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(fifthPageHeader)),
                firstImageUploadButton(),
                finishButton());
    }

    public void uploadDocuments(String identityFilePath, String selfieFilePath) {
        // Brief stabilization before file upload
        page.waitForTimeout(ConfigReader.getAnimationTimeout());

        // Drive the underlying Ant Upload file inputs directly to avoid native OS dialogs.
        Locator inputs = page.locator(".ant-upload input[type='file']");
        try {
            inputs.first().waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(ConfigReader.getMediumTimeout()));
            inputs.nth(1).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.ATTACHED).setTimeout(ConfigReader.getMediumTimeout()));
        } catch (Exception e) {
            throw new RuntimeException("Expected two Ant Upload file inputs for identity/selfie, but found " + inputs.count(), e);
        }

        Locator firstInput = inputs.nth(0);
        firstInput.setInputFiles(Paths.get(identityFilePath));
        logger.info("Uploaded identity document via input[type=file]: {}", identityFilePath);

        Locator secondInput = inputs.nth(1);
        secondInput.setInputFiles(Paths.get(selfieFilePath));
        logger.info("Uploaded selfie document via input[type=file]: {}", selfieFilePath);
    }

    public void submitFifthPage() {
        Locator btn = finishButton();
        btn.scrollIntoViewIfNeeded();
        btn.click();
        logger.info("Clicked FINISH button on fifth page");
    }

    public boolean isFinalConfirmationVisible() {
        return isPageVisible(finalConfirmationText,
                page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(finalConfirmationText)));
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
        logger.info("Creator registration flow completed");
    }
}
