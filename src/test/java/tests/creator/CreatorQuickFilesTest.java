package tests.creator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.creator.CreatorSettingsPage;
import utils.ConfigReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CreatorQuickFilesTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorQuickFilesTest.class);
    private static final int POST_CONFIRM_PAUSE_MS = 1000;

    @Test(priority = 1, description = "Create Quick Files album with videos only")
    public void creatorCreatesQuickAlbum_VideosOnly() {
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        String albumName = settings.createQuickAlbum("videoalbum_");
        logger.info("Created album: {}", albumName);

        List<Path> videos = List.of(
                Paths.get("src/test/resources/Videos/QuickVideoA.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoB.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoC.mp4")
        );

        settings.addMediaFiles(videos);
        settings.confirmUploadAndStay();
        // brief settle and navigate back to profile to keep uploads processing in background
        try {
            page.waitForTimeout(POST_CONFIRM_PAUSE_MS);
        } catch (Exception ignored) {
        }
        settings.navigateBackToProfile(2);
    }

    @Test(priority = 2, description = "Create Quick Files album with images only")
    public void creatorCreatesQuickAlbum_ImagesOnly() {
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        String albumName = settings.createQuickAlbum("imagealbum_");
        logger.info("Created album: {}", albumName);

        List<Path> images = List.of(
                Paths.get("src/test/resources/Images/QuickImageA.jpg"),
                Paths.get("src/test/resources/Images/QuickImageB.jpg"),
                Paths.get("src/test/resources/Images/QuickImageC.jpg")
        );

        settings.addMediaFiles(images);
        settings.confirmUploadAndStay();
        // brief settle and navigate back to profile to keep uploads processing in background
        try {
            page.waitForTimeout(POST_CONFIRM_PAUSE_MS);
        } catch (Exception ignored) {
        }
        settings.navigateBackToProfile(2);
    }

    @Test(priority = 3, description = "Create Quick Files album with both videos and images")
    public void creatorCreatesQuickAlbum_MixedMedia() {
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        String albumName = settings.createQuickAlbum("mixalbum_");
        logger.info("Created album: {}", albumName);

        List<Path> mixed = List.of(
                Paths.get("src/test/resources/Videos/QuickVideoA.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoB.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoC.mp4"),
                Paths.get("src/test/resources/Images/QuickImageA.jpg"),
                Paths.get("src/test/resources/Images/QuickImageB.jpg"),
                Paths.get("src/test/resources/Images/QuickImageC.jpg")
        );

        settings.addMediaFiles(mixed);
        settings.confirmUploadAndStay();
        // brief settle and navigate back to profile to keep uploads processing in background
        try {
            page.waitForTimeout(POST_CONFIRM_PAUSE_MS);
        } catch (Exception ignored) {
        }
        settings.navigateBackToProfile(2);
    }

    @Test(priority = 4, description = "Create Quick Files album with audio only")
    public void creatorCreatesQuickAlbum_AudioOnly() {
        CreatorSettingsPage settings = new CreatorSettingsPage(page);

        // Config-driven prefix and audio file path with safe defaults
        String prefix = ConfigReader.getProperty("quickfiles.audio.prefix", "audioalbum_");
        String audioPathProp = ConfigReader.getProperty("quickfiles.audio.path", "src/test/resources/Audios/A5.mp3");
        Path audioPath = Paths.get(audioPathProp);

        String albumName = settings.createAudioAlbum(prefix, audioPath);
        logger.info("Created audio album: {}", albumName);

        // brief settle and navigate back to profile to keep processing in background
        try {
            page.waitForTimeout(POST_CONFIRM_PAUSE_MS);
        } catch (Exception ignored) {
        }
        settings.navigateBackToProfile(2);
    }

    @Test(priority = 5, description = "Create Quick Files album with recorded audio")
    public void creatorCreatesQuickAlbum_AudioRecording() {
        CreatorSettingsPage settings = new CreatorSettingsPage(page);

        String albumPrefix = ConfigReader.getProperty("quickfiles.audio.record.albumPrefix", "audioalbum_");
        String recordingNameBase = ConfigReader.getProperty("quickfiles.audio.record.namePrefix", "audioRecord");
        long durationMs;
        try {
            durationMs = Long.parseLong(ConfigReader.getProperty("quickfiles.audio.record.durationMs", "10000"));
        } catch (NumberFormatException e) {
            durationMs = 10000L;
        }

        String albumName = settings.createAudioAlbumByRecording(albumPrefix, recordingNameBase, durationMs);
        logger.info("Created audio recording album: {}", albumName);

        try {
            page.waitForTimeout(POST_CONFIRM_PAUSE_MS);
        } catch (Exception ignored) {
        }
        settings.navigateBackToProfile(2);
    }
}
