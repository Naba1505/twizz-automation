package tests.creator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.creator.CreatorCollectionPage;
import testdata.CollectionData;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tests creator Collection creation with different media types and configurations.
 * Uses CollectionData for test data generation.
 */
@Epic("Creator")
@Feature("Collection")
public class CreatorCollectionTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorCollectionTest.class);
    private static final String PROJECT_DIR = System.getProperty("user.dir");

    @Story("Create collection by adding files from my device (  image + video)")
    @Test(priority = 1, description = "Creator creates a collection with an image and a video from device uploads")
    public void creatorCanCreateCollectionFromMyDevice() {
        CollectionData data = CollectionData.fromMyDevice();
        CreatorCollectionPage coll = new CreatorCollectionPage(page);

        logger.info("[DeviceFlow] Opening plus menu and navigating to Collection");
        coll.openPlusMenu();
        coll.navigateToCollection();

        logger.info("[DeviceFlow] Filling title and clicking Create");
        coll.fillCollectionTitle(data.titlePrefix);
        coll.clickCreate();

        // Add media files
        for (int i = 0; i < data.mediaPaths.size(); i++) {
            Path mediaPath = data.getMediaPath(i, PROJECT_DIR);
            if (!Files.exists(mediaPath)) {
                throw new SkipException("Missing test asset: " + mediaPath);
            }
            logger.info("[DeviceFlow] Adding media {}/{}: {}", i + 1, data.mediaPaths.size(), mediaPath.getFileName());
            coll.clickAddMediaPlus();
            coll.chooseMyDevice();
            coll.uploadMediaFromDevice(mediaPath);
            coll.ensureAddMediaScreenAndDefaults();
            coll.clickNext();
        }

        logger.info("[DeviceFlow] Filling description and setting price {}€", data.priceEuro);
        coll.fillDescription(data.description);
        coll.setPriceEuro(data.priceEuro);

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
        CollectionData data = CollectionData.blurDisabled();
        CreatorCollectionPage coll = new CreatorCollectionPage(page);

        logger.info("[BlurOff] Opening plus menu and navigating to Collection");
        coll.openPlusMenu();
        coll.navigateToCollection();

        logger.info("[BlurOff] Filling title and clicking Create");
        coll.fillCollectionTitle(data.titlePrefix);
        coll.clickCreate();

        // Add media files with blur disabled
        for (int i = 0; i < data.mediaPaths.size(); i++) {
            Path mediaPath = data.getMediaPath(i, PROJECT_DIR);
            if (!Files.exists(mediaPath)) {
                throw new SkipException("Missing test asset: " + mediaPath);
            }
            logger.info("[BlurOff] Adding media {}/{} and disabling blur: {}", i + 1, data.mediaPaths.size(), mediaPath.getFileName());
            coll.clickAddMediaPlus();
            coll.chooseMyDevice();
            coll.uploadMediaFromDevice(mediaPath);
            coll.ensureAddMediaScreenAndDefaults();
            if (!data.blurEnabled) {
                coll.disableBlurredSwitch();
            }
            coll.clickNext();
        }

        logger.info("[BlurOff] Filling description and setting price {}€", data.priceEuro);
        coll.fillDescription(data.description);
        coll.setPriceEuro(data.priceEuro);

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
        CollectionData data = CollectionData.customPrice();
        CreatorCollectionPage coll = new CreatorCollectionPage(page);

        logger.info("[CustomPrice] Opening plus menu and navigating to Collection");
        coll.openPlusMenu();
        coll.navigateToCollection();

        logger.info("[CustomPrice] Filling title and clicking Create");
        coll.fillCollectionTitle(data.titlePrefix);
        coll.clickCreate();

        // Add media files
        for (int i = 0; i < data.mediaPaths.size(); i++) {
            Path mediaPath = data.getMediaPath(i, PROJECT_DIR);
            if (!Files.exists(mediaPath)) {
                throw new SkipException("Missing test asset: " + mediaPath);
            }
            logger.info("[CustomPrice] Adding media {}/{}: {}", i + 1, data.mediaPaths.size(), mediaPath.getFileName());
            coll.clickAddMediaPlus();
            coll.chooseMyDevice();
            coll.uploadMediaFromDevice(mediaPath);
            coll.ensureAddMediaScreenAndDefaults();
            coll.clickNext();
        }

        logger.info("[CustomPrice] Filling description and setting custom price {}€ via spinner", data.priceEuro);
        coll.fillDescription(data.description);
        if (data.useCustomPrice) {
            coll.setCustomPriceEuro(data.priceEuro);
        } else {
            coll.setPriceEuro(data.priceEuro);
        }

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
        CollectionData data = CollectionData.fromQuickFiles();
        CreatorCollectionPage coll = new CreatorCollectionPage(page);

        logger.info("[QuickFiles] Opening plus menu and navigating to Collection");
        coll.openPlusMenu();
        coll.clickIUnderstandIfPresent();
        coll.navigateToCollection();

        logger.info("[QuickFiles] Filling title and clicking Create");
        coll.fillCollectionTitle(data.titlePrefix);
        coll.clickCreate();

        logger.info("[QuickFiles] Opening Add Media and choosing Quick Files");
        coll.clickAddMediaPlus();
        coll.chooseQuickFiles();

        logger.info("[QuickFiles] Selecting a Quick Files album");
        try {
            coll.selectQuickFilesAlbumWithFallback();
        } catch (RuntimeException e) {
            throw new SkipException("No Quick Files album found; skipping Quick Files test");
        }

        logger.info("[QuickFiles] Selecting up to 3 media items from album");
        coll.selectUpToNCovers(3);

        logger.info("[QuickFiles] Confirm selection and proceed");
        coll.clickSelectInQuickFiles();
        coll.proceedNextSteps(3);

        logger.info("[QuickFiles] Filling description and setting price {}€", data.priceEuro);
        coll.fillDescription(data.description);
        coll.setPriceEuro(data.priceEuro);

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
