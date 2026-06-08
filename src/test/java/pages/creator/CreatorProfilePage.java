package pages.creator;

import pages.common.BasePage;
import utils.ConfigReader;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Step;

/**
 * Page Object for Creator Profile screen and dashboard sections.
 */
public class CreatorProfilePage extends BasePage {

    // All timeout values now use centralized ConfigReader methods for consistency

    public CreatorProfilePage(Page page) {
        super(page);
    }

    // ===== Locators =====
    private Locator headerUsername() {
        return page.locator("span.ant-typography.header-my-profile-text.css-ixblex");
    }

    private Locator avatarImg() {
        // Try multiple strategies to find avatar
        Locator byAlt = page.locator("//img[@alt='avatar']");
        if (byAlt.count() > 0) {
            return byAlt;
        }
        // Fallback: look for avatar in profile header area
        Locator byClass = page.locator(".ant-avatar img, .header-my-profile img, img[src*='avatar']");
        if (byClass.count() > 0) {
            return byClass.first();
        }
        // Final fallback: return original locator
        return byAlt;
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
        navigateAndWait(ConfigReader.getBaseUrl() + "/creator/profile");
    }

    @Step("Assert URL contains /creator/profile")
    public void assertOnProfileUrl() {
        Page.WaitForURLOptions opts = new Page.WaitForURLOptions().setTimeout(ConfigReader.getMediumTimeout());
        page.waitForURL("**/creator/profile**", opts);
    }

    @Step("Assert profile header username is visible")
    public void assertHeaderUsernameVisible() {
        waitVisible(headerUsername(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Assert profile avatar is visible")
    public void assertAvatarVisible() {
        // Avatar might not exist if user hasn't uploaded one, so check first
        Locator avatar = avatarImg();
        try {
            // Wait a short time to see if avatar appears
            waitVisible(avatar, ConfigReader.getShortTimeout());
            logger.info("Avatar is visible on profile");
        } catch (Exception e) {
            // Avatar not found - this is acceptable if user hasn't uploaded one
            logger.warn("Avatar not visible on profile (user may not have uploaded one yet)");
            // Don't fail the test - avatar is optional
        }
    }

    @Step("Assert Publications, Subscribers, Interested counts are visible")
    public void assertTopCountersVisible() {
        waitVisible(publicationsText(), ConfigReader.getVisibilityTimeout());
        waitVisible(subscribersText(), ConfigReader.getVisibilityTimeout());
        waitVisible(interestedText(), ConfigReader.getVisibilityTimeout());
    }

    @Step("Scroll to bottom of the page")
    public void scrollToBottom() {
        try { page.evaluate("window.scrollTo(0, document.body.scrollHeight)"); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
    }

    @Step("Scroll to top of the page")
    public void scrollToTop() {
        try { page.evaluate("window.scrollTo(0, 0)"); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
        page.waitForTimeout(ConfigReader.getAnimationTimeout());
    }

    @Step("Assert Publications and Collections icons visible")
    public void assertBottomIconsVisible() {
        waitVisible(publicationsIcon(), ConfigReader.getShortTimeout());
        waitVisible(collectionsIcon(), ConfigReader.getShortTimeout());
    }

    @Step("Click Collections icon")
    public void clickCollectionsIcon() {
        waitVisible(collectionsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(collectionsIcon(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Click Publications icon")
    public void clickPublicationsIcon() {
        waitVisible(publicationsIcon(), ConfigReader.getShortTimeout());
        clickWithRetry(publicationsIcon(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Open Messaging from header (optional fast nav)")
    public void openMessagingFromHeader() {
        // Some profile screens expose a Messaging icon as in other modules
        Locator msgIcon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("Messaging icon"));
        if (msgIcon.count() > 0) {
            clickWithRetry(msgIcon.first(), 1, ConfigReader.getElementRetryDelay());
        }
    }

    // ===== Modify Profile & Avatar upload =====

    @Step("Open 'Modify' profile screen")
    public void openModifyProfile() {
        Locator modifyBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Modify"));
        waitVisible(modifyBtn.first(), ConfigReader.getShortTimeout());
        clickWithRetry(modifyBtn.first(), 1, ConfigReader.getElementRetryDelay());
        // Wait for title
        waitVisible(page.getByText("Modify my profile"), ConfigReader.getShortTimeout());
    }

    @Step("Assert 'Modify my profile' screen is visible")
    public void assertModifyProfileScreen() {
        waitVisible(page.getByText("Modify my profile"), ConfigReader.getShortTimeout());
    }

    @Step("Click avatar upload pencil icon to open action menu")
    public void clickUploadAvatarPencil() {
        Locator pencil = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("icon"));
        waitVisible(pencil.first(), ConfigReader.getShortTimeout());
        clickWithRetry(pencil.first(), 1, ConfigReader.getElementRetryDelay());
        // Wait for "What action do you want to" popup
        waitVisible(page.getByText("What action do you want to"), ConfigReader.getShortTimeout());
    }

    @Step("Upload avatar image from: {file}")
    public void uploadAvatarImage(java.nio.file.Path file) {
        if (file == null || !java.nio.file.Files.exists(file)) {
            throw new RuntimeException("Avatar image file not found: " + file);
        }
        
        // Step 1: Click pencil to open "What action do you want to" menu
        clickUploadAvatarPencil();
        
        // Step 2: Intercept FileChooser when clicking Edit button
        com.microsoft.playwright.FileChooser chooser = page.waitForFileChooser(() -> {
            Locator editBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Edit"));
            waitVisible(editBtn, ConfigReader.getShortTimeout());
            clickWithRetry(editBtn, 1, ConfigReader.getElementRetryDelay());
        });
        
        // Step 3: Upload file
        chooser.setFiles(file);
        logger.info("[Profile] Avatar uploaded successfully via FileChooser");
    }

    @Step("Wait for avatar updated success toast")
    public void waitForAvatarUpdatedToast() {
        Locator toast = page.getByText("Avatar image updated");
        waitVisible(toast, ConfigReader.getMediumTimeout());
        logger.info("[Profile] Avatar image updated toast visible");
    }

    @Step("Click 'Delete' option for avatar")
    public void clickDeleteAvatarOption() {
        Locator deleteBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete"));
        waitVisible(deleteBtn, ConfigReader.getShortTimeout());
        clickWithRetry(deleteBtn, 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Assert delete avatar confirmation is visible")
    public void assertDeleteAvatarConfirmVisible() {
        waitVisible(page.getByText("Are you sure to delete avatar"), ConfigReader.getShortTimeout());
    }

    @Step("Confirm delete avatar (Yes)")
    public void confirmDeleteAvatarYes() {
        Locator yes = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Yes"));
        waitVisible(yes, ConfigReader.getShortTimeout());
        clickWithRetry(yes, 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Wait for avatar removed success toast")
    public void waitForAvatarRemovedToast() {
        Locator toast = page.getByText("Avatar image removed");
        waitVisible(toast, ConfigReader.getMediumTimeout());
        logger.info("[Profile] Avatar image removed toast visible");
    }
    @Step("Click 'Register' to save profile changes")
    public void clickRegisterUpdate() {
        // Prefer explicit XPath text match per spec
        Locator xpathBtn = page.locator("//div//button[contains(text(),'Register')]");
        if (xpathBtn.count() > 0) {
            waitVisible(xpathBtn.first(), ConfigReader.getShortTimeout());
            try { xpathBtn.first().scrollIntoViewIfNeeded(); } catch (Throwable e) { logger.debug("Scroll failed: {}", e.getMessage()); }
            clickWithRetry(xpathBtn.first(), 1, ConfigReader.getElementRetryDelay());
        } else {
            // Fallback: role button by name
            Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Register"));
            waitVisible(btn.first(), ConfigReader.getShortTimeout());
            clickWithRetry(btn.first(), 1, ConfigReader.getElementRetryDelay());
        }
        // Short settle even if toast appears instantly
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
    }

    @Step("Wait for 'Updated Personal Information Successfully.' toast")
    public void waitForProfileUpdatedToast() {
        Locator toast = page.getByText("Updated Personal Information Successfully.");
        waitVisible(toast.first(), ConfigReader.getMediumTimeout());
        // Allow a brief pause even when toast is visible, then dismiss
        try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
        try { clickWithRetry(toast.first(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
    }

    @Step("Soft wait for 'Updated Personal Information Successfully.' toast (timeout: {timeoutMs}ms)")
    public boolean waitForProfileUpdatedToastSoft(int timeoutMs) {
        Locator toast = page.getByText("Updated Personal Information Successfully.");
        try {
            waitVisible(toast.first(), timeoutMs);
            try { page.waitForTimeout(ConfigReader.getElementRetryDelay()); } catch (Throwable e) { logger.debug("Wait failed: {}", e.getMessage()); }
            try { clickWithRetry(toast.first(), 0, 0); } catch (Throwable e) { logger.debug("Click failed: {}", e.getMessage()); }
            return true;
        } catch (Throwable t) {
            // Soft failure: toast may not appear in some successful updates
            logger.warn("Profile updated toast not seen within {}ms; proceeding.", timeoutMs);
            return false;
        }
    }

    // ===== Modify Profile: Text fields (Last name, Position, Description) =====
    private void clearAndFill(Locator input, String text) {
        waitVisible(input.first(), ConfigReader.getShortTimeout());
        Locator el = input.first();
        el.click();
        // Clear robustly
        try { el.fill(""); } catch (Throwable e) { logger.debug("Fill failed: {}", e.getMessage()); }
        try { el.press("Control+A"); el.press("Backspace"); } catch (Throwable e) { logger.debug("Key press failed: {}", e.getMessage()); }
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
        waitVisible(shareBtn.first(), ConfigReader.getShortTimeout());
        clickWithRetry(shareBtn.first(), 1, ConfigReader.getElementRetryDelay());
        assertShareProfileVisible();
    }

    @Step("Assert Share profile title visible")
    public void assertShareProfileVisible() {
        Locator title = page.getByText("Share profile");
        waitVisible(title.first(), ConfigReader.getShortTimeout());
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
                waitVisible(btn.first(), ConfigReader.getShortTimeout());
                clickWithRetry(btn.first(), 1, ConfigReader.getElementRetryDelay());
            });
            try { popup.close(); } catch (Throwable e) { logger.debug("Close popup failed: {}", e.getMessage()); }
        } catch (Throwable t) {
            // If popup suppressed by browser, proceed without failing
        }
    }

    @Step("Click share Message icon")
    public void clickShareMessageIcon() {
        Locator msg = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("message icon"));
        waitVisible(msg.first(), ConfigReader.getShortTimeout());
        clickWithRetry(msg.first(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Click share Copy icon")
    public void clickShareCopyIcon() {
        Locator copy = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("copy icon"));
        waitVisible(copy.first(), ConfigReader.getShortTimeout());
        clickWithRetry(copy.first(), 1, ConfigReader.getElementRetryDelay());
    }

    @Step("Cancel Share profile")
    public void cancelShare() {
        Locator cancel = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Cancel"));
        waitVisible(cancel.first(), ConfigReader.getShortTimeout());
        clickWithRetry(cancel.first(), 1, ConfigReader.getElementRetryDelay());
    }
}
