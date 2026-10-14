package cucumber.context;

import com.microsoft.playwright.Page;

import pages.auth.IntroPage;
import pages.auth.SignInPage;
import pages.auth.SignUpPage;
import pages.common.LandingPage;
import pages.public_.AuthModalPage;
import pages.public_.CreatorPublicProfilePage;
import pages.public_.LivesPage;
import pages.public_.PublicDiscoverPage;
import pages.public_.PublicExplorePage;
import utils.BrowserFactory;
import utils.ConfigReader;

/**
 * Shared test context injected into every step definition via PicoContainer.
 * Holds the Playwright Page and lazily-created page objects.
 * Recreated per scenario by Cucumber's DI lifecycle.
 */
public class TestContext {

    private Page page;

    // Lazy page objects
    private LandingPage landingPage;
    private PublicDiscoverPage publicDiscoverPage;
    private PublicExplorePage publicExplorePage;
    private CreatorPublicProfilePage creatorPublicProfilePage;
    private AuthModalPage authModalPage;
    private LivesPage livesPage;
    private IntroPage introPage;
    private SignInPage signInPage;
    private SignUpPage signUpPage;

    // Shared state between steps
    private String currentCreatorHandle;

    public Page getPage() {
        if (page == null) {
            BrowserFactory.initialize();
            page = BrowserFactory.getPage();
            page.setDefaultNavigationTimeout(ConfigReader.getNavigationTimeout());
            page.setDefaultTimeout(ConfigReader.getDefaultTimeout());
        }
        return page;
    }

    // -- Navigation helpers (avoids hitting landing page when not needed) --

    public void navigateTo(String url) {
        getPage().navigate(url);
        getPage().waitForLoadState();
    }

    public void navigateToLandingPage() {
        navigateTo(ConfigReader.getLandingPageUrl());
    }

    public void navigateToPublicDiscover() {
        navigateTo(ConfigReader.getBaseUrl() + "/public-discover");
    }

    public void navigateToPublicExplore() {
        navigateTo(ConfigReader.getBaseUrl() + "/public-explore");
    }

    public void navigateToLives() {
        navigateTo(ConfigReader.getBaseUrl() + "/p/fan/lives");
    }

    public void navigateToIntro() {
        navigateTo(ConfigReader.getBaseUrl() + "/auth/intro");
    }

    public void navigateToSignIn() {
        navigateTo(ConfigReader.getLoginUrl());
    }

    public void navigateToSignUp() {
        navigateTo(ConfigReader.getFanSignupUrl());
    }

    public void navigateToCreatorSignUp() {
        navigateTo(ConfigReader.getCreatorRegistrationUrl());
    }

    // -- Lazy page object getters --

    public LandingPage getLandingPage() {
        if (landingPage == null) landingPage = new LandingPage(getPage());
        return landingPage;
    }

    public PublicDiscoverPage getPublicDiscoverPage() {
        if (publicDiscoverPage == null) publicDiscoverPage = new PublicDiscoverPage(getPage());
        return publicDiscoverPage;
    }

    public PublicExplorePage getPublicExplorePage() {
        if (publicExplorePage == null) publicExplorePage = new PublicExplorePage(getPage());
        return publicExplorePage;
    }

    public CreatorPublicProfilePage getCreatorPublicProfilePage() {
        if (creatorPublicProfilePage == null) creatorPublicProfilePage = new CreatorPublicProfilePage(getPage());
        return creatorPublicProfilePage;
    }

    public AuthModalPage getAuthModalPage() {
        if (authModalPage == null) authModalPage = new AuthModalPage(getPage());
        return authModalPage;
    }

    public LivesPage getLivesPage() {
        if (livesPage == null) livesPage = new LivesPage(getPage());
        return livesPage;
    }

    public IntroPage getIntroPage() {
        if (introPage == null) introPage = new IntroPage(getPage());
        return introPage;
    }

    public SignInPage getSignInPage() {
        if (signInPage == null) signInPage = new SignInPage(getPage());
        return signInPage;
    }

    public SignUpPage getSignUpPage() {
        if (signUpPage == null) signUpPage = new SignUpPage(getPage());
        return signUpPage;
    }

    // -- Shared state --

    public String getCurrentCreatorHandle() {
        return currentCreatorHandle;
    }

    public void setCurrentCreatorHandle(String handle) {
        this.currentCreatorHandle = handle;
    }

    public String getCurrentUrl() {
        return getPage().url();
    }
}
