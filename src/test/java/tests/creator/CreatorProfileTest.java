package tests.creator;

import org.testng.annotations.Test;
import pages.creator.CreatorProfilePage;
import utils.ConfigReader;

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

        // The DOB field must be bound into the save payload — visually filled
        // but empty in form state is the "@identifier" bug the backend rejects
        // with 400 "dateOfBirth should not be empty"
        org.testng.Assert.assertTrue(profile.isDobBound(), "DOB input is empty or unbound");
        org.testng.Assert.assertTrue(profile.isBioFooterVisible(), "'@ Identification' bio footer not visible");

        // Submit and confirm — assert the PATCH itself succeeds (soft toast
        // alone would silently swallow a 400)
        int saveStatus = profile.clickRegisterUpdateAndWaitSave(ConfigReader.getMediumTimeout());
        org.testng.Assert.assertEquals(saveStatus, 200, "PATCH /users/infos failed — profile save rejected");
        profile.waitForProfileUpdatedToastSoft(ConfigReader.getMediumTimeout());
    }

    @Test(priority = 6, description = "Creator exercises Modify profile switches (Featured collection, Free subscription, Free chat) and saves")
    public void creatorCanToggleProfileSwitches() {
        CreatorProfilePage profile = new CreatorProfilePage(page);
        profile.navigateToProfile();
        profile.assertOnProfileUrl();
        profile.openModifyProfile();
        profile.assertModifyProfileScreen();

        // Flip each switch on and back off — state must actually change and restore
        org.testng.Assert.assertTrue(profile.exerciseProfileSwitch(0, "Featured collection"), "Featured collection switch did not toggle");
        org.testng.Assert.assertTrue(profile.exerciseProfileSwitch(1, "Free subscription"), "Free subscription switch did not toggle");
        org.testng.Assert.assertTrue(profile.exerciseProfileSwitch(2, "Free chat"), "Free chat switch did not toggle");

        // Same DOB-binding race guard as the fields test — an unbound DOB
        // makes the save fail with 400 "dateOfBirth should not be empty"
        org.testng.Assert.assertTrue(profile.isDobBound(), "DOB input empty/unbound before save");

        int saveStatus = profile.clickRegisterUpdateAndWaitSave(ConfigReader.getMediumTimeout());
        org.testng.Assert.assertEquals(saveStatus, 200, "PATCH /users/infos failed after switch exercise");
    }

    @Test(priority = 4, description = "Creator verifies Share Profile options (WhatsApp, X, Telegram, SMS, Copy link, close)")
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
        profile.clickShareSms();
        profile.clickCopyLink();
        profile.closeSharePanel();
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
