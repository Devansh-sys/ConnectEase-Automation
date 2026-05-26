@Regression @UI @AIChat
Feature: CT_REG_AIChat - AI Chat Regression Tests

  @CE-FE-AI-TC001
  Scenario: AI Chat page loads and shows input field
    Given the user is logged in as a customer
    And the user navigates to the AI Chat page
    Then the query input field should be visible

  @CE-FE-AI-TC002
  Scenario: AI Chat responds to a user query
    Given the user is logged in as a customer
    And the user navigates to the AI Chat page
    When the user asks AI Chat "What services do you offer?"
    Then the AI Chat should respond to the first query
