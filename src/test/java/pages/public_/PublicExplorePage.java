package pages.public_;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import pages.common.BasePage;
import utils.ConfigReader;

/**
 * Page object for the public explore grid (/public-explore).
 * Shows a thumbnail grid of creator content. Accessed via the Search icon in the bottom nav.
 */
public class PublicExplorePage extends BasePage {

    private static final String PUBLIC_EXPLORE_URL_PART = "/public-explore";
    private static final String SEARCH_PLACEHOLDER = "Search";

    public PublicExplorePage(Page page) {
        super(page);
    }

    public boolean isOnPublicExplore() {
        return page.url().contains(PUBLIC_EXPLORE_URL_PART);
    }

    public boolean isSearchBarVisible() {
        Locator searchBox = page.getByRole(AriaRole.SEARCHBOX, new Page.GetByRoleOptions().setName(SEARCH_PLACEHOLDER));
        try {
            searchBox.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Explore search bar wait failed: {}", e.getMessage()); }
        return safeIsVisible(searchBox);
    }

    public boolean isThumbnailGridVisible() {
        // The explore page shows clickable thumbnail elements in a grid
        Locator thumbnails = page.locator("main [cursor=pointer], main img");
        try {
            thumbnails.first().waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Thumbnail grid wait failed: {}", e.getMessage()); }
        return thumbnails.count() > 0;
    }

    public int getThumbnailCount() {
        // Count clickable items in the main content area
        Locator items = page.locator("main").locator("[class*='cursor']");
        int count = items.count();
        logger.info("Explore thumbnails visible: {}", count);
        return count;
    }
}
