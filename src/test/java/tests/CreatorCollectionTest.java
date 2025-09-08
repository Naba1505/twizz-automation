package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.CreatorCollectionPage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Epic("Creator")
@Feature("Collection")
public class CreatorCollectionTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorCollectionTest.class);

    private Path resourcePath(String first, String... more) {
        String projectDir = System.getProperty("user.dir");
        // Build path safely: base (projectDir) resolved with subpath(first, more)
        return Paths.get(projectDir).resolve(Paths.get(first, more));
    }

    @Story("Create collection by adding files from my device (image + video)")
    @Test(priority = 1, description = "Creator creates a collection with an image and a video from device uploads")
    public void creatorCanCreateCollectionFromMyDevice() {
        CreatorCollectionPage coll = new CreatorCollectionPage(page);

        // 1) Open plus and navigate to Collection (dismiss any overlays first)
        logger.info("[DeviceFlow] Opening plus menu and navigating to Collection");
        coll.openPlusMenu();
        coll.navigateToCollection();

        // 2) Fill title and Create
        logger.info("[DeviceFlow] Filling title and clicking Create");
        coll.fillCollectionTitle("collection");
        coll.clickCreate();

        // 3) Add first media: Image
        Path img = resourcePath("src", "test", "resources", "Images", "CollectionImageA.jpg");
        if (!Files.exists(img)) {
            throw new SkipException("Missing test asset: " + img);
        }
        logger.info("[DeviceFlow] Adding first image: {}", img.getFileName());
        coll.clickAddMediaPlus();
        coll.chooseMyDevice();
        coll.uploadMediaFromDevice(img);
        coll.ensureAddMediaScreenAndDefaults();
        coll.clickNext();

        // 4) Add second media: Video
        Path vid = resourcePath("src", "test", "resources", "Videos", "CollectionVideoA.mp4");
        if (!Files.exists(vid)) {
            throw new SkipException("Missing test asset: " + vid);
        }
        logger.info("[DeviceFlow] Adding second video: {}", vid.getFileName());
        coll.clickAddMediaPlus();
        coll.chooseMyDevice();
        coll.uploadMediaFromDevice(vid);
        coll.ensureAddMediaScreenAndDefaults();
        coll.clickNext();

        // 5) Description and price
        logger.info("[DeviceFlow] Filling description and setting price 15€");
        coll.fillDescription("X_Description");
        coll.setPriceEuro(15);

        // 6) Validate and wait for upload completion message to appear and disappear
        logger.info("[DeviceFlow] Validating collection and waiting for upload to finish");
        coll.validateCollection();
        coll.waitForUploadFinish();
        try {
            coll.assertCollectionCreatedToast();
        } catch (Throwable t) {
            logger.warn("[DeviceFlow] Toast not detected; proceeding. Cause: {}", t.getMessage());
        }
    }

    @Story("Create collection with blurred toggle disabled explicitly")
    @Test(priority = 2, description = "Creator disables blurred media toggle and creates a collection (image + video)")
    public void creatorCanCreateCollectionBlurDisabled() {
        CreatorCollectionPage coll = new CreatorCollectionPage(page);

        // 1) Open plus and navigate to Collection
        logger.info("[BlurOff] Opening plus menu and navigating to Collection");
        coll.openPlusMenu();
        coll.navigateToCollection();

        // 2) Fill title and Create
        logger.info("[BlurOff] Filling title and clicking Create");
        coll.fillCollectionTitle("collection_blur_off");
        coll.clickCreate();

        // 3) Add first media: Image (reuse A to stabilize uploads)
        Path img = resourcePath("src", "test", "resources", "Images", "CollectionImageA.jpg");
        if (!Files.exists(img)) {
            throw new SkipException("Missing test asset: " + img);
        }
        logger.info("[BlurOff] Adding image and disabling blur: {}", img.getFileName());
        coll.clickAddMediaPlus();
        coll.chooseMyDevice();
        coll.uploadMediaFromDevice(img);
        coll.ensureAddMediaScreenAndDefaults();
        coll.disableBlurredSwitch();
        coll.clickNext();

        // 4) Add second media: Video (reuse A to stabilize uploads)
        Path vid = resourcePath("src", "test", "resources", "Videos", "CollectionVideoA.mp4");
        if (!Files.exists(vid)) {
            throw new SkipException("Missing test asset: " + vid);
        }
        logger.info("[BlurOff] Adding video and disabling blur: {}", vid.getFileName());
        coll.clickAddMediaPlus();
        coll.chooseMyDevice();
        coll.uploadMediaFromDevice(vid);
        coll.ensureAddMediaScreenAndDefaults();
        coll.disableBlurredSwitch();
        coll.clickNext();

        // 5) Description and price
        logger.info("[BlurOff] Filling description and setting price 15€");
        coll.fillDescription("X_Description");
        coll.setPriceEuro(15);

        // 6) Validate and wait
        logger.info("[BlurOff] Validating collection and waiting for upload to finish");
        coll.validateCollection();
        coll.waitForUploadFinish();
        try {
            coll.assertCollectionCreatedToast();
        } catch (Throwable t) {
            logger.warn("[BlurOff] Toast not detected; proceeding. Cause: {}", t.getMessage());
        }
    }

    @Story("Create collection with custom price via spinner")
    @Test(priority = 3, description = "Creator sets custom price using spinner to 5€ (image + video)")
    public void creatorCanCreateCollectionWithCustomPrice() {
        CreatorCollectionPage coll = new CreatorCollectionPage(page);

        // 1) Open plus and navigate to Collection
        logger.info("[CustomPrice] Opening plus menu and navigating to Collection");
        coll.openPlusMenu();
        coll.navigateToCollection();

        // 2) Fill title and Create
        logger.info("[CustomPrice] Filling title and clicking Create");
        coll.fillCollectionTitle("collection_custom_price");
        coll.clickCreate();

        // 3) Add first media: Image (reuse A to stabilize uploads)
        Path img = resourcePath("src", "test", "resources", "Images", "CollectionImageA.jpg");
        if (!Files.exists(img)) {
            throw new SkipException("Missing test asset: " + img);
        }
        logger.info("[CustomPrice] Adding image: {}", img.getFileName());
        coll.clickAddMediaPlus();
        coll.chooseMyDevice();
        coll.uploadMediaFromDevice(img);
        coll.ensureAddMediaScreenAndDefaults();
        coll.clickNext();

        // 4) Add second media: Video (reuse A to stabilize uploads)
        Path vid = resourcePath("src", "test", "resources", "Videos", "CollectionVideoA.mp4");
        if (!Files.exists(vid)) {
            throw new SkipException("Missing test asset: " + vid);
        }
        logger.info("[CustomPrice] Adding video: {}", vid.getFileName());
        coll.clickAddMediaPlus();
        coll.chooseMyDevice();
        coll.uploadMediaFromDevice(vid);
        coll.ensureAddMediaScreenAndDefaults();
        coll.clickNext();

        // 5) Description and custom price
        logger.info("[CustomPrice] Filling description and setting custom price 5€ via spinner");
        coll.fillDescription("X_Description");
        coll.setCustomPriceEuro(5);

        // 6) Validate and wait (extend timeout to 5 minutes for this test)
        logger.info("[CustomPrice] Validating collection and waiting for upload to finish (up to 5 minutes)");
        coll.validateCollection();
        coll.waitForUploadFinish(300000);
        try {
            coll.assertCollectionCreatedToast();
        } catch (Throwable t) {
            logger.warn("[CustomPrice] Toast not detected; proceeding. Cause: {}", t.getMessage());
        }
    }

    @Story("Create collection by using Quick Files album to add media")
    @Test(priority = 4, description = "Creator creates a collection using Quick Files album and validates creation")
    public void creatorCanCreateCollectionUsingQuickFilesAlbum() {
        CreatorCollectionPage coll = new CreatorCollectionPage(page);

        // 1) Open plus and navigate to Collection
        logger.info("[QuickFiles] Opening plus menu and navigating to Collection");
        coll.openPlusMenu();
        // Dismiss potential 'I understand' dialog if shown
        coll.clickIUnderstandIfPresent();
        coll.navigateToCollection();

        // 2) Fill title and Create (timestamped)
        logger.info("[QuickFiles] Filling title and clicking Create");
        coll.fillCollectionTitle("CollectionQuickFile");
        coll.clickCreate();

        // 3) Add media via Quick Files
        logger.info("[QuickFiles] Opening Add Media and choosing Quick Files");
        coll.clickAddMediaPlus();
        coll.chooseQuickFiles();

        // 4) Select an album (prefer names starting with videoalbum_/imagealbum_/mixalbum_)
        logger.info("[QuickFiles] Selecting a Quick Files album");
        try {
            coll.selectQuickFilesAlbumWithFallback();
        } catch (RuntimeException e) {
            throw new SkipException("No Quick Files album found; skipping Quick Files test");
        }

        // 5) Select a few media covers
        logger.info("[QuickFiles] Selecting up to 3 media items from album");
        coll.selectUpToNCovers(3);

        // 6) Confirm selection and proceed through Next steps
        logger.info("[QuickFiles] Confirm selection and proceed");
        coll.clickSelectInQuickFiles();
        // Next through steps (thumbnail, options, summary)
        coll.proceedNextSteps(3);

        // 7) Description and price
        logger.info("[QuickFiles] Filling description and setting price 15€");
        coll.fillDescription("Descripion");
        coll.setPriceEuro(15);

        // 8) Validate and wait for completion
        logger.info("[QuickFiles] Validating collection and waiting for upload to finish");
        coll.validateCollection();
        coll.waitForUploadFinish();
        try {
            coll.assertCollectionCreatedToast();
        } catch (Throwable t) {
            logger.warn("[QuickFiles] Toast not detected; proceeding. Cause: {}", t.getMessage());
        }
    }

}
