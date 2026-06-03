package testdata;

import utils.TestAssets;
import java.nio.file.Path;

/**
 * Test data for publication tests.
 * Provides media files and captions for different publication scenarios.
 */
public class PublicationData {
    public final Path mediaPath;
    public final String caption;
    public final boolean blurEnabled;
    public final String mediaType;

    private PublicationData(Path mediaPath, String caption, boolean blurEnabled, String mediaType) {
        this.mediaPath = mediaPath;
        this.caption = caption;
        this.blurEnabled = blurEnabled;
        this.mediaType = mediaType;
    }

    public static PublicationData videoBlurred() {
        return new PublicationData(
            TestAssets.videoOrNull(TestAssets.TOAST_VIDEO),
            "video with blurred media enabled",
            true,
            "video"
        );
    }

    public static PublicationData videoUnblurred() {
        return new PublicationData(
            TestAssets.videoOrNull(TestAssets.HIDE_AND_SEEK),
            "video with blurred media disabled",
            false,
            "video"
        );
    }

    public static PublicationData imageBlurred() {
        return new PublicationData(
            TestAssets.imageOrNull(TestAssets.HOME_IMAGE),
            "image with blurred media enabled",
            true,
            "image"
        );
    }

    public static PublicationData imageUnblurred() {
        return new PublicationData(
            TestAssets.imageOrNull(TestAssets.LANDSCAPE_IMAGE),
            "image with blurred media disabled",
            false,
            "image"
        );
    }
}
