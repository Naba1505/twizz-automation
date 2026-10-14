package pages.public_;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import pages.common.BasePage;
import utils.ConfigReader;

/**
 * Page object for the public lives page (/p/fan/lives).
 * Shows Live, Events, and Replay tabs for scheduled/ongoing live streams.
 */
public class LivesPage extends BasePage {

    private static final String LIVES_URL_PART = "/p/fan/lives";
    private static final String LIVES_HEADING = "Lives";
    private static final String LIVE_TAB = "Live";
    private static final String EVENTS_TAB = "Events";
    private static final String REPLAY_TAB = "Replay";
    private static final String TODAYS_EVENTS = "Today's events";
    private static final String UPCOMING = "Upcoming";
    private static final String NO_SCHEDULED = "No scheduled events for today";
    private static final String NO_UPCOMING = "No upcoming events found";

    public LivesPage(Page page) {
        super(page);
    }

    public boolean isOnLivesPage() {
        return page.url().contains(LIVES_URL_PART);
    }

    public boolean isLivesHeadingVisible() {
        Locator heading = page.getByText(LIVES_HEADING).first();
        try {
            heading.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Lives heading wait failed: {}", e.getMessage()); }
        return safeIsVisible(heading);
    }

    public boolean isLiveTabVisible() {
        Locator tab = page.getByRole(AriaRole.TAB).filter(
                new Locator.FilterOptions().setHasText(LIVE_TAB));
        return safeIsVisible(tab);
    }

    public boolean isEventsTabVisible() {
        Locator tab = page.getByRole(AriaRole.TAB).filter(
                new Locator.FilterOptions().setHasText(EVENTS_TAB));
        return safeIsVisible(tab);
    }

    public boolean isReplayTabVisible() {
        Locator tab = page.getByRole(AriaRole.TAB).filter(
                new Locator.FilterOptions().setHasText(REPLAY_TAB));
        return safeIsVisible(tab);
    }

    public void clickLiveTab() {
        page.getByRole(AriaRole.TAB).filter(new Locator.FilterOptions().setHasText(LIVE_TAB)).click();
        logger.info("Clicked Live tab");
    }

    public void clickEventsTab() {
        page.getByRole(AriaRole.TAB).filter(new Locator.FilterOptions().setHasText(EVENTS_TAB)).click();
        logger.info("Clicked Events tab");
    }

    public void clickReplayTab() {
        page.getByRole(AriaRole.TAB).filter(new Locator.FilterOptions().setHasText(REPLAY_TAB)).click();
        logger.info("Clicked Replay tab");
    }

    public boolean isTodaysEventsVisible() {
        Locator text = page.getByText(TODAYS_EVENTS);
        try {
            text.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Today's events wait failed: {}", e.getMessage()); }
        return safeIsVisible(text);
    }

    public boolean isUpcomingVisible() {
        Locator text = page.getByText(UPCOMING);
        try {
            text.waitFor(new Locator.WaitForOptions().setTimeout(ConfigReader.getVisibilityTimeout()));
        } catch (Exception e) { logger.debug("Upcoming wait failed: {}", e.getMessage()); }
        return safeIsVisible(text);
    }

    public boolean isNoScheduledEventsVisible() {
        return safeIsVisible(page.getByText(NO_SCHEDULED));
    }

    public boolean isNoUpcomingEventsVisible() {
        return safeIsVisible(page.getByText(NO_UPCOMING));
    }
}
