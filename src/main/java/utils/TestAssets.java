package utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Centralized test asset paths for images, videos, and audio files.
 * All paths are resolved relative to project root.
 */
public final class TestAssets {
    private TestAssets() {}

    // ===== Base Directories =====
    private static final Path RESOURCES_DIR = Paths.get("src", "test", "resources");
    private static final Path IMAGES_DIR = RESOURCES_DIR.resolve("Images");
    private static final Path VIDEOS_DIR = RESOURCES_DIR.resolve("Videos");
    private static final Path AUDIOS_DIR = RESOURCES_DIR.resolve("Audios");

    // ===== Image Assets =====
    public static final String IDENTITY_IMAGE = "Identity.png";
    public static final String SELFIE_IMAGE = "Selfie.jpg";
    public static final String PROFILE_IMAGE_A = "ProfileImageA.jpg";
    public static final String AUTO_MESSAGE_IMAGE = "AutoMessageImage.png";
    public static final String SCRIPT_IMAGE_A = "ScriptImageA.png";
    public static final String SCRIPT_IMAGE_B = "ScriptImageB.png";

    // ===== Video Assets =====
    public static final String SCRIPT_VIDEO_A = "ScriptVideoA.mp4";
    public static final String SCRIPT_VIDEO_B = "ScriptVideoB.mp4";
    public static final String HIDE_AND_SEEK = "HideAndSeek.mp4";

    // ===== Audio Assets =====
    public static final String SCRIPT_AUDIO_A = "ScriptAudioA.mp3";
    public static final String SCRIPT_AUDIO_B = "ScriptAudioB.mp3";

    // ===== Path Resolvers =====

    /**
     * Get absolute path to an image file, or null if not found.
     */
    public static Path imageOrNull(String fileName) {
        Path p = IMAGES_DIR.resolve(fileName);
        return Files.exists(p) ? p : null;
    }

    /**
     * Get absolute path to an image file. Throws if not found.
     */
    public static String imagePath(String fileName) {
        Path p = IMAGES_DIR.resolve(fileName);
        if (!Files.exists(p)) {
            throw new RuntimeException("Image not found: " + p.toAbsolutePath());
        }
        return p.toAbsolutePath().toString();
    }

    /**
     * Get absolute path to a video file, or null if not found.
     */
    public static Path videoOrNull(String fileName) {
        Path p = VIDEOS_DIR.resolve(fileName);
        return Files.exists(p) ? p : null;
    }

    /**
     * Get absolute path to a video file. Throws if not found.
     */
    public static String videoPath(String fileName) {
        Path p = VIDEOS_DIR.resolve(fileName);
        if (!Files.exists(p)) {
            throw new RuntimeException("Video not found: " + p.toAbsolutePath());
        }
        return p.toAbsolutePath().toString();
    }

    /**
     * Get absolute path to an audio file, or null if not found.
     */
    public static Path audioOrNull(String fileName) {
        Path p = AUDIOS_DIR.resolve(fileName);
        return Files.exists(p) ? p : null;
    }

    /**
     * Get absolute path to an audio file. Throws if not found.
     */
    public static String audioPath(String fileName) {
        Path p = AUDIOS_DIR.resolve(fileName);
        if (!Files.exists(p)) {
            throw new RuntimeException("Audio not found: " + p.toAbsolutePath());
        }
        return p.toAbsolutePath().toString();
    }

    // ===== Convenience Methods =====

    public static String identityImagePath() {
        return imagePath(IDENTITY_IMAGE);
    }

    public static String selfieImagePath() {
        return imagePath(SELFIE_IMAGE);
    }

    public static String profileImagePath() {
        return imagePath(PROFILE_IMAGE_A);
    }
}
