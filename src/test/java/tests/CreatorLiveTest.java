package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.CreatorLivePage;

import java.nio.file.Path;
import java.time.LocalDateTime;
import utils.DateTimeUtils;
import utils.TestAssets;

@Epic("Creator")
@Feature("Live")
public class CreatorLiveTest extends BaseCreatorTest {
    
    // Smart common cleanup: after each test, attempt to delete any scheduled live if present
    @AfterMethod(alwaysRun = true)
    public void cleanUpScheduledLive() {
        // This is safe: it internally checks presence and returns false if nothing to delete
        CreatorLivePage live = new CreatorLivePage(page);
        live.tryDeleteLatestLiveEvent();
    }

    @Story("Create scheduled live event")
    @Test(priority = 1, description = "Creator schedules a live with price and description")
    public void creatorCanScheduleLive() {
        // 1) Navigate to Live (plus menu -> Live)
        CreatorLivePage live = new CreatorLivePage(page);
        live.openPlusMenu();
        live.navigateToLive();

        // 3) Prepare scheduling time using utility
        LocalDateTime when = DateTimeUtils.futureAtDaysHour(1, 3, 0);

        // 4) Coverage image (optional if not present)
        Path coverage = TestAssets.imageOrNull("Live A.jpg");

        // 2) Fill live form step-by-step
        live.setAccessEveryone();
        live.setPriceEuro(15);
        live.enableChatEveryoneIfPresent();
        live.chooseSchedule();
        live.pickDate(when);
        // Prefer dynamic time candidates matching UI from the chosen date
        String[] timeCandidates = DateTimeUtils.futureTimeCandidates(when);
        live.pickTimeCandidates(timeCandidates);
        live.uploadCoverage(coverage);
        live.setDescription("Test");
        live.submitAndVerify();

        // Flow completed without exceptions
    }

    

    @Story("Create scheduled live event for Subscribers with custom price")
    @Test(priority = 2, description = "Creator schedules a live for Subscribers with custom price and chat")
    public void creatorCanScheduleLiveForSubscribersCustomPrice() {
        CreatorLivePage live = new CreatorLivePage(page);
        live.openPlusMenu();
        live.navigateToLive();

        LocalDateTime when = DateTimeUtils.futureAtDaysHour(1, 3, 0);

        Path coverage = TestAssets.imageOrNull("Live D.jpg");

        live.setAccessSubscribers();
        // custom amount from request
        live.setCustomPriceEuro("5");
        live.enableChatSubscribersIfPresent();
        live.chooseSchedule();
        live.pickDate(when);
        String[] timeCandidates2 = DateTimeUtils.futureTimeCandidates(when);
        live.pickTimeCandidates(timeCandidates2);
        live.uploadCoverage(coverage);
        live.setDescription("Test - subscribers");
        live.submitAndVerify();

        // Flow completed without exceptions
    }

    @Story("Create free live event for Everyone with chat limited to Subscribers")
    @Test(priority = 3, description = "Creator schedules a free live for Everyone with chat only for Subscribers")
    public void creatorCanScheduleFreeLiveEveryoneChatSubscribers() {
        CreatorLivePage live = new CreatorLivePage(page);
        live.openPlusMenu();
        live.navigateToLive();

        LocalDateTime when = DateTimeUtils.futureAtDaysHour(1, 3, 0);

        Path coverage = TestAssets.imageOrNull("Live C.jpg");

        live.setAccessEveryone();
        live.setPriceFree();
        live.enableChatSubscribersIfPresent();
        live.chooseSchedule();
        live.pickDate(when);
        String[] timeCandidates2 = DateTimeUtils.futureTimeCandidates(when);
        live.pickTimeCandidates(timeCandidates2);
        live.uploadCoverage(coverage);
        live.setDescription("Free event - Everyone access, chat for Subscribers");
        live.submitAndVerify();

        // Flow completed without exceptions
    }

    @Story("Create free live event for Subscribers")
    @Test(priority = 4, description = "Creator schedules a free live for Subscribers")
    public void creatorCanScheduleFreeLiveForSubscribers() {
        CreatorLivePage live = new CreatorLivePage(page);
        live.openPlusMenu();
        live.navigateToLive();

        LocalDateTime when = DateTimeUtils.futureAtDaysHour(1, 3, 0);

        Path coverage = TestAssets.imageOrNull("Live B.jpg");

        live.setAccessSubscribers();
        live.setPriceFree();
        live.enableChatSubscribersIfPresent();
        live.chooseSchedule();
        live.pickDate(when);
        String[] timeCandidates4 = DateTimeUtils.futureTimeCandidates(when);
        live.pickTimeCandidates(timeCandidates4);
        live.uploadCoverage(coverage);
        live.setDescription("Free event - Subscribers access");
        live.submitAndVerify();

        // Flow completed without exceptions
    }
}
