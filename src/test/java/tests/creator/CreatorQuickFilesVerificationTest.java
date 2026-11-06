package tests.creator;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route.FulfillOptions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.creator.CreatorSettingsPage;

public class CreatorQuickFilesVerificationTest {
    @DataProvider(name = "albums")
    public Object[][] albums() {
        return new Object[][]{{"Photos & videos", true}, {"Audios", true}, {"", false}};
    }

    @Test
    public void undeletableAlbumCannotPassCleanup() throws Exception {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {
            var page = browser.newPage();
            page.route("**/*", route -> route.fulfill(new FulfillOptions().setContentType("text/html").setBody("""
                    <div class="quick-file-switch" aria-pressed="true">Photos &amp; videos</div>
                    <div class="qf-row">
                        <span class="qf-row-icon">Album</span>
                        <span class="qf-row-title">imagealbum_261105_150935</span>
                    </div>
                    """)));
            page.navigate("https://preprod.twizz.com/creator/quickLink");
            var cleanup = CreatorSettingsPage.class.getDeclaredMethod("deleteAlbumsInCurrentTab", String.class);
            cleanup.setAccessible(true);
            var failure = Assert.expectThrows(java.lang.reflect.InvocationTargetException.class,
                    () -> cleanup.invoke(new CreatorSettingsPage(page), "Photos & videos"));
            Assert.assertTrue(failure.getCause() instanceof AssertionError);
            Assert.assertTrue(failure.getCause().getMessage().contains("imagealbum_261105_150935"));
        }
    }

    @Test(dataProvider = "albums")
    public void freshVerificationRejectsRemainingAlbums(String remainingTab, boolean shouldFail) {
        try (Playwright playwright = Playwright.create();
             Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))) {
            var page = browser.newPage();
            String html = """
                    <div class="quick-file-switch" aria-pressed="false">Photos &amp; videos</div>
                    <div class="quick-file-switch" aria-pressed="true">Audios</div>
                    <main></main>
                    <script>
                    let generation = 0;
                    function render(tab) {
                        const current = ++generation;
                        document.querySelector('main').innerHTML = '';
                        document.querySelectorAll('.quick-file-switch').forEach(e => {
                            e.setAttribute('aria-pressed', String(e.textContent === tab));
                        });
                        setTimeout(() => {
                            if (current !== generation) return;
                            document.querySelector('main').innerHTML = tab === '%s'
                                ? '<div class="qf-row-title">imagealbum_261105_150935</div>'
                                : (tab === 'Audios' ? "You haven't created any audio albums yet"
                                    : "You haven't created any photo or video albums yet");
                        }, 1500);
                    }
                    document.querySelectorAll('.quick-file-switch').forEach(e => {
                        e.onclick = () => render(e.textContent);
                    });
                    render('Audios');
                    </script>
                    """.formatted(remainingTab);
            page.route("**/*", route -> route.fulfill(new FulfillOptions()
                    .setContentType("text/html").setBody(html)));
            CreatorSettingsPage settings = new CreatorSettingsPage(page);
            if (shouldFail) {
                AssertionError failure = Assert.expectThrows(AssertionError.class, settings::assertQuickFilesAlbumsEmpty);
                Assert.assertTrue(failure.getMessage().contains("imagealbum_261105_150935"));
                Assert.assertTrue(failure.getMessage().contains(remainingTab));
            } else {
                settings.assertQuickFilesAlbumsEmpty();
            }
        }
    }
}
