Feature: Merchant account
  Scenario: Create a new merchant account
    When A merchant wants to register to DTU Pay with name "John Merchant"
    And merchant cpr "123456-1234"
    And a merchant DTUBank account
    Then the merchant is added on DTU Pay
    And Cleanup

  Scenario: Delete a merchant account from DTU Pay
    When a merchant's name is "John", cpr is "123456-1234" and has a DTUBank account
    And merchant is registered to DTU Pay
    And the merchant wants delete their account
    Then the merchant's account is deleted and gets a response