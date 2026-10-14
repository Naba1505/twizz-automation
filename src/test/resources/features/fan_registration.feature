@registration @positive
Feature: Fan Registration Flow
  As a new visitor who wants to become a Twizz fan
  I want to register with a valid email
  So that I can subscribe to creators and access exclusive content

  Scenario: Complete fan registration with random email
    Given I am on the sign-up page
    And I am on the Fan tab
    When I fill in the fan registration form with random data
    And I click the Registration button
    Then the registration should proceed successfully
