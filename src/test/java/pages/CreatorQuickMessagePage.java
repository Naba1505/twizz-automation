package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;
import org.testng.Assert;

public class CreatorQuickMessagePage extends BasePage {

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

    @Step("Open Settings from profile and verify landing on Settings screen")
    public void openSettingsFromProfile(String expectedDisplayName) {
        waitVisible(settingsIcon(), DEFAULT_WAIT);
        clickWithRetry(settingsIcon(), 1, 200);
        // Expect a recognizable display name on settings screen
        waitVisible(settingsHeader(expectedDisplayName), DEFAULT_WAIT);
    }

    @Step("Navigate to Quick message screen from Settings")
    public void goToQuickMessage() {
        Locator item = quickMessageMenuItem();
        try { item.scrollIntoViewIfNeeded(); } catch (Exception ignored) {}
        clickWithRetry(item, 1, 200);
        waitVisible(quickMessageTitleExact(), DEFAULT_WAIT);
    }

    @Step("Add a Quick message titled: {title}")
    public void addQuickMessage(String title, String text) {
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
        page.waitForTimeout(300);
    }

    @Step("Assert that quick message with title is visible: {title}")
    public void assertQuickMessageVisible(String title) {
        Locator titleCell = page.getByText(title);
        waitVisible(titleCell, DEFAULT_WAIT);
        Assert.assertTrue(titleCell.isVisible(), "Quick message title not visible: " + title);
    }
}
