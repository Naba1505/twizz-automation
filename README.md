# Twizz Automation (Java + Playwright + TestNG)

End-to-end UI automation for Twizz with Creator Registration flow, robust waits, parallel-safe Playwright usage, and reporting via Allure (with Playwright traces and screenshots).

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
  - `LandingPage`, `CreatorRegistrationPage`, `CreatorLoginPage`, `CreatorPublicationPage`, `FanRegistrationPage`, `FanLoginPage`: Page Objects with robust waits and fallbacks.
  - `BaseTestClass`: Setup/teardown, screenshots, Allure/trace attachments.
- `src/test/java/tests/`
  - `LandingPageTest`, `CreatorRegistrationTest`, `FanRegistrationTest`, `CreatorLoginTest`, `CreatorPublicationTest`, `FanLoginTest`.
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
- Single test class:
  ```bash
  mvn test -Dtest=CreatorRegistrationTest
  ```
- If you hit parallel issues on some environments, switch `testng.xml` to `parallel="tests"` or run sequentially.

## Reports
  - Allure raw results: `target/allure-results/`
  - To view Allure report (after installing Allure CLI):
    ```bash
    allure serve target/allure-results
    ```

On failure, screenshots and Playwright trace ZIPs are attached to Allure. Optional success screenshots/trace export are configurable.

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

## License
Proprietary/Internal (adjust as needed).
