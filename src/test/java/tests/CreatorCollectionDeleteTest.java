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

    @Story("Delete all existing collections for cleanup")
    @Test(priority = 1, description = "Cleanup: delete all existing collections in creator account")
    public void deleteAllExistingCollections() {
        CreatorCollectionPage coll = new CreatorCollectionPage(page);
        logger.info("[Cleanup] Starting collections cleanup until contentinfo is visible");
        coll.deleteUntilContentInfoVisible(50);
        if (coll.isContentInfoVisible()) {
            logger.info("[Cleanup] Contentinfo visible; finishing test and letting teardown close the browser");
        } else {
            logger.warn("[Cleanup] Contentinfo not visible after max iterations; finishing test");
        }
    }
}
