package pages.creator;

import pages.common.BasePage;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page Object for Creator Profile screen and dashboard sections.
 */
public class CreatorProfilePage extends BasePage {

    public CreatorProfilePage(Page page) {
        super(page);
    }

    // ===== Locators =====
    private Locator headerUsername() {
        return page.locator("span.ant-typography.header-my-profile-text.css-ixblex");
    }

    private Locator avatarImg() {
        return page.locator("//img[@alt='avatar']");
    }

    private Locator publicationsText() {
        return page.locator("//span[contains(text(),'Publications')]");
    }

    private Locator subscribersText() {
        return page.locator("//span[contains(text(),'Subscribers')]");
    }

    private Locator interestedText() {
        return page.locator("//span[contains(text(),'Interested')]");
    }

    private Locator publicationsIcon() {
        return page.locator("//img[@alt='publications icon']");
    }

    private Locator collectionsIcon() {
        return page.locator("//img[@alt='collections icon']");
    }

    // ===== Actions & Asserts =====

    @Step("Navigate to Creator Profile via URL")
    public void navigateToProfile() {
        // Navigate directly to stage profile URL per spec
        page.navigate("https://stg.twizz.app/creator/profile");
    }

    @Step("Assert URL contains /creator/profile")
    public void assertOnProfileUrl() {
        Page.WaitForURLOptions opts = new Page.WaitForURLOptions().setTimeout(DEFAULT_WAIT);
        page.waitForURL("**/creator/profile**", opts);
    }

    @Step("Assert profile header username is visible")
    public void assertHeaderUsernameVisible() {
        waitVisible(headerUsername(), DEFAULT_WAIT);
    }

    @Step("Assert profile avatar is visible")
    public void assertAvatarVisible() {
        waitVisible(avatarImg(), DEFAULT_WAIT);
    }

    @Step("Assert Publications, Subscribers, Interested counts are visible")
    public void assertTopCountersVisible() {
        waitVisible(publicationsText(), DEFAULT_WAIT);
        waitVisible(subscribersText(), DEFAULT_WAIT);
        waitVisible(interestedText(), DEFAULT_WAIT);
    }

    @Step("Scroll to bottom of the page")
    public void scrollToBottom() {
        try { page.evaluate("window.scrollTo(0, document.body.scrollHeight)"); } catch (Throwable ignored) {}
        page.waitForTimeout(300);
    }

    @Step("Scroll to top of the page")
    public void scrollToTop() {
        try { page.evaluate("window.scrollTo(0, 0)"); } catch (Throwable ignored) {}
        page.waitForTimeout(300);
    }

    @Step("Assert Publications and Collections icons visible")
    public void assertBottomIconsVisible() {
        waitVisible(publicationsIcon(), DEFAULT_WAIT);
        waitVisible(collectionsIcon(), DEFAULT_WAIT);
    }

    @Step("Click Collections icon")
    public void clickCollectionsIcon() {
        waitVisible(collectionsIcon(), DEFAULT_WAIT);
        clickWithRetry(collectionsIcon(), 1, 200);
    }

    @Step("Click Publications icon")
    public void clickPublicationsIcon() {
        waitVisible(publicationsIcon(), DEFAULT_WAIT);
        clickWithRetry(publicationsIcon(), 1, 200);
    }

    @Step("Open Messaging from header (optional fast nav)")
    public void openMessagingFromHeader() {
        // Some profile screens expose a Messaging icon as in other modules
        Locator msgIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Messaging icon"));
        if (msgIcon.count() > 0) {
            clickWithRetry(msgIcon.first(), 1, 150);
        }
    }

    // ===== Modify Profile & Avatar upload =====

    @Step("Open 'Modify' profile screen")
    public void openModifyProfile() {
        Locator modifyBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Modify"));
        waitVisible(modifyBtn.first(), DEFAULT_WAIT);
        clickWithRetry(modifyBtn.first(), 1, 150);
        // Wait for title
        waitVisible(page.getByText("Modify my profile"), DEFAULT_WAIT);
    }

    @Step("Assert 'Modify my profile' screen is visible")
    public void assertModifyProfileScreen() {
        waitVisible(page.getByText("Modify my profile"), DEFAULT_WAIT);
    }

    @Step("Click avatar upload pencil icon")
    public void clickUploadAvatarPencil() {
        Locator pencil = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("icon"));
        waitVisible(pencil.first(), DEFAULT_WAIT);
        clickWithRetry(pencil.first(), 1, 120);
    }

    @Step("Click avatar edit menu item")
    public void clickAvatarEditMenuItem() {
        Locator menuItem = page.getByRole(AriaRole.MENUITEM, new Page.GetByRoleOptions().setName("edit Edit"));
        waitVisible(menuItem.first(), DEFAULT_WAIT);
        clickWithRetry(menuItem.first(), 1, 120);
    }

    @Step("Upload avatar image from: {file}")
    public void uploadAvatarImage(java.nio.file.Path file) {
        if (file == null || !java.nio.file.Files.exists(file)) {
            throw new RuntimeException("Avatar image file not found: " + file);
        }
        // Prefer a direct input[type=file] if present
        Locator input = page.locator("input[type='file']");
        if (input.count() > 0) {
            input.first().setInputFiles(file);
            return;
        }
        // Fallback to file chooser triggered by 'Edit' button
        try {
            com.microsoft.playwright.FileChooser chooser = page.waitForFileChooser(() -> {
                Locator editBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Edit"));
                waitVisible(editBtn.first(), DEFAULT_WAIT);
                clickWithRetry(editBtn.first(), 1, 120);
            });
            chooser.setFiles(file);
            return;
        } catch (Throwable ignored) {}
        // As last resort, try clicking any visible button with name Edit and re-check for input
        Locator editBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Edit"));
        if (editBtn.count() > 0) {
            clickWithRetry(editBtn.first(), 1, 120);
            Locator anyInput = page.locator("input[type='file']");
            if (anyInput.count() > 0) {
                anyInput.first().setInputFiles(file);
                return;
            }
        }
        throw new RuntimeException("Unable to find upload control to set avatar image");
    }

    @Step("Wait for avatar updated success toast")
    public void waitForAvatarUpdatedToast() {
        Locator toast = page.getByText("Avatar image updated successfully");
        waitVisible(toast.first(), 15_000);
        // Click to dismiss if clickable
        try { clickWithRetry(toast.first(), 0, 0); } catch (Throwable ignored) {}
    }

    @Step("Click 'Delete' option for avatar")
    public void clickDeleteAvatarOption() {
        Locator delete = page.getByText("Delete");
        waitVisible(delete.first(), DEFAULT_WAIT);
        clickWithRetry(delete.first(), 1, 120);
    }

    @Step("Assert delete avatar confirmation is visible")
    public void assertDeleteAvatarConfirmVisible() {
        waitVisible(page.getByText("Are you sure to delete avatar image ?"), DEFAULT_WAIT);
    }

    @Step("Confirm delete avatar (Yes)")
    public void confirmDeleteAvatarYes() {
        Locator yes = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes"));
        waitVisible(yes.first(), DEFAULT_WAIT);
        clickWithRetry(yes.first(), 1, 120);
    }

    @Step("Wait for avatar removed success toast")
    public void waitForAvatarRemovedToast() {
        Locator toast = page.getByText("Avatar image removed successfully");
        waitVisible(toast.first(), 15_000);
        // Click to dismiss if clickable
        try { clickWithRetry(toast.first(), 0, 0); } catch (Throwable ignored) {}
    }
    @Step("Click 'Register' to save profile changes")
    public void clickRegisterUpdate() {
        // Prefer explicit XPath text match per spec
        Locator xpathBtn = page.locator("//div//button[contains(text(),'Register')]");
        if (xpathBtn.count() > 0) {
            waitVisible(xpathBtn.first(), DEFAULT_WAIT);
            try { xpathBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable ignored) {}
            clickWithRetry(xpathBtn.first(), 1, 150);
        } else {
            // Fallback: role button by name
            Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register"));
            waitVisible(btn.first(), DEFAULT_WAIT);
            clickWithRetry(btn.first(), 1, 150);
        }
        // Short settle even if toast appears instantly
        try { page.waitForTimeout(500); } catch (Throwable ignored) {}
    }

    @Step("Wait for 'Updated Personal Information Successfully.' toast")
    public void waitForProfileUpdatedToast() {
        Locator toast = page.getByText("Updated Personal Information Successfully.");
        waitVisible(toast.first(), 15_000);
        // Allow a brief pause even when toast is visible, then dismiss
        try { page.waitForTimeout(600); } catch (Throwable ignored) {}
        try { clickWithRetry(toast.first(), 0, 0); } catch (Throwable ignored) {}
    }

    @Step("Soft wait for 'Updated Personal Information Successfully.' toast (timeout: {timeoutMs}ms)")
    public boolean waitForProfileUpdatedToastSoft(int timeoutMs) {
        Locator toast = page.getByText("Updated Personal Information Successfully.");
        try {
            waitVisible(toast.first(), timeoutMs);
            try { page.waitForTimeout(400); } catch (Throwable ignored) {}
            try { clickWithRetry(toast.first(), 0, 0); } catch (Throwable ignored) {}
            return true;
        } catch (Throwable t) {
            // Soft failure: toast may not appear in some successful updates
            logger.warn("Profile updated toast not seen within {}ms; proceeding.", timeoutMs);
            return false;
        }
    }

    // ===== Modify Profile: Text fields (Last name, Position, Description) =====
    private void clearAndFill(Locator input, String text) {
        waitVisible(input.first(), DEFAULT_WAIT);
        Locator el = input.first();
        el.click();
        // Clear robustly
        try { el.fill(""); } catch (Throwable ignored) {}
        try { el.press("Control+A"); el.press("Backspace"); } catch (Throwable ignored) {}
        // Fill new value
        el.fill(text == null ? "" : text);
    }

    @Step("Set Last name: {lastName}")
    public void setLastName(String lastName) {
        // Primary selector from codegen
        Locator input = page.locator("div:nth-child(4) > .ant-row > .ant-space > div:nth-child(2) > .w-full > .ant-input");
        if (input.count() == 0) {
            // Fallback: any input with label 'Last name' nearby (if present)
            input = page.getByPlaceholder("Last name");
        }
        clearAndFill(input, lastName);
    }

    @Step("Set Position: {position}")
    public void setPosition(String position) {
        Locator input = page.locator("div:nth-child(6) > .ant-row > .ant-space > div:nth-child(2) > .w-full > .ant-input");
        if (input.count() == 0) {
            input = page.getByPlaceholder("Position");
        }
        clearAndFill(input, position);
    }

    @Step("Set Description: {desc}")
    public void setDescription(String desc) {
        Locator ta = page.locator("textarea");
        if (ta.count() == 0) {
            // Fallback to any role textbox with multiline
            ta = page.getByRole(AriaRole.TEXTBOX);
        }
        clearAndFill(ta.first(), desc);
    }

    // ===== Share Profile =====
    @Step("Open Share profile panel")
    public void openShareProfile() {
        Locator shareBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Share profile"));
        waitVisible(shareBtn.first(), DEFAULT_WAIT);
        clickWithRetry(shareBtn.first(), 1, 120);
        assertShareProfileVisible();
    }

    @Step("Assert Share profile title visible")
    public void assertShareProfileVisible() {
        Locator title = page.getByText("Share profile");
        waitVisible(title.first(), DEFAULT_WAIT);
    }

    @Step("Open share option '{option}' and close popup")
    public void openShareOptionAndClose(String option) {
        String name = option == null ? "" : option.trim().toLowerCase();
        String btnName;
        switch (name) {
            case "whatsapp": btnName = "whatsapp"; break;
            case "twitter": btnName = "twitter"; break;
            case "telegram": btnName = "telegram"; break;
            default: throw new IllegalArgumentException("Unsupported share option: " + option);
        }
        try {
            Page popup = page.waitForPopup(() -> {
                Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(btnName));
                waitVisible(btn.first(), DEFAULT_WAIT);
                clickWithRetry(btn.first(), 1, 120);
            });
            try { popup.close(); } catch (Throwable ignored) {}
        } catch (Throwable t) {
            // If popup suppressed by browser, proceed without failing
        }
    }

    @Step("Click share Message icon")
    public void clickShareMessageIcon() {
        Locator msg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("message icon"));
        waitVisible(msg.first(), DEFAULT_WAIT);
        clickWithRetry(msg.first(), 1, 120);
    }

    @Step("Click share Copy icon")
    public void clickShareCopyIcon() {
        Locator copy = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("copy icon"));
        waitVisible(copy.first(), DEFAULT_WAIT);
        clickWithRetry(copy.first(), 1, 120);
    }

    @Step("Cancel Share profile")
    public void cancelShare() {
        Locator cancel = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
        waitVisible(cancel.first(), DEFAULT_WAIT);
        clickWithRetry(cancel.first(), 1, 120);
    }
}

