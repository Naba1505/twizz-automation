package testdata;

import utils.DateTimeUtils;
import utils.TestAssets;

import java.nio.file.Path;
import java.time.LocalDateTime;

/**
 * Test data for live event scheduling tests.
 * Provides configuration for different live event scenarios.
 */
public class LiveEventData {
    public final String access;
    public final Integer priceEuro;
    public final String customPrice;
    public final String chatWith;
    public final LocalDateTime scheduledTime;
    public final Path coverageImage;
    public final String description;

    private LiveEventData(String access, Integer priceEuro, String customPrice, String chatWith, 
                          LocalDateTime scheduledTime, Path coverageImage, String description) {
        this.access = access;
        this.priceEuro = priceEuro;
        this.customPrice = customPrice;
        this.chatWith = chatWith;
        this.scheduledTime = scheduledTime;
        this.coverageImage = coverageImage;
        this.description = description;
    }

    public static LiveEventData everyoneWithFixedPrice() {
        return new LiveEventData(
            "Everyone",
            15,
            null,
            "Everyone",
            DateTimeUtils.futureAtDaysHour(1, 3, 0),
            TestAssets.imageOrNull("Live A.jpg"),
            "Test - Everyone @ 15€"
        );
    }

    public static LiveEventData subscribersWithCustomPrice() {
        return new LiveEventData(
            "Subscribers",
            null,
            "5",
            "Subscribers",
            DateTimeUtils.futureAtDaysHour(1, 3, 0),
            TestAssets.imageOrNull("Live D.jpg"),
            "Test - Subscribers @ 5€"
        );
    }
}
