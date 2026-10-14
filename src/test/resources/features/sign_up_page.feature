@signup @positive
Feature: Sign Up Page
  As a new visitor wanting to join Twizz
  I want to see a complete registration form
  So that I can create my fan or creator account

  Background:
    Given I am on the sign-up page

  Scenario: Sign-up page loads with heading
    Then the heading "Sign Up on Twizz" should be visible

  Scenario: Sign-up page shows Registration label
    Then the "Registration" label should be visible

  Scenario: Sign-up page shows Creator and Fan tabs
    Then the "Creator" tab should be visible
    And the "Fan" tab should be visible

  Scenario: Fan tab is selected by default
    Then the "Fan" tab should be selected

  Scenario: Sign-up page shows Google registration option
    Then the "Continue with Google" button should be visible

  Scenario: Sign-up page shows OR separator
    Then the "OR" separator should be visible on sign-up page

  Scenario: Fan registration form shows all required fields
    Then the first name field should be visible
    And the last name field should be visible
    And the email field should be visible on sign-up page
    And the username field should be visible
    And the password field should be visible on sign-up page

  Scenario: Sign-up page shows password eye toggle
    Then the password eye toggle should be visible on sign-up page

  Scenario: Sign-up page shows terms and conditions checkbox
    Then the terms and conditions checkbox should be visible

  Scenario: Registration button is visible but disabled initially
    Then the "Registration" button should be visible
    And the "Registration" button should be disabled

  Scenario: Sign-up page shows language selector
    Then the language selector should be visible on sign-up page

  Scenario: Sign-up page shows back arrow
    Then the back arrow should be visible on sign-up page
