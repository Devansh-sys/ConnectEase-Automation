@Smoke @UI @Signup
Feature: CT_SMOKE_Signup - Signup Page Smoke Tests

  @TC_SIGNUP_01
  Scenario: Successful signup with valid details
    Given the user navigates to the signup page
    When the user fills in valid signup details with name "BDD User" phone "9876543210" email "bdd_smoke_user@test.com" and password "BDD@1234"
    And the user submits the signup form
    Then the user should be redirected to the login page after signup

  @TC_SIGNUP_02
  Scenario: Signup page loads successfully
    Given the user navigates to the signup page
    Then validation error messages should appear on the signup form

  @TC_SIGNUP_09
  Scenario: Signup form shows errors when empty
    Given the user is on the signup page
    When the user submits the signup form without filling any fields
    Then validation error messages should appear on the signup form
