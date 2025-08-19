package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CreatorSettingsPage;

public class CreatorQuickFilesDeleteTest extends BaseCreatorTest {
        
    @Test(priority = 1, description = "Direct cleanup: delete all existing Quick Files albums (no dependency)")
    public void deleteAllQuickFilesAlbums_Direct() {
        CreatorSettingsPage settings = new CreatorSettingsPage(page);
        settings.deleteAllQuickFileAlbums();
        int remaining = settings.quickFilesTrashIconCount();
        Assert.assertEquals(remaining, 0, "Some Quick Files albums still present (trash icons left): " + remaining);
    }
}
