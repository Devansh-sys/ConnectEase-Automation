@Regression @UI @CommunityForum
Feature: CT_REG_CommunityForum - Community Forum Regression Tests

#  @CE-FE-COM-TC001
#  Scenario: Community forum page loads and shows real stories
#    Given the user is logged in as a customer
#    And the user is on the community forum page
#    Then at least one community story card should be visible

  @CE-FE-COM-TC002
  Scenario: Logged-in user can share a new story
    Given the user is logged in as a customer
    And the user is on the community forum page
    When the user creates a new forum post with title "BDD Test Post"
    Then the new post should appear in the forum

#  @CE-FE-COM-TC003
#  Scenario: Category filter narrows the stories feed
#    Given the user is logged in as a customer
#    And the user is on the community forum page
#    When the user filters posts by category "General"
#    Then community posts should be filtered by "General"
