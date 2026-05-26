@Regression @UI @Chat
Feature: CT_REG_Chat - Real-Time Chat Regression Tests

  @CE-FE-CHAT-TC001
  Scenario: User can send a message via Chat with Vendor on a service detail page
    Given the user is logged in as a customer
    And the user is on a service detail page
    When the user clicks Chat with Vendor
    Then the chat panel should be open
    When the user sends a message "Hello BDD Test"
    Then the message should appear in the chat window
