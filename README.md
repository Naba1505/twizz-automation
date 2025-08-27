# Twizz Automation (Java + Playwright + TestNG)

End-to-end UI automation for Twizz with Creator and Fan flows. The framework emphasizes robust, resilient interactions and uses Allure only for reporting (with Playwright traces and screenshots).

## Tech Stack
- Java 21
- Playwright for Java 1.54.0
- TestNG 7.11.0
- Allure TestNG 2.29.1
- SLF4J + Logback
- Maven Surefire Plugin 3.2.5

## Quick Start
1. Clone and build
   ```bash
   mvn clean install -DskipTests
   ```
2. (First time only) Ensure Playwright browsers are installed
   ```bash
   mvn -Dplaywright.cli.install=true test -DskipTests
   ```
3. Run the full suite (uses `testng.xml`)
   ```bash
   mvn test
   ```
   Or explicitly:
   ```bash
   mvn -B "-Dsurefire.suiteXmlFiles=testng.xml" test
   ```
4. View reports
   - Allure (requires Allure CLI):
     ```bash
     allure serve target/allure-results
     ```

## Project Structure
- `src/main/java/utils/`
  - `BrowserFactory`: ThreadLocal Playwright/Browser/Context/Page for parallel safety, optional tracing.
  - `ConfigReader`: Loads `config.properties` and exposes typed getters.
  - `RetryAnalyzer`, `AnnotationTransformer`: Centralized retry with logging and optional delay.
- `src/test/java/pages/`
  - `BasePage`: Common helpers.
  - `LandingPage`, `CreatorRegistrationPage`, `CreatorLoginPage`, `CreatorPublicationPage`, `CreatorCollectionPage`, `FanRegistrationPage`, `FanLoginPage`: Page Objects with robust waits and fallbacks.
  - `BaseTestClass`: Setup/teardown, screenshots, Allure/trace attachments.
- `src/test/java/tests/`
  - `LandingPageTest`, `CreatorRegistrationTest`, `FanRegistrationTest`, `CreatorLoginTest`, `CreatorPublicationTest`, `CreatorQuickFilesTest`, `CreatorQuickFilesDeleteTest`, `CreatorCollectionTest`, `CreatorCollectionDeleteTest`, `FanLoginTest`.
- `testng.xml`: Suite config, listeners (`utils.AnnotationTransformer`); Allure via TestNG adapter dependency, class-level parallel by default.

## Prerequisites
- Java 21+
- Maven 3.9+
- Playwright browsers: first run will download automatically when Playwright creates the context. If needed, run: `mvn -Dplaywright.cli.install=true test` once.
- (Optional) Allure CLI to view the Allure HTML report locally.
  - Windows: `choco install allure` or `scoop install allure`

## Configuration (`src/main/resources/config.properties`)
Key entries (with defaults):
- Environment/Navigation
  - `environment=stage`
  - `url.dev=...`, `url.stage=...`, `url.prod=...`
- Browser
  - `browser=chrome`
  - `viewport.width=1280`
  - `viewport.height=720`
  - `headless=true`
- Registration Test Data
  - `registration.dob=15-05-1995`
  - `registration.gender=Male`
  - `registration.subscriptionPrice=5`
  - `registration.contentTypes=Model,Artist,Influencer,ASMR,Lingerie`
- Reporting
  - `author=Your Name`
  - `screenshot.dir=screenshots`
  - `screenshot.on.success=false`
  - `retry.max=2`
  - `retry.delay.ms=0`
  - `trace.enable=true`
  - `trace.dir=traces`
  - `trace.export.on.success=false`

## Running Tests
- Full suite (uses `testng.xml`):
  ```bash
  mvn test
  ```
- Sequential via XML (handy for debugging):
  ```bash
  mvn -B "-Dsurefire.suiteXmlFiles=testng.xml" test
  ```
- Parallel via XML:
  ```bash
  mvn -B "-Dsurefire.suiteXmlFiles=testng-parallel.xml" test
  ```
- Single test class:
  ```bash
  mvn test -Dtest=CreatorRegistrationTest
  ```

### Collections Cleanup (Creator)
- Test: `CreatorCollectionDeleteTest`
- What it does:
  - From the Creator profile screen, opens Collections via `IMG[name='collections icon']`.
  - Iteratively opens a collection using the top `files` icon, navigates to Details, opens the three-dots menu, chooses “Delete collection”, confirms “Yes, delete”, and verifies deletion via toast/alert or UI-state fallbacks.
  - Loops until no `files` icon remains.
- Run only this cleanup:
  ```bash
  mvn -Dtest=CreatorCollectionDeleteTest test
  ```
- If you hit parallel issues on some environments, switch `testng.xml` to `parallel="tests"` or run sequentially.

## Quick Files (Creator) Upload Tests
- Tests: `CreatorQuickFilesTest`
  - Three scenarios split by media type with album name prefixes:
    - Videos only: prefix `videoalbum_`
    - Images only: prefix `imagealbum_`
    - Mixed media: prefix `mixalbum_`
- Upload strategy:
  - Prefer sequential per-file uploads via the PLUS button with tab switching to the appropriate media tab.
  - If PLUS is not a native input (e.g., Ant Upload wrapper), fallback to `input[type=file]` and set files (batch or sequential by `multiple`).
- Timing constants used (see `pages/CreatorSettingsPage.java`):
  - `SHORT_PAUSE_MS = 300`
  - `SEQUENTIAL_PAUSE_MS = 500`
  - Tests use `POST_CONFIRM_PAUSE_MS = 1000` (see `tests/CreatorQuickFilesTest.java`).
- How to run:
  ```bash
  mvn -Dtest=CreatorQuickFilesTest#creatorCreatesQuickAlbum_VideosOnly test
  mvn -Dtest=CreatorQuickFilesTest#creatorCreatesQuickAlbum_ImagesOnly test
  mvn -Dtest=CreatorQuickFilesTest#creatorCreatesQuickAlbum_MixedMedia test
  ```

## Fan Login
- Page object: `pages/FanLoginPage`
- Behavior:
  - After clicking Connect, avoids `NETWORKIDLE` waits (flaky on SPAs).
  - Waits for redirect to `/fan/home` using URL-based wait helpers.
- Test: `tests/FanLoginTest`
  - Uses `pageObj.waitForFanHomeUrl()` and asserts current URL contains `/fan/home`.

## Reports (Allure)
- Allure raw results are written to: `target/allure-results/`
- Install Allure CLI:
  - Windows: `choco install allure` or `scoop install allure`
  - macOS: `brew install allure`
  - Linux: `sudo npm i -g allure-commandline --save-dev` or download from GitHub releases
- Quick preview (one-off web server):
  ```bash
  allure serve target/allure-results
  ```
- Generate static report and open:
  ```bash
  allure generate -c -o target/allure-report target/allure-results
  allure open target/allure-report
  ```
- What gets attached automatically:
  - Failure screenshot (PNG)
  - Page HTML on failure
  - Playwright trace ZIP on failure
  - Optional on success (configurable via `src/main/resources/config.properties`):
    - `screenshot.on.success=false|true`
    - `trace.export.on.success=false|true`
    - `trace.enable=true|false` and `trace.dir=traces`
- CI notes:
  - GitHub Actions uploads `target/allure-results` as an artifact (see `.github/workflows/ci.yml`).
  - Jenkins pipeline attempts to publish Allure results if the Allure plugin is installed (see `Jenkinsfile`).
- Troubleshooting:
  - If `allure` command is not found, ensure CLI is installed and on PATH. On Windows, restart the shell after installing via Chocolatey/Scoop.
  - If the report is empty, confirm tests ran and `target/allure-results` contains `.json`/attachments.

## Creator Registration Flow Coverage
- Page 1: Basic info (name, username, names, DOB, email, password, phone, gender)
- Page 2: Content types (config-driven, trimmed)
- Page 3: Subscription price (robust toggle/price handling)
- Page 4: Status (private individual)
- Page 5: Document uploads (identity + selfie)
- Final confirmation validated: `"Thank you for your interest!"`

## Parallel Test Safety
- `BrowserFactory` uses ThreadLocal Playwright/Browser/Context/Page per thread.
- Each test method uses its own `Page`, traced optionally.

## Common Troubleshooting
- Maven property parsing errors on Windows PowerShell: put `-D` properties before the goal or quote them: `mvn test "-Dsurefire.suiteXmlFiles=testng.xml"`
- Allure CLI not found: install via `choco` or `scoop`, then run `allure serve`.
- Flaky network/visibility: run sequentially (`thread-count=1`) to confirm stability, then re-enable parallel.

## Housekeeping
- Git ignores common transient artifacts to keep the repo clean:
  - `allure-results/` (root-level if generated by tools)
  - `traces/` and `screenshots/` (configurable via `config.properties`)
  - `playwright-report/` (if Playwright HTML report is enabled)
  - `test-output/` (TestNG default output)
- You can safely delete contents of these folders locally before pushing if you created them manually.

## License
Proprietary/Internal (adjust as needed).
