# Twizz Automation

End-to-end UI automation framework for the **Twizz** platform — covering Creator, Fan, Admin, and Business application flows.

| Attribute | Detail |
|-----------|--------|
| **Language** | Java 21 LTS |
| **Browser Engine** | Playwright for Java 1.54.0 |
| **Test Runner** | TestNG 7.11.0 |
| **Reporting** | Allure TestNG 2.29.1 |
| **Logging** | SLF4J + Logback |
| **Build** | Maven 3.9+ / Surefire 3.2.5 |
| **Total Tests** | 138+ automated scenarios |

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
   - [macOS](#macos)
   - [Windows](#windows)
3. [Build & Install](#build--install)
4. [Running Tests](#running-tests)
5. [Test Reports (Allure)](#test-reports-allure)
6. [Project Structure](#project-structure)
7. [Test Suite Overview](#test-suite-overview)
8. [Configuration](#configuration)
9. [CI/CD](#cicd)
10. [IDE Setup](#ide-setup)
11. [Troubleshooting](#troubleshooting)
12. [Known Limitations](#known-limitations)
13. [License](#license)

---

## Prerequisites

| Tool | Version | Verify |
|------|---------|--------|
| Java | 21 LTS (Eclipse Temurin) | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Git | Any recent | `git --version` |

---

## Environment Setup

### macOS

#### 1. Install Java 21

```bash
# Option A: Homebrew (recommended)
brew install --cask temurin@21

# Option B: SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.9-tem
```

If `JAVA_HOME` is not set automatically, add to `~/.zshrc`:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```

Then run `source ~/.zshrc`.

#### 2. Install Maven

```bash
brew install maven
```

#### 3. Install Allure CLI (optional — for viewing reports)

```bash
brew install allure
```

---

### Windows

#### 1. Install Java 21

| Method | Command / Steps |
|--------|-----------------|
| **MSI Installer** (recommended) | Download from [adoptium.net](https://adoptium.net/temurin/releases/?version=21) — check **"Set JAVA_HOME"** and **"Add to PATH"** during install |
| **Chocolatey** | `choco install temurin21` |
| **Winget** | `winget install EclipseAdoptium.Temurin.21.JDK` |

> **Manual JAVA_HOME** (only if installer did not set it):
> System Properties → Environment Variables → `JAVA_HOME` = `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot` and add `%JAVA_HOME%\bin` to `Path`.

#### 2. Install Maven

| Method | Command / Steps |
|--------|-----------------|
| **Manual** | Download from [maven.apache.org](https://maven.apache.org/download.cgi), extract, set `M2_HOME` and add `%M2_HOME%\bin` to `Path` |
| **Chocolatey** | `choco install maven` |
| **Winget** | `winget install Apache.Maven` |

#### 3. Install Git

| Method | Command / Steps |
|--------|-----------------|
| **Installer** | Download from [git-scm.com](https://git-scm.com/download/win) |
| **Chocolatey** | `choco install git` |

#### 4. Install Allure CLI (optional — for viewing reports)

```powershell
choco install allure
# or
scoop install allure
```

> **PowerShell tip:** Always wrap `-D` Maven parameters in quotes to avoid parsing issues:
> `mvn test "-Dsurefire.suiteXmlFiles=testng.xml"`

---

## Build & Install

```bash
# 1. Clone the repository
git clone https://github.com/Naba1505/twizz-automation.git
cd twizz-automation

# 2. Build (skip tests)
mvn clean install -DskipTests

# 3. Install Playwright browsers (first time only, ~500 MB)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```

---

## Running Tests

### Full Suite

```bash
# Sequential (uses testng.xml)
mvn test

# Parallel (4 threads)
mvn test -Pparallel
```

### Specific Tests

```bash
# Single test class
mvn test -Dtest=CreatorLoginTest

# Single test method
mvn test -Dtest=CreatorPromotionsTest#testAddPromoCode

# Multiple classes
mvn test -Dtest="CreatorRegistrationTest,AdminApproveCreatorTest,FanSubscriptionTest"
```

### Suite XML Files

```bash
# Sequential (explicit)
mvn -B "-Dsurefire.suiteXmlFiles=testng.xml" test

# Parallel
mvn -B "-Dsurefire.suiteXmlFiles=testng-parallel.xml" test

# Business tests only
mvn test "-Dsurefire.suiteXmlFiles=business-testng.xml"
```

### Environment & Browser Options

```bash
# Headless mode
mvn test -Dheadless=true

# Different browser
mvn test -Dbrowser=firefox

# Different environment (dev / stage / prod)
mvn test -Denvironment=dev
```

> Any property from `config.properties` can be overridden via `-D` on the command line.

---

## Test Reports (Allure)

Results are written to `target/allure-results/`.

```bash
# Quick preview (starts local web server)
allure serve target/allure-results

# Generate static report
allure generate -c -o target/allure-report target/allure-results
allure open target/allure-report
```

> **Note**: Always use `target/allure-results` (not root-level `allure-results/`). The `target/` directory is automatically cleaned by `mvn clean`.

### Report Contents

| Artifact | When Captured |
|----------|---------------|
| **Environment Properties** | Always (auto-generated at suite start) |
| Screenshot (PNG) | On failure (always) |
| Page HTML | On failure |
| Playwright Trace (ZIP) | On failure (open at [trace.playwright.dev](https://trace.playwright.dev)) |
| Screenshot on success | Configurable via `screenshot.on.success` |
| Trace on success | Configurable via `trace.export.on.success` |

### Environment Information

The Allure report automatically includes environment details via `AllureEnvironmentWriter` listener:
- Environment (dev/stage/prod)
- Browser type and settings
- Java version and OS
- Playwright and TestNG versions
- Viewport size and timeouts
- Test executed by (automatically uses system username)

This information appears in the Allure report header under the **Environment** section.

> **Note**: The "Test Executed By" field automatically uses your system username. To override, set `-Dauthor="Your Name"` or uncomment `author` in `config.properties`.

---

## Project Structure

```
twizz-automation/
├── pom.xml
├── testng.xml                          # Sequential suite
├── testng-parallel.xml                 # Parallel suite (thread-count=4)
├── business-testng.xml                 # Business tests (sequential)
├── business-testng-parallel.xml        # Business tests (parallel)
├── Jenkinsfile                         # Jenkins CI pipeline
├── .github/workflows/ci.yml           # GitHub Actions CI
│
├── src/main/
│   ├── java/utils/
│   │   ├── ConfigReader.java           # Loads config.properties
│   │   ├── BrowserFactory.java         # ThreadLocal Playwright/Browser/Context/Page
│   │   ├── WaitUtils.java              # Wait helpers
│   │   ├── RetryAnalyzer.java          # Centralized retry with logging
│   │   └── AnnotationTransformer.java
│   └── resources/
│       └── config.properties           # All environment & test configuration
│
└── src/test/
    ├── java/
    │   ├── listeners/
    │   │   └── AllureEnvironmentWriter.java  # Auto-generates environment.properties
    │   │
    │   ├── pages/
    │   │   ├── common/                 # BasePage, BaseTestClass, LandingPage
    │   │   ├── creator/                # 30+ Creator page objects
    │   │   ├── fan/                    # 15+ Fan page objects
    │   │   ├── admin/                  # AdminCreatorApprovalPage
    │   │   └── business/
    │   │       ├── common/             # BusinessLandingPage, BusinessBaseTestClass
    │   │       ├── manager/            # Manager page objects
    │   │       └── employee/           # Employee page objects
    │   │
    │   └── tests/
    │       ├── common/                 # LandingPageTest
    │       ├── creator/                # 30+ Creator test classes
    │       ├── fan/                    # 15+ Fan test classes
    │       ├── admin/                  # AdminApproveCreatorTest
    │       └── business/
    │           ├── common/             # BusinessLandingPageTest
    │           ├── manager/            # Manager test classes
    │           └── employee/           # Employee test classes
    │
    └── resources/
        ├── Images/                     # Test image assets
        ├── Videos/                     # Test video assets
        └── Audios/                     # Test audio assets
```

---

## Test Suite Overview

The full suite runs in the following order (via `testng.xml`):

**1. Common Tests** → **2. Creator Tests** → **3. Admin Tests** → **4. Fan Tests** → **5. Cleanup Tests**

### Module Breakdown

| Module | Tests | Coverage |
|--------|------:|----------|
| **Creator** | 30+ | Registration, Login, Profile, Messaging (13 scenarios), Scripts (12 scenarios), Publications, Collections, Quick Files, Media Push (19 scenarios), Live, Revenues, Promotions, Payment Method, Presentation Videos, Settings (Language, Legal, Help, Logout, etc.) |
| **Fan** | 17+ | Registration, Login, Home Screen, Discover, Bookmarks, Subscription (3DS payment), Free Subscription (3 flows), Messaging (4 scenarios), Live Events (instant + scheduled), Settings (Language, Personal Info, Email Notifications, Terms, Help, Bug Report, Logout, Clear Search, My Creators) |
| **Admin** | 1 | Creator approval on admin dashboard |
| **Business** | 11 | Landing page, Manager signup/login/language/agency management (invite, accept, reject, delete for both creators and employees), Employee signup/login |
| **Cleanup** | 5+ | Scripts cleanup, Promo codes cleanup, Quick Files cleanup, Collection cleanup, Disable free subscription |

### Key Technical Features

- **Configuration validation** — Automatic validation of all properties at startup with clear error messages
- **Dual browser context** — Fan + Creator isolation for messaging and live event tests
- **3DS payment handling** — SecurionPay test gateway integration with resilient selectors
- **FileChooser / input[type="file"]** — No native OS dialog popups during media uploads
- **ThreadLocal browsers** — Safe parallel execution via `BrowserFactory`
- **Automatic retry** — Configurable via `RetryAnalyzer` (default: 2 retries)
- **Allure step annotations** — Every page object method is annotated for trace-level reporting
- **Environment properties** — Auto-generated environment details in Allure reports

---

## Configuration

All settings live in `src/main/resources/config.properties`.

### Configuration Validation

The framework automatically validates all configuration properties at startup:
- **Environment** must be `dev`, `stage`, or `prod`
- **Browser** must be valid (chrome, firefox, webkit, etc.)
- **Timeouts** must be positive integers
- **URLs** must be well-formed and start with http:// or https://
- **Booleans** must be `true` or `false`
- **Viewport** dimensions must be in reasonable ranges

If validation fails, you'll get a clear error message showing exactly what's wrong:
```
═══════════════════════════════════════════════════════════════
  CONFIGURATION VALIDATION FAILED
═══════════════════════════════════════════════════════════════

Found 2 error(s) in config.properties:

1. Property 'environment' has invalid value 'staging'. Must be one of: [dev, stage, prod]
2. Property 'timeout.default' must be a positive integer (> 0), found: -5000

Please fix these errors in src/main/resources/config.properties
and try again.
═══════════════════════════════════════════════════════════════
```

### Key Configuration Properties

### Environment

| Property | Default | Description |
|----------|---------|-------------|
| `environment` | `stage` | Target environment (`dev` / `stage` / `prod`) |
| `url.dev` | — | Dev base URL |
| `url.stage` | — | Stage base URL |
| `url.prod` | — | Prod base URL |

### Browser

| Property | Default | Description |
|----------|---------|-------------|
| `browser` | `chrome` | Browser type (`chrome` / `firefox` / `webkit`) |
| `headless` | `false` | Run without browser window |
| `viewport.width` | `1280` | Browser viewport width |
| `viewport.height` | `720` | Browser viewport height |

### Timeouts & Retries

| Property | Default | Description |
|----------|---------|-------------|
| `timeout.default` | `60000` | Default wait timeout (ms) |
| `retry.max` | `2` | Auto-retry failed tests |
| `retry.delay.ms` | `0` | Delay between retries |

### Reporting

| Property | Default | Description |
|----------|---------|-------------|
| `screenshot.dir` | `screenshots` | Screenshot output directory |
| `screenshot.on.success` | `false` | Capture screenshots on pass |
| `trace.enable` | `true` | Enable Playwright tracing |
| `trace.dir` | `traces` | Trace output directory |
| `trace.export.on.success` | `false` | Export traces on pass |

### Test Data

| Property | Default | Description |
|----------|---------|-------------|
| `registration.dob` | `15-05-1995` | Date of birth for registration |
| `registration.gender` | `Male` | Gender for registration |
| `registration.subscriptionPrice` | `5` | Subscription price |
| `payment.card.number` | `4012 0018 0000 0016` | Test payment card |
| `payment.card.expiry` | `07/34` | Card expiry |
| `payment.card.cvc` | `657` | Card CVC |

---

## CI/CD

The project includes ready-to-use CI configurations:

| Platform | File | Features |
|----------|------|----------|
| **Jenkins** | `Jenkinsfile` | Parameterized pipeline (build_only / sequential / parallel), Allure report publishing |
| **GitHub Actions** | `.github/workflows/ci.yml` | Manual trigger, JDK 21 Temurin, Allure results as artifacts |

Both pipelines use **JDK 21 Temurin** and produce **Allure reports** as build artifacts.

---

## IDE Setup

### IntelliJ IDEA

1. **File** → **Open** → select the `twizz-automation` folder
2. If prompted "Eclipse project or Maven project" → select **Maven project**
3. IntelliJ auto-detects `pom.xml` and configures everything

### VS Code / Windsurf

1. Install the **Extension Pack for Java** (by Microsoft)
2. Open the `twizz-automation` folder
3. The Java Language Server auto-detects `pom.xml`

---

## Troubleshooting

| Issue | Platform | Solution |
|-------|----------|----------|
| `java: command not found` / `java is not recognized` | All | Install Java 21 and ensure `JAVA_HOME` is set. Restart terminal. |
| `mvn: command not found` / `mvn is not recognized` | All | Install Maven and ensure it is on `PATH`. Restart terminal. |
| Playwright browsers not found | All | Run: `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"` |
| `allure: command not found` | macOS | `brew install allure` |
| `allure is not recognized` | Windows | `choco install allure` or `scoop install allure`. Restart terminal. |
| Tests fail with timeout | All | Try headless: `mvn test -Dheadless=true` or increase timeout: `mvn test -Dtimeout.default=120000` |
| PowerShell `-D` parsing errors | Windows | Wrap parameters in quotes: `mvn test "-Dsurefire.suiteXmlFiles=testng.xml"` |
| `BUILD FAILURE` on compile | All | Verify `java -version` shows 21.x and `mvn -version` shows 3.9+ |
| Browser crashes on Mac | macOS | Ensure macOS permissions: System Preferences → Privacy → Screen Recording |
| IntelliJ asks Eclipse vs Maven | Windows | Always select **Maven project** when importing |
| Flaky network/visibility | All | Run sequentially (`thread-count=1`) to confirm stability, then re-enable parallel |
| Report is empty | All | Confirm tests ran and `target/allure-results` contains `.json` files |

### Housekeeping

Git ignores common transient artifacts:
- `target/` (includes `allure-results/`, compiled classes, etc.)
- `traces/`, `screenshots/`, `playwright-report/`, `test-output/`

**Clean everything**: Run `mvn clean` to remove all generated files including test results.

---

## Known Limitations

### Drag-and-Drop Automation

**Test:** `CreatorScriptsTest.creatorCanChangeScriptOrder()` — **Status: Disabled**

The drag-and-drop library used in the application does not respond to Playwright's `dragTo()`, manual mouse operations, or JS event simulation. The test remains in the codebase (disabled) to document the expected flow and can be re-enabled when a compatible solution is found.

---

## License

Proprietary / Internal.
