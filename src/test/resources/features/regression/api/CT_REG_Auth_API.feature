@Regression @API @Auth
Feature: CT_REG_Auth_API - Auth API Regression Tests

  @CE-AUTH-TC002
  Scenario: Register API returns 201 with new user
    Given the Auth API is available at "https://connectease-1.onrender.com"
    When the client sends a POST to "/api/auth/signup" with new user credentials
    Then the API response status should be 201

  @CE-AUTH-TC003
  Scenario: Register with existing email returns 400
    Given the Auth API is available at "https://connectease-1.onrender.com"
    When the client sends a POST to "/api/auth/signup" with valid credentials
    Then the API response status should be 400

  @CE-AUTH-TC004
  Scenario: Login returns error body on bad credentials
    Given the Auth API is available at "https://connectease-1.onrender.com"
    When the client sends a POST to "/api/auth/signin" with invalid credentials
    Then the response body should contain an error message
