# Changelog

## [Unreleased] - 2025-12-04

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
