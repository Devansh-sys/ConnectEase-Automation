@Regression @UI @VendorDashboard
Feature: CT_REG_VendorDashboard - Vendor Dashboard Regression Tests

  @CE-FE-VEND-TC003
  Scenario: Add new service form appears when clicking Add New Service
    Given the user is logged in as a vendor
    And the vendor is on the vendor dashboard
    When the vendor clicks Add New Service
    Then the add service form should appear

  @CE-FE-VEND-TC004
  Scenario: Vendor can create a new service listing
    Given the user is logged in as a vendor
    And the vendor is on the vendor dashboard
    When the vendor fills in and submits the new service form
    Then the new service should appear in the listings
