package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.CreatorMediaPushPage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Epic("Creator")
@Feature("Media Push")
public class CreatorMediaPushTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorMediaPushTest.class);

    private Path resourcePath(String first, String... more) {
        String projectDir = System.getProperty("user.dir");
        return Paths.get(projectDir).resolve(Paths.get(first, more));
    }

    @Story("Creator sends media push to Subscribers with image and video from device")
    @Test(priority = 1, description = "Media push flow via My Device: add image and video, set price, propose push, land on Messaging")
    public void creatorCanSendMediaPushToSubscribers() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPush] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();
        mp.clickIUnderstandIfPresent();

        // 2) Choose Media push and ensure segments screen
        logger.info("[MediaPush] Choosing 'Media push' and selecting segment: Subscribers");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.clickCreateNext();

        // 3) Ensure Add Push Media screen
        mp.ensureAddPushMediaScreen();

        // 4) Add first media: Image
        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageA.jpg");
        if (!Files.exists(img)) {
            throw new SkipException("Missing test asset: " + img);
        }
        logger.info("[MediaPush] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 5) Add second media: Video
        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoA.mp4");
        if (!Files.exists(vid)) {
            throw new SkipException("Missing test asset: " + vid);
        }
        logger.info("[MediaPush] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 6) Message + price
        logger.info("[MediaPush] Filling message and setting price 15€");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();

        // 7) Propose push media and assert final screen
        logger.info("[MediaPush] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends clear media push by disabling blur for each media")
    @Test(priority = 6, description = "Disable blur for both media (image+video), set price 15€, no promotion, then propose push and land on Messaging")
    public void creatorCanSendClearMediaPush() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPushClear] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();
        mp.clickIUnderstandIfPresent();

        // 2) Choose Media push and ensure segments screen
        logger.info("[MediaPushClear] Choosing 'Media push' and selecting segment: Subscribers");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.clickCreateNext();

        // 3) Ensure Add Push Media screen
        mp.ensureAddPushMediaScreen();

        // 4) Add first media: Image (Quick B) and disable blur
        Path img = resourcePath("src", "test", "resources", "Images", "QuickImageB.jpg");
        if (!Files.exists(img)) {
            throw new SkipException("Missing test asset: " + img);
        }
        logger.info("[MediaPushClear] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.disableBlurIfEnabled();
        mp.ensureBlurToggleDisabled();
        mp.clickNext();

        // 5) Add second media: Video (Quick B) and disable blur
        Path vid = resourcePath("src", "test", "resources", "Videos", "QuickVideoB.mp4");
        if (!Files.exists(vid)) {
            throw new SkipException("Missing test asset: " + vid);
        }
        logger.info("[MediaPushClear] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.disableBlurIfEnabled();
        mp.ensureBlurToggleDisabled();
        mp.clickNext();

        // 6) Message + price (no promotion)
        logger.info("[MediaPushClear] Filling message and setting price 15€ (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();

        // 7) Propose push media and assert final screen
        logger.info("[MediaPushClear] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push for Free (no promotion)")
    @Test(priority = 5, description = "Media push flow for Free price: select Free, upload random media, then propose push and land on Messaging")
    public void creatorCanSendMediaPushFree() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPushFree] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();
        mp.clickIUnderstandIfPresent();

        // 2) Choose Media push and ensure segments screen
        logger.info("[MediaPushFree] Choosing 'Media push' and selecting segment: Subscribers");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.clickCreateNext();

        // 3) Ensure Add Push Media screen
        mp.ensureAddPushMediaScreen();

        // 4) Add first media: pick a random existing image (QuickImageA.jpg)
        Path img = resourcePath("src", "test", "resources", "Images", "QuickImageA.jpg");
        if (!Files.exists(img)) {
            throw new SkipException("Missing test asset: " + img);
        }
        logger.info("[MediaPushFree] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 5) Add second media: pick a random existing video (QuickVideoA.mp4)
        Path vid = resourcePath("src", "test", "resources", "Videos", "QuickVideoA.mp4");
        if (!Files.exists(vid)) {
            throw new SkipException("Missing test asset: " + vid);
        }
        logger.info("[MediaPushFree] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 6) Message + price Free (no promotion)
        logger.info("[MediaPushFree] Filling message and selecting Free price (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.selectPriceFree();
        mp.ensureAddPromotionDisabled();

        // 7) Propose push media and assert final screen
        logger.info("[MediaPushFree] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with custom price 10€ and no promotion")
    @Test(priority = 4, description = "Media push flow with custom price: set 10€ using custom field, no promotion, then propose push and land on Messaging")
    public void creatorCanSendMediaPushWithCustomPriceNoPromotion() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPushCustomPrice] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();
        mp.clickIUnderstandIfPresent();

        // 2) Choose Media push and ensure segments screen
        logger.info("[MediaPushCustomPrice] Choosing 'Media push' and selecting segment: Subscribers");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.clickCreateNext();

        // 3) Ensure Add Push Media screen
        mp.ensureAddPushMediaScreen();

        // 4) Add first media: Image (A)
        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageA.jpg");
        if (!Files.exists(img)) {
            throw new SkipException("Missing test asset: " + img);
        }
        logger.info("[MediaPushCustomPrice] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 5) Add second media: Video (A)
        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoA.mp4");
        if (!Files.exists(vid)) {
            throw new SkipException("Missing test asset: " + vid);
        }
        logger.info("[MediaPushCustomPrice] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 6) Message + custom price (no promotion)
        logger.info("[MediaPushCustomPrice] Filling message and setting custom price to 10€ (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.openCustomPriceField();
        mp.fillCustomPriceEuro(10);
        mp.ensureAddPromotionDisabled();

        // 7) Propose push media and assert final screen
        logger.info("[MediaPushCustomPrice] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with euro discount (5€) and 7 days validity")
    @Test(priority = 3, description = "Media push flow with euro discount: enable promo, set 5€, 7 days validity, then propose push and land on Messaging")
    public void creatorCanSendMediaPushWithEuroDiscount() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPushEuro] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();
        mp.clickIUnderstandIfPresent();

        // 2) Choose Media push and ensure segments screen
        logger.info("[MediaPushEuro] Choosing 'Media push' and selecting segment: Subscribers");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.clickCreateNext();

        // 3) Ensure Add Push Media screen
        mp.ensureAddPushMediaScreen();

        // 4) Add first media: Image (C)
        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageC.jpg");
        if (!Files.exists(img)) {
            throw new SkipException("Missing test asset: " + img);
        }
        logger.info("[MediaPushEuro] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 5) Add second media: Video (C)
        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoC.mp4");
        if (!Files.exists(vid)) {
            throw new SkipException("Missing test asset: " + vid);
        }
        logger.info("[MediaPushEuro] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 6) Message + price + euro promotion
        logger.info("[MediaPushEuro] Filling message, price 15€, then enabling promotion with 5€ discount and 7 days validity");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();
        mp.enablePromotionToggle();
        // For euro discount path
        mp.openEuroDiscountField();
        mp.fillEuroDiscountEuro(5);
        mp.ensureValidityTitle();
        mp.selectValidity7Days();

        // 7) Propose push media and assert final screen
        logger.info("[MediaPushEuro] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with promotion (10% discount, unlimited validity)")
    @Test(priority = 2, description = "Media push flow with promotion: enable promo, set 10% discount, unlimited validity, then propose push and land on Messaging")
    public void creatorCanSendMediaPushWithPromotion() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPushPromo] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();
        mp.clickIUnderstandIfPresent();

        // 2) Choose Media push and ensure segments screen
        logger.info("[MediaPushPromo] Choosing 'Media push' and selecting segment: Subscribers");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.clickCreateNext();

        // 3) Ensure Add Push Media screen
        mp.ensureAddPushMediaScreen();

        // 4) Add first media: Image (B)
        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageB.jpg");
        if (!Files.exists(img)) {
            throw new SkipException("Missing test asset: " + img);
        }
        logger.info("[MediaPushPromo] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 5) Add second media: Video (B)
        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoB.mp4");
        if (!Files.exists(vid)) {
            throw new SkipException("Missing test asset: " + vid);
        }
        logger.info("[MediaPushPromo] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        // 6) Message + price + promotion
        logger.info("[MediaPushPromo] Filling message, price 15€, then enabling promotion with 10% discount and unlimited validity");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();
        mp.enablePromotionToggle();
        mp.ensureDiscountVisible();
        mp.openDiscountPercentField();
        mp.fillDiscountPercent(10);
        mp.ensureValidityTitle();
        mp.selectValidityUnlimited();

        // 7) Propose push media and assert final screen
        logger.info("[MediaPushPromo] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }
}
