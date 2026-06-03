package tests.creator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import pages.creator.CreatorLivePage;
import testdata.LiveEventData;
import utils.DateTimeUtils;

/**
 * Tests creator live event scheduling with different access and pricing configurations.
 * Uses LiveEventData for test data generation.
 */
@Epic("Creator")
@Feature("Live")
public class CreatorLiveTest extends BaseCreatorTest {

    @AfterMethod(alwaysRun = true)
    public void cleanUpScheduledLive() {
        CreatorLivePage live = new CreatorLivePage(page);
        live.tryDeleteLatestLiveEvent();
    }

    @Story("Create scheduled live event")
    @Test(priority = 1, description = "Creator schedules a live with price and description")
    public void creatorCanScheduleLive() {
        LiveEventData data = LiveEventData.everyoneWithFixedPrice();
        CreatorLivePage live = new CreatorLivePage(page);
        
        live.openPlusMenu();
        live.navigateToLive();
        live.setAccessEveryone();
        live.setPriceEuro(data.priceEuro);
        live.enableChatEveryoneIfPresent();
        live.chooseSchedule();
        live.pickDate(data.scheduledTime);
        String[] timeCandidates = DateTimeUtils.futureTimeCandidates(data.scheduledTime);
        live.pickTimeCandidates(timeCandidates);
        live.uploadCoverage(data.coverageImage);
        live.setDescription(data.description);
        live.submitAndVerify();
        
        Assert.assertTrue(live.isLiveLogoVisibleOnProfile(), 
            "Live logo should be visible on profile after scheduling");
    }

    @Story("Create scheduled live event for Subscribers with custom price")
    @Test(priority = 2, description = "Creator schedules a live for Subscribers with custom price and chat")
    public void creatorCanScheduleLiveForSubscribersCustomPrice() {
        LiveEventData data = LiveEventData.subscribersWithCustomPrice();
        CreatorLivePage live = new CreatorLivePage(page);
        
        live.openPlusMenu();
        live.navigateToLive();
        live.setAccessSubscribers();
        live.setCustomPriceEuro(data.customPrice);
        live.enableChatSubscribersIfPresent();
        live.chooseSchedule();
        live.pickDate(data.scheduledTime);
        String[] timeCandidates = DateTimeUtils.futureTimeCandidates(data.scheduledTime);
        live.pickTimeCandidates(timeCandidates);
        live.uploadCoverage(data.coverageImage);
        live.setDescription(data.description);
        live.submitAndVerify();
        
        Assert.assertTrue(live.isLiveLogoVisibleOnProfile(), 
            "Live logo should be visible on profile after scheduling");
    }
}
