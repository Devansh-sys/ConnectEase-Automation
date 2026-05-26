@Regression @UI @ServiceListings
Feature: CT_REG_ServiceListings - Service Listings Regression Tests

#  @CE-FE-LIST-TC002
#  Scenario: Category filter narrows down results
#    Given the user is logged in as a customer
#    And the user is on the service listings page
#    When the user applies a category filter
#    Then only services of the selected category should be displayed

  @CE-FE-LIST-TC003
  Scenario: Search by keyword returns results
    Given the user is logged in as a customer
    And the user is on the service listings page
    When the user searches for a service by keyword "plumb"
    Then search results containing "plumb" should appear

  @CE-FE-LIST-TC004
  Scenario: Sort listings by price
    Given the user is logged in as a customer
    And the user is on the service listings page
    When the user sorts listings by "price"
    Then listings should be sorted accordingly
