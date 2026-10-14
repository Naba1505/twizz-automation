package pages.public_;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import pages.common.BasePage;
import utils.ConfigReader;

/**
 * Page object for the public discover feed (/public-discover).
 * This is the first page a fan sees after clicking "Fans" or "Discover TWIZZ" from the landing page.
 * Shows creator video cards in a vertical feed with a bottom navigation bar.
 */
public class PublicDiscoverPage extends BasePage {

    private static final String PUBLIC_DISCOVER_URL_PART = "/public-discover";
    private static final String SEARCH_PLACEHOLDER = "Search";
    private static final String HOME_ICON = "Home icon";
    private static final String SEARCH_ICON = "Search icon";
    private static final String MESSAGING_ICON = "Messaging icon";
    private static final String LIVE_ICON = "Live icon";
    private static final String SETTINGS_ICON = "Settings icon";
    private static final String DISCOVER_PROFILE_TEXT = "Discover profile";
    private static final String UNMUTE_BTN = "Unmute";

    public PublicDiscoverPage(Page page) {
        super(page);
    }

    public boolean isOnPublicDiscover() {
        return page.url().contains(PUBLIC_DISCOVER_URL_PART);
    }

    public boolean isSearchBarVisible() {
        Locator searchBox = page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName(SEARCH_PLACEHOLDER));
        try {
            searchBox.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Search bar wait failed: {}", e.getMessage()); }
        return safeIsVisible(searchBox);
    }

    public boolean isCreatorFeedVisible() {
        Locator discoverLinks = page.getByText(DISCOVER_PROFILE_TEXT);
        try {
            discoverLinks.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Creator feed wait failed: {}", e.getMessage()); }
        return discoverLinks.count() > 0;
    }

    public int getCreatorCardCount() {
        Locator discoverLinks = page.getByText(DISCOVER_PROFILE_TEXT);
        try {
            discoverLinks.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Creator card count wait failed: {}", e.getMessage()); }
        int count = discoverLinks.count();
        logger.info("Creator cards visible: {}", count);
        return count;
    }

    public boolean isUnmuteButtonVisible() {
        Locator unmute = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(UNMUTE_BTN));
        try {
            unmute.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getMediumTimeout()));
        } catch (Exception e) { logger.debug("Unmute button wait failed: {}", e.getMessage()); }
        return safeIsVisible(unmute);
    }

    // -- Bottom navigation bar --

    public boolean isHomeIconVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(HOME_ICON)));
    }

    public boolean isSearchIconVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SEARCH_ICON)));
    }

    public boolean isMessagingIconVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(MESSAGING_ICON)));
    }

    public boolean isLiveIconVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(LIVE_ICON)));
    }

    public boolean isSettingsIconVisible() {
        return safeIsVisible(page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SETTINGS_ICON)));
    }

    public void clickHomeIcon() {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(HOME_ICON)).click();
        page.waitForLoadState();
        logger.info("Clicked Home icon. URL: {}", page.url());
    }

    public void clickSearchIcon() {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SEARCH_ICON)).click();
        page.waitForLoadState();
        logger.info("Clicked Search icon. URL: {}", page.url());
    }

    public void clickMessagingIcon() {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(MESSAGING_ICON)).click();
        logger.info("Clicked Messaging icon. URL: {}", page.url());
    }

    public void clickLiveIcon() {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(LIVE_ICON)).click();
        page.waitForLoadState();
        logger.info("Clicked Live icon. URL: {}", page.url());
    }

    public void clickSettingsIcon() {
        page.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName(SETTINGS_ICON)).click();
        logger.info("Clicked Settings icon. URL: {}", page.url());
    }

    /**
     * Clicks the first "Discover profile" link in the feed.
     * Returns the creator name text from the card.
     */
    public String clickFirstCreatorCard() {
        Locator firstCard = page.getByText(DISCOVER_PROFILE_TEXT).first();
        // The creator name is in a sibling element above the "Discover profile" text
        Locator cardContainer = firstCard.locator("xpath=ancestor::*[contains(@class, 'cursor')]").first();
        String creatorName = "";
        try {
            // Get the text content of the container which includes the creator name
            String fullText = cardContainer.textContent();
            creatorName = fullText.replace(DISCOVER_PROFILE_TEXT, "").trim();
        } catch (Exception e) {
            logger.debug("Could not extract creator name: {}", e.getMessage());
        }
        firstCard.click();
        page.waitForLoadState();
        logger.info("Clicked first creator card. Creator: {}. URL: {}", creatorName, page.url());
        return creatorName;
    }

    public void typeInSearchBar(String query) {
        Locator searchBox = page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName(SEARCH_PLACEHOLDER));
        searchBox.click();
        searchBox.fill(query);
        logger.info("Typed '{}' in search bar", query);
    }
}
