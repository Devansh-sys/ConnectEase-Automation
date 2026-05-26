@Smoke @UI @Login
Feature: CT_SMOKE_Login - Login Page Smoke Tests

  @CE_FE_TS_04
  Scenario: Successful customer login
    Given the user is on the login page
    When the user logs in with email "vishal.user@gmail.com" and password "123456789"
    Then the user should be redirected to the home page

  @CE_FE_TS_05
  Scenario: Login with invalid credentials shows error
    Given the user is on the login page
    When the user logs in with email "vishal.user@gmail.com" and password "wrongpass"
    Then an error message should be displayed on login

  @CE_FE_TS_06
  Scenario: Successful vendor login
    Given the user is on the login page
    When the user logs in with email "vishal.vendor@gmail.com" and password "123456"
    Then the user should be redirected to the home page
