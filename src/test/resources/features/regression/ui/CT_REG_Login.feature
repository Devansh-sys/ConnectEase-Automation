@Regression @UI @Login
Feature: CT_REG_Login - Login Regression Tests

  @CE_FE_TS_07
  Scenario: Customer logout redirects to login page
    Given the user is logged in as a customer
    When the user clicks the logout button
    Then the user should be redirected to the login page

  @CE_FE_TS_08
  Scenario: Vendor logout redirects to login page
    Given the user is logged in as a vendor
    When the user clicks the logout button
    Then the user should be redirected to the login page

  @CE_FE_TS_09
  Scenario: Unauthenticated access to chats page is redirected to login
    Given the user is not logged in
    When the user navigates directly to "/chats"
    Then the browser URL should contain "/login"

  @CE_FE_TS_10
  Scenario: Unauthenticated access to vendor dashboard is guarded
    Given the user is not logged in
    When the user navigates directly to "/vendor/dashboard"
    Then the browser URL should contain "/login"
