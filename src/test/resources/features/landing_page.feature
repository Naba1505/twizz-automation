@landing @positive
Feature: Landing Page
  As a new visitor to Twizz
  I want to see a well-structured landing page with clear navigation
  So that I can understand the platform and find my way to register or explore

  Background:
    Given I am on the Twizz landing page

  # --- Page Load & Core Content ---

  Scenario: Landing page loads with Twizz logo
    Then the Twizz logo should be visible

  Scenario: Hero section displays exclusive experience heading
    Then the text "EXCLUSIVE" should be visible

  Scenario: Hero section shows monetization pitch
    Then the text "Want to monetize your content? Try Twizz." should be visible
    And the text "No matter how you want to convert" should be visible
    And the text "Sign up for free in one click" should be visible

  Scenario: Becoming a creator section is visible with description
    Then the text "Becoming a creator ?" should be visible
    And the text "Monetize your content with the best possible conversion rate" should be visible

  Scenario: Simple and better section shows platform description
    Then the text "It's simple, it's better." should be visible
    And the text "Twizz is a social network that allows creators" should be visible

  Scenario: Agency CRM section displays information
    Then the text "Do you have an agency? Use" should be visible
    And the text "We have developed Twizz Business specially" should be visible

  Scenario: Smart security section shows protection details
    Then the text "Smart and safe security" should be visible
    And the text "Secure payments, verified creators" should be visible

  Scenario: Already taking advantage section with social proof
    Then the text "THEY ARE ALREADY TAKING" should be visible
    And the text "They tried, they converted better" should be visible

  Scenario: FAQ section has all five question headings
    Then the text "If you have any questions" should be visible
    And the heading "Comment devenir un créateur ?" should be visible
    And the heading "Qui peut s'inscrire sur Twizz ?" should be visible

  Scenario: FAQ answers contain registration guidance
    Then the text "To register as a user:" should be visible
    And the text "Start by creating a creator" should be visible
    And the text "Our team acts quickly to" should be visible
    And the text "To withdraw your money on" should be visible

  Scenario: Footer shows payment methods image
    Then the image "Payment Methods" should be visible

  Scenario: Footer shows Earth image
    Then the image "Earth" should be visible

  # --- Header Navigation ---

  Scenario: Header Login button navigates to sign-in page
    When I click the "Login" button
    Then the URL should contain "/auth/signIn"

  Scenario: Header Register button navigates to sign-up page
    When I click the "Register" button
    Then the URL should contain "/auth/signUp"

  Scenario: Header Contact us button navigates to contact page
    When I click the "Contact us" button
    Then the URL should contain "/contact"

  # --- CTA Navigation ---

  Scenario: Become a fan button navigates to fan registration
    When I click the "Become a fan" button
    Then the URL should contain "/auth/signUp"
    And the URL should contain "currentTab=fan"
    And the "Fan" tab should be visible

  Scenario: Become a creator button navigates to creator registration
    When I click the "Become a creator" button
    Then the URL should contain "/auth/signUp"
    And the URL should contain "currentTab=creator"
    And the "Creator" tab should be visible

  Scenario: Fans button navigates to public discover
    When I click the "Fans" button
    Then the URL should contain "/public-discover"
    And the Home icon should be visible on discover page

  Scenario: Discover TWIZZ button navigates to public discover
    When I click the "Discover TWIZZ" button
    Then the URL should contain "/public-discover"

  Scenario: Second Login button in hero section navigates to sign-in
    When I click the "Login" button at index 1
    Then the URL should contain "/auth/signIn"

  # --- Section Button Navigation ---

  Scenario: Becoming a creator button navigates to creator registration
    When I click the "Becoming a creator" button
    Then the URL should contain "/auth/signUp"
    And the URL should contain "currentTab=creator"

  Scenario: Open the application button navigates to creator registration
    When I click the "Open the application" button
    Then the URL should contain "/auth/signUp"
    And the URL should contain "currentTab=creator"

  Scenario: Agency To start up button navigates to business site
    When I click the "To start up" button at index 0
    Then the URL should contain "business.twizz"

  Scenario: Agency Contact us navigates to contact page
    When I click the "Contact us" button at index 1
    Then the URL should contain "/contact"

  Scenario: Smart security To start up navigates to creator registration
    When I click the "To start up" button at index 1
    Then the URL should contain "/auth/signUp"
    And the URL should contain "currentTab=creator"

  Scenario: Already taking To start up navigates to creator registration
    When I click the "To start up" button at index 2
    Then the URL should contain "/auth/signUp"

  Scenario: User 1 link navigates to public discover
    When I click the "User 1" image link
    Then the URL should contain "/public-discover"

  Scenario: User 2 link navigates to public discover
    When I click the "User 2" image link
    Then the URL should contain "/public-discover"

  Scenario: User 3 link navigates to public discover
    When I click the "User 3" image link
    Then the URL should contain "/public-discover"

  # --- Footer FAQ Button Navigation ---

  Scenario: Register as a creator button navigates to creator signup
    When I click the "Register as a creator" button
    Then the URL should contain "/auth/signUp"
    And the URL should contain "currentTab=creator"

  Scenario: Sign up button navigates to fan signup
    When I click the "Sign up" button
    Then the URL should contain "/auth/signUp"

  Scenario: First Go to Twizz button navigates to creator signup
    When I click the "Go to Twizz" button at index 0
    Then the URL should contain "/auth/signUp"
    And the URL should contain "currentTab=creator"

  Scenario: Second Go to Twizz button navigates to intro page
    When I click the "Go to Twizz" button at index 1
    Then the URL should contain "/auth/intro"

  Scenario: Third Go to Twizz button navigates to intro page
    When I click the "Go to Twizz" button at index 2
    Then the URL should contain "/auth/intro"

  Scenario: Explore Twizz button navigates to public discover
    When I click the "Explore Twizz arrow-up" button
    Then the URL should contain "/public-discover"

  # --- Footer Legal Links ---

  Scenario: Contact footer link navigates to contact page
    When I click the "Contact arrow-up" footer link
    Then the URL should contain "/contact"

  Scenario: Legal notices footer link navigates correctly
    When I click the "Legal notices arrow-up" footer link
    Then the URL should contain "/legal-notices"
    And the heading "Legal information" should be visible

  Scenario: Content Policy footer link navigates correctly
    When I click the "Content Policy and Child Protection arrow-up" footer link
    Then the URL should contain "/content-policy-and-child-protection"
    And the heading "CONTENT POLICY" should be visible

  Scenario: Confidentiality footer link navigates correctly
    When I click the "Confidentiality arrow-up" footer link
    Then the URL should contain "/confidentiality-and-data-protection"

  Scenario: General Conditions of Sale footer link navigates correctly
    When I click the "General Conditions of Sale arrow-up" footer link
    Then the URL should contain "/general-conditions-of-sale"

  Scenario: General Conditions of Use footer link navigates correctly
    When I click the "General Conditions of Use arrow-up" footer link
    Then the URL should contain "/general-conditions-of-use"

  Scenario: Blog footer link navigates correctly
    When I click the "Blog arrow-up" footer link
    Then the URL should contain "/blogs"

  # --- Logo Navigation ---

  Scenario: Twizz logo links to homepage
    Then the Twizz logo should link to "/"

  # --- Language Switcher ---

  Scenario: Language switcher cycles English to French to Spanish and back
    Then the language selector should show "English"
    When I switch language from "English" to "Français"
    Then the French landing text should be visible
    When I switch language from "Français" to "Español"
    When I switch language from "Español" to "English"
    Then the English landing text should be visible
