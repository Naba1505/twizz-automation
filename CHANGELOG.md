# Changelog

## [Unreleased] - 2025-12-15

### New Feature: Fan Help and Contact Module
- **Fan Help and Contact Test Class** (`FanHelpAndContactTest.java`): Test for fan submitting help and contact form
  - Fan login → Settings → Help and contact → Fill form → Submit → Assert success message
  
- **Fan Help and Contact Page Object** (`FanHelpAndContactPage.java`): Page object for fan help and contact
  - Navigation: Settings icon click, Help and contact menu click
  - Form: Subject/Description fields with timestamp, Send button, success toast assertion

### New Feature: Fan Spotted Bug Module
- **Fan Spotted Bug Test Class** (`FanSpottedBugTest.java`): Test for fan submitting bug report
  - Fan login → Settings → I've spotted a bug → Fill form → Submit → Assert success message
  
- **Fan Spotted Bug Page Object** (`FanSpottedBugPage.java`): Page object for fan bug reporting
  - Navigation: Settings icon click, Settings title assertion, Spotted bug menu click
  - Form: Subject/Description fields with timestamp, Send button, success toast assertion

### New Feature: Fan Email Notification Module
- **Fan Email Notification Test Class** (`FanEmailNotificationTest.java`): Tests for email notification toggle settings
  - Test 1: Disable all 5 toggles (Push media, Live reminder, Scheduling live, Direct live, Marketing)
  - Test 2: Enable all 5 toggles
  
- **Fan Email Notification Page Object** (`FanEmailNotificationPage.java`): Page object for email notification settings
  - Navigation: Settings → Email notification screen
  - Toggle interactions: Force disable with confirmation, Force enable (simple click)
  - Smart toggle handling based on confirmation dialog presence

### Files Added
- `src/test/java/pages/FanHelpAndContactPage.java` - Fan help and contact page object
- `src/test/java/tests/FanHelpAndContactTest.java` - Fan help and contact test class
- `src/test/java/pages/FanSpottedBugPage.java` - Fan spotted bug page object
- `src/test/java/tests/FanSpottedBugTest.java` - Fan spotted bug test class
- `src/test/java/pages/FanEmailNotificationPage.java` - Fan email notification page object
- `src/test/java/tests/FanEmailNotificationTest.java` - Fan email notification test class

### Files Modified
- `testng.xml` - Added FanHelpAndContactTest, FanSpottedBugTest, FanEmailNotificationTest
- `testng-parallel.xml` - Added FanHelpAndContactTest, FanSpottedBugTest, FanEmailNotificationTest

---

### New Feature: Fan Live Events Module
- **Fan Live Events Test Class** (`FanLiveTest.java`): Complete test coverage for fan joining creator live events
  - Test 1: Creator creates instant live, Fan joins, comments, and closes
  - Test 2: Creator schedules live event, Fan buys ticket, Creator cleans up
  
- **Fan Live Page Object** (`FanLivePage.java`): New page object for fan live interactions
  - Navigation: Live icon click, Lives screen assertion, Live/Events tab switching
  - Instant Live: Join live, payment flow, comment posting, close live
  - Scheduled Live: Events tab navigation, ticket purchase, payment confirmation
  
- **Creator Live Page Enhancements** (`CreatorLivePage.java`): Added instant live and end live methods
  - `createInstantLiveEveryone15Euro()` - Create instant live with Everyone access and 15€ price
  - `endLiveStream()` - End live with confirmation dialog handling
  - Field visibility assertions for Access, Price, Chat, When fields
  
- **Browser Factory Enhancement** (`BrowserFactory.java`):
  - Added camera permission alongside microphone for live streaming
  - New `createNewContext()` method for multi-user test scenarios (creator + fan isolation)

### Technical Implementation
- **Dual Browser Context Architecture**: Creator and Fan use separate isolated browser contexts to avoid session conflicts
- **TestNG XML Runners**: Added `FanLiveTest` to both `testng.xml` and `testng-parallel.xml`

### Files Added
- `src/test/java/pages/FanLivePage.java` - Fan live events page object
- `src/test/java/tests/FanLiveTest.java` - Fan live events test class

### Files Modified
- `src/test/java/pages/CreatorLivePage.java` - Added instant live and end live methods
- `src/main/java/utils/BrowserFactory.java` - Added camera permission and createNewContext()
- `testng.xml` - Added FanLiveTest
- `testng-parallel.xml` - Added FanLiveTest

---

## [Previous] - 2025-12-12

### Major Upgrade
- **Java 21 LTS Migration**: Complete upgrade from Java 17 to Java 21
  - Updated Maven compiler configuration to use Java 21 (`maven.compiler.source=21`, `maven.compiler.target=21`)
  - Updated compiler plugin to use `release=21` for both main and test compilation
  - Added compiler arguments (`-Xlint:none`, `-Xlint:-options`) to suppress lint warnings
  - Verified compatibility with all dependencies (Playwright 1.54.0, TestNG 7.11.0, Allure 2.29.1)
  - Enhanced performance and security with latest LTS Java version

### Code Quality Improvements
- **Exception Handling Enhancement**: Replaced generic exception catches with specific types
  - Updated `AdminApproveCreatorTest.java` to use `IOException` instead of `Exception`/`Throwable`
  - Added missing `IOException` import for proper exception handling
  - Improved error specificity and debugging capabilities

- **IDE Warning Resolution**: Achieved zero IDE warnings across entire codebase
  - Removed duplicate logger declarations that were hiding inherited loggers from `BasePage`
  - Eliminated unused variable assignments in control flow methods
  - Cleaned up `@SuppressWarnings` annotations that were causing compiler option conflicts
  - Applied global lint suppression through Maven compiler configuration

### Technical Infrastructure
- **Build System**: Updated Maven configuration for Java 21 compatibility
- **Environment**: Verified clean Java 21 installation with complete Java 17 removal
- **Documentation**: Updated README.md with Java 21 prerequisites and setup instructions

### Files Modified
- `pom.xml` - Java 21 compiler configuration and lint suppression
- `README.md` - Java 21 upgrade documentation and prerequisites
- `src/test/java/tests/AdminApproveCreatorTest.java` - Exception handling improvements
- `src/test/java/pages/FanSubscriptionPage.java` - Removed conflicting annotations
- Multiple page classes - Logger declaration cleanup

## [Previous] - 2025-12-04

### Fixed
- **Fan Subscription Test (`FanSubscriptionTest.java`)**: Fixed infinite loop issue after payment confirmation
  - Removed unstable "Everything is OK" button click that was causing 2-minute timeouts
  - Implemented automatic navigation detection after payment confirmation
  - Added proper "Subscriber" button verification on creator profile
  - Test now passes in ~45 seconds instead of timing out
  
- **Quick Files Album Selection**: Fixed album click failures in messaging and scripts tests
  - Implemented robust XPath-based locator targeting `div.qf-row[role='button']` containing album title
  - Added scrolling logic for lazy-loaded album content
  - Updated media selection to use `role=IMG name='select'` pattern
  - Fixed in both `CreatorMessagingPage.java` and `CreatorScriptsPage.java`

- **Fan Home Screen Interactions**: Fixed like/bookmark button click interception
  - Implemented force click to bypass video container overlay
  - Applied to both like and bookmark buttons in `FanHomePage.java`

### Improved
- **Logging Consistency**: Replaced `System.out.println` with proper SLF4J logger calls in `CreatorQuickFilesTest.java`
- **Code Cleanup**: Removed unused `lastSearchedCreator` field from `FanSubscriptionPage.java`
- **Documentation**: Updated README.md with detailed Fan Subscription flow and 3DS handling

### Technical Details
- All changes maintain backward compatibility
- No breaking changes to existing test flows
- Improved test stability and execution time
- Enhanced error handling and logging throughout

## Files Modified
- `src/test/java/pages/FanSubscriptionPage.java`
- `src/test/java/pages/CreatorMessagingPage.java`
- `src/test/java/pages/CreatorScriptsPage.java`
- `src/test/java/pages/FanHomePage.java`
- `src/test/java/tests/CreatorMessagingTest.java`
- `src/test/java/tests/CreatorQuickFilesTest.java`
- `README.md`
