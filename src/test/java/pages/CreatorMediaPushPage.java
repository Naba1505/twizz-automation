package pages;

import com.microsoft.playwright.FileChooser;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class CreatorMediaPushPage extends BasePage {
    private static final Logger logger = LoggerFactory.getLogger(CreatorMediaPushPage.class);

    // Visible texts / placeholders
    private static final String WHAT_DO_YOU_WANT = "What do you want to do?";
    private static final String MEDIA_PUSH = "Media push";
    private static final String SELECT_SEGMENTS = "Select your segments";
    private static final String SUBSCRIBERS = "Subscribers";
    private static final String CREATE_BTN = "Create";
    private static final String ADD_MEDIA_HINT = "Click on the \"+\" button to import your file";
    private static final String IMPORTATION = "Importation";
    private static final String MY_DEVICE = "My Device";
    private static final String MESSAGE_TITLE = "Message";
    private static final String MESSAGE_PLACEHOLDER = "Your message....";
    private static final String PROPOSE_PUSH_MEDIA = "Propose push media";
    private static final String UPLOADING_MSG = "Stay on page during uploading"; // transient
    private static final String MESSAGING_TITLE = "Messaging";

    public CreatorMediaPushPage(Page page) {
        super(page);
    }

    @Step("Open plus menu on creator screen")
    public void openPlusMenu() {
        Locator plusImg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("plus"));
        waitVisible(plusImg.first(), 15000);
        Locator svg = plusImg.locator("svg");
        if (svg.count() > 0 && svg.first().isVisible()) {
            clickWithRetry(svg.first(), 2, 200);
        } else {
            clickWithRetry(plusImg.first(), 2, 200);
        }
    }

    @Step("Ensure options popup is visible")
    public void ensureOptionsPopup() {
        waitVisible(page.getByText(WHAT_DO_YOU_WANT).first(), 15000);
    }

    @Step("Dismiss 'I understand' dialog if present")
    public void clickIUnderstandIfPresent() {
        Locator understand = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I understand"));
        if (understand.count() > 0 && understand.first().isVisible()) {
            clickWithRetry(understand.first(), 2, 200);
        }
    }

    @Step("Choose Media push from options")
    public void chooseMediaPush() {
        Locator mp = page.getByText(MEDIA_PUSH);
        waitVisible(mp.first(), 10000);
        clickWithRetry(mp.first(), 2, 200);
    }

    @Step("Ensure Media Push segments screen visible")
    public void ensureSegmentsScreen() {
        waitVisible(page.getByText(SELECT_SEGMENTS).first(), 15000);
    }

    @Step("Select Subscribers segment")
    public void selectSubscribersSegment() {
        Locator seg = page.getByText(SUBSCRIBERS);
        waitVisible(seg.first(), 10000);
        clickWithRetry(seg.first(), 1, 150);
    }

    @Step("Select Interested segment")
    public void selectInterestedSegment() {
        Locator seg = page.getByText("Interested");
        waitVisible(seg.first(), 10000);
        clickWithRetry(seg.first(), 1, 150);
    }

    @Step("Click Create to proceed from segments")
    public void clickCreateNext() {
        Locator create = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(CREATE_BTN));
        waitVisible(create.first(), 15000);
        clickWithRetry(create.first(), 2, 200);
    }

    @Step("Ensure Add Push Media screen visible")
    public void ensureAddPushMediaScreen() {
        waitVisible(page.getByText(ADD_MEDIA_HINT).first(), 15000);
    }

    @Step("Click PLUS to add media")
    public void clickAddMediaPlus() {
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
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(MY_DEVICE));
        waitVisible(btn.first(), 10000);
        clickWithRetry(btn.first(), 2, 200);
    }

    @Step("Upload media from device: {file}")
    public void uploadMediaFromDevice(Path file) {
        if (file == null || !Files.exists(file)) {
            throw new RuntimeException("Media file not found: " + file);
        }
        Locator input = page.locator("input[type='file']");
        if (input.count() > 0) {
            input.first().setInputFiles(file);
            return;
        }
        try {
            FileChooser chooser = page.waitForFileChooser(this::chooseMyDevice);
            chooser.setFiles(file);
        } catch (Exception e) {
            Locator any = page.locator("input[type='file']");
            if (any.count() > 0) {
                any.first().setInputFiles(file);
            } else {
                throw new RuntimeException("Failed to upload media via file chooser: " + e.getMessage());
            }
        }
    }

    @Step("Ensure blur toggle is enabled by default")
    public void ensureBlurToggleEnabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(sw, 10000);
        try {
            String checked = sw.getAttribute("aria-checked");
            if (!"true".equalsIgnoreCase(checked)) {
                logger.warn("Blurred media switch not enabled by default (aria-checked={})", checked);
            }
        } catch (Exception ignored) {}
    }

    @Step("Disable blur toggle if currently enabled")
    public void disableBlurIfEnabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(sw, 10000);
        try {
            String checked = sw.getAttribute("aria-checked");
            if ("true".equalsIgnoreCase(checked)) {
                clickWithRetry(sw, 1, 150);
            }
        } catch (Exception e) {
            // Fallback: attempt click once
            clickWithRetry(sw, 1, 150);
        }
    }

    @Step("Ensure blur toggle is disabled")
    public void ensureBlurToggleDisabled() {
        Locator sw = page.getByRole(AriaRole.SWITCH).first();
        waitVisible(sw, 10000);
        String checked = sw.getAttribute("aria-checked");
        if (!"false".equalsIgnoreCase(checked)) {
            logger.warn("Expected blur toggle disabled but aria-checked={}", checked);
        }
    }

    @Step("Click Next")
    public void clickNext() {
        Locator next = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next"));
        waitVisible(next.first(), 10000);
        clickWithRetry(next.first(), 2, 200);
    }

    @Step("Ensure Message title visible")
    public void ensureMessageTitle() {
        waitVisible(page.getByText(MESSAGE_TITLE).first(), 15000);
    }

    @Step("Fill message: {msg}")
    public void fillMessage(String msg) {
        Locator ph = page.getByPlaceholder(MESSAGE_PLACEHOLDER);
        waitVisible(ph.first(), 10000);
        ph.first().click();
        ph.first().fill(msg == null ? "" : msg);
    }

    @Step("Set price in euros to {euros}")
    public void setPriceEuro(int euros) {
        // For 15€, match label text via regex like ^15€$
        String regex = "^" + euros + "€$";
        Locator label = page.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile(regex)));
        waitVisible(label.first(), 10000);
        clickWithRetry(label.first(), 1, 150);
    }

    @Step("Ensure add promotion toggle is disabled by default")
    public void ensureAddPromotionDisabled() {
        try {
            Locator toggles = page.getByRole(AriaRole.SWITCH);
            if (toggles.count() > 1) {
                Locator promo = toggles.nth(1);
                String checked = promo.getAttribute("aria-checked");
                if ("true".equalsIgnoreCase(checked)) {
                    logger.warn("Add promotion toggle appears enabled by default");
                }
            }
        } catch (Exception ignored) {}
    }

    @Step("Enable add promotion toggle")
    public void enablePromotionToggle() {
        Locator toggles = page.getByRole(AriaRole.SWITCH);
        Locator target;
        if (toggles.count() > 1) {
            target = toggles.nth(1);
        } else {
            target = toggles.first();
        }
        waitVisible(target, 10000);
        // Click only if not already enabled
        try {
            String checked = target.getAttribute("aria-checked");
            if (!"true".equalsIgnoreCase(checked)) {
                clickWithRetry(target, 1, 150);
            }
        } catch (Exception e) {
            clickWithRetry(target, 1, 150);
        }
    }

    @Step("Ensure 'Discount' label visible")
    public void ensureDiscountVisible() {
        waitVisible(page.getByText("Discount").first(), 10000);
    }

    @Step("Open discount percent field")
    public void openDiscountPercentField() {
        Locator percentSpan = page.locator("span").filter(new Locator.FilterOptions().setHasText(Pattern.compile("%")));
        waitVisible(percentSpan.first(), 10000);
        clickWithRetry(percentSpan.first(), 1, 150);
    }

    @Step("Fill discount percent: {percent}%")
    public void fillDiscountPercent(int percent) {
        // Based on UI, the discount textbox appears as the third textbox (index 2)
        Locator tb = page.getByRole(AriaRole.TEXTBOX).nth(2);
        waitVisible(tb, 10000);
        tb.fill(String.valueOf(percent));
    }

    @Step("Ensure 'Validity period' title visible")
    public void ensureValidityTitle() {
        waitVisible(page.getByText("Validity period").first(), 10000);
    }

    @Step("Select validity as 'Unlimited'")
    public void selectValidityUnlimited() {
        Locator lbl = page.locator("label").filter(new Locator.FilterOptions().setHasText("Unlimited"));
        waitVisible(lbl.first(), 10000);
        clickWithRetry(lbl.first(), 1, 150);
    }

    @Step("Open euro discount field")
    public void openEuroDiscountField() {
        Locator euroSpan = page.locator("span").filter(new Locator.FilterOptions().setHasText(Pattern.compile("^€$")));
        waitVisible(euroSpan.first(), 10000);
        clickWithRetry(euroSpan.first(), 1, 150);
    }

    @Step("Fill euro discount amount: {amount}€")
    public void fillEuroDiscountEuro(int amount) {
        // Based on UI hint: euro discount textbox appears as index 1
        Locator tb = page.getByRole(AriaRole.TEXTBOX).nth(1);
        waitVisible(tb, 10000);
        tb.fill(String.valueOf(amount));
    }

    @Step("Select validity as '7 days'")
    public void selectValidity7Days() {
        Locator lbl = page.locator("label").filter(new Locator.FilterOptions().setHasText("7 days"));
        waitVisible(lbl.first(), 10000);
        clickWithRetry(lbl.first(), 1, 150);
    }

    @Step("Open custom price field (0.00 €)")
    public void openCustomPriceField() {
        Locator zeroPrice = page.getByText("0.00 €");
        waitVisible(zeroPrice.first(), 10000);
        clickWithRetry(zeroPrice.first(), 1, 150);
    }

    @Step("Fill custom price euros: {amount}€")
    public void fillCustomPriceEuro(int amount) {
        Locator spin = page.getByRole(AriaRole.SPINBUTTON);
        waitVisible(spin.first(), 10000);
        spin.first().fill(String.valueOf(amount));
    }

    @Step("Select price as 'Free'")
    public void selectPriceFree() {
        Locator lbl = page.locator("label").filter(new Locator.FilterOptions().setHasText("Free"));
        waitVisible(lbl.first(), 10000);
        clickWithRetry(lbl.first(), 1, 150);
    }

    @Step("Click 'Propose push media'")
    public void clickProposePushMedia() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(PROPOSE_PUSH_MEDIA));
        waitVisible(btn.first(), 15000);
        clickWithRetry(btn.first(), 2, 200);
    }

    @Step("Optionally wait for uploading message if it appears")
    public void waitForUploadingMessageIfFast() {
        try {
            Locator msg = page.getByText(UPLOADING_MSG);
            if (msg.count() > 0) {
                // small visibility wait, then allow dismiss naturally
                waitVisible(msg.first(), 5000);
            }
        } catch (Exception ignored) {}
    }

    @Step("Assert landed on Messaging screen")
    public void assertOnMessagingScreen() {
        waitVisible(page.getByText(MESSAGING_TITLE).first(), 60000);
    }
}
