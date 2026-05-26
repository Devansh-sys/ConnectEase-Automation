@Smoke @API @Auth
Feature: CT_SMOKE_Auth_API - Auth API Smoke Tests

  @CE-AUTH-TC001
  Scenario: Login API returns 200 with valid credentials
    Given the Auth API is available at "https://connectease-1.onrender.com"
    When the client sends a POST to "/api/auth/signin" with valid credentials
    Then the API response status should be 200
    And the response body should contain a uid

  @CE-AUTH-TC005
  Scenario: Login API returns 401 with invalid credentials
    Given the Auth API is available at "https://connectease-1.onrender.com"
    When the client sends a POST to "/api/auth/signin" with invalid credentials
    Then the API response status should be 401

  @CE-AUTH-TC006
  Scenario: Login API returns 401 with empty credentials
    Given the Auth API is available at "https://connectease-1.onrender.com"
    When the client sends a POST to "/api/auth/signin" with missing fields
    Then the API response status should be 401
