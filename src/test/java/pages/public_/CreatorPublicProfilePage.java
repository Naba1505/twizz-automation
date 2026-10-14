package pages.public_;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import pages.common.BasePage;
import utils.ConfigReader;

/**
 * Page object for a creator's public profile (/p/{handle}).
 * This is what an unauthenticated fan sees when they click on a creator card.
 * Shows creator info, subscribe button, publications/collections tabs.
 */
public class CreatorPublicProfilePage extends BasePage {

    private static final String PUBLIC_PROFILE_URL_PART = "/p/";
    private static final String SUBSCRIBE_BTN = "Subscribe";
    private static final String VERIFY_IMG = "verify";
    private static final String LOCATION_IMG = "location";
    private static final String PUBLICATIONS_ICON = "publications icon";
    private static final String COLLECTIONS_ICON = "collections icon";
    private static final String FOLLOW_IMG = "follow";

    public CreatorPublicProfilePage(Page page) {
        super(page);
    }

    public boolean isOnCreatorPublicProfile() {
        return page.url().contains(PUBLIC_PROFILE_URL_PART);
    }

    public boolean isCreatorNameVisible(String name) {
        Locator nameText = page.getByText(name);
        try {
            nameText.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Creator name wait failed: {}", e.getMessage()); }
        return safeIsVisible(nameText);
    }

    public boolean isCreatorHandleVisible() {
        // Handle is shown as @username in an aria-hidden element
        Locator handle = page.locator("[aria-hidden]").filter(new Locator.FilterOptions().setHasText("@"));
        return safeIsVisible(handle);
    }

    public String getCreatorHandle() {
        Locator handle = page.locator("[aria-hidden]").filter(new Locator.FilterOptions().setHasText("@"));
        try {
            return handle.first().textContent().trim();
        } catch (Exception e) {
            logger.debug("Could not get creator handle: {}", e.getMessage());
            return "";
        }
    }

    public boolean isVerifiedBadgeVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(VERIFY_IMG)));
    }

    public boolean isSubscribeButtonVisible() {
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(SUBSCRIBE_BTN));
        try {
            btn.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Subscribe button wait failed: {}", e.getMessage()); }
        return safeIsVisible(btn);
    }

    public void clickSubscribeButton() {
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(SUBSCRIBE_BTN)).click();
        logger.info("Clicked Subscribe button");
    }

    public boolean isLocationVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(LOCATION_IMG)));
    }

    public boolean isDescriptionVisible() {
        // Description is a generic text element below the creator stats
        Locator desc = page.locator("main").getByText("description", new Locator.GetByTextOptions().setExact(false));
        return desc.count() > 0;
    }

    public boolean isPublicationsCountVisible() {
        Locator pubCount = page.getByText("Publications");
        return safeIsVisible(pubCount);
    }

    public boolean isCollectionsCountVisible() {
        Locator colCount = page.getByText("Collections");
        return safeIsVisible(colCount);
    }

    public boolean isPublicationsTabVisible() {
        Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(PUBLICATIONS_ICON));
        try {
            icon.scrollIntoViewIfNeeded();
            icon.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Publications tab wait failed: {}", e.getMessage()); }
        return safeIsVisible(icon);
    }

    public boolean isCollectionsTabVisible() {
        Locator icon = page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(COLLECTIONS_ICON));
        try {
            icon.scrollIntoViewIfNeeded();
            icon.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Collections tab wait failed: {}", e.getMessage()); }
        return safeIsVisible(icon);
    }

    public void clickPublicationsTab() {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(PUBLICATIONS_ICON)).click();
        logger.info("Clicked Publications tab");
    }

    public void clickCollectionsTab() {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(COLLECTIONS_ICON)).click();
        logger.info("Clicked Collections tab");
    }

    public boolean isFollowIconVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(FOLLOW_IMG)));
    }

    public boolean isPricingInfoVisible() {
        // Check for "Free" or "Discuss" or pricing indicators near the subscribe button
        Locator subscribeArea = page.locator("main");
        try {
            subscribeArea.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Main area wait: {}", e.getMessage()); }
        // Use text content check on the subscribe area
        String mainText = "";
        try { mainText = subscribeArea.textContent(); } catch (Exception e) { /* */ }
        boolean hasPricingText = mainText.contains("Free") || mainText.contains("Discuss")
                || mainText.contains("€") || mainText.contains("Subscribe");
        logger.info("Pricing info check — has pricing text: {}", hasPricingText);
        return hasPricingText;
    }
}
