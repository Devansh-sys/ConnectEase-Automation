@Regression @UI @Signup
Feature: CT_REG_Signup - Signup Regression Tests

  @TC_SIGNUP_04
  Scenario: Signup with duplicate email shows error
    Given the user is on the signup page
    When the user enters an already registered email "vishal.user@gmail.com"
    Then a duplicate email error should be displayed

  @TC_SIGNUP_05
  Scenario: Signup with password too short shows error
    Given the user is on the signup page
    When the user enters a password "123" that is too short
    Then a password length error should be displayed

  @TC_SIGNUP_06
  Scenario: Signup with invalid phone shows error
    Given the user is on the signup page
    When the user enters an invalid phone number "175725783"
    Then a phone number error should be displayed

  @TC_SIGNUP_07
  Scenario: Signup with blank name shows error
    Given the user is on the signup page
    When the user enters the name " "
    Then a name validation error should be displayed

  @TC_SIGNUP_08
  Scenario: Signup form clears errors when corrected
    Given the user is on the signup page
    When the user submits the signup form without filling any fields
    Then validation error messages should appear on the signup form
