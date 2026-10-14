@explore @positive
Feature: Public Explore Page
  As an unauthenticated fan
  I want to browse the explore grid of content thumbnails
  So that I can discover content visually

  Background:
    Given I am on the public explore page

  Scenario: Explore page loads with search bar
    Then the search bar should be visible on explore page

  Scenario: Explore page shows thumbnail grid
    Then the thumbnail grid should be visible
