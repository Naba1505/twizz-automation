package testdata;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Test data for Collection creation tests.
 * Provides media file paths and configuration for different collection scenarios.
 */
public class CollectionData {
    public final String titlePrefix;
    public final List<Path> mediaPaths;
    public final String description;
    public final int priceEuro;
    public final boolean blurEnabled;
    public final boolean useCustomPrice;

    private CollectionData(String titlePrefix, List<Path> mediaPaths, String description, 
                          int priceEuro, boolean blurEnabled, boolean useCustomPrice) {
        this.titlePrefix = titlePrefix;
        this.mediaPaths = mediaPaths;
        this.description = description;
        this.priceEuro = priceEuro;
        this.blurEnabled = blurEnabled;
        this.useCustomPrice = useCustomPrice;
    }

    public static CollectionData fromMyDevice() {
        return new CollectionData(
            "collection",
            List.of(
                Paths.get("src", "test", "resources", "Images", "CollectionImageA.jpg"),
                Paths.get("src", "test", "resources", "Videos", "CollectionVideoA.mp4")
            ),
            "X_Description",
            15,
            true,
            false
        );
    }

    public static CollectionData blurDisabled() {
        return new CollectionData(
            "collection_blur_off",
            List.of(
                Paths.get("src", "test", "resources", "Images", "CollectionImageA.jpg"),
                Paths.get("src", "test", "resources", "Videos", "CollectionVideoA.mp4")
            ),
            "X_Description",
            15,
            false,
            false
        );
    }

    public static CollectionData customPrice() {
        return new CollectionData(
            "collection_custom_price",
            List.of(
                Paths.get("src", "test", "resources", "Images", "CollectionImageA.jpg"),
                Paths.get("src", "test", "resources", "Videos", "CollectionVideoA.mp4")
            ),
            "X_Description",
            5,
            true,
            true
        );
    }

    public static CollectionData fromQuickFiles() {
        return new CollectionData(
            "CollectionQuickFile",
            List.of(), // No files needed for Quick Files flow
            "Description",
            15,
            true,
            false
        );
    }

    public Path getMediaPath(int index, String projectDir) {
        if (index < 0 || index >= mediaPaths.size()) {
            throw new IndexOutOfBoundsException("Media index out of range: " + index);
        }
        return Paths.get(projectDir).resolve(mediaPaths.get(index));
    }
}
