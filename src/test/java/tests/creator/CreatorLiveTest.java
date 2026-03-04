package tests.creator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.creator.CreatorLivePage;

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
        CreatorLivePage live = new CreatorLivePage(page);
        live.openPlusMenu();
        live.navigateToLive();

        LocalDateTime when = DateTimeUtils.futureAtDaysHour(1, 3, 0);
        Path coverage = TestAssets.imageOrNull("Live A.jpg");

        live.setAccessEveryone();
        live.setPriceEuro(15);
        live.enableChatEveryoneIfPresent();
        live.chooseSchedule();
        live.pickDate(when);
        String[] timeCandidates = DateTimeUtils.futureTimeCandidates(when);
        live.pickTimeCandidates(timeCandidates);
        live.uploadCoverage(coverage);
        live.setDescription("Test");
        live.submitAndVerify();
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
        live.setCustomPriceEuro("5");
        live.enableChatSubscribersIfPresent();
        live.chooseSchedule();
        live.pickDate(when);
        String[] timeCandidates = DateTimeUtils.futureTimeCandidates(when);
        live.pickTimeCandidates(timeCandidates);
        live.uploadCoverage(coverage);
        live.setDescription("Test - subscribers");
        live.submitAndVerify();
    }
}
