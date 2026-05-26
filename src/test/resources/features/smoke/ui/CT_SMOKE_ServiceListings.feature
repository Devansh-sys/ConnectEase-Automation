@Smoke @UI @ServiceListings
Feature: CT_SMOKE_ServiceListings - Service Listings Smoke Tests

  @CE-FE-LIST-TC001
  Scenario: Service listing cards are displayed
    Given the user is logged in as a customer
    And the user is on the service listings page
    Then service listing cards should be displayed

  @CE-FE-LIST-TC008
  Scenario: Clicking a listing card navigates to detail page
    Given the user is logged in as a customer
    And the user is on the service listings page
    When the user clicks on the first service listing card
    Then the user should be navigated to a service detail page
