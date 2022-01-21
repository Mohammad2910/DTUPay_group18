Feature: Merchant account
  Scenario: Create a new merchant account
    When A merchant wants to register to DTU Pay with name "G18 Merchant"
    And merchant cpr "298765-6789"
    And a merchant DTUBank account
    Then the merchant is added on DTU Pay
    And Cleanup

  Scenario: Delete a merchant account from DTU Pay
    When a merchant's name is "G18 Merchant", cpr is "298765-6789" and has a DTUBank account
    And merchant is registered to DTU Pay
    And the merchant wants to delete their account
    Then the merchant's account is deleted and gets a response
    And Cleanup