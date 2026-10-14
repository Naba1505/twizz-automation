@signin @positive
Feature: Sign In Page
  As a returning Twizz user
  I want to see all sign-in options and form elements
  So that I can log into my account

  Background:
    Given I am on the sign-in page

  Scenario: Sign-in page loads with heading
    Then the heading "Sign In to Twizz" should be visible

  Scenario: Sign-in page shows Login label
    Then the "Login" label should be visible

  Scenario: Sign-in page shows email and password fields
    Then the email field should be visible on sign-in page
    And the password field should be visible on sign-in page

  Scenario: Sign-in page shows Connect button
    Then the "Connect" button should be visible on sign-in page

  Scenario: Sign-in page shows Google login option
    Then the "Connection with Google" button should be visible

  Scenario: Sign-in page shows OR separator
    Then the "OR" separator should be visible

  Scenario: Sign-in page shows password eye toggle
    Then the password eye toggle should be visible

  Scenario: Sign-in page shows Forgot password link
    Then the "Forgot your password?" text should be visible

  Scenario: Sign-in page shows Remember me checkbox checked by default
    Then the "Remember me" checkbox should be visible
    And the "Remember me" checkbox should be checked by default

  Scenario: Sign-in page shows Sign up link for new users
    Then the "Not account yet" text should be visible
    And the "Sign up" link should be visible

  Scenario: Sign-in page shows back arrow
    Then the back arrow should be visible on sign-in page

  Scenario: Sign up link navigates to registration page
    When I click the "Sign up" link on sign-in page
    Then the URL should contain "/auth/signUp"
