@E2E @UI
Feature: CT_E2E_UserJourney - End-to-End User Journey

  @CE-E2E-TS-01
  Scenario: Customer completes full journey from login to service booking
    Given the user is on the login page
    When the user logs in with email "vishal.user@gmail.com" and password "123456789"
    Then the user should be redirected to the home page
    And the user is on the home page
    And service category cards should be displayed
    When the user clicks on a service category card
    Then the user should be navigated to the service listings page
    And service listing cards should be displayed
    When the user clicks on the first service listing card
    Then the user should be navigated to a service detail page
    And the service name should be displayed
