@discover @positive
Feature: Public Discover Page
  As an unauthenticated fan visiting Twizz
  I want to browse the discover feed and see creator content
  So that I can find creators I'm interested in

  Background:
    Given I am on the public discover page

  Scenario: Discover page loads with search bar
    Then the search bar should be visible on discover page

  Scenario: Discover page shows creator feed with cards
    Then the creator feed should be visible
    And at least 1 creator card should be displayed

  Scenario: Discover page shows unmute button on first video
    Then the unmute button should be visible

  Scenario: Bottom navigation bar shows all five icons
    Then the Home icon should be visible on discover page
    And the Search icon should be visible
    And the Messaging icon should be visible
    And the Live icon should be visible
    And the Settings icon should be visible

  Scenario: Clicking a creator card navigates to their public profile
    When I click on the first creator card
    Then the URL should contain "/p/"
    And the Subscribe button should be visible

  Scenario: Search icon navigates to explore page
    When I click the Search icon
    Then the URL should contain "/public-explore"

  Scenario: Live icon navigates to lives page
    When I click the Live icon
    Then the URL should contain "/p/fan/lives"

  Scenario: Home icon stays on discover page
    When I click the Home icon on discover page
    Then the URL should contain "/public-discover"
