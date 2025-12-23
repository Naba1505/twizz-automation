package tests.creator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.creator.CreatorUnlockLinksPage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Epic("Creator")
@Feature("Unlock Links")
public class CreatorUnlockLinksTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorUnlockLinksTest.class);

    private Path resourcePath(String first, String... more) {
        String projectDir = System.getProperty("user.dir");
        return Paths.get(projectDir).resolve(Paths.get(first, more));
    }

    private String ts() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
    }

    @Story("Creator generates image unlock link via My Device upload")
    @Test(priority = 1, description = "Unlock Links: upload image, set 5€, generate link, name it, confirm, and close")
    public void creatorGeneratesImageUnlockLink() {
        CreatorUnlockLinksPage ul = new CreatorUnlockLinksPage(page);

        logger.info("[UnlockImage] Opening plus menu and navigating to Unlock");
        ul.openPlusMenu();
        ul.ensureOptionsPopup();
        ul.clickIUnderstandIfPresent();
        ul.chooseUnlock();
        ul.ensureUnlockScreen();

        logger.info("[UnlockImage] Adding media via My Device");
        ul.clickAddMediaPlus();
        ul.ensureImportation();
        ul.chooseMyDevice();

        Path img = resourcePath("src", "test", "resources", "Images", "DecryptImage.jpg");
        if (!Files.exists(img)) throw new SkipException("Missing test asset: " + img);
        ul.uploadMediaFromDevice(img);

        logger.info("[UnlockImage] Setting price to 5€ and verifying earnings message");
        ul.openPriceField();
        ul.fillPriceEuro(5);
        ul.ensureEarningsMessage("You will receive 4.50 €");

        logger.info("[UnlockImage] Generating link and naming it");
        ul.clickGenerateLink();
        ul.ensureGiveYourUnlockName();
        ul.fillUnlockName("UnlockLink_Image_" + ts());
        ul.clickCreate();

        logger.info("[UnlockImage] Verifying success and closing dialogs");
        ul.ensureEverythingIsReady();
        ul.closeWithCross();
        ul.ensureImportantPopup();
        ul.clickCEstCompris();
    }

    @Story("Creator generates video unlock link via My Device upload")
    @Test(priority = 2, description = "Unlock Links: upload video, set 5€, generate link, name it, confirm, and close")
    public void creatorGeneratesVideoUnlockLink() {
        CreatorUnlockLinksPage ul = new CreatorUnlockLinksPage(page);

        logger.info("[UnlockVideo] Opening plus menu and navigating to Unlock");
        ul.openPlusMenu();
        ul.ensureOptionsPopup();
        ul.clickIUnderstandIfPresent();
        ul.chooseUnlock();
        ul.ensureUnlockScreen();

        logger.info("[UnlockVideo] Adding media via My Device");
        ul.clickAddMediaPlus();
        ul.ensureImportation();
        ul.chooseMyDevice();

        Path vid = resourcePath("src", "test", "resources", "Videos", "DecryptVideo.mp4");
        if (!Files.exists(vid)) throw new SkipException("Missing test asset: " + vid);
        ul.uploadMediaFromDevice(vid);

        logger.info("[UnlockVideo] Setting price to 5€ and verifying earnings message");
        ul.openPriceField();
        ul.fillPriceEuro(5);
        ul.ensureEarningsMessage("You will receive 4.50 €");

        logger.info("[UnlockVideo] Generating link and naming it");
        ul.clickGenerateLink();
        ul.ensureGiveYourUnlockName();
        ul.fillUnlockName("UnlockLink_Video_" + ts());
        ul.clickCreate();

        logger.info("[UnlockVideo] Verifying success and closing dialogs");
        ul.ensureEverythingIsReady();
        ul.closeWithCross();
        ul.ensureImportantPopup();
        ul.clickCEstCompris();
    }
}
