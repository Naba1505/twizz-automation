package testdata;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Test data class for Media Push creation tests.
 * Provides centralized management of media file paths, pricing, and other test configurations.
 */
public class MediaPushData {
    
    // Media file paths for device uploads
    public static final Path MEDIA_IMAGE_A = Paths.get("src", "test", "resources", "Images", "MediaImageA.jpg");
    public static final Path MEDIA_VIDEO_A = Paths.get("src", "test", "resources", "Videos", "MediaVideoA.mp4");
    
    // Media file paths for Quick Files uploads
    public static final Path QUICK_IMAGE_A = Paths.get("src", "test", "resources", "Images", "QuickImageA.jpg");
    public static final Path QUICK_IMAGE_B = Paths.get("src", "test", "resources", "Images", "QuickImageB.jpg");
    public static final Path QUICK_VIDEO_A = Paths.get("src", "test", "resources", "Videos", "QuickVideoA.mp4");
    public static final Path QUICK_VIDEO_B = Paths.get("src", "test", "resources", "Videos", "QuickVideoB.mp4");
    
    // Pricing configurations
    public static final int STANDARD_PRICE_EURO = 15;
    public static final int CUSTOM_PRICE_EURO = 10;
    public static final int FREE_PRICE = 0;
    
    // Promotion configurations
    public static final int PROMO_DISCOUNT_PERCENT = 10;
    public static final int PROMO_DISCOUNT_EURO = 5;
    public static final int PROMO_VALIDITY_7_DAYS = 7;
    
    // Test messages
    public static final String TEST_MESSAGE = "Test Message";
    
    // Segment configurations
    public static final String[] SUBSCRIBERS_ONLY = {"Subscribers"};
    public static final String[] INTERESTED_ONLY = {"Interested"};
    public static final String[] SUBSCRIBERS_AND_INTERESTED = {"Subscribers", "Interested"};
    
    // Blur settings
    public static final boolean BLUR_ENABLED = true;
    public static final boolean BLUR_DISABLED = false;
    
    // Media combinations for different test scenarios
    public static class MediaCombination {
        public final Path image;
        public final Path video;
        public final String description;
        
        public MediaCombination(Path image, Path video, String description) {
            this.image = image;
            this.video = video;
            this.description = description;
        }
    }
    
    // Predefined media combinations
    public static final MediaCombination DEVICE_MEDIA = new MediaCombination(
        MEDIA_IMAGE_A, MEDIA_VIDEO_A, "Device uploaded media"
    );
    
    public static final MediaCombination QUICK_FILES_MEDIA = new MediaCombination(
        QUICK_IMAGE_A, QUICK_VIDEO_A, "Quick Files media"
    );
    
    public static final MediaCombination QUICK_FILES_MEDIA_B = new MediaCombination(
        QUICK_IMAGE_B, QUICK_VIDEO_B, "Quick Files media B"
    );
    
    // Pricing configurations
    public static class PricingConfig {
        public final int priceEuro;
        public final boolean hasPromotion;
        public final int promoDiscountPercent;
        public final int promoDiscountEuro;
        public final int promoValidityDays;
        public final String description;
        
        public PricingConfig(int priceEuro, boolean hasPromotion, int promoDiscountPercent, 
                           int promoDiscountEuro, int promoValidityDays, String description) {
            this.priceEuro = priceEuro;
            this.hasPromotion = hasPromotion;
            this.promoDiscountPercent = promoDiscountPercent;
            this.promoDiscountEuro = promoDiscountEuro;
            this.promoValidityDays = promoValidityDays;
            this.description = description;
        }
    }
    
    // Predefined pricing configurations
    public static final PricingConfig STANDARD_PRICING = new PricingConfig(
        STANDARD_PRICE_EURO, false, 0, 0, 0, "Standard pricing (15€)"
    );
    
    public static final PricingConfig CUSTOM_PRICING = new PricingConfig(
        CUSTOM_PRICE_EURO, false, 0, 0, 0, "Custom pricing (10€)"
    );
    
    public static final PricingConfig FREE_PRICING = new PricingConfig(
        FREE_PRICE, false, 0, 0, 0, "Free pricing"
    );
    
    public static final PricingConfig PROMO_PERCENT_PRICING = new PricingConfig(
        STANDARD_PRICE_EURO, true, PROMO_DISCOUNT_PERCENT, 0, 0, "Standard with 10% discount"
    );
    
    public static final PricingConfig PROMO_EURO_PRICING = new PricingConfig(
        STANDARD_PRICE_EURO, true, 0, PROMO_DISCOUNT_EURO, PROMO_VALIDITY_7_DAYS, "Standard with 5€ discount, 7 days"
    );
    
    public static final PricingConfig PROMO_UNLIMITED_PRICING = new PricingConfig(
        STANDARD_PRICE_EURO, true, PROMO_DISCOUNT_PERCENT, 0, -1, "Standard with 10% discount, unlimited"
    );
    
    // Test scenario configurations
    public static class TestScenario {
        public final String name;
        public final MediaCombination media;
        public final String[] segments;
        public final PricingConfig pricing;
        public final boolean blurEnabled;
        public final String description;
        
        public TestScenario(String name, MediaCombination media, String[] segments, 
                          PricingConfig pricing, boolean blurEnabled, String description) {
            this.name = name;
            this.media = media;
            this.segments = segments;
            this.pricing = pricing;
            this.blurEnabled = blurEnabled;
            this.description = description;
        }
    }
    
    // Predefined test scenarios
    public static final TestScenario[] STANDARD_SCENARIOS = {
        new TestScenario("StandardMediaPush", DEVICE_MEDIA, SUBSCRIBERS_ONLY, 
                        STANDARD_PRICING, BLUR_ENABLED, "Standard media push with device files"),
        new TestScenario("ClearMediaPush", DEVICE_MEDIA, SUBSCRIBERS_ONLY, 
                        STANDARD_PRICING, BLUR_DISABLED, "Clear media push (blur disabled)"),
        new TestScenario("FreeMediaPush", DEVICE_MEDIA, SUBSCRIBERS_ONLY, 
                        FREE_PRICING, BLUR_ENABLED, "Free media push"),
        new TestScenario("CustomPriceMediaPush", DEVICE_MEDIA, SUBSCRIBERS_ONLY, 
                        CUSTOM_PRICING, BLUR_ENABLED, "Custom price media push"),
        new TestScenario("PromoEuroMediaPush", DEVICE_MEDIA, SUBSCRIBERS_ONLY, 
                        PROMO_EURO_PRICING, BLUR_ENABLED, "Media push with euro discount"),
        new TestScenario("PromoPercentMediaPush", DEVICE_MEDIA, SUBSCRIBERS_ONLY, 
                        PROMO_PERCENT_PRICING, BLUR_ENABLED, "Media push with percent discount")
    };
    
    public static final TestScenario[] INTERESTED_SCENARIOS = {
        new TestScenario("InterestedMediaPush", DEVICE_MEDIA, INTERESTED_ONLY, 
                        STANDARD_PRICING, BLUR_ENABLED, "Media push to Interested segment"),
        new TestScenario("InterestedEuroMediaPush", DEVICE_MEDIA, INTERESTED_ONLY, 
                        PROMO_EURO_PRICING, BLUR_ENABLED, "Interested segment with euro promo"),
        new TestScenario("InterestedFreeMediaPush", DEVICE_MEDIA, INTERESTED_ONLY, 
                        FREE_PRICING, BLUR_ENABLED, "Interested segment free push"),
        new TestScenario("InterestedClearMediaPush", DEVICE_MEDIA, INTERESTED_ONLY, 
                        STANDARD_PRICING, BLUR_DISABLED, "Interested segment clear push"),
        new TestScenario("InterestedCustomMediaPush", DEVICE_MEDIA, INTERESTED_ONLY, 
                        CUSTOM_PRICING, BLUR_ENABLED, "Interested segment custom price")
    };
    
    public static final TestScenario[] MULTI_MEDIA_SCENARIOS = {
        new TestScenario("MultiMediaPush", DEVICE_MEDIA, SUBSCRIBERS_ONLY, 
                        STANDARD_PRICING, BLUR_ENABLED, "Multiple media push"),
        new TestScenario("ClearMultiMediaPush", DEVICE_MEDIA, SUBSCRIBERS_ONLY, 
                        STANDARD_PRICING, BLUR_DISABLED, "Multiple clear media push"),
        new TestScenario("FreeMultiMediaPush", QUICK_FILES_MEDIA, SUBSCRIBERS_ONLY, 
                        FREE_PRICING, BLUR_ENABLED, "Multiple free media push"),
        new TestScenario("CustomMultiMediaPush", DEVICE_MEDIA, SUBSCRIBERS_ONLY, 
                        CUSTOM_PRICING, BLUR_ENABLED, "Multiple custom price media push"),
        new TestScenario("PromoMultiMediaPush", DEVICE_MEDIA, SUBSCRIBERS_AND_INTERESTED, 
                        PROMO_UNLIMITED_PRICING, BLUR_ENABLED, "Multiple media with promo to both segments")
    };
    
    public static final TestScenario QUICK_FILES_SCENARIO = new TestScenario(
        "QuickFilesMediaPush", QUICK_FILES_MEDIA, SUBSCRIBERS_ONLY, 
        STANDARD_PRICING, BLUR_ENABLED, "Quick Files media push"
    );
}
