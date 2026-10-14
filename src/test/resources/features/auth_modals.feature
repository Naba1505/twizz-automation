@auth-modal @positive
Feature: Authentication Gate Modals
  As an unauthenticated fan on the public pages
  I want to be prompted to register when I try restricted actions
  So that I know I need an account to access full features

  Scenario: Messaging icon triggers registration modal
    Given I am on the public discover page
    When I click the Messaging icon
    Then the "Register to chat on Twizz" text should be visible
    And the "With my mail" button should be visible on the modal
    And the "With Google" button should be visible on the modal
    And the "Sign in" link should be visible on the modal

  Scenario: Settings icon triggers registration modal
    Given I am on the lives page
    When I click the Settings icon on bottom nav
    Then the "Register to see the content of TWIZZ" text should be visible
    And the "With my mail" button should be visible on the modal
    And the "With Google" button should be visible on the modal

  Scenario: Subscribe button on creator profile triggers registration modal
    Given I navigate to a creator public profile from discover
    When I click the Subscribe button
    Then the registration modal should be visible
    And the "With my mail" button should be visible on the modal
