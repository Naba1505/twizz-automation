# Twizz Automation (Java 21 + Playwright + TestNG)

## 🚀 Latest Updates
- **Business Landing Page Tests**: Complete test coverage for Business app landing page (navigation, footer links, language switching)
- **Media Upload OS Dialog Fix**: Eliminated OS file dialog interruptions during automated media uploads using FileChooser
- **Project Structure Refactoring**: Reorganized into modular folders (creator/fan/admin/common/business)
- **Twizz Business App Integration**: New test coverage for Business application landing page
- **Fan Live Events Module**: Test coverage for fan joining creator live events (instant and scheduled)
- **Java 21 LTS**: Upgraded from Java 17 to Java 21 for enhanced performance and long-term support
- **Enhanced Code Quality**: Comprehensive framework improvements with zero IDE warnings

## 📋 Prerequisites
- **Java 21 LTS** (Eclipse Temurin recommended)
- **Maven 3.6+**
- **Git**

## 🔧 Quick Setup
```bash
# Verify Java 21 installation
java -version  # Should show: openjdk version "21.x.x"

# Clone and build
git clone <repository-url>
cd twizz-automation
mvn clean compile
```

## 🏢 Business Application Tests

### Business Landing Page
- Page object: `pages/business/BusinessLandingPage`
- Tests class: `tests/business/BusinessLandingPageTest`
- Scenarios:
  1. **Landing Page Navigation**: Verify logo, heading, Contact Us, Login, and Register navigation
  2. **Footer Links Navigation**: Test all footer links (Contact Us, Legal notices, Content Policy, Confidentiality, General Conditions, Blog)
  3. **Language Switching**: Switch between English, French (Français), and Spanish (Español)
- Run examples:
  ```bash
  mvn -Dtest=BusinessLandingPageTest#testBusinessLandingPage test
  mvn -Dtest=BusinessLandingPageTest#testBusinessLandingPageFooterLinks test
  mvn -Dtest=BusinessLandingPageTest#testBusinessLandingPageLanguageSwitch test
  # Run all Business Landing Page tests
  mvn -Dtest=BusinessLandingPageTest test
  ```

### Business Manager Sign Up
- Page object: `pages/business/manager/BusinessManagerSignUpPage`
- Tests class: `tests/business/manager/BusinessManagerSignUpTest`
- Flow:
  - **Page 1**: Basic Information (name, username, email, birth date)
  - **Page 2**: Password, agency name, profile image, phone, gender
  - **Page 3**: Status selection (private individual)
  - **Page 4**: Identity verification (ID document + selfie)
  - **Success**: Registration confirmation and return to sign-in page
- Features:
  - Unique username/email generation with timestamp
  - FileChooser for profile image upload
  - Direct file input for identity documents
  - Complete 4-page registration flow
- Run example:
  ```bash
  mvn -Dtest=tests.business.manager.BusinessManagerSignUpTest#managerCanSignUp test
  ```

### Business Manager Login
- Page object: `pages/business/manager/BusinessManagerLoginPage`
- Tests class: `tests/business/manager/BusinessManagerLoginTest`
- Flow:
  - Navigate to sign-in page
  - Fill username and password (from config.properties)
  - Click Login button
  - Verify navigation to manager dashboard
  - Verify welcome message with manager's display name
- Features:
  - Credentials loaded from config.properties
  - URL validation for manager dashboard
  - Welcome message verification
- Run example:
  ```bash
  mvn -Dtest=tests.business.manager.BusinessManagerLoginTest#managerCanLogin test
  ```

### Business Manager Add Employee (Invite)
- Page objects:
  - `pages/business/manager/BusinessManagerAddEmployeePage`
  - `pages/business/manager/BusinessManagerSettingsPage`
  - `pages/business/employee/EmployeeSettingsPage`
- Tests class: `tests/business/manager/BusinessManagerAddEmployeeTest`
- **Test 1: Invite Employee from Agency** (priority 1)
  - Login as Manager
  - Click on Agency icon
  - Verify 'Your agency' and 'Your employees' messages
  - Click Add button to invite employee
  - Search for employee by username
  - Select employee checkbox
  - Send invitation
  - Verify invitation sent message
  - Click 'I understand' button (if present)
- **Test 2: Duplicate Invitation from Agency** (priority 2)
  - Login as Manager
  - Navigate to Agency screen
  - Attempt to invite same employee again
  - Verify 'there is an invitation already sent' message appears
- **Test 3: Employee Reject Invitation** (priority 3)
  - Set viewport to mobile size (375x667)
  - Login as Employee
  - Navigate to Settings → View invitations
  - Verify invitation is visible
  - Click Decline button
  - Confirm rejection with 'Finish'
  - Verify 'Rejected' message
- **Test 4: Invite Employee from Settings** (priority 4)
  - Login as Manager
  - Click on Settings icon
  - Click on 'Employee Go' button
  - Verify 'Your employees' text
  - Click 'Invite a employee'
  - Search for employee by username
  - Select employee checkbox
  - Send invitation
  - Verify invitation sent message
  - Click 'I understand' button
- **Test 5: Duplicate Invitation from Settings** (priority 5)
  - Login as Manager
  - Navigate to Settings → Employee Go → Invite a employee
  - Attempt to invite same employee again
  - Verify 'there is an invitation' message appears
- **Test 6: Employee Accept Invitation** (priority 6)
  - Set viewport to mobile size (375x667)
  - Login as Employee
  - Navigate to Settings → View invitations
  - Verify invitation is visible
  - Click Accept button
  - Confirm acceptance with 'Finish'
  - Verify 'Invitation accepted' message
- **Test 7: View Added Employee** (priority 7)
  - Login as Manager
  - Navigate to Agency screen
  - Verify 'Your employees' section
  - Click on employee card
  - Verify 'Twizz identity Card' heading (confirms employee is added)
- Features:
  - Complete agency management flow
  - Employee invitation from Agency and Settings screens
  - Employee search and selection
  - Invitation success verification
  - Duplicate invitation detection
  - Employee rejection and acceptance flows
  - View added employee details
  - Mobile viewport support
  - Flexible success message handling
- Run examples:
  ```bash
  mvn -Dtest=tests.business.manager.BusinessManagerAddEmployeeTest#managerCanInviteEmployee test
  mvn -Dtest=tests.business.manager.BusinessManagerAddEmployeeTest#managerSeesDuplicateInvitationMessage test
  mvn -Dtest=tests.business.manager.BusinessManagerAddEmployeeTest#employeeCanRejectInvitation test
  mvn -Dtest=tests.business.manager.BusinessManagerAddEmployeeTest#managerCanInviteEmployeeFromSettings test
  mvn -Dtest=tests.business.manager.BusinessManagerAddEmployeeTest#managerSeesDuplicateInvitationFromSettings test
  mvn -Dtest=tests.business.manager.BusinessManagerAddEmployeeTest#employeeCanAcceptInvitation test
  mvn -Dtest=tests.business.manager.BusinessManagerAddEmployeeTest#managerCanViewAddedEmployee test
  mvn -Dtest=tests.business.manager.BusinessManagerAddEmployeeTest test
  ```

### Business Manager Add Creator (Invite)
- Page objects: 
  - `pages/business/manager/BusinessManagerAddCreatorPage`
  - `pages/business/manager/BusinessManagerSettingsPage`
  - `pages/creator/CreatorManagerPage`
- Tests class: `tests/business/manager/BusinessManagerAddCreatorTest`
- **Test 1: Invite Creator from Agency** (priority 1)
  - Login as Manager
  - Click on Agency icon
  - Verify 'Your agency' and 'Your creators' messages
  - Click Add button to invite creator
  - Search for creator by username
  - Select creator checkbox
  - Send invitation
  - Verify invitation sent message
  - Click 'I understand' button (if present)
- **Test 2: Duplicate Invitation from Agency** (priority 2)
  - Login as Manager
  - Navigate to Agency screen
  - Attempt to invite same creator again
  - Verify 'there is an invitation already sent' message appears
- **Test 3: Creator Reject Invitation** (priority 3)
  - Login as Creator
  - Navigate to Settings → Manager
  - Verify invitation is visible
  - Click Refuse button
  - Confirm rejection with 'I refuse'
  - Verify 'Invitation rejected' message
- **Test 4: Invite Creator from Settings** (priority 4)
  - Login as Manager
  - Click on Settings icon
  - Click on 'Creator Go' button
  - Verify 'Your creators' text
  - Click 'Invite a creator'
  - Search for creator by username
  - Select creator checkbox
  - Send invitation
  - Verify invitation sent message
  - Click 'I understand' button
- **Test 5: Duplicate Invitation from Settings** (priority 5)
  - Login as Manager
  - Navigate to Settings → Creator Go → Invite a creator
  - Attempt to invite same creator again
  - Verify 'there is an invitation' message appears
- **Test 6: Creator Accept Invitation** (priority 6)
  - Login as Creator
  - Navigate to Settings → Manager
  - Verify invitation is visible
  - Click Accept button
  - Confirm acceptance with 'I accept'
  - Verify 'Invitation accepted' message
- **Test 7: View Added Creator** (priority 7)
  - Login as Manager
  - Navigate to Agency screen
  - Verify 'Your creators' section
  - Click on creator card
  - Verify 'Twizz identity Card' heading (confirms creator is added)
- Features:
  - Complete agency management flow
  - Creator invitation from Agency and Settings screens
  - Creator search and selection
  - Invitation success verification
  - Duplicate invitation detection
  - Creator rejection and acceptance flows
  - View added creator details
  - Flexible success message handling
- Run examples:
  ```bash
  mvn -Dtest=tests.business.manager.BusinessManagerAddCreatorTest#managerCanInviteCreator test
  mvn -Dtest=tests.business.manager.BusinessManagerAddCreatorTest#managerSeesDuplicateInvitationMessage test
  mvn -Dtest=tests.business.manager.BusinessManagerAddCreatorTest#creatorCanRejectInvitation test
  mvn -Dtest=tests.business.manager.BusinessManagerAddCreatorTest#managerCanInviteCreatorFromSettings test
  mvn -Dtest=tests.business.manager.BusinessManagerAddCreatorTest#managerSeesDuplicateInvitationFromSettings test
  mvn -Dtest=tests.business.manager.BusinessManagerAddCreatorTest#creatorCanAcceptInvitation test
  mvn -Dtest=tests.business.manager.BusinessManagerAddCreatorTest#managerCanViewAddedCreator test
  mvn -Dtest=tests.business.manager.BusinessManagerAddCreatorTest test
  ```

### Business Manager Delete Creator (Cleanup)
- Page object: `pages/business/manager/BusinessManagerDeleteCreatorPage`
- Tests class: `tests/business.manager.BusinessManagerDeleteCreatorTest`
- **Test 1: Delete Creator** (priority 1)
  - Login as Manager
  - Navigate to Agency screen
  - Click on creator card
  - Verify creator details screen
  - Click 'Delete the creator'
  - Verify delete confirmation dialog
  - Click 'Validate' to confirm deletion
  - Verify 'Creator deleted successfully' message
- Features:
  - Cleanup test to remove test data
  - Delete creator from agency
  - Confirmation dialog handling
  - Success message verification
- Run example:
  ```bash
  mvn -Dtest=tests.business.manager.BusinessManagerDeleteCreatorTest#managerCanDeleteCreator test
  ```

### Business Manager Delete Employee (Cleanup)
- Page object: `pages/business/manager/BusinessManagerDeleteEmployeePage`
- Tests class: `tests/business.manager.BusinessManagerDeleteEmployeeTest`
- **Test 1: Delete Employee** (priority 1)
  - Login as Manager
  - Navigate to Agency screen
  - Click on employee card
  - Verify employee details screen
  - Click 'Delete this account'
  - Verify delete confirmation dialog
  - Click 'Validate' to confirm deletion
  - Verify 'Employee deleted successfully' message
- Features:
  - Cleanup test to remove test data
  - Delete employee from agency
  - Confirmation dialog handling
  - Success message verification
- Run example:
  ```bash
  mvn -Dtest=tests.business.manager.BusinessManagerDeleteEmployeeTest#managerCanDeleteEmployee test
  ```

### Business Employee Sign Up
- Page object: `pages/business/employee/BusinessEmployeeSignUpPage`
- Tests class: `tests/business/employee/BusinessEmployeeSignUpTest`
- Flow:
  - **Page 1**: Basic Information (name, username, email, birth date)
  - **Page 2**: Password, phone, gender
  - **Success**: Direct navigation to employee dashboard
- Features:
  - Verifies Employee tab is selected by default
  - Unique username/email generation with timestamp
  - Simpler 2-page registration flow (no agency/documents required)
  - Validates employee dashboard URL and agency avatar visibility
- Run example:
  ```bash
  mvn -Dtest=tests.business.employee.BusinessEmployeeSignUpTest#employeeCanSignUp test
  ```

### Business Employee Login
- Page object: `pages/business/employee/BusinessEmployeeLoginPage`
- Tests class: `tests/business/employee/BusinessEmployeeLoginTest`
- Flow:
  - Navigate to sign-in page
  - Fill username and password (from config.properties)
  - Click Login button
  - Verify navigation to employee dashboard
  - Verify welcome message with employee's display name
- Features:
  - Credentials loaded from config.properties
  - URL validation for employee dashboard
  - Welcome message verification
- Run example:
  ```bash
  mvn -Dtest=tests.business.employee.BusinessEmployeeLoginTest#employeeCanLogin test
  ```

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


## Scripts (Creator)
- Page object: `src/test/java/pages/CreatorScriptsPage.java`
- Tests class: `src/test/java/tests/CreatorScriptsTest.java`
- Scenarios (by priority):
  1. **Images script (device)** (priority 1)
     - Name prefix: `ImageScript_`
     - Media: 2 images from device:
       - `src/test/resources/Images/ScriptImageA.png`
       - `src/test/resources/Images/ScriptImageB.png`
     - Flow: Login → Profile → Settings → Scripts → `+` → Create script with 2 blurred images → price `15€` → bookmark → message (`Test /name`) → note → Confirm.
  2. **Videos script with promo (device)** (priority 2)
     - Name prefix: `ScriptVideo_`
     - Media: 2 videos from device:
       - `src/test/resources/Videos/ScriptVideoA.mp4`
       - `src/test/resources/Videos/ScriptVideoB.mp4`
     - Flow: same entry as above, but media are videos, price set to `10€` via spinbutton, promo slider enabled with `2`€ discount and validity `Unlimited`, then note and Confirm.
  3. **Audios script with promo (device)** (priority 3)
     - Name prefix: `ScriptAudio_`
     - Media: 2 audios from device:
       - `src/test/resources/Audios/ScriptAudioA.mp3`
       - `src/test/resources/Audios/ScriptAudioB.mp3`
     - Flow: media are audios, price set to `50€`, promo slider using second discount input with `20`% and validity `7 days`, then note and Confirm.
  4. **Mixed media free script (device)** (priority 4)
     - Name prefix: `ScriptMixed_`
     - Media: 1 image + 1 video + 1 audio:
       - `ScriptImageA.png`, `ScriptVideoA.mp4`, `ScriptAudioA.mp3` (same folders as above)
     - Flow: create script with the 3 mixed media, **keep default free price** (no price change), then bookmark, message, note and Confirm.
  5. **Scripts search** (priority 5)
     - Flow: from Scripts screen, open Search, enter multiple keywords (`image`, `video`, `audio`, `mixed`) in sequence, clear between them, Cancel, and assert Scripts heading is still visible.
  6. **Edit scripts (image/video/audio/mixed)** (priorities 6–9)
     - Each test logs in, navigates to Scripts, opens the first script in edit mode, renames it to `ImageUpdated_*`, `VideoUpdated_*`, `AudioUpdated_*` or `MixedUpdated_*`, adds one extra media item of the corresponding type, updates message and (optionally) note, clicks Confirm, and waits for the `Script updated successfully` toast.
  7. **Quick Files image script** (priority 10)
     - Name prefix: `ImageScriptQF_`
     - Media: 2 images selected from a Quick Files album whose name starts with `imagealbum_` (created by `CreatorQuickFilesTest`).
  8. **Quick Files video script with promo** (priority 11)
     - Name prefix: `ScriptVideoQF_`
     - Media: 2 videos selected from a Quick Files album whose name starts with `videoalbum_` (created by `CreatorQuickFilesTest`).
     - Price/promo: custom price `10€` + promo slider with `2`€ discount and `Unlimited` validity.
  9. **Quick Files audio script with promo** (priority 12)
     - Name prefix: `ScriptAudioQF_`
     - Media: 1 audio selected from a Quick Files album whose name starts with `audioalbum_` (created by `CreatorQuickFilesTest`).
     - Price/promo: price `50€` + promo slider second discount input `20`% with validity `7 days`.
- Notes:
  - All flows use robust upload handling (`input[type='file']` / Playwright file chooser / Quick Files album selection) and a hardened Confirm click (prefers `//div[@class='chat-scripts-button enabled']` with fallbacks).
  - Script creation and update success toasts (`"Script created successfully"`, `"Script updated successfully"`) are treated as **hard assertions** in the POM: missing toasts cause the test to fail, ensuring scripts are really created/updated.
  - Quick Files based tests expect Quick Files albums to exist. Run `CreatorQuickFilesTest` first to create `videoalbum_*`, `imagealbum_*`, `mixalbum_*`, and `audioalbum_*` albums.
- Run example:
  ```bash
  mvn -Dtest=CreatorQuickFilesTest test
  mvn -Dtest=CreatorScriptsTest test
  ```

## Fan Bookmarks
- Page object: `src/test/java/pages/FanBookmarksPage.java`
- Tests class: `src/test/java/tests/FanBookmarksTest.java`
- Scenarios:
  1. **Bookmark multiple feeds** (priority 1)
     - Flow: Login as Fan → Home screen → Bookmark 3 feeds → Verify bookmarkFill icons displayed.
  2. **View bookmarked feeds** (priority 2, depends on test 1)
     - Flow: Login as Fan → Settings → Bookmarks → Verify 3 watermarked feeds displayed.
  3. **Unbookmark feeds** (priority 3, depends on test 2)
     - Flow: Login as Fan → Settings → Bookmarks → Click watermarked feed → Click bookmarkFill to unbookmark (repeat for all 3) → Navigate back → Hard refresh → Verify "No bookmarks found!" text.
- Notes:
  - Tests are dependent and must run in sequence.
  - Uses `img[alt='watermarked']` to identify bookmarked feeds on the bookmarks screen.
  - Uses `bookmarkFill` icon to identify highlighted/bookmarked state.
- Run example:
  ```bash
  mvn -Dtest=FanBookmarksTest test
  ```

## Fan My Creators
- Page object: `src/test/java/pages/FanMyCreatorsPage.java`
- Tests class: `src/test/java/tests/FanMyCreatorsTest.java`
- Scenarios:
  1. **View My Creators and subscription details** (priority 1)
     - Flow: Login as Fan → Home screen → Settings → My creators → Click first creator arrow → View details → Cancel → Click "See all results" → Scroll to end → Click last creator arrow → View details → Cancel → Scroll to top → Verify title → Navigate back to home.
- Notes:
  - Views subscribed creators list and individual subscription details.
  - Uses "See all results" to load all creators.
  - Navigates back to home screen at the end.
- Run example:
  ```bash
  mvn -Dtest=FanMyCreatorsTest test
  ```

## Scripts Cleanup (Creator)
- Page object: `src/test/java/pages/CreatorScriptsPage.java`
- Tests class: `src/test/java/tests/CreatorScriptsCleanupTest.java`
- Scenarios:
  1. **Delete all QA bookmarks and scripts** (priority 1)
     - Flow: Login → Profile → Settings → Scripts → Edit Categories → Handle popup → Long-press each QA_ prefixed bookmark → Delete → Confirm → Repeat until "You haven't created any" message appears.
- Notes:
  - Deleting a bookmark automatically deletes all associated scripts.
  - Uses long-press gesture (2 second hold) to trigger delete dialog.
  - Should run after `CreatorScriptsTest` to clean up test data.
- Run example:
  ```bash
  mvn -Dtest=CreatorScriptsCleanupTest test
  ```


End-to-end UI automation for Twizz with Creator and Fan flows. The framework emphasizes robust, resilient interactions and uses Allure only for reporting (with Playwright traces and screenshots).

## Tech Stack
- Java 21 LTS
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
```
src/
├── main/java/utils/
│   ├── ConfigReader.java      # Loads config.properties, typed getters
│   ├── BrowserFactory.java    # Playwright browser management
│   ├── WaitUtils.java         # Wait utilities
│   ├── RetryAnalyzer.java     # Centralized retry with logging
│   └── AnnotationTransformer.java
│
└── test/java/
    ├── pages/
    │   ├── common/            # Shared page objects
    │   │   ├── BasePage.java
    │   │   ├── BaseTestClass.java
    │   │   └── LandingPage.java
    │   ├── creator/           # Creator app page objects
    │   │   ├── CreatorLoginPage.java
    │   │   ├── CreatorRegistrationPage.java
    │   │   ├── CreatorPublicationPage.java
    │   │   └── ... (30+ page objects)
    │   ├── fan/               # Fan app page objects
    │   │   ├── FanLoginPage.java
    │   │   ├── FanHomePage.java
    │   │   └── ... (15+ page objects)
    │   ├── admin/             # Admin page objects
    │   │   └── AdminCreatorApprovalPage.java
    │   └── business/          # Business app page objects
    │       ├── BusinessLandingPage.java
    │       └── BusinessBaseTestClass.java
    │
    └── tests/
        ├── common/            # Common tests (LandingPageTest)
        ├── creator/           # Creator tests (30+ test classes)
        ├── fan/               # Fan tests (15+ test classes)
        ├── admin/             # Admin tests (AdminApproveCreatorTest)
        └── business/          # Business tests (BusinessLandingPageTest)
```

### TestNG XML Runners
- `testng.xml` - Sequential execution of all tests (Creator → Admin → Fan)
- `testng-parallel.xml` - Parallel execution (thread-count=4)
- `business-testng.xml` - Business tests only (sequential)
- `business-testng-parallel.xml` - Business tests only (parallel)

## Prerequisites
- Java 21 LTS
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
  - Uses Playwright's `input[type="file"].setInputFiles(...)` for all media uploads (videos, images, audio, recordings) to avoid native OS file chooser dialogs.
  - Prefer sequential per-file uploads via the PLUS button with tab switching to the appropriate media tab; when PLUS is a wrapper (e.g., Ant Upload), the code locates the underlying `input[type='file']` instead of clicking the button that would open a native dialog.
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
   - Device-based media uploads for collections (image/video from "My Device") also drive `input[type='file']` directly and dismiss the Importation bottom sheet via its Cancel button to avoid native OS file choosers and overlay blocking.
   - Creator Quick Files, Creator Collections, and Creator Registration use no-native-dialog uploads via `input[type='file'].setInputFiles`, and microphone permission is granted in `BrowserFactory` to avoid Chrome popups.

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

## Fan Home Screen
- Page object: `pages/FanHomePage`
- Test class: `tests/FanHomeScreenTest`
- Flow:
  - Fan login → assert `/fan/home` URL
  - Assert popcorn logo next to username
  - Scroll to first video and play
  - Scroll back to top and interact with the first visible feed
    - Like/Bookmark with robust icon discovery and scroll
    - Open three-dots action menu and Cancel
  - Search by handle, open profile by exact texts, ensure `Subscriber` button, navigate back to Home
- Config keys (optional overrides with defaults):
  - `fan.home.firstFeedUsername` (default: `badrzt`)
  - `fan.home.search.handle` (default: `john_smith`)
  - `fan.home.search.lastNameExact` (default: `Smith`)
- Run example:
  ```bash
  mvn -Dtest=tests.FanHomeScreenTest test
  ```

## Fan Discover
- Page object: `pages/FanDiscoverPage`
- Test class: `tests/FanDiscoverTest`
- Flow (3 tests):
  1) Navigate via Search icon → assert URL contains `/common/discover` → ensure feeds while scrolling → unmute visible feeds → scroll up
  2) From Discover open a random visible profile → ensure profile screen → back to Discover
  3) Open search on Discover → search query → click result → ensure profile → back to Discover
- Config keys (optional overrides):
  - `fan.discover.search.query` (default: `igor`)
  - `fan.discover.search.resultText` (default: `igor test`)
- Run example:
  ```bash
  mvn -Dtest=tests.FanDiscoverTest test
  ```

## Fan Help and Contact
- Page object: `pages/FanHelpAndContactPage`
- Test class: `tests/FanHelpAndContactTest`
- Flow:
  - Fan login → Home screen → Click Settings icon
  - Scroll to "Help and contact" → Click
  - Assert on Help and Contact screen (title visible)
  - Fill Subject field (with timestamp for reference)
  - Fill Description field (with timestamp for reference)
  - Click Send button
  - Assert success message "Your message has been sent"
- Run example:
  ```bash
  mvn -Dtest=FanHelpAndContactTest test
  ```

## Fan Spotted Bug
- Page object: `pages/FanSpottedBugPage`
- Test class: `tests/FanSpottedBugTest`
- Flow:
  - Fan login → Home screen → Click Settings icon
  - Assert on Settings screen (title visible)
  - Scroll to "I've spotted a bug" → Click
  - Assert on screen (title visible)
  - Fill Subject field (with timestamp for reference)
  - Fill Description field (with timestamp for reference)
  - Click Send button
  - Assert success message "Your message has been sent"
- Run example:
  ```bash
  mvn -Dtest=FanSpottedBugTest test
  ```

## Fan Email Notification
- Page object: `pages/FanEmailNotificationPage`
- Test class: `tests/FanEmailNotificationTest`
- Tests:
  1. **Disable all toggles** (priority 1)
     - Fan login → Settings → Email notification
     - Disable all 5 toggles with confirmation dialog:
       - Push media from a creator
       - Live reminder
       - Scheduling a live
       - Direct live
       - Marketing
  2. **Enable all toggles** (priority 2)
     - Fan login → Settings → Email notification
     - Enable all 5 toggles (simple click, no confirmation)
- Run example:
  ```bash
  mvn -Dtest=FanEmailNotificationTest test
  ```

## Fan Terms and Policies
- Page object: `pages/FanTermsAndPoliciesPage`
- Test class: `tests/FanTermsAndPoliciesTest`
- Flow:
  - Fan login → Home screen → Settings
  - **Terms and Conditions of Sale:** Click → Scroll to end → Scroll back to top → Navigate back
  - **Community Regulations:** Click → Scroll to end → Scroll back to top → Navigate back
  - **Content Policy:** Click → Scroll to end → Scroll back to top → Navigate back
  - Assert back on Settings screen
- Run example:
  ```bash
  mvn -Dtest=FanTermsAndPoliciesTest test
  ```

## Fan Language
- Page object: `pages/FanLanguagePage`
- Test class: `tests/FanLanguageTest`
- Flow:
  - Fan login → Home screen → Settings
  - **English → French:** Click "Language" → Select "Français" → Assert "Langue" title → Back
  - **French → Spanish:** Click "Langue" → Select "Español" → Assert "Idioma" title → Back
  - **Spanish → English:** Click "Idioma" → Select "English" → Assert "Language" title → Back
  - Assert "Language" menu visible (back to English)
- Run example:
  ```bash
  mvn -Dtest=FanLanguageTest test
  ```

## Fan Personal Information
- Page object: `pages/FanPersonalInfoPage`
- Test class: `tests/FanPersonalInfoTest`
- Flow:
  - Fan login → Home screen → Settings → Personal Information
  - Verify fields visible: Identity (locked), User name (locked), Date of birth, Account type (Fan)
  - Update email and phone number fields
  - Click Register button
  - Verify success message: "Updated Personal Information"
- Run example:
  ```bash
  mvn -Dtest=FanPersonalInfoTest test
  ```

## Fan Logout
- Page object: `pages/FanLogoutPage`
- Test class: `tests/FanLogoutTest`
- Flow:
  - Fan login → Home screen → Settings
  - Click "Disconnect" button
  - Verify "Login" text visible (user on login page)
- Run example:
  ```bash
  mvn -Dtest=FanLogoutTest test
  ```

## Fan Messaging
- Page objects: `pages/FanMessagingPage`, `pages/CreatorMessagingPage`
- Test class: `tests/FanMessagingTest`
- Scenarios (4 tests):
  1. **Image + Fixed Price** (priority 1)
     - Fan sends message → Creator accepts with fixed price (15€) → Fan pays → Creator sends image via My Device → Fan views preview
  2. **Video + Custom Price** (priority 2)
     - Fan sends message → Creator accepts with custom price (10) → Fan pays → Creator sends video via My Device → Fan verifies video play icon
  3. **Audio + Free Price** (priority 3)
     - Fan sends message → Creator accepts with FREE price (default) → Fan accepts (no payment) → Creator sends audio → Fan verifies audio element
  4. **Mixed Media via Quick Files** (priority 4)
     - Fan sends message → Creator accepts with fixed price → Fan pays → Creator sends mixed media (image + video + audio) via Quick Files album selection → Fan verifies all media (2 previews + audio element)
- Technical notes:
  - Uses dual browser context (fan + creator isolation)
  - Messages include timestamps for uniqueness
  - Handles payment flow with registered card
  - Quick Files flow uses album selection from pre-created albums (mixalbum_*, audioalbum_*)
- Run examples:
  ```bash
  mvn -Dtest=FanMessagingTest test
  mvn -Dtest=FanMessagingTest#completeMessagingFlow test
  mvn -Dtest=FanMessagingTest#completeMessagingFlowWithVideoAndCustomPrice test
  mvn -Dtest=FanMessagingTest#completeMessagingFlowWithAudioAndFreePrice test
  mvn -Dtest=FanMessagingTest#completeMessagingFlowWithMixedMedia test
  ```

## Twizz Business App
- Page object: `pages/business/BusinessLandingPage`
- Base class: `pages/business/BusinessBaseTestClass`
- Test class: `tests/business/BusinessLandingPageTest`
- URL: `https://devbusiness.twizz.app/` (dev), `https://business.twizz.app/` (prod)
- Coverage:
  - Verify Twizz Business logo displayed
  - Verify "Designed for managers" heading on landing page
  - Navigate to Contact Us → verify "We are in different places" heading
  - Navigate to Login → verify "Connection" heading
  - Navigate to Register → verify "Inscription" heading
  - Switch between Employee and Manager registration tabs
- Run examples:
  ```bash
  mvn -Dtest=tests.business.BusinessLandingPageTest test
  mvn -B "-Dsurefire.suiteXmlFiles=business-testng.xml" test
  ```

## Fan Live Events
- Page object: `pages/fan/FanLivePage`
- Test class: `tests/fan/FanLiveTest`
- Scenarios:
  1. **Creator creates instant live, Fan joins** (priority 1)
     - Creator: Login → Navigate to Live → Create instant live (Everyone, 15€) → Start now
     - Fan: Login (separate context) → Lives screen → Join live → Pay → Comment "Hi" → Close
     - Creator: End live stream
  2. **Creator schedules live, Fan buys ticket** (priority 2)
     - Creator: Login → Navigate to Live → Schedule live event (Everyone, 15€, future date)
     - Fan: Login (separate context) → Lives screen → Events tab → Buy ticket → Payment
     - Creator: Delete scheduled live for cleanup
- Technical notes:
  - Uses dual browser context architecture (creator + fan isolation)
  - Microphone and camera permissions granted via BrowserFactory
- Run examples:
  ```bash
  mvn -Dtest=FanLiveTest test
  mvn -Dtest=FanLiveTest#creatorCreatesInstantLiveFanJoins test
  mvn -Dtest=FanLiveTest#creatorSchedulesLiveFanBuysTicket test
  ```

## Fan Subscription (3DS)
- Page object: `pages/FanSubscriptionPage`
- Test class: `tests/FanSubscriptionTest`
- Depends on: `CreatorRegistrationTest` and `AdminApproveCreatorTest` (creates and approves a creator username used in search)
- Flow (summary):
  - Fan logs in → opens Search → searches and opens approved creator profile
  - Clicks "Subscribe" button → waits for "Premium" plan modal → clicks "Continue"
  - Fills payment card details on "Secure payment" page
  - Confirms payment and completes 3DS flow (SecurionPay test gateway)
  - Waits for "Payment confirmed!" message
  - Automatically navigates to creator profile and verifies "Subscriber" button is visible
- Config keys (with safe defaults in `config.properties`):
  - `fan.username`, `fan.password`
  - `approval.username` (used as fallback if the username is not produced by earlier tests)
  - `payment.card.number` (default: `4012 0018 0000 0016`), `payment.card.expiry` (default: `07/34`), `payment.card.cvc` (default: `657`)
- 3DS handling: 
  - Interacts only with the real SecurionPay popup/iframe (no direct navigation)
  - Uses resilient selectors with JS-click fallbacks
  - Waits for automatic navigation after payment confirmation
  - Verifies subscription success via "Subscriber" button on creator profile
- Run end-to-end in one JVM:
  ```bash
  mvn -Dtest="CreatorRegistrationTest,AdminApproveCreatorTest,FanSubscriptionTest" test
  ```
  Or via XML order (sequential):
  ```bash
  mvn -B "-Dsurefire.suiteXmlFiles=testng.xml" test
  ```

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

## Admin Approval (Admin Dashboard)
- Page object: `src/test/java/pages/AdminCreatorApprovalPage.java`
- Tests class: `src/test/java/tests/AdminApproveCreatorTest.java`
- Flow:
  - Navigate to admin login → Log in as manager → open `Creators > All creators`.
  - Search using the "Enter keyword" input (robust XPath + iframe handling).
  - Row-scoped Action → Edit → toggle "Verified Email?" and "Verified Account?".
  - Change status from Pending to Registered (Ant Design dropdown by text).
  - Submit and verify "Updated successfully".
- Run examples:
  - Standalone with an explicit username (PowerShell quoting included):
    ```bash
    mvn -Dtest=AdminApproveCreatorTest "-Dapproval.username=<creator_username>" test
    ```
  - End-to-end in a single JVM (registration then admin approval):
    ```bash
    mvn -Dtest="CreatorRegistrationTest,AdminApproveCreatorTest" test
    ```
  - Username resolution order when running admin standalone:
    - `tests.CreatorRegistrationTest.createdUsername` (same-JVM run)
    - `target/created-username.txt` (written by the registration test)
    - `approval.username` (from `-Dapproval.username` or `config.properties`)

> Note: JVM system properties (e.g., `-Dapproval.username=...`) override `src/main/resources/config.properties`.

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

## Known Limitations

### Drag-and-Drop Automation
**Test**: `CreatorScriptsTest.creatorCanChangeScriptOrder()` (Priority 13)

**Status**: Disabled (`enabled = false`)

**Issue**: The drag-and-drop functionality for reordering scripts cannot be automated with Playwright's current capabilities. The specific drag-and-drop library used in the application does not respond to:
- Playwright's `dragTo()` method
- Manual mouse operations (`mouse().down()`, `mouse().move()`, `mouse().up()`)
- JavaScript HTML5 drag-and-drop event simulation

**What Works**:
- Navigation to Scripts page ✓
- Clicking edit icon on first script ✓
- Clicking "Change order" button ✓
- Verifying "Hold the button on the right" heading ✓
- Locating reorder handles via `getByRole(LISTITEM).getByLabel("reorder")` ✓

**What Doesn't Work**:
- Actual drag-and-drop operation to reorder scripts ✗
- "Order updated" success message never appears ✗

**Manual Testing**: The drag-and-drop functionality works correctly when tested manually in a browser.

**Resolution Options**:
1. Application team updates to a Playwright-compatible drag-and-drop library
2. Playwright adds better support for complex drag-and-drop interactions
3. A working JavaScript-based drag simulation solution is found

**Workaround**: This test remains in the codebase (disabled) to document the expected flow. It can be re-enabled if/when a solution is found.

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
