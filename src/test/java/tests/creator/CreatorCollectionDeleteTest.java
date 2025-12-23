package tests.creator;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import pages.creator.CreatorCollectionPage;

@Epic("Creator")
@Feature("Collection")
public class CreatorCollectionDeleteTest extends BaseCreatorTest {
    private static final Logger logger = LoggerFactory.getLogger(CreatorCollectionDeleteTest.class);

    @Story("Cleanup via Collections icon -> scroll -> select second 'collection' tile")
    @Test(priority = 1, description = "Cleanup: delete all existing collections using Collections icon + second tile flow")
    public void deleteAllExistingCollections() {
        CreatorCollectionPage coll = new CreatorCollectionPage(page);
        logger.info("[Cleanup] Starting collections cleanup using exact flow: icon -> scroll -> second tile -> menu -> delete");
        // Follows UI steps: click collections icon, scroll, click second 'collection' tile, three-dots menu, Delete collection -> Yes, delete
        coll.deleteAllCollectionsExactFlow(10);
        // Verify empty state is shown after all deletions
        coll.assertNoCollectionsEmptyState();
    }
}
