package tests.creator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.testng.annotations.Test;
import pages.creator.CreatorMediaPushPage;
import testdata.MediaPushData;

@Epic("Creator")
@Feature("Media Push")
public class CreatorMediaPushTest extends BaseCreatorTest {

    // ===== Subscribers only — device media (priorities 1-6) =====

    @Story("Creator sends media push to Subscribers with image and video from device")
    @Test(priority = 1, description = "Media push flow via My Device: add image and video, set price, propose push, land on Messaging")
    public void creatorCanSendMediaPushToSubscribers() {
        new CreatorMediaPushPage(page).executeMediaPushFlow("MediaPush", MediaPushData.STANDARD_SCENARIOS[0]);
    }

    @Story("Creator sends clear media push by disabling blur for each media")
    @Test(priority = 2, description = "Disable blur for both media (image+video), set price 15€, no promotion, then propose push and land on Messaging")
    public void creatorCanSendClearMediaPush() {
        new CreatorMediaPushPage(page).executeMediaPushFlow("MediaPushClear", MediaPushData.STANDARD_SCENARIOS[1]);
    }

    @Story("Creator sends media push for Free (no promotion)")
    @Test(priority = 3, description = "Media push flow for Free price: select Free, upload random media, then propose push and land on Messaging")
    public void creatorCanSendMediaPushFree() {
        new CreatorMediaPushPage(page).executeMediaPushFlow("MediaPushFree", MediaPushData.STANDARD_SCENARIOS[2]);
    }

    @Story("Creator sends media push with custom price 10€ and no promotion")
    @Test(priority = 4, description = "Media push flow with custom price: set 10€ using custom field, no promotion, then propose push and land on Messaging")
    public void creatorCanSendMediaPushWithCustomPriceNoPromotion() {
        new CreatorMediaPushPage(page).executeMediaPushFlow("MediaPushCustomPrice", MediaPushData.STANDARD_SCENARIOS[3]);
    }

    @Story("Creator sends media push with euro discount (5€) and 7 days validity")
    @Test(priority = 5, description = "Media push flow with euro discount: enable promo, set 5€, 7 days validity, then propose push and land on Messaging")
    public void creatorCanSendMediaPushWithEuroDiscount() {
        new CreatorMediaPushPage(page).executeMediaPushFlow("MediaPushEuro", MediaPushData.STANDARD_SCENARIOS[4]);
    }

    @Story("Creator sends media push with promotion (10% discount, unlimited validity)")
    @Test(priority = 6, description = "Media push flow with promotion 10% unlimited")
    public void creatorCanSendMediaPushWithPromotion() {
        new CreatorMediaPushPage(page).executeMediaPushFlow("MediaPushPromo", MediaPushData.STANDARD_SCENARIOS[5]);
    }

    // ===== Subscribers + Quick Files (priority 7) =====

    @Story("Creator sends media push to Subscribers via Quick Files")
    @Test(priority = 7, description = "Media push flow: select Subscribers segment, use Quick Files, propose push, land on Messaging")
    public void creatorCanSendMediaPushWithQuickFiles() {
        new CreatorMediaPushPage(page).executeMediaPushFlow("MediaPushQuickFiles", MediaPushData.QUICK_FILES_SCENARIO);
    }
}
