Feature: Merchant account
  Scenario: Create a new merchant account
    When A merchant wants to register to DTU Pay with name "John Merchant"
    And merchant cpr "123456-1234"
    And a merchant DTUBank account
    Then the merchant is added on DTU Pay
    And Cleanup