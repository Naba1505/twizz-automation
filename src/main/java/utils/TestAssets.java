package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestAssets {
    private TestAssets() {}

    public static Path imageOrNull(String fileName) {
        // Default path where repo keeps images
        Path p = Paths.get("src", "test", "resources", "Images", fileName);
        if (Files.exists(p)) {
            return p;
        }
        return null;
    }
}
