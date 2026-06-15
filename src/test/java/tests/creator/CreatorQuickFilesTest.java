package tests.creator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.creator.CreatorSettingsPage;
import testdata.QuickFilesData;
import utils.ConfigReader;

/**
 * Tests creator Quick Files album creation with different media types.
 * Uses QuickFilesData for test data generation.
 */
public class CreatorQuickFilesTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorQuickFilesTest.class);

    private void confirmAndNavigateBack(CreatorSettingsPage settings) {
        settings.confirmUploadAndStay();
        try {
            page.waitForTimeout(ConfigReader.getUiSettleTimeout());
        } catch (Exception e) {
            logger.debug("UI settle wait failed: {}", e.getMessage());
        }
        settings.navigateBackToProfile(2);
    }

    @Test(priority = 1, description = "Create Quick Files album with videos only")
    public void creatorCreatesQuickAlbum_VideosOnly() {
        QuickFilesData data = QuickFilesData.videosOnly();
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        
        String albumName = settings.createQuickAlbum(data.albumPrefix);
        logger.info("Created album: {}", albumName);
        Assert.assertNotNull(albumName, "Album name should not be null");
        Assert.assertTrue(albumName.startsWith(data.albumPrefix), "Album name should start with prefix");

        settings.addMediaFiles(data.files);
        confirmAndNavigateBack(settings);
    }

    @Test(priority = 2, description = "Create Quick Files album with images only")
    public void creatorCreatesQuickAlbum_ImagesOnly() {
        QuickFilesData data = QuickFilesData.imagesOnly();
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        
        String albumName = settings.createQuickAlbum(data.albumPrefix);
        logger.info("Created album: {}", albumName);
        Assert.assertNotNull(albumName, "Album name should not be null");
        Assert.assertTrue(albumName.startsWith(data.albumPrefix), "Album name should start with prefix");

        settings.addMediaFiles(data.files);
        confirmAndNavigateBack(settings);
    }

    @Test(priority = 3, description = "Create Quick Files album with both videos and images")
    public void creatorCreatesQuickAlbum_MixedMedia() {
        QuickFilesData data = QuickFilesData.mixedMedia();
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        
        String albumName = settings.createQuickAlbum(data.albumPrefix);
        logger.info("Created album: {}", albumName);
        Assert.assertNotNull(albumName, "Album name should not be null");
        Assert.assertTrue(albumName.startsWith(data.albumPrefix), "Album name should start with prefix");

        settings.addMediaFiles(data.files);
        confirmAndNavigateBack(settings);
    }

    @Test(priority = 4, description = "Create Quick Files album with audio only")
    public void creatorCreatesQuickAlbum_AudioOnly() {
        QuickFilesData data = QuickFilesData.audioFile();
        CreatorSettingsPage settings = new CreatorSettingsPage(page);

        String albumName = settings.createAudioAlbum(data.albumPrefix, data.files.get(0));
        logger.info("Created audio album: {}", albumName);
        Assert.assertNotNull(albumName, "Album name should not be null");
        Assert.assertTrue(albumName.startsWith(data.albumPrefix), "Album name should start with prefix");

        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Exception e) { logger.debug("UI settle wait failed: {}", e.getMessage()); }
        settings.navigateBackToProfile(2);
    }

    @Test(priority = 5, description = "Create Quick Files album with recorded audio")
    public void creatorCreatesQuickAlbum_AudioRecording() {
        QuickFilesData data = QuickFilesData.audioRecording();
        CreatorSettingsPage settings = new CreatorSettingsPage(page);

        String recordingNameBase = ConfigReader.getProperty("quickfiles.audio.record.namePrefix", "audioRecord");
        long durationMs;
        try {
            durationMs = Long.parseLong(ConfigReader.getProperty("quickfiles.audio.record.durationMs", "10000"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid duration config, using default: {}", e.getMessage());
            durationMs = 10000L;
        }

        String albumName = settings.createAudioAlbumByRecording(data.albumPrefix, recordingNameBase, durationMs);
        logger.info("Created audio recording album: {}", albumName);
        Assert.assertNotNull(albumName, "Album name should not be null");
        Assert.assertTrue(albumName.startsWith(data.albumPrefix), "Album name should start with prefix");

        try { page.waitForTimeout(ConfigReader.getUiSettleTimeout()); } catch (Exception e) { logger.debug("UI settle wait failed: {}", e.getMessage()); }
        settings.navigateBackToProfile(2);
    }
}
