package testdata;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Test data for Quick Files album creation tests.
 * Provides media file paths for different album scenarios.
 */
public class QuickFilesData {
    public final String albumPrefix;
    public final List<Path> files;
    public final String type;

    private QuickFilesData(String albumPrefix, List<Path> files, String type) {
        this.albumPrefix = albumPrefix;
        this.files = files;
        this.type = type;
    }

    public static QuickFilesData videosOnly() {
        return new QuickFilesData(
            "videoalbum_",
            List.of(
                Paths.get("src/test/resources/Videos/QuickVideoA.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoB.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoC.mp4")
            ),
            "videos"
        );
    }

    public static QuickFilesData imagesOnly() {
        return new QuickFilesData(
            "imagealbum_",
            List.of(
                Paths.get("src/test/resources/Images/QuickImageA.jpg"),
                Paths.get("src/test/resources/Images/QuickImageB.jpg"),
                Paths.get("src/test/resources/Images/QuickImageC.jpg")
            ),
            "images"
        );
    }

    public static QuickFilesData mixedMedia() {
        return new QuickFilesData(
            "mixalbum_",
            List.of(
                Paths.get("src/test/resources/Videos/QuickVideoA.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoB.mp4"),
                Paths.get("src/test/resources/Videos/QuickVideoC.mp4"),
                Paths.get("src/test/resources/Images/QuickImageA.jpg"),
                Paths.get("src/test/resources/Images/QuickImageB.jpg"),
                Paths.get("src/test/resources/Images/QuickImageC.jpg")
            ),
            "mixed"
        );
    }

    public static QuickFilesData audioFile() {
        String audioPathProp = utils.ConfigReader.getProperty("quickfiles.audio.path", "src/test/resources/Audios/A5.mp3");
        return new QuickFilesData(
            utils.ConfigReader.getProperty("quickfiles.audio.prefix", "audioalbum_"),
            List.of(Paths.get(audioPathProp)),
            "audio"
        );
    }

    public static QuickFilesData audioRecording() {
        return new QuickFilesData(
            utils.ConfigReader.getProperty("quickfiles.audio.record.albumPrefix", "audioalbum_"),
            List.of(), // No files for recording
            "audio-recording"
        );
    }
}
