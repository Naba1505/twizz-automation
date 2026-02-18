# Twizz Automation — Windows Setup & Run Guide

## Prerequisites

| Tool | Version | Check Command |
|------|---------|---------------|
| Java | 21 LTS (Eclipse Temurin) | `java -version` |
| Maven | 3.9+ | `mvn -version` |
| Git | Any recent | `git --version` |

---

## Step 1: Install Java 21

### Option A: Direct Download (Recommended)
1. Go to https://adoptium.net/temurin/releases/?version=21
2. Download the **Windows x64 .msi** installer
3. Run the installer — check **"Set JAVA_HOME variable"** and **"Add to PATH"** during installation

### Option B: Using Chocolatey
```powershell
choco install temurin21
```

### Option C: Using Winget
```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```

### Verify Java installation
Open a **new** PowerShell/Command Prompt window:
```powershell
java -version
# Expected: openjdk version "21.x.x"
```

### Set JAVA_HOME manually (only if not set by installer)
1. Open **System Properties** → **Environment Variables**
2. Add/Edit **JAVA_HOME** = `C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot` (adjust to your actual path)
3. Add `%JAVA_HOME%\bin` to **Path**
4. Restart your terminal

---

## Step 2: Install Maven

### Option A: Direct Download
1. Go to https://maven.apache.org/download.cgi
2. Download the **Binary zip archive** (e.g., `apache-maven-3.9.x-bin.zip`)
3. Extract to `C:\Program Files\Apache\maven` (or any folder)
4. Add to Environment Variables:
   - **M2_HOME** = `C:\Program Files\Apache\maven`
   - Add `%M2_HOME%\bin` to **Path**

### Option B: Using Chocolatey
```powershell
choco install maven
```

### Option C: Using Winget
```powershell
winget install Apache.Maven
```

### Verify Maven installation
Open a **new** PowerShell/Command Prompt:
```powershell
mvn -version
# Should show: Apache Maven 3.9.x and Java version: 21.x.x
```

---

## Step 3: Install Git

### Option A: Direct Download
1. Go to https://git-scm.com/download/win
2. Download and run the installer (use default settings)

### Option B: Using Chocolatey
```powershell
choco install git
```

### Verify Git installation
```powershell
git --version
```

---

## Step 4: Clone the Repository

```powershell
git clone https://github.com/Naba1505/twizz-automation.git
cd twizz-automation
```

---

## Step 5: Build the Project

```powershell
mvn clean install -DskipTests
```
Expected output: `BUILD SUCCESS`

---

## Step 6: Install Playwright Browsers (First Time Only)

```powershell
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
```
This downloads Chromium, Firefox, and WebKit browsers (~500MB). Only needed once.

---

## Step 7: Run Tests

### Run the Full Test Suite (Sequential)
```powershell
mvn test
```
This uses `testng.xml` and runs all tests in order:
1. Common Tests → 2. Creator Tests → 3. Admin Tests → 4. Fan Tests → 5. Cleanup Tests

### Run Full Suite in Parallel (4 threads)
```powershell
mvn test -Pparallel
```

### Run a Specific Test Class
```powershell
mvn test -Dtest=CreatorLoginTest
mvn test -Dtest=CreatorProfileTest
mvn test -Dtest=FanLoginTest
```

### Run a Specific Test Method
```powershell
mvn test -Dtest=CreatorPromotionsTest#testAddPromoCode
```

### Run Multiple Test Classes
```powershell
mvn test "-Dtest=CreatorRegistrationTest,AdminApproveCreatorTest,FanSubscriptionTest"
```

### Run Business Tests Only
```powershell
mvn test "-Dsurefire.suiteXmlFiles=business-testng.xml"
```

### Run in Headless Mode (No Browser Window)
```powershell
mvn test -Dheadless=true
```

### Run Against a Different Environment
```powershell
# Stage (default)
mvn test -Denvironment=stage

# Dev
mvn test -Denvironment=dev

# Prod
mvn test -Denvironment=prod
```

> **PowerShell Note:** If a `-D` parameter contains special characters, wrap it in quotes: `"-Dsurefire.suiteXmlFiles=testng.xml"`

---

## Step 8: View Test Reports (Allure)

### Install Allure CLI
```powershell
# Using Chocolatey
choco install allure

# Or using Scoop
scoop install allure
```

### Generate and Open Report
```powershell
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

```powershell
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
| `java is not recognized` | Install Java 21 and ensure `JAVA_HOME` is set and `%JAVA_HOME%\bin` is in PATH. Restart terminal. |
| `mvn is not recognized` | Install Maven and ensure `%M2_HOME%\bin` is in PATH. Restart terminal. |
| `Playwright browsers not found` | Run: `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"` |
| `allure is not recognized` | Install Allure: `choco install allure` or `scoop install allure`. Restart terminal. |
| Tests fail with timeout | Try headless mode: `mvn test -Dheadless=true` or increase timeout: `mvn test -Dtimeout.default=120000` |
| PowerShell `-D` parsing errors | Wrap parameters in quotes: `mvn test "-Dsurefire.suiteXmlFiles=testng.xml"` |
| `BUILD FAILURE` on compile | Verify Java 21: `java -version` and Maven: `mvn -version` show correct versions |
| IntelliJ asks Eclipse vs Maven | Always select **Maven project** when importing |

---

## IDE Setup (Optional)

### IntelliJ IDEA
1. Open IntelliJ → **File → Open** → select the `twizz-automation` folder
2. If prompted "Eclipse project or Maven project" → select **Maven project**
3. IntelliJ will auto-detect `pom.xml` and configure everything

### VS Code / Windsurf
1. Install the **Extension Pack for Java** (by Microsoft)
2. Open the `twizz-automation` folder
3. The Java Language Server will auto-detect `pom.xml`

---

## CI/CD

The project includes ready-to-use CI configs:
- **Jenkins**: `Jenkinsfile` (parameterized: build_only / sequential / parallel)
- **GitHub Actions**: `.github/workflows/ci.yml` (manual trigger)

Both use JDK 21 Temurin and produce Allure reports as artifacts.
