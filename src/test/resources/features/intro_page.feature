@intro @positive
Feature: Intro Page
  As a visitor directed to the Twizz intro page
  I want to see clear options to connect or register
  So that I can join the platform easily

  Background:
    Given I am on the intro page

  Scenario: Intro page loads with Join Twizz heading
    Then the heading "Join Twizz" should be visible

  Scenario: Intro page shows Twizz logo
    Then the Twizz logo should be visible on intro page

  Scenario: Intro page shows content tagline
    Then the text "Content by the thousand" should be visible

  Scenario: Intro page shows Connect button
    Then the "Connect" button should be visible

  Scenario: Intro page shows Continue with Google button
    Then the "Continue with Google" button should be visible

  Scenario: Intro page shows Free registration link
    Then the "Free registration" link should be visible

  Scenario: Free registration link navigates to sign-up page
    When I click the "Free registration" link
    Then the URL should contain "/auth/signUp"

  Scenario: Intro page has language selector
    Then the language selector should be visible on intro page
