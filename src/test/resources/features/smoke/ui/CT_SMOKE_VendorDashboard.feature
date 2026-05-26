@Smoke @UI @VendorDashboard
Feature: CT_SMOKE_VendorDashboard - Vendor Dashboard Smoke Tests

  @CE-FE-VEND-TC001
  Scenario: Vendor dashboard loads for a logged-in vendor
    Given the user is logged in as a vendor
    And the vendor is on the vendor dashboard
    Then the vendor dashboard should load successfully

  @CE-FE-VEND-TC002
  Scenario: Vendor listings are visible on dashboard
    Given the user is logged in as a vendor
    And the vendor is on the vendor dashboard
    Then the vendor's service listings should be visible
