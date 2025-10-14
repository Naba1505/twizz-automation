# Twizz Automation (Java + Playwright + TestNG)
## Presentation Videos (Creator)
- Page object: `pages/CreatorPresentationVideosPage`
- Tests class: `tests/CreatorPresentationVideosTest`
- Flow:
  - Login → Settings → Presentation Videos → Upload short video → optional uploading banner → assert Waiting status
  - Includes clicking `.presentation-video-sticky-button` after upload
- Run example:
  ```bash
  mvn -Dtest=CreatorPresentationVideosTest test
  ```

## Push History (Creator)
- Page object: `pages/CreatorPushHistoryPage`
- Tests class: `tests/CreatorPushHistoryTest`
- Flow:
  - Login → Settings → History of pushes → open last item → assert Performance → back → open first item → assert Performance → back to profile
- Run example:
  ```bash
  mvn -Dtest=CreatorPushHistoryTest test
  ```

## Unlock History (Creator)
- Page object: `pages/CreatorUnlockHistoryPage`
- Tests class: `tests/CreatorUnlockHistoryTest`
- Flow:
  - Login → Settings → Unlock history → open last entry → assert Details → back → open first entry → assert Details → back to profile
- Run example:
  ```bash
  mvn -Dtest=CreatorUnlockHistoryTest test
  ```

## Help and Contact (Creator)
- Page object: `pages/CreatorHelpAndContactPage`
- Tests class: `tests/CreatorHelpAndContactTest`
- Flow:
  - Login → Settings → Help and contact → Subject/Message fill → Send → assert "Your message has been sent"
- Run example:
  ```bash
  mvn -Dtest=CreatorHelpAndContactTest test
  ```

## Legal Pages (Creator)
- Page object: `pages/CreatorLegalPages`
- Tests class: `tests/CreatorLegalPagesTest`
- Flow:
  - Terms & Conditions of Sale: assert title/url → scroll to bottom snippet and back
  - Community Regulations: assert title/url → scroll to bottom snippet and back
- Run example:
  ```bash
  mvn -Dtest=CreatorLegalPagesTest test
  ```

## Payment Method (Creator)
- Page object: `src/test/java/pages/CreatorPaymentMethodPage.java`
- Tests class: `src/test/java/tests/CreatorPaymentMethodTest.java`
- Scenarios:
  1. Add Bank Account: fills Name of the bank, SWIFT, IBAN/Account number, Country, Address, Postal code, City → submits → validates success and card.
  2. Set as Default: opens the added card (RevoCard) → clicks "Set as default".
  3. Delete Bank Account: opens card → clicks "Delete this card" → confirms "Yes delete".
  4. Deposit Duration Switching: ensures "Whenever you want." text → selects Every 7 days/On pause/Every 30 days, confirming each dialog.
- Run examples:
  ```bash
  mvn -Dtest=CreatorPaymentMethodTest#creatorCanAddBankAccount test
  mvn -Dtest=CreatorPaymentMethodTest#creatorCanSetBankAccountAsDefault test
  mvn -Dtest=CreatorPaymentMethodTest#creatorCanDeleteBankAccount test
  mvn -Dtest=CreatorPaymentMethodTest#creatorCanSwitchDepositDurations test
  ```

## Logout (Creator)
- Page object: `src/test/java/pages/CreatorLogoutPage.java`
- Tests class: `src/test/java/tests/CreatorLogoutTest.java`
- Flow: Profile → Settings (URL contains `/common/setting`) → click `Disconnect` → ensure Twizz logo is visible on intro.
- Run example:
  ```bash
  mvn -Dtest=CreatorLogoutTest test
  ```

## Language (Creator)
- Page object: `src/test/java/pages/CreatorLanguagePage.java`
- Tests class: `src/test/java/tests/CreatorLanguageTest.java`
- Flow: Profile → Settings → Language → switch Français (title `Langue`), Español (title `Idioma`), back to English (title `Language`) → back to profile.
- Run example:
  ```bash
  mvn -Dtest=CreatorLanguageTest test
  ```

## History of Collections (Creator)
- Page object: `src/test/java/pages/CreatorCollectionsHistoryPage.java`
- Tests class: `src/test/java/tests/CreatorCollectionsHistoryTest.java`
- Flow: Profile → Settings → History of collections → ensure title `Collections` → open first collection (icon `collection`) → ensure `Details` → back to profile.
- Run example:
  ```bash
  mvn -Dtest=CreatorCollectionsHistoryTest test
  ```

## Automatic Message (Creator)
- Page object: `src/test/java/pages/CreatorAutomaticMessagePage.java`
- Tests class: `src/test/java/tests/CreatorAutomaticMessageTest.java`
- Scenario covered:
  - New subscriber automatic message: open Settings → Automatic Message (title `Automation`) → validate "New subscriber" header and info → Modify → add media from My Device (`src/test/resources/Images/AutoMessageImage.png`) → Next → fill message and set price (15€) → Save → wait for upload prompt to disappear → ensure Modify visible again → ensure first toggle is enabled.
- Run example:
  ```bash
  mvn -Dtest=CreatorAutomaticMessageTest#verifyNewSubscriberAutoMessageCreateAndEnable test
  ```


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

## Promotions (Creator)
- Page object: `pages/CreatorPromotionsPage`
- Tests class: `tests/CreatorPromotionsTest`
- Coverage:
  - Create promo code with percent discount for Subscription (Unlimited)
  - Create promo code with percent discount for Media push / Collection (7 days)
  - Create promo code with fixed amount for Subscription (Unlimited)
  - Create promo code with fixed amount for Media push / Collection (7 days)
  - Copy links: clicks all visible `Copy` buttons (`//span[contains(text(),'Copy')]`), dismisses intermediate copy toasts opportunistically, and validates copy-success only on the last click (soft style to reduce flakiness)
- Run examples:
  ```bash
  mvn -Dtest=CreatorPromotionsTest test
  mvn -Dtest=CreatorPromotionsTest#testAddPromoCode test
  mvn -Dtest=CreatorPromotionsTest#testCopyAllPromoLinks test
  ```

### Promotions Cleanup
- Test: `tests/CleanupDeletePromoCodesTest`
- What it does:
  - Logs in as Creator, opens Settings → Promo code
  - Iteratively deletes all promo codes whose title starts with "Automation" (case-insensitive), using locator `//span[starts-with(normalize-space(translate(text(),'abcdefghijklmnopqrstuvwxyz','ABCDEFGHIJKLMNOPQRSTUVWXYZ')),'AUTOMATION')]`
  - Confirms via `Yes, delete` button and handles success toast "Promo code deleted successfully !"
  - Soft assertion approach: closes intermediate toasts if present without asserting; for the final deletion it attempts to observe the toast but does not fail if it is not visible (reduces UI timing flakiness). A final verification loop ensures zero remaining promos; if any remain due to virtualization/pagination, it re-opens the Promo page and retries until none remain or a safety cap is reached.
- Run only this cleanup:
  ```bash
  mvn -Dtest=CleanupDeletePromoCodesTest test
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
  - `ConfigReader`: Loads `config.properties` and exposes typed getters.
  - `RetryAnalyzer`, `AnnotationTransformer`: Centralized retry with logging and optional delay.
- `src/test/java/pages/`
  - `BasePage`: Common helpers.
  - `LandingPage`, `CreatorRegistrationPage`, `CreatorLoginPage`, `CreatorPublicationPage`, `CreatorCollectionPage`, `FanRegistrationPage`, `FanLoginPage`: Page Objects with robust waits and fallbacks.
    44→  - `BaseTestClass`: Setup/teardown, screenshots, Allure/trace attachments.
- `src/test/java/tests/`
  - `LandingPageTest`, `CreatorRegistrationTest`, `FanRegistrationTest`, `CreatorLoginTest`, `CreatorPublicationTest`, `CreatorLiveTest`, `CreatorQuickFilesTest`, `CreatorMediaPushTest`, `CreatorUnlockLinksTest`, `CreatorDiscoverTest`, `CreatorRevenuesTest`, `CreatorQuickFilesDeleteTest`, `CreatorCollectionTest`, `CreatorCollectionDeleteTest`, `FanLoginTest`.
  - `testng.xml`: Suite config, listeners (`utils.AnnotationTransformer`); Allure via TestNG adapter dependency. Runs sequentially by default.
  - To run in parallel, use `testng-parallel.xml` or the Maven profile: `mvn -P parallel test`.
  - New: `CreatorPromotionsTest` (promo code create + copy) and `CleanupDeletePromoCodesTest` (cleanup 'AUTOMATION' promos)

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

- Promotions only:
  ```bash
  mvn -Dtest=CreatorPromotionsTest test
  ```

- Promotions cleanup only:
  ```bash
  mvn -Dtest=CleanupDeletePromoCodesTest test
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

### Quick Files Collection Flow (POM)
- Test: `CreatorCollectionTest#creatorCanCreateCollectionUsingQuickFilesAlbum`
- What it does:
  - Opens the Creator plus menu, dismisses any "I understand" dialog, navigates to `Collection`.
  - Creates a collection, adds media via Quick Files, selects album by case-insensitive prefix (video/image/mix), selects up to 3 covers via mouse hover + click, proceeds next steps, validates, and waits for the success toast only.
  - All locators and interactions are encapsulated in `pages/CreatorCollectionPage`.
- How to run just this test:
  ```bash
  mvn -Dtest=CreatorCollectionTest#creatorCanCreateCollectionUsingQuickFilesAlbum test
  ```
 - Notes:
   - The test asserts only the success toast "Collection is created successfully" and dismisses it.
   - Interactions prefer mouse hover and clicks to mimic real user behavior.

## Media Push (Creator)
- __Page object__: `pages/CreatorMediaPushPage`
- __Tests class__: `tests/CreatorMediaPushTest` (19 tests total)
- __Coverage__:
  - Subscribers-only flows (priorities 1–6)
  - Interested-only flows (priorities 7–12)
  - Multi-select: Subscribers + Interested (priorities 13–18)
  - Quick Files album import (priority 19): selects a Quick Files album, picks up to 3 media, fills message and price, proposes, and asserts Messaging. Skips if no Quick Files albums are available.
- __General flow__:
  - Open plus menu, ensure options popup, dismiss "I understand" if present.
  - Choose "Media push" and ensure "Select your segments" screen.
  - Select segment(s) and proceed.
  - Add two media (image + video) from device with robust toggles (blur, etc.).
  - Fill message, set price/promotion per scenario.
  - Click "Propose push media" and assert landing on Messaging.
- __Limiter popup handling__ (Interested and Multi-select): after proposing, a weekly limit dialog may appear for Interested recipients. Tests call `handleIUnderstandAfterProposeIfVisible()` to click "I understand" and exit early. If not shown, they proceed to validate Messaging as usual.

### Running Media Push tests
- Single baseline test:
  ```bash
  mvn -Dtest=CreatorMediaPushTest#creatorCanSendMediaPushToSubscribers test
  ```
- Only Interested flows (priorities 7–12):
  ```bash
  mvn -Dtest=CreatorMediaPushTest#creatorCanSend*Interested* test
  ```
- Only Multi-select flows (priorities 13–18):
  ```bash
  mvn -Dtest=CreatorMediaPushTest#creatorCanSend*MultiSelect* test
  ```

- Quick Files album flow only (priority 19):
  ```bash
  mvn -Dtest=CreatorMediaPushTest#creatorCanSendMediaPushUsingQuickFilesAlbum test
  ```

## Revenues (Creator)
- __Page object__: `pages/CreatorRevenuesPage`
- __Tests class__: `tests/CreatorRevenuesTest`
- __Coverage__:
  - Screen load and header blocks
    - Open from dashboard via `IMG[name='Revenues icon']`
    - Assert title, currency image, validated/waiting price blocks and info popovers
  - Chart basics per tab
    - Today: chart container visible, title "Total of the day", "Receipt in your bank account" clickable, two price texts visible
    - This week: title "Total of the week" (same validations)
    - This month: title "Total of the month" (same validations)
    - All: title "Total since the creation" (same validations)
  - Last report + Filter flow
    - Scroll to "Last report" section, ensure content container visible
    - Change dropdown (Daily → Monthly → Detailed), verifying content remains visible
    - Filter dropdown: clicks each in order (Decrypt → Live → Private medias → Medias push → Collection → Stream → Monthly subscription → All)
    - Robust fallbacks when Filter button is not visible (opens via change icon) and stable scrolling back to top after "All"

### Running Revenues tests
- Entire class:
  ```bash
  mvn -Dtest=CreatorRevenuesTest test
  ```
- Specific scenarios:
  ```bash
  mvn -Dtest=CreatorRevenuesTest#creatorCanViewRevenues test
  mvn -Dtest=CreatorRevenuesTest#revenuesTodayChartBasics test
  mvn -Dtest=CreatorRevenuesTest#revenuesThisWeekChartBasics test
  mvn -Dtest=CreatorRevenuesTest#revenuesThisMonthChartBasics test
  mvn -Dtest=CreatorRevenuesTest#revenuesAllChartBasics test
  mvn -Dtest=CreatorRevenuesTest#revenuesLastReportAndFilterFlows test
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

## Messaging (Creator)
- Page object: `src/test/java/pages/CreatorMessagingPage.java`
- Tests class: `src/test/java/tests/CreatorMessagingTest.java`
- Coverage (13 tests):
  1. Send normal text message (with timestamp)
  2. Send saved response (Quick answer) with appended timestamp
  3. Send image media via Importation → My Device
  4. Send video media via Importation → My Device
  5. Send media via Importation → Quick Files (albums)
  6. Private media: image + video via My Device
  7. Private media with promotion (10€; validity Unlimited)
  8. Private media with promotion (5% discount; validity 7 days)
  9. Private media Free (image + video)
  10. Private media Free (unblurred)
  11. Private media via Quick Files (multi-select and stepper)
  12. View Private Gallery (open actions menu, scroll, preview and close)
  13. Messaging dashboard tabs + Filter + Search actions

### Running Messaging tests
- Entire `CreatorMessagingTest` class:
  ```bash
  mvn -Dtest=tests.CreatorMessagingTest test
  ```
- Selected tests by method names:
  ```bash
  mvn -Dtest=tests.CreatorMessagingTest#creatorCanSendQuickAnswerWithTimestamp+creatorCanSendPrivateMediaViaQuickFiles test
  mvn -Dtest=tests.CreatorMessagingTest#creatorCanViewPrivateGallery+creatorCanUseMessagingTabsFilterAndSearch test
  ```

### Notes on robustness
- Send button targeting hardened:
  - Prefers `getByRole(AriaRole.BUTTON, name="Send")`, scopes to conversation footer when needed, with `.messageSendLabel` fallback.
- Quick Files private media flow:
  - Album selection uses regex + CSS fallbacks.
  - Item selection mirrors codegen for first two covers and supports broader selectors.
  - Stepper advancement uses `clickNextUntilMessagePlaceholder()` with resilience against variable steps.
  - Upload completion waits for banner/spinner to disappear before final assertions.

## Creator Profile (Creator)
- Page object: `src/test/java/pages/CreatorProfilePage.java`
- Tests class: `src/test/java/tests/CreatorProfileTest.java`
- Coverage (7 tests):
  1. Login and land on Profile, verify key elements (URL, username header, avatar, Publications/Subscribers/Interested, bottom icons; navigate Collections Publications)
  2. Upload profile avatar (Modify profile pencil icon Edit pick `src/test/resources/Images/ProfileImageA.jpg`; success toast)
  3. Delete profile avatar (Modify profile pencil icon Delete confirm Yes; success toast)
  4. Update profile switches (Post grid Collection, Subscriber chat Free chat; Register; success toast)
  5. Revert profile switches (Collection Post grid, Free chat Subscriber chat; Register; success toast)
  6. Update profile fields (Last name = Smith, Position = India, Description = Automation Test Creator; Register; success toast)
  7. Share Profile options (open Share profile; whatsapp/twitter/telegram popups; message icon; copy icon; Cancel)

### Running Creator Profile tests
- Entire class:
  ```bash
  mvn -Dtest=tests.CreatorProfileTest test
  ```
- Selected tests by method names:
  ```bash
  mvn -Dtest=tests.CreatorProfileTest#creatorCanLandOnProfileAndSeeKeyElements test
  mvn -Dtest=tests.CreatorProfileTest#creatorCanUploadProfileAvatar test
  mvn -Dtest=tests.CreatorProfileTest#creatorCanDeleteProfileAvatar test
  mvn -Dtest=tests.CreatorProfileTest#creatorCanUpdateProfileSettings+creatorCanRevertProfileSettings test
  mvn -Dtest=tests.CreatorProfileTest#creatorCanUpdateProfileFields test
  mvn -Dtest=tests.CreatorProfileTest#creatorCanUseShareProfileOptions test
  ```

### Notes on robustness
- Profile switches use specific container XPath first (e.g., `//div[@class='edit-profile-switch ']//span[contains(text(),'Collection')]`) with role/button fallbacks and scroll-into-view.
- Register/Toast handling includes a short settle after clicking Register and after toast becomes visible before dismissing it.
- Modify profile fields use robust clear-and-fill (fill(""), Ctrl+A, Backspace) prior to setting values.
- Share profile options tolerate blocked popups and close popups if opened.
- Login hardened in `pages/CreatorLoginPage` to reduce flakiness: pre-check for logged-in state, ensure login form/header, retry Connect, broaden post-login detection (icon/URL), and light fallback waits.

## Code Coverage
- We use JaCoCo for code coverage reporting of test runs.
- Generate a coverage report:
  ```bash
  mvn clean test verify
  ```
- Open the HTML report:
  - `target/site/jacoco/index.html`
- Notes:
  - Coverage is most meaningful for helper/util layers (`pages/`, `utils/`), not Playwright binaries.
  - If you run a subset of tests, coverage will reflect only those executions.

## License
Proprietary/Internal (adjust as needed).
