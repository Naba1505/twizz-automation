# Twizz Automation — Mac Setup & Run Guide

## Prerequisites

| Tool | Version | Check Command |
|------|---------|---------------|
| Java | 21 LTS (Eclipse Temurin) | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Git | Any recent | `git --version` |

---

## Step 1: Install Java 21

```bash
# Option A: Using Homebrew (recommended)
brew install --cask temurin@21

# Option B: Using SDKMAN (alternative)
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 21.0.9-tem
```

### Verify Java installation
```bash
java -version
# Expected output: openjdk version "21.x.x"
```

### Set JAVA_HOME (if not set automatically)
Add to `~/.zshrc` (or `~/.bash_profile` if using bash):
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH="$JAVA_HOME/bin:$PATH"
```
Then reload:
```bash
source ~/.zshrc
```

---

## Step 2: Install Maven

```bash
# Using Homebrew
brew install maven
```

### Verify Maven installation
```bash
mvn -version
# Should show: Apache Maven 3.9.x and Java version: 21.x.x
```

---

## Step 3: Clone the Repository

```bash
git clone https://github.com/Naba1505/twizz-automation.git
cd twizz-automation
```

---

## Step 4: Build the Project

```bash
mvn clean install -DskipTests
```
Expected output: `BUILD SUCCESS`

---

## Step 5: Install Playwright Browsers (First Time Only)

```bash
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```
This downloads Chromium, Firefox, and WebKit browsers (~500MB). Only needed once.

---

## Step 6: Run Tests

### Run the Full Test Suite (Sequential)
```bash
mvn test
```
This uses `testng.xml` and runs all tests in order:
1. Common Tests → 2. Creator Tests → 3. Admin Tests → 4. Fan Tests → 5. Cleanup Tests

### Run Full Suite in Parallel (4 threads)
```bash
mvn test -Pparallel
```

### Run a Specific Test Class
```bash
mvn test -Dtest=CreatorLoginTest
mvn test -Dtest=CreatorProfileTest
mvn test -Dtest=FanLoginTest
```

### Run a Specific Test Method
```bash
mvn test -Dtest=CreatorPromotionsTest#testAddPromoCode
```

### Run Multiple Test Classes
```bash
mvn test -Dtest="CreatorRegistrationTest,AdminApproveCreatorTest,FanSubscriptionTest"
```

### Run Business Tests Only
```bash
mvn test -Dsurefire.suiteXmlFiles=business-testng.xml
```

### Run in Headless Mode (No Browser Window)
```bash
mvn test -Dheadless=true
```

### Run Against a Different Environment
```bash
# Stage (default)
mvn test -Denvironment=stage

# Dev
mvn test -Denvironment=dev

# Prod
mvn test -Denvironment=prod
```

---

## Step 7: View Test Reports (Allure)

### Install Allure CLI
```bash
brew install allure
```

### Generate and Open Report
```bash
# Quick preview (starts a local web server)
allure serve target/allure-results

# Or generate static report
allure generate -c -o target/allure-report target/allure-results
allure open target/allure-report
```

### What's in the Report
- **Pass/Fail status** for every test
- **Screenshots** on failure (auto-captured)
- **Page HTML** on failure
- **Playwright Trace** on failure (downloadable ZIP — open at https://trace.playwright.dev)

---

## Test Suite Overview

| Module | Tests | What It Covers |
|--------|-------|----------------|
| **Creator** | 30+ | Registration, Login, Profile, Messaging, Scripts, Publications, Collections, Media Push, Live, Revenues, Promotions, Settings |
| **Fan** | 17+ | Registration, Login, Home, Bookmarks, Subscriptions (3DS), Messaging, Discover, Live Events, Settings |
| **Admin** | 1 | Creator approval on admin dashboard |
| **Business** | 11 | Manager/Employee signup, login, language, agency management (invite/accept/reject/delete) |
| **Cleanup** | 5 | Scripts cleanup, promo codes cleanup, quick files cleanup, collection cleanup, disable free subscription |

**Total: 138+ automated tests**

---

## Configuration

All settings are in `src/main/resources/config.properties`. Key settings:

| Setting | Default | Description |
|---------|---------|-------------|
| `environment` | `stage` | Target environment (dev/stage/prod) |
| `browser` | `chrome` | Browser type (chrome/firefox/webkit) |
| `headless` | `false` | Run without browser window |
| `retry.max` | `2` | Auto-retry failed tests |
| `timeout.default` | `60000` | Default wait timeout (ms) |

> **Note:** Any config property can be overridden via command line: `mvn test -Dbrowser=firefox -Dheadless=true`

---

## Quick Reference Commands

```bash
# Build only (no tests)
mvn clean install -DskipTests

# Run all tests
mvn test

# Run all tests in parallel
mvn test -Pparallel

# Run specific test
mvn test -Dtest=CreatorLoginTest

# Run headless
mvn test -Dheadless=true

# Run on Firefox
mvn test -Dbrowser=firefox

# View Allure report
allure serve target/allure-results

# Clean everything
mvn clean
```

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| `java: command not found` | Install Java 21: `brew install --cask temurin@21` |
| `mvn: command not found` | Install Maven: `brew install maven` |
| `Playwright browsers not found` | Run: `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"` |
| `allure: command not found` | Install Allure: `brew install allure` |
| Tests fail with timeout | Try headless mode: `mvn test -Dheadless=true` or increase timeout: `mvn test -Dtimeout.default=120000` |
| Browser crashes on Mac | Ensure Playwright browsers are installed and macOS permissions allow screen recording (System Preferences → Privacy → Screen Recording) |
| `BUILD FAILURE` on compile | Verify Java 21: `java -version` and Maven: `mvn -version` |

---

## CI/CD

The project includes ready-to-use CI configs:
- **Jenkins**: `Jenkinsfile` (parameterized: build_only / sequential / parallel)
- **GitHub Actions**: `.github/workflows/ci.yml` (manual trigger)

Both use JDK 21 Temurin and produce Allure reports as artifacts.
