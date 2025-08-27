package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.CreatorCollectionPage;

@Epic("Creator")
@Feature("Collection")
public class CreatorCollectionDeleteTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorCollectionDeleteTest.class);

    @Story("Delete all existing collections for cleanup via files icon flow")
    @Test(priority = 1, description = "Cleanup: delete all existing collections using files icon")
    public void deleteAllExistingCollections() {
        CreatorCollectionPage coll = new CreatorCollectionPage(page);
        logger.info("[Cleanup] Starting collections cleanup using files icon loop");
        coll.deleteAllCollectionsUsingFilesIcon(100);
    }
}
