@lives @positive
Feature: Fan Lives Page
  As an unauthenticated fan
  I want to see the lives page with scheduled events
  So that I can discover live streams

  Background:
    Given I am on the lives page

  Scenario: Lives page loads with heading
    Then the "Lives" heading should be visible on lives page

  Scenario: Lives page shows Live, Events, and Replay tabs
    Then the Live tab should be visible
    And the Events tab should be visible
    And the Replay tab should be visible

  Scenario: Events tab shows today and upcoming sections
    Then the "Today's events" section should be visible
    And the "Upcoming" section should be visible
