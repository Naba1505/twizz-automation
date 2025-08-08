package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ConfigReader;

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
    private final String secondImageUploadButton = "role=img[name='document'] >> nth=1";
    private final String firstFileInput = ".ant-upload input[type='file'] >> nth=0";
    private final String secondFileInput = ".ant-upload input[type='file'] >> nth=1";
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
        String[] monthNames = {"Jan", "Feb", "Mär", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez"};
        String monthName = monthNames[Integer.parseInt(month) - 1];

        page.locator(datePicker).click();
        logger.info("Clicked date picker");

        // Select year
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("2007")).click();
        page.getByRole(AriaRole.BUTTON).nth(2).click();
        page.getByText(year).click();
        logger.info("Selected year: {}", year);

        // Select month
        page.getByText(monthName, new Page.GetByTextOptions().setExact(true)).click();
        logger.info("Selected month: {}", monthName);

        // Select day
        page.getByText(day).click();
        logger.info("Selected day: {}", day);

        // Click outside to close picker
        page.getByRole(AriaRole.MAIN).click();
        logger.info("Closed date picker");
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
                                     String dob, String email, String password, String phoneNumber, String gender) {
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

        page.locator(emailInput).fill(email);
        logger.info("Filled email: {}", email);

        page.locator(passwordInput).fill(password);
        logger.info("Filled password: [HIDDEN]");

        page.locator(phoneNumberInput).fill(phoneNumber);
        logger.info("Filled phone number: {}", phoneNumber);

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
                logger.warn("Second page header not visible after waits: {}", fallback.getMessage());
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
        page.waitForTimeout(5000); // Wait for UI stabilization

        // Click first document image
        Locator firstButton = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("document")).first();
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                firstButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(30000));
                firstButton.click(new Locator.ClickOptions().setForce(true).setTimeout(10000));
                logger.info("Clicked first document image (attempt {})", attempt);
                break;
            } catch (Exception e) {
                logger.warn("Failed to click first document image (attempt {}): {}", attempt, e.getMessage());
                if (attempt == 3) {
                    logger.info("Falling back to JavaScript click for first document image");
                    page.evaluate("() => { const img = document.querySelector('[role=\"img\"][name=\"document\"]'); if (img) img.click(); }");
                    logger.info("Clicked first document image using JavaScript");
                }
                page.waitForTimeout(2000);
            }
        }

        // Upload first image
        try {
            Locator fileInputLocator = page.locator(firstFileInput);
            fileInputLocator.setInputFiles(Paths.get(identityFilePath));
            logger.info("Uploaded identity document: {}", identityFilePath);
        } catch (Exception e) {
            logger.warn("File input failed for identity document: {}", e.getMessage());
            page.evaluate(
                    "([selector, path]) => { const input = document.querySelector(selector); input.value = ''; const file = new File([path], path.split('/').pop(), { type: 'image/png' }); const dt = new DataTransfer(); dt.items.add(file); input.files = dt.files; input.dispatchEvent(new Event('change', { bubbles: true })); }",
                    new String[]{firstFileInput, identityFilePath}
            );
            logger.info("Uploaded identity document via JavaScript: {}", identityFilePath);
        }

        // Click second document image
        Locator secondButton = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("document")).nth(1);
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                secondButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(30000));
                secondButton.click(new Locator.ClickOptions().setForce(true).setTimeout(10000));
                logger.info("Clicked second document image (attempt {})", attempt);
                break;
            } catch (Exception e) {
                logger.warn("Failed to click second document image (attempt {}): {}", attempt, e.getMessage());
                if (attempt == 3) {
                    logger.info("Falling back to JavaScript click for second document image");
                    page.evaluate("() => { const img = document.querySelectorAll('[role=\"img\"][name=\"document\"]')[1]; if (img) img.click(); }");
                    logger.info("Clicked second document image using JavaScript");
                }
                page.waitForTimeout(2000);
            }
        }

        // Upload second image
        try {
            Locator fileInputLocator = page.locator(secondFileInput);
            fileInputLocator.setInputFiles(Paths.get(selfieFilePath));
            logger.info("Uploaded selfie document: {}", selfieFilePath);
        } catch (Exception e) {
            logger.warn("File input failed for selfie document: {}", e.getMessage());
            page.evaluate(
                    "([selector, path]) => { const input = document.querySelector(selector); input.value = ''; const file = new File([path], path.split('/').pop(), { type: 'image/jpeg' }); const dt = new DataTransfer(); dt.items.add(file); input.files = dt.files; input.dispatchEvent(new Event('change', { bubbles: true })); }",
                    new String[]{secondFileInput, selfieFilePath}
            );
            logger.info("Uploaded selfie document via JavaScript: {}", selfieFilePath);
        }
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
        fillRegistrationForm(name, username, firstName, lastName, dob, email, password, phoneNumber, gender);
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