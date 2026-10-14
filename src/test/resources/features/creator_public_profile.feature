@profile @positive
Feature: Creator Public Profile
  As an unauthenticated fan
  I want to view a creator's public profile
  So that I can see their content preview and decide to subscribe

  Background:
    Given I navigate to a creator public profile from discover

  Scenario: Creator public profile displays creator name and handle
    Then the creator name should be visible
    And the creator handle should be visible

  Scenario: Creator public profile shows subscribe button
    Then the Subscribe button should be visible

  Scenario: Creator public profile shows pricing info
    Then the pricing info should be visible

  Scenario: Creator public profile shows publications and collections count
    Then the publications count should be visible
    And the collections count should be visible

  Scenario: Creator public profile has publications and collections tabs
    Then the publications tab should be visible
    And the collections tab should be visible

  Scenario: Creator public profile shows follow icon
    Then the follow icon should be visible

  Scenario: Clicking subscribe triggers registration modal
    When I click the Subscribe button
    Then the registration modal should be visible
    And the "With my mail" button should be visible on the modal
    And the "With Google" button should be visible on the modal
    And the "Already have an account?" text should be visible on the modal
    And the "Sign in" link should be visible on the modal
