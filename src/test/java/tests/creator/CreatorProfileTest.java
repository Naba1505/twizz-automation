package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorProfilePage;

public class CreatorProfileTest extends BaseCreatorTest {

    @Test(priority = 1, description = "Creator login and lands on profile screen; verify key elements and icons navigation")
    public void creatorCanLandOnProfileAndSeeKeyElements() {
        // Navigate to profile
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Assert header & top counters
        profile.assertHeaderUsernameVisible();
        profile.assertAvatarVisible();
        profile.assertTopCountersVisible();

        // Scroll to bottom and validate icons
        profile.scrollToBottom();
        profile.assertBottomIconsVisible();
        // Click on collections and then back to publications
        profile.clickCollectionsIcon();
        profile.clickPublicationsIcon();

        // Scroll back to top
        profile.scrollToTop();
    }

    @Test(priority = 2, description = "Creator uploads profile avatar from Modify profile screen and sees success toast")
    public void creatorCanUploadProfileAvatar() {
        java.nio.file.Path avatar = java.nio.file.Paths.get("src/test/resources/Images/ProfileImageA.jpg");

        // Navigate to profile
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Open Modify profile screen and upload avatar
        profile.openModifyProfile();
        profile.assertModifyProfileScreen();
        profile.clickUploadAvatarPencil();
        profile.clickAvatarEditMenuItem();
        profile.uploadAvatarImage(avatar);
        profile.waitForAvatarUpdatedToast();
    }

    @Test(priority = 3, description = "Creator updates Last name, Position, and Description on Modify profile and saves successfully")
    public void creatorCanUpdateProfileFields() {
        // Navigate to profile and open Modify screen
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();
        profile.openModifyProfile();
        profile.assertModifyProfileScreen();

        // Update fields using robust clear/fill helpers
        profile.setLastName("Smith");
        profile.setPosition("India");
        profile.setDescription("Automation Test Creator");

        // Submit and confirm
        profile.clickRegisterUpdate();
        profile.waitForProfileUpdatedToastSoft(8000);
    }

    @Test(priority = 4, description = "Creator verifies Share Profile options (whatsapp, twitter, telegram, message, copy, cancel)")
    public void creatorCanUseShareProfileOptions() {
        // Navigate to profile
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Open Share profile and exercise options
        profile.openShareProfile();
        profile.openShareOptionAndClose("whatsapp");
        profile.openShareOptionAndClose("twitter");
        profile.openShareOptionAndClose("telegram");
        profile.clickShareMessageIcon();
        profile.clickShareCopyIcon();
        profile.cancelShare();
    }

    @Test(priority = 5, description = "Creator deletes uploaded avatar from Modify profile with confirmation")
    public void creatorCanDeleteProfileAvatar() {
        // Navigate to profile
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();

        // Open Modify profile screen and delete avatar
        profile.openModifyProfile();
        profile.assertModifyProfileScreen();
        profile.clickUploadAvatarPencil();
        profile.clickDeleteAvatarOption();
        profile.assertDeleteAvatarConfirmVisible();
        profile.confirmDeleteAvatarYes();
        profile.waitForAvatarRemovedToast();
    }
}
