package tests;

import org.testng.annotations.Test;
import pages.CreatorSettingsPage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CreatorQuickFilesTest extends BaseCreatorTest {
    private static final int POST_CONFIRM_PAUSE_MS = 1000;

    @Test(priority = 1, description = "Create Quick Files album with videos only")
    public void creatorCreatesQuickAlbum_VideosOnly() {
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        String albumName = settings.createQuickAlbum("videoalbum_");
        System.out.println("Created album: " + albumName);

        List<Path> videos = List.of(
                Paths.get("src/test/resources/Videos/QuickVideoA.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoB.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoC.mp4")
        );

        settings.addMediaFiles(videos);
        settings.confirmUploadAndStay();
        // brief settle and navigate back to profile to keep uploads processing in background
        try { page.waitForTimeout(POST_CONFIRM_PAUSE_MS); } catch (Exception ignored) {}
        settings.navigateBackToProfile(2);
    }

    @Test(priority = 2, description = "Create Quick Files album with images only")
    public void creatorCreatesQuickAlbum_ImagesOnly() {
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        String albumName = settings.createQuickAlbum("imagealbum_");
        System.out.println("Created album: " + albumName);

        List<Path> images = List.of(
                Paths.get("src/test/resources/Images/QuickImageA.jpg"),
                Paths.get("src/test/resources/Images/QuickImageB.jpg"),
                Paths.get("src/test/resources/Images/QuickImageC.jpg")
        );

        settings.addMediaFiles(images);
        settings.confirmUploadAndStay();
        // brief settle and navigate back to profile to keep uploads processing in background
        try { page.waitForTimeout(POST_CONFIRM_PAUSE_MS); } catch (Exception ignored) {}
        settings.navigateBackToProfile(2);
    }

    @Test(priority = 3, description = "Create Quick Files album with both videos and images")
    public void creatorCreatesQuickAlbum_MixedMedia() {
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        String albumName = settings.createQuickAlbum("mixalbum_");
        System.out.println("Created album: " + albumName);

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
        try { page.waitForTimeout(POST_CONFIRM_PAUSE_MS); } catch (Exception ignored) {}
        settings.navigateBackToProfile(2);
    }
}
