package tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
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

    // Handles post-propose transient weekly-limit popup robustly.
    // Waits up to ~5s for either the popup text or the button to appear, clicks if found, and ends the test.
    private boolean handleIUnderstandAfterProposeIfVisible() {
        Locator msg = page.getByText("Your weekly limit has been reached");
        Locator btn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("I understand"));

        try {
            for (int i = 0; i < 25; i++) { // ~5 seconds @ 200ms
                boolean msgVisible = false;
                boolean btnVisible = false;
                try {
                    msgVisible = msg.isVisible();
                } catch (Exception ignored) {
                }
                try {
                    btnVisible = btn.isVisible();
                } catch (Exception ignored) {
                }

                if (msgVisible || btnVisible) {
                    logger.info("[PostPropose] Limiter popup/button detected - clicking 'I understand' and ending test");
                    try {
                        btn.click();
                    } catch (Exception clickErr) {
                        // Retry once if stale timing
                        page.waitForTimeout(250);
                        btn.click();
                    }
                    return true;
                }
                page.waitForTimeout(200);
            }
        } catch (Exception ignored) {
            // Ignore and fall through
        }

        return false;
    }

    @Story("Creator sends media push to Subscribers with image and video from device")
    @Test(priority = 1, description = "Media push flow via My Device: add image and video, set price, propose push, land on Messaging")
    public void creatorCanSendMediaPushToSubscribers() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPush] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

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
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends clear media push by disabling blur for each media")
    @Test(priority = 2, description = "Disable blur for both media (image+video), set price 15€, no promotion, then propose push and land on Messaging")
    public void creatorCanSendClearMediaPush() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPushClear] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

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
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push for Free (no promotion)")
    @Test(priority = 3, description = "Media push flow for Free price: select Free, upload random media, then propose push and land on Messaging")
    public void creatorCanSendMediaPushFree() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPushFree] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

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
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
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
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with euro discount (5€) and 7 days validity")
    @Test(priority = 5, description = "Media push flow with euro discount: enable promo, set 5€, 7 days validity, then propose push and land on Messaging")
    public void creatorCanSendMediaPushWithEuroDiscount() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPushEuro] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

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
    @Test(priority = 6, description = "Media push flow with promotion: enable promo, set 10% discount, unlimited validity, then propose push and land on Messaging")
    public void creatorCanSendMediaPushWithPromotion() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        // 1) Open plus menu and ensure options popup
        logger.info("[MediaPushPromo] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

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

    @Story("Creator sends media push to Interested with image and video from device")
    @Test(priority = 7, description = "Media push flow via My Device (Interested): add image and video, set price, propose push, land on Messaging")
    public void creatorCanSendMediaPushToInterested() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushInterested] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushInterested] Choosing 'Media push' and selecting segment: Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageA.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushInterested] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        Page p = this.page;
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).click();

        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoA.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushInterested] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).click();

        logger.info("[MediaPushInterested] Filling message and setting price 15€");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushInterested] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with promotion (10% discount, unlimited validity) to Interested")
    @Test(priority = 8, description = "Media push flow with promotion (Interested): 10% discount, unlimited validity")
    public void creatorCanSendMediaPushWithPromotionInterested() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushPromoInterested] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushPromoInterested] Choosing 'Media push' and selecting segment: Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageB.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushPromoInterested] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoB.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushPromoInterested] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        logger.info("[MediaPushPromoInterested] Filling message, price 15€, enabling promotion 10% unlimited");
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

        logger.info("[MediaPushPromoInterested] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with euro discount (5€) and 7 days validity to Interested")
    @Test(priority = 9, description = "Media push flow with euro discount (Interested): 5€, 7 days validity")
    public void creatorCanSendMediaPushWithEuroDiscountInterested() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushEuroInterested] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushEuroInterested] Choosing 'Media push' and selecting segment: Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageC.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushEuroInterested] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoC.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushEuroInterested] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        logger.info("[MediaPushEuroInterested] Filling message, price 15€, euro discount 5€, validity 7 days");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();
        mp.enablePromotionToggle();
        mp.openEuroDiscountField();
        mp.fillEuroDiscountEuro(5);
        mp.ensureValidityTitle();
        mp.selectValidity7Days();

        logger.info("[MediaPushEuroInterested] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with custom price 10€ and no promotion to Interested")
    @Test(priority = 10, description = "Media push flow with custom price 10€ (Interested), no promotion")
    public void creatorCanSendMediaPushWithCustomPriceNoPromotionInterested() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushCustomPriceInterested] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushCustomPriceInterested] Choosing 'Media push' and selecting segment: Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageA.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushCustomPriceInterested] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoA.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushCustomPriceInterested] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        logger.info("[MediaPushCustomPriceInterested] Message + custom price 10€ (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.openCustomPriceField();
        mp.fillCustomPriceEuro(10);
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushCustomPriceInterested] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push for Free (no promotion) to Interested")
    @Test(priority = 11, description = "Media push flow Free price (Interested)")
    public void creatorCanSendMediaPushFreeInterested() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushFreeInterested] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushFreeInterested] Choosing 'Media push' and selecting segment: Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "QuickImageA.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushFreeInterested] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        Path vid = resourcePath("src", "test", "resources", "Videos", "QuickVideoA.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushFreeInterested] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        logger.info("[MediaPushFreeInterested] Message + Free price (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.selectPriceFree();
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushFreeInterested] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends clear media push to Interested by disabling blur for each media")
    @Test(priority = 12, description = "Disable blur for image+video (Interested), set price 15€, no promotion")
    public void creatorCanSendClearMediaPushInterested() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushClearInterested] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushClearInterested] Choosing 'Media push' and selecting segment: Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "QuickImageB.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushClearInterested] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.disableBlurIfEnabled();
        mp.ensureBlurToggleDisabled();
        mp.clickNext();

        Path vid = resourcePath("src", "test", "resources", "Videos", "QuickVideoB.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushClearInterested] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.disableBlurIfEnabled();
        mp.ensureBlurToggleDisabled();
        mp.clickNext();

        logger.info("[MediaPushClearInterested] Message + price 15€ (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushClearInterested] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    // ===== Multi-select: Subscribers + Interested (priorities 13-18) =====

    @Story("Creator sends media push to Subscribers + Interested with image and video from device")
    @Test(priority = 13, description = "Media push via My Device (multi-select): add image and video, set price 15€, propose push, land on Messaging")
    public void creatorCanSendMediaPushMultiSelectDevice() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushMulti] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushMulti] Choosing 'Media push' and selecting segments: Subscribers + Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageA.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushMulti] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoA.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushMulti] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        logger.info("[MediaPushMulti] Message + price 15€ (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushMulti] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends clear media push (disable blur) to Subscribers + Interested")
    @Test(priority = 14, description = "Disable blur for image+video (multi-select), set price 15€, no promotion, propose push, land on Messaging")
    public void creatorCanSendClearMediaPushMultiSelect() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushClearMulti] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushClearMulti] Choosing 'Media push' and selecting segments: Subscribers + Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "QuickImageB.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushClearMulti] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.disableBlurIfEnabled();
        mp.ensureBlurToggleDisabled();
        mp.clickNext();

        Path vid = resourcePath("src", "test", "resources", "Videos", "QuickVideoB.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushClearMulti] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.disableBlurIfEnabled();
        mp.ensureBlurToggleDisabled();
        mp.clickNext();

        logger.info("[MediaPushClearMulti] Message + price 15€ (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushClearMulti] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push for Free (no promotion) to Subscribers + Interested")
    @Test(priority = 15, description = "Media push flow Free price (multi-select)")
    public void creatorCanSendMediaPushFreeMultiSelect() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushFreeMulti] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushFreeMulti] Choosing 'Media push' and selecting segments: Subscribers + Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "QuickImageA.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushFreeMulti] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        Path vid = resourcePath("src", "test", "resources", "Videos", "QuickVideoA.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushFreeMulti] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        logger.info("[MediaPushFreeMulti] Message + Free price (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.selectPriceFree();
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushFreeMulti] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with custom price 10€ (no promotion) to Subscribers + Interested")
    @Test(priority = 16, description = "Media push flow with custom price 10€ (multi-select), no promotion")
    public void creatorCanSendMediaPushWithCustomPriceNoPromotionMultiSelect() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushCustomPriceMulti] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushCustomPriceMulti] Choosing 'Media push' and selecting segments: Subscribers + Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageA.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushCustomPriceMulti] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        Path vid = resourcePath("src", "test", "resources", "Videos", "MediaVideoA.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        logger.info("[MediaPushCustomPriceMulti] Adding second video: {}", vid.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        logger.info("[MediaPushCustomPriceMulti] Message + custom price 10€ (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.openCustomPriceField();
        mp.fillCustomPriceEuro(10);
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushCustomPriceMulti] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with promotion (10% discount, unlimited validity) to Subscribers + Interested")
    @Test(priority = 18, description = "Media push flow with promotion 10% unlimited (multi-select)")
    public void creatorCanSendMediaPushWithPromotionMultiSelect() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushPromoMulti] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushPromoMulti] Choosing 'Media push' and selecting segments: Subscribers + Interested");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.selectInterestedSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageB.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushPromoMulti] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        Path vid2 = resourcePath("src", "test", "resources", "Videos", "MediaVideoB.mp4");
        if (!Files.exists(vid2)) throw new SkipException("Missing test asset: " + vid2);
        logger.info("[MediaPushPromoMulti] Adding second video: {}", vid2.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid2);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        logger.info("[MediaPushPromoMulti] Message + price 15€, promotion 10% unlimited");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();
        mp.enablePromotionToggle();
        mp.ensureDiscountVisible();

        Page p = this.page;
        p.getByRole(AriaRole.TEXTBOX).nth(1).click();
        p.getByRole(AriaRole.TEXTBOX).nth(1).fill("10");
        p.locator("label").filter(new Locator.FilterOptions().setHasText("Unlimited")).click();

        logger.info("[MediaPushPromoMulti] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push with Quick Files")
    @Test(priority = 19, description = "Media push flow with Quick Files")
    public void creatorCanSendMediaPushWithQuickFiles() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushQuickFiles] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushQuickFiles] Choosing 'Media push' and selecting segment: Subscribers");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.clickCreateNext();

        // Arrive on Add Push Media screen
        mp.ensureAddPushMediaScreen();

        logger.info("[MediaPushQuickFiles] Opening Add Media and choosing Quick Files");
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseQuickFiles();

        Page p = this.page;

        // Ensure we are on My albums screen and default filter is selected
        p.getByText("My albums").first().waitFor();
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Selected Photos & videos")).first().waitFor();

        // Click the Quick Files album whose name starts with "icon mixalbum"
        Locator albumBtn = p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions()
                .setName(java.util.regex.Pattern.compile("^icon\\s+mixalbum", java.util.regex.Pattern.CASE_INSENSITIVE)));

        long start = System.currentTimeMillis();
        long timeoutMs = 10_000;
        while (albumBtn.count() == 0 && System.currentTimeMillis() - start < timeoutMs) {
            try { p.waitForTimeout(250); } catch (Exception ignored) {}
        }
        if (albumBtn.count() == 0) {
            throw new SkipException("Quick Files album starting with 'icon mixalbum' not found; skipping test");
        }
        albumBtn.first().click();

        // Ensure we are inside the album ("Select media" title visible)
        p.getByText("Select media").first().waitFor();

        // Select all files in the album (6 items) by clicking the IMG role with name "select"
        p.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("select")).first().click();
        p.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("select")).nth(1).click();
        p.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("select")).nth(2).click();
        p.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("select")).nth(3).click();
        p.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("select")).nth(4).click();
        p.getByRole(AriaRole.IMG, new Page.GetByRoleOptions().setName("select")).nth(5).click();

        // Confirm the selection with the "Select (6)" button
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Select (6)")).click();

        // Click Next until all files are confirmed
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).first().click();
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).first().click();
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).first().click();
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).first().click();
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).first().click();
        p.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Next")).click();

        // Fill the message and choose the 30€ price
        p.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your message....")).click();
        p.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your message....")).fill("QA Test ");
        p.getByText("/name").click();
        p.locator("label").filter(new Locator.FilterOptions().setHasText("30€")).click();
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushQuickFiles] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }

    @Story("Creator sends media push to Subscribers + Former subscribers")
    @Test(priority = 21, description = "Media push flow via My Device: Subscribers + Former subscribers, image and video, price 15€, land on Messaging or limiter popup")
    public void creatorCanSendMediaPushToSubscribersAndFormer() {
        CreatorMediaPushPage mp = new CreatorMediaPushPage(page);

        logger.info("[MediaPushSubsFormer] Opening plus menu");
        logger.info("[MediaPushAllSegments] Opening plus menu");
        mp.openPlusMenu();
        mp.ensureOptionsPopup();

        logger.info("[MediaPushAllSegments] Choosing 'Media push' and selecting segments: Subscribers + Interested + Former subscribers");
        mp.chooseMediaPush();
        mp.ensureSegmentsScreen();
        mp.selectSubscribersSegment();
        mp.selectInterestedSegment();
        mp.selectFormerSubscriberSegment();
        mp.clickCreateNext();

        mp.ensureAddPushMediaScreen();

        Path img = resourcePath("src", "test", "resources", "Images", "MediaImageA.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        logger.info("[MediaPushAllSegments] Adding first image: {}", img.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(img);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        Path vid2 = resourcePath("src", "test", "resources", "Videos", "MediaVideoA.mp4");
        if (!Files.exists(vid2)) throw new SkipException("Missing test asset: " + vid2);
        logger.info("[MediaPushAllSegments] Adding second video: {}", vid2.getFileName());
        mp.clickAddMediaPlus();
        mp.ensureImportation();
        mp.chooseMyDevice();
        mp.uploadMediaFromDevice(vid2);
        mp.ensureBlurToggleEnabled();
        mp.clickNext();

        logger.info("[MediaPushAllSegments] Message + price 15€ (no promotion)");
        mp.ensureMessageTitle();
        mp.fillMessage("Test Message");
        mp.setPriceEuro(15);
        mp.ensureAddPromotionDisabled();

        logger.info("[MediaPushAllSegments] Proposing push media and asserting Messaging screen");
        mp.clickProposePushMedia();
        if (handleIUnderstandAfterProposeIfVisible()) {
            return;
        }
        mp.waitForUploadingMessageIfFast();
        mp.assertOnMessagingScreen();
    }
}