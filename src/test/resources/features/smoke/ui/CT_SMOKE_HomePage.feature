@Smoke @UI @HomePage
Feature: CT_SMOKE_HomePage - Home Page Smoke Tests

  @CE_FE_TS_11
  Scenario: Home page hero section is visible after login
    Given the user is logged in as a customer
    And the user is on the home page
    Then the hero section should be visible

  @CE_FE_TS_12
  Scenario: Service category cards are displayed on home page
    Given the user is logged in as a customer
    And the user is on the home page
    Then service category cards should be displayed
