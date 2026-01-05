# Changelog

## [Unreleased] - 2026-01-01

### New Feature: Business Manager Add Employee (Invite) - Extended
- **BusinessManagerAddEmployeeTest**: Seven test scenarios for complete invite/accept/reject/view flow
  - **Test 1 - Invite Employee from Agency** (priority 1):
    * Login as Manager and navigate to dashboard
    * Click on Agency icon and verify agency screen
    * Verify 'Your employees' section is visible
    * Click Add button to open invite employee dialog
    * Search for employee by username
    * Select employee checkbox
    * Send invitation and verify success message
    * Click 'I understand' button if present
  - **Test 2 - Duplicate Invitation from Agency** (priority 2):
    * Login as Manager and navigate to Agency
    * Attempt to invite same employee again
    * Verify 'there is an invitation already sent' message appears
    * No 'I understand' button in duplicate scenario
  - **Test 3 - Employee Reject Invitation** (priority 3):
    * Set viewport to mobile size (375x667) for proper UI display
    * Login as Employee and navigate to dashboard
    * Click on Settings button
    * Navigate to View invitations
    * Verify invitation is visible
    * Click Decline button
    * Confirm rejection with 'Finish' button
    * Verify 'Rejected' success message
  - **Test 4 - Invite Employee from Settings** (priority 4):
    * Login as Manager and navigate to dashboard
    * Click on Settings icon
    * Click on 'Employee Go' button
    * Verify 'Your employees' text is visible
    * Click 'Invite a employee' text
    * Search for employee by username
    * Select employee checkbox
    * Send invitation and verify success message
    * Click 'I understand' button
  - **Test 5 - Duplicate Invitation from Settings** (priority 5):
    * Login as Manager and navigate to Settings
    * Navigate to Employee Go → Invite a employee
    * Attempt to invite same employee again
    * Verify 'there is an invitation' message appears
  - **Test 6 - Employee Accept Invitation** (priority 6):
    * Set viewport to mobile size (375x667)
    * Login as Employee and navigate to dashboard
    * Click on Settings button
    * Navigate to View invitations
    * Verify invitation is visible
    * Click Accept button
    * Confirm acceptance with 'Finish' button
    * Verify 'Invitation accepted' success message
  - **Test 7 - View Added Employee** (priority 7):
    * Login as Manager and navigate to dashboard
    * Click on Agency icon
    * Verify 'Your employees' section is visible
    * Click on employee card
    * Verify 'Twizz identity Card' heading (confirms employee is added)

- **BusinessManagerAddEmployeePage**: Page object with agency employee management methods
  - Agency icon navigation
  - Add employee button interaction
  - Employee search functionality
  - Checkbox selection for employee
  - Invitation sending with success verification
  - Duplicate invitation message detection
  - Optional 'I understand' button handling
  - Employee card click for viewing details
  - Twizz identity card verification

- **BusinessManagerSettingsPage**: Page object for settings screen employee invitation
  - Settings icon navigation
  - Employee Go button interaction
  - Your employees text verification
  - Invite a employee text click
  - Employee search functionality
  - Checkbox selection
  - Send invitation button
  - Success and duplicate message verification
  - I understand button handling

- **EmployeeSettingsPage**: Page object for employee settings and invitation management
  - Settings button navigation
  - Manage relationships verification
  - View invitations interaction
  - Decline button interaction (for rejection)
  - Accept button interaction (for acceptance)
  - Confirmation dialog handling
  - Rejection and acceptance success message verification

### New Feature: Business Manager Delete Employee (Cleanup)
- **BusinessManagerDeleteEmployeeTest**: Cleanup test to remove test data
  - **Test 1 - Delete Employee** (priority 1):
    * Login as Manager and navigate to dashboard
    * Click on Agency icon
    * Verify 'Your employees' text is visible
    * Click on employee card to view details
    * Verify 'Twizz identity Card' heading
    * Click 'Delete this account' text
    * Verify delete confirmation dialog
    * Click 'Validate' button to confirm deletion
    * Verify 'Employee deleted successfully' message

- **BusinessManagerDeleteEmployeePage**: Page object for delete employee flow
  - Agency icon navigation
  - Your employees text verification
  - Employee card click
  - Twizz identity card verification
  - Delete this account text click
  - Delete confirmation dialog verification
  - Validate button interaction
  - Success message verification

### New Feature: Business Manager Add Creator (Invite) - Extended
- **BusinessManagerAddCreatorTest**: Seven test scenarios for complete invite/accept/reject/view flow
  - **Test 1 - Invite Creator from Agency** (priority 1):
    * Login as Manager and navigate to dashboard
    * Click on Agency icon and verify agency screen
    * Click Add button to open invite creator dialog
    * Search for creator by username
    * Select creator checkbox
    * Send invitation and verify success message
    * Click 'I understand' button if present
  - **Test 2 - Duplicate Invitation** (priority 2):
    * Login as Manager and navigate to Agency
    * Attempt to invite same creator again
    * Verify 'there is an invitation already sent' message appears
    * No 'I understand' button in duplicate scenario
  - **Test 3 - Creator Reject Invitation** (priority 3):
    * Login as Creator and navigate to profile
    * Click on settings icon
    * Navigate to Manager menu
    * Verify invitation is visible
    * Click Refuse button
    * Confirm rejection with 'I refuse' button
    * Verify 'Invitation rejected' success message
  - **Test 4 - Invite Creator from Settings** (priority 4):
    * Login as Manager and navigate to dashboard
    * Click on Settings icon
    * Click on 'Creator Go' button
    * Verify 'Your creators' text is visible
    * Click 'Invite a creator' text
    * Search for creator by username
    * Select creator checkbox
    * Send invitation and verify success message
    * Click 'I understand' button
  - **Test 5 - Duplicate Invitation from Settings** (priority 5):
    * Login as Manager and navigate to Settings
    * Navigate to Creator Go → Invite a creator
    * Attempt to invite same creator again
    * Verify 'there is an invitation' message appears
  - **Test 6 - Creator Accept Invitation** (priority 6):
    * Login as Creator and navigate to profile
    * Click on settings icon
    * Navigate to Manager menu
    * Verify invitation is visible
    * Click Accept button
    * Confirm acceptance with 'I accept' button
    * Verify 'Invitation accepted' success message
  - **Test 7 - View Added Creator** (priority 7):
    * Login as Manager and navigate to dashboard
    * Click on Agency icon
    * Verify 'Your creators' section is visible
    * Click on creator card
    * Verify 'Twizz identity Card' heading (confirms creator is added)

- **BusinessManagerAddCreatorPage**: Page object with agency management methods
  - Agency icon navigation
  - Add creator button interaction
  - Creator search functionality
  - Checkbox selection for creator
  - Invitation sending with success verification
  - Duplicate invitation message detection
  - Optional 'I understand' button handling
  - Creator card click for viewing details
  - Twizz identity card verification

- **BusinessManagerSettingsPage**: Page object for settings screen creator invitation
  - Settings icon navigation
  - Creator Go button interaction
  - Your creators text verification
  - Invite a creator text click
  - Creator search functionality
  - Checkbox selection
  - Send invitation button
  - Success and duplicate message verification
  - I understand button handling

- **CreatorManagerPage**: Page object for creator manager settings
  - Settings icon navigation
  - Manager menu item interaction
  - Invitation visibility verification
  - Refuse button interaction (for rejection)
  - Accept button interaction (for acceptance)
  - Confirmation dialog handling
  - Rejection and acceptance success message verification

### New Feature: Business Manager Delete Creator (Cleanup)
- **BusinessManagerDeleteCreatorTest**: Cleanup test to remove test data
  - **Test 1 - Delete Creator** (priority 1):
    * Login as Manager and navigate to dashboard
    * Click on Agency icon
    * Verify 'Your creators' text is visible
    * Click on creator card to view details
    * Verify 'Twizz identity Card' heading
    * Click 'Delete the creator' text
    * Verify delete confirmation dialog
    * Click 'Validate' button to confirm deletion
    * Verify 'Creator deleted successfully' message

- **BusinessManagerDeleteCreatorPage**: Page object for delete creator flow
  - Agency icon navigation
  - Your creators text verification
  - Creator card click
  - Twizz identity card verification
  - Delete the creator text click
  - Delete confirmation dialog verification
  - Validate button interaction
  - Success message verification

### New Feature: Business Employee Login
- **BusinessEmployeeLoginTest**: Complete employee login flow
  - Navigate to sign-in page and verify login page heading
  - Fill credentials from config.properties (username, password)
  - Click Login button and wait for navigation
  - Verify employee dashboard URL contains '/employee'
  - Verify welcome message 'Hello {displayname}' is visible

- **BusinessEmployeeLoginPage**: Page object with login methods
  - Form filling methods for username and password
  - URL validation for employee dashboard navigation
  - Welcome message verification with dynamic display name
  - Complete login flow method for reusability

- **Config Properties**: Added employee credentials
  - `business.employee.username=TwizzEmployee@proton.me`
  - `business.employee.password=Twizz$123`
  - `business.employee.displayname=Scarlett`

### New Feature: Business Manager Login
- **BusinessManagerLoginTest**: Complete manager login flow
  - Navigate to sign-in page and verify login page heading
  - Fill credentials from config.properties (username, password)
  - Click Login button and wait for navigation
  - Verify manager dashboard URL contains '/manager'
  - Verify welcome message 'Hello {displayname}' is visible

- **BusinessManagerLoginPage**: Page object with login methods
  - Form filling methods for username and password
  - URL validation for manager dashboard navigation
  - Welcome message verification with dynamic display name
  - Complete login flow method for reusability

- **Config Properties**: Added manager credentials
  - `business.manager.username=TwizzManager@proton.me`
  - `business.manager.password=Twizz$123`
  - `business.manager.displayname=David`

### New Feature: Business Employee Sign Up
- **BusinessEmployeeSignUpTest**: Complete employee registration flow
  - 2-page registration process (Basic Info, Password/Phone)
  - Employee tab selected by default verification
  - Unique username/email generation with timestamp
  - Direct navigation to employee dashboard after registration
  - Agency avatar visibility verification on dashboard

- **BusinessEmployeeSignUpPage**: Page object with 15+ methods
  - Form filling methods for employee registration fields
  - URL validation for employee dashboard navigation
  - Simpler flow compared to Manager (no agency details or documents)

### New Feature: Business Manager Sign Up
- **Folder Structure**: Organized Business tests into common/manager/employee subfolders
  - `pages/business/common/` - Shared page objects (BusinessLandingPage, BusinessBaseTestClass)
  - `tests/business/common/` - Common test classes (BusinessLandingPageTest)
  - `pages/business/manager/` - Manager-specific page objects
  - `tests/business/manager/` - Manager-specific test classes
  - `pages/business/employee/` - Employee-specific page objects
  - `tests/business/employee/` - Employee-specific test classes

- **TestNG XML Organization**: Separated Business tests into distinct test groups
  - Business Common Tests (landing page)
  - Business Manager Tests (manager sign up, manager login, add creator)
  - Business Employee Tests (employee sign up, employee login)
  - Removed Business tests from main testng.xml (use business-testng.xml instead)

- **BusinessManagerSignUpTest**: Complete manager registration flow
  - 4-page registration process (Basic Info, Agency Details, Status, Identity Verification)
  - Unique username/email generation with timestamp
  - FileChooser integration for profile image upload
  - Direct file input for identity document uploads
  - Success message verification and navigation back to sign-in

- **BusinessManagerSignUpPage**: Page object with 20+ methods
  - Form filling methods for all registration fields
  - File upload methods with FileChooser fallback
  - Page navigation and verification methods
  - Timestamp-based unique data generation

### Previous - 2025-12-30

### New Feature: Business Landing Page Complete Test Coverage
- **BusinessLandingPageTest**: Three comprehensive test scenarios
  1. **testBusinessLandingPage**: Landing page navigation (logo, heading, Contact Us, Login, Register)
  2. **testBusinessLandingPageFooterLinks**: Footer links navigation (Contact Us, Legal notices, Content Policy, Confidentiality, General Conditions of Sale/Use, Blog)
  3. **testBusinessLandingPageLanguageSwitch**: Multi-language support testing (English ↔ Français ↔ Español)

- **BusinessLandingPage**: Enhanced page object with 20+ methods
  - Footer navigation methods for all legal/policy pages
  - Language switching with dropdown interaction
  - Scroll and verification methods for each page section
  - Robust locators using Playwright's filter and nth() methods

### Bug Fix: Media Upload OS Dialog Prevention
- **Fixed**: OS file dialog appearing during automated media uploads
  - Implemented `FileChooser` interception in `CreatorMessagingPage`
  - Updated `uploadMessageMedia()` and `uploadMediaFile()` methods
  - Added `dismissImportationModal()` to close modal after upload
  - Enhanced `clickSendButtonForMedia()` with overlay dismissal
  - All media upload tests now run without OS dialog interruption

### Bug Fix: FanMessagingTest Accept Button
- **Fixed**: Accept button click failures when run after CreatorMessagingTest
  - Changed from `.first()` to `.last()` to click most recent Accept button
  - Updated `acceptMediaButton()` in FanMessagingPage
  - Updated `clickAcceptButtonForMessage()` in CreatorMessagingPage

### Bug Fix: FanBookmarksTest
- **Fixed**: `fanCanUnbookmarkFeeds` failure when no bookmarks exist
  - Added skip logic when initial bookmark count is 0
  - Adjusted unbookmark count to handle available bookmarks

### Test Suite Status
- **138 tests passing** (100% pass rate)
- **0 failures**
- **0 errors**

## [Previous] - 2025-12-23

### Major Refactoring: Project Structure Reorganization
- **Modular Folder Structure**: Reorganized all page objects and test classes into logical modules:
  - `pages/creator/` - All Creator-related page objects
  - `pages/fan/` - All Fan-related page objects
  - `pages/admin/` - Admin page objects (AdminCreatorApprovalPage)
  - `pages/common/` - Shared page objects (BasePage, BaseTestClass, LandingPage)
  - `pages/business/` - New Business application page objects
  - `tests/creator/`, `tests/fan/`, `tests/admin/`, `tests/common/`, `tests/business/` - Corresponding test classes

### New Feature: Twizz Business App Integration
- **Business Landing Page Test** (`BusinessLandingPageTest.java`): Initial test coverage for Twizz Business App
  - Verify Twizz Business logo displayed
  - Verify "Designed for managers" heading
  - Navigate to Contact Us page and verify heading
  - Navigate to Login page and verify "Connection" heading
  - Navigate to Register page and verify "Inscription" heading
  - Switch between Employee and Manager registration tabs
  
- **Business Landing Page Object** (`BusinessLandingPage.java`): Page object for Business landing page
  - Navigation methods for Contact Us, Login, Register
  - Page verification methods for each screen
  - Manager/Employee tab switching
  
- **Business Base Test Class** (`BusinessBaseTestClass.java`): Base class for Business tests
  - Separate setup/teardown for Business application
  - Screenshot and trace capture on failure

- **Configuration Updates** (`config.properties`, `ConfigReader.java`):
  - Added Business URLs for dev/stage/prod environments
  - Added getter methods for Business landing, login, and register URLs

### New TestNG XML Runners
- **business-testng.xml**: Sequential execution of Business tests
- **business-testng-parallel.xml**: Parallel execution of Business tests (thread-count=4)
- Updated `testng.xml` and `testng-parallel.xml` with new package paths and organized test groups

### Bug Fix: FanBookmarksTest Flaky Test
- **Fixed** `fanCanBookmarkMultipleFeeds` intermittent failures
  - Changed locator from role-based `getByRole(AriaRole.IMG, name="bookmark")` to exact CSS selector `img[alt='bookmark']`
  - Added `scrollIntoViewIfNeeded()` before clicking bookmark icons
  - Added verification after each click to confirm bookmark state changed
  - Added retry logic with scrolling in `verifyAllBookmarksHighlighted()`

### Files Added
- `src/test/java/pages/business/BusinessLandingPage.java`
- `src/test/java/pages/business/BusinessBaseTestClass.java`
- `src/test/java/tests/business/BusinessLandingPageTest.java`
- `business-testng.xml`
- `business-testng-parallel.xml`

### Files Modified
- All page objects moved to `pages/{creator,fan,admin,common,business}/` with updated package declarations
- All test classes moved to `tests/{creator,fan,admin,common,business}/` with updated package declarations
- `testng.xml` - Updated with new package paths, organized into Common/Creator/Admin/Fan test groups
- `testng-parallel.xml` - Updated with new package paths
- `src/main/resources/config.properties` - Added Business URLs
- `src/main/java/utils/ConfigReader.java` - Added Business URL getters
- `src/main/java/utils/WaitUtils.java` - Moved from test to main for proper accessibility
- `src/test/java/pages/fan/FanBookmarksPage.java` - Fixed flaky bookmark test

---

## [Previous] - 2025-12-15

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

### New Feature: Fan Terms and Policies Module
- **Fan Terms and Policies Test Class** (`FanTermsAndPoliciesTest.java`): Single test verifying all 3 policy pages
  - Terms and Conditions of Sale: Navigate, scroll to end, scroll back, return
  - Community Regulations: Navigate, scroll to end, scroll back, return
  - Content Policy: Navigate, scroll to end, scroll back, return
  
- **Fan Terms and Policies Page Object** (`FanTermsAndPoliciesPage.java`): Page object for policy verification
  - Navigation: Settings → Each policy screen
  - Scroll verification: Scroll to end text, scroll back to title
  - Back navigation: Arrow left click to return to Settings

### New Feature: Fan Language Module
- **Fan Language Test Class** (`FanLanguageTest.java`): Single test switching through all available languages
  - English → French → Spanish → English cycle
  - Verifies title changes according to selected language
  
- **Fan Language Page Object** (`FanLanguagePage.java`): Page object for language switching
  - Navigation: Settings → Language screen
  - Language selection: English, Français, Español with checkbox verification
  - Title assertions in each language (Language/Langue/Idioma)

### New Feature: Fan Personal Information Module
- **Fan Personal Info Test Class** (`FanPersonalInfoTest.java`): Single test verifying and updating personal info
  - Verifies all fields visible (Identity, User name, Date of birth, Account type)
  - Verifies locked fields with lock icons
  - Updates email and phone number
  - Verifies success message after save
  
- **Fan Personal Info Page Object** (`FanPersonalInfoPage.java`): Page object for personal information
  - Field verification: Identity, User name, Date of birth, Account type
  - Lock icon verification for non-editable fields
  - Email and phone number update methods
  - Register button and success message verification

### New Feature: Fan Logout Module
- **Fan Logout Test Class** (`FanLogoutTest.java`): Single test for fan logout functionality
  - Fan login → Settings → Click Disconnect → Verify Login page
  
- **Fan Logout Page Object** (`FanLogoutPage.java`): Page object for logout
  - Navigation: Settings → Disconnect button
  - Verification: Login text visible after logout

### New Feature: Fan Messaging Module
- **Fan Messaging Test Class** (`FanMessagingTest.java`): Complete 5-step messaging flow between fan and creator
  - Step 1: Fan login → Messaging → Send message to creator (with timestamp)
  - Step 2: Creator login → Accept message → Set price 15€ → Reply (with timestamp)
  - Step 3: Fan reload → Accept paid message → Complete payment (Registered card)
  - Step 4: Creator → To Deliver → Upload and send media image
  - Step 5: Fan reload → Preview media → Close preview
  
- **Fan Messaging Page Object** (`FanMessagingPage.java`): Page object for fan messaging
  - Navigation: Messaging icon, Your subscriptions tab, Creator conversation
  - Message: Type and send message, verify message visible
  - Payment: Accept media button, Secure payment, Registered card, Confirm, Everything is OK
  - Media preview: Preview text click, Close button
  
- **Creator Messaging Page Enhancements** (`CreatorMessagingPage.java`): Added fan conversation methods
  - `verifyGeneralTabSelected()` - Verify General tab is default
  - `clickOnFanConversation()` - Click on fan in conversation list
  - `acceptFanMessageAndReply()` - Accept, set price, type reply, send
  - `sendMediaToFan()` - To Deliver tab, upload media, send

### Files Added
- `src/test/java/pages/FanHelpAndContactPage.java` - Fan help and contact page object
- `src/test/java/tests/FanHelpAndContactTest.java` - Fan help and contact test class
- `src/test/java/pages/FanSpottedBugPage.java` - Fan spotted bug page object
- `src/test/java/tests/FanSpottedBugTest.java` - Fan spotted bug test class
- `src/test/java/pages/FanEmailNotificationPage.java` - Fan email notification page object
- `src/test/java/tests/FanEmailNotificationTest.java` - Fan email notification test class
- `src/test/java/pages/FanTermsAndPoliciesPage.java` - Fan terms and policies page object
- `src/test/java/tests/FanTermsAndPoliciesTest.java` - Fan terms and policies test class
- `src/test/java/pages/FanLanguagePage.java` - Fan language page object
- `src/test/java/tests/FanLanguageTest.java` - Fan language test class
- `src/test/java/pages/FanPersonalInfoPage.java` - Fan personal information page object
- `src/test/java/tests/FanPersonalInfoTest.java` - Fan personal information test class
- `src/test/java/pages/FanLogoutPage.java` - Fan logout page object
- `src/test/java/tests/FanLogoutTest.java` - Fan logout test class
- `src/test/java/pages/FanMessagingPage.java` - Fan messaging page object
- `src/test/java/tests/FanMessagingTest.java` - Fan messaging test class

### Files Modified
- `testng.xml` - Added FanHelpAndContactTest, FanSpottedBugTest, FanEmailNotificationTest, FanTermsAndPoliciesTest, FanLanguageTest, FanPersonalInfoTest, FanLogoutTest, FanMessagingTest
- `testng-parallel.xml` - Added FanHelpAndContactTest, FanSpottedBugTest, FanEmailNotificationTest, FanTermsAndPoliciesTest, FanLanguageTest, FanPersonalInfoTest, FanLogoutTest, FanMessagingTest
- `src/test/java/pages/CreatorMessagingPage.java` - Added fan conversation methods for messaging flow

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
