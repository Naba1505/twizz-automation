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

    // Timeout constants (in milliseconds) - Standardized values
    private static final int POLLING_WAIT = 50;          // Quick polling operations
    private static final int PAGE_TRANSITION = 250;      // Page transition delays
    private static final int STABILIZATION_WAIT = 500;   // UI stabilization
    private static final int SHORT_TIMEOUT = 2000;       // Short waits
    private static final int LONG_TIMEOUT = 5000;        // Long waits
    private static final int FILE_INPUT_DEADLINE = 5000; // For file input polling deadline

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
        super(page);
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

        // Map month number to month name
        String[] monthNames = {"January", "February", "March", "April", "May", "June", 
                               "July", "August", "September", "October", "November", "December"};
        int monthNum = Integer.parseInt(month);
        String monthName = monthNames[monthNum - 1];
        String dayText = String.valueOf(Integer.parseInt(day)); // normalize no leading zero

        // Click date picker with retry
        boolean pickerOpened = false;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                page.locator(datePicker).click();
                logger.info("Clicked date picker (attempt {})", attempt);
                page.waitForTimeout(STABILIZATION_WAIT);
                
                // Verify calendar modal appeared
                if (page.locator(".calendar-modal-overlay").isVisible()) {
                    pickerOpened = true;
                    logger.info("Calendar modal opened successfully");
                    break;
                }
            } catch (Exception e) {
                logger.warn("Attempt {} to open date picker failed: {}", attempt, e.getMessage());
                page.waitForTimeout(STABILIZATION_WAIT);
            }
        }

        if (!pickerOpened) {
            logger.warn("Failed to open calendar modal after 3 attempts, using typing fallback");
            typeDobFallback(day, month, year);
            return;
        }

        // Select year - click on year header to open year selector, then select target year
        try {
            // Wait for year selector to be visible
            Locator yearHeader = page.locator(".calendar-header-text.clickable").filter(new Locator.FilterOptions().setHasText(Pattern.compile("\\d{4}")));
            if (WaitUtils.waitForVisible(yearHeader, SHORT_TIMEOUT)) {
                yearHeader.first().click();
                logger.info("Clicked year header to open year selector");
                page.waitForTimeout(STABILIZATION_WAIT);
                
                // Select target year
                Locator yearOption = page.getByText(year, new Page.GetByTextOptions().setExact(true));
                if (WaitUtils.waitForVisible(yearOption, SHORT_TIMEOUT)) {
                    yearOption.click();
                    logger.info("Selected year: {}", year);
                    page.waitForTimeout(STABILIZATION_WAIT);
                } else {
                    throw new Exception("Year option not visible: " + year);
                }
            } else {
                throw new Exception("Year header not visible");
            }
        } catch (Exception e) {
            logger.warn("Failed to select year: {}, attempting fallback", e.getMessage());
            typeDobFallback(day, month, year);
            return;
        }

        // Select month - click on month header to open month selector, then select target month
        try {
            // Click month header (first clickable header text after year selection)
            Locator monthHeader = page.locator(".calendar-header-text.clickable").first();
            if (WaitUtils.waitForVisible(monthHeader, SHORT_TIMEOUT)) {
                monthHeader.click();
                logger.info("Clicked month header to open month selector");
                page.waitForTimeout(STABILIZATION_WAIT);
                
                // Select target month from month selector
                Locator monthOption = page.locator(".month-selector-item").filter(new Locator.FilterOptions().setHasText(monthName));
                if (WaitUtils.waitForVisible(monthOption, SHORT_TIMEOUT)) {
                    monthOption.click();
                    logger.info("Selected month: {}", monthName);
                    page.waitForTimeout(STABILIZATION_WAIT);
                } else {
                    throw new Exception("Month option not visible: " + monthName);
                }
            } else {
                throw new Exception("Month header not visible");
            }
        } catch (Exception e) {
            logger.warn("Failed to select month: {}, attempting fallback", e.getMessage());
            typeDobFallback(day, month, year);
            return;
        }

        // Select day with retry
        boolean daySelected = false;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                Locator dayOption = page.locator(".calendar-day").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^" + dayText + "$")));
                if (dayOption.count() == 0) {
                    // Fallback to getByText with exact match
                    dayOption = page.getByText(dayText, new Page.GetByTextOptions().setExact(true));
                }
                
                if (WaitUtils.waitForVisible(dayOption, SHORT_TIMEOUT)) {
                    dayOption.first().click();
                    logger.info("Selected day: {} (attempt {})", dayText, attempt);
                    page.waitForTimeout(STABILIZATION_WAIT);
                    daySelected = true;
                    break;
                }
            } catch (Exception e) {
                logger.warn("Attempt {} to select day failed: {}", attempt, e.getMessage());
                page.waitForTimeout(POLLING_WAIT);
            }
        }

        if (!daySelected) {
            logger.warn("Failed to select day after 3 attempts, using typing fallback");
            typeDobFallback(day, month, year);
            return;
        }

        // Click Confirm button with retry
        boolean confirmed = false;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                Locator confirmBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Confirm"));
                if (WaitUtils.waitForVisible(confirmBtn, SHORT_TIMEOUT)) {
                    confirmBtn.click();
                    logger.info("Clicked Confirm button (attempt {})", attempt);
                    page.waitForTimeout(STABILIZATION_WAIT);
                    
                    // Verify calendar modal closed
                    if (!page.locator(".calendar-modal-overlay").isVisible()) {
                        confirmed = true;
                        logger.info("Calendar modal closed successfully");
                        break;
                    }
                }
            } catch (Exception e) {
                logger.warn("Attempt {} to click Confirm failed: {}", attempt, e.getMessage());
                page.waitForTimeout(POLLING_WAIT);
            }
        }

        if (!confirmed) {
            logger.warn("Failed to confirm date selection, attempting to close modal with Escape");
            try {
                page.keyboard().press("Escape");
                page.waitForTimeout(STABILIZATION_WAIT);
            } catch (Exception ignored) {}
        }

        // Verify date input is populated
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
                page.waitForTimeout(POLLING_WAIT);
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
                page.waitForTimeout(POLLING_WAIT);
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
        // Wait for Registration button to be enabled (form validation complete)
        Locator regButton = page.locator(registrationButton);
        try {
            regButton.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(ConfigReader.getVisibilityTimeout()));
            
            // Wait for button to be enabled if it's currently disabled
            long deadline = System.currentTimeMillis() + SHORT_TIMEOUT;
            while (!regButton.isEnabled() && System.currentTimeMillis() < deadline) {
                page.waitForTimeout(POLLING_WAIT);
            }
            
            if (!regButton.isEnabled()) {
                logger.warn("Registration button still not enabled after wait, clicking anyway");
            }
        } catch (Exception e) {
            logger.warn("Registration button wait encountered issue: {}", e.getMessage());
        }
        
        regButton.click();
        logger.info("Clicked Registration button");
        // Allow the next page to render/load before checks
        try {
            page.waitForLoadState();
            // Small breathing time to allow UI render if needed
            page.waitForTimeout(PAGE_TRANSITION);
        } catch (Exception e) {
            logger.warn("Post-submit load wait timed out or not applicable: {}", e.getMessage());
        }
    }

    public boolean isSecondPageVisible() {
        try {
            Locator header = page.getByText(secondPageHeader, new Page.GetByTextOptions().setExact(true));
            header.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(ConfigReader.getVisibilityTimeout()));
            boolean isVisible = header.isVisible();
            logger.info("Second page visibility: {}", isVisible);
            return isVisible;
        } catch (Exception primary) {
            logger.warn("Exact header not immediately visible: {}. Trying fallback...", primary.getMessage());
            try {
                Locator headingRole = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(secondPageHeader));
                headingRole.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
                logger.info("Second page visibility via heading role: true");
                return true;
            } catch (Exception fallback) {
                logger.warn("Second page not visible after fallback: {}", fallback.getMessage());
                // Final fallback: wait for a known option label to appear (e.g., 'Model')
                try {
                    Locator optionModel = page.getByText("Model", new Page.GetByTextOptions().setExact(true));
                    optionModel.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
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
            page.waitForTimeout(PAGE_TRANSITION);
        } catch (Exception e) {
            logger.warn("Post second-page submit wait encountered issue: {}", e.getMessage());
        }
    }

    public boolean isThirdPageVisible() {
        try {
            Locator header = page.getByText(thirdPageHeader, new Page.GetByTextOptions().setExact(true));
            header.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(ConfigReader.getVisibilityTimeout()));
            boolean isVisible = header.isVisible();
            logger.info("Third page visibility: {}", isVisible);
            return isVisible;
        } catch (Exception primary) {
            logger.warn("Exact third-page header not immediately visible: {}. Trying fallbacks...", primary.getMessage());
            // Fallback 1: heading role
            try {
                Locator headingRole = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(thirdPageHeader));
                headingRole.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
                logger.info("Third page visibility via heading role: true");
                return true;
            } catch (Exception fallback1) {
                logger.warn("Heading role check failed: {}. Trying control fallbacks...", fallback1.getMessage());
                // Fallback 2: check for price input
                try {
                    Locator price = page.locator(priceInput).first();
                    price.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
                    logger.info("Third page visibility via price input: true");
                    return true;
                } catch (Exception fallback2) {
                    // Fallback 3: check for subscription toggle
                    try {
                        Locator toggle = page.locator(subscriptionToggle);
                        toggle.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
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
            page.waitForTimeout(PAGE_TRANSITION);
        } catch (Exception e) {
            logger.warn("Post third-page submit wait encountered issue: {}", e.getMessage());
        }
    }

    public boolean isFourthPageVisible() {
        try {
            Locator header = page.getByText(fourthPageHeader, new Page.GetByTextOptions().setExact(true));
            header.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(ConfigReader.getVisibilityTimeout()));
            boolean isVisible = header.isVisible();
            logger.info("Fourth page visibility: {}", isVisible);
            return isVisible;
        } catch (Exception primary) {
            logger.warn("Exact fourth-page header not immediately visible: {}. Trying fallbacks...", primary.getMessage());
            // Fallback 1: heading role
            try {
                Locator headingRole = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(fourthPageHeader));
                headingRole.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
                logger.info("Fourth page visibility via heading role: true");
                return true;
            } catch (Exception fallback1) {
                logger.warn("Heading role check failed: {}. Trying control fallbacks...", fallback1.getMessage());
                // Fallback 2: known option label
                try {
                    Locator option = page.getByText(privateIndividualOption, new Page.GetByTextOptions().setExact(true));
                    option.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
                    logger.info("Fourth page visibility via known option label: true");
                    return true;
                } catch (Exception fallback2) {
                    // Fallback 3: Continue button on page
                    try {
                        Locator cont = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue").setExact(true));
                        cont.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
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
        page.waitForLoadState();
        page.locator(fourthPageContinueButton).click();
        logger.info("Clicked Continue button on fourth page");
        // Wait for transition to fifth page
        try {
            page.waitForLoadState();
            page.waitForTimeout(PAGE_TRANSITION);
        } catch (Exception e) {
            logger.warn("Post fourth-page submit wait encountered issue: {}", e.getMessage());
        }
    }

    public boolean isFifthPageVisible() {
        try {
            Locator header = page.getByText(fifthPageHeader, new Page.GetByTextOptions().setExact(true));
            header.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(ConfigReader.getVisibilityTimeout()));
            boolean isVisible = header.isVisible();
            logger.info("Fifth page visibility: {}", isVisible);
            return isVisible;
        } catch (Exception primary) {
            logger.warn("Exact fifth-page header not immediately visible: {}. Trying fallbacks...", primary.getMessage());
            // Fallback 1: heading role
            try {
                Locator headingRole = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(fifthPageHeader));
                headingRole.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
                logger.info("Fifth page visibility via heading role: true");
                return true;
            } catch (Exception fallback1) {
                logger.warn("Heading role check failed: {}. Trying control fallbacks...", fallback1.getMessage());
                // Fallback 2: presence of first upload image placeholder
                try {
                    Locator firstImg = page.locator(firstImageUploadButton);
                    firstImg.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
                    logger.info("Fifth page visibility via first upload image: true");
                    return true;
                } catch (Exception fallback2) {
                    // Fallback 3: FINISH button visible
                    try {
                        Locator finish = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("FINISH").setExact(true));
                        finish.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(LONG_TIMEOUT));
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
        page.waitForTimeout(STABILIZATION_WAIT); // brief stabilization

        // Drive the underlying Ant Upload file inputs directly to avoid native OS dialogs.
        Locator inputs = page.locator(".ant-upload input[type='file']");

        long deadline = System.currentTimeMillis() + FILE_INPUT_DEADLINE;
        while (inputs.count() < 2 && System.currentTimeMillis() < deadline) {
            try { page.waitForTimeout(POLLING_WAIT); } catch (Throwable ignored) { }
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
            thankYou.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(ConfigReader.getVisibilityTimeout()));
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
